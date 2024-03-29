/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zqhero.clean.data.cache;

import android.content.Context;

import com.zqhero.clean.data.cache.serializer.JsonSerializer;
import com.zqhero.clean.data.entity.UserEntity;
import com.zqhero.clean.data.exception.UserNotFoundException;
import com.zqhero.clean.data.executor.JobExecutor;
import com.zqhero.clean.data.executor.ThreadExecutor;

import java.io.File;

import rx.Observable;
import rx.Subscriber;

/**
 * {@link UserCache} implementation.
 */
public class UserCacheImpl implements UserCache {

	private static final String SETTINGS_FILE_NAME = "com.zqhero.android10.SETTINGS";
	private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";

	private static final String DEFAULT_FILE_NAME = "user_";
	private static final long EXPIRATION_TIME = 60 * 10 * 1000;

	private final Context context;
	private final File cacheDir;
	private JsonSerializer serializer;
	private  FileManager fileManager;
	private ThreadExecutor threadExecutor;

	/**
	 * Constructor of the class {@link UserCacheImpl}.
	 * <p/>
	 * <p/>
	 * UserCacheSerializer {@link JsonSerializer} for object serialization.
	 * {@link FileManager} for saving serialized objects to the file system.
	 */
	public UserCacheImpl(Context applicationContext) {
		this(applicationContext, new JsonSerializer(), new FileManager(), new JobExecutor());
	}

	/**
	 * Constructor of the class {@link UserCacheImpl}.
	 *
	 * @param context             A
	 * @param userCacheSerializer {@link JsonSerializer} for object serialization.
	 * @param fileManager         {@link FileManager} for saving serialized objects to the file system.
	 */
	public UserCacheImpl(Context context, JsonSerializer userCacheSerializer,
	                     FileManager fileManager, ThreadExecutor executor) {
		if (context == null || userCacheSerializer == null || fileManager == null || executor == null) {
			throw new IllegalArgumentException("Invalid null parameter");
		}
		this.context = context.getApplicationContext();
		this.cacheDir = this.context.getCacheDir();
		this.serializer = userCacheSerializer;
		this.fileManager = fileManager;
		this.threadExecutor = executor;
	}

	public void setSerializer(JsonSerializer serializer) {
		this.serializer = serializer;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setThreadExecutor(ThreadExecutor threadExecutor) {
		this.threadExecutor = threadExecutor;
	}

	@Override
	public synchronized Observable<UserEntity> get(final int userId) {
		return Observable.create(new Observable.OnSubscribe<UserEntity>() {
			@Override
			public void call(Subscriber<? super UserEntity> subscriber) {
				File userEntityFile = UserCacheImpl.this.buildFile(userId);
				String fileContent = UserCacheImpl.this.fileManager.readFileContent(userEntityFile);
				UserEntity userEntity = UserCacheImpl.this.serializer.deserialize(fileContent);

				if (userEntity != null) {
					subscriber.onNext(userEntity);
					subscriber.onCompleted();
				} else {
					subscriber.onError(new UserNotFoundException());
				}
			}
		});
	}

	@Override
	public synchronized void put(UserEntity userEntity) {
		if (userEntity != null) {
			File userEntitiyFile = this.buildFile(userEntity.getUserId());
			if (!isCached(userEntity.getUserId())) {
				String jsonString = this.serializer.serialize(userEntity);
				this.executeAsynchronously(new CacheWriter(this.fileManager, userEntitiyFile,
						jsonString));
				setLastCacheUpdateTimeMillis();
			}
		}
	}

	@Override
	public boolean isCached(int userId) {
		File userEntitiyFile = this.buildFile(userId);
		return this.fileManager.exists(userEntitiyFile);
	}

	@Override
	public boolean isExpired() {
		long currentTime = System.currentTimeMillis();
		long lastUpdateTime = this.getLastCacheUpdateTimeMillis();

		boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);

		if (expired) {
			this.evictAll();
		}

		return expired;
	}

	@Override
	public synchronized void evictAll() {
		this.executeAsynchronously(new CacheEvictor(this.fileManager, this.cacheDir));
	}

	/**
	 * Build a file, used to be inserted in the disk cache.
	 *
	 * @param userId The id user to build the file.
	 * @return A valid file.
	 */
	private File buildFile(int userId) {
		StringBuilder fileNameBuilder = new StringBuilder();
		fileNameBuilder.append(this.cacheDir.getPath());
		fileNameBuilder.append(File.separator);
		fileNameBuilder.append(DEFAULT_FILE_NAME);
		fileNameBuilder.append(userId);

		return new File(fileNameBuilder.toString());
	}

	/**
	 * Set in millis, the last time the cache was accessed.
	 */
	private void setLastCacheUpdateTimeMillis() {
		long currentMillis = System.currentTimeMillis();
		this.fileManager.writeToPreferences(this.context, SETTINGS_FILE_NAME,
				SETTINGS_KEY_LAST_CACHE_UPDATE, currentMillis);
	}

	/**
	 * Get in millis, the last time the cache was accessed.
	 */
	private long getLastCacheUpdateTimeMillis() {
		return this.fileManager.getFromPreferences(this.context, SETTINGS_FILE_NAME,
				SETTINGS_KEY_LAST_CACHE_UPDATE);
	}

	/**
	 * Executes a {@link Runnable} in another Thread.
	 *
	 * @param runnable {@link Runnable} to execute
	 */
	private void executeAsynchronously(Runnable runnable) {
		this.threadExecutor.execute(runnable);
	}

	/**
	 * {@link Runnable} class for writing to disk.
	 */
	private static class CacheWriter implements Runnable {
		private final FileManager fileManager;
		private final File fileToWrite;
		private final String fileContent;

		CacheWriter(FileManager fileManager, File fileToWrite, String fileContent) {
			this.fileManager = fileManager;
			this.fileToWrite = fileToWrite;
			this.fileContent = fileContent;
		}

		@Override
		public void run() {
			this.fileManager.writeToFile(fileToWrite, fileContent);
		}
	}

	/**
	 * {@link Runnable} class for evicting all the cached files
	 */
	private static class CacheEvictor implements Runnable {
		private final FileManager fileManager;
		private final File cacheDir;

		CacheEvictor(FileManager fileManager, File cacheDir) {
			this.fileManager = fileManager;
			this.cacheDir = cacheDir;
		}

		@Override
		public void run() {
			this.fileManager.clearDirectory(this.cacheDir);
		}
	}
}

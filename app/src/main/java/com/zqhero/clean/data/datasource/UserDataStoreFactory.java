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
package com.zqhero.clean.data.datasource;

import android.content.Context;

import com.zqhero.clean.data.entity.mapper.UserEntityJsonMapper;
import com.zqhero.clean.data.cache.UserCache;
import com.zqhero.clean.data.cache.UserCacheImpl;
import com.zqhero.clean.data.net.RestApi;
import com.zqhero.clean.data.net.RestApiImpl;

/**
 * Factory that creates different implementations of {@link UserDataStore}.
 */
public class UserDataStoreFactory {

	private final Context context ;
	private UserCache userCache;

	public UserDataStoreFactory(Context applicationContext) {
		this(applicationContext, new UserCacheImpl(applicationContext));
	}

	public UserDataStoreFactory(Context context, UserCache userCache) {
		if (context == null || userCache == null) {
			throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
		}
		this.context = context.getApplicationContext();
		this.userCache = userCache;
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}

	/**
	 * Create {@link UserDataStore} from a user id.
	 */
	public UserDataStore create(int userId) {
		UserDataStore userDataStore;

		if (!this.userCache.isExpired() && this.userCache.isCached(userId)) {
			userDataStore = new DiskUserDataStore(this.userCache);
		} else {
			userDataStore = createCloudDataStore();
		}

		return userDataStore;
	}

	/**
	 * Create {@link UserDataStore} to retrieve data from the Cloud.
	 */
	public CloudUserDataStore createCloudDataStore() {
		UserEntityJsonMapper userEntityJsonMapper = new UserEntityJsonMapper();
		RestApi restApi = new RestApiImpl(this.context, userEntityJsonMapper);

		return new CloudUserDataStore(restApi, this.userCache);
	}
}

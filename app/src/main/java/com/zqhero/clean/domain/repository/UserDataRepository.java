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
package com.zqhero.clean.domain.repository;

import android.content.Context;

import android.util.Log;
import com.zqhero.clean.data.datasource.CloudUserDataStore;
import com.zqhero.clean.data.dto.User;
import com.zqhero.clean.data.entity.UserEntity;
import com.zqhero.clean.data.entity.mapper.UserEntityDataMapper;
import com.zqhero.clean.data.datasource.UserDataStore;
import com.zqhero.clean.data.datasource.UserDataStoreFactory;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * {@link UserRepository} for retrieving user data.
 */
public class UserDataRepository implements UserRepository {

	private UserDataStoreFactory userDataStoreFactory;
	private UserEntityDataMapper userEntityDataMapper;

	public UserDataRepository(Context appContext) {
		this(new UserDataStoreFactory(appContext), new UserEntityDataMapper());
	}

	/**
	 * Constructs a {@link UserRepository}.
	 *
	 * @param dataStoreFactory     A factory to construct different data source implementations.
	 * @param userEntityDataMapper {@link UserEntityDataMapper}.
	 */
	public UserDataRepository(UserDataStoreFactory dataStoreFactory,
	                          UserEntityDataMapper userEntityDataMapper) {
		this.userDataStoreFactory = dataStoreFactory;
		this.userEntityDataMapper = userEntityDataMapper;
	}

	public void setUserDataStoreFactory(UserDataStoreFactory userDataStoreFactory) {
		this.userDataStoreFactory = userDataStoreFactory;
	}

	public void setUserEntityDataMapper(UserEntityDataMapper userEntityDataMapper) {
		this.userEntityDataMapper = userEntityDataMapper;
	}

	@SuppressWarnings("Convert2MethodRef")
	@Override
	public Observable<List<User>> users() {
		//we always get all users from the cloud
		final CloudUserDataStore userDataStore = this.userDataStoreFactory.createCloudDataStore();

		return userDataStore.userEntityList()
				.map(new Func1<List<UserEntity>, List<User>>() {
					@Override
					public List<User> call(List<UserEntity> userEntities) {
						Log.i("info",getClass().getName() + "=============users===" + userEntities.toString());
						return userEntityDataMapper.transform(userEntities);
					}
				});
	}

	@SuppressWarnings("Convert2MethodRef")
	@Override
	public Observable<User> user(int userId) {
		final UserDataStore userDataStore = this.userDataStoreFactory.create(userId);
		return userDataStore.userEntityDetails(userId)
				.map(new Func1<UserEntity, User>() {
					@Override
					public User call(UserEntity userEntity) {
						return userEntityDataMapper.transform(userEntity);
					}
				});
	}
}

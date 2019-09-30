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
package com.zqhero.clean.domain.interactor;

import com.zqhero.clean.data.executor.JobExecutor;
import com.zqhero.clean.data.executor.PostExecutionThread;
import com.zqhero.clean.data.executor.ThreadExecutor;
import com.zqhero.clean.data.executor.UIThread;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each UseCase implementation will return the result using a {@link rx.Subscriber}
 * that will execute its job in a background thread and will post the result in the UI thread.
 */
public abstract class UseCase {

	ThreadExecutor threadExecutor = new JobExecutor();
	PostExecutionThread postExecutionThread = new UIThread();

	private Subscription subscription = Subscriptions.empty();
	protected abstract Observable buildUseCaseObservable();

	/**
	 * Executes the current use case.
	 */
	@SuppressWarnings("unchecked")
	public void execute(Subscriber UseCaseSubscriber) {
		this.subscription = this.buildUseCaseObservable()
				.subscribeOn(Schedulers.from(threadExecutor))
				.observeOn(postExecutionThread.getScheduler())
				.subscribe(UseCaseSubscriber);
	}

	/**
	 * Unsubscribes from current {@link rx.Subscription}.
	 */
	public void unsubscribe() {
		if (!subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}

	//--

	public void setThreadExecutor(ThreadExecutor threadExecutor) {
		this.threadExecutor = threadExecutor;
	}

	public void setPostExecutionThread(PostExecutionThread postExecutionThread) {
		this.postExecutionThread = postExecutionThread;
	}
}

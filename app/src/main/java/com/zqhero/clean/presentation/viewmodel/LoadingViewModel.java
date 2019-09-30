package com.zqhero.clean.presentation.viewmodel;

import android.view.View;
import androidx.databinding.ObservableBoolean;

/**
 * Created by rocko on 15-11-5.
 */
public abstract class LoadingViewModel extends ViewModel {
	public final ObservableBoolean showRetry = new ObservableBoolean(false);
	public final ObservableBoolean showLoading = new ObservableBoolean(false);

	@BindView
	public void showLoading() {
		showRetry.set(false);
		showLoading.set(true);
	}

	@BindView
	public void showRetry() {
		showLoading.set(false);
		showRetry.set(true);
	}

	public abstract View.OnClickListener onRetryClick();
}

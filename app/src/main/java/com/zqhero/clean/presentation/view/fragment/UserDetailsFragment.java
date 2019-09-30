package com.zqhero.clean.presentation.view.fragment; /**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import com.zqhero.android10.sample.presentation.UserDetailsBinding;
import com.zqhero.clean.R;
import com.zqhero.clean.presentation.viewmodel.UserDetailsViewModel;

/**
 * Fragment that shows details of a certain user.
 */
public class UserDetailsFragment extends BaseFragment<UserDetailsViewModel, UserDetailsBinding> {

	public final static String TAG = UserDetailsFragment.class.getSimpleName();

	private static final String ARGUMENT_KEY_USER_ID = "org.android10.ARGUMENT_USER_ID";

	private int userId;

	public UserDetailsFragment() {
		super();

	}

	public static UserDetailsFragment newInstance(int userId) {
		UserDetailsFragment userDetailsFragment = new UserDetailsFragment();

		Bundle argumentsBundle = new Bundle();
		argumentsBundle.putInt(ARGUMENT_KEY_USER_ID, userId);
		userDetailsFragment.setArguments(argumentsBundle);

		return userDetailsFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		setViewModel(new UserDetailsViewModel());
		setBinding(DataBindingUtil.<UserDetailsBinding>inflate(inflater, R.layout.fragment_user_details, container, false));
		getBinding().setViewModel(getViewModel());

		return getBinding().getRoot();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.initialize();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initialize() {
		this.userId = getArguments().getInt(ARGUMENT_KEY_USER_ID);

		getViewModel().loadUserDetailsCommand(userId);
	}

	@Override
	public Context getContext() {
		return getActivity().getApplicationContext();
	}
}

package com.zqhero.clean.presentation.viewmodel;

import android.util.Log;
import android.view.View;
import com.zqhero.clean.presentation.navigation.ActivityNavigator;
import com.zqhero.clean.presentation.view.activity.UserListActivity;


/**
 * Created by rocko on 15-11-5.
 */
public class HomeViewModel extends ViewModel {

	@Command
	public View.OnClickListener onClickLoadData() {
		return new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.i("info","------------------------");
				ActivityNavigator.navigateTo(UserListActivity.class);
			}
		};
	}
}

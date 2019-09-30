package com.zqhero.clean.presentation.viewmodel;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import com.zqhero.clean.presentation.mapper.UserModelDataMapper;
import com.zqhero.clean.presentation.model.UserModel;
import com.zqhero.clean.presentation.navigation.ActivityNavigator;
import com.zqhero.clean.presentation.view.activity.UserDetailsActivity;
import com.zqhero.clean.presentation.view.adapter.UsersAdapter;
import com.zqhero.clean.data.dto.User;
import com.zqhero.clean.domain.interactor.DefaultSubscriber;
import com.zqhero.clean.domain.interactor.GetUserList;
import com.zqhero.clean.domain.interactor.UseCase;
import com.zqhero.clean.AndroidApplication;

import java.util.Collection;
import java.util.List;

/**
 * Created by rocko on 15-11-5.
 */
public class UserListViewModel extends LoadingViewModel {
    private final static String TAG = UserListViewModel.class.getSimpleName();

    public final ObservableBoolean showContentList = new ObservableBoolean(false);
    public final ObservableField<UsersAdapter> usersListAdapter = new ObservableField<>();

    UseCase getUserList = new GetUserList(AndroidApplication.getContext());
    UserModelDataMapper userModelDataMapper = new UserModelDataMapper();

    @BindView
    @Override
    public void showLoading() {
        super.showLoading();
        showContentList.set(false);
    }

    @BindView
    @Override
    public void showRetry() {
        super.showRetry();
        showContentList.set(false);
    }

    @BindView
    public void showContentList(UsersAdapter usersAdapter) {
        showLoading.set(false);
        showRetry.set(false);
        showContentList.set(true);
        usersListAdapter.set(usersAdapter);
    }

    @BindView
    public void showMoreContent() {
        // userAdapter
    }

    @Command
    public void loadUsersCommand() {
        if (showLoading.get()) {
            return;
        }
        showLoading();
        getUserList.execute(new DefaultSubscriber<List<User>>() {
            @Override
            public void onNext(List<User> users) {

                Log.i("info", "----------------------" + users.toString());

                Collection<UserModel> userModelsCollection = userModelDataMapper.transformUsers(users);
                UsersAdapter usersAdapter = new UsersAdapter(AndroidApplication.getContext(), userModelsCollection);
                usersAdapter.setOnItemClickListener(onUserItemClick());
                showContentList(usersAdapter);
            }

            @Override
            public void onError(Throwable e) {
                showRetry();
            }
        });
    }

    @Override
    public View.OnClickListener onRetryClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUsersCommand();

            }
        };
    }

    public UsersAdapter.OnItemClickListener onUserItemClick() {
        return new UsersAdapter.OnItemClickListener() {
            @Override
            public void onUserItemClicked(UserModel userModel) {
                Intent intent = UserDetailsActivity.getCallingIntent(AndroidApplication.getInstance().getCurrentActivity(), userModel.getUserId());
                ActivityNavigator.navigateTo(UserDetailsActivity.class, intent);
            }
        };
    }

}

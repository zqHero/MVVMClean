package com.zqhero.clean.presentation.view.adapter; /**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.zqhero.android10.sample.presentation.RowUserBinding;
import com.zqhero.clean.R;
import com.zqhero.clean.presentation.model.UserModel;

import java.util.Collection;
import java.util.List;


/**
 * Adaptar that manages a collection of {@link UserModel}.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

	private RowUserBinding rowUserBinding;

	public interface OnItemClickListener {
		void onUserItemClicked(UserModel userModel);
	}

	private List<UserModel> usersCollection;
	private final LayoutInflater layoutInflater;

	private OnItemClickListener onItemClickListener;

	public UsersAdapter(Context context, Collection<UserModel> usersCollection) {
		this.validateUsersCollection(usersCollection);
		this.layoutInflater =
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.usersCollection = (List<UserModel>) usersCollection;
	}

	@Override
	public int getItemCount() {
		return (this.usersCollection != null) ? this.usersCollection.size() : 0;
	}

	@Override
	public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		rowUserBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_user, parent, false);
		UserViewHolder userViewHolder = new UserViewHolder(rowUserBinding);
		return userViewHolder;
	}

	@Override
	public void onBindViewHolder(UserViewHolder holder, final int position) {
		final UserModel userModel = this.usersCollection.get(position);
		holder.textViewTitle.setText(userModel.getFullName());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UsersAdapter.this.onItemClickListener != null) {
					UsersAdapter.this.onItemClickListener.onUserItemClicked(userModel);
				}
			}
		});
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setUsersCollection(Collection<UserModel> usersCollection) {
		this.validateUsersCollection(usersCollection);
		this.usersCollection = (List<UserModel>) usersCollection;
		this.notifyDataSetChanged();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	private void validateUsersCollection(Collection<UserModel> usersCollection) {
		if (usersCollection == null) {
			throw new IllegalArgumentException("The list cannot be null");
		}
	}

	static class UserViewHolder extends RecyclerView.ViewHolder {
		TextView textViewTitle;

		public UserViewHolder(RowUserBinding rowUserBinding) {
			super(rowUserBinding.getRoot());
			textViewTitle = rowUserBinding.title;
		}
	}
}

package com.zqhero.clean.presentation.viewmodel;

import androidx.databinding.BaseObservable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rocko on 15-11-5.
 */
public abstract class ViewModel extends BaseObservable { // TODO: Need Context(Activity)?

	/* Just mark a method in ViewModel */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.SOURCE)
	protected  @interface Command {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.SOURCE)
	protected @interface BindView {
	}

	// ... InstanceState in ViewModel


}

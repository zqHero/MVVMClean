package com.zqhero.clean.presentation.view.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import androidx.databinding.DataBindingUtil;
import com.zqhero.clean.R;
import com.zqhero.clean.presentation.HomeBinding;
import com.zqhero.clean.presentation.viewmodel.HomeViewModel;

/**
 * Main application screen. This is the app entry point.
 */
public class MainActivity extends BaseActivity<HomeViewModel, HomeBinding> {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setViewModel(new HomeViewModel());
    setBinding(DataBindingUtil.<HomeBinding>setContentView(this, R.layout.home_activity));
    getBinding().setViewModel(getViewModel());

    initWidget();
  }

  private void initWidget() {
    getBinding().linkTv.setText(Html.fromHtml(getResources().getString(R.string.url)));
    getBinding().linkTv.setMovementMethod(LinkMovementMethod.getInstance());
  }

}

package com.jude.fishing.module.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jude.beam.bijection.RequiresPresenter;
import com.jude.beam.expansion.data.BeamDataFragment;
import com.jude.fishing.R;
import com.jude.fishing.model.AccountModel;
import com.jude.fishing.model.ImageModel;
import com.jude.fishing.model.entities.Account;
import com.jude.fishing.module.setting.SettingActivity;
import com.jude.tagview.TAGView;
import com.jude.utils.JUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Mr.Jude on 2015/9/17.
 */
@RequiresPresenter(DrawerPresenter.class)
public class DrawerFragment extends BeamDataFragment<DrawerPresenter, Account> implements View.OnClickListener {

    @InjectView(R.id.imgFace)
    SimpleDraweeView imgFace;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.viewAccount)
    LinearLayout viewAccount;
    @InjectView(R.id.nav_place)
    RelativeLayout navPlace;
    @InjectView(R.id.nav_blog)
    RelativeLayout navBlog;
    @InjectView(R.id.nav_message)
    RelativeLayout navMessage;
    @InjectView(R.id.nav_user)
    RelativeLayout navUser;
    @InjectView(R.id.nav_setting)
    RelativeLayout navSetting;
    @InjectView(R.id.nav_logout)
    RelativeLayout navLogout;
    @InjectView(R.id.message_count)
    TextView messageCount;
    @InjectView(R.id.nav_date)
    RelativeLayout navDate;
    @InjectView(R.id.mark)
    TAGView mark;
    @InjectView(R.id.tv_score)
    TextView tvScore;
    @InjectView(R.id.img_score)
    ImageView imgScore;
    @InjectView(R.id.nav_article)
    RelativeLayout navArticle;

    @Override
    public void setData(Account info) {
        if (info == null) {
            imgFace.setImageURI(null);
            tvName.setText("未登录,点击登陆");
            mark.setVisibility(View.INVISIBLE);
            tvScore.setVisibility(View.INVISIBLE);
            imgScore.setVisibility(View.INVISIBLE);
        } else {
            imgFace.setImageURI(ImageModel.getInstance().getSmallImage(info.getAvatar()));
            tvName.setText(info.getName());
            tvScore.setText(info.getScore() + "");
            tvScore.setVisibility(View.VISIBLE);
            imgScore.setVisibility(View.VISIBLE);
            mark.setVisibility(View.VISIBLE);
            if (info.isHasSignIn()) {
                mark.setText("已签到");
                mark.setTextColor(getResources().getColor(R.color.gray));
                mark.setBackgroundColor(getResources().getColor(R.color.gray));
                mark.setEnabled(false);
            } else {
                mark.setText("签到");
                mark.setTextColor(getResources().getColor(R.color.white));
                mark.setBackgroundColor(getResources().getColor(R.color.white));
                mark.setEnabled(true);
            }
        }
        if (AccountModel.getInstance().checkIsSuper()){
            navArticle.setVisibility(View.VISIBLE);
            navDate.setVisibility(View.VISIBLE);
        }else{
            navArticle.setVisibility(View.GONE);
            navDate.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_drawer, container, false);
        ButterKnife.inject(this, view);

        //为API20以上的statusBar做适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((RelativeLayout.LayoutParams) imgFace.getLayoutParams()).setMargins(
                    JUtils.dip2px(16),
                    JUtils.dip2px(16) + JUtils.getStatusBarHeight(),
                    JUtils.dip2px(16),
                    JUtils.dip2px(16)
            );
        }

        imgFace.setOnClickListener(v -> getPresenter().showUserDetail());
        viewAccount.setOnClickListener(v -> getPresenter().showUserDetail());
        navSetting.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingActivity.class)));
        navLogout.setOnClickListener(v -> showLogoutDialog());

        navPlace.setOnClickListener(this);
        navBlog.setOnClickListener(this);
        navMessage.setOnClickListener(this);
        navUser.setOnClickListener(this);
        navDate.setOnClickListener(this);
        navArticle.setOnClickListener(this);
        navBlog.post(() -> navBlog.performClick());
        mark.setOnClickListener(v -> getPresenter().signIn());

        return view;
    }

    public void setMessageCount(int i) {
        messageCount.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
        messageCount.setText(i + "");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private View lastView;

    public void focusView(View view) {
        if (lastView != null) lastView.setBackgroundColor(getResources().getColor(R.color.white));
        view.setBackgroundColor(getResources().getColor(R.color.gray_light));
        lastView = view;
    }

    private void showLogoutDialog() {
        new MaterialDialog.Builder(getContext()).
                title("注销登录").
                content("您确定要退出登录吗？").
                positiveText("注销").
                negativeText("取消").
                callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        navBlog.performClick();
                        AccountModel.getInstance().logout();
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        if (drawerChangedListener != null) {
            drawerChangedListener.onChange(v);
            focusView(v);
        }
    }

    DrawerChangedListener drawerChangedListener;

    public void setDrawerChangedListener(DrawerChangedListener drawerChangedListener) {
        this.drawerChangedListener = drawerChangedListener;
    }

    public interface DrawerChangedListener {
        void onChange(View view);
    }
}

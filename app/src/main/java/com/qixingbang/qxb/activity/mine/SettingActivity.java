package com.qixingbang.qxb.activity.mine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.login.LoginActivity;
import com.qixingbang.qxb.activity.mine.changeInfo.ChangeNicknameAty;
import com.qixingbang.qxb.activity.mine.clipHeadPortrait.RoundImageView;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.mine.UserInfoBean;
import com.qixingbang.qxb.common.application.QApplication;
import com.qixingbang.qxb.common.utils.FileUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.dialog.TextDialog;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class SettingActivity extends BaseActivity {

    private final String TAG = SettingActivity.class.getName();
    public final static int REQUEST_GALLERY = 100;
    public final static int REQUEST_CAMERA = 200;
    public final static int REQUEST_CLIP = 300;
    public static Bitmap mSelectedBitmap;
    //public final Typeface YAHEI_FONT = Typeface.createFromAsset(getAssets(), "fonts/yahei.ttf");
    //public final Typeface SIMHEI_FONT = Typeface.createFromAsset(getAssets(), "fonts/simhei.ttf");

    @Bind(R.id.imageView_back)
    ImageView mImageViewBack;
    @Bind(R.id.imageView_share)
    ImageView mImageViewShare;
    @Bind(R.id.textView_tabTip)
    TextView mTextViewTabTip;
    @Bind(R.id.iv_head_portrait)
    RoundImageView mIvHeadPortrait;
    @Bind(R.id.tv_age)
    TextView mTvAge;
    @Bind(R.id.tv_sex)
    TextView mTvSex;
    @Bind(R.id.tv_nickname)
    TextView mTvNickname;

    @Bind(R.id.rl_change_head)
    RelativeLayout mRlChangeHead;
    @Bind(R.id.rl_change_nickname)
    RelativeLayout mRlChangeNickname;
    @Bind(R.id.rl_change_age)
    RelativeLayout mRlChangeAge;
    @Bind(R.id.rl_change_password)
    RelativeLayout mRlChangePassword;
    @Bind(R.id.rl_change_user)
    RelativeLayout mRlChangeUser;
    @Bind(R.id.rl_change_sex)
    RelativeLayout mRlChangeSex;

    private TextView tvTitle;

    private BitmapUtils mBitmapUtils;

    private PopupWindow mPopupWindow;

    private boolean sex;
    private String nickname;

    private static final int NICKNAME_CODE = 0x01;
    //private static final int NICKNAME_RESULT_CODE = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.textView_tabTip);
        boolean isPush = QAccount.getIsPush();
    }

    @Override
    public void initData() {
        tvTitle.setText(R.string.setting);
        //tvTitle.setTypeface(YAHEI_FONT);
        String userInfo = QAccount.getUserInfo();
        Gson gson = new Gson();
        UserInfoBean userInfoBean = gson.fromJson(userInfo, UserInfoBean.class);
        if (userInfoBean.result == 200) {
            QApplication.getImageLoader().display(mIvHeadPortrait, userInfoBean.user.icon);

            sex = userInfoBean.user.sex;
            if (sex) {
                mTvSex.setText("女");
            }else {
                mTvSex.setText("男");
            }
            mTvAge.setText(userInfoBean.user.age + "岁");
            nickname = userInfoBean.user.nickname;
            mTvNickname.setText(nickname);
        }


    }

    /**
     * 布局文件中的点击监听
     *
     * @param v
     */
    @OnClick({R.id.rl_change_head, R.id.imageView_back, R.id.rl_change_sex, R.id.rl_change_user,
            R.id.rl_change_nickname, R.id.rl_change_password, R.id.rl_change_age})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.rl_change_head:
                if (dismissPopupWindow())
                    return;
                View view = initPopupWindow();
                if (mPopupWindow == null) {
                    Log.d("PopupWindow", "create a new popupWindow");
                    mPopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    mPopupWindow.setAnimationStyle(R.style.popup_style);
                    mPopupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                } else if (!mPopupWindow.isShowing() && mPopupWindow != null) {
                    Log.d("PopupWindow", "show the popupWindow");
                    mPopupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                }
                break;
            case R.id.rl_change_nickname:
                Intent intent = new Intent(this, ChangeNicknameAty.class);
                Bundle bundle = new Bundle();
                bundle.putString("nickname", nickname);
                intent.putExtras(bundle);
                startActivityForResult(intent, NICKNAME_CODE);
                break;
            case R.id.rl_change_sex:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                int checkItem;
                if (sex) {
                    checkItem = 1;
                }else {
                    checkItem = 0;
                }
                builder.setSingleChoiceItems(new String[]{"男", "女"}, checkItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SettingActivity.this, which + "", Toast.LENGTH_SHORT).show();
                        //TODO:上传服务器
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.rl_change_age:
                break;
            case R.id.rl_change_password:
                break;
            case R.id.rl_change_user:
                changeUser();
                break;
            case R.id.imageView_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void changeUser() {
        TextDialog dialog = new TextDialog(this);
        dialog.show();
        dialog.setTitle(R.string.change_user);
        dialog.setContent(R.string.change_user_hint);
        dialog.setConfirmText(R.string.switch_account);
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QAccount.clear();
                LoginActivity.start(SettingActivity.this);
                SettingActivity.this.finish();
            }
        });
    }

    /**
     * 初始化popupWindow布局
     *
     * @return popupWindow  View
     */
    private View initPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_select_img_source, null);

        TextView tvAlbum = (TextView) view.findViewById(R.id.tv_select_img_from_album);
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_select_img_from_camera);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

        tvAlbum.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        return view;
    }

    /**
     * popupWindow上的点击监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_img_from_album:
                Intent intent = new Intent(SettingActivity.this, ChooseHeadPortraitActivity.class);
                startActivityForResult(intent, REQUEST_GALLERY);
                dismissPopupWindow();
                break;
            case R.id.tv_select_img_from_camera:
                takePhoto();
                dismissPopupWindow();
                break;
            case R.id.tv_cancel:
                dismissPopupWindow();
                break;
        }
    }

    private boolean dismissPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (!dismissPopupWindow())
                    this.finish();
                break;
        }
        return true;
    }

    private void takePhoto() {
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtil.getTakePhotoPath());
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (openCamera.resolveActivity(getPackageManager()) == null) {
            ToastUtil.toast(R.string.no_camera_app);
            return;
        }
        startActivityForResult(openCamera, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY || requestCode == REQUEST_CLIP) {
            if (resultCode == RESULT_OK) {
                if (null != mSelectedBitmap) {
                    setResult(RESULT_OK);
                    mIvHeadPortrait.setImageBitmap(mSelectedBitmap);
                    ToastUtil.toast(R.string.portrait_change_success);
                }
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                //                Bitmap bitmap = BitmapUtil.resizeBitmap(BitmapFactory.decodeFile(FileUtil.getTakePhotoPath()), 800);// 图像重塑，节省空间，减少OOM。
                //                new File(FileUtil.getTakePhotoPath()).delete();//临时牌照的图片就删了，相册选的则不删
                ClipHeadPortraitActivity.start(this, FileUtil.getTakePhotoPath(), REQUEST_CLIP);
            }
        }
    }

    /**
     * 控制软键盘弹出
     */
    private void initInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

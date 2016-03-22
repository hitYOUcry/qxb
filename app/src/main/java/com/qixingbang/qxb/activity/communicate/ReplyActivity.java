package com.qixingbang.qxb.activity.communicate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.login.LoginActivity;
import com.qixingbang.qxb.adapter.communicate.ReplyListAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.ReplyBean;
import com.qixingbang.qxb.beans.communicate.CommunicateBean;
import com.qixingbang.qxb.common.application.QApplication;
import com.qixingbang.qxb.common.utils.FileUtil;
import com.qixingbang.qxb.common.utils.LogUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.dialog.DialogUtil;
import com.qixingbang.qxb.dialog.communicate.PreviewImageDialog;
import com.qixingbang.qxb.server.MultipartRequest;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;


public class ReplyActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener, ReplyListAdapter.OnElementClickListener {
    private static CommunicateBean mQuestion;

    ImageView backImageView;
    ImageView shareImageView;
    TextView tabTipTextView;

    PullToRefreshListView contentListView;

    ImageView addPicImageView;
    EditText commentEditText;
    TextView commitCommentTxv;

    PopupWindow choosePicWindow;

    private ReplyListAdapter mReplyAdapter;
    private List<ReplyBean> mReplayBeanList;
    private int mCommentCount;

    private List<String> mBitmapPath = new ArrayList<>();

    private Handler mHandler;
    private final String TAG = ReplyActivity.class.getName();

    public static int COMMENTED = 300;
    private final int REQUEST_CODE_GALLERY = 100;
    private final int REQUEST_CODE_CAMERA = 200;

    /*
    zoom part
     */
    View backgroundView;
    ImageView originalImageView;
    PhotoView mExpandPhotoView;
    private Rect mFinalBounds;
    private Rect mStartBounds = new Rect();
    private float mScaleX;
    private float mScaleY;
    boolean isPreviewing = false;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private final static float MIN_ALPHA = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);
        initView();
        initData();
        getReplayFromServer();
    }

    @Override
    public void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);

        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setVisibility(View.VISIBLE);
        shareImageView.setOnClickListener(this);

        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);
        tabTipTextView.setText(R.string.reply);

        contentListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        contentListView.setOnRefreshListener(this);
        contentListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        ILoadingLayout endLabels = contentListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(getString(R.string.load_more));// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel(getString(R.string.loading));// 刷新时
        endLabels.setReleaseLabel(getString(R.string.begin_load));// 下来达到一定距离时，显示的提示

        addPicImageView = (ImageView) findViewById(R.id.imageView_addPic);
        addPicImageView.setOnClickListener(this);

        commentEditText = (EditText) findViewById(R.id.editText_comment);

        commitCommentTxv = (TextView) findViewById(R.id.textView_submitComment);
        commitCommentTxv.setOnClickListener(this);
        backgroundView = findViewById(R.id.view_bg);
        backgroundView.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mHandler = new Handler();
        mReplayBeanList = new ArrayList<>();
        mReplayBeanList.add(new ReplyBean(mQuestion));
        mReplyAdapter = new ReplyListAdapter(mReplayBeanList, this, this);
        contentListView.setAdapter(mReplyAdapter);
        mExpandPhotoView = (PhotoView) findViewById(R.id.photoView_expanded);
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        getReplayFromServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_back:
                finish();
                break;
            case R.id.imageView_share:
                //TODO
                break;
            case R.id.textView_submitComment:
                submitComment();
                break;
            case R.id.imageView_addPic:
                addPics();
                break;
            case R.id.tv_cancel:
                dismissPopWindow();
                break;
            case R.id.tv_select_img_from_album:
                choosePhotoFromAlbum();
                break;
            case R.id.tv_select_img_from_camera:
                takePhoto();
                break;
            case R.id.view_bg:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private long mLastClickTime = 0;

    private void submitComment() {
        if (System.currentTimeMillis() - mLastClickTime < 1000) {
            mLastClickTime = System.currentTimeMillis();
            ToastUtil.toast(R.string.click_too_fre);
            return;
        }
        String commentContent = commentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(commentContent)) {
            ToastUtil.toast(R.string.must_have_text);
            return;
        }
        if (!QAccount.hasAccount()) {
            LoginActivity.start(this);
            ToastUtil.toast(R.string.not_login_yet);
            return;
        }
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addPart("questionId",
                new StringBody(String.valueOf(mQuestion.getQuestionId()), ContentType.create("text/plain", Consts.UTF_8)));
        entityBuilder.addPart("content",
                new StringBody(commentContent, ContentType.create("text/plain", Consts.UTF_8)));

        for (String path : mBitmapPath) {
            if (null != path && !path.isEmpty()) {
                FileBody fileBody = new FileBody(new File(path));
                entityBuilder.addPart("ansPic", fileBody);
            }
        }
        DialogUtil.showWaitingDialog(this, R.string.sending);
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, UrlUtil.getSendAnswerUrl(), entityBuilder.build(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optInt("result") == 200) {
                            setResult(COMMENTED);
                            ToastUtil.toast(R.string.comment_send_success);
                            commentEditText.setText("");
                            commentEditText.clearFocus();
                            mCommentCount = 0;
                            mBitmapPath.clear();
                            if (null != mReplayBeanList && !mReplayBeanList.isEmpty()) {
                                mReplayBeanList.clear();
                                mReplayBeanList.add(new ReplyBean(mQuestion));
                            }
                            getReplayFromServer();
                        } else if (300 == response.optInt("result")) {
                            ToastUtil.toast(R.string.comment_send_failed);
                            DialogUtil.dismissWaitingDialog();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                        DialogUtil.dismissWaitingDialog();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", QAccount.getToken());
                return headers;
            }
        };
        RequestUtil.getInstance().addToRequestQueue(multipartRequest);
    }

    private void getReplayFromServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getReplyUrl(mQuestion.getQuestionId(), mCommentCount),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("asd", response.toString());
                        int resultCode = response.optInt("result");
                        if (300 == resultCode) {
                            //                            ToastUtil.toast(R.string.system_error);
                        } else {
                            Gson gson = new Gson();
                            List<ReplyBean> list;
                            list = gson.fromJson(response.optJSONArray("result").toString(),
                                    new TypeToken<List<ReplyBean>>() {
                                    }.getType());
                            mCommentCount += list.size();
                            mReplayBeanList.addAll(list);
                            mReplyAdapter.notifyDataSetChanged();
                        }
                        if (contentListView.isRefreshing()) {
                            contentListView.onRefreshComplete();
                        }
                        DialogUtil.dismissWaitingDialog();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtil.dismissWaitingDialog();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (QAccount.hasAccount()) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", QAccount.getToken());
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request);
    }

    @Override
    public void likeClicked(int likeFlagBeforeClick, int answerId, final Runnable runnable) {
        int method;
        if (0 == likeFlagBeforeClick) {
            method = Request.Method.POST;
        } else {
            method = Request.Method.DELETE;
        }
        JsonObjectRequest request = new JsonObjectRequest(method, UrlUtil.getLikeUrl("answer", answerId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int resultCode = response.optInt("result");
                        if (resultCode == 200) {
                            mHandler.post(runnable);
                        } else if (resultCode == 250) {
                            ToastUtil.toast(R.string.not_login_yet);
                        } else if (resultCode == 300) {
                            ToastUtil.toast(R.string.system_error);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", QAccount.getToken());
                return headers;
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void imageClicked(int index, List<String> picUrls) {
        PreviewImageDialog dialog = new PreviewImageDialog(this, picUrls, index);
        dialog.show();
    }

    @Override
    public void imageClicked(ImageView clickedView, String picUrl) {
        originalImageView = clickedView;
        commentEditText.clearFocus();
        zoomIn(picUrl);
    }

    private void addPics() {
        View view = View.inflate(this, R.layout.popupwindow_select_img_source, null);
        choosePicWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        choosePicWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        choosePicWindow.setOutsideTouchable(true);

        TextView tvAlbum = (TextView) view.findViewById(R.id.tv_select_img_from_album);
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_select_img_from_camera);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvAlbum.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    private void choosePhotoFromAlbum() {
        if (mBitmapPath.size() >= 3) {
            ToastUtil.toast(R.string.pic_num_outOfBounder);
            return;
        }
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }

    private void takePhoto() {
        if (mBitmapPath.size() >= 3) {
            ToastUtil.toast(R.string.pic_num_outOfBounder);
            return;
        }
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtil.getTakePhotoPath());
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (openCamera.resolveActivity(getPackageManager()) == null) {
            ToastUtil.toast(R.string.no_camera_app);
            return;
        }
        startActivityForResult(openCamera, REQUEST_CODE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                //                saveSelectedBitmap(ResizeBitmap(BitmapFactory.decodeFile(FileUtil.getTakePhotoPath()), 800));// 图像重塑，节省空间，减少OOM。
                saveSelectedBitmap(BitmapFactory.decodeFile(FileUtil.getTakePhotoPath()));// 原图。
                new File(FileUtil.getTakePhotoPath()).delete();//临时牌照的图片就删了，相册选的则不删
            }
        } else if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri galleyUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(galleyUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                saveSelectedBitmap(ResizeBitmap(BitmapFactory.decodeFile(picturePath), 800));
            }
        }
        dismissPopWindow();
    }

    private boolean dismissPopWindow() {
        if (null != choosePicWindow && choosePicWindow.isShowing()) {
            choosePicWindow.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dismissPopWindow()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void saveSelectedBitmap(Bitmap bitmap) {
        String bitmapFilePath = FileUtil.PATH_QXB + "answerPic" + System.currentTimeMillis() + ".jpg";
        File bitmapFile = new File(bitmapFilePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(bitmapFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            mBitmapPath.add(bitmapFilePath);
            ToastUtil.toast("pic save success");
        } catch (IOException e) {
            LogUtil.e(TAG, "save bitmap failed");
        }
    }

    /**
     * @param bitmap
     * @param newWidth
     * @return
     * @title: ResizeBitmap
     * @description: 图像尺寸调整函数
     * @author: ZQJ
     * @date: 2015年6月27日 下午4:56:20
     */
    private Bitmap ResizeBitmap(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float temp = ((float) height) / ((float) width);
        int newHeight = (int) ((newWidth) * temp);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static void start(Context context, CommunicateBean question) {
        mQuestion = question;
        Intent intent = new Intent(context, ReplyActivity.class);
        context.startActivity(intent);
    }

    public static void start(Activity activity, CommunicateBean question, int requestCode) {
        mQuestion = question;
        Intent intent = new Intent(activity, ReplyActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private void zoomIn(String picUrl) {
        if (null == mFinalBounds) {
            mFinalBounds = new Rect();
            mExpandPhotoView.getGlobalVisibleRect(mFinalBounds);
        }
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        QApplication.getImageLoader().display(mExpandPhotoView, picUrl);

        final ImageView clickedView = originalImageView;
        clickedView.getGlobalVisibleRect(mStartBounds);

        mScaleX = (float) mStartBounds.width() / mFinalBounds.width();
        mScaleY = (float) mStartBounds.height() / mFinalBounds.height();

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        clickedView.setAlpha(0f);
        mExpandPhotoView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        mExpandPhotoView.setPivotX(0f);
        mExpandPhotoView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(mExpandPhotoView, View.X,
                mStartBounds.left, mFinalBounds.left))
                .with(ObjectAnimator.ofFloat(mExpandPhotoView, View.Y,
                        mStartBounds.top, mFinalBounds.top))
                .with(ObjectAnimator.ofFloat(mExpandPhotoView, View.SCALE_X,
                        mScaleX, 1f))
                .with(ObjectAnimator.ofFloat(mExpandPhotoView, View.SCALE_Y,
                        mScaleY, 1f))
                .with(ObjectAnimator.ofFloat(backgroundView, View.ALPHA, MIN_ALPHA, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                backgroundView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                clickedView.setAlpha(1f);
                mCurrentAnimator = null;
                isPreviewing = false;
            }
        });
        set.start();
        isPreviewing = true;
        mCurrentAnimator = set;
    }

    private void zoomOut() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(mExpandPhotoView, View.X, mStartBounds.left))
                .with(ObjectAnimator
                        .ofFloat(mExpandPhotoView,
                                View.Y, mStartBounds.top))
                .with(ObjectAnimator
                        .ofFloat(mExpandPhotoView,
                                View.SCALE_X, mScaleX))
                .with(ObjectAnimator
                        .ofFloat(mExpandPhotoView,
                                View.SCALE_Y, mScaleY))
                .with(ObjectAnimator.ofFloat(backgroundView, View.ALPHA, 1f, MIN_ALPHA));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                originalImageView.setAlpha(1f);
                mExpandPhotoView.setVisibility(View.GONE);
                backgroundView.setVisibility(View.GONE);
                mCurrentAnimator = null;
                isPreviewing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                originalImageView.setAlpha(1f);
                mExpandPhotoView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    @Override
    public void onBackPressed() {
        if (isPreviewing) {
            zoomOut();
            commentEditText.setFocusable(true);
        } else {
            super.onBackPressed();
        }
    }
}



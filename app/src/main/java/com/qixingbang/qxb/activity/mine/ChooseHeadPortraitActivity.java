package com.qixingbang.qxb.activity.mine;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.chooseHeadPortrait.ImageAdapter;
import com.qixingbang.qxb.activity.mine.chooseHeadPortrait.ImageFolder;
import com.qixingbang.qxb.activity.mine.chooseHeadPortrait.ImageModel;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Z.H. on 2015/9/5 15:53.
 */
public class ChooseHeadPortraitActivity extends BaseActivity {

    private static final String TAG = "ChooseHeadPortrait";
    private final static int REQUEST_IMAGE_LOADER = 100;
    public final static int REQUEST_CLIP = 200;

    ImageView backImageView;
    TextView changeFolder;
    TextView folderName;
    TextView imageNum;

    /**
     * 所有图片的数组
     */
    private ArrayList<ImageModel> imageList;

    private GridView gvImages;
    /**
     * GridView的适配器
     */
    private ImageAdapter adapter;
    private TextView tvTitle;

    /**
     * 所有的文件夹
     */
    private HashSet<String> folderDirs = new HashSet<String>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            imageNum.setText(String.format(getString(R.string.img_count), imageList.size()));
            Log.e(TAG, "folderDirs.length: " + String.valueOf(folderDirs.size()));
            Log.e(TAG, "imageList.length: " + String.valueOf(imageList.size()));
            for (int i = 0; i < folderDirs.size(); i++) {
                Log.e(TAG, "Name: " + QAccount.mImageFolders.get(i).getFolderName() +
                        "  Count: " + QAccount.mImageFolders.get(i).getCount() +
                        "  Path: " + QAccount.mImageFolders.get(i).getFirstImagePath());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_head_portrait);
        initView();
        initData();
        getImages();
    }

    @Override
    public void initView() {
        gvImages = (GridView) findViewById(R.id.gv_images);
        tvTitle = (TextView) findViewById(R.id.textView_tabTip);
        tvTitle.setText(R.string.head_portrait_title);
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);
        changeFolder = (TextView) findViewById(R.id.tv_change_folder);
        changeFolder.setOnClickListener(this);
        folderName = (TextView) findViewById(R.id.tv_dir_name);
        imageNum = (TextView) findViewById(R.id.tv_img_count);
    }

    @Override
    public void initData() {
        imageList = new ArrayList<>();
        adapter = new ImageAdapter(imageList, this);
        gvImages.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_back:
                finish();
                break;
            case R.id.tv_change_folder:
                Intent intent = new Intent(ChooseHeadPortraitActivity.this, ImageFolderListActivity.class);
                startActivityForResult(intent, REQUEST_IMAGE_LOADER);
                break;
            default:
                break;
        }
    }

    private void getImages() {
        //获取所有图片实体
        imageList.clear();
        //检查SD卡是否存在
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        //确认存在SD卡，使用子线程对SD卡进行扫描
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(imageUri, null, MediaStore.Images.Media.MIME_TYPE + " = \"image/png\" or "
                        + MediaStore.Images.Media.MIME_TYPE + " = \"image/jpeg\"", null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
                int count = cursor.getCount();
                Log.e("count", String.valueOf(count));
                if (cursor.moveToFirst()) {
                    if (QAccount.mImageFolders != null) {
                        QAccount.mImageFolders.clear();
                    }
                    while (cursor.moveToNext()) {
                        //存在Image实体中,同时按父文件夹分类
                        ImageModel imageModel = new ImageModel();

                        ImageFolder imageFolder = null;
                        //图片的路径
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));


                        //*********imageModel***********//
                        imageModel.path = path;
                        //TODO:date id 加以判断
                        try {
                            imageModel.date = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                            imageModel.id = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
                        } catch (Exception e) {

                        }

                        imageList.add(imageModel);
                        //*********imageModel***********//


                        //图片父文件夹的路径
                        File parentFile = new File(path).getParentFile();
                        //父文件夹路径可能为空
                        if (parentFile == null) {
                            continue;
                        }
                        String parentPath = parentFile.getAbsolutePath();

                        if (folderDirs.contains(parentPath)) {
                            //不新建imageFolder
                            continue;
                        } else {
                            //新建imageFolder  保存信息
                            folderDirs.add(parentPath);
                            imageFolder = new ImageFolder();
                            imageFolder.setFolderDir(parentPath);
                            imageFolder.setFirstImagePath(path);
                        }

                        //获得文件夹中图片的数量
                        int picCount = parentFile.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String filename) {
                                if (filename.endsWith(".jpg")
                                        || filename.endsWith(".png")
                                        || filename.endsWith(".jpeg"))
                                    return true;
                                return false;
                            }
                        }).length;
                        imageFolder.setCount(picCount);
                        QAccount.mImageFolders.add(imageFolder);
                    }
                }
                cursor.close();
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_LOADER) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String folderDir = bundle.getString("folderDir");
                File imgDir = new File(folderDir);
                List<String> arrayList = Arrays.asList(imgDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpg") || filename.endsWith(".png")
                                || filename.endsWith(".jpeg")) {
                            return true;
                        }
                        return false;
                    }
                }));
                imageList.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    ImageModel model = new ImageModel();
                    model.path = folderDir + '/' + arrayList.get(i);
                    imageList.add(model);
                }
                folderName.setText(imgDir.getName());
                mHandler.sendEmptyMessage(0x110);
            }
        } else if (requestCode == REQUEST_CLIP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}

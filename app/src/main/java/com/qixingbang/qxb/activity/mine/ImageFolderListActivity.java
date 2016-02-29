package com.qixingbang.qxb.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.chooseHeadPortrait.ImageFolder;
import com.qixingbang.qxb.adapter.mine.ImageFolderListAdapter;
import com.qixingbang.qxb.beans.QAccount;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Z.H. on 2015/11/13 16:04.
 */
public class ImageFolderListActivity extends Activity implements ImageFolderListAdapter.OnImageListItemSelected {

    @Bind(R.id.lv_image_folder)
    ListView mLvImageFolder;
    private ArrayList<ImageFolder> mImageFolders;
    private ImageFolderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_folder_list);
        ButterKnife.bind(this);
        mImageFolders = QAccount.mImageFolders;
        adapter = new ImageFolderListAdapter(this, mImageFolders);
        mLvImageFolder.setAdapter(adapter);
        adapter.setImageListItemSelected(this);
    }

    @Override
    public void OnSelected(ImageFolder folder) {
        Toast.makeText(ImageFolderListActivity.this, folder.getFolderName(), Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putString("folderDir", folder.getFolderDir());
        Intent intent = new Intent();
        intent.setAction("ImageFolder");
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}

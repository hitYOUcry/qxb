package com.qixingbang.qxb.common.photo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by cr30 on 2015/10/24.
 */
public class Photo {

    private Activity mcontext;
    private View mView;
//相机图库
    private int GALLERY_REQUEST=1;
    private int CAMEAR_REQUEST=2;

    private File tempFile = null;
    private File pa = null;
    private ImageButton mimageButton;

    public Photo(Activity context, ImageButton imageButton) {

        this.mcontext = context;
        this.mimageButton = imageButton;
    }

    public void Gallery() {
        Intent mintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mcontext.startActivityForResult(mintent, GALLERY_REQUEST);
    }

    public void Camera() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempFile = new File(pa, "tem");
            try {
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }
                Uri muri = Uri.fromFile(tempFile);
                mIntent.putExtra(MediaStore.EXTRA_OUTPUT, muri);
                mcontext.startActivityForResult(mIntent, CAMEAR_REQUEST);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Debug("No SD Card!");
        }


    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode==mcontext.RESULT_OK&&data!=null){
            switch (requestCode){
                case 1:
                    Uri galleyUri=data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = mcontext.getContentResolver().query(galleyUri,
                    filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    // 显示图片
                    mimageButton.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    break;
                default:
                    break;
            }
        }
    }*/
    public void Debug(String text) {
        Toast.makeText(mcontext, text, Toast.LENGTH_SHORT).show();
    }


}

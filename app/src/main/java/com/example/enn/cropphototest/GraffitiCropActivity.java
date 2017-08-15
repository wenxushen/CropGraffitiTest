package com.example.enn.cropphototest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by wxs on 2017/8/7.
 */

public class GraffitiCropActivity extends Activity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener,View.OnClickListener{

    private CropImageView mCropImageView;


    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if(error==null){
            Toast.makeText(this,"Image load successful",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Image load failed: "+error.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graffiti_crop);
        initView();
        mCropImageView.setImageBitmap(MainGraffitiActivity.bitmapTemp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCropImageView!=null){
            mCropImageView.setOnSetImageUriCompleteListener(null);
            mCropImageView.setOnCropImageCompleteListener(null);

        }

    }

    private void handleCropResult(CropImageView.CropResult result) {

        if (result.getError() == null) {
            Intent intent = new Intent(this, MainGraffitiActivity.class);
            intent.putExtra("SAMPLE_SIZE", result.getSampleSize());
            if (result.getUri() != null) {
                intent.putExtra("URI", result.getUri());
            } else {
                MainGraffitiActivity.mImage = mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
                        ? CropImage.toOvalBitmap(result.getBitmap())
                        : result.getBitmap();

            }

            finish();
            overridePendingTransition(R.anim.run_enter_anim,R.anim.run_exit_anim);
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(this, "Image crop failed: " + result.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void initView(){
        mCropImageView= (CropImageView) findViewById(R.id.crop_container);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
        findViewById(R.id.main_action_cancle).setOnClickListener(this);
        findViewById(R.id.main_action_repeat).setOnClickListener(this);
        findViewById(R.id.main_action_rotate).setOnClickListener(this);
        findViewById(R.id.main_action_save).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_action_cancle:
                finish();
                overridePendingTransition(R.anim.run_enter_anim,R.anim.run_exit_anim);
                break;
            case R.id.main_action_repeat:
                mCropImageView.resetCropRect();
                break;
            case R.id.main_action_rotate:
                mCropImageView.rotateImage(90);
                break;
            case R.id.main_action_save:
                mCropImageView.getCroppedImageAsync();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.run_enter_anim,R.anim.run_exit_anim);
    }
}

package com.example.enn.cropphototest;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.enn.cropphototest.graffiti.GraffitiListener;
import com.example.enn.cropphototest.graffiti.GraffitiSelectableItem;
import com.example.enn.cropphototest.graffiti.GraffitiText;
import com.example.enn.cropphototest.graffiti.GraffitiView;
import com.example.enn.cropphototest.graffiti.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wxs on 2017/8/3.
 */

public class MainGraffitiActivity extends Activity {

    public static final int RESULT_ERROR = -111; // 出现错误

    /**
     * The image to show in the activity.
     */
    static Bitmap mImage;

    public static final String KEY_PARAMS = "key_graffiti_params";
    public static final String KEY_IMAGE_PATH = "key_image_path";

    public static Bitmap bitmapTemp = null;

    private Bitmap mBitmap;

    private FrameLayout mFrameLayout;
    private GraffitiView mGraffitiView;
    private ImageView mRepeal;

    private View mGraffitiPenSelector,mSettingsPanel,mGraffitiSelectable;

    private SeekBar mPaintSizeBar;
    private View.OnClickListener mOnClickListener;

    private int saveTemp = 0;

    private AlphaAnimation mViewShowAnimation, mViewHideAnimation; // view隐藏和显示时用到的渐变动画

    // 触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mHideDelayRunnable;
    //触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mShowDelayRunnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImage = null;


        mBitmap = ImageUtils.createBitmapFromPath("/storage/emulated/0/DCIM/P70324-192253.jpg", this);
        if (mBitmap == null) {
            this.finish();
            return;
        }


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_graffiti_main);

        mFrameLayout = (FrameLayout) findViewById(R.id.main_graffiti_container);


        startGraffti();
        mFrameLayout.addView(mGraffitiView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mOnClickListener = new GraffitiOnClickListener();
        initView();
    }

    private class GraffitiOnClickListener implements View.OnClickListener {
        private View mLastColorView;
        private View mLastPenView;
        private boolean mDone = false;

        @Override
        public void onClick(View v) {
            mDone = false;
            if (v.getId() == R.id.graffiti_color_write) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.WHITE);
                } else {
                    mGraffitiView.setColor(Color.WHITE);
                }
                mDone = true;

            } else if (v.getId() == R.id.graffiti_color_black) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.BLACK);
                } else {
                    mGraffitiView.setColor(Color.BLACK);
                }
                mDone = true;
            } else if (v.getId() == R.id.graffiti_color_red) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.RED);
                } else {
                    mGraffitiView.setColor(Color.RED);
                }
                mDone = true;
            } else if (v.getId() == R.id.graffiti_color_yellow) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.parseColor("#FFFF00"));
                } else {
                    mGraffitiView.setColor(Color.parseColor("#FFFF00"));
                }
                mDone = true;
            } else if (v.getId() == R.id.graffiti_color_blue) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.BLUE);
                } else {
                    mGraffitiView.setColor(Color.BLUE);
                }
                mDone = true;
            } else if (v.getId() == R.id.graffiti_color_green) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.GREEN);
                } else {
                    mGraffitiView.setColor(Color.GREEN);
                }
                mDone = true;
            } else if (v.getId() == R.id.graffiti_color_purple) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.parseColor("#CC33FF"));
                } else {
                    mGraffitiView.setColor(Color.parseColor("#CC33FF"));
                }
                mDone = true;
            } else if (v.getId() == R.id.graffiti_color_pink) {

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemColor(Color.parseColor("#FF99FF"));
                } else {
                    mGraffitiView.setColor(Color.parseColor("#FF99FF"));
                }
                mDone = true;
            }

            if (mDone) {
                if (mLastColorView != null) {
                    mLastColorView.setSelected(false);
                }
                v.setSelected(true);
                mLastColorView = v;
                return;
            }


            if (v.getId() == R.id.main_graffiti_pen) {
                mGraffitiView.setPen(GraffitiView.Pen.HAND);
                mDone = true;
            }

            if (v.getId() == R.id.main_graffiti_text) {
                mGraffitiView.setPen(GraffitiView.Pen.TEXT);
                createGraffitiText(null, getResources().getDisplayMetrics().widthPixels / 2, getResources().getDisplayMetrics().heightPixels / 2);
                mDone = true;
            }
            if (mDone) {
                if (mLastPenView != null) {
                    mLastPenView.setSelected(false);
                }
                v.setSelected(true);
                mLastPenView = v;
                return;
            }


            if (v.getId() == R.id.main_graffiti_repeal) {

                mGraffitiView.undo();
                switchRepeal();
                mDone = true;
            }


            if (v.getId() == R.id.main_graffiti_cut) {
                saveTemp = 1;
                mImage = null;
                mGraffitiView.save();
                mDone = true;
            }

            if (v.getId() == R.id.main_graffiti_cancel) {
                finish();
                mDone = true;
            }

            if (v.getId() == R.id.main_graffiti_save) {
                saveTemp = 0;
                mGraffitiView.save();
                mDone = true;
            }

            if(v.getId() ==R.id.main_graffiti_selectable_edit){
                createGraffitiText((GraffitiText) mGraffitiView.getSelectedItem(), -1, -1);
                mDone=true;
            } else if (v.getId() ==  R.id.main_graffiti_selectable_dele) {
                mGraffitiView.removeSelectedItem();
                mDone = true;
            }
            if (mDone) {
                return;
            }
        }
    }

    public void switchRepeal(){
        if (!mGraffitiView.isModified()) {
            mRepeal.setImageResource(R.drawable.editing_repeal);
        } else {
            mRepeal.setImageResource(R.drawable.editing_repeal2x);
        }
    }
    private void createGraffitiText(final GraffitiText graffitiText, final float x, final float y) {
        Activity activity = this;

        boolean fullScreen = (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        Dialog dialog = null;
        if (fullScreen) {
            dialog = new Dialog(activity,
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        } else {
            dialog = new Dialog(activity,
                    android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        ViewGroup container = (ViewGroup) View.inflate(getApplicationContext(), R.layout.graffiti_create_text, null);
        final Dialog finalDialog = dialog;
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        dialog.setContentView(container);

        final EditText textView = (EditText) container.findViewById(R.id.graffiti_selectable_edit);
        final View cancelBtn = container.findViewById(R.id.graffiti_text_cancel_btn);
        final TextView enterBtn = (TextView) container.findViewById(R.id.graffiti_text_enter_btn);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = (textView.getText() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    enterBtn.setEnabled(false);
                    enterBtn.setTextColor(0xffb3b3b3);
                } else {
                    enterBtn.setEnabled(true);
                    enterBtn.setTextColor(0xff232323);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setText(graffitiText == null ? "" : graffitiText.getText());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn.setSelected(true);
                finalDialog.dismiss();
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (cancelBtn.isSelected()) {
                    mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                    return;
                }
                String text = (textView.getText() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (graffitiText == null) {
                    mGraffitiView.addSelectableItem(new GraffitiText(mGraffitiView.getPen(), text, mGraffitiView.getPaintSize(), mGraffitiView.getColor().copy(),
                            0, mGraffitiView.getGraffitiRotateDegree(), x, y, mGraffitiView.getOriginalPivotX(), mGraffitiView.getOriginalPivotY()));
                } else {
                    graffitiText.setText(text);
                }
                mGraffitiView.invalidate();
            }
        });

        if (graffitiText == null) {
            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
        }

    }

    public void initView() {
        findViewById(R.id.graffiti_color_write).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_color_black).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_color_red).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_color_blue).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_color_green).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_color_purple).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_color_pink).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_color_yellow).setOnClickListener(mOnClickListener);
        findViewById(R.id.main_graffiti_text).setOnClickListener(mOnClickListener);

        mPaintSizeBar = (SeekBar) findViewById(R.id.main_graffiti_pan_size);
        mGraffitiPenSelector = findViewById(R.id.main_graffiti_color_selector);
        mSettingsPanel=findViewById(R.id.main_graffiti_panel);
        findViewById(R.id.main_graffiti_pen).setOnClickListener(mOnClickListener);
        mRepeal=(ImageView) findViewById(R.id.main_graffiti_repeal);
        mRepeal.setOnClickListener(mOnClickListener);
        findViewById(R.id.main_graffiti_cut).setOnClickListener(mOnClickListener);
        findViewById(R.id.main_graffiti_cancel).setOnClickListener(mOnClickListener);
        findViewById(R.id.main_graffiti_save).setOnClickListener(mOnClickListener);

        mGraffitiSelectable=findViewById(R.id.main_graffiti_selectable);
        findViewById(R.id.main_graffiti_selectable_edit).setOnClickListener(mOnClickListener);
        findViewById(R.id.main_graffiti_selectable_dele).setOnClickListener(mOnClickListener);

        mPaintSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 5) {
                    mPaintSizeBar.setProgress(5);
                    return;
                }

                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemSize(progress);
                } else {
                    mGraffitiView.setPaintSize(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mGraffitiView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                        mSettingsPanel.removeCallbacks(mShowDelayRunnable);
                        mSettingsPanel.postDelayed(mHideDelayRunnable, 500); //触摸屏幕超过一定时间才判断为需要隐藏设置面板
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mSettingsPanel.removeCallbacks(mHideDelayRunnable);
                        mSettingsPanel.removeCallbacks(mShowDelayRunnable);
                        mSettingsPanel.postDelayed(mShowDelayRunnable, 500); //离开屏幕超过一定时间才判断为需要显示设置面板
                        break;
                }
                return false;
            }
        });

        mViewShowAnimation = new AlphaAnimation(0, 1);
        mViewShowAnimation.setDuration(300);
        mViewHideAnimation = new AlphaAnimation(1, 0);
        mViewHideAnimation.setDuration(300);
        mHideDelayRunnable = new Runnable() {
            public void run() {
                hideView(mSettingsPanel);
            }

        };
        mShowDelayRunnable = new Runnable() {
            public void run() {
                showView(mSettingsPanel);
            }
        };


    }

    public void startGraffti() {

        mGraffitiView = new GraffitiView(this, mBitmap, null, true,
                new GraffitiListener() {
                    @Override
                    public void onSaved(Bitmap bitmap, Bitmap bitmapEraser) {

                        if (bitmapEraser != null) {
                            bitmapEraser.recycle(); // 回收图片，不再涂鸦，避免内存溢出
                        }
                        if(saveTemp==1){
                            bitmapTemp=bitmap;

                            Intent intent=new Intent(MainGraffitiActivity.this,GraffitiCropActivity.class);
                            startActivity(intent);

                            overridePendingTransition(R.anim.run_enter_anim,R.anim.run_exit_anim);
                        }else {
                            File graffitiFile = null;
                            File file = null;
                            String savePath = null;
                            boolean isDir = false;

                            File dcimFile = new File(Environment.getExternalStorageDirectory(), "DCIM");
                            graffitiFile = new File(dcimFile, "Graffiti");
                            //　保存的路径
                            file = new File(graffitiFile, System.currentTimeMillis() + ".jpg");

                            graffitiFile.mkdirs();

                            FileOutputStream outputStream = null;

                            try {
                                outputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                                ImageUtils.addImage(getContentResolver(), file.getAbsolutePath());

                                Intent intent = new Intent();
                                intent.putExtra(KEY_IMAGE_PATH, file.getAbsolutePath());
                                setResult(Activity.RESULT_OK, intent);
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                                onError(GraffitiView.ERROR_SAVE, e.getMessage());
                            } finally {
                                if (outputStream != null) {
                                    try {
                                        outputStream.close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String msg) {
                        setResult(RESULT_ERROR);
                        finish();
                    }

                    @Override
                    public void onReady() {

                        mPaintSizeBar.setProgress((int) (mGraffitiView.getPaintSize() + 0.5f));
                        mPaintSizeBar.setMax(225);

                        findViewById(R.id.graffiti_color_red).performClick();
                        findViewById(R.id.main_graffiti_pen).performClick();
                    }

                    @Override
                    public void onSelectedItem(GraffitiSelectableItem selectableItem, boolean selected) {
                        if (selected) {
                            mGraffitiSelectable.setVisibility(View.VISIBLE);
                            //createGraffitiText((GraffitiText) mGraffitiView.getSelectedItem(), -1, -1);
                        }else{
                            mGraffitiSelectable.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCreateSelectableItem(GraffitiView.Pen pen, float x, float y) {
                        if (pen == GraffitiView.Pen.TEXT) {
                            createGraffitiText(null, x, y);
                        }
                    }

                    @Override
                    public void onAddGraffiti() {
                        switchRepeal();
                    }

                    @Override
                    public void onRemoveGraffiti() {
                        switchRepeal();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mImage != null) {
            mFrameLayout.removeView(mGraffitiView);
            mBitmap = mImage;
            startGraffti();
            mFrameLayout.addView(mGraffitiView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
    private void showView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        view.clearAnimation();
        view.startAnimation(mViewShowAnimation);
        view.setVisibility(View.VISIBLE);
        if (view == mSettingsPanel) {
            mGraffitiView.setAmplifierScale(-1);
        }
    }

    private void hideView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            if (view == mSettingsPanel && mGraffitiView.getAmplifierScale() > 0) {
                mGraffitiView.setAmplifierScale(-1);
            }
            return;
        }
        view.clearAnimation();
        view.startAnimation(mViewHideAnimation);
        view.setVisibility(View.GONE);
         if ((view == mSettingsPanel && mGraffitiView.getAmplifierScale() > 0)) {
            mGraffitiView.setAmplifierScale(-1);
        }
    }
}

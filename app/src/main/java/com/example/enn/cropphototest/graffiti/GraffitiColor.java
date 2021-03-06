package com.example.enn.cropphototest.graffiti;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * 画笔底色
 * Created by wxs on 2017/7/26.
 */

public class GraffitiColor {
    public enum Type{
        COLOR,           //颜色值
        BITMAP           //图片
    }

    private int mColor;
    private Bitmap mBitmap;
    private Type mType;
    private Shader.TileMode mTileX = Shader.TileMode.MIRROR;   //着色器的镜像
    private Shader.TileMode mTileY = Shader.TileMode.MIRROR;

    public GraffitiColor(int color){
        mType= Type.COLOR;
        mColor=color;
    }

    public GraffitiColor(Bitmap bitmap){
        mType= Type.BITMAP;
        mBitmap=bitmap;
    }

    public GraffitiColor(Bitmap bitmap, Shader.TileMode tileX, Shader.TileMode tileY) {
        mType = Type.BITMAP;
        mBitmap = bitmap;
        mTileX = tileX;
        mTileY = tileY;
    }

    public void initColor(Paint paint, Matrix matrix){
        if(mType== Type.COLOR){
            paint.setColor(mColor);
        }else if (mType == Type.BITMAP){
            BitmapShader shader = new BitmapShader(mBitmap,mTileX,mTileY);
            shader.setLocalMatrix(matrix);
            paint.setShader(shader);
        }
    }

    public void setColor(int color){
        mType= Type.COLOR;
        mColor=color;
    }

    public void setColor(Bitmap bitmap){
        mType= Type.BITMAP;
        mBitmap=bitmap;
    }

    public void setColor(Bitmap bitmap , Shader.TileMode tileX,Shader.TileMode tileY){
        mType= Type.BITMAP;
        mBitmap=bitmap;
        mTileX=tileX;
        mTileY=tileY;
    }

    public int getColor(){
        return mColor;
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }

    public Type getType(){
        return mType;
    }

    public GraffitiColor copy(){
        GraffitiColor color=null;
        if(mType == Type.COLOR){
            color=new GraffitiColor(mColor);
        }else{
            color=new GraffitiColor(mBitmap);
        }
        color.mTileX=mTileX;
        color.mTileY=mTileY;
        return color;
    }

}

package com.zhan.haoqi.bottle.view;/*
 *  Copyright (C) 2015, gelitenight(gelitenight@gmail.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.zhan.haoqi.bottle.R;

public class WaveView extends View {
    /**
     * +------------------------+
     * |<--wave length->        |______
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|____
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level
     * |                        |  |
     * |                        |  |
     * +------------------------+__|____
     */
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.05f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;

    public static final int DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#8CD7F7");
    public static final int DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#2CB9FF");
    // shader containing repeated waves
    private BitmapShader mWaveShader;
    // shader matrix
    private Matrix mShaderMatrix;
    // paint to draw wave
    private Paint mViewPaint;
    // paint to draw border

    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private float mDefaultWaveLength;
    private double mDefaultAngularFrequency;

    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;

    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
    private int mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;


    //云朵图片
    private Bitmap[] wind;
    private int wind_top;

    private int half_wh[][];

    private float speed[]={0.3f,0.2f,0.15f};

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        mShaderMatrix = new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);
        wind=new Bitmap[3];
        wind[0]= BitmapFactory.decodeResource(getResources(), R.mipmap.wind_big);
        wind[1]= BitmapFactory.decodeResource(getResources(), R.mipmap.wind_mid);
        wind[2]= BitmapFactory.decodeResource(getResources(), R.mipmap.wind_small);
        wind_top=getResources().getDisplayMetrics().heightPixels/12-wind[0].getHeight()/2;

        half_wh=new int[][]{{wind[0].getWidth()/2,wind[0].getHeight()/2},{wind[1].getWidth()/2,wind[1].getHeight()/2},{wind[2].getWidth()/2,wind[2].getHeight()/2}};
    }
    public void setWaveShiftRatio(float waveShiftRatio) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }
    public void setWaterLevelRatio(float waterLevelRatio) {
        if (mWaterLevelRatio != waterLevelRatio) {
            mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    /**
     * Set vertical size of wave according to <code>amplitudeRatio</code>
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     *                       <br/>Ratio of amplitude to height of WaveView.
     */
    public void setAmplitudeRatio(float amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio) {
            mAmplitudeRatio = amplitudeRatio;
            invalidate();
        }
    }

    public float getWaveLengthRatio() {
        return mWaveLengthRatio;
    }

    /**
     * Set horizontal size of wave according to <code>waveLengthRatio</code>
     *
     * @param waveLengthRatio Default to be 1.
     *                        <br/>Ratio of wave length to width of WaveView.
     */
    public void setWaveLengthRatio(float waveLengthRatio) {
        mWaveLengthRatio = waveLengthRatio;
    }
    boolean hasMeasure=false;

    private int width;
    private int height;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=getMeasuredWidth();
        height=getMeasuredHeight();
        if(width>0&&height>0){
            hasMeasure=true;
            createShader();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createShader();
    }

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private void createShader() {

        if(!hasMeasure||width<=0||height<=0){
            return;
        }
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width;
        mDefaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO;
        mDefaultWaveLength = width;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setAntiAlias(true);

        // Draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        final int endX = width + 1;
        final int endY = height + 1;

        float[] waveY = new float[endX];

        wavePaint.setColor(mBehindWaveColor);
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mDefaultAngularFrequency;
            float beginY = (float) (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);

            waveY[beginX] = beginY;
        }

        wavePaint.setColor(mFrontWaveColor);
        final int wave2Shift = (int) (mDefaultWaveLength / 4);
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
        }

        // use the bitamp to create the shader
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
    }

    private float xs[]={getResources().getDisplayMetrics().widthPixels/2,getResources().getDisplayMetrics().widthPixels/2+100,20};
    @Override
    protected void onDraw(Canvas canvas) {
        // modify paint shader according to mShowWave state
        if ( mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }

            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix.setScale(
                    mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                    mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                    0,
                    mDefaultWaterLevel);
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix.postTranslate(
                    mWaveShiftRatio * width,
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * height);

            // assign matrix to invalidate the shader
            mWaveShader.setLocalMatrix(mShaderMatrix);
            canvas.drawRect(0, 0, width,
                            height, mViewPaint);
        } else {
            mViewPaint.setShader(null);
        }
        //draw sky

         if(hasMeasure){
             float left=xs[0];
             int top=wind_top;
             float right=xs[0]+half_wh[0][0];
             int bottom=top+half_wh[0][1];

             if(xs[0]>=-half_wh[0][0]&&xs[0]<=width+half_wh[0][0]){
                 canvas.drawBitmap(wind[0],null,new RectF(left,top,right,bottom),null);
             }else if(xs[0]>0){
                 xs[0]=-half_wh[0][0]*1.2f;
             }




             left=xs[1];
             top+=wind[0].getHeight();
             right=xs[1]+half_wh[1][0];
             bottom=top+half_wh[1][1];

             if(xs[1]>=-half_wh[1][0]&&xs[1]<=width+half_wh[1][0]) {
                 canvas.drawBitmap(wind[1], null, new RectF(left, top, right, bottom), null);
             }else if(xs[1]>0){
                 xs[1]=-half_wh[1][0]*1.2f;
             }




             left=xs[2];
             top+=wind[1].getHeight();
             right=xs[2]+half_wh[2][0];
             bottom=top+half_wh[2][1];

             if(xs[2]>=-half_wh[2][0]&&xs[2]<=width+half_wh[2][0]) {
                 canvas.drawBitmap(wind[2],null,new RectF(left,top,right,bottom),null);
             }else if(xs[2]>0){
                 xs[2]=-half_wh[2][0]*1.2f;
             }
                 xs[0]+=speed[0];
                 xs[1]+=speed[1];
                 xs[2]+=speed[2];

         }
    }
}

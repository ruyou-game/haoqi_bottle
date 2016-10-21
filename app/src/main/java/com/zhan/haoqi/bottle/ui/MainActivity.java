package com.zhan.haoqi.bottle.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.util.DensityUtil;
import com.zhan.haoqi.bottle.util.WaveHelper;
import com.zhan.haoqi.bottle.view.WaveView;


public class MainActivity extends Activity {
    private WaveHelper mWaveHelper;
    private RelativeLayout beach_layout;
    private SoundPool sndPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBeachView();
        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        mWaveHelper = new WaveHelper(waveView);

        findViewById(R.id.send_bottle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),SendBottleActivity.class));
            }
        });
        findViewById(R.id.bottle_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),MineActivity.class));
            }
        });
    }


    private void addBeachView(){
        beach_layout= (RelativeLayout) findViewById(R.id.beach_layout);
        ImageView beachView=new ImageView(this);
        beachView.setBackgroundResource(R.mipmap.beach_bg);
        int height=getResources().getDisplayMetrics().heightPixels/3;
        RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams(-1,height);
        beach_layout.addView(beachView,param);

        RelativeLayout.LayoutParams landParam= (RelativeLayout.LayoutParams) findViewById(R.id.land).getLayoutParams();

        landParam.width=getResources().getDisplayMetrics().widthPixels/2;
        int top=getResources().getDisplayMetrics().heightPixels/6;
        landParam.setMargins(0,top,0,0);


        RelativeLayout.LayoutParams weaveParam= (RelativeLayout.LayoutParams) findViewById(R.id.weave_layout).getLayoutParams();
        weaveParam.height=getResources().getDisplayMetrics().heightPixels/2- DensityUtil.dip2px(getApplicationContext(),30);

        weaveAnim();
    }

    private void weaveAnim(){
          findViewById(R.id.above_weave).startAnimation(AnimationUtils.loadAnimation(this,R.anim.anim_wave));
          findViewById(R.id.bottom_weave).startAnimation(AnimationUtils.loadAnimation(this,R.anim.anim_wave_bottom));
    }


    boolean nextClickFinish=false;
    long lastBackPress;
    @Override
    public void onBackPressed() {
        long now=System.currentTimeMillis()/1000;
        if(nextClickFinish&&now-lastBackPress<2){
            super.onBackPressed();
        }else{
            nextClickFinish=true;
            lastBackPress=now;
            Toast.makeText(this,"在按一次推出应用",Toast.LENGTH_SHORT).show();
        }
    }

    boolean soundPoolInited=false;
    private void playMusic(){
        if(soundPoolInited){
            sndPool.resume(1);
            return;
        }
        sndPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0 ) ;
        sndPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                AudioManager am = (AudioManager) getApplicationContext() .getSystemService(Context.AUDIO_SERVICE);
                float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                float volumnRatio = volumnCurrent / audioMaxVolumn;
                sndPool.play( 1, volumnRatio,volumnRatio, 1, -1, 1) ;
                soundPoolInited=true;
            }
        });
          sndPool.load(this,R.raw.sea,1);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sndPool.pause(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nextClickFinish=false;
        mWaveHelper.start();
        playMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sndPool.stop(1);
        sndPool.release();
        sndPool=null;
    }
}

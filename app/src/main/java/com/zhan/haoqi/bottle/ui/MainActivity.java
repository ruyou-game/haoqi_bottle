package com.zhan.haoqi.bottle.ui;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.util.WaveHelper;
import com.zhan.haoqi.bottle.view.WaveView;


public class MainActivity extends Activity {
    private WaveHelper mWaveHelper;
    private RelativeLayout beach_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBeachView();
        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        mWaveHelper = new WaveHelper(waveView);
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


    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaveHelper.start();
    }
}

package com.zhan.haoqi.bottle.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zhan.haoqi.bottle.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zah on 2016/10/21.
 */
public class SendBottleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bottle);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.close)
    public void onClick() {
        finish();
    }
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.bottle_type_img:
               break;
           case R.id.bottle_type_audio:
               break;
           case R.id.bottle_type_question:
               break;
           case R.id.bottle_type_secret:
               break;
           case R.id.bottle_type_redpackage:
               break;
      }
    }
}

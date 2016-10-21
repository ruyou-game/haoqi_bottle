package com.zhan.haoqi.bottle.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.tencent.tauth.IUiListener;
import com.zhan.haoqi.bottle.R;

/**
 * Created by zah on 2016/10/21.
 */
public class MineActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

    }
    public void login(View v){
        IUiListener listener =new BaseU
        mTencent.login(this, SCOPE, listener);
    }
}

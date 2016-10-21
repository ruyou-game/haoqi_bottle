package com.zhan.haoqi.bottle.data;

import com.tencent.tauth.Tencent;

/**
 * Created by zah on 2016/10/21.
 */
public class UserManager {
    private  static  UserManager userManager;
    private UserManager(){}
    private Account account;
    public static  UserManager getInstance(){
        if(userManager==null){
            userManager=new UserManager();
        }
        return userManager;
    }

    public boolean isLogin(){
       return account!=null;
    }


    public void tencentAuth(){
        Tencent.createInstance()
    }
}

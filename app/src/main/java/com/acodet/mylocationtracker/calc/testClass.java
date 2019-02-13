package com.acodet.mylocationtracker.calc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.acodet.mylocationtracker.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class testClass extends AppCompatActivity implements interfacecalculate {
    static int c;
    LoginButton login_button;
    TextView tv_login;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.facebooklogin);
        login_button = (LoginButton) findViewById(R.id.login_button);
        tv_login =(TextView)findViewById(R.id.tv_login);
        callbackManager = CallbackManager.Factory.create();
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                tv_login.setText("Login Sucess \n" + loginResult.getAccessToken().getUserId()+ "\n" + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {

                tv_login.setText("Login Cancelled");

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void display() {

        System.out.println("Geek");
    }

    @Override
    public void show() {

        System.out.println(a);
    }


    @Override
    public void add() {

        c = a+b;

    }

    // Driver Code
    public static void main(String[] args) {
        testClass t = new testClass();
        t.display();
        System.out.println(a);
        System.out.println(c);
    }


}
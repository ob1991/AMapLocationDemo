package com.amap.location.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.location.demo.DB.NetUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 2017/05/24.
 */

public class main extends Activity {
    private final String url="http://202.118.16.50:8101/locationlogin.ashx";
    private Button bt1;
    private Button bt2;
    private EditText textname;
    private EditText textpassword;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what==1) {
                if(msg.obj!=null&& msg.obj.toString().equals("1")){
                    Toast.makeText(main.this, R.string.ok, Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(main.this,LocationModeSourceActivity.class);
                    intent.putExtra("name",textname.getText().toString());
                    intent.putExtra("password", textpassword.getText().toString());
                    startActivity(intent);
                }else{
                    Toast.makeText(main.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        textname = (EditText)findViewById(R.id.username);
        textpassword = (EditText)findViewById(R.id.password);
        bt1=(Button) findViewById(R.id.button_login);
        bt1.setOnClickListener(new mybuttonlistener());
        bt2=(Button) findViewById(R.id.button_regin);
        bt2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(main.this,LocationModeSourceActivity.class);
                startActivity(intent);
            }
        });
    }
    class sendValueToServer implements Runnable
    {
        Map<String, String> map;
        public sendValueToServer(Map<String, String> map) {
            this.map=map;
        }

        @Override
        public void run() {
            String result = NetUtils.getRequest(url, map);
            Message message = Message.obtain(handler, 1, result);
            handler.sendMessage(message);
        }
    }

    private void btnget()
    {
        String name;
        String password;
        try {
            name = textname.getText().toString();
            name = new String(name.getBytes("ISO8859-1"), "UTF-8");
            password = textpassword.getText().toString();
            password = new String(password.getBytes("ISO8859-1"), "UTF-8");
            Map<String, String> map=new HashMap<String, String>();
            map.put("name",name);
            map.put("password", password);
            new Thread(new sendValueToServer(map)).start();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }
    private class mybuttonlistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            btnget();
        }
    }
}

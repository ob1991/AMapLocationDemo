package com.amap.location.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.location.demo.DB.NewService;

import java.io.UnsupportedEncodingException;

/**
 * Created by Admin on 2017/05/24.
 */

public class main extends Activity {

    private Button bt1;
    private Button bt2;
    private Bundle savedInstanceState;
    private EditText textname;
    private EditText textpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.savedInstanceState = savedInstanceState;
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
    private class mybuttonlistener implements View.OnClickListener {
        boolean result=false;
        String name;
        String password;
        public void onClick(View v) {
            try {
                name = textname.getText().toString();
                name = new String(name.getBytes("ISO8859-1"), "UTF-8");
                password = textpassword.getText().toString();
                password = new String(password.getBytes("ISO8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                result = NewService.save(name,password,"http://127.0.0.1:8080/Register/ManageServlet");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(result){
                Toast.makeText(main.this, R.string.ok, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(main.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.lg.deepdreamer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lg.deepdreamer.R;
import com.example.lg.deepdreamer.server.RegisterAuth;

//메일 인증 엑티비티
public class RegisterAuthActivity extends AppCompatActivity {

    private EditText authEmail;
    //Button authBtn;
    RegisterAuth registerAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_auth);
        authEmail = (EditText) findViewById(R.id.authEmail);
        //authBtn = (Button) findViewById(R.id.authBtn);



    }
    public void authBtn(View v){
        if(authEmail.getText().toString().length()<1){
            Toast.makeText(RegisterAuthActivity.this,"이메일을 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else {
            String param = "u_email=" + authEmail.getText().toString()+"";
            Log.i("이메일 아이디 : ",param);
            registerAuth = new RegisterAuth(this);
            registerAuth.execute(param);
        }
    }

}


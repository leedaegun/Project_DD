package com.example.lg.deepdreamer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lg.deepdreamer.R;
import com.example.lg.deepdreamer.server.LoginDB;

//로그인 화면
public class LoginActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;
    private CheckBox auto_Login,save_ID;
    private Button registerBtn;
    private boolean loginChecked;
    private boolean save_ID_chk;
    private InputMethodManager inputMethodManager;
    public SharedPreferences setting;

    //private final int MY_PERMISSION_REQUEST_STORAGE = 200;
    private EditText et_Email,et_Pw;
    private String sEmail,sPw;
    private LoginDB loginDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //뒤로 두번 -> 종료
        backPressCloseHandler = new BackPressCloseHandler(this);
        if(!NetworkConnection()){//네트워크 상태확인
            NotConnected_showAlert();
        }


        et_Email = (EditText)findViewById(R.id.main_et_Email);//이메일 입력
        et_Pw = (EditText)findViewById(R.id.main_et_Pw);//패스워드 입력
        auto_Login = (CheckBox) findViewById(R.id.cb_Auto_Login);//자동로그인 체크박스
        save_ID = (CheckBox)findViewById(R.id.cb_save_ID);//아이디 저장 체크박스

        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);//로그인정보담을 setting
        loginChecked = setting.getBoolean("autoLogin",false);//자동로그인 boolean변수
        save_ID_chk = setting.getBoolean("saveID",false);//아이디저장 boolean변수

        inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        if(loginChecked){
            //자동로그인 체크되어있으면 ID,PW저장
            et_Email.setText(setting.getString("id", ""));
            et_Pw.setText(setting.getString("pw", ""));
            auto_Login.setChecked(true);

        }
        if(save_ID_chk){
            //아이디저장 체크되어있으면 ID저장
            et_Email.setText(setting.getString("id",""));
            save_ID.setChecked(true);

        }




        //패스워드 암호처리
        et_Pw.setInputType(InputType.TYPE_CLASS_TEXT);
        PasswordTransformationMethod et_Pw_fm = new PasswordTransformationMethod();
        et_Pw.setTransformationMethod(et_Pw_fm);




        //회원가입 버튼
        registerBtn = (Button)findViewById(R.id.registerBtn);
        registerBtn.setPaintFlags(registerBtn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);//텍스트에 밑줄
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);//Auth 생략
                startActivity(intent);

            }

        });

    }
    public void loginBtn(View v)
    {

        sEmail = et_Email.getText().toString();
        sPw = et_Pw.getText().toString();
        if(sEmail.length()<1){
            Toast.makeText(this,"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
        else if(sPw.length()<1){
            Toast.makeText(this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }

        else{
            String param = "u_email=" + sEmail + "&u_pw=" + sPw + "";
            //loginDB lDB = new loginDB();
            //lDB.execute();
            loginDB = new LoginDB(this);
            loginDB.execute(param);


        }



    }


    @Override
    protected void onStop() {
        super.onStop();

        if(auto_Login.isChecked()) {
             setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
             SharedPreferences.Editor editor = setting.edit();

             // if autoLogin Checked, save values
             editor.putString("id", sEmail);
             editor.putString("pw", sPw);
             editor.putBoolean("autoLogin", true);

             editor.commit();
         }else{
             setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
             SharedPreferences.Editor editor = setting.edit();

             editor.clear();
             editor.commit();
         }

         if(save_ID.isChecked()){
             setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
             SharedPreferences.Editor editor = setting.edit();

             editor.putString("id", sEmail);
             editor.putBoolean("saveID", true);
             editor.commit();

             Log.d("id",setting.getString("id","NOT"));
         }
         else{
             setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
             SharedPreferences.Editor editor = setting.edit();

             editor.clear();
             editor.commit();
         }


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();

    }
    // 취소 두 번 종료
    private class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;
        private BackPressCloseHandler(Activity context) { this.activity = context; }
        private void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return; }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel(); }
        }
        private void showGuide() {
            toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show(); }
    }
    public void loginFocusOut(View v)
    {
        //inputMethodManager.hideSoftInputFromWindow(et_Email.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(et_Pw.getWindowToken(), 0);
    }
    private void NotConnected_showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.\n" + "먼저 무선네트워크 연결상태를 확인해 주세요.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // exit
                        //application 프로세스를 강제 종료
                        android.os.Process.killProcess(android.os.Process.myPid() );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
    private boolean NetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}

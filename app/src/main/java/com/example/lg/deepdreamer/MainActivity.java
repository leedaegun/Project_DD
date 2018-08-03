package com.example.lg.deepdreamer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
    private CheckBox auto_Login,save_ID;
    boolean loginChecked;
    boolean save_ID_chk;
    boolean successLogin;
    public SharedPreferences setting;
    //SharedPreferences.Editor editor;
    final int MY_PERMISSION_REQUEST_STORAGE = 200;
    String s;
    EditText et_Email,et_Pw;
    String sEmail,sPw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //권한체크
        checkPermission();

        //뒤로 두번 -> 종료
        backPressCloseHandler = new BackPressCloseHandler(this);

        successLogin = false;

        et_Email = (EditText)findViewById(R.id.main_et_Email);//이메일 입력
        et_Pw = (EditText)findViewById(R.id.main_et_Pw);//패스워드 입력
        auto_Login = (CheckBox) findViewById(R.id.cb_Auto_Login);//자동로그인 체크박스
        save_ID = (CheckBox)findViewById(R.id.cb_save_ID);//아이디 저장 체크박스

        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);//로그인정보담을 setting
        loginChecked = setting.getBoolean("autoLogin",false);//자동로그인 boolean변수
        save_ID_chk = setting.getBoolean("saveID",false);//아이디저장 boolean변수


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
        Button registerBtn = (Button)findViewById(R.id.registerBtn);
        registerBtn.setPaintFlags(registerBtn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);//텍스트에 밑줄
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
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

            loginDB lDB = new loginDB();
            lDB.execute();
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

    public class loginDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_email=" + sEmail + "&u_pw=" + sPw + "";

            Log.e("POST",param);
            try {
            /* 서버연결 */
                URL url = new URL("http://192.168.0.17/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;


                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



            if(data.equals("1"))
            {
                Log.e("RESULT","로그인 성공!");

                setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();

                editor.putString("success_ID",sEmail);
                editor.putString("success_PW",sPw);

                editor.commit();

                Intent intent = new Intent(MainActivity.this, RealMainActivity.class);
                startActivity(intent);
                finish();
            }
            else if(data.equals("0"))
            {
                Log.e("RESULT","비밀번호가 일치하지 않습니다.");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("비밀번호가 일치하지 않습니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + data);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("로그인 중 에러가 발생했습니다! errcode : "+ data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();

    }
    public class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;
        public BackPressCloseHandler(Activity context) { this.activity = context; }
        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return; }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel(); }
        }
        public void showGuide() {
            toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show(); }
    }


    //어플리케이션 권한 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "Permission Allow");

                    //알림창에서 권한을 허용했을때
                } else {
                    Log.d("PERMISSION", "Permission always deny");
                    finish();
                    //알림창에서 권한을 거부했을때 앱종료
                }
                break;
        }

    }
    private boolean checkPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                //권한 허용한적이 없을때
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    //권한을 한번이라도 거부했을때
                    Toast.makeText(this, "please Read Phone State", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSION_REQUEST_STORAGE);
                //권한 허용할것인지 알림창 띄우기
            } else {

                // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
                return true;
            }
        }
        return true;
    }
}

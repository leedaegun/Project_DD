package com.example.lg.deepdreamer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    EditText et_Name, et_Email,et_Pw, et_pw_chk;
    String sName, sEmail,sPw, sPw_chk,birth,sex;
    Spinner spinner;
    Calendar mCalendar;
    CheckBox cb_male,cb_female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = new  Intent(this.getIntent());

        et_Name = (EditText) findViewById(R.id.et_Name);//이름
        et_Email=(EditText)findViewById(R.id.et_Email);//이메일
        et_Pw = (EditText) findViewById(R.id.et_Pw);//패스워드
        et_pw_chk = (EditText) findViewById(R.id.et_pw_chk);//패스워드 체크
        spinner = (Spinner)findViewById(R.id.spinner);//생년월일 스피너
        cb_female = (CheckBox)findViewById(R.id.cb_female);//여자 체크박스
        cb_male = (CheckBox)findViewById(R.id.cb_male);//남자 체크박스
        //패스워드 *표시
        et_Pw.setInputType(InputType.TYPE_CLASS_TEXT);
        PasswordTransformationMethod et_Pw_fm = new PasswordTransformationMethod();
        et_Pw.setTransformationMethod(et_Pw_fm);
        //패스워드 확인 *표시
        et_pw_chk.setInputType(InputType.TYPE_CLASS_TEXT);
        PasswordTransformationMethod et_pw_chk_fm = new PasswordTransformationMethod();
        et_pw_chk.setTransformationMethod(et_pw_chk_fm);

        //리스트에 현재 기준 년수 -100 부터
        mCalendar = Calendar.getInstance();
        final ArrayList<String> list = new ArrayList<>();
        for(int i = 0;i<100;i++){
            list.add(Integer.toString(mCalendar.get(Calendar.YEAR)-i));
        }

        // 스피너 커스텀마이징

        final ArrayAdapter<String> spinnerAdapter= new ArrayAdapter<String>(RegisterActivity.this,R.layout.spinner_item,list){

            @NonNull
            @Override

            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquarel.ttf");//스피너 폰트
                //((TextView) v).setTypeface(externalFont);

                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {

                View v =super.getDropDownView(position, convertView, parent);

                //Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquarel.ttf");//스피너 폰트
                //((TextView) v).setTypeface(externalFont);

                //v.setBackgroundColor(Color.BLUE);//스피너 배경
                //((TextView) v).setTextColor(Color.WHITE);//스피너 글자색
                return v;
            }

        };
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(spinnerAdapter);
        

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(RegisterActivity.this,"선택된 아이템 : "+spinner.getItemAtPosition(position),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);

            }
        });
        cb_male.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!cb_female.isChecked()){
                    if(cb_male.isChecked()){
                        sex = "Male";
                    }
                }
                else{
                    cb_female.setChecked(false);
                    if(cb_male.isChecked()){
                        sex = "Male";
                    }
                }
                Log.e("Sex : ",sex);
            }
        });
        cb_female.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!cb_male.isChecked()){
                    if(cb_female.isChecked()){
                        sex = "Female";
                    }
                }
                else{
                    cb_male.setChecked(false);
                    if(cb_female.isChecked()){
                        sex = "Female";
                    }
                }
                Log.e("Sex : ",sex);
            }
        });


    }

    public  void bt_register(View v){

        //버튼이 동작할때
        sName = et_Name.getText().toString();
        sEmail = et_Email.getText().toString();
        sPw = et_Pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();
        birth = spinner.getSelectedItem().toString();

        if(sName.length()<1){
            Toast.makeText(RegisterActivity.this,"이름을 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else if(sEmail.length()<1){
            Toast.makeText(RegisterActivity.this,"이메일을 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else if(sPw.length()<1){
            Toast.makeText(RegisterActivity.this,"패스워드를 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else if(sPw_chk.length()<1){
            Toast.makeText(RegisterActivity.this,"패스워드를 확인해주세요.",Toast.LENGTH_LONG).show();
        }
        else if(!(cb_male.isChecked()||cb_female.isChecked())){
            Toast.makeText(RegisterActivity.this,"성별을 체크해주세요.",Toast.LENGTH_LONG).show();
        }
        else{
            if(sPw.equals(sPw_chk)){
                //pw 일치시
                registDB rdb = new registDB();
                rdb.execute();

            }
            else{
                //pw 불일치시
                Toast.makeText(RegisterActivity.this,"패스워드가 다릅니다.",Toast.LENGTH_LONG).show();
            }
        }

    }
    public class registDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisterActivity.this);

        @Override
        protected Void doInBackground(Void... unused) {

/* 인풋 파라메터값 생성 */
            String param = "u_id=" + sName + "&u_email=" + sEmail + "&u_pw=" + sPw  + "&u_birth=" + birth + "&u_sex=" + sex + "";
            try {
/* 서버연결 */
                URL url = new URL("http://192.168.0.17/register.php");
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
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

            Log.e("RECV DATA",data);

            if(data.equals("0"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("성공적으로 등록되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
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
                        .setMessage("등록중 에러가 발생했습니다! errcode : "+ data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }





        }

    }





}


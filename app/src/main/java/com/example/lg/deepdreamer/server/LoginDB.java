package com.example.lg.deepdreamer.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lg.deepdreamer.activity.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by lg on 2018-09-02.
 */

public class LoginDB extends AsyncTask<String, Integer, String> {
    private Context context;
    String data ;
    ManagerServer managerServer;
    //public SharedPreferences setting;
    HttpURLConnection conn;
    public LoginDB(Context context){
        this.context = context;

    }
    @Override
    protected String doInBackground(String... param) {

            /* 인풋 파라메터값 생성 */

        Log.e("POST",param[0]);
        try {
            /* 서버연결 */
            URL url = new URL(managerServer.getLoginIP());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();
            //setCookieHeader();

                /* 안드로이드 -> 서버 파라메터값 전달 */
            OutputStream outs = conn.getOutputStream();
            outs.write(param[0].getBytes("UTF-8"));
            outs.flush();
            outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
            InputStream is = null;
            BufferedReader in = null;

            //BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            is = conn.getInputStream();
            //in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
            in = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line = null;
            StringBuilder buff = new StringBuilder();
            while ( ( line = in.readLine() ) != null )
            {
                buff.append(line );//+ "\n"
            }
            in.close();
            data = buff.toString().trim();//
            return data;
            //getCookieHeader();


        } catch (MalformedURLException e) {
            e.printStackTrace();

            return null;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

    }
    @Override
    protected void onPostExecute(String result) {//Void aVoid
        //super.onPostExecute(aVoid);
        super.onPostExecute(result);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        //Log.e("조건문전에data",data.getBytes().toString());
        try{

            data = URLEncoder.encode(data,"EUC-KR");

        }catch (UnsupportedEncodingException e1){
            e1.printStackTrace();
        }
        Log.e("변환후 data",data.getBytes().toString());
        if(data.equals("%3F1"))//data.equals("1")
        {
            Log.e("RESULT","로그인 성공!");


            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
        else if(data.equals("%3Fnotfind"))
        {
            Log.e("RESULT","비밀번호가 일치하지 않습니다.");
            alertBuilder
                    .setTitle("알림")
                    .setMessage("이메일과 비밀번호를 확인해주세요.")
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
    private void getCookieHeader(){//Set-Cookie에 배열로 돼있는 쿠키들을 스트링 한줄로 변환
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
        //cookies -> [JSESSIONID=D3F829CE262BC65853F851F6549C7F3E; Path=/smartudy; HttpOnly] -> []가 쿠키1개임.
        //Path -> 쿠키가 유효한 경로 ,/smartudy의 하위 경로에 위의 쿠키를 사용 가능.
        if (cookies != null) {
            for (String cookie : cookies) {
                String sessionid = cookie.split(";\\s*")[0];
                //JSESSIONID=FB42C80FC3428ABBEF185C24DBBF6C40를 얻음.
                //세션아이디가 포함된 쿠키를 얻었음.
                setSessionIdInSharedPref(sessionid);

            }
        }
    }
    private void setSessionIdInSharedPref(String sessionid){
        SharedPreferences pref = context.getSharedPreferences("sessionCookie",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        if(pref.getString("sessionid",null) == null){ //처음 로그인하여 세션아이디를 받은 경우
            Log.d("LOG","처음 로그인하여 세션 아이디를 pref에 넣었습니다."+sessionid);
        }else if(!pref.getString("sessionid",null).equals(sessionid)){ //서버의 세션 아이디 만료 후 갱신된 아이디가 수신된경우
            Log.d("LOG","기존의 세션 아이디"+pref.getString("sessionid",null)+"가 만료 되어서 "
                    +"서버의 세션 아이디 "+sessionid+" 로 교체 되었습니다.");
        }
        edit.putString("sessionid",sessionid);
        edit.apply(); //비동기 처리
    }
    private void setCookieHeader(){
        SharedPreferences pref = context.getSharedPreferences("sessionCookie",Context.MODE_PRIVATE);
        String sessionid = pref.getString("sessionid",null);
        if(sessionid!=null) {
            Log.d("LOG","세션 아이디"+sessionid+"가 요청 헤더에 포함 되었습니다.");
            conn.setRequestProperty("Cookie", sessionid);
        }
    }


}


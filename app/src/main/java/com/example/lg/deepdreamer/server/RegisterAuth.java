package com.example.lg.deepdreamer.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by lg on 2018-09-23.
 */

public class RegisterAuth extends AsyncTask<String, Integer, String> {

    private Context context;
    private String data = "";
    ManagerServer managerServer;

    public RegisterAuth(Context context){
        this.context = context;
    }
    @Override
    protected String doInBackground(String... param) {

/* 인풋 파라메터값 생성 */
        try {
/* 서버연결 */
            Log.e("RECV param",param[0]);
            //인증이랑 등록 IP를 파라미터로 넣어 사용하기
            URL url = new URL(managerServer.getRegisterAuthIP());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();

/* 안드로이드 -> 서버 파라메터값 전달 */
            OutputStream outs = conn.getOutputStream();
            outs.write(param[0].getBytes("UTF-8"));
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
    protected void onPostExecute(String s){
        super.onPostExecute(s);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        Log.e("RECV DATA",data);
        try{

            data = URLEncoder.encode(data,"EUC-KR");

        }catch (UnsupportedEncodingException e1){
            e1.printStackTrace();
        }

        if(data.equals("%3F1"))//존재하는 아이디
        {
            alertBuilder
                .setTitle("알림")
                .setMessage("이미 존재하는 아이디입니다.")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
        else
        {alertBuilder
                .setTitle("알림")
                .setMessage("사용가능한 아이디입니다.")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }

    }
}

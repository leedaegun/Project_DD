package com.example.lg.deepdreamer.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lg.deepdreamer.activity.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lg on 2018-09-02.
 */

public class LoginDB extends AsyncTask<String, Integer, Void> {
    private Context context;
    String data = "";
    ManagerServer managerServer;
    //public SharedPreferences setting;

    public LoginDB(Context context){
        this.context = context;

    }
    @Override
    protected Void doInBackground(String... param) {

            /* 인풋 파라메터값 생성 */

        Log.e("POST",param[0]);
        try {
            /* 서버연결 */
            URL url = new URL(managerServer.getLoginIP());
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);


        if(data.equals("1"))
        {
            Log.e("RESULT","로그인 성공!");


            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
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

package com.example.lg.deepdreamer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class LoadingActivity extends AppCompatActivity {

    private final int MY_PERMISSION_REQUEST_STORAGE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{
            checkPermission();
            Thread.sleep(1000);     //대기 초 설정
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,LoginActivity.class));//메인(로그인)액티비티에서 시작
        finish();
    }

    //어플리케이션 권한 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                /*if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "Permission Allow");

                    //알림창에서 권한을 허용했을때
                } else {
                    Log.d("PERMISSION", "Permission always deny");
                    finish();
                    //알림창에서 권한을 거부했을때 앱종료
                }
                break;*/
                if (grantResults.length > 0)
                {
                    for (int i=0; i<grantResults.length; ++i)
                    {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                        {
                            // 하나라도 거부한다면.
                            /*
                            new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                    getApplicationContext().startActivity(intent);
                                }
                            }).setCancelable(false).show();
                            return;*/
                            finish();
                        }
                    }
                    //startApp();
                }
        }

    }
    private boolean checkPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)//녹음 권한 -> 녹음 측정시
                    != PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)//외부저장소 권한 -> 녹음한거 저장
                            != PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) //센서 권한 -> 자이로센서측정
                            != PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)//주소록일기권한-> 회원가입시 이메일 전송
                            != PackageManager.PERMISSION_GRANTED) {
                //권한 허용한적이 없을때

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    //권한을 한번이라도 거부했을때
                    Toast.makeText(this, "please Read Phone State", Toast.LENGTH_SHORT).show();
                }
                //권한 목록 4개
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.BODY_SENSORS}, MY_PERMISSION_REQUEST_STORAGE);
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},MY_PERMISSION_REQUEST_STORAGE);
                //권한 허용할것인지 알림창 띄우기
            } else {

                // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
                return true;
            }
        }
        return true;
    }
}

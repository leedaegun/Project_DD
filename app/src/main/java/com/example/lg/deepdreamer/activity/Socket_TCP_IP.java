package com.example.lg.deepdreamer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lg.deepdreamer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Socket_TCP_IP extends AppCompatActivity {


    Button btn;
    TextView tv;

    //  TCP연결 관련
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 7777;
    private final String ip = "192.168.0.8";
    private MyHandler myHandler;
    private MyThread myThread;

    private  File file ;
    private FileReader fileReader = null ;
    private BufferedReader bufferedReader =null;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket__tcp__ip);

            // StrictMode는 개발자가 실수하는 것을 감지하고 해결할 수 있도록 돕는 일종의 개발 툴
            // - 메인 스레드에서 디스크 접근, 네트워크 접근 등 비효율적 작업을 하려는 것을 감지하여
            //   프로그램이 부드럽게 작동하도록 돕고 빠른 응답을 갖도록 함, 즉  Android Not Responding 방지에 도움
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                clientSocket = new Socket(ip, port);
                socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            myHandler = new MyHandler();
            myThread = new MyThread();
            myThread.start();

            btn = (Button) findViewById(R.id.btn);
            tv = (TextView) findViewById(R.id.tv);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        // open file.
                        file = new File("file.txt") ;
                        fileReader = new FileReader(file) ;
                        bufferedReader = new BufferedReader(fileReader);
                        // read file.
                        String line = null;
                        String buf=null;
                        while ((line = bufferedReader.readLine()) != null) {
                            // TODO : use data
                            buf+=line;
                            //ch = (char) data ;
                            //System.out.println("ch : " + ch) ;
                        }
                        bufferedReader.close();
                        fileReader.close() ;
                        socketOut.println(buf);
                    } catch (Exception e) {
                        e.printStackTrace() ;
                    }

                }
            });
        }

        class MyThread extends Thread {
            @Override
            public void run() {
                while (true) {
                    try {
                        // InputStream의 값을 읽어와서 data에 저장
                        String data = socketIn.readLine();
                        // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                        Message msg = myHandler.obtainMessage();
                        msg.obj = data;
                        myHandler.sendMessage(msg);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        class MyHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                tv.setText(msg.obj.toString());
            }
        }


}

package com.example.lg.deepdreamer.server;

/**
 * Created by lg on 2018-09-01.
 */

public class ManagerServer {
    private static String serverIP= "http://192.168.0.89/";
    private static String loginPHP = "login.php";
    private static String registerPHP = "register.php";
    private static String transportPHP = "transport.php";
    public String getServerIP(){
        return serverIP;
    }

    public static String getLoginIP(){
        return serverIP+loginPHP;
    }
    public static String getRegisterIP(){
        return serverIP;
    }
    public static String getTransportIP(){
        return serverIP;
    }
}

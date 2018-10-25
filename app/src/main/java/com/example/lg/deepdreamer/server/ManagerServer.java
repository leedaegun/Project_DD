package com.example.lg.deepdreamer.server;

/**
 * Created by lg on 2018-09-01.
 */

public class ManagerServer {
    private final String serverIP= "http://192.168.0.89/";
    private static final String loginPHP = "http://192.168.0.9/login.php";
    private static final String registerPHP = "http://192.168.0.9/register.php";
    private static final String transportPHP = "http://192.168.0.9/transport.php";
    private static final String registerAuthPHP = "http://192.168.0.9/registerAuth.php";

    public  String getServerIP(){
        return serverIP;
    }

    public static String getLoginIP(){
        return loginPHP;
    }
    public static String getRegisterIP(){
        return registerPHP;
    }
    public static String getRegisterAuthIP(){
        return registerAuthPHP;
    }
    public static String getTransportIP(){
        return transportPHP;
    }
}

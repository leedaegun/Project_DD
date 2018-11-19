package com.example.lg.deepdreamer.server;

/**
 * Created by lg on 2018-09-01.
 */

public class ManagerServer {
    //52.79.227.49
    private static final String loginPHP = "http://52.79.227.49/login.php";
    private static final String registerPHP = "http://52.79.227.49/register.php";
    private static final String transportPHP = "http://52.79.227.49/transport.php";
    private static final String registerAuthPHP = "http://52.79.227.49/registerAuth.php";


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

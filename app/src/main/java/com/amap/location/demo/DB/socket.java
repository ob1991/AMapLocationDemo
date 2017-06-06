package com.amap.location.demo.DB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Created by Admin on 2017/06/04.
 */

public class socket extends Thread {
    private String ip = "192.168.16.254";
    private int port = 8080;
    private String TAG = "socket thread";
    private int timeout = 10000;

    public Socket client = null;
    PrintWriter out;
    BufferedReader in;
    public boolean isRun = true;
    Handler inHandler;
        int time;
    java.util.Timer timer = new java.util.Timer(true);
    public socket(Handler handlerin,String ip,int port,int time) {
        inHandler = handlerin;
        this.ip=ip;
        this.port=port;
        this.time=time;
//        MyLog.i(TAG, "创建线程socket");
    }

    /**
     * 连接socket服务器
     */
    public void conn() {

        try {
            Log.i(TAG, "连接中……");
            client = new Socket(ip, port);
            client.setSoTimeout(timeout);// 设置阻塞时间
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream())), true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            conn();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 实时接受数据
     */
    @Override
    public void run() {
        conn();
        TimerTask task = new TimerTask() {
            public void run() {
            //// TODO: 2017/06/05
                Send(toStringHex("0203000100035438"));
            }
        };
        String line = "";
        timer.schedule(task, 20, time);
        while (isRun) {
            try {
                if (client != null) {
                    while ((line = in.readLine()) != null) {
                        Message msg = inHandler.obtainMessage();
                        msg.obj = line;
                        inHandler.sendMessage(msg);// 结果返回给UI处理
                    }
                } else {
                    conn();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送数据
     *
     * @param mess
     */
    public void Send(String mess) {
        try {
            if (client != null) {
                out.println(mess);
                out.flush();
            } else {
                conn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
        }
    }
    public String toStringHex(String s)
    {
        byte[] baKeyword = new byte[s.length()/2];
        for(int i = 0; i < baKeyword.length; i++)
        {
            try
            {
                baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return s;
    }
    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (client != null) {
//                MyLog.i(TAG, "close in");
                in.close();
//                MyLog.i(TAG, "close out");
                out.close();
//                MyLog.i(TAG, "close client");
                client.close();
                timer.cancel();
            }
        } catch (Exception e) {
//            MyLog.i(TAG, "close err");
            e.printStackTrace();
        }
    }
}
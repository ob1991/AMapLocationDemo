package com.amap.location.demo.DB;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

/**
 * Created by Admin on 2017/06/05.
 */

public class mysocket {
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
    public mysocket(Handler handlerin,String ip,int port,int time) {
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
//            MyLog.i(TAG, "连接成功");
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream())), true);
//            MyLog.i(TAG, "输入输出流获取成功");
        } catch (UnknownHostException e) {
//            MyLog.i(TAG, "连接错误UnknownHostException 重新获取");
            e.printStackTrace();
            conn();
        } catch (IOException e) {
//            MyLog.i(TAG, "连接服务器io错误");
            e.printStackTrace();
        } catch (Exception e) {
//            MyLog.i(TAG, "连接服务器错误Exception" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 实时接受数据
     */
    public void run() {
//        MyLog.i(TAG, "线程socket开始运行");
        conn();
        TimerTask task = new TimerTask() {
            public void run() {
                // TODO: 2017/06/05
                Send(toStringHex("0203000100035438"));
            }
        };
//        MyLog.i(TAG, "1.run开始");
        String line = "";
        timer.schedule(task, 20, time);
        while (isRun) {
            try {
                if (client != null) {
//                    MyLog.i(TAG, "2.检测数据");
                    while ((line = in.readLine()) != null) {
//                        MyLog.i(TAG, "3.getdata" + line + " len=" + line.length());
//                        MyLog.i(TAG, "4.start set Message");
                        Message msg = inHandler.obtainMessage();
                        msg.obj = line;
                        inHandler.sendMessage(msg);// 结果返回给UI处理
//                        MyLog.i(TAG1, "5.send to handler");
                    }
                } else {
//                    MyLog.i(TAG, "没有可用连接");
                    conn();
                }
            } catch (Exception e) {
//                MyLog.i(TAG, "数据接收错误" + e.getMessage());
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
            if (client != null)
            {
                out.println(mess);
                out.flush();
            } else {
                conn();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
                in.close();
                out.close();
                client.close();
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.amap.location.demo.DB;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hp on 2017/5/24.
 */

public class NewService {
    /**
     * 登录验证
     * @param name 姓名
     * @param password 密码
     * @return
     */
    public static boolean save(String name, String password, String Path){
        String path = Path;
        Map<String, String> student = new HashMap<String, String>();
        student.put("name", name);
        student.put("password", password);
        try {
            return SendGETRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 发送GET请求
     * @param path  请求路径
     * @param student  请求参数
     * @return 请求是否成功
     * @throws Exception
     */
    private static boolean SendGETRequest(String path, Map<String, String> student, String ecoding) throws Exception {
        // http://127.0.0.1:8080/Register/ManageServlet?name=1233&password=abc
        StringBuilder url = new StringBuilder(path);
        url.append("?");
        for(Map.Entry<String, String> map : student.entrySet()){
            url.append(map.getKey()).append("=");
            url.append(URLEncoder.encode(map.getValue(), ecoding));
            url.append("&");
        }
        url.deleteCharAt(url.length()-1);
        System.out.println(url);
        HttpsURLConnection conn = (HttpsURLConnection)new URL(url.toString()).openConnection();
        conn.setConnectTimeout(1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 1){
            return true;
        }
        return false;
    }
}
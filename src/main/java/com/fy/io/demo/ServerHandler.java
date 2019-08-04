package com.fy.io.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerHandler implements Runnable {
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        String inputContent;
        System.out.println(stringNowTime() + ": id为" + socket.hashCode()+ "的Clientsocket connected");
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((inputContent = reader.readLine()) != null) {
                System.out.println("收到id为" + socket.hashCode() + "  "+inputContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("id为" + socket.hashCode()+ "的Clientsocket "+stringNowTime()+"读取结束");
    }

    public String stringNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}

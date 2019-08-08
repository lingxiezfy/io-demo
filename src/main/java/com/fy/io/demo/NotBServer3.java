package com.fy.io.demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotBServer3 {
    public void initBIOServer(int port) {
        //服务端Socket
        ServerSocket serverSocket = null;
        //客户端socket
        Socket socket = null;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            System.out.println(stringNowTime() + ": serverSocket started");
            while(true) {
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    //运行到这里表示本次accept是没有收到任何数据的，服务端的主线程在这里可以做一些其他事情
                    System.out.println("now time is: " + stringNowTime());
                    continue;
                }
                threadPool.execute(new ServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                assert socket != null;
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String stringNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static void main(String[] args) {
        NotBServer3 server = new NotBServer3();
        server.initBIOServer(8888);
    }
}

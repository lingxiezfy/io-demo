package com.fy.io.demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThirdServer {
    public void initBIOServer(int port) {
        //服务端Socket
        ServerSocket serverSocket = null;
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        try {
            serverSocket = new ServerSocket(port);

            System.out.println(stringNowTime() + ": serverSocket started");
            while(true) {
                Socket socket = serverSocket.accept();
                // 针对每一个socket创建一个线程为其处理
                executorService.execute(new ServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                assert serverSocket != null;
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String stringNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static void main(String[] args) {
        BIOServer server = new BIOServer();
        server.initBIOServer(8888);

    }
}

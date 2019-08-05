package com.fy.io.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotBServer2 {
    public void initBIOServer(int port)
    {
        ServerSocket serverSocket;//服务端Socket
        Socket socket;//客户端socket
        ClientSocketThread thread ;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(stringNowTime() + ": serverSocket started");
            while(true) {
                socket = serverSocket.accept();
                System.out.println(stringNowTime() + ": id为" + socket.hashCode()+ "的Clientsocket connected");
                thread = new ClientSocketThread(socket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String stringNowTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    class ClientSocketThread extends Thread {
        public Socket socket;

        public ClientSocketThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            String inputContent;
            int count = 0;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((inputContent = reader.readLine()) != null) {
                    System.out.println("收到id为" + socket.hashCode() + "  " + inputContent);
                    count++;
                }
                System.out.println("id为" + socket.hashCode() + "的Clientsocket " + stringNowTime() + "读取结束");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        NotBServer2 server = new NotBServer2();
        server.initBIOServer(8888);

    }
}

package com.fy.io.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BIOServer {
    public void initBIOServer(int port)
    {
        //服务端Socket
        ServerSocket serverSocket = null;
        //客户端socket
        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(stringNowTime() + ": serverSocket started");
            while(true) {
                // 产生阻塞，阻塞主线程
                socket = serverSocket.accept();
                System.out.println(stringNowTime() + "Receive ClientSocket:" + socket.hashCode()+ " connected");
                try {
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    String inputContent;
                    while ((inputContent = reader.readLine()) != null) {
                        System.out.println("Receive socket:" + socket.hashCode() + ", " + inputContent);
                        writer.write("Success\n");
                        writer.flush();
                    }
                    System.out.println("Receive ClientSocket:" + socket.hashCode()+ " - "+stringNowTime()+" - End");
                }catch (Exception e){
                    System.out.println("Receive ClientSocket:" + socket.hashCode()+ " - "+stringNowTime()+" - Disconnect");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                assert reader != null;
                assert writer != null;
                assert socket != null;
                writer.close();
                reader.close();
                socket.close();
            } catch (Exception e) {
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

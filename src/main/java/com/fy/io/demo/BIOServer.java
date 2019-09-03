package com.fy.io.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class BIOServer {
    public void initBIOServer(int port)
    {
        //服务端Socket
        ServerSocket serverSocket = null;
        //客户端socket
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(stringNowTime() + ": serverSocket started");
            while(true) {
                // 产生阻塞，阻塞主线程
                socket = serverSocket.accept();
                System.out.println(stringNowTime() + "Receive ClientSocket:" + socket.hashCode()+ " connected");
                BufferedReader reader = null;
                BufferedWriter writer = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    String inputContent;
                    while ((inputContent = reader.readLine()) != null) {
                        System.out.println("Receive socket:" + inputContent);
                        writer.write("Success\n");
                        writer.flush();
                    }
                    System.out.println("Receive ClientSocket:"+ stringNowTime() + " - End");
                }catch (Exception e){
                    System.out.println("Receive ClientSocket:" + stringNowTime() + " - Disconnect");
                }finally {
                    assert reader != null;
                    reader.close();
                    assert writer != null;
                    writer.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                assert socket != null;
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

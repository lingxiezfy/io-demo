package com.fy.io.demo;

//javac -encoding utf8 BIOClient.java
//java BIOClient
import javax.net.SocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BIOClient {
    public void initBIOClient(String host, int port) {
        BufferedReader input = null;
        String inputContent;
        try {
            input = new BufferedReader(new InputStreamReader(System.in));
            InetSocketAddress server = new InetSocketAddress(host, port);
            System.out.println("clientSocket started: " + stringNowTime());
            while (((inputContent = input.readLine()) != null)) {
                BufferedReader reader = null;
                BufferedWriter writer = null;
                Socket socket = null;
                try {
                    socket = SocketFactory.getDefault().createSocket();
                    socket.connect(server);
                    System.out.println("New Connection for doing something");
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //将消息发送给服务端
                    writer.write(inputContent + "\n");
                    writer.flush();
                    socket.shutdownOutput();
                    String receiveContent;
                    while ((receiveContent = reader.readLine()) != null) {
                        System.out.println("Return From Server:"+receiveContent);
                    }
                }catch (Exception e){
                    System.out.println("DisConnect from Server");
                    e.printStackTrace();
                }finally {
                    try {
                        assert socket != null;
                        socket.close();
                        assert reader != null;
                        reader.close();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String stringNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static void main(String[] args) {
        BIOClient client = new BIOClient();
        client.initBIOClient("127.0.0.1", 8888);
    }
}

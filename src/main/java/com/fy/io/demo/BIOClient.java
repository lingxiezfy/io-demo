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
        BufferedReader reader = null;
        BufferedWriter writer = null;
        BufferedReader input = null;
        Socket socket = null;
        String inputContent;
        int count = 0;
        try {
            input = new BufferedReader(new InputStreamReader(System.in));
            socket = SocketFactory.getDefault().createSocket();
            socket.setTcpNoDelay(true);
            InetSocketAddress server = new InetSocketAddress(host, port);
            socket.connect(server);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("clientSocket started: " + stringNowTime());
            while (((inputContent = input.readLine()) != null)) {
                inputContent = stringNowTime()+" - MessageID:" + count + ",Content: " + inputContent + "\n";
                //将消息发送给服务端
                writer.write(inputContent);
                writer.flush();
//                String content = null;
//                while ((content = reader.readLine()) != null){
//
//                }
                System.out.println(reader.readLine());
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert socket != null;
                assert reader != null;
                socket.close();
                reader.close();
                writer.close();
                input.close();
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
        BIOClient client = new BIOClient();
        client.initBIOClient("127.0.0.1", 8888);
    }
}

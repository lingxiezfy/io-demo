package com.fy.io.demo;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fengyu.zhang
 */
public class ServerHandler extends Thread {
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;
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
        } catch (Exception e) {
            System.out.println("Receive ClientSocket:" + socket.hashCode()+ " - "+stringNowTime()+" - Disconnect");
        }finally {
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
}

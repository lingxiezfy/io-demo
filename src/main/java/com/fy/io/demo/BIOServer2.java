package com.fy.io.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BIOServer2 {
    public void initBIOServer(int port) {
        //服务端Socket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);

            System.out.println(stringNowTime() + ": serverSocket started");
            while(true) {
                final Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InputStream in = null;
                        OutputStream out = null;
                        try {
                            in = socket.getInputStream();
                            out = socket.getOutputStream();
                            byte[] content = new byte[8];
                            int len;
                            StringBuilder sb = new StringBuilder();
                            while ((len = in.read(content)) != -1) {
                                sb.append(new String(content,0,len));
                                System.out.println("Receive :" + new String(content,0,len));
                            }
                            out.write("Receive Success!\n".getBytes());
                            out.flush();
                            socket.shutdownOutput();
                            System.out.println("Finish once IO");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                assert in != null;
                                in.close();
                                assert out != null;
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
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
        BIOServer2 server = new BIOServer2();
        server.initBIOServer(8888);

    }
}

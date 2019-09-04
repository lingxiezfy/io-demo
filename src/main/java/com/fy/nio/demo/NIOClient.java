package com.fy.nio.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Description:
 *
 * @author fengyu.zhang
 * Create Date Time: 2019/8/5 13:08
 */
public class NIOClient {
    private static final Log LOG = LogFactory.getLog(NIOClient.class);
    private Socket socket;
    private OutputStream out;
    private InputStream in;

    private static final int PERSON_SIZE = 100;
    private static final int PRE_TIMES = 100;

    public NIOClient(String ip,int port) throws IOException {
        socket = SocketFactory.getDefault().createSocket();
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        InetSocketAddress server = new InetSocketAddress(ip, port);
        LOG.info("connect to server-" + ip+":"+port);
        socket.connect(server, 10000);
        out = socket.getOutputStream();
        in = socket.getInputStream();
    }

    public Socket getSocket() {
        return socket;
    }

    public OutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }

    public void send(String message) throws IOException {
        out.write(message.getBytes());
        out.flush();
    }


    public static void main(String[] args) {
        int j = 0;
        while (j< PERSON_SIZE) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NIOClient client = new NIOClient("localhost", 8888);
                                int i = 0;
                                while (i < PRE_TIMES) {
                                    LOG.info("send something to server");
                                    client.send("Hello Server!"+Thread.currentThread().getName()+":"+i);
                                    i++;
                                }
                                client.socket.shutdownOutput();
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = client.getIn().read(buffer)) != -1){
                                    String data = new String(buffer, 0, len);
                                    LOG.info("receive from server: " + data);
                                }
                            } catch (Exception e) {
                                LOG.error("", e);
                            }
                        }
                    },j+""
            ).start();
            j++;
        }

        while (true){}
    }
}

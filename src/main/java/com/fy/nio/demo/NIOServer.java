package com.fy.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    private int port = 8888;
    private Selector selector;
    private InetSocketAddress address = null;

    public NIOServer(int port){
        try{
            if(port > 0){
                this.port = port;
            }
            this.address = new InetSocketAddress(this.port);
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(this.address);
            // 设置成非阻塞模式
            server.configureBlocking(false);
            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Selector服务器启动，端口："+this.port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Selector轮询
    public void listen(){
        try {
            while (true){
                int wait = selector.select();// select 方法是阻塞的
                if(wait == 0){continue;}
                Set<SelectionKey> keys = this.selector.selectedKeys();
                Iterator<SelectionKey> i = keys.iterator();
                while (i.hasNext()){
                    // 获取客户端的号码牌
                    SelectionKey key = i.next();
                    i.remove();
                    // 针对每一个正常连接的客户端处理业务操作
                    if(key.isValid()) {
                        process(key);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void process(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        if(key.isAcceptable()){ // OP_ACCEPT
            // 该key标识 - 可接受一个新的连接
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept(); // todo
            // 客户端连接设置成非阻塞
            client.configureBlocking(false);
            // 客户端连接上来，不直接进行IO操作，而是往Selector上注册号码牌,这里注册的是 OP_READ -- 标识，可以读了
            client.register(selector,SelectionKey.OP_READ);
            System.out.println(stringNowTime() + ": id为" + client.hashCode()+ "的客户端 connected");
        }else if(key.isReadable()){
            SocketChannel client = (SocketChannel)key.channel();
            int len = client.read(buffer);
            if(len > 0){
                buffer.flip(); // todo
                String content = new String(buffer.array(),0,len);
                buffer.clear();
                System.out.println(content);
                client.register(selector,SelectionKey.OP_WRITE,content);
            }
        }else if(key.isWritable()){
            SocketChannel client = (SocketChannel) key.channel();
            buffer.put(("Finish once IO!"+key.attachment()).getBytes());
            buffer.flip();
            client.write(buffer);
            buffer.clear();
            client.register(selector,SelectionKey.OP_READ);
        }
    }
    public String stringNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }


    public static void main(String[] args){
        new NIOServer(8888).listen();
    }

}

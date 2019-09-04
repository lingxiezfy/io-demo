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
    private static int port = 8888;
    private static final int BUFFER_MAX_SIZE = 1024;
    private Selector selector;
    private InetSocketAddress address = null;

    public NIOServer(int port){
        try{
            if(port > 0){
                NIOServer.port = port;
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

    /**
     * Selector轮询
     */
    public void listen(){
        try {
            while (true){
                // select 方法是阻塞的
                int wait = selector.select();
                System.out.print("Listen to process:"+wait+" - ");
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

    class Connection{
        private SocketChannel channel;
        private ByteBuffer buffer;

        private boolean closeInput;
        private boolean closeOutput;
        public Connection(SocketChannel channel) {
            this.channel = channel;
            this.buffer = ByteBuffer.allocate(BUFFER_MAX_SIZE);
            closeInput = false;
            closeOutput = false;
        }

        public ByteBuffer getDataBuffer() {
            return buffer;
        }

        public SocketChannel getChannel() {
            return channel;
        }

        public void read() throws IOException {
            int initPosition = buffer.position();
            if(buffer.hasRemaining()){
                int len = channel.read(buffer);
                if(len == -1){
                    System.out.println("Finish read!");
                    closeInput = true;
                    channel.register(selector,SelectionKey.OP_WRITE,this);
                }else {
                    String content = new String(buffer.array(),initPosition,len);
                    if(!content.endsWith("\n")){
                        content += "\n";
                    }
                    System.out.print("Read from client："+content);
                    channel.register(selector,SelectionKey.OP_READ,this);
                }
            }else {
                channel.register(selector,SelectionKey.OP_WRITE,this);
            }
        }

        public void write() throws IOException {
            System.out.println("Echo to client："+new String(buffer.array(),0,buffer.position()));
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
            if(isCloseInput()){
                closeOutput = true;
            }else {
                channel.register(selector,SelectionKey.OP_READ,this);
            }
        }

        public void close() throws IOException {
            System.out.println("Close connect!");
            channel.close();
        }

        public boolean isCloseInput() {
            return closeInput;
        }

        public boolean isCloseOutput() {
            return closeOutput;
        }
    }

    private void process(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            // OP_ACCEPT
            // 该key标识 - 可接受一个新的连接
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            // todo
            SocketChannel client = server.accept();
            // 客户端连接设置成非阻塞
            client.configureBlocking(false);
            // 客户端连接上来，不直接进行IO操作，而是往Selector上注册号码牌,这里注册的是 OP_READ -- 标识，可以读了
            client.register(selector,SelectionKey.OP_READ);
            System.out.println("ClientSocket:" + client.hashCode()+ " connected");
        }else if(key.isReadable()){
            SocketChannel client = (SocketChannel)key.channel();
            Connection conn = key.attachment() == null?new Connection(client):(Connection) key.attachment();
            conn.read();
        }else if(key.isWritable()){
            Connection conn = (Connection) key.attachment();
            if(conn.isCloseInput() && conn.isCloseOutput()){
                conn.close();
                return;
            }
            conn.write();

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

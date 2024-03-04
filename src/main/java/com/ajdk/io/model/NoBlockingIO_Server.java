package com.ajdk.io.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * NoBlockingIO是同步非阻塞IO，相对比阻塞IO，他在接收数据的时候是非阻塞的，会一直轮询去问内核是否准备好数据，直到有数据返回
 * ps: NoBlockingIO并不是真正意义上的NIO
 * 2.3 优点和缺点
 * 优点：
 * 非阻塞I/O可以同时处理多个客户端连接，提高服务器的并发处理能力。
 * 由于非阻塞I/O的模式下，一个线程可以处理多个I/O操作，因此可以减少线程切换次数，提高系统性能
 *
 * 缺点：
 * 有很多无效访问，因为没有连接的时候accept也不会阻塞，很多为空的accpet
 * 如果客户端没有写数据，会一直向内核访问，每次都是一个系统调用，非常浪费系统资源
 * 2.4 思考
 * 问 ：既然一直轮询会产生很多的无效轮询，并浪费系统资源，那么有没有更好的办法呢
 * 答： 通过事件注册的方式(多路复用器)
 */
public class NoBlockingIO_Server {
    public static List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // 相当于serverSocket
            // 1.支持非阻塞  2.数据总是写入buffer,读取也是从buffer中去读  3.可以同时读写
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 设置非阻塞
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(8080));
            while (true) {
                // 这里将不再阻塞
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    socketChannel.configureBlocking(false);
                    channelList.add(socketChannel);
                } else {
                    System.out.println("没有请求过来！！！");
                }
                for (SocketChannel client : channelList) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    // 也不阻塞
                    int num = client.read(byteBuffer);
                    if (num > 0) {
                        System.out.println("客户端端口：" + client.socket().getPort() + ",客户端收据：" + new String(byteBuffer.array()));
                    } else {
                        System.out.println("等待客户端写数据");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
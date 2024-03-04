package com.ajdk.io.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO是同步阻塞IO，所有连接都是同步执行的，在上一个连接未处理完的时候是无法接收下一个连接
 * 优点和缺点
 * <p>
 * 优点：
 * 简单易用，代码实现比较简单。
 * 对于低并发量的场景，因为每个连接都有独占的线程处理IO操作，因此可以保证每个连接的IO操作都能够及时得到处理。
 * 对于数据量较小的IO操作，同步阻塞IO模型的性能表现较好。
 * <p>
 * 缺点：
 * 由于每一个客户端连接都需要开启一个线程，因此无法承载高并发的场景。
 * 线程切换的开销比较大，会导致系统性能下降。
 * 对于IO操作较慢的情况下，会占用大量的线程资源，导致系统负载过高。
 * 对于处理大量连接的服务器，BIO模型的性能较低，无法满足需求。
 * <p>
 * 思考
 * 问：既然每个连接进来都会阻塞，那么是否可以使用多线程的方式接收处理？
 * 答：当然可以，但是这样如果有1w个连接那么就要启动1w个线程去处理吗，线程是非常宝贵的资源，频繁使用线程对系统的开销是非常大的
 *
 * 测试: telnet 127.0.0.1 8080
 */
public class BIO_Server {
    public static void main(String[] args) {
        try {
            // 监听端口
            ServerSocket serverSocket = new ServerSocket(8080);
            // 等待客户端的连接过来,如果没有连接过来，就会阻塞
            while (true) {
                // 阻塞IO中一个线程只能处理一个连接
                Socket socket = serverSocket.accept();
                System.out.println("客户端建立连接:" + socket.getPort());
                String line = null;
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    line = bufferedReader.readLine();
                    System.out.println("客户端的数据：" + line);

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bufferedWriter.write("ok\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
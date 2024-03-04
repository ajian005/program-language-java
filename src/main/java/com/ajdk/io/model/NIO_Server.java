package com.ajdk.io.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NewIO才是真正意义上的NIO，NoBlockingIO只能算是NIO的前身，因为NewIO在NoBlockingIO上加上了多路复用器，使得NIO更加完美
 * channel不再是直接循环调用内核，而是将连接，接收，读取，写入等事件注册到多路复用器中，如果没有事件到来将会阻塞等待
 * <p>
 * NIO三件套（记）：
 * channel: 介于字节缓冲区(buffer)和套接字(socket)之间，可以同时读写，支持异步IO
 * buffer: 字节缓冲区，是应用程序和通道之间进行IO数据传输的中转
 * selector:多路复用器，监听服务端和客户端的管道上注册的事件
 * <p>
 * 具体流程：
 * 1 服务端创建Selector，并注册OP_ACCEPT接受连接事件，然后调用select阻塞等待连接进来
 * 2 客户端注册OP_CONNECT事件，表示连接客户端，连接成功后会调用handlerConnect方法
 * 2.1 handlerConnect方法会注册OP_READ事件并向服务端写数据
 * 3 这时候服务端会收到OP_ACCEPT后就会走到handlerAccept方法，表示接受连接
 * 3.1 handlerAccept方法也会注册一个OP_READ事件并向客户端写数据
 * 4 客户端接收到服务端的数据后会再次唤醒select方法，然后判断为isReadable(读事件，服务端写入给客户端，那么客户端就是读)，handlerRead方法将会把服务端写入的数据读取
 * 5 反之亦然，服务端也会收到客户端写入的数据，然后通过读事件将数据读取
 * NewIOServer
 *
 * 优点和缺点
 * 优点：
 * NIO使用了非阻塞IO，可以大大提高系统的吞吐量和并发性能。
 * NIO提供了可扩展的选择器，可以监控多个通道的状态，从而实现高效的事件驱动模型。
 * NIO采用直接内存缓冲区，可以避免Java堆内存的GC问题，提高内存管理的效率。
 *
 * 缺点：
 * NIO的编程模型相比传统的IO模型更加复杂，需要掌握较多的API和概念。
 * NIO的实现难度较高，需要处理很多细节问题，如缓冲区的管理、选择器的使用等。
 * NIO的可靠性不如传统的IO模型，容易出现空轮询、系统负载过高等问题。
 *
 * 3.3 思考
 * 问：select方法不是也阻塞吗，那跟BIO有什么区别？
 * 答：虽然他是在select阻塞，但是他通过事件注册的方式，可以将多个selectKey同时加载到selectionKeys集合中，通过for循环处理不同的事件，而BIO只能由一个连接处理完才能处理下一个连接
 *
 * 问：什么是多路复用?
 * 答：
 * 多路：是指多个连接的管道，通道
 * 复用：复用一个系统调用，原本多次系统调用变成一次
 */
public class NIO_Server {
    static Selector selector;
    public static void main(String[] args) {
        try {
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(8080));

            // 需要把serverSocketChannel注册到多路复用器上
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 阻塞
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        handlerAccept(key);
                    } else if (key.isReadable()) {
                        handlerRead(key);
                    } else if (key.isWritable()) {

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlerRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        try {
            socketChannel.read(allocate);
            System.out.println("server msg:" + new String(allocate.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlerAccept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        // 不阻塞
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.write(ByteBuffer.wrap("It‘s server msg".getBytes()));
            // 读取客户端的数据
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
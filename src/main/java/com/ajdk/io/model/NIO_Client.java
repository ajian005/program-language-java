package com.ajdk.io.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIO_Client {
    static Selector selector;

    public static void main(String[] args) {
        try {
            selector = Selector.open();
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost", 8080));

            // 需要把socketChannel注册到多路复用器上
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            while (true) {
                // 阻塞
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isConnectable()) {
                        handlerConnect(key);
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
            System.out.println("client msg:" + new String(allocate.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlerConnect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
        }
        socketChannel.configureBlocking(false);socketChannel.write(ByteBuffer.wrap("it‘s client msg".getBytes()));
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}


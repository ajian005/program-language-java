package com.ajdk.io.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 在上面将的BIO,NIO中都是同步IO，BIO叫做同步阻塞，NIO叫做同步非阻塞，那么AIO则是异步IO，全名(Asynchronous I/O)
 * 优点和缺点
 * 优势：
 * 1 更加高效：AIO采用回调方式，可以避免轮询等操作对CPU的占用，减少CPU的负担，从而提高了系统的性能。
 * 2 可以更好地利用系统资源：AIO能够在I/O操作完成之前把线程释放出来，可以更好地利用系统资源，提高系统的并发处理能力。
 * 3 适用于高并发场景：AIO适用于高并发场景，能够支持大量的并发连接，提高系统的处理能力。
 *
 * 缺点：
 * 1 学习成本高：相比于NIO，AIO的编程模型更加复杂，需要学习更多的知识，学习成本更高。
 * 2 实现难度大：AIO的实现难度比较大，需要对操作系统的底层机制有深入的了解，因此开发成本较高。
 * 3 并非所有操作系统都支持：AIO并非所有操作系统都支持，只有Linux 2.6以上的内核才支持AIO，因此跨平台的支持较差。
 */
public class AIO_Server {

    public static void main(String[] args) throws Exception {
        // 创建一个SocketChannel并绑定了8080端口
        final AsynchronousServerSocketChannel serverChannel =
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8080));

        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                try {
                    // 打印线程的名字
                    System.out.println("2--" + Thread.currentThread().getName());
                    System.out.println(socketChannel.getRemoteAddress());
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // socketChannel异步的读取数据到buffer中
                    socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer buffer) {
                            // 打印线程的名字
                            System.out.println("3--" + Thread.currentThread().getName());
                            buffer.flip();
                            System.out.println(new String(buffer.array(), 0, result));
                            socketChannel.write(ByteBuffer.wrap("HelloClient".getBytes()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer buffer) {
                            exc.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        System.out.println("1--" + Thread.currentThread().getName());
        Thread.sleep(Integer.MAX_VALUE);
    }
}
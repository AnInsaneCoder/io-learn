package top.insanecoder.server;

import top.insanecoder.utils.AIOSendRunnable;
import top.insanecoder.utils.ReadCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by shaohang.zsh on 2016/7/14.
 */
public class AIOTcpServer {

    private static final int PORT = 5555;
    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("main thread id: " + Thread.currentThread().getId());
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));

        serverSocketChannel.accept(serverSocketChannel, new AcceptCompletionHandler());

        // keep the main thread out of exiting
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Listening on port: " + PORT);
                while(true);
            }
        });

        t.start();
        t.join();
    }

    private static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

        @Override
        public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {

            System.out.println("Get a new client!!!");
            System.out.println("accept thread id: " + Thread.currentThread().getId());
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            result.read(byteBuffer, result, new ReadCompletionHandler(byteBuffer, "client"));
            // start the send thread
            new Thread(new AIOSendRunnable(result)).start();
            // continue listening
            attachment.accept(attachment, this);
        }

        @Override
        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {

            System.out.println("Get a new client failed!!!");
            attachment.accept(attachment, this);
        }
    }

}

package top.insanecoder.client;

import top.insanecoder.utils.AIOSendRunnable;
import top.insanecoder.utils.ReadCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by shaohang.zsh on 2016/7/14.
 */
public class AIOTcpClient {

    private static final String ADDRESS = "localhost";
    private static final int PORT = 5555;
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        Future<Void> future = socketChannel.connect(new InetSocketAddress(ADDRESS, PORT));
        future.get();

        System.out.println("Connecting to " + ADDRESS + " on " + PORT);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer, socketChannel, new ReadCompletionHandler(byteBuffer, "server"));

        Thread t = new Thread(new AIOSendRunnable(socketChannel));
        t.start();
        t.join();
    }
}

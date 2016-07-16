package top.insanecoder.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by shaohang.zsh on 2016/7/14.
 */
public class AIOSendRunnable implements Runnable {

    private AsynchronousSocketChannel socketChannel;
    public AIOSendRunnable(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        System.out.println("Type to send message:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                String msg = reader.readLine();
                this.socketChannel.write(ByteBuffer.wrap(msg.getBytes()), null, new WriteComPletionHandler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

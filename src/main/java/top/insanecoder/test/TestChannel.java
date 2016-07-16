package top.insanecoder.test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by shaohang.zsh on 2016/7/13.
 */
public class TestChannel {

    public static void main(String[] args) throws IOException {

        RandomAccessFile randomAccessFile = new RandomAccessFile("src\\main\\resources\\test", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        StringBuilder sb = new StringBuilder();
        int length;

        // read to buffer from channel
        while ((length = fileChannel.read(byteBuffer)) > 0) {
            byteBuffer.flip();
            byte[] data = byteBuffer.array();
            sb.append(new String(data, 0, length));
            byteBuffer.clear();
        }
        System.out.println(sb.toString());

        // write to channel from buffer
        fileChannel.write(ByteBuffer.wrap("another hello world !!!!".getBytes()));
    }
}

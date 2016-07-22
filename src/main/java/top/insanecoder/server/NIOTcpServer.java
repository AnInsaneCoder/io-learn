package top.insanecoder.server;

import top.insanecoder.utils.FixLengthWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by shaohang.zsh on 2016/7/14.
 */
public class NIOTcpServer {

    private static final int PORT = 5555;
    private static ByteBuffer byteBuffer = ByteBuffer.allocate(10240);
    private static int number = 0;

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("server start on port " + PORT + " ...");

        while (true) {

            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                if (!selectionKey.isValid())
                    continue;

                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel serverChannel = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel socketChannel = serverChannel.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
                    // start the thread of writing
                    new Thread(new SendRunnable(socketChannel)).start();
                    Socket socket = socketChannel.socket();
                    System.out.println("Get a client, the remote client address: " + socket.getRemoteSocketAddress());
                } else if (selectionKey.isReadable()) {
                    // Read data from channel to buffer
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    socketChannel.socket().setReceiveBufferSize(102400);
                    String remoteAddress = socketChannel.socket().getRemoteSocketAddress().toString();

                    // do nothing
                    processNormally(socketChannel);

                    // variable length
//                    processByHead(socketChannel);

                    // fix length
//                    processByFixLength(socketChannel);
                }

                iterator.remove();
            }
        }
    }

    private static class SendRunnable implements Runnable {

        private SocketChannel socketChannel;

        public SendRunnable(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }
        public void run() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String msg = bufferedReader.readLine();
                    this.socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void processNormally(SocketChannel socketChannel) throws IOException {

        StringBuilder sb = new StringBuilder();
        ByteBuffer tmpByteBuffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(tmpByteBuffer) > 0) {
            tmpByteBuffer.flip();
            sb.append(new String(tmpByteBuffer.array()));
            tmpByteBuffer.clear();
        }
        System.out.println(sb.toString() + " <---> " + number++);
    }

    private static void processByHead(SocketChannel socketChannel) throws IOException {

        while (socketChannel.read(byteBuffer) > 0) {
            int position = byteBuffer.position();
            int limit = byteBuffer.limit();
            byteBuffer.flip();
            if (byteBuffer.remaining() < 4) {
                byteBuffer.position(position);
                byteBuffer.limit(limit);
                continue;
            }

            int length = byteBuffer.getInt();
            if (byteBuffer.remaining() < length) {
                byteBuffer.position(position);
                byteBuffer.limit(limit);
                continue;
            }

            byte[] data = new byte[length];

            byteBuffer.get(data, 0, length);
            System.out.println(new String(data) + " <---> " + number++);
            byteBuffer.compact();
        }
    }

    private static void processByFixLength(SocketChannel socketChannel) throws IOException {
        while (socketChannel.read(byteBuffer) > 0) {

            byteBuffer.flip();
            while (byteBuffer.remaining() >= FixLengthWrapper.MAX_LENGTH) {
                byte[] data = new byte[FixLengthWrapper.MAX_LENGTH];
                byteBuffer.get(data, 0, FixLengthWrapper.MAX_LENGTH);
                System.out.println(new String(data) + " <---> " + number++);
            }
            byteBuffer.compact();
        }
    }
}

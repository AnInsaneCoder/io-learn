package top.insanecoder.server;

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
                    String remoteAddress = socketChannel.socket().getRemoteSocketAddress().toString();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    StringBuilder sb = new StringBuilder();
                    while (socketChannel.read(byteBuffer) > 0) {
                        byteBuffer.flip();
                        sb.append(new String(byteBuffer.array()));
                        byteBuffer.clear();
                    }
                    System.out.println("[" + remoteAddress + "] " + sb.toString());
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
}

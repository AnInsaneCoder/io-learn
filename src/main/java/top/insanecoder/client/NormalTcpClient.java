package top.insanecoder.client;

import top.insanecoder.utils.PrintRunnable;
import top.insanecoder.utils.SendRunnable;

import java.io.*;
import java.net.Socket;

/**
 * Created by shaohang.zsh on 2016/7/13.
 */
public class NormalTcpClient {

    private static final int PORT = 5555;
    private static final String IP_ADDRESS = "localhost";

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = new Socket(IP_ADDRESS, PORT);
        System.out.println("Connecting to server ip: " + IP_ADDRESS + ", port: " + PORT);
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        Thread sendThread = new Thread(new SendRunnable(dout));
        Thread printThread = new Thread(new PrintRunnable(dis, "server"));

        sendThread.start();
        printThread.start();

        sendThread.join();
        printThread.join();

        socket.close();
    }
}

package top.insanecoder.utils;

import java.nio.ByteBuffer;

/**
 * Author: shaohang.zsh
 * Date: 2016/7/20
 */
public class FixLengthWrapper {

    public static final int MAX_LENGTH = 32;
    private byte[] data;

    public FixLengthWrapper(String msg) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_LENGTH);
        byteBuffer.put(msg.getBytes());
        byte[] fillData = new byte[MAX_LENGTH - msg.length()];
        byteBuffer.put(fillData);
        data = byteBuffer.array();
    }

    public FixLengthWrapper(byte[] msg) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_LENGTH);
        byteBuffer.put(msg);
        byte[] fillData = new byte[MAX_LENGTH - msg.length];
        byteBuffer.put(fillData);
        data = byteBuffer.array();
    }

    public byte[] getBytes() {
        return data;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : getBytes()) {
            sb.append(String.format("0x%02X ", b));
        }
        return sb.toString();
    }
}

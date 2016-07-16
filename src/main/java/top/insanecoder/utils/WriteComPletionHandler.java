package top.insanecoder.utils;

import java.nio.channels.CompletionHandler;

/**
 * Created by shaohang.zsh on 2016/7/14.
 */
public class WriteComPletionHandler implements CompletionHandler<Integer, Void> {

    @Override
    public void completed(Integer result, Void attachment) {
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        System.out.println("Write failed!!!");
    }
}

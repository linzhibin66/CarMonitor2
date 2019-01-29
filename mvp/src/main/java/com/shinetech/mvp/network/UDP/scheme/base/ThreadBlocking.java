package com.shinetech.mvp.network.UDP.scheme.base;

import com.shinetech.mvp.network.UDP.listener.ThreadBlockListener;

/**
 * Created by Lenovo on 2017-09-14.
 */

public class ThreadBlocking implements Runnable {

    private long sendTime;

    private Thread blockThread;

    private ThreadBlockListener mListener;

    private boolean isdebug = false;

    public ThreadBlocking(long sendTime, Thread blockThread ,ThreadBlockListener listener) {
        this.sendTime = sendTime;
        this.blockThread = blockThread;
        this.mListener = listener;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
//            e.printStackTrace();
            if(isdebug)System.out.println("- - interrupt  ThreadBlocking - -" );

        }
        if(mListener != null){
            if(mListener.checkBlock(sendTime)) {
                if(isdebug)System.out.println("- -  ThreadBlocking  Blocking - -" );
                blockThread.interrupt();
                mListener.isBlock();
            }
        }

    }
}

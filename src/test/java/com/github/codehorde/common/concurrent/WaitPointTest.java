package com.github.codehorde.common.concurrent;

/**
 * Created by baomingfeng at 2017-06-15 15:40:20
 */
public class WaitPointTest {


    public static void main(String[] args) throws InterruptedException {
        final WaitPoint waitPoint = new WaitPoint(2);

        for (int i = 0; i < 7; i++) {
            new Thread("WaitPoint" + i) {
                @Override
                public void run() {
                    try {
                        System.out.println(getName() + " --> waitPoint.await()");
                        waitPoint.await();
                        System.out.println(getName() + " --> Run ");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        System.out.println(Thread.currentThread().getName() + " --> Thread.sleep ");
        Thread.sleep(1000L * 7);
        System.out.println(Thread.currentThread().getName() + " --> waitPoint.release()");
        waitPoint.release();
        System.out.println(Thread.currentThread().getName() + " --> waitPoint.release() ");
        waitPoint.release();

        System.out.println("- Main End -");
    }
}
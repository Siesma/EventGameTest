package game;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

public class Timer implements Runnable {
    private final long delta_t; // 16 -> 60FPS
    private final Thread thread;
    private boolean running;

    private long prevTime;
    private long curTime;

    private Callable<Void> handler;

    public Timer(long delta_t, Callable<Void> handler) {
        prevTime = System.currentTimeMillis();
        this.handler = handler;
        this.delta_t = delta_t;
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
        curTime = System.currentTimeMillis();
    }
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(delta_t);
                curTime = System.currentTimeMillis();
                try {
                    handler.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                prevTime = curTime;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public Object get (String name) {
        Object o;
        try {
            Field f  = getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(this);
        } catch (Exception e) {
            System.out.printf("Field \"%s\" does not exist.\n", name);
            return null;
        }
    }

    public void set (String name, Object o) {

        this.prevTime = (long) o;
    }


    public void stop() {
        this.running = false;
        this.thread.interrupt();
    }
}

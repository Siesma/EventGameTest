package game;

import event.EventBus;
import event.events.TimerEvent;

public class Timer implements Runnable {
    private final long delta_t; // 16 -> 60FPS
    private final Thread thread;
    private boolean running;

    public Timer(long delta_t) {
        this.delta_t = delta_t;
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
    }
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(delta_t);
                EventBus.raise(new TimerEvent());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        this.running = false;
        this.thread.interrupt();
    }
}

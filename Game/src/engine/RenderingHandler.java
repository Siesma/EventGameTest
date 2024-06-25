package engine;

import rendering.IsoWindow;

public class RenderingHandler implements Runnable {
    private final Thread thread;

    private IsoWindow window;

    protected boolean running;

    public RenderingHandler () {
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
        window = new IsoWindow();
    }

    @Override
    public void run() {
        window.run();
        this.running = false;
    }

    public void stop () {
        this.running = false;
        window.stop();
        this.thread.interrupt();
    }
}

package engine;

import event.EventBus;
import event.EventSubscriber;
import event.events.TimerEvent;
import game.Timer;
import other.Pair;
import rendering.FrameTime;
import rendering.IsoWindow;

import java.util.concurrent.Callable;

public class Engine {

    private Timer timer;

    private RenderingHandler handler;


    private int cycles = 0;

    public Engine (long updateInterval) {
        EventBus.register(this);
        this.timer = new Timer(updateInterval, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                long now = System.currentTimeMillis();
                long prevTime = (long) timer.get("prevTime");
                long deltaTime = now - prevTime;

                EventBus.raise(
                        new TimerEvent(
                                new Pair<>("CurTime", now),
                                new Pair<String, Long>("PrevTime", prevTime),
                                new Pair<String, Long>("DT", deltaTime),
                                new Pair<String, Boolean>("Running", (boolean) timer.get("running"))
                        )
                );
                timer.set("prevTime", now);
                return null;
            }
        });

        this.handler = new RenderingHandler();
        handler.run();
    }

    @EventSubscriber
    public void onTimerEvent (TimerEvent event) {
        if(!handler.running) {
            stop();
        }
    }

    private void stop () {
        timer.stop();
        handler.stop();
    }

    public static void main(String[] args) {
        Engine engine = new Engine(16);
    }

}

package core.tasks;

import core.events.SpeedLimiter;

public class SpeedFinder extends Thread {
    private final Object lock = new Object();

    @Override
    public void run() {
        synchronized (lock) {
            try {
                while (true) {
                    SpeedLimiter.speedCheck();
                    lock.wait(500L);
                }

            } catch (InterruptedException ignore) { }
        }
    }
}

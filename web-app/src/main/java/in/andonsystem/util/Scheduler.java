package in.andonsystem.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by razamd on 4/4/2017.
 */
public class Scheduler {
    private static Scheduler INSTANCE;
    private final int POOL_SIZE = 10;
    private ScheduledExecutorService scheduler;

    private Scheduler(){
        scheduler = Executors.newScheduledThreadPool(POOL_SIZE);
    }

    public static Scheduler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Scheduler();
        }
        return INSTANCE;
    }

    public ScheduledExecutorService getScheduler(){
        return scheduler;
    }

    public void submit(Thread task, Long delay){
        scheduler.schedule(task, delay, TimeUnit.MINUTES);
    }
}

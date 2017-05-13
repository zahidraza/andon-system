/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.scheduler;

import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author Administrator
 */
public class FutureTask {
    private MyTask myTask;
    private ScheduledFuture<?> future;

    public FutureTask(MyTask myTask, ScheduledFuture<?> future) {
        this.myTask = myTask;
        this.future = future;
    }

    public MyTask getMyTask() {
        return myTask;
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }
    
    
}

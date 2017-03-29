/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.scheduler;

/**
 *
 * @author Administrator
 */
public class MyTask {
    public static final int QUEUED = 0;
    public static final int SUBMITTED = 1;
    public static final int PROCESSING = 2;
    public static final int DONE = 3;
    
    public  final long maxProcessingTime = 2*1000;
    
    
    private long timeSubmit;
    private long delay;
    private Thread task;
    private int flag;

    public MyTask(long timeSubmit, long delay, Thread task, int flag) {
        this.timeSubmit = timeSubmit;
        this.delay = delay;
        this.task = task;
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getTimeSubmit() {
        return timeSubmit;
    }

    public void setTimeSubmit(long timeSubmit) {
        this.timeSubmit = timeSubmit;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public Thread getTask() {
        return task;
    }

    public void setTask(Thread task) {
        this.task = task;
    }
    
    
    public int checkCompleted(){
        long timeNow = System.currentTimeMillis();
        
        if(timeNow < (timeSubmit + delay)){
            return MyTask.SUBMITTED;
        }else if(timeNow < (timeSubmit + delay + maxProcessingTime) ){
            return MyTask.PROCESSING;
        }else{
            return MyTask.DONE;
        }
    }
}

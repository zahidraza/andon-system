/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.scheduler;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Administrator
 */
public class FutureTaskManager extends Thread {
    private static final int poolSize = 15;
    private static int counter;
    private static Collection<FutureTask> futureTask;
    private static ScheduledExecutorService scheduler; 
    private static Boolean isRunning;
    private final long monitorTime = 5*60*1000; // 5 minute
    
    public FutureTaskManager(){
        futureTask = new HashSet<FutureTask>();
        isRunning = true;
        counter = 0;
        scheduler = Executors.newScheduledThreadPool(poolSize);
        this.setName("HungThreadMonitor");
    }
    
    public static void manage(MyTask myTask){
        
        myTask.setFlag(MyTask.SUBMITTED);
        
        futureTask.add(new  FutureTask(
                                myTask,
                                scheduler.schedule(myTask.getTask(), myTask.getDelay(), TimeUnit.MILLISECONDS)
                            )
        );
    }

    @Override
    public void run() {
        while(isRunning){
            System.out.println("\n"+new Date() + " :Monitoring Hung Thread");

            FutureTask data;
            for(Iterator<FutureTask> itr = futureTask.iterator(); itr.hasNext(); ){
                data = itr.next();
                
                if(data.getMyTask().checkCompleted() == MyTask.DONE){
                    //if future is not finished (i.e Hung)
                    if(!data.getFuture().isDone()){
                        counter++;
                        data.getFuture().cancel(true);
                        
                        System.out.println("Task hanged. Re-Submitting the Task");
                        scheduler.schedule(data.getMyTask().getTask(), 0, TimeUnit.MILLISECONDS);
                    }else{
                        //data.getFuture().cancel(true);
                        itr.remove();
                    }
                }
                
            }
            System.out.println("Pending Tasks: " + futureTask.size());
            System.out.println("No. of Threads Hung : " + counter);
            try {
                Thread.sleep(monitorTime);
            } catch (InterruptedException e) {
                    System.out.println("Thread Manager interupted, shutting down");

            }
        }
    }
    
    public void shutdown(){
        this.interrupt();
    }
    
    public static void setIsRunning(Boolean isRunning){
        FutureTaskManager.isRunning = isRunning;
    }
    
    public static ScheduledExecutorService getScheduler(){
        return scheduler;
    }
    
}

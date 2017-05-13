/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 * @author Administrator
 */
public class SchedulerService {
    private static ScheduledExecutorService scheduler;// = Executors.newScheduledThreadPool(1);
    public SchedulerService(){
        scheduler = Executors.newScheduledThreadPool(20);
    }
    
    public static ScheduledExecutorService getScheduler(){
        return scheduler;
    }
}

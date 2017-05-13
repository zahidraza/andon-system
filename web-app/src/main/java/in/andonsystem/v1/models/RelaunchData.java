/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

/**
 *
 * @author Md Zahid Raza
 */
public class RelaunchData {
    
    private String relaunched;
    private String updateApp;
    private long launch_time;
    private long current_time;

    public RelaunchData(){}

    public RelaunchData(String code,long launch_time, long current_time) {
        this.relaunched = code;
        this.launch_time = launch_time;
        this.current_time = current_time;
    }

    public RelaunchData(String relaunched, String updateApp, long launch_time, long current_time) {
        this.relaunched = relaunched;
        this.updateApp = updateApp;
        this.launch_time = launch_time;
        this.current_time = current_time;
    }
    
    public String getUpdateApp() {
        return updateApp;
    }

    public void setUpdateApp(String updateApp) {
        this.updateApp = updateApp;
    }
  
    public String getRelaunched() {
        return relaunched;
    }

    public void setRelaunched(String relaunched) {
        this.relaunched = relaunched;
    }

    public long getLaunch_time() {
        return launch_time;
    }

    public void setLaunch_time(long launch_time) {
        this.launch_time = launch_time;
    }

    public long getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(long current_time) {
        this.current_time = current_time;
    }

    
    
}

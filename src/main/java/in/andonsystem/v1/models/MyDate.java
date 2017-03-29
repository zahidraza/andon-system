/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class MyDate{

    private Date date;
    
    public MyDate(){}
    
    public MyDate(Date date){
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }
    
    
}

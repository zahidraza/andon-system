/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.threads;


import in.andonsystem.v1.services.DesignationService;
import in.andonsystem.v1.services.IssueService;
import in.andonsystem.v1.services.SMSService;
import in.andonsystem.v1.services.UserService;
import in.andonsystem.v1.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 */
public class ScheduleAckThread extends Thread {

    private int issueId;
    private int line;
    private int probId;
    private int level;  //schedule  for level 2 and 3
    private String message;

    public ScheduleAckThread(int issueId, int line, int probId, String message, int level) {
        this.issueId = issueId;
        this.level = level;
        this.line = line;
        this.probId = probId;
        this.message = message;
    }

    @Override
    public void run() {

        System.out.println(
                "\nTime: " + new Date(System.currentTimeMillis()) + ",ScheduleAckThread.run() for issuedId :" +
                issueId + " , and level:" + level);
        Connection conn = null;
        try {
            ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        IssueService iService = new IssueService(conn);
        UserService uService = new UserService(conn);
        DesignationService desgnService = new DesignationService(conn);
        try {
            Boolean result = iService.isAcknowledged(issueId);
            System.out.println("Is issue acknowledged : " + result);
            if (result) {
                System.out.println("Ignoring SMS scheduled run.");
            }
            if (!result) {
                iService.saveIssueStatus(issueId, level);
                System.out.println("Saved Processing at = " + level + "  for Issue id : " + issueId);
                List<Integer> list = null;          //List of desgnId concerned
                if (level == 2) {
                    list = desgnService.getDesgnConcerned(line, probId, 2);
                }
                if (level == 3) {
                    list = desgnService.getDesgnConcerned(line, probId, 3);
                }

                System.out.println("Level " + level + " Designations mapped:\n");
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(desgnService.getDesgnName(list.get(i)));
                }
                String to = "";
                if (list.size() > 0) {
                    //Find all designation Ids
                    String desgnIds = "(" + String.valueOf(list.get(0));
                    for (int i = 1; i < list.size(); i++) {
                        desgnIds += "," + String.valueOf(list.get(i));
                    }
                    desgnIds += ")";
                    //Find mobile number of all user with these desination ids
                    List<String> mobiles = uService.getUserMobiles(desgnIds);
                    if (mobiles.size() > 0) {
                        to += mobiles.get(0);
                        for (int i = 1; i < mobiles.size(); i++) {
                            to += "," + mobiles.get(i);
                        }
                    }
                    System.out.println("Send SMS to mobiles:" + to);
                    SMSService.sendSMS(to, message);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //while(!Thread.currentThread().isInterrupted());

    }
}
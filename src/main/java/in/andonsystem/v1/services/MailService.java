/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;

import in.andonsystem.v1.models.User;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Md Zahid Raza
 */
public class MailService {
    
    public Boolean send(String to,String sub,String msg){
       /*
        String host = "mail.laguna-clothing.com";
        String user = "itsupport@laguna-clothing.com";
        String password = "***S***@522";
        */
        
        String host = "mail.zahidraza.in";
        String user = "noreply@zahidraza.in";
        String password = "Munnu@90067";
        

        Boolean status = false;
        
        Properties prop = new Properties();
        
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.auth",true);
        
        Session session = Session.getInstance(prop,  
                                new javax.mail.Authenticator() {  
                                    protected PasswordAuthentication getPasswordAuthentication() {  
                                        return new PasswordAuthentication(user,password);  
                                    }  
                                }); 
        
        //Compose the message  
        try {  
            MimeMessage message = new MimeMessage(session);  
            message.setFrom(new InternetAddress(user));  
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
            message.setSubject(sub);  
            message.setText(msg);  

            //send the message  
            Transport.send(message);  

            status = true;
        }catch (MessagingException e) {
             e.printStackTrace();
        } 
        
        return status;
    }
    
    public Boolean forgotPasswordMail(String email,String link){
        String subject = "Password Reset";
        String message =    "\nYou accessed the Password Reset Service of Laguna-Clothing pvt ltd TNA Application\n"+
                            "Click on link below to Reset your Password\n\n";
        String url = "http://192.168.1.111:8080/tna/forgot_password?link=" + link;
        //String url = "http://zahidraza.in/tna/forgot_password?link=" + link;
        
        message += url;
        message += "\n\nNote: This is one time use link and valid only within 24 hours.";
        
        Boolean status = send(email,subject,message);
        
        return status;
    }
    /*
    public Boolean userRegistrationMail(User user){
        DesignationService dService = new DesignationService();
        
        String sub = "Registered to ANDON SYSTEM Application";
        String msg = "\nYou are Successfully registred in ANDON SYSTEM Android Application of Laguna-Clothing pvt ltd.\n" +
                        "Your details registered with Application are:\n\n" +
                        "User ID:\t\t  " + user.getUserId()+ "\n" +
                        "Name:\t\t\t " + user.getUsername()+"\n" +
                        "Email:\t\t\t  " + user.getEmail()+ "\n" +
                        "Password:\t\t" +  user.getPassword()+ "\n" +
                        "User Level:\t\t Level " + user.getLevel()+ "\n" +
                        "Designation:\t\t" + dService.getDesgnName(user.getDesgnId()) + "\n" +
                        "Mobile No.:\t\t" + user.getMobile()+ "\n\n" +
                        
                        "Contact administrator if any information does not match with your details\n\n" +
                
                        "You should change your password immediately." ;
        
        return send(user.getEmail(),sub,msg);
        
    }
*/
}

package com.spring6.spring_project.helper;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.spring6.spring_project.dto.MyUser;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class HelperFor_SendingMail {

    @Autowired
    JavaMailSender mailSender;
 
    public void sendEmail(MyUser myUser){

          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
          try{
          helper.setFrom("gokulramchandru@gmail.com", "Student Management System");
          System.out.println(myUser.getEmail());
          helper.setTo(myUser.getEmail());
          helper.setSubject("OTP Verification");
          helper.setText("<h1>Hello, "+myUser.getName()+" Your OTP is : "+myUser.getOpt()+"</h1>",true);
          } catch(MessagingException | UnsupportedEncodingException e){
            e.printStackTrace();
          }

          mailSender.send(mimeMessage);
    }
}

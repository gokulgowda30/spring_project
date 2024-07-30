package com.spring6.spring_project.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


//import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.spring6.spring_project.dto.MyUser;
import com.spring6.spring_project.dto.Student;
import com.spring6.spring_project.helper.AES;
import com.spring6.spring_project.helper.HelperFor_SendingMail;
import com.spring6.spring_project.repository.MyUserRepository;
import com.spring6.spring_project.repository.StudentJpa;

import jakarta.mail.Multipart;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@MultipartConfig
//@RestController
public class MyController {

   @Autowired
   MyUserRepository repository;

   @Autowired
   HelperFor_SendingMail for_SendingMail;

   @Autowired
   StudentJpa studentJpa;

    @GetMapping("/")
    public String loadSignup1(){
        return "home.html";
    }

    @GetMapping("/login")
    public String loadSignup2() {
        return "login.html";
    }

    @GetMapping("/signup")
    public String loadsignup(ModelMap map) {
        map.put("myUser", new MyUser());//At the first time if we click on signup button we cant login because we are not carrying any object ,so we carry an empty object 
        return "signup.html";
    
    }
    

    @PostMapping("/signup")  //Here the refferece variable name should be same as class name whilr we put @valid eg:MyUser myUser 
    //To carry msg to front end we use ModelMap//Here we use @Valid
    public String signup(@Valid MyUser myUser,BindingResult result,ModelMap map) {
       if(repository.existsByEmail(myUser.getEmail()))//to check the mail weather it already exist int database or not If email exist donot show the defaultMessage else continue with the sign up part
       //we use error.email to send the errors to front end
        result.rejectValue("email", "error.email", "Email already exist ,please enter new one ");

        if(result.hasErrors())
           return "signup.html";
        else{
            //Check Email Duplicate
           int otp=new Random().nextInt(100000, 1000000);
           myUser.setOpt(otp);
           //To encrypt the password
           myUser.setPassword(AES.encrypt(myUser.getPassword(), "123"));
           System.out.println(myUser.getOpt());
           //logic for sending otp
         //  for_SendingMail.sendEmail(myUser);
           repository.save(myUser);
           map.put("success", "OTP sent successfully, Check your email");
           map.put("id", myUser.getId());
            return "enter-otp.html";
        }

             
    }

    @PostMapping("/verify-otp")
    public String verify(@RequestParam int id,@RequestParam int opt,ModelMap map){
        MyUser myUser = repository.findById(id).orElseThrow();
        if(myUser.getOpt()==opt){
            myUser.setVerified(true);
            repository.save(myUser);
            map.put("success", "Account Created Successfully");
            return "home.html";
        }else{
            map.put("failure", "Invalid OTP , Try Again");
            map.put("id", myUser.getId());
            return "enter-otp.html";
        }
    }
    
    
    @PostMapping("/login")
    public String login(HttpSession session,@RequestParam String email,@RequestParam String password,ModelMap map){
    //     MyUser myUser = repository.findByEmail(email);
    
    // // Check if the user exists and the password matches
    // if (myUser != null && AES.decrypt(myUser.getPassword(), "123").equals(password)) {
    //     System.out.println("login success");
    //     return "main.html";
    // } else {
    //     // Add an error message to the model
    //     map.put("error", "Invalid email or password");
    //     return "login.html";
    // }

       MyUser myUser = repository.findByEmail(email);
       if(myUser==null){
        map.put("failure", "Email not found");
        return "login.html";
       }else{

        if(password.equals(AES.decrypt(myUser.getPassword(), "123"))){
            if(myUser.isVerified()){
                session.setAttribute("user", myUser);
                map.put("success", "Login Successful");
                return "home.html";
            }else{
                int otp=new Random().nextInt(100000, 1000000);
                myUser.setOpt(otp);
                repository.save(myUser);
                System.out.println(myUser.getOpt());
                //for_SendingMail.sendEmail(myUser);
                map.put("success", "OTP set succesfully");
                map.put("id", myUser.getId());
                return "enter-otp.html";
            }
            }else{
                map.put("failure", "Password is wrong");
                return "login.html";
            }
        }
       }
    

    @GetMapping("/main")
    public String main() {
        return "main.html";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session,ModelMap map){
        session.removeAttribute("user");//here the parameter name should be same as session attribut name in session.setAttribute("user", myUser);
        map.put("success", "Logout Successfully");
        return "home.html";
    }

    @GetMapping("/insert")
    public String insert( HttpSession session,ModelMap map){
        if(session.getAttribute("user")!=null){
            return "insert.html";
          //return student.toString();
        }else{
            map.put("failure", "Invalid Session");
            return "login.html";
        }
    }

@GetMapping("/fetch")
public String fetch(HttpSession session,ModelMap map){
    if(session.getAttribute("user")!=null){
        return "fetch.html";
    }else{
        map.put("failure", "Invalid Session");
        return "login.html";
    }
}

@GetMapping("/update")
public String update(HttpSession session,ModelMap map){
    if(session.getAttribute("user")!=null){
        return "update.html";
    }else{
        map.put("failure", "Invalid Session");
        return "login.html";
    }
}

@GetMapping("/delete")
public String delete(HttpSession session,ModelMap map){
    if(session.getAttribute("user")!=null){
        return "delete.html";
    }else{
        map.put("failure", "Invalid Session");
        return "login.html";
    }
}

@PostMapping("/insert")
//@ResponseBody
public String insert( Student student,HttpSession session,ModelMap map,@RequestParam MultipartFile image){
    if(session.getAttribute("user")!=null){
      //  return "insert.html";
      student.setPicture(addToCloudinary(image));
      studentJpa.save(student);
      map.put("success", "Record Saved Successfully");
     // return student.toString();
     return "home.html";
    }else{
        map.put("failure", "Invalid Session");
        return "login.html";
    }
}
// To insert the image in spring boot follow the follwing steps:
//1. in the insert.html page's form gve the attribute enctype="multipart/form-data"
//2. The come to controller class and annotate with @MultipartConfig
//3. and add addToCloudinary method which is used to upload the data to the Cloudinary website And gives us the link and this link will be stored in the data base 
public String addToCloudinary(MultipartFile image) {
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "dw9lkzcvs", "api_key",
				"425881681569198", "api_secret", "vAacs36r1fOWpWs1kyuIxABU2gg", "secure", true));

		Map resume = null;
		try {
			Map<String, Object> uploadOptions = new HashMap<String, Object>();
			uploadOptions.put("folder", "Student Pictures");
			resume = cloudinary.uploader().upload(image.getBytes(), uploadOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (String) resume.get("url");
	}
}

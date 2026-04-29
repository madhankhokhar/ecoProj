package com.example.ecoProj.Controller;

import com.example.ecoProj.Service.UserService;
import com.example.ecoProj.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    public UserService service;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return service.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        System.out.println(user);
        return service.verify(user);
    }
      @GetMapping("/profile")
        public String profile(){
        return "User Profile";
    }

    @GetMapping("/dashboard")
    public String dashboard(){
        return "Dashboard";
    }
    
    @GetMapping("/delete-user")
    public String deleteUser(){
        return "Delete User";
    }

    @GetMapping("/manage-products")
    public String manageProducts(){
        return "Manage Products";
    }

}

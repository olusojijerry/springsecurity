package com.jerryssec.springsecuritysetup.controller;

import com.jerryssec.springsecuritysetup.model.Customer;
import com.jerryssec.springsecuritysetup.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersController {
    @Autowired
    CustomerRepository customerRepository;
    PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        try{
            String hashedPwd = passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashedPwd);
            Customer savedCustomer = customerRepository.save(customer);
            if (savedCustomer.getId() > 0)
                return ResponseEntity.status(HttpStatus.CREATED).body("Successfully Created user with username: " +customer.getUsername());
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("USer registration failed.");
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception error occurred: " + ex.getMessage());
        }
    }
}

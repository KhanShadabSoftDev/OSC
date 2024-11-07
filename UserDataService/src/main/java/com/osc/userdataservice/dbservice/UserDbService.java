package com.osc.userdataservice.dbservice;

import com.osc.userdataservice.entity.User;
import com.osc.userdataservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDbService {

    @Autowired
    private UserRepository userRepository;


    public boolean checkUniqueEmail(String emailId) {
        User user = userRepository.findByEmailId(emailId);
        return user == null;
    }

    public boolean save(User user) {

        User savedUser = this.userRepository.save(user);
        return savedUser != null;
    }

    public User checkValidEmail(String email) {
        User user = this.userRepository.findByEmailId(email);
        return user;
    }

    public User verifyCredential(String userId) {

        Optional<User> byId = this.userRepository.findById(userId);

        return byId.orElse(null);
    }

    public User resetPassword(String email, String password) {
        User user = this.userRepository.findByEmailId(email);
        // Check if user exists
        if (user != null) {
            user.setPassword(password);
            return userRepository.save(user);
        }
        return null;
    }
}

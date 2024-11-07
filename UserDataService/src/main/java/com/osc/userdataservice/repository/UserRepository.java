package com.osc.userdataservice.repository;

import com.osc.userdataservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {

   User findByEmailId(String emailId);
}

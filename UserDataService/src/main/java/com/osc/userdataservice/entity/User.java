package com.osc.userdataservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User_Table")
public class User {

    @Id
    private String userId;
    private String name;
    private String emailId;
    private String password;
    private String mobileNumber;
    private String dateOfBirth;

}

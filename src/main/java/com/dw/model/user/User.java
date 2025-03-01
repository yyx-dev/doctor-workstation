package com.dw.model.user;

import java.util.Date;
import java.util.Set;
import lombok.Data;


@Data
public class User {
    private int id;
    private int doctorId;
    private int patientId;

    private String username;
    private String password;

    private String role; // 'doctor' 或 'patient'
    private Set<String> permissions;

    private Date createTime;

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
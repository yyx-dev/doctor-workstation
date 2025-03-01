package com.dw.service;

import com.dw.model.user.Doctor;

public class LoginService {
    private static Doctor currentDoctor;

    public static void login(Doctor doctor) {
        currentDoctor = doctor;
    }

    public static void logout() {
        currentDoctor = null;
    }

    public static Doctor currentDoctor() {
        if (currentDoctor == null) {
            throw new IllegalStateException("没有登录用户");
        }
        return currentDoctor;
    }
}
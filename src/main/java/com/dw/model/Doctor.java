package com.dw.model;

/**
 * 医生实体类
 */
public class Doctor {
    private int id;
    private int userId;
    private String name;
    private String gender;
    private String department;
    private String title; // 职称
    private String phone;

    // 关联的User对象(非数据库映射)
    private User user;

    // 默认构造函数
    public Doctor() {
    }

    // 带参数构造函数
    public Doctor(int userId, String name, String gender, String department, String title, String phone) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.department = department;
        this.title = title;
        this.phone = phone;
    }

    // getter和setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                ", title='" + title + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
package com.example.android2project.models;

public class User {

    public String fullName,age,email;
    public int jobsCounter;

    public User()
    {

    }

    public User(String fullname,String age,String email,int num)
    {
        this.fullName=fullname;
        this.age = age;
        this.email=email;
        this.jobsCounter=num;

    }


}

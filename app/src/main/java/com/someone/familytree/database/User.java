package com.someone.familytree.database;

public class User {
    private String email;
    private String password;
    private Boolean isPremium;

    public User(String email, String password, Boolean isPremium) {
        this.email = email;
        this.password = password;
        this.isPremium = isPremium;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

}

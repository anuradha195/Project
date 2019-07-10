package com.example.BootcampProject;

public class User {
    private String username;
    private String password;
    private String pwdConfirm;

    public String getUsername() {
        return username;
    }
    

    public User(String username, String password, String pwdConfirm) {
		super();
		this.username = username;
		this.password = password;
		this.pwdConfirm = pwdConfirm;
	}


	public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPwdConfirm() {
        return pwdConfirm;
    }

    public void setPwdConfirm(String pwdConfirm) {
        this.pwdConfirm = pwdConfirm;
    }
}

package com.franzoo.requestdto;

import lombok.Data;

@Data
public class UserSignInDTO {

	private String userMob;
	private String password;
	public String getUserMob() {
		return userMob;
	}
	public void setUserMob(String userMob) {
		this.userMob = userMob;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}

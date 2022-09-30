package com.franzoo.customresponse;



public class CustomResponseForSignIn {

private String name;

private String mob;

private String email;

private long friendscount;

private String location;

public CustomResponseForSignIn(String name, String mob, String email, long friendscount, String location) {
super();
this.name = name;
this.mob = mob;
this.email = email;
this.friendscount = friendscount;
this.location = location;
}

public CustomResponseForSignIn() {
super();
// TODO Auto-generated constructor stub
}
public long getFriendscount() {
return friendscount;
}



public void setFriendscount(long friendscount) {
this.friendscount = friendscount;
}



public String getLocation() {
return location;
}



public void setLocation(String location) {
this.location = location;
}




@Override
public String toString() {
return "CustomResponseForSignIn [name=" + name + ", mob=" + mob + ", email=" + email + ", friendscount="
+ friendscount + ", location=" + location + "]";
}



public String getName() {
return name;
}



public void setName(String name) {
this.name = name;
}



public String getMob() {
return mob;
}



public void setMob(String mob) {
this.mob = mob;
}



public String getEmail() {
return email;
}



public void setEmail(String email) {
this.email = email;
}





}
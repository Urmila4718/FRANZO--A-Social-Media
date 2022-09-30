package com.franzoo.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Friendreq")
public class Friendreq {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long fid;

@Column(nullable=false,length=2,unique=true)
private long userId;

@Column(nullable=false,length=20)
private long friendId;

@Column(nullable=true,length=3)
private long status;

public Friendreq() {
	super();
	// TODO Auto-generated constructor stub
}

public Friendreq(long fid, long userId, long friendId, long status) {
	super();
	this.fid = fid;
	this.userId = userId;
	this.friendId = friendId;
	this.status = status;
	
}
public long getFid() {
	return fid;
}

public void setFid(long fid) {
	this.fid = fid;
}

public long getUserId() {
	return userId;
}

public void setUserId(long userId) {
	this.userId = userId;
}

public long getFriendId() {
	return friendId;
}

public void setFriendId(long friendId) {
	this.friendId = friendId;
}

public long getStatus() {
	return status;
}

public void setStatus(long status) {
	this.status = status;
}



}
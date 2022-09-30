package com.franzoo.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import com.franzoo.entities.Friendreq;
import com.franzoo.entities.User;



public interface FriendRepository extends JpaRepository<Friendreq , Long> {
	

//	@Query("Select u from Friendreq u WHERE u.fid=?1")
//	Friendreq findByFriendId(long uid);

	@Transactional
	@Modifying
	@Query("UPDATE Friendreq f SET f.status=1 WHERE f.userId=?1 AND f.friendId=?2")
	int acceptFriendRequest(long userId, long friendId);

	@Transactional
	@Modifying
	@Query("UPDATE Friendreq f SET f.status=2 WHERE f.userId=?1 AND f.friendId=?2")
	int deleteReq(long userId, long friendId);
	
	@Query("SELECT u FROM Friendreq u WHERE u.status=?1")
	Friendreq Fetch_status(String status);
		
	@Query("Select f from Friendreq f WHERE f.userId = ?1 AND f.friendId = ?2")
	Friendreq fetchByID(long userId, long friendId);
	
	@Query("Select f from Friendreq f WHERE f.friendId = ?1")
	Friendreq fetchBy_friendID(long friendId);
	
	@Query("Select f from Friendreq f WHERE f.userId = ?1")
	Friendreq fetchBy_userID(long userId);

	@Query("Select f.userId from Friendreq f WHERE f.status =1 ")
	ArrayList<Friendreq> countbyuserId(long status);
	

	
}
	



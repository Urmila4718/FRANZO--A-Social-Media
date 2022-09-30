package com.franzoo.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//import javax.validation.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.franzoo.customresponse.CustomRequestForChangePassword;
import com.franzoo.customresponse.CustomResponse;
import com.franzoo.customresponse.CustomResponseForFetchUser;
import com.franzoo.customresponse.CustomResponseForPrivateStatus;
import com.franzoo.customresponse.CustomResponseForSendMessage;
import com.franzoo.customresponse.CustomResponseForSignIn;
import com.franzoo.customresponse.CustomResponseForTwoFAStatus;
import com.franzoo.entities.Advertise;
import com.franzoo.entities.ChatEntity;
import com.franzoo.entities.Friendreq;
import com.franzoo.entities.Postdata;
import com.franzoo.entities.User;
import com.franzoo.repository.AdvertiseRepository;
import com.franzoo.repository.ChatRepository;
import com.franzoo.repository.FriendRepository;

import com.franzoo.repository.PostRepository;
import com.franzoo.repository.UserRepository;
import com.franzoo.requestdto.ChatDTO;
import com.franzoo.requestdto.NewPasswordDTO;
import com.franzoo.requestdto.OtpAuthDTO;
import com.franzoo.requestdto.UserSignInDTO;
import com.franzoo.requestdto.friendDTO;
import com.franzoo.responsedto.CustomResponseForException;
import com.franzoo.service.MailService;
import com.franzoo.service.Validations;

@Controller
public class AppController {
	@Autowired
	UserRepository repo;
	@Autowired
	MailService mailService;
	@Autowired
	PostRepository postd;
	@Autowired
	ChatRepository chatRepository;
	@Autowired
	FriendRepository frepo;
	@Autowired
	Validations validation;
	
//*******************Finding all the Users from Database***************//
		@GetMapping("/process_register")
			public List<User> GetRegistration(@RequestBody User user) {
			return repo.findAll();
		}
	
		@RequestMapping("/")
		public String getHomePage() {
			return "welcome";	
		}
		@RequestMapping("/signUpOtp")
		public String getSignUpOTPPage() {
			return "SignUp_OTP";	
		}
		@RequestMapping("/sign_in")
		public String getSignInPage() {
			return "sign_in";	
		}
		@RequestMapping("/signUp")
		public String getSignUpPage() {
			return "SignUp";
		}
		@RequestMapping("/franzoohomepage")
		public String getDashboardPage() {
			return "combine_profile";
		}
		@RequestMapping("/forgetPassword")
		public String getforgetpasswordPage() {
			return "forg_pass";
		}
		
		@RequestMapping("/forgetmailverify")
		public String getforgetEmailVerify() {
			return "otpverify";
		}
		
		@RequestMapping("/setnewpassword")
		public String getNewPasssordPage(){
			return "new_password";
		}
		@RequestMapping("/PreferencePage")
		public String getPreferncePage() {
			return "Prefernces_Page";	
	}
		@RequestMapping("/friendlist")
		public String getfriendlist() {
			return "friend_list";	
	}
		@RequestMapping("/aboutme")
		public String getprofile() {
			return "about_me_dummy";	
	}
		@RequestMapping("/terms")
		public String getterms() {
			return "terms";	
	}
		@RequestMapping("/privacy")
		public String getpolicy() {
			return "privacy";	
	}
		
//*******************SignUp********************//	
	@RequestMapping(value = "/process_register", method = RequestMethod.POST )
		public ResponseEntity <Object> processRegistration(@RequestBody User user) {
		Date dt=  Calendar.getInstance().getTime();
		user.setCreated_at(dt.getTime()+"");
		validation.signUpValidation(user);
		 try {
			 String passwd= user.getPassword();
			 System.out.println("user pass is:-"+user.getPassword());
			 MessageDigest md = MessageDigest.getInstance("MD5");
			 byte[] messageDigest = md.digest(passwd.getBytes());
			 BigInteger number = new BigInteger(1, messageDigest);
			 String hashtext= number.toString(16);
			 while(hashtext.length()< 32)
			 {
			 hashtext = "0" + hashtext;
			 }
			 System.out.println("Enc is:"+hashtext);
			 user.setPassword(hashtext);
			 user.setCreated_at(dt.toString());
			 repo.save(user);
			 //****************************************//
				String s = mailService.sendEmail(user);
			 	
				//String s = String.valueOf(mailService.generateOtp());
				user.setOTP(s);
				System.out.print("harsh_1");
				repo.save(user);
				System.out.print("harsh_2");
				//return "register saved successfully";
				//return "/SignUp_OTP";
				User response = new User(user.getUid(), user.getEmail(), user.getMob(), user.getName(),user.getPassword(),user.getCreated_at(),user.getIs_Private(),user.getOTP(),user.getTwoFA());
		// CustomResponseForSendMessage response = new CustomResponseForSendMessage("Message Send Successfully",chat.getToId(),chat.getFromId(),date.toString(),chat.getMessage());

				return new ResponseEntity<Object>(response,HttpStatus.OK);
				}catch(Exception ex) {
					ex.printStackTrace();
					CustomResponseForException response =  new CustomResponseForException("Exception occured","500",ex);
					return new ResponseEntity<Object>(response,HttpStatus.OK);
					
				//return "failed:"+ex.getMessage();
				}	
		}
	
	//*********************Otp Authentication After Sign Up*******************//
		@PostMapping("/otpauth")
		public ResponseEntity <Object> authenticateUser(@RequestBody OtpAuthDTO otpauth){
			User o= repo.findByOtp(otpauth.getOtp());
			if(o!=null)
			{
				//System.out.print("harsh_3");
				return new ResponseEntity<Object>("OTP Verified, You can Log-In",HttpStatus.OK);
			}else
			{
				//System.out.print("harsh_4");
				return new ResponseEntity<Object>("OTP Verification Failed",HttpStatus.NOT_FOUND);
			}
		}
		
		@DeleteMapping("/delete_user/{uid}")
		public String deleteUser(@PathVariable("uid") Long uid) {
			repo.deleteById(uid);
			return "user deleted";
		}
//****************************Searching Public Users************************//	
		@PostMapping("/search")
		public ResponseEntity<Object> search(@RequestBody User user){
			List<User> fetchUser = repo.searchName(user.getName());
			List<CustomResponseForFetchUser> users = new ArrayList<>();
			for(int i=0;i<fetchUser.size();i++) {
				CustomResponseForFetchUser reponse = new CustomResponseForFetchUser(fetchUser.get(i).getName()
						,fetchUser.get(i).getEmail(),fetchUser.get(i).getMob());
				users.add(reponse);
			}
			return new ResponseEntity<Object>(users,HttpStatus.OK);
		}
//*****************************USER PREFERENCES CHANGE PASSSWORD*****************************//		
		@PostMapping("/changePassword")
		public ResponseEntity<Object> changePassword(@RequestBody CustomRequestForChangePassword user){
			User fetchUser = repo.fetchUserbyEmail(user.getEmail());
			if(fetchUser!=null) {
				String passwd= user.getOldPassword();
				 MessageDigest md = null;
				try {
					md = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 byte[] messageDigest = md.digest(passwd.getBytes());
				 BigInteger number = new BigInteger(1, messageDigest);
				 String hashtext= number.toString(16);
				 while(hashtext.length()< 32)
				 {
				 hashtext = "0" + hashtext;
				 }
				 validation.changePasswordValidation(user);
				if(fetchUser.getPassword().equals(hashtext)) {
					String newPassword= user.getNewPassword();
					 MessageDigest mdNew = null;
					try {
						mdNew = MessageDigest.getInstance("MD5");
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 byte[] messageDigestNew = mdNew.digest(newPassword.getBytes());
					 BigInteger numberNew = new BigInteger(1, messageDigestNew);
					 String hashNewPassword= numberNew.toString(16);
					 while(hashNewPassword.length()< 32)
					 {
						 hashNewPassword = "0" + hashNewPassword;
					 }
					if(fetchUser.getPassword().equals(hashNewPassword)) {
						CustomResponse response = new CustomResponse("Old and New Password can't be same");
						return new ResponseEntity<Object>(response,HttpStatus.OK);
					}else
						{
						
						repo.changePassword(hashNewPassword,fetchUser.getEmail());
						CustomResponse response = new CustomResponse("Password Changed Successfully");
						return new ResponseEntity<Object>(response,HttpStatus.OK);
					}
				}else {
					CustomResponse response = new CustomResponse("Old Password not matched");
					return new ResponseEntity<Object>(response,HttpStatus.OK);
				}
				
			}else {
				CustomResponse response = new CustomResponse("User Not Found");
				return new ResponseEntity<Object>(response,HttpStatus.OK);
			}
//			repo.changePassword(user.getEmail(),user.getPassword());
//			return null;
		}
		
		
		
//***************************************Chat Message Send*************************************//
		
		@PostMapping("/sendMessage")
		public ResponseEntity<Object> sendMessage(@RequestBody ChatEntity chat) {
		 Date date=  Calendar.getInstance().getTime();
		 validation.chatValidation(chat);
		 if(chat.getFromId() != chat.getToId()) {
			 User fetchToId = repo.fetchUserById(chat.getToId());
			 if(fetchToId!=null) {
				 User fetchFromId = repo.fetchUserById(chat.getFromId());
				 if(fetchFromId != null) {
					 chat.setToId(fetchToId.getUid());
					 chat.setFromId(chat.getFromId());
					 chat.setCreatedAt(date.toString());
					 chat.setMessage(chat.getMessage());
					 chatRepository.save(chat);
					 CustomResponseForSendMessage response = new CustomResponseForSendMessage("Message Send Successfully",chat.getToId(),chat.getFromId(),date.toString(),chat.getMessage());
					 return new ResponseEntity<Object>(response, HttpStatus.OK);
				 }else {
					 CustomResponse response = new CustomResponse("Sender not found");
					 return new ResponseEntity<Object>(response, HttpStatus.OK);
				 }
			 }else {
				 CustomResponse response = new CustomResponse("Receiver not found");
				 return new ResponseEntity<Object>(response, HttpStatus.OK);
			 }
		 }else {
			 CustomResponse response = new CustomResponse("Sender and Receiver id can not be same");
			 return new ResponseEntity<Object>(response, HttpStatus.OK);
		 }
	 }
		
		
//*****************************************Receive Message**************************////
		
		@GetMapping("/receiveMessage")
		public ResponseEntity<Object> receiveMessage() {
			List<ChatEntity> fetchMessages = chatRepository.receiveAllMessages();			
			List<ChatDTO> chatReceiver = new ArrayList<>();
			for(int i=0;i<fetchMessages.size();i++) {
				ChatDTO chatdto = new ChatDTO(fetchMessages.get(i).getToId(),fetchMessages.get(i).getFromId(),  fetchMessages.get(i).getCreatedAt(),fetchMessages.get(i).getMessage());
				chatReceiver.add(chatdto);
			}
			
			return new ResponseEntity<Object>(chatReceiver, HttpStatus.OK);
		}

//****************Sign In With Encrypted Password*****************//
	@PostMapping("/signin")
	public ResponseEntity <Object> signIn(@RequestBody UserSignInDTO usersign){
		String passwd=usersign.getPassword();
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(passwd.getBytes());
			 BigInteger number = new BigInteger(1, messageDigest);
			 String hashtext= number.toString(16);
			 while(hashtext.length()< 32)
			 {
			 hashtext = "0" + hashtext;
			 }
			 System.out.println("Enc is:"+hashtext);
			 usersign.setPassword(hashtext);
			User u= repo.findByMob(usersign.getUserMob(), usersign.getPassword());
			if(u!=null)
			{
				System.out.print("login successfull");
				CustomResponseForSignIn response = new CustomResponseForSignIn(u.getName(),u.getMob(),u.getEmail(),u.getFriendscount(),u.getLocation());
				return new ResponseEntity<Object>(response,HttpStatus.OK);
				
			}else
			{
				System.out.print("login failed");
				return new ResponseEntity<Object>("Login Failed",HttpStatus.NOT_FOUND);
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.print("login failed_1");
			return new ResponseEntity<Object>("Login Failed",HttpStatus.NOT_FOUND);
		}		 
	}
	//***************************************is_private*********************************//
	@GetMapping("/notprivate")
		public List<User> PrivateRegistration(){
		return repo.findByPrivate();
	}
	
//**********************New Email Mapping for Forgot_pass***********************//
	@PostMapping("/forgetPasswordEmail")
	public ResponseEntity<Object> resendEmail(@RequestBody User user) {
		User f = repo.fetchUserbyEmail(user.getEmail());
		if(f !=null) {
			if(f.getEmail().equals(user.getEmail())) {
		try
			{
				String s = mailService.sendEmail(user);
				user.setOTP(s);
				repo.updateOtp(s,user.getEmail());
				System.out.print("Otp Send on your email!!!");
				return new ResponseEntity<Object>("Otp Send on your email!!!",HttpStatus.OK);
				}
		catch(Exception ex)
				{
			System.out.print("failed");
					return new ResponseEntity<Object> ("failed"+ex.getMessage(),HttpStatus.OK);
				}
		}
		}
		else {
			System.out.print("Email not found");
			return new ResponseEntity<Object> ("Email not found",HttpStatus.OK);
		}
		System.out.print("Invalid Data");
		return new ResponseEntity<Object> ("Invalid Data",HttpStatus.OK);
}
	
//*****************Otp validation for forget password***************//
	@PostMapping("/forgetPasswordOtp")
	public ResponseEntity <Object> forgetPasswordOtp(@RequestBody OtpAuthDTO otpauth){
	User f= repo.findByOtp(otpauth.getOtp());
	if(f!=null)
	{
		if(f.getOTP().equals(otpauth.getOtp())) 
		{
			if(f.getEmail().equals(otpauth.getEmail())) 
			{	
				return new ResponseEntity<Object>("OTP Verified, Please Change your Password",HttpStatus.OK);
			}
		}
	}
	else
	{
	return new ResponseEntity<Object>("OTP Verification Failed,Please Put Correct OTP or Press on Resend OTP",HttpStatus.NOT_FOUND);
	}
	return new ResponseEntity<Object>("Invalid Data",HttpStatus.OK);
	}	
	
	//***********************************post table crud operation**********************************************	
		@GetMapping("/viewpost")
		public List<Postdata> GetPost(){
			return postd.findAll();
		}

		@DeleteMapping("/deletepost/{postId}")
		public String deletePost(@PathVariable("postId") Long postId)
		{
			postd.deleteById(postId);
			return "Post deleted";
		}
		@PostMapping("/upload")
		public String Uploadpost(@RequestBody Postdata postdata)
		{
			postd.save(postdata);
			return("Post uploaded");
		}
		
		 class Advertise_Controller {
			@Autowired
			private AdvertiseRepository ad;
			
			@GetMapping("/advertise")
			public List<Advertise> GetAdvertise() {
			return ad.findAll();
			}
			@DeleteMapping("advertise/{id}")
			public String deleteCourse(@PathVariable("id") Long id)
			    {

				ad.deleteById(id);
				return "Successfully deleted!";
				}
			
			@PostMapping("/advertise")
			public String PostAdvertise(@RequestBody Advertise advertise)
			{
				ad.save(advertise);
				return "Advertise Uploaded";
				}
		}
		 
//*****************************************Forget Password***********************************************************//
		 @PostMapping("/forgetNewPassword")
			public ResponseEntity <Object> resetPassword(@RequestBody NewPasswordDTO newPassword){
			 	User fetchUser = repo.fetchUserbyEmail(newPassword.getEmail());
			 		if(fetchUser != null) {
			 			String newpasswd=newPassword.getPassword();
			 			MessageDigest md;
			 				//if(fetchUser.getEmail().equals(newPassword.getEmail())) {
			 			validation.forgetPasswordOtpValidation(newPassword,fetchUser);
			 				//	if(fetchUser.getOTP().equals(newPassword.getOtp())) {
			 			
			 							try 
			 							{
			 								md = MessageDigest.getInstance("MD5");
			 								byte[] messageDigest = md.digest(newpasswd.getBytes());
			 								BigInteger number = new BigInteger(1, messageDigest);
			 								String hashtext= number.toString(16);
			 									while(hashtext.length()< 32)
			 									{
			 										hashtext = "0" + hashtext;
			 									}
			 									System.out.println("Enc is:"+hashtext);
			 									newPassword.setPassword(hashtext);
			 									repo.changePassword(newPassword.getPassword(),newPassword.getEmail());
			 									NewPasswordDTO response = new NewPasswordDTO(newPassword.getOtp(),newPassword.getPassword(),newPassword.getEmail());
			 									return new ResponseEntity<Object>(response,HttpStatus.OK);
			 							} 
			 							catch (NoSuchAlgorithmException e) 
			 							{
			 								return new ResponseEntity<Object>(e.getMessage(),HttpStatus.NOT_FOUND);
			 							}
			 					}
//			 					else {
//				 					return new ResponseEntity<Object>("Please Enter Valid Otp",HttpStatus.OK);
//				 				}
//			 				}
//			 				else {
//			 					return new ResponseEntity<Object>("Please Enter Valid Email",HttpStatus.OK);
//			 				}
//			 			}
			 		
			 		return new ResponseEntity<Object>("password change failed",HttpStatus.NOT_FOUND);
		 }
		 
//********************************************UPDATE PRIVATE STATUS************************************//
		 @RequestMapping(value = "/updatePrivateStatus", method = RequestMethod.POST )
		 public ResponseEntity<Object> updatePrivateStatus(@RequestBody User user)
		 {
			 User fetchUser = repo.fetchUserbyEmail(user.getEmail());
			 try {
			 if(fetchUser != null) {
				 if(fetchUser.getEmail().equals(user.getEmail())) {
					 if(fetchUser.getIs_Private()!=user.getIs_Private()) {
						//validation.privateStatusValidation(user);
						 boolean s = user.getIs_Private();
						 user.setIs_Private(s);
						 repo.updateByPrivateStatus(s,user.getEmail());
						 CustomResponseForPrivateStatus response = new CustomResponseForPrivateStatus(user.getIs_Private(),user.getEmail());
						 return new ResponseEntity<Object>(response, HttpStatus.OK);
				 }
//					 else {
//					 return new ResponseEntity<Object>("Status is already changed",HttpStatus.OK);
//				 }
			 }
			 }
			 }
			 catch(Exception ex) {
				 return new ResponseEntity<Object>(ex.getMessage(),HttpStatus.NOT_FOUND);
			 }
			 return new ResponseEntity<Object>("Status Not Updated",HttpStatus.OK);
		 }
		 
//*******************************************UPDATE TWOFA STATUS**************************************//
	
		 @RequestMapping(value = "/updateTwoFAStatus", method = RequestMethod.POST )
		 public ResponseEntity<Object> updateTwoFAStatus(@RequestBody User user)
		 {
			 User fetchUser = repo.fetchUserbyEmail(user.getEmail());
			 try {
			 if(fetchUser != null) {
				 if(fetchUser.getEmail().equals(user.getEmail())) {
					 if(fetchUser.getTwoFA()!=user.getTwoFA()) {
						 
						//validation.privateStatusValidation(user);
						 boolean s = user.getTwoFA();
						 user.setTwoFA(s);
						 repo.updateByTwoFAStatus(s,user.getEmail());
						 CustomResponseForTwoFAStatus response = new CustomResponseForTwoFAStatus(user.getTwoFA(),user.getEmail());
						 return new ResponseEntity<Object>(response, HttpStatus.OK);
				 }
			 }
			 }
			 }
			 catch(Exception ex) {
				 return new ResponseEntity<Object>(ex.getMessage(),HttpStatus.NOT_FOUND);
			 }
			 return new ResponseEntity<Object>("Status Not Updated",HttpStatus.OK);
		 }
			
			

			 @PostMapping("/sendreq")
			 public ResponseEntity<Object> sendRequest(@RequestBody Friendreq req) {
			 if(req.getUserId() != req.getFriendId()) {
			 User friend_id = repo.fetchUserById(req.getFriendId());
			 if(friend_id!=null) {
			 User user_id = repo.fetchUserById(req.getUserId());
			 if(user_id != null) {
			 validation.Friendrequest(req);
			 req.setFriendId(req.getFriendId());
			 req.setUserId(req.getUserId());
			 req.setStatus(req.getStatus());
			 frepo.save(req);
			 return new ResponseEntity<Object>("Friend Request Sent", HttpStatus.OK);
			
			 }else {
			 return new ResponseEntity<Object>("User not found", HttpStatus.NOT_FOUND);
			 }
			 }else {

			 }
			 return new ResponseEntity<Object>("Friend not found", HttpStatus.NOT_FOUND);
			 }
			 else {
			 return new ResponseEntity<Object>("User and Friend id should not be same", HttpStatus.OK); }

			 }
		
			 @PostMapping("/acceptFriendRequest")
			 public ResponseEntity <Object> accept_Friend_Request(@RequestBody Friendreq req){
			 Friendreq fetchid = frepo.fetchByID(req.getUserId(), req.getFriendId());
			 long pending_status = 0;
			 long accept_status = 1;
			 long delete_status = 2;
			 System.out.println(fetchid);
			 if(fetchid!=null)
			 {
			 if (fetchid.getStatus()==accept_status) {
			 return new ResponseEntity<Object>("Already Friend",HttpStatus.OK);
			 }
			 else if (fetchid.getStatus()==pending_status) {
			 frepo.acceptFriendRequest(req.getUserId(),req.getFriendId());
			 return new ResponseEntity<Object>("Request Accepted",HttpStatus.OK);
			 }

			 else if (fetchid.getStatus()==delete_status) {
			 return new ResponseEntity<Object>("Send request again",HttpStatus.OK);
			 }
			 }
			 return new ResponseEntity<Object>("Invalid",HttpStatus.NOT_FOUND);
			 }

			 @PostMapping("/deletereq")
			 public ResponseEntity <Object> deleteReq(@RequestBody Friendreq req)
			 {
			 Friendreq fetchid = frepo.fetchByID(req.getUserId(), req.getFriendId());
			 long pending_status = 0;
			 long accept_status = 1;
			 long delete_status = 2;
			 if(fetchid!=null)
			 {
			 if (fetchid.getStatus()==accept_status) {
			 return new ResponseEntity<Object>(" You can't delete request",HttpStatus.OK);
			 }
			 else if (fetchid.getStatus()==pending_status) {
			 frepo.acceptFriendRequest(req.getUserId(),req.getFriendId());
			 return new ResponseEntity<Object>("Request Deleted",HttpStatus.OK);
			 }

			 else if (fetchid.getStatus()==delete_status) {
			 return new ResponseEntity<Object>("Already Request deleted",HttpStatus.OK);
			 }
			 }
			 return new ResponseEntity<Object>("Invalid",HttpStatus.NOT_FOUND);

			 }
			 @DeleteMapping("/removefriend")
			 public ResponseEntity <Object> remove_Friend(@RequestBody Friendreq req)
			 {
				 
			 frepo.deleteById(req.getFid());
			 return new ResponseEntity<Object>("Friend Removed",HttpStatus.OK);
			 		
			 } 
		//****************************************************************************************************//
//			 @RequestMapping(value = "/profile", method = RequestMethod.POST )
//				public ResponseEntity <Object> procfileRegistration(@RequestBody Friendreq req) {
//				 List<Friendreq> r=frepo.countbyuserId(req.getUserId());
//				 int c=Collections.frequency(r, r);
//				 System.out.println(c);
//				return new ResponseEntity<Object>("Updated",HttpStatus.OK);
//				}
			 @RequestMapping(value = "/profile", method = RequestMethod.POST)
				public ResponseEntity<Object> profileRegistration(Friendreq req) {
					List<Friendreq> r = frepo.countbyuserId(req.getStatus());	
	
					List<friendDTO> fr = new ArrayList<>();

					for(int i=0;i<r.size();i++) {
		
						friendDTO fdto = new friendDTO(r.get(i).getUserId());
						fr.add(fdto);
					}
					int fd=fr.size();
					return new ResponseEntity<Object>(fd, HttpStatus.OK);
				}
			
				
}

var dataparameter="";
function userSignIn(){
	//debugger;
	var mobile = document.getElementById('phone').value;
	console.log(mobile);
	var password = document.getElementById('SignInPassword').value;
	console.log(password);
	
	if(mobile == ""){
		console.log("1");
		alert("Enter the valid mobile number");
		
	}
	else if(password == ""){
		console.log("2");
		alert("please enter the password");
	}
	else {
		console.log("3")
		var signInParams = {
			"userMob" : mobile,
			"password" : password
		};
		var signInURL =  "http://127.0.0.1:8080/signin";
		console.log(signInURL);
		console.log(signInParams);
		$.ajax({
			method : "POST",
			url: signInURL, 
			data:JSON.stringify(signInParams),
			contentType:"application/json",
			dataType : 'json',
			success :
				    function (data, response) {// success callback function
				    console.log("This is data");
				    console.log(data);
				    console.log("This is response");
				    console.log(response);
				  //  if(data.responseText == "Login Successful"){
					
					alert("Logged In Successful");
					dataparameter = data;
					console.log("this is data parameter");
					console.log(dataparameter);
					localStorage.setItem("Name",dataparameter.name);
					localStorage.setItem("Location", dataparameter.location);
					localStorage.setItem("Friendscount",dataparameter.friendscount);
					
				         window.location = "franzoohomepage";
			},	            	
			error : function (data, response) {// success callback function
            //alert(JSON.stringify(data)+" -> "+status);
            console.log("5");
            console.log("This is data");
				    console.log(data);
				   // console.log("This is response");
				    console.log(response);
            	    console.log("Response-->", response);
           
				alert("wrong credentials");
				window.location = "sign_in";


    			
	 }
		});
	}
}

  function onlyNumberKey(evt){
		var code = (evt.which) ? evt.which : evt.keyCode
			if(code>31 && (code<48 || code>57))
				return false;
			return true;
}

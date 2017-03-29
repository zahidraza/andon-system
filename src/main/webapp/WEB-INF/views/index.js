function Login(){
    //var emailRegex = /^[A-Za-z0-9._]*\@[A-Za-z]*\.[A-Za-z]{2,5}$/;
    var user_idRegex = /[0-9]{5}/;
    var user_id = document.form.user_id.value;
    var password = document.form.password.value;
    var user_level = document.form.user_level.value;
    
    if( user_id==="" ){
        document.form.user_id.focus();
        document.getElementById("errorBox").innerHTML = "Enter Employee ID";
        return false;
    }else if(!user_idRegex.test(user_id)){
        document.form.user_id.focus();
        document.getElementById("errorBox").innerHTML = "Invalid Employee ID. It can only be Numeric value of 5 digits";
        return false;
    }
 
    if(password==="" ){
        document.form.password.focus();
        document.getElementById("errorBox").innerHTML = "Enter Password";
        return false;
    }else if(password.length < 5){
        document.form.password.focus();
        document.getElementById("errorBox").innerHTML = "Password should be at least 5 character long";
        return false;
    }
    
    if(user_level===""){
        document.form.user_level.focus();
        document.getElementById("errorBox").innerHTML = "Select User Role";
        return false;
    }
    
}



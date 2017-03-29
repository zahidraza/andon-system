var ajaxRequest;
function ajaxFunction(){

    try{
       // Opera 8.0+, Firefox, Safari 
       ajaxRequest = new XMLHttpRequest();
    }catch (e){

       // Internet Explorer Browsers
        try{
            ajaxRequest = new ActiveXObject("Msxml2.XMLHTTP");
        }catch (e) {
            try{
                ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
            }catch (e){

                // Something went wrong
                alert("Your browser broke!");
                return false;
            }
        }
    }
}
function showUsersView(desgnId){
    ajaxFunction();
    if(!(desgnId == "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_users").innerHTML = ajaxRequest.responseText;
            }
        };
        
        var url = "user?action=showUsers&page=viewPage&desgnId=" + desgnId;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}
function showUsersRemove(desgnId){
    ajaxFunction();
    if(!(desgnId == "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_users").innerHTML = ajaxRequest.responseText;
            }
        };
        
        var url = "user?action=showUsers&page=removePage&desgnId=" + desgnId;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}

//Show Problems for view page
function showProblems(dept_id){
    
    ajaxFunction();
    var user_id = document.getElementById('userId').value;
    if(!(dept_id === "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_problem").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "user?action=showProblems&dept_id=" + dept_id + "&user_id=" + user_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}



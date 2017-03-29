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
function abc(level){
    alert(level);
    return false;
}
            
function showUsersView(level){
    alert(level);
    ajaxFunction();
    if(!(level == "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_users").innerHTML = ajaxRequest.responseText;
            }
        };
        
        var url = "user?action=view&level=" + level;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}
function showUsersRemove(user_level){
    //alert(user_level);
    ajaxFunction();
    if(!(user_level == "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_users").innerHTML = ajaxRequest.responseText;
            }
        };
        
        var url = "user?page=remove&user_level=" + user_level;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}
function showSection(dept_id){

    ajaxFunction();
    
    if(!(dept_id === "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_section").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "user2?dept_id=" + dept_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
} 
function showProblem(sec_id){
     
    ajaxFunction();
    
    if(!(sec_id === "")){
        var select = document.getElementById("dept-select");
        var dept_id = select.options[select.selectedIndex].value;
        var user_id = document.getElementById("user").value;

        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                //alert(ajaxRequest.responseText);
                document.getElementById("display_problem").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "user2?dept_id=" + dept_id + "&sec_id=" + sec_id + "&user_id=" + user_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}

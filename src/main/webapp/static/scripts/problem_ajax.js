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

//Show Problems for view page
function showProblemView(dept_id){
    
    ajaxFunction();
    
    if(!(dept_id === "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_problem").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "problem?action=showProblems&page=view&dept_id=" + dept_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}

//Show Problems for update page
function showProblemUpdate(dept_id){
    ajaxFunction();
    if(!(dept_id === "")){
       // var select = document.getElementById("dept-select");
        //var dept_id = select.options[select.selectedIndex].value;
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_problem").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "problem?action=showProblems&page=update&dept_id=" + dept_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}

//Show Problems for view page
function showProblemRemove(dept_id){
    ajaxFunction();
    if(!(dept_id === "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_problem").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "problem?action=showProblems&page=remove&dept_id=" + dept_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}
//Show Users on prob_mapping.jsp file
function showUsers(level){
    ajaxFunction();
    if(!(level == "")){
        var prob_id = document.getElementById("prob_id").value;
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_users").innerHTML = ajaxRequest.responseText;
            }
        };
        
        var url = "problem?action=showUsers&level=" + level + "&prob_id=" + prob_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}




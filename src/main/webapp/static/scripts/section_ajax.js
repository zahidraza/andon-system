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

function viewSection(dept_id){
    //alert(dept_id);
    ajaxFunction();
    
    if(!(dept_id === "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_sec").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "section?page=view&dept_id=" + dept_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}
function removeSection(dept_id){
    ajaxFunction();
    
    if(!(dept_id === "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_sec").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "section?page=remove&dept_id=" + dept_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}

function updateSection(dept_id){
    ajaxFunction();
    
    if(!(dept_id === "")){
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_sec").innerHTML = ajaxRequest.responseText;
            }
        };
        var url = "section?page=update&dept_id=" + dept_id;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}
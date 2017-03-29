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

function showUsers(level){
    
    ajaxFunction();
    if(!(level == "")){
        //alert(level);
        var line = document.getElementById("line_no").value;
        //alert(line);
        ajaxRequest.onreadystatechange = function(){
            if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200){
                document.getElementById("display_users").innerHTML = ajaxRequest.responseText;
            }
        };
        
        var url = "line?action=showUsers&level=" + level + "&line=" + line;
        ajaxRequest.open("GET",url,true);
        ajaxRequest.send();
    }
}

var addCoordinatesToInput = function(latitude, longitude) {
	 $("#koordinate").val(latitude + "," + longitude)
}

$(document).ready(function(){
	var ws = new WebSocket("ws://localhost:8080/pokemon-finder-web/action");
	
	ws.onopen = function()
    {
       console.log("Spojen na WebSocket...");
    };
	ws.onmessage = function (evt) 
    { 
       console.log("Got from WebSocket " + evt.data);

       if(evt.data == "KRAJ") {
            var now = new Date();
            $("#messageCatch").append("<br><br> Kraj lova u " + now.getHours() + ":" + (now.getMinutes()<10?'0':'') + now.getMinutes());
       		$("#loader2").hide();
      	} else {
       		$("#messageCatch").append(evt.data);
      	}
       
    };
	
    if(localStorage.mandatoryParams != null) {
    	$("#messageLogin").html("Logirani ste kao " + JSON.parse(localStorage.mandatoryParams).username);
    }
    
    $(".scanButton").click(function(){
    	$("#loader1").show();
        $.get("/pokemon-finder-web/near/" + event.target.id, function(data, status){
        	$("#loader1").hide();
        	$("#scannedPokemonsUl").empty();
        	
        	for(i=0;i<data.length;i++){
        		var pokemon = data[i];
            	$("#scannedPokemonsUl").append('<li onclick=\"addCoordinatesToInput('+ pokemon.latitude + ',' + pokemon.longitude  +')\"><b>' + pokemon.name + "</b> istice u " 
            			+ new Date(pokemon.expires).getHours() + ":" + 
           			new Date(pokemon.expires).getMinutes() + " na lokaciji " + pokemon.latitude + ',' + pokemon.longitude + '   '
           			+  '<a target="_blank" href="http://maps.google.com/?q=' + pokemon.latitude + ',' + pokemon.longitude 
           					+ '">KARTA</a></li>');
        	}
        	$("#message").html("Nasao sam ih: " + data.length + "\nStatus: " + status);
        });
    });
    
    $("#buttonLogin").click(function(){
        $.post("/pokemon-finder-web/login?username=" + $("#username").val() + "&password=" + $("#password").val(), 
        function(data, status){
        	console.log(JSON.stringify(data));
		 	localStorage.setItem("mandatoryParams", JSON.stringify(data));
		    $("#messageLogin").html("Logirani ste kao " + $("#username").val());
		    $('#myModal').modal('hide');
		    $('#errorLogin').hide();
        })
        .fail(function() {
		    $("#messageLogin").html("Neuspješan login");
		    $('#errorLogin').show();
		  })
        });
        
      $("#uloviPokemona").click(function(){
      		$("#messageCatch").html("");
    	    var mandatoryParams = JSON.parse(localStorage.mandatoryParams);
      		if(mandatoryParams == null) {
      			alert("Molimo da se prvo prijavite se !");
      		}
      
    		$("#loader2").show();
          	var mandatoryParams = JSON.parse(localStorage.mandatoryParams);
        	var x = $("#koordinate").val().split(',')[0];
        	var y = $("#koordinate").val().split(',')[1];
    	    mandatoryParams.action = "CATCH";
        	mandatoryParams.latitude = x;
        	mandatoryParams.longitude = y;

			ws.send(JSON.stringify(mandatoryParams));        	
    });
});
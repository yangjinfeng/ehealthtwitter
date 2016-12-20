	function openDialog(url,deltaX,deltaY){
		var x = window.event.screenX + deltaX;
		var y = window.event.screenY + deltaY;
		var param = 'height=400, width=400,top='+y+', left='+x+',status=no';
		var chld = window.open(url , null, param);
		window.onfocus=function (){chld.focus();};
        window.onclick=function (){chld.focus();};
            
	}
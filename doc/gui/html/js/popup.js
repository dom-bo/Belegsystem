
function togglePopup(id, show)
{
	var ele = document.getElementById(id);
	
	if(ele) {
		ele.style.visibility = (show == true) ? 'visible' : 'hidden';
	}	
}
var g = {};
g.oldClass = {};

function highlightId(ids, bookable) {
	for(var i = 0; i < ids.length; i++) {
		var element = document.getElementById(ids[i]);
		if(element == null) 
			return;
				
		g.oldClass[ids[i]] = element.getAttribute("class");
		element.setAttribute("class", g.oldClass[ids[i]] + " highlight_" + bookable);
	}		
};

function unhighlightId(ids) {
	for(var i = 0; i < ids.length; i++) {
		var element = document.getElementById(ids[i]);
		if(element == null) 
			return;

		element.setAttribute("class", g.oldClass[ids[i]]);
	}		
};

function togglePopup(id, show) {
	var element = document.getElementById(id);
	
	if(element) {
		element.style.visibility = (show == true) ? 'visible' : 'hidden';
	}	
};
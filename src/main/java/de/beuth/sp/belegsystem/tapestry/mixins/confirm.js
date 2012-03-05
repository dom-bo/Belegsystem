// A class that attaches a confirmation box (with logic)  to
// the 'onclick' event of any HTML element.
// @author Chris Lewis Apr 18, 2008 <chris@thegodcode.net>

var Confirm = Class.create();
Confirm.prototype = {

	initialize: function(element, message) {
		this.message = message;
		Event.observe($(element), 'click', this.doConfirm.bindAsEventListener(this, element));
	},

	doConfirm: function(e, element) {
		if(!confirm(this.message)) {
			e.stop();
		} else {
			Event.stopObserving($(element), 'click', null);
		}
	}
};
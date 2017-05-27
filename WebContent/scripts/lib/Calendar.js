var Calendar = function(){
	
	initDay = function(){
		$('.day-select-list').find('option').remove().end();
	    $('.day-select-list').append("<option selected disabled>Choose a day</option>");
	    
	    Data.get('dayInit', function(data){
	    	$.each(data, function (index, element) {
	    		$('.day-select-list').append("<option>" + element);
	    	})
	    }, true);
	};
	
	initTime = function(id){
		hideInfoMessage();
	    $('#time-select').find('option').remove().end();
	    $('#time-select').append("<option selected disabled>Choose a time</option>");
	    
	    console.log("id= " + id);
	    
	    Data.give('timeInit', [id], function(data){
	    	$.each(data, function (index, element) {
	    		$('#time-select').append("<option>" + element);
	    	})
	    }, true);
	};
	
	refreshDayAndTime = function(){
		$('#time-select').empty();
	    $('#day-sel').empty();
	    Calendar.initDay();
	};
	
	
	return{
		initDay: initDay,
		initTime: initTime,
		refreshDayAndTime: refreshDayAndTime
	}
}();
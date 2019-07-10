$(document).ready(function() {

	$.getJSON('http://localhost:8080/api/charts/default',function(json_data){
		var ctx=document.getElementById("weeklyStepChart").getContext("2d");
		var data = json_data;
		var chart = new Chart(ctx,data);
	});

$.ajax({
	type: 'POST',
	url: 'http://loclhost:8080/api/auth/oauth',
	data: '{"url":"'+document.URL+'"}', // or json.stringify({name: 'jonas'}),
	success: function(data) { console.log('data: ' +data);},
	contentType: "application/json",
	dataType: 'json'
});
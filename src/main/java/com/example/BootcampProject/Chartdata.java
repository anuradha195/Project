package com.example.BootcampProject;

public class Chartdata {
	/* var chart = new CanvasJS.Chart("chartContainer", {
		animationEnabled: true,
		exportEnabled: true,
		title: {
			text: "Weekly Step Count Data"
		},
		data: [{
			type: "bar", //change type to bar, line, area, pie, etc
			dataPoints: <%out.print(dataPoints);%>
		}]
	}); */
	
	Boolean animationEnabled;
	public Boolean getAnimationEnabled() {
		return animationEnabled;
	}
	public void setAnimationEnabled(Boolean animationEnabled) {
		this.animationEnabled = animationEnabled;
	}
	public Boolean getExportEnabled() {
		return exportEnabled;
	}
	public void setExportEnabled(Boolean exportEnabled) {
		this.exportEnabled = exportEnabled;
	}
	public ChartTitle getTitle() {
		return title;
	}
	public void setTitle(ChartTitle title) {
		this.title = title;
	}
	public ChartArray[] getData() {
		return data;
	}
	public void setData(ChartArray[] data) {
		this.data = data;
	}
	Boolean exportEnabled;
	ChartTitle title ;
	ChartArray[] data;
	
	
	
	

}

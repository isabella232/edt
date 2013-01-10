/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineWidget(
		'dojo.widgets', 'DojoPieChart',
		'dojo.widgets', 'DojoChartBase',
		'div',
{
	"constructor" : function() {
		this.fontColor = "white";
		this.setFontSize(12);
		this.setFontWeight("normal");
	},
	"createDojoWidget" : function(parent){
		this.radius = this.radius || 100;
		var font = "normal normal "+this.getFontWeight()+" "+this.getFontSize()+" Tahoma";
		this.createChart(parent, this.radius*2 + 30, this.radius*2 + 30, {
			type: "Pie",
			fontColor: this.fontColor,
			font: font,
			labelOffset: this.labelOffSet || 40,
			radius: this.radius
	    });
		var data = [];
		for (var n=0; n<this.data.length; n++) {
			var slice = this.data[n]; 
			data.push({
				y: slice.y,
				color: slice.color,
				text: slice.text,
				fontColor: slice.fontColor || this.fontColor || "white"
			});
		}
		this.dojoWidget.addSeries("egl rocks", data);
		parent.setAttribute("align","left");
		this.dojoWidget.render();
	},
	"setFontColor" : function(color){ 
		this.fontColor=color;  
	},
	"getFontColor" : function(){ 
		return this.fontColor; 
	}	
});

/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
	'dojo.widgets', 'DojoLineGraph',
	'dojo.widgets', 'DojoChartBase',
	'div',
{
	"createDojoWidget" : function(parent) {
		this.createChart(parent, 300, 300, { 
			type:  "Lines", 
			markers : this.showMarkers, 
			tension : this.tension,
			shadows: this.showShadows ? {dx: 2, dy: 2, dw: 2} : {} 
		});
		var data = [];
		for (var n=0; n<this.data.length; n++) {
			data.push({
				tooltip: ""+this.data[n].value,
				x: n, 
				y: this.data[n].value
			});
		}
		if(this.minX == null) this.minX = 0;
		if(this.maxX == null) this.maxX = this.data.length - 1;
		this.addAxesWithMinMax();		
		this.dojoWidget.addSeries("default", data);
		parent.setAttribute("align","left");
		this.dojoWidget.render();
	}
});
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
	'dojo.widgets', 'DojoBubbleChart',
	'dojo.widgets', 'DojoChartBase',
	'div',								
{
	"createDojoWidget" : function(parent) {
		this.createChart(parent, 300, 300, { 
			type:  "Bubble", 
			areas: true, 
			gap:   this.barGap||5 
		});
		for (var n=0; n<this.data.length; n++)
			this.dojoWidget.addSeries("series "+n, [this.data[n]], {
				stroke: {color: "black"}, 
				fill: this.data[n].color || "red", 
				tooltip: this.data[n].tooltip || this.data[n]
			});
		this.addAxesWithMinMax();
		new dojox.charting.action2d.Magnify(this.dojoWidget, "default", { scale: 1.1 });
		new dojox.charting.action2d.Highlight(this.dojoWidget, "default");
		new dojox.charting.action2d.Tooltip(this.dojoWidget, "default");		
		this.dojoWidget.render();
		parent.style.whiteSpace = "nowrap";
		parent.setAttribute("align","left");
	}
});

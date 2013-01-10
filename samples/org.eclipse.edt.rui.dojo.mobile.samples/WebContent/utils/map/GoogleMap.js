/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

var mapInstance = 1;

egl.defineWidget(
	'utils.map', 'GoogleMap',  		// this class
	'eglx.ui.rui', 'Widget',  	// the super class
	'div',						// dom element type name
{
	"constructor" : function() {	
		this.eze$$DOMElement.id = "map0" 
		
		this.eze$$DOMElement.style.width = '100%';
		this.eze$$DOMElement.style.height = '100%';
		if(mapWidgets.length == 0){
			appendBootstrap();
		}
		if(typeof(google)!="undefined" && typeof(google.map)!="undefined"){
			this.createMap();
		}else{
			mapWidgets.appendElement(this);
		}		
	},
	"createMap" : function() {
		var thisWidget = this;
		if (!thisWidget.map) {
			var myLatlng = new google.maps.LatLng(thisWidget.centerLat || 35.7575731, thisWidget.centerLng || -79.0192997);
			var myOptions = {
			   zoom: thisWidget.zoom || 8,
			   center: (thisWidget.center ? null : myLatlng),
			   mapTypeId: thisWidget.mapType || google.maps.MapTypeId.ROADMAP
			};

			thisWidget.geocoder = new google.maps.Geocoder();
			thisWidget.directionsService = new google.maps.DirectionsService();
			
			thisWidget.map = new google.maps.Map(thisWidget.eze$$DOMElement, myOptions);		
			thisWidget.map.eglWidget = thisWidget;
			
			thisWidget.directionsDisplay = new google.maps.DirectionsRenderer();
			thisWidget.directionsDisplay.setMap(thisWidget.map);
			
			if (thisWidget.center) {
				thisWidget.setCenter(thisWidget.center);
			}
			
			if (thisWidget.markersOnInit) {
				for (var i = 0; i < thisWidget.markersOnInit.length; i++) {
					if (thisWidget.markersOnInit[i] != null) {
						thisWidget.addMarker(thisWidget.markersOnInit[i]);	
					}				
				} 
			}			
		}
	},
	"refresh" : function() {
		if (this.map) {
			google.maps.event.trigger(this.map, 'resize');
		}
	}
});

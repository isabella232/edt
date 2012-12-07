/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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
		this.eze$$DOMElement.id = "map_" + mapInstance;
				
		mapInstance++;		
		
		this.eze$$DOMElement.style.width = '100%';
		this.eze$$DOMElement.style.height = '100%';
		if(mapWidgets.length == 0){
			appendBootstrap();
		}
		if(typeof(google)!="undefined" && typeof(google.maps)!="undefined"){
			this.createMap();
		}else{
			mapWidgets.appendElement(this);
		}		
	},
	"destroy" : function() {
		
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
			
			//google.maps.event.addListener(thisWidget.map, 'zoom_changed', thisWidget.zoomChange);
			//google.maps.event.addListener(thisWidget.map, 'center_changed', thisWidget.centerChange);
			//google.maps.event.addListener(thisWidget.map, 'maptypeid_changed', thisWidget.mapTypeChange);
						
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
	"setWidth" : function(width) {
		this.width = width; 
		egl.setWidth(this.eze$$DOMElement, egl.toPX(width));
		
		if (this.map) {
			google.maps.event.trigger(this.map, 'resize');
		}
	},
	"setHeight" : function(height) {
		this.height = height;
		egl.setHeight(this.eze$$DOMElement, egl.toPX(height));
		
		if (this.map) {
			google.maps.event.trigger(this.map, 'resize');
		}
	},
	"getCenter" : function() {
		return (this.center ? this.center : null);
	},
	"setCenter" : function(address) {		
		this.center = address;
		if (!this.map) return;
		
		var thisWidget = this;
		var thisMap = this.map;
		
		this.geocoder.geocode( { 'address': address}, function(results, status) {
	      if (status == google.maps.GeocoderStatus.OK) {
	    	  thisMap.setCenter(results[0].geometry.location);
	    	  thisWidget.centerLat = results[0].geometry.location.lat();
	    	  thisWidget.centerLng = results[0].geometry.location.lng();
	      } else {
	    	  alert("Could not find this address. " + status);
	      }
	    });
	},
	"getCenterPosition" : function() {
		if (this.map) {
			var pos = this.map.getCenter();
			return [ pos.lat(), pos.lng() ];
		} else {
			return [ this.centerLat, this.centerLng ];	
		}			
		
	},
	"setCenterPosition" : function(pos) {
		this.centerLat = pos[0];
		this.centerLng = pos[1];		
		
		if (!this.map) {
			return;
		} else {			
			this.map.setCenter(new google.maps.LatLng(pos[0], pos[1]));
		}
	},	
	"addMarker" : function(m) {
		var thisWidget = this;
		var thisMap = this.map;
		
		if (!this.map) {
			if (!this.markersOnInit) {
				this.markersOnInit = new Array();
			}
			this.markersOnInit.push(m);
			return;
		}
		
		// If map coordinates have already been specified, no need to look up address
		if (m.latitude && m.latitude != 0 && m.longitude && m.longitude != 0) {	
			thisWidget._addMarker(m, new google.maps.LatLng(m.latitude, m.longitude));
		} else {		
			this.geocoder.geocode( { 'address': m.address}, function(results, status) {
			      if (status == google.maps.GeocoderStatus.OK) {		    	  
			    	  thisWidget._addMarker(m, results[0].geometry.location);
			      } else {
			    	  alert("Could not find this address. " + status);
			      }
			    });
		}
	},
	"_addMarker" : function(m, location) {
		var thisWidget = this;
		var thisMap = this.map;
		
        var marker = new google.maps.Marker({
            map: thisMap, 		            
            position: location,
            title: m.description,
            animation: google.maps.Animation.DROP
        });
        m.latitude = location.lat();
        m.longitude = location.lng();
        m.marker = marker;        
        if (!thisWidget.markers) {
        	thisWidget.markers = new Array();
        }
        thisWidget.markers.push(marker);

        var infowindow = new google.maps.InfoWindow({
            content: m.widget != null ? m.widget.eze$$DOMElement : m.description 
        });
        
        google.maps.event.addListener(marker, 'click', function() {
            infowindow.open(thisMap,marker);
        });

        // Center the map on the new marker
    	thisMap.setCenter(location);
	},	
	"removeMarker" : function(m) {
		if (m.marker != null) {
			m.marker.setMap(null);
		}
	},
	"removeAllMarkers" : function() {
		if (this.markers) {
			for (var i = 0; i < this.markers.length; i++) {
				if (this.markers[i] != null) {
					this.markers[i].setMap(null);	
				}				
			}
		}
	},
	"showRoute" : function(start, end) {
		var thisWidget = this;
		
		  var request = {
		    origin:start, 
		    destination:end,
		    travelMode: google.maps.DirectionsTravelMode.DRIVING
		  };
		  
		  thisWidget.directionsService.route(request, function(result, status) {
		    if (status == google.maps.DirectionsStatus.OK) {
		    	thisWidget.directionsDisplay.setDirections(result);
		    }
		  });
	},
	"getChildren" : function() {
		return null;
	},
	"getZoom" : function() {
		return this.zoom;
	},
	"setZoom" : function(zoom) {
		this.zoom = zoom;
		
		if (this.map) {			
			this.map.setZoom(this.zoom);
		}
	},
	"getMapType" : function() {
		return this.mapType;
	},
	"setMapType" : function(mapType) {
		this.mapType = mapType;
		
		if (this.map) {
			this.map.setMapTypeId(mapType);
		}
	},
	"showCurrentLocation" : function(description) {
		// This function is experimental and subject to change
		var thisWidget = this;
		var thisMap = this.map;
		
		var unsupported = false;
		
		// Try W3C Geolocation (Chrome, Firefox, Android, etc)
		if(navigator.geolocation) {
			try {
				navigator.geolocation.getCurrentPosition(function(position) {
					var location = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);		      
					thisWidget.showMarkerAtPosition(location, description);		     
				}, function() {
					alert("Location not found.");
		        });
			} catch (ex) {
				unsupported = true;
			}			
		//Google Gears
		} else if (google.gears) {
			var geo = google.gears.factory.create('beta.geolocation');
			geo.getCurrentPosition(function(position) {		      
				var location = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);			      
				thisWidget.showMarkerAtPosition(location, description);		    			    	
			}, function() {
				alert("Location not found.");
		});
		//Browser doesn't support Geolocation
		} else {		  
			unsupported = true;
		}	
		
		if (unsupported) {
			alert("This browser does not support geolocation.");
		}
	},
	"showAddress" : function (address, description) {
		var marker = { address: address, description: description, openInfoWindowOnCreate: true };	
		this.addMarker(marker);		
	},
	"refresh" : function() {
		if (this.map) {
			google.maps.event.trigger(this.map, 'resize');
		}
	}
});

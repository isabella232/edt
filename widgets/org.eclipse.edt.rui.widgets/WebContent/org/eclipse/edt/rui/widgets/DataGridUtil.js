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
egl.eze$$dataGridID = 1;

egl.defineClass("org.eclipse.edt.rui.widgets", "DataGridUtil", {
	
	"constructor" : function() {
		this.start = new Date().getTime();
		this.selectionKey = "eze$$selected_" + this.start;
		this.checkedKey =  "eze$$checked_" + this.start;
		this.HTMLBuffer = new Array();
		this.htmlStringCount = 0;
	},
	
	"getUniqueID" : function() {
		return "eze$$grid" + egl.eze$$dataGridID++;
	},
	  	
  	"getTextWidth" : function(s) {
  		var span = egl.createChild(document.body, "span");
  		span.innerHTML = s;
  		var w = egl.getWidthInPixels(span);
  		document.body.removeChild(span);
  		return w;
  	},
  	
  	"getOriginalTarget" : function(e) { 
  		var element = e.srcElement || e.target;
  		return element.eze$$widget || egl.createWidget(element);
  	},
  	
  	"getIntAttribute" : function(widget, name) {
  		return widget.eze$$DOMElement.getAttribute(name) || 0;
  	},
  	
  	"setSelected" : function(object, value) {
  		if (object) object[this.selectionKey] = value; 
  	},
  	
  	"getSelected" : function(object) {
  		return (object ? object[this.selectionKey] == true : false);
  	},
  	
  	"setChecked" : function(object, value) {
  		if (object) object[this.checkedKey] = value;
  	},
  	
  	"getChecked" : function(object) {
  		return (object ? object[this.checkedKey] == true : false);
  	},
  
  	"sort" : function(array,fieldName, up, ic, comparator) {
		try {
			var row=egl.unboxAny(array[0]);
			if(!row) return (array);
			if(array[0][fieldName]==null){
				var isProp=false;
				for(var p in row){
					if(p.toLowerCase()==fieldName.toLowerCase()){
						fieldName=p;
						isProp=true;
						break;
					}							
				}
				if(!isProp) return (array);
			}
			
			array.sort(function(a, b) {
				var result;
				a = egl.unboxAny(a);
				b = egl.unboxAny(b);
				var data1 = a[fieldName].eze$$value || a[fieldName];
				var data2 = b[fieldName].eze$$value || b[fieldName];
				if( ic ){
					if(data1.toLowerCase)
						data1 = data1.toLowerCase();
					if(data2.toLowerCase)
						data2 = data2.toLowerCase();
				}
				if(comparator) result=comparator(egl.boxAny(data1),egl.boxAny(data2));
				else result = data1.compareTo(data2);				
				return up ? result : -result;
			});
			return(array);
		}
		catch (e) {
			egl.println(e.message);
			throw egl.createRuntimeException( "CRRUI2102E", [ ] );
		}
  	},
  	
  	"getFieldNames" : function(object) {
  		result = [];
  		var filedValues = egl.unboxAny(object);
		for(filed in filedValues){
			if(egl.isUserField(filedValues, filed))
				result.push(filed);
		}
		return result;
  	},
  	"setBiDiMarkersStr" : function(text, isTextTypeVisual, isTextOrientationRightToLeft) {
		var endMarker = "", startMarker = "";
		if(text.charAt(0) >= egl.LRE && text.charAt(0) <= egl.RLO) 
			text = text.substring(1);
		if(isTextTypeVisual) 
			startMarker = (isTextOrientationRightToLeft) ? egl.RLO : egl.LRO;
		text = startMarker + text + endMarker;
		return (text);
  	},
  	"destroyHeaderCell" : function(grid){
  		if(grid && grid.eze$$DOMElement && grid.eze$$DOMElement.children && grid.eze$$DOMElement.children.length > 0){
  			var header = grid.eze$$DOMElement.children[0];
  			var tds = header.getElementsByTagName("span");
  			var count = tds.length;
  			for(var i=0; i<count; i++){
				row = tds[i].getAttribute("row") || 0;
				column = tds[i].getAttribute("column") || 0;
				if(row < 1 && column >= 1){
					egl.doDestroyDomElement(tds[i]);
				}
  			}
  		}  		
  	}
});

egl.org.eclipse.edt.rui.widgets.DataGridUtil.dataGridCheckboxClick = function(id, checkbox) {
	try {
		var grid = document.getElementById(id).eze$$widget.eze$$container;		
		while(grid.eze$$DOMElement.eze$$widget.RUIDataGridID != "RUIDataGridID"){
			grid = grid.eze$$container;			
		}
		grid = grid.eze$$DOMElement.eze$$widget;
		if(grid != null){
			grid.checkSelection(egl.createWidget(checkbox));
		}
	}
	catch (e) {
		egl.printError("Could not publish checkbox event", e);
	}
	return 0;
}

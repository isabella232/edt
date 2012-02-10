/*******************************************************************************
 * Copyright � 2011 IBM Corporation and others.
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
	'dojo.widgets', 'DojoGrid',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"constructor" : function() {
		this.headerBehaviors = [];
		this.behaviors = [];
		this.data = [];
		dojo.require("dojox.grid.DataGrid");
		dojo.require("dojo.data.ItemFileWriteStore");
	},
	"createDojoWidget" : function(parent) {
		this.setInnerHTML("");
		var eglWidget = this;
		this.dojoColumns = this.createColumns();
		this.dojoStore = new dojo.data.ItemFileWriteStore({ data: this.createData(this.data, this.dojoColumns) });
		// 
		this._mergeArgs({
			id: eglWidget.id, 
			structure: eglWidget.dojoColumns,
			store : eglWidget.dojoStore,
			autoHeight: true,
			autoWidth: true,
			splitter: false
		});
		this.dojoWidget = new dojox.grid.DataGrid(this._args, parent);
	    var runBehaviorsFunc = function() {
	    	setTimeout(function() { eglWidget.runBehaviors(); },1);
	    };
	    this.dojoWidget.domNode.setAttribute("align","left");
	    this.dojoWidget.connect(this.dojoWidget, "postrender", runBehaviorsFunc );
	    this.dojoWidget.render();
	    this.dojoWidget.startup();
	    this.dojoWidget.resize({w: this.width, h: this.height });
    },
    "setData" : function (data) {
    	if (!data)
    		data = [];
    	egl.dojo.widgets.DojoBase.prototype.setData.call(this, data);
    },
    "getData" : function(){
    	var index = this.dojoWidget.getSortIndex();
    	if(-1 != index && index < this.columns.length && index > -1){
    		var desc = !this.dojoWidget.getSortAsc() ;
    		var attr = this.columns[index].name;
    		var eglWidget = this;
    		this.dojoStore.fetch({onComplete:function(items, requests){
    			if(items && items.length>0 && eglWidget.data && eglWidget.data.length > 0){
    				for(var i=0; i<items.length; i++){
    					for(var col=0; col<eglWidget.columns.length; col++){
    						egl.unboxAny(eglWidget.data[i])[eglWidget.columns[col].name] = items[i][eglWidget.columns[col].name][0];
    					}
    				}
    			}
    		},
    		sort:[{attribute:attr, descending:desc}]});
    	}
    	
    	return this.data;
    },
    "runBehaviors" : function() {
    	this.addBehaviors(this.behaviors, "td", this.data.length);
    	this.addBehaviors(this.headerBehaviors, "th", 1);	
    	this.fixSizes();
    },
    "fixSizes": function() {
    	var tables = this.dojoWidget.domNode.getElementsByTagName("tbody");
	    var w = 0, h = 0, headerHeight;
	    for (var n=1; n<tables.length; n++) {
	    	if (tables[n].className != "EglRuiTbody") {
		    	h += egl.getHeightInPixels(tables[n]) + 1;
		    	w = Math.max(w, egl.getWidthInPixels(tables[n]) + 1);
	    	}
	    }
	    var headerHeight = egl.getHeightInPixels(tables[0]);
	    w = this.width ? parseInt(this.width) : w;
	    h = this.height ? parseInt(this.height) - headerHeight : h;
	    var divs = this.dojoWidget.domNode.getElementsByTagName("div");
	    if( !this.width || !this.height){
	    	for (var n=0; n<divs.length; n++) { 
		    	var div = divs[n];
	    		if (this.isGridDiv(div.className)) {
	    			if(!this.height){
	    				div.style.height = egl.toPX(h); 
	    			}
	    			if(!this.width) {
	    				div.style.width = egl.toPX(w);
	    			}		    		
		    	}
	    		if("dojoxGridHeader" == div.className){
	    			div.style.width = egl.toPX(w);
	    		}
		    }
	    }
	    egl.setWidth(this.eze$$DOMElement, egl.toPX(w)); 
	    egl.setHeight(this.eze$$DOMElement, egl.toPX(h + headerHeight)); 
    },
    "setHeight" : function(height){
    	this.height = height;
    	if(this.dojoWidget){
    		this.dojoWidget.resize({h: this.height });
    	}
    },
    "getHeight" : function(){
    	return this.height;
    },
    "setWidth" : function(width){
    	this.width = width;
    	if(this.dojoWidget){
    		this.dojoWidget.resize({h: this.width });
    	}
    },
    "getWidth" : function(){
    	return this.width;
    },
    "isGridDiv" : function(name) {
    	switch (name) {
	    	case "dojoxGrid-master-view" :
	    	case "dojoxGridMasterView" :
	    	case "dojoxGrid" : 
	    	case "dojoxGrid-view" :
	    	case "dojoxGridView" :
	    	case "dojoxGrid-content" :
	    	case "dojoxGridContent":
			case "dojoxGrid-scrollbox":
			case "dojoxGridScrollbox":
				return true;
    	}
		return false;
    },
    "showError": function(w, h) {
    	var s = "This is an empty DojoGrid. Nothing was rendered.";
    	if (this.data.length > 0) {
    		s += "<p>There are "+this.data.length+" records and "+this.columns.length+" columns."
	    	s += "<p>Columns:";
    		var missing = null;
    		var value = this.data.length>0 ? this.data[0] : {};
    		for (var n=0; n<this.dojoColumns.length; n++) {
    			s += " "+n+":\""+this.dojoColumns[n].field + "\""
    			if (!(this.dojoColumns[n].field in value))
    				missing = missing || this.dojoColumns[n].field;
    		}
    		s += "<p>Data: ["+this.data.length+"] {";
    		for (f in value)
				if (egl.isUserField(value, f))
					s += " \""+f+"\"";
    		s += "}";
    		s += "<p>Grid width = "+w + ". Grid height = "+h;
    		if (missing)
    			s += "<br>Check your column definitions. Missing data field: "+missing;
    		else
    			s += "<p><b>Internal Error</b>. This appears to be a race condition. " + 
    				"Please report this problem to the proper authorities.<br>" +
    				"Browser: " + window.navigator.userAgent;
    	}
	    this.setWidth(600);  
  		this.setHeight(400);
    	this.setInnerHTML(s);
    	egl.setEnableTextSelection(true);
    },
    "createData": function(data, columns) {
		var cells = {identifier: '', label: '', items: [{ }]};
		var EMPTY_ROW_NUM = 5;
		var rowNum = data.length > 0 ? data.length : 5;		
		for (var row=0; row < rowNum; row++) {
			cells.items[row] = {};
		    for (var column=0; column < columns.length; column++) {
		    	var value;
		    	if(data.length <= 0)
		    		value = "";
		    	else{
		    		value = egl.unboxAny(egl.unboxAny(data[row])[columns[column].field]);		    		
			    	if (value === undefined){
			    		value = "";
			    	}			    			
		    	} 				    	
		    	var field = columns[column].field;
	    		cells.items[row][field] = value;
		    }
		}
		return cells;
    },
    "createColumns" : function() {
		this.columns = this.columns || [];
		var eglWidget = this;
		var result = [];
	    if (!this.columns.length && this.data.length) {
	    	var data = egl.unboxAny(this.data[0]);
			for (f in data)
				if (egl.isUserField(data, f))
					result.push({name: f, field: f});
			this.columns = result;
	    }
	    else {
			for (var n=0; n < this.columns.length; n++) {	    	
				var column = this.columns[n];
				var name = column.displayName || column.name;
				name = name.replace(/ /g, "&nbsp;");
				result.push({
					field: column.name,
					name: name,
					width: column.width ? egl.toPX(column.width) : '100px'
				});
			}
	    }
		return result;
    },
    "addBehaviors" : function(behaviors, cellType, rowCount) {
    	var cells = [].appendAll(this.dojoWidget.domNode.getElementsByTagName(cellType));
    	for (var n=0; n<behaviors.length; n++) {
    		for (var row=0; row<rowCount; row++) {
	    		for (var column=0; column < this.columns.length; column++) {
	    			var cell = cells[row*this.columns.length + column];
    				try {
	    				behaviors[n](
	    					this, 
	    					egl.createWidget(cell), 
	    				    this.data[row], 
	    				    row+1, 
	    				    this.columns[column]
	    				);
	    			}
	    			catch (e) {
	    				var msg = "Error while running behavior for " +	(rowCount ? "row="+row : "header") + 
	    					", column=\""+this.columns[column].name+"\", cell="+ (row*this.columns.length + column)+
	    					" of " + cells.length;
	    				if (row) {
	    					msg += " row data is: <ul><table cellPadding=3px border=1>"
	    						for (var column=0; column < this.columns.length; column++) {
	    							msg += "<tr><td>" + this.columns[column].displayName + "</td>";
	    							msg += "<td>" + this.data[row][this.columns[column].name] + "</td></tr>";
	    						}
	    					msg += "</table></ul>Cancelling the rest of the behaviors for this grid.";
	    				}
	    				egl.printError(msg, e);
	    				return;
	    			}
	    		}
        	}
    	}
    },
	"addRow" : function( rowData ) {
		var newItem = (this.createData(rowData, this.dojoColumns)).items[0];
		this.dojoWidget.store.newItem(newItem);
	},
	"removeRow" : function( rowIndex ) {
		rowIndex--;
		if(rowIndex>=1 && rowIndex<this.dojoWidget.rowCount){
			this.dojoWidget.selection.deselectAll();
			this.dojoWidget.selection.setSelected(rowIndex,true);
			this.dojoWidget.removeSelectedRows();	
		}			
	},
	"destroy" : function() {
	}
});
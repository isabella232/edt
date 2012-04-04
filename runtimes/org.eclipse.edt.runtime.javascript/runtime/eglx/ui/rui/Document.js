/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass(
	'eglx.ui.rui', 'Document',
	{
	"setOnKeyDown" : function(handler) { document.onkeydown = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnKeyPress" : function(handler) { document.onkeypress = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnKeyUp" : function(handler) { document.onkeyup = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnMouseDown" : function(handler) { document.onmousedown = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnMouseMove" : function(handler) { document.onmousemove = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnMouseOver" : function(handler) { document.onmouseover = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnMouseOut" : function(handler) { document.onmouseout = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnMouseUp" : function(handler) { document.onmouseup = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnScroll" : function(handler) { document.onscroll = function (e) { handler(egl.wrapEvent(e)) } },
	"setOnChange" : function(handler) { document.onchange = function (e) { handler(egl.wrapEvent(e)) } },						
	"setOnClick" : function(handler) { document.onclick = function (e) { handler(egl.wrapEvent(e)) } },	

	"setOnSelectStart" : function(handler) { document.onselectstart = function (e) { handler(egl.wrapEvent(e)) } },						
	"setOnDragStart" : function(handler) { document.ondragstart = function (e) { handler(egl.wrapEvent(e)) } },						
	
	"getLocation" : function() {
		return ""+document.location;
	},
	"setLocation" : function(loc) {
		document.location = loc;
	},
	
	"eze$$getChildVariables": function() {
		return [
			{name: "body", value : egl.Document.body, type : "eglx.ui.rui.Widget"},
			{name: "location", value : this.getLocation(), type : "eglx.lang.EString", getter : "getLocation", setter : "setLocation"},
			{name: "onChange", value : document.onchange, type : "eglx.ui.rui.EventHandler", setter : "setOnChange"},
			{name: "onClick", value : document.onclick, type : "eglx.ui.rui.EventHandler", setter : "setOnClick"},
			{name: "onDragStart", value : document.ondragstart, type : "eglx.ui.rui.EventHandler", setter : "setOnDragStart"},
			{name: "onKeyDown", value : document.onkeydown, type : "eglx.ui.rui.EventHandler", setter : "setOnKeyDown"},
			{name: "onKeyPress", value : document.onkeypress, type : "eglx.ui.rui.EventHandler", setter : "setOnKeyPress"},
			{name: "onKeyUp", value : document.onkeyup, type : "eglx.ui.rui.EventHandler", setter : "setOnKeyUp"},
			{name: "onMouseDown", value : document.onmousedown, type : "eglx.ui.rui.EventHandler", setter : "setOnMouseDown"},
			{name: "onMouseMove", value : document.onmousemove, type : "eglx.ui.rui.EventHandler", setter : "setOnMouseMove"},
			{name: "onMouseOut", value : document.onmouseout, type : "eglx.ui.rui.EventHandler", setter : "setOnMouseOut"},
			{name: "onMouseOver", value : document.onmouseover, type : "eglx.ui.rui.EventHandler", setter : "setOnMouseOver"},
			{name: "onMouseUp", value : document.onmouseup, type : "eglx.ui.rui.EventHandler", setter : "setOnMouseUp"},
			{name: "onScroll", value : document.onscroll, type : "eglx.ui.rui.EventHandler", setter : "setOnScroll"},
			{name: "onSelectStart", value : document.onselectstart, type : "eglx.ui.rui.EventHandler", setter : "setOnSelecStart"}
		];
	}
});

egl.Document = egl.Document || new egl.eglx.ui.rui.Document();
egl.Document.body = egl.createWidget(document.body);

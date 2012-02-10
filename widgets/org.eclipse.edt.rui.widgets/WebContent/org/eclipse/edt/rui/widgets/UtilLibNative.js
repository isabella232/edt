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
egl.defineClass("org.eclipse.edt.rui.widgets", "UtilLibNative", {
	
	"getWidgetVariableName" : function(widget) {
		if (widget != null && widget.eze$$variableName != undefined) {
			return widget.eze$$variableName;
		} 
		return "";
	},
	"destroyWidget" : function(widget) {
		egl.destroyWidget(widget);
	},
	"destroyWidgetChildren" : function(widget) {
		egl.destroyWidgetChildren(widget);
	},
	"clearHTMLString" : function(){
		this.HTMLBuffer = [];
		this.htmlStringCount = 0;
	},
	"appendHTMLString" : function(str) {
		this.HTMLBuffer[this.htmlStringCount++] = str;
	},
	"getHTMLString" : function(){
		return this.HTMLBuffer.join("");
	},
	"getMessage" : function(msgId, inserts) {
		return egl.getRuntimeMessage(msgId, inserts);
	}
});

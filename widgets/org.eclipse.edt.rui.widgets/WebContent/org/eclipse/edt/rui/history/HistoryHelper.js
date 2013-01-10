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
egl.defineClass('org.eclipse.edt.rui.history', 'HistoryHelper',
{
	"setLocationHash" : function(hash) {
		document.location.hash = encodeURIComponent(hash);
	},
	"getLocationHash" : function()  {
		return decodeURIComponent(document.location.hash.replace(/.*#/,""));
	},
	"setLocationHref" : function(href) {
		document.location.href = href;
	},
	"getLocationHref" : function()  {
		return document.location.href;
	},
	"setOnbeforeUnload" : function(beforeUnload) {
		window.onbeforeunload = beforeUnload;
	},
	"getFrameSrc" : function(frame) {
		return String(frame.eze$$DOMElement.contentWindow.document.location);
	},
	"goBack" : function () {
		history.back();
	},
	"goForward" : function () {
		history.forward();
	},
	"setFrameSrc" : function(frame, uri) {
		frame.eze$$DOMElement.src = uri;
	},
	"isIE" : function() {
		return egl.IE;
	},
	"inVisualEditorDesignMode" : function() {
		return egl.enableEditing;
	}
});

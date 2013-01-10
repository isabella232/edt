/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
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
	'utils.theme', 'DojoTheme',
{	
	"constructor" : function() {
		this.curTheme = "claro";	
	},
	"setTheme" : function (t) {
		var theme;
		if(t.charAt(0).match(/\w/)) theme=t;
		else theme=t.substring(1);
		
		if(this.curTheme == theme){
			return;
		}
		try {
			dojo.removeClass(document.body, this.curTheme);
		}
		catch (e) {
		}
		this.changeCSS(this.curTheme, theme);
		this.curTheme = theme;		
		dojo.addClass(document.body, this.curTheme);
	},
	"getTheme" : function() {

	},
	"getURL" : function(theme) {
		var cssURL={};
		if(theme == "claro"){
			cssURL.urlBase = "dijit/themes/claro/claro.css";
			cssURL.urlGrid = "dojox/grid/resources/claroGrid.css";
		}else if(theme == "nihilo"){
			cssURL.urlBase = "dijit/themes/nihilo/nihilo.css";
			cssURL.urlGrid = "dojox/grid/resources/nihiloGrid.css";			
		}else if(theme == "tundra"){
			cssURL.urlBase = "dijit/themes/tundra/tundra.css";
			cssURL.urlGrid = "dojox/grid/resources/tundraGrid.css";			
		}else if(theme == "soria"){
			cssURL.urlBase = "dijit/themes/soria/soria.css";
			cssURL.urlGrid = "dojox/grid/resources/soriaGrid.css";
		}
		else return null;
		return cssURL;
	},
	"changeCSS" : function(oldTheme, newTheme){
		var cssURL1 = this.getURL(oldTheme);
		var cssURL2 = this.getURL(newTheme)
		if(cssURL1 != null){
			document.getElementById("EGLDOJOTheme_CSS").href = egl.urlPrefix+cssURL2.urlBase;
			document.getElementById("EGLDOJOGridTheme_CSS").href = egl.urlPrefix+cssURL2.urlGrid;
		}else{
			var e = document.createElement("link");
			e.href = egl.urlPrefix+cssURL2.urlBase;
			e.type = "text/css";
			e.rel = "stylesheet";
			e.media = "screen";
			e.id = "EGLDOJOTheme_CSS"
			document.getElementsByTagName("head")[0].appendChild(e); 
			
			e = document.createElement("link");
			e.href = egl.urlPrefix+cssURL2.urlGrid;
			e.type = "text/css";
			e.rel = "stylesheet";
			e.media = "screen";
			e.id = "EGLDOJOGridTheme_CSS"
			document.getElementsByTagName("head")[0].appendChild(e); 
		}				
	}
});

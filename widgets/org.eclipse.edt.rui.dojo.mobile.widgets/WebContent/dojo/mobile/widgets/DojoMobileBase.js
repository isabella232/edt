////////////////////////////////////////////////////////////////////////////////
// This sample is provided AS IS.
// Permission to use, copy and modify this software for any purpose and
// without fee is hereby granted. provided that the name of IBM not be used in
// advertising or publicity pertaining to distribution of the software without
// specific written permission.
//
// IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SAMPLE, INCLUDING ALL
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL IBM
// BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY
// DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER
// IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING
// OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SAMPLE.
////////////////////////////////////////////////////////////////////////////////

egl.defineWidget(
	'dojo.mobile.widgets', 'DojoMobileBase',
	'dojo.widgets', 'DojoBase',
	'div',
{
	"renderWhenDojoIsDoneLoading" : function() {
		var eglWidget = this;		
		require(["dojo", "dojox/mobile", "dojox/mobile/deviceTheme"], function(){
			eglWidget.renderingStep = 0;
			eglWidget.renderWhenDojoIsDoneLoadingSafely();
		});		
	},
	/**
	 * @summary default print msg in VE & Preview
	 * @param msg string, default print msg in VE & Preview
	 * @param notDrawVE Boolean, default false
	 * @param notDrawPreview Boolean, default false
	 */
	"log" : function( msg, notDrawVE, notDrawPreview ){
		notDrawVE = !!notDrawVE, notDrawPreview = !!notDrawPreview;
		
		// print into dom node
		function logToDom( msg ){
			var msgContainerBox = document.getElementById("egl-mobile-msg-box");
			var msgBox = document.createElement("DIV");
			if( !msgContainerBox ){
				msgContainerBox = document.createElement("DIV");
				msgContainerBox.id = "egl-mobile-msg-box";
				msgContainerBox.style["border"] = "1px solid #555555";
				msgContainerBox.style["background"] = "#E5F3FF";
				msgContainerBox.style["width"] = "90%";
				msgContainerBox.style["padding"] = "4%";
				msgContainerBox.style["margin"]  = "5px auto";
				
				var bodyEle = document.getElementsByTagName( "body" )[0];
				if( bodyEle.firstChild )
					bodyEle.insertBefore( msgContainerBox, bodyEle.firstChild );
				else
					bodyEle.appendChild( msgContainerBox );
			}
			msgContainerBox.innerHTML = msgContainerBox.innerHTML ?  (msgContainerBox.innerHTML + "<hr>" + msg) : msg;
		}
		
		if( (!notDrawVE && egl.enableEditing) || (!notDrawPreview && (!egl.enableEditing)) )
			logToDom( msg );
		else if( console && console.warn )
			console.warn( msg );
	},
	"printStartupMessage" : function(){
		if (egl.dojo.widgets.DojoDiagnostics && !egl.enableEditing && !egl.debugg && egl.contextAware && !egl.dojoNow) {
		    egl.dojoNow = new Date().getTime();
		    var duration = (egl.dojoNow-egl.startTime);
		    egl.println("<div style='border: 1px solid #555555; background-color: #E5F3FF; width:700px; padding: 9px;'><b>Dojo Statistics: </b>" +
		    		"Total startup (including loading of Dojo) took: <b>"+duration+
		    		"</b>ms.<hr>Dojo provider is: \"" + egl.dojoProvider + 
		    		"\".<br>Read <b>README_FIRST.html</b> in the <b>dojo.runtime</b> " +
		    		"project for more information.<br>" +
		    		"<hr>You are using: "+navigator.userAgent + (document.documentMode ? ", documentMode="+document.documentMode : "") +
		    		"<hr>This message is only printed in Preview mode, and not when you deploy or debug your application. See DojoDiagnostics.egl." +
		    		"</div>"
		    );
		    var http = "http:/";
			if (navigator.userAgent.indexOf("MSIE 6") != -1) 
			   egl.println("<font color=red><b>You are using IE6. For performance and security reasons, upgrade your browser from Internet Explorer 6 to a newer version.");
		}
		
		if ( !egl.dectectWebKit ) {
			if( !egl.WebKit )
				this.log(
					"<b>Warning: </b>You are using non-webkit browser. For performance and stability reasons, please use WebKit kernel browser to render this page.<br>"
					+ "<hr>This message is only printed in development mode, and not when you deploy application."
					,true
				);			
			egl.dectectWebKit = true;
		}
	},
	"appendChild" : function(child) {
		throw eze$$typename + ": appendChild is not implemented on this dojo widget";
	},
	"appendChildren" : function(children) {
		throw eze$$typename + ": appendChildren is not implemented on this dojo widget";		
	},
	"setChildren" : function(children) {	
		throw eze$$typename + ": setChildren is not implemented on this dojo widget";
	},
	"getChildren" : function() {

	},	
	"removeChild" : function(child) {
		throw eze$$typename + ": removeChild is not implemented on this dojo widget";		
	},
	"removeChildren" : function() {
		throw eze$$typename + ": removeChildren is not implemented on this dojo widget";
	}	
});

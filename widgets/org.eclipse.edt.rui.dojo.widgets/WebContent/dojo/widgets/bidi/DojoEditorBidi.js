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
if(!dojo._hasResource["bidi.DojoEditorBidi"]){
dojo._hasResource["bidi.DojoEditorBidi"]=true;

//in "bidi/DojoEditorBidi.js"
define(["dojo/_base/declare", "dijit/_editor/RichText", "dijit/Editor", "dijit/_WidgetBase"], function(declare, _WidgetBase){
	declare("bidi/DojoEditorBidi", [_WidgetBase], {
		postCreate:function(){
			this.inherited(arguments);
			this.isVisualMode = false;
			this.isTextReversed = false;
			this.dir = "ltr";
			this._savedPushBoundaries = null;
			this._selectionLength = 0;
			if(this.isTextReversed) {
				var self = this;
				require(["dojo/_base/xhr", "dojo/_base/lang"], function(xhr, lang){
					  var hitchNode = lang.hitch(self, "_reverseNodes");
					  self.contentDomPreFilters.push(hitchNode);
					  self.contentDomPostFilters.push(hitchNode);		
				});
			}
		},
		_adjustCursorOnPushTyping: function(offset) {
		 try{	
			if(dojo.isIE){
				var b=dojo.withGlobal(this.window,dijit.getBookmark);
				var selection = dijit.range.getSelection(this.window);
				if(b && b.mark && !dojo.isArray(b.mark) && selection){
					if(selection.rangeCount)
						range = selection.getRangeAt(0);
				
					if(range)
						b.mark = range.cloneRange();
					else
						b.mark = dojo.withGlobal(this.window,dijit.getBookmark);
		
					if(selection.removeAllRanges){
						selection.removeAllRanges();
						r = dijit.range.create(this.window);
						var container = dijit.range.getIndex(b.mark.startContainer,this.editNode).o;
						node = dijit.range.getNode(container,this.editNode);
						if(offset == null)
							offset = b.mark.startOffset-1;
						
						if(node){
							r.setStart(node,offset);
							r.setEnd(node,offset);
							selection.addRange(r);
						}
					}			
				}
			} else if (dojo.global.getSelection){
				var selection = dojo.global.getSelection();
				if(selection.removeAllRanges){
					var selectionRange = selection.getRangeAt(0);
					if(offset == null)
						offset = selectionRange.startOffset - 1;
					
					selectionRange.setStart(selectionRange.startContainer, offset);
					selectionRange.setEnd(selectionRange.endContainer, offset);	
					selection.removeAllRanges();
					selection.addRange(selectionRange);
				}
			}
		 }catch(e){}	
		},
		_getCursorPos: function() {
		 var cursorPos = null;
		 try{	
			if(dojo.isIE){
					var b=dojo.withGlobal(this.window,dijit.getBookmark);
					var selection = dijit.range.getSelection(this.window);
					if(b && b.mark && !dojo.isArray(b.mark) && selection){
						if(selection.rangeCount)
							range = selection.getRangeAt(0);
					
						if(range)
							b.mark = range.cloneRange();
						else
							b.mark = dojo.withGlobal(this.window,dijit.getBookmark);					
		
						if(b.mark){
							var container = dijit.range.getIndex(b.mark.startContainer,this.editNode).o;
							node = dijit.range.getNode(container,this.editNode);
							if(node)
								cursorPos = {
									container : node,
									leftBound : b.mark.startOffset,
									rightBound : b.mark.endOffset
								};
						}				
					}
			} else if (dojo.global.getSelection){
				var selection = dojo.global.getSelection();
				if(selection){
					var selectionRange = selection.getRangeAt(0);
					if(selectionRange)
						cursorPos = {
							container : selectionRange.startContainer,
							leftBound : selectionRange.startOffset,
							rightBound : selectionRange.endOffset
						};
				}
			}
		 }catch(e){}
		 return cursorPos;
		},
		onKeyPress: function(e){
			if(this.isVisualMode){
				try{		
					var eKey = (dojo.isIE) ? e.keyCode : e.charCode;
					this._isKeyboardLayerRtl = null;
					this._isKeyUpDone = false;	
					//
					if(((eKey > 64) && (eKey < 91)) || ((eKey > 96) && (eKey < 123)))
						this._isKeyboardLayerRtl = false;
					else if((eKey > 1487) && !((eKey > 1631) && (eKey < 1642)))
						this._isKeyboardLayerRtl = true;
					else if((eKey != 32) && this._isKeyboardLayerRtl && 
							(((eKey > 47) && (eKey < 58)) || ((eKey > 1631) && (eKey < 1642)))) 
					{		
						this._isKeyboardLayerRtl = false;					
					}
		
					var curPos = dojo.withGlobal(this.window,"_getCursorPos", this, []);			
					if (this._isKeyboardLayerRtl != null) {			
						if ((this._isKeyboardLayerRtl != (this.dir.toLowerCase() == "rtl")) && (this._savedPushBoundaries == null)) {			
							this._savedPushBoundaries = curPos;					
						}
						
						if (this._isKeyboardLayerRtl == (this.dir.toLowerCase() == "rtl")) {
							if ((this._savedPushBoundaries != null) && (curPos.leftBound == this._savedPushBoundaries.leftBound)) {			
								var positionToJump = (this._savedPushBoundaries.rightBound) ? this._savedPushBoundaries.rightBound : null;					
								dojo.withGlobal(this.window,"_adjustCursorOnPushTyping", this, [positionToJump]);
							}
							this._savedPushBoundaries = null;					
						}					
					}
			
					if(this._isKeyboardLayerRtl != null) {
						if(curPos && (curPos.rightBound != undefined))
							this._selectionLength = Math.abs(curPos.rightBound - curPos.leftBound);
						else
							this._selectionLength = 0;				
					}
					
					if(this._isOverWriteMode() && (this._isKeyboardLayerRtl != null) &&
						((this.dir.toLowerCase() == "rtl") != this._isKeyboardLayerRtl))
						{
							dojo.withGlobal(this.window,"_adjustCursorOnPushTyping", this, [null]);
						}
				 }catch(e){}	
			}
			this.inherited(arguments);	
		},
		onKeyUp: function(e){
			this.inherited(arguments);
			if(!this.isVisualMode)
				return;
		
		  try{	
			if(!this._isKeyUpDone && (this._isKeyboardLayerRtl != null) && !e.ctrlKey &&
				((this.dir.toLowerCase() == "rtl") != this._isKeyboardLayerRtl))
			{		
				dojo.withGlobal(this.window,"_adjustCursorOnPushTyping", this, [null]);
				
				if (this._savedPushBoundaries != null) {
					if (this._isOverWriteMode()) {
						var curPos = dojo.withGlobal(this.window,"_getCursorPos", this, []);
						if(this._savedPushBoundaries.leftBound > 0 && (this._savedPushBoundaries.leftBound > curPos.leftBound))
							this._savedPushBoundaries.leftBound -= 1;				
					}
					else
						this._savedPushBoundaries.rightBound += (this._selectionLength != 0)?(1 - this._selectionLength):1;
				}		
			}
			this._isKeyUpDone = true;
		
			var key = e.keyCode, ks = dojo.keys;	
			switch(key) {
				case ks.LEFT_ARROW:
				case ks.RIGHT_ARROW:
					if (this._savedPushBoundaries != null) {
						var cursorPos = dojo.withGlobal(this.window,"_getCursorPos", this, []);
						if((cursorPos == null) || (cursorPos.container != this._savedPushBoundaries.container)
							|| (cursorPos.rightBound > this._savedPushBoundaries.rightBound) 
							|| (cursorPos.leftBound < this._savedPushBoundaries.leftBound)) {
							
							this._savedPushBoundaries = null;
						}
					}
					break;
				case ks.UP_ARROW:
				case ks.DOWN_ARROW:
					if (this._savedPushBoundaries != null) {
						var cursorPos = dojo.withGlobal(this.window,"_getCursorPos", this, []);
						if((cursorPos == null) || (cursorPos.container != this._savedPushBoundaries.container))
							this._savedPushBoundaries = null;
					}
					break;
				case ks.HOME:
				case ks.END:
				case ks.PAGE_UP:
				case ks.PAGE_DOWN:
				case ks.ENTER:
					this._savedPushBoundaries = null; 
					break;
			}
		  }catch(e){}	
		},
		onKeyDown:function(e){	
			if(dojo.isIE && (65 <= e.keyCode && e.keyCode <= 90))
				return;
		
			var key = e.keyCode, ks = dojo.keys;	
			if(this.isVisualMode && (key == ks.BACKSPACE || key == ks.DELETE) && (this._savedPushBoundaries != null)) {
				if (this._savedPushBoundaries.leftBound == this._savedPushBoundaries.rightBound) {
					this._savedPushBoundaries = null;			
				}
				if (this._savedPushBoundaries != null) {
					var cursorPos = dojo.withGlobal(this.window,"_getCursorPos", this, []);				
					if((cursorPos != null) && (cursorPos.rightBound != undefined))
						this._selectionLength = Math.abs(cursorPos.rightBound - cursorPos.leftBound);
					else
						this._selectionLength = 0;
					
					if ((this._savedPushBoundaries.leftBound > 0) && (this._savedPushBoundaries.rightBound > 0)) {						
						if((key == ks.BACKSPACE) && (this._selectionLength == 0) 
							&& (this._savedPushBoundaries.leftBound == cursorPos.leftBound)) {
							
							this._savedPushBoundaries.leftBound -= 1;
						}
			
						this._savedPushBoundaries.rightBound -= (this._selectionLength != 0) ? (this._selectionLength) : 1;			
					}
					else if (this._selectionLength > 0) {			
						var pushSegmentLength = Math.abs(this._savedPushBoundaries.rightBound - this._savedPushBoundaries.leftBound);				
						if(this._selectionLength >= pushSegmentLength)
							this._savedPushBoundaries = null;		
					}
				}
			}
			
			this.inherited(arguments);
				
			if((key == dojo.keys.SHIFT) && e.ctrlKey){
				this.dir = ((this.dir.toLowerCase()=="rtl")?"ltr":"rtl");
				if(this.focusNode.style.direction != this.dir) {			
					this.focusNode.style.direction = this.dir;
					this._savedPushBoundaries = null;			
				}
			}
		},
		_onMouseUp: function(e){
			if((this._savedPushBoundaries != null) && (this._savedPushBoundaries.leftBound > 0) 
				&& (this._savedPushBoundaries.rightBound > 0)) 
			{
				var cursorPos = dojo.withGlobal(this.window,"_getCursorPos", this, []);		
				if((cursorPos == null) || (cursorPos.container != this._savedPushBoundaries.container)
					|| (cursorPos.rightBound > this._savedPushBoundaries.rightBound) 
					|| (cursorPos.leftBound < this._savedPushBoundaries.leftBound)) {
					
					this._savedPushBoundaries = null;
				}
			}	
		
			this.inherited(arguments);
		},
		_onPaste: function(e){	
			this._savedPushBoundaries = null;
		},
		_onCut: function(e){
			if (this._savedPushBoundaries != null) {
				var cursorPos = dojo.withGlobal(this.window,"_getCursorPos", this, []);				
				if((cursorPos != null) && (cursorPos.rightBound != undefined))
					this._selectionLength = Math.abs(cursorPos.rightBound - cursorPos.leftBound);
				else
					this._selectionLength = 0;
		
				if (this._selectionLength > 0) {		
					if ((this._savedPushBoundaries.leftBound < cursorPos.leftBound) &&
						(this._savedPushBoundaries.rightBound > cursorPos.rightBound)) 
					{				
						this._savedPushBoundaries.rightBound -= (this._selectionLength);							
					}
					else {
						this._savedPushBoundaries = null;		
					}
				}
			}
		},
		_onBlur: function(e){
			this.inherited(arguments);
			if(this.isVisualMode)
				this._savedPushBoundaries = null;
		},
		onLoad: function(html){
			this.inherited(arguments);
			if(this.isVisualMode) {
				this.editNode.style.unicodeBidi="bidi-override";
				this.connect(this.editNode, "onmouseup", this._onMouseUp);
				this.connect(this.editNode, "oncut", this._onCut);
				this.connect(this.editNode, "onpaste", this._onPaste);
		
				if(this.document.createStyleSheet) {
					var oStyleSheet = this.document.styleSheets[this.document.styleSheets.length-1];		
					oStyleSheet.addRule("p","{unicode-bidi: bidi-override}");
					oStyleSheet.addRule("li","{unicode-bidi: bidi-override}");
					oStyleSheet.addRule("div","{unicode-bidi: bidi-override}");
					oStyleSheet.addRule("blockquote","{unicode-bidi: bidi-override}");		
				} else if(dojo.isMoz) {	
					this.document.styleSheets[0].insertRule("div,ul>li,ol>li,blockquote {unicode-bidi: bidi-override}",0);
				}
			}
		},
		_doSymSwap: function(symbol){
		    switch(symbol)
		    {
		        case "(":
		        	symbol = ")";break;
		        case ")":
		            symbol = "(";break;
		        case "{":
		            symbol = "}";break;
		        case "}":
		            symbol = "{";break;
		        case "[":
		            symbol = "]";break;
		        case "]":
		            symbol = "[";break;                
		        case "<": 
		            symbol = ">";break;
		        case ">":
		            symbol = "<";break;                             
		    }
		    return symbol;
		},
		
		_reverseText: function(text){
			var len = text.length;
			if (len < 2)
				return text;
		
			var ret = "";
			for ( var i = 0; i < len; i++) {
				ret += this._doSymSwap(text.charAt(len - i - 1));
			}
			return ret;           
		},
		_reverseNodes: function(first){
			try{
				var len = first.childNodes.length;		
				if (len > 0) {
					for (var i = 0; i < len; i++) { 
						this._reverseNodes(first.childNodes[i]);
					}
				} else {
					if (first.nodeType == 1) {
						tagName = first.tagName.toLowerCase();
						if ((tagName != "style") && (tagName != "script") && (tagName != "br")) {
							first.innerHTML = this._reverseText(first.innerHTML);
						}
					} else if (first.nodeType == 3) {
						first.nodeValue = this._reverseText(first.nodeValue);
					}
				}
			} catch (e) {}
			
			return first;
		},
		_isOverWriteMode: function() {
			return ((dojo.isIE) ? document.queryCommandValue("OverWrite") : false);
		}
	});
	});
}
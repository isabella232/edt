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
egl.defineClass( "org.eclipse.edt.rui.widgets", "VisualHandlers", 
{
	"ReverseText" : function(widget) {
		reverseText(widget.eze$$DOMElement);
	}, 	
	"BidiReorder" : function(text, inFormat, outFormat){
			return doBidiReorder(text, inFormat, outFormat);
    },
	"AttachHandlers" : function(widget) {

		if(widget.textLayoutThis == "Visual") {
			if(!widget.eze$$DOMElement.maxLength || widget.eze$$DOMElement.maxLength < 0 || widget.eze$$DOMElement.maxLength > 10000000)
				widget.eze$$DOMElement.maxLength = 10000000;

			widget.eze$$DOMElement.maxLength++;
			
			if(widget.eze$$DOMElement.tagName == "TEXTAREA") {
				widget.eze$$DOMElement.style.overflow = "hidden"; //Defect 75587
				if(egl.IE)
					widget.eze$$DOMElement.style.wordWrap = "normal";
				else
					widget.eze$$DOMElement.setAttribute("wrap", "off");
			}
			
			egl.addEventListener(widget.eze$$DOMElement, "copy",handleCopy,widget.eze$$DOMElement);	
			egl.addEventListener(widget.eze$$DOMElement, "cut",handleCut,widget.eze$$DOMElement);
			egl.addEventListener(widget.eze$$DOMElement, "paste",handlePaste,widget.eze$$DOMElement);			
			egl.addEventListener(widget.eze$$DOMElement, "dblclick",handleDblClick,widget.eze$$DOMElement);			
			egl.addEventListener(widget.eze$$DOMElement, "mousedown",handleMouseDown,widget.eze$$DOMElement);
			egl.addEventListener(widget.eze$$DOMElement, "mouseup",handleMouseUp,widget.eze$$DOMElement);		
			egl.addEventListener(widget.eze$$DOMElement, "blur",handleBlur,widget.eze$$DOMElement);
			egl.addEventListener(widget.eze$$DOMElement, "focus",handleFocus,widget.eze$$DOMElement);
			egl.addEventListener(widget.eze$$DOMElement, "mouseout",handleMouseOut,widget.eze$$DOMElement);		
			egl.addEventListener(widget.eze$$DOMElement, "keydown",handleKeyDown,widget.eze$$DOMElement);
			egl.addEventListener(widget.eze$$DOMElement, "keyup",handleKeyUp,widget.eze$$DOMElement);					
			egl.addEventListener(widget.eze$$DOMElement, "keypress",handleKeyPress,widget.eze$$DOMElement);
		}
		else {
			egl.removeEventListener(widget.eze$$DOMElement, "copy",handleCopy);	
			egl.removeEventListener(widget.eze$$DOMElement, "cut",handleCut);
			egl.removeEventListener(widget.eze$$DOMElement, "paste",handlePaste);			
			egl.removeEventListener(widget.eze$$DOMElement, "dblclick",handleDblClick);			
			egl.removeEventListener(widget.eze$$DOMElement, "mousedown",handleMouseDown);
			egl.removeEventListener(widget.eze$$DOMElement, "mouseup",handleMouseUp);		
			egl.removeEventListener(widget.eze$$DOMElement, "blur",handleBlur);
			egl.removeEventListener(widget.eze$$DOMElement, "focus",handleFocus);
			egl.removeEventListener(widget.eze$$DOMElement, "mouseout",handleMouseOut);		
			egl.removeEventListener(widget.eze$$DOMElement, "keydown",handleKeyDown);
			egl.removeEventListener(widget.eze$$DOMElement, "keyup",handleKeyUp);					
			egl.removeEventListener(widget.eze$$DOMElement, "keypress",handleKeyPress);			
		}
	}
});

//function BiDiHandlers() {
	var CODE_BACKSPACE = 8;
	var CODE_TAB       = 9;
	var CODE_ENTER     = 13;
	var CODE_PAGEUP    = 33;
	var CODE_PAGEDOWN  = 34;
	var CODE_END       = 35;
	var CODE_HOME      = 36;
	var CODE_ARROWLEFT = 37;
	var CODE_ARROWRIGHT= 39;
	var CODE_ARROWUP   = 38;
	var CODE_ARROWDOWN = 40;
	var CODE_INSERT    = 45;
	var CODE_DELETE    = 46;
	var CODE_A		   = 65;
	var CODE_Z		   = 90;
	var CODE_CTRL		= 17;	
	var adjustCaretOnKeyUp = false;
	var isFldreversed = false;
	var curPos = 1;
	var selectionStart;
	var autoPush = false;
	var pushMode = false;
	var rightPushBound = 0;
	var leftPushBound = 0;
	var layerGuess = false;
	var swapChar = 0;
	var hadnoFocus = true;
	var preventAltNumpad = false;
	var isOverwriteMozilla = false;
	var isArabic = false;
	var isLinux = false;
	var isIE = false;
	var fieldStart = 0;
	var CR;
	var hint;
	var LF = '\n';
	var Reorderer = null;

	isLinux = (navigator.platform.indexOf("Linux") != -1) ? true : false;
	if(isLinux) {
	    if(navigator.appName.indexOf('Netscape') == -1)
	        alert("Use Firefox/Mozilla browser");
	    else {
	        index = navigator.userAgent.indexOf("Netscape/");
	        if((index > 0) && (parseInt(navigator.userAgent.substring(index+9,index+10)) < 7))
	            alert("To have full Bidi support use Netscape version 8 or above");
	    }
	}

	CR = (egl.IE) ? "\r\n" : "\n";

	isArabic = false;
	var hint;
	if(egl.IE) {
	    var hint = document.createElement("<div dir='ltr' id='hint' style=\"BACKGROUND-COLOR: rgb(255,255,128); COLOR: black; BORDER-BOTTOM-STYLE: groove; BORDER-LEFT-STYLE: groove; BORDER-RIGHT-STYLE: groove; BORDER-TOP-STYLE: groove; FONT-FAMILY: serif; FONT-SIZE: xx-small; HEIGHT: 100px; LEFT: 1px; POSITION: absolute; TOP: 1px; VISIBILITY: hidden; WIDTH: 320px; Z-INDEX: 1\"></div>");
	    hint.innerText = "Field reverse-'Alt+Numpad Numlock'\nAuto push-'Alt+Numpad /'\nPush on-'Shift+Numpad Numlock'\nPush off-'Shift+Numpad /'";
	}
	else {
	    hint = document.createElement("div");
	    hint.setAttribute("dir","ltr");
	    hint.setAttribute("id","hint");
	    hint.setAttribute("style","BACKGROUND-COLOR: rgb(255,255,128); COLOR: black; BORDER-BOTTOM-STYLE: groove; BORDER-LEFT-STYLE: groove; BORDER-RIGHT-STYLE: groove; BORDER-TOP-STYLE: groove; FONT-FAMILY: serif; FONT-SIZE: small; HEIGHT: 100px; LEFT: 1px; POSITION: absolute; TOP: 1px; VISIBILITY: hidden; WIDTH: 320px; Z-INDEX: 1");
	    if(isLinux)
	        hint.innerHTML =  "Field reverse-'Shift+Numpad 4'<br\>Auto push-'Shift+Numpad 6'<br\>Push on-'Shift+Numpad 7'<br\>Push off-'Shift+Numpad 8'";
	    else            
	        hint.innerHTML =  "Field reverse-'Alt+Numpad Numlock'<br\>Auto push-'Alt+Numpad /'<br\>Push on-'Shift+Numpad Numlock'<br\>Push off-'Shift+Numpad /'";        
	}

	var body = document.getElementsByTagName("BODY");
	body[0].appendChild(hint);

	function isOverWriteMode() {
	    if (egl.IE)
	        return document.queryCommandValue("OverWrite");
	    else
	        return isOverwriteMozilla;
	}

function doBidiReorder(text, inFormat, outFormat) {
	if (inFormat == outFormat)
        return text;
    var inTL =  inFormat.substring(0,1);
    var inTD =  inFormat.substring(1,4);
    var outTL = outFormat.substring(0,1);
    var outTD =  outFormat.substring(1,4);
    
    if ((inTL == "L") && (outTL == "V") && (outTD == "LTR")) { //core cases
        //cases: LLTR->VLTR, LRTL->VLTR 
        if (inTD == "LTR")
            return reorderTextLTR(null, text, false);
        if (inTD == "RTL")
            return reorderTextRTL(null, text, false);
     }
        
    if ((inTL == "V") && (outTL == "V")) {
        //inTD != outTD
        //cases: VRTL->VLTR, VLTR->VRTL
        return invertStr(text);
    }
    
    if ((inTL == "L") &&  
        (outTL == "V") && (outTD == "RTL")){
        //cases: LLTR->VRTL, LRTL->VRTL   
        if (inTD == LTR) 
            text = reorderTextLTR(null, text, false);
        else //inTD == RTL
            text = reorderTextRTL(null, text, false);
        return invertStr(text);
    }
    
    if ((inTL == "V") && (inTD == "LTR") &&
        (outTL == "L") && (outTD == "LTR"))
        //case: VLTR->LLTR
        return reorderTextLTR(null, text, false);
    
    if ((inTL == "V") && (outTL == "L") &&
        (inTD != outTD)) {
        //cases: VLTR->LRTL, VRTL->LLTR
        text = invertStr(text);
        if (inTD="RTL"){ //VRTL->LLTR
            return doBidiReorder(text, "LLTR","VLTR");
        } else { //VLTR->LRTL
            return doBidiReorder(text, "LRTL","VRTL");
        }
    }
    if ((inTL == "V") && (outTL == "L") &&
        (inTD == "RTL") && (outTD == "RTL")) {
        //case VRTL->LRTL
        return doBidiReorder(text, "LRTL","VRTL");
    }
    if ((inTL == "L") && (outTL == "L") &&
        (inTD != outTD)) {
        //cases: LRTL->LLTR, LLTR->LRTL
        if (inTD == "RTL") {
          //LRTL->LLTR
          text = reorderTextRTL(null, text, false);
          return doBidiReorder(text, "VLTR","LLTR");
        } else {//LLTR->LRTL
            text = reorderTextLTR(null, text, false);
            return doBidiReorder(text, "VLTR","LRTL");
        }
    }
}

function reorderTextLTR(obj,text,isCopyOperation){
    Reorderer = new JSReorder(0);
    return reorderText(obj,text,isCopyOperation);
}

function reorderTextRTL(obj,text,isCopyOperation){
    Reorderer = new JSReorder(1);
    return reorderText(obj,text,isCopyOperation);
}
	function reorderText(obj,text,isCopyOperation) {
		if(Reorderer == null){
			Reorderer = new JSReorder(0);
		}
	    var resultText = "";
		
	    if(isCopyOperation && ((obj != null) && (obj.style.direction == "rtl")) ) {
	    	
	        var len = text.length;
	        for(i = 0;i < len;i++) {
	            symbol = text.charAt(len - i - 1);
	            symbol = doSymSwap(symbol);
	            resultText += symbol;
	        }
	    }
	    else
	        resultText = text;

	    resultText = Reorderer.doReorder(resultText);

	    if(!isCopyOperation && ((obj != null) && (obj.style.direction == "rtl")) ) {
	        var len = text.length;
	        text = resultText;
	        resultText = "";
	        for(i = 0;i < len;i++) {
	            symbol = text.charAt(len - i - 1);
	            symbol = doSymSwap(symbol);
	            resultText += symbol;
	        }
	    }

	    return resultText;
	}

	function processCopy(obj) {
		var text;
		if(egl.IE) {
		    var range = document.selection.createRange();
		    text = range.text;
		}
		else
		    text = obj.value.substring(obj.selectionStart,obj.selectionEnd);

	    if((text.charAt(0) == egl.LRO) || (text.charAt(0) == egl.RLO))
	        text = text.substring(1);

	    var textToClipboard = reorderText(obj,text,true);

	    if(egl.IE) {
	        window.clipboardData.setData("Text", textToClipboard);
	        return true;
	    }
	}

	function ajustCaretPositions(event_,obj) {
	    if((fieldStart > 0) && (selectionStart == curPos)) { //for selection by context menu
	        preventEraseMarker(obj);
	        var selection = getCaretPos(event_,obj);
	        if (selection){
	        	selectionStart = Math.min(selection[0],selection[1]);
	        	curPos = Math.max(selection[0],selection[1]);
	        }
	    }
	}

	function handleSelect(e) {
	    var obj = this; 
	    if(egl.IE){
	        if(document.selection.createRange().text.length == obj.value.length) {
	            e = window.event;
	            handleDblClick(e);
	            preventDefault(e);
	        }
	    }
	    else {
	        if((obj.selectionEnd - obj.selectionStart) == obj.value.length) {
	            handleDblClick(e);
	            preventDefault(e);
	        }
	    }
	}

	function handleCopy(e) {
		if(egl.IE) {
		    e = window.event;
		    preventDefault(e);
		    ajustCaretPositions(e,this);
		    return processCopy(this);
		}
	}

	function handleCut(e) {
	    var obj = this;
	    ajustCaretPositions(e,obj);
	        
	    if(pushMode) {
	        if(egl.IE)
	            leftPushBound -= Math.abs(selectionStart - curPos);
	        else
	            leftPushBound -= Math.abs(obj.selectionStart - obj.selectionEnd);
	    }

	    var ret = processCopy(obj);
	    if(!ret) return false;

	    if(egl.IE) {
	        e = window.event;   
	        curPos = Math.min(selectionStart,curPos);
	        selectionStart = curPos;
	        preventDefault(e);
	        var range = document.selection.clear();
	    }
	    else {
	        curPos = obj.selectionStart;
	        obj.value = obj.value.substring(0,curPos) + obj.value.substring(obj.selectionEnd);
	        obj.setSelectionRange(curPos,curPos);
	    }

	    return true;
	}

	function handlePaste(e) {
		if(egl.IE) {		
		    e = window.event;
		    var obj = this;
		    
		    ajustCaretPositions(e,obj);
		        	
			preventDefault(e);
			var range = document.selection.createRange();
			var clipboardText = window.clipboardData.getData("Text");
			selectionStart = Math.min(selectionStart, curPos);	
			range.text = reorderText(obj,clipboardText,false);
			
			selectionStart = curPos = selectionStart + clipboardText.length;
			
		    if(fieldStart > 0)
		    	adjustCaretOnKeyUp = true;
	
		    if(pushMode)   
		    	toggleFieldOrient(obj,true,false);
		}
	}

	function handleDblClick(e) {
		var obj = this;
		if(!obj.type && egl.IE)
			obj = e.srcElement;

	    fieldStart = (obj.type.toLowerCase() == 'textarea') ? 0 : 1;
	    selectionStart = fieldStart;            
	    curPos = obj.value.length;
	    if(pushMode) { 
	    	toggleFieldOrient(obj,false,false);
	    	pushMode = false;
	    }
	    setSelectedRange(obj,selectionStart,curPos);

	    preventDefault(e);
	}

	function getCaretPos(event_,obj) {
	    if(!egl.IE) {
	        return new Array(event_.target.selectionStart, event_.target.selectionEnd);
	    }
	    else {
	        var range = document.selection.createRange().duplicate();
	        var range2 = range.duplicate();
	        var rangeLength = range.text.length;
	        if (obj.type == "textarea")
	            range2.moveToElementText(obj);
	        else
	            range2.expand('textedit');

	        var isLeftBoundaryPosition = range2.offsetLeft == range.offsetLeft;    
	        while (range.compareEndPoints('StartToStart', range2) > 0) {
	            range.moveStart('character', -1);
	        }

	        var position = range.text.length;
	        if (obj.type == "textarea" && (obj.value.charAt(position) == '\r')) {
	        	if(((event_.type != "mouseup" || rangeLength == 0) && isLeftBoundaryPosition) //mouse click or up/down
	        	 ||(event_.type == "mouseup" && !isLeftBoundaryPosition && (event_.offsetX < 5) && (event_.offsetX > 0))) //mouse selection
				position += 2;
	        }
	        return new Array(position - rangeLength, position);
	    }
	}

	function handleMouseDown(e) {
	    if(egl.IE) {
			e = window.event;	    
	        fieldStart = (this.type == "textarea") ? 0 : 1;


	        if(selectionStart != curPos)
	        	setSelectedRange(this,fieldStart,fieldStart);           
	    }
		
	    if(e.button == 2) {
	        var hint = document.getElementById("hint");
	        if(hint != null) {
	            var hintElement = (hint.length > 0) ? hint[0] : hint;
	            hintElement.style.left = ((e.clientX >= hint.scrollWidth) ? (e.clientX - hint.scrollWidth) : 0)+"px";
	            hintElement.style.top = e.clientY +"px";
	            hintElement.style.visibility = "visible";
	        }
	    }
	}

	function handleMouseUp(e) {
	    var hint = document.getElementById("hint");
	    if(hint != null) {
	        if(hint.length > 0)
	            hint[0].style.visibility = "hidden";
	        else
	            hint.style.visibility = "hidden";
	    }

	    if(egl.IE) e = window.event;      
	    obj = this;
	    var selection = getCaretPos(e,obj);
	    if (selection && (!egl.IE || e.button != 2)){
	     selectionStart = Math.min(selection[0],selection[1]);
	     curPos = Math.max(selection[0],selection[1]);
	     if(fieldStart > 0 && selectionStart == 0) {
	        if(egl.IE) {
	             var rng = document.selection.createRange();
	             rng.moveStart('character', fieldStart);
	             selectionStart = fieldStart;
	             if(curPos == 0)
	                 curPos = fieldStart;
	 
	             rng.select();
	         }
	         else {
	         	if(curPos != selectionStart)             
	         		obj.setSelectionRange(selectionStart + 1,curPos);
	         	else
	         		obj.setSelectionRange(selectionStart + 1,curPos + 1);      	            	
	         }
	     }
	    }

	    if(pushMode && ((curPos > leftPushBound)||(selectionStart < rightPushBound)))
	        toggleFieldOrient(obj,true,false);
	}

	function handleContextmenu(e,obj){
	if(egl.IE)
	 try{
	    obj = this;
	    e = window.event;
	    var selection = getCaretPos(e,obj);
	    if(selection) {
	        selectionStart = Math.min(selection[0],selection[1]);
	        curPos = Math.max(selection[0],selection[1]);			    
	    }
	 } catch(_e){}
	}

	function handleBlur() {
	    var obj = this;
	    if(!isLinux || !preventAltNumpad) {
	        if(pushMode) {
	            toggleFieldOrient(obj,false,false);
	            pushMode = false;
	        }

	        if (isFldreversed)
	            processFieldReverse(obj,false);
	    }
	}

	function handleFocus() {
	    var obj = this;
	    var text = obj.value;
	    isFldreversed = false;
	    fieldStart = (obj.type == "textarea") ? 0 : 1;
	    if(egl.IE) {
	        e = window.event;
	        var selection = getCaretPos(e,obj);
            if (selection){
                selectionStart = Math.min(selection[0],selection[1]);
                curPos = Math.max(selection[0],selection[1]);
                if(selectionStart == 0 && fieldStart > 0) {         
                    var rng = document.selection.createRange();
                    rng.moveStart('character', fieldStart);
                    rng.select();
                    selectionStart = curPos = fieldStart;
                }           
            }
	    }

	    if(text.length == 0)
	        obj.style.textAlign = (obj.style.direction == "ltr") ? "left" : "right";

	    if(isLinux && (curPos > 0))
	        obj.setSelectionRange(curPos,curPos);

	}

	function handleMouseOut() {
	    var hint = document.getElementById("hint");
	    if(hint != null) {
	        if(hint.length > 0)
	            hint[0].style.visibility = "hidden";
	        else
	            hint.style.visibility = "hidden";
	    }
	}

	function swapBrackets(event_,fieldDirection,swapped) {
	    if(isLinux) {
	        if(fieldDirection ^ (layerGuess && !isArabic) ) {
	            if(swapped == 40)
	                swapped =  41;
	            else if(swapped == 41)
	                swapped = 40;
	            else if(swapped == 60)
	                swapped = 62;
	            else if(swapped == 62)
	                swapped = 60;
	            else if(swapped == 91)
	                swapped = 93;
	            else if(swapped == 93)
	                swapped = 91;
	            else if(swapped == 123)
	                swapped = 125;
	            else if(swapped == 125)
	                swapped = 123;
	        }

	        return swapped;
	    }
	    else
            if(!fieldDirection && layerGuess) {
                if(swapChar == 219) {
                    if (!this.isArabic)
                        swapped = (event_.shiftKey) ? 123 : 91;
                }
                else if(swapChar == 221) {
                    if (!this.isArabic)
                        swapped = (event_.shiftKey) ? 125 : 93;
                }
                else if(event_.shiftKey && (swapChar == 48) )
                        swapped = 41;
                else if(event_.shiftKey && (swapChar == 57))
                        swapped = 40;
                else if(!this.isArabic && event_.shiftKey && (swapChar == 188))
                    swapped = 60;
                else if(!this.isArabic && event_.shiftKey && (swapChar == 190))
                    swapped = 62;
                else if(this.isArabic) {
                    if (swapChar == 68)
                        swapped = 91;
                    else if (swapChar == 70)
                        swapped = 93;
                    if (swapChar == 67)
                        swapped = 123;
                    else if (swapChar == 86)
                        swapped = 125;
                }
            }
            else if(fieldDirection && !layerGuess ){
                if(swapChar == 219) 
                        swapped = (event_.shiftKey) ? 125 : 93;
                else if(swapChar == 221)
                        swapped = (event_.shiftKey) ? 123 : 91;
                else if(event_.shiftKey && (swapChar == 48)) 
                        swapped = 40;
                else if(event_.shiftKey && (swapChar == 57)) 
                        swapped = 41;
                else if(event_.shiftKey && (swapChar == 188)) 
                    swapped = 62;
                else if(event_.shiftKey && (swapChar == 190)) 
                    swapped = 60;
            }
            else if(this.isArabic && fieldDirection && layerGuess){
                if(swapChar == 219) 
                        swapped = 62;
                else if(swapChar == 221)
                        swapped = 60;
            }

	    swapChar = 0;
	    return swapped;
	}

	function handleKeyPress(e) {
	    if(egl.IE) 
	    	e = window.event;

	    var obj = this;
	    if(preventAltNumpad || (e.returnValue == false)) {
	        preventDefault(e);
	        return;
	    }

	    var replacedKey = null;
	    var fieldDirection;
	    var charClass = fieldDirection = (obj.style.direction == "rtl");

	    if(egl.IE) {	    	
	        e.keyCode = swapBrackets(e,fieldDirection,e.keyCode);
	        ieKey = e.keyCode;

	        if(isArabic && layerGuess && (ieKey > 47) && (ieKey < 58))//Arabic, transform into Hindi
	            ieKey = e.keyCode = ieKey + 1584;
	    }
	    else {
	        ieKey = e.charCode;

	        if(e.keyCode == CODE_INSERT) {
	            isOverwriteMozilla = !isOverwriteMozilla;
	        }
	        else if((ieKey > 31) & !e.altKey & !e.ctrlKey) { //regular character
	            charCode = swapBrackets(e,fieldDirection,e.charCode);
	            if(charCode != ieKey) {
	                replacedKey = charCode;
	            }
	            else if(isArabic && layerGuess && (ieKey > 47) && (ieKey < 58)) {//Arabic, transform into Hindi
	                replacedKey = ieKey + 1584;
	            }
	        }
	    }

	    if((e.keyCode == CODE_ENTER) && (obj.rows == 1)){
	        preventDefault(e);
	        return;
	    } 

	    if(!egl.IE && ((e.keyCode == 8) || (e.keyCode == 46)) )
	        return;

	    if(((ieKey > 64) & (ieKey < 91)) || ((ieKey > 96) & (ieKey < 123)))
	        layerGuess = charClass = false;  //English
	    else if(ieKey > 1487) {
	        layerGuess = charClass = true;
	        isArabic = (ieKey > 1547) ? true : false;
	    }
	    else if(ieKey == 32)
	        charClass = layerGuess;
	    else if(fieldDirection && ((ieKey > 47) && (ieKey < 58)))  //like windows			
	        charClass = false; //English (Numerals)	

	    if(autoPush) {
	        if(fieldDirection != charClass) { //enter/leave push mode
	            curPos = (egl.IE) ? curPos : obj.selectionEnd;           
	            toggleFieldOrient(obj,true,pushMode && (curPos == leftPushBound));
	        }
	    }

	    if((ieKey > 31) && !e.altKey && !e.ctrlKey) { //regular character
	        preventEraseMarker(obj);    
	        var cursorPos = (egl.IE) ? curPos : obj.selectionStart;       
	        var selectionLength = (egl.IE) ? Math.abs(selectionStart - curPos) : obj.selectionEnd - obj.selectionStart;     
	        if(!isOverWriteMode() || (selectionLength > 0)) {
	            if(obj.value.length - selectionLength >= obj.maxLength) {
	                preventDefault(e);
	                return;
	            }
	        }
	        else {
	            if (cursorPos >= obj.maxLength) {
	                preventDefault(e);
	                return;
	            }
	        }

	        if(pushMode) {        
	            if(!isOverWriteMode() || (leftPushBound == cursorPos) || (selectionLength > 0))
	            	leftPushBound += 1 - selectionLength;
	        }

	        if(egl.IE) {
	            if(selectionLength > 0) {
	                preventDefault(e);
	                var range = document.selection.createRange();
	                range.text = String.fromCharCode(ieKey);
	                range.select();
	            }
	            selectionStart = curPos = Math.min(selectionStart,curPos) + 1;
	        }
	        else {
	            if(isLinux && fieldDirection && isArabic && (ieKey > 1487)) {
	                replacedKey = (replacedKey != null) ? replacedKey : ieKey;
	                e.preventDefault();
	                curPos = obj.selectionStart;
	                if(isOverWriteMode() && (obj.selectionStart == obj.selectionEnd))
	                    text = obj.value.substring(0,obj.selectionStart) + String.fromCharCode(replacedKey) + obj.value.substring(obj.selectionEnd+1);
	                else
	                    text = obj.value.substring(0,obj.selectionStart) + String.fromCharCode(replacedKey) + obj.value.substring(obj.selectionEnd);

	                obj.value = ara_type(obj.selectionStart,text,fieldDirection);
	                obj.setSelectionRange(curPos+1,curPos+1);
	            }
	            else if((replacedKey != null) || isOverWriteMode()) {
	                replacedKey = (replacedKey != null) ? replacedKey : ieKey;
	                e.preventDefault();
	                replaceFieldText(obj,replacedKey);
	            }
	        }
	    }
	}

	function replaceFieldText(obj,characterCode) {
	    curPos = obj.selectionStart;
	    if(isOverWriteMode() && (obj.selectionStart == obj.selectionEnd))
	        obj.value = obj.value.substring(0,obj.selectionStart) + String.fromCharCode(characterCode) + obj.value.substring(obj.selectionEnd+1);
	    else
	        obj.value = obj.value.substring(0,obj.selectionStart) + String.fromCharCode(characterCode) + obj.value.substring(obj.selectionEnd);

	    obj.setSelectionRange(curPos+1,curPos+1);
	}

	function handleKeyUp(e) {
	    if(egl.IE)
	    	e = window.event;

	    var obj = this;
	    var ieKey = e.keyCode;
	    var fieldDirection = (obj.style.direction == "ltr") ? false : true;

	    if(!egl.IE)
	        curPos = obj.selectionStart;

	    if (((ieKey == CODE_ARROWLEFT) && (fieldDirection == false))||((ieKey == CODE_ARROWRIGHT) && (fieldDirection == true))) {
	        if(pushMode && (curPos < rightPushBound))
	            toggleFieldOrient(obj,true,false);
	    }
	    else if (((ieKey == CODE_ARROWRIGHT) && (fieldDirection == false))||((ieKey == CODE_ARROWLEFT) && (fieldDirection == true))) {
	        if(pushMode && (curPos > leftPushBound))
	            toggleFieldOrient(obj,true,false);
	    }

	    if (ieKey == CODE_ARROWUP || ieKey == CODE_ARROWDOWN || (e.shiftKey && (ieKey == CODE_HOME || ieKey== CODE_END)) ||
	    	(egl.IE && (e.ctrlKey || adjustCaretOnKeyUp) && (ieKey == CODE_Z || ieKey == CODE_CTRL))) {
	    	
	        if(pushMode && !adjustCaretOnKeyUp)   
	        	toggleFieldOrient(obj,true,false);

	        var selection = getCaretPos(e,obj);
	        if (selection){
	        	selectionStart = Math.min(selection[0],selection[1]);
	        	curPos = Math.max(selection[0],selection[1]);
	        }
	        adjustCaretOnKeyUp = false;       
	    }
	    else if (ieKey == CODE_HOME || ieKey == CODE_PAGEUP)
	        processHome(obj,e);
	    else if (ieKey == CODE_END || ieKey == CODE_PAGEDOWN)
	        processEnd(obj,e);
	    else if(adjustCaretOnKeyUp){
	    	setSelectedRange(obj,selectionStart,curPos);
	    	adjustCaretOnKeyUp = false;
	    }    
	}

	function handleKeyDown(e) {
	    if(egl.IE)
	    	e = window.event;

	    var obj = this;
	    var ieKey = e.keyCode;

	    if(ieKey == 18)
	        preventAltNumpad = true;
	    else if(!e.altKey)
	        preventAltNumpad = false;

	    fieldDirection = (obj.style.direction == "ltr") ? false : true;
	    swapChar = ieKey;
    
	    if(e.shiftKey)
	        processPush(obj,ieKey);

	    if((!isLinux && e.shiftKey && e.altKey) || (isLinux && (ieKey == 18)) ) {
	        layerGuess = !layerGuess;
	    }

	    if((e.altKey && !isLinux) || (e.shiftKey && isLinux)) {
	        if(((ieKey == 111) && !isLinux) || ((ieKey == 102) && isLinux)) {  //Toggle Autopush
	            autoPush = !autoPush;
	            if(pushMode)
	            	toggleFieldOrient(obj,true,false);

	            preventAltNumpad = true;
	        }
	        else if(((ieKey == 144) && !isLinux) || ((ieKey == 100) && isLinux)) {  //Field reverse
	            processFieldReverse(obj,true);
	            preventAltNumpad = true;
	        }
	    }
	    else if (ieKey == CODE_BACKSPACE) {
	        processBackspace(obj,e);
	    }
	    else if (ieKey == CODE_DELETE) {
	        processDelete(obj);
	    }
	    else if(egl.IE) {
	        if(e.ctrlKey) {	        	
	        	if (ieKey == CODE_A)
	        		handleDblClick(e);
	        	else if (ieKey == CODE_Z)
	        		adjustCaretOnKeyUp = true;	        	
	        }
	        else if(ieKey == CODE_ENTER) {
	            if(pushMode)
	                toggleFieldOrient(obj,false,false);

	            if(obj.type == "textarea") 
	            	selectionStart = curPos = curPos + 2;
	        }
	        else if ((ieKey == CODE_HOME)||(ieKey == CODE_END)||(ieKey == CODE_PAGEUP)||(ieKey == CODE_PAGEDOWN)) {
	            if(!e.shiftKey)
	            	preventDefault(e); //disable, process it in KeyUp
	        }
	        else if (((ieKey == 37) & (fieldDirection == 0))|((ieKey == 39) & (fieldDirection == 1))) { // Left Arrow
	            processLeftarrow(obj,e);
	        }
	        else if (((ieKey == 39) & (fieldDirection == 0))|((ieKey == 37) & (fieldDirection == 1))) { // Right Arrow
	            processRightarrow(obj,e);
	        }
	    }
	    else if (e.ctrlKey && (ieKey == 88 || ieKey == 67 || ieKey == 86)) { //copy/paste/cut for firefox
	    	preventEraseMarker(obj);
	    }    
	}

	function preventDefault(event_) {
	    if(egl.IE)
	        event_.returnValue = false;
	    else
	        event_.preventDefault();
	}

	function doSymSwap(symbol) {
	    switch(symbol)
	    {
	        case "(":
	            symbol = ")";
	            break;
	        case ")":
	            symbol = "(";
	            break;
	        case "{":
	            symbol = "}";
	            break;
	        case "}":
	            symbol = "{";
	            break;
	        case "[":
	            symbol = "]";
	            break;
	        case "]":
	            symbol = "[";
	            break;
	        case "<":
	            symbol = ">";
	            break;
	        case ">":
	            symbol = "<";
	            break;
	    }
	    return symbol;
	}

	function setSelectedRange(obj,selectionStartMod,curPosMod){
	    if(egl.IE) {
	        var range = obj.createTextRange();
	        if (range){
	            if (obj.type == "textarea") { 
	                range.moveToElementText(obj);
	                len = curPosMod;				
	                for(var i = fieldStart; i < len; i++){
	                    if((obj.value.charAt(i) == '\r')) {
	                        selectionStartMod--;
	                        curPosMod--;
	                    }
	                }
				}
	            else
	                range.expand('textedit');
	    
	            range.collapse();
	            range.moveEnd('character', curPosMod);
	            range.moveStart('character', selectionStartMod);
	            range.select();
	        }
	    } else {
	        obj.setSelectionRange(selectionStartMod,curPosMod);
	    }
	}

	function processHome(obj,event_) {
	    var boundaries = getLineBoundaries(obj);
	    var lineStart = (obj.type == "textarea") ? boundaries[0] + 1 : boundaries[0];
	    var lineEnd = (obj.type == "textarea") ? boundaries[1] : boundaries[1] + 1;
	    if(pushMode)
	        selectionStart = curPos = lineEnd;
	    else
	        selectionStart = curPos = lineStart;

	    setSelectedRange(obj,selectionStart,curPos);

	    if(pushMode & (curPos > leftPushBound))
	        toggleFieldOrient(obj,true,false);

	    preventDefault(event_);
	}

	function processEnd(obj,event_) {
	    var boundaries = getLineBoundaries(obj);
	    var lineStart = (obj.type == "textarea") ? boundaries[0] + 1 : boundaries[0];
	    var lineEnd = (obj.type == "textarea") ? boundaries[1] : boundaries[1] + 1;
	    if(pushMode)
	        selectionStart = curPos = lineStart;
	    else
	        selectionStart = curPos = lineEnd;

	    setSelectedRange(obj,selectionStart,curPos);

	    if(pushMode & (curPos < rightPushBound))
	        toggleFieldOrient(obj,true,false);

	    preventDefault(event_);
	}

	function getLineBoundaries(obj) {
	    var len = obj.value.length;
	    var selectionStart_ = (egl.IE) ? selectionStart : obj.selectionStart;

	    var lineStart = fieldStart, lineEnd = len - 1;
	    if (obj.type == "textarea") {
	        for(lineStart = selectionStart_; lineStart >= fieldStart; lineStart--){
	            if(obj.value.charAt(lineStart) == LF)
	                break;
	        }
	    
	        for(lineEnd = curPos; lineEnd < len; lineEnd++){
	            if((obj.value.charAt(lineEnd) == LF) || (obj.value.charAt(lineEnd) == '\r'))
	                break;
	        }
	    }
		return new Array(lineStart,lineEnd);  
	}

	function toggleFieldOrient(obj,setCursor,jumpFromPushSegment) {
	    var delta = 0;
	    if(!egl.IE) {
	        selectionStart = obj.selectionStart;
	        curPos = obj.selectionEnd;
	    }

	    obj.style.direction = (obj.style.direction == "rtl") ? "ltr" : "rtl";
	    if(obj.value.charAt(0) == egl.LRO)
	        obj.value = egl.RLO + obj.value.substring(1);
	    else if(obj.value.charAt(0) == egl.RLO)
	        obj.value = egl.LRO + obj.value.substring(1);

	    var boundaries = getLineBoundaries(obj);
	    reverseText(obj);

	    if(setCursor) {
	        var lineStart = boundaries[0];
	        var lineEnd = boundaries[1];

	        pushMode = !pushMode;
	        if(pushMode) {
	            delta = Math.abs(selectionStart - curPos);
	            leftPushBound = rightPushBound = selectionStart = lineEnd + 1 - Math.max(selectionStart,curPos) + lineStart;	            
	            curPos = selectionStart + delta;
	        }
	        else {
	            if(jumpFromPushSegment)
	                curPos = rightPushBound;

	            selectionStart = curPos = lineEnd + 1 - curPos + lineStart;
	        }

	        setSelectedRange(obj,selectionStart,curPos);
	    }

	}

	function processPush(obj,ieKey) {
	    if(((ieKey == 144) && !isLinux) || ((ieKey == 103) && isLinux)) {
	        preventAltNumpad = true;
	        if(!pushMode)
	            toggleFieldOrient(obj,true,true);
	    }
	    else if(((ieKey == 111) && !isLinux) || ((ieKey == 104) && isLinux)) {
	        preventAltNumpad = true;
	        if(pushMode)
	            toggleFieldOrient(obj,true,true);
	    }
	}

	function reverse(text) {
	    var len = text.length;
	    var temp = "";
	    for(i = 0;i < len;i++)  {
	        symbol = text.charAt(len - i - 1);
	        symbol = doSymSwap(symbol);
	        temp += symbol;
	    }
	    return temp;
	}

	function reverseText(obj) {
	    var text = obj.value;
	    var temp = "";

		if(text.charAt(0) == egl.LRO || text.charAt(0) == egl.RLO) {
			temp = text.charAt(0) + reverse(text.substring(1));
		}
		else { //textarea
		    textPool = text.split(CR);
		    temp += reverse(textPool[0]);
		    for(var j = 1;j < textPool.length; j++)
		    	temp += CR + reverse(textPool[j]);
		}
		
	    if(isLinux && isArabic)
	        obj.value = ara_type(0,temp,(obj.style.direction == "rtl"));
	    else
	        obj.value = temp;
	}

	function processFieldReverse(obj,setCursor) {
	    isFldreversed = !isFldreversed;
	    var len = obj.value.length;

	    if(!egl.IE)
	        curPos = obj.selectionStart;

	    if(len == fieldStart) {
	        obj.style.textAlign = (obj.style.direction == "rtl") ? "left" : "right";
	        setCursor = false;
	    }

	    var boundaries = getLineBoundaries(obj);
	    toggleFieldOrient(obj,false,false);

	    if(setCursor) {
	        var lineStart = boundaries[0];
	        var lineEnd = boundaries[1];

	        selectionStart = curPos = lineEnd + 1 - curPos + lineStart;
	        setSelectedRange(obj,selectionStart,curPos);
	    }
	}

	function preventEraseMarker(obj) {
	    if(fieldStart > 0) {
	    	if(egl.IE) {
	            var rng = document.selection.createRange();
	            var firstChar = rng.text.charAt(0);
	            if((firstChar == egl.LRO) || (firstChar == egl.RLO)) {
	                rng.moveStart('character', fieldStart);
	                rng.select();
	                curPos = fieldStart;
	            }
	    	}
	    	else {	
	            if(obj.selectionStart < fieldStart)	{ 
	             	if(obj.selectionStart != obj.selectionEnd)       
	            	    obj.setSelectionRange(obj.selectionStart + 1,obj.selectionEnd);
	             	else
	            	    obj.setSelectionRange(obj.selectionStart + 1,obj.selectionEnd + 1);      	            	
	            }
	    	}
	    }
	}

	function processDelete(obj) {
	    if(!egl.IE) {
	        selectionStart = obj.selectionStart;
	        curPos = obj.selectionEnd;
	    }

	    if(pushMode && (selectionStart < leftPushBound)){
	        if(selectionStart == curPos)
	            leftPushBound--;
	        else
	            leftPushBound -= Math.abs(selectionStart - curPos);
	    }

	    preventEraseMarker(obj);

	    if(egl.IE)
	        selectionStart = curPos = Math.min(selectionStart,curPos);
	}

	function processBackspace(obj,event_) {
	    var noSelection;
	    if(egl.IE) {
	        var range = document.selection.createRange();
	        noSelection = ((range.text).length == 0) ;
	    }
	    else {
	        selectionStart = obj.selectionStart;
	        curPos = obj.selectionEnd;
	        noSelection = (selectionStart == curPos);
	    }
	    
	    if(noSelection) {
	        if(curPos > fieldStart) {
	            if(pushMode && (curPos <= rightPushBound)) {
	                if(egl.IE)
	                    event_.returnValue = false;
	                else{
	                    event_.preventDefault();
	                    preventAltNumpad = true;
	                }
	                return;
	            }
	            curPos = (egl.IE && (obj.value.charAt(curPos - 1) == LF)) ? curPos - 2 : curPos - 1;
	            if(pushMode)
	                leftPushBound--;
	        }
	        else {      
	            preventDefault(event_);
	            return;
	        }
	    }
	    else {
	        if(pushMode)
	            leftPushBound -= Math.abs(selectionStart - curPos);

	            preventEraseMarker(obj);
	            curPos = Math.min(selectionStart,curPos);
	    }
	    selectionStart = curPos;
	}

	function processLeftarrow(obj,event_) {
	    if(curPos > fieldStart) {
	        if (event_.shiftKey) {//start selection
	            if (selectionStart == curPos)
	                selectionStart = curPos;

	            curPos = (egl.IE && (obj.value.charAt(curPos - 1) == LF)) ? curPos - 2 : curPos - 1;
	        }
	        else {
	            if(selectionStart != curPos)
	                selectionStart = curPos = Math.min(selectionStart,curPos);
	            else
	                selectionStart = curPos = (egl.IE && (obj.value.charAt(curPos - 1) == LF)) ? curPos - 2 : curPos - 1;
	        }
	    }
	    else
	        preventDefault(event_);
	}

	function processRightarrow(obj,event_) {
	    var text = obj.value;
	    if(curPos < text.length) {
	        if (event_.shiftKey) {//start selection
	            if (selectionStart == curPos)
	                selectionStart = curPos;

	            curPos = (egl.IE && (obj.value.charAt(curPos + 1) == LF)) ? curPos + 2 : curPos + 1;
	        }
	        else {
	            if(selectionStart != curPos)
	                selectionStart = curPos = Math.max(selectionStart,curPos);
	            else
	                selectionStart = curPos = (egl.IE && (obj.value.charAt(curPos + 1) == LF)) ? curPos + 2 : curPos + 1;
	        }
	    }
	    else
	        preventDefault(event_);;
	}
	//BiDi ENGINE

	function JSReorder(input_logical_rtl){

		if(input_logical_rtl==null)
			this.LogicalRTL=0
		else	
			this.LogicalRTL=input_logical_rtl;  //0 means LTR, 1 means RTL

	this.INIT           =  0;
	this.LATIN          =  1;
	this.LOCAL          =  2;
	this.NEUTRAL            =  3;
	this.LOCALNUMBER        =  4;
	this.LATINNUMBER        =  5;
	this.LATINNUMERICTERMINATOR =  6;
	this.NUMERICSEPARATOR       =  8;
	this.ENDOFLINE          =  9;
	this.CARRIAGERETURN     = 10;
	this.LINEFEED           = 11;
	this.SPACE          = 12;
	this.ARABICDIGIT        = 13;
	this.SEGMENTSEPARATOR       = 14;
	this.BLOCKSEPARATOR     = 15;
	this.BIDISPECIAL        = 16;
	this.NONSEGMENTMARKER   	= 17;		

	this.LATIN_STATE        =  0;
	this.LOCAL_STATE        =  1;
	this.LATIN_NUM_STATE        =  2;
	this.LOCAL_NUM_STATE        =  3;
	this.NEUTRAL_STATE      =  4;
	this.EOL_STATE          =  5;

	this.MASK_STATE         = 0x00FF;
	this.MASK_ACTION        = 0xFF00;

	this.ACTION         = 0x4000;
	this.NL             = 0x4100;     //NEUTRALS TO LATIN
	this.NR             = 0x4200;     //NEUTRALS TO LOCAL
	this.LN             = 0x4300	  //LOCAL TO NUMBER; 

	this.LocalInvert        = 1;
	this.LocalNumInvert     = 2;
	this.LatinInvert	    = 3; 
	this.AllLineInvert	    = 4; 

	var NL = this.NL;
	var NR = this.NR;
	var LN = this.LN;				

	this.StateTable = new Table(6,10);


if(this.LogicalRTL==1){
	this.StateTable.initTable(
                          /*   I,   L,   R,  N,   ,   D,   ,   ,  E,    */
/* 0  LATIN_STATE      */      1,   0,   1,  4, -1,   2, -1, -1,  4,   5 ,
/* 1  LOCAL_STATE      */      1,   0,   1,  1, -1,LN|3, -1, -1,  1,   5 ,
/* 2  LATIN_NUM_STATE  */      1,   0,   1,  4, -1,   2, -1, -1,  0,   5 ,
/* 3  LOCAL_NUM_STATE  */      1,   0,   1,  1, -1,   3, -1, -1,  1,   5 ,
/* 4  NEUTRAL_STATE    */   NR|1,NL|0,NR|1,  4, -1,NL|2, -1, -1,  4,NR|5 ,
/* 5  EOL_STATE        */      1,   0,   1,  1, -1,   3, -1, -1,  1,   5 
)

}else{
	this.StateTable.initTable(
	                    /*  I   L   R   N   .   D   .   .   E   .*/

	/* 0 LATIN_STATE     */ 0,  0,  1,  0,  -1, 2,  -1, -1, 0,  5,
	/* 1 LOCAL_STATE     */ 0,  0,  1,  4,  -1, 3,  -1, -1, 4,  5,
	/* 2 LATIN_NUM_STATE */ 0,  0,  1,  0,  -1, 2,  -1, -1, 0,  5,
	/* 3 LOCAL_NUM_STATE */ 0,  0,  1,  4,  -1, 3,  -1, -1, 4,  5,
	/* 4 NEUTRAL_STATE   */ NL|0,   NL|0,   NR|1,   4,  -1, NR|3,   -1, -1, 4,  NL|5,
	/* 5 EOL_STATE       */ 0,  0,  1,  0,  -1, 2,  -1, -1, 0,  5
)	
}

function getUnicodeSymmSwap()	//GBR1 - add method
{
   return this.UnicodeSymmSwap
}

//////////////////////
function DoSymSwap(array)		//GBR1 - add method
{
 var i,mid,low,high; 
 var ss = this.getUnicodeSymmSwap().getArray() 

 for(i=0; i<array.length; i++){

	if(this.stringAsClasses[i] != this.LOCAL)	
		continue;			

	low = 0;
	high = this.SIZE_OF_UNISYMM-1;
	while(low < high) {
	  mid = Math.ceil( (low+high) /2);
	  if (array[i] < ss[mid][0])
		 high = mid-1;
	  else if (array[i] > ss[mid][0])
		 low = mid+1;
	  else {
		 array[i] = ss[mid][1];
		 break;
	  }
	}

	if(low==high && array[i]==ss[low][0])
		array[i] = ss[low][1];
 }

}
	this.getStateTable = getStateTable;

	this.StateToClass = new Array( this.LATIN, this.LOCAL, this.LATINNUMBER, this.LOCALNUMBER,
	                   this.NEUTRAL, this.ENDOFLINE );

	var I               = this.INIT;
	var L               = this.LATIN;
	var R               = this.LOCAL;
	var D               = this.LATINNUMBER;
	var X               = this.LATINNUMERICTERMINATOR;
	var E               = this.NUMERICSEPARATOR;
	var N               = this.NEUTRAL;
	var S               = this.SPACE;
	var C               = this.CARRIAGERETURN;
	var F               = this.LINEFEED;
	var A               = this.ARABICDIGIT;
	var G               = this.SEGMENTSEPARATOR;
	var B               = this.BLOCKSEPARATOR;
	var H               = this.BIDISPECIAL;
	var M               = this.NONSEGMENTMARKER;			

	this.UnicodeTable = new Table(9,256);
	this.UnicodeTable.initTable(
	   /* Table 0: Unicode 00xx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  I,  F,  N,  N,  C,  N,  N,
	   /*1-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*2-*/ S,  N,  N,  X,  X,  X,  N,  N,  N,  N,  N,  N,  E,  N,  E,  E,
	   /*3-*/ D,  D,  D,  D,  D,  D,  D,  D,  D,  D,  E,  N,  N,  N,  N,  N,
	   /*4-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*5-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  N,  N,  N,  N,  N,
	   /*6-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*7-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  N,  N,  N,  N,  N,
	   /*8-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*9-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*A-*/ S,  N,  X,  X,  X,  X,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*B-*/ X,  X,  D,  D,  N,  N,  N,  N,  N,  D,  N,  N,  N,  N,  N,  N,
	   /*C-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*D-*/ L,  L,  L,  L,  L,  L,  L,  N,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*E-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*F-*/ L,  L,  L,  L,  L,  L,  L,  N,  L,  L,  L,  L,  L,  L,  L,  L,

	   /* Table 1: Unicode 05xx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*1-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*2-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*3-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*4-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*5-*/ L,  L,  L,  L,  L,  L,  L,  N,  N,  L,  L,  L,  L,  L,  L,  L,
	   /*6-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*7-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*8-*/ L,  L,  L,  L,  L,  L,  L,  L,  N,  L,  N,  N,  N,  N,  N,  N,
	   /*9-*/ N,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*A-*/ R,  R,  N,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*B-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  N,  R,  R,  R,  R,  R,
	   /*C-*/ R,  R,  R,  R,  R,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*D-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*E-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  N,  N,  N,  N,  N,
	   /*F-*/ R,  R,  R,  R,  R,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,

	   /* Table 2: Unicode 06xx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  R,  N,  N,  N,
	   /*1-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  R,  N,  N,  N,  N,
	   /*2-*/ N,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*3-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  N,  N,  N,  N,  N,
	   /*4-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*5-*/ R,  R,  R,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*6-*/ A,  A,  A,  A,  A,  A,  A,  A,  A,  A,  X,  A,  A,  R,  N,  N,
	   /*7-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*8-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*9-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*A-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*B-*/ R,  R,  R,  R,  R,  R,  R,  R,  N,  N,  R,  R,  R,  R,  R,  N,
	   /*C-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  N,
	   /*D-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*E-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  N,  N,
	   /*F-*/ D,  D,  D,  D,  D,  D,  D,  D,  D,  D,  N,  N,  N,  N,  N,  N,

	   /* Table 3: Unicode 20xx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ S,  S,  S,  S,  S,  S,  S,  E,  S,  S,  S,  S,  N,  N,  L,  R,
	   /*1-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*2-*/ N,  N,  N,  N,  N,  N,  N,  N,  B,  B,  H,  N,  N,  N,  H,  N,
	   /*3-*/ X,  X,  X,  X,  X,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*4-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*5-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*6-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  H,  H,  H,  H,
	   /*7-*/ D,  N,  N,  N,  D,  D,  D,  D,  D,  D,  X,  X,  N,  N,  N,  N,
	   /*8-*/ D,  D,  D,  D,  D,  D,  D,  D,  D,  D,  X,  X,  N,  N,  N,  N,
	   /*9-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*A-*/ X,  X,  X,  X,  X,  X,  X,  X,  X,  X,  X,  X,  N,  N,  N,  N,
	   /*B-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*C-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*D-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*E-*/ L,  L,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*F-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,

	   /* Table 4: Unicode 21xx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*1-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*2-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*3-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*4-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*5-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*6-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*7-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*8-*/ L,  L,  L,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*9-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*A-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*B-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*C-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*D-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*E-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*F-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,

	   /* Table 5: Unicode 30xx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ S,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*1-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*2-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*3-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*4-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*5-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*6-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*7-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*8-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*9-*/ L,  L,  L,  L,  L,  N,  N,  N,  N,  L,  L,  L,  L,  L,  L,  N,
	   /*A-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*B-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*C-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*D-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*E-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*F-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  N,

	   /* Table 6: Unicode FBxx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ L,  L,  L,  L,  L,  L,  L,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*1-*/ N,  N,  N,  L,  L,  L,  L,  L,  N,  N,  N,  N,  N,  N,  R,  R,
	   /*2-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*3-*/ R,  R,  R,  R,  R,  R,  R,  N,  R,  R,  R,  R,  R,  N,  R,  N,
	   /*4-*/ R,  R,  N,  R,  R,  N,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*5-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*6-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*7-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*8-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*9-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*A-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*B-*/ R,  R,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*C-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*D-*/ N,  N,  N,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*E-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*F-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,

	   /* Table 7: Unicode FExx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*1-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*2-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*3-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*4-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*5-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*6-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*7-*/ R,  R,  R,  N,  R,  N,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*8-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*9-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*A-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*B-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*C-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*D-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*E-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,
	   /*F-*/ R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  R,  N,  N,  N,

	   /* Table 8: Unicode FFxx */
	   /************************************************************************/
	   /*     0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F    */
	   /************************************************************************/
	   /*0-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*1-*/ D,  D,  D,  D,  D,  D,  D,  D,  D,  D,  N,  N,  N,  N,  N,  N,
	   /*2-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*3-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  N,  N,  N,  N,  N,
	   /*4-*/ N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*5-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  N,  N,  N,  N,  N,
	   /*6-*/ N,  N,  N,  N,  N,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*7-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*8-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*9-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*A-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,
	   /*B-*/ L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  L,  N,
	   /*C-*/ N,  N,  L,  L,  L,  L,  L,  L,  N,  N,  L,  L,  L,  L,  L,  L,
	   /*D-*/ N,  N,  L,  L,  L,  L,  L,  L,  N,  N,  L,  L,  L,  N,  N,  N,
	   /*E-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,
	   /*F-*/ N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N,  N
) 

/******************************************************************************/
/* Array in which directional characters are replaced by their symmetric.     */
/******************************************************************************/
//static char UnicodeSymmSwap[][]= ///const uint16 UnicodeSymmSwap[SIZE_OF_UNISYMM][2] =

this.SIZE_OF_UNISYMM	= 24;

this.UnicodeSymmSwap = new Table(this.SIZE_OF_UNISYMM,2)
this.UnicodeSymmSwap.initTable
(
    "\u0028", "\u0029",  /* Round brackets                            */
    "\u0029", "\u0028",
    "\u003C", "\u003E",  /* Less than/greater than                    */
    "\u003E", "\u003C",
    "\u005B", "\u005D",  /* Square brackets                           */
    "\u005D", "\u005B",
    "\u007B", "\u007D",  /* Curly brackets                            */
    "\u007D", "\u007B",
    "\u00AB", "\u00BB",  /* Double angle quotation marks              */
    "\u00BB", "\u00AB",
    "\u207D", "\u207E",  /* Superscript parentheses                   */
    "\u207E", "\u207D",
    "\u208D", "\u208E",  /* Subscript parentheses                     */
    "\u208E", "\u208D",
    "\u2329", "\u232A",  /* Angle brackets                            */
    "\u232A", "\u2329",
    "\uFE59", "\uFE5A",  /* Small round brackets                      */
    "\uFE5A", "\uFE59",
    "\uFE5B", "\uFE5C",  /* Small curly brackets                      */
    "\uFE5C", "\uFE5B",
    "\uFE5D", "\uFE5E",  /* Small tortoise shell brackets             */
    "\uFE5E", "\uFE5D",
    "\uFE64", "\uFE65",  /* Small less than/greater than              */
    "\uFE65", "\uFE64"
	);
        this.getUnicodeSymmSwap = getUnicodeSymmSwap	
	this.DoSymSwap = DoSymSwap		
	this.getUnicodeTable = getUnicodeTable;

	this.stringAsClasses = new Array();
	this.terminatorFlag = 0;

	this.doReorder = doReorder;
	this.stringToClass = stringToClass;
	this.applyClass = applyClass;
	this.getCharClass = getCharClass;
	this.getCharacterType = getCharacterType;
	this.doAction = doAction;
	this.invertSegment = invertSegment;
	this.needInvert = needInvert;
	}

	function getStateTable()
	{
	   return this.StateTable;
	}

	function getUnicodeTable()
	{
	   return this.UnicodeTable;
	}

	function getCharacterType(ch)
	{
	    var utab = this.getUnicodeTable().getArray();
	    var str = new String(ch);
	    var code = str.charCodeAt(0);
	    var index = (code & 0xFF00) >> 8;
	    switch (index)
	    {
	        case 0x00: break;
	        case 0x05: index = 1;
	               break;
	        case 0x06: index = 2;
	               break;
	        case 0x20: index = 3;
	               break;
	        case 0x21: index = 4;
	               break;
	        case 0x30: index = 5;
	               break;
	        case 0xFB: index = 6;
	               break;
	        case 0xFE: index = 7
	               break;
	        case 0xFF: index = 8
	               break;
	      default:   return(this.NEUTRAL);
	    }
	    return (utab[index][code & 0xFF]);
	}

	function doReorder(str)
	{
	  var array = new Array(str.length)
  	  var Str = new String("");
	  var i;
	  for (i=0; i<str.length; i++)
            array[i] = str.charAt(i);	
	
	  this.stringToClass(str);

	  this.DoSymSwap(array);		

	  if(this.LogicalRTL==1){
            this.invertSegment(array,this.LatinInvert);
            this.invertSegment(array,this.LocalNumInvert);
            this.invertSegment(array,this.AllLineInvert);
          } else{
            this.invertSegment(array,this.LocalNumInvert);
            this.invertSegment(array,this.LocalInvert);
          }

          for (i=0; i<array.length; i++)
            Str += array[i]

	  return (Str)
	}

	function stringToClass(str)
	{
	    var stab = this.getStateTable().getArray();
	    var prevState = 0;
	    var initialCharClass = this.LATIN;
	    if(this.LogicalRTL==1)			//GBR 
	      initialCharClass = this.LOCAL; 		//GBR 
	    var curState = stab[prevState][initialCharClass];
	    var curCharClass;
	    var i;

	    for (i=0; i<str.length; i++)
	    {
	        prevState = curState;
	        curCharClass = this.getCharClass(str,i);
	        curState = stab[prevState][curCharClass];
	        this.stringAsClasses[i] = this.StateToClass[curState & this.MASK_STATE] | this.terminatorFlag;
	        this.doAction(curState,i);
	        curState &= this.MASK_STATE;
	    }
	    for (i=0; i<str.length; i++){
	        if ((this.stringAsClasses[i] & this.MASK_ACTION) == this.ACTION)
	            this.stringAsClasses[i] &= this.MASK_STATE;
	    }
	}

	function getCharClass(str,index)
	{
	    var prev = this.NEUTRAL;
	    var next = this.NEUTRAL;
	    var curr;
	    var result;

	    this.terminatorFlag = 0;
	    if (index > 0)
	        prev = this.getCharacterType(str.charAt(index-1));
	    curr = this.getCharacterType(str.charAt(index));

	    switch (curr)
	    {
	        case this.BLOCKSEPARATOR:
	        case this.SEGMENTSEPARATOR:
	            result = this.LATIN;
	            break;
		
		 case this.NONSEGMENTMARKER:
	            result=prev;
	            break;
		
	        case this.ARABICDIGIT:
	            result = this.LATINNUMBER;
	            break;
	        case this.LATINNUMERICTERMINATOR:
	            if (index>0)
	                prev = this.stringAsClasses[index-1];
                        var j;
			for(j=index+1;j<str.length;j++){
				next = this.getCharacterType(str.charAt(j));
				if(next != this.LATINNUMERICTERMINATOR)
					break;
			}
                    if (prev == this.LATINNUMBER || prev == this.LOCALNUMBER ||
 		        next == this.LATINNUMBER || next == this.LOCALNUMBER)
	                result = this.LATINNUMBER;
	            else
	            {
	                this.terminatorFlag = this.ACTION;
	                result = this.NEUTRAL;
	            }
	            break;
                    case this.LATINNUMBER:
		    //we should process sequence of numbers between local and latin segments
			var j
			for(j=index+1;j<str.length;j++){
				next = this.getCharacterType(str.charAt(j))
				if(next != this.LATINNUMBER && next != this.NEUTRAL )
					break
			}
			
			if(next == this.LATIN && this.LogicalRTL == 1)// || prev==this.LATIN)
				result = this.LATIN
				else
				result = curr

			break;
	        case this.NUMERICSEPARATOR:
	            result = this.NEUTRAL;
	            if (prev == this.LATINNUMBER)
	            {
	                if (index < str.length - 1)
	                    next = this.getCharacterType(str.charAt(index+1));
	                switch (next)
	                {
	                    case this.LATINNUMBER:
	                        result = next;
	                        break;
	                    case this.LOCAL:
	                    case this.NEUTRAL:
	                        result = curr;
	                        break;
	                }
	            }
	            break;
	        case this.BIDISPECIAL:
	        case this.SPACE:
	            result = this.NEUTRAL;
	            break;
	        case this.CARRIAGERETURN:
	        case this.LINEFEED:
	            result = this.ENDOFLINE;
	            break;
	        default:
	            result = curr;
	    }
	    return (result);
	}

	function doAction(currState,index)
	{
	    var action = currState & this.MASK_ACTION;
	    var classFromAction;

	    switch (action)
	    {
	        case this.NL:
	        case this.NR:
	            classFromAction = (action & this.MASK_ACTION & ~this.ACTION) >> 8;
	            while (index-- > 0)
	            {
	                if ((this.stringAsClasses[index] & this.MASK_STATE) == this.NEUTRAL)
	                {
	                    if ((this.stringAsClasses[index] & this.MASK_ACTION) &&
	                        (this.stringAsClasses[index+1] == this.LOCALNUMBER ||
	                         this.stringAsClasses[index+1] == this.LATINNUMBER))
	                        this.stringAsClasses[index] = this.stringAsClasses[index+1];
	                    else
	                        this.stringAsClasses[index] = classFromAction;
	                }
	                else
	                    break;
	            }
	            break;
	    }
	}

	function applyClass(str)
	{
	    var array = new Array(str.length);
	    var Str = new String("");
	    var i;
	    for (i=0; i<str.length; i++)
	        array[i] = str.charAt(i);
	    if(this.LogicalRTL==1){
	      this.invertSegment(array,this.IsLatinInvert);
	      this.invertSegment(array,this.IsLocalNumInvert);   
	    } else {
	      this.invertSegment(array,this.LocalNumInvert);
	      this.invertSegment(array,this.LocalInvert);
	    }
	    if(this.LogicalRTL==1)
	      this.invertSegment(array,this.IsAllLineInvert);
	    for (i=0; i<array.length; i++)
	        Str += array[i];
	    return (Str);
	}

	function needInvert(invertType,index)
	{
	    switch (invertType)
	    {
	        case this.LocalInvert:
	            return (this.stringAsClasses[index] == this.LOCAL ||
	                    this.stringAsClasses[index] == this.LOCALNUMBER);
	        case this.LocalNumInvert:
	            return (this.stringAsClasses[index] == this.LOCALNUMBER);
	        case this.LatinInvert:	
	        	return ((this.stringAsClasses[index]==this.LATIN)|| 
						(this.stringAsClasses[index]==this.LATINNUMBER));
	        case this.AllLineInvert:	
	        	return (this.stringAsClasses[index]!=this.ENDOFLINE);
	        default:
	            return (false);
	    }
	}

	function invertSegment(s,invertType)
	{
	    var start = 0;
	    var end;
	    var sum;
	    var tmp;
	    var i;

	    while (start < s.length)
	    {
	        if (this.needInvert(invertType,start))
	        {
	            end = start + 1;
	            while (end < s.length)
	            {
	                if (!this.needInvert(invertType,end))
	                    break;
	                end++;
	            }
	            sum = end - 1 + start;
	            for (; start < sum - start; start++)
	            {
	                tmp = doSymSwap( s[start] );
	                s[start] = doSymSwap( s[sum-start] );
	                s[sum-start] = tmp;
	            }
	            start = end - 1;
	        }
	        start++;
	    }
	}

	// --------------------------------------------------------

	function Table(nrows,ncols)
	{
	    var i = 0;
	    this.trows = nrows;
	    this.tcols = ncols;
	    this.rows = new Array(nrows);
	    for (i=0; i<nrows; i++)
	        this.rows[i] = new Array(ncols);
	    this.initTable = initTable;
	    this.getArray = getArray;
	}

	function getArray()
	{
	    return this.rows;
	}

	function initTable()
	{
	    var i = 0;
	    var j = 0;
	    var k = 0;
	    var l = 0;
	    var m = Math.min(this.trows*this.tcols,initTable.arguments.length);
	    for (k=0; k<m; k++,l++)
	    {
	        if (l == this.tcols)
	        {
	            i++;
	            j=0;
	            l=0;
	        }
	        this.rows[i][j++] = this.initTable.arguments[k];
	    }
	}

	//SHAPING ENGINE
	var INIT                      = 0x80000000 ;
	var LEVEL                     = 0x70000000 ;
	var TEXTTYPE                  = 0x01000000 ;
	var TEXT_ORIENTATION          = 0x00030000 ;
	var NUMERALS                  = 0x00003000 ;
	var SYM_SWAP                  = 0x00000100 ;
	var WORD_BREAK                = 0x00000200 ;
	var TEXT_SHAPE                = 0x000000FF ;
	var ROUND_TRIP                = 0X00100000 ;

	var TEXT_VISUAL               = 0x00000000 ;
	var TEXT_IMPLICIT             = 0x01000000 ;

	var ORIENTATION_LTR           = 0x00000000 ;
	var ORIENTATION_RTL           = 0x00010000 ;
	var ORIENTATION_CONTEXT_LTR   = 0x00020000 ;
	var ORIENTATION_CONTEXT_RTL   = 0x00030000 ;

	var NUMERALS_NOMINAL          = 0x00000000 ;
	var NUMERALS_NATIONAL         = 0x00002000 ;
	var NUMERALS_CONTEXTUAL       = 0x00003000 ;

	var BREAK                     = 0x00000200 ;
	var NO_BREAK                  = 0x00000000 ;

	var SWAPPING                  = 0x00000100 ;
	var NO_SWAPPING               = 0x00000000 ;

	var TEXT_SHAPED               = 0x00000000 ;
	var TEXT_NOMINAL              = 0x00000010 ;
	var TEXT_INITIAL              = 0x00000011 ;
	var TEXT_MIDDLE               = 0x00000012 ;
	var TEXT_FINAL                = 0x00000013 ;
	var TEXT_ISOLATED             = 0x00000014 ;

	var TAIL                      = 0x200B ;

	var ROUND_TRIP_ON             = 0x00000000 ;
	var ROUND_TRIP_OFF            = 0x00100000 ;

	var formatedAttributes        = 0;
	//////////////**************** HODbidiAttribute Constants End********************/////////////

	//////////////**************** HODbidiAttribute Functions Start********************/////////////
	function setAttribute (name ,value){

	  formatedAttributes = ((formatedAttributes & (~(name)) ) | (value & (name)) )  ;

	}

	function getAttribute (attr ,name){

	  return ( (attr) & (name) );

	}
	//////////////**************** HODbidiAttribute Functions End********************/////////////

	//////////////**************** HODbidiShape Constants ****************************/////////////
	var LINKR = 1;
	var LINKL = 2;
	var IRRELEVANT = 4;
	var LAMTYPE = 16;
	var ALEFTYPE = 32;
	var LINKFIELD = 3;
	var lamAlphCount = 0;

	//////////////**************** HODbidiShape Array definitions********************/////////////
	var shapeTable    =     [
	                [ [0,0,0,0],   [0,0,0,0],     [0,1,0,3],     [0,1,0,1] ],
	        [ [0,0,2,2],   [0,0,1,2],     [0,1,1,2],     [0,1,1,3] ],
	        [ [0,0,0,0],   [0,0,0,0],     [0,1,0,3],     [0,1,0,3] ],
	        [ [0,0,1,2],   [0,0,1,2],     [0,1,1,2],     [0,1,1,3] ]
	        ];

	var convertFEto06 =     [
	                                0x64B, 0x64B,
	                                0x64C, 0x64C,
	                                0x64D, 0x64D,
	                                0x64E, 0x64E,
	                                0x64F, 0x64F,
	                                0x650, 0x650,
	                                0x651, 0x651,
	                                0x652, 0x652,
	                                0x621,
	                                0x622, 0x622,
	                                0x623,0x623,
	                                0x624,0x624,
	                                0x625,0x625,
	                                0x626,0x626,0x626,0x626,
	                                0x627,0x627,
	                                0x628,0x628,0x628,0x628,
	                                0x629,0x629,
	                                0x62A,0x62A,0x62A,0x62A,
	                                0x62B,0x62B,0x62B,0x62B,
	                                0x62C,0x62C,0x62C,0x62C,
	                                0x62D,0x62D,0x62D,0x62D,
	                                0x62E,0x62E,0x62E,0x62E,
	                                0x62F,0x62F,
	                                0x630,0x630,
	                                0x631,0x631,
	                                0x632,0x632,
	                                0x633,0x633, 0x633,0x633,
	                                0x634, 0x634,0x634,0x634,
	                                0x635,0x635,0x635,0x635,
	                                0x636,0x636,0x636,0x636,
	                                0x637,0x637,0x637,0x637,
	                                0x638,0x638,0x638,0x638,
	                                0x639,0x639,0x639,0x639,
	                                0x63A,0x63A,0x63A,0x63A,
	                                0x641,0x641,0x641,0x641,
	                                0x642,0x642,0x642,0x642,
	                                0x643,0x643,0x643,0x643,
	                                0x644,0x644,0x644,0x644,
	                                0x645,0x645,0x645,0x645,
	                                0x646,0x646,0x646,0x646,
	                                0x647,0x647,0x647,0x647,
	                                0x648, 0x648,
	                                0x649,0x649,
	                                0x64A,0x64A,0x64A,0x64A,
	                                0x65C, 0x65C,
	                                0x65D,0x65D,
	                                0x65E,0x65E,
	                                0x65F,0x65F


	                               ];

	var Link06 = [
	                               1           + 32 + 256 * 0x11,
	                               1           + 32 + 256 * 0x13,
	                               1                + 256 * 0x15,
	                               1           + 32 + 256 * 0x17,
	                               1 + 2            + 256 * 0x19,
	                               1           + 32 + 256 * 0x1D,
	                               1 + 2            + 256 * 0x1F,
	                               1                + 256 * 0x23,
	                               1 + 2            + 256 * 0x25,
	                               1 + 2            + 256 * 0x29,
	                               1 + 2            + 256 * 0x2D,
	                               1 + 2            + 256 * 0x31,
	                               1 + 2            + 256 * 0x35,
	                               1                + 256 * 0x39,
	                               1                + 256 * 0x3B,
	                               1                + 256 * 0x3D,
	                               1                + 256 * 0x3F,
	                               1 + 2            + 256 * 0x41,
	                               1 + 2            + 256 * 0x45,
	                               1 + 2            + 256 * 0x49,
	                               1 + 2            + 256 * 0x4D,
	                               1 + 2            + 256 * 0x51,
	                               1 + 2            + 256 * 0x55,
	                               1 + 2            + 256 * 0x59,
	                               1 + 2            + 256 * 0x5D,
	                               0, 0, 0, 0, 0, /* 0x63B - 0x63F */
	                               1 + 2,
	                               1 + 2            + 256 * 0x61,
	                               1 + 2            + 256 * 0x65,
	                               1 + 2            + 256 * 0x69,
	                               1 + 2       + 16 + 256 * 0x6D,
	                               1 + 2            + 256 * 0x71,
	                               1 + 2            + 256 * 0x75,
	                               1 + 2            + 256 * 0x79,
	                               1                + 256 * 0x7D,
	                               1                + 256 * 0x7F,
	                               1 + 2            + 256 * 0x81,
	                               4, 4, 4, 4,
	                               4, 4, 4, 4,      /* 0x64B - 0x652 */
	                               0, 0, 0, 0, 0,
	                               0, 0, 0, 0,      /* 0x653 - 0x65B */
	                               1                + 256 * 0x85,
	                               1                + 256 * 0x87,
	                               1                + 256 * 0x89,
	                               1                + 256 * 0x8B,
	                               0, 0, 0, 0, 0,
	                               0, 0, 0, 0, 0,
	                               0, 0, 0, 0, 0, 0, /* 0x660 - 0x66F */
	                               4,
	                               0,
	                               1           + 32,
	                               1           + 32,
	                               0,
	                               1           + 32,
	                               1, 1,
	                               1+2, 1+2, 1+2, 1+2, 1+2, 1+2,
	                               1+2, 1+2, 1+2, 1+2, 1+2, 1+2,
	                               1+2, 1+2, 1+2, 1+2,
	                               1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	                               1, 1, 1, 1, 1, 1, 1, 1,
	                               1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2,
	                               1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2,
	                               1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2,
	                               1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2, 1+2,
	                               1,
	                               1+2,
	                               1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	                               1+2,
	                               1,
	                               1+2, 1+2, 1+2, 1+2,
	                               1, 1
	                             ];

	var LinkFE =[

	                               1 + 2,
	                               1 + 2,
	                               1 + 2, 0, 1+ 2, 0, 1+ 2,
	                               1 + 2,
	                               1+ 2, 1 + 2, 1+2, 1 + 2,
	                               1+ 2, 1 + 2, 1+2, 1 + 2,

	                               0, 0 + 32, 1 + 32, 0 + 32,
	                               1 + 32, 0, 1,  0 + 32,
	                               1 + 32, 0, 2,  1 + 2,
	                               1, 0 + 32, 1 + 32, 0,
	                               2, 1 + 2, 1, 0,
	                               1, 0, 2, 1 + 2,
	                               1, 0, 2, 1 + 2,
	                               1, 0, 2, 1 + 2,
	                               1, 0, 2, 1 + 2,
	                               1, 0, 2, 1 + 2,
	                               1, 0, 1, 0,
	                               1, 0, 1, 0,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0 + 16, 2 + 16, 1 + 2 +16,
	                               1 + 16, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 2, 1+2,
	                               1, 0, 1, 0,
	                               1, 0, 2, 1+2,
	                               1, 0, 1, 0,
	                               1, 0, 1, 0,
	                               1

	                            ];

	var IrreleventPos =          [
	                             0x0,
	                             0x2,
	                             0x4,
	                             0x6,
	                             0x8,
	                             0xA,
	                             0xC,
	                             0xE,
	                             ]  ;


	var AlefTable =             [
	                            0x0622,
	                            0x0623,
	                            0x0625,
	                            0x0627
	                            ];
	var LamAlefTable =          [
	                            0xfef5,
	                            0xfef7,
	                            0xfef9,
	                            0xfefb
	                            ];


	var AlefTableFE  =          [
	                            0xFE81,
	                            0xFE82,
	                            0xFE83,
	                            0xFE84,
	                            0xFE87,
	                            0xFE88,
	                            0xFE8D,
	                            0xFE8E
	                            ];


	var LamTableFE =            [
	                            0xFEDD,
	                            0xFEDE,
	                            0xFEDF,
	                            0xFEE0
	                            ];

	var  LamAlefTableFE =       [
	                            0xfef5,
	                            0xfef7,
	                            0xfef9,
	                            0xfefb,
	                            0xfef6,
	                            0xfef8,
	                            0xfefa,
	                            0xfefc
	                            ];

	//////////////**************** HODbidiShape Function Defnitions ****************************/////////////
	function uba_getLink(x){

	       if(x >= 0x0622 && x <= 0x06D3)
	           return(Link06[x-0x0622]);
	       else if(x == 0x200D)
	           return(3);
	       else if(x >= 0x206D && x <= 0x206F)
	           return(4);
	       else if(x >= 0xFE70 && x <= 0xFEFC)
	           return(LinkFE[x-0xFE70]);
	       else
	           return(0);

	}

	function LamAlef(alef){
	      for(var i=0;i<AlefTable.length;i++){
	          if(AlefTable[i]==alef)
	              return LamAlefTable[i];
	      }
	      return 0;
	}

	function isAlef(c){
	      for(var i=0;i<AlefTable.length;i++){
	          if(AlefTable[i]==c) return true;
	      }
	      return false;
	}

	function isAlefFE(c){
	      for(var i=0;i<AlefTableFE.length;i++){
	          if(AlefTableFE[i]==c) return true;
	      }
	      return false;
	}

	function isLamFE(c){
	      for(var i=0;i<LamTableFE.length;i++){
	          if(LamTableFE[i]==c) return true;
	      }
	      return false;
	}

	function isLamAlefFE(c){
	      for(var i=0;i<LamAlefTableFE.length;i++){
	          if(LamAlefTableFE[i]==c) return true;
	      }
	      return false;
	}

	function LamAlefFE(alef){
	      for(var i=0;i<AlefTableFE.length;i++){
	          if(AlefTableFE[i]==alef)
	          return LamAlefTableFE[i/2];
	      }
	     return 0;
	}

	function specialChar(ch){

	     if((ch >= 0x0621 && ch < 0x0626)|| (ch == 0x0627 )||
	           (ch > 0x062e && ch < 0x0633) ||
	                (ch > 0x0647 && ch < 0x064a) || (ch == 0x0629)
	                 ||  (ch >= 0x065C && ch <= 0x065F))
	       return(1);
	     else
	       return(0);
	}

	function YeehHamzaChar(ch){

	     if ( (ch==0xFE89) ||  (ch==0xFE8A))
	           return(true);
	     else
	          return(false);
	}

	function Lamalef(x){

	        if(x == 0x0622)
	          return(0x065C);
	        else if(x == 0x0623)
	          return(0x065D);
	        else if(x == 0x0625)
	          return(0x065E);
	        else if(x == 0x0627)
	          return(0x065F);
	        else
	          return(0);
	}

	function SeenChar(ch){

	    if ( (ch==0xFEB1) || (ch==0xFEB2) ||
	         (ch==0xFEB5) || (ch==0xFEB6) ||
	         (ch==0xFEB9) || (ch==0xFEBA) ||
	         (ch==0xFEBD) || (ch==0xFEBE) )
	      return(true);
	    else
	      return(false);
	}

	function CompressLamAlef(UniBuff,length,bRev){

	var  CompressedArray= new Array(length);
	var i=0;
	var j=0;
	var Lam= 0x0644 ;

	 for(i=0;i < length - 1;i++){
	        if(( bRev && isAlef(UniBuff[i]) && UniBuff[i+1]==Lam)||( !bRev && isAlef(UniBuff[i+1]) && UniBuff[i]==Lam)){
	            CompressedArray[j++]=LamAlef((bRev)?UniBuff[i]:UniBuff[i+1]);
	            i++;
	        }else
	           CompressedArray[j++]=UniBuff[i];
	 }
	 if(i<length)
	 CompressedArray[j++]=UniBuff[i];

	 return CompressedArray;
	}
	function invertStr(str) {
	   var array = new Array(str.length);
	   var Str = new String("");
	   var i;
	   for (i=0; i<str.length; i++)
	       array[i] = str.charAt(str.length - i - 1);
	   for (i=0; i<array.length; i++)
	       Str += array[i];
	   return (Str);
	}

	//////////////**************** HODbidiShape Shape Method ****************************/////////////

	  function shape (TXT_ORIENTATION_RTL,INPUT_BUFF) {
	    shape (TXT_ORIENTATION_RTL,INPUT_BUFF,true)
	  }
	  function shape (TXT_ORIENTATION_RTL,INPUT_BUFF,expand_seen_yeh) {

	    var currLink;
	    var lastLink=0;
	    var nextLink=0;
	    var prevLink=0;
	    var lastPos;
	    var Nx;
	    var prevPos;
	    var iEnd, Nw, step,  Shape, i, Ix;
	    var wLamalef;
	    var wBuf ;

	    var j,x;
	    var flag;
	    var trgIdx = 0;

	    var RTL = true;
	    var inOutDiff = false ;
	    var size;
	    if (INPUT_BUFF.length == 0)
	        return;
	    var str06 = new Array(INPUT_BUFF.length);
	    str06 = INPUT_BUFF;




	    if(TXT_ORIENTATION_RTL)
	             RTL = true;
	          else
	             RTL = false;


	    inOutDiff = true;


	    //str06 = CompressLamAlef(str06,str06.length,!TXT_ORIENTATION_RTL);

	    for (var idx = 0; idx < str06.length ; idx++){

	        var inputChar = str06[idx];
	        if ( ( inputChar >= 0xFE70) && (inputChar <= 0xFEFC )){
	            str06[trgIdx] = convertFEto06 [(inputChar - 0xFE70) ]  ;
	        }else{
	            str06[trgIdx] = inputChar ;
	        }
	        trgIdx++;
	    }
	    if (RTL && inOutDiff){
	         Ix = 0;
	         iEnd = str06.length ;
	         step = +1;
	    }
	    else {
	         Ix = str06.length - 1;
	         iEnd = -1;
	         step = -1;
	    }
	    size = str06.length;
	    prevLink = 0;
	    lastLink = 0;

	    currLink = uba_getLink ( str06[Ix] );

	    prevPos = Ix;
	    lastPos = Ix;
	    Nx = -2;
	    while ( Ix != iEnd ) {
	        if ((currLink & 0xFF00) > 0 ) {
	            Nw = Ix + step;
	               while ( Nx < 0  ) {
	                  if (Nw == iEnd) {
	                     nextLink = 0;
	                     Nx = 30000;
	                  }
	                  else {
	                     nextLink = uba_getLink(str06[Nw]);
	                     if ((nextLink & IRRELEVANT) == 0)
	                        Nx = Nw;
	                     else Nw += step;
	                  }
	               }
	               if ( ((currLink & ALEFTYPE) > 0)  &&  ((lastLink & LAMTYPE) > 0) )
	               {
	                  wLamalef = Lamalef( str06[Ix] );
	                  if ( wLamalef != 0)
	                  {
	                    if (RTL && inOutDiff)
	                    {
	                        str06[lastPos] = wLamalef ;
	                        str06[Ix]=  0x0020 ;

	                        Ix=lastPos;

	                     }
	                     else {
	                        str06[Ix] = 0x0020;
	                        str06[lastPos] = wLamalef ;
	                        Ix=lastPos;
	                      }
	                  }
	               lastLink = prevLink;
	               currLink = uba_getLink(wLamalef);
	             }

	             flag=specialChar (str06[Ix]);

	             if (getAttribute(formatedAttributes, TEXT_SHAPE)  == TEXT_INITIAL)
	       {
	               if(flag==0)
	                    Shape = 2;
	               else
	                    Shape = 0;
	             }
	             else
	                 if (getAttribute(formatedAttributes,TEXT_SHAPE) == TEXT_MIDDLE)
	            {
	             if(flag == 0)
	                 Shape = 3;
	             else
	                 Shape = 1;
	            }
	           else
	             if (getAttribute(formatedAttributes,TEXT_SHAPE) == TEXT_FINAL)
	             {
	              if(flag == 0)
	                  Shape = 1;
	              else
	                  Shape = 1;
	             }
	             else
	               if (getAttribute(formatedAttributes, TEXT_SHAPE) == TEXT_ISOLATED)
	               {
	                  Shape = 0;
	                     }
	                  else{

	                    Shape = shapeTable[nextLink & (LINKR + LINKL)]
	                                    [lastLink & (LINKR + LINKL)]
	                                    [currLink & (LINKR + LINKL)];
	                  }
	                str06[Ix] =  (0xFE70 + ( currLink >> 8 ) + Shape) ;

	            }
	            if ((currLink & IRRELEVANT) == 0) {
	               prevLink = lastLink;
	               lastLink = currLink;
	               prevPos = lastPos;
	               lastPos = Ix;
	            }
	            if ((currLink & IRRELEVANT) > 0) {

	            var charidx = str06[Ix] - 0x064B;
	                var  MyShape =0;
	                var next =  (nextLink & (LINKR + LINKL));
	                var last =lastLink & (LINKR + LINKL);

	                if  ( ( (last==3)&& (next==1) )
	                   || ( (last==3) && (next==3) ) )
	                   MyShape= 1;

	               if ( ((nextLink & ALEFTYPE) > 0)  &&  ((lastLink & LAMTYPE) > 0) )
	                   MyShape=0;


	                if ( (str06[Ix]==0x064C)  || (str06[Ix]==0x064D) )
	                   MyShape=0;
	               str06[Ix] =  (0xFE70 + IrreleventPos[charidx]+ MyShape) ;

	            }
	            Ix += step;
	            if ( Ix == Nx ) {
	               currLink = nextLink;

	               Nx = -2;
	            }
	            else            {
	               if (Ix != iEnd)
	                 currLink = uba_getLink ( str06[Ix]) ;

	            }
	      }
	      if(expand_seen_yeh)
	        for (var idx = 0; idx < str06.length ; idx++)
	        {
	             if (RTL && inOutDiff)
	             {
	                 if ( (SeenChar(str06[idx]))  && ( (idx+1 < str06.length)
	                        && ( str06[idx+1] == 0x0020) ) )

	                     str06[idx+1] = TAIL;


	                   if ( (!SeenChar(str06[idx]))

	                     && ( (idx+1 < str06.length) && ( str06[idx+1] == TAIL) ) )
	                      str06[idx+1] = 0x0020;


	                   if  (YeehHamzaChar(str06[idx]))
	                     if ( (idx+1 < str06.length) && ( str06[idx+1] == 0x0020))
	                     {
	                          if(str06[idx] == 0xFE89)
	                           str06[idx]   =  0xFEEF;
	                          else
	                           str06[idx]   =  0xFEF0;
	                          str06[idx+1]  =  0xFE80;
	                     }
	                     else
	                     {
	                          if(str06[idx] == 0xFE89)
	                           str06[idx]   =  0xFE8B;
	                          else
	                           str06[idx]   =  0xFE8C;
	                     }

	                }else
	                {
	                   if ( (SeenChar(str06[idx]))
	                     && ( (idx-1 >= 0) && ( str06[idx-1] == 0x0020) ) )

	                     str06[idx-1] =TAIL;

	                   if ( (!SeenChar(str06[idx]))

	                     && ( (idx-1 >= 0) && ( str06[idx-1] == TAIL ) ))
	                        str06[idx-1]=0x0020;

	                   if  (YeehHamzaChar(str06[idx]))
	                     if ( (idx-1 >= 0) && ( str06[idx-1] == 0x0020))
	                     {
	                          if(str06[idx] == 0xFE89)
	                           str06[idx]   =  0xFEEF;
	                          else
	                           str06[idx]   =  0xFEF0;
	                          str06[idx-1]  =  0xFE80;
	                     }
	                     else
	                     {
	                          if(str06[idx] == 0xFE89)
	                           str06[idx]   =  0xFE8B;
	                          else
	                           str06[idx]   =  0xFE8C;
	                     }


	                }

	      }
	     return str06;
	}
	/***********************************************************************************/
	 function ara_type (index,INPUT_STR,TXT_ORIENTATION_RTL)
	 {

	    var shapeStart = 0;
	    var shapeEnd   = 0;

	    var displayStart = 0 ;
	    var displayEnd   = 0 ;

	    var len = INPUT_STR.length;
	    var shapeBuff = new Array(len);

	    for (var idx0 = 0; idx0 < len ; idx0++)
	         shapeBuff[idx0] = INPUT_STR.charCodeAt(idx0);


	      if ( len > index + 4 )
	      {
	          shapeEnd = index + 4;
	          displayEnd = index + 2;
	      }else
	        if ( len > index + 3 )
	        {
	            shapeEnd = index + 3;
	            displayEnd = index + 2;
	        }else
	          if ( len > index + 2 )
	          {
	              shapeEnd = index + 2;
	              displayEnd = index + 2;
	          }else
	            if ( len > index + 1 )
	            {
	                shapeEnd = index + 1;
	                displayEnd = index + 1;
	            }else
	              if ( len > index  )
	              {
	                  shapeEnd = index ;
	                  displayEnd = index ;
	              }

	      if ( index - 4 >= 0)
	      {
	          shapeStart = index - 4;
	          displayStart = index - 2;
	      }else
	        if ( index - 3 >= 0)
	        {
	            shapeStart = index - 3;
	            displayStart = index - 2;
	        }else
	          if ( index - 2 >= 0)
	          {
	              shapeStart = index - 2;
	              displayStart = index - 2;
	          }else
	            if ( index - 1 >= 0)
	            {
	                shapeStart = index - 1;
	                displayStart = index - 1;
	            }else
	              if ( index  >= 0.0)
	              {
	                  shapeStart = index ;
	                  displayStart = index ;
	              }


	     shapeBuff =  shape (TXT_ORIENTATION_RTL,shapeBuff);

	     var OUTPUT_BUFF = new Array(len);
	     for (var idx0 = 0; idx0 < len ; idx0++)
	         OUTPUT_BUFF[idx0] = INPUT_STR.charCodeAt(idx0);

	     var dstLen =shapeEnd - shapeStart + 1 ;
	     if(shapeEnd - shapeStart > 0 )
	     {
	        for (var indx = shapeStart; indx <= shapeEnd;indx++)
	         OUTPUT_BUFF[indx] = shapeBuff[indx];

	     }

	     var retVal = "";
	     for(var idxt =0 ; idxt < OUTPUT_BUFF.length ; idxt++){
	           retVal += String.fromCharCode(OUTPUT_BUFF[idxt]);
	     }
	      return retVal;
	}
//}		

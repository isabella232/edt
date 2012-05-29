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
if(!dojo._hasResource["bidi.DojoComboBoxBidi"]){
dojo._hasResource["bidi.DojoComboBoxBidi"]=true;

var LRO = String.fromCharCode(8237);
var RLO = String.fromCharCode(8238);

//in "bidi/DojoComboBoxBidi"
define(["dijit/form/ComboBox","dijit/form/ComboBoxMixin", "bidi/DojoTextBoxBidi"], function(){
//		templateString:dojo.cache("dijit.form","templates/ComboBox.html","<div class=\"dijit dijitReset dijitInlineTable dijitLeft\"\n\tid=\"widget_${id}\"\n\tdojoAttachEvent=\"onmousedown:_onArrowMouseDown,onmouseup:_onMouseUp,onmouseout:_onMouseOut\" dojoAttachPoint=\"comboNode\" waiRole=\"combobox\" tabIndex=\"-1\"\n\t><div style=\"overflow:hidden;\"\n\t\t><div class='dijitReset dijitRight dijitButtonNode dijitArrowButton dijitDownArrowButton dijitArrowButtonContainer'\n\t\t\tdojoAttachPoint=\"downArrowNode\" waiRole=\"presentation\"\n\t\t\tdojoAttachEvent=\"onmousedown:_onArrowMouseDown,onmouseup:_onMouseUp,onmouseout:_onMouseOut\"\n\t\t\t><div class=\"dijitArrowButtonInner\" style=\"background:url('" + dojo.moduleUrl('dijit/themes/tundra/images', 'spriteArrows.png') + "') no-repeat scroll 0px center; width: 7px;\">&thinsp;</div\n\t\t\t><div class=\"dijitArrowButtonChar\">&#9660;</div\n\t\t></div\n\t\t><div class=\"dijitReset dijitInputField\"\n\t\t\t><input ${nameAttrSetting} type=\"DojoTextBoxBidi\" autocomplete=\"off\" class='dijitReset'\n\t\t\tdojoAttachEvent=\"onkeypress:_onKeyPress\"\n\t\t\tdojoAttachPoint=\"textbox,focusNode\" waiRole=\"textbox\" waiState=\"haspopup-true,autocomplete-list\"\n\t\t/></div\n\t></div\n></div>\n"),		

        postCreate: function(){
        	if(this.isVisualMode) {		
	            var options = this.srcNodeRef;            
	            for (var k = 0; k < options.length; k++) {
	                if(options[k].tagName == "OPTION") {
	                    var text = (dojo.isIE) ? options[k].innerText : options[k].textContent;
	                    if (this.dir == "rtl"){
	                    	text = this.reverseText(text);
	                    }

	                    if(dojo.isIE)
	                        options[k].innerText = ((this.dir == "rtl") ? RLO : LRO) + text;
	                    else
	                    	options[k].innerText = text;
	                }
	            }
            } 
            if (!dojo.isIE) {
            	var selector = "#" + this.id + "_popup li.dijitReset.dijitMenuItem";
               	var ss = document.styleSheets[0];
               	var qRules = ss.cssRules.length;
               	var pos = -1;
               	for (var i=ss.cssRules.length-1; i>=0; i--) {
               		if (ss.cssRules[i].selectorText && ss.cssRules[i].selectorText.toLowerCase().indexOf(selector.toLowerCase()) >=0) {
               		   pos = i;
               		   break;
               		}	
               	}	
               	if (this.isVisualMode && pos < 0)
               	   ss.insertRule(selector + " { unicode-bidi: bidi-override;}",qRules);
               	else if(!this.isVisualMode && pos >=0)
               	   ss.deleteRule(pos);
            }            
            this.inherited(arguments);                 
        },

        _onKey: function(evt) {
          dijit.form.ComboBoxMixin.prototype._onKey.call(this,evt);
           if (evt.charOrCode == dojo.keys.ENTER)
               this.processComboSelection(evt);	
           else //shensis
        	   var self = this;
        	   require(["dojo/_base/xhr", "dojo/_base/lang"], function(xhr, lang){
        		   lang.hitch(self, "_onKeyPressBidi");
        	   });
        },
        	                	      	        
        displayMessage: function(message) {
        },

        validate: function(isFocused){
        	return true;
        },
        
        _selectOption: function(/*Event*/ evt){
        	this.inherited(arguments);
        	this.processComboSelection(evt);
        },
        
        processComboSelection: function(evt) {
        	if(this.isVisualMode && dojo.isIE) { 
            	this._onFocus(evt);        		
				var selection = this.getCaretPos(evt,this.focusNode);
				if (selection) {
				    selectionStart = Math.min(selection[0],selection[1]);
				    curPos = Math.max(selection[0],selection[1]);
				}
				this.setCaretPositions(selectionStart, curPos);
        	}        	
        }
});
}
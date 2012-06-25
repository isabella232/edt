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
egl.defineClass('org.eclipse.edt.rui.mvc', 'InternalFormattingUtil',
{
	"decimalValueIsValid" : function(value, length, decimals) {
		value = egl.unboxAny(value);
		if (value instanceof egl.javascript.BigDecimal) {
			var limit = "";
			for (var len = length; len > 0; len--) {
				limit += "9";
			}
			if (decimals > 0) {
				limit = limit.substring(0, length - decimals) + '.' + limit.substring(length - decimals);
			}
			limit = new egl.javascript.BigDecimal(limit);
			
			if (value.scale() > decimals ) {
				value = value.setScale(decimals, egl.javascript.BigDecimal.prototype.ROUND_DOWN);
			}
			
			if (value.compareTo(limit) > 0 || value.compareTo(limit.negate()) < 0) {
				return false;
			}
		}
		return true;
	},
	"getCurrencySymbol" : function() {
		return egl.getCurrencySymbol();
	},
	"getDecimalSeparator" : function() {
		return egl.getDecimalSymbol();
	},
	"getGroupingSeparator" : function() {
		return egl.getSeparatorSymbol();
	},
	"asDate" : function(inputValue, mask, strict){
		if (inputValue == null || mask == null) {
			return false;
		}
		return egl.stringToDateInternal(inputValue, mask, strict);		
	},
	"asTime" : function(inputValue, mask, strict){
		if (inputValue == null || mask == null) {
			return false;
		}
		return egl.stringToTimeInternal(inputValue, mask, strict);		
	},
	"asTimestamp" : function(inputValue, mask, strict){
		if (inputValue == null || mask == null) {
			return false;
		}
		return egl.stringToTimeStampInternal(inputValue, mask, strict);		
	}
});

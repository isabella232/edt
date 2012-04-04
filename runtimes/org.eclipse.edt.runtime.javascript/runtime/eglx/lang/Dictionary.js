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


egl.createDictionary = function( c, o ) {
	return new egl.eglx.lang.EDictionary( c, o );
};

egl.defineClass("eglx.lang", "EDictionary", {
	"constructor" : function() {
		this.eze$$caseSensitive = arguments[0] || false;
		this.eze$$byKeyOrdering = arguments[1] || egl.eglx.lang.OrderingKind.none;
		this.eze$$typename = "EDictionary";
		this.eze$$signature = "y;";
		this.toString = function() {
			return this.$text || "[EDictionary]";
		};
	},
	"containsKey" : function(key) {
		return egl.containsKey(this, key);
	},
	"getKeys" : function() {
		return egl.getKeys(this);
	},
	"getValues" : function() {
		return egl.getValues(this);
	},
	"insertAll" : function(src) {
		egl.insertAll(this, src);
	},
	"removeAll" : function(){
		egl.removeAll(this);
	},
	"removeElement" : function(key) {
		egl.removeElement(this, key);
	},
	"size" : function() {
		return egl.size(this);
	},
	"getCaseSensitive" : function(){
		return this.eze$$caseSensitive;
	},
	"getOrdering" : function(){
		return this.eze$$byKeyOrdering;
	},
	"clone" : function(){
		return egl.clone(this);
	},
	"eze$$clone" : function(){
		return egl.clone(this);
	}
});

egl.eglx.lang.EDictionary.lookup = function (dict, key) {
	return egl.valueByKey(egl.checkNull(dict), key);
};


/* EDictionary.set:  Note that it must be declared static in order to take
 * advantage of the auto-unboxing that egl.valueByKey does;  otherwise, 
 * access chains (such as referencing a field of a record in a dictionary)
 * won't work. 
 */ 
egl.eglx.lang.EDictionary.set = function(dict, key, boxedValue){
	 boxedValue = egl.boxAny(boxedValue);
	 return egl.valueByKey(egl.checkNull(dict), key, boxedValue.eze$$value, boxedValue.eze$$signature);
};

/* EDictionary.get:  Note that it must be declared static in order to take
 * advantage of the auto-unboxing that egl.valueByKey does;  otherwise, 
 * access chains (such as referencing a field of a record in a dictionary)
 * won't work. 
 */ 
egl.eglx.lang.EDictionary.get = function(dict, key){
	return egl.valueByKey(egl.checkNull(dict), key);
};

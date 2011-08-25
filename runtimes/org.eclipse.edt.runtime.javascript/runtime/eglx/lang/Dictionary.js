/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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
		this.eze$$byKeyOrdering = arguments[1] || false;
		this.eze$$typename = "EDictionary";
		this.eze$$signature = "y;";
		this.toString = function() {
			return this.$text || "[EDictionary]";
		};
	},
	"removeAll" : function(){
		for (f in this) {
			if(typeof this[f] !== "function" && f.indexOf("eze$$") != 0){
				delete this[f];
			}
		}
	}
});

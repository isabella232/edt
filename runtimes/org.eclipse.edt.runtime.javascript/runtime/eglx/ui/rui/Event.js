/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass( 'eglx.ui.rui', 'Event', {
	"constructor" : function()
	{
		this.x = 0;
		this.y = 0;
		this.screenX = 0;
		this.screenY = 0;
		this.clientX = 0;
		this.clientY = 0;
		this.pageX = 0;
		this.pageY = 0;
		this.ch = 0;
		this.widget = null;
		this.mouseButton = 0;
		this.altKey = false;
		this.ctrlKey = false;
		this.metaKey = false;
		this.shiftKey = false;
	},

	"getX" : function() { return this.x; },						
	"setX" : function( newX ) { this.x = newX; },						
	
	"getY" : function() { return this.y; },						
	"setY" : function( newY ) { this.y = newY; },						
	
	"stopPropagation" : function() {},
	"preventDefault" : function() {}
});

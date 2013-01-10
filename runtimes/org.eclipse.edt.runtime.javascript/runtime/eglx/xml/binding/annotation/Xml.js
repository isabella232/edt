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
egl.defineClass(
	    'eglx.xml.binding.annotation', 'FieldInfo',
{
		"constructor" : function( /*function*/ getterFunction, /*function*/ setterFunction, /*string*/ eglSignature, /*string*/ eglType, /*annotation[]*/ annotations ){
			this.getterFunction = getterFunction;
			this.setterFunction = setterFunction;
			this.eglSignature = eglSignature;
			this.eglType = eglType;
			this.annotations = annotations;
		}
});

egl.defineClass(
	    'eglx.xml.binding.annotation', 'XMLAttribute',
{
		"constructor" : function( /*string*/ name, /*string*/ namespace, /*boolean*/ required) {
			this.name = name;
			this.namespace = namespace;
			this.required = required;
		}
});

egl.defineClass(
	    'eglx.xml.binding.annotation', 'XMLValue',
{
		"constructor" : function( /*string*/ kind) {
			this.kind = kind;
		}
});

egl.defineClass(
	    'eglx.xml.binding.annotation', 'XMLElement',
{
		"constructor" : function( /*string*/ name, /*string*/ namespace, /*boolean*/ required, /*boolean*/ nillable) {
			this.name = name;
			this.namespace = namespace;
			this.nillable = nillable;
			this.required = required;
		}
});
egl.defineClass(
	    'eglx.xml.binding.annotation', 'XMLArray',
{
		"constructor" : function( /*string*/ wrapped, /*string[]*/ names) {
			this.wrapped = wrapped;
			this.names = names;
		}
});
egl.defineClass(
	    'eglx.xml.binding.annotation', 'XMLSchemaType',
{
		"constructor" : function( /*string*/ name, /*string*/namespace) {
			this.name = name;
			this.namespace = namespace;
		}
});
egl.defineClass(
	    'eglx.xml.binding.annotation', 'XMLRootElement',
{
		"constructor" : function( /*string*/ name, /*string[]*/ namespace, /*boolean*/ nillable) {
			  this.name = name;
			  this.namespace = namespace;
			  this.nillable = nillable;
		}
});
egl.defineClass(
	    'eglx.xml.binding.annotation', 'XMLStructure',
{
		"constructor" : function( /*enumeration*/ value) {
			//  choice, sequence, simpleContent, unordered
			  this.value = value;
		}
});

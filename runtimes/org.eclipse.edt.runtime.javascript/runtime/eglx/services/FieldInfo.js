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
	    'eglx.services', 'FieldInfo',
{
		"constructor" : function( /*function*/ getterFunction, /*function*/ setterFunction, /*string*/ eglSignature, /*string*/ eglType, /*XMLAnnotation[]*/ annotations ){
			this.getterFunction = getterFunction;
			this.setterFunction = setterFunction;
			this.eglSignature = eglSignature;
			this.eglType = eglType;
			this.annotations = annotations;
		}
});

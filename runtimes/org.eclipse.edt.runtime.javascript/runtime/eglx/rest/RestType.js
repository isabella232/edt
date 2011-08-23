/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
egl.defineClass('eglx.rest', "RestType", "egl.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.rest.RestType['TrueRest'] = new egl.eglx.rest.RestType(1);
egl.eglx.rest.RestType['EglRpc'] = new egl.eglx.rest.RestType(2);
egl.eglx.rest.RestType['EglDedicated'] = new egl.eglx.rest.RestType(3);
;

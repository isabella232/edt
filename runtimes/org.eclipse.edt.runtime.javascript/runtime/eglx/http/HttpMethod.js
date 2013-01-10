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
egl.defineClass('eglx.http', "HttpMethod", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.http.HttpMethod['_GET'] = new egl.eglx.http.HttpMethod(1);
egl.eglx.http.HttpMethod['POST'] = new egl.eglx.http.HttpMethod(2);
egl.eglx.http.HttpMethod['_DELETE'] = new egl.eglx.http.HttpMethod(3);
egl.eglx.http.HttpMethod['PUT'] = new egl.eglx.http.HttpMethod(4);
;

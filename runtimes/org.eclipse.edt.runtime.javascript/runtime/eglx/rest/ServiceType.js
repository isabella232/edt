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
egl.defineClass('eglx.rest', "ServiceType", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.rest.ServiceType['TrueRest'] = new egl.eglx.rest.ServiceType(1);
egl.eglx.rest.ServiceType['EglRpc'] = new egl.eglx.rest.ServiceType(2);
egl.eglx.rest.ServiceType['EglDedicated'] = new egl.eglx.rest.ServiceType(3);
;

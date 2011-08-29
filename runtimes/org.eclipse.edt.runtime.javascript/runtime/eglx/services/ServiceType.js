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
egl.defineClass('eglx.services', "ServiceType", "egl.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.services.ServiceType['TrueRest'] = new egl.eglx.services.ServiceType(1);
egl.eglx.services.ServiceType['EglRpc'] = new egl.eglx.services.ServiceType(2);
egl.eglx.services.ServiceType['EglDedicated'] = new egl.eglx.services.ServiceType(3);
egl.eglx.services.ServiceType['SOAP'] = new egl.eglx.services.ServiceType(4);
;

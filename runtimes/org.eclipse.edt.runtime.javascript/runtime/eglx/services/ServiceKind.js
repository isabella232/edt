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
egl.defineClass('eglx.services', "ServiceKind", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.services.ServiceKind['EGL'] = new egl.eglx.services.ServiceKind(1);
egl.eglx.services.ServiceKind['WEB'] = new egl.eglx.services.ServiceKind(2);
egl.eglx.services.ServiceKind['NATIVE'] = new egl.eglx.services.ServiceKind(3);
egl.eglx.services.ServiceKind['REST'] = new egl.eglx.services.ServiceKind(4);
;

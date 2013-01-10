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
egl.defineClass('eglx.ui.rui', "PurposeKind", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.ui.rui.PurposeKind['FOR_DISPLAY'] = new egl.eglx.ui.rui.PurposeKind(1);
egl.eglx.ui.rui.PurposeKind['FOR_CREATE'] = new egl.eglx.ui.rui.PurposeKind(2);
egl.eglx.ui.rui.PurposeKind['FOR_UPDATE'] = new egl.eglx.ui.rui.PurposeKind(3);
;

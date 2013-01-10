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
egl.defineClass('eglx.ui', "SignKind", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.ui.SignKind['leading'] = new egl.eglx.ui.SignKind(1);
egl.eglx.ui.SignKind['none'] = new egl.eglx.ui.SignKind(2);
egl.eglx.ui.SignKind['parens'] = new egl.eglx.ui.SignKind(3);
egl.eglx.ui.SignKind['trailing'] = new egl.eglx.ui.SignKind(4);
;

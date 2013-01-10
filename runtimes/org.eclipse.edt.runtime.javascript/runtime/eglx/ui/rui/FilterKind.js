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
egl.defineClass('eglx.ui.rui', "FilterKind", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.ui.rui.FilterKind['excludeAll'] = new egl.eglx.ui.rui.FilterKind(1);
egl.eglx.ui.rui.FilterKind['excludeAllExcept'] = new egl.eglx.ui.rui.FilterKind(2);
egl.eglx.ui.rui.FilterKind['includeAll'] = new egl.eglx.ui.rui.FilterKind(3);
egl.eglx.ui.rui.FilterKind['includeAllExcept'] = new egl.eglx.ui.rui.FilterKind(4);
;

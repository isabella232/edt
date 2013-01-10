/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass('org.eclipse.edt.eunit.runtime', "targetLangKind", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.org.eclipse.edt.eunit.runtime.targetLangKind['JAVA'] = new egl.org.eclipse.edt.eunit.runtime.targetLangKind(1);
egl.org.eclipse.edt.eunit.runtime.targetLangKind['JAVASCRIPT'] = new egl.org.eclipse.edt.eunit.runtime.targetLangKind(2);
;

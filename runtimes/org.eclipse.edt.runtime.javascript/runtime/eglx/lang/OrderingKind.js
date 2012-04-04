/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

egl.defineClass('eglx.lang', "OrderingKind", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.lang.OrderingKind['byKey'] = new egl.eglx.lang.OrderingKind(1);
egl.eglx.lang.OrderingKind['byInsertion'] = new egl.eglx.lang.OrderingKind(2);
egl.eglx.lang.OrderingKind['none'] = new egl.eglx.lang.OrderingKind(3);

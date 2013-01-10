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
egl.defineClass('eglx.xml.binding.annotation', "XMLStructureKind", "eglx.lang", "Enumeration",{
		"constructor": function(valueIn) {
			this.value = valueIn;
		}
	}
);
egl.eglx.xml.binding.annotation.XMLStructureKind['choice'] = new egl.eglx.xml.binding.annotation.XMLStructureKind(1);
egl.eglx.xml.binding.annotation.XMLStructureKind['sequence'] = new egl.eglx.xml.binding.annotation.XMLStructureKind(2);
egl.eglx.xml.binding.annotation.XMLStructureKind['simpleContent'] = new egl.eglx.xml.binding.annotation.XMLStructureKind(3);
egl.eglx.xml.binding.annotation.XMLStructureKind['unordered'] = new egl.eglx.xml.binding.annotation.XMLStructureKind(4);
;

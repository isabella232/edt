/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass(
	'eglx.services', 'ServiceLib',
{
	"constructor" : function() {				
	}
});
egl.eglx.services.ServiceLib["completeBind"] = function(serviceOrInterface, resource) {
	serviceOrInterface.ezeCopy(egl.eglx.lang.EAny.unbox(resource));
	return egl.eglx.lang.EAny.unbox(resource);
};

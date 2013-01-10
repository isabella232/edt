/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.edt.ide.core.model.IPart;

/**
 * @author svihovec
 *
 */
public class EGLPublicPartRequestor extends EGLElementRequestor {

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.model.internal.core.IEGLElementRequestor#acceptPart(com.ibm.etools.egl.model.core.IPart)
	 */
	public void acceptPart(IPart type) {
		if(type.isPublic())
		{
			super.acceptPart(type);
		}
	}
}

/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.jtopen.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.filters.AbstractTypeFilter;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * Filters out com.ibm.as400.* classes.
 */
public class JTOpenFilter extends AbstractTypeFilter {
	
	@Override
	public boolean filter(IJavaStackFrame frame, IEGLJavaDebugTarget target) {
		try {
			return frame.getDeclaringTypeName().startsWith("com.ibm.as400."); //$NON-NLS-1$
		}
		catch (DebugException de) {
			return false;
		}
	}
}

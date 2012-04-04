/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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

//import com.ibm.etools.egl.internal.model.core.IField;
//import com.ibm.etools.egl.internal.model.core.IInitializer;
//import com.ibm.etools.egl.internal.model.core.IMethod;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;

/**
 * The SinglePartRequestor is an IEGLElementRequestor that 
 * only accepts one result element and then cancels.
 */
/* package */ class SinglePartRequestor implements IEGLElementRequestor {
	/**
	 * The single accepted element
	 */
	protected IPart fElement= null;
/**
 * @see IEGLElementRequestor
 */
public void acceptMemberPart(IPart type) {
	fElement= type;
}
/**
 * @see IEGLElementRequestor
 */
public void acceptPackageFragment(IPackageFragment packageFragment) {
}
/**
 * @see IEGLElementRequestor
 */
public void acceptPart(IPart type) {
	fElement= type;
}
/**
 * Returns the type accepted by this requestor, or <code>null</code>
 * if no type has been accepted.
 */
public IPart getPart() {
	return fElement;
}
/**
 * @see IEGLElementRequestor
 */
public boolean isCanceled() {
	return fElement != null;
}
/**
 * Reset the state of this requestor
 */
public void reset() {
	fElement= null;
}
}

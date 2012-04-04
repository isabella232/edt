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
 * This interface is used by IRequestorNameLookup. As results
 * are found by IRequestorNameLookup, they are reported to this
 * interface. An IEGLElementRequestor is able to cancel
 * at any time (that is, stop receiving results), by responding
 * <code>true</code> to <code>#isCancelled</code>.
 */
public interface IEGLElementRequestor {
//public void acceptField(IField field);
//public void acceptInitializer(IInitializer initializer);
//public void acceptMemberType(IType type);
//public void acceptMethod(IMethod method);
public void acceptPackageFragment(IPackageFragment packageFragment);
public void acceptPart(IPart part);
/**
 * Returns <code>true</code> if this IEGLElementRequestor does
 * not want to receive any more results.
 */
boolean isCanceled();
}

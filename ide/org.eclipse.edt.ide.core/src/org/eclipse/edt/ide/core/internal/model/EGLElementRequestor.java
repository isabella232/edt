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

import java.util.ArrayList;

import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;


/**
 * @see IEGLElementRequestor
 */

public class EGLElementRequestor implements IEGLElementRequestor {
	/**
	 * True if this requestor no longer wants to receive
	 * results from its <code>IRequestorNameLookup</code>.
	 */
	protected boolean fCanceled= false;
	
	/**
	 * A collection of the resulting fields, or <code>null</code>
	 * if no field results have been received.
	 */
	protected ArrayList fFields= null;

	/**
	 * A collection of the resulting initializers, or <code>null</code>
	 * if no initializer results have been received.
	 */
	protected ArrayList fInitializers= null;

	/**
	 * A collection of the resulting member types, or <code>null</code>
	 * if no member type results have been received.
	 */
	protected ArrayList fMemberParts= null;

	/**
	 * A collection of the resulting methods, or <code>null</code>
	 * if no method results have been received.
	 */
	protected ArrayList fMethods= null;

	/**
	 * A collection of the resulting package fragments, or <code>null</code>
	 * if no package fragment results have been received.
	 */
	protected ArrayList fPackageFragments= null;

	/**
	 * A collection of the resulting types, or <code>null</code>
	 * if no type results have been received.
	 */
	protected ArrayList fParts= null;

	/**
	 * Empty arrays used for efficiency
	 */
//	protected static IField[] fgEmptyFieldArray= new IField[0];
//	protected static IInitializer[] fgEmptyInitializerArray= new IInitializer[0];
	protected static IPart[] fgEmptyPartArray= new IPart[0];
	protected static IPackageFragment[] fgEmptyPackageFragmentArray= new IPackageFragment[0];
//	protected static IMethod[] fgEmptyMethodArray= new IMethod[0];
/**
 * @see IEGLElementRequestor
 */
public void acceptMemberPart(IPart type) {
	if (fMemberParts == null) {
		fMemberParts= new ArrayList();
	}
	fMemberParts.add(type);
}
/**
 * @see IEGLElementRequestor
 */
public void acceptPackageFragment(IPackageFragment packageFragment) {
	if (fPackageFragments== null) {
		fPackageFragments= new ArrayList();
	}
	fPackageFragments.add(packageFragment);
}
/**
 * @see IEGLElementRequestor
 */
public void acceptPart(IPart type) {
	if (fParts == null) {
		fParts= new ArrayList();
	}
	fParts.add(type);
}
/**
 * @see IEGLElementRequestor
 */
public IPart[] getMemberParts() {
	if (fMemberParts == null) {
		return fgEmptyPartArray;
	}
	int size = fMemberParts.size();
	IPart[] results = new IPart[size];
	fMemberParts.toArray(results);
	return results;
}
/**
 * @see IEGLElementRequestor
 */
public IPackageFragment[] getPackageFragments() {
	if (fPackageFragments== null) {
		return fgEmptyPackageFragmentArray;
	}
	int size = fPackageFragments.size();
	IPackageFragment[] results = new IPackageFragment[size];
	fPackageFragments.toArray(results);
	return results;
}
/**
 * @see IEGLElementRequestor
 */
public IPart[] getParts() {
	if (fParts== null) {
		return fgEmptyPartArray;
	}
	int size = fParts.size();
	IPart[] results = new IPart[size];
	fParts.toArray(results);
	return results;
}
/**
 * @see IEGLElementRequestor
 */
public boolean isCanceled() {
	return fCanceled;
}
/**
 * Reset the state of this requestor.
 */
public void reset() {
	fCanceled = false;
	fFields = null;
	fInitializers = null;
	fMemberParts = null;
	fMethods = null;
	fPackageFragments = null;
	fParts = null;
}
/**
 * Sets the #isCanceled state of this requestor to true or false.
 */
public void setCanceled(boolean b) {
	fCanceled= b;
}
}

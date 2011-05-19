/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.bde.target;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * Resolved bundle implementation.
 * 
 * @since 3.5
 */
public class ResolvedBundle implements IResolvedBundle {

	private BinaryProjectInfo fInfo;
	private IBundleContainer fContainer;
	private IStatus fStatus;

	/**
	 * Constructs a resolved bundle 
	 * 
	 * @param info underlying bundle
	 * @param status any status regarding the bundle or <code>null</code> if OK
	 * @param source <code>null</code> if this is an executable bundle.  To create a source bundle, this must be the bundle that this bundle will provide source for
	 */
	ResolvedBundle(BinaryProjectInfo info, IBundleContainer parentContainer, IStatus status) {
		fInfo = info;
		fContainer = parentContainer;
		if (status == null) {
			fStatus = Status.OK_STATUS;
		} else {
			fStatus = status;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IResolvedBundle#getBundleInfo()
	 */
	public BinaryProjectInfo getBundleInfo() {
		return fInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IResolvedBundle#getParentContainer()
	 */
	public IBundleContainer getParentContainer() {
		return fContainer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IResolvedBundle#setParentContainer(org.eclipse.pde.internal.core.target.provisional.IBundleContainer)
	 */
	public void setParentContainer(IBundleContainer newParent) {
		fContainer = newParent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IResolvedBundle#getStatus()
	 */
	public IStatus getStatus() {
		return fStatus;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IResolvedBundle#isSourceBundle()
	 */
	public boolean isSourceBundle() {
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer().append(fInfo.toString());
		if (fStatus != null && !fStatus.isOK()) {
			result = result.append(' ').append(fStatus.toString());
		}
		return result.toString();
	}
}


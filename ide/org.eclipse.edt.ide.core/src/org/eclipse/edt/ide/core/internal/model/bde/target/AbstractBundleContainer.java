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


import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.edt.ide.core.internal.model.util.Util;

/**
 * Common function for bundle containers.
 * 
 * @since 3.5
 */
public abstract class AbstractBundleContainer implements IBundleContainer {

	/**
	 * Resolved bundles or <code>null</code> if unresolved
	 */
	private IResolvedBundle[] fBundles;

	/**
	 * Status generated when this container was resolved, possibly <code>null</code>
	 */
	private IStatus fResolutionStatus;

	/**
	 * Resolves any string substitution variables in the given text returning
	 * the result.
	 * 
	 * @param text text to resolve
	 * @return result of the resolution
	 * @throws CoreException if unable to resolve 
	 */
	protected String resolveVariables(String text) throws CoreException {
		return text;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IBundleContainer#isResolved()
	 */
	public final boolean isResolved() {
		return fResolutionStatus != null && fResolutionStatus.getSeverity() != IStatus.CANCEL;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IBundleContainer#resolve(org.eclipse.pde.internal.core.target.provisional.ITargetDefinition, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final IStatus resolve(ITargetDefinition definition, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 150);
		try {
			fBundles = resolveBundles(definition, subMonitor.newChild(100));
			fResolutionStatus = Status.OK_STATUS;
			if (subMonitor.isCanceled()) {
				fBundles = null;
				fResolutionStatus = Status.CANCEL_STATUS;
			}
		} catch (CoreException e) {
			fBundles = new IResolvedBundle[0];
			fResolutionStatus = e.getStatus();
		} finally {
			subMonitor.done();
			if (monitor != null) {
				monitor.done();
			}
		}
		return fResolutionStatus;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IBundleContainer#getStatus()
	 */
	public IStatus getStatus() {
		if (!isResolved()) {
			return null;
		}
		return fResolutionStatus;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.IBundleContainer#getBundles()
	 */
	public final IResolvedBundle[] getBundles() {
		if (isResolved()) {
			return fBundles;
		}
		return null;
	}

	/**
	 * Resolves all source and executable bundles in this container
	 * <p>
	 * Subclasses must implement this method.
	 * </p><p>
	 * <code>beginTask()</code> and <code>done()</code> will be called on the given monitor by the caller. 
	 * </p>
	 * @param definition target context
	 * @param monitor progress monitor
	 * @return all source and executable bundles in this container
	 * @throws CoreException if an error occurs
	 */
	protected abstract IResolvedBundle[] resolveBundles(ITargetDefinition definition, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns a string that identifies the type of bundle container.  This type is persisted to xml
	 * so that the correct bundle container is created when deserializing the xml.  This type is also
	 * used to alter how the containers are presented to the user in the UI.
	 * 
	 * @return string identifier for the type of bundle container.
	 */
	public abstract String getType();

	/**
	 * Returns a path in the local file system to the root of the bundle container.
	 * <p>
	 * TODO: Ideally we won't need this method. Currently the PDE target platform preferences are
	 * based on a home location and additional locations, so we need the information.
	 * </p>
	 * @param resolve whether to resolve variables in the path
	 * @return home location
	 * @exception CoreException if unable to resolve the location
	 */
	public abstract String getLocation(boolean resolve) throws CoreException;

	/**
	 * Returns whether this container has equivalent bundle content to the given container
	 * 
	 * @param container bundle container
	 * @return whether content is equivalent
	 */
	public abstract boolean isContentEqual(AbstractBundleContainer container);

	protected IResolvedBundle resolveBundle(BinaryProjectInfo info, boolean isSource) {
		File file = null;
		try {
			file = new File(info.getLocation());
			return generateBundle(file);
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a resolved bundle for the given file or <code>null</code> if none.
	 * <p>
	 * Clients of this method must call 
	 * </p>
	 * @param file root jar or folder that contains a bundle
	 * @return resolved bundle or <code>null</code>
	 * @exception CoreException if not a valid bundle
	 */
	protected IResolvedBundle generateBundle(File file) throws CoreException {
		
		if(Util.isBinaryProject(file)) {
			BinaryProjectInfo info = new BinaryProjectInfo();
			info.setSymbolicName(file.getName());
			info.setLocation(file.toURI());
			ResolvedBundle bundle = new ResolvedBundle(info, this, null);
			return bundle;
		} else {
			return null;
		}
	}
}

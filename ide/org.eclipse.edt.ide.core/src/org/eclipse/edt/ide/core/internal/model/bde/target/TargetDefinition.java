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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
//import org.eclipse.equinox.frameworkadmin.BundleInfo;
import org.eclipse.osgi.util.NLS;
import org.xml.sax.SAXException;

import org.eclipse.edt.ide.core.model.EGLCore;
/**
 * Target definition implementation.
 * 
 */
public class TargetDefinition implements ITargetDefinition {

	// name and description
	private String fName;

	// included and optional filtering
	private NameVersionDescriptor[] fIncluded;
	private NameVersionDescriptor[] fOptional;

	// bundle containers
	private IBundleContainer[] fContainers;

	// handle
	private ITargetHandle fHandle;

	// implicit dependencies
	private NameVersionDescriptor[] fImplicit;

	// internal settings for UI mode (how content is displayed to the user
	private int fUIMode = MODE_PLUGIN;
	public static final int MODE_PLUGIN = 0;


	private IResolvedBundle[] fOtherBundles;

	/**
	 * Constructs a target definition based on the given handle. 
	 */
	TargetDefinition(ITargetHandle handle) {
		fHandle = handle;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getBundleContainers()
	 */
	public IBundleContainer[] getBundleContainers() {
		return fContainers;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getName()
	 */
	public String getName() {
		return fName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#setName(java.lang.String)
	 */
	public void setName(String name) {
		fName = name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#setBundleContainers(org.eclipse.pde.internal.core.target.provisional.IBundleContainer[])
	 */
	public void setBundleContainers(IBundleContainer[] containers) {
		// Clear the feature model cache as it is based on the bundle container locations
		fOtherBundles = null;

		if (containers != null && containers.length == 0) {
			containers = null;
		}
		fContainers = containers;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#resolve(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus resolve(IProgressMonitor monitor) {
		IBundleContainer[] containers = getBundleContainers();
		int num = 0;
		if (containers != null) {
			num = containers.length;
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.TargetDefinition_1, num * 10);
		try {
			MultiStatus status = new MultiStatus(EGLCore.PLUGIN_ID, 0, Messages.TargetDefinition_2, null);
			if (containers != null) {
				for (int i = 0; i < containers.length; i++) {
					if (subMonitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					subMonitor.subTask(Messages.TargetDefinition_4);
					IStatus s = containers[i].resolve(this, subMonitor.newChild(10));
					if (!s.isOK()) {
						status.add(s);
					}
				}
			}
			if (status.isOK()) {
				return Status.OK_STATUS;
			}
			return status;
		} finally {
			subMonitor.done();
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#isResolved()
	 */
	public boolean isResolved() {
		IBundleContainer[] containers = getBundleContainers();
		if (containers != null) {
			for (int i = 0; i < containers.length; i++) {
				IBundleContainer container = containers[i];
				if (!container.isResolved()) {
					return false;
				}
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getBundleStatus()
	 */
	public IStatus getBundleStatus() {
		if (isResolved()) {
			IBundleContainer[] containers = getBundleContainers();
			if (containers != null) {
				// Check if the containers have any resolution problems
				MultiStatus result = new MultiStatus(EGLCore.PLUGIN_ID, 0, Messages.TargetDefinition_5, null);
				for (int i = 0; i < containers.length; i++) {
					IBundleContainer container = containers[i];
					IStatus containerStatus = container.getStatus();
					if (containerStatus != null && !containerStatus.isOK()) {
						result.add(containerStatus);
					}
				}

				// Check if any of the included bundles have problems
				// build status from bundle list
				IResolvedBundle[] bundles = getBundles();
				for (int i = 0; i < bundles.length; i++) {
					if (!bundles[i].getStatus().isOK()) {
						result.add(bundles[i].getStatus());
					}
				}

				if (result.isOK()) {
					// Return generic ok status instead of problem multi-status with no children
					return Status.OK_STATUS;
				}
				return result;
			}
			return Status.OK_STATUS;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#setIncluded(org.eclipse.pde.internal.core.target.provisional.NameVersionDescriptor[])
	 */
	public void setIncluded(NameVersionDescriptor[] included) {
		fIncluded = included;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getIncluded()
	 */
	public NameVersionDescriptor[] getIncluded() {
		return fIncluded;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#setOptional(org.eclipse.pde.internal.core.target.provisional.NameVersionDescriptor[])
	 */
	public void setOptional(NameVersionDescriptor[] optional) {
		fOptional = optional;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getOptional()
	 */
	public NameVersionDescriptor[] getOptional() {
		return fOptional;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getBundles()
	 */
	public IResolvedBundle[] getBundles() {
		return getBundles(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getAllBundles()
	 */
	public IResolvedBundle[] getAllBundles() {
		return getBundles(true);
	}

	/**
	 * Gathers and returns all or included bundles in this target or <code>null</code> if
	 * not resolved.
	 * 
	 * @param allBundles whether to consider all bundles, or just those included/optional
	 * @return bundles or <code>null</code>
	 */
	private IResolvedBundle[] getBundles(boolean allBundles) {
		if (isResolved()) {
			IBundleContainer[] containers = getBundleContainers();
			if (containers != null) {
				List all = new ArrayList();
				for (int i = 0; i < containers.length; i++) {
					IBundleContainer container = containers[i];
					IResolvedBundle[] bundles = container.getBundles();
					for (int j = 0; j < bundles.length; j++) {
						IResolvedBundle rb = bundles[j];
						all.add(rb);
					}
				}

				IResolvedBundle[] allResolvedBundles = (IResolvedBundle[]) all.toArray(new IResolvedBundle[all.size()]);
				if (allBundles) {
					return allResolvedBundles;
				}
				return filterBundles(allResolvedBundles, getIncluded());
			}
			return new IResolvedBundle[0];
		}
		return null;
	}

	private IResolvedBundle[] filterBundles(IResolvedBundle[] bundles, NameVersionDescriptor[] filter) {
		if (filter == null) {
			// All bundles are included, but still need to check for optional bundles
			IBundleContainer parent = fContainers != null && fContainers.length > 0 ? fContainers[0] : null;
			List resolved = getMatchingBundles(bundles, null, fOptional, parent);
			return (IResolvedBundle[]) resolved.toArray(new IResolvedBundle[resolved.size()]);
		}
		if (filter.length == 0) {
			return new IResolvedBundle[0];
		}

		// If there are features, don't set errors for missing bundles as they are caused by missing OS specific fragments
		boolean containsFeatures = false;

		// If there are any included features that are missing, add errors as resolved bundles (the same thing we would do for missing bundles)
		List missingFeatures = new ArrayList();

		List included = new ArrayList();
		// For feature filters, get the list of included bundles, for bundle filters just add them to the list
		for (int i = 0; i < filter.length; i++) {
			if (filter[i].getType() == NameVersionDescriptor.TYPE_PLUGIN) {
				included.add(filter[i]);
			} 
		}

		// Return matching bundles
		IBundleContainer parent = fContainers != null && fContainers.length > 0 ? fContainers[0] : null;
		List result = getMatchingBundles(bundles, (NameVersionDescriptor[]) included.toArray(new NameVersionDescriptor[included.size()]), fOptional, containsFeatures ? null : parent);

		return (IResolvedBundle[]) result.toArray(new IResolvedBundle[result.size()]);
	}

	/**
	 * Returns bundles from the specified collection that match the symbolic names
	 * and/or version in the specified criteria. When no version is specified
	 * the newest version (if any) is selected.
	 * <p>
	 * If a parent error container is specified, bundles listed in the included and optional filters that
	 * are not found in the given collection will be added as IResolvedBundles with error statuses explaining
	 * the problem.  If no parent container is specified, missing included and optional bundles will be ignored.
	 * </p> 
	 * @param collection bundles to resolve against match criteria
	 * @param included bundles to include or <code>null</code> if no restrictions
	 * @param optional optional bundles or <code>null</code> of no optional bundles
	 * @param errorParentContainer 
	 * 
	 * @return list of IResolvedBundle bundles that match this container's restrictions
	 */
	static List getMatchingBundles(IResolvedBundle[] collection, NameVersionDescriptor[] included, NameVersionDescriptor[] optional, IBundleContainer errorParentContainer) {
		if (included == null && optional == null) {
			ArrayList result = new ArrayList();
			result.addAll(Arrays.asList(collection));
			return result;
		}
		// map bundles names to available versions
		Map bundleMap = new HashMap(collection.length);
		for (int i = 0; i < collection.length; i++) {
			IResolvedBundle resolved = collection[i];
			List list = (List) bundleMap.get(resolved.getBundleInfo().getSymbolicName());
			if (list == null) {
				list = new ArrayList(3);
				bundleMap.put(resolved.getBundleInfo().getSymbolicName(), list);
			}
			list.add(resolved);
		}
		List resolved = new ArrayList();
		if (included == null) {
			for (int i = 0; i < collection.length; i++) {
				resolved.add(collection[i]);
			}
		} else {
			for (int i = 0; i < included.length; i++) {
				BinaryProjectInfo info = new BinaryProjectInfo(included[i].getId(), included[i].getVersion(), null);
				IResolvedBundle bundle = resolveBundle(bundleMap, info, false, errorParentContainer);
				if (bundle != null) {
					resolved.add(bundle);
				}
			}
		}
		if (optional != null) {
			for (int i = 0; i < optional.length; i++) {
				BinaryProjectInfo option = new BinaryProjectInfo(optional[i].getId(), optional[i].getVersion(), null);
				IResolvedBundle resolveBundle = resolveBundle(bundleMap, option, true, errorParentContainer);
				if (resolveBundle != null) {
					IStatus status = resolveBundle.getStatus();
					if (status.isOK()) {
						// add to list if not there already
						if (!resolved.contains(resolveBundle)) {
							resolved.add(resolveBundle);
						}
					} else {
						// missing optional bundle - add it to the list
						resolved.add(resolveBundle);
					}
				}
			}
		}
		return resolved;
	}

	/**
	 * Resolves a bundle for the given info from the given map. The map contains
	 * keys of symbolic names and values are lists of {@link IResolvedBundle}'s available
	 * that match the names.
	 * <p>
	 * If an parent container for errors is provided, if a resolve bundle matching the requirements cannot be found
	 * a IResolvedBundle will be returned containing an status.  If no parent container is specified,
	 * missing bundles will result in a return value of <code>null</code>
	 * </p>
	 * 
	 * @param bundleMap available bundles to resolve against
	 * @param info name and version to match against
	 * @param optional whether the bundle is optional
	 * @param errorParentContainer bundle container the resolved bundle belongs too
	 * @return resolved bundle or <code>null</code>
	 */
	private static IResolvedBundle resolveBundle(Map bundleMap, BinaryProjectInfo info, boolean optional, IBundleContainer errorParentContainer) {
		List list = (List) bundleMap.get(info.getSymbolicName());
		if (list != null) {
			String version = info.getVersion();
//			if (version == null || version.equals(BundleInfo.EMPTY_VERSION)) {
//				// select newest
//				if (list.size() > 1) {
//					// sort the list
//					Collections.sort(list, new Comparator() {
//						public int compare(Object o1, Object o2) {
////							BundleInfo b1 = ((IResolvedBundle) o1).getBundleInfo();
////							BundleInfo b2 = ((IResolvedBundle) o2).getBundleInfo();
////							return b1.getVersion().compareTo(b2.getVersion());
//							return 0;
//						}
//					});
//				}
//				// select the last one
//				ResolvedBundle rb = (ResolvedBundle) list.get(list.size() - 1);
//				return rb;
//			}
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				IResolvedBundle bundle = (IResolvedBundle) iterator.next();
				if (bundle.getBundleInfo().getVersion().equals(version)) {
					return bundle;
				}
			}
			// VERSION DOES NOT EXIST
			if (errorParentContainer == null) {
				return null;
			}
			int sev = IStatus.ERROR;
			String message = NLS.bind(Messages.AbstractBundleContainer_1, new Object[] {info.getVersion(), info.getSymbolicName()});
			if (optional) {
				sev = IStatus.INFO;
				message = NLS.bind(Messages.AbstractBundleContainer_2, new Object[] {info.getVersion(), info.getSymbolicName()});
			}
			return new ResolvedBundle(info, errorParentContainer, new Status(sev, EGLCore.PLUGIN_ID, IResolvedBundle.STATUS_VERSION_DOES_NOT_EXIST, message, null));
		}
		// DOES NOT EXIST
		if (errorParentContainer == null) {
			return null;
		}
		int sev = IStatus.ERROR;
		String message = NLS.bind(Messages.AbstractBundleContainer_3, info.getSymbolicName());
		if (optional) {
			sev = IStatus.INFO;
			message = NLS.bind(Messages.AbstractBundleContainer_4, info.getSymbolicName());
		}
		return new ResolvedBundle(info, errorParentContainer, new Status(sev, EGLCore.PLUGIN_ID, IResolvedBundle.STATUS_DOES_NOT_EXIST, message, null));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getHandle()
	 */
	public ITargetHandle getHandle() {
		return fHandle;
	}

	/**
	 * Build contents from the given stream.
	 * 
	 * @param stream input stream
	 * @throws CoreException if an error occurs
	 */
	void setContents(InputStream stream) throws CoreException {
		try {
			fContainers = null;
			fImplicit = null;
			fName = null;
			TargetDefinitionPersistenceHelper.initFromXML(this, stream);
		} catch (ParserConfigurationException e) {
			abort(Messages.TargetDefinition_0, e);
		} catch (SAXException e) {
			abort(Messages.TargetDefinition_0, e);
		} catch (IOException e) {
			abort(Messages.TargetDefinition_0, e);
		}
	}

	/**
	 * Persists contents to the given stream.
	 * 
	 * @param stream output stream
	 * @throws CoreException if an error occurs
	 */
	void write(OutputStream stream) throws CoreException {
		try {
			TargetDefinitionPersistenceHelper.persistXML(this, stream);
		} catch (IOException e) {
			abort(Messages.TargetDefinition_3, e);
		} catch (ParserConfigurationException e) {
			abort(Messages.TargetDefinition_3, e);
		} catch (TransformerException e) {
			abort(Messages.TargetDefinition_3, e);
		}
	}

	/**
	 * Throws a core exception with the given message and underlying exception (possibly
	 * <code>null</code>).
	 * 
	 * @param message message
	 * @param e underlying cause of the exception or <code>null</code>
	 * @throws CoreException
	 */
	private void abort(String message, Exception e) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, message, e));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#getImplicitDependencies()
	 */
	public NameVersionDescriptor[] getImplicitDependencies() {
		return fImplicit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.target.provisional.ITargetDefinition#setImplicitDependencies(org.eclipse.pde.internal.core.target.provisional.NameVersionDescriptor[])
	 */
	public void setImplicitDependencies(NameVersionDescriptor[] bundles) {
		if (bundles != null && bundles.length == 0) {
			bundles = null;
		}
		fImplicit = bundles;
	}

	/**
	 * Returns whether the content of this definition is equal to the content of the specified definition.
	 * 
	 * @param definition
	 * @return whether the content of this definition is equal to the content of the specified definition
	 */
	public boolean isContentEqual(ITargetDefinition definition) {
		if (isNullOrEqual(getName(), definition.getName())) {
			// Check includes/optional
			if (isNullOrEqual(getIncluded(), definition.getIncluded()) && isNullOrEqual(getOptional(), definition.getOptional())) {
				// Check containers
				IBundleContainer[] c1 = getBundleContainers();
				IBundleContainer[] c2 = definition.getBundleContainers();
				if (areContainersEqual(c1, c2)) {
					// Check implicit dependencies
					return isNullOrEqual(getImplicitDependencies(), definition.getImplicitDependencies());
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether the content of this definition is equivalent to the content of the
	 * specified definition (excluding name/description).
	 * 
	 * @param definition
	 * @return whether the content of this definition is equivalent to the content of the
	 * specified definition
	 */
	public boolean isContentEquivalent(ITargetDefinition definition) {
		// Check includes/optional
		if (isNullOrEqual(getIncluded(), definition.getIncluded()) && isNullOrEqual(getOptional(), definition.getOptional())) {
			// Check containers
			IBundleContainer[] c1 = getBundleContainers();
			IBundleContainer[] c2 = definition.getBundleContainers();
			if (areContainersEqual(c1, c2)) {
				// Check implicit dependencies
				return isNullOrEqual(getImplicitDependencies(), definition.getImplicitDependencies());
			}
		}
		return false;
	}

	private boolean isNullOrEqual(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		if (o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	/**
	 * Returns whether the arrays have equal contents or are both <code>null</code>.
	 * 
	 * @param objects1
	 * @param objects2
	 * @return whether the arrays have equal contents or are both <code>null</code>
	 */
	private boolean isNullOrEqual(Object[] objects1, Object[] objects2) {
		if (objects1 == null) {
			return objects2 == null;
		}
		if (objects2 == null) {
			return false;
		}
		if (objects1.length == objects2.length) {
			for (int i = 0; i < objects1.length; i++) {
				if (!objects1[i].equals(objects2[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean areContainersEqual(IBundleContainer[] c1, IBundleContainer[] c2) {
		if (c1 == null) {
			return c2 == null;
		}
		if (c2 == null) {
			return false;
		}
		if (c1.length == c2.length) {
			for (int i = 0; i < c2.length; i++) {
				AbstractBundleContainer ac1 = (AbstractBundleContainer) c1[i];
				AbstractBundleContainer ac2 = (AbstractBundleContainer) c2[i];
				if (!ac1.isContentEqual(ac2)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(fName != null ? fName : "No Name"); //$NON-NLS-1$
		if (fContainers == null) {
			buf.append("\n\tNo containers"); //$NON-NLS-1$
		} else {
			for (int i = 0; i < fContainers.length; i++) {
				buf.append("\n\t").append(fContainers.toString()); //$NON-NLS-1$
			}
		}
		buf.append("\nImplicit: ").append(fImplicit == null ? "null" : Integer.toString(fImplicit.length)); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\nHandle: ").append(fHandle.toString()); //$NON-NLS-1$
		return buf.toString();
	}

	/**
	 * Returns the set of IResolvedBundle available in this target that are not part of any features, will return a cached copy if available
	 * 
	 * @see #getAllFeatures()
	 * @return set of resolved bundles available in this target that don't belong to any features, possibly empty
	 */
	public IResolvedBundle[] getOtherBundles() {
		if (!isResolved()) {
			return null;
		}

		if (fOtherBundles != null) {
			return fOtherBundles;
		}

		IResolvedBundle[] allBundles = getAllBundles();
		Map remaining = new HashMap();
		for (int i = 0; i < allBundles.length; i++) {
			remaining.put(allBundles[i].getBundleInfo().getSymbolicName(), allBundles[i]);
		}

		Collection values = remaining.values();
		fOtherBundles = (IResolvedBundle[]) values.toArray(new IResolvedBundle[values.size()]);
		return fOtherBundles;
	}

	/**
	 * Convenience method to return the set of IFeatureModels that are included in this
	 * target as well as any other included plug-ins as IResolvedBundles (that are not part 
	 * of the features). Also returns any bundles with error statuses.  Will return <code>null</code> 
	 * if this target has not been resolved.
	 * 
	 * @see #getAllFeatures()
	 * @see #getOtherBundles()
	 * @return set of IFeatureModels and IResolvedBundles or <code>null</code>
	 */
	public Set getFeaturesAndBundles() {
		if (!isResolved()) {
			return null;
		}

		IResolvedBundle[] allExtraBundles = getOtherBundles();

		NameVersionDescriptor[] included = getIncluded();
		NameVersionDescriptor[] optional = getOptional();

		if (included == null && optional == null) {
			Set result = new HashSet();
			result.addAll(Arrays.asList(allExtraBundles));
			return result;
		}

		Set result = new HashSet();
		for (int i = 0; i < included.length; i++) {
			if (included[i].getType() == NameVersionDescriptor.TYPE_PLUGIN) {
				for (int j = 0; j < allExtraBundles.length; j++) {
					if (allExtraBundles[j].getBundleInfo().getSymbolicName().equals(included[i].getId())) {
						result.add(allExtraBundles[j]);
					}
				}
			} 
		}

		if (optional != null) {
			for (int i = 0; i < optional.length; i++) {
				for (int j = 0; j < allExtraBundles.length; j++) {
					if (allExtraBundles[j].getBundleInfo().getSymbolicName().equals(optional[i].getId())) {
						result.add(allExtraBundles[j]);
					}
				}
			}
		}

		return result;
	}

	public int getUIMode() {
		return fUIMode;
	}

	public void setUIMode(int mode) {
		fUIMode = mode;
	}
}

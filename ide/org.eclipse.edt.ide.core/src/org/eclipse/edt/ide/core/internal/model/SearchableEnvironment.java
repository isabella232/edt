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


/**
 *	This class provides a <code>SearchableBuilderEnvironment</code> for code assist which
 *	uses the EGL model as a search tool.  
 */
public class SearchableEnvironment
//	implements ISearchableNameEnvironment, IEGLSearchConstants 
	{
//	protected NameLookup nameLookup;
//	protected com.ibm.etools.egl.internal.pgm.model.IEGLFile unitToSkip;
//
//	protected IEGLProject project;
//	protected IEGLSearchScope searchScope;
//
//	/**
//	 * Creates a SearchableEnvironment on the given project
//	 */
//	public SearchableEnvironment(IEGLProject project) throws EGLModelException {
//		this.project = project;
//		this.nameLookup = (NameLookup) ((EGLProject) project).getNameLookup();
//
//		// Create search scope with visible entry on the project's eglpath
//		this.searchScope = SearchEngine.createEGLSearchScope(this.project.getAllPackageFragmentRoots());
//	}
//
//	/**
//	 * Returns the given type in the the given package if it exists,
//	 * otherwise <code>null</code>.
//	 */
//	protected NameEnvironmentAnswer find(String typeName, String packageName) {
//		if (packageName == null)
//			packageName = IPackageFragment.DEFAULT_PACKAGE_NAME;
//		IPart type =
//			this.nameLookup.findPart(
//				typeName,
//				packageName,
//				false,
//				NameLookup.ACCEPT_PARTS);
//		if (type != null) {
//			try {
//				// retrieve the requested type
//				SourcePartElementInfo sourcePart = (SourcePartElementInfo)((SourcePart)type).getElementInfo();
//				IEGLPart topLevelType = sourcePart;
//				// find all siblings (other types declared in same unit, since may be used for name resolution)
//				IPart[] parts = sourcePart.getHandle().getEGLFile().getParts();
//				IEGLPart[] sourceParts = new IEGLPart[parts.length];
//
//				// in the resulting collection, ensure the requested type is the first one
//				sourceParts[0] = sourcePart;
//				for (int i = 0, index = 1; i < parts.length; i++) {
//					IEGLPart otherType =
//						(IEGLPart) ((EGLElement) parts[i]).getElementInfo();
//					if (!otherType.equals(topLevelType))
//						sourceParts[index++] = otherType;
//				}
//				return new NameEnvironmentAnswer(sourceParts);
//			} catch (EGLModelException npe) {
//				return null;
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * @see ISearchableNameEnvironment#findPackages(char[], ISearchRequestor)
//	 */
//	public void findPackages(char[] prefix, ISearchRequestor requestor) {
//		this.nameLookup.seekPackageFragments(
//			new String(prefix),
//			true,
//			new SearchableEnvironmentRequestor(requestor));
//	}
//
//	/**
//	 * @see INameEnvironment#findType(char[][])
//	 */
//	public NameEnvironmentAnswer findPart(char[][] compoundTypeName) {
//		if (compoundTypeName == null) return null;
//
//		int length = compoundTypeName.length;
//		if (length <= 1) {
//			if (length == 0) return null;
//			return find(new String(compoundTypeName[0]), null);
//		}
//
//		int lengthM1 = length - 1;
//		char[][] packageName = new char[lengthM1][];
//		System.arraycopy(compoundTypeName, 0, packageName, 0, lengthM1);
//
//		return find(
//			new String(compoundTypeName[lengthM1]),
//			CharOperation.toString(packageName));
//	}
//
//	/**
//	 * @see INameEnvironment#findType(char[], char[][])
//	 */
//	public NameEnvironmentAnswer findPart(char[] name, char[][] packageName) {
//		if (name == null) return null;
//
//		return find(
//			new String(name),
//			packageName == null || packageName.length == 0 ? null : CharOperation.toString(packageName));
//	}
//
//	/**
//	 * @see ISearchableNameEnvironment#findTypes(char[], ISearchRequestor)
//	 */
//	public void findParts(char[] prefix, final ISearchRequestor storage) {
//
//		/*
//			if (true){
//				findTypes(new String(prefix), storage, NameLookup.ACCEPT_CLASSES | NameLookup.ACCEPT_INTERFACES);
//				return;		
//			}
//		*/
//		try {
//			final String excludePath;
//			if (this.unitToSkip != null) {
//				if (!(this.unitToSkip instanceof IEGLElement)) {
//					// revert to model investigation
//					findParts(
//						new String(prefix),
//						storage,
//						NameLookup.ACCEPT_PARTS);
//					return;
//				}
//				excludePath = ((IEGLElement) this.unitToSkip).getPath().toString();
//			} else {
//				excludePath = null;
//			}
//			int lastDotIndex = CharOperation.lastIndexOf('.', prefix);
//			char[] qualification, simpleName;
//			if (lastDotIndex < 0) {
//				qualification = null;
//				simpleName = CharOperation.toLowerCase(prefix);
//			} else {
//				qualification = CharOperation.subarray(prefix, 0, lastDotIndex);
//				simpleName =
//					CharOperation.toLowerCase(
//						CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length));
//			}
//
//			IProgressMonitor progressMonitor = new IProgressMonitor() {
//				boolean isCanceled = false;
//				public void beginTask(String name, int totalWork) {
//				}
//				public void done() {
//				}
//				public void internalWorked(double work) {
//				}
//				public boolean isCanceled() {
//					return isCanceled;
//				}
//				public void setCanceled(boolean value) {
//					isCanceled = value;
//				}
//				public void setTaskName(String name) {
//				}
//				public void subTask(String name) {
//				}
//				public void worked(int work) {
//				}
//			};
//			IPartNameRequestor nameRequestor = new IPartNameRequestor() {
//				public void acceptPart(
//					char[] packageName,
//					char[] simpleTypeName,
//					char partType,
//					char[][] enclosingTypeNames,
//					String path) {
//					if (excludePath != null && excludePath.equals(path))
//						return;
//					if (enclosingTypeNames != null && enclosingTypeNames.length > 0)
//						return; // accept only top level types
//					storage.acceptPart(packageName, simpleTypeName, Flags.AccPublic);
//				}
//			};
//			try {
//				new SearchEngine().searchAllPartNames(
//					this.project.getProject().getWorkspace(),
//					qualification,
//					simpleName,
//					PREFIX_MATCH,
//					CASE_INSENSITIVE,
//					IEGLSearchConstants.PART,
//					this.searchScope,
//					nameRequestor,
//					CANCEL_IF_NOT_READY_TO_SEARCH,
//					progressMonitor);
//			} catch (OperationCanceledException e) {
//				findParts(
//					new String(prefix),
//					storage,
//					NameLookup.ACCEPT_PARTS);
//			}
//		} catch (EGLModelException e) {
//			findParts(
//				new String(prefix),
//				storage,
//				NameLookup.ACCEPT_PARTS);
//		}
//	}
//
//	/**
//	 * Returns all types whose name starts with the given (qualified) <code>prefix</code>.
//	 *
//	 * If the <code>prefix</code> is unqualified, all types whose simple name matches
//	 * the <code>prefix</code> are returned.
//	 */
//	private void findParts(String prefix, ISearchRequestor storage, int type) {
//		SearchableEnvironmentRequestor requestor =
//			new SearchableEnvironmentRequestor(storage, this.unitToSkip);
//		int index = prefix.lastIndexOf('.');
//		if (index == -1) {
//			this.nameLookup.seekParts(prefix, null, true, type, requestor);
//		} else {
//			String packageName = prefix.substring(0, index);
//			EGLElementRequestor elementRequestor = new EGLElementRequestor();
//			this.nameLookup.seekPackageFragments(packageName, false, elementRequestor);
//			IPackageFragment[] fragments = elementRequestor.getPackageFragments();
//			if (fragments != null) {
//				String className = prefix.substring(index + 1);
//				for (int i = 0, length = fragments.length; i < length; i++)
//					if (fragments[i] != null)
//						this.nameLookup.seekParts(className, fragments[i], true, type, requestor);
//			}
//		}
//	}
//
//	/**
//	 * @see INameEnvironment#isPackage(char[][], char[])
//	 */
//	public boolean isPackage(char[][] parentPackageName, char[] subPackageName) {
//		if (subPackageName == null || CharOperation.contains('.', subPackageName))
//			return false;
//		if (parentPackageName == null || parentPackageName.length == 0)
//			return isTopLevelPackage(subPackageName);
//		for (int i = 0, length = parentPackageName.length; i < length; i++)
//			if (parentPackageName[i] == null || CharOperation.contains('.', parentPackageName[i]))
//				return false;
//
//		String packageName = new String(CharOperation.concatWith(parentPackageName, subPackageName, '.'));
//		return this.nameLookup.findPackageFragments(packageName, false) != null;
//	}
//
//	public boolean isTopLevelPackage(char[] packageName) {
//		return packageName != null &&
//			!CharOperation.contains('.', packageName) &&
//			this.nameLookup.findPackageFragments(new String(packageName), false) != null;
//	}
//
//	/**
//	 * Returns a printable string for the array.
//	 */
//	protected String toStringChar(char[] name) {
//		return "["  //$NON-NLS-1$
//		+ new String(name) + "]" ; //$NON-NLS-1$
//	}
//
//	/**
//	 * Returns a printable string for the array.
//	 */
//	protected String toStringCharChar(char[][] names) {
//		StringBuffer result = new StringBuffer();
//		for (int i = 0; i < names.length; i++) {
//			result.append(toStringChar(names[i]));
//		}
//		return result.toString();
//	}
//	
//	public void cleanup() {
//	}
}

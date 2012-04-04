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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.model.util.PerThreadObject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

/**
 * A <code>NameLookup</code> provides name resolution within a EGL project.
 * The name lookup facility uses the project's classpath to prioritize the 
 * order in which package fragments are searched when resolving a name.
 *
 * <p>Name lookup only returns a handle when the named element actually
 * exists in the model; otherwise <code>null</code> is returned.
 *
 * <p>There are two logical sets of methods within this interface.  Methods
 * which start with <code>find*</code> are intended to be convenience methods for quickly
 * finding an element within another element; for instance, for finding a class within a
 * package.  The other set of methods all begin with <code>seek*</code>.  These methods
 * do comprehensive searches of the <code>IEGLProject</code> returning hits
 * in real time through an <code>IEGLElementRequestor</code>.
 *
 */
public class NameLookup {
	/**
	 * Accept flag for specifying classes.
	 */
	public static final int ACCEPT_EGLFILES = 0x00000002;

	/**
	 * Accept flag for specifying interfaces.
	 */
	public static final int ACCEPT_PARTS = 0x00000004;

	/*** The <code>IPackageFragmentRoot</code>'s associated
	 * with the classpath of this NameLookup facility's
	 * project.
	 */
	protected IPackageFragmentRoot[] fPackageFragmentRoots= null;

	/**
	 * Table that maps package names to lists of package fragments for
	 * all package fragments in the package fragment roots known
	 * by this name lookup facility. To allow > 1 package fragment
	 * with the same name, values are arrays of package fragments
	 * ordered as they appear on the classpath.
	 */
	protected Map fPackageFragments;

	/**
	 * The <code>IWorkspace</code> that this NameLookup
	 * is configure within.
	 */
	protected IWorkspace workspace;
	
	/**
	 * A map from compilation unit handles to units to look inside (compilation
	 * units or working copies).
	 * Allows working copies to take precedence over compilation units.
	 * The cache is a 2-level cache, first keyed by thread.
	 */
	protected PerThreadObject unitsToLookInside = new PerThreadObject();

	public NameLookup(IEGLProject project) throws EGLModelException {
		configureFromProject(project);
	}

	/**
	 * Returns true if:<ul>
	 *  <li>the given type is an existing part and the flag's <code>ACCEPT_PARTS</code>
	 *      bit is on
	 *  </ul>
	 * Otherwise, false is returned. 
	 */
	protected boolean acceptPart(IPart part, int acceptFlags) {
		if (acceptFlags == 0)
			return true; // no flags, always accepted
		else 
			return (acceptFlags & ACCEPT_PARTS) != 0;
	}

	/**
	 * Configures this <code>NameLookup</code> based on the
	 * info of the given <code>IEGLProject</code>.
	 *
	 * @throws EGLModelException if the <code>IEGLProject</code> has no classpath.
	 */
	private void configureFromProject(IEGLProject project) throws EGLModelException {
		workspace= ResourcesPlugin.getWorkspace();
		fPackageFragmentRoots= ((EGLProject) project).getAllPackageFragmentRoots();
		fPackageFragments= new HashMap();
		IPackageFragment[] frags = this.getPackageFragmentsInRoots(fPackageFragmentRoots, project);
		for (int i= 0; i < frags.length; i++) {
			IPackageFragment fragment= frags[i];
			IPackageFragment[] entry= (IPackageFragment[]) fPackageFragments.get(fragment.getElementName());
			if (entry == null) {
				entry= new IPackageFragment[1];
				entry[0]= fragment;
				fPackageFragments.put(fragment.getElementName(), entry);
			} else {
				IPackageFragment[] copy= new IPackageFragment[entry.length + 1];
				System.arraycopy(entry, 0, copy, 0, entry.length);
				copy[entry.length]= fragment;
				fPackageFragments.put(fragment.getElementName(), copy);
			}
		}
	}

	/**
	 * Finds every type in the project whose simple name matches
	 * the prefix, informing the requestor of each hit. The requestor
	 * is polled for cancellation at regular intervals.
	 *
	 * <p>The <code>partialMatch</code> argument indicates partial matches
	 * should be considered.
	 */
	private void findAllParts(String prefix, boolean partialMatch, int acceptFlags, IEGLElementRequestor requestor) {
		int count= fPackageFragmentRoots.length;
		for (int i= 0; i < count; i++) {
			if (requestor.isCanceled())
				return;
			IPackageFragmentRoot root= fPackageFragmentRoots[i];
			IEGLElement[] packages= null;
			try {
				packages= root.getChildren();
			} catch (EGLModelException npe) {
				continue; // the root is not present, continue;
			}
			if (packages != null) {
				for (int j= 0, packageCount= packages.length; j < packageCount; j++) {
					if (requestor.isCanceled())
						return;
					seekParts(prefix, (IPackageFragment) packages[j], partialMatch, acceptFlags, requestor);
				}
			}
		}
	}

	/**
	 * Returns the <code>ICompilationUnit</code> which defines the type
	 * named <code>qualifiedTypeName</code>, or <code>null</code> if
	 * none exists. The domain of the search is bounded by the classpath
	 * of the <code>IEGLProject</code> this <code>NameLookup</code> was
	 * obtained from.
	 * <p>
	 * The name must be fully qualified (eg "java.lang.Object", "java.util.Hashtable$Entry")
	 */
	public IEGLFile findEGLFile(String qualifiedPartName) {
		String pkgName= IPackageFragment.DEFAULT_PACKAGE_NAME;
		String cuName= qualifiedPartName;
		// TODO Must not use name of part to create file name - many parts to 1 file
		int index= qualifiedPartName.lastIndexOf('.');
		if (index != -1) {
			pkgName= qualifiedPartName.substring(0, index);
			cuName= qualifiedPartName.substring(index + 1);
		}
		// EGLTODO: This prob. can't be a $
		index= cuName.indexOf('$');
		if (index != -1) {
			cuName= cuName.substring(0, index);
		}
		cuName += ".egl"; //$NON-NLS-1$
		IPackageFragment[] frags= (IPackageFragment[]) fPackageFragments.get(pkgName);
		if (frags != null) {
			for (int i= 0; i < frags.length; i++) {
				IPackageFragment frag= frags[i];
				IEGLFile cu= frag.getEGLFile(cuName);
				if (cu != null && cu.exists()) {
					return cu;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the package fragment whose path matches the given
	 * (absolute) path, or <code>null</code> if none exist. The domain of
	 * the search is bounded by the classpath of the <code>IEGLProject</code>
	 * this <code>NameLookup</code> was obtained from.
	 * The path can be:
	 * 	- internal to the workbench: "/Project/src"
	 *  - external to the workbench: "c:/jdk/classes.zip/java/lang"
	 */
	public IPackageFragment findPackageFragment(IPath path) {
		if (!path.isAbsolute()) {
			throw new IllegalArgumentException(EGLModelResources.pathMustBeAbsolute);
		}
/*
 * this code should rather use the package fragment map to find the candidate package, then
 * check if the respective enclosing root maps to the one on this given IPath.
 */		
		IResource possibleFragment = workspace.getRoot().findMember(path);
		if (possibleFragment == null) {
			//external jar
			for (int i = 0; i < fPackageFragmentRoots.length; i++) {
				IPackageFragmentRoot root = fPackageFragmentRoots[i];
				if (!root.isExternal()) {
					continue;
				}
				IPath rootPath = root.getPath();
				int matchingCount = rootPath.matchingFirstSegments(path);
				if (matchingCount != 0) {
					String name = path.toOSString();
					// + 1 is for the File.separatorChar
					name = name.substring(rootPath.toOSString().length() + 1, name.length());
					name = name.replace(File.separatorChar, '.');
					IEGLElement[] list = null;
					try {
						list = root.getChildren();
					} catch (EGLModelException npe) {
						continue; // the package fragment root is not present;
					}
					int elementCount = list.length;
					for (int j = 0; j < elementCount; j++) {
						IPackageFragment packageFragment = (IPackageFragment) list[j];
						if (nameMatches(name, packageFragment, false)) {
							return packageFragment;
						}
					}
				}
			}
		} else {
			IEGLElement fromFactory = EGLCore.create(possibleFragment);
			if (fromFactory == null) {
				return null;
			}
			if (fromFactory instanceof IPackageFragment) {
				return (IPackageFragment) fromFactory;
			} else
				if (fromFactory instanceof IEGLProject) {
					// default package in a default root
					EGLProject project = (EGLProject) fromFactory;
					try {
						IEGLPathEntry entry = project.getEGLPathEntryFor(path);
						if (entry != null) {
							IPackageFragmentRoot root =
								project.getPackageFragmentRoot(project.getResource());
							IPackageFragment[] pkgs = (IPackageFragment[]) fPackageFragments.get(IPackageFragment.DEFAULT_PACKAGE_NAME);
							if (pkgs == null) {
								return null;
							}
							for (int i = 0; i < pkgs.length; i++) {
								if (pkgs[i].getParent().equals(root)) {
									return pkgs[i];
								}
							}
						}
					} catch (EGLModelException e) {
						return null;
					}
				}
		}
		return null;
	}

	/**
	 * Returns the package fragments whose name matches the given
	 * (qualified) name, or <code>null</code> if none exist.
	 *
	 * The name can be:
	 *	- empty: ""
	 *	- qualified: "pack.pack1.pack2"
	 * @param partialMatch partial name matches qualify when <code>true</code>,
	 *	only exact name matches qualify when <code>false</code>
	 */
	public IPackageFragment[] findPackageFragments(String name, boolean partialMatch) {
		int count= fPackageFragmentRoots.length;
		if (partialMatch) {
			name= name.toLowerCase();
			for (int i= 0; i < count; i++) {
				IPackageFragmentRoot root= fPackageFragmentRoots[i];
				IEGLElement[] list= null;
				try {
					list= root.getChildren();
				} catch (EGLModelException npe) {
					continue; // the package fragment root is not present;
				}
				int elementCount= list.length;
				IPackageFragment[] result = new IPackageFragment[elementCount];
				int resultLength = 0; 
				for (int j= 0; j < elementCount; j++) {
					IPackageFragment packageFragment= (IPackageFragment) list[j];
					if (nameMatches(name, packageFragment, true)) {
						result[resultLength++] = packageFragment;
					}
				}
				if (resultLength > 0) {
					System.arraycopy(result, 0, result = new IPackageFragment[resultLength], 0, resultLength);
					return result;
				} else {
					return null;
				}
			}
		} else {
			IPackageFragment[] fragments= (IPackageFragment[]) fPackageFragments.get(name);
			if (fragments != null) {
				IPackageFragment[] result = new IPackageFragment[fragments.length];
				int resultLength = 0; 
				for (int i= 0; i < fragments.length; i++) {
					IPackageFragment packageFragment= fragments[i];
					result[resultLength++] = packageFragment;
				}
				if (resultLength > 0) {
					System.arraycopy(result, 0, result = new IPackageFragment[resultLength], 0, resultLength);
					return result;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public IPart findPart(String typeName, String packageName, boolean partialMatch, int acceptFlags) {
		if (packageName == null) {
			packageName= IPackageFragment.DEFAULT_PACKAGE_NAME;
		}
		EGLElementRequestor elementRequestor = new EGLElementRequestor();
		seekPackageFragments(packageName, false, elementRequestor);
		IPackageFragment[] packages= elementRequestor.getPackageFragments();

		for (int i= 0, length= packages.length; i < length; i++) {
			IPart type= findPart(typeName, packages[i], partialMatch, acceptFlags);
			if (type != null)
				return type;
		}
		return null;
	}
	/**
	 * Returns all the package fragments found in the specified
	 * package fragment roots. Make sure the returned fragments have the given
	 * project as great parent. This ensures the name lookup will not refer to another
	 * project (through jar package fragment roots)
	 */
	private IPackageFragment[] getPackageFragmentsInRoots(IPackageFragmentRoot[] roots, IEGLProject project) {

		// The following code assumes that all the roots have the given project as their parent
		ArrayList frags = new ArrayList();
		for (int i = 0; i < roots.length; i++) {
			IPackageFragmentRoot root = roots[i];
			try {
				IEGLElement[] children = root.getChildren();

				/* 2 jar package fragment roots can be equals but not belonging 
				   to the same project. As a result, they share the same element info.
				   So this jar package fragment root could get the children of
				   another jar package fragment root.
				   The following code ensures that the children of this jar package
				   fragment root have the given project as a great parent.
				 */
				int length = children.length;
				if (length == 0) continue;
				if (children[0].getParent().getParent().equals(project)) {
					// the children have the right parent, simply add them to the list
					for (int j = 0; j < length; j++) {
						frags.add(children[j]);
					}
				} else {
					// create a new handle with the root as the parent
					for (int j = 0; j < length; j++) {
						frags.add(root.getPackageFragment(children[j].getElementName()));
					}
				}
			} catch (EGLModelException e) {
				// do nothing
			}
		}
		IPackageFragment[] fragments = new IPackageFragment[frags.size()];
		frags.toArray(fragments);
		return fragments;
	}

	/**
	 * Returns the first type in the given package whose name
	 * matches the given (unqualified) name, or <code>null</code> if none
	 * exist. Specifying a <code>null</code> package will result in no matches.
	 * The domain of the search is bounded by the EGL project from which 
	 * this name lookup was obtained.
	 *
	 * @param name the name of the type to find
	 * @param pkg the package to search
	 * @param partialMatch partial name matches qualify when <code>true</code>,
	 *	only exact name matches qualify when <code>false</code>
	 * @param acceptFlags a bit mask describing if classes, interfaces or both classes and interfaces
	 * 	are desired results. If no flags are specified, all types are returned.
	 *
	 * @see #ACCEPT_PARTS
	 */
	public IPart findPart(String name, IPackageFragment pkg, boolean partialMatch, int acceptFlags) {
		if (pkg == null) {
			return null;
		}
		// Return first found (ignore duplicates).
//synchronized(EGLModelManager.getEGLModelManager()){	
		SinglePartRequestor typeRequestor = new SinglePartRequestor();
		seekParts(name, pkg, partialMatch, acceptFlags, typeRequestor);
		IPart part= typeRequestor.getPart();
		return part;
//}
	}

	/**
	 * Returns the type specified by the qualified name, or <code>null</code>
	 * if none exist. The domain of
	 * the search is bounded by the EGL project from which this name lookup was obtained.
	 *
	 * @param name the name of the type to find
	 * @param partialMatch partial name matches qualify when <code>true</code>,
	 *	only exact name matches qualify when <code>false</code>
	 * @param acceptFlags a bit mask describing if classes, interfaces or both classes and interfaces
	 * 	are desired results. If no flags are specified, all types are returned.
	 *
	 * @see #ACCEPT_PARTS
	 */
	public IPart findPart(String name, boolean partialMatch, int acceptFlags) {
		int index= name.lastIndexOf('.');
		String partName= null, packageName= null;
		if (index == -1) {
			packageName= IPackageFragment.DEFAULT_PACKAGE_NAME;
			partName= name;
		} else {
			packageName= name.substring(0, index);
			partName= name.substring(index + 1);
		}
		return findPart(partName, packageName, partialMatch, acceptFlags);
	}

	/**
	 * Returns true if the given element's name matches the
	 * specified <code>searchName</code>, otherwise false.
	 *
	 * <p>The <code>partialMatch</code> argument indicates partial matches
	 * should be considered.
	 */
	protected boolean nameMatches(String searchName, IEGLElement element, boolean partialMatch) {
		if (partialMatch) {
			// partial matches are used in completion mode, thus case insensitive mode
			return element.getElementName().toLowerCase().startsWith(searchName);
		} else {
			return element.getElementName().equalsIgnoreCase(searchName);
		}
	}

	/**
	 * Notifies the given requestor of all package fragments with the
	 * given name. Checks the requestor at regular intervals to see if the
	 * requestor has canceled. The domain of
	 * the search is bounded by the <code>IEGLProject</code>
	 * this <code>NameLookup</code> was obtained from.
	 *
	 * @param partialMatch partial name matches qualify when <code>true</code>;
	 *	only exact name matches qualify when <code>false</code>
	 */
	public void seekPackageFragments(String name, boolean partialMatch, IEGLElementRequestor requestor) {
		int count= fPackageFragmentRoots.length;
		String matchName= partialMatch ? name.toLowerCase() : name;
		for (int i= 0; i < count; i++) {
			if (requestor.isCanceled())
				return;
			IPackageFragmentRoot root= fPackageFragmentRoots[i];
			IEGLElement[] list= null;
			try {
				list= root.getChildren();
			} catch (EGLModelException npe) {
				continue; // this root package fragment is not present
			}
			int elementCount= list.length;
			for (int j= 0; j < elementCount; j++) {
				if (requestor.isCanceled())
					return;
				IPackageFragment packageFragment= (IPackageFragment) list[j];
				if (nameMatches(matchName, packageFragment, partialMatch))
					requestor.acceptPackageFragment(packageFragment);
			}
		}
	}

	/**
	 * Notifies the given requestor of all types (classes and interfaces) in the
	 * given package fragment with the given (unqualified) name.
	 * Checks the requestor at regular intervals to see if the requestor
	 * has canceled. If the given package fragment is <code>null</code>, all types in the
	 * project whose simple name matches the given name are found.
	 *
	 * @param name The name to search
	 * @param pkg The corresponding package fragment
	 * @param partialMatch partial name matches qualify when <code>true</code>;
	 *	only exact name matches qualify when <code>false</code>
	 * @param acceptFlags a bit mask describing if classes, interfaces or both classes and interfaces
	 * 	are desired results. If no flags are specified, all types are returned.
	 * @param requestor The requestor that collects the result
	 *
	 * @see #ACCEPT_CLASSES
	 * @see #ACCEPT_INTERFACES
	 */
	public void seekParts(String name, IPackageFragment pkg, boolean partialMatch, int acceptFlags, IEGLElementRequestor requestor) {

		String matchName= partialMatch ? name.toLowerCase() : name;
		if (matchName.indexOf('.') >= 0) { //looks for member type A.B
			matchName= matchName.replace('.', '$');
		}
		if (pkg == null) {
			findAllParts(matchName, partialMatch, acceptFlags, requestor);
			return;
		}
		IPackageFragmentRoot root= (IPackageFragmentRoot) pkg.getParent();
		try {
			int packageFlavor= root.getKind();
			switch (packageFlavor) {
				case IPackageFragmentRoot.K_BINARY :
					// TODO when Library can be treated as binary
					 seekPartsInBinaryPackage(matchName, pkg, partialMatch, acceptFlags, requestor);
					break;
				case IPackageFragmentRoot.K_SOURCE :
					seekPartsInSourcePackage(matchName, pkg, partialMatch, acceptFlags, requestor);
					break;
				default :
					return;
			}
		} catch (EGLModelException e) {
			return;
		}
	}
	/**
	 * Notifies the given requestor of all functions  in the
	 * given type with the given (possibly qualified) name. If or
	 * when other part types are allowed to be Checks
	 * the requestor at regular intervals to see if the requestor
	 * has canceled.
	 *
	 * @param partialMatch partial name matches qualify when <code>true</code>,
	 *  only exact name matches qualify when <code>false</code>
	 */
	protected void seekQualifiedMemberParts(String qualifiedName, IPart type, boolean partialMatch, IEGLElementRequestor requestor, int acceptFlags) {
		if (type == null)
			return;
		IPart[] types= null;
		try {
			types= type.getFunctions();
		} catch (EGLModelException npe) {
			return; // the enclosing type is not present 
		}
		String matchName= qualifiedName;
		int index= qualifiedName.indexOf('$');
		boolean nested= false;
		if (index != -1) {
			matchName= qualifiedName.substring(0, index);
			nested= true;
		}
		int length= types.length;
		for (int i= 0; i < length; i++) {
			if (requestor.isCanceled())
				return;
			IPart memberType= types[i];
			if (nameMatches(matchName, memberType, partialMatch))
				if (nested) {
					seekQualifiedMemberParts(qualifiedName.substring(index + 1, qualifiedName.length()), memberType, partialMatch, requestor, acceptFlags);
				} else {
					if (acceptPart(memberType, acceptFlags)) requestor.acceptPart(memberType);
				}
		}
	}

	
	/**
	 * Performs part search in a source package.
	 */
	protected void seekPartsInSourcePackage(String name, IPackageFragment pkg, boolean partialMatch, int acceptFlags, IEGLElementRequestor requestor) {
		IEGLFile[] eglfiles = null;
		try {
			eglfiles = pkg.getEGLFiles();
		} catch (EGLModelException npe) {
			return; // the package is not present
		}
		int length= eglfiles.length;
		String matchName = name;
		int index= name.indexOf('$');
		boolean potentialMemberType = false;
		String potentialMatchName = null;
		if (index != -1) {
			//the compilation unit name of the inner type
			potentialMatchName = name.substring(0, index);
			potentialMemberType = true;
		}

		for (int i= 0; i < length; i++) {
			if (requestor.isCanceled())
				return;
			IEGLFile eglfile = eglfiles[i];
			
			// unit to look inside
			IEGLFile unitToLookInside = null;
			Map workingCopies = (Map) this.unitsToLookInside.getCurrent();
			if (workingCopies != null 
					&& (unitToLookInside = (IEGLFile)workingCopies.get(eglfile)) != null){
					eglfile = unitToLookInside;
					try {
						eglfile.reconcile(true, null);
					} catch (EGLModelException e) {
						EDTCoreIDEPlugin.getPlugin().log("NameLookup Failure", e); //$NON-NLS-1$
					}
				}
				
			IPart[] parts= null;
			try {
				parts= eglfile.getParts();
			} catch (EGLModelException npe) {
				continue; // the compilation unit is not present
			}
			int typeLength= parts.length;
			for (int j= 0; j < typeLength; j++) {
				if (requestor.isCanceled())
					return;
				IPart part= parts[j];
				if (nameMatches(matchName, part, partialMatch)) {
					if (acceptPart(part, acceptFlags)) requestor.acceptPart(part);
				}
			}
		
// EGLTODO - does anyone search for  "Nested" parts?
//			if (potentialMemberType) {
//				IPart[] types= null;
//				try {
//					types= eglfile.getParts();
//				} catch (EGLModelException npe) {
//					continue; // the compilation unit is not present
//				}
//				typeLength= types.length;
//				for (int j= 0; j < typeLength; j++) {
//					if (requestor.isCanceled())
//						return;
//					IPart type= types[j]; 
//					if (nameMatches(potentialMatchName, type, partialMatch)) {
//						seekQualifiedMemberParts(name.substring(index + 1, name.length()), type, partialMatch, requestor, acceptFlags);
//					}
//				}
//			}
		}
		
	}
	
	/**
	 * Performs part search in a binary package.
	 */
	protected void seekPartsInBinaryPackage(String name, IPackageFragment pkg, boolean partialMatch, int acceptFlags, IEGLElementRequestor requestor) {
		IClassFile[] classFiles = null;
		try {
			classFiles = pkg.getClassFiles();	
		} catch (EGLModelException e) {
			return; // the package is not present?
		}
		int length= classFiles.length;
		String matchName = name;
		//TODO for nested parts?
		
		for(int i=0; i<length; i++){
			if (requestor.isCanceled())
				return;
			IClassFile classFile = classFiles[i];
			IPart[] parts= null;
			try {
				parts = classFile.getParts();
			} catch (EGLModelException e) {
				continue;	//why?
			}
			int typeLength= parts.length;
			for (int j = 0; j < typeLength; j++) {
				if (requestor.isCanceled())
					return;
				IPart part= parts[j];
				if (nameMatches(matchName, part, partialMatch)) {
					if (acceptPart(part, acceptFlags))
						requestor.acceptPart(part);
				}
			}
		}
	}
/**
 * Remembers a set of compilation units that will be looked inside
 * when looking up a type. If they are working copies, they take
 * precedence of their compilation units.
 * <code>null</code> means that no special compilation units should be used.
 */
public void setUnitsToLookInside(IWorkingCopy[] unitsToLookInside) {
	
	if (unitsToLookInside == null) {
		this.unitsToLookInside.setCurrent(null); 
	} else {
		HashMap workingCopies = new HashMap();
		this.unitsToLookInside.setCurrent(workingCopies);
		for (int i = 0, length = unitsToLookInside.length; i < length; i++) {
			IWorkingCopy unitToLookInside = unitsToLookInside[i];
			IEGLFile original = (IEGLFile)unitToLookInside.getOriginalElement();
			if (original != null) {
				workingCopies.put(original, unitToLookInside);
			} else {
				workingCopies.put(unitToLookInside, unitToLookInside);
			}
		}
	}
}

}

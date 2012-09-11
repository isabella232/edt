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
package org.eclipse.edt.ide.core.internal.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.ElementChangedEvent;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IElementChangedListener;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.IPartNameRequestor;
import org.eclipse.edt.ide.core.search.SearchEngine;

/**
 * Manages a search cache for types in the workspace. Instead of returning objects of type <code>IPart</code>
 * the methods of this class returns a list of the lightweight objects <code>PartInfo</code>.
 */
public class AllPartsCache {
	

	private static PartInfo[] fgTypeCache= null;
	private static int fgSizeHint= 2000;
	
	private static int fgNumberOfCacheFlushes= 0;

	private static class PartNameComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			return ((PartInfo)o1).getPartName().compareTo(((PartInfo)o2).getPartName());
		}
	}
	
	private static Comparator fgPartNameComparator= new PartNameComparator();

	/**
	 * Returns all types in the given scope.
	 * @param kind IEGLSearchConstants.CLASS, IEGLSearchConstants.INTERFACE
	 * or IEGLSearchConstants.PART
	 * @param typesFound The resulting <code>PartInfo</code> elements are added to this collection
	 */		
	public static void getParts(IEGLSearchScope scope, int kind, IProgressMonitor monitor, Collection partsFound) throws EGLModelException {
	    getParts(scope, kind, null, monitor, partsFound);
	}
	
	private static void addPartToCollection(IEGLSearchScope scope, PartInfo info, Collection partsFound, int kind, String subType) throws EGLModelException
	{
	    if((info.getPartType() & kind) != 0)
	    {
		    if(subType != null && subType.length()>0)
		    {
		        IPart part = info.resolvePart(scope);
		        if(part != null && part.getSubTypeSignature() != null)
		        {
			        String partSubTypeString = Signature.toString(part.getSubTypeSignature());
			        if(partSubTypeString.equalsIgnoreCase(subType))
			            partsFound.add(info);
		        }
		    }
		    else	//ignore the subType
		    {
		    	partsFound.add(info);
		    }
	    }
	}
	
	/**
	 * Returns all types in the given scope.
	 * @param scope
	 * @param kind IEGLSearchConstants.CLASS, IEGLSearchConstants.INTERFACE
	 * or IEGLSearchConstants.PART
	 * @param subType the subType String, i.e. EGLInterfaceType.EGL_JAVAOBJECT_INSTANCE.getName()
	 * @param monitor
	 * @param partsFound
	 * @throws EGLModelException
	 */
	public static void getParts(IEGLSearchScope scope, int kind, String subType, IProgressMonitor monitor, Collection partsFound) throws EGLModelException {
		PartInfo[] allParts= getAllParts(monitor);
		
		boolean isWorkspaceScope= scope.equals(SearchEngine.createWorkspaceScope());
		for (int i= 0; i < allParts.length; i++) {
			PartInfo info= (PartInfo) fgTypeCache[i];
			if (isWorkspaceScope || info.isEnclosed(scope)) {
			    addPartToCollection(scope, info, partsFound, kind, subType);
			}
		}	    
	}

	/**
	 * Returns all types in the workspace. The returned array must not be
	 * modified. The elements in the array are sorted by simple type name.
	 */
	public static synchronized PartInfo[] getAllParts(IProgressMonitor monitor) throws EGLModelException { 	
		if (fgTypeCache == null) {
			ArrayList searchResult= new ArrayList(fgSizeHint);
			doSearchParts(SearchEngine.createWorkspaceScope(), IEGLSearchConstants.PART, monitor, searchResult);
			if (monitor != null && monitor.isCanceled()) {
				return null;
			}
			monitor= null; // prevents duplicated invocation of monitor.done
			fgTypeCache= (PartInfo[]) searchResult.toArray(new PartInfo[searchResult.size()]);
			Arrays.sort(fgTypeCache, getPartNameComperator());
			fgSizeHint= fgTypeCache.length;
	
			EGLCore.addElementChangedListener(new PartCacheDeltaListener());
		}
		if (monitor != null) {
			monitor.done();
		}		
		return fgTypeCache;
	}
	
	/**
	 * Returns true if the type cache is up to date.
	 */
	public static boolean isCacheUpToDate() {
		return fgTypeCache != null;
	}
	
	/**
	 * Returns a hint for the number of all types in the workspace.
	 */
	public static int getNumberOfAllPartsHint() {
		return fgSizeHint;
	}
	
	/**
	 * Returns a compartor that compares the simple type names
	 */
	public static Comparator getPartNameComperator() {
		return fgPartNameComparator;
	}	


	private static void doSearchParts(IEGLSearchScope scope, int style, IProgressMonitor monitor, Collection typesFound) throws EGLModelException {
		new SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(),
			null,
			null,
			IIndexConstants.PATTERN_MATCH,
			IEGLSearchConstants.CASE_INSENSITIVE,
			style,
			scope,
			new PartInfoRequestor(typesFound),
			IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
			monitor);
	}
	
	private static class PartCacheDeltaListener implements IElementChangedListener {
		
		/*
		 * @see IElementChangedListener#elementChanged
		 */
		public void elementChanged(ElementChangedEvent event) {
			boolean needsFlushing= processDelta(event.getDelta());
			if (needsFlushing) {
				fgTypeCache= null;
				fgNumberOfCacheFlushes++;
				EGLCore.removeElementChangedListener(this); // it's ok to remove listener while delta processing
			}
		}
		
		/*
		 * returns true if the cache needs to be flushed
		 */
		private boolean processDelta(IEGLElementDelta delta) {
			IEGLElement elem= delta.getElement();
			boolean isAddedOrRemoved= (delta.getKind() != IEGLElementDelta.CHANGED)
			 || (delta.getFlags() & (IEGLElementDelta.F_ADDED_TO_EGLPATH | IEGLElementDelta.F_REMOVED_FROM_EGLPATH)) != 0;
			
			switch (elem.getElementType()) {
				case IEGLElement.EGL_MODEL:
				case IEGLElement.EGL_PROJECT:
				case IEGLElement.PACKAGE_FRAGMENT_ROOT:
				case IEGLElement.PACKAGE_FRAGMENT:
				case IEGLElement.CLASS_FILE:
				case IEGLElement.PART: // type children can be inner classes
					if (isAddedOrRemoved) {
						return true;
					}				
					return processChildrenDelta(delta);
				case IEGLElement.EGL_FILE: // content change means refresh from local
					if (((IEGLFile) elem).isWorkingCopy()) {
						return false;
					}
					if (isAddedOrRemoved || isPossibleStructuralChange(delta.getFlags())) {
						return true;
					}
					return processChildrenDelta(delta);
				default:
					// fields, methods, imports ect
					return false;
			}	
		}
		
		private boolean isPossibleStructuralChange(int flags) {
			return (flags & (IEGLElementDelta.F_CONTENT | IEGLElementDelta.F_FINE_GRAINED)) == IEGLElementDelta.F_CONTENT;
		}		
		
		private boolean processChildrenDelta(IEGLElementDelta delta) {
			IEGLElementDelta[] children= delta.getAffectedChildren();
			for (int i= 0; i < children.length; i++) {
				if (processDelta(children[i])) {
					return true;
				}
			}
			return false;
		}
	}
	

	/**
	 * Gets the number of times the cache was flushed. Used for testing.
	 * @return Returns a int
	 */
	public static int getNumberOfCacheFlushes() {
		return fgNumberOfCacheFlushes;
	}

	/**
	 * Returns all types for a given name in the scope. The elements in the array are sorted by simple type name.
	 */
	public static PartInfo[] getPartsForName(String simplePartName, IEGLSearchScope searchScope, IProgressMonitor monitor) throws EGLModelException {
		Collection result= new ArrayList();
		Set namesFound= new HashSet();
		PartInfo[] allParts= AllPartsCache.getAllParts(monitor); // all types in workspace, sorted by type name
		PartInfo key= new UnresolvablePartInfo("", simplePartName, null, null); //$NON-NLS-1$
		int index= Arrays.binarySearch(allParts, key, AllPartsCache.getPartNameComperator());
		if (index >= 0 && index < allParts.length) {
			for (int i= index - 1; i>= 0; i--) {
				PartInfo curr= allParts[i];
				if (simplePartName.equals(curr.getPartName())) {
					if (!namesFound.contains(curr.getFullyQualifiedName()) && curr.isEnclosed(searchScope)) {
						result.add(curr);
						namesFound.add(curr.getFullyQualifiedName());
					}
				} else {
					break;
				}
			}
	
			for (int i= index; i < allParts.length; i++) {
				PartInfo curr= allParts[i];
				if (simplePartName.equals(curr.getPartName())) {
					if (!namesFound.contains(curr.getFullyQualifiedName()) && curr.isEnclosed(searchScope)) {
						result.add(curr);
						namesFound.add(curr.getFullyQualifiedName());
					}
				} else {
					break;
				}
			}
		}
		return (PartInfo[]) result.toArray(new PartInfo[result.size()]);		
	}
	
	/**
	 * Similar to getPartsForName but we include duplicates and filter by part type
	 */
	public static PartInfo[] getAllPartsForName(String simplePartName, IEGLSearchScope searchScope, IProgressMonitor monitor, int partType) throws EGLModelException {
		Collection result= new ArrayList();
		PartInfo[] allParts= AllPartsCache.getAllParts(monitor); // all types in workspace, sorted by type name
		PartInfo key= new UnresolvablePartInfo("", simplePartName, null, null); //$NON-NLS-1$
		
		int index= Arrays.binarySearch(allParts, key, AllPartsCache.getPartNameComperator());
		if (index >= 0 && index < allParts.length) {
			for (int i= index - 1; i>= 0; i--) {
				PartInfo curr= allParts[i];
				if (simplePartName.equals(curr.getPartName())) {
					if ((curr.getPartType() & partType) != 0 && curr.isEnclosed(searchScope)) {
						result.add(curr);
					}
				} else {
					break;
				}
			}
	
			for (int i= index; i < allParts.length; i++) {
				PartInfo curr= allParts[i];
				if (simplePartName.equals(curr.getPartName())) {
					if ((curr.getPartType() & partType) != 0 && curr.isEnclosed(searchScope)) {
						result.add(curr);
					}
				} else {
					break;
				}
			}
		}
		return (PartInfo[]) result.toArray(new PartInfo[result.size()]);		
	}
	
	/**
	 * Checks if the search index is up to date.
	 */
	public static boolean isIndexUpToDate() {
		class TypeFoundException extends Error {private static final long serialVersionUID = 1L;}
		IPartNameRequestor requestor= new IPartNameRequestor() {
			public void acceptPart(char[] packageName, char[] simplePartName, char partType, char[][] enclosingPartNames,  String path, IPath projectPath) {
				throw new TypeFoundException();
			}
		};
		try {
			new SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(),
				null,
				null,
				IIndexConstants.PATTERN_MATCH,
				IEGLSearchConstants.CASE_INSENSITIVE,
				IEGLSearchConstants.PART,
				SearchEngine.createWorkspaceScope(),
				requestor,
				IEGLSearchConstants.CANCEL_IF_NOT_READY_TO_SEARCH,
				new NullProgressMonitor());
		} catch (EGLModelException e) {
			// TODO Log the error EGLPlugin.log(e);
			return false;
		} catch (OperationCanceledException e) {
			return false;			
		} catch (TypeFoundException e) {
		}
		return true;
	}		
	
	public static void flushCache() {
		fgNumberOfCacheFlushes += 1;
		fgTypeCache = null;
	}
}

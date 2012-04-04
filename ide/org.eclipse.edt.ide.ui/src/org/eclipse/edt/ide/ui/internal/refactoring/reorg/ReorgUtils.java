/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.jdt.internal.corext.refactoring.util.ResourceUtil;

public class ReorgUtils {

	private ReorgUtils() {
	}
	
	public static IResource[] getResources(List elements) {
		List resources= new ArrayList(elements.size());
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IResource)
				resources.add(element);
		}
		return (IResource[]) resources.toArray(new IResource[resources.size()]);
	}
	
	public static IResource[] getResources(IEGLElement[] elements) {
		IResource[] result= new IResource[elements.length];
		for (int i= 0; i < elements.length; i++) {
			result[i]= getResource(elements[i]);
		}
		return result;
	}
	
	public static IResource[] getNotNulls(IResource[] resources) {
		Collection result= new ArrayList(resources.length);
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource != null && ! result.contains(resource))
				result.add(resource);
		}
		return (IResource[]) result.toArray(new IResource[result.size()]);
	}
	
	public static IResource[] setMinus(IResource[] setToRemoveFrom, IResource[] elementsToRemove) {
		Set setMinus= new HashSet(setToRemoveFrom.length - setToRemoveFrom.length);
		setMinus.addAll(Arrays.asList(setToRemoveFrom));
		setMinus.removeAll(Arrays.asList(elementsToRemove));
		return (IResource[]) setMinus.toArray(new IResource[setMinus.size()]);		
	}

	public static IEGLElement[] setMinus(IEGLElement[] setToRemoveFrom, IEGLElement[] elementsToRemove) {
		Set setMinus= new HashSet(setToRemoveFrom.length - setToRemoveFrom.length);
		setMinus.addAll(Arrays.asList(setToRemoveFrom));
		setMinus.removeAll(Arrays.asList(elementsToRemove));
		return (IEGLElement[]) setMinus.toArray(new IEGLElement[setMinus.size()]);		
	}
	
	public static boolean isArchiveMember(IEGLElement[] elements) {
		for (int i= 0; i < elements.length; i++) {
			IEGLElement element= elements[i];
			IPackageFragmentRoot root= (IPackageFragmentRoot)element.getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
			if (root != null && root.isArchive())
				return true;
		}
		return false;
	}
	
	public static boolean hasElementsNotOfType(IResource[] resources, int typeMask) {
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource != null && ! isOfType(resource, typeMask))
				return true;
		}
		return false;
	}

	public static boolean hasElementsNotOfType(IEGLElement[] eglElements, int type) {
		for (int i= 0; i < eglElements.length; i++) {
			IEGLElement element= eglElements[i];
			if (element != null && ! isOfType(element, type))
				return true;
		}
		return false;
	}
	
	public static boolean hasElementsOfType(IEGLElement[] eglElements, int type) {
		for (int i= 0; i < eglElements.length; i++) {
			IEGLElement element= eglElements[i];
			if (element != null && isOfType(element, type))
				return true;
		}
		return false;
	}

	public static boolean hasElementsOfType(IEGLElement[] eglElements, int[] types) {
		for (int i= 0; i < types.length; i++) {
			if (hasElementsOfType(eglElements, types[i])) return true;
		}
		return false;
	}

	public static boolean hasElementsOfType(IResource[] resources, int typeMask) {
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource != null && isOfType(resource, typeMask))
				return true;
		}
		return false;
	}
	
	private static boolean isOfType(IEGLElement element, int type) {
		return element.getElementType() == type;//this is _not_ a mask
	}
		
	private static boolean isOfType(IResource resource, int type) {
		return resource != null && isFlagSet(resource.getType(), type);
	}
	
	private static boolean isFlagSet(int flags, int flag){
		return (flags & flag) != 0;
	}
	
	public static IFolder[] getFolders(IResource[] resources) {
		Set result= getResourcesOfType(resources, IResource.FOLDER);
		return (IFolder[]) result.toArray(new IFolder[result.size()]);
	}
	
	public static IFile[] getFiles(IResource[] resources) {
		Set result= getResourcesOfType(resources, IResource.FILE);
		return (IFile[]) result.toArray(new IFile[result.size()]);
	}
	
	public static IFile[] getFiles(IEGLFile[] cus) {
		List files= new ArrayList(cus.length);
		for (int i= 0; i < cus.length; i++) {
			IResource resource= getResource(cus[i]);
			if (resource != null && resource.getType() == IResource.FILE)
				files.add(resource);
		}
		return (IFile[]) files.toArray(new IFile[files.size()]);
	}
	
	public static Set getResourcesOfType(IResource[] resources, int typeMask){
		Set result= new HashSet(resources.length);
		for (int i= 0; i < resources.length; i++) {
			if (isOfType(resources[i], typeMask))
				result.add(resources[i]);
		}
		return result;
	}
	
	public static boolean isInsideEGLFile(IEGLElement element) {
		return 	!(element instanceof IEGLFile) && 
				hasAncestorOfType(element, IEGLElement.EGL_FILE);
	}
	
	public static boolean isInsideIRFile(IEGLElement element) {
//		return 	!(element instanceof IClassFile) && 
//				hasAncestorOfType(element, IEGLElement.CLASS_FILE);
		return false;
	}
	
	public static boolean hasAncestorOfType(IEGLElement element, int type){
		return element.getAncestor(type) != null;
	}
	
	public static IEGLFile getEGLFile(IEGLElement eglElement){
		if (eglElement instanceof IEGLFile)
			return (IEGLFile) eglElement;
		return (IEGLFile) eglElement.getAncestor(IEGLElement.EGL_FILE);
	}

	public static IEGLFile[] getEGLFiles(IEGLElement[] eglElements){
		IEGLFile[] result= new IEGLFile[eglElements.length];
		for (int i= 0; i < eglElements.length; i++) {
			result[i]= getEGLFile(eglElements[i]);
		}
		return result;
	}
	
	public static boolean isDeletedFromEditor(IEGLElement elem) throws EGLModelException {
		if (! isInsideEGLFile(elem))
			return false;
		IEGLFile cu= getEGLFile(elem);
		if (cu == null)
			return false;
		IEGLFile wc= cu;
		if (cu.equals(wc))
			return false;
		IEGLElement wcElement= EGLModelUtil.findInEGLFile(wc, elem);
		return wcElement == null || ! wcElement.exists();
	}
	
	public static boolean canBeDestinationForLinkedResources(IResource resource) {
		return resource.isAccessible() && resource instanceof IProject;
	}

	public static boolean canBeDestinationForLinkedResources(IEGLElement eglElement) {
		if (eglElement instanceof IPackageFragmentRoot){
			return isPackageFragmentRootCorrespondingToProject((IPackageFragmentRoot)eglElement);
		} else if (eglElement instanceof IEGLProject){
			return true;
		} else return false;
	}
	
	private static boolean isPackageFragmentRootCorrespondingToProject(IPackageFragmentRoot root) {
		return root.getResource() instanceof IProject;
	}
	
	public static IPackageFragmentRoot getCorrespondingPackageFragmentRoot(IEGLProject p) throws EGLModelException {
		IPackageFragmentRoot[] roots= p.getPackageFragmentRoots();
		for (int i= 0; i < roots.length; i++) {
			if (isPackageFragmentRootCorrespondingToProject(roots[i]))
				return roots[i];
		}
		return null;
	}
	
	public static IEGLElement[] union(IEGLElement[] set1, IEGLElement[] set2) {
		List union= new ArrayList(set1.length + set2.length);//use lists to avoid sequence problems
		addAll(set1, union);
		addAll(set2, union);
		return (IEGLElement[]) union.toArray(new IEGLElement[union.size()]);
	}	

	public static IResource[] union(IResource[] set1, IResource[] set2) {
		List union= new ArrayList(set1.length + set2.length);//use lists to avoid sequence problems
		addAll(getNotNulls(set1), union);
		addAll(getNotNulls(set2), union);
		return (IResource[]) union.toArray(new IResource[union.size()]);
	}	

	private static void addAll(Object[] array, List list) {
		for (int i= 0; i < array.length; i++) {
			if (! list.contains(array[i]))
				list.add(array[i]);
		}
	}

	public static Set union(Set set1, Set set2){
		Set union= new HashSet(set1.size() + set2.size());
		union.addAll(set1);
		union.addAll(set2);
		return union;
	}
	
	public static boolean containsLinkedResources(IResource[] resources){
		for (int i= 0; i < resources.length; i++) {
			if (resources[i] != null && resources[i].isLinked()) return true;
		}
		return false;
	}
	
	public static boolean containsLinkedResources(IEGLElement[] eglElements){
		for (int i= 0; i < eglElements.length; i++) {
			IResource res= getResource(eglElements[i]);
			if (res != null && res.isLinked()) return true;
		}
		return false;
	}
	
	public static IResource getResource(Object o){
		if (o instanceof IResource)
			return (IResource)o;
		if (o instanceof IEGLElement)
			return getResource((IEGLElement)o);
		return null;	
	}

	private static IResource getResource(IEGLElement element){
		if (element.getElementType() == IEGLElement.EGL_FILE) 
			return getResource((IEGLFile) element);
		else if (element instanceof IOpenable) 
			return element.getResource();
		else	
			return null;	
	}
	
	public static IResource getResource(IEGLFile cu) {
		return cu.getResource();
	}
	
	public static boolean isDefaultPackage(Object element) {
		return (element instanceof IPackageFragment) && ((IPackageFragment)element).isDefaultPackage();
	}
	
	public static String getLineDelimiterUsed(IEGLProject project) {
		return getProjectLineDelimiter(project);
	}

	private static String getProjectLineDelimiter(IEGLProject eglProject) {
		IProject project= null;
		if (eglProject != null)
			project= eglProject.getProject();
		
		String lineDelimiter= getLineDelimiterPreference(project);
		if (lineDelimiter != null)
			return lineDelimiter;
		
		return System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String getLineDelimiterPreference(IProject project) {
		IScopeContext[] scopeContext;
		if (project != null) {
			// project preference
			scopeContext= new IScopeContext[] { new ProjectScope(project) };
			String lineDelimiter= Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null, scopeContext);
			if (lineDelimiter != null)
				return lineDelimiter;
		}
		// workspace preference
		scopeContext= new IScopeContext[] { new InstanceScope() };
		String platformDefault= System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		return Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, platformDefault, scopeContext);
	}
	
	public static String getLineDelimiterUsed(IEGLElement elem) {
		return getProjectLineDelimiter(elem.getEGLProject());
	}
	
	public static IPart getMainPart(IEGLFile cu) throws EGLModelException{
		IPart[] types= cu.getParts();
		for (int i = 0; i < types.length; i++) {
			if (isMainType(types[i], cu.getElementName()))
				return types[i];
		}
		return null;
	}

	private static boolean isMainType(IPart part, String eglFileName) {
		return part instanceof SourcePart && ((SourcePart) part).isGeneratable() && part.getElementName().equals(eglFileName);
	}
	
	public static boolean isReadOnly(IResource resource) {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes == null)  // not supported on this platform for this resource 
			return false;
		return resourceAttributes.isReadOnly();
	}
	
	public static boolean isParentInWorkspaceOrOnDisk(IPackageFragment pack, IPackageFragmentRoot root){
		if (pack == null)
			return false;		
		IEGLElement packParent= pack.getParent();
		if (packParent == null)
			return false;		
		if (packParent.equals(root))	
			return true;
		IResource packageResource= getResource(pack);
		IResource packageRootResource= getResource(root);
		return isParentInWorkspaceOrOnDisk(packageResource, packageRootResource);
	}

	public static boolean isParentInWorkspaceOrOnDisk(IPackageFragmentRoot root, IEGLProject eglProject){
		if (root == null)
			return false;		
		IEGLElement rootParent= root.getParent();
		if (rootParent == null)
			return false;		
		if (rootParent.equals(root))	
			return true;
		IResource packageResource= getResource(root);
		IResource packageRootResource= getResource(eglProject);
		return isParentInWorkspaceOrOnDisk(packageResource, packageRootResource);
	}

	public static boolean isParentInWorkspaceOrOnDisk(IEGLFile cu, IPackageFragment dest){
		if (cu == null)
			return false;
		IEGLElement cuParent= cu.getParent();
		if (cuParent == null)
			return false;
		if (cuParent.equals(dest))	
			return true;
		IResource cuResource= ResourceUtil.getResource(cu);
		IResource packageResource= ResourceUtil.getResource(dest);
		return isParentInWorkspaceOrOnDisk(cuResource, packageResource);
	}
	
	public static boolean isParentInWorkspaceOrOnDisk(IResource res, IResource maybeParent){
		if (res == null)
			return false;
		return areEqualInWorkspaceOrOnDisk(res.getParent(), maybeParent);
	}
	
	public static boolean areEqualInWorkspaceOrOnDisk(IResource r1, IResource r2){
		if (r1 == null || r2 == null)
			return false;
		if (r1.equals(r2))
			return true;
		URI r1Location= r1.getLocationURI();
		URI r2Location= r2.getLocationURI();
		if (r1Location == null || r2Location == null)
			return false;
		return r1Location.equals(r2Location);
	}
}

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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;

import com.ibm.icu.util.StringTokenizer;

/**
 * Implementation of <code>IEGLModel<code>. The EGL Model maintains a cache of
 * active <code>IEGLProject</code>s in a workspace. A EGL Model is specific to a
 * workspace. To retrieve a workspace's model, use the
 * <code>#getEGLModel(IWorkspace)</code> method.
 *
 * @see IEGLModel
 */
public class EGLModel extends Openable implements IEGLModel {

	/**
	 * A set of egl.io.Files used as a cache of external jars that 
	 * are known to be existing.
	 * Note this cache is kept for the whole session.
	 */ 
	public static HashSet existingExternalFiles = new HashSet();

	/**
	 * A set of external files ({@link #existingExternalFiles}) which have
	 * been confirmed as file (ie. which returns true to {@link java.io.File#isFile()}.
	 * Note this cache is kept for the whole session.
	 */
	public static HashSet existingExternalConfirmedFiles = new HashSet();
/**
 * Constructs a new EGL Model on the given workspace.
 * Note that only one instance of EGLModel handle should ever be created.
 * One should only indirect through EGLModelManager#getEGLModel() to get
 * access to it.
 * 
 * @exception Error if called more than once
 */
protected EGLModel() throws Error {
	super(EGL_MODEL, null, "" /*workspace has empty name*/); //$NON-NLS-1$
}
/*
 * @see IEGLModel
 */
public boolean contains(IResource resource) {
	switch (resource.getType()) {
		case IResource.ROOT:
		case IResource.PROJECT:
			return true;
	}
	// file or folder
	IEGLProject[] projects;
	try {
		projects = this.getEGLProjects();
	} catch (EGLModelException e) {
		return false;
	}
	for (int i = 0, length = projects.length; i < length; i++) {
		EGLProject project = (EGLProject)projects[i];
		if (!project.contains(resource)) {
			return false;
		}
	}
	return true;
}
/**
 * @see IEGLModel
 */
public void copy(IEGLElement[] elements, IEGLElement[] containers, IEGLElement[] siblings, String[] renamings, boolean force, IProgressMonitor monitor) throws EGLModelException {
	if (elements != null && elements.length > 0 && elements[0] != null && elements[0].getElementType() < IEGLElement.PART) {
		runOperation(new CopyResourceElementsOperation(elements, containers, force), elements, siblings, renamings, monitor);
	} else {
//		runOperation(new CopyElementsOperation(elements, containers, force), elements, siblings, renamings, monitor);
		throw new UnsupportedOperationException();
	}
}
/**
 * Returns a new element info for this element.
 */
protected OpenableElementInfo createElementInfo() {
	return new EGLModelInfo();
}

/**
 * @see IEGLModel
 */
public void delete(IEGLElement[] elements, boolean force, IProgressMonitor monitor) throws EGLModelException {
	// TODO handle later
	if (elements != null && elements.length > 0 && elements[0] != null && elements[0].getElementType() <= IEGLElement.EGL_FILE) {
		new DeleteResourceElementsOperation(elements, force).runOperation(monitor);
	} /*else {
		runOperation(new DeleteElementsOperation(elements, force), monitor);
	}*/
}
/**
 * Finds the given project in the list of the egl model's children.
 * Returns null if not found.
 */
public IEGLProject findEGLProject(IProject project) {
	try {
		IEGLProject[] projects = this.getOldEGLProjectsList();
		for (int i = 0, length = projects.length; i < length; i++) {
			IEGLProject eglProject = projects[i];
			if (project.equals(eglProject.getProject())) {
				return eglProject;
			}
		}
	} catch (EGLModelException e) {
	}
	return null;
}

/**
 * Flushes the cache of external files known to be existing.
 */
public static void flushExternalFileCache() {
	existingExternalFiles = new HashSet();
	existingExternalConfirmedFiles = new HashSet();
}

/**
 */
protected boolean generateInfos(
	OpenableElementInfo info,
	IProgressMonitor pm,
	Map newElements,
	IResource underlyingResource)	throws EGLModelException {

	EGLModelManager.getEGLModelManager().putInfo(this, info);
	// determine my children
	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	for (int i = 0, max = projects.length; i < max; i++) {
		IProject project = projects[i];
		if (EGLProject.hasEGLNature(project)) {
			info.addChild(getEGLProject(project));
		}
	}
	return true;
}
/**
 * Returns the <code>IPackageFragmentRoot</code> represented by the <code>String</code>
 * memento.
 * @see getHandleMemento()
 */
protected IPackageFragmentRoot getHandleFromMementoForRoot(String memento, EGLProject project, int projectEnd, int rootEnd) {
	String rootName = null;
	if (rootEnd == projectEnd - 1) {
		//default root
		rootName = IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH;
	} else {
		rootName = memento.substring(projectEnd + 1, rootEnd);
	}
	return project.getPackageFragmentRoot(new Path(rootName));
}
/**
 * Returns the <code>IEGLElement</code> represented by the <code>String</code>
 * memento.
 * @see getHandleMemento()
 */
// TODO Will handle member stuff later

protected IEGLElement getHandleFromMementoForSourceMembers(String memento, IPackageFragmentRoot root, int rootEnd, int end) throws EGLModelException {

	//deal with compilation units and source members
	IPackageFragment frag = null;
	if (rootEnd == end - 1) {
		//default package
		frag= root.getPackageFragment(IPackageFragment.DEFAULT_PACKAGE_NAME);
	} else {
		frag= root.getPackageFragment(memento.substring(rootEnd + 1, end));
	}
	int oldEnd = end;
	end = memento.indexOf(EGLElement.EGLM_PACKAGEDECLARATION, end);
	if (end != -1) {
		//package declaration
		IEGLFile cu = frag.getEGLFile(memento.substring(oldEnd + 1, end));
		return cu.getPackageDeclaration(memento.substring(end + 1));
	}
	end = memento.indexOf(EGLElement.EGLM_IMPORTDECLARATION, oldEnd);
	if (end != -1) {
		//import declaration
		IEGLFile cu = frag.getEGLFile(memento.substring(oldEnd + 1, end));
		return cu.getImport(memento.substring(end + 1));
	}
	int typeStart = memento.indexOf(EGLElement.EGLM_PART, oldEnd);
	if (typeStart == -1) {
		//we ended with a compilation unit
		return frag.getEGLFile(memento.substring(oldEnd + 1));
	}

	//source members
	IEGLFile cu = frag.getEGLFile(memento.substring(oldEnd + 1, typeStart));
	end = memento.indexOf(EGLElement.EGLM_FIELD, oldEnd);
	if (end != -1) {
		//source field
		IPart type = getHandleFromMementoForSourcePart(memento, cu, typeStart, end);
		return type.getField(memento.substring(end + 1));
	}
	end = memento.indexOf(EGLElement.EGLM_FUNCTION, oldEnd);
	if (end != -1) {
		//source method
		IPart type = getHandleFromMementoForSourcePart(memento, cu, typeStart, end);
		oldEnd = end;
		String methodName;
		end = memento.lastIndexOf(EGLElement.EGLM_FUNCTION);
		String[] parameterTypes = null;
		if (end == oldEnd) {
			methodName = memento.substring(end + 1);
			//no parameter types
			parameterTypes = new String[] {};
		} else {
			String parameters = memento.substring(oldEnd + 1);
			StringTokenizer mTokenizer = new StringTokenizer(parameters, new String(new char[] {EGLElement.EGLM_FUNCTION}));
			parameterTypes = new String[mTokenizer.countTokens() - 1];
			methodName = mTokenizer.nextToken();
			int i = 0;
			while (mTokenizer.hasMoreTokens()) {
				parameterTypes[i] = mTokenizer.nextToken();
				i++;
			}
		}
		return type.getFunction(methodName, parameterTypes);
	}
	/* Deal with initializers later if necessary
	end = memento.indexOf(EGLElement.EGLM_INITIALIZER, oldEnd);
	if (end != -1 ) {
		//initializer
		IPart type = getHandleFromMementoForSourceType(memento, cu, typeStart, end);
		return type.getInitializer(Integer.parseInt(memento.substring(end + 1)));
	}
	*/
	//source type
	return getHandleFromMementoForSourcePart(memento, cu, typeStart, memento.length());
}

/**
 * Returns the <code>IEGLElement</code> represented by the <code>String</code>
 * memento.
 * @see getHandleMemento()
 */

protected IPart getHandleFromMementoForSourcePart(String memento, IEGLFile cu, int typeStart, int typeEnd) throws EGLModelException {
	int end = memento.lastIndexOf(EGLElement.EGLM_PART);
	IPart type = null;
	if (end == typeStart) {
		String typeName = memento.substring(typeStart + 1, typeEnd);
		type = cu.getPart(typeName);
		
	} else {
		String typeNames = memento.substring(typeStart + 1, typeEnd);
		StringTokenizer tokenizer = new StringTokenizer(typeNames, new String(new char[] {EGLElement.EGLM_PART}));
		type = cu.getPart(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			//deal with inner types
			type= type.getPart(tokenizer.nextToken());
		}
	}
	return type;
}

/**
 * @see EGLElement#getHandleMemento()
 */
public String getHandleMemento(){
	return getElementName();
}
/**
 * Returns the <code>char</code> that marks the start of this handles
 * contribution to a memento.
 */
protected char getHandleMementoDelimiter(){
	Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
	return 0;
}
/**
 * @see IEGLModel
 */
public IEGLProject getEGLProject(String name) {
	return new EGLProject(ResourcesPlugin.getWorkspace().getRoot().getProject(name), this);
}
/**
 * Returns the active EGL project associated with the specified
 * resource, or <code>null</code> if no EGL project yet exists
 * for the resource.
 *
 * @exception IllegalArgumentException if the given resource
 * is not one of an IProject, IFolder, or IFile.
 */
public IEGLProject getEGLProject(IResource resource) {
	switch(resource.getType()){
		case IResource.FOLDER:
			return new EGLProject(((IFolder)resource).getProject(), this);
		case IResource.FILE:
			return new EGLProject(((IFile)resource).getProject(), this);
		case IResource.PROJECT:
			return new EGLProject((IProject)resource, this);
		default:
			throw new IllegalArgumentException(EGLModelResources.elementInvalidResourceForProject);
	}
}
/**
 * @see IEGLModel
 */
public IEGLProject[] getEGLProjects() throws EGLModelException {
	ArrayList list = getChildrenOfType(EGL_PROJECT);
	IEGLProject[] array= new IEGLProject[list.size()];
	list.toArray(array);
	return array;

}
/**
 * @see IEGLModel
 */
public Object[] getNonEGLResources() throws EGLModelException {
		return ((EGLModelInfo) getElementInfo()).getNonEGLResources();
}

/**
 * Workaround for bug 15168 circular errors not reported 
 * Returns the list of egl projects before resource delta processing
 * has started.
 */
public IEGLProject[] getOldEGLProjectsList() throws EGLModelException {
	EGLModelManager manager = EGLModelManager.getEGLModelManager();
	return 
		manager.eglProjectsCache == null ? 
			this.getEGLProjects() : 
			manager.eglProjectsCache; 
}
/*
 * @see IEGLElement
 */
public IPath getPath() {
	return Path.ROOT;
}
/*
 * @see IEGLElement
 */
public IResource getResource() {
	return ResourcesPlugin.getWorkspace().getRoot();
}
/**
 * @see IOpenable
 */
public IResource getUnderlyingResource() throws EGLModelException {
	return null;
}
/**
 * Returns the workbench associated with this object.
 */
public IWorkspace getWorkspace() {
	return ResourcesPlugin.getWorkspace();
}

/**
 * @see IEGLModel
 */
public void move(IEGLElement[] elements, IEGLElement[] containers, IEGLElement[] siblings, String[] renamings, boolean force, IProgressMonitor monitor) throws EGLModelException {
	// TODO handle later
	
	if (elements != null && elements.length > 0 && elements[0] != null && elements[0].getElementType() <= IEGLElement.EGL_FILE) {
		runOperation(new MoveResourceElementsOperation(elements, containers, force), elements, siblings, renamings, monitor);
	}/* else {
		runOperation(new MoveElementsOperation(elements, containers, force), elements, siblings, renamings, monitor);
	}
	*/
}


/**
 * @see IEGLModel
 */
public void rename(IEGLElement[] elements, IEGLElement[] destinations, String[] renamings, boolean force, IProgressMonitor monitor) throws EGLModelException {
	// TODO handle later
	MultiOperation op = null;
	if (elements != null && elements.length > 0 && elements[0] != null && elements[0].getElementType() <= IEGLElement.EGL_FILE) {
		op = new RenameResourceElementsOperation(elements, destinations, renamings, force);
	} /*else {
		op = new RenameElementsOperation(elements, destinations, renamings, force);
	}*/
	if(op != null)
	{
		op.runOperation(monitor);
	}
}
/*
 * @see EGLElement#rootedAt(IEGLProject)
 */
public IEGLElement rootedAt(IEGLProject project) {
	return this;

}
/**
 * Configures and runs the <code>MultiOperation</code>.
 */
protected void runOperation(MultiOperation op, IEGLElement[] elements, IEGLElement[] siblings, String[] renamings, IProgressMonitor monitor) throws EGLModelException {
	op.setRenamings(renamings);
	if (siblings != null) {
		for (int i = 0; i < elements.length; i++) {
			op.setInsertBefore(elements[i], siblings[i]);
		}
	}
	op.runOperation(monitor);
}
/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	buffer.append("EGL Model"); //$NON-NLS-1$
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	}
}

/**
 * Helper method - returns the targeted item (IResource if internal or egl.io.File if external), 
 * or null if unbound
 * Internal items must be referred to using container relative paths.
 */
public static Object getTarget(IContainer container, IPath path, boolean checkResourceExistence) {

	if (path == null) return null;
	
	// lookup - inside the container
	if (path.getDevice() == null) { // container relative paths should not contain a device 
												// (see http://dev.eclipse.org/bugs/show_bug.cgi?id=18684)
												// (case of a workspace rooted at d:\ )
		IResource resource = container.findMember(path);
		if (resource != null){
			if (!checkResourceExistence ||resource.exists()) return resource;
			return null;
		}
	}
	
	// if path is relative, it cannot be an external path
	// (see http://dev.eclipse.org/bugs/show_bug.cgi?id=22517)
	if (!path.isAbsolute()) return null; 

	// lookup - outside the container
	File externalFile = new File(path.toOSString());
	if (!checkResourceExistence) {
		return externalFile;
	} else if (existingExternalFiles.contains(externalFile)) {
		return externalFile;
	} else { 
		//if (EGLModelManager.ZIP_ACCESS_VERBOSE) {
		//	System.out.println("(" + Thread.currentThread() + ") [EGLModel.getTarget(...)] Checking existence of " + path.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		//}
		if (externalFile.exists()) {
			// cache external file
			existingExternalFiles.add(externalFile);
			return externalFile;
		}
	}
	return null;	
}
/**
 * Helper method - returns whether an object is afile (ie. which returns true to {@link java.io.File#isFile()}.
 */
public static boolean isFile(Object target) {
	return getFile(target) != null;
}

/**
 * Helper method - returns the file item (ie. which returns true to {@link java.io.File#isFile()},
 * or null if unbound
 */
public static synchronized File getFile(Object target) {
	if (existingExternalConfirmedFiles.contains(target))
		return (File) target;
	if (target instanceof File) {
		File f = (File) target;
		if (f.isFile()) {
			existingExternalConfirmedFiles.add(f);
			return f;
		}
	}

	return null;
}
}

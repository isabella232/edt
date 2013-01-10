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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;

/**
 * @see IPackageFragmentRoot
 */
public class PackageFragmentRoot extends Openable implements IPackageFragmentRoot {

	/**
	 * The delimiter between the source path and root path in the
	 * attachment server property.
	 */
	protected final static char ATTACHMENT_PROPERTY_DELIMITER= '*';
	
	/*
	 * No source attachment property
	 */
	public final static String NO_SOURCE_ATTACHMENT = ""; //$NON-NLS-1$
	
	
	/**
	 * The resource associated with this root.
	 * (an IResource )
	 */
	protected Object resource;
	
/**
 * Constructs a package fragment root which is the root of the java package
 * directory hierarchy.
 */
protected PackageFragmentRoot(IResource resource, IEGLProject project, String name) {
	super(PACKAGE_FRAGMENT_ROOT, project, name);
	this.resource = resource;
}

/**
 * Compute the package fragment children of this package fragment root.
 * 
 * @exception EGLModelException  The resource associated with this package fragment root does not exist
 */
protected boolean computeChildren(OpenableElementInfo info) throws EGLModelException {
	try {
		// the underlying resource may be a folder or a project (in the case that the project folder
		// is actually the package fragment root)
		IResource resource = getResource();
		if (resource.getType() == IResource.FOLDER || resource.getType() == IResource.PROJECT) {
			ArrayList vChildren = new ArrayList(5);
			char[][] exclusionPatterns = fullExclusionPatternChars();
			computeFolderChildren((IContainer) resource, "", vChildren, exclusionPatterns); //$NON-NLS-1$
			IEGLElement[] children = new IEGLElement[vChildren.size()];
			vChildren.toArray(children);
			info.setChildren(children);
		}
	} catch (EGLModelException e) {
		//problem resolving children; structure remains unknown
		info.setChildren(new IEGLElement[]{});
		throw e;
	}
	return true;
}

/**
 * Starting at this folder, create package fragments and add the fragments that are not exclused
 * to the collection of children.
 * 
 * @exception EGLModelException  The resource associated with this package fragment does not exist
 */
protected void computeFolderChildren(IContainer folder, String prefix, ArrayList vChildren, char[][] exclusionPatterns) throws EGLModelException {
	IPackageFragment pkg = getPackageFragment(prefix);
	vChildren.add(pkg);
	try {
		EGLProject javaProject = (EGLProject)getEGLProject();
		IResource[] members = folder.members();
		for (int i = 0, max = members.length; i < max; i++) {
			IResource member = members[i];
			String memberName = member.getName();
			if (member.getType() == IResource.FOLDER 
				&& Util.isValidFolderNameForPackage(memberName)
				&& !Util.isExcluded(member, exclusionPatterns)) {
					
				// eliminate binary output only if nested inside direct subfolders
				if (javaProject.contains(member)) {
					String newPrefix;
					if (prefix.length() == 0) {
						newPrefix = memberName;
					} else {
						newPrefix = prefix + "." + memberName; //$NON-NLS-1$
					}
					computeFolderChildren((IFolder) member, newPrefix, vChildren, exclusionPatterns);
				}
			}
		}
	} catch(IllegalArgumentException e){
		throw new EGLModelException(e, IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST); // could be thrown by ElementTree when path is not found
	} catch (CoreException e) {
		throw new EGLModelException(e);
	}
}
/*
 * Computes and returns the source attachment root path for the given source attachment path.
 * Returns <code>null</code> if none could be found.
 * 
 * @param sourceAttachmentPath the given absolute path to the source archive or folder
 * @return the computed source attachment root path or <code>null</cde> if none could be found
 * @throws EGLModelException
 */
 // TODO handle Source attachment later
 /*
public IPath computeSourceAttachmentRootPath(IPath sourceAttachmentPath) throws EGLModelException {
	IPath sourcePath = this.getSourceAttachmentPath();
	if (sourcePath == null) return null;
	SourceMapper mapper = 
		new SourceMapper(
		sourcePath, 
		null, // detect root path
		this.isExternal() ? EGLCore.getOptions() : this.getEGLProject().getOptions(true) // only project options if associated with resource
	);
	if (mapper.rootPath == null) return null;
	return new Path(mapper.rootPath);
}
*/
/*
 * @see com.ibm.etools.egl.internal.model.core.IPackageFragmentRoot#copy
 */
// TODO handle copy later

public void copy(
	IPath destination,
	int updateResourceFlags,
	int updateModelFlags,
	IEGLPathEntry sibling,
	IProgressMonitor monitor)
	throws EGLModelException {
	/*	
	CopyPackageFragmentRootOperation op = 
		new CopyPackageFragmentRootOperation(this, destination, updateResourceFlags, updateModelFlags, sibling);
	runOperation(op, monitor);
	*/
}


/**
 * Returns a new element info for this element.
 */
protected OpenableElementInfo createElementInfo() {
	return new PackageFragmentRootInfo();
}

/**
 * @see IPackageFragmentRoot
 */
public IPackageFragment createPackageFragment(String name, boolean force, IProgressMonitor monitor) throws EGLModelException {
	CreatePackageFragmentOperation op = new CreatePackageFragmentOperation(this, name, force);
	op.runOperation(monitor);
	return getPackageFragment(name);
}

/**
 * Returns the root's kind - K_SOURCE or K_BINARY, defaults
 * to K_SOURCE if it is not on the classpath.
 *
 * @exception NotPresentException if the project and root do
 * 		not exist.
 */
protected int determineKind(IResource underlyingResource) throws EGLModelException {
	IEGLPathEntry[] entries= ((EGLProject)getEGLProject()).getExpandedEGLPath(true);
	for (int i= 0; i < entries.length; i++) {
		IEGLPathEntry entry= entries[i];
		if (entry.getPath().equals(underlyingResource.getFullPath())) {
			return entry.getContentKind();
		}
	}
	return IPackageFragmentRoot.K_SOURCE;
}

/*
 * @see com.ibm.etools.egl.internal.core.IPackageFragmentRoot#delete
 */

public void delete(
	int updateResourceFlags,
	int updateModelFlags,
	IProgressMonitor monitor)
	throws EGLModelException {
		
	//Always maintain eglPath
	int resourceUpdateFlags= IResource.KEEP_HISTORY;
	int eCoreUpdateFlags= IPackageFragmentRoot.ORIGINATING_PROJECT_EGLPATH | IPackageFragmentRoot.OTHER_REFERRING_PROJECTS_EGLPATH;
	
	DeletePackageFragmentRootOperation op = new DeletePackageFragmentRootOperation(this, resourceUpdateFlags, eCoreUpdateFlags);
	op.runOperation(monitor);
	
}

/**
 * Compares two objects for equality;
 * for <code>PackageFragmentRoot</code>s, equality is having the
 * same <code>EGLModel</code>, same resources, and occurrence count.
 *
 */
public boolean equals(Object o) {
	if (this == o)
		return true;
	if (!(o instanceof PackageFragmentRoot))
		return false;
	PackageFragmentRoot other = (PackageFragmentRoot) o;
	return getEGLModel().equals(other.getEGLModel()) && 
			this.resource.equals(other.resource) &&
			fOccurrenceCount == other.fOccurrenceCount;
}

/**
 * @see IEGLElement
 */
public boolean exists() {
	return super.exists() 
				&& isOnEGLPath();
}


/*
 * Returns the exclusion patterns from the classpath entry associated with this root.
 */
char[][] fullExclusionPatternChars() {
	try {
		if (this.isOpen() && this.getKind() != IPackageFragmentRoot.K_SOURCE) return null;
		EGLPathEntry entry = (EGLPathEntry)getRawEGLPathEntry();
		if (entry == null) {
			return null;
		} else {
			return entry.fullExclusionPatternChars();
		}
	} catch (EGLModelException e) { 
		return null;
	}
}		

/**
 * @see Openable
 */
protected boolean generateInfos(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws EGLModelException {
	
	((PackageFragmentRootInfo) info).setRootKind(determineKind(underlyingResource));
	return computeChildren(info);
}

/**
 * @see EGLElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return EGLElement.EGLM_PACKAGEFRAGMENTROOT;
}
/**
 * @see EGLElement#getHandleMemento()
 */
public String getHandleMemento(){
	IPath path;
	IResource resource = getResource();
	if (resource != null) {
		// internal jar or regular root
		if (getResource().getProject().equals(getEGLProject().getProject())) {
			path = resource.getProjectRelativePath();
		} else {
			path = resource.getFullPath();
		}
	} else {
		// external jar
		path = getPath();
	}
	StringBuffer buff= new StringBuffer(((EGLElement)getParent()).getHandleMemento());
	buff.append(getHandleMementoDelimiter());
	buff.append(path.toString()); 
	return buff.toString();
}
/**
 * @see IPackageFragmentRoot
 */
public int getKind() throws EGLModelException {
	return ((PackageFragmentRootInfo)getElementInfo()).getRootKind();
}

/**
 * Returns an array of non-java resources contained in the receiver.
 */
public Object[] getNonEGLResources() throws EGLModelException {
	return ((PackageFragmentRootInfo) getElementInfo()).getNonEGLResources(getEGLProject(), getResource(), this);
}

/**
 * @see IPackageFragmentRoot
 */
public IPackageFragment getPackageFragment(String packageName) {
	// tolerate package names with spaces (e.g. 'x . y') (http://bugs.eclipse.org/bugs/show_bug.cgi?id=21957)
	List pkgs = new ArrayList();
	char[][] compoundName = Util.toCompoundChars(packageName);
	StringBuffer buffer = new StringBuffer(packageName.length());
	for (int i = 0, length = compoundName.length; i < length; i++) {
		pkgs.add(String.valueOf(CharOperation.trim(compoundName[i])));
	}
	packageName = buffer.toString();
	return getPackageFragment((String[])pkgs.toArray(new String[pkgs.size()]));
}
public PackageFragment getPackageFragment(String[] pkgName) {
	return new PackageFragment(this, pkgName);
}
/**
 * Returns the package name for the given folder
 * (which is a decendent of this root).
 */
protected String getPackageName(IFolder folder) throws EGLModelException {
	IPath myPath= getPath();
	IPath pkgPath= folder.getFullPath();
	int mySegmentCount= myPath.segmentCount();
	int pkgSegmentCount= pkgPath.segmentCount();
	StringBuffer name = new StringBuffer(IPackageFragment.DEFAULT_PACKAGE_NAME);
	for (int i= mySegmentCount; i < pkgSegmentCount; i++) {
		if (i > mySegmentCount) {
			name.append('.');
		}
		name.append(pkgPath.segment(i));
	}
	return name.toString();
}

/**
 * @see IEGLElement
 */
public IPath getPath() {
	return getResource().getFullPath();
}

/*
 * @see IPackageFragmentRoot 
 */
public IEGLPathEntry getRawEGLPathEntry() throws EGLModelException {

	IEGLPathEntry rawEntry = null;
	IEGLProject project = this.getEGLProject();
	project.getResolvedEGLPath(true); // force the reverse rawEntry cache to be populated
	EGLModelManager.PerProjectInfo perProjectInfo = 
		EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(project.getProject());
	if (perProjectInfo != null && perProjectInfo.resolvedPathToRawEntries != null) {
		rawEntry = (IEGLPathEntry) perProjectInfo.resolvedPathToRawEntries.get(this.getPath());
	}
	return rawEntry;
}

/*
 * @see IEGLElement
 */
public IResource getResource() {
	return (IResource)this.resource;
}


/**
 * @see IEGLElement
 */
public IResource getUnderlyingResource() throws EGLModelException {
	if (!exists()) throw newNotPresentException();
	return getResource();
}

public int hashCode() {
	return this.resource.hashCode();
}

/**
 * @see IPackageFragmentRoot
 */
public boolean isArchive() {
	return false;
}

/**
 * @see IPackageFragmentRoot
 */
public boolean isExternal() {
	return false;
}

/*
 * Returns whether this package fragment root is on the classpath of its project.
 */
protected boolean isOnEGLPath() {
	if (this.getElementType() == IEGLElement.EGL_PROJECT){
		return true;
	}
	
	IPath path = this.getPath();
	try {
		// check package fragment root on classpath of its project
		IEGLProject project = this.getEGLProject();
		IEGLPathEntry[] classpath = project.getResolvedEGLPath(true);	
		for (int i = 0, length = classpath.length; i < length; i++) {
			IEGLPathEntry entry = classpath[i];
			if (entry.getPath().equals(path)) {
				return true;
			}
		}
	} catch(EGLModelException e){
		// could not read classpath, then assume it is outside
	}
	return false;
}
/*
 * @see com.ibm.etools.egl.internal.model.core.IPackageFragmentRoot#move
 */
// TODO handle move operation later

public void move(
	IPath destination,
	int updateResourceFlags,
	int updateModelFlags,
	IEGLPathEntry sibling,
	IProgressMonitor monitor)
	throws EGLModelException {

	//Always maintain eglPath
	//int resourceUpdateFlags= IResource.NONE;	//Do not account for linked source folders
	int resourceUpdateFlags = IResource.KEEP_HISTORY | IResource.SHALLOW;
	int eCoreUpdateFlags = 	IPackageFragmentRoot.DESTINATION_PROJECT_EGLPATH |
							IPackageFragmentRoot.ORIGINATING_PROJECT_EGLPATH | 
						  	IPackageFragmentRoot.OTHER_REFERRING_PROJECTS_EGLPATH |
						  	IPackageFragmentRoot.REPLACE;

	MovePackageFragmentRootOperation op = new MovePackageFragmentRootOperation(this, destination, resourceUpdateFlags, eCoreUpdateFlags, sibling);
	op.runOperation(monitor);
}


protected void openWhenClosed(IProgressMonitor pm) throws EGLModelException {
	if (!this.resourceExists() 
			|| !this.isOnEGLPath()) {
		throw newNotPresentException();
	}
	super.openWhenClosed(pm);
}

/**
 * Recomputes the children of this element, based on the current state
 * of the workbench.
 */
public void refreshChildren() {
	try {
		OpenableElementInfo info= (OpenableElementInfo)getElementInfo();
		computeChildren(info);
	} catch (EGLModelException e) {
		// do nothing.
	}
}

/*
 * @see EGLElement#rootedAt(IEGLProject)
 */
public IEGLElement rootedAt(IEGLProject project) {
	return
		new PackageFragmentRoot(
			getResource(),
			project, 
			fName);
}

/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	if (getElementName().length() == 0) {
		buffer.append("[project root]"); //$NON-NLS-1$
	} else {
		IPath path = getPath();
		if (getEGLProject().getElementName().equals(path.segment(0))) {
			buffer.append(path.removeFirstSegments(1).makeRelative());
		} else {
			buffer.append(path);
		}
	}
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	}
}

public SourceMapper getSourceMapper() {
	SourceMapper mapper;
	try {
		PackageFragmentRootInfo rootInfo = (PackageFragmentRootInfo) getElementInfo();
		mapper = rootInfo.getSourceMapper();
		IPath sourcePath= getSourceAttachmentPath();
		if (mapper == null) {
			// first call to this method
			IPath rootPath= getSourceAttachmentRootPath();
			mapper = createSourceMapper(sourcePath, rootPath);
			rootInfo.setSourceMapper(mapper);
		} else{
			if(sourcePath == null || !sourcePath.equals(mapper.getSourcePath())){
				IPath rootPath= getSourceAttachmentRootPath();
				mapper = createSourceMapper(sourcePath, rootPath);
				rootInfo.setSourceMapper(mapper);
			}
		}
	} catch (EGLModelException e) {
		// no source can be attached
		mapper = null;
	} catch (CoreException e) {
		// TODO Auto-generated catch block
		mapper = null;
	}
	return mapper;
}


/**
 * @throws CoreException 
 * @see IPackageFragmentRoot
 */
public IPath getSourceAttachmentPath() throws CoreException {
	if (getKind() != K_BINARY) return null;

	// 1) look source attachment property (set iff attachSource(...) was called
	IPath path = getPath();
	String serverPathString= getSourceAttachmentProperty(path);
	if (serverPathString != null) {
		int index= serverPathString.lastIndexOf(ATTACHMENT_PROPERTY_DELIMITER);
		if (index < 0) {
			// no root path specified
			return new Path(serverPathString);
		} else {
			String serverSourcePathString= serverPathString.substring(0, index);
			return new Path(serverSourcePathString);
		}
	}

	// 2) look at eglpath entry
	IEGLPathEntry entry = ((EGLProject) getParent()).getEGLPathEntryFor(path);
	IPath sourceAttachmentPath;
	if (entry != null && (sourceAttachmentPath = entry.getSourceAttachmentPath()) != null)
		return sourceAttachmentPath;

	// 3) look for a recommendation
	entry = findSourceAttachmentRecommendation();
	if (entry != null && (sourceAttachmentPath = entry.getSourceAttachmentPath()) != null) {
		return sourceAttachmentPath;
	}

	return null;
}

private IEGLPathEntry findSourceAttachmentRecommendation() {
	try {
		IPath rootPath = getPath();
		IEGLPathEntry entry;

		// try on enclosing project first
		EGLProject parentProject = (EGLProject) getEGLProject();
		try {
			entry = parentProject.getEGLPathEntryFor(rootPath);
			if (entry != null) {
				Object target = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), entry.getSourceAttachmentPath(), true);
				if (target != null) {
					return entry;
				}
			}
		} catch(EGLModelException e){
			// ignore
		}

		// iterate over all projects
		IEGLModel model = getEGLModel();
		IEGLProject[] eglProjects = model.getEGLProjects();
		for (int i = 0, max = eglProjects.length; i < max; i++){
			EGLProject eglProject = (EGLProject) eglProjects[i];
			if (eglProject == parentProject) continue; // already done
			try {
				entry = eglProject.getEGLPathEntryFor(rootPath);
				if (entry != null){
					Object target = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), entry.getSourceAttachmentPath(), true);
					if (target != null) {
						return entry;
					}
				}
			} catch(EGLModelException e){
				// ignore
			}
		}
	} catch(EGLModelException e){
		// ignore
	}

	return null;
}

/*
 * Returns the source attachment property for this package fragment root's path
 */
public static String getSourceAttachmentProperty(IPath path) throws CoreException {
	Map rootPathToAttachments = EGLModelManager.getEGLModelManager().rootPathToAttachments;
	String property = (String) rootPathToAttachments.get(path);
	if (property == null) {
			property = ResourcesPlugin.getWorkspace().getRoot().getPersistentProperty(getSourceAttachmentPropertyName(path));
			if (property == null) {
				rootPathToAttachments.put(path, PackageFragmentRoot.NO_SOURCE_ATTACHMENT);
				return null;
			}
			rootPathToAttachments.put(path, property);
			return property;
	} else if (property.equals(EglarPackageFragmentRoot.NO_SOURCE_ATTACHMENT)) {
		return null;
	} else
		return property;
}

private static QualifiedName getSourceAttachmentPropertyName(IPath path) {
	return new QualifiedName(EGLCore.PLUGIN_ID, "sourceattachment: " + path.toOSString()); //$NON-NLS-1$
}

/**
 * @throws CoreException 
 * @see IPackageFragmentRoot
 */
public IPath getSourceAttachmentRootPath() throws CoreException {
	if (getKind() != K_BINARY) return null;

	// 1) look source attachment property (set iff attachSource(...) was called
	IPath path = getPath();
	String serverPathString= getSourceAttachmentProperty(path);
	if (serverPathString != null) {
		int index = serverPathString.lastIndexOf(ATTACHMENT_PROPERTY_DELIMITER);
		if (index == -1) return null;
		String serverRootPathString= IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH;
		if (index != serverPathString.length() - 1) {
			serverRootPathString= serverPathString.substring(index + 1);
		}
		return new Path(serverRootPathString);
	}
	
	// 2) look at eglpath entry
	IEGLPathEntry entry = ((EGLProject)getParent()).getEGLPathEntryFor(path);
	IPath sourceAttachmentRootPath;
	if (entry != null && (sourceAttachmentRootPath = entry.getSourceAttachmentRootPath()) != null)
		return sourceAttachmentRootPath;
	
	// 3) look for a recomendation
	entry = findSourceAttachmentRecommendation();
	if (entry != null && (sourceAttachmentRootPath = entry.getSourceAttachmentRootPath()) != null)
		return sourceAttachmentRootPath;

	return null;
}

SourceMapper createSourceMapper(IPath sourcePath, IPath rootPath) {
	SourceMapper mapper = new SourceMapper(
		sourcePath,
		rootPath == null ? null : rootPath.toOSString(),
		getEGLProject().getOptions(true)); // cannot use workspace options if external jar is 1.5 jar and workspace options are 1.4 options
	return mapper;
}

}

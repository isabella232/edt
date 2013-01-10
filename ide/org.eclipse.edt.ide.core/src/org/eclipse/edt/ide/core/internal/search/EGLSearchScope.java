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
package org.eclipse.edt.ide.core.internal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.eglar.FileInEglar;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.indexing.AbstractSearchScope;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;

/**
 * A EGL-specific scope for searching relative to one or more EGL elements.
 */
public class EGLSearchScope extends AbstractSearchScope implements IEGLSearchScope {
	
	private ArrayList elements;

	/* The paths of the resources in this search scope 
	   (or the classpath entries' paths 
	   if the resources are projects) */
	private IPath[] paths;
	private boolean[] pathWithSubFolders;
	private int pathsCount;
	
	private IPath[] enclosingProjects;

    private ArrayList parts;
    
    private Map<IPath, Set> eglarProjectsMap;
	
public EGLSearchScope() {
	this.initialize();
	
	//disabled for now as this could be expensive
	//EGLModelManager.getEGLModelManager().rememberScope(this);
}
	
private void addEnclosingProject(IPath path) {
	int length = this.enclosingProjects.length;
	for (int i = 0; i < length; i++) {
		if (this.enclosingProjects[i].equals(path)) return;
	}
	System.arraycopy(
		this.enclosingProjects,
		0,
		this.enclosingProjects = new IPath[length+1],
		0,
		length);
	this.enclosingProjects[length] = path;
}

public void add(IEGLProject javaProject, boolean includesPrereqProjects, HashSet visitedProjects) throws EGLModelException {
	add(javaProject, includesPrereqProjects, true, visitedProjects);
}

public void add(IEGLProject javaProject, boolean includesPrereqProjects, boolean addNonExportedEntries, HashSet visitedProjects) throws EGLModelException {
	IProject project = javaProject.getProject();
	if (!project.isAccessible() || !visitedProjects.add(project)) return;

	this.addEnclosingProject(project.getFullPath());

	IEGLPathEntry[] entries = ((EGLProject) javaProject).getExpandedEGLPath(true);
	IEGLModel model = javaProject.getEGLModel();
	for (int i = 0, length = entries.length; i < length; i++) {
		IEGLPathEntry entry = entries[i];
		switch (entry.getEntryKind()) {
			case IEGLPathEntry.CPE_LIBRARY:
				IPath path = entry.getPath();
				this.add(path, true);
				this.addEnclosingProject(path);
				break;
			case IEGLPathEntry.CPE_PROJECT:
				if (includesPrereqProjects && (addNonExportedEntries || entry.isExported())) {
					this.add(model.getEGLProject(entry.getPath().lastSegment()), true, false, visitedProjects);
				}
				break;
			case IEGLPathEntry.CPE_SOURCE:
				this.add(entry.getPath(), true);
				break;
		}
	}
}
public void add(IEGLElement element) throws EGLModelException {
	IPackageFragmentRoot root = null;
	switch (element.getElementType()) {
		case IEGLElement.EGL_MODEL:
			// a workspace sope should be used
			break; 
		case IEGLElement.EGL_PROJECT:
			this.add((IEGLProject)element, true, new HashSet(2));
			break;
		case IEGLElement.PACKAGE_FRAGMENT_ROOT:
			root = (IPackageFragmentRoot)element;
			this.add(root.getPath(), true);
			break;
		case IEGLElement.PACKAGE_FRAGMENT:
			root = (IPackageFragmentRoot)element.getParent();
			if (root.isArchive()) {
				this.add(root.getPath().append(new Path(element.getElementName().replace('.', '/'))), false);
			} else {
				IResource resource = element.getResource();
				if (resource != null && resource.isAccessible()) {
					this.add(resource.getFullPath(), false);
				}
			}
			break;
		default:
			// remember sub-cu (or sub-class file) java elements
			if (element instanceof IMember) {
				if (this.elements == null) {
					this.elements = new ArrayList();
				}
				this.elements.add(element);
			}
			this.add(this.fullPath(element), true);
			
			// find package fragment root including this java element
			IEGLElement parent = element.getParent();
			while (parent != null && !(parent instanceof IPackageFragmentRoot)) {
				parent = parent.getParent();
			}
			if (parent instanceof IPackageFragmentRoot) {
				root = (IPackageFragmentRoot)parent;
			}
	}
	
	if (root != null) {
		if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
			this.addEnclosingProject(root.getPath());
		} else {
			this.addEnclosingProject(root.getEGLProject().getProject().getFullPath());
		}
	}
}

public void add(Node node) throws EGLModelException {
	node.accept(new AbstractASTPartVisitor(){
		private void addPart(Node part,Name name){
			if (parts == null) {
				parts = new ArrayList();
			}
			
			parts.add(part);
			
			IFile file = getFile(name);
			if (file != null){
				add(file.getFullPath(),true);
				addEnclosingProject(file.getProject().getFullPath());
			}
			
		}
		public void visitPart(Part part){
			addPart(part,part.getName());
		}
	});
}

/**
 * Adds the given path to this search scope. Remember if subfolders need to be included as well.
 */
private void add(IPath path, boolean withSubFolders) {
	if (this.paths.length == this.pathsCount) {
		System.arraycopy(
			this.paths,
			0,
			this.paths = new IPath[this.pathsCount * 2],
			0,
			this.pathsCount);
		System.arraycopy(
			this.pathWithSubFolders,
			0,
			this.pathWithSubFolders = new boolean[this.pathsCount * 2],
			0,
			this.pathsCount);
	}
	this.paths[this.pathsCount] = path;
	this.pathWithSubFolders[this.pathsCount++] = withSubFolders; 
}

/* (non-EGLdoc)
 * @see IEGLSearchScope#encloses(String)
 */
public boolean encloses(String resourcePathString) {
	if ( resourcePathString.startsWith( FileInEglar.EGLAR_PREFIX ) ) {
		resourcePathString = resourcePathString.substring( FileInEglar.EGLAR_PREFIX.length() );
	}
	
	int index = resourcePathString.indexOf(FileInEglar.EGLAR_SEPARATOR);
	if(index != -1) {
		StringBuffer sb = new StringBuffer();
		sb.append(resourcePathString.substring(0, index));
		sb.append("/");
		sb.append(resourcePathString.substring(index + 1, resourcePathString.length()));
		resourcePathString = sb.toString();
	}
	IPath resourcePath = new Path(resourcePathString);
	return this.encloses(resourcePath);
}

/**
 * Returns whether this search scope encloses the given path.
 */
private boolean encloses(IPath path) {
	for (int i = 0; i < this.pathsCount; i++) {
		if (this.pathWithSubFolders[i]) {
			if (this.paths[i].isPrefixOf(path)) {
				return true;
			}
		} else {
			// if not looking at subfolders, this scope encloses the given path 
			// if this path is a direct child of the scope's ressource
			// or if this path is the scope's resource (see bug 13919 Declaration for package not found if scope is not project)
			IPath scopePath = this.paths[i];
			if (scopePath.isPrefixOf(path) 
				&& ((scopePath.segmentCount() == path.segmentCount() - 1)
					|| (scopePath.segmentCount() == path.segmentCount()))) {
				return true;
			}
		}
	}
	return false;
}

protected IFile getFile(Part part){
	return getFile(part.getName());
}
protected IFile getFile(Name part){
	// TODO No code references this anymore (parts browser and parts reference views were removed), and the new model doesn't have enough info to resolve the IFile.
//	if (part != null){
//		IBinding binding = part.resolveBinding();
//		if (binding != null && binding != IBinding.NOT_FOUND_BINDING){
//			IPartBinding partBinding = null;
//			if (binding.isDataBinding()){
//				partBinding = ((IDataBinding)binding).getDeclaringPart();
//			}else if (binding.isTypeBinding() && ((ITypeBinding)binding).isPartBinding()){
//				partBinding = (IPartBinding)binding;
//			}else if (binding.isFunctionBinding()){
//				partBinding  = (IPartBinding)binding;
//			}
//			
//			if (partBinding != null && partBinding.getEnvironment() != null){
//				IEnvironment ienv = partBinding.getEnvironment();
//				if (ienv instanceof WorkingCopyProjectEnvironment) {
//					WorkingCopyProjectEnvironment environment = (WorkingCopyProjectEnvironment) partBinding.getEnvironment();
//					IFile declaringFile = environment.getPartOrigin(partBinding.getPackageName(), partBinding.getName()).getEGLFile();
//					return declaringFile;
//				}
//			}
//		}
//	}
	return null;
}
/* (non-EGLdoc)
 * @see IEGLSearchScope#encloses(IEGLElement)
 */
public boolean encloses(IEGLElement element) {

	if (this.parts != null) {
	    for (Iterator iter = parts.iterator(); iter.hasNext();) {
            Node part = (Node) iter.next();
            
            try {
	            // Work up the part tree to the file level
	            IEGLElement searchedElement = element.getAncestor(IEGLElement.PART); // may return same element
	            while(searchedElement != null){
	                if(part.getOffset() == ((IMember)searchedElement).getSourceRange().getOffset()){
                        // Verify that these two parts are in the same file
	                	
                        IFile partFile = null;
                        if (part instanceof Part){
                        	partFile = getFile((Part)part);
                        }
                        	
                        IEGLElement elementFile = searchedElement.getAncestor(IEGLElement.EGL_FILE);
                        if (partFile != null && elementFile != null){
                        	if (partFile.equals(((IEGLFile)elementFile).getCorrespondingResource())){
                        		return true;
                        	}else break;
                        }else{
                            break;
                        }
                    }else{
                        searchedElement = searchedElement.getParent().getAncestor(IEGLElement.PART);
                    }
	            }
            } catch (EGLModelException e) {
                // continue to the next part
            }
        }
	    return false;
	}else if (this.elements != null) {
	    
		for (int i = 0, length = this.elements.size(); i < length; i++) {
			IEGLElement scopeElement = (IEGLElement)this.elements.get(i);
			IEGLElement searchedElement = element;
			while (searchedElement != null) {
				if (searchedElement.equals(scopeElement)) {
					return true;
				} else {
					searchedElement = searchedElement.getParent();
				}
			}
		}
		return false;
	} else {
		return this.encloses(this.fullPath(element));
	}
}

/* (non-EGLdoc)
 * @see IEGLSearchScope#enclosingProjects()
 */
public IPath[] enclosingProjects() {
	return this.enclosingProjects;
}
private IPath fullPath(IEGLElement element) {
	if (element instanceof IPackageFragmentRoot) {
		return ((IPackageFragmentRoot)element).getPath();
	} else 	{
		IEGLElement parent = element.getParent();
		IPath parentPath = parent == null ? null : this.fullPath(parent);
		IPath childPath;
		if (element instanceof IPackageFragment) {
			childPath = new Path(element.getElementName().replace('.', '/'));
		} else if (element instanceof IOpenable) {
			childPath = new Path(element.getElementName());
		} else {
			return parentPath;
		}
		return parentPath == null ? childPath : parentPath.append(childPath);
	}
}

protected void initialize() {
	this.paths = new IPath[1];
	this.pathWithSubFolders = new boolean[1];
	this.pathsCount = 0;
	this.enclosingProjects = new IPath[0];
}
/*
 * @see AbstractSearchScope#processDelta(IEGLElementDelta)
 */
public void processDelta(IEGLElementDelta delta) {
	switch (delta.getKind()) {
		case IEGLElementDelta.CHANGED:
			IEGLElementDelta[] children = delta.getAffectedChildren();
			for (int i = 0, length = children.length; i < length; i++) {
				IEGLElementDelta child = children[i];
				this.processDelta(child);
			}
			break;
		case IEGLElementDelta.REMOVED:
			IEGLElement element = delta.getElement();
			if (this.encloses(element)) {
				if (this.elements != null) {
					this.elements.remove(element);
				} 
				IPath path = null;
				switch (element.getElementType()) {
					case IEGLElement.EGL_PROJECT:
						path = ((IEGLProject)element).getProject().getFullPath();
					case IEGLElement.PACKAGE_FRAGMENT_ROOT:
						if (path == null) {
							path = ((IPackageFragmentRoot)element).getPath();
						}
						int toRemove = -1;
						for (int i = 0; i < this.pathsCount; i++) {
							if (this.paths[i].equals(path)) {
								toRemove = i;
								break;
							}
						}
						if (toRemove != -1) {
							int last = this.pathsCount-1;
							if (toRemove != last) {
								this.paths[toRemove] = this.paths[last];
								this.pathWithSubFolders[toRemove] = this.pathWithSubFolders[last];
							}
							this.pathsCount--;
						}
				}
			}
			break;
	}
}
public String toString() {
	StringBuffer result = new StringBuffer("EGLSearchScope on "); //$NON-NLS-1$
	if (this.elements != null) {
		result.append("["); //$NON-NLS-1$
		for (int i = 0, length = this.elements.size(); i < length; i++) {
			EGLElement element = (EGLElement)this.elements.get(i);
			result.append("\n\t"); //$NON-NLS-1$
			result.append(element.toStringWithAncestors());
		}
		result.append("\n]"); //$NON-NLS-1$
	} else {
		if (this.pathsCount == 0) {
			result.append("[empty scope]"); //$NON-NLS-1$
		} else {
			result.append("["); //$NON-NLS-1$
			for (int i = 0; i < this.pathsCount; i++) {
				IPath path = this.paths[i];
				result.append("\n\t"); //$NON-NLS-1$
				result.append(path.toString());
			}
			result.append("\n]"); //$NON-NLS-1$
		}
	}
	return result.toString();
}

  public void putIntoEglarProjectsMap(IPath eglarPath, IEGLProject projPath){
	  if(eglarProjectsMap == null){
		 eglarProjectsMap = new HashMap<IPath, Set>();
	  }
	  Set<IEGLProject> projects = eglarProjectsMap.get(eglarPath);
	  if(projects == null){
		projects = new HashSet<IEGLProject>();
		eglarProjectsMap.put(eglarPath, projects);
	  }
	 projects.add(projPath);
  }

  public Map<IPath, Set> getEglarProjectsMap(){
	 return eglarProjectsMap;
  }

}

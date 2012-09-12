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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.ElementChangedEvent;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

import org.eclipse.edt.ide.core.internal.model.indexing.IndexManager;

/**
 * <code>IResourceDelta</code>s into <code>IEGLElementDelta</code>s.
 * It also does some processing on the <code>EGLElement</code>s involved
 * (e.g. closing them or updating eglpaths).
 */
public class DeltaProcessor implements IResourceChangeListener {
	
	final static int IGNORE = 0;
	final static int SOURCE = 1;
	final static int BINARY = 2;
	
	final static int NON_EGL_RESOURCE = -1;
	
	/**
	 * The <code>EGLElementDelta</code> corresponding to the <code>IResourceDelta</code> being translated.
	 */
	protected EGLElementDelta currentDelta;
	
	private List projectChangedListeners = new ArrayList();
	
	protected IndexManager indexManager = new IndexManager();
		
	/* A table from IPath (from a eglpath entry) to RootInfo */
	public Map roots;
	
	/* A table from IPath (from a eglpath entry) to ArrayList of RootInfo
	 * Used when an IPath corresponds to more than one root */
	Map otherRoots;
	
	/* Whether the roots tables should be recomputed */
	public boolean rootsAreStale = true;
	
	/* A table from IPath (from a eglpath entry) to RootInfo
	 * from the last time the delta processor was invoked. */
	public Map oldRoots;

	/* A table from IPath (from a eglpath entry) to ArrayList of RootInfo
	 * from the last time the delta processor was invoked.
	 * Used when an IPath corresponds to more than one root */
	Map oldOtherRoots;
	
	/* A table from IPath (a source attachment path from a eglpath entry) to IPath (a root path) */
	Map sourceAttachments;

	/* The egl element that was last created (see createElement(IResource)). 
	 * This is used as a stack of egl elements (using getParent() to pop it, and 
	 * using the various get*(...) to push it. */
	Openable currentElement;
		
	public HashMap externalTimeStamps = new HashMap();
	public HashSet projectsToUpdate = new HashSet();
	public HashSet changedProjects = new HashSet();
	// list of root projects which namelookup caches need to be updated for dependents
	// TODO: (jerome) is it needed? projectsToUpdate might be sufficient
	public HashSet projectsForDependentNamelookupRefresh = new HashSet();  
	
	EGLModelManager manager;
	
	/* A table from IEGLProject to an array of IPackageFragmentRoot.
	 * This table contains the pkg fragment roots of the project that are being deleted.
	 */
	public Map removedRoots;
	
	/**
	 * Workaround for bug 15168 circular errors not reported
	 * This is a cache of the projects before any project addition/deletion has started.
	 */
	private HashSet eglProjectNamesCache;
	/*
	 * A list of IEGLElement used as a scope for external archives refresh during POST_CHANGE.
	 * This is null if no refresh is needed.
	 */
	HashSet refreshedElements;
	public static boolean VERBOSE = false;
	public static final int DEFAULT_CHANGE_EVENT = 0; // must not collide with ElementChangedEvent event masks
	class OutputsInfo {
		IPath[] paths;
		int[] traverseModes;
		int outputCount;
		OutputsInfo(IPath[] paths, int[] traverseModes, int outputCount) {
			this.paths = paths;
			this.traverseModes = traverseModes;
			this.outputCount = outputCount;
		}
		public String toString() {
			if (this.paths == null) return "<none>"; //$NON-NLS-1$
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < this.outputCount; i++) {
				buffer.append("path="); //$NON-NLS-1$
				buffer.append(this.paths[i].toString());
				buffer.append("\n->traverse="); //$NON-NLS-1$
				switch (this.traverseModes[i]) {
					case BINARY:
						buffer.append("BINARY"); //$NON-NLS-1$
						break;
					case IGNORE:
						buffer.append("IGNORE"); //$NON-NLS-1$
						break;
					case SOURCE:
						buffer.append("SOURCE"); //$NON-NLS-1$
						break;
					default:
						buffer.append("<unknown>"); //$NON-NLS-1$
				}
				if (i+1 < this.outputCount) {
					buffer.append('\n');
				}
			}
			return buffer.toString();
		}
	}
	class RootInfo {
		IEGLProject project;
		IPath rootPath;
		char[][] exclusionPatterns;
		RootInfo(IEGLProject project, IPath rootPath, char[][] exclusionPatterns) {
			this.project = project;
			this.rootPath = rootPath;
			this.exclusionPatterns = exclusionPatterns;
		}
		boolean isRootOfProject(IPath path) {
			return this.rootPath.equals(path) && this.project.getProject().getFullPath().isPrefixOf(path);
		}
		public String toString() {
			StringBuffer buffer = new StringBuffer("project="); //$NON-NLS-1$
			if (this.project == null) {
				buffer.append("null"); //$NON-NLS-1$
			} else {
				buffer.append(this.project.getElementName());
			}
			buffer.append("\npath="); //$NON-NLS-1$
			if (this.rootPath == null) {
				buffer.append("null"); //$NON-NLS-1$
			} else {
				buffer.append(this.rootPath.toString());
			}
			buffer.append("\nexcluding="); //$NON-NLS-1$
			if (this.exclusionPatterns == null) {
				buffer.append("null"); //$NON-NLS-1$
			} else {
				for (int i = 0, length = this.exclusionPatterns.length; i < length; i++) {
					buffer.append(new String(this.exclusionPatterns[i]));
					if (i < length-1) {
						buffer.append("|"); //$NON-NLS-1$
					}
				}
			}
			return buffer.toString();
		}
	}
	private final static String EXTERNAL_JAR_ADDED = "external jar added"; //$NON-NLS-1$
	private final static String EXTERNAL_JAR_CHANGED = "external jar changed"; //$NON-NLS-1$
	private final static String EXTERNAL_JAR_REMOVED = "external jar removed"; //$NON-NLS-1$
	private final static String EXTERNAL_JAR_UNCHANGED = "external jar unchanged"; //$NON-NLS-1$
	private final static String INTERNAL_JAR_IGNORE = "internal jar ignore"; //$NON-NLS-1$
	
	public int[] elementChangedListenerMasks = new int[5];
	public int elementChangedListenerCount = 0;
	
	DeltaProcessor(EGLModelManager manager) {
		this.manager = manager;
	}

	/*
	 * Adds the dependents of the given project to the list of the projects
	 * to update.
	 */
	void addDependentProjects(IPath projectPath, HashSet result) {
		try {
			IEGLProject[] projects = this.manager.getEGLModel().getEGLProjects();
			for (int i = 0, length = projects.length; i < length; i++) {
				IEGLProject project = projects[i];
				IEGLPathEntry[] eglpath = ((EGLProject)project).getExpandedEGLPath(true);
				for (int j = 0, length2 = eglpath.length; j < length2; j++) {
					IEGLPathEntry entry = eglpath[j];
						if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT
								&& entry.getPath().equals(projectPath)) {
							result.add(project);
						}
					}
				}
		} catch (EGLModelException e) {
		}
	}
	/*
	 * Adds the given element to the list of elements used as a scope for external jars refresh.
	 */
	public void addForRefresh(IEGLElement element) {
		if (this.refreshedElements == null) {
			this.refreshedElements = new HashSet();
		}
		this.refreshedElements.add(element);
	}
	public synchronized HashSet removeExternalElementsToRefresh() {
		HashSet result = this.refreshedElements;
		this.refreshedElements = null;
		return result;
	}
	/*
	 * Adds the given project and its dependents to the list of the projects
	 * to update.
	 */
	void addToProjectsToUpdateWithDependents(IProject project) {
		this.projectsToUpdate.add(EGLCore.create(project));
		this.addDependentProjects(project.getFullPath(), this.projectsToUpdate);
	}
	
	/**
	 * Adds the given child handle to its parent's cache of children. 
	 */
	protected void addToParentInfo(Openable child) {

		Openable parent = (Openable) child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				EGLElementInfo info = (EGLElementInfo)parent.getElementInfo();
				info.addChild(child);
			} catch (EGLModelException e) {
				// do nothing - we already checked if open
			}
		}
	}

	/*
	 * Check if external archives have changed and create the corresponding deltas.
	 * Returns whether at least on delta was created.
	 */
	public boolean createExternalArchiveDelta(HashSet refreshedElements,IProgressMonitor monitor) {
		HashMap externalArchivesStatus = new HashMap();
		boolean hasDelta = false;

		// find JARs to refresh
		HashSet archivePathsToRefresh = new HashSet();
		Iterator iterator = refreshedElements.iterator();
		while (iterator.hasNext()) {
			IEGLElement element = (IEGLElement)iterator.next();
			switch(element.getElementType()){
				case IEGLElement.PACKAGE_FRAGMENT_ROOT :
					archivePathsToRefresh.add(element.getPath());
					break;
				case IEGLElement.EGL_PROJECT :
					EGLProject eglProject = (EGLProject) element;
					if (!EGLProject.hasEGLNature(eglProject.getProject())) {
						// project is not accessible or has lost its Java nature
						break;
					}
					IEGLPathEntry[] classpath;
					try {
						classpath = eglProject.getResolvedEGLPath(true);
						for (int j = 0, cpLength = classpath.length; j < cpLength; j++){
							if (classpath[j].getEntryKind() == IEGLPathEntry.CPE_LIBRARY){
								archivePathsToRefresh.add(classpath[j].getPath());
							}
						}
					} catch (EGLModelException e) {
						// project doesn't exist -> ignore
					}
					break;
				case IEGLElement.EGL_MODEL :
					Iterator projectNames = this.getOldJavaProjecNames().iterator();
					while (projectNames.hasNext()) {
						String projectName = (String) projectNames.next();
						IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
						if (!EGLProject.hasEGLNature(project)) {
							// project is not accessible or has lost its Java nature
							continue;
						}
						eglProject = (EGLProject) EGLCore.create(project);
						try {
							classpath = eglProject.getResolvedEGLPath(true);
							for (int k = 0, cpLength = classpath.length; k < cpLength; k++){
								if (classpath[k].getEntryKind() == IEGLPathEntry.CPE_LIBRARY){
									archivePathsToRefresh.add(classpath[k].getPath());
								}
							}
						} catch (EGLModelException e2) {
							// project doesn't exist -> ignore
							continue;
						}
					}
					break;
			}
		}

		// perform refresh
		Iterator projectNames = this.getOldJavaProjecNames().iterator();
		IWorkspaceRoot wksRoot = ResourcesPlugin.getWorkspace().getRoot();
		while (projectNames.hasNext()) {

			if (monitor != null && monitor.isCanceled()) break;

			String projectName = (String) projectNames.next();
			IProject project = wksRoot.getProject(projectName);
			if (!EGLProject.hasEGLNature(project)) {
				// project is not accessible or has lost its Java nature
				continue;
			}
			EGLProject javaProject = (EGLProject) EGLCore.create(project);
			IEGLPathEntry[] entries;
			try {
				entries = javaProject.getResolvedEGLPath(true);
			} catch (EGLModelException e1) {
				// project does not exist -> ignore
				continue;
			}
			for (int j = 0; j < entries.length; j++){
				if (entries[j].getEntryKind() == IEGLPathEntry.CPE_LIBRARY) {
					IPath entryPath = entries[j].getPath();

					if (!archivePathsToRefresh.contains(entryPath)) continue; // not supposed to be refreshed

					String status = (String)externalArchivesStatus.get(entryPath);
					if (status == null){

						// compute shared status
						IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
						Object targetLibrary = EGLModel.getTarget(root, entryPath, true);

						if (targetLibrary == null){ // missing JAR
							if (this.getExternalLibTimeStamps().remove(entryPath) != null /* file was known*/
									&& this.roots.get(entryPath) != null /* and it was on the classpath*/) {
								externalArchivesStatus.put(entryPath, EXTERNAL_JAR_REMOVED);
								// the jar was physically removed: remove the index
								this.manager.getIndexManager().removeIndex(entryPath);
							}

						} else if (targetLibrary instanceof File){ // external JAR

							File externalFile = (File)targetLibrary;

							// check timestamp to figure if JAR has changed in some way
							Long oldTimestamp =(Long) this.getExternalLibTimeStamps().get(entryPath);
							long newTimeStamp = getTimeStamp(externalFile);
							if (oldTimestamp != null){

								if (newTimeStamp == 0){ // file doesn't exist
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_REMOVED);
									this.getExternalLibTimeStamps().remove(entryPath);
									// remove the index
									this.manager.getIndexManager().removeIndex(entryPath);

								} else if (oldTimestamp.longValue() != newTimeStamp){
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_CHANGED);
									this.getExternalLibTimeStamps().put(entryPath, new Long(newTimeStamp));
									// first remove the index so that it is forced to be re-indexed
									this.manager.getIndexManager().removeIndex(entryPath);
									// then index the jar
									this.manager.getIndexManager().indexLibrary(entryPath, project.getProject());
								} else {
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_UNCHANGED);
								}
							} else {
								if (newTimeStamp == 0){ // jar still doesn't exist
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_UNCHANGED);
								} else {
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_ADDED);
									this.getExternalLibTimeStamps().put(entryPath, new Long(newTimeStamp));
									// index the new jar
									this.manager.getIndexManager().indexLibrary(entryPath, project.getProject());
								}
							}
						} else { // internal JAR
							externalArchivesStatus.put(entryPath, INTERNAL_JAR_IGNORE);
						}
					}
					// according to computed status, generate a delta
					status = (String)externalArchivesStatus.get(entryPath);
					if (status != null){
						if (status == EXTERNAL_JAR_ADDED){
							PackageFragmentRoot root = (PackageFragmentRoot) javaProject.getPackageFragmentRoot(entryPath.toString());
							if (VERBOSE){
								System.out.println("- External JAR ADDED, affecting root: "+root.getElementName()); //$NON-NLS-1$
							}
							elementAdded(root, null, null);
							//TODO Rocky: no chained jar? javaProject.resetResolvedClasspath(); // in case it contains a chained jar
							//TODO Rocky: for what? this.state.addClasspathValidation(javaProject); // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=185733
							hasDelta = true;
						} else if (status == EXTERNAL_JAR_CHANGED) {
							PackageFragmentRoot root = (PackageFragmentRoot) javaProject.getPackageFragmentRoot(entryPath.toString());
							if (VERBOSE){
								System.out.println("- External JAR CHANGED, affecting root: "+root.getElementName()); //$NON-NLS-1$
							}
							contentChanged(root);
							//TODO Rocky: no chained jar? javaProject.resetResolvedClasspath(); // in case it contains a chained jar
							hasDelta = true;
						} else if (status == EXTERNAL_JAR_REMOVED) {
							PackageFragmentRoot root = (PackageFragmentRoot) javaProject.getPackageFragmentRoot(entryPath.toString());
							if (VERBOSE){
								System.out.println("- External JAR REMOVED, affecting root: "+root.getElementName()); //$NON-NLS-1$
							}
							elementRemoved(root, null, null);
							//TODO Rocky: no chained jar? javaProject.resetResolvedClasspath(); // in case it contains a chained jar
							//TODO Rocky:for what? this.state.addClasspathValidation(javaProject); // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=185733
							hasDelta = true;
						}
					}
				}
			}
		}
		// ensure the external file cache is reset so that if a .jar file is deleted but no longer on the classpath, it won't appear as changed next time it is added
		EGLModel.flushExternalFileCache();

		if (hasDelta){
			// flush jar type cache
			EGLModelManager.getEGLModelManager().resetJarTypeCache();
		}
		return hasDelta;
	}
	
	/*
	 * Check all external archive (referenced by given roots, projects or model) status and issue a corresponding root delta.
	 * Also triggers index updates
	 */
	public void checkExternalArchiveChanges(IEGLElement[] elementsScope, IProgressMonitor monitor) throws EGLModelException {
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
		try {
			if (monitor != null) monitor.beginTask("", 1); //$NON-NLS-1$

			for (int i = 0, length = elementsScope.length; i < length; i++) {
				this.addForRefresh(elementsScope[i]);
			}
			HashSet elementsToRefresh = this.removeExternalElementsToRefresh();
			boolean hasDelta = elementsToRefresh != null && createExternalArchiveDelta(elementsToRefresh, monitor);
			if (hasDelta){
				IEGLElementDelta[] projectDeltas = this.currentDelta.getAffectedChildren();
				final int length = projectDeltas.length;
				final IProject[] projectsToTouch = new IProject[length];
				for (int i = 0; i < length; i++) {
					IEGLElementDelta delta = projectDeltas[i];
					EGLProject javaProject = (EGLProject)delta.getElement();
					projectsToTouch[i] = javaProject.getProject();
				}

				// touch the projects to force them to be recompiled while taking the workspace lock
				// so that there is no concurrency with the Java builder
				// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=96575
				IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
					public void run(IProgressMonitor progressMonitor) throws CoreException {
						for (int i = 0; i < length; i++) {
							IProject project = projectsToTouch[i];

							// touch to force a build of this project
//							if (JavaBuilder.DEBUG)
//								System.out.println("Touching project " + project.getName() + " due to external jar file change"); //$NON-NLS-1$ //$NON-NLS-2$
							project.touch(progressMonitor);
						}
					}
				};
				try {
					ResourcesPlugin.getWorkspace().run(runnable, monitor);
				} catch (CoreException e) {
					throw new EGLModelException(e);
				}

				if (this.currentDelta != null) { // if delta has not been fired while creating markers
					EGLModelManager.getEGLModelManager().fire(this.currentDelta, DEFAULT_CHANGE_EVENT);
				}
			} 
		} finally {
			this.currentDelta = null;
			if (monitor != null) monitor.done();
		}
	}	
	
	/*
	 * Generic processing for elements with changed contents:<ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * <li>An entry is made in the delta reporting a content change (K_CHANGE with F_CONTENT flag set).
	 * </ul>
	 * Delta argument could be null if processing an external JAR change
	 */
	private void contentChanged(Openable element) {
		currentDelta().changed(element, IEGLElementDelta.F_CONTENT);
//		boolean isPrimary = false;
//		boolean isPrimaryWorkingCopy = false;
//		if (element.getElementType() == IEGLElement.COMPILATION_UNIT) {
//			CompilationUnit cu = (CompilationUnit)element;
//			isPrimary = cu.isPrimary();
//			isPrimaryWorkingCopy = isPrimary && cu.isWorkingCopy();
//		}
//		if (isPrimaryWorkingCopy) {
//			// filter out changes to primary compilation unit in working copy mode
//			// just report a change to the resource (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=59500)
//			currentDelta().changed(element, IJavaElementDelta.F_PRIMARY_RESOURCE);
//		} else {
//			close(element);
//			int flags = IJavaElementDelta.F_CONTENT;
//			if (element instanceof JarPackageFragmentRoot){
//				flags |= IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED;
//				// need also to reset project cache otherwise it will be out-of-date
//				// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=162621
//				this.projectCachesToReset.add(element.getJavaProject());
//			}
//			if (isPrimary) {
//				flags |= IJavaElementDelta.F_PRIMARY_RESOURCE;
//			}
//			currentDelta().changed(element, flags);
//		}
	}	
	/*
	 * Workaround for bug 15168 circular errors not reported
	 * Returns the list of java projects before resource delta processing
	 * has started.
	 */
	public synchronized HashSet getOldJavaProjecNames() {
		if (this.eglProjectNamesCache == null) {
			HashSet result = new HashSet();
			IEGLProject[] projects;
			try {
				projects = EGLModelManager.getEGLModelManager().getEGLModel().getEGLProjects();
			} catch (EGLModelException e) {
				return this.eglProjectNamesCache;
			}
			for (int i = 0, length = projects.length; i < length; i++) {
				IEGLProject project = projects[i];
				result.add(project.getElementName());
			}
			return this.eglProjectNamesCache = result;
		}
		return this.eglProjectNamesCache;
	}
	
	public HashMap getExternalLibTimeStamps() {
		if (this.externalTimeStamps == null) {
			HashMap timeStamps = new HashMap();
			File timestampsFile = getTimeStampsFile();
			DataInputStream in = null;
			try {
				in = new DataInputStream(new BufferedInputStream(new FileInputStream(timestampsFile)));
				int size = in.readInt();
				while (size-- > 0) {
					String key = in.readUTF();
					long timestamp = in.readLong();
					timeStamps.put(Path.fromPortableString(key), new Long(timestamp));
				}
			} catch (IOException e) {
				if (timestampsFile.exists())
					Util.log(e, "Unable to read external time stamps"); //$NON-NLS-1$
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// nothing we can do: ignore
					}
				}
			}
			this.externalTimeStamps = timeStamps;
		}
		return this.externalTimeStamps;
	}
	
	private File getTimeStampsFile() {
		return EDTCoreIDEPlugin.getPlugin().getStateLocation().append("externalLibsTimeStamps").toFile(); //$NON-NLS-1$
	}
	
	EGLElementDelta currentDelta() {
		if (this.currentDelta == null) {
			this.currentDelta = new EGLElementDelta(this.manager.getEGLModel());
		}
		return this.currentDelta;
	}
	/*
	 * Process the given delta and look for projects being added, opened, closed or
	 * with a egl nature being added or removed.
	 * Note that projects being deleted are checked in deleting(IProject).
	 * In all cases, add the project's dependents to the list of projects to update
	 * so that the eglpath related markers can be updated.
	 */
	public void checkProjectsBeingAddedOrRemoved(IResourceDelta delta) {
		IResource resource = delta.getResource();
		boolean processChildren = false;
		
		switch (resource.getType()) {
			case IResource.ROOT :
				// workaround for bug 15168 circular errors not reported 
				if (this.manager.eglProjectsCache == null) {
					try {
						this.manager.eglProjectsCache = this.manager.getEGLModel().getEGLProjects();
					} catch (EGLModelException e) {
					}
				}
				processChildren = true;
				break;
			case IResource.PROJECT :
				// NB: No need to check project's nature as if the project is not a egl project:
				//     - if the project is added or changed this is a noop for projectsBeingDeleted
				//     - if the project is closed, it has already lost its egl nature
				IProject project = (IProject)resource;
				EGLProject eglProject = (EGLProject)EGLCore.create(project);
				int deltaKind = delta.getKind();
				if (deltaKind == IResourceDelta.ADDED) {
					// remember project and its dependents
					this.addToProjectsToUpdateWithDependents(project);
					changedProjects.add(project);
					
					// workaround for bug 15168 circular errors not reported 
					if (EGLProject.hasEGLNature(project)) {
						this.addToParentInfo(eglProject);
					}
					
					rootsAreStale = true;

				} else if (deltaKind == IResourceDelta.CHANGED) {
					if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
						// project opened or closed: remember  project and its dependents
						this.addToProjectsToUpdateWithDependents(project);
						changedProjects.add(project);
					
						// workaround for bug 15168 circular errors not reported 
						if (project.isOpen()) {
							if (EGLProject.hasEGLNature(project)) {
								this.addToParentInfo(eglProject);
							}
						} else {
							try {
								eglProject.close();
							} catch (EGLModelException e) {
							}
							this.removeFromParentInfo(eglProject);
							this.manager.removePerProjectInfo(eglProject);
							
						}
						rootsAreStale = true;
					} else if ((delta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
						boolean wasEGLProject = this.manager.getEGLModel().findEGLProject(project) != null;
						boolean isEGLProject = EGLProject.hasEGLNature(project);
						if (wasEGLProject != isEGLProject) {
							// egl nature added or removed: remember  project and its dependents
							this.addToProjectsToUpdateWithDependents(project);
							changedProjects.add(project);

							// workaround for bug 15168 circular errors not reported 
							if (isEGLProject) {
								this.addToParentInfo(eglProject);
							} else {
								this.manager.removePerProjectInfo(eglProject);
								// TODO flush eglpath markers
								/*
								eglProject.
									flushEGLPathProblemMarkers(
										true, // flush cycle markers
										true  //flush eglpath format markers
									);
									
								// remove problems and tasks created  by the builder
								EGLBuilder.removeProblemsAndTasksFor(project);
								*/
								// close project
								try {
									eglProject.close();
								} catch (EGLModelException e) {
								}
								this.removeFromParentInfo(eglProject);
							}
							rootsAreStale = true;
						} else {
							// in case the project was removed then added then changed (see bug 19799)
							if (EGLProject.hasEGLNature(project)) { // need nature check - 18698
								this.addToParentInfo(eglProject);
								processChildren = true;
							}
						}
					} else {
						// workaround for bug 15168 circular errors not reported 
						// in case the project was removed then added then changed
						if (EGLProject.hasEGLNature(project)) { // need nature check - 18698
							this.addToParentInfo(eglProject);
							processChildren = true;
						}	
						
						rootsAreStale = true;
					}					
				}else if (deltaKind == IResourceDelta.REMOVED) {
					//remove classpath cache so that initializeRoots() will not consider the project has a classpath
					this.manager.removePerProjectInfo(eglProject);
					changedProjects.add(project);
					
					rootsAreStale = true;
				}
				break;
			case IResource.FILE :
				IFile file = (IFile) resource;
				/* check eglpath file change */
				if (file.getName().equals(EGLProject.EGLPATH_FILENAME)) {
					reconcileEGLPathFileUpdate(delta, file, (EGLProject)EGLCore.create(file.getProject()));
					this.rootsAreStale = true;
					break;
				}
				break;
		}
		if (processChildren) {
			IResourceDelta[] children = delta.getAffectedChildren();
			for (int i = 0; i < children.length; i++) {
				checkProjectsBeingAddedOrRemoved(children[i]);
			}
		}
	}
	
	private void checkEGLPathChange(IResourceDelta delta, IResource res) {
		if (res.getName().equals(".eglPath")) {
			changedProjects.add(res.getProject());
			
		}
	}

	

	// TODO Do not handle binaries with source yet
	private void checkSourceAttachmentChange(IResourceDelta delta, IResource res) {
		IPath rootPath = (IPath)this.sourceAttachments.get(res.getFullPath());
		if (rootPath != null) {
			RootInfo rootInfo = this.rootInfo(rootPath, delta.getKind());
			if (rootInfo != null) {
				IEGLProject projectOfRoot = rootInfo.project;
				IPackageFragmentRoot root = null;
				try {
					// close the root so that source attachement cache is flushed
					root = projectOfRoot.findPackageFragmentRoot(rootPath);
					if (root != null) {
						root.close();
					}
				} catch (EGLModelException e) {
				}
				if (root == null) return;
				switch (delta.getKind()) {
					case IResourceDelta.ADDED:
						currentDelta().sourceAttached(root);
						break;
					case IResourceDelta.CHANGED:
						currentDelta().sourceDetached(root);
						currentDelta().sourceAttached(root);
						break;
					case IResourceDelta.REMOVED:
						currentDelta().sourceDetached(root);
						break;
				}
			} 
		}
	}

	/**
	 * Closes the given element, which removes it from the cache of open elements.
	 */
	protected static void close(Openable element) {

		try {
			element.close();
		} catch (EGLModelException e) {
			// do nothing
		}
	}
	/**
	 * Generic processing for elements with changed contents:<ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * <li>An entry is made in the delta reporting a content change (K_CHANGE with F_CONTENT flag set).
	 * </ul>
	 * Delta argument could be null if processing an external JAR change
	 */
	protected void contentChanged(Openable element, IResourceDelta delta) {

		close(element);
		int flags = IEGLElementDelta.F_CONTENT;
		currentDelta().changed(element, flags);
	}
	
	/**
	 * Creates the openables corresponding to this resource.
	 * Returns null if none was found.
	 */
	protected Openable createElement(IResource resource, int elementType, RootInfo rootInfo) {
		if (resource == null) return null;
		
		IPath path = resource.getFullPath();
		IEGLElement element = null;
		switch (elementType) {
			
			case IEGLElement.EGL_PROJECT:
			
				// note that non-egl resources rooted at the project level will also enter this code with
				// an elementType EGL_PROJECT (see #elementType(...)).
				if (resource instanceof IProject){

					this.popUntilPrefixOf(path);
					
					if (this.currentElement != null 
						&& this.currentElement.getElementType() == IEGLElement.EGL_PROJECT
						&& ((IEGLProject)this.currentElement).getProject().equals(resource)) {
						return this.currentElement;
					}
					if  (rootInfo != null && rootInfo.project.getProject().equals(resource)){
						element = (Openable)rootInfo.project;
						break;
					}
					IProject proj = (IProject)resource;
					if (EGLProject.hasEGLNature(proj)) {
						element = EGLCore.create(proj);
					} else {
						// egl project may have been been closed or removed (look for
						// element amongst old egl project s list).
						element =  (Openable) manager.getEGLModel().findEGLProject(proj);
					}
				}
				break;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT:
				element = rootInfo == null ? EGLCore.create(resource) : rootInfo.project.getPackageFragmentRoot(resource);
				break;
			case IEGLElement.PACKAGE_FRAGMENT:
				// find the element that encloses the resource
				this.popUntilPrefixOf(path);
				
				if (this.currentElement == null) {
					element = rootInfo == null ? EGLCore.create(resource) : EGLModelManager.create(resource, rootInfo.project);
				} else {
					// find the root
					IPackageFragmentRoot root = this.currentElement.getPackageFragmentRoot();
					if (root == null) {
						element =  rootInfo == null ? EGLCore.create(resource) : EGLModelManager.create(resource, rootInfo.project);
					} else if (((EGLProject)root.getEGLProject()).contains(resource)) {
						// create package handle
						IPath pkgPath = path.removeFirstSegments(root.getPath().segmentCount());
						String pkg = Util.packageName(pkgPath);
						if (pkg == null) return null;
						element = root.getPackageFragment(pkg);
					}
				}
				break;
			case IEGLElement.EGL_FILE:
			case IEGLElement.CLASS_FILE:
				// find the element that encloses the resource
				this.popUntilPrefixOf(path);
				
				if (this.currentElement == null) {
					element =  rootInfo == null ? EGLCore.create(resource) : EGLModelManager.create(resource, rootInfo.project);
				} else {
					// find the package
					IPackageFragment pkgFragment = null;
					switch (this.currentElement.getElementType()) {
						case IEGLElement.PACKAGE_FRAGMENT_ROOT:
							IPackageFragmentRoot root = (IPackageFragmentRoot)this.currentElement;
							IPath rootPath = root.getPath();
							IPath pkgPath = path.removeLastSegments(1);
							String pkgName = Util.packageName(pkgPath.removeFirstSegments(rootPath.segmentCount()));
							if (pkgName != null) {
								pkgFragment = root.getPackageFragment(pkgName);
							}
							break;
						case IEGLElement.PACKAGE_FRAGMENT:
							Openable pkg = (Openable)this.currentElement;
							if (pkg.getPath().equals(path.removeLastSegments(1))) {
								pkgFragment = (IPackageFragment)pkg;
							} // else case of package x which is a prefix of x.y
							break;
						case IEGLElement.EGL_FILE:
						case IEGLElement.CLASS_FILE:
							pkgFragment = (IPackageFragment)this.currentElement.getParent();
							break;
					}
					if (pkgFragment == null) {
						element =  rootInfo == null ? EGLCore.create(resource) : EGLModelManager.create(resource, rootInfo.project);
					} else {
						if (elementType == IEGLElement.EGL_FILE) {
							// create compilation unit handle 
							// fileName validation has been done in elementType(IResourceDelta, int, boolean)
							String fileName = path.lastSegment();
							element = pkgFragment.getEGLFile(fileName);
						} // else {
							// EGLTODO: Brian: Need to do something here?
							// TODO handle class files that were gen'ed from an egl file
							// create class file handle
							// fileName validation has been done in elementType(IResourceDelta, int, boolean)
							
							// String fileName = path.lastSegment();
							// element = pkgFragment.getClassFile(fileName);
						// }
					}
				}
				break;
		}
		if (element == null) {
			return null;
		} else {
			this.currentElement = (Openable)element;
			return this.currentElement;
		}
	}
	/**
	 * Note that the project is about to be deleted.
	 */
	public void deleting(IProject project) {
		
		try {
			// discard indexing jobs that belong to this project so that the project can be 
			// deleted without interferences from the index manager
			this.indexManager.discardJobs(project.getName());

			EGLProject eglProject = (EGLProject)EGLCore.create(project);
			
			// remember roots of this project
			if (this.removedRoots == null) {
				this.removedRoots = new HashMap();
			}
			if (eglProject.isOpen()) {
				this.removedRoots.put(eglProject, eglProject.getPackageFragmentRoots());
			} else {
				// compute roots without opening project
				this.removedRoots.put(
					eglProject, 
					eglProject.computePackageFragmentRoots(
						eglProject.getResolvedEGLPath(true), 
						false));
			}
			
			eglProject.close();

			// workaround for bug 15168 circular errors not reported  
			if (this.manager.eglProjectsCache == null) {
				this.manager.eglProjectsCache = this.manager.getEGLModel().getEGLProjects();
			}
			this.removeFromParentInfo(eglProject);

		} catch (EGLModelException e) {
		}
		
		this.addDependentProjects(project.getFullPath(), this.projectsToUpdate);
	}


	/**
	 * Processing for an element that has been added:<ul>
	 * <li>If the element is a project, do nothing, and do not process
	 * children, as when a project is created it does not yet have any
	 * natures - specifically a egl nature.
	 * <li>If the elemet is not a project, process it as added (see
	 * <code>basicElementAdded</code>.
	 * </ul>
	 * Delta argument could be null if processing an external JAR change
	 */
	protected void elementAdded(Openable element, IResourceDelta delta, RootInfo rootInfo) {
		int elementType = element.getElementType();
		
		if (elementType == IEGLElement.EGL_PROJECT) {
			// project add is handled by EGLProject.configure() because
			// when a project is created, it does not yet have a egl nature
			if (delta != null && EGLProject.hasEGLNature((IProject)delta.getResource())) {
				addToParentInfo(element);
				if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
					Openable movedFromElement = (Openable)element.getEGLModel().getEGLProject(delta.getMovedFromPath().lastSegment());
					currentDelta().movedTo(element, movedFromElement);
				} else {
					currentDelta().added(element);
				}
				this.projectsToUpdate.add(element);
				this.updateRoots(element.getPath(), delta);
				this.projectsForDependentNamelookupRefresh.add((EGLProject) element);
			}
		} else {			
			addToParentInfo(element);
			
			// Force the element to be closed as it might have been opened 
			// before the resource modification came in and it might have a new child
			// For example, in an IWorkspaceRunnable:
			// 1. create a package fragment p using a egl model operation
			// 2. open package p
			// 3. add file X.egl in folder p
			// When the resource delta comes in, only the addition of p is notified, 
			// but the package p is already opened, thus its children are not recomputed
			// and it appears empty.
			close(element);
			
			if (delta != null && (delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
				IPath movedFromPath = delta.getMovedFromPath();
				IResource res = delta.getResource();
				IResource movedFromRes;
				if (res instanceof IFile) {
					movedFromRes = res.getWorkspace().getRoot().getFile(movedFromPath);
				} else {
					movedFromRes = res.getWorkspace().getRoot().getFolder(movedFromPath);
				}
				
				// find the element type of the moved from element
				RootInfo movedFromInfo = this.enclosingRootInfo(movedFromPath, IResourceDelta.REMOVED);
				int movedFromType = 
					this.elementType(
						movedFromRes, 
						IResourceDelta.REMOVED,
						element.getParent().getElementType(), 
						movedFromInfo);
				
				// reset current element as it might be inside a nested root (popUntilPrefixOf() may use the outer root)
				this.currentElement = null;
			
				// create the moved from element
				Openable movedFromElement = 
					elementType != IEGLElement.EGL_PROJECT && movedFromType == IEGLElement.EGL_PROJECT ? 
						null : // outside eglpath
						this.createElement(movedFromRes, movedFromType, movedFromInfo);
				if (movedFromElement == null) {
					// moved from outside eglpath
					currentDelta().added(element);
				} else {
					currentDelta().movedTo(element, movedFromElement);
				}
			} else {
				currentDelta().added(element);
			}
			
			switch (elementType) {
				case IEGLElement.PACKAGE_FRAGMENT_ROOT :
					// when a root is added, and is on the eglpath, the project must be updated
					EGLProject project = (EGLProject) element.getEGLProject();
					this.projectsToUpdate.add(project);
					this.projectsForDependentNamelookupRefresh.add(project);
					
					break;
				case IEGLElement.PACKAGE_FRAGMENT :
					// get rid of namelookup since it holds onto obsolete cached info 
					project = (EGLProject) element.getEGLProject();
					try {
						project.getEGLProjectElementInfo().setNameLookup(null);
						this.projectsForDependentNamelookupRefresh.add(project);						
					} catch (EGLModelException e) {
					}
					// add subpackages
					if (delta != null){
						PackageFragmentRoot root = element.getPackageFragmentRoot();
						String name = element.getElementName();
						IResourceDelta[] children = delta.getAffectedChildren();
						for (int i = 0, length = children.length; i < length; i++) {
							IResourceDelta child = children[i];
							IResource resource = child.getResource();
							if (resource instanceof IFolder) {
								String folderName = resource.getName();
								if (Util.isValidFolderNameForPackage(folderName)) {
									String subpkgName = 
										name.length() == 0 ? 
											folderName : 
											name + "." + folderName; //$NON-NLS-1$
									Openable subpkg = (Openable)root.getPackageFragment(subpkgName);
									this.updateIndex(subpkg, child);
									this.elementAdded(subpkg, child, rootInfo);
								}
							}
						}
					}
					break;
			}
		}
	}

	/**
	 * Generic processing for a removed element:<ul>
	 * <li>Close the element, removing its structure from the cache
	 * <li>Remove the element from its parent's cache of children
	 * <li>Add a REMOVED entry in the delta
	 * </ul>
	 * Delta argument could be null if processing an external JAR change
	 */
	protected void elementRemoved(Openable element, IResourceDelta delta, RootInfo rootInfo) {
		
		if (element.isOpen()) {
			close(element);
		}
		removeFromParentInfo(element);
		int elementType = element.getElementType();
		if (delta != null && (delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
			IPath movedToPath = delta.getMovedToPath();
			IResource res = delta.getResource();
			IResource movedToRes;
			switch (res.getType()) {
				case IResource.PROJECT:
					movedToRes = res.getWorkspace().getRoot().getProject(movedToPath.lastSegment());
					break;
				case IResource.FOLDER:
					movedToRes = res.getWorkspace().getRoot().getFolder(movedToPath);
					break;
				case IResource.FILE:
					movedToRes = res.getWorkspace().getRoot().getFile(movedToPath);
					break;
				default:
					return;
			}

			// find the element type of the moved from element
			RootInfo movedToInfo = this.enclosingRootInfo(movedToPath, IResourceDelta.ADDED);
			int movedToType = 
				this.elementType(
					movedToRes, 
					IResourceDelta.ADDED,
					element.getParent().getElementType(), 
					movedToInfo);

			// reset current element as it might be inside a nested root (popUntilPrefixOf() may use the outer root)
			this.currentElement = null;
			
			// create the moved To element
			Openable movedToElement = 
				elementType != IEGLElement.EGL_PROJECT && movedToType == IEGLElement.EGL_PROJECT ? 
					null : // outside eglpath
					this.createElement(movedToRes, movedToType, movedToInfo);
			if (movedToElement == null) {
				// moved outside eglpath
				currentDelta().removed(element);
			} else {
				currentDelta().movedFrom(element, movedToElement);
			}
		} else {
			currentDelta().removed(element);
		}

		switch (elementType) {
			case IEGLElement.EGL_MODEL :
				this.indexManager.reset();
				break;
			case IEGLElement.EGL_PROJECT :
				this.manager.removePerProjectInfo(
					(EGLProject) element);
				this.updateRoots(element.getPath(), delta);
				this.projectsForDependentNamelookupRefresh.add((EGLProject) element);
				break;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT :
				EGLProject project = (EGLProject) element.getEGLProject();
				this.projectsToUpdate.add(project);
				this.projectsForDependentNamelookupRefresh.add(project);				
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				//1G1TW2T - get rid of namelookup since it holds onto obsolete cached info 
				project = (EGLProject) element.getEGLProject();
				try {
					project.getEGLProjectElementInfo().setNameLookup(null); 
					this.projectsForDependentNamelookupRefresh.add(project);
				} catch (EGLModelException e) { 
				}
				// remove subpackages
				if (delta != null){
					PackageFragmentRoot root = element.getPackageFragmentRoot();
					String name = element.getElementName();
					IResourceDelta[] children = delta.getAffectedChildren();
					for (int i = 0, length = children.length; i < length; i++) {
						IResourceDelta child = children[i];
						IResource resource = child.getResource();
						if (resource instanceof IFolder) {
							String folderName = resource.getName();
							if (Util.isValidFolderNameForPackage(folderName)) {
								String subpkgName = 
									name.length() == 0 ? 
										folderName : 
										name + "." + folderName; //$NON-NLS-1$
								Openable subpkg = (Openable)root.getPackageFragment(subpkgName);
								this.updateIndex(subpkg, child);
								this.elementRemoved(subpkg, child, rootInfo);
							}
						}
					}
				}
				break;
		}
	}

	/*
	 * Returns the type of the egl element the given delta matches to.
	 * Returns NON_EGL_RESOURCE if unknown (e.g. a non-egl resource or excluded .egl file)
	 */
	private int elementType(IResource res, int kind, int parentType, RootInfo rootInfo) {
		switch (parentType) {
			case IEGLElement.EGL_MODEL:
				// case of a movedTo or movedFrom project (other cases are handled in processResourceDelta(...)
				return IEGLElement.EGL_PROJECT;
			case NON_EGL_RESOURCE:
			case IEGLElement.EGL_PROJECT:
				if (rootInfo == null) {
					rootInfo = this.enclosingRootInfo(res.getFullPath(), kind);
				}
				if (rootInfo != null && rootInfo.isRootOfProject(res.getFullPath())) {
					return IEGLElement.PACKAGE_FRAGMENT_ROOT;
				} else {
					return NON_EGL_RESOURCE; // not yet in a package fragment root or root of another project
				}
			case IEGLElement.PACKAGE_FRAGMENT_ROOT:
			case IEGLElement.PACKAGE_FRAGMENT:
				if (rootInfo == null) {
					rootInfo = this.enclosingRootInfo(res.getFullPath(), kind);
				}
				if (rootInfo == null || Util.isExcluded(res, rootInfo.exclusionPatterns)) {
					return NON_EGL_RESOURCE;
				}
				if (res instanceof IFolder) {
					if (Util.isValidFolderNameForPackage(res.getName())) {
						return IEGLElement.PACKAGE_FRAGMENT;
					} else {
						return NON_EGL_RESOURCE;
					}
				} else {
					String fileName = res.getName();
					if (Util.isValidEGLFileName(fileName)) {
						return IEGLElement.EGL_FILE;
					} else if (Util.isValidClassFileName(fileName)) {
						return IEGLElement.CLASS_FILE;
					} else if (this.rootInfo(res.getFullPath(), kind) != null) {
						// case of proj=src=bin and resource is a jar file on the eglpath
						return IEGLElement.PACKAGE_FRAGMENT_ROOT;
					} else {
						return NON_EGL_RESOURCE;
					}
				}
			default:
				return NON_EGL_RESOURCE;
		}
	}

	/**
	 * Answer a combination of the lastModified stamp and the size.
	 * Used for detecting external JAR changes
	 */
	public static long getTimeStamp(File file) {
		return file.lastModified() + file.length();
	}

	public void initializeRoots() {
		// remember roots infos as old roots infos
		this.oldRoots = this.roots == null ? new HashMap() : this.roots;
		this.oldOtherRoots = this.otherRoots == null ? new HashMap() : this.otherRoots;
		
		// recompute root infos only if necessary
		if (!rootsAreStale) return;

		this.roots = new HashMap();
		this.otherRoots = new HashMap();
		this.sourceAttachments = new HashMap();
		
		IEGLModel model = this.manager.getEGLModel();
		IEGLProject[] projects;
		try {
			projects = model.getEGLProjects();
		} catch (EGLModelException e) {
			// nothing can be done
			return;
		}
		for (int i = 0, length = projects.length; i < length; i++) {
			IEGLProject project = projects[i];
			IEGLPathEntry[] eglpath;
			try {
				eglpath = project.getResolvedEGLPath(true);
			} catch (EGLModelException e) {
				// continue with next project
				continue;
			}
			for (int j= 0, eglpathLength = eglpath.length; j < eglpathLength; j++) {
				IEGLPathEntry entry = eglpath[j];
				if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) continue;
				
				// root path
				IPath path = entry.getPath();
				if (this.roots.get(path) == null) {
					this.roots.put(path, new RootInfo(project, path, ((EGLPathEntry)entry).fullExclusionPatternChars()));
				} else {
					ArrayList rootList = (ArrayList)this.otherRoots.get(path);
					if (rootList == null) {
						rootList = new ArrayList();
						this.otherRoots.put(path, rootList);
					}
					rootList.add(new RootInfo(project, path, ((EGLPathEntry)entry).fullExclusionPatternChars()));
				}
				
				// source attachment path
				if (entry.getEntryKind() != IEGLPathEntry.CPE_LIBRARY) continue;
				QualifiedName qName = new QualifiedName(EGLCore.PLUGIN_ID, "sourceattachment: " + path.toOSString()); //$NON-NLS-1$;
				String propertyString = null;
				try {
					propertyString = ResourcesPlugin.getWorkspace().getRoot().getPersistentProperty(qName);
				} catch (CoreException e) {
					continue;
				}
				IPath sourceAttachmentPath;
				if (propertyString != null) {
					int index= propertyString.lastIndexOf(PackageFragmentRoot.ATTACHMENT_PROPERTY_DELIMITER);
					sourceAttachmentPath = (index < 0) ?  new Path(propertyString) : new Path(propertyString.substring(0, index));
				} else {
					sourceAttachmentPath = entry.getSourceAttachmentPath();
				}
				if (sourceAttachmentPath != null) {
					this.sourceAttachments.put(sourceAttachmentPath, path);
				}
			}
		}
		this.rootsAreStale = false;
	}

	/*
	 * Returns whether a given delta contains some information relevant to the EGLModel,
	 * in particular it will not consider SYNC or MARKER only deltas.
	 */
	public boolean isAffectedBy(IResourceDelta rootDelta){
		//if (rootDelta == null) System.out.println("NULL DELTA");
		//long start = System.currentTimeMillis();
		if (rootDelta != null) {
			// use local exception to quickly escape from delta traversal
			class FoundRelevantDeltaException extends RuntimeException {private static final long serialVersionUID = 1L;}
			try {
				rootDelta.accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) throws CoreException {
						switch (delta.getKind()){
							case IResourceDelta.ADDED :
							case IResourceDelta.REMOVED :
								throw new FoundRelevantDeltaException();
							case IResourceDelta.CHANGED :
								// if any flag is set but SYNC or MARKER, this delta should be considered
								if (delta.getAffectedChildren().length == 0 // only check leaf delta nodes
										&& (delta.getFlags() & ~(IResourceDelta.SYNC | IResourceDelta.MARKERS)) != 0) {
									throw new FoundRelevantDeltaException();
								}
						}
						return true;
					}
				});
			} catch(FoundRelevantDeltaException e) {
				//System.out.println("RELEVANT DELTA detected in: "+ (System.currentTimeMillis() - start));
				return true;
			} catch(CoreException e) { // ignore delta if not able to traverse
			}
		}
		//System.out.println("IGNORE SYNC DELTA took: "+ (System.currentTimeMillis() - start));
		return false;
	}
	
	/*
	 * Returns whether the given resource is in one of the given output folders and if
	 * it is filtered out from this output folder.
	 */
	private boolean isResFilteredFromOutput(OutputsInfo info, IResource res, int elementType) {
		if (info != null) {
			IPath resPath = res.getFullPath();
			for (int i = 0;  i < info.outputCount; i++) {
				if (info.paths[i].isPrefixOf(resPath)) {
					if (info.traverseModes[i] != IGNORE) {
						// case of bin=src
						if (info.traverseModes[i] == SOURCE && elementType == IEGLElement.CLASS_FILE) {
							return true;
						} else {
							// case of .class file under project and no source folder
							// proj=bin
							if (elementType == IEGLElement.EGL_PROJECT 
									&& res instanceof IFile 
									&& Util.isValidClassFileName(res.getName())) {
								return true;
							}
						}
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Generic processing for elements with changed contents:<ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * <li>An entry is made in the delta reporting a content change (K_CHANGE with F_CONTENT flag set).
	 * </ul>
	 */
	protected void nonEGLResourcesChanged(Openable element, IResourceDelta delta)
		throws EGLModelException {

		// reset non-egl resources if element was open
		if (element.isOpen()) {
			EGLElementInfo info = (EGLElementInfo)element.getElementInfo();
			switch (element.getElementType()) {
				case IEGLElement.EGL_MODEL :
					((EGLModelInfo) info).nonEGLResources = null;
					currentDelta().addResourceDelta(delta);
					return;
				case IEGLElement.EGL_PROJECT :
					((EGLProjectElementInfo) info).setNonEGLResources(null);
	
					// if a package fragment root is the project, clear it too
					EGLProject project = (EGLProject) element;
					PackageFragmentRoot projectRoot =
						(PackageFragmentRoot) project.getPackageFragmentRoot(project.getProject());
					if (projectRoot.isOpen()) {
						((PackageFragmentRootInfo) projectRoot.getElementInfo()).setNonEGLResources(
							null);
					}
					break;
				case IEGLElement.PACKAGE_FRAGMENT :
					 ((PackageFragmentInfo) info).setNonEGLResources(null);
					break;
				case IEGLElement.PACKAGE_FRAGMENT_ROOT :
					 ((PackageFragmentRootInfo) info).setNonEGLResources(null);
			}
		}

		EGLElementDelta elementDelta = currentDelta().find(element);
		if (elementDelta == null) {
			currentDelta().changed(element, IEGLElementDelta.F_CONTENT);
			elementDelta = currentDelta().find(element);
		}
		elementDelta.addResourceDelta(delta);
	}
	private OutputsInfo outputsInfo(RootInfo rootInfo, IResource res) {
		try {
			IEGLProject proj =
				rootInfo == null ?
					(IEGLProject)this.createElement(res.getProject(), IEGLElement.EGL_PROJECT, null) :
					rootInfo.project;
			if (proj != null) {
				IPath projectOutput = proj.getOutputLocation();
				int traverseMode = IGNORE;
				if (proj.getProject().getFullPath().equals(projectOutput)){ // case of proj==bin==src
					return new OutputsInfo(new IPath[] {projectOutput}, new int[] {SOURCE}, 1);
				} else {
					IEGLPathEntry[] eglpath = proj.getResolvedEGLPath(true);
					IPath[] outputs = new IPath[eglpath.length+1];
					int[] traverseModes = new int[eglpath.length+1];
					int outputCount = 1;
					outputs[0] = projectOutput;
					traverseModes[0] = traverseMode;
					for (int i = 0, length = eglpath.length; i < length; i++) {
						IEGLPathEntry entry = eglpath[i];
						IPath entryPath = entry.getPath();
						IPath output = entry.getOutputLocation();
						if (output != null) {
							outputs[outputCount] = output;
							// check case of src==bin
							if (entryPath.equals(output)) {
								traverseModes[outputCount++] = (entry.getEntryKind() == IEGLPathEntry.CPE_SOURCE) ? SOURCE : BINARY;
							} else {
								traverseModes[outputCount++] = IGNORE;
							}
						}
						
						// check case of src==bin
						if (entryPath.equals(projectOutput)) {
							traverseModes[0] = (entry.getEntryKind() == IEGLPathEntry.CPE_SOURCE) ? SOURCE : BINARY;
						}
					}
					return new OutputsInfo(outputs, traverseModes, outputCount);
				}
			}
		} catch (EGLModelException e) {
		}
		return null;
	}
	
	private void popUntilPrefixOf(IPath path) {
		while (this.currentElement != null) {
			IPath currentElementPath = null;
			if (this.currentElement instanceof IPackageFragmentRoot) {
				currentElementPath = ((IPackageFragmentRoot)this.currentElement).getPath();
			} else {
				IResource currentElementResource = this.currentElement.getResource();
				if (currentElementResource != null) {
					currentElementPath = currentElementResource.getFullPath();
				}
			}
			if (currentElementPath != null) {
				if (this.currentElement instanceof IPackageFragment 
					&& this.currentElement.getElementName().length() == 0
					&& currentElementPath.segmentCount() != path.segmentCount()-1) {
						// default package and path is not a direct child
						this.currentElement = (Openable)this.currentElement.getParent();
				}
				if (currentElementPath.isPrefixOf(path)) {
					return;
				}
			}
			this.currentElement = (Openable)this.currentElement.getParent();
		}
	}

	/**
	 * Converts a <code>IResourceDelta</code> rooted in a <code>Workspace</code> into
	 * the corresponding set of <code>IEGLElementDelta</code>, rooted in the
	 * relevant <code>EGLModel</code>s.
	 */
	public IEGLElementDelta processResourceDelta(IResourceDelta changes) {
		try {
			IEGLModel model = this.manager.getEGLModel();
			if (!model.isOpen()) {
				// force opening of egl model so that egl element delta are reported
				try {
					model.open(null);
				} catch (EGLModelException e) {
					if (VERBOSE) {
						e.printStackTrace();
					}
					return null;
				}
			}
			this.initializeRoots();
			this.currentElement = null;
			
			// get the workspace delta, and start processing there.
			IResourceDelta[] deltas = changes.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				IResourceDelta delta = deltas[i];
				IResource res = delta.getResource();
				
				// find out the element type
				RootInfo rootInfo = null;
				int elementType;
				IProject proj = (IProject)res;
				boolean wasEGLProject = this.manager.getEGLModel().findEGLProject(proj) != null;
				boolean isEGLProject = EGLProject.hasEGLNature(proj);
				if (!wasEGLProject && !isEGLProject) {
					elementType = NON_EGL_RESOURCE;
				} else {
					rootInfo = this.enclosingRootInfo(res.getFullPath(), delta.getKind());
					if (rootInfo != null && rootInfo.isRootOfProject(res.getFullPath())) {
						elementType = IEGLElement.PACKAGE_FRAGMENT_ROOT;
					} else {
						elementType = IEGLElement.EGL_PROJECT; 
					}
				}
				
				// traverse delta
				if (!this.traverseDelta(delta, elementType, rootInfo, null) 
						|| (wasEGLProject != isEGLProject && (delta.getKind()) == IResourceDelta.CHANGED)) { // project has changed nature (description or open/closed)
					try {
						// add child as non egl resource
						nonEGLResourcesChanged((EGLModel)model, delta);
					} catch (EGLModelException e) {
					}
				}

			}
			
			// update package fragment roots of projects that were affected
			Iterator iterator = this.projectsToUpdate.iterator();
			while (iterator.hasNext()) {
				EGLProject project = (EGLProject)iterator.next();
				project.updatePackageFragmentRoots();
			}
	
			updateDependentNamelookups();

			return this.currentDelta;
		} finally {
			this.currentDelta = null;
			this.projectsToUpdate.clear();
			this.projectsForDependentNamelookupRefresh.clear();
		}
	}

	/**
	 * Update the EGLModel according to a .eglpath file change. The file can have changed as a result of a previous
	 * call to EGLProject#setRawEGLPath or as a result of some user update (through repository)
	 */
	void reconcileEGLPathFileUpdate(IResourceDelta delta, IFile file, EGLProject project) {
	
		switch (delta.getKind()) {
			case IResourceDelta.REMOVED : // recreate one based on in-memory eglpath
				try {
					EGLModelManager.PerProjectInfo info = EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(project.getProject());
					if (info.eglpath != null) { // if there is an in-memory eglpath
						project.saveEGLPath(info.eglpath, info.outputLocation);
					}
				} catch (EGLModelException e) {
					if (project.getProject().isAccessible()) {
						Util.log(e, "Could not save eglpath for "+ project.getPath()); //$NON-NLS-1$
					}
				}
				break;
			case IResourceDelta.CHANGED :
				if ((delta.getFlags() & IResourceDelta.CONTENT) == 0  // only consider content change
						&& (delta.getFlags() & IResourceDelta.MOVED_FROM) == 0) // and also move and overide scenario (see http://dev.eclipse.org/bugs/show_bug.cgi?id=21420)
					break;
			case IResourceDelta.ADDED :
				// check if any actual difference
				project.flushEGLPathProblemMarkers(false, true);
				boolean wasSuccessful = false; // flag recording if .eglpath file change got reflected
				try {
					// force to (re)read the property file
					IEGLPathEntry[] fileEntries = project.readEGLPathFile(true, false);
					if (fileEntries == null)
						break; // could not read, ignore 
					EGLModelManager.PerProjectInfo info = EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(project.getProject());
					if (info.eglpath != null) { // if there is an in-memory eglpath
						if (project.isEGLPathEqualsTo(info.eglpath, info.outputLocation, fileEntries)) {
							wasSuccessful = true;
							break;
						}
					}
		
					// will force an update of the eglpath/output location based on the file information
					// extract out the output location
					IPath outputLocation = null;
					if (fileEntries != null && fileEntries.length > 0) {
						IEGLPathEntry entry = fileEntries[fileEntries.length - 1];
						if (entry.getContentKind() == EGLPathEntry.K_OUTPUT) {
							outputLocation = entry.getPath();
							IEGLPathEntry[] copy = new IEGLPathEntry[fileEntries.length - 1];
							System.arraycopy(fileEntries, 0, copy, 0, copy.length);
							fileEntries = copy;
						}
					}
					// restore output location				
					if (outputLocation == null) {
						outputLocation = SetEGLPathOperation.ReuseOutputLocation;
						// clean mode will also default to reusing current one
					}
					project.setRawEGLPath(
						fileEntries, 
						outputLocation, 
						null, // monitor
						!ResourcesPlugin.getWorkspace().isTreeLocked(), // canChangeResource
						project.getResolvedEGLPath(true), // ignoreUnresolvedVariable
						true, // needValidation
						false); // no need to save
					
					// if reach that far, the eglpath file change got absorbed
					wasSuccessful = true;
				} catch (RuntimeException e) {
					// setRawEGLPath might fire a delta, and a listener may throw an exception
					if (project.getProject().isAccessible()) {
						Util.log(e, "Could not set eglpath for "+ project.getPath()); //$NON-NLS-1$
					}
					break;
				} catch (EGLModelException e) { // CP failed validation
					if (project.getProject().isAccessible()) {
						if (e.getEGLModelStatus().getException() instanceof CoreException) {
							// happens if the .eglpath could not be written to disk
							project.createEGLPathProblemMarker(new EGLModelStatus(
									IEGLModelStatusConstants.INVALID_EGLPATH_FILE_FORMAT,
									EGLModelResources.bind(EGLModelResources.eglpathCouldNotWriteEGLpathFile, project.getElementName(), e.getMessage())));
						} else {
							project.createEGLPathProblemMarker(new EGLModelStatus(
									IEGLModelStatusConstants.INVALID_EGLPATH_FILE_FORMAT,
									EGLModelResources.bind(EGLModelResources.eglpathInvalidEGLpathInEGLpathFile, project.getElementName(), e.getMessage())));
						}			
					}
					break;
				} finally {
					if (!wasSuccessful) { 
						try {
							project.setRawEGLPath0(EGLProject.INVALID_EGLPATH);
							project.updatePackageFragmentRoots();
						} catch (EGLModelException e) {
						}
					}
				}
		}
		
	}

	/**
	 * Update the EGLModel according to a .jprefs file change. The file can have changed as a result of a previous
	 * call to EGLProject#setOptions or as a result of some user update (through repository)
	 * Unused until preference file get shared (.jpref)
	 */
	void reconcilePreferenceFileUpdate(IResourceDelta delta, IFile file, EGLProject project) {
		// TODO handle preferences later
		/*	
		switch (delta.getKind()) {
			case IResourceDelta.REMOVED : // flush project custom settings
				project.setOptions(null);
				return;
			case IResourceDelta.CHANGED :
				if ((delta.getFlags() & IResourceDelta.CONTENT) == 0  // only consider content change
						&& (delta.getFlags() & IResourceDelta.MOVED_FROM) == 0) // and also move and overide scenario
					break;
				identityCheck : { // check if any actual difference
					// force to (re)read the property file
					Preferences filePreferences = project.loadPreferences();
					if (filePreferences == null){ 
						project.setOptions(null); // should have got removed delta.
						return;
					}
					Preferences projectPreferences = project.getPreferences();
					if (projectPreferences == null) return; // not a EGL project
						
					// compare preferences set to their default
					String[] defaultProjectPropertyNames = projectPreferences.defaultPropertyNames();
					String[] defaultFilePropertyNames = filePreferences.defaultPropertyNames();
					if (defaultProjectPropertyNames.length == defaultFilePropertyNames.length) {
						for (int i = 0; i < defaultProjectPropertyNames.length; i++){
							String propertyName = defaultProjectPropertyNames[i];
							if (!projectPreferences.getString(propertyName).trim().equals(filePreferences.getString(propertyName).trim())){
								break identityCheck;
							}
						}		
					} else break identityCheck;

					// compare custom preferences not set to their default
					String[] projectPropertyNames = projectPreferences.propertyNames();
					String[] filePropertyNames = filePreferences.propertyNames();
					if (projectPropertyNames.length == filePropertyNames.length) {
						for (int i = 0; i < projectPropertyNames.length; i++){
						String propertyName = projectPropertyNames[i];
							if (!projectPreferences.getString(propertyName).trim().equals(filePreferences.getString(propertyName).trim())){
								break identityCheck;
							}
						}		
					} else break identityCheck;
					
					// identical - do nothing
					return;
				}
			case IResourceDelta.ADDED :
				// not identical, create delta and reset cached preferences
				project.setPreferences(null);
				// create delta
				//fCurrentDelta.changed(project, IEGLElementDelta.F_OPTIONS_CHANGED);				
		}
		*/
	}

	/**
	 * Removes the given element from its parents cache of children. If the
	 * element does not have a parent, or the parent is not currently open,
	 * this has no effect. 
	 */
	protected void removeFromParentInfo(Openable child) {

		Openable parent = (Openable) child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				EGLElementInfo info = (EGLElementInfo)parent.getElementInfo();
				info.removeChild(child);
			} catch (EGLModelException e) {
				// do nothing - we already checked if open
			}
		}
	}
	/**
	 * Notification that some resource changes have happened
	 * on the platform, and that the EGL Model should update any required
	 * internal structures such that its elements remain consistent.
	 * Translates <code>IResourceDeltas</code> into <code>IEGLElementDeltas</code>.
	 *
	 * @see IResourceDelta
	 * @see IResource 
	 */
	public void resourceChanged(IResourceChangeEvent event) {
	
		if (event.getSource() instanceof IWorkspace) {
			IResource resource = event.getResource();
			IResourceDelta delta = event.getDelta();
			
			switch(event.getType()){
				case IResourceChangeEvent.PRE_DELETE :
					try {
						if(resource.getType() == IResource.PROJECT 
							&& ((IProject) resource).hasNature(EGLCore.NATURE_ID)) {
								
							this.deleting((IProject)resource);
						}
					} catch(CoreException e){
					}
					return;
					
				case IResourceChangeEvent.PRE_BUILD :
					if(isAffectedBy(delta)) { // avoid populating for SYNC or MARKER deltas
						
						// Record the last update time
						this.manager.setLastUpdateTime(System.currentTimeMillis());
						
						// update the eglpath related markers
						this.updateEGLPathMarkers();
					}
					
				// only fire already computed deltas (resource ones will be
				// processed in post change only)
				this.manager.fire(null, ElementChangedEvent.PRE_AUTO_BUILD);

				// force the dynamic references to update
				if (changedProjects.size() > 0) {
					fireProjectsChanged((IProject[]) changedProjects.toArray(new IProject[changedProjects.size()]));
					this.changedProjects.clear();
				}					
				break;

			  case IResourceChangeEvent.POST_BUILD :
			  //	EGLBuilder.finishedBuilding(event);
				  break;
					
			  case IResourceChangeEvent.POST_CHANGE :
				// This notification 
			  	if(isAffectedBy(delta)){
			  		try{
			  			try {
							// don't fire eglpath change deltas right away, but batch them
							this.manager.stopDeltas();
							checkProjectsBeingAddedOrRemoved(delta);
							  if (this.refreshedElements != null) {
								 createExternalArchiveDelta(refreshedElements, null);
							  }
							  IEGLElementDelta translatedDelta = this.processResourceDelta(delta);
							  if (translatedDelta != null) { 
								  this.manager.registerEGLModelDelta(translatedDelta);
							  }
						} finally {
							this.manager.startDeltas();
						}
			  		this.manager.fire(null, ElementChangedEvent.POST_CHANGE);
			  		}	finally {
					  // workaround for bug 15168 circular errors not reported 
					  this.manager.eglProjectsCache = null;
					  this.removedRoots = null;
				  	}
			  	}
//**************************
//This block of code moved to PRE_AUTO_BUILD so that the resource changes are indexed
//before the builder runs, due to the fact that the builder uses the index.
//
//				  if (isAffectedBy(delta)) {
//					  try {
//						  if (this.refreshedElements != null) {
//							  try {
//							  createExternalArchiveDelta(null);
//							  } catch (EGLModelException e) {
//								  e.printStackTrace();
//							  }
//						  }
//						  IEGLElementDelta translatedDelta = this.processResourceDelta(delta);
//						  if (translatedDelta != null) { 
//							  this.manager.registerEGLModelDelta(translatedDelta);
//						  }
//					  //	this.manager.fire(null, ElementChangedEvent.POST_CHANGE);
//					  } finally {
//						  // workaround for bug 15168 circular errors not reported 
//						  this.manager.eglProjectsCache = null;
//						  this.removedRoots = null;
//					  }
//				  }
//***************************
					
			}
		}
	}
	/*
	 * Finds the root info this path is included in.
	 * Returns null if not found.
	 */
	RootInfo enclosingRootInfo(IPath path, int kind) {
		while (path != null && path.segmentCount() > 0) {
			RootInfo rootInfo =  this.rootInfo(path, kind);
			if (rootInfo != null) return rootInfo;
			path = path.removeLastSegments(1);
		}
		return null;
	}
	/*
	 * Returns the root info for the given path. Look in the old roots table if kind is REMOVED.
	 */
	RootInfo rootInfo(IPath path, int kind) {
		if (kind == IResourceDelta.REMOVED) {
			return (RootInfo)this.oldRoots.get(path);
		} else {
			return (RootInfo)this.roots.get(path);
		}
	}
	/*
	 * Returns the other root infos for the given path. Look in the old other roots table if kind is REMOVED.
	 */
	ArrayList otherRootsInfo(IPath path, int kind) {
		if (kind == IResourceDelta.REMOVED) {
			return (ArrayList)this.oldOtherRoots.get(path);
		} else {
			return (ArrayList)this.otherRoots.get(path);
		}
	}	

	/**
	 * Converts an <code>IResourceDelta</code> and its children into
	 * the corresponding <code>IEGLElementDelta</code>s.
	 * Return whether the delta corresponds to a egl element.
	 * If it is not a egl element, it will be added as a non-egl
	 * resource by the sender of this method.
	 */
	protected boolean traverseDelta(
		IResourceDelta delta, 
		int elementType, 
		RootInfo rootInfo,
		OutputsInfo outputsInfo) {
			
		IResource res = delta.getResource();
	
		// set stack of elements
		if (this.currentElement == null && rootInfo != null) {
			this.currentElement = (Openable)rootInfo.project;
		}
		
		// process current delta
		boolean processChildren = true;
		if (res instanceof IProject) {
			processChildren = 
				this.updateCurrentDeltaAndIndex(
					delta, 
					elementType == IEGLElement.PACKAGE_FRAGMENT_ROOT ? 
						IEGLElement.EGL_PROJECT : // case of prj=src
						elementType, 
					rootInfo);
		} else if (rootInfo != null) {
			processChildren = this.updateCurrentDeltaAndIndex(delta, elementType, rootInfo);
		} else {
			// not yet inside a package fragment root
			processChildren = true;
		}
		
		// get the project's output locations and traverse mode
		if (outputsInfo == null) outputsInfo = this.outputsInfo(rootInfo, res);
	
		// process children if needed
		if (processChildren) {
			IResourceDelta[] children = delta.getAffectedChildren();
			boolean oneChildOnEGLPath = false;
			int length = children.length;
			IResourceDelta[] orphanChildren = null;
			Openable parent = null;
			boolean isValidParent = true;
			for (int i = 0; i < length; i++) {
				IResourceDelta child = children[i];
				IResource childRes = child.getResource();
	
				// check source attachment change
				this.checkSourceAttachmentChange(child, childRes);
				this.checkEGLPathChange(child, childRes);
				
				// find out whether the child is a package fragment root of the current project
				IPath childPath = childRes.getFullPath();
				int childKind = child.getKind();
				RootInfo childRootInfo = this.rootInfo(childPath, childKind);
				if (childRootInfo != null && !childRootInfo.isRootOfProject(childPath)) {
					// package fragment root of another project (dealt with later)
					childRootInfo = null;
				}
				
				// compute child type
				int childType = 
					this.elementType(
						childRes, 
						childKind,
						elementType, 
						rootInfo == null ? childRootInfo : rootInfo
					);
						
				// is childRes in the output folder and is it filtered out ?
				boolean isResFilteredFromOutput = this.isResFilteredFromOutput(outputsInfo, childRes, childType);

				boolean isNestedRoot = rootInfo != null && childRootInfo != null;
				if (!isResFilteredFromOutput 
						&& !isNestedRoot) { // do not treat as non-egl rsc if nested root
					if (!this.traverseDelta(child, childType, rootInfo == null ? childRootInfo : rootInfo, outputsInfo)) { // traverse delta for child in the same project
						// it is a non-egl resource
						try {
							if (rootInfo != null) { // if inside a package fragment root
								if (!isValidParent) continue; 
								if (parent == null) {
									// find the parent of the non-egl resource to attach to
									if (this.currentElement == null 
											|| !this.currentElement.getEGLProject().equals(rootInfo.project)) {
										// force the currentProject to be used
										this.currentElement = (Openable)rootInfo.project;
									}
									if (elementType == IEGLElement.EGL_PROJECT
										|| (elementType == IEGLElement.PACKAGE_FRAGMENT_ROOT 
											&& res instanceof IProject)) { 
										// NB: attach non-egl resource to project (not to its package fragment root)
										parent = (Openable)rootInfo.project;
									} else {
										parent = this.createElement(res, elementType, rootInfo);
									}
									if (parent == null) {
										isValidParent = false;
										continue;
									}
								}
								// add child as non egl resource
								nonEGLResourcesChanged(parent, child);
							} else {
								// the non-egl resource (or its parent folder) will be attached to the egl project
								if (orphanChildren == null) orphanChildren = new IResourceDelta[length];
								orphanChildren[i] = child;
							}
						} catch (EGLModelException e) {
						}
					} else {
						oneChildOnEGLPath = true;
					}
				} else {
					oneChildOnEGLPath = true; // to avoid reporting child delta as non-egl resource delta
				}
								
				// if child is a nested root 
				// or if it is not a package fragment root of the current project
				// but it is a package fragment root of another project, traverse delta too
				if (isNestedRoot 
						|| (childRootInfo == null && (childRootInfo = this.rootInfo(childPath, childKind)) != null)) {
					this.traverseDelta(child, IEGLElement.PACKAGE_FRAGMENT_ROOT, childRootInfo, null); // binary output of childRootInfo.project cannot be this root
					// NB: No need to check the return value as the child can only be on the eglpath
				}
	
				// if the child is a package fragment root of one or several other projects
				ArrayList rootList;
				if ((rootList = this.otherRootsInfo(childPath, childKind)) != null) {
					Iterator iterator = rootList.iterator();
					while (iterator.hasNext()) {
						childRootInfo = (RootInfo) iterator.next();
						this.traverseDelta(child, IEGLElement.PACKAGE_FRAGMENT_ROOT, childRootInfo, null); // binary output of childRootInfo.project cannot be this root
					}
				}
			}
			if (orphanChildren != null
					&& (oneChildOnEGLPath // orphan children are siblings of a package fragment root
						|| res instanceof IProject)) { // non-egl resource directly under a project
						
				// attach orphan children
				IProject rscProject = res.getProject();
				EGLProject adoptiveProject = (EGLProject)EGLCore.create(rscProject);
				if (adoptiveProject != null 
						&& EGLProject.hasEGLNature(rscProject)) { // delta iff EGL project (18698)
					for (int i = 0; i < length; i++) {
						if (orphanChildren[i] != null) {
							try {
								nonEGLResourcesChanged(adoptiveProject, orphanChildren[i]);
							} catch (EGLModelException e) {
							}
						}
					}
				}
			} // else resource delta will be added by parent
			return elementType != NON_EGL_RESOURCE; // TODO: (jerome) do we still need to return? (check could be done by caller)
		} else {
			return elementType != NON_EGL_RESOURCE;
		}
	}

	/**
	 * Update the eglpath markers and cycle markers for the projects to update.
	 */
	void updateEGLPathMarkers() {
		// TODO handle update markers later
	
		try {
			if (!ResourcesPlugin.getWorkspace().isAutoBuilding()) {
				Iterator iterator = this.projectsToUpdate.iterator();
				while (iterator.hasNext()) {
					try {
						EGLProject project = (EGLProject)iterator.next();
						
						 // force eglpath marker refresh
						project.getResolvedEGLPath(
							true, // ignoreUnresolvedEntry
							true); // generateMarkerOnError
						
					} catch (EGLModelException e) {
					}
				}
			}
			if (!this.projectsToUpdate.isEmpty()){
				try {
					// update all cycle markers
					EGLProject.updateAllCycleMarkers();
				} catch (EGLModelException e) {
				}
			}				
		} finally {
			this.projectsToUpdate = new HashSet();
		}
	
	}

	/**
	 * Update the current delta (ie. add/remove/change the given element) and update the correponding index.
	 * Returns whether the children of the given delta must be processed.
	 * @throws EGLModelException if the delta doesn't correspond to a egl element of the given type.
	 */
	private boolean updateCurrentDeltaAndIndex(IResourceDelta delta, int elementType, RootInfo rootInfo) {
		Openable element;
		switch (delta.getKind()) {
			case IResourceDelta.ADDED :
				IResource deltaRes = delta.getResource();
				element = this.createElement(deltaRes, elementType, rootInfo);
				if (element == null) {
					// resource might be containing shared roots (see bug 19058)
					this.updateRoots(deltaRes.getFullPath(), delta);
					return false;
				}
				this.updateIndex(element, delta);
				this.elementAdded(element, delta, rootInfo);
				return false;
			case IResourceDelta.REMOVED :
				deltaRes = delta.getResource();
				element = this.createElement(deltaRes, elementType, rootInfo);
				if (element == null) {
					// resource might be containing shared roots (see bug 19058)
					this.updateRoots(deltaRes.getFullPath(), delta);
					return false;
				}
				this.updateIndex(element, delta);
				this.elementRemoved(element, delta, rootInfo);
	
				return false;
			case IResourceDelta.CHANGED :
				int flags = delta.getFlags();
				if ((flags & IResourceDelta.CONTENT) != 0) {
					// content has changed
					element = this.createElement(delta.getResource(), elementType, rootInfo);
					if (element == null) return false;
					this.updateIndex(element, delta);
					this.contentChanged(element, delta);
				} else if (elementType == IEGLElement.EGL_PROJECT) {
					if ((flags & IResourceDelta.OPEN) != 0) {
						// project has been opened or closed
						IProject res = (IProject)delta.getResource();
						element = this.createElement(res, elementType, rootInfo);
						if (element == null) {
							// resource might be containing shared roots (see bug 19058)
							this.updateRoots(res.getFullPath(), delta);
							return false;
						}
						if (res.isOpen()) {
							if (EGLProject.hasEGLNature(res)) {
								this.elementAdded(element, delta, rootInfo);
								this.indexManager.indexAll(res);
							}
						} else {
							EGLModel eglModel = this.manager.getEGLModel();
							boolean wasEGLProject = eglModel.findEGLProject(res) != null;
							if (wasEGLProject) {
								this.elementRemoved(element, delta, rootInfo);
								this.indexManager.discardJobs(element.getElementName());
								this.indexManager.removeIndexFamily(res.getFullPath());
								
							}
						}
						return false; // when a project is open/closed don't process children
					}
					if ((flags & IResourceDelta.DESCRIPTION) != 0) {
						IProject res = (IProject)delta.getResource();
						EGLModel eglModel = this.manager.getEGLModel();
						boolean wasEGLProject = eglModel.findEGLProject(res) != null;
						boolean isEGLProject = EGLProject.hasEGLNature(res);
						if (wasEGLProject != isEGLProject) {
							// project's nature has been added or removed
							element = this.createElement(res, elementType, rootInfo);
							if (element == null) return false; // note its resources are still visible as roots to other projects
							if (isEGLProject) {
								this.elementAdded(element, delta, rootInfo);
								this.indexManager.indexAll(res);
							} else {
								this.elementRemoved(element, delta, rootInfo);
								this.indexManager.discardJobs(element.getElementName());
								this.indexManager.removeIndexFamily(res.getFullPath());
							}
							return false; // when a project's nature is added/removed don't process children
						}
					}
				}
				return true;
		}
		return true;
	}

	/**
	 * Traverse the set of projects which have changed namespace, and refresh their dependents
	 */
	public void updateDependentNamelookups() {
		Iterator iterator;
		// update namelookup of dependent projects
		iterator = this.projectsForDependentNamelookupRefresh.iterator();
		HashSet affectedDependents = new HashSet();
		while (iterator.hasNext()) {
			EGLProject project = (EGLProject)iterator.next();
			addDependentProjects(project.getPath(), affectedDependents);
		}
		iterator = affectedDependents.iterator();
		while (iterator.hasNext()) {
			EGLProject project = (EGLProject) iterator.next();
			if (project.isOpen()){
				try {
					((EGLProjectElementInfo)project.getElementInfo()).setNameLookup(null);
				} catch (EGLModelException e) {
				}
			}
		}
	}

protected void updateIndex(Openable element, IResourceDelta delta) {
   
	if (indexManager == null)
		return;

	switch (element.getElementType()) {
		case IEGLElement.EGL_PROJECT :
			switch (delta.getKind()) {
				case IResourceDelta.ADDED :
					this.indexManager.indexAll(element.getEGLProject().getProject());
					break;
				case IResourceDelta.REMOVED :
					this.indexManager.removeIndexFamily(element.getEGLProject().getProject().getFullPath());
					// NB: Discarding index jobs belonging to this project was done during PRE_DELETE
					break;
				// NB: Update of index if project is opened, closed, or its egl nature is added or removed
				//     is done in updateCurrentDeltaAndIndex
			}
			break;
		case IEGLElement.PACKAGE_FRAGMENT_ROOT :
			int kind = delta.getKind();
			if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
				IPackageFragmentRoot root = (IPackageFragmentRoot)element;
				//for eglar inside BP, should handle in different way
				IProject proj = root.getEGLProject().getProject();
				if(kind == IResourceDelta.ADDED && org.eclipse.edt.ide.core.internal.model.util.Util.isBinaryProject(proj) && root instanceof EglarPackageFragmentRoot){
					this.indexManager.indexLibrary(root.getPath(), proj);
					return;
				}
				else{
					this.updateRootIndex(root, root.getPackageFragment(""), delta); //$NON-NLS-1$
				}
				break;
			}
			
			// don't break as packages of the package fragment root can be indexed below
		case IEGLElement.PACKAGE_FRAGMENT :
			switch (delta.getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.REMOVED:
					IPackageFragment pkg = null;
					if (element instanceof IPackageFragmentRoot) {
						IPackageFragmentRoot root = (IPackageFragmentRoot)element;
						pkg = root.getPackageFragment(""); //$NON-NLS-1$
					} else {
						pkg = (IPackageFragment)element;
					}
					IResourceDelta[] children = delta.getAffectedChildren();
					for (int i = 0, length = children.length; i < length; i++) {
						IResourceDelta child = children[i];
						IResource resource = child.getResource();
						if (resource instanceof IFile) {
							String name = resource.getName();
							if (Util.isEGLFileName(name)) {
								Openable cu = (Openable)pkg.getEGLFile(name);
								this.updateIndex(cu, child);
							} 
//							else if (Util.isClassFileName(name)) {
//								Openable classFile = (Openable)pkg.getClassFile(name);
//								this.updateIndex(classFile, child);
//							}
						}
					}
					break;
			}
			break;
		case IEGLElement.EGL_FILE :
			IFile file = (IFile) delta.getResource();
			switch (delta.getKind()) {
				case IResourceDelta.CHANGED :
					// no need to index if the content has not changed
					if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
						break;
				case IResourceDelta.ADDED :
					//no index for EGL source file inside BP
					if(!org.eclipse.edt.ide.core.internal.model.util.Util.isBinaryProject(file.getProject().getProject())){
						indexManager.addSource(file, file.getProject().getProject().getFullPath());
					}
					break;
				case IResourceDelta.REMOVED :
					indexManager.remove(file.getFullPath().toString(), file.getProject().getProject().getFullPath());
					break;
			}
	}
}
	
/**
 * Upadtes the index of the given root (assuming it's an addition or a removal).
 * This is done recusively, pkg being the current package.
 */
private void updateRootIndex(IPackageFragmentRoot root, IPackageFragment pkg, IResourceDelta delta) {
	this.updateIndex((Openable)pkg, delta);
	IResourceDelta[] children = delta.getAffectedChildren();
	String name = pkg.getElementName();
	for (int i = 0, length = children.length; i < length; i++) {
		IResourceDelta child = children[i];
		IResource resource = child.getResource();
		if (resource instanceof IFolder) {
			String subpkgName = 
				name.length() == 0 ? 
					resource.getName() : 
					name + "." + resource.getName(); //$NON-NLS-1$
			IPackageFragment subpkg = root.getPackageFragment(subpkgName);
			this.updateRootIndex(root, subpkg, child);
		}
	}
}
/*
 * Update the roots that are affected by the addition or the removal of the given container resource.
 */
private void updateRoots(IPath containerPath, IResourceDelta containerDelta) {
	Map roots;
	Map otherRoots;
	if (containerDelta.getKind() == IResourceDelta.REMOVED) {
		roots = this.oldRoots;
		otherRoots = this.oldOtherRoots;
	} else {
		roots = this.roots;
		otherRoots = this.otherRoots;
	}
	Iterator iterator = roots.keySet().iterator();
	while (iterator.hasNext()) {
		IPath path = (IPath)iterator.next();
		if (containerPath.isPrefixOf(path) && !containerPath.equals(path)) {
			IResourceDelta rootDelta = containerDelta.findMember(path.removeFirstSegments(1));
			if (rootDelta == null) continue;
			RootInfo rootInfo = (RootInfo)roots.get(path);

			if (!rootInfo.project.getPath().isPrefixOf(path)) { // only consider roots that are not included in the container
				this.updateCurrentDeltaAndIndex(rootDelta, IEGLElement.PACKAGE_FRAGMENT_ROOT, rootInfo);
			}
			
			ArrayList rootList = (ArrayList)otherRoots.get(path);
			if (rootList != null) {
				Iterator otherProjects = rootList.iterator();
				while (otherProjects.hasNext()) {
					rootInfo = (RootInfo)otherProjects.next();
					if (!rootInfo.project.getPath().isPrefixOf(path)) { // only consider roots that are not included in the container
						this.updateCurrentDeltaAndIndex(rootDelta, IEGLElement.PACKAGE_FRAGMENT_ROOT, rootInfo);
					}
				}
			}
		}
	}
}

public void addProjectsChangedListener(IProjectsChangedListener listener) {
	projectChangedListeners.add(listener);
}

public void removeProjectsChangedListener(IProjectsChangedListener listener) {
	projectChangedListeners.remove(listener);
}

public void fireProjectsChanged(IProject[] projects) {
	Iterator i = projectChangedListeners.iterator();
	
	while (i.hasNext()) {
		IProjectsChangedListener listener = (IProjectsChangedListener)i.next();
		listener.projectsChanged(projects);
	}
}

}

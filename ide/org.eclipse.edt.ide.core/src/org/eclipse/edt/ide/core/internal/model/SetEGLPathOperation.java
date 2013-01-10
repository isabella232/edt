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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.util.EGLProjectFileUtilityLocator;
import org.eclipse.edt.ide.core.internal.model.util.IEGLProjectFileUtility;
import org.eclipse.edt.ide.core.internal.model.util.ObjectVector;


/**
 * This operation sets an <code>IEGLProject</code>'s eglpath.
 *
 * @see IEGLProject
 */
public class SetEGLPathOperation extends EGLModelOperation {

	IEGLPathEntry[] oldResolvedPath, newResolvedPath;
	IEGLPathEntry[] newRawPath;
	boolean canChangeResource;
	boolean needCycleCheck;
	boolean needValidation;
	boolean needSave;
	IPath newOutputLocation;
	
	public static final IEGLPathEntry[] ReuseEGLPath = new IEGLPathEntry[0];
	public static final IEGLPathEntry[] UpdateEGLPath = new IEGLPathEntry[0];
	// if reusing output location, then also reuse clean flag
	public static final IPath ReuseOutputLocation = new Path("Reuse Existing Output Location");  //$NON-NLS-1$
	
	/**
	 * When executed, this operation sets the eglpath of the given project.
	 */
	public SetEGLPathOperation(
		IEGLProject project,
		IEGLPathEntry[] oldResolvedPath,
		IEGLPathEntry[] newRawPath,
		IPath newOutputLocation,
		boolean canChangeResource,
		boolean needValidation,
		boolean needSave) {

		super(new IEGLElement[] { project });
		this.oldResolvedPath = oldResolvedPath;
		this.newRawPath = newRawPath;
		this.newOutputLocation = newOutputLocation;
		this.canChangeResource = canChangeResource;
		this.needValidation = needValidation;
		this.needSave = needSave;
	}

	/**
	 * Adds deltas for the given roots, with the specified change flag,
	 * and closes the root. Helper method for #setEGLPath
	 */
	protected void addEGLPathDeltas(
		IPackageFragmentRoot[] roots,
		int flag,
		EGLElementDelta delta) {

		for (int i = 0; i < roots.length; i++) {
			IPackageFragmentRoot root = roots[i];
			delta.changed(root, flag);
			if ((flag & IEGLElementDelta.F_REMOVED_FROM_EGLPATH) != 0){
				try {
					root.close();
				} catch (EGLModelException e) {}
			}
		}
	}



	/**
	 * Returns the index of the item in the list if the given list contains the specified entry. If the list does
	 * not contain the entry, -1 is returned.
	 * A helper method for #setEGLPath
	 * Rocky (Jan, 7, 2011): now we allow the duplicated egl path entry (mainly due to allow eglar files under binary project can 
	 * either be added by binary project or through the external eglar file), so need return the multiple index value.
	 * 
	 */
	protected int[] eglpathContains(
		IEGLPathEntry[] list,
		IEGLPathEntry entry) {
		int[] temp = new int[list.length];
		int index = 0;
		for (int i = 0; i < list.length; i++) {
			IEGLPathEntry other = list[i];
			if (other.equals(entry)) {
				temp[index++] = i;
			}
		}
		if(index == 0) {
			return new int[0];
		} else {
			int[] ret = new int[index];
			System.arraycopy(temp, 0, ret, 0, index);
			return ret;
		}
		
	}
	
	private boolean foundIndex(int index, int[] indexes) {
		for(int value:indexes) {
			if(value == index) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Recursively adds all subfolders of <code>folder</code> to the given collection.
	 */
	protected void collectAllSubfolders(IFolder folder, ArrayList collection) throws EGLModelException {
		try {
			IResource[] members= folder.members();
			for (int i = 0, max = members.length; i < max; i++) {
				IResource r= members[i];
				if (r.getType() == IResource.FOLDER) {
					collection.add(r);
					collectAllSubfolders((IFolder)r, collection);
				}
			}	
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}

	/**
	 * Returns a collection of package fragments that have been added/removed
	 * as the result of changing the output location to/from the given
	 * location. The collection is empty if no package fragments are
	 * affected.
	 */
	protected ArrayList determineAffectedPackageFragments(IPath location) throws EGLModelException {
		ArrayList fragments = new ArrayList();
		EGLProject project =getProject();
	
		// see if this will cause any package fragments to be affected
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResource resource = null;
		if (location != null) {
			resource = workspace.getRoot().findMember(location);
		}
		if (resource != null && resource.getType() == IResource.FOLDER) {
			IFolder folder = (IFolder) resource;
			// only changes if it actually existed
			IEGLPathEntry[] eglpath = project.getExpandedEGLPath(true);
			for (int i = 0; i < eglpath.length; i++) {
				IEGLPathEntry entry = eglpath[i];
				IPath path = eglpath[i].getPath();
				if (entry.getEntryKind() != IEGLPathEntry.CPE_PROJECT && path.isPrefixOf(location) && !path.equals(location)) {
					IPackageFragmentRoot[] roots = project.computePackageFragmentRoots(eglpath[i]);
					IPackageFragmentRoot root = roots[0];
					// now the output location becomes a package fragment - along with any subfolders
					ArrayList folders = new ArrayList();
					folders.add(folder);
					collectAllSubfolders(folder, folders);
					Iterator elements = folders.iterator();
					int segments = path.segmentCount();
					while (elements.hasNext()) {
						IFolder f = (IFolder) elements.next();
						IPath relativePath = f.getFullPath().removeFirstSegments(segments);
						String name = relativePath.toOSString();
						name = name.replace(File.pathSeparatorChar, '.');
						if (name.endsWith(".")) { //$NON-NLS-1$
							name = name.substring(0, name.length() - 1);
						}
						IPackageFragment pkg = root.getPackageFragment(name);
						fragments.add(pkg);
					}
				}
			}
		}
		return fragments;
	}

	/**
	 * Sets the eglpath of the pre-specified project.
	 */
	protected void executeOperation() throws EGLModelException {
		// project reference updated - may throw an exception if unable to write .project file
		updateProjectReferencesIfNecessary();

		// eglpath file updated - may throw an exception if unable to write .eglpath file
		saveEGLPathIfNecessary();
		
		// perform eglpath and output location updates, if exception occurs in eglpath update,
		// make sure the output location is updated before surfacing the exception (in case the output
		// location update also throws an exception, give priority to the eglpath update one).
		EGLModelException originalException = null;

		try {
			EGLProject project = getProject();
			if (this.newRawPath == UpdateEGLPath) this.newRawPath = project.getRawEGLPath();
			if (this.newRawPath != ReuseEGLPath){
				updateEGLPath();
				project.updatePackageFragmentRoots();
				EGLModelManager.getEGLModelManager().deltaProcessor.addForRefresh(project);
			}

		} catch(EGLModelException e){
			originalException = e;
			throw e;

		} finally { // if traversed by an exception we still need to update the output location when necessary

			try {
				if (this.newOutputLocation != ReuseOutputLocation) updateOutputLocation();

			} catch(EGLModelException e){
				if (originalException != null) throw originalException; 
				throw e;
			}
		}
		done();
	}

	/**
	 * Generates the delta of removed/added/reordered roots.
	 * Use three deltas in case the same root is removed/added/reordered (for
	 * instance, if it is changed from K_SOURCE to K_BINARY or vice versa)
	 */
	protected void generateEGLPathChangeDeltas(
		IEGLPathEntry[] oldResolvedPath,
		IEGLPathEntry[] newResolvedPath,
		final EGLProject project) {
	
		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		boolean needToUpdateDependents = false;
		EGLElementDelta delta = new EGLElementDelta(getEGLModel());
		boolean hasDelta = false;
		int oldLength = oldResolvedPath.length;
		int newLength = newResolvedPath.length;
			
		//final IndexManager indexManager = manager.getIndexManager();
		final Object indexManager = null;
		
		Map oldRoots = null;
		IPackageFragmentRoot[] roots = null;
		if (project.isOpen()) {
			try {
				roots = project.getPackageFragmentRoots();
			} catch (EGLModelException e) {
			}
		} else {
			Map allRemovedRoots ;
			if ((allRemovedRoots = manager.deltaProcessor.removedRoots) != null) {
		 		roots = (IPackageFragmentRoot[]) allRemovedRoots.get(project);
			}
		}
		if (roots != null) {
			oldRoots = new HashMap();
			for (int i = 0; i < roots.length; i++) {
				IPackageFragmentRoot root = roots[i];
				oldRoots.put(root.getPath(), root);
			}
		}
		for (int i = 0; i < oldLength; i++) {
			
			int[] index = eglpathContains(newResolvedPath, oldResolvedPath[i]);
			if (index.length == 0) {
				// do not notify remote project changes
				if (oldResolvedPath[i].getEntryKind() == IEGLPathEntry.CPE_PROJECT){
					needToUpdateDependents = true;
					this.needCycleCheck = true;
					continue; 
				}
	
				IPackageFragmentRoot[] pkgFragmentRoots = null;
				if (oldRoots != null) {
					IPackageFragmentRoot oldRoot = (IPackageFragmentRoot)  oldRoots.get(oldResolvedPath[i].getPath());
					if (oldRoot != null) { // use old root if any (could be none if entry wasn't bound)
						pkgFragmentRoots = new IPackageFragmentRoot[] { oldRoot };
					}
				}
				if (pkgFragmentRoots == null) {
					try {
						ObjectVector accumulatedRoots = new ObjectVector();
						HashSet rootIDs = new HashSet(5);
						rootIDs.add(project.rootID());
						project.computePackageFragmentRoots(
							oldResolvedPath[i], 
							accumulatedRoots, 
							rootIDs,
							true, // inside original project
							false, // don't check existency
							false); // don't retrieve exported roots
						pkgFragmentRoots = new IPackageFragmentRoot[accumulatedRoots.size()];
						accumulatedRoots.copyInto(pkgFragmentRoots);
					} catch (EGLModelException e) {
						pkgFragmentRoots =  new IPackageFragmentRoot[] {};
					}
				}
				addEGLPathDeltas(pkgFragmentRoots, IEGLElementDelta.F_REMOVED_FROM_EGLPATH, delta);
				
				int changeKind = oldResolvedPath[i].getEntryKind();
				needToUpdateDependents |= (changeKind == IEGLPathEntry.CPE_SOURCE) || oldResolvedPath[i].isExported();
	
				// Remove the .java files from the index for a source folder
				// For a lib folder or a .jar file, remove the corresponding index if not shared.
				if (indexManager != null) {
//					IEGLPathEntry oldEntry = oldResolvedPath[i];
//					final IPath path = oldEntry.getPath();
//					switch (changeKind) {
//						case IEGLPathEntry.CPE_SOURCE:
//							final char[][] exclusionPatterns = ((EGLPathEntry)oldEntry).fullExclusionPatternChars();
//							postAction(new IPostAction() {
//								public String getID() {
//									return path.toString();
//								}
//								public void run() throws EGLModelException {
//									indexManager.removeSourceFolderFromIndex(project, path, exclusionPatterns);
//								}
//							}, 
//							REMOVEALL_APPEND);
//							break;
//						case IEGLPathEntry.CPE_LIBRARY:
//							final DeltaProcessor deltaProcessor = manager.deltaProcessor;
//							postAction(new IPostAction() {
//								public String getID() {
//									return path.toString();
//								}
//								public void run() throws EGLModelException {
//									if (deltaProcessor.otherRoots.get(path) == null) { // if root was not shared
//										indexManager.discardJobs(path.toString());
//										indexManager.removeIndex(path);
//										// TODO: we could just remove the in-memory index and have the indexing check for timestamps
//									}
//								}
//							}, 
//							REMOVEALL_APPEND);
//							break;
//					}		
				}
				hasDelta = true;
	
			} else {
				// do not notify remote project changes
				if (oldResolvedPath[i].getEntryKind() == IEGLPathEntry.CPE_PROJECT){
					this.needCycleCheck |= (oldResolvedPath[i].isExported() != newResolvedPath[index[0]].isExported());
					continue; 
				}				
				needToUpdateDependents |= (oldResolvedPath[i].isExported() != newResolvedPath[index[0]].isExported());
				if (!foundIndex(i, index)) { //reordering of the eglpath
						addEGLPathDeltas(
							project.computePackageFragmentRoots(oldResolvedPath[i]),
							IEGLElementDelta.F_REORDER,
							delta);
						int changeKind = oldResolvedPath[i].getEntryKind();
						needToUpdateDependents |= (changeKind == IEGLPathEntry.CPE_SOURCE);
		
						hasDelta = true;
				}
				
			}
		}
	
		for (int i = 0; i < newLength; i++) {
	
			int[] index = eglpathContains(oldResolvedPath, newResolvedPath[i]);
			if (index.length == 0) {
				// do not notify remote project changes
				if (newResolvedPath[i].getEntryKind() == IEGLPathEntry.CPE_PROJECT){
					needToUpdateDependents = true;
					this.needCycleCheck = true;
					continue; 
				}
				addEGLPathDeltas(
					project.computePackageFragmentRoots(newResolvedPath[i]),
					IEGLElementDelta.F_ADDED_TO_EGLPATH,
					delta);
				int changeKind = newResolvedPath[i].getEntryKind();
				
				// Request indexing
				if (indexManager != null) {
//					switch (changeKind) {
//						case IEGLPathEntry.CPE_LIBRARY:
//							boolean pathHasChanged = true;
//							final IPath newPath = newResolvedPath[i].getPath();
//							for (int j = 0; j < oldLength; j++) {
//								IEGLPathEntry oldEntry = oldResolvedPath[j];
//								if (oldEntry.getPath().equals(newPath)) {
//									pathHasChanged = false;
//									break;
//								}
//							}
//							if (pathHasChanged) {
//								postAction(new IPostAction() {
//									public String getID() {
//										return newPath.toString();
//									}
//									public void run() throws EGLModelException {
//										indexManager.indexLibrary(newPath, project.getProject());
//									}
//								}, 
//								REMOVEALL_APPEND);
//							}
//							break;
//						case IEGLPathEntry.CPE_SOURCE:
//							IEGLPathEntry entry = newResolvedPath[i];
//							final IPath path = entry.getPath();
//							final char[][] exclusionPatterns = ((EGLPathEntry)entry).fullExclusionPatternChars();
//							postAction(new IPostAction() {
//								public String getID() {
//									return path.toString();
//								}
//								public void run() throws EGLModelException {
//									indexManager.indexSourceFolder(project, path, exclusionPatterns);
//								}
//							}, 
//							APPEND); // append so that a removeSourceFolder action is not removed
//							break;
//					}
				}
				
				needToUpdateDependents |= (changeKind == IEGLPathEntry.CPE_SOURCE) || newResolvedPath[i].isExported();
				hasDelta = true;
	
			} // eglpath reordering has already been generated in previous loop
		}
	
		if (hasDelta) {
			this.addDelta(delta);
		}
		if (needToUpdateDependents){
			updateAffectedProjects(project.getProject().getFullPath());
		}
	}

	protected EGLProject getProject() {
		return ((EGLProject) getElementsToProcess()[0]);
	}


	/**
	 * Returns <code>true</code> if this operation performs no resource modifications,
	 * otherwise <code>false</code>. Subclasses must override.
	 */
	public boolean isReadOnly() {
		return !this.canChangeResource;
	}

	protected void saveEGLPathIfNecessary() throws EGLModelException {
		
		if (!this.canChangeResource || !this.needSave) return;
				
		IEGLPathEntry[] eglpathForSave;
		EGLProject project = getProject();
		if (this.newRawPath == ReuseEGLPath || this.newRawPath == UpdateEGLPath){
			eglpathForSave = project.getRawEGLPath();
		} else {
			eglpathForSave = this.newRawPath;
		}
		IPath outputLocationForSave;
		if (this.newOutputLocation == ReuseOutputLocation){
			if (isProjectBinary(project)) {
				outputLocationForSave = null;
			}
			else {
				outputLocationForSave = project.getOutputLocation(true);
			}
			
		} else {
			outputLocationForSave = this.newOutputLocation;
		}
		// if read-only .eglpath, then the eglpath setting will never been performed completely
		if (project.saveEGLPath(eglpathForSave, outputLocationForSave)) {
			this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		}
	}
	
	private boolean isProjectBinary(EGLProject proj) {
		IEGLProjectFileUtility util = EGLProjectFileUtilityLocator.INSTANCE.getUtil();
		if (util != null) {
			return util.isBinaryProject(proj.getProject());
		}
		return false;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer(20);
		buffer.append("SetEGLPathOperation\n"); //$NON-NLS-1$
		buffer.append(" - eglpath : "); //$NON-NLS-1$
		if (this.newRawPath == ReuseEGLPath){
			buffer.append("<Reuse Existing EGLPath>"); //$NON-NLS-1$
		} else {
			buffer.append("{"); //$NON-NLS-1$
			for (int i = 0; i < this.newRawPath.length; i++) {
				if (i > 0) buffer.append(","); //$NON-NLS-1$
				IEGLPathEntry element = this.newRawPath[i];
				buffer.append(" ").append(element.toString()); //$NON-NLS-1$
			}
		}
		buffer.append("\n - output location : ");  //$NON-NLS-1$
		if (this.newOutputLocation == ReuseOutputLocation){
			buffer.append("<Reuse Existing Output Location>"); //$NON-NLS-1$
		} else {
			buffer.append(this.newOutputLocation.toString()); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	private void updateEGLPath() throws EGLModelException {

		EGLProject project = ((EGLProject) getElementsToProcess()[0]);

		beginTask(EGLModelResources.bind(EGLModelResources.eglpathSettingProgress, project.getElementName()), 2);

		// SIDE-EFFECT: from thereon, the eglpath got modified
		project.setRawEGLPath0(this.newRawPath);

		// resolve new path (asking for marker creation if problems)
		if (this.newResolvedPath == null) {
			this.newResolvedPath = project.getResolvedEGLPath(true, this.canChangeResource);
		}
		
		if (this.oldResolvedPath != null) {
			generateEGLPathChangeDeltas(
				this.oldResolvedPath,
				this.newResolvedPath,
				project);
		} else {
			this.needCycleCheck = true;
			updateAffectedProjects(project.getProject().getFullPath());
		}
		
		updateCycleMarkersIfNecessary(newResolvedPath);
	}

	/**
	 * Update projects which are affected by this eglpath change:
	 * those which refers to the current project as source
	 */
	protected void updateAffectedProjects(IPath prerequisiteProjectPath) {

		try {
			IEGLModel model = EGLModelManager.getEGLModelManager().getEGLModel();
			IEGLProject originatingProject = getProject();
			IEGLProject[] projects = model.getEGLProjects();
			for (int i = 0, projectCount = projects.length; i < projectCount; i++) {
				try {
					EGLProject project = (EGLProject) projects[i];
					if (project.equals(originatingProject)) continue; // skip itself
					
					// consider ALL dependents (even indirect ones), since they may need to
					// flush their respective namelookup caches (all pkg fragment roots).

					IEGLPathEntry[] eglpath = project.getExpandedEGLPath(true);
					for (int j = 0, entryCount = eglpath.length; j < entryCount; j++) {
						IEGLPathEntry entry = eglpath[j];
						if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT
							&& entry.getPath().equals(prerequisiteProjectPath)) {
							project.setRawEGLPath(
								UpdateEGLPath, 
								SetEGLPathOperation.ReuseOutputLocation, 
								this.fMonitor, 
								this.canChangeResource,  
								project.getResolvedEGLPath(true), 
								false, // updating only - no validation
								false); // updating only - no need to save
							break;
						}
					}
				} catch (EGLModelException e) {
				}
			}
		} catch (EGLModelException e) {
		}
		
	}

	/**
	 * Update cycle markers
	 */
	protected void updateCycleMarkersIfNecessary(IEGLPathEntry[] newResolvedPath) {

		if (!this.needCycleCheck) return;
		if (!this.canChangeResource) return;
		 
		try {
			EGLProject project = getProject();
			if (!project.hasCycleMarker() && !project.hasEGLPathCycle(project.getResolvedEGLPath(true))){
				return;
			}
		
			postAction(
				new IPostAction() {
					public String getID() {
						return "updateCycleMarkers";  //$NON-NLS-1$
					}
					public void run() throws EGLModelException {
						EGLProject.updateAllCycleMarkers();
					}
				},
				REMOVEALL_APPEND);
		} catch(EGLModelException e){
		}
	}

	/**
	 * Sets the output location of the pre-specified project.
	 *
	 * <p>This can cause changes in package fragments, in case either  the
	 * old or new output location folder are considered as a package fragment.
	 */
	protected void updateOutputLocation() throws EGLModelException {
		
		EGLProject project= ((EGLProject) getElementsToProcess()[0]);

		beginTask(EGLModelResources.bind(EGLModelResources.eglpathSettingProgress, project.getElementName()), 2);
		
		IPath oldLocation= project.getOutputLocation();
	
		// see if this will cause any package fragments to be added
		boolean deltaToFire= false;
		EGLElementDelta delta = newEGLElementDelta();
		ArrayList added= determineAffectedPackageFragments(oldLocation);
		Iterator iter = added.iterator();
		while (iter.hasNext()){
			IPackageFragment frag= (IPackageFragment)iter.next();
			((IPackageFragmentRoot)frag.getParent()).close();
			if (!Util.isExcluded(frag)) {
				delta.added(frag);
				deltaToFire = true;
			}
		}
	
		// see if this will cause any package fragments to be removed
		ArrayList removed= determineAffectedPackageFragments(this.newOutputLocation);
		iter = removed.iterator();
		while (iter.hasNext()){
			IPackageFragment frag= (IPackageFragment)iter.next();
			((IPackageFragmentRoot)frag.getParent()).close(); 
			if (!Util.isExcluded(frag)) {
				delta.removed(frag);
				deltaToFire = true;
			}
		}

		EGLModelManager.PerProjectInfo perProjectInfo = EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(project.getProject());
		synchronized (perProjectInfo) {
			perProjectInfo.outputLocation = this.newOutputLocation;
		}
				
		if (deltaToFire) {
			addDelta(delta);	
		}
		worked(1);
	}
	
	/**
	 * Update projects references so that the build order is consistent with the eglpath
	 */
	protected void updateProjectReferencesIfNecessary() throws EGLModelException {
		
		if (!this.canChangeResource) return;
		if (this.newRawPath == ReuseEGLPath || this.newRawPath == UpdateEGLPath) return;
	
		EGLProject jproject = getProject();
		String[] oldRequired = jproject.projectPrerequisites(this.oldResolvedPath);

		if (this.newResolvedPath == null) {
			this.newResolvedPath = jproject.getResolvedEGLPath(this.newRawPath, null, true, this.needValidation, null /*no reverse map*/);
		}
		String[] newRequired = jproject.projectPrerequisites(this.newResolvedPath);
	
		try {		
			IProject project = jproject.getProject();
			IProjectDescription description = project.getDescription();
			 
			IProject[] projectReferences = description.getReferencedProjects();
			
			HashSet oldReferences = new HashSet(projectReferences.length);
			for (int i = 0; i < projectReferences.length; i++){
				String projectName = projectReferences[i].getName();
				oldReferences.add(projectName);
			}
			HashSet newReferences = (HashSet)oldReferences.clone();
	
			for (int i = 0; i < oldRequired.length; i++){
				String projectName = oldRequired[i];
				newReferences.remove(projectName);
			}
			for (int i = 0; i < newRequired.length; i++){
				String projectName = newRequired[i];
				newReferences.add(projectName);
			}
	
			Iterator iter;
			int newSize = newReferences.size();
			
			checkIdentity: {
				if (oldReferences.size() == newSize){
					iter = newReferences.iterator();
					while (iter.hasNext()){
						if (!oldReferences.contains(iter.next())){
							break checkIdentity;
						}
					}
					return;
				}
			}
			String[] requiredProjectNames = new String[newSize];
			int index = 0;
			iter = newReferences.iterator();
			while (iter.hasNext()){
				requiredProjectNames[index++] = (String)iter.next();
			}
			Util.sort(requiredProjectNames); // ensure that if changed, the order is consistent
			
			IProject[] requiredProjectArray = new IProject[newSize];
			IWorkspaceRoot wksRoot = project.getWorkspace().getRoot();
			for (int i = 0; i < newSize; i++){
				requiredProjectArray[i] = wksRoot.getProject(requiredProjectNames[i]);
			}
	
			description.setReferencedProjects(requiredProjectArray);
			project.setDescription(description, this.fMonitor);
	
		} catch(CoreException e){
			throw new EGLModelException(e);
		}
	}

	public IEGLModelStatus verify() {

		IEGLModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}

		if (needValidation) {
			IEGLProject project = (IEGLProject) getElementToProcess();
			// retrieve eglpath 
			IEGLPathEntry[] entries = this.newRawPath;
			if (entries == ReuseEGLPath){
				try {
					entries = project.getRawEGLPath();			
				} catch (EGLModelException e) {
					return e.getEGLModelStatus();
				}
			}		
			// retrieve output location
			IPath outputLocation = this.newOutputLocation;
			if (outputLocation == ReuseOutputLocation){
				try {
					outputLocation = project.getOutputLocation();
				} catch (EGLModelException e) {
					return e.getEGLModelStatus();
				}
			}
					
			// perform validation
			return EGLConventions.validateEGLPath(
				project,
				entries,
				outputLocation);
		}
		
		return EGLModelStatus.VERIFIED_OK;
	}
}

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
package org.eclipse.edt.ide.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class EGLSourceFolderOperation extends WorkspaceModifyOperation {
	
	private EGLSourceFolderConfiguration configuration;

	public EGLSourceFolderOperation(EGLSourceFolderConfiguration configuration) {
		super();
		this.configuration = configuration;
	}
	
	public EGLSourceFolderOperation(EGLSourceFolderConfiguration configuration, ISchedulingRule rule) {
		super(rule);
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {
		
		IEGLProject fCurrEProject;
		
		IEGLPathEntry[] fEntries;
		IPath fOutputLocation;
		
		IEGLPathEntry[] fNewEntries;
		IPath fNewOutputLocation;
		
		IPath projPath;
		
		try {
			if (monitor == null) {
				monitor= new NullProgressMonitor();
			}
			
			//Initialize needed variables
			IPath path = new Path(configuration.getProjectName());
						
			IProject project= ResourcesPlugin.getWorkspace().getRoot().getProject(path.toString());
			fCurrEProject = EGLCore.create(project);
			fEntries= fCurrEProject.getRawEGLPath();
			fOutputLocation= fCurrEProject.getOutputLocation();
			
			path = path.append(configuration.getSourceFolderName());
			IEGLPathEntry newEntry= EGLCore.newSourceEntry(path.makeAbsolute());
			
			projPath= fCurrEProject.getProject().getFullPath();
			
			//Update path and entry information as necessary
			ArrayList newEntries= new ArrayList(fEntries.length + 1);
			int projectEntryIndex= -1;

			for (int i= 0; i < fEntries.length; i++) {
				IEGLPathEntry curr= fEntries[i];
				if (curr.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
					if (projPath.equals(curr.getPath())) {
						projectEntryIndex= i;
					}	
				}
				newEntries.add(curr);
			}

			if (projectEntryIndex != -1) {
				newEntries.set(projectEntryIndex, newEntry);
			} else {
				newEntries.add(EGLCore.newSourceEntry(path.makeAbsolute()));
			}
			
			fNewEntries= (IEGLPathEntry[]) newEntries.toArray(new IEGLPathEntry[newEntries.size()]);
			fNewOutputLocation= fOutputLocation;
			
			
			IEGLModelStatus status= EGLConventions.validateEGLPath(fCurrEProject, fNewEntries, fNewOutputLocation);
			if (!status.isOK()) {
				if (fOutputLocation.equals(projPath)) {
					fNewOutputLocation= projPath.append(EDTCorePreferenceConstants.getPreferenceStore().getString(EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER));
				}
			}

			//Do the create operation
			//MATT Figure out where this block of code should go
//			if (fOutputLocation.equals(projPath) && !fNewOutputLocation.equals(projPath)) {
//				if (BuildPathsBlock.hasClassfiles(fCurrEProject.getProject())) {
//					if (BuildPathsBlock.getRemoveOldBinariesQuery(getShell()).doQuery(projPath)) {
//						BuildPathsBlock.removeOldClassfiles(fCurrEProject.getProject());
//					}
//				}
//			}		
			
			String relPath= configuration.getSourceFolderName();
				
			IFolder folder= fCurrEProject.getProject().getFolder(relPath);
			if (!folder.exists()) {
				CoreUtility.createFolder(folder, true, true, null);			
			}
			
			fCurrEProject.setRawEGLPath(fNewEntries, fNewOutputLocation, monitor);
		}
		finally {
			monitor.done();
		}
	}

}

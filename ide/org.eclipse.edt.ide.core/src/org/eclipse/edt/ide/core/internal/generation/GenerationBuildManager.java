/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.builder.BuildManager;

public class GenerationBuildManager {
	protected static final IPath BUILD_MANAGER_SAVED_FILE = EDTCoreIDEPlugin.getPlugin().getStateLocation().append(".genbuildmanager"); //$NON-NLS-1$
	
	private static GenerationBuildManager INSTANCE = new GenerationBuildManager();
	
	private HashMap<String, BuildManagerEntry> projectMap;
	
	public static GenerationBuildManager getInstance() {
		return INSTANCE;
	}
	
	private GenerationBuildManager() {
		read();
	}
	
	private void prune() {
		ArrayList<IProject> projectsToRemove = new ArrayList<IProject>();
		
		Iterator<String> iter = projectMap.keySet().iterator();
		while (iter.hasNext()) {
			String projName = iter.next();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
			if (!project.exists() || !project.isOpen()) {
				projectsToRemove.add(project);
			}
			else {
				BuildManagerEntry entry = getEntry(project);
				if (entry.getState() < BuildManager.EDT_VERSION) {
					entry.setState(BuildManager.FULL_BUILD_REQUIRED_STATE);
				}
			}
		}
		
		if (projectsToRemove.size() > 0) {
			for (Iterator<IProject> iterator = projectsToRemove.iterator(); iterator.hasNext();) {
				IProject project = iterator.next();
				removeProject(project);
			}
			save();
		}
	}
	
	private void read() {
		File file = BUILD_MANAGER_SAVED_FILE.toFile();
		if (file.exists()) {
			try {
				ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
				
				try {
					projectMap = (HashMap)inputStream.readObject();
				}
				finally {
					inputStream.close();
				}
				
				prune();
			}
			catch (Exception e) {
				projectMap = new HashMap<String, BuildManagerEntry>();
			}
		}
		else {
			projectMap = new HashMap<String, BuildManagerEntry>();
		}
	}
	
	private void save() {
		File file = BUILD_MANAGER_SAVED_FILE.toFile();
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			
			try {
				outputStream.writeObject(projectMap);
			}
			finally {
				outputStream.close();
			}
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
	}
	
	private BuildManagerEntry getEntry(IProject project) {
		BuildManagerEntry entry = projectMap.get(project.getName());
		
		if (entry == null) {
			entry = new BuildManagerEntry();
			projectMap.put(project.getName(), entry);
		}
		
		return entry;
	}
	
	public void removeProject(IProject project) {
		projectMap.remove( project.getName() );
		save();
	}
	
	public void clear(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	entry.setState(BuildManager.FULL_BUILD_REQUIRED_STATE);
    	save();
    }
	
	public boolean getProjectState(IProject project) {
		BuildManagerEntry entry = getEntry(project);
		return entry.getState() == BuildManager.EDT_VERSION;
	}
	
	public void setProjectState(IProject project, boolean state) {
		BuildManagerEntry entry = getEntry(project);
		entry.setState(state ? BuildManager.EDT_VERSION : BuildManager.FULL_BUILD_REQUIRED_STATE);
		save();
	}
	
	public String[] getDefaultGenIDs(IProject project) {
		BuildManagerEntry entry = getEntry(project);
		return entry.getDefaultGenIDs();
	}
	
	public void putProject(IProject project, String[] defaultGenIDs){
		BuildManagerEntry entry = getEntry(project);
		entry.setDefaultGenIDs(defaultGenIDs);
		entry.setState(BuildManager.EDT_VERSION);
		save();
	}
	
	private static class BuildManagerEntry implements Serializable {
		private static final long serialVersionUID = 3834040728112637975L;
		
		// state == -1, project never built or failed last build
		// state >= 0, project successfully built, integer is build version number
		private int state = BuildManager.FULL_BUILD_REQUIRED_STATE;
		
		private String[] defaultGenIds;
		
		public int getState() {
			return state;
		}
		
		public void setState(int state) {
			this.state = state;
		}
		
		public String[] getDefaultGenIDs() {
			return defaultGenIds;
		}
		
		public void setDefaultGenIDs(String[] ids) {
			this.defaultGenIds = ids;
		}
	}
}

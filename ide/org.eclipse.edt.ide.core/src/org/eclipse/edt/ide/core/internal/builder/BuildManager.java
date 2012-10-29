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
package org.eclipse.edt.ide.core.internal.builder;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;


/**
 * @author cduval
 *
 * TODO There is a case where a user could have deleted a project and then added the project back again, all while the EDT plugin was not loaded.
 * In this situation, prune would not remove the project, and we would use an old/invalid build state.  A clean would solve this problem.  There are two solutions:
 * 1) Store a token in the projects .metadata directory.  If the project exists in Prune, we check for the token.
 * 2) Split the build manager up into files that are stored in the projects .metadata directory - difficult because we store information in reverse order
 */
public class BuildManager {
	private static final String NO_OUTPUT_LOCATION = ""; //$NON-NLS-1$
    protected static final IPath BUILD_MANAGER_SAVED_FILE = EDTCoreIDEPlugin.getPlugin().getStateLocation().append(".buildmanager"); //$NON-NLS-1$
	
	//This is the version of the EDT plugin classes.
	//If the persistence changes for any of EDT classes, we need to bump up this version.
	//Each project when built will check for version change and do a clean prior to full build.
	public static final int   EDT_VERSION = 2;
	public static final int   FULL_BUILD_REQUIRED_STATE = -1;
	
	private static BuildManager INSTANCE = new BuildManager();
	private static final int MAX_PART_CHANGE = 1000;
	
	public static class BuildPathEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		protected static final int ENTRY_TYPE_UNKNOWN = -1;
		protected static final int ENTRY_TYPE_PROJECT = 1;
		protected static final int ENTRY_TYPE_ZIPFILE = 2;
		private int type = ENTRY_TYPE_UNKNOWN;
		private String id = "";
		public BuildPathEntry(int type , String id){
			this.type = type;
			this.id = id;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
	}
	
	private static class BuildManagerEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		private HashSet mailBoxes = new HashSet();
    	private MailBox mailBox;
    	
    	//state == -1, project never built or failed last build
    	//state >=  0, project successfully built, integer is build version number
    	private int state = FULL_BUILD_REQUIRED_STATE;
    	
    	private String[] requiredProjects = NO_REQUIRED_PROJECTS;
    	private String[] sourceLocations = NO_SOURCE_LOCATIONS;
    	private String   outputLocation = NO_OUTPUT_LOCATION;
    	private BuildPathEntry[] pathEntries = NO_EGLPATH_ENTRIES;
    	private String compilerId = NO_COMPILER;
    	
    	public BuildManagerEntry(){
    		this.mailBox = new MailBox();
    	}
    	
    	public Set getDependentMailBoxes(){
    		return mailBoxes;
    	}
    	
    	public MailBox getMailBox(){
    		return mailBox;
    	}
    	
    	
		public int getState() {
			return state;
		}
		public void setState(int state) {
			this.state = state;
		}
		
		public String[] getRequiredProjects() {
			return requiredProjects;
		}
		
		public void setRequiredProjects(String[] requiredProjects) {
			this.requiredProjects = requiredProjects;
		}
		
		
		public BuildPathEntry[] getPathEntries() {
			return pathEntries;
		}

		public void setPathEntries(BuildPathEntry[] pathEntries) {
			this.pathEntries = pathEntries;
		}

		public String[] getSourceLocations() {
			return sourceLocations;
		}
		public void setSourceLocations(String[] sourceLocations) {
			this.sourceLocations = sourceLocations;
		}
		
		public String getOutputLocation() {
			return outputLocation;
		}
		public void setOutputLocation(String outputLocation) {
			this.outputLocation = outputLocation;
		}
		
		public String getCompilerId() {
			return compilerId;
		}
		
		public void setCompilerId(String id) {
			this.compilerId = id;
		}
    }
    
    private static class MailBox implements Serializable{
		private static final long serialVersionUID = 1L;
		private ArrayList changes = new ArrayList();
    	private boolean full = false;
    	
		public boolean isFull() {
			return full;
		}
		
		public void addChange(BuildManagerChange entry){
			if (!full){
				changes.add(entry);
				if (changes.size() >= MAX_PART_CHANGE){
					changes.clear();
					full = true;					
				}
			}
		}
		
		public BuildManagerChange[] getChanges(){
			return (BuildManagerChange[])changes.toArray(new BuildManagerChange[changes.size()]);
		}
		
		public void clear(){
			full = false;
			changes = new ArrayList();
		}
    }
    
    private HashMap projectMap;

	private static final String[] NO_REQUIRED_PROJECTS = new String[0];
	private static final String[] NO_SOURCE_LOCATIONS = new String[0];
	private static final BuildPathEntry[] NO_EGLPATH_ENTRIES = new BuildPathEntry[0];
	private static final String NO_COMPILER = ""; //$NON-NLS-1$
    
    private BuildManager() {
		super();
		read();
	}

    private void prune(){
    	ArrayList projectsToRemove = new ArrayList();
    	
    	Iterator iter = projectMap.keySet().iterator();
    	while(iter.hasNext()){
    		String projName = (String)iter.next();
    		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
    		if (!project.exists() || !project.isOpen()){
    			projectsToRemove.add(project);
    		}else {
    			BuildManagerEntry entry = getEntry(project);
    			if (entry.getState() < EDT_VERSION){
    				entry.setState(FULL_BUILD_REQUIRED_STATE);
    			}
    		}
    	}
    	
    	if(projectsToRemove.size() > 0){
    		for (Iterator iterator = projectsToRemove.iterator(); iterator.hasNext();) {
				IProject project = (IProject) iterator.next();
				removeProject(project);
			}
    		save();
    	}
    }
    
    private void read() {
		File file = BUILD_MANAGER_SAVED_FILE.toFile();
		if (file.exists()){
			try {
				ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
				
				try{
					projectMap = (HashMap)inputStream.readObject();
				}finally{
					inputStream.close();
				}
			      
		        prune();
			} catch (Exception e) {
				projectMap = new HashMap();
			}
		}else{
			projectMap = new HashMap();
		}
		
		
	}

	public static BuildManager getInstance() {
         return INSTANCE;
    }
	
	public void putProject(IProject project, IProject[] requiredProjects,IContainer[] sourceLocations,IContainer outputLocation,IBuildPathEntry[] pathentries){
		BuildManagerEntry entry = getEntry(project);
		String[] requiredProjectStrings = new String[requiredProjects.length];
    	for (int i = 0; i < requiredProjects.length; i++) {
    		requiredProjectStrings[i] = requiredProjects[i].getName();
			BuildManagerEntry otherEntry = getEntry(requiredProjects[i]);
			otherEntry.getDependentMailBoxes().add(entry.getMailBox());
		}
    	entry.setRequiredProjects(requiredProjectStrings);
    	
    	if (sourceLocations != null){
	    	String[] sourceLocationsStrings = new String[sourceLocations.length];
	    	for (int i = 0; i < sourceLocations.length; i++){
	    		sourceLocationsStrings[i] = sourceLocations[i].getFullPath().toString();
	    	}
	    	
	    	entry.setSourceLocations(sourceLocationsStrings);
    	}else{
    		entry.setSourceLocations(NO_SOURCE_LOCATIONS);
    	}
    	
    	if (outputLocation != null){
	    	entry.setOutputLocation(outputLocation.getFullPath().toString());
    	}else{
    		entry.setOutputLocation(NO_OUTPUT_LOCATION);
    	}
    	
    	IIDECompiler compiler = ProjectSettingsUtility.getCompiler(project);
    	if (compiler == null) {
    		entry.setCompilerId(NO_COMPILER);
    	}
    	else {
    		entry.setCompilerId(compiler.getId());
    	}

    	if (pathentries != null){
    		BuildPathEntry[]newpaths = new BuildPathEntry[pathentries.length];
    		for (int i = 0; i < pathentries.length; i++){
    			IBuildPathEntry newEntry = pathentries[i];
    			newpaths[i] = new BuildPathEntry(newEntry.isProject()? BuildPathEntry.ENTRY_TYPE_PROJECT: BuildPathEntry.ENTRY_TYPE_ZIPFILE,newEntry.getID());
    		}
    		entry.setPathEntries(newpaths);
    	}
    	
    	entry.setState(EDT_VERSION);
    	entry.getMailBox().clear();
    	
    	save();
    }
	
	private BuildManagerEntry getEntry(IProject project){
    	BuildManagerEntry entry = (BuildManagerEntry)projectMap.get(project.getName());
    	
    	if(entry == null){
    		entry = new BuildManagerEntry();
        	projectMap.put(project.getName(), entry);
    	}
    	
    	return entry;
    }
    
	public void recordPart(IProject project, String packageName, String partName, int partType){
		BuildManagerEntry entry = getEntry(project);
    	for (Iterator iter = entry.getDependentMailBoxes().iterator(); iter.hasNext();) {
			MailBox mailBox = (MailBox) iter.next();
			mailBox.addChange(new BuildManagerPartChange(packageName, partName, partType));
    	}
	}
	
	public void recordPackage(IProject project, String packageName){
		BuildManagerEntry entry = getEntry(project);
    	for (Iterator iter = entry.getDependentMailBoxes().iterator(); iter.hasNext();) {
			MailBox mailBox = (MailBox) iter.next();
			mailBox.addChange(new BuildManagerPackageChange(packageName));
    	}
	}
   
    public BuildManagerChange[] getChanges(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getMailBox().getChanges();
    }
    
    public boolean isFullBuildRequired(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getMailBox().isFull();
    }
    
    public boolean getProjectState(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getState() == EDT_VERSION;
    }
    
    public String[] getRequiredProjects(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getRequiredProjects();
    }
    
    public BuildPathEntry[] getPathEntries(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getPathEntries();
    }
    
    public String[] getSourceLocations(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getSourceLocations();
    }
    
    public String getOutputLocation(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getOutputLocation();
    }
    
    public String getCompilerId(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	return entry.getCompilerId();
    }
    
    public void setProjectState(IProject project, boolean state){
    	BuildManagerEntry entry = getEntry(project);
    	entry.setState(state ? EDT_VERSION: FULL_BUILD_REQUIRED_STATE);
    	
    	save();
	}
    
    
   private void save(){
    	File file = BUILD_MANAGER_SAVED_FILE.toFile();
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			
			try{
				outputStream.writeObject(projectMap);
			}finally{
				outputStream.close();
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
    }
    
    public void clear(IProject project){
    	BuildManagerEntry entry = getEntry(project);
    	
    	entry.setState(FULL_BUILD_REQUIRED_STATE);
    	entry.getMailBox().clear();
    	entry.setRequiredProjects(NO_REQUIRED_PROJECTS);
    	entry.setSourceLocations(NO_SOURCE_LOCATIONS);
    	entry.setOutputLocation(NO_OUTPUT_LOCATION);
    	entry.setPathEntries(NO_EGLPATH_ENTRIES);
    	entry.setCompilerId(NO_COMPILER);
    	removeDependentProject(entry);
    	
    	save();
    }
    
    private void removeDependentProject(BuildManagerEntry entry){
    	MailBox mailBox = entry.getMailBox();
    	for (Iterator iter = projectMap.values().iterator(); iter.hasNext();) {
			BuildManagerEntry otherEntry = (BuildManagerEntry) iter.next();
			otherEntry.getDependentMailBoxes().remove(mailBox);	
		}  
    }
    
    public void removeProject(IProject project) {
    	BuildManagerEntry entry = getEntry(project);
    	removeDependentProject(entry);
	    projectMap.remove(project.getName());
	    save();
    }

	/**
     * FOR DEBUG ONLY
     *
     */
    public void clearAll(){
    	projectMap.clear();
    	BUILD_MANAGER_SAVED_FILE.toFile().delete();
    }
    
    // Debug
    public int getCount(){
    	return projectMap.size();
    }
}

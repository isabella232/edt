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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.edt.compiler.internal.eglar.EglarFile;
import org.eclipse.edt.compiler.internal.eglar.EglarFileCache;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.EGLNature;
import org.eclipse.edt.ide.core.internal.model.RUINature;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.core.utils.EGLProjectFileUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.preferences.CSSUIPreferenceNames;
import org.eclipse.wst.css.ui.internal.templates.TemplateContextTypeIdsCSS;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.utils.StringUtils;

public final class EGLProjectUtility {
	public static final String JAVA_DEFAULT_BIN_FOLDER = "bin"; //$NON-NLS-1$
	public  static final String JAVA_DEFAULT_SOURCE_FOLDER = "src"; //$NON-NLS-1$

	public static void addEGLNature(IProject project, IProgressMonitor monitor)
			throws CoreException {
		if (!project.hasNature(EGLNature.EGL_NATURE_ID)) {
			addNatureToProject(project, EGLNature.EGL_NATURE_ID, monitor);
		} else {
			monitor.worked(1);
		}
	}

	public static void addJavaNature(IProject project, IProgressMonitor monitor)
			throws CoreException {
		if (!project.hasNature(JavaCore.NATURE_ID)) {
			addNatureToProject(project, JavaCore.NATURE_ID, monitor);
		} else {
			monitor.worked(1);
		}
	}
	
	public static void addRUINature(IProject project, IProgressMonitor monitor) throws CoreException {
		boolean hasRUINatureID = project.hasNature(RUINature.RUI_NATURE_ID);
		
		if (!hasRUINatureID) {	
			addNatureToProject(project, RUINature.RUI_NATURE_ID, monitor);
		} else {
			monitor.worked(1);
		}
	}

	private static void addNatureToProject(IProject proj, String natureId,
			IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, monitor);
	}

	/**
	 * Return whether or not a classpath entry exists in the project's Java
	 * build path.
	 * 
	 * @param javaProject
	 * @param entry
	 * @return
	 * @throws JavaModelException
	 */
	public static boolean classpathEntryExists(IJavaProject javaProject,
			IClasspathEntry entry) throws JavaModelException {
		int jarIndex = -1;
		IClasspathEntry curEntry;
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();

		// Look for the jar file in the current build path
		for (int i = 0; i < oldEntries.length; i++) {
			curEntry = oldEntries[i];
			if (curEntry.getPath() != null) {
				if (curEntry.getPath().toString()
						.equalsIgnoreCase(entry.getPath().toString())) {
					jarIndex = i;
					break;
				}
			}
		}
		// We will add a new entry if the entry does not exist.
		if (jarIndex < 0) {
			return false;
		} else {
			return true;
		}
	}

	public static IEGLPathEntry[] createEGLConfiguration(ProjectConfiguration configuration,
			IProgressMonitor monitor) throws CoreException {

		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project= fWorkspaceRoot.getProject(configuration.getProjectName());
		IEGLProject fCurrEProject = EGLCore.create(project);

		List <PPListElement>eglPathEntries = configuration.getRequiredProjects();		
		if(eglPathEntries.size()==0 && !configuration.getProjectName().equals("")) { //$NON-NLS-1$
			eglPathEntries = initializeEGLPathEntries(project);
		}

		IPath eglOutputLocation = EGLProjectUtility.getDefaultEGLOutputPath(fCurrEProject);
		// create and set the output path first
		if (!fWorkspaceRoot.exists(eglOutputLocation)) {
			IFolder folder = fWorkspaceRoot.getFolder(eglOutputLocation);
			CoreUtility.createFolder(folder, true, true, null);
			folder.setDerived(true);
		}

		monitor.worked(2);

		int nEntries = eglPathEntries.size();
		IEGLPathEntry[] classpath = new IEGLPathEntry[nEntries];

		// create and set the class path
		for (int i = 0; i < nEntries; i++) {
			PPListElement entry = ((PPListElement) eglPathEntries.get(i));
			IResource res = entry.getResource();
			if ((res instanceof IFolder) && !res.exists()) {
				CoreUtility.createFolder((IFolder) res, true, true, null);
			}
			if (entry.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
				IPath folderOutput = (IPath) entry
						.getAttribute(PPListElement.OUTPUT);
				if (folderOutput != null && folderOutput.segmentCount() > 1) {
					IFolder folder = fWorkspaceRoot.getFolder(folderOutput);
					CoreUtility
							.createFolder((IFolder) folder, true, true, null);
				}
			}

			classpath[i] = entry.getEGLPathEntry();

			// set javadoc location
			configureJavaDoc(entry);
		}
		monitor.worked(1);
		fCurrEProject.setRawEGLPath(classpath, eglOutputLocation,
				new SubProgressMonitor(monitor, 7));
		
		return classpath;

	}

	/**
	 * Makes a folder if it doesn't already exist, and returns it.
	 * 
	 * @param project
	 *            project containing the folder.
	 * @param folderName
	 *            name of the folder.
	 * @param encoding
	 *            The desired encoding for files created here.
	 * @return the IFolder.
	 */
	static IFolder createFolderIfNecessary(IProject project, String folderName,
			String encoding) throws InvocationTargetException {
		IFolder folder = project.getFolder(folderName);
		if (!folder.exists()) {
			// Create the folders one at a time.
			int start = 0;
			int nextSlash = 0;
			String subFolderName;
			IFolder subFolder;
			nextSlash = folderName.indexOf('/', 0);
			try {
				while (nextSlash != -1) {
					subFolderName = folderName.substring(0, nextSlash);
					subFolder = project.getFolder(subFolderName);
					if (!subFolder.exists()) {
						subFolder.create(false, true, null);
						subFolder.setDefaultCharset(encoding, null);
					}
					start = nextSlash + 1;
					nextSlash = folderName.indexOf('/', start);
				}
				folder.create(false, true, null);
				folder.setDefaultCharset(encoding, null);
			} catch (CoreException ex) {
				throw new InvocationTargetException(ex, ex.getMessage());
			}
		}

		return folder;
	}
	
	private static IClasspathEntry[] createNewClasspathEntries( IJavaProject javaProject,
					String folderName, String binFolderName, String encoding, IClasspathEntry[] oldEntries)
					throws InvocationTargetException
	{
		// Get or create the necessary folders as well as the file.
		IFolder baseFolder = createFolderIfNecessary(javaProject.getProject(), folderName,
				encoding);
		IFolder binFolder = createFolderIfNecessary(javaProject.getProject(), binFolderName,
				encoding);

		try {
			IPackageFragmentRoot sourceRoot = javaProject
					.getPackageFragmentRoot(baseFolder);
			IPackageFragmentRoot binRoot = javaProject
					.getPackageFragmentRoot(binFolder);
			IClasspathEntry newEntry = JavaCore.newSourceEntry(
					sourceRoot.getPath(), new IPath[] {}, binRoot.getPath());
			if (!EGLProjectUtility.classpathEntryExists(javaProject, newEntry)) {
				//oldEntries = javaProject.getRawClasspath();
				IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
				newEntries[0] = newEntry;
				System.arraycopy(oldEntries, 0, newEntries, 1,
						oldEntries.length);
				return newEntries;
			}
		} catch( JavaModelException ex ) {
			throw new InvocationTargetException(ex, ex.getMessage());
		}
		// Return the old ones if a new entry wasn't created
		return oldEntries;
	}

	private static void configureJavaDoc(PPListElement entry) {
		if (entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY) {
			URL javadocLocation = (URL) entry
					.getAttribute(PPListElement.JAVADOC);
			IPath path = entry.getPath();
			if (entry.getEntryKind() == IEGLPathEntry.CPE_VARIABLE) {
				path = EGLCore.getResolvedVariablePath(path);
			}
			if (path != null) {
				// TODO Look at Javadoc
				JavaUI.setLibraryJavadocLocation(path, javadocLocation);
			}
		} else if (entry.getEntryKind() == IEGLPathEntry.CPE_CONTAINER) {
			Object[] children = entry.getChildren(false);
			for (int i = 0; i < children.length; i++) {
				PPListElement curr = (PPListElement) children[i];
				configureJavaDoc(curr);
			}
		}
	}

	private static List<PPListElement> initializeEGLPathEntries(
			IProject project) {
		List<PPListElement> newEGLPath = null;
		IEGLPathEntry[] eglPathEntries = null;

		IEGLProject eglProject = EGLCore.create(project);

		try {
			boolean projectExists = (project.exists() && project.getFile(
					".eglPath").exists()); //$NON-NLS-1$
			if (projectExists) {
				if (eglPathEntries == null) {
					eglPathEntries = eglProject.getRawEGLPath();
				}
			}
			if (eglPathEntries != null) {
				newEGLPath = EGLProjectUtility.getExistingEntries(
						eglPathEntries, eglProject);
			}
		} catch (CoreException e) {
			EDTUIPlugin.log( e );
		}
		if (newEGLPath == null) {
			newEGLPath = EGLProjectUtility.getDefaultClassPath(eglProject);
		}

		List<PPListElement> exportedEntries = new ArrayList<PPListElement>();
		for (int i = 0; i < newEGLPath.size(); i++) {
			PPListElement currEGL = (PPListElement) newEGLPath.get(i);
			if (currEGL.isExported()
					|| currEGL.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
				exportedEntries.add(currEGL);
			}
		}

		return newEGLPath;
	}

	public static ArrayList<PPListElement> getExistingEntries(
			IEGLPathEntry[] eglPathEntries, IEGLProject project) {
		ArrayList<PPListElement> newEGLPath = new ArrayList<PPListElement>();
		for (int i = 0; i < eglPathEntries.length; i++) {
			IEGLPathEntry curr = eglPathEntries[i];
			newEGLPath.add(PPListElement.createFromExisting(curr, project));
		}
		return newEGLPath;
	}

	/**
	 * This method adds a Java nature and Java build path to a project.
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	public static void createJavaConfiguration(IProject project, IProgressMonitor monitor)
							throws CoreException {
		addJavaNature( project, monitor );
		createJavaBuildPath( project, monitor );
	}

	/**
	 * Create a new Java build path for a new project.
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	public static void createJavaBuildPath( IProject project,
			IProgressMonitor monitor )
			throws CoreException {
		IPath javaOutputLocation = EGLProjectUtility
				.getDefaultJavaOutputPath(project);

		List<IClasspathEntry> newJavaClasspathEntry = EGLProjectUtility
				.getDefaultJavaClassPathEntryList( project );
		IClasspathEntry[] jclasspath = (IClasspathEntry[]) newJavaClasspathEntry
				.toArray(new IClasspathEntry[newJavaClasspathEntry.size()]);

		JavaCore.create(project).setRawClasspath(jclasspath,
				javaOutputLocation, new SubProgressMonitor(monitor, 7));
	}

	public static List<PPListElement> getDefaultClassPath(IEGLProject eproj) {
		List<PPListElement> list = new ArrayList<PPListElement>();
		IResource srcFolder;

		String sourceFolderName = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_SOURCE_FOLDER );
		if ( sourceFolderName.length() > 0 ) {
			srcFolder = eproj.getProject().getFolder(sourceFolderName);
		} else {
			srcFolder = eproj.getProject();
		}

		list.add(new PPListElement(eproj, IEGLPathEntry.CPE_SOURCE, srcFolder
				.getFullPath(), srcFolder));
		list.add(new PPListElement(eproj, IEGLPathEntry.CPE_CONTAINER, new Path(EDTCoreIDEPlugin.EDT_SYSTEM_RUNTIME_CONTAINER_ID), null));
		return list;
	}

	public static IPath getDefaultEGLOutputPath(IEGLProject eproj) {
		String outputLocationName = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER );
		return eproj.getProject().getFullPath().append(outputLocationName);
	}

	/**
	 * Return a list with classpath/container entries for the default
	 * Java source folder and the JRE.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static List<IClasspathEntry> getDefaultJavaClassPathEntryList(
			IProject project ) throws CoreException {
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		
		// Get the default Java source folder name from Java workspace
		// preferences
		String sourceFolderName;
		IPreferenceStore prefs = PreferenceConstants.getPreferenceStore();
		if ( prefs.getBoolean( PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ ) )
		{
			sourceFolderName = prefs.getString( PreferenceConstants.SRCBIN_SRCNAME );
		} else {
			sourceFolderName = JAVA_DEFAULT_SOURCE_FOLDER;
		}
		IResource srcFolder = project.getFolder(sourceFolderName);

		if ((srcFolder instanceof IFolder) && !srcFolder.exists())
			CoreUtility.createFolder((IFolder) srcFolder, true, true, null);

		IClasspathEntry javaClasspathEntry = JavaCore.newSourceEntry(
				srcFolder.getFullPath(), new Path[0], new Path[0], null );
		list.add(javaClasspathEntry);

		list.add(JavaCore.newContainerEntry(new Path(
				"org.eclipse.jdt.launching.JRE_CONTAINER"))); //$NON-NLS-1$

		return list;
	}

	/**
	 * Get the default Java output folder name from Java workspace
	 * preferences.
	 * 
	 * @param project
	 * @return
	 */
	public static IPath getDefaultJavaOutputPath(IProject project) {
		String outputLocationName;		
		IPreferenceStore prefs = PreferenceConstants.getPreferenceStore();
		if ( prefs.getBoolean( PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ ) )
		{
			outputLocationName = prefs.getString( PreferenceConstants.SRCBIN_BINNAME );
		} else {
			outputLocationName = JAVA_DEFAULT_BIN_FOLDER;
		}
		return project.getFullPath().append(outputLocationName);
	}

	/**
	 * Return whether a source entry exists in the classpath.
	 * (Don't count a source entry that only has a segment with the 
	 * project name, as found in general projects.)
	 * 
	 * @param project
	 * @return
	 * @throws JavaModelException
	 */
	public static boolean classpathEntriesExist( IJavaProject javaProject )
								throws JavaModelException
	{
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		for (int i = 0; i < entries.length; i++) {
			if ( entries[i].getPath().segmentCount() > 1 ) {
				return true;
			}
		}		
		return false;
	}
	
	public static boolean sourceClasspathEntryExists( IJavaProject javaProject )
								throws JavaModelException
	{
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE &&
					entries[i].getPath().segmentCount() > 1) {
				return true;
			}
		}		
		return false;
	}
   
   public static EglarFile createEglarFileFromPathEntry(IEGLProject project, IEGLPathEntry entry) throws IOException {
	    	IPath entryPath = entry.getPath();
	    	String eglarFile = getEglarAbsolutePath(entryPath, project.getProject());
	    	
	    	if(new File(eglarFile).exists()){
	    		return EglarFileCache.instance.getEglarFile(eglarFile);
	    	}
	    	else{
	    		return null;
	    	}
   }
   
   public static String getEglarAbsolutePath(IPath path, IProject project){
   	  String eglarFilePath = path.toOSString();
   	//if the eglar is external, then just use the path as the path for EglarFile
   	//else, consider if the eglar in under the given project or not.
   	if( ResourcesPlugin.getWorkspace().getRoot().exists( path )){
   		int index = eglarFilePath.indexOf(File.separator);
   		if(index == 0){
   			eglarFilePath = eglarFilePath.substring(1);
   			index = eglarFilePath.indexOf(File.separator);
   		}
   		if(index > -1){
   			String eglarProjName = eglarFilePath.substring(0, index);
   			if(eglarProjName.equals(project.getName())){	//use the eglar under this project
   				eglarFilePath = project.getProject().getLocation().toFile().getParentFile().getAbsolutePath() + File.separator + eglarFilePath;
   			} else{	//use eglar under other project
   				IProject eglarProject = project.getProject().getWorkspace().getRoot().getProject(eglarProjName);
   				eglarFilePath = eglarProject.getProject().getLocation().toFile().getParentFile().getAbsolutePath() + File.separator + eglarFilePath;
   			}
   		}
   	}
   	return eglarFilePath;
   }
   
   public static IFile[] createFilesFromEglar(IResource destination, EglarFile eglarFile, String[] entries, boolean useEntryFolder){
		IFile[] files = new IFile[entries.length];
		for(int i=0; i<entries.length; i++){
			String entryName = entries[i];
			if(useEntryFolder){
				files[i] = EGLProjectUtility.createFileFromEglar(destination, eglarFile, entryName, entryName);
			}
			else{
				String fileName = entryName;
				int index = fileName.lastIndexOf("/");
				if(index > -1){
					fileName = fileName.substring(index + 1);
				}				
				files[i] = EGLProjectUtility.createFileFromEglar(destination, eglarFile, entryName, fileName);
			}
		}
		return files;
	}
   
   public static IFile[] createFilesFromEglar(IResource destination, EglarFile eglarFile, String[] entries){
		return createFilesFromEglar(destination, eglarFile, entries, true);
   }
   
   
   /**
	 * copy the jar from eglar into the destination location under project
	 * @param destination
	 * @param eglarFile
	 * @param jarEntry
	 */
	public static IFile createFileFromEglar(IResource destination, EglarFile eglarFile, String entryName, String createFileName){
		try {
			if(!(destination instanceof IContainer)){
				return null;
			}
			if(!destination.exists()){
				if(destination instanceof IFolder) {
					EGLProjectFileUtility.createFolder(destination.getProject(), destination.getProjectRelativePath());
				}
				else {
					return null;
				}
			}
			
			ZipEntry zipEntry = eglarFile.getEntry(entryName);
			IFile targetResource = ((IContainer)destination).getFile(new Path(createFileName));
			InputStream is = eglarFile.getInputStream(zipEntry);
			if(targetResource.exists()){
				targetResource.setContents(is,IResource.KEEP_HISTORY, null);
			}
			else{
				int index = createFileName.lastIndexOf("/");
				if(index > 0){
					String folder = createFileName.substring(0, index);
					EGLProjectFileUtility.createFolder((IContainer)destination, new Path(folder));
				}
				targetResource.create(is, false, null);
			}
			return targetResource;			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void addClasspathEntriesIfNecessary(IProject project, IClasspathEntry[] classpathEntries) throws CoreException{
		IJavaProject javaProject; //The Java "view" of the project.
		if(project.hasNature(JavaCore.NATURE_ID)){
			javaProject = JavaCore.create(project);
			int index = -1;
			IClasspathEntry curEntry;
			List<IClasspathEntry> newClasspaths = new ArrayList<IClasspathEntry>();
			IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
			
			for(IClasspathEntry pathEntry: classpathEntries){
				// Look for the jar file in the current build path
				for (int i = 0; i < oldEntries.length; i++){
					curEntry = oldEntries[i];
					if (curEntry.getPath() != null ) 
					{
						if (  curEntry.getPath().toOSString().equalsIgnoreCase(pathEntry.getPath().toOSString()) )
						{
							index = i;
							break;
						} 
					}
				}			
				// We will add a new entry if the entry does not exist.
				if ( index < 0 )
				{
					newClasspaths.add(pathEntry);
				}
			}
			addClasspathLibraryEntries(javaProject, newClasspaths.toArray(new IClasspathEntry[newClasspaths.size()]));
		}
	}
	
	public static void addClasspathLibraryEntries( IJavaProject javaProject, IClasspathEntry[] classpathEntries )throws CoreException{
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry [] newEntries = new IClasspathEntry[oldEntries.length + classpathEntries.length];
		
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		for(int i=0; i<classpathEntries.length; i++){
			newEntries[oldEntries.length + i] = classpathEntries[i];
		}
		javaProject.setRawClasspath(newEntries, null);
	}
	
	public static IFile[] removeFilesFromEglar(IResource target, EglarFile eglarFile, String[] entries, boolean useEntryFolder){
		IFile[] files = new IFile[entries.length];
		for(int i=0; i<entries.length; i++){
			String entryName = entries[i];
			if(useEntryFolder){
				files[i] = EGLProjectUtility.removeFileFromEglar(target, eglarFile, entryName, entryName);
			}
			else{
				String fileName = entryName;
				int index = fileName.lastIndexOf("/");
				if(index > -1){
					fileName = fileName.substring(index + 1);
				}				
				files[i] = EGLProjectUtility.removeFileFromEglar(target, eglarFile, entryName, fileName);
			}
		}
		return files;
	}
	
	public static IFile[] removeFilesFromEglar(IResource target, EglarFile eglarFile, String[] entries){
		return removeFilesFromEglar(target, eglarFile, entries, true);
	}
	
	/**
	 * copy the jar from eglar into the destination location under project
	 * @param destination
	 * @param eglarFile
	 * @param jarEntry
	 */
	public static IFile removeFileFromEglar(IResource target, EglarFile eglarFile, String entryName, String createFileName){
		try {
			if(!target.exists() || !(target instanceof IContainer)){
				return null;
			}
			
			ZipEntry zipEntry = eglarFile.getEntry(entryName);
			IFile toDelResource = ((IContainer)target).getFile(new Path(createFileName));
			InputStream is = eglarFile.getInputStream(zipEntry);
			if(toDelResource.exists()){
				toDelResource.delete(false, null);
				
				List<String> parents = new ArrayList<String>();
				String fileName = createFileName;
				int index = fileName.lastIndexOf("/");
				while(index > 0){
					fileName = fileName.substring(0, index);
					parents.add(fileName);
					index = fileName.lastIndexOf("/");
				}
				
				for(String parent: parents){
					IFolder parentFolder = ((IContainer)target).getFolder(new Path(parent));
					if(parentFolder.exists()){
						if(parentFolder.members().length == 0){
							parentFolder.delete(false, null);	//the folder is introduced by adding eglar library, delete it
						}
					}
					else{
						return null;
					}
				}
			}
			else{
				return null;
			}
			return toDelResource;		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void removeClasspathLibraryEntriesIfNecessary( IProject project, IPath[] jarPaths ) throws CoreException {
		IJavaProject javaProject; //The Java "view" of the project.
		if(project.hasNature(JavaCore.NATURE_ID)){
			javaProject = JavaCore.create(project);
			int jarIndex = -1;
			IClasspathEntry curEntry;
			List<IPath> delJarPaths = new ArrayList<IPath>(); 
			
			for(IPath jarPath: jarPaths){
//				String jarFile = jarPath.lastSegment();
				IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		
				// Look for the jar file in the current build path
				for (int i = 0; i < oldEntries.length; i++)
				{
					curEntry = oldEntries[i];
					if (curEntry.getPath() != null ) 
					{
						if (curEntry.getPath().equals(jarPath))
						{
							jarIndex = i;
							break;
						} 	
					}
				}	
				
				// We will add a new entry into delJarPaths if the entry does exist.
				if ( jarIndex > 0 )
				{
					delJarPaths.add(jarPath);
				}
				
			}
			removeClasspathLibraryEntries(javaProject, delJarPaths.toArray(new IPath[delJarPaths.size()]));
		}
	}
	
	public  static void removeClasspathLibraryEntries( IJavaProject javaProject, IPath[] jarPaths ) throws CoreException {
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		if(oldEntries.length < jarPaths.length)
			return;		//there are more paths to be deleted than the existing paths, should not reach here
		IClasspathEntry [] newEntries = new IClasspathEntry[oldEntries.length - jarPaths.length];
		int k = 0;
		
		for (int i = 0; i < oldEntries.length; i++) {
			boolean isEntryKept = true;
			for(int j = 0; j < jarPaths.length; j++){
				if(jarPaths[j].toString().equalsIgnoreCase(oldEntries[i].getPath().toString())){
					isEntryKept = false;
					break;
				}
			}
			if(isEntryKept){
				newEntries[k++] = oldEntries[i];
			}
		}		
		javaProject.setRawClasspath(newEntries, null);
	}
	
	public static IPath getDefaultRUIWebContentPath(IEGLProject eproj) {
		String outputLocationName= "WebContent"; //$NON-NLS-1$
		return eproj.getProject().getFullPath().append(outputLocationName);
	}
	
	public static IPath getDefaultRUICSSFolderPath(IEGLProject eproj) {
		String outputLocationName= "WebContent/css"; //$NON-NLS-1$
		return eproj.getProject().getFullPath().append(outputLocationName);
	}
	
	public static IPath getDefaultRUIIconsFolderPath(IEGLProject eproj) {
		String outputLocationName= "WebContent/icons"; //$NON-NLS-1$
		return eproj.getProject().getFullPath().append(outputLocationName);
	}
	
	/**
	 * Returns the path to the RUI project properties folder. This is not a default, this
	 * is specifically where users HAVE to place their properties files to be recognized
	 * at deployment time 
	 * 
	 * @param eproj
	 * @return
	 */
	public static IPath getPropertiesFolderPath(IEGLProject eproj) {
		String outputLocationName= "WebContent/properties"; //$NON-NLS-1$
		return eproj.getProject().getFullPath().append(outputLocationName);
	}
	
	public static void createRUIWebContentAndSubFolders(IProject project) throws CoreException {
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IEGLProject eproj = EGLCore.create(project);

		IPath eglRUIWebContentLocation = EGLProjectUtility.getDefaultRUIWebContentPath(eproj);
		if (!fWorkspaceRoot.exists(eglRUIWebContentLocation)) {
			IFolder folder = fWorkspaceRoot.getFolder(eglRUIWebContentLocation);
			CoreUtility.createFolder(folder, true, true, null);
		}

		IPath cssFolder = EGLProjectUtility.getDefaultRUICSSFolderPath(eproj);
		if (!fWorkspaceRoot.exists(cssFolder)) {
			IFolder folder = fWorkspaceRoot.getFolder(cssFolder);
			CoreUtility.createFolder(folder, true, true, null);
			/**
			 * create a default css file
			 */
			createDefaultCSSFile(folder);
		}

		IPath iconsFolder = EGLProjectUtility.getDefaultRUIIconsFolderPath(eproj);
		/**
		 * create and set the icons content path
		 */
		if (!fWorkspaceRoot.exists(iconsFolder)) {
			IFolder folder = fWorkspaceRoot.getFolder(iconsFolder);
			CoreUtility.createFolder(folder, true, true, null);
		}

		IPath propertiesFolder = EGLProjectUtility.getPropertiesFolderPath(eproj);
		/**
		 * create and set the properties content path
		 */
		if (!fWorkspaceRoot.exists(propertiesFolder)) {
			IFolder folder = fWorkspaceRoot.getFolder(propertiesFolder);
			CoreUtility.createFolder(folder, true, true, null);
		}
	}

	private static String getTemplateString() {
		Template template = null;
		TemplateStore fTemplateStore = CSSUIPlugin.getDefault().getTemplateStore();
		String templateName = CSSUIPlugin.getDefault().getPreferenceStore().getString(CSSUIPreferenceNames.NEW_FILE_TEMPLATE_NAME);
		Template[] templates = fTemplateStore.getTemplates(TemplateContextTypeIdsCSS.NEW);
		for (int i = 0; i < templates.length && template == null; i++) {
			Template template2 = templates[i];
			if (template2.getName().equals(templateName)) {
				template = template2;
			}
		}
		String templateString = ""; //$NON-NLS-1$
		if (template != null) {
			TemplateContextType contextType = CSSUIPlugin.getDefault().getTemplateContextRegistry().getContextType(TemplateContextTypeIdsCSS.NEW);
			IDocument document = new Document();
			TemplateContext context = new DocumentTemplateContext(contextType, document, 0, 0);
			try {
				TemplateBuffer buffer = context.evaluate(template);
				templateString = buffer.getString();
			}
			catch (Exception e) {
				//	Logger.log(Logger.WARNING_DEBUG, "Could not create template for new css", e); //$NON-NLS-1$
			}
		}

		return templateString;
	}
	
	private static String applyLineDelimiter(IFile file, String text) {
		String lineDelimiter = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator"), new IScopeContext[] {new ProjectScope(file.getProject()), new InstanceScope() });//$NON-NLS-1$
		String convertedText = StringUtils.replace(text, "\r\n", "\n");  //$NON-NLS-1$//$NON-NLS-2$
		convertedText = StringUtils.replace(convertedText, "\r", "\n");  //$NON-NLS-1$//$NON-NLS-2$
		convertedText = StringUtils.replace(convertedText, "\n", lineDelimiter); //$NON-NLS-1$
		return convertedText;
	}
	
	public static void createDefaultCSSFile(IFolder folder) {
		IProject project = folder.getProject();
		IFile file = folder.getFile(new Path(project.getName() + ".css")); //$NON-NLS-1$

		// if there was problem with creating file, it will be null, so make
		// sure to check
		if (file != null && !file.exists()) {
			// put template contents into file
			String templateString = getTemplateString();
			if (templateString != null) {
				templateString = applyLineDelimiter(file, templateString);
				// determine the encoding for the new file
				Preferences preference = CSSCorePlugin.getDefault().getPluginPreferences();
				String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

				try {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					OutputStreamWriter outputStreamWriter = null;
					if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
					// just use default encoding
						outputStreamWriter = new OutputStreamWriter(outputStream);
					} else {
						outputStreamWriter = new OutputStreamWriter(outputStream, charSet);
					}
					outputStreamWriter.write(templateString);
					outputStreamWriter.flush();
					outputStreamWriter.close();
					ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

					file.create(inputStream, true, null);
					inputStream.close();
				} catch (Exception e) {
					//Logger.log(Logger.WARNING_DEBUG, "Could not create contents for new CSS file", e); //$NON-NLS-1$
				}
			}
		}
	}
	
	public static void modifyClasspathLibraryEntry(IProject project, IClasspathEntry[] modifiedEntries) throws CoreException{
		IJavaProject javaProject; //The Java "view" of the project.
		if(project.hasNature(JavaCore.NATURE_ID)){
			javaProject = JavaCore.create(project);
			IClasspathEntry[] entries = javaProject.getRawClasspath();
			IClasspathEntry[] newEntries = entries;
			
			for(int j = 0; j < modifiedEntries.length; j++){
				for (int i = 0; i < entries.length; i++){
					if(modifiedEntries[j].getPath().toOSString().equalsIgnoreCase(entries[i].getPath().toOSString())){
						newEntries[i] = modifiedEntries[j];
						break;
					}
				}
			}
			javaProject.setRawClasspath(newEntries, null);
		}
	}
	
}

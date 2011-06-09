/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.EGLNature;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.PPListElement;
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

public final class EGLProjectUtility {
	public static final String JAVA_DEFAULT_BIN_FOLDER = "bin"; //$NON-NLS-1$
	public  static final String JAVA_DEFAULT_SOURCE_FOLDER = "src"; //$NON-NLS-1$

	public static void addEGLNature(IProject project, IProgressMonitor monitor)
			throws CoreException {
		boolean hasEGLCoreNatureID = project.hasNature(EGLCore.NATURE_ID);
		boolean hasEGLNatureID = project.hasNature(EGLNature.EGL_NATURE_ID);

		if (!hasEGLCoreNatureID || !hasEGLNatureID) {
			if (!hasEGLNatureID) {
				addNatureToProject(project, EGLNature.EGL_NATURE_ID, monitor);
			}
			if (!hasEGLCoreNatureID) {
				addNatureToProject(project, EGLCore.NATURE_ID, monitor);
			}
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
	
//	public static void addRUINature(IProject project, IProgressMonitor monitor) throws CoreException {
//TODO EDT Add RUINature
//		boolean hasRUINatureID = project.hasNature(RUINature.RUI_NATURE_ID);
//		
//		if (!hasRUINatureID) {	
//			addNatureToProject(project, RUINature.RUI_NATURE_ID, monitor);
//		} else {
//			monitor.worked(1);
//		}
//	}

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

	public static void createEGLConfiguration(ProjectConfiguration configuration,
			IProgressMonitor monitor) throws CoreException {

		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace()
				.getRoot();
		IProject project= fWorkspaceRoot.getProject(configuration.getProjectName());
		IEGLProject fCurrEProject = EGLCore.create(project);

		List eglPathEntries = configuration.getRequiredProjects();		
		if(eglPathEntries.size()==0 && !configuration.getProjectName().equals("")) { //$NON-NLS-1$
			eglPathEntries = initializeEGLPathEntries(project);
		}

		IPath eglOutputLocation = EGLProjectUtility
				.getDefaultEGLOutputPath(fCurrEProject);
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
}

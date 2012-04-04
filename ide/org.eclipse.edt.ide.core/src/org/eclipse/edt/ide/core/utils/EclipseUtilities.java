/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.externaltools.internal.model.BuilderCoreUtils;
import org.eclipse.core.externaltools.internal.model.ExternalToolBuilder;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.ide.core.AbstractGenerator;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.osgi.util.NLS;

/**
 * Utility methods for working within Eclipse.
 */
@SuppressWarnings("restriction")
public class EclipseUtilities {
	
	private EclipseUtilities() {
		// No instances.
	}
	
	/**
	 * @return true if the outputFolder represents a folder within the workspace
	 */
	public static boolean shouldWriteFileInEclipse( String outputFolder ) {
		return !new Path(outputFolder).isAbsolute();
	}
	
	/**
	 * Returns the container in which the file should be written.
	 * 
	 * @param outputFolder         The folder in which to generate, in the internal folder format (see {@link #convertGenerationDirectoryToPath(String)})
	 * @param eglFile              The source file being generated.
	 * @param relativeFilePath     The path of the file to be written inside outputFolder. e.g. my/pkg/Foo.java
	 * @return the container in which the file should be written.
	 * @throws CoreException
	 */
	public static IContainer getOutputContainer(String outputFolder, IFile eglFile, String relativeFilePath) throws CoreException {
		IContainer container = null;
		IPath outputFolderPath = new Path(convertFromInternalPath(outputFolder));
		int lastSlash = relativeFilePath.lastIndexOf('/');
		if (outputFolderPath.isAbsolute()) {
			IPath path = outputFolderPath;
			if (lastSlash != -1) {
				path = path.append(relativeFilePath.substring(0, lastSlash));
			}
			
			if (path.segmentCount() > 1) {
				container = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
				// Verify the containing project exists
				if (!container.getProject().isAccessible()) {
					throw new CoreException(new Status(IStatus.ERROR, EDTCoreIDEPlugin.PLUGIN_ID, NLS.bind(CoreIDEPluginStrings.ProjectNotAccessible, container.getProject().getName())));
				}
			}
			else if (path.segmentCount() == 1){
				container = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
				// Projects must exist, we can't just create them like folders
				if (!container.isAccessible()) {
					throw new CoreException(new Status(IStatus.ERROR, EDTCoreIDEPlugin.PLUGIN_ID, NLS.bind(CoreIDEPluginStrings.ProjectNotAccessible, container.getName())));
				}
			}
			else {
				throw new CoreException(new Status(IStatus.ERROR, EDTCoreIDEPlugin.PLUGIN_ID, NLS.bind(CoreIDEPluginStrings.CouldNotGetOutputFolder, outputFolderPath)));
			}
		}
		else {
			// Relative to the source project
			if (lastSlash == -1) {
				container = outputFolderPath.segmentCount() == 0 ? eglFile.getProject() : eglFile.getProject().getFolder(outputFolderPath);
			}
			else {
				container = eglFile.getProject().getFolder(outputFolderPath.append(relativeFilePath.substring(0, lastSlash)));
			}
		}
		
		return container;
	}
	
	/**
	 * Returns the path that should be used for the output file.
	 * 
	 * @param outputFolder         The folder in which to generate, in the internal folder format (see {@link #convertGenerationDirectoryToPath(String)})
	 * @param eglFile              The source file being generated.
	 * @param relativeFilePath     The path of the file to be written inside outputFolder. e.g. my/pkg/Foo.java
	 * @return the path that should be used for the output file.
	 */
	public static IPath getOutputFilePath(String outputFolder, IFile eglFile, String relativeFilePath) {
		int lastSlash = relativeFilePath.lastIndexOf('/');
		String fileName = lastSlash == -1 ? relativeFilePath : relativeFilePath.substring(lastSlash + 1);
		return new Path(fileName);
	}
	
	/**
	 * Writes a file using Eclipse API, so that the Eclipse filesystem is kept in sync.
	 * 
	 * @param part                 The IR being generated.
	 * @param outputFolder         The folder in which to generate, in the internal folder format (see {@link #convertGenerationDirectoryToPath(String)})
	 * @param eglFile              The source file being generated.
	 * @param contentsToWrite      The content of the file to write.
	 * @param relativeFilePath     The path of the file to be written inside outputFolder. e.g. my/pkg/Foo.java
	 * @return the file that was written
	 * @throws CoreException
	 */
	public static IFile writeFileInEclipse(Part part, String outputFolder, IFile eglFile, String contentsToWrite, String relativeFilePath) throws CoreException {
		IPath filePath = getOutputFilePath(outputFolder, eglFile, relativeFilePath);
		IContainer container = getOutputContainer(outputFolder, eglFile, relativeFilePath);
		writeFileInEclipse(container, filePath, EclipseUtilities.getInputStream(eglFile, contentsToWrite), true);
		return container.getFile(filePath);
	}
	
	/**
	 * Writes a file using Eclipse API, so that the Eclipse filesystem is kept in sync. The input stream will be closed before the method returns.
	 * 
	 * @param outputContainer           The location in which to write the file.
	 * @param fileName                  The name of the file to write.
	 * @param contents                  The contents to write to the file.
	 * @param createFoldersIfNecessary  Flag indicating if we should create parent folders if they don't already exist.
	 * @throws CoreException 
	 */
	public static void writeFileInEclipse(IContainer outputContainer, IPath fileName, InputStream dataStream, boolean createFoldersIfNecessary) throws CoreException {
		try {
			if (createFoldersIfNecessary && outputContainer instanceof IFolder) {
				createFolder((IFolder)outputContainer);
			}
			
			IFile outputFile = outputContainer.getFile(fileName);
			if (outputFile.exists()) {
				outputFile.setContents(dataStream, true, false, null);
			}
			else {
				outputFile.create(dataStream, IResource.FORCE, null);
			}
		}
		finally {
			if (dataStream != null) {
				try {
					dataStream.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * Creates the folder and its parents if they don't already exist.
	 * 
	 * @param folder  The folder to create.
	 * @throws CoreException
	 */
	public static void createFolder(IFolder folder) throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder)parent);
			}
			folder.create(true, true, null);
			folder.setDerived(true, null);
		}
	}
	
	/**
	 * Creates an InputStream for the data, attempting to use the charset of the source file, if available.
	 * 
	 * @param sourceFile  The source file from which to get the charset (may be null).
	 * @param data        The data the input stream will write.
	 * @return an InputStream for the data, attempting to use the charset of the source file, if available.
	 */
	public static InputStream getInputStream(IFile sourceFile, String data) {
		if (sourceFile != null) {
			try {
				String encoding = sourceFile.getCharset();
				if (encoding != null) {
					try {
						return new ByteArrayInputStream(data.getBytes(encoding));
					}
					catch (UnsupportedEncodingException e) {
					}
				}
			}
			catch (CoreException e) {
			}
		}
		return new ByteArrayInputStream(data.getBytes());
	}
	
	/**
	 * Adds the outputFolder as a Java source folder if the project is a Java project.
	 * 
	 * @param project       The project containing the folder (used when outputFolder is a relative path)
	 * @param outputFolder  The path of the folder. It may be project-relative, or workspace-relative. If workspace-relative
	 *                      it should start with 'F/' for a folder or 'P/' for a project.
	 * @param forceClasspathRefresh A classpath needs to be refreshed if an entry already exists for the output folder, but the folder has yet to be
	 * 								created.  This can occur when a project is exported without a generation directory.
	 * @throws CoreException
	 */
	public static void addToJavaBuildPathIfNecessary(IProject project, String outputFolder, boolean forceClasspathRefresh) throws CoreException {
		if (project.hasNature(JavaCore.NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject.exists()) {
				IClasspathEntry[] entries = javaProject.getRawClasspath();
				IPath outputFolderPath = new Path(convertFromInternalPath(outputFolder));
				boolean needToAdd = true;
				
				IPath fullPath = outputFolderPath.isAbsolute()
						? outputFolderPath
						: outputFolderPath.segmentCount() == 0
								? project.getFullPath()
								: project.getFolder(outputFolderPath).getFullPath();
				
				for (int i = 0; i < entries.length; i++) {
					if (entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath nextPath = entries[i].getPath();
						// JDT throws an error if you have a source folder within a source folder. We could add exclusions to support this, but
						// for now we just won't add the folder.
						if (nextPath.isPrefixOf(fullPath) || fullPath.isPrefixOf(nextPath)) {
							needToAdd = false;
							break;
						}
					}
				}
				
				if (needToAdd){
					IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
					System.arraycopy(entries, 0, newEntries, 0, entries.length);
					newEntries[newEntries.length - 1] = JavaCore.newSourceEntry(fullPath);
					javaProject.setRawClasspath(newEntries, null);
				}
				
				if (!needToAdd && forceClasspathRefresh){
					javaProject.setRawClasspath(javaProject.readRawClasspath(), javaProject.readOutputLocation(), null);
				}
			}
		}
	}
	
	/**
	 * Returns a path representing the generation directory, which may be worspace- or project-relative. An internal
	 * convention is used for paths and this routine will normalize it to be of the format "/myproject/my/folder" for
	 * workspace-relative paths and "my/folder" for project-relative paths. A blank string is returned for "this project".
	 * 
	 * @param path  The internal representation of the generation directory path
	 * @return the normalized generation directory path
	 */
	public static String convertFromInternalPath(String path) {
		// We prefix workspace-relative paths with W/, project-relative paths with P/.
		if (path.startsWith("W/")) { //$NON-NLS-1$
			return path.substring(1);
		}
		else if (path.startsWith("P/")) { //$NON-NLS-1$
			return path.substring(2);
		}
		return path;
	}
	
	public static String convertToInternalPath(String path) {
		if (path.startsWith("/")) { //$NON-NLS-1$
			return "W" + path; //$NON-NLS-1$
		}
		return "P/" + path; //$NON-NLS-1$
	}
	
	/**
	 * Adds the runtime containers to the project if necessary. This does nothing if the project is
	 * not a Java project.
	 * 
	 * @param project   The Java project.
	 * @param generator  The generator provider.
	 * @param ctx  The generation context.
	 */
	public static void addRuntimesToProject(IProject project, IGenerator generator, EglContext ctx) {
		EDTRuntimeContainer[] baseRuntimes = generator instanceof AbstractGenerator ? ((AbstractGenerator)generator).resolveBaseRuntimeContainers() : null;
		
		EDTRuntimeContainer[] containersToAdd;
		Set<String> requiredContainers = ctx.getRequiredRuntimeContainers();
		if (requiredContainers.size() == 0) {
			if (baseRuntimes == null || baseRuntimes.length == 0) {
				return;
			}
			containersToAdd = baseRuntimes;
		}
		else {
			Set<EDTRuntimeContainer> containers = new HashSet<EDTRuntimeContainer>(10);
			if (baseRuntimes != null && baseRuntimes.length > 0) {
				containers.addAll(Arrays.asList(baseRuntimes));
			}
			for (EDTRuntimeContainer container : generator.getRuntimeContainers()) {
				if (requiredContainers.contains(container.getId())) {
					containers.add(container);
				}
			}
			containersToAdd = containers.toArray(new EDTRuntimeContainer[containers.size()]);
		}
		
		if (containersToAdd == null || containersToAdd.length == 0) {
			return;
		}
		
		try {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(project);
				IClasspathEntry[] classpath = javaProject.getRawClasspath();
				
				List<IClasspathEntry> additions = new ArrayList<IClasspathEntry>();
				
				for (int i = 0; i < containersToAdd.length; i++) {
					IPath path = containersToAdd[i].getPath();
					boolean found = false;
					for (int j = 0; j < classpath.length; j++) {
						if (classpath[j].getEntryKind()== IClasspathEntry.CPE_CONTAINER
								&& classpath[j].getPath().equals(path)) {
							found = true;
							break;
						}
					}
					if (!found) {
						additions.add(JavaCore.newContainerEntry(path));
					}
				}
				
				if (additions.size() > 0) {
					IClasspathEntry[] newEntries = new IClasspathEntry[classpath.length + additions.size()];
					System.arraycopy(classpath, 0, newEntries, 0, classpath.length);
					for (int i = 0; i < additions.size(); i++) {
						newEntries[classpath.length + i] = additions.get(i);
					}
					javaProject.setRawClasspath(newEntries, null);
				}
			}
		}
		catch (CoreException e) {
			EDTCoreIDEPlugin.log(e);
		}
	}
	
	/**
	 * Adds the SMAP builder to the project if necessary. This does nothing if the project is not a Java project.
	 * @param project  The Java project.
	 */
	public static void addSMAPBuilder(IProject project) {
		try {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				String builderID = "org.eclipse.edt.debug.core.smapBuilder";
				IProjectDescription description = project.getDescription();
				
				ICommand smapCommand = null;
				ICommand[] commands = description.getBuildSpec();
				for (ICommand command : commands) {
					if (command.getBuilderName().equals(builderID)) {
						smapCommand = command;
						break;
					}
					// Disabled builders become "external tool builders"
					else if (ExternalToolBuilder.ID.equals(command.getBuilderName())) {
						Object attr = command.getArguments().get(BuilderCoreUtils.LAUNCH_CONFIG_HANDLE);
						if (attr instanceof String && ((String)attr).contains(builderID)) {
							smapCommand = command;
							break;
						}
					}
				}
				
				if (smapCommand == null) {
					smapCommand = description.newCommand();
					smapCommand.setBuilderName(builderID);
					
					ICommand[] newCommands = new ICommand[commands.length + 1];
					System.arraycopy(commands, 0, newCommands, 0, commands.length);
					newCommands[commands.length] = smapCommand;
					
					description.setBuildSpec(newCommands);
					project.setDescription(description, null);
				}
			}
		}
		catch (CoreException e) {
			EDTCoreIDEPlugin.log(e);
		}
	}
	

	/**
	 * Returns true if this project is a J2EE web project. Returns false if this
	 * is not a web project or is a static web project.
	 * 
	 * @param project
	 *            Project
	 * @return boolean
	 */
	public static boolean isWebProject(IProject project) {
		return JavaEEProjectUtilities.isDynamicWebProject(project);
	}

	/**
	 * Get the source folder name. For new Java projects, check the source
	 * folder name in the Java Build Path preferences. For all types of Java
	 * projects, use the first source folder from the classpath.
	 * 
	 * @param myProject
	 *            The project where the folder for Java source resides.
	 * 
	 * @return String The Java source folder name.
	 */
	public static String getJavaSourceFolderName(IProject project) {
		String folderName = null;

		try {
			if ( project.hasNature( JavaCore.NATURE_ID ) ) {
				// Use the first folder from the project's classpath. 
				IJavaProject javaProject = JavaCore.create( project );
				IClasspathEntry[] entries = javaProject.getRawClasspath();
				for ( int i = 0; i < entries.length; i++ )
				{
					IClasspathEntry entry = entries[ i ];
					if ( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE )
					{
						// Get the folder from the entry's path.  The project name
						// needs to be removed from the path before this will work.
						IPath path = entry.getPath().removeFirstSegments( 1 );
						return path.toString();
					}
				}
			}
		} catch (Exception e) {
		}
		return folderName;
	}
	
	public static List<String> getDependentDescriptors( IProject project ) throws Exception {
		List eglProjectPath = org.eclipse.edt.ide.core.internal.utils.Util.getEGLProjectPath(project);
		
		final List<String> egldds = new ArrayList<String>();
		for (Iterator<IEGLProject> iter1 = eglProjectPath.iterator(); iter1.hasNext();) {
			IEGLProject eglProject = iter1.next();
			IProject dependentPro = eglProject.getProject();
			final IPath outputPath = eglProject.getOutputLocation();
			dependentPro.accept( new IResourceVisitor() {
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (outputPath.isPrefixOf(resource.getFullPath())) {
						if ( resource instanceof IFile && "egldd".equals( resource.getFileExtension() ) ) {
							try {
								egldds.add( resource.getFullPath().toString() );
							} catch (Exception e) {
							}
							return false;
						}
					}
					return resource.getFullPath().isPrefixOf(outputPath);
				}
			});
		}
		
		return egldds;
	}
}

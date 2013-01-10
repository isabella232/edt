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
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.jarpackager.IJarDescriptionWriter;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptorProxy;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.edt.ide.core.internal.model.Assert;

public class EglarPackageData {
	/*
	 * What to export - internal locations
	 * The list fExported* is null if fExport* is false)
	 */
	private boolean fExportEGLIRFiles;	// export generated ir files and resources
	private boolean fExportOutputFolders;	// export all output folder of enclosing projects
	private boolean fExportEGLFiles;		// export egl files and resources
	private boolean fExportTLFSrcFiles;	//	export top level function source files
	
	/*
	 * Source folder hierarchy is created in the Eglar if true
	 */
	private boolean fUseSourceFolderHierarchy;

	/*
	 * Projects of which files are exported will be built if true
	 * and auto-build is off.
	 */
	private boolean fBuildIfNeeded;

	/*
	 * Leaf elements (no containers) to export
	 */
	private Object[]	fElements; // inside workspace

	private IPath		fEglarLocation; // external location
	private boolean	fOverwrite;
	private boolean	fCompress;

	private boolean	fSaveDescription;
	private IPath		fDescriptionLocation; // internal location

	
	private IPath binaryProjectLocation;

 	private String fComment; // the EGLAR comment

	/*
	 * Error handling
	 */
	private boolean fExportErrors;
	private boolean fExportWarnings;

	// The provider for the manifest file
//	private IEglarManifestProvider fManifestProvider;

	// Add directory entries to the jar
	private boolean fIncludeDirectoryEntries;

	// Projects for which to store refactoring information
	private IProject[] fRefactoringProjects= {};

	// Should the package be refactoring aware?
	private boolean fRefactoringAware= false;

	// Should the exporter only export refactorings causing structural changes?
	private boolean fRefactoringStructural= false;

	// Should the exporter include deprecation resolving information?
	private boolean fDeprecationAware= true;

	// The refactoring descriptors to export
	private RefactoringDescriptorProxy[] fRefactoringDescriptors= {};

	// Builder used by the EglarFileExportOperation to build the jar file
	private IEglarBinaryProjectBuilder fEglarBuilder;
	
	private IEglarBinaryProjectBuilder fBinaryProjectBuilder;

	// The launch configuration used by the fat jar builder to determine dependencies
 	private String  fLaunchConfigurationName;
 	
 	
 	private boolean isEglarGeneratable = true;
 	
	private String vendorName;
 	
 	private String versionName;
	/**
	 * Creates a new Jar Package Data structure
	 */
	public EglarPackageData() {
		setExportEGLSrcFiles(true);
		setExportEGLIRFiles(true);
		setExportOutputFolders(false);
		setUseSourceFolderHierarchy(false);
		setCompress(true);
		setSaveDescription(false);
		setEglarLocation(Path.EMPTY);
		setBinaryProjectLocation(Path.EMPTY);
		setDescriptionLocation(Path.EMPTY);
		setExportErrors(true);
		setExportWarnings(true);
		setBuildIfNeeded(true);
		setIncludeDirectoryEntries(false);
		setEglarGeneratable(true);
		setVendorName("");
		setVersionName("1.0");
		setExportTLFSrcFiles(false);
	}

	public boolean isEglarGeneratable() {
		return isEglarGeneratable;
	}


	public void setEglarGeneratable(boolean isEglarGeneratable) {
		this.isEglarGeneratable = isEglarGeneratable;
	}


	public String getVendorName() {
		return vendorName;
	}


	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}


	public String getVersionName() {
		return versionName;
	}


	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	// ----------- Accessors -----------

	public IPath getBinaryProjectLocation() {
		return binaryProjectLocation;
	}


	public void setBinaryProjectLocation(IPath binaryProjectLocation) {
		this.binaryProjectLocation = binaryProjectLocation;
	}

	public boolean isBinaryProjectExport() {
		return binaryProjectLocation != Path.EMPTY;
	}

	/**
	 * Tells whether the Eglar is compressed or not.
	 *
	 * @return	<code>true</code> if the Eglar is compressed
	 */
	public boolean isCompressed() {
		return fCompress;
	}

	/**
	 * Set whether the Eglar is compressed or not.
	 *
	 * @param state a boolean indicating the new state
	 */
	public void setCompress(boolean state) {
		fCompress= state;
	}

	/**
	 * Tells whether files can be overwritten without warning.
	 *
	 * @return	<code>true</code> if files can be overwritten without warning
	 */
	public boolean allowOverwrite() {
		return fOverwrite;
	}

	/**
	 * Sets whether files can be overwritten without warning.
	 *
	 * @param state a boolean indicating the new state
	 */
	public void setOverwrite(boolean state) {
		fOverwrite= state;
	}

	/**
	 * Tells whether class files and resources are exported.
	 *
	 * @return	<code>true</code> if class files and resources are exported
	 */
	public boolean areEGLIRFilesExported() {
		return fExportEGLIRFiles;
	}
	
	/**
	 * Tells whether EGL source files and resources are exported.
	 *
	 * @return	<code>true</code> if EGL source files and resources are exported
	 */
	public boolean areEGLSrcFilesExported() {
		return fExportEGLFiles;
	}

	/**
	 * Sets option to export class files and resources.
	 *
	 * @param state a boolean indicating the new state
	 */
	public void setExportEGLIRFiles(boolean state) {
		fExportEGLIRFiles= state;
	}
	
	/**
	 * Sets option to export EGL source files and resources.
	 *
	 * @param state a boolean indicating the new state
	 */
	public void setExportEGLSrcFiles(boolean state) {
		fExportEGLFiles = state;
	}

	/**
	 * Tells whether all output folders for the
	 * enclosing projects of the exported elements.
	 *
	 * @return	<code>true</code> if output folder are exported
	 * @since 3.0
	 */
	public boolean areOutputFoldersExported() {
		return fExportOutputFolders;
	}

	/**
	 * Sets option to export all output folders for the
	 * enclosing projects of the exported elements.
	 *
	 * @param state a boolean indicating the new state
	 * @since 3.0
	 */
	public void setExportOutputFolders(boolean state) {
		fExportOutputFolders= state;
	}

	/**
	 * Tells whether files created by the egl builder are exported.
	 *
	 * @return	<code>true</code> if output folder are exported
	 * @since 3.0
	 */
	public boolean areGeneratedFilesExported() {
		return fExportOutputFolders || fExportEGLIRFiles;
	}

	/**
	 * Tells whether the source folder hierarchy is used.
	 * <p>
	 * Using the source folder hierarchy only makes sense if
	 * egl files are but class files aren't exported.
	 * </p>
	 *
	 * @return	<code>true</code> if source folder hierarchy is used
	 */
	public boolean useSourceFolderHierarchy() {
		return fUseSourceFolderHierarchy;
	}

	/**
	 * Set the option to export the source folder hierarchy.
	 *
	 * @param state the new state
	 */
	public void setUseSourceFolderHierarchy(boolean state) {
		fUseSourceFolderHierarchy= state;
	}

	/**
	 * Gets the absolute location of the Eglar file.
	 * This path is normally external to the workspace.
	 *
	 * @return the absolute path representing the location of the Eglar file
	 *
	 */
	public IPath getAbsoluteEglarLocation() {
		if (!fEglarLocation.isAbsolute()) {
			IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
			if (fEglarLocation.segmentCount() >= 2 && !"..".equals(fEglarLocation.segment(0))) { //$NON-NLS-1$
				// reverse of AbstractJarDestinationWizardPage#handleDestinationBrowseButtonPressed()
				IFile file= root.getFile(fEglarLocation);
				IPath absolutePath= file.getLocation();
				if (absolutePath != null) {
					return absolutePath;
				}
			}
			// The path does not exist in the workspace (e.g. because there's no such project).
			// Fallback is to just append the path to the workspace root.
			return root.getLocation().append(fEglarLocation);
		}
		return fEglarLocation;
	}
	
	public IPath getBaseEglarLocation() {
		if(fEglarLocation != null) {
			String fileExt = fEglarLocation.getFileExtension();
			if("eglar".equalsIgnoreCase(fileExt)) {
				return fEglarLocation.removeLastSegments(1);
			} else {
				return fEglarLocation;
			}
		}
		return null;
	}
	
	/**
	 * Gets the absolute location of the binary projects.
	 * This path is normally external to the workspace.
	 *
	 * @return the absolute path representing the location of the Eglar file
	 *
	 */
	public IPath getAbsoluteBinaryProjectLocation() {
		if (!binaryProjectLocation.isAbsolute()) {
			IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
			if (binaryProjectLocation.segmentCount() >= 2 && !"..".equals(binaryProjectLocation.segment(0))) { //$NON-NLS-1$
				// reverse of AbstractJarDestinationWizardPage#handleDestinationBrowseButtonPressed()
				IFile file= root.getFile(binaryProjectLocation);
				IPath absolutePath= file.getLocation();
				if (absolutePath != null) {
					return absolutePath;
				}
			}
			// The path does not exist in the workspace (e.g. because there's no such project).
			// Fallback is to just append the path to the workspace root.
			return root.getLocation().append(binaryProjectLocation);
		}
		return binaryProjectLocation;
	}
	
	/**
	 * Get the absolute path representing the EGL source zip file location of the Eglar file
	 * @return
	 */
	public IPath getAbsoluteEglarSrcLocation() {
		//C:/temp/myfile.eglar
		IPath eglarLocation = getAbsoluteEglarLocation();
		//C:/temp/
		IPath eglarSrcFileName = eglarLocation.removeLastSegments(1);
		//myfile.eglar
		String eglarFileFullName = eglarLocation.lastSegment();
		//eglar
		String fileExtension = eglarLocation.getFileExtension();
		int index = eglarFileFullName.lastIndexOf(fileExtension);
		if(index != -1) {
			eglarSrcFileName = eglarSrcFileName.append(eglarFileFullName.substring(0, index) + "zip");
		}
		return eglarSrcFileName;
	}

	/**
	 * Gets the location of the Eglar file.
	 * This path is normally external to the workspace.
	 *
	 * @return the path representing the location of the Eglar file
	 */
	public IPath getEglarLocation() {
		return fEglarLocation;
	}

	/**
	 * Sets the Eglar file location.
	 *
	 * @param jarLocation a path denoting the location of the Eglar file
	 */
	public void setEglarLocation(IPath eglarLocation) {
		fEglarLocation= eglarLocation;
	}



	/**
	 * Tells whether a description of this Eglar package must be saved
	 * to a file by a Eglar description writer during the export operation.
	 * <p>
	 * The Eglar writer defines the format of the file.
	 * </p>
	 *
	 * @return	<code>true</code> if this Eglar package will be saved
	 * @see #getDescriptionLocation()
	 */
	public boolean isDescriptionSaved() {
		return fSaveDescription;
	}

	/**
	 * Set whether a description of this Eglar package must be saved
	 * to a file by a Eglar description writer during the export operation.
	 * <p>
	 * The format is defined by the client who implements the
	 * reader/writer pair.
	 * </p>
	 * @param state a boolean containing the new state
	 * @see #getDescriptionLocation()
	 * @see IJarDescriptionWriter
	 */
	public void setSaveDescription(boolean state) {
		fSaveDescription= state;
	}

	/**
	 * Returns the location of file containing the description of a Eglar.
	 * This location is inside the workspace.
	 *
	 * @return	the path of the description file location,
	 * 			or <code>null</code> if none is specified
	 */
	public IPath getDescriptionLocation() {
		return fDescriptionLocation;
	}

	/**
	 * Set the location of the Eglar description file.
	 *
	 * @param descriptionLocation the path of location
	 */
	public void setDescriptionLocation(IPath descriptionLocation) {
		fDescriptionLocation= descriptionLocation;
	}

	/**
	 * Gets the description file (as workspace resource).
	 *
	 * @return a file which points to the description
	 */
	public IFile getDescriptionFile() {
		IPath path= getDescriptionLocation();
		if (path.isValidPath(path.toString()) && path.segmentCount() >= 2)
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		else
			return null;
	}

	/**
	 * Returns the elements which will be exported.
	 * These elements are leaf objects e.g. <code>IFile</code>
	 * and not containers.
	 *
	 * @return an array of leaf objects
	 */
	public Object[] getElements() {
		if (fElements == null)
			setElements(new Object[0]);
		return fElements;
	}

	/**
	 * Set the elements which will be exported.
	 *
	 * These elements are leaf objects e.g. <code>IFile</code>.
	 * and not containers.
	 *
	 * @param elements	an array with leaf objects
	 */
	public void setElements(Object[] elements) {
		fElements= elements;
	}

	/**
	 * Returns the Eglar's comment.
	 *
	 * @return the comment string or <code>null</code>
	 * 			if the Eglar does not contain a comment
	 */
	public String getComment() {
		return fComment;
	}

	/**
	 * Sets the Eglar's comment.
	 *
	 * @param comment	a string or <code>null</code>
	 * 					if the Eglar does not contain a comment
	 */
	public void setComment(String comment) {
		fComment= comment;
	}


	/**
	 * Answers if compilation units with errors are exported.
	 *
	 * @return <code>true</code> if CUs with errors should be exported
	 */
	public boolean areErrorsExported() {
		return fExportErrors;
	}

	/**
	 * Sets if compilation units with errors are exported.
	 *
	 * @param exportErrors <code>true</code> if CUs with errors should be exported
	 */
	public void setExportErrors(boolean exportErrors) {
		fExportErrors= exportErrors;
	}

	/**
	 * Answers if compilation units with warnings are exported.
	 *
	 * @return <code>true</code> if CUs with warnings should be exported
	 */
	public boolean exportWarnings() {
		return fExportWarnings;
	}

	/**
	 * Sets if compilation units with warnings are exported.
	 *
	 * @param exportWarnings <code>true</code> if CUs with warnings should be exported
	 */
	public void setExportWarnings(boolean exportWarnings) {
		fExportWarnings= exportWarnings;
	}

	/**
	 * Answers if a build should be performed before exporting files.
	 * This flag is only considered if auto-build is off.
	 *
	 * @return a boolean telling if a build should be performed
	 */
	public boolean isBuildingIfNeeded() {
		return fBuildIfNeeded;
	}

	/**
	 * Sets if a build should be performed before exporting files.
	 * This flag is only considered if auto-build is off.
	 *
	 * @param buildIfNeeded a boolean telling if a build should be performed
	 */
	public void setBuildIfNeeded(boolean buildIfNeeded) {
		fBuildIfNeeded= buildIfNeeded;
	}
	// ----------- Utility methods -----------


	public IFile[] findIRfilesFor(IFile eglFile) throws CoreException {
		return null;
	}

	/**
	 * Creates and returns an eglar builder capable of handling
	 * files but not archives.
	 *
	 * @return a new instance of a plain eglar builder
	 *
	 * @since 3.4
	 */
	public IEglarBinaryProjectBuilder createPlainEglarBuilder() {
		return new PlainEglarBuilder();
	}

	/**
	 * Creates and returns an EglarExportRunnable.
	 *
	 * @param	parent	the parent for the dialog,
	 * 			or <code>null</code> if no questions should be asked and
	 * 			no checks for unsaved files should be made.
	 * @return an EglarExportRunnable
	 */
	public IEglarExportRunnable createEglarExportRunnable(Shell parent) {
		return new NewEglarFileExportOperation(this, parent);
	}
	
	/**
	 * Creates and returns an EglarExportRunnable.
	 *
	 * @param	parent	the parent for the dialog,
	 * 			or <code>null</code> if no questions should be asked and
	 * 			no checks for unsaved files should be made.
	 * @return an EglarExportRunnable
	 */
	public IEglarExportRunnable createBinaryProjectExportRunnable(Shell parent) {
		//return new BinaryProjectExportOperation(this, parent);
		return null;
	}
	
	

	/**
	 * Creates and returns an EglarExportRunnable for a list of Eglar package
	 * data objects.
	 *
	 * @param	EglarPackagesData	an array with Eglar package data objects
	 * @param	parent			the parent for the dialog,
	 * 							or <code>null</code> if no dialog should be presented
	 * @return the {@link IEglarExportRunnable}
	 */
	public IEglarExportRunnable createEglarExportRunnable(EglarPackageData[] EglarPackagesData, Shell parent) {
		return new EglarFileExportOperation(EglarPackagesData, parent);
	}

	/**
	 * Tells whether this Eglar package data can be used to generate
	 * a valid Eglar.
	 *
	 * @return <code>true</code> if the Eglar Package info is valid
	 */
	public boolean isValid() {
		return ( getElements() != null && getElements().length > 0
			&& getAbsoluteEglarLocation() != null
//			&& isManifestAccessible()
//			&& isMainIRValid(new BusyIndicatorRunnableContext())
			);
	}

	/**
	 * Tells whether directory entries are added to the jar.
	 *
	 * @return	<code>true</code> if directory entries are to be included
	 *
	 * @since 3.1
	 */
	public boolean areDirectoryEntriesIncluded() {
		return fIncludeDirectoryEntries;
	}

	/**
	 * Sets the option to include directory entries into the jar.
	 *
	 * @param includeDirectoryEntries <code>true</code> to include
	 *  directory entries <code>false</code> otherwise
	 *
	 *  @since 3.1
	 */
	public void setIncludeDirectoryEntries(boolean includeDirectoryEntries) {
		fIncludeDirectoryEntries = includeDirectoryEntries;
	}

	/**
	 * Returns the projects for which refactoring information should be stored.
	 * <p>
	 * This information is used for Eglar export.
	 * </p>
	 *
	 * @return the projects for which refactoring information should be stored,
	 *         or an empty array
	 *
	 * @since 3.2
	 */
	public IProject[] getRefactoringProjects() {
		return fRefactoringProjects;
	}

	/**
	 * Is the Eglar export wizard only exporting refactorings causing structural
	 * changes?
	 * <p>
	 * This information is used for Eglar export.
	 * </p>
	 *
	 * @return <code>true</code> if exporting structural changes only,
	 *         <code>false</code> otherwise
	 * @since 3.2
	 */
	public boolean isExportStructuralOnly() {
		return fRefactoringStructural;
	}

	/**
	 * Is the Eglar package refactoring aware?
	 * <p>
	 * This information is used both in Eglar export and import
	 * </p>
	 *
	 * @return <code>true</code> if it is refactoring aware,
	 *         <code>false</code> otherwise
	 *
	 * @since 3.2
	 */
	public boolean isRefactoringAware() {
		return fRefactoringAware;
	}

	/**
	 * Is the Eglar package deprecation aware?
	 * <p>
	 * This information is used in Eglar export.
	 * </p>
	 *
	 * @return <code>true</code> if it is deprecation aware,
	 *         <code>false</code> otherwise
	 *
	 * @since 3.2
	 */
	public boolean isDeprecationAware() {
		return fDeprecationAware;
	}

	/**
	 * Sets the projects for which refactoring information should be stored.
	 * <p>
	 * This information is used for Eglar export.
	 * </p>
	 *
	 * @param projects
	 *            the projects for which refactoring information should be
	 *            stored
	 *
	 * @since 3.2
	 */
	public void setRefactoringProjects(IProject[] projects) {
		Assert.isNotNull(projects);
		fRefactoringProjects= projects;
	}

	/**
	 * Determines whether the jar package is refactoring aware.
	 * <p>
	 * This information is used both in Eglar export and import.
	 * </p>
	 *
	 * @param aware
	 *            <code>true</code> if it is refactoring aware,
	 *            <code>false</code> otherwise
	 *
	 * @since 3.2
	 */
	public void setRefactoringAware(boolean aware) {
		fRefactoringAware= aware;
	}

	/**
	 * Determines whether the jar package is deprecation aware.
	 * <p>
	 * This information is used in Eglar export.
	 * </p>
	 *
	 * @param aware
	 *            <code>true</code> if it is deprecation aware,
	 *            <code>false</code> otherwise
	 *
	 * @since 3.2
	 */
	public void setDeprecationAware(boolean aware) {
		fDeprecationAware= aware;
	}

	/**
	 * Sets the refactoring descriptors to export.
	 * <p>
	 * This information is used in Eglar export.
	 * </p>
	 *
	 * @param descriptors
	 *            the refactoring descriptors
	 *
	 * @since 3.2
	 */
	public void setRefactoringDescriptors(RefactoringDescriptorProxy[] descriptors) {
		Assert.isNotNull(descriptors);
		fRefactoringDescriptors= descriptors;
	}

	/**
	 * Returns the refactoring descriptors to export.
	 * <p>
	 * This information is used in Eglar export.
	 * </p>
	 *
	 * @return the refactoring descriptors to export
	 *
	 * @since 3.2
	 */
	public RefactoringDescriptorProxy[] getRefactoringDescriptors() {
		return fRefactoringDescriptors;
	}

	/**
	 * Determines whether the jar packager exports only refactorings causing
	 * structural changes.
	 * <p>
	 * This information is used for Eglar export.
	 * </p>
	 *
	 * @param structural
	 *            <code>true</code> if it exports only refactorings causing
	 *            structural changes, <code>false</code> otherwise
	 *
	 * @since 3.2
	 */
	public void setExportStructuralOnly(boolean structural) {
		fRefactoringStructural= structural;
	}

	/**
	 * Returns the jar builder which can be used to build the jar
	 * described by this package data.
	 *
	 * @return the builder to use
	 * @since 3.4
	 */
	public IEglarBinaryProjectBuilder getEglarBuilder() {
		if (fEglarBuilder == null)
			fEglarBuilder= createPlainEglarBuilder();
		return fEglarBuilder;
	}
	
	/**
	 * 
	 * @return
	 */
	public IEglarBinaryProjectBuilder getBinaryProjectBuilder() {
		if(fBinaryProjectBuilder == null) {
			//fBinaryProjectBuilder = new BinaryProjectBuilder();
		}
		return fBinaryProjectBuilder;
	}

	/**
	 * Set the jar builder to use to build the jar.
	 *
	 * @param jarBuilder
	 *        the builder to use
	 * @since 3.4
	 */
	public void setEglarBuilder(IEglarBinaryProjectBuilder eglarBuilder) {
		fEglarBuilder= eglarBuilder;
	}

	/**
	 * Get the name of the launch configuration from
	 * which to retrieve classpath information.
	 *
	 * @return the name of the launch configuration
	 * @since 3.4
	 */
	public String getLaunchConfigurationName() {
		return fLaunchConfigurationName;
	}

	/**
	 * Set the name of the launch configuration form which
	 * to retrieve classpath information.
	 *
	 * @param name
	 *        name of the launch configuration
	 * @since 3.4
	 */
	public void setLaunchConfigurationName(String name) {
		fLaunchConfigurationName= name;
	}

	public boolean areTLFSrcFilesExported() {
		return fExportTLFSrcFiles;
	}

	public void setExportTLFSrcFiles(boolean fExportTLFSrcFiles) {
		this.fExportTLFSrcFiles = fExportTLFSrcFiles;
	}
}

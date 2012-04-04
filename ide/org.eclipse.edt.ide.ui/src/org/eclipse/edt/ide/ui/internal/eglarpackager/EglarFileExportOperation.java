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
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.ui.jarpackager.IJarBuilder;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.property.pages.BasicElementLabels;
import org.eclipse.edt.ide.ui.internal.property.pages.Messages;

public class EglarFileExportOperation extends WorkspaceModifyOperation implements IEglarExportRunnable {
	
	public static final int INTERNAL_ERROR= 10001;
	public static final String EGLAR_FILE_EXTENSION = ".eglar";
	private static class MessageMultiStatus extends MultiStatus {
		MessageMultiStatus(String pluginId, int code, String message, Throwable exception) {
			super(pluginId, code, message, exception);
		}

		protected void setMessage(String message) {
			super.setMessage(message);
		}
	}
	
	private IEglarBinaryProjectBuilder fEglarBuilder;
	private EglarPackageData fEglarPackage;
	private EglarPackageData[] fEglarPackages;
	private Shell fParentShell;
	private MessageMultiStatus fStatus;
	private boolean fFilesSaved;
	protected int totalWork;
	private int irFileCounts = 0;
	protected int[] irFileCountsPerElements = new int[0];
	protected boolean singleEGLARExport = true;
	
	private int work = 0;
	/**
	 * Creates an instance of this class.
	 *
	 * @param	eglarPackage	the eglar package specification
	 * @param	parent	the parent for the dialog,
	 * 			or <code>null</code> if no dialog should be presented
	 */
	public EglarFileExportOperation(EglarPackageData eglarPackage, Shell parent) {
		this(new EglarPackageData[] {eglarPackage}, parent);
	}

	/**
	 * Creates an instance of this class.
	 *
	 * @param	eglarPackages		an array with eglar package data objects
	 * @param	parent			the parent for the dialog,
	 * 			or <code>null</code> if no dialog should be presented
	 */
	public EglarFileExportOperation(EglarPackageData[] eglarPackages, Shell parent) {
		this(parent);
		fEglarPackages= eglarPackages;
	}

	private EglarFileExportOperation(Shell parent) {
		fParentShell= parent;
		fStatus= new MessageMultiStatus(EDTUIPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$
	}

	/**
	 * 
	 * @return
	 */
	public IEglarBinaryProjectBuilder getfEglarBuilder() {
		return fEglarBuilder;
	}
	/**
	 * 
	 * @param fEglarBuilder
	 */
	public void setfEglarBuilder(IEglarBinaryProjectBuilder fEglarBuilder) {
		this.fEglarBuilder = fEglarBuilder;
	}

	/**
	 * 
	 * @return
	 */
	public EglarPackageData getfEglarPackage() {
		return fEglarPackage;
	}

	/**
	 * 
	 * @param fEglarPackage
	 */
	public void setfEglarPackage(EglarPackageData fEglarPackage) {
		this.fEglarPackage = fEglarPackage;
	}

	/**
	 * 
	 * @return
	 */
	public EglarPackageData[] getfEglarPackages() {
		return fEglarPackages;
	}

	/**
	 * 
	 * @param fEglarPackages
	 */
	public void setfEglarPackages(EglarPackageData[] fEglarPackages) {
		this.fEglarPackages = fEglarPackages;
	}

	/**
	 * 
	 * @return
	 */
	public Shell getfParentShell() {
		return fParentShell;
	}

	/**
	 * 
	 * @param fParentShell
	 */
	public void setfParentShell(Shell fParentShell) {
		this.fParentShell = fParentShell;
	}

	/**
	 * 
	 * @return
	 */
	public MessageMultiStatus getfStatus() {
		return fStatus;
	}

	/**
	 * 
	 * @param fStatus
	 */
	public void setfStatus(MessageMultiStatus fStatus) {
		this.fStatus = fStatus;
	}

	protected void addToStatus(CoreException ex) {
		IStatus status= ex.getStatus();
		String message= ex.getLocalizedMessage();
		if (message == null || message.length() < 1) {
			message= EglarPackagerMessages.EglarFileExportOperation_coreErrorDuringExport;
			status= new Status(status.getSeverity(), status.getPlugin(), status.getCode(), message, ex);
		}
		fStatus.add(status);
	}

	/**
	 * Adds a new info to the list with the passed information.
	 * Normally the export operation continues after a warning.
	 * @param	message		the message
	 * @param	error 	the throwable that caused the warning, or <code>null</code>
	 */
	protected void addInfo(String message, Throwable error) {
		fStatus.add(new Status(IStatus.INFO, EDTUIPlugin.getPluginId(), INTERNAL_ERROR, message, error));
	}

	/**
	 * Adds a new warning to the list with the passed information.
	 * Normally the export operation continues after a warning.
	 * @param	message		the message
	 * @param	error	the throwable that caused the warning, or <code>null</code>
	 */
	protected void addWarning(String message, Throwable error) {
		fStatus.add(new Status(IStatus.WARNING, EDTUIPlugin.getPluginId(), INTERNAL_ERROR, message, error));
	}

	/**
	 * Adds a new error to the list with the passed information.
	 * Normally an error terminates the export operation.
	 * @param	message		the message
	 * @param	error 	the throwable that caused the error, or <code>null</code>
	 */
	protected void addError(String message, Throwable error) {
		fStatus.add(new Status(IStatus.ERROR, EDTUIPlugin.getPluginId(), INTERNAL_ERROR, message, error));
	}

	/**
	 * Answers the number of file resources specified by the Eglar package.
	 *
	 * @return int
	 */
	protected int countSelectedElements() {
//		Set enclosingEGLProjects= new HashSet(10);
		int count= 0;

		int n= fEglarPackage.getElements().length;
		irFileCountsPerElements = new int[n];
		for (int i= 0; i < n; i++) {
			Object element= fEglarPackage.getElements()[i];

//			IEGLProject eglProject= (IEGLProject)element;//getEnclosingEGLProject(element);
//			if (eglProject != null)
//				enclosingEGLProjects.add(eglProject);

			IResource resource= null;
			if (element instanceof IEGLElement) {
				IEGLElement ee= (IEGLElement)element;
				resource= ee.getResource();
			} else if (element instanceof IResource) {
				resource= (IResource) element;
			}
			if (resource != null) {
				if (resource.getType() == IResource.FILE) {
					count++;
					if(isIRFile(resource) || isRequiredResourceFile(resource)) {
						irFileCounts++;
						irFileCountsPerElements[i]++;
					}
				}
				else
					count+= getTotalChildCount((IContainer) resource, i);
			}
		}

		return count;
	}

	private int getTotalChildCount(IContainer container, int elementIndex) {
		IResource[] members;
		try {
			members= container.members();
		} catch (CoreException ex) {
			return 0;
		}
		int count= 0;
		for (int i= 0; i < members.length; i++) {
			if (members[i].getType() == IResource.FILE) {
				count++;
				if(isIRFile(members[i]) || isRequiredResourceFile(members[i])) {
					irFileCounts ++;
					irFileCountsPerElements[elementIndex]++;
				}
			}
			else
				count += getTotalChildCount((IContainer)members[i], elementIndex);
		}
		return count;
	}

	/**
	 * Exports the resources as specified by the Eglar package.
	 * @param progressMonitor the progress monitor
	 * @throws InterruptedException thrown when cancelled
	 * @throws CoreException 
	 */
	protected void exportSelectedElements(IProgressMonitor progressMonitor) throws InterruptedException, CoreException {
		fEglarBuilder = fEglarPackage.getEglarBuilder();
		fEglarBuilder.open(fEglarPackage, fParentShell, fStatus);
		int n= fEglarPackage.getElements().length;
		for (int i= 0; i < n; i++) {
			Object element= fEglarPackage.getElements()[i];
			try {
				exportEGLProject2EGLAR((IEGLProject) element,progressMonitor);
				if(fEglarPackage.areEGLSrcFilesExported()) {
					exportEGLProject2SourceZip((IEGLProject) element, progressMonitor);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param eglProject
	 * @param progressMonitor
	 * @throws CoreException
	 */
	protected void exportEGLProject2EGLAR(IEGLProject eglProject, final IProgressMonitor progressMonitor) throws CoreException {
		IPath outputLocation = eglProject.getOutputLocation();
		//For 69080: Export binary projects failed with console UI project
		//If the project is from RBD801 or older, the ouptut directory is EGLBin, regardless of what is found in the .eglpath file
		if(outputLocation != null && "bin".equalsIgnoreCase(outputLocation.lastSegment())) {
			outputLocation = eglProject.getPath().append("EGLbin");
		}
		IFolder outputFolder= createFolderHandle(outputLocation);
		if(outputFolder != null) {
			outputFolder.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					IPath path = resource.getFullPath();
					if(isIRFile(resource) || isRequiredResourceFile(resource)) {
						progressMonitor.worked(work++);
						IPath classFilePath = path.removeFirstSegments(2);
						IFile file = (IFile)resource;
						progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(classFilePath, false)));
						try {
							fEglarBuilder.writeFile(file, classFilePath);
						} catch (CoreException ex) {
							handleCoreExceptionOnExport(ex);
						}
						return false;
					}
					return true;
				}
			});
		}
	}
	
	/**
	 * 
	 * @param eglProject
	 * @param progressMonitor
	 * @throws CoreException
	 */
	private void exportEGLProject2SourceZip(IEGLProject eglProject, final IProgressMonitor progressMonitor)  throws CoreException {
		IPath[] srcPaths = getEGLSourceFolder(eglProject);
		for(IPath srcPath : srcPaths) {
			IFolder srcFolder = createFolderHandle(srcPath);
			if(srcFolder != null) {
				srcFolder.accept(new IResourceVisitor() {
					public boolean visit(IResource resource) throws CoreException {
						IPath path = resource.getFullPath();
						if(isEGLFile(resource)) {
							progressMonitor.worked(work++);
							IPath eglFilePath = path.removeFirstSegments(2);
							IFile file = (IFile)resource;
							progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(eglFilePath, false)));
							try {
								fEglarBuilder.writeEGLSourceFile(file, eglFilePath);
							} catch (CoreException ex) {
								handleCoreExceptionOnExport(ex);
							}
							return false;
						}
						return true;
					}
				});
			}
		}
	}

	/**
	 * 
	 * @param ieglProject
	 * @return
	 */
	protected IPath[] getEGLSourceFolder(IEGLProject ieglProject) {
		ArrayList<IPath> srcPathsArrayList = new ArrayList<IPath>();
		try {
			IEGLPathEntry[] entries = ieglProject.getRawEGLPath();
			for(IEGLPathEntry entry : entries) {
				if(entry.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
					srcPathsArrayList.add(entry.getPath());
				}
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
			return new IPath[0];
		}
		IPath[] srcPathsArray = new IPath[srcPathsArrayList.size()];
		srcPathsArrayList.toArray(srcPathsArray);
		return srcPathsArray;
	}

	/**
	 * Answers whether the given resource is an egl file.
	 * The resource must be a file whose file name ends with ".egl",
	 * or an extension defined as egl source.
	 *
	 * @param file the file to test
	 * @return a <code>true<code> if the given resource is an egl file
	 */
	protected boolean isEGLFile(IResource file) {
		return file != null
			&& file.getType() == IResource.FILE
			&& file.getFileExtension() != null
			&& file.getFileExtension().toLowerCase().equals("egl");
	}
	

	/**
	 * Answers whether the given resource is a ir file.
	 * The resource must be a file whose file name ends with ".ir".
	 *
	 * @param file the file to test
	 * @return a <code>true<code> if the given resource is a ir file
	 */
	protected boolean isIRFile(IResource file) {
		return file != null
			&& file.getType() == IResource.FILE
			&& file.getFileExtension() != null
			&& file.getFileExtension().toLowerCase().equalsIgnoreCase("ir"); //$NON-NLS-1$
	}
	/**
	 * 
	 * @param file
	 * @return
	 */
	protected boolean isRequiredResourceFile(IResource file) {
		if (file != null && file.getType() == IResource.FILE
				&& file.getFileExtension() != null) {
			if(file.getFileExtension().toLowerCase().equalsIgnoreCase("wsdl")) {
				return true;
			}
		} 
		return false;
	}

	/**
	 * Creates a folder resource handle for the folder with the given workspace path.
	 *
	 * @param folderPath the path of the folder to create a handle for
	 * @return the new folder resource handle
	 */
	private IFolder createFolderHandle(IPath folderPath) {
		if (folderPath.isValidPath(folderPath.toString()) && folderPath.segmentCount() >= 2)
			return ResourcesPlugin.getWorkspace().getRoot().getFolder(folderPath);
		else
			return null;
	}

	/**
	 * Handles core exceptions that are thrown by {@link IJarBuilder#writeFile(IFile, IPath)}.
	 * 
	 * @param ex the core exception
	 * @since 3.5
	 */
	private void handleCoreExceptionOnExport(CoreException ex) {
		Throwable realEx= ex.getStatus().getException();
		if (realEx instanceof ZipException && realEx.getMessage() != null
				&& realEx.getMessage().startsWith("duplicate entry:")) //$NON-NLS-1$ hardcoded message string from java.util.zip.ZipOutputStream.putNextEntry(ZipEntry)
			addWarning(ex.getMessage(), realEx);
		else
			addToStatus(ex);
	}

	/**
	 * Returns the status of this operation.
	 * The result is a status object containing individual
	 * status objects.
	 *
	 * @return the status of this operation
	 */
	public IStatus getStatus() {
		String message= null;
		switch (fStatus.getSeverity()) {
			case IStatus.OK:
				message= ""; //$NON-NLS-1$
				break;
			case IStatus.INFO:
				message= EglarPackagerMessages.EglarFileExportOperation_exportFinishedWithInfo;
				break;
			case IStatus.WARNING:
				message= EglarPackagerMessages.EglarFileExportOperation_exportFinishedWithWarnings;
				break;
			case IStatus.ERROR:
				if (fEglarPackages.length > 1)
					message= EglarPackagerMessages.EglarFileExportOperation_creationOfSomeEglarsFailed;
				else
					message= EglarPackagerMessages.EglarFileExportOperation_EglarCreationFailed;
				break;
			default:
				// defensive code in case new severity is defined
				message= ""; //$NON-NLS-1$
				break;
		}
		fStatus.setMessage(message);
		return fStatus;
	}

	/**
	 * Exports the resources as specified by the Eglar package.
	 *
	 * @param	progressMonitor	the progress monitor that displays the progress
	 * @throws InvocationTargetException thrown when an ecxeption occurred
	 * @throws InterruptedException thrown when cancelled
	 * @see	#getStatus()
	 */
	protected void execute(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
		int count= fEglarPackages.length;
		progressMonitor.beginTask("", count); //$NON-NLS-1$
		try {
			for (int i= 0; i < count; i++) {
				SubProgressMonitor subProgressMonitor= new SubProgressMonitor(progressMonitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				fEglarPackage= fEglarPackages[i];
				if (fEglarPackage != null)
					singleRun(subProgressMonitor);
			}
		} finally {
			progressMonitor.done();
		}
	}

	private void singleRun(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
		try {
			if (!preconditionsOK())
				throw new InvocationTargetException(null, getExportOperationCreationFailedSeeDetailsMessage());
			if (fEglarPackage.areGeneratedFilesExported()
				&& ((!isAutoBuilding() && fEglarPackage.isBuildingIfNeeded())
					|| (isAutoBuilding() && fFilesSaved))) {
				int subMonitorTicks= totalWork/10;
				totalWork += subMonitorTicks;
				progressMonitor.beginTask("", totalWork); //$NON-NLS-1$
				SubProgressMonitor subProgressMonitor= new SubProgressMonitor(progressMonitor, subMonitorTicks, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				buildProjects(subProgressMonitor);
			} else
				progressMonitor.beginTask("", totalWork); //$NON-NLS-1$
			exportSelectedElements(progressMonitor);
		} catch (CoreException ex) {
			addToStatus(ex);
		} finally {
			try {
				if (fEglarBuilder != null)
					fEglarBuilder.close();
			} catch (CoreException ex) {
				addToStatus(ex);
			}
			progressMonitor.done();
		}
	}

	protected String getExportOperationCreationFailedSeeDetailsMessage() {
		return EglarPackagerMessages.EglarFileExportOperation_eglarCreationFailedSeeDetails;
	}
	protected boolean preconditionsOK() {
		if (fEglarPackage.getElements() == null || fEglarPackage.getElements().length == 0) {
			addError(EglarPackagerMessages.EglarFileExportOperation_noResourcesSelected, null);
			return false;
		}
		if (fEglarPackage.getAbsoluteEglarLocation() == null) {
			addError(EglarPackagerMessages.EglarFileExportOperation_invalidEglarLocation, null);
			return false;
		}
		File targetFile= fEglarPackage.getAbsoluteEglarLocation().toFile();
		if (targetFile.exists() && !targetFile.canWrite()) {
			addError(EglarPackagerMessages.EglarFileExportOperation_EglarFileExistsAndNotWritable, null);
			return false;
		}
		totalWork= countSelectedElements();

		return true;
	}

	private boolean isAutoBuilding() {
		//return ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding();
		return false;
	}

	private void buildProjects(IProgressMonitor progressMonitor) {
		Set builtProjects= new HashSet(10);
		Object[] elements= fEglarPackage.getElements();
		for (int i= 0; i < elements.length; i++) {
			IProject project= null;
			Object element= elements[i];
			if (element instanceof IResource)
				project= ((IResource)element).getProject();
			else if (element instanceof IEGLElement)
				project= ((IEGLElement)element).getEGLProject().getProject();
			if (project != null && !builtProjects.contains(project)) {
				try {
					project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, progressMonitor);
				} catch (CoreException ex) {
					String message= Messages.format(EglarPackagerMessages.EglarFileExportOperation_errorDuringProjectBuild, BasicElementLabels.getResourceName(project));
					addError(message, ex);
				} finally {
					// don't try to build same project a second time even if it failed
					builtProjects.add(project);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param eglProject
	 * @param folderPath
	 * @return
	 */
	protected boolean isBinaryFolder(IEGLProject eglProject, IPath folderPath) {
		try {
			IPath output = eglProject.getOutputLocation();
			//For 69080: Export binary projects failed with console UI project
			if(output != null && "bin".equalsIgnoreCase(output.lastSegment())) {
				output = eglProject.getPath().append("EGLbin");
			}
			return output.isPrefixOf(folderPath);
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return false;
	}
}

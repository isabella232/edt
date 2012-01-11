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
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.edt.compiler.internal.eglar.EglarAttributes;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.utils.ResourceAndTLFMap;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.utils.EGLProjectInfoUtility;
import org.eclipse.edt.ide.ui.internal.property.pages.BasicElementLabels;
import org.eclipse.edt.ide.ui.internal.property.pages.Messages;

public class NewEglarFileExportOperation extends EglarFileExportOperation {
	
	public static final String EGLBLD_EXTENSION = "eglbld";
	public static final String EGLDD_EXTENSION = "egldd";
	public static final String EGL_EXTENSION = "egl";

	public static final String EGL_GEN_FOLDER = "EGLGen";
	public static final String EGL_BIN_FOLDER = "EGLbin";
	public static final String EGL_WEB_CONTENT = "WebContent";
	public static final String EGL_ICONS = "icons";
	public static final String JAR_FILE_EXTENSION = ".jar";
	public static final String EGL_GEN_JAVA_FOLDER = "JavaSource";
	public static final String CUI_MESSAGE_SOURCE_FOLDER = "MessageSource";

	public NewEglarFileExportOperation(EglarPackageData eglarPackage, Shell parent) {
		super(eglarPackage, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.etools.egl.internal.ui.eglarpackager.EglarFileExportOperation
	 * #exportSelectedElements(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void exportSelectedElements(IProgressMonitor progressMonitor) throws InterruptedException, CoreException {
		int n = getfEglarPackage().getElements().length;
		
		Map<IEGLProject, ResourceAndTLFMap[]> prjAndTlfMap = new HashMap<IEGLProject, ResourceAndTLFMap[]>();
		//confirm if export tlf
		boolean haveTLF = false;
		for(int i=0; i<n; i++){
			Object element = getfEglarPackage().getElements()[i];
			IEGLProject eglProj = (IEGLProject) element;
			ResourceAndTLFMap[] tlfSources = EglarPackagerUtil.getTopLevelFunctionFiles(eglProj);
//			boolean exportTLFSource = false;
			if(tlfSources != null && tlfSources.length > 0){
				haveTLF = true;
				prjAndTlfMap.put(eglProj, tlfSources);
			}
		}
		if(haveTLF){
			if (!EglarPackagerUtil.askToExportTLFSource(getfParentShell())){
				getfEglarPackage().setExportTLFSrcFiles(false);
			} else{
				getfEglarPackage().setExportTLFSrcFiles(true);
			}
		}
		
		for (int i = 0; i < n; i++) {
			Object element = getfEglarPackage().getElements()[i];
			try {
				IEGLProject eglProject = (IEGLProject) element;
				IPath path = new Path(getfEglarPackage().getBaseEglarLocation().toOSString());
				
				path = path.append(eglProject.getElementName() + EGLAR_FILE_EXTENSION);
				getfEglarPackage().setEglarLocation(path);

				setfEglarBuilder(getfEglarPackage().getEglarBuilder());
				getfEglarBuilder().open(getfEglarPackage(), getfParentShell(), getfStatus());
				exportEGLProject2NewEGLAR(eglProject, progressMonitor, prjAndTlfMap.get(eglProject));
			} catch (CoreException e) {
				e.printStackTrace();
			} finally {
				try {
					if (getfEglarBuilder() != null)
						getfEglarBuilder().close();
				} catch (CoreException ex) {
					addToStatus(ex);
				}
				progressMonitor.done();
			}
		}
	}

	/**
	 * 
	 * @param eglProject
	 * @param progressMonitor
	 * @throws CoreException
	 */
	protected void exportEGLProject2NewEGLAR(final IEGLProject eglProject, final IProgressMonitor progressMonitor, ResourceAndTLFMap[] resAndTLFMap) throws CoreException {
		//The manifest file
		getfEglarBuilder().writeFolder(eglProject.getProject(), eglProject.getProject().getFullPath());
		//The binary IR file
		IResource binIRFolder = getBinaryIRFolder(eglProject);
		if(binIRFolder != null) {
			exportIR(binIRFolder, resAndTLFMap, progressMonitor);
		}
		//The generated Java class files folder
		IResource[] binJavaClassFolders = EglarUtility.getGeneratedJavaClassFolder(eglProject.getProject());
		if(binJavaClassFolders != null) {
			HashSet addedSet = new HashSet();
			exportJavaClassFolders(eglProject.getProject(), binJavaClassFolders, progressMonitor);
			for ( int i = 0; i < binJavaClassFolders.length; i ++ ) {
				if ( binJavaClassFolders[i].exists() ) {
					exportJavaDeployFiles(eglProject.getProject(), binJavaClassFolders[i], progressMonitor, addedSet);
				}
			}
			IResource javaSourceFolder = eglProject.getProject().getFolder( EGLProjectInfoUtility.getGeneratedJavaFolder(eglProject.getProject())[0] );
			exportJavaDeployFiles(eglProject.getProject(), javaSourceFolder, progressMonitor, addedSet);
		}
		
		
//		//The generated JS files folder
//		IResource binJavaScriptFolder = getGeneratedJSFolder(eglProject.getProject());
//		if(binJavaScriptFolder != null && binJavaScriptFolder.exists()) {
//			exportGeneralFolder(binJavaScriptFolder,new Path(EglarAttributes.MANIFEST_GENERATED_JS_FOLDER_DEFAULT), progressMonitor);
//		}
		//The EGLGen folder, not include JavaSource sub directory
		IResource genFolder = getGenFolder(eglProject.getProject());
		if(genFolder != null && genFolder.exists()) {
			if(genFolder instanceof IContainer){
				IResource[] subFolders = ((IContainer)genFolder).members();
				for(IResource subFolder: subFolders){
					if(!subFolder.getName().equals(EGL_GEN_JAVA_FOLDER)){
						exportGeneralFolder(subFolder,new Path(""), progressMonitor);
					}
				}
			}	
		}
		
		//The RUI resource
		IResource ruiResFolder = getRuiResourceFolder(eglProject.getProject());
		if(ruiResFolder != null && ruiResFolder.exists()) {
			exportGeneralFolder(ruiResFolder,new Path(""), progressMonitor);
		}
		
		IResource ruiIcons = getRuiIconsFolder(eglProject.getProject());
		if(ruiIcons != null && ruiIcons.exists()) {
			exportGeneralFolder(ruiIcons,new Path(""), progressMonitor);
		}
		
		IResource srcFolder = getSrcFolder(eglProject);
		if(srcFolder != null && srcFolder.exists()) {
			exportEGLSrcFolder(srcFolder, new Path(""), progressMonitor, getfEglarPackage().areEGLSrcFilesExported());
		}
				
		//tlf source
		if(getfEglarPackage().areTLFSrcFilesExported() && resAndTLFMap != null){
			exportEGLSourceFiles(resAndTLFMap, new Path(""), progressMonitor);
		}


		//EGLbin
		exportEGLbinFiles(getBinFolder(eglProject.getProject()), progressMonitor);
	}
	
	//for workitem 74425
	private void exportEGLSourceFiles(ResourceAndTLFMap[] maps, final IPath rootFolder, final IProgressMonitor progressMonitor) throws CoreException{
		for(ResourceAndTLFMap map: maps){
			if(map.getResource() instanceof IFile){
				IPath relPath = map.getResource().getFullPath().removeFirstSegments(1);
				getfEglarBuilder().writeFile((IFile) map.getResource(), rootFolder.append(relPath));
			}
		}
		
	}
	
	private void exportEGLbinFiles(IResource eglbin, final IProgressMonitor progressMonitor) throws CoreException{
		eglbin.accept(new IResourceVisitor() {
			int i = 0;
			public boolean visit(IResource resource) throws CoreException {
				progressMonitor.worked(i++);
				progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(resource.getFullPath(), false)));
				//Remove the project name
				IPath relPath = resource.getFullPath().removeFirstSegments(1);
				switch (resource.getType()) {
				case IResource.FOLDER:
//					getfEglarBuilder().writeFolder(resource, relPath);
					return true;
				case IResource.FILE:
					if ( ((IFile)resource).getFileExtension().equals( "egldd" ) ) {
						getfEglarBuilder().writeFile((IFile) resource, relPath);
					}
					return false;
				}
				return false;
			}
		});
	}
	
	private IResource getRuiResourceFolder(IProject project) {
		IPath newPath = new Path(EGL_WEB_CONTENT);
		return project.getFolder(newPath);
	}
	
	private IResource getRuiIconsFolder(IProject project) {
		IPath newPath = new Path(EGL_ICONS);
		return project.getFolder(newPath);
	}
	
	private IResource getGenFolder(IProject project) {
		IPath newPath = new Path(EGL_GEN_FOLDER);
		return project.getFolder(newPath);
	}
	
	private IResource getBinFolder(IProject project) {
		IPath newPath = new Path(EGL_BIN_FOLDER);
		return project.getFolder(newPath);
	}
	
	private IResource getBinaryIRFolder(IEGLProject eglProject) {
		IPath output;
		try {
			output = eglProject.getOutputLocation();
			//For 69080: Export binary projects failed with console UI project
			if(output != null && "bin".equalsIgnoreCase(output.lastSegment())) {
				output = eglProject.getPath().append("EGLbin");
			}
			return eglProject.getProject().getFolder(output.removeFirstSegments(1));
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private IResource getSrcFolder(IEGLProject project) {
		try {
			if (EGLProject.hasEGLNature(project.getProject())) {
				IEGLPathEntry[] entries = project.getRawEGLPath();
				for (IEGLPathEntry classpathEntry : entries) {
					if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						return project.getProject().getFolder(classpathEntry.getPath().removeFirstSegments(1));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param project
	 * @param javaClassFolders
	 * @throws CoreException
	 */
	private void exportJavaClassFolders(IProject project, final IResource[] javaClassFolders, final IProgressMonitor progressMonitor) throws CoreException {
		IPath newPath = new Path(EglarAttributes.MANIFEST_JAVA_JARS_FOLDER_DEFAULT).append(project.getName() + JAR_FILE_EXTENSION);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			final EglarOutputStream jarOutputStream = new EglarOutputStream(new BufferedOutputStream(outputStream));
			final EglarWriterUtility eglarWriterUtility = new EglarWriterUtility(jarOutputStream, getfParentShell());

			for(final IResource javaClassFolder: javaClassFolders){
				javaClassFolder.accept(new IResourceVisitor() {
					
					public boolean visit(IResource resource) throws CoreException {
						progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(resource.getFullPath(), false)));
						IPath path = resource.getFullPath();
						path = path.removeFirstSegments(javaClassFolder.getFullPath().segmentCount());
						try {
							switch (resource.getType()) {
							case IResource.FOLDER:
								eglarWriterUtility.addDirectories(path);
								return true;
							case IResource.FILE:
								eglarWriterUtility.addFile((IFile) resource, path);
								return false;
							}
							return false;
						} catch (IOException e) {
							e.printStackTrace();
							return true;
						}
					}

				});
			}
		
			//MessageSource folder
			final IResource msgSourceFolder = project.getFolder(CUI_MESSAGE_SOURCE_FOLDER);
			if(msgSourceFolder != null && msgSourceFolder.exists()) {
				msgSourceFolder.accept(new IResourceVisitor(){
					public boolean visit(IResource resource) throws CoreException {
						progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(resource.getFullPath(), false)));
						IPath path = resource.getFullPath();
						try {
							switch (resource.getType()) {
							case IResource.FOLDER:
								return true;
							case IResource.FILE:
								eglarWriterUtility.addFile((IFile) resource, new Path(path.lastSegment()));
								return false;
							}
							return false;
						} catch (IOException e) {
							e.printStackTrace();
							return true;
						}
					}
				});
			}
						
			jarOutputStream.close();
		} catch (Exception ex) {
//			ex.printStackTrace();
		} finally {
			byte[] bytes = outputStream.toByteArray();
			if(bytes.length > 0)
				getfEglarBuilder().writeFileFromBytes(bytes, newPath);
			try {
				if(outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void exportJavaDeployFiles(IProject project, final IResource javaClassFolder, final IProgressMonitor progressMonitor, final HashSet addedSet) throws CoreException {
		final IPath rootPath = new Path( EGLProjectInfoUtility.getGeneratedJavaFolder(project)[0] );

		try {
			javaClassFolder.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(resource.getFullPath(), false)));
					IPath path = resource.getFullPath();
					path = rootPath.append( path.removeFirstSegments(javaClassFolder.getProjectRelativePath().segmentCount() + 1) );
					switch (resource.getType()) {
					case IResource.FOLDER:
						if ( !addedSet.contains( path ) ) {
							getfEglarBuilder().writeFolder(resource, path);
							addedSet.add( path );
						}
						return true;
					case IResource.FILE:
						if ( !"class".equals( resource.getFileExtension() ) && !"java".equals( resource.getFileExtension() ) && !addedSet.contains( resource.getName() ) ) {
							getfEglarBuilder().writeFile((IFile)resource, path);
							addedSet.add(  resource.getName()  );
						}
						return false;
					}
					return false;
				}

			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void exportGeneralFolder(IResource jsFolder, final IPath rootFolder, final IProgressMonitor progressMonitor) throws CoreException {
		jsFolder.accept(new IResourceVisitor() {
			int i = 0;
			public boolean visit(IResource resource) throws CoreException {
				progressMonitor.worked(i++);
				progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(resource.getFullPath(), false)));
				//Remove the project name
				IPath relPath = resource.getFullPath().removeFirstSegments(1);
				switch (resource.getType()) {
				case IResource.FOLDER:
					getfEglarBuilder().writeFolder(resource, rootFolder.append(relPath));
					return true;
				case IResource.FILE:
					getfEglarBuilder().writeFile((IFile) resource, rootFolder.append(relPath));
					return false;
				}
				return false;
			}
		});
	}
	
	private void exportEGLSrcFolder(IResource eglSRCFolder, final IPath rootFolder, final IProgressMonitor progressMonitor, final boolean exportEglSource) throws CoreException {
		eglSRCFolder.accept(new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(resource.getFullPath(), false)));
				//Remove the project name
				IPath relPath = resource.getFullPath().removeFirstSegments(1);
				switch (resource.getType()) {
				case IResource.FOLDER:
					getfEglarBuilder().writeFolder(resource, rootFolder.append(relPath));
					return true;
				case IResource.FILE:
					if (exportEglSource || !isEGL(resource)) { // Write to the build folder
						getfEglarBuilder().writeFile((IFile) resource, relPath);
					}
					return false;
				}
				return false;
			}
		});
	}
	
	/**
	 * 
	 * @throws CoreException
	 */
	private void exportIR(IResource resource, final ResourceAndTLFMap[] resAndTLFMap, final IProgressMonitor progressMonitor) throws CoreException {
		resource.accept(new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				IPath path = resource.getFullPath();
				path = path.removeFirstSegments(2);
				progressMonitor.worked(1);
				progressMonitor.subTask(Messages.format(EglarPackagerMessages.EglarFileExportOperation_exporting, BasicElementLabels.getPathLabel(resource.getFullPath(), false)));
				switch (resource.getType()) {
				case IResource.FOLDER:
					getfEglarBuilder().writeFolder(resource, path);
					return true;
				case IResource.FILE:
					if(isEGLBLDDD(resource)) {
						return false;
					}
					//If user choose not to export the source file of TLF, then the IR files for TLF will not be exported as well.
					if(!getfEglarPackage().areTLFSrcFilesExported() && resAndTLFMap != null) {
						IPath irPath = ((IFile) resource).getFullPath().removeFirstSegments(2).removeFileExtension();
						for(ResourceAndTLFMap resourceAndTLFMap : resAndTLFMap) {
							if(!resourceAndTLFMap.isSamePackage(resource)) {
								continue;
							}
							if(resourceAndTLFMap.includedPath(irPath)) { 
								return false;
							}
						}
					}
					getfEglarBuilder().writeFile((IFile) resource, path);
					return false;
				}
				return false;
			}
		});
	}

		/**
	 * 
	 * @param resource
	 * @return
	 */
	private boolean isEGL(IResource resource) {
		if (resource == null) {
			return false;
		}
		String fileExtension = resource.getFullPath().getFileExtension();
		if (fileExtension != null && fileExtension.equalsIgnoreCase(EGL_EXTENSION)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	private boolean isEGLBLDDD(IResource resource) {
		if (resource == null) {
			return false;
		}
		String fileExtension = resource.getFullPath().getFileExtension();
		if (fileExtension != null && (fileExtension.equalsIgnoreCase(EGLBLD_EXTENSION) || fileExtension.equalsIgnoreCase(EGLDD_EXTENSION))) {
			return true;
		}
		return false;
	}
}

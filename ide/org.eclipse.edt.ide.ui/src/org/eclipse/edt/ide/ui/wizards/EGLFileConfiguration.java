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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class EGLFileConfiguration extends EGLPackageConfiguration {
	
	/** Constants for determining if templates are in error */
	public final static String TEMPLATE_NOT_FOUND = NewWizardMessages.NewEGLFileWizardPageTemplateSelectionErrorTemplatenotfound;
	public final static String TEMPLATE_DISABLED = NewWizardMessages.NewEGLFileWizardPageTemplateSelectionErrorTemplatedisabled;
	public final static String TEMPLATE_CORRUPTED = NewWizardMessages.NewEGLFileWizardPageTemplateSelectionErrorTemplatecorrupted;
	
	/** The file name. */
	private String fileName;
	
	/** The IFile */
	private IFile file;
	
	/** init to be not overwrite existing file */
	private boolean bOverwrite = false;
	public final static String PROPERTY_OVERWRITE = "EGLFileConfiguration.Overwrite"; //$NON-NLS-1$
	private final static String[] EGLCORE = new String[] {"egl", "core"}; //$NON-NLS-1$ //$NON-NLS-2$
	
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public EGLFileConfiguration(){
    	super();
    	setDefaultAttributes();
    }
    
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		setDefaultAttributes();
	}
	
	private void setDefaultAttributes() {
		fileName = ""; //$NON-NLS-1$
		file = null;
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param string
	 */
	public void setFileName(String string) {
		fileName = string;
	}

	/**
	 * @return
	 */
	public IFile getFile() {
		IFile fileHandle = getFileHandle();
		if(fileHandle.exists())
			file = fileHandle;
		return file;
	}
	
	public IFile getFileHandle(){
		//Attempt to create a handle to this file
		if(/*file==null && */!fileName.equals("")){ //$NON-NLS-1$
			
			try{
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
				IEGLProject eproject = EGLCore.create(project);
				IPath sourcePath = new Path(getContainerName());
				IPackageFragmentRoot root = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
				IPackageFragment frag = root.getPackageFragment(getFPackage());
				IContainer container = (IContainer) frag.getResource();
				
				IPath path = new Path(getFileName());
				String fileExt = getFileExtension();
				if(fileExt.length()>0)
					path = path.addFileExtension(fileExt);
				IFile fileHandle = container.getFile(path); //$NON-NLS-1$
				return fileHandle;
			}
			catch(EGLModelException e){
				return null;
			}
		}
		
		return null;		
	}
	
	public String getFileExtension()
	{
		return "egl"; //$NON-NLS-1$
	}

	/**
	 * @param file
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

    /**
     * @return Returns the bOverwrite.
     */
    public boolean isOverwrite() {
        return bOverwrite;
    }
    /**
     * @param overwrite The bOverwrite to set.
     */
    public void setOverwrite(boolean overwrite) {
        boolean oldValue = bOverwrite;        
        bOverwrite = overwrite;
        
        pcs.firePropertyChange(PROPERTY_OVERWRITE, oldValue, isOverwrite());        
    }
    
	public void addPropertyChangeListener(PropertyChangeListener pcl)
	{
		pcs.addPropertyChangeListener(pcl);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener pcl)
	{
		pcs.removePropertyChangeListener(pcl);
	}
	
	
	static public Part getBoundPart(IEGLFile eglFile, String partSimpleName)
	{
		final Part[] boundPart = new Part[]{null};
		try {		

			IFile file = (IFile)(eglFile.getCorrespondingResource());
		
			IEGLElement eglPkgFrag = eglFile.getParent();
			String packageName = eglPkgFrag.getElementName();
			
			IBufferFactory UIBufferFactory = EGLUI.getBufferFactory();		
			WorkingCopyCompiler compiler = WorkingCopyCompiler.getInstance();
			compiler.compilePart(file.getProject(), packageName, file, EGLCore.getSharedWorkingCopies(UIBufferFactory), partSimpleName, 
					new IWorkingCopyCompileRequestor(){
						public void acceptResult(WorkingCopyCompilationResult result) {
							boundPart[0] = (Part)result.getBoundPart();						
						}			
			});
		}catch (EGLModelException e) {
			e.printStackTrace();
		}
		
		return boundPart[0];
	}
		
	static public List getBoundParts(IEGLFile eglFile)
	{
		final List boundPartList = new ArrayList();
		try {		

			IFile file = (IFile)(eglFile.getCorrespondingResource());
		
			IEGLElement eglPkgFrag = eglFile.getParent();
			String packageName = eglPkgFrag.getElementName();
			
			Path pkgPath = new Path(packageName.replace('.', IPath.SEPARATOR));
// TODO EDT Uncomment when working copy compiler is ready			
//			String[] pkgName = Util.pathToStringArray(pkgPath);
			
//			IBufferFactory UIBufferFactory = EGLUI.getBufferFactory();		
//			WorkingCopyCompiler compiler = WorkingCopyCompiler.getInstance();
//			compiler.compileAllParts(file.getProject(), pkgName, file, EGLCore.getSharedWorkingCopies(UIBufferFactory), 
//					new IWorkingCopyCompileRequestor(){
//						public void acceptResult(WorkingCopyCompilationResult result) {
//							Node boundNode = result.getBoundPart();
//							if(boundNode instanceof Part)
//								boundPartList.add((Part)boundNode);
//						}			
//			});
		}catch (EGLModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return boundPartList;
		//return (Part[])boundPartList.toArray(new Part[boundPartList.size()]);
	}
	
    /**
     * try to get the string value of property name and namespace for the xml complex annotation
     * @param nameBinding - name binding got it from name.resolveBinding()
     * @param nameVal	- output parameter
     * @param namespaceVal - output parameter
     */
	static public void getXMLAnnotationValueFromBinding(Element nameBinding, StringBuffer nameVal, StringBuffer namespaceVal)
	{
        if(nameBinding != null)
        {
        	Annotation xmlAnnotationBinding = nameBinding.getAnnotation(EGLCORE + "." + IEGLConstants.PROPERTY_XML);
        	if(xmlAnnotationBinding != null)
        	{
	        	//get the xml annoation vlaues
	        	Object nameObj = xmlAnnotationBinding.getValue(IEGLConstants.PROPERTY_NAME);
	        	if(nameObj != null)
	        	{
	        		nameVal.append(nameObj.toString());
	        	}
	        	
	        	Object namespaceObj = xmlAnnotationBinding.getValue(IEGLConstants.PROPERTY_NAMESPACE);
	        	if(namespaceObj != null)
	        	{
	        		namespaceVal.append(namespaceObj.toString());
	        	}
        	}
        }
		
	}    
	
}

/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;


public class EGLReadOnlyEditorInput implements IStorageEditorInput {
    private EGLReadOnlyFile eglReadOnlyFile;
    private IClassFile classFile;
    
    public EGLReadOnlyEditorInput(EGLReadOnlyFile binaryReadOnlyFile) {
    	this.eglReadOnlyFile = binaryReadOnlyFile;
    }
    
    public boolean exists() {
    	return true;
    }
    
    public ImageDescriptor getImageDescriptor() {
    	return null;
    }
    
    /*
	 * get the IR file name (without package, with extension)
	 * e.g.
	 * demointerface.eglxml
	 */
    public String getName() {
       return eglReadOnlyFile.getName();
    }
    public IPersistableElement getPersistable() {return null;}
    public IStorage getStorage() {
       return eglReadOnlyFile;
    }
    public String getToolTipText() {
       return "file: " + getName();
    }
    public Object getAdapter(Class adapter) {
      return null;
    }
 
    public String getSource(){
    	return(eglReadOnlyFile.getSource());
    }
    
    /*
	 * get the full path for the IR file
	 * e.g.
	 * TestProj1/Test.eglar|com/ibm/egl/test/interfaces/demointerface.eglxml
	 * C:/Temp/Test.eglar|com/ibm/egl/test/interfaces/demointerface.eglxml
	 * TestProj1/Test.eglar|demointerface.eglxml
	 * 
	 */
    public String getFullPath() {
    	return eglReadOnlyFile.getFullPath().toString();
    }
    
    public String getProject(){
    	return eglReadOnlyFile.getProject();
    }
    
    public IClassFile getClassFile() {
		return classFile;
	}
    
    public void setClassFile(IClassFile classFile) {
    	this.classFile = classFile;
    }
    
    public boolean equals(Object o) {
    	if(o instanceof EGLReadOnlyEditorInput) {
    		return (this.getFullPath().equals(((EGLReadOnlyEditorInput) o).getFullPath()) && this.getProject().equals(((EGLReadOnlyEditorInput) o).getProject()));
    	}
    	return super.equals(o);
    }
	
}

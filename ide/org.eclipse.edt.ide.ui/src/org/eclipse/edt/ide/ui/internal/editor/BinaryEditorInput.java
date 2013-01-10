/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragment;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;


public class BinaryEditorInput implements IStorageEditorInput, IPersistableElement{
    private BinaryReadOnlyFile binaryReadOnlyFile;
    private IClassFile classFile;
    
    public BinaryEditorInput(BinaryReadOnlyFile binaryReadOnlyFile) {
    	this.binaryReadOnlyFile = binaryReadOnlyFile;
    }
    
    public BinaryEditorInput(BinaryReadOnlyFile binaryReadOnlyFile,IClassFile classFile) {
    	this.binaryReadOnlyFile = binaryReadOnlyFile;
    	this.classFile = classFile;
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
       return binaryReadOnlyFile.getName();
    }
    public IPersistableElement getPersistable() {
    	return this;
    }
    
    public IStorage getStorage() {
       return binaryReadOnlyFile;
    }
    public String getToolTipText() {
       return "file: " + getName();
    }
    public Object getAdapter(Class adapter) {
      return null;
    }
    
    public String getSource(){
    	return(binaryReadOnlyFile.getSource());
    }
    
    public void setClassFile(IClassFile classFile){
    	this.classFile = classFile;
    }
    
    public IClassFile getClassFile(){
    	if(classFile != null)
    		return classFile;
		IProject proj = null;
		//get the IProject element of current project (i.e. under which project the class file is to be open)
		proj = binaryReadOnlyFile.getProject();
		if(proj == null){
    		return null;
    	}
		IEGLProject eglProj = EGLCore.create(proj);
		try {
			IPackageFragmentRoot myRoot = eglProj.getPackageFragmentRoot(binaryReadOnlyFile.getFullPath().toString());
			if(myRoot instanceof  EglarPackageFragmentRoot && myRoot.exists()){
				EglarPackageFragmentRoot packageFragmentRoot = (EglarPackageFragmentRoot)myRoot;
				String[] pkgName = IRFileNameUtility.toIRFileName(binaryReadOnlyFile.getPackageSegments());
				EglarPackageFragment packageFragment = (EglarPackageFragment)packageFragmentRoot.getPackageFragment(pkgName);
				if(packageFragment != null && packageFragment.exists()){
					classFile = packageFragment.getClassFile(binaryReadOnlyFile.getIrName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classFile;
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
    	return binaryReadOnlyFile.getIrFullPathString();
    }
    
    public IProject getProject(){
    	return binaryReadOnlyFile.getProject();
    }
    
    public boolean equals(Object o) {
    	if(o instanceof BinaryEditorInput) {
    		return (this.getFullPath().equals(((BinaryEditorInput) o).getFullPath()) && this.getProject().equals(((BinaryEditorInput) o).getProject()));
    	}
    	return super.equals(o);
    }
	
    public BinaryReadOnlyFile getBinaryReadOnlyFile(){
    	return(this.binaryReadOnlyFile);
    }

	@Override
	public void saveState(IMemento memento) {
		BinaryEditorInputFactory.saveState(memento, this);
	}

	@Override
	public String getFactoryId() {
		return BinaryEditorInputFactory.getFactoryId();
	}
}

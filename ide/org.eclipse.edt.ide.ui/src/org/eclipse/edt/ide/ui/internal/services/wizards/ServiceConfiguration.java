/*******************************************************************************
 * Copyright 漏 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.services.wizards;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.edt.ide.ui.wizards.InterfaceListConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class ServiceConfiguration extends InterfaceListConfiguration {
	/** The name of the EGL Service */
	private String serviceName;
	
	//list of called basic program that this service can wrap, each element in
	//the key is the fully qualified called basic program name, the value is part	
	private Hashtable<String,IPart> calledPgms;
	private Hashtable calledProgramPgmHash;		//the key is the fully qualifed called basic program name(String), the value is IEGLProgram in pgm model
	
	private String originalProgramPackage;
	
	private boolean genAsWebService;
	private boolean genAsRestService;
	
	private List superInterfaces;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setDefaultAttributes();
	}
	
	/**
	 * 
	 * @return
	 */
	public List getSuperInterfaces() {
		return superInterfaces;
	}
	
	@Override
	public void setFileName(String string) {
		super.setFileName(string);
		setServiceName(string);
	}
	/**
	 * 
	 * @param superInterfaces
	 */
	public void setSuperInterfaces(List superInterfaces) {
		this.superInterfaces = superInterfaces;
	}
	/**
	 * return the fully qualified selected program name
	 * @param workbench
	 * @param selection
	 * @return
	 */
	public String initBasedOnSelectedProgram(IWorkbench workbench, IStructuredSelection selection) {
		init(workbench, selection);
		String FQPgmName = ""; //$NON-NLS-1$
		IEGLFile programFile = null;				
		Object selectedElement= selection.getFirstElement();
		
		if(selectedElement instanceof IFile) {
		    IEGLElement eglElem = EGLCore.create((IFile)selectedElement);	
		    if(eglElem instanceof IEGLFile)
		        programFile = (IEGLFile)eglElem;
		} else if(selectedElement instanceof IEGLFile) {
		    programFile = (IEGLFile)selectedElement;
		}
		
		if(programFile != null) {
			//init the package name to be the same as the service package
			IEGLElement parentElem = programFile.getParent();
			originalProgramPackage = parentElem.getElementName();
			setFPackage(originalProgramPackage);
					
            //init the file name,get the filename without the extension
            String fileName = programFile.getElementName();
            int dot = fileName.indexOf('.');
            fileName = fileName.substring(0, dot);               
            setFileName(fileName + "CallService");		 //$NON-NLS-1$
            		
            //init the interface name to be the same as the file name
            setServiceName(getFileName());
            
            //should we init to let user overwrite the existing file?
            //setOverwrite(true);
            
            //get the fully qualified name of the program
            FQPgmName = originalProgramPackage;
            if(originalProgramPackage.length()>0)
                FQPgmName += '.';
            FQPgmName += fileName;
            //get the IPart
            IPart programPart = programFile.getPart(fileName);
            calledPgms.put(FQPgmName, programPart);
            
            try {
	            File fileast = EGLModelUtility.getEGLFileAST(programFile, EGLUI.getBufferFactory());
	            
	            final String FQPgmNameCopy = FQPgmName;
	            fileast.accept(new DefaultASTVisitor() {
	            	public boolean visit(File file) {
	            		return true;
	            	}
	            	
	            	public boolean visit(Program program) {
	            		calledProgramPgmHash.put(FQPgmNameCopy, program);
	            		return false;
	            	};
	            });
            } catch(Exception e) {
            	e.printStackTrace();
            }
            
		}
	    return FQPgmName;
	}
	
	 /**
     * @return Returns the serviceName.
     */
    public String getServiceName() {
        return serviceName;
    }
    /**
     * @param serviceName The serviceName to set.
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
	private void setDefaultAttributes() {
	    serviceName = ""; //$NON-NLS-1$
	    calledPgms = new Hashtable<String,IPart>();
	    calledProgramPgmHash = new Hashtable();
	    genAsWebService = false;
	    genAsRestService = false;
	}
	
	/**
	 * get the Program Part, IPart
	 */
	public IPart getCalledBasicPgm(String pgmFullyQualifiedName) {
		return calledPgms.get(pgmFullyQualifiedName);
	}
    
	/**
	 * get the IEGLProgram - pgm model
	 * @param pgmFullyQualifiedName
	 * @return the bound Program node
	 */
	public Program getBoundCalledBasicProgramPgm(String pgmFullyQualifiedName) {
		IPart pgmPart = calledPgms.get(pgmFullyQualifiedName);
		IEGLFile eglfile = (IEGLFile)pgmPart.getParent();
		return (Program)(getBoundPart(eglfile, pgmPart.getElementName()));
	}
	
	public BinaryPart getBinaryCalledBasicProgramPgm(String pgmFullyQualifiedName){
		return (BinaryPart)calledProgramPgmHash.get(pgmFullyQualifiedName);
	}
		
	/**
	 * key is the fully qualifed interface name, the value is the IPart of this interface
	 */
	public void addCalledBasicPgm(String pgmFullyQualifiedName, IPart pgmPart, Program pgmProgramPart) {
	    calledPgms.put(pgmFullyQualifiedName, pgmPart);
	    calledProgramPgmHash.put(pgmFullyQualifiedName, pgmProgramPart);
	}
	
	public void addCalledBasicPgm(String pgmFullyQualifiedName, IPart pgmPart, BinaryPart pgmProgramPart){
		calledPgms.put(pgmFullyQualifiedName, pgmPart);
		calledProgramPgmHash.put(pgmFullyQualifiedName, pgmProgramPart);
	}
	
	public boolean IsGenAsWebService() {
		return genAsWebService;
	}
	
	public void setGenAsWebService(boolean genAsWS) {
		this.genAsWebService = genAsWS;
	}
	
	public boolean isGenAsRestService() {
		return genAsRestService;		
	}
	
	public void setGenAsRestService(boolean genAsRest) {
		this.genAsRestService = genAsRest;
	}
}

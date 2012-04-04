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
package org.eclipse.edt.ide.ui.internal.dialogs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.edt.ide.ui.internal.services.wizards.ServiceConfiguration;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Shell;

public class CalledBasicProgramSelectionDialog extends InterfaceSelectionDialog {
    private ServiceConfiguration fConfig;
	
	//cache the following information, for performance purpose
	private Hashtable<String,IPart> partHash;
	private Hashtable pgmHash;
	
	/**
     * @param parent
     * @param context
     * @param list
     * @param InterfaceSubType
     * @param config
     * @param p
     */
    public CalledBasicProgramSelectionDialog(Shell parent,
            IRunnableContext context, ListDialogField list, ServiceConfiguration config,
            IEGLProject p) {
        super(parent, context, list, IEGLSearchConstants.PROGRAM, IEGLConstants.PROGRAM_SUBTYPE_BASIC, config, p);
        fConfig = config;
        partHash = new Hashtable<String,IPart>();
        pgmHash = new Hashtable();
    }
    
    protected void addSelectedPart() {
		Object ref= getLowerSelectedElement();
		if (ref instanceof PartInfo) {
		    PartInfo partinfo = (PartInfo) ref;
			String qualifiedName= (partinfo).getFullyQualifiedName();			
			fList.addElement(qualifiedName);
			IPart part = partHash.get(qualifiedName);
			
			if(part != null) {
				if(part.getParent() instanceof IEGLFile) {
				    Program pgmProgram = (Program)(pgmHash.get(qualifiedName));
				    fConfig.addCalledBasicPgm(qualifiedName, part, pgmProgram);
				} else if(part.getParent() instanceof IClassFile) {
					BinaryPart pgmProgram = (BinaryPart)(pgmHash.get(qualifiedName));
					fConfig.addCalledBasicPgm(qualifiedName, part, pgmProgram);
				}
			}
			String message= NewWizardMessages.bind(NewWizardMessages.EGLInterfaceSelectionDialogInterfaceaddedInfo, qualifiedName); //$NON-NLS-1$
			updateStatus(new StatusInfo(IStatus.INFO, message));
		}
	}
	
	private BinaryPart getPGMProgramPartFromBinaryFile(IPart programPart){
		if(programPart.getParent() instanceof IClassFile) {
			if(programPart instanceof BinaryPart && ((BinaryPart)programPart).isProgram()) {
				return (BinaryPart)programPart;
			}
        }
		return null;
	}
    
    private Program getPGMProgramPart(IPart programpart) {
        if(programpart.getParent() instanceof IEGLFile) {
            IEGLFile eglFile = (IEGLFile)(programpart.getParent());
			try {
				File fileAST = EGLModelUtility.getEGLFileAST(eglFile, EGLUI.getBufferFactory());

	            final Program[] pgm = new Program[]{null};
	            fileAST.accept(new DefaultASTVisitor() {
	            	public boolean visit(File file) {return true;}
	            	
	            	public boolean visit(Program program) {
	            		pgm[0] = program;
	            		return false;
	            	};
	            });
	            return pgm[0];	            
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
        }
        return null;
    }
	
    @Override
    protected int addParts(ArrayList partsList, IEGLSearchScope scope,
            int elementKinds, String subType) {
        ArrayList typeList = new ArrayList();
        int superReturn =  super.addParts(partsList, scope, elementKinds, subType);
        
        if(superReturn == OK) {
	        //filter out the non-called program from the partsList 
	        Iterator it = partsList.iterator();
	        while(it.hasNext()) {
	            PartInfo partinfo = (PartInfo)(it.next());
	            IPart part = getPartFromPartInfo(partinfo);
	            if(part.getParent() instanceof IEGLFile){
	            	Program pgmProgramPart = getPGMProgramPart(part);
	            	 if(pgmProgramPart != null && pgmProgramPart.isCallable())
	 	            {
	 	                typeList.add(partinfo);
	 	                String fullyqualifeidname = partinfo.getFullyQualifiedName();
	 	                partHash.put(fullyqualifeidname, part);
	 	                pgmHash.put(fullyqualifeidname, pgmProgramPart);
	 	            }
	            } else {
	            	BinaryPart pgmProgramPart = getPGMProgramPartFromBinaryFile(part);
	            	try {
						if(pgmProgramPart != null && ((SourcePartElementInfo)pgmProgramPart.getElementInfo()).isCalledProgram()){
							typeList.add(partinfo);
		 	                String fullyqualifeidname = partinfo.getFullyQualifiedName();
		 	                partHash.put(fullyqualifeidname, part);
		 	                pgmHash.put(fullyqualifeidname, pgmProgramPart);
						}
					} catch (EGLModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	           
	        }
	        partsList.clear();
	        partsList.addAll(typeList);
        }
        return superReturn;
    }
}

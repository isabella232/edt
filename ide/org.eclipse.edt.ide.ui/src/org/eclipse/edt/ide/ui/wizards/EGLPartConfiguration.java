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

import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class EGLPartConfiguration extends EGLFileConfiguration {
	
	/** Template Selections */
	public final static int USE_DEFAULT = 0;
	public final static int USE_CUSTOM = 1;
	
	/** Selection Chosen */
	private int chosenTemplateSelection;
	
	public EGLPartConfiguration(){
		super();
		setDefaultAttributes();
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		setDefaultAttributes();
	}
	
	private void setDefaultAttributes() {
		chosenTemplateSelection = EGLPartConfiguration.USE_CUSTOM;
	}
	

	/**
	 * @return
	 */
	public int getChosenTemplateSelection() {
		return chosenTemplateSelection;
	}

	/**
	 * @param i
	 */
	public void setChosenTemplateSelection(int i) {
		chosenTemplateSelection = i;
	}

	static private boolean isUniqueServiceVarName(String varName, IEGLElement[] varfields)
	{
	    boolean bFnd = false;
	    for(int i=0; i<varfields.length && !bFnd; i++)
	    {
	        String varFieldName = varfields[i].getElementName();
	        if(varName.equalsIgnoreCase(varFieldName))
	            bFnd = true;
	    }	        
	    return !bFnd;	    
	}
	
	static public String getUniqueServiceVarName(String varName, IEGLElement[] varfields)
	{
	    String uniqueName = varName;
	    int cnt = 0;
	    
	    while(!isUniqueServiceVarName(uniqueName, varfields))
	    {
	        cnt++;
	        uniqueName = varName + Integer.toString(cnt);
	    }	    	   
	    return uniqueName;
	}	
}

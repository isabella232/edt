/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class InterfaceConfiguration extends InterfaceListConfiguration {
	
	public final static int BASIC_INTERFACE = 0;
	public final static int JAVAOBJ_INTERFACE = 1;
	
	/** The type of program */
	private int interfaceType;	
    
    /** The name of the EGL Interface */
	private String interfaceName;
	
	public InterfaceConfiguration(){
		super();
		
		setDefaultAttributes();
	}
    
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setDefaultAttributes();			
	}
    
    /**
     * @return Returns the interface EGL Name.
     */
    public String getInterfaceName() {
        return interfaceName;
    }
    /**
     * @param serviceName The serviceName to set.
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
 
  
	private void setDefaultAttributes() {
		interfaceName = ""; //$NON-NLS-1$
	}
    
    /**
     * @return Returns the interfaceType.
     */
    public int getInterfaceType() {
        return interfaceType;
    }
    
    /**
     * @param interfaceType The interfaceType to set.
     */
    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
    }
}

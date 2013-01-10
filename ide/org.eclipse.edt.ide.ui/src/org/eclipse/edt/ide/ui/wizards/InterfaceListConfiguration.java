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

import java.util.Hashtable;

import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class InterfaceListConfiguration extends EGLPartConfiguration {
	//list of super interfaces that this service implements, each element in
	//the key is the fully qualified interface name, the value is partInfo
	private Hashtable<String,IPart> superInterfaces;
	
	public InterfaceListConfiguration() {
		super();
		setDefaultAttributes();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setDefaultAttributes();
	}
	
	private void setDefaultAttributes() {
	    superInterfaces = new Hashtable<String,IPart>();
	}
	
	/*
	 * get the Interface Part, IPart
	 */
	public IPart getInterface(String interfaceFullyQualifiedName) {
		return superInterfaces.get(interfaceFullyQualifiedName);
	}
    	
	/*
	 * key is the fully qualifed interface name, the value is the IPart of this interface
	 */
	public void addInterface(String interfaceFullyQualifiedName, IPart interfacePart) {
	    superInterfaces.put(interfaceFullyQualifiedName, interfacePart);
	}	
}

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
package org.eclipse.edt.ide.ui.internal.libraries.wizards;

import org.eclipse.edt.ide.ui.wizards.EGLPartConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class LibraryConfiguration extends EGLPartConfiguration {
	public final static int BASIC_LIBRARY = 0;
	public final static int NATIVE_LIBRARY = 1;
//	public final static int SERVICE_BINDING_LIBRARY = 2;
	public final static int RUIPROP_LIBRARY = 2;
	public final static int DATAACCESS_LIBRARY = 3;
	
	/** The name of the library */
	private String libraryName;
	
	/** The type of library */
	private int libraryType;
	
	private String codeTemplateId;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		setDefaultAttributes();
	}

	/**
	 * @return
	 */
	public String getLibraryName() {
		return libraryName;
	}

	/**
	 * @param string
	 */
	public void setLibraryName(String string) {
		libraryName = string;
	}

	private void setDefaultAttributes() {
		libraryName = ""; //$NON-NLS-1$
	}
	/**
	 * @return Returns the libraryType.
	 */
	public int getLibraryType() {
		return libraryType;
	}
	/**
	 * @param libraryType The libraryType to set.
	 */
	public void setLibraryType(int libraryType) {
		this.libraryType = libraryType;
	}

	public String getCodeTemplateId() {
		return codeTemplateId;
	}

	public void setCodeTemplateId(String codeTemplateId) {
		this.codeTemplateId = codeTemplateId;
		if(codeTemplateId.equals("org.eclipse.edt.ide.ui.templates.basic_library")){
			setLibraryType(BASIC_LIBRARY);
		}else if(codeTemplateId.equals("org.eclipse.edt.ide.ui.templates.ruiProp_library")){
			setLibraryType(RUIPROP_LIBRARY);
		}else{
			setLibraryType(DATAACCESS_LIBRARY);			
		}
	}	
	
}

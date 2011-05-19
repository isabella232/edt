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
package org.eclipse.edt.ide.ui.wizards;

public class LibraryOperation extends PartOperation {

	public LibraryOperation(LibraryConfiguration configuration) {
		super(configuration);
	}

	protected String getFileContents() throws PartTemplateException {
		String templateid = "org.eclipse.edt.ide.ui.templates.basic_library";
		LibraryConfiguration configuration = (LibraryConfiguration) super.configuration;
		String partName = configuration.getLibraryName();

		//Determine type of library and update template Description
		int libType = configuration.getLibraryType();
		switch(libType){
		case (LibraryConfiguration.NATIVE_LIBRARY):
			templateid = "org.eclipse.edt.ide.ui.templates.native_library"; //$NON-NLS-1$
			break;
		case (LibraryConfiguration.RUIPROP_LIBRARY):
			templateid = "org.eclipse.edt.ide.ui.templates.ruiProp_library"; //$NON-NLS-1$
			break;
		default:
			templateid = "org.eclipse.edt.ide.ui.templates.basic_library";
			break;
		}
		
		return getFileContents(
			"library", //$NON-NLS-1$
			templateid,
			new String[] {
				"${libraryName}" //$NON-NLS-1$
			},
			new String[] {
				partName
			});
	}
}

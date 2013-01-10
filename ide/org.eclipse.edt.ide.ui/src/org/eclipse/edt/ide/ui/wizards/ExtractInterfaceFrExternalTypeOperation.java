/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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


public class ExtractInterfaceFrExternalTypeOperation extends EGLFileOperation {
    private StringBuffer fContent;
	
    public ExtractInterfaceFrExternalTypeOperation(EGLFileConfiguration configuration, StringBuffer fileContent)  {
        //configuration.setFPackage(strPkg);
        super(configuration);   
        fContent = fileContent;
    }

	protected String getFileContents() {
		return fContent.toString();		
	}
}

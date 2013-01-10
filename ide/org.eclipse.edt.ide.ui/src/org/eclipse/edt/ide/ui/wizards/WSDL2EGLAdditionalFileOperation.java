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

import java.util.Enumeration;
import java.util.Hashtable;


public class WSDL2EGLAdditionalFileOperation extends EGLFileOperation {
    public static final String ADDITIONALDATAFILE_APPENDNAME = "_Data";  //$NON-NLS-1$
	
	/**
	 * the key is the dataitem name, the value is the dataitem definition stringBuffer
	 */
	Hashtable<String,StringBuffer> HashDataItemDefStringBuffer;
    
    /**
     * @param configuration
     */
    public WSDL2EGLAdditionalFileOperation(EGLFileConfiguration configuration, Hashtable<String,StringBuffer> dataItemStringBuffer) {
        //configuration.setFPackage(strPkg);
        super(configuration);
        this.HashDataItemDefStringBuffer = dataItemStringBuffer;               
    }
    
	protected String getFileContents() {
	    StringBuffer strDataDef = new StringBuffer();
	        	    
	    //this is dataitem definition
	    //get a enum of the values in the Hashtable
	    Enumeration<StringBuffer> enumx = HashDataItemDefStringBuffer.elements();
	    while(enumx.hasMoreElements()) {
	        StringBuffer strbuf = enumx.nextElement();
	        strDataDef.append(strbuf);
	        strDataDef.append(newLine);
	        strDataDef.append(newLine);
	    }	  
	    
	    return strDataDef.toString();
	}    
}

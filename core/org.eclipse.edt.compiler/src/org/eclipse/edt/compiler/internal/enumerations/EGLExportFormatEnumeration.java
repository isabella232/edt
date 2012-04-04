/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.enumerations;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.edt.compiler.internal.IEGLConstants;



/**
 * @author svihovec
 *
 */
public class EGLExportFormatEnumeration extends EGLEnumeration {

    private static final EGLExportFormatEnumeration INSTANCE = new EGLExportFormatEnumeration();
    
    public static final EGLEnumerationValue CSV = new EGLEnumerationValue(IEGLConstants.MNEMONIC_CSV, IEGLConstants.EXPORTFORMAT_HTML);
    public static final EGLEnumerationValue HTML = new EGLEnumerationValue(IEGLConstants.MNEMONIC_HTML, IEGLConstants.EXPORTFORMAT_HTML);
    public static final EGLEnumerationValue PDF = new EGLEnumerationValue(IEGLConstants.MNEMONIC_PDF, IEGLConstants.EXPORTFORMAT_PDF);
    public static final EGLEnumerationValue TEXT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_TEXT, IEGLConstants.EXPORTFORMAT_TEXT);
    public static final EGLEnumerationValue XML = new EGLEnumerationValue(IEGLConstants.MNEMONIC_XML, IEGLConstants.EXPORTFORMAT_XML);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(CSV.getName().toUpperCase().toLowerCase(), CSV);
        valuesMap.put(HTML.getName().toUpperCase().toLowerCase(), HTML);
        valuesMap.put(PDF.getName().toUpperCase().toLowerCase(), PDF);
        valuesMap.put(TEXT.getName().toUpperCase().toLowerCase(), TEXT);
        valuesMap.put(XML.getName().toUpperCase().toLowerCase(), XML);
    }
    
    private EGLExportFormatEnumeration() {}
    
    public static EGLExportFormatEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return EXPORTFOMAT_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_EXPORTFORMAT;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getValue(java.lang.String)
     */
    public EGLEnumerationValue getValue(String valueName) {
        return (EGLEnumerationValue)valuesMap.get(valueName.toUpperCase().toLowerCase());
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getValues()
     */
    public Collection getValues() {
        return valuesMap.values();
    }
	public boolean isResolvable() {
		return true;
	}

}

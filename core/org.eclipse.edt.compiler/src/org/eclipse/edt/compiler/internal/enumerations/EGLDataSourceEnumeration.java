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
package org.eclipse.edt.compiler.internal.enumerations;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.edt.compiler.internal.IEGLConstants;



/**
 * @author svihovec
 *
 */
public class EGLDataSourceEnumeration extends EGLEnumeration {

    private static final EGLDataSourceEnumeration INSTANCE = new EGLDataSourceEnumeration();
    
    public static final EGLEnumerationValue DATABASECONNECTION = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DATABASECONNECTION, IEGLConstants.DATASOURCE_DATABASECONNECTION);
    public static final EGLEnumerationValue REPORTDATA = new EGLEnumerationValue(IEGLConstants.MNEMONIC_REPORTDATA, IEGLConstants.DATASOURCE_REPORTDATA);
    public static final EGLEnumerationValue SQLSTATEMENT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_SQLSTATEMENT, IEGLConstants.DATASOURCE_SQLSTATEMENT);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(DATABASECONNECTION.getName().toUpperCase().toLowerCase(), DATABASECONNECTION);
        valuesMap.put(REPORTDATA.getName().toUpperCase().toLowerCase(), REPORTDATA);
        valuesMap.put(SQLSTATEMENT.getName().toUpperCase().toLowerCase(), SQLSTATEMENT);
    }
    
    private EGLDataSourceEnumeration() {}
    
    public static EGLDataSourceEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return DATASOURCE_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_DATASOURCE;
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

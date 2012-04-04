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
package org.eclipse.edt.compiler.internal;

import java.util.HashMap;

/**
 * @author svihovec
 *
 */
public class EGLStrLib extends EGLLib {

    private static final EGLStrLib INSTANCE = new EGLStrLib();
    
    public static class EGLStrLibConstant extends EGLLibConstant{
       
        public static final int DATE_TYPE = 0;
        public static final int TIME_TYPE = 1;
        public static final int TIMESTAMP_TYPE = 2;
        public static final int FILLCHARACTER_TYPE = 3;
        
        private int type;
        private int defaultFieldLength;
        
        public EGLStrLibConstant(String constantName, int type, int defaultFieldLength){
            super(constantName);
            this.type = type;
            this.defaultFieldLength = defaultFieldLength;
        }
        
        /**
         * @return Returns the defaultFieldLength.
         */
        public int getDefaultFieldLength() {
            return defaultFieldLength;
        }
        
        /**
         * @return Returns the type.
         */
        public int getType() {
            return type;
        }
    }
    
    public static final EGLStrLibConstant ISO_DATE_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_ISODATEFORMAT, EGLStrLibConstant.DATE_TYPE, 10);
    public static final EGLStrLibConstant EUR_DATE_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_EURDATEFORMAT, EGLStrLibConstant.DATE_TYPE, 10);
    public static final EGLStrLibConstant JIS_DATE_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_JISDATEFORMAT, EGLStrLibConstant.DATE_TYPE, 10);
    public static final EGLStrLibConstant USA_DATE_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_USADATEFORMAT, EGLStrLibConstant.DATE_TYPE, 10);
    public static final EGLStrLibConstant DEFAULT_DATE_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_DEFAULTDATEFORMAT, EGLStrLibConstant.DATE_TYPE, 10); 
    public static final EGLStrLibConstant ISO_TIME_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_ISOTIMEFORMAT, EGLStrLibConstant.TIME_TYPE, 8);
    public static final EGLStrLibConstant EUR_TIME_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_EURTIMEFORMAT, EGLStrLibConstant.TIME_TYPE, 8);
    public static final EGLStrLibConstant JIS_TIME_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_JISTIMEFORMAT, EGLStrLibConstant.TIME_TYPE, 8);
    public static final EGLStrLibConstant USA_TIME_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_USATIMEFORMAT, EGLStrLibConstant.TIME_TYPE, 8);
    public static final EGLStrLibConstant DEFAULT_TIME_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_DEFAULTTIMEFORMAT, EGLStrLibConstant.TIME_TYPE, 8);
    public static final EGLStrLibConstant DB2_TIMESTAMP_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_DB2TIMESTAMPFORMAT, EGLStrLibConstant.TIMESTAMP_TYPE, 26);
    public static final EGLStrLibConstant ODBC_TIMESTAMP_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_ODBCTIMESTAMPFORMAT, EGLStrLibConstant.TIMESTAMP_TYPE, 26);
    public static final EGLStrLibConstant DEFAULT_TIMESTAMP_FORMAT = new EGLStrLibConstant(IEGLConstants.MNEMONIC_DEFAULTTIMESTAMPFORMAT, EGLStrLibConstant.TIMESTAMP_TYPE, 26);
    public static final EGLStrLibConstant NULL_FILL = new EGLStrLibConstant(IEGLConstants.MNEMONIC_NULLFILL, EGLStrLibConstant.FILLCHARACTER_TYPE, 0);
    
//    private static final EGLStrLibConstant[] constants = {ISO_DATE_FORMAT, EUR_DATE_FORMAT, JIS_DATE_FORMAT, USA_DATE_FORMAT, DEFAULT_DATE_FORMAT, ISO_TIME_FORMAT, EUR_TIME_FORMAT, JIS_TIME_FORMAT, USA_TIME_FORMAT, DEFAULT_TIME_FORMAT, DB2_TIMESTAMP_FORMAT, ODBC_TIMESTAMP_FORMAT, DEFAULT_TIMESTAMP_FORMAT, NULL_FILL};
    private static final HashMap constantsMap = new HashMap();
    
    static{
        constantsMap.put(IEGLConstants.MNEMONIC_ISODATEFORMAT.toUpperCase().toLowerCase(), ISO_DATE_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_EURDATEFORMAT.toUpperCase().toLowerCase(), EUR_DATE_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_USADATEFORMAT.toUpperCase().toLowerCase(), USA_DATE_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_JISDATEFORMAT.toUpperCase().toLowerCase(), JIS_DATE_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_DEFAULTDATEFORMAT.toUpperCase().toLowerCase(), DEFAULT_DATE_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_ISOTIMEFORMAT.toUpperCase().toLowerCase(), ISO_TIME_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_EURTIMEFORMAT.toUpperCase().toLowerCase(), EUR_TIME_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_JISTIMEFORMAT.toUpperCase().toLowerCase(), JIS_TIME_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_USATIMEFORMAT.toUpperCase().toLowerCase(), USA_TIME_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_DEFAULTTIMEFORMAT.toUpperCase().toLowerCase(), DEFAULT_TIME_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_DB2TIMESTAMPFORMAT.toUpperCase().toLowerCase(), DB2_TIMESTAMP_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_ODBCTIMESTAMPFORMAT.toUpperCase().toLowerCase(), ODBC_TIMESTAMP_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_DEFAULTTIMESTAMPFORMAT.toUpperCase().toLowerCase(), DEFAULT_TIMESTAMP_FORMAT);
        constantsMap.put(IEGLConstants.MNEMONIC_NULLFILL.toUpperCase().toLowerCase(), NULL_FILL);
    }
    
    public static final EGLStrLibConstant[] timeConstants = {ISO_TIME_FORMAT, EUR_TIME_FORMAT, JIS_TIME_FORMAT, USA_TIME_FORMAT, DEFAULT_TIME_FORMAT};
    public static final EGLStrLibConstant[] dateConstants = {ISO_DATE_FORMAT, EUR_DATE_FORMAT, USA_DATE_FORMAT, JIS_DATE_FORMAT, DEFAULT_DATE_FORMAT};
    public static final EGLStrLibConstant[] timeStampConstants = {DB2_TIMESTAMP_FORMAT, ODBC_TIMESTAMP_FORMAT, DEFAULT_TIMESTAMP_FORMAT};
    
    private static final String LIBRARY_NAME = IEGLConstants.KEYWORD_STRLIB;
    
    private EGLStrLib(){
    }
    
    public static EGLStrLib getInstance(){
        return INSTANCE;
    }
    
    /**
     * Given a string reference to a strlib constant, attempt to locate the appropriate constant.
     * 
     * A constant will be located if it the string contains only the name of the constant (i.e. "isoDateFormat"),
     * if it is qualified with the library name (i.e. "strlib.isoDateFormat"), or if it is qualified with the
     * package and library name (i.e. "egl.core.strlib.isoDateFormat").  
     * 
     * All comparisons are case insensitive.
     *  
     * @param resolveString
     * @return null if a constant could not be resolved
     */
    public EGLStrLibConstant resolve(String resolveString){
        
        return (EGLStrLibConstant)super.resolve(LIBRARY_NAME, resolveString);
     }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.EGLLib#getConstant(java.lang.String)
     */
    public EGLLibConstant getConstant(String name) {
        return (EGLLibConstant)constantsMap.get(name.toUpperCase().toLowerCase());
    }
}

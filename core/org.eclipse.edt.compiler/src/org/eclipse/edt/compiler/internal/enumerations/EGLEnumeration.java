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

import org.eclipse.edt.compiler.internal.IEGLConstants;


/**
 * @author svihovec
 *
 */
public abstract class EGLEnumeration {
    
    public static final String ALIGNKIND_STRING = "AlignKind";
    public static final String BOOLEAN_STRING = "Boolean";
    public static final String CALLINGCONVENTIONKIND_STRING = "CallingConventionKind";
    public static final String CASEFORMATKIND_STRING = "CaseFormatKind";
    public static final String COLORKIND_STRING = "ColorKind";
    public static final String DATASOURCE_STRING = "DataSource";
    public static final String DEVICETYPEKIND_STRING = "DeviceTypeKind";
    public static final String DISPLAYUSEKIND_STRING = "DisplayUseKind";
    public static final String ENCODINGKIND_STRING = "EncodingKind";
    public static final String EVENTKIND_STRING = "EventKind";
    public static final String EXPORTFOMAT_STRING = "ExportFormat";
    public static final String HIGHLIGHTKIND_STRING = "HighlightKind";
    public static final String INDEXORIENTATIONKIND_STRING = "IndexOrientationKind";
    public static final String INTENSITYKIND_STRING = "IntensityKind";
    public static final String OUTLINEKIND_STRING = "OutlineKind";
    public static final String PFKEYKIND_STRING = "PfKeyKind";
    public static final String PROTECTKIND_STRING = "ProtectKind";
    public static final String SELECTTYPEKIND_STRING = "SelectTypeKind";
    public static final String SIGNKIND_STRING = "SignKind";
    public static final String LINEWRAPKIND_STRING = "LineWrapKind";
    public static final String UITypeKind_STRING = "UITypeKind";
    public static final String COMMTYPEKIND_STRING = "CommTypeKind";
    public static final String WINDOWATTRIBUTEKIND_STRING = "WindowAttributeKind";

    private static EGLEnumeration[] enumerations = null;
    
    private static void initializeEnumerations() {
    	enumerations = new EGLEnumeration[25];
    	enumerations[0] = EGLAlignKindEnumeration.getInstance();
    	enumerations[1] = EGLBooleanEnumeration.getInstance();
    	enumerations[2] = EGLCallingConventionKindEnumeration.getInstance();
    	enumerations[3] = EGLCaseFormatKindEnumeration.getInstance();
    	enumerations[4] = EGLColorKindEnumeration.getInstance();
    	enumerations[5] = EGLDataSourceEnumeration.getInstance();
    	enumerations[6] = EGLDeviceTypeKindEnumeration.getInstance();
    	enumerations[7] = EGLDisplayUseKindEnumeration.getInstance();
    	enumerations[8] = EGLEventKindEnumeration.getInstance();
    	enumerations[9] = EGLExportFormatEnumeration.getInstance();
    	enumerations[10] = EGLHighlightKindEnumeration.getInstance();
    	enumerations[11] = EGLIndexOrientationKindEnumeration.getInstance();
    	enumerations[12] = EGLIntensityKindEnumeration.getInstance();
    	enumerations[13] = EGLOutlineKindEnumeration.getInstance();
    	enumerations[14] = EGLPFKeyKindEnumeration.getInstance();
    	enumerations[15] = EGLProtectKindEnumeration.getInstance();
    	enumerations[16] = EGLSelectTypeKindEnumeration.getInstance();
    	enumerations[17] = EGLSignKindEnumeration.getInstance();
    	enumerations[18] = EGLLineWrapKindEnumeration.getInstance();
    	enumerations[20] = EGLUITypeKindEnumeration.getInstance();
    	enumerations[21] = EGLCommTypeKindEnumeration.getInstance();
    	enumerations[23] = EGLWindowAttributeKindEnumeration.getInstance();
    	enumerations[24] = EGLEncodingKindEnumeration.getInstance();
    }

	public static EGLEnumeration[] getEnumerations() {
		if (enumerations == null) {
			initializeEnumerations();
		}
		return enumerations;
	}
    
    public static class EGLEnumerationValue {
        
        private final String name;
        private final int type;
        
        public EGLEnumerationValue(String enumValueName, int type) {
            this.name = enumValueName;
            this.type = type;
        }
        
        public String getName(){
            return name;
        }
        
        public int getType(){
            return type;
        }
    }
    
    public abstract String getName();
    public abstract int getType();
    public abstract EGLEnumerationValue getValue(String valueName);
    public abstract Collection getValues();
    public abstract boolean isResolvable();

    public EGLEnumerationValue resolve(String resolveString){
        return resolve(getName(), resolveString);
    }
    private EGLEnumerationValue resolve(String enumString, String resolveString) {
        
        EGLEnumerationValue foundConstant = null;
        
        if(resolveString != null){
            boolean shouldResolveString = false;
	        String tempResolveString = resolveString;
	        	        
	        // If it's qualified, verify that the qualifier is egl.core."enumString" or "enumString"
	        if(resolveString.indexOf(IEGLConstants.PACKAGE_SEPARATOR) != -1){
	            int lastSeparatorIndex = resolveString.lastIndexOf(IEGLConstants.PACKAGE_SEPARATOR);
	            
	            String qualifier = resolveString.substring(0, lastSeparatorIndex);
	            
	            if(qualifier.equalsIgnoreCase(enumString)
	                    || qualifier.equalsIgnoreCase(IEGLConstants.EGL_CORE_PACKAGE + IEGLConstants.PACKAGE_SEPARATOR + enumString)){
	                
	                shouldResolveString = true;
	                tempResolveString = resolveString.substring(lastSeparatorIndex + 1);
	            }
	        }else{
	            shouldResolveString = true;
	            tempResolveString = resolveString;
	        }
	        
	        if(shouldResolveString){
	            foundConstant = (EGLEnumerationValue)getValue(tempResolveString);
		    }
        }
        
        return foundConstant;
    }
}

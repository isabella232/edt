/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.sql.util;


import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IRecordBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;


public class SQLUtility {
    
    public static final String[] EGLIOSQL = new String[] {"egl", "io", "sql"};

    public static boolean containsOnlyKeyOrReadOnlyColumns(IDataBinding sqlRecordData, String[][] usingKeyItemAndColumnNames) {

        // Check the columns in the I/O object (SQL record) to see if there are
        // only key or read only columns.

        if (!isValid(sqlRecordData)) {
            return false;
        }

        IDataBinding[] sqlDataItems = getFields(sqlRecordData);

        IDataBinding itemBinding;
        for (int i = 0; i < sqlDataItems.length; i++) {
            itemBinding = sqlDataItems[i];
            if (getIsReadOnly(itemBinding, sqlRecordData) || isUsingKeyColumn(itemBinding, usingKeyItemAndColumnNames, sqlRecordData)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isUsingKeyColumn(IDataBinding itemBinding, String[][] usingKeyItemAndColumnNames, IDataBinding recordData) {
        for (int i = 0; i < usingKeyItemAndColumnNames.length; i++) {
            if (getColumnName(itemBinding, recordData).equalsIgnoreCase(usingKeyItemAndColumnNames[i][1])) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsOnlyKeyOrReadOnlyColumns(IDataBinding sqlRecordData, IDataBinding[] recordKeyItems) {

        // Check the columns in the I/O object (SQL record) to see if there are
        // only key or read only columns.

        if (!isValid(sqlRecordData)) {
            return false;
        }

        IDataBinding[] sqlDataItems = getFields(sqlRecordData);

        IDataBinding itemBinding;
        for (int i = 0; i < sqlDataItems.length; i++) {
            itemBinding = sqlDataItems[i];
            if (getIsReadOnly(itemBinding, sqlRecordData) || isRecordKey(itemBinding, recordKeyItems)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isRecordKey(IDataBinding dataBinding, IDataBinding[] keyItems) {

        for (int i = 0; i < keyItems.length; i++) {
            if (dataBinding == keyItems[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasReadWriteColumns(IDataBinding sqlRecordData) {
        // Return true if the I/O object (SQL record) has any read/write
        // columns. Otherwise, return false.

        if (!isValid(sqlRecordData)) {
            return false;
        }

        IDataBinding[] sqlDataItems = getFields(sqlRecordData);

        IDataBinding itemBinding;

        // Check the structure items in the I/O object (SQL record) to see
        // if there are any read/write columns.
        for (int i = 0; i < sqlDataItems.length; i++) {
            itemBinding = sqlDataItems[i];
            if (!getIsReadOnly(itemBinding, sqlRecordData)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSQLIOType(String ioType) {
        if (ioType != null) {
            for (int i = 0; i < SQLConstants.SQL_IO_TYPE_STRINGS.length; i++) {
                if (SQLConstants.SQL_IO_TYPE_STRINGS[i].equalsIgnoreCase(ioType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSQLRecordDefinedWithMultipleTables(IDataBinding sqlRecordData) {
        // Return true if the I/O object (SQL record) is defined with more than
        // one table. Otherwise, return false.

        if (!isValid(sqlRecordData)) {
            return false;
        }

        int numTables = 0;
        int numTableVars = 0;

        IAnnotationBinding annotation = getField(sqlRecordData.getAnnotation(EGLIOSQL, "SQLRecord"), IEGLConstants.PROPERTY_TABLENAMES);
        if (annotation != null) {
            String[][] tableNames = (String[][]) annotation.getValue();
            if (tableNames != null) {
                numTables = tableNames.length;
                if (numTables > 1) {
                    return true;
                }
            }
        }

        annotation = getField(sqlRecordData.getAnnotation(EGLIOSQL, "SQLRecord"), IEGLConstants.PROPERTY_TABLENAMEVARIABLES);
        if (annotation != null) {
            String[][] tableNameVars = (String[][]) annotation.getValue();
            if (tableNameVars != null) {
                numTableVars = tableNameVars.length;
                if (numTableVars > 1) {
                    return true;
                }
            }
        }

        return (numTables + numTableVars > 1);
    }
    
    public static boolean isSQLRecord(IDataBinding sqlRecordData) {
        if (!isValid(sqlRecordData)) {
            return false;
        }
        if (sqlRecordData.getAnnotation(EGLIOSQL, "SQLRecord") != null) {
           return true;
        }
        return false;
    }
    
    public static boolean isSQLRecordPart(Record record) {
    	if(record.hasSubType() && record.getSubType().getIdentifier() == InternUtil.intern("SQLRecord")) {
			return true;
    	}
    	return false;
    }

    public static String getColumnName(IDataBinding itemBinding, IDataBinding recordData) {
        IAnnotationBinding annotation = recordData.getAnnotationFor(EGLIOSQL, "Column",
                new IDataBinding[] { itemBinding });
        if (annotation != null) {
            return (String) annotation.getValue();
        } else {
            return itemBinding.getName();
        }
    }

    public static boolean getIsReadOnly(IDataBinding itemBinding, IDataBinding recordData) {
        IAnnotationBinding annotation = recordData.getAnnotationFor(EGLIOSQL, "IsReadOnly",
                new IDataBinding[] { itemBinding });

        if (annotation != null) {
            if (annotation.getValue() == Boolean.YES) {
                return true;
            }
        }

        return isSQLRecordDefinedWithMultipleTables(recordData);

    }

    public static boolean isValid(IDataBinding dataBinding) {
        if (dataBinding == null || dataBinding == IBinding.NOT_FOUND_BINDING) {
            return false;
        }
        if (dataBinding.getType() == null || dataBinding.getType() == IBinding.NOT_FOUND_BINDING) {
            return false;
        }
        if (dataBinding.getType().getBaseType() == null || dataBinding.getType().getBaseType() == IBinding.NOT_FOUND_BINDING) {
            return false;
        }
        return true;
    }

    public static IDataBinding[] getFields(IDataBinding dataBinding) {
        if (isValid(dataBinding)) {
            ITypeBinding typeBinding = dataBinding.getType().getBaseType();
	        if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
	            return ((IRecordBinding) typeBinding).getFields();
	        }
        }
        return new IDataBinding[0];
    }
    
    public static String getColumnName(Expression expr) {
        //TODO this must be changed to get the property based on the variable container
        IDataBinding data = expr.resolveDataBinding();
        if (data != null && data != IBinding.NOT_FOUND_BINDING) {
            IAnnotationBinding annotation = data.getAnnotation(EGLIOSQL, "Column");
            if (annotation != null) {
                return (String) annotation.getValue();
            }
            return data.getName();
        }
        return null;
    }
    
    public static IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
    	IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
}

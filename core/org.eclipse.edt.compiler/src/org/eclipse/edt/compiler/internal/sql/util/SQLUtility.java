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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IRecordBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NotFoundBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class SQLUtility {
    
    public static final String[] EGLIOSQL = new String[] {"egl", "io", "sql"};
    
    public static final String[] EGLXSQL = new String[] {"eglx", "persistence", "sql"};
    public static final String[] EGLXPERSISTENCE = new String[] {"eglx", "persistence"};

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

        if(sqlRecordData.getAnnotation(EGLIOSQL, "SQLRecord") == null) {
        	return false;
        }
        
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
    
    public static boolean isEntityRecord(IDataBinding sqlRecordData) {
        if (!isValid(sqlRecordData)) {
            return false;
        }
        
        IAnnotationBinding annotation = null;
        
        ITypeBinding typeBinding = sqlRecordData.getType();
        if(typeBinding != null && typeBinding.isReference()) {
        	ITypeBinding baseTypeBinding = typeBinding.getBaseType();
        	annotation  = baseTypeBinding.getAnnotation(SQLUtility.EGLXPERSISTENCE, IEGLConstants.PROPERTY_ENTITY);
        } else {
        	 annotation  = typeBinding.getAnnotation(SQLUtility.EGLXPERSISTENCE, IEGLConstants.PROPERTY_ENTITY);
        }
        
        if(annotation != null) {
         	return true;
         }
        
        return false;
    }
    
    public static boolean isBasicRecord(IDataBinding sqlRecordData) {
        if (!isValid(sqlRecordData)) {
            return false;
        }
        
        ITypeBinding typeBinding = sqlRecordData.getType();
        if(typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING ) {
        	typeBinding = ((ArrayTypeBinding) typeBinding).getElementType();
        }
        boolean isValid = typeBinding.isTypeBinding() 
        		&& (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING 
        		      || (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING));
        return isValid;
    }
    
    public static boolean isSQLRecordPart(Record record) {
    	if(record.hasSubType() && record.getSubType().getIdentifier() == InternUtil.intern("SQLRecord")) {
			return true;
    	}
    	return false;
    }

    public static String getColumnName(IDataBinding itemBinding, IDataBinding recordData) {
        IAnnotationBinding annotation = itemBinding.getAnnotation(EGLXSQL, IEGLConstants.PROPERTY_COLUMN);
        if (annotation != null) {
        	IAnnotationBinding columnNameAnnotation= (IAnnotationBinding)annotation.findData(IEGLConstants.PROPERTY_NAME);
        	if(!(columnNameAnnotation instanceof NotFoundBinding)) {
               return (String) columnNameAnnotation.getValue();
        	} else {
        	   return itemBinding.getName();
        	}
        } else {
            return itemBinding.getName();
        }
    }

    /**
     * Check whether a column is insertable or not
     * true: not insertable
     * false: insertable
     */
    public static boolean getIsReadOnly(IDataBinding itemBinding, IDataBinding recordData) {
    	boolean isReadOnly = false;
    	IAnnotationBinding annotation = itemBinding.getAnnotation(SQLUtility.EGLXSQL, IEGLConstants.PROPERTY_GENERATEDVALUE);
    
        if (annotation != null) {
        	isReadOnly = true;
        } else {
        	IAnnotationBinding insertableAnnotation = getColumnAnnotation(itemBinding,IEGLConstants.PROPERTY_INSERTABLE);
        	if(insertableAnnotation != null && !(insertableAnnotation instanceof NotFoundBinding)) {
    	    	 if (insertableAnnotation.getValue() == Boolean.NO) {
    	    		 isReadOnly = true;
    	         }
    	    }
        }

        return isReadOnly;
    }
    
    public static boolean getIsUpdateable(IDataBinding itemBinding) {
    	boolean isUpdateable = true;
    	
    	IAnnotationBinding updateableAnnotation = getColumnAnnotation(itemBinding,IEGLConstants.PROPERTY_UPDATEABLE);
    	if(updateableAnnotation != null && !(updateableAnnotation instanceof NotFoundBinding)) {
	    	 if (updateableAnnotation.getValue() == Boolean.NO) {
	    		 isUpdateable = false;
	         }
	    }
    	
    	return isUpdateable;
    }
    
    private static IAnnotationBinding getColumnAnnotation(IDataBinding itemBinding, String annotationName) {
    	IAnnotationBinding columnAnnotation = null;
    	
    	IAnnotationBinding annotation = itemBinding.getAnnotation(SQLUtility.EGLXSQL, IEGLConstants.PROPERTY_COLUMN);
    	if(annotation != null) {
    		columnAnnotation = (IAnnotationBinding) annotation.findData(annotationName);
    	}
    	
    	return columnAnnotation;
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
    
    public static IDataBinding[] getFields(ITypeBinding typeBinding) {
    	if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
            return ((IRecordBinding) typeBinding).getFields();
        } else {
        	return new IDataBinding[0];
        }
    }
    
    public static String getTableName(ITypeBinding typeBinding) {
    	if(typeBinding != null) {
    		ITypeBinding elementTypeBinding = null;
    		if(typeBinding.isReference()) {
    			elementTypeBinding = typeBinding.getBaseType();
    		} else {
    			elementTypeBinding = typeBinding;
    		}
    		IAnnotationBinding annotation = elementTypeBinding.getAnnotation(SQLUtility.EGLXSQL, IEGLConstants.PROPERTY_TABLE);
        	if (annotation != null) {
    			IAnnotationBinding tableNameAnnotation = (IAnnotationBinding) annotation.findData(IEGLConstants.PROPERTY_NAME);
    			return (String) tableNameAnnotation.getValue();
    		}
    	}
    	
    	return null;
    }
    
    public static String getIdColumnName(ITypeBinding typeBinding) {
    	if(typeBinding != null) {
    		IDataBinding[] fields = getFields(typeBinding);
    		for(IDataBinding field : fields) {
    			IAnnotationBinding annotation = field.getAnnotation(SQLUtility.EGLXPERSISTENCE, IEGLConstants.PROPERTY_ID);
    			if (annotation != null) {
    				annotation = field.getAnnotation(SQLUtility.EGLXPERSISTENCE, IEGLConstants.PROPERTY_COLUMN);
    				if(annotation != null) {
    					IAnnotationBinding tableNameAnnotation = (IAnnotationBinding) annotation.findData(IEGLConstants.PROPERTY_NAME);
            			return (String) tableNameAnnotation.getValue();
    				} else {
    					return field.getCaseSensitiveName();
    				}
        			
        		}
    		}
    	}
    	
    	return null;
    }
    
    //The map key refers to type binding of master table
    //The map value refers to name of foreign key of slave table
    public static Map<ITypeBinding,String> getForeignKeys(IDataBinding dataBinding) {
    	Map<ITypeBinding,String> foreignKeys = null;
    	IDataBinding[] fieldBindings = getFields(dataBinding);
    	
    	for(IDataBinding fieldBinding : fieldBindings) {
    		String foreignKey = getForeignKeyName(fieldBinding);
    		if(foreignKey != null) {
    			if(foreignKeys == null) {
    				foreignKeys = new HashMap<ITypeBinding,String>();
    			}
    			if(fieldBinding.getType().getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
    				foreignKeys.put(dataBinding.getDeclaringPart(), foreignKey);
    			} else {
    				foreignKeys.put(fieldBinding.getDeclaringPart(), foreignKey);
    			}
    		}
    	}
    	
    	return foreignKeys;
    }
    
    private static String getForeignKeyName(IDataBinding fieldBinding) {
    	String columnName = null;
    	 IAnnotationBinding annotation = fieldBinding.getAnnotation(EGLXPERSISTENCE, "ManyToOne");
    	 if(annotation != null) {
    		 /**
    		  * if typeBinding is PrimitiveTypeBinding, use entity field as column name;
    		  * Otherwise, get column name from JoinColumn annotation.
    		  */
    		 //TODO:
    		 columnName = fieldBinding.getCaseSensitiveName();
    	 }
    	
    	return columnName;
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
    
    public static boolean isTerraDataSupported() {
        String temp = System.getProperty("EGL_TERADATA", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        return ("true".equals(temp));
	}
}

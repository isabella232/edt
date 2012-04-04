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
package org.eclipse.edt.compiler.internal.sql.statements;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLParser;


public abstract class EGLSQLStatementFactory {
    
    private static final String[] EGLIOSQL = new String[] {"egl", "io", "sql"};

    String sqlStatement = null;

    protected int numSQLDataItems = 0;

    List sqlDataItems = null;

    IDataBinding[] structureItemBindings;

    String[] itemNames = null;

    String[] columnNames = null;

    String[] tableNames = null;

    String[] tableLabels = null;

    ArrayList errorMessages = new ArrayList();

    boolean invalidIoObject = false;

    boolean addIOObjectValidationErrorMessages = false;

    protected IDataBinding sqlRecordData;

    protected ITypeBinding sqlRecordType;

    protected ITypeBinding sqlRecordTypeBinding;

    protected String ioObjectName;

    IDataBinding[] keyItems;

    protected String[][] keyItemAndColumnNames;

	private ICompilerOptions compilerOptions;
	
	boolean useRecordKeys = true;


    public EGLSQLStatementFactory(ICompilerOptions compilerOptions) {
        super();
        this.compilerOptions = compilerOptions;
    }

    public EGLSQLStatementFactory(IDataBinding recordData, String ioObjectName, ICompilerOptions compilerOptions) {
        super();
        sqlRecordData = recordData;
        this.ioObjectName = ioObjectName;
        this.compilerOptions = compilerOptions;
    }

    public String buildDefaultSQLStatement() {
        return null;
    }

    protected boolean containsOnlyKeyOrReadOnlyColumns() {
        if (getSQLRecordTypeBinding() != null) {
            return SQLUtility.containsOnlyKeyOrReadOnlyColumns(sqlRecordData, getKeyItems());
        }
        return false;
    }
    
    private int getTableIndex(String tableName) {
    	int index = -1;
    	if(tableNames != null && tableName != null) {
    		for(int i=0; i < tableNames.length; i++) {
    			if(tableName.equals(tableNames[i])){
    				index = i;
    				break;
    			}
    		}
    	}
    	
    	return index;
    }

    protected String getDefaultSelectConditions() {

        String defaultSelectConditions = null;

        if ((SQLUtility.isEntityRecord(sqlRecordData) || SQLUtility.isBasicRecord(sqlRecordData))
        		&& tableNames.length > 0) {
        	Map<ITypeBinding,String> foreignKeys = SQLUtility.getForeignKeys(sqlRecordData);
        	String masterTableName = null;
        	String slaveTableName = SQLUtility.getTableName(sqlRecordData.getType());
        	int slaveIndex = getTableIndex(slaveTableName);
        	 
        	if(slaveTableName!=null && slaveIndex!=-1 && foreignKeys != null) {
        		for(ITypeBinding masterTable : foreignKeys.keySet()) {
        			masterTableName = SQLUtility.getTableName(masterTable);

					if (masterTableName != null) {
						int tableIndex = getTableIndex(masterTableName);
						if(tableIndex != -1) {
							defaultSelectConditions = tableLabels[slaveIndex] + SQLConstants.QUALIFICATION_DELIMITER
									+ foreignKeys.get(masterTable) + "=" 
									+ tableLabels[tableIndex] + SQLConstants.QUALIFICATION_DELIMITER
									+ SQLUtility.getIdColumnName(masterTable)
									+ SQLConstants.SPACE + SQLConstants.AND + SQLConstants.SPACE
									+ tableLabels[tableIndex] + SQLConstants.QUALIFICATION_DELIMITER
									+ SQLUtility.getIdColumnName(masterTable)
									+ "=" + SQLConstants.PARAMETER_MARKER;
						}
					}
        		}
        	}
        }

        // If no default select conditions are defined or if it is empty,
        if (defaultSelectConditions == null) {
            return null;
        } else {
            defaultSelectConditions = defaultSelectConditions.trim();
            if (defaultSelectConditions.length() == 0) {
                return null;
            }
        }

        // Call the EGL SQL parser to remove all SQL comments.
        defaultSelectConditions = new EGLSQLParser(defaultSelectConditions, "ANY", compilerOptions).getAllClauses(); //$NON-NLS-1$

        // Trim leading CR's.
        defaultSelectConditions = trimLeadingChar(defaultSelectConditions, '\r');
        // Trim leading LF's.
        defaultSelectConditions = trimLeadingChar(defaultSelectConditions, '\n');
        // Trim leading blanks.
        defaultSelectConditions = trimLeadingChar(defaultSelectConditions, ' ');

        if (defaultSelectConditions.trim().length() == 0)
            return null;
        else
            return defaultSelectConditions;
    }

    public String trimLeadingChar(String s, char charToTrim) {
        char[] c = s.toCharArray();
        int length = c.length;
        int start = 0;

        while ((start < length) && (c[start] == charToTrim)) {
            start++;
        }
        return (start > 0) ? s.substring(start, length) : s;
    }

    /**
     * Replaces every occurrance of <code>s</code> in <code>str</code> with
     * <code>replacement</code>.
     * 
     * @param str
     *            the String to process.
     * @param s
     *            the String to replace.
     * @param replacement
     *            the String to use in place of <code>s</code>.
     * @return the string with replacements.
     */
    private String replaceString(String str, String s, String replacement) {
        int strLength = str.length();
        StringBuffer buffer = new StringBuffer(strLength);

        int start = 0;
        int index = str.indexOf(s);
        while (index != -1) {
            // Add the characters before this one to the buffer.
            buffer.append(str.substring(start, index));
            start = index + s.length();

            // Add the replacement string to the buffer.
            buffer.append(replacement);

            index = str.indexOf(s, start);
        }

        // Add the characters from the end of the string to the buffer.
        if (start <= strLength) {
            buffer.append(str.substring(start, strLength));
        }

        return buffer.toString();
    }

    public ArrayList getErrorMessages() {
        return errorMessages;
    }

    public abstract String getIOType();

    public String getWhereCurrentOfClause() {
        // This is used only for replace and delete statements.
        return SQLConstants.WHERE_CURRENT_OF_CLAUSE;
    }

    protected boolean hasReadWriteColumns() {
        if (getSQLRecordTypeBinding() != null) {
            return SQLUtility.hasReadWriteColumns(sqlRecordData);
        }
        return true;
    }

    protected boolean isIoObjectValid() {

        //TODO must implement this
        return true;
    }

    protected boolean isSQLRecordDefinedWithMultipleTables() {
        return SQLUtility.isSQLRecordDefinedWithMultipleTables(sqlRecordData);
    }

    protected void setupItemColumnAndKeyInfo() {
    }

    protected boolean setupSQLInfo() {

        boolean isValidIoObject = true;

        //SQLUtility.isBasicRecord(dataBinding
        if (!SQLUtility.isEntityRecord(sqlRecordData) && !SQLUtility.isBasicRecord(sqlRecordData))
            isValidIoObject = false;
        else {
            // Need the list of SQL data items before checking to see if the I/O
            // object is valid.
            IDataBinding[] dataBindings = SQLUtility.getFields(sqlRecordData);
            IDataBinding itemBinding;
            structureItemBindings = new IDataBinding[dataBindings.length];
            for (int i = 0; i < dataBindings.length; i++) {
                itemBinding = dataBindings[i];
                if (shouldKeep(itemBinding)) {
                    structureItemBindings[numSQLDataItems] = itemBinding;
                    numSQLDataItems++;
                }
            }

            if (SQLUtility.isEntityRecord(sqlRecordData) && !isIoObjectValid())
                isValidIoObject = false;
        }

        if (!isValidIoObject) {
            errorMessages.add(0, getCouldNotBuildDefaultMessage());
            return false;
        }

        setupItemColumnAndKeyInfo();
        setupTableInfo();

        return true;
    }

    private boolean shouldKeep(IDataBinding dataBinding) {
        boolean persistentValue = true;

        if (!SQLUtility.isValid(dataBinding)) {
            return false;
        }

        IAnnotationBinding annotation = sqlRecordData.getAnnotationFor(EGLIOSQL, "Persistent",
                new IDataBinding[] { dataBinding });
        if (annotation != null) {
            persistentValue = annotation.getValue() == Boolean.YES;
        }

        if (!persistentValue) {
            return false;
        }

        ITypeBinding typeBinding = dataBinding.getType();
        if (typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING) {
            return false;
        }

        if (((PrimitiveTypeBinding) typeBinding).getPrimitive() == Primitive.ANY) {
            return false;
        }
        return true;

    }

    protected void setupTableInfo() {
    	List<String> sqlTables = new ArrayList<String>();
    	List<String> sqlTableLables = new ArrayList<String>();
    	int tableIndex = 1;
    	boolean haveTableAnnotation = false;
    	
        if (SQLUtility.isEntityRecord(sqlRecordData) || SQLUtility.isBasicRecord(sqlRecordData)) {
        	 List<ITypeBinding> declarers = new ArrayList<ITypeBinding>();
        	 declarers.add( getSQLRecordTypeBinding() );
        	 if(sqlRecordData.getDeclaringPart() != null 
        			 && ITypeBinding.FLEXIBLE_RECORD_BINDING == sqlRecordData.getDeclaringPart().getKind()
        			 && SQLConstants.GET_IO_TYPE.toUpperCase().equals(getIOType())) {
        		 declarers.add( sqlRecordData.getDeclaringPart() );
        	 }
        			
        	 
        	 for(ITypeBinding declarer : declarers) {
        		 IAnnotationBinding annotation = declarer.getAnnotation(SQLUtility.EGLXSQL, IEGLConstants.PROPERTY_TABLE);
        		 haveTableAnnotation = false;
        		 
        		 if (annotation != null) {
                 	IAnnotationBinding tableNameAnnotation= (IAnnotationBinding)annotation.findData(IEGLConstants.PROPERTY_NAME);
                 	if(tableNameAnnotation != null) {
                 		haveTableAnnotation = true;
                 		sqlTables.add((String) tableNameAnnotation.getValue());
                 		sqlTableLables.add("t" + tableIndex);
                 		tableIndex++;
                 	}
                 }
        		 
        		 if(!haveTableAnnotation) {
        			 if(declarer == sqlRecordTypeBinding) {
        				 sqlTables.add(getSQLRecordTypeBinding().getCaseSensitiveName());
        			 } else {
        				 sqlTables.add(declarer.getCaseSensitiveName());
        			 }
             		sqlTableLables.add("t" + tableIndex);
             		tableIndex++;
                 }
        	 }
        }
        
        tableNames = sqlTables.toArray(new String[0]);
    	tableLabels = sqlTableLables.toArray(new String[0]);
    }

    protected Problem getCouldNotBuildDefaultMessage() {
        return new Problem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.COULD_NOT_BUILD_DEFAULT_STATEMENT, new String[] {
                getSQLStatementType(), getIOType(), ioObjectName });
    }

    protected boolean validateSQLRecordContainsOneNonReadOnlyOrNonKeyColumn() {

        // SQL record must be defined with at least one column that is not a key
        // and not read only.
        if (containsOnlyKeyOrReadOnlyColumns()) {
            errorMessages.add(new Problem(0, 0, IMarker.SEVERITY_ERROR,
            	IProblemRequestor.IO_OBJECT_CONTAINS_ONLY_KEY_OR_READONLY_COLUMNS,
				new String[] { ioObjectName, getIOType() }));
            return false;
        }

        return true;
    }

    protected boolean validateSQLRecordNotJoin() {

        // SQL Record must be defined with only one table. Return false if a
        // join. True otherwise.
        if (isSQLRecordDefinedWithMultipleTables()) {
            errorMessages.add(new Problem(0, 0, IMarker.SEVERITY_ERROR,
            	IProblemRequestor.IO_OBJECT_IS_SQL_JOIN,
				new String[] {ioObjectName, getIOType()}));
            return false;
        }

        return true;
    }

    protected boolean validateSQLRecordNotJoinAndContainsOneNonReadOnlyOrNonKeyColumn() {

        // SQL record must not be defined with more than one table (join).
        boolean isValid = validateSQLRecordNotJoin();

        // SQL record must be defined with at least one column that is not a key
        // and not read only.
        if (!validateSQLRecordContainsOneNonReadOnlyOrNonKeyColumn()) {
            isValid = false;
        }

        return isValid;
    }

    public boolean getAddIOObjectValidationErrorMessages() {
        return addIOObjectValidationErrorMessages;
    }

    public void setAddIOObjectValidationErrorMessages(boolean addIOObjectValidationErrorMessages) {
        this.addIOObjectValidationErrorMessages = addIOObjectValidationErrorMessages;
    }

    public IDataBinding[] getKeyItems() {
        if (keyItems == null) {
            if (SQLUtility.isValid(sqlRecordData)) {
            	
            	IDataBinding[] dataBindings = SQLUtility.getFields(sqlRecordData);
            	IAnnotationBinding annotation = null;
            	List<IDataBinding> annoData = new ArrayList<IDataBinding>();
            	
            	for(IDataBinding dataBinding : dataBindings) {
            		annotation = dataBinding.getAnnotation(SQLUtility.EGLXPERSISTENCE, IEGLConstants.PROPERTY_ID);
                    if(annotation != null) {
                    	annoData.add(dataBinding);
                    }
            	}

                //IAnnotationBinding annotation = getField(sqlRecordData.getAnnotation(EGLIOSQL, "SQLRecord"), IEGLConstants.PROPERTY_KEYITEMS);
                if (annoData != null) {
                	//Object[] value = (Object[]) annotation.getValue();
                    keyItems = eliminateInvalid(annoData.toArray());
                }
           }

            if (keyItems == null) {
                keyItems = new IDataBinding[0];
            }
        }
        return keyItems;
    }
    
    private IDataBinding[] eliminateInvalid(Object[] bindings) {
        List list = new ArrayList();
        if (bindings != null) {
	        for (int i = 0; i < bindings.length; i++) {
	            if (SQLUtility.isValid((IDataBinding)bindings[i])) {
	                list.add(bindings[i]);
	            }
	        }
        }
        return (IDataBinding[])list.toArray(new IDataBinding[list.size()]);
    }

    protected boolean validateSQLRecordContainsReadWriteColumns() {

        // SQL record must be defined with at least one read/write column.
        // Return false if there are no read/write columns. True otherwise.
        if (!hasReadWriteColumns()) {
            errorMessages.add(new Problem(0, 0, IMarker.SEVERITY_ERROR,
            	IProblemRequestor.IO_OBJECT_CONTAINS_NO_READ_WRITE_COLUMNS,
				new String[] { ioObjectName, getIOType() }));
            return false;
        }

        return true;
    }

    protected boolean validateSQLRecordNotJoinAndContainsReadWriteColumns() {

        // SQL record must not be defined with more than one table (join).
        boolean isValid = validateSQLRecordNotJoin();

        // SQL record must be defined with at least one column that is not read
        // only.
        if (!validateSQLRecordContainsReadWriteColumns()) {
            isValid = false;
        }

        return isValid;
    }

    public void setupForSQLUpdateStatement() {

        // For update, need to create lists of items and columns that are
        // not readonly or keys.
        String[] items = new String[numSQLDataItems];
        String[] columns = new String[numSQLDataItems];
        int numNonReadOnlyAndKeys = 0;
        int numKeys = 0;
        String itemName;
        String columnName;
        
		if (keyItemAndColumnNames == null) {
			if (getKeyItems() != null) {
				keyItemAndColumnNames = new String[getKeyItems().length][2];
			} else {
				keyItemAndColumnNames = new String[0][0];
			}
		} else {
			useRecordKeys = false;
		}


        if (structureItemBindings != null) {
            IDataBinding itemBinding;
            boolean isUpdateable;
            for (int i = 0; i < numSQLDataItems; i++) {
                itemBinding = structureItemBindings[i];
                itemName = itemBinding.getName();
                columnName = getColumnName(itemBinding);
                isUpdateable = getIsUpdateable(itemBinding);
                if ((isUpdateable && !isRecordKeyItem(itemBinding))) {
                    items[numNonReadOnlyAndKeys] = itemName;
                    columns[numNonReadOnlyAndKeys] = columnName;
                    numNonReadOnlyAndKeys++;
                }
                if (useRecordKeys && isRecordKeyItem(itemBinding)) {
                    keyItemAndColumnNames[numKeys][0] = itemName;
                    keyItemAndColumnNames[numKeys][1] = columnName;
                    numKeys++;
                }
            }
        }

        if (numNonReadOnlyAndKeys != 0) {
            itemNames = new String[numNonReadOnlyAndKeys];
            columnNames = new String[numNonReadOnlyAndKeys];
            for (int i = 0; i < numNonReadOnlyAndKeys; i++) {
                itemNames[i] = items[i];
                columnNames[i] = columns[i];
            }
        }

        if (useRecordKeys && numKeys == 0 && getKeyItems().length > 0) {
            keyItemAndColumnNames = new String[0][0];
        }

    }

    protected boolean isRecordKeyItem(IDataBinding data) {
        return SQLUtility.isRecordKey(data, getKeyItems());
    }

    protected void setupForSQLInsertStatement() {

        // Create lists of items and columns that are not readonly. In this
        // case,
        // keys aren't treated as readonly. Add statements don't do anything
        // special
        // for keys so don't need to set up key info.
        String[] items = new String[numSQLDataItems];
        String[] columns = new String[numSQLDataItems];
        int numNonReadOnly = 0;

        if (structureItemBindings != null) {
            IDataBinding itemBinding;
            String itemName;
            String columnName;
            boolean isReadOnly;
            for (int i = 0; i < numSQLDataItems; i++) {
                itemBinding =  structureItemBindings[i];
                itemName = itemBinding.getName();
                columnName = getColumnName(itemBinding);
                isReadOnly = getIsReadOnly(itemBinding);
                if (!isReadOnly) {
                    items[numNonReadOnly] = itemName;
                    columns[numNonReadOnly] = columnName;
                    numNonReadOnly++;
                }
            }
        }

        if (numNonReadOnly != 0) {
            itemNames = new String[numNonReadOnly];
            columnNames = new String[numNonReadOnly];
            for (int i = 0; i < numNonReadOnly; i++) {
                itemNames[i] = items[i];
                columnNames[i] = columns[i];
            }
        }
    }

    public abstract String getSQLStatementType();
    public  String getEglUsingClause() {
    	return null;
    }

    protected ITypeBinding getSQLRecordTypeBinding() {
        if (sqlRecordTypeBinding == null && SQLUtility.isValid(sqlRecordData)) {
            sqlRecordTypeBinding = sqlRecordData.getType().getBaseType();
        }
        return sqlRecordTypeBinding;
    }

    protected String getColumnName(IDataBinding itemBinding) {
        return SQLUtility.getColumnName(itemBinding, sqlRecordData);
    }

    protected boolean getIsReadOnly(IDataBinding itemBinding) {
        return SQLUtility.getIsReadOnly(itemBinding, sqlRecordData);
    }
    
    protected boolean getIsUpdateable(IDataBinding itemBinding) {
        return SQLUtility.getIsUpdateable(itemBinding);
    }
    
    private static IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
    	IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}

}

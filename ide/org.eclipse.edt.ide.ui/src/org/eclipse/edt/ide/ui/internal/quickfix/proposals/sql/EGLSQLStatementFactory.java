package org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementUtility;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;

public abstract class EGLSQLStatementFactory {
	
	List errorMessages;
	
	protected Member sqlRecordData;
	
	protected Type sqlRecordTypeBinding;
	
	List<Field> structureItemBindings;
	
	protected int numSQLDataItems;
	
	protected String ioObjectName;
	
    String[] itemNames;

    String[] columnNames;

    String[] tableNames;
    
    String[] tableLabels;
    
    List<Field> keyItems;
    
    protected String[][] keyItemAndColumnNames;
	
	String sqlStatement;
	
	boolean useRecordKeys;
	
	public EGLSQLStatementFactory(Member recordData, String ioObjectName) {
        super();
        this.sqlRecordData = recordData;
        this.ioObjectName = ioObjectName;
        this.errorMessages = new ArrayList();
        this.useRecordKeys = true;
    }
	
	public String buildDefaultSQLStatement() {
        return null;
    }
	
	public  String getEglUsingClause() {
    	return null;
    }
	
	public List getErrorMessages() {
        return errorMessages;
    }
	
	protected boolean setupSQLInfo() {

        boolean isValidIoObject = true;

        if (!SQLIOStatementUtility.isEntityRecord(getSQLRecordTypeBinding()) && !SQLIOStatementUtility.isBasicRecord(getSQLRecordTypeBinding())) {
            isValidIoObject = false;
        }
        else {
            // Need the list of SQL data items before checking to see if the I/O
            // object is valid.
            List<Field> dataBindings = getSQLRecordTypeBinding() instanceof LogicAndDataPart ? ((LogicAndDataPart)getSQLRecordTypeBinding()).getFields() : null;
            if (dataBindings == null) {
            	numSQLDataItems = 0;
            	structureItemBindings = new ArrayList<Field>(numSQLDataItems);
            }
            else {
	            Field itemBinding;
	            structureItemBindings = new ArrayList<Field>(dataBindings.size());
	            for (int i = 0; i < dataBindings.size(); i++) {
	                itemBinding = dataBindings.get(i);
	                if (shouldKeep(itemBinding)) {
	                    structureItemBindings.add(itemBinding);
	                    numSQLDataItems++;
	                }
	            }
            }

            if (SQLIOStatementUtility.isEntityRecord(getSQLRecordTypeBinding()) && !isIoObjectValid()) {
                isValidIoObject = false;
            }
        }

        if (!isValidIoObject) {
            errorMessages.add(0, getCouldNotBuildDefaultMessage());
            return false;
        }

        setupItemColumnAndKeyInfo();
        setupTableInfo();

        return true;
    }
	
	private boolean shouldKeep(Field dataBinding) {
        if (dataBinding == null) {
            return false;
        }

        Type typeBinding = dataBinding.getType();
        if (!TypeUtils.isPrimitive(typeBinding)) {
            return false;
        }

        if (TypeUtils.Type_ANY.equals(typeBinding)) {
            return false;
        }
        return true;
    }
	
	protected void setupTableInfo() {
    	List<String> sqlTables = new ArrayList<String>();
    	List<String> sqlTableLables = new ArrayList<String>();
    	int tableIndex = 1;
    	boolean haveTableAnnotation = false;
    	
        if (SQLIOStatementUtility.isEntityRecord(getSQLRecordTypeBinding()) || SQLIOStatementUtility.isBasicRecord(getSQLRecordTypeBinding())) {
        	 List<Type> declarers = new ArrayList<Type>();
        	 declarers.add(getSQLRecordTypeBinding());
        	 Part declaringPart = BindingUtil.getDeclaringPart(sqlRecordData);
        	 if(declaringPart instanceof Record
        			 && SQLConstants.GET_IO_TYPE.toUpperCase().equals(getIOType())) {
        		 declarers.add(declaringPart);
        	 }
        	 
        	 for(Type declarer : declarers) {
        		 Annotation annotation = declarer.getAnnotation("eglx.persistence.sql.Table");
        		 haveTableAnnotation = false;
        		 
        		 if (annotation != null) {
                 	String tableName = (String)annotation.getValue("name");
                 	if(tableName != null) {
                 		haveTableAnnotation = true;
                 		sqlTables.add(tableName);
                 		sqlTableLables.add("t" + tableIndex);
                 		tableIndex++;
                 	}
                 }
        		 
        		 if(!haveTableAnnotation) {
       				sqlTables.add(BindingUtil.getShortTypeString(declarer, false));
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
	
	protected void setupItemColumnAndKeyInfo() {
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
            String itemName;
            String columnName;
            boolean isReadOnly;
            for (Field itemBinding : structureItemBindings) {
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
				keyItemAndColumnNames = new String[getKeyItems().size()][2];
			} else {
				keyItemAndColumnNames = new String[0][0];
			}
		} else {
			useRecordKeys = false;
		}


        if (structureItemBindings != null) {
            Field itemBinding;
            boolean isUpdateable;
            for (int i = 0; i < numSQLDataItems; i++) {
                itemBinding = structureItemBindings.get(i);
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

        if (useRecordKeys && numKeys == 0 && getKeyItems().size() > 0) {
            keyItemAndColumnNames = new String[0][0];
        }

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
	
	protected boolean hasReadWriteColumns() {
        if (getSQLRecordTypeBinding() != null) {
        	if (structureItemBindings != null && structureItemBindings.size() > 0) {
        		for (Field field : structureItemBindings) {
        			if (!getIsReadOnly(field)) {
        				return true;
        			}
        		}
        	}
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
	
	protected boolean containsOnlyKeyOrReadOnlyColumns() {
        if (getSQLRecordTypeBinding() != null) {
            return SQLIOStatementUtility.containsOnlyKeyOrReadOnlyColumns(getSQLRecordTypeBinding(), getKeyItems());
        }
        return false;
    }
	
	protected Type getSQLRecordTypeBinding() {
        if (sqlRecordTypeBinding == null && sqlRecordData != null) {
            sqlRecordTypeBinding = sqlRecordData.getType();
            while (sqlRecordTypeBinding instanceof ArrayType) {
            	sqlRecordTypeBinding = ((ArrayType)sqlRecordTypeBinding).getElementType();
            }
        }
        return sqlRecordTypeBinding;
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
	
	public List<Field> getKeyItems() {
        if (keyItems == null) {
        	keyItems = new ArrayList();
            if (getSQLRecordTypeBinding() instanceof LogicAndDataPart) {
            	
            	List<Field> dataBindings = ((LogicAndDataPart)getSQLRecordTypeBinding()).getFields();
            	
            	for(Field dataBinding : dataBindings) {
            		if (Utils.isKeyField(dataBinding)) {
            			// Only add if the field was resolved.
            			if (dataBinding.getType() != null) {
            				keyItems.add(dataBinding);
            			}
            		}
            	}
           }
        }
        return keyItems;
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

        if ((SQLIOStatementUtility.isEntityRecord(getSQLRecordTypeBinding()) || SQLIOStatementUtility.isBasicRecord(getSQLRecordTypeBinding()))
        		&& tableNames.length > 0) {
        	Map<Type,String> foreignKeys = SQLIOStatementUtility.getForeignKeys(sqlRecordData, getSQLRecordTypeBinding());
        	String masterTableName = null;
        	String slaveTableName = SQLIOStatementUtility.getTableName(sqlRecordData.getType());
        	int slaveIndex = getTableIndex(slaveTableName);
        	 
        	if(slaveTableName!=null && slaveIndex !=-1 && foreignKeys != null) {
        		for(Type masterTable : foreignKeys.keySet()) {
        			masterTableName = SQLIOStatementUtility.getTableName(masterTable);

					if (masterTableName != null) {
						int tableIndex = getTableIndex(masterTableName);
						if(tableIndex != -1) {
							defaultSelectConditions = tableLabels[slaveIndex] + SQLConstants.QUALIFICATION_DELIMITER
									+ foreignKeys.get(masterTable) + "=" 
									+ tableLabels[tableIndex] + SQLConstants.QUALIFICATION_DELIMITER
									+ SQLIOStatementUtility.getIdColumnName(masterTable)
									+ SQLConstants.SPACE + SQLConstants.AND + SQLConstants.SPACE
									+ tableLabels[tableIndex] + SQLConstants.QUALIFICATION_DELIMITER
									+ SQLIOStatementUtility.getIdColumnName(masterTable)
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
        //TODO necessary? the parser doesn't exist anymore. but the current implementation doesn't support foreign keys so we don't get here.
//        defaultSelectConditions = new EGLSQLParser(defaultSelectConditions, "ANY", compilerOptions).getAllClauses(); //$NON-NLS-1$

        // Trim leading CR's.
        defaultSelectConditions = trimLeadingChar(defaultSelectConditions, '\r');
        // Trim leading LF's.
        defaultSelectConditions = trimLeadingChar(defaultSelectConditions, '\n');
        // Trim leading blanks.
        defaultSelectConditions = trimLeadingChar(defaultSelectConditions, ' ');

        if (defaultSelectConditions.trim().length() == 0) {
            return null;
        }
        else {
            return defaultSelectConditions;
        }
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
	
	protected boolean isIoObjectValid() {
        //subclasses should override as necessary
        return true;
    }
	
	protected boolean isRecordKeyItem(Field data) {
		return getKeyItems().contains(data);
    }
	
	protected boolean isSQLRecordDefinedWithMultipleTables() {
		return getSQLRecordTypeBinding() instanceof EGLClass && Utils.getTableName((EGLClass)getSQLRecordTypeBinding()).indexOf(',') != -1;
	}
	
	protected String getColumnName(Field itemBinding) {
        return Utils.getColumnName(itemBinding);
    }
	
	protected boolean getIsReadOnly(Field itemBinding) {
        return SQLIOStatementUtility.getIsReadOnly(itemBinding);
    }
	
	protected boolean getIsUpdateable(Field itemBinding) {
        return SQLIOStatementUtility.getIsUpdateable(itemBinding);
    }
	
	public String getWhereCurrentOfClause() {
        // This is used only for replace and delete statements.
        return SQLConstants.WHERE_CURRENT_OF_CLAUSE;
    }
	
	public abstract String getIOType();
	
	public abstract String getSQLStatementType();
}

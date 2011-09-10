package org.eclipse.edt.mof.eglx.persistence.sql.utils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class SQL {
	
	private SQL() {}

	public static String getTableName(EGLClass entity) {
		Annotation table = entity.getAnnotation("eglx.persistence.sql.Table");
		Annotation secTables = entity.getAnnotation("eglx.persistence.sql.SecondaryTables");
		String names = table == null ? entity.getName() : (String)table.getValue();
		if (secTables != null) {
			names += " t1";
			int i = 2;
			for (String tname : (List<String>)secTables.getValue()) {
				names += ", " + tname + "t" + i;
				i++;
			}
		}
		return names;
	}
	
	public static String getColumnName(Field field) {
		Annotation column = field.getAnnotation("eglx.persistence.sql.Column");
		String name = column == null ? field.getName() : (String)column.getValue("name");
		// Determine table alias prefix if necessary
		EGLClass entity = (EGLClass)field.getContainer();
		Annotation secTables = entity.getAnnotation("eglx.persistence.sql.SecondaryTables");
		if (secTables != null) {
			String table = (String)column.getValue("table");
			if (table == null) {
				// Column is part of primary table
				name = "t1." + name;
			}
			else {
				List<String> tables = (List<String>)secTables.getValue("tableNames");
				int index = 2;
				for (int i=0; i<tables.size(); i++) {
					if (table.equalsIgnoreCase(tables.get(i))) {
						index += i;
						break;
					}
				}
				name = "t" + index + '.' + name;
			}
		}
		return name;
	}

	public static boolean isKeyField(Field field) {
		return field.getAnnotation("eglx.persistence.Id") != null;
	}

	public static boolean isAutoGenKeyField(Field field) {
		return isKeyField(field) && field.getAnnotation("eglx.persistence.sql.GeneratedValue") != null;
	}

	public static boolean isAssociationField(Field field) {
		Type type = field.getType();
		if (type instanceof ArrayType) return true;
		return false;
	}
	
	public static boolean isTransient(Field field) {
		return field.getAnnotation("eglx.persistence.Transient") != null;
	}
	
	public static boolean isPersistable(Field field) {
		return !isAutoGenKeyField(field) 
			&& !isAssociationField(field)
			&& !isTransient(field);
	}
	public static boolean isSQLResultSet(Type datasource) {
		return datasource.getTypeSignature().equals("eglx.persistence.sql.SQLResultSet");
	}
	
	public static boolean isSQLDateTimeType(Classifier type) {
		return type.equals(TypeUtils.Type_DATE) 
				|| type.equals(TypeUtils.Type_TIME)
				|| type.equals(TypeUtils.Type_TIMESTAMP);		
	}
	
	public static boolean isWrappedSQLType(Classifier type) {
		return isSQLDateTimeType(type)
				|| type.equals(TypeUtils.Type_BLOB)
				|| type.equals(TypeUtils.Type_CLOB);
	}
	
	public static String getSQLTypeName(Classifier type) {
		String name = getSqlSimpleTypeName(type);
		
		if (name != null) {
			if (isWrappedSQLType(type)) {
				name = "java.sql." + name;
			}
		}
		return name;
	}
	
	public static String getSqlSimpleTypeName(Classifier type) {
		String name = null;
		if (type.equals(TypeUtils.Type_DATE)) name = "Date"; 
		else if (type.equals(TypeUtils.Type_TIME)) name = "Time";
		else if (type.equals(TypeUtils.Type_TIMESTAMP)) name = "Timestamp";
		else if (type.equals(TypeUtils.Type_BLOB)) name = "Blob";
		else if (type.equals(TypeUtils.Type_CLOB)) name = "Clob";
		else if (type.equals(TypeUtils.Type_BOOLEAN)) name = "Boolean";
		else if (type.equals(TypeUtils.Type_FLOAT)) name = "Double";
		else if (type.equals(TypeUtils.Type_SMALLFLOAT)) name = "Float";
		else if (type.equals(TypeUtils.Type_BIGINT)) name = "Long";
		else if (type.equals(TypeUtils.Type_INT)) name = "Int";
		else if (type.equals(TypeUtils.Type_SMALLINT)) name = "Short";
		else if (type.equals(TypeUtils.Type_DECIMAL)) name = "BigDecimal";
		else if (type.equals(TypeUtils.Type_STRING)) name = "String";
		return name;
	}
	
	public static String getSQLTypeConstant(Classifier type) {
		String constant = "java.sql.Types.";
		if (type.equals(TypeUtils.Type_DATE)) constant += "DATE"; 
		else if (type.equals(TypeUtils.Type_TIME)) constant += "TIME";
		else if (type.equals(TypeUtils.Type_TIMESTAMP)) constant += "TIMESTAMP";
		else if (type.equals(TypeUtils.Type_BLOB)) constant += "BLOB";
		else if (type.equals(TypeUtils.Type_CLOB)) constant += "CLOB";
		else if (type.equals(TypeUtils.Type_BOOLEAN)) constant += "BOOLEAN";
		else if (type.equals(TypeUtils.Type_FLOAT)) constant += "DOUBLE";
		else if (type.equals(TypeUtils.Type_SMALLFLOAT)) constant += "FLOAT";
		else if (type.equals(TypeUtils.Type_BIGINT)) constant += "BIGINT";
		else if (type.equals(TypeUtils.Type_INT)) constant += "INTEGER";
		else if (type.equals(TypeUtils.Type_SMALLINT)) constant += "SHORT";
		else if (type.equals(TypeUtils.Type_DECIMAL)) constant += "DECIMAL";
		else if (type.equals(TypeUtils.Type_STRING)) constant += "VARCHAR";
		return constant;		
	}
	
	public static boolean isMappedSQLType(EGLClass type) {
		return getSqlSimpleTypeName(type) != null;
	}
	
	public static String getConvertToSQLConstructorOptions(EGLClass type) {
		if (type.equals(TypeUtils.Type_DATE) 
				|| type.equals(TypeUtils.Type_TIME)
				|| type.equals(TypeUtils.Type_TIMESTAMP)) {
			return "getTimeInMillis()";
		}
		return "unsupportedConversion()";
	}
	
	public static boolean isCallStatement(String sqlStmt) {
		try {
			StringTokenizer parser = new StringTokenizer(sqlStmt);
			String stmtKind = parser.nextToken();
			return (stmtKind.equalsIgnoreCase("call"));
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}

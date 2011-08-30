package org.eclipse.edt.mof.eglx.persistence.sql.utils;

import java.util.List;

import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

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
}

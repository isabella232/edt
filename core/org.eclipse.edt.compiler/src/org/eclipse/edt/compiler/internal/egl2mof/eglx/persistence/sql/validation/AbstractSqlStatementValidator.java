package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation;

import java.util.Arrays;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class AbstractSqlStatementValidator {

	protected static final String[] SQL_PKG = InternUtil.intern(new String[] {"eglx", "persistence", "sql"});
	protected static final String[] PERSISTENCE_PKG = InternUtil.intern(new String[] {"eglx", "persistence"});
	
	
	
	protected boolean isResultSet(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		if (type.getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
			return false;
		}
		
		ExternalTypeBinding et = (ExternalTypeBinding)type;
		
		if (type.getName() == InternUtil.intern("SQLResultSet") && type.getPackageName() == SQL_PKG) {
			return true;
		}
		
		return false;
	}
	
	protected boolean isDataSource(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		if (type.getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
			return false;
		}
		
		ExternalTypeBinding et = (ExternalTypeBinding)type;
		
		if (type.getName() == InternUtil.intern("SQLDataSource") && type.getPackageName() == SQL_PKG) {
			return true;
		}
		
		return false;
	}

	protected boolean isSqlStatement(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		if (type.getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
			return false;
		}
		
		ExternalTypeBinding et = (ExternalTypeBinding)type;
		
		if (type.getName() == InternUtil.intern("SQLStatement") && type.getPackageName() == SQL_PKG) {
			return true;
		}
		
		return false;
	}
	
	protected boolean isEntity(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		return type.getAnnotation(PERSISTENCE_PKG, InternUtil.intern("entity")) != null;
	}
	
	protected boolean isEntityWithID(ITypeBinding type) {
		if (!Binding.isValidBinding(type) || !isEntity(type)) {
			return false;
		}
		
		List<IDataBinding> fields = null;
		
		switch (type.getKind()) {
		case ITypeBinding.FLEXIBLE_RECORD_BINDING:
			fields = ((FlexibleRecordBinding)type).getDeclaredFields();
			break;
		case ITypeBinding.HANDLER_BINDING:
			fields = ((HandlerBinding)type).getDeclaredData();
			break;
		case ITypeBinding.EXTERNALTYPE_BINDING:
			fields = ((ExternalTypeBinding)type).getDeclaredAndInheritedData();
			break;

		default:
			break;
		}
		
		if (fields == null) {
			return false;
		}
		
		for(IDataBinding field: fields) { 
			if (field.getAnnotation(PERSISTENCE_PKG, "ID") != null) {
				return true;
			}
		}
		return false;
	}

	
	protected boolean isAssociationExpression(Expression exp) {
		
		IDataBinding data = exp.resolveDataBinding();
		if (!Binding.isValidBinding(data)) {
			return false;
		}
		
		if (!Binding.isValidBinding(data.getDeclaringPart())) {
			return false;
		}
		
		if (!isEntity(data.getDeclaringPart())) {
			return false;
		}
		
		IAnnotationBinding annotation = null;
		
		annotation = data.getAnnotation(PERSISTENCE_PKG, InternUtil.intern("OneToOne"));
		if (annotation == null) {
			annotation = data.getAnnotation(PERSISTENCE_PKG, InternUtil.intern("OneToMany"));
		}
		if (annotation == null) {
			annotation = data.getAnnotation(PERSISTENCE_PKG, InternUtil.intern("ManyToOne"));
		}
		if (annotation == null) {
			annotation = data.getAnnotation(PERSISTENCE_PKG, InternUtil.intern("ManyToMany"));
		}
		
		if (annotation == null) {
			return false;
		}
		
		return true;
		
	}
}

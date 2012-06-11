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
package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation;

import java.util.Collection;
import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
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
		
		if (type.getName() == InternUtil.intern("SQLDataSource") && type.getPackageName() == SQL_PKG) {
			return true;
		}
		
		for (ITypeBinding parent : (List<ITypeBinding>)((ExternalTypeBinding)type).getExtendedTypes()) {
			if (isDataSource(parent)) {
				return true;
			}
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
		
		if (type.getName() == InternUtil.intern("SQLStatement") && type.getPackageName() == SQL_PKG) {
			return true;
		}
		
		return false;
	}
	
	protected boolean isEntity(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		switch (type.getKind()) {
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
			case ITypeBinding.HANDLER_BINDING:
			case ITypeBinding.EXTERNALTYPE_BINDING:
				return true;
			default:
				return false;
		}
	}
	
	protected boolean isEntityWithID(ITypeBinding type) {
		if (!Binding.isValidBinding(type) || !isEntity(type)) {
			return false;
		}
		
		return hasID(type);
	}
	
	protected boolean hasID(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		List<IDataBinding> fields = null;
		
		while (type != null && type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			type = ((ArrayTypeBinding)type).getElementType();
		}
		
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
		//TODO this is probably not complete. might need to validate the type being pointed to has an association back to exp.
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
	
	protected boolean isDataExpr(Expression expr) {
		ITypeBinding type = expr.resolveTypeBinding();
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		//TODO Once generation supports dictionaries, uncomment the next couple lines.
		if (type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			ITypeBinding elementType = ((ArrayTypeBinding)type).getElementType();
			return Binding.isValidBinding(elementType) && (/*elementType.getKind() == ITypeBinding.DICTIONARY_BINDING ||*/ isEntity(elementType));
		}
		
		return /*type.getKind() == ITypeBinding.DICTIONARY_BINDING ||*/ isScalar(expr) || isEntity(type) || isAssociationExpression(expr);
	}
	
	protected boolean isScalar(Expression expr) {
		ITypeBinding type = expr.resolveTypeBinding();
		return Binding.isValidBinding(type) && ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getKind();
	}
	
	protected boolean mapsToColumns(List exprs) {
		// To map to a column, the expression must be a primitive type.
		int size = exprs.size();
		for (int i = 0; i < size; i++) {
			Node n = (Node)exprs.get(i);
			if (n instanceof Expression) {
				Expression expr = (Expression)n;
				if (!isScalar(expr)) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @return true if every expr maps to the same table.
	 */
	protected boolean mapsToSingleTable(List<Expression> exprs) {
		IPartBinding firstParent = null;
		for (Expression e : exprs) {
			IDataBinding binding = e.resolveDataBinding();
			if (Binding.isValidBinding(binding)) {
				IPartBinding parent = binding.getDeclaringPart();
				if (parent == null) {
					// If even 1 column has no table then we return false.
					return false;
				}
				
				if (firstParent == null) {
					firstParent = parent;
				}
				else if (!firstParent.equals(parent)) {
					return false;
				}
			}
		}
		
		if (firstParent == null) {
			return false;
		}
		return isEntity(firstParent) && isSingleTable(firstParent);
	}
	
	protected int[] getOffsets(Collection<Node> nodes) {
		int startOffset = -1;
		int endOffset = -1;
		for (Node n : nodes) {
			int nextStart = n.getOffset();
			int nextEnd = nextStart + n.getLength();
			if (startOffset == -1 || nextStart < startOffset) {
				startOffset = nextStart;
			}
			if (endOffset == -1 || nextEnd > endOffset) {
				endOffset = nextEnd;
			}
		}
		return new int[]{startOffset, endOffset};
	}
	
	protected boolean isSingleTable(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		if (type.getAnnotation(SQL_PKG, "SECONDARYTABLE") != null) {
			return false;
		}
		
		IAnnotationBinding annot = type.getAnnotation(SQL_PKG, "SECONDARYTABLES");
		if (annot != null) {
			Object value = annot.getValue();
			if (value instanceof IAnnotationBinding[]) {
				return ((IAnnotationBinding[])value).length == 0;
			}
		}
		return true;
	}
}

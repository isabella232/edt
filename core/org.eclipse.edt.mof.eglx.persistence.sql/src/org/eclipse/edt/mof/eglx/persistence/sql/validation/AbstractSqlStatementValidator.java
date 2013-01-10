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
package org.eclipse.edt.mof.eglx.persistence.sql.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.utils.NameUtile;

public class AbstractSqlStatementValidator {

	protected static final String OneToOne = NameUtile.getAsName("eglx.persistence.OneToOne");
	protected static final String OneToMany = NameUtile.getAsName("eglx.persistence.OneToMany");
	protected static final String ManyToOne = NameUtile.getAsName("eglx.persistence.ManyToOne");
	protected static final String ManyToMany = NameUtile.getAsName("eglx.persistence.ManyToMany");
	protected static final String Id = NameUtile.getAsName("eglx.persistence.Id");
	protected static final String SecondaryTable = NameUtile.getAsName("eglx.persistence.sql.SecondaryTable");
	protected static final String SecondaryTables = NameUtile.getAsName("eglx.persistence.sql.SecondaryTables");
	
	
	protected boolean isEntity(Type type) {
		return type instanceof Record || type instanceof Handler || type instanceof ExternalType;
	}
	
	protected boolean isEntityWithID(Type type) {
		if (type == null || !isEntity(type)) {
			return false;
		}
		
		return hasID(type);
	}
	
	protected boolean hasID(Type type) {
		if (type == null) {
			return false;
		}
		
		List<Field> fields = null;
		
		while (type instanceof ArrayType) {
			type = ((ArrayType)type).getElementType();
		}
		
		if (type instanceof Record) {
			fields = ((Record)type).getFields();
		}
		else if (type instanceof Handler) {
			fields = ((Handler)type).getFields();
		}
		else if (type instanceof ExternalType) {
			List<Member> members = ((ExternalType)type).getAllMembers();
			fields = new ArrayList<Field>(members.size());
			for (Member m : members) {
				if (m instanceof Field) {
					fields.add((Field)m);
				}
			}
		}
		
		if (fields == null) {
			return false;
		}
		
		for (Field field: fields) { 
			if (field.getAnnotation(Id) != null) {
				return true;
			}
		}
		return false;
	}

	
	protected boolean isAssociationExpression(Expression exp) {
		//TODO this is probably not complete. might need to validate the type being pointed to has an association back to exp.
		Member data = exp.resolveMember();
		if (data == null) {
			return false;
		}
		
		if (!isEntity(getContainingType(data))) {
			return false;
		}
		
		Annotation annotation = data.getAnnotation(OneToOne);
		if (annotation == null) {
			annotation = data.getAnnotation(OneToMany);
		}
		if (annotation == null) {
			annotation = data.getAnnotation(ManyToOne);
		}
		if (annotation == null) {
			annotation = data.getAnnotation(ManyToMany);
		}
		
		if (annotation == null) {
			return false;
		}
		
		return true;
	}
	
	protected Type getContainingType(Member m) {
		if (m == null) {
			return null;
		}
		
		Container container = m.getContainer();
		while (container instanceof Member && ((Member)container).getContainer() != null) {
			container = ((Member)container).getContainer();
		}
		
		if (container instanceof Type) {
			return (Type)container;
		}
		
		return null;
	}
	
	protected boolean isDataExpr(Expression expr) {
		Type type = expr.resolveType();
		if (type == null) {
			return false;
		}
		
		//TODO Once generation supports dictionaries, uncomment the next couple lines.
		if (type instanceof ArrayType) {
			Type elementType = ((ArrayType)type).getElementType();
			return elementType != null && (/*elementType instanceof Dictionary ||*/ isEntity(elementType));
		}
		
		return /*type instanceof Dictionary ||*/ isScalar(expr) || isEntity(type) || isAssociationExpression(expr);
	}
	
	protected boolean isScalar(Expression expr) {
		Type type = expr.resolveType();
		return type != null && TypeUtils.isPrimitive(type);
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
		Type firstParent = null;
		for (Expression e : exprs) {
			Member binding = e.resolveMember();
			if (binding != null) {
				Type parent = getContainingType(binding);
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
	
	protected boolean isSingleTable(Type type) {
		if (type == null) {
			return false;
		}
		
		if (type.getAnnotation(SecondaryTable) != null) {
			return false;
		}
		
		Annotation annot = type.getAnnotation(SecondaryTables);
		if (annot != null) {
			Object value = annot.getValue();
			if (value instanceof Object[]) {
				return ((Object[])value).length == 0;
			}
		}
		return true;
	}
}

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
package org.eclipse.edt.compiler.core.ast;

import java.util.List;


/**
 * SettingsBlock AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class SettingsBlock extends Node {

	private List settings;	// List of Settings

	public SettingsBlock(List settings, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.settings = setParent(settings);
	}
	
	public List getSettings() {
		return settings;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, settings);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new SettingsBlock(cloneList(settings), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append('{');
		
		boolean first = true;
		for (Object setting : settings) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(setting.toString());
		}
		buf.append('}');
		return buf.toString();
	}
	
//	public Assignment getSetting(final String propertyName) {
//		final Assignment[] result = new Assignment[] {null};
//		acceptChildren(new DefaultASTVisitor() {
//			public boolean visit(Assignment assignment) {
//				IBinding binding = assignment.getLeftHandSide().resolveDataBinding();
//				if(binding != null && binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
//					if(propertyName == binding.getName()) {
//						result[0] = assignment;
//					}
//				}
//				return false;
//			}
//		}, settings);
//		return result[0];
//	}
	
//	public Assignment getSetting(final IAnnotationTypeBinding annotationTypeBinding) {
//		final Assignment[] result = new Assignment[] {null};
//		acceptChildren(new DefaultASTVisitor() {
//			public boolean visit(Assignment assignment) {
//				IBinding binding = assignment.getLeftHandSide().resolveDataBinding();
//				if(binding != null && binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
//					if(annotationTypeBinding.getName() == binding.getName()) {
//						result[0] = assignment;
//					}
//				}
//				return false;
//			}
//		}, settings);
//		return result[0];
//	}
	
//	public Node getSetting(final String[] annotationPackage, final String anotationName) {
//		final Node[] result = new Node[] {null};
//		acceptChildren(new DefaultASTVisitor() {
//			public boolean visit(Assignment assignment) {
//				IBinding binding = assignment.getLeftHandSide().resolveDataBinding();
//				if(binding != null && binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
//					if(AbstractBinder.annotationIs(((IAnnotationBinding) binding).getAnnotationType(), annotationPackage, anotationName)) {
//						result[0] = assignment;
//					}
//				}
//				return false;
//			}
//			
//			public boolean visit(SetValuesExpression setValuesExpression) {
//				setValuesExpression.getExpression().accept(this);
//				return false;
//			}
//			
//			public boolean visit(AnnotationExpression annotationExpression) {
//				IBinding binding = annotationExpression.resolveDataBinding();
//				if(binding != null && binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
//					if(AbstractBinder.annotationIs(((IAnnotationBinding) binding).getAnnotationType(), annotationPackage, anotationName)) {
//						result[0] = annotationExpression;
//					}
//				}
//				return false;
//			}
//		}, settings);
//		return result[0];
//	}
}

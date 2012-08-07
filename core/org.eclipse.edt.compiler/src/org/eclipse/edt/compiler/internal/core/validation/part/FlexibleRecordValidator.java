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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.FieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class FlexibleRecordValidator extends AbstractASTVisitor {
	
	private abstract class RecordNameValidator {
		abstract void validate(Name recordName);
	}
	
	private class DefaultRecordNameValidator extends RecordNameValidator {
		void validate(Name recordName) {
			EGLNameValidator.validate(recordName, EGLNameValidator.RECORD, problemRequestor, compilerOptions);
		}
	}
	
	private abstract class ItemInfo {
		Node nodeForErrors;
		String canonicalName;
		StructureItem structureItemNode;
		FlexibleRecordFieldBinding binding;
		
		public abstract ItemValidator getValidator();
	}
	
	private class DefaultItemInfo extends ItemInfo {
		public ItemValidator getValidator() {
			return new DefaultItemValidator(this);
		}
	}
	
	private abstract class ItemValidator {
		protected ItemInfo itemInfo;
		
		public ItemValidator(ItemInfo itemInfo) {
			this.itemInfo = itemInfo;
		}
		
		public abstract void validate();
	}
	
	private class DefaultItemValidator extends ItemValidator {		
		public DefaultItemValidator(ItemInfo itemInfo) {
			super(itemInfo);
		}
		
		public void validate() {
			if(itemInfo.structureItemNode.hasInitializer()) {
				Expression expression = itemInfo.structureItemNode.getInitializer();
				ITypeBinding expressionTBinding = expression.resolveTypeBinding();
				if(expressionTBinding != null && expressionTBinding != IBinding.NOT_FOUND_BINDING) {
					if(!TypeCompatibilityUtil.isMoveCompatible(itemInfo.binding.getType(), expressionTBinding, expression, compilerOptions) &&
					   !expressionTBinding.isDynamic() &&
					   !TypeCompatibilityUtil.areCompatibleExceptions(expressionTBinding, itemInfo.binding.getType(), compilerOptions)) {
						problemRequestor.acceptProblem(
							expression,
							IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
							new String[] {
								StatementValidator.getShortTypeString(itemInfo.binding.getType()),
								StatementValidator.getShortTypeString(expressionTBinding),										
								itemInfo.canonicalName + "=" + expression.getCanonicalString()
							});
					}
				}
			}
			
			
			Type type = itemInfo.structureItemNode.getType();
			if(type != null) {
				StatementValidator.validateRequiredFieldsInCUIDeclaration(type, itemInfo.structureItemNode.getSettingsBlock(), itemInfo.canonicalName.equals("*"), problemRequestor);
			}
			
			IBinding itemBinding = itemInfo.structureItemNode.resolveBinding();
			if(itemBinding != null && IBinding.NOT_FOUND_BINDING != itemBinding && itemBinding.isDataBinding()) {
				StatementValidator.validateDeclarationForStereotypeContext((IDataBinding) itemBinding, problemRequestor, itemInfo.nodeForErrors);
			}
		}
	}
	
	protected IProblemRequestor problemRequestor;
	private FlexibleRecordBinding recordBinding;
	private Name recordNameNode;
	private String canonicalRecordName;
    private ICompilerOptions compilerOptions;
	
	public FlexibleRecordValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(Record record) {
		recordNameNode = record.getName();
		canonicalRecordName = record.getName().getCanonicalName();
		recordBinding = (FlexibleRecordBinding) record.getName().resolveBinding();		
		getRecordNameValidator().validate(record.getName());
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(record);		
		
		return true;
	}
	
	public boolean visit(StructureItem structureItem) {
		if(structureItem.isEmbedded()) {
		}
		else {
			ItemInfo itemInfo = createItemInfo(structureItem);
			if(itemInfo != null) {
				itemInfo.getValidator().validate();
			}
		}
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(structureItem);
		structureItem.accept(new FieldValidator(problemRequestor, compilerOptions));
		
		if (structureItem.hasType()) {
			StatementValidator.validateDataDeclarationType(structureItem.getType(), problemRequestor, recordBinding);
		}
		
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
	
	private RecordNameValidator getRecordNameValidator() {
		return new DefaultRecordNameValidator();
	}
	
	private ItemInfo createItemInfo(StructureItem sItem) {
		IBinding binding = sItem.resolveBinding();
		if(binding == null || binding == IBinding.NOT_FOUND_BINDING) {
			return null;
		}
		FlexibleRecordFieldBinding itemBinding = (FlexibleRecordFieldBinding) binding; 
		
		ItemInfo result = new DefaultItemInfo();
		
		if(sItem.isFiller()) {
			result.nodeForErrors = sItem;
			result.canonicalName = "*";			
		}
		else {
			result.nodeForErrors = sItem.getName();
			result.canonicalName = sItem.getName().getCanonicalName();			
		}
		result.structureItemNode = sItem;
		result.binding = itemBinding;
		
		return result;		
	}
	
	private static Node getAnnotationNode(StructureItem sItemNode, final String fieldName) {
		final Node[] result = new Node[] {null};
		sItemNode.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				return true;
			}
			public boolean visit(SetValuesExpression setValuesExpression) {
				return true;
			}
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			public boolean visit(Assignment assignment) {
				IBinding binding = assignment.getLeftHandSide().resolveDataBinding();
				if(binding != null && binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
					if(fieldName == binding.getName()) {
						result[0] = assignment;
					}
				}
				return false;
			}
		});
		return result[0];
	}
	
	private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
}

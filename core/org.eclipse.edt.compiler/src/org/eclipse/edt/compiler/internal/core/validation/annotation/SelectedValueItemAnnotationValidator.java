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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class SelectedValueItemAnnotationValidator extends AbstractSelectedItemAnnotationValidator {
	
	public SelectedValueItemAnnotationValidator() {
		super(IEGLConstants.PROPERTY_SELECTEDVALUEITEM);
	}
	
	private boolean isCompatible(ITypeBinding selectedItemType, ITypeBinding selectionRecieverType, ICompilerOptions compilerOptions) {
		if(ITypeBinding.ARRAY_TYPE_BINDING == selectedItemType.getKind()) {
			selectedItemType = ((ArrayTypeBinding) selectedItemType).getElementType();
		}
		if(ITypeBinding.ARRAY_TYPE_BINDING == selectionRecieverType.getKind()) {
			selectionRecieverType = ((ArrayTypeBinding) selectionRecieverType).getElementType();
		}
		return typesMatch(selectionRecieverType, selectedItemType, compilerOptions);
	}
	
	protected void targetIsDataTableOrRecordArray(Node errorNode, IBinding targetBinding, ITypeBinding targetTypeBinding, ITypeBinding selectedItemType, IDataBinding value, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IDataBinding valueItem = getValueItem(targetBinding, targetTypeBinding);
		if(valueItem != null) {
			ITypeBinding valueType = valueItem.getType();
			if(valueType != null && IBinding.NOT_FOUND_BINDING != valueType) {
				if(!isCompatible(selectedItemType, valueType, compilerOptions)) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.SELECTEDVALUEITEM_VALUEITEM_TYPE_MISMATCH,
						new String[] {
							StatementValidator.getTypeString(selectedItemType),
							value.getCaseSensitiveName(),
							StatementValidator.getTypeString(valueType),
							valueItem.getCaseSensitiveName()
						}
					);
				}
			}			
		}
	}

	private boolean typesMatch(ITypeBinding type1, ITypeBinding type2, ICompilerOptions compilerOptions) {
		return SelectTypeForPageItemFieldValidator.typesIdenticalOrAreCharAndString(type1, type2, compilerOptions);
	}

	protected void targetIsPrimitiveArray(Node errorNode, IBinding targetBinding, ITypeBinding targetTypeBinding, ITypeBinding selectedItemType, IDataBinding value, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(!isCompatible(selectedItemType, targetTypeBinding, compilerOptions)) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.VALUEITEM_PRIMITIVEARRAY_OR_COLUMN_TARGET_TYPE_MISMATCH,
				new String[] {
					StatementValidator.getTypeString(selectedItemType),
					value.getCaseSensitiveName(),
					StatementValidator.getTypeString(targetTypeBinding),
					targetBinding.getCaseSensitiveName()
				}
			);
		}
	}

	private IDataBinding getValueItem(IBinding targetBinding, ITypeBinding targetTypeBinding) {
		IAnnotationBinding selectionListAnnotation = targetBinding.getAnnotation(new String[] {"egl", "ui"}, IEGLConstants.PROPERTY_SELECTIONLIST);
		if(selectionListAnnotation != null) {
			IAnnotationBinding valueItemAnnotation = (IAnnotationBinding) selectionListAnnotation.findData(InternUtil.intern(IEGLConstants.PROPERTY_VALUEITEM));
			if(IBinding.NOT_FOUND_BINDING != valueItemAnnotation) {
				Object value = valueItemAnnotation.getValue();
				return value instanceof Name ? ((Name) value).resolveDataBinding() : null;
			}
			return getDefaultValueItem(targetTypeBinding);
		}
		return getDefaultValueItem(targetTypeBinding);
	}

	private IDataBinding getDefaultValueItem(ITypeBinding targetTypeBinding) {
		List fields = null;
		ITypeBinding baseType = targetTypeBinding.getBaseType();
		switch(baseType.getKind()) {
			case ITypeBinding.FIXED_RECORD_BINDING:
			case ITypeBinding.DATATABLE_BINDING:
				fields = ((FixedStructureBinding) baseType).getStructureItems();
				break;
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
				fields = ((FlexibleRecordBinding) baseType).getDeclaredFields();
				break;
		}
		if(fields == null || fields.size() == 0) {
			return null;
		}
		if(fields.size() == 1) {
			return (IDataBinding) fields.get(0);
		}
		return (IDataBinding) fields.get(1);
	}
}

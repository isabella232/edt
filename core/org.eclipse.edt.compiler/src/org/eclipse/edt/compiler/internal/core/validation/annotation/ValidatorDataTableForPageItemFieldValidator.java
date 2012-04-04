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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class ValidatorDataTableForPageItemFieldValidator extends DefaultFieldContentAnnotationValidationRule {
	
	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORDATATABLE))) {
			ITypeBinding containerType = containerBinding.getType();
			if(Binding.isValidBinding(containerType)) {
				ITypeBinding baseType = containerType.getBaseType();
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == baseType.getKind()) {
					Primitive basePrimitive = ((PrimitiveTypeBinding) baseType).getPrimitive();
					if(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN) == baseType ||
					   Primitive.MONTHSPAN_INTERVAL == basePrimitive ||
					   Primitive.SECONDSPAN_INTERVAL == basePrimitive ||
					   PrimitiveTypeBinding.getInstance(Primitive.BLOB) == baseType ||
					   PrimitiveTypeBinding.getInstance(Primitive.CLOB) == baseType) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.PROPERTY_NOT_VALID_FOR_TYPES,
							new String[] {
								IEGLConstants.PROPERTY_VALIDATORDATATABLE,
								containerBinding.getCaseSensitiveName(),
								containerType.getCaseSensitiveName(),
								AbstractBinder.toCommaList(new ITypeBinding[] {
									PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
									PrimitiveTypeBinding.getInstance(Primitive.INTERVAL),
									PrimitiveTypeBinding.getInstance(Primitive.BLOB),
									PrimitiveTypeBinding.getInstance(Primitive.CLOB),
								})
							});
					}
					else {
						IAnnotationBinding aBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORDATATABLE));
						Object value = aBinding.getValue();
						if(value instanceof DataTableBinding) {
							DataTableBinding tblBinding = (DataTableBinding) value;
							List structureItems = tblBinding.getStructureItems();
							IPartSubTypeAnnotationTypeBinding subType = tblBinding.getSubType();
							if(!AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, IEGLConstants.DATATABLE_SUBTYPE_MATCHVALID)){
								IAnnotationBinding typeaheadannotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_TYPEAHEAD));
								if(typeaheadannotationBinding != null && typeaheadannotationBinding.getValue() != null) {
									org.eclipse.edt.compiler.core.Boolean typeaheadvalue = (org.eclipse.edt.compiler.core.Boolean)typeaheadannotationBinding.getValue();
									if (typeaheadvalue.booleanValue() ){
										problemRequestor.acceptProblem(
												errorNode,
												IProblemRequestor.VALDATATABLE_TYPE_INVALID_FOR_TYPEAHEAD,
												new String[] {tblBinding.getCaseSensitiveName(),IEGLConstants.PROPERTY_VALIDATORDATATABLE});
									}
								}
							}
							
							if(AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, IEGLConstants.DATATABLE_SUBTYPE_MATCHVALID) ||
							   AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, IEGLConstants.DATATABLE_SUBTYPE_MATCHINVALID)) {
								ITypeBinding firstColumnType = structureItems.isEmpty() ? null : ((IDataBinding) structureItems.get(0)).getType();
								if(firstColumnType != null) {
									if(!typesAreCompatible(firstColumnType, baseType, compilerOptions)) {
										problemRequestor.acceptProblem(
											errorNode,
											IProblemRequestor.VALIDATOR_DATATABLE_MATCH_VALID_COLUMN_TYPE_MISMATCH,
											new String[] {
												tblBinding.getCaseSensitiveName(),
												((IDataBinding) structureItems.get(0)).getCaseSensitiveName(),
												StatementValidator.getTypeString(firstColumnType),
												StatementValidator.getTypeString(baseType)
											});	
									}
								}
							}
							else if(AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, IEGLConstants.DATATABLE_SUBTYPE_RANGECHK)) {
								ITypeBinding firstColumnType = structureItems.isEmpty() ? null : ((IDataBinding) structureItems.get(0)).getType();
								if(firstColumnType != null) {
									if(!typesAreCompatible(firstColumnType, baseType, compilerOptions)) {
										problemRequestor.acceptProblem(
											errorNode,
											IProblemRequestor.VALIDATOR_DATATABLE_RANGECHK_COLUMN_TYPE_MISMATCH,
											new String[] {
												tblBinding.getCaseSensitiveName(),
												((IDataBinding) structureItems.get(0)).getCaseSensitiveName(),
												StatementValidator.getTypeString(firstColumnType),
												StatementValidator.getTypeString(baseType)
											});	
									}
									else {
										ITypeBinding secondColumnType = structureItems.size() < 2 ? null : ((IDataBinding) structureItems.get(1)).getType();
										if(secondColumnType != null) {
											if(!typesAreCompatible(secondColumnType, baseType, compilerOptions)) {
												problemRequestor.acceptProblem(
													errorNode,
													IProblemRequestor.VALIDATOR_DATATABLE_RANGECHK_COLUMN_TYPE_MISMATCH,
													new String[] {
														tblBinding.getCaseSensitiveName(),
														((IDataBinding) structureItems.get(0)).getCaseSensitiveName(),
														StatementValidator.getTypeString(secondColumnType),
														StatementValidator.getTypeString(baseType)
													});	
											}	
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	private boolean typesAreCompatible(ITypeBinding type1, ITypeBinding type2, ICompilerOptions compilerOptions) {
		return (TypeCompatibilityUtil.isMoveCompatible(type1, type2, null, compilerOptions) ||
		         TypeCompatibilityUtil.isMoveCompatible(type2, type1, null, compilerOptions));

	}
}

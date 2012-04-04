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
package org.eclipse.edt.compiler.binding;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FixedStructureScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.NullScope;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.TypeBindingScope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class FixedRecordBindingCompletor extends FixedStructureBindingCompletor {

	private FixedRecordBinding recordBinding;

	private IProblemRequestor problemRequestor;

	public FixedRecordBindingCompletor(Scope currentScope, FixedRecordBinding recordBinding, IDependencyRequestor dependencyRequestor,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super(recordBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
		this.recordBinding = recordBinding;
		this.problemRequestor = problemRequestor;
	}

	public boolean visit(Record record) {
		record.getName().setBinding(recordBinding);
		recordBinding.setPrivate(record.isPrivate());
		record.accept(getPartSubTypeAndAnnotationCollector());
		processSubType();
		record.accept(new StructureItemsCompletor(currentScope, recordBinding, record.getName().getCanonicalName(), dependencyRequestor,
				problemRequestor, compilerOptions));
		recordBinding.clearSimpleNamesToDataBindingsMap();

		processSettingsBlocks();
		return false;
	}

	protected Scope getFixedStructureScope() {
		return new FixedStructureScope(currentScope, recordBinding);
	}

	public void endVisit(Record record) {
		recordBinding.setValid(true);

		record.accept(new DefaultASTVisitor() {
			public boolean visit(Record record) {
				return true;
			}

			public boolean visit(SettingsBlock settingsBlock) {
				processResolvableProperties(settingsBlock, null);
				return false;
			}

			public boolean visit(StructureItem structureItem) {
				if (structureItem.hasSettingsBlock()) {
					IBinding binding = structureItem.resolveBinding();
					if (!Binding.isValidBinding(binding) || !binding.isDataBinding()) {
						return false;
					}
					IDataBinding dataBinding = (IDataBinding) binding;
					processResolvableProperties(structureItem.getSettingsBlock(), dataBinding);
				}

				if (isUIRecord()) {
					processResolvableProperties(structureItem);
				}
				return false;
			}
		});
		
		addSqlNullableAnnotations();
	}
	
    private void addSqlNullableAnnotations() {
    	
    	if (!AbstractBinder.annotationIs(recordBinding.getSubType(), new String[] {"egl", "io", "sql"}, "SQLRecord")) {
    		return;
    	}
    	
		IAnnotationBinding aBinding = recordBinding.getAnnotation(new String[] {"egl", "io", "sql"}, IEGLConstants.PROPERTY_ISSQLNULLABLE);
		if (aBinding == null) {
			return;
		}
		
		if(aBinding != null && Boolean.YES == aBinding.getValue()) {
			IDataBinding[] fields = recordBinding.getFields();
			for(int i = 0; i < fields.length; i++) {
				if (fields[i].getAnnotation(new String[] {"egl", "io", "sql"}, IEGLConstants.PROPERTY_ISSQLNULLABLE) == null) {
					fields[i].addAnnotation(aBinding);
				}
			}
		}
	}	

	private void processResolvableProperties(StructureItem item) {

		if (item.hasType()) {
			ITypeBinding type = item.getType().resolveTypeBinding();
			if (type == null || type == IBinding.NOT_FOUND_BINDING) {
				return;
			}
			type = type.getBaseType();
			if (type.getKind() == ITypeBinding.DATAITEM_BINDING && item.getName() != null) {
				processResolvableProperties(item.getName().resolveDataBinding(), item.getType());
				return;
			}
			if (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
				if (item.getName() != null && item.getName().resolveDataBinding() != null
						&& item.getName().resolveDataBinding() != IBinding.NOT_FOUND_BINDING) {
					processResolvablePropertiesInStructItemBinding((StructureItemBinding) item.getName().resolveDataBinding(), item
							.getType());
				} else { // must be an embed...
					if (item.resolveBinding() != null && item.resolveBinding() != IBinding.NOT_FOUND_BINDING) {
						processResolvablePropertiesInStructItemBinding((StructureItemBinding) item.resolveBinding(), item.getType());
					}
				}
			}

		}
	}

	private void processResolvablePropertiesInStructItemBinding(StructureItemBinding binding, Node node) {
		processResolvableProperties(binding, node);
		Iterator i = binding.getChildren().iterator();
		while (i.hasNext()) {
			StructureItemBinding itemBinding = (StructureItemBinding) i.next();
			processResolvablePropertiesInStructItemBinding(itemBinding, node);
		}
	}

	private void processResolvableProperties(IDataBinding dataBinding, Node node) {
		IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] { "egl", "ui" }, "NumElementsItem");
		if (annotation != null) {
			Object value = annotation.getValue();
			annotation = new AnnotationBinding(InternUtil.internCaseSensitive("NumElementsItem"), recordBinding,
					new AnnotationTypeBindingImpl((FlexibleRecordBinding) currentScope.findPackage(InternUtil.intern("egl"))
							.resolvePackage(InternUtil.intern("ui")).resolveType(InternUtil.intern("NumElementsItem")), recordBinding));
			dataBinding.addAnnotation(annotation);
			annotation.setValue(resolveNumElementsItem(node, value, dataBinding, null), null, null, compilerOptions, false);
		}

		annotation = dataBinding.getAnnotation(new String[] { "egl", "ui" }, "SelectedIndexItem");
		if (annotation != null) {
			Object value = annotation.getValue();
			annotation = new AnnotationBinding(InternUtil.internCaseSensitive("SelectedIndexItem"), recordBinding,
					new AnnotationTypeBindingImpl((FlexibleRecordBinding) currentScope.findPackage(InternUtil.intern("egl"))
							.resolvePackage(InternUtil.intern("ui")).resolveType(InternUtil.intern("SelectedIndexItem")), recordBinding));
			dataBinding.addAnnotation(annotation);
			annotation.setValue(resolveSelectedIndexItem(node, value, dataBinding, null), null, null, compilerOptions, false);
		}
	}

	private void processResolvableProperties(SettingsBlock block, final IDataBinding dataBinding) {

		block.accept(new DefaultASTVisitor() {
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}

			public boolean visit(Assignment assignment) {
				IAnnotationBinding annotation = assignment.resolveBinding();
				if (annotation != null) {
					processResolvableProperty(annotation.getAnnotationType(), annotation.getValue(), dataBinding, assignment
							.getRightHandSide());

					if (isUIRecord()) {
						assignment.getRightHandSide().accept(new DefaultASTVisitor() {
							public boolean visit(ArrayLiteral arrayLiteral) {
								return true;
							}

							public boolean visit(SetValuesExpression setValuesExpression) {
								IDataBinding exprData = setValuesExpression.getExpression().resolveDataBinding();
								if (exprData != null && exprData != IBinding.NOT_FOUND_BINDING) {
									processResolvableProperties(setValuesExpression.getSettingsBlock(), exprData);
								}
								return false;
							}
						});
					}
				}
				return false;
			}

			public boolean visit(SetValuesExpression setValuesExpression) {
				IDataBinding exprData = setValuesExpression.getExpression().resolveDataBinding();
				if (exprData != null && exprData != IBinding.NOT_FOUND_BINDING) {
					processResolvableProperties(setValuesExpression.getSettingsBlock(), exprData);
				}
				return false;
			}
		});

	}

	private void processResolvableProperty(IAnnotationTypeBinding annotationType, Object value, IDataBinding dataBinding, Expression expr) {
		if (dataBinding == null || value == null) {
			return;
		}
		IAnnotationBinding annotation = dataBinding.getAnnotation(annotationType);

		if (isUIRecord()) {
			if (annotation == null) {
				if (annotationIs(dataBinding.getType(), new String[] { "egl", "ui", "webTransaction" }, "LinkParameter")) {
					if (InternUtil.intern(IEGLConstants.PROPERTY_VALUEREF) == annotationType.getName()) {
						((IAnnotationBinding) dataBinding.findData(InternUtil.intern(IEGLConstants.PROPERTY_VALUEREF))).setValue(
								resolveValueRef(expr, value, dataBinding, null), null, null, compilerOptions, false);
						return;
					}
				}

				return;
			}
			if (annotationIs(annotationType, new String[] { "egl", "ui" }, "NumElementsItem")) {
				annotation.setValue(resolveNumElementsItem(expr, value, dataBinding, null), null, null, compilerOptions, false);
				return;
			}
			if (annotationIs(annotationType, new String[] { "egl", "ui" }, "SelectedIndexItem")) {
				annotation.setValue(resolveSelectedIndexItem(expr, value, dataBinding, null), null, null, compilerOptions, false);
				return;
			}
		}
		if (annotationIs(annotationType, new String[] { "egl", "core" }, "redefines")) {
			if (dataBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
				Scope saveScope = currentScope;
				currentScope = new TypeBindingScope(NullScope.INSTANCE, ((StructureItemBinding)dataBinding).getEnclosingStructureBinding(), null);
				annotation.setValue(resolveRedefines(expr, value, dataBinding, null), null, null, compilerOptions, false);
				currentScope = saveScope;
				return;
			}
		}
	}

	private boolean isUIRecord() {
		return (recordBinding.getAnnotation(new String[] { "egl", "ui", "webTransaction" }, "VGUIRecord") != null);
	}

	protected IPartSubTypeAnnotationTypeBinding getDefaultSubType() {
		return null;
	}

}

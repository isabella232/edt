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

import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author Dave Murray
 */
public abstract class PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator extends AnnotationValidationAnnotationTypeBinding {
	
	protected IAnnotationTypeBinding annotationType;
	protected String canonicalAnnotationName;
	
	protected PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator(IAnnotationTypeBinding annotationType, String canonicalAnnotationName) {
		super(InternUtil.internCaseSensitive("PropertyApplicableForCertainPrimitiveOnly"));
		this.annotationType = annotationType;
		this.canonicalAnnotationName = canonicalAnnotationName;
	}
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		final IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(annotationType.getName());
		
		// TODO REmove this null check and possibly the not found check?
		if(annotationBinding.getValue() != null  && annotationBinding.getValue()!= IBinding.NOT_FOUND_BINDING){
			if(targetTypeBinding != null && IBinding.NOT_FOUND_BINDING != targetTypeBinding) {
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == targetTypeBinding.getKind()) {
					validatePrimitiveType(errorNode, annotationBinding, problemRequestor, ((PrimitiveTypeBinding) targetTypeBinding).getPrimitive(), getCanonicalName(target));
				}
			}
		}
	}
	
	protected abstract void validatePrimitiveType(final Node errorNode, final IAnnotationBinding annotationBinding, final IProblemRequestor problemRequestor, Primitive primitive, String canonicalItemName);
	
	private String getCanonicalName(Node node) {
		final String[] result = new String[] {""};
		node.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				result[0] = structureItem.isFiller() || structureItem.isEmbedded() ? "*" : structureItem.getName().getCanonicalName();
				return false;
			}
			public boolean visit(VariableFormField variableFormField) {
				result[0] = variableFormField.getName().getCanonicalName();
				return false;
			}
			public boolean visit(ConstantFormField constantFormField) {
				result[0] = "*";
				return false;
			}
			public boolean visit(SimpleName simpleName) {
				result[0] = simpleName.getCanonicalName();
				return false;
			}
			public boolean visit(QualifiedName qualifiedName) {
				result[0] = qualifiedName.getCanonicalName();
				return false;
			}
			public boolean visit(DataItem dataItem) {
				result[0] = dataItem.getName().getCanonicalName();
				return false;
			}
		});
		return result[0];
	}
}

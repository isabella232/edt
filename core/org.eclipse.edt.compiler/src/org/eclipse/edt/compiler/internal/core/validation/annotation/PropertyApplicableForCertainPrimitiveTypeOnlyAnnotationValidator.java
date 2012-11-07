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

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.utils.NameUtile;



/**
 * @author Dave Murray
 */
public abstract class PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator extends AnnotationValidationRule {
	
	protected String canonicalAnnotationName;
	
	protected PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator(String canonicalAnnotationName) {
		super(NameUtile.getAsCaseSensitiveName("PropertyApplicableForCertainPrimitiveOnly"));
		this.canonicalAnnotationName = canonicalAnnotationName;
	}
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		final Annotation annotationBinding = (Annotation)allAnnotationsAndFields.get(NameUtile.getAsName(canonicalAnnotationName));
		
		// TODO REmove this null check?
		if(annotationBinding != null && annotationBinding.getValue() != null){
			Type type = null;
			if (targetElement instanceof Type) { 
				type = (Type)targetElement;
			}
			else if (targetElement instanceof TypedElement) {
				type = ((TypedElement)targetElement).getType();
			}
			
			if(type != null) {
				validateType(errorNode, annotationBinding, problemRequestor, type, getCanonicalName(target));
			}
		}
	}
	
	protected abstract void validateType(final Node errorNode, final Annotation annotationBinding, final IProblemRequestor problemRequestor, Type type, String canonicalItemName);
	
	private String getCanonicalName(Node node) {
		final String[] result = new String[] {""};
		node.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				result[0] = structureItem.getName().getCanonicalName();
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

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

import org.eclipse.edt.compiler.binding.annotationType.EGLDataItemPropertyProblemAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLDataItemPropertyProblemsAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * Some kinds of problems related to DataItem properties are only true problems when the item is
 * referenced from a certain context. For example, if the item specifies a validatorFunction
 * property whose value does not resolve to a function, a part for which validatorFunctions have
 * no meaning should be allowed to compile without error. That is, a program subtyped BasicProgram
 * that references the item could compile without error, but a handler subtyped JSFHandler should not.
 * Before applicable errors are issued, the IProblemRequestor being used should be wrapped in an
 * instance of this class. If the 'targetBinding' is a DataItemBinding, calls to acceptProblem()
 * will result in a warning instead of an error message on the DataItem part, and an annotation of type
 * EGLDataItemPropertyProblemsAnnotationTypeBinding will be added to the targetBinding which includes
 * the text of the message. 
 *
 */
public class DataItemPropertiesProblemsProblemRequestor extends DefaultProblemRequestor {

	private IProblemRequestor baseProblemRequestor;
	private IBinding targetBinding;
	private IAnnotationTypeBinding annotationType;

	public DataItemPropertiesProblemsProblemRequestor(IProblemRequestor baseProblemRequestor, IBinding targetBinding, IAnnotationTypeBinding annotationType) {
		this.baseProblemRequestor = baseProblemRequestor;
		this.targetBinding = targetBinding;
		this.annotationType = annotationType;
	}

	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
		int trueSeverity = severity;
		if(isDataItemBinding(targetBinding) && IMarker.SEVERITY_ERROR == severity) {
			trueSeverity = IMarker.SEVERITY_WARNING;
			appendProblem(problemKind, inserts);
		}
		baseProblemRequestor.acceptProblem(startOffset, endOffset, trueSeverity, problemKind, inserts);
	}

	private void appendProblem(int problemKind, String[] inserts) {
		IPartBinding dataItemBinding = (IPartBinding) targetBinding;
		
		IAnnotationBinding propertyProblemsABinding = dataItemBinding.getAnnotation(EGLDataItemPropertyProblemsAnnotationTypeBinding.getInstance());
		if(propertyProblemsABinding == null) {
			propertyProblemsABinding = new AnnotationBinding(EGLDataItemPropertyProblemsAnnotationTypeBinding.caseSensitiveName, dataItemBinding, EGLDataItemPropertyProblemsAnnotationTypeBinding.getInstance());
			dataItemBinding.addAnnotation(propertyProblemsABinding);
		}
		
		IAnnotationBinding newProblemABinding = createProblemAnnotationBinding(dataItemBinding, problemKind, inserts);
		
		IAnnotationBinding[] propertyProblemABindings = (IAnnotationBinding[]) propertyProblemsABinding.getValue();
		if(propertyProblemABindings == null) {
			propertyProblemABindings = new IAnnotationBinding[] {newProblemABinding};
		}
		else {
			IAnnotationBinding[] newPropertyProblemABindings = new IAnnotationBinding[propertyProblemABindings.length+1];
			System.arraycopy(propertyProblemABindings, 0, newPropertyProblemABindings, 0, propertyProblemABindings.length);
			newPropertyProblemABindings[newPropertyProblemABindings.length-1] = newProblemABinding;
			propertyProblemABindings = newPropertyProblemABindings;
		}		
		
		propertyProblemsABinding.setValue(propertyProblemABindings, null, null, null, false);
	}

	private IAnnotationBinding createProblemAnnotationBinding(IPartBinding dataItemBinding, int problemKind, String[] inserts) {
		IAnnotationBinding aBinding = new AnnotationBinding(EGLDataItemPropertyProblemAnnotationTypeBinding.caseSensitiveName, dataItemBinding, EGLDataItemPropertyProblemAnnotationTypeBinding.getInstance());
		
		IAnnotationBinding fieldBinding = new AnnotationFieldBinding(EGLDataItemPropertyProblemAnnotationTypeBinding.QUALIFIED_DATAITEM_NAME, dataItemBinding, PrimitiveTypeBinding.getInstance(Primitive.STRING), EGLDataItemPropertyProblemAnnotationTypeBinding.getInstance());
		fieldBinding.setValue(dataItemBinding.getPackageQualifiedName(), null, null, null, false);
		aBinding.addField(fieldBinding);
		
		fieldBinding = new AnnotationFieldBinding(EGLDataItemPropertyProblemAnnotationTypeBinding.ANNOTATION_NAME, dataItemBinding, PrimitiveTypeBinding.getInstance(Primitive.STRING), EGLDataItemPropertyProblemAnnotationTypeBinding.getInstance());
		fieldBinding.setValue(annotationType.getPackageQualifiedName(), null, null, null, false);
		aBinding.addField(fieldBinding);
		
		fieldBinding = new AnnotationFieldBinding(EGLDataItemPropertyProblemAnnotationTypeBinding.ERROR_MSG, dataItemBinding, PrimitiveTypeBinding.getInstance(Primitive.STRING), EGLDataItemPropertyProblemAnnotationTypeBinding.getInstance());
		fieldBinding.setValue(getMessageFromBundle(problemKind, inserts), null, null, null, false);
		aBinding.addField(fieldBinding);
		return aBinding;
	}

	private boolean isDataItemBinding(IBinding targetBinding2) {
		return targetBinding.isTypeBinding() && ITypeBinding.DATAITEM_BINDING == ((ITypeBinding) targetBinding).getKind();
	}
}

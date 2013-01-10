/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.ConstructorInvocation;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Literal;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class CommonUtilities {

	public static boolean hasSideEffects(Expression expr, EglContext ctx) {
		return (new CheckSideEffects()).checkSideEffect(expr, ctx);
	}

	public static class CheckSideEffects extends AbstractVisitor {
		boolean has = false;
		EglContext ctx;
		
		public boolean checkSideEffect(Expression expr, EglContext ctx) {
			this.ctx = ctx;
			disallowRevisit();
			setReturnData(false);
			expr.accept(this);
			return (Boolean)getReturnData();
		}
		public boolean visit(EObject obj) {
			return false;
		}
		public boolean visit(Expression expr) {
			if (has) return false;
			return true;
		}
		public boolean visit(NewExpression expr) {
			has = true;
			setReturnData(has);
			return true;
		}
		public boolean visit(Assignment expr) {
			has = true;
			setReturnData(has);
			return true;
		}
		public boolean visit(FunctionInvocation expr) {
			boolean altered = false;
			// we need to scan the function arguments for any conditions that require temporary variables to be set
			// up. Things like IN args, INOUT args with java primitives, OUT arg initialization, etc. We also need to
			// remember when this statement has already been processed for function invocations, and ignore on
			// subsequent attempts
			// first determine whether we are going to modify the argument and set up pre/post assignments
			for (int i = 0; i < expr.getTarget().getParameters().size(); i++) {
				if (CommonUtilities.isArgumentToBeAltered(expr.getTarget().getParameters().get(i), expr.getArguments().get(i), ctx)) {
					altered = true;
				}
			}
			// if no work needs to be done, continue with the visiting
			if (!altered)
				return true;
			has = true;
			setReturnData(has);
			return false;
		}
		public boolean visit(DelegateInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
		public boolean visit(QualifiedFunctionInvocation expr) {
			boolean altered = false;
			// we need to scan the function arguments for any conditions that require temporary variables to be set
			// up. Things like IN args, INOUT args with java primitives, OUT arg initialization, etc. We also need to
			// remember when this statement has already been processed for function invocations, and ignore on
			// subsequent attempts
			// first determine whether we are going to modify the argument and set up pre/post assignments
			for (int i = 0; i < expr.getTarget().getParameters().size(); i++) {
				if (CommonUtilities.isArgumentToBeAltered(expr.getTarget().getParameters().get(i), expr.getArguments().get(i), ctx)) {
					altered = true;
				}
			}
			// if no work needs to be done, continue with the visiting
			if (!altered)
				return true;
			has = true;
			setReturnData(has);
			return false;
		}
	}
	
	public static boolean isExpressionStatementNeedingGeneration(Expression expr, EglContext ctx) {
		return (new CheckExpressionStatementNeedingGeneration()).checkForGeneration(expr, ctx);
	}

	public static class CheckExpressionStatementNeedingGeneration extends AbstractVisitor {
		boolean has = false;
		EglContext ctx;
		
		public boolean checkForGeneration(Expression expr, EglContext ctx) {
			this.ctx = ctx;
			disallowRevisit();
			setReturnData(false);
			expr.accept(this);
			return (Boolean)getReturnData();
		}
		public boolean visit(EObject obj) {
			return false;
		}
		public boolean visit(Expression expr) {
			if (has) return false;
			return true;
		}
		public boolean visit(NewExpression expr) {
			has = true;
			setReturnData(has);
			return true;
		}
		public boolean visit(Assignment expr) {
			has = true;
			setReturnData(has);
			return true;
		}
		public boolean visit(ConstructorInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
		public boolean visit(FunctionInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
		public boolean visit(DelegateInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
		public boolean visit(QualifiedFunctionInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
	}
	
	public static boolean hasLocalVariableSideEffects(Expression expr, EglContext ctx) {
		return (new CheckLocalVariableSideEffects()).checkLocalVariableSideEffect(expr, ctx);
	}

	public static class CheckLocalVariableSideEffects extends AbstractVisitor {
		boolean has = false;
		EglContext ctx;

		public boolean checkLocalVariableSideEffect(Expression expr, EglContext ctx) {
			this.ctx = ctx;
			disallowRevisit();
			setReturnData(false);
			expr.accept(this);
			return (Boolean) getReturnData();
		}

		public boolean visit(EObject obj) {
			return false;
		}

		public boolean visit(Expression expr) {
			if (has)
				return false;
			return true;
		}

		public boolean visit(FunctionInvocation expr) {
			processInvocation(expr);
			return false;
		}

		public boolean visit(DelegateInvocation expr) {
			processInvocation(expr);
			return false;
		}

		public boolean visit(QualifiedFunctionInvocation expr) {
			processInvocation(expr);
			return false;
		}

		private void processInvocation(InvocationExpression object) {
			// we need to scan the function arguments for any conditions that require temporary variables to be set
			// up. Things like IN args, INOUT args with java primitives, OUT arg initialization, etc.
			for (int i = 0; i < object.getTarget().getParameters().size(); i++) {
				if (isArgumentToBeAltered(object.getTarget().getParameters().get(i), object.getArguments().get(i), ctx)) {
					has = true;
					setReturnData(has);
					break;
				}
			}
			return;
		}
	}

	public static Expression hasBoxingExpression(Expression expr) {
		return (new CheckForBoxingExpression()).checkForBoxingExpression(expr);
	}

	public static class CheckForBoxingExpression extends AbstractVisitor {
		boolean has = false;

		public Expression checkForBoxingExpression(Expression expr) {
			disallowRevisit();
			setReturnData(null);
			expr.accept(this);
			return (Expression) getReturnData();
		}

		public boolean visit(EObject obj) {
			return false;
		}

		public boolean visit(Expression expr) {
			if (has)
				return false;
			return true;
		}

		public boolean visit(BoxingExpression expr) {
			has = true;
			setReturnData(expr);
			return false;
		}
	}

	public static boolean isArgumentToBeAltered(FunctionParameter parameter, Expression expression, EglContext ctx) {
		if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
			// if the parameter is reference then do not make a temporary
			if (TypeUtils.isReferenceType(parameter.getType()))
				return false;
			// if nullable, or not java primitive, then create a temporary
			if (parameter.isNullable() || expression.isNullable() || !ctx.mapsToPrimitiveType(parameter.getType()))
				// if the parameter is a const then we should not make a copy
				return !parameter.isConst();
			// if the argument and parameter types mismatch
			if (!parameter.getType().equals(expression.getType())) {
				// if the parameter is a const then we should not make a copy
				if (parameter.isConst())
					return false;
				// if the argument is a literal then we should not make a copy
				if (expression instanceof Literal)
					return false;
				return true;
			}
			return false;
		} else {
			// if the parameter is a const we should not make a copy
			return isBoxedParameterType(parameter, ctx) && !parameter.isConst();
		}
	}

	public static boolean isBoxedParameterType(FunctionParameter parameter, EglContext ctx) {
		if (parameter.getParameterKind() == ParameterKind.PARM_INOUT) {
			if (TypeUtils.isReferenceType(parameter.getType()))
				return true;
			if (ctx.mapsToPrimitiveType(parameter.getType()))
				return true;
			if (parameter.isNullable())
				return true;
		} else if (parameter.getParameterKind() == ParameterKind.PARM_OUT)
			return true;
		return false;
	}

	public static Annotation includeEndOffset(Annotation annotation, EglContext ctx) {
		if (annotation != null) {
			annotation.addAnnotation(ctx.getFactory().createAnnotation(EGLMessage.IncludeEndOffset));
		}
		return annotation;
	}
	public static void addGeneratorAnnotation(Element element, Annotation annot, EglContext ctx){
		if(annot != null){
			@SuppressWarnings("unchecked")
			Map<String, Annotation> annotations = (Map<String, Annotation>)ctx.getAttribute(element, Constants.SubKey_GeneratorAnnotations);
			if(annotations == null){
				annotations = new HashMap<String, Annotation>();
				ctx.putAttribute(element, Constants.SubKey_GeneratorAnnotations, annotations);
			}
			annotations.put(annot.getEClass().getETypeSignature(), annot);
		}
	}
	public static Annotation getAnnotation(Element element, String annotationSignature, EglContext ctx){
		//check the context first because array will add the subtype to the field on the context
		Annotation annotation = null;
		@SuppressWarnings("unchecked")
		Map<String, Annotation> annotations = ((Map<String, Annotation>)ctx.getAttribute(element, Constants.SubKey_GeneratorAnnotations));
		if(annotations != null){
			annotation = annotations.get(annotationSignature);
		}
		if(annotation == null){
			annotation = element.getAnnotation(annotationSignature);
		}
		return annotation;
	}
	public static List<Annotation> getAnnotations(Element element, EglContext ctx){
		//check the context first because array will add the subtype to the field on the context
		List<Annotation> annotations = new ArrayList<Annotation>();
		List<String> annotationTypes = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		Map<String, Annotation> generatorAnnotations = ((Map<String, Annotation>)ctx.getAttribute(element, Constants.SubKey_GeneratorAnnotations));
		if(generatorAnnotations != null){
			annotations.addAll(generatorAnnotations.values());
			for(Annotation annot : annotations){
				annotationTypes.add(annot.getEClass().getETypeSignature());
			}
		}
		if(element.getAnnotations() != null){
			for(Annotation annot : element.getAnnotations()){
				if(!annotationTypes.contains(annot.getEClass().getETypeSignature())){
					annotationTypes.add(annot.getEClass().getETypeSignature());
					annotations.add(annot);
				}
			}
		}
		return annotations;
	}

	public static Annotation annotationNewInstance(EglContext ctx, String key) throws MofObjectNotFoundException, DeserializationException {
		EObject eObject = Environment.getCurrentEnv().find(key);
		return newInstance(eObject);
	}
	

	public static Annotation newInstance(EObject eObject) {
		if (eObject instanceof StereotypeType && (eObject = ((StereotypeType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		} else if (eObject instanceof AnnotationType && (eObject = ((AnnotationType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		}
		return null;
	}
}

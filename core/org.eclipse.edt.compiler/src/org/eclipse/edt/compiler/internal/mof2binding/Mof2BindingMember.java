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
package org.eclipse.edt.compiler.internal.mof2binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.ClassConstantBinding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.ConstantFormFieldBinding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ProgramParameterBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.SystemFunctionParameterSpecialTypeBinding;
import org.eclipse.edt.compiler.binding.VariableFormFieldBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLSystemParameterTypesAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.ETypedElement;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.ConstantFormField;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FloatingPointLiteral;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Literal;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.PrimitiveTypeLiteral;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.TextTypeLiteral;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.VariableFormField;
import org.eclipse.edt.mof.egl.lookup.ProxyPart;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.utils.EList;


public abstract class Mof2BindingMember extends Mof2BindingPart {

	public Mof2BindingMember(IBindingEnvironment env) {
		super(env);
	}
	
	public boolean visit(ConstantField field) {
		IBinding binding = getBinding(field);
		if (binding == null) {
			String name = InternUtil.intern(field.getName());
			ITypeBinding type = bindingForType(field);
			field.getContainer().accept(this);
			IPartBinding declarer = (IPartBinding)stack.pop();
			Object value = null;
			if (field.getValue() != null) {
				value = field.getValue().getObjectValue();
			}
			binding = new ClassConstantBinding(name, declarer, type, value);
			((ClassConstantBinding)binding).setIsStatic(field.isStatic());
			((ClassConstantBinding)binding).setIsPrivate(field.getAccessKind() == AccessKind.ACC_PRIVATE);
			handleAnnotations(field, binding);
			putBinding(field, binding);
		}
		stack.push(binding);
		return false;
	}

	public boolean visit(Field field) {
		IBinding binding = getBinding(field);
		if (binding == null) {
			String name = InternUtil.intern(field.getName());
			ITypeBinding type = bindingForType(field);
			field.getContainer().accept(this);
			IPartBinding declarer = (IPartBinding)stack.pop();
			if (field.getContainer() instanceof Record) {
				binding = new FlexibleRecordFieldBinding(name, declarer, type);
			}
			else {
				binding = new ClassFieldBinding(name, declarer, type);
				((ClassFieldBinding)binding).setIsStatic(field.isStatic());
				((ClassFieldBinding)binding).setIsPrivate(field.getAccessKind() == AccessKind.ACC_PRIVATE);
			}
			handleAnnotations(field, binding);
			putBinding(field, binding);
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(StructuredField field) {
		StructureItemBinding binding = (StructureItemBinding)getBinding(field);
		if (binding == null) {
			String name = InternUtil.intern(field.getName());
			ITypeBinding type = bindingForType(field);
			field.getContainer().accept(this);
			IPartBinding declarer = (IPartBinding)stack.pop();
			binding = new StructureItemBinding(name, declarer, type);
			binding.setOccurs(field.getOccurs());
			handleAnnotations(field, binding);
			putBinding(field, binding);
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(VariableFormField field) {
		VariableFormFieldBinding binding = (VariableFormFieldBinding)getBinding(field);
		if (binding == null) {
			String name = InternUtil.intern(field.getName());
			ITypeBinding type = bindingForType(field);
			field.getContainer().accept(this);
			IPartBinding declarer = (IPartBinding)stack.pop();
			binding = new VariableFormFieldBinding(name, declarer, type);
			binding.setOccurs(field.getOccurs());
			handleAnnotations(field, binding);
			handleElementAnnotations(field, binding);
			putBinding(field, binding);
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(ConstantFormField field) {
		ConstantFormFieldBinding binding = (ConstantFormFieldBinding)getBinding(field);
		if (binding == null) {
			String name = InternUtil.intern(field.getName());
			field.getContainer().accept(this);
			IPartBinding declarer = (IPartBinding)stack.pop();
			binding = new ConstantFormFieldBinding(declarer);
			binding.setOccurs(field.getOccurs());
			handleAnnotations(field, binding);
			handleElementAnnotations(field, binding);
			putBinding(field, binding);
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(Constructor constructor) {
		IBinding binding = getBinding(constructor);
		if (binding == null) {
			IPartBinding type = (IPartBinding)getBinding(constructor.getContainer());
			if (type == null) {
				constructor.getContainer().accept(this);
				type = (IPartBinding)stack.pop();
			}
			binding = new ConstructorBinding((IPartBinding)type);
			
			((ConstructorBinding)binding).setPrivate(constructor.getAccessKind() == AccessKind.ACC_PRIVATE);
			
			putBinding(constructor, ((ConstructorBinding)binding).getType());
			for (FunctionParameter parm : constructor.getParameters()) {
				parm.accept(this);
				((ConstructorBinding)binding).addParameter((FunctionParameterBinding)stack.pop());
			}

			handleAnnotations(constructor, binding);
		}
		
		stack.push(binding);
		return false;
	}
	

	public boolean visit(Function function) {
		IBinding binding = getBinding(function);
		if (binding == null) {
			String name = InternUtil.intern(function.getName());
			IPartBinding type = (IPartBinding)getBinding(function.getContainer());
			if (type == null) {
				function.getContainer().accept(this);
				type = (IPartBinding)stack.pop();
			}
			binding = new FunctionBinding(name, (IPartBinding)type);
			putBinding(function, binding);
			for (FunctionParameter parm : function.getParameters()) {
				parm.accept(this);
				((FunctionBinding)binding).addParameter((FunctionParameterBinding)stack.pop());
			}
			if (function.getType() != null) {
				((FunctionBinding)binding).setReturnType(bindingForType(function));
			}
			
			((FunctionBinding)binding).setStatic(function.isStatic());
			((FunctionBinding)binding).setPrivate(function.getAccessKind() == AccessKind.ACC_PRIVATE);
			handleAnnotations(function, binding);
			
			overrideFunctionParameterTypes((FunctionBinding)binding);   // TODO temporary workaround
			
			binding = new NestedFunctionBinding(name, type, (FunctionBinding)binding);
		}
		stack.push(binding);
		return false;
	}
	
	//Temporary workaround! remove ASAP
	private void overrideFunctionParameterTypes(FunctionBinding function) {
		IAnnotationBinding ann = function.getAnnotation(EGLSystemParameterTypesAnnotationTypeBinding.getInstance());
		if (ann != null) {
			overrideParmTypes((Object[]) ann.getValue(), function);
		}
	}
	
	private void overrideParmTypes(Object[] parmMap, FunctionBinding function) {
		for (int i = 0; i < parmMap.length; i++) {
			String value = parmMap[i].toString();
			if (value.length() > 0) {
				SystemFunctionParameterSpecialTypeBinding newType = SystemFunctionParameterSpecialTypeBinding.getType(value);
				if (newType != null) {
					((FunctionParameterBinding)function.getParameters().get(i)).setType(newType);
				}
			}
		}
	}
	
	public boolean visit(FunctionParameter parm) {
		IBinding binding = getBinding(parm);
		if (binding == null) {
			String name = InternUtil.intern(parm.getName());
			ITypeBinding type = bindingForType(parm);
			
			IPartBinding declarer;
			FunctionBinding func;
			if (parm.getContainer() instanceof Delegate) {
				declarer = (IPartBinding)getBinding(parm.getContainer());				
				func = null;
			}
			else {
				declarer = (IPartBinding)getBinding(((Member)parm.getContainer()).getContainer());				
				func = (FunctionBinding)getBinding(parm.getContainer());
			}
			binding = new FunctionParameterBinding(name, declarer, type, func);
			
			if (parm.getParameterKind() == ParameterKind.PARM_IN) {
				((FunctionParameterBinding)binding).setInput(true);
			}
			else if (parm.getParameterKind() == ParameterKind.PARM_OUT) {
				((FunctionParameterBinding)binding).setOutput(true);
			}
			
			putBinding(parm, binding);
		}
		stack.push(binding);
		return false;
	}
	public boolean visit(ProgramParameter parm) {
		IBinding binding = getBinding(parm);
		if (binding == null) {
			String name = InternUtil.intern(parm.getName());
			parm.getType().accept(this);
			ITypeBinding type = (ITypeBinding)stack.pop();
			
			IPartBinding declarer;
			declarer = (IPartBinding)getBinding(parm.getContainer());				
			binding = new ProgramParameterBinding(name, declarer, type);
			
			putBinding(parm, binding);
		}
		stack.push(binding);
		return false;
	}

	
	public boolean visit(EField field) {
		IBinding binding = getBinding(field);
		if (binding == null) {
			String name = InternUtil.intern(field.getName());
			field.getDeclarer().accept(this);
			IPartBinding declarer = (IPartBinding)stack.pop();
			ITypeBinding type = bindingForType(field, declarer);
			if (declarer instanceof FlexibleRecordBinding) {
				binding = new FlexibleRecordFieldBinding(name, declarer, type);
				
				Object initialValue = field.getInitialValue();
				if (initialValue instanceof Literal) {
					initialValue = getLiteralValue((Literal) initialValue);
				}
				else {
					if (initialValue instanceof EObject) {
						((EObject)field.getInitialValue()).accept(this);
						initialValue = stack.pop();
					}
				}
				((FlexibleRecordFieldBinding)binding).setInitialValue(initialValue);
			}
			else if (declarer instanceof EClassBinding) {
				binding = new EFieldBinding(name, declarer, type);
				((EFieldBinding)binding).setTransient(field.isTransient());
				((EFieldBinding)binding).setContainment(field.getContainment());
			}
			else {
				binding = new ClassFieldBinding(name, declarer, type);
			}
			handleMetadata(field, binding);
			putBinding(field, binding);
		}
		stack.push(binding);
		return false;
	}

	public boolean visit(EFunction function) {
		IBinding binding = getBinding(function);
		if (binding == null) {
			String name = InternUtil.intern(function.getName());
			ITypeBinding type = null;
			function.getDeclarer().accept(this);
			IPartBinding declarer = (IPartBinding)stack.pop();
			if (function.getEType() != null) {
			 	type = bindingForType(function, declarer);
			}
			binding = new EFunctionBinding(name, declarer);
			putBinding(function, binding);
			for (EParameter parm : function.getEParameters()) {
				parm.accept(this);
				((FunctionBinding)binding).addParameter((EParameterBinding)stack.pop());
			}
			if (type != null) {
				((FunctionBinding)binding).setReturnType(type);
			}

			binding = new NestedFunctionBinding(name, declarer, (FunctionBinding)binding);
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(EParameter parm) {
		IBinding binding = getBinding(parm);
		String name = InternUtil.intern(parm.getName());
		
		ITypeBinding type = null;
		if (parm.getEType() != null) {
			parm.getEType().accept(this);
			type = (ITypeBinding)stack.pop();
		}
		IPartBinding declarer = (IPartBinding)getBinding(parm.getFunction().getDeclarer());
		FunctionBinding func = (FunctionBinding)getBinding(parm.getFunction());
		binding = new EParameterBinding(name, declarer, type, func);
		putBinding(parm, binding);
		stack.push(binding);
		return false;
	}

	public boolean visit(EDataType type) {
		IBinding binding;
		
		String key = type.getMofSerializationKey();
		// Assume the only EDatatypes there are that have EGL definitions
		// come from StereotypeType and AnnotationType declarations that
		// can only use the basic types below or Lists
		if (key.equals(MofFactory.INSTANCE.getEStringEDataType().getMofSerializationKey())) {
			binding = PrimitiveTypeBinding.getInstance(Primitive.STRING);
		}
		else if (key.equals(MofFactory.INSTANCE.getEIntEDataType().getMofSerializationKey())) {
			binding = PrimitiveTypeBinding.getInstance(Primitive.INT);
		}
		else if (key.equals(MofFactory.INSTANCE.getEBooleanEDataType().getMofSerializationKey())) {
			binding = PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN);
		}
		else {
			binding = PrimitiveTypeBinding.getInstance(Primitive.ANY);
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(EGenericType type) {
		// Assumes Dynamic arrays are only type of EGenericTypes
		// that can come from EGL definitions
		type.getETypeArguments().get(0).accept(this);
		ITypeBinding elementType = (ITypeBinding)stack.pop();
		ArrayTypeBinding binding = ArrayTypeBinding.getInstance(elementType);
		stack.push(binding);
		return false;
	}
	
	
	private ITypeBinding bindingForType(TypedElement element) {
		ITypeBinding binding;
		
		if (element instanceof ProxyPart) {
			binding = createProxyBinding((ProxyPart)element);
		}
		else {	
			element.getType().accept(this);
			binding = (ITypeBinding)stack.pop();
		}

		if (element.isNullable()) {
			binding = binding.getNullableInstance();
		}
		return binding;
	}
	
	private ITypeBinding bindingForType(ETypedElement element, ITypeBinding declarer) {
		ITypeBinding binding = null;
		if (declarer instanceof FlexibleRecordBinding && isEGLReflectType(element.getEType().getEClassifier())) {
			binding = getEGLReflectTypeFor((EClass)element.getEType().getEClassifier());
		}
		else { 
			element.getEType().accept(this);
			binding = (ITypeBinding)stack.pop();
		}
		if (element.isNullable()) {
			binding = binding.getNullableInstance();
		}
		return binding;
	}

	
	private boolean isEGLReflectType(EType type) {
		if (type.getEClassifier() instanceof EClass) {
			EClass eClass = (EClass)type.getEClassifier();
			String typeSignature = eClass.getETypeSignature();
			return typeSignature.equals(Type_EField)
					|| typeSignature.equals(Type_EClass)
					|| typeSignature.equals(Type_EClassifier)
					|| typeSignature.equals(Type_EFunction);
		}
		else {
			return false;
		}
	}
	
	private ITypeBinding getEGLReflectTypeFor(EClass eClass) {
		IPartBinding binding = null;
		String typeSignature = eClass.getETypeSignature();
		if (typeSignature.equals(Type_EField)) {
			binding = SystemPartManager.FIELDREF_BINDING;
		}
		else if (typeSignature.equals(Type_EClass)) {
			binding = SystemPartManager.TYPEREF_BINDING;
		}
		else if (typeSignature.equals(Type_EClassifier)) {
			binding = SystemPartManager.TYPEREF_BINDING;
		}
		else if (typeSignature.equals(Type_EFunction)) {
			binding = SystemPartManager.FUNCTIONREF_BINDING;
		}
		return binding;
	}
	
	private Object getLiteralValue(Literal lit) {
		if (lit instanceof BooleanLiteral) {
			return Boolean.toBoolean(((BooleanLiteral)lit).booleanValue().booleanValue());
		}
		
		if (lit instanceof NullLiteral) {
			return null;
		}
		
		if (lit instanceof TextTypeLiteral) {
			return ((TextTypeLiteral)lit).getValue();
		}
		
		if (lit instanceof IntegerLiteral) {
			return new Integer(((IntegerLiteral)lit).getValue());
		}
		
		if (lit instanceof FloatingPointLiteral) {
			return new Float(((FloatingPointLiteral)lit).getValue());
		}
		
		if (lit instanceof PrimitiveTypeLiteral) {
			return ((PrimitiveTypeLiteral)lit).getObjectValue();
		}
		
		if (lit instanceof ArrayLiteral) {
			List<Object> list = new ArrayList<Object>();
			for (Expression exp : ((ArrayLiteral)lit).getEntries()) {
				if (exp instanceof Literal) {
					list.add(getLiteralValue((Literal)exp));
				}
				else {
					list.add(exp);
				}
			}
			return list.toArray(new Object[list.size()]);
		}
		return null;
		
	}

}

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

import java.util.Stack;

import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBindingImpl;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBindingImpl;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.binding.ProgramParameterBinding;
import org.eclipse.edt.compiler.binding.ServiceBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EMetadataType;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.DataTable;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.FormGroup;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Parameter;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.StructuredRecord;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.ProxyEClass;


public class Mof2BindingPart extends Mof2BindingBase {

	Stack<IPartBinding> partStack = new Stack<IPartBinding>();
	
	protected boolean resolvePrimitiveAsExternalTypeBinding = false;

	public Mof2BindingPart(IBindingEnvironment env) {
		super(env);
	}

	@Override
	IPartBinding getPartBinding() {
		return partStack.peek();
	}
	
	public boolean visit(ProxyEClass ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new EClassBinding(packageName, name);
			binding.setValid(true);
			putProxy(binding, ir);
			putBinding(ir, binding);
		}
		partStack.push(binding);
		return false;
	}
	
	private ITypeBinding checkForPrimiveType(StructPart ir) {

		//unless told otherwise, attempt to resolve this as a primitiveTypeBinding
		if (!resolvePrimitiveAsExternalTypeBinding) {
			// Handle references to Primitive type class bindings
			
			if (ir instanceof ParameterizableType) {
				handleParameterizableType((ParameterizableType)ir);
			}
			else {
				super.visit(ir);
			}				
			return (ITypeBinding)stack.pop();
		}
		resolvePrimitiveAsExternalTypeBinding = false;
		return null;
	}
	
	public boolean visit(EGLClass ir) {
		ITypeBinding binding = checkForPrimiveType(ir);

		if (binding == null) {
			binding = (IPartBinding)getBinding(ir);
	
			if (binding == null) {
				
				if (binding == null) {
					String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
					String name = InternUtil.internCaseSensitive(ir.getName());
					binding = new ClassBinding(packageName, name);
					((ClassBinding)binding).setValid(true);
					partStack.push((IPartBinding)binding);
					handleVisitLogicAndDataPart(ir, (ClassBinding)binding);
					partStack.pop();				
				}
			}
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(StructPart ir) {
		ITypeBinding binding = checkForPrimiveType(ir);

		if (binding == null) {
			binding = (IPartBinding)getBinding(ir);
	
			if (binding == null) {
				
				if (binding == null) {
					String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
					String name = InternUtil.internCaseSensitive(ir.getName());
					binding = new ClassBinding(packageName, name);
					((ClassBinding)binding).setValid(true);
					partStack.push((IPartBinding)binding);
					handleVisitStructPart(ir, (ClassBinding)binding);
					partStack.pop();				
				}
			}
		}
		stack.push(binding);
		return false;
	}


	public boolean visit(Program ir) {
		ProgramBinding binding = (ProgramBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new ProgramBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitLogicAndDataPart((LogicAndDataPart)ir, (FunctionContainerBinding)binding);
			for (ProgramParameter parm : ir.getParameters()) {
				parm.accept(this);
				binding.addParameter((ProgramParameterBinding)stack.pop());
			}
			binding.setCallable(ir.isCallable());
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(Handler ir) {
		HandlerBinding binding = (HandlerBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new HandlerBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitLogicAndDataPart((LogicAndDataPart)ir, (FunctionContainerBinding)binding);
			for (Interface iface : ir.getInterfaces()) {
				iface.accept(this);
				binding.addExtenedInterface((InterfaceBinding)stack.pop());
			}

			for (StructPart supertype: ir.getSuperTypes()) {
				if (supertype instanceof Interface) {
					supertype.accept(this);
					binding.addExtenedInterface((InterfaceBinding)stack.pop());
				}
			}			
			
			for (Constructor constructor : ir.getConstructors()) {
				constructor.accept(this);
				binding.addConstructor((ConstructorBinding)stack.pop());
			}

			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(Library ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new LibraryBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitLogicAndDataPart((LogicAndDataPart)ir, (FunctionContainerBinding)binding);
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(Service ir) {
		ServiceBinding binding = (ServiceBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new ServiceBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitLogicAndDataPart((LogicAndDataPart)ir, (FunctionContainerBinding)binding);
			for (Interface iface : ir.getInterfaces()) {
				iface.accept(this);
				binding.addExtenedInterface((InterfaceBinding)stack.pop());
			}
			for (StructPart supertype: ir.getSuperTypes()) {
				if (supertype instanceof Interface) {
					supertype.accept(this);
					binding.addExtenedInterface((InterfaceBinding)stack.pop());
				}
			}

			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	

	public boolean visit(Interface ir) {
		InterfaceBinding binding = (InterfaceBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new InterfaceBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitLogicAndDataPart(ir, (InterfaceBinding)binding);

			for (Interface iface : ir.getInterfaces()) {
				iface.accept(this);
				binding.addExtendedType((InterfaceBinding)stack.pop());
			}
			
			for (StructPart supertype: ir.getSuperTypes()) {
				if (supertype instanceof Interface) {
					supertype.accept(this);
					binding.addExtendedType((InterfaceBinding)stack.pop());
				}
			}

			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(ExternalType ir) {
		ExternalTypeBinding binding = (ExternalTypeBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new ExternalTypeBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitLogicAndDataPart(ir, (ExternalTypeBinding)binding);
			for (StructPart type : ir.getSuperTypes()) {
				type.accept(this);
				
				IBinding aBinding = (IBinding) stack.pop();
				
				//Sometimes, the primitive type instances must be treated as ExternalTypes!
				if (aBinding instanceof PrimitiveTypeBinding) {
					resolvePrimitiveAsExternalTypeBinding = true;
					type.accept(this);
					resolvePrimitiveAsExternalTypeBinding = false;
					aBinding = (IBinding) stack.pop();
				}
				
				binding.addExtendedType((ITypeBinding)aBinding);
			}		
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	

	public boolean visit(Record ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new FlexibleRecordBindingImpl(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleDataPart(ir, (FlexibleRecordBinding)binding);
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	private void reParentFieldBindings(StructuredField field) {
		StructureItemBinding parent = (StructureItemBinding)getBinding(field);
		for (StructuredField f : field.getChildren()) {
			StructureItemBinding child = (StructureItemBinding)getBinding(f);
			parent.addChild(child);
			reParentFieldBindings(f);
		}		
	}
	
	public boolean visit(StructuredRecord ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new FixedRecordBindingImpl(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleDataPart(ir, (FixedRecordBinding)binding);
			partStack.pop();
			for (StructuredField field : ir.getStructuredFields()) {
				reParentFieldBindings(field);
			}
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(FormGroup ir) {
		FormGroupBinding binding = (FormGroupBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new FormGroupBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitPart(ir, binding);
			for (Form form : ir.getForms()) {
				form.accept(this);
				binding.addNestedForm(binding);
			}
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(Form ir) {
		IBinding binding = getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			FormGroupBinding parent = (FormGroupBinding)getBinding(ir.getContainer());
			binding = new FormBinding(packageName, name, parent);
			((FormBinding)binding).setValid(true);
			partStack.push((FormBinding)binding);
			handleDataPart(ir, (FormBinding)binding);
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(Delegate ir) {
		DelegateBinding binding = (DelegateBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new DelegateBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitPart(ir, binding);
			partStack.pop();
			for (Parameter parm : ir.getParameters()) {
				parm.accept(this);
				binding.addParameter((FunctionParameterBinding)stack.pop());
			}
			if (ir.getReturnType() != null) {
				ir.getReturnType().accept(this);
				ITypeBinding typeBinding = (ITypeBinding)stack.pop();
				if (ir.isNullable()) {
					typeBinding = typeBinding.getNullableInstance();
				}
				binding.setReturnType(typeBinding);
				// TODO: handle isSqlNullable on return type
			}
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(FunctionPart ir) {
		TopLevelFunctionBinding binding = (TopLevelFunctionBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.intern(ir.getName());
			binding = new TopLevelFunctionBinding(packageName, name);
			binding.setValid(true);
			handleVisitPart(ir, binding);
			for (Parameter parm : ir.getFunction().getParameters()) {
				parm.accept(this);
				binding.addParameter((FunctionParameterBinding)stack.pop());
			}
			if (ir.getFunction().getType() != null) {
				ir.getFunction().getType().accept(this);
				binding.setReturnType((ITypeBinding)stack.pop());
				// TODO: handle isSqlNullable on return type
			}
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(DataTable ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new DataTableBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleDataPart(ir, (DataTableBinding)binding);
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(DataItem ir) {
		DataItemBinding binding = (DataItemBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new DataItemBinding(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			handleVisitPart(ir, binding);
			ir.getBaseType().accept(this);
			binding.setPrimitiveTypeBinding((PrimitiveTypeBinding)stack.pop());
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(StereotypeType ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new FlexibleRecordBindingImpl(packageName, name);
			binding.setValid(true);
			putBinding(ir,  binding);
			partStack.push(binding);
			handleAnnotations(ir, binding);
			handleMetadata(ir, binding);
			for (EField field : ir.getEFields()) {
				field.accept(this);
				((FlexibleRecordBinding)binding).addField((FlexibleRecordFieldBinding)stack.pop());
			}
			IAnnotationBinding subtype = createStereotypeSubtypeFor(ir);
			binding.addAnnotation(subtype);
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(AnnotationType ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new FlexibleRecordBindingImpl(packageName, name);
			binding.setValid(true);
			partStack.push(binding);
			putBinding(ir,  binding);
			handleAnnotations(ir, binding);
			handleMetadata(ir, binding);
			for (EField field : ir.getEFields()) {
				field.accept(this);
				((FlexibleRecordBinding)binding).addField((FlexibleRecordFieldBinding)stack.pop());
			}
			IAnnotationBinding subtype = createAnnotationSubtypeFor(ir);
			binding.addAnnotation(subtype);
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}

	public boolean visit(EMetadataType ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new FlexibleRecordBindingImpl(packageName, name);
			binding.setValid(true);
			putBinding(ir,  binding);
			partStack.push(binding);
			for (EField field : ir.getEFields()) {
				field.accept(this);
				((FlexibleRecordBinding)binding).addField((FlexibleRecordFieldBinding)stack.pop());
			}
			IAnnotationBinding subtype = createAnnotationSubtypeFor(ir);
			binding.addAnnotation(subtype);
			partStack.pop();
			
		}
		stack.push(binding);
		return false;
	}

	
	ITypeBinding getSystemPart(EClass aClass) {
		
		String key = aClass.getMofSerializationKey();
		ITypeBinding binding = null;
				
		if (key.equals(Type_FieldRef)) {
			binding = SystemPartManager.FIELDREF_BINDING;
		}
		else if(key.equals(Type_FieldInTargetRef)) {
			binding = SystemPartManager.FIELDINTARGETREF_BINDING;
		}
		else if(key.equals(Type_FunctionRef)) {
			binding = SystemPartManager.FUNCTIONREF_BINDING;
		}
		else if(key.equals(Type_FunctionMemberRef)) {
			binding = SystemPartManager.FUNCTIONMEMBERREF_BINDING;
		}
		else if(key.equals(Type_TypeRef)) {
			binding = SystemPartManager.TYPEREF_BINDING;
		}
		else if(key.equals(Type_RecordRef)) {
			binding = SystemPartManager.RECORDREF_BINDING;
		}
		else if(key.equals(Type_ServiceRef)) {
			binding = SystemPartManager.SERVICEREF_BINDING;
		}
		else if(key.equals(Type_InternalRef)) {
			binding = SystemPartManager.INTERNALREF_BINDING;
		}
		else if(key.equals(Type_SQLStringRef)) {
			binding = SystemPartManager.SQLSTRING_BINDING;			
		}
	
		return binding;
	}

	public boolean visit(EClass ir) {
		
		ITypeBinding sysPartBinding =  getSystemPart(ir);
		if (sysPartBinding != null) {
			stack.push(sysPartBinding);
			return false;
		}
		
		String key = ir.getMofSerializationKey();
		IPartBinding binding = (IPartBinding)getPartBinding(key);
		boolean process = false;
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new EClassBinding(packageName, name);
			binding.setValid(true);
			putPartBinding(key,  binding);
			if (ir instanceof ProxyEClass) {
				putProxy(binding, (ProxyEClass)ir);
			}
			else {
				process = true;
			}
		}
		else if (getProxy(binding) != null && ir != getProxy(binding)) {
			process = true;
			removeProxy(binding);
		}
		
		if (process) {
			partStack.push(binding);
			for (EField field : ir.getEFields()) {
				field.accept(this);
				((EClassBinding)binding).addClassField((EFieldBinding)stack.pop());
			}
			for (EFunction function : ir.getEFunctions()) {
				function.accept(this);
				((EClassBinding)binding).addDeclaredFunction((NestedFunctionBinding)stack.pop());
			}
			for (EClass superType : ir.getSuperTypes()) {
				superType.accept(this);
				((EClassBinding)binding).addExtendedType((ITypeBinding)stack.pop());
			}
			handleMetadata(ir, binding);
			addMofClassAnnotation(binding);
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	public boolean visit(Enumeration ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new EnumerationTypeBinding(packageName, name);
			binding.setValid(true);
			putBinding(ir,  binding);
			partStack.push(binding);
			for (EEnumLiteral field : ir.getEntries()) {
				field.accept(this);
				((EnumerationTypeBinding)binding).addEnumeration((EnumerationDataBinding)stack.pop());
			}			
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(EnumerationEntry ir) {
		IBinding binding = getBinding(ir);
		if (binding == null) {
			String name = InternUtil.intern(ir.getName());
			IPartBinding type = (IPartBinding)getBinding(ir.getContainer());
			binding = new EnumerationDataBinding(name, type, type, ir.getValue());
			putBinding(ir, binding);
		}
		stack.push(binding);
		return false;
	}

	public boolean visit(EEnum ir) {
		IPartBinding binding = (IPartBinding)getBinding(ir);
		if (binding == null) {
			String[] packageName = InternUtil.intern(ir.getPackageName().split("[.]"));
			String name = InternUtil.internCaseSensitive(ir.getName());
			binding = new EnumerationTypeBinding(packageName, name);
			binding.setValid(true);
			putBinding(ir,  binding);
			partStack.push(binding);
			for (EEnumLiteral field : ir.getLiterals()) {
				field.accept(this);
				((EnumerationTypeBinding)binding).addEnumeration((EnumerationDataBinding)stack.pop());
			}			
			partStack.pop();
		}
		stack.push(binding);
		return false;
	}
	
	public boolean visit(EEnumLiteral ir) {
		IBinding binding = getBinding(ir);
		if (binding == null) {
			String name = InternUtil.intern(ir.getName());
			IPartBinding type = (IPartBinding)getBinding(ir.getDeclarer());
			binding = new EnumerationDataBinding(name, type, type, ir.getValue());
			putBinding(ir, binding);
		}
		stack.push(binding);
		return false;
	}

	private void handleVisitPart(Part ir, IPartBinding binding) {
		putBinding(ir,  binding);
		handleAnnotations(ir, binding);
	}
	
	private void handleVisitLogicAndDataPart(LogicAndDataPart ir, FunctionContainerBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Field field : ir.getFields()) {
			field.accept(this);
			binding.addClassField((ClassFieldBinding)stack.pop());
		}
		for (Function function : ir.getFunctions()) {
			function.accept(this);
			binding.addDeclaredFunction((NestedFunctionBinding)stack.pop());
		}
	}
	
	private void handleVisitLogicAndDataPart(LogicAndDataPart ir, ExternalTypeBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Field field : ir.getFields()) {
			field.accept(this);
			binding.addClassField((ClassFieldBinding)stack.pop());
		}
		for (Function function : ir.getFunctions()) {
			function.accept(this);
			binding.addDeclaredFunction((NestedFunctionBinding)stack.pop());
		}
		for (Constructor constructor : ir.getConstructors()) {
			constructor.accept(this);
			binding.addConstructor((ConstructorBinding)stack.pop());
		}
		
		for (StructPart structure : ir.getSuperTypes()) {
			
			structure.accept(this);

			IBinding aBinding = (IBinding) stack.pop();
			
			//Sometimes, the primitive type instances must be treated as ExternalTypes!
			if (aBinding instanceof PrimitiveTypeBinding) {
				resolvePrimitiveAsExternalTypeBinding = true;
				structure.accept(this);
				resolvePrimitiveAsExternalTypeBinding = false;
				aBinding = (IBinding) stack.pop();
			}
			
			binding.addExtendedType((ExternalTypeBinding)aBinding);
			resolvePrimitiveAsExternalTypeBinding = false;
		}
	}

	private void handleVisitStructPart(StructPart ir, ExternalTypeBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Function function : ir.getFunctions()) {
			function.accept(this);
			binding.addDeclaredFunction((NestedFunctionBinding)stack.pop());
		}
		for (Constructor constructor : ir.getConstructors()) {
			constructor.accept(this);
			binding.addConstructor((ConstructorBinding)stack.pop());
		}
		
		for (StructPart structure : ir.getSuperTypes()) {
			
			structure.accept(this);

			IBinding aBinding = (IBinding) stack.pop();
			
			//Sometimes, the primitive type instances must be treated as ExternalTypes!
			if (aBinding instanceof PrimitiveTypeBinding) {
				resolvePrimitiveAsExternalTypeBinding = true;
				structure.accept(this);
				resolvePrimitiveAsExternalTypeBinding = false;
				aBinding = (IBinding) stack.pop();
			}
			
			binding.addExtendedType((ExternalTypeBinding)aBinding);
			resolvePrimitiveAsExternalTypeBinding = false;
		}
	}

	private void handleDataPart(Record ir, FlexibleRecordBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Field field : ir.getFields()) {
			field.accept(this);
			binding.addField((FlexibleRecordFieldBinding)stack.pop());
		}
	}
	
	private void handleDataPart(StructuredRecord ir, FixedRecordBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Field field : ir.getStructuredFields()) {
			field.accept(this);
			binding.addStructureItem((StructureItemBinding)stack.pop());
		}
	}
	
	private void handleDataPart(Form ir, FormBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Field field : ir.getFields()) {
			field.accept(this);
			binding.addFormField((FormFieldBinding)stack.pop());
		}
	}
	
	private void handleDataPart(DataTable ir, DataTableBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Field field : ir.getStructuredFields()) {
			field.accept(this);
			binding.addStructureItem((StructureItemBinding)stack.pop());
		}
	}

	
	private void handleVisitLogicAndDataPart(Interface ir, InterfaceBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Function function : ir.getFunctions()) {
			function.accept(this);
			binding.addDeclaredFunction((NestedFunctionBinding)stack.pop());
		}
		
	}
	
	private void handleVisitLogicAndDataPart(ExternalType ir, ExternalTypeBinding binding) {
		handleVisitPart(ir, binding);
		
		for (Field field : ir.getFields()) {
			field.accept(this);
			binding.addClassField((ClassFieldBinding)stack.pop());
		}
		for (Function function : ir.getFunctions()) {
			function.accept(this);
			binding.addDeclaredFunction((NestedFunctionBinding)stack.pop());
		}
		
		for (Constructor constructor : ir.getConstructors()) {
			constructor.accept(this);
			binding.addConstructor((ConstructorBinding)stack.pop());
		}
		
	}

}

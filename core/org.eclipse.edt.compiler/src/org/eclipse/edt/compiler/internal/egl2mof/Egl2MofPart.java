/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.egl2mof;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EMemberContainer;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EMetadataType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.FormField;
import org.eclipse.edt.mof.egl.FormGroup;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredRecord;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ProxyEClass;
import org.eclipse.edt.mof.serialization.ProxyEObject;


abstract class Egl2MofPart extends Egl2MofBase {
	public MofSerializable currentPart;
	protected FunctionMember currentFunction;
	public List<Node> functionsToProcess = new ArrayList<Node>();
	 

 	Egl2MofPart(IEnvironment env) {
		super(env);
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Record node) {
		MofSerializable part = defaultHandleVisitPart(node);
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Enumeration node) {
		MofSerializable part = defaultHandleVisitPart(node);
		setElementInformation(node, part);
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExternalType node) {
		IPartBinding partBinding = (IPartBinding)node.getName().resolveBinding();
		inMofProxyContext = isMofProxy(partBinding);
		inEMetadataTypeContext = isEMetadataType(partBinding);
		inAnnotationTypeContext = isAnnotationType(partBinding) || isStereotypeType(partBinding);
		inMofContext = isMofReflectType(partBinding) || inEMetadataTypeContext;
		MofSerializable part = handleVisitPart(node);
		handleContents(node, part);
		if (part instanceof EClass) {
			// handle all super types together to allow
			// proper handling of multiple inheritance
			List<EClass> superTypes = new ArrayList<EClass>();
			for (Object name : node.getExtendedTypes()) {
				IPartBinding superType = (IPartBinding)((Name)name).resolveBinding();
				if (Binding.isValidBinding(superType)) {
					EClass superTypeClass = (EClass)mofTypeFor(superType);
					fixSuperTypes(superTypeClass);
					superTypes.add((EClass)mofTypeFor(superType));
				}
			}
			((EClass)part).addSuperTypes(superTypes);
		}
		else {
			for (Object name : node.getExtendedTypes()) {
				IPartBinding superType = (IPartBinding)((Name)name).resolveBinding();
				if (Binding.isValidBinding(superType) && superType.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
					((EGLClass)part).getSuperTypes().add((EGLClass)mofTypeFor(superType));
				}
			}
			
		}
		handleEndVisitPart(node, part);
		stack.push(part);
		return false;
	}
	
	// fix object identity problems that can occur when compiling the mof model
	private void fixSuperTypes(EClass eclass) {
		List<EClass> superTypes = eclass.getSuperTypes();
		for (int i = 0; i < superTypes.size(); i++) {
			EObject obj = getMofSerializable(superTypes.get(i).getMofSerializationKey());
			if (obj instanceof EClass) {
				if (obj == superTypes.get(i)) {
					fixSuperTypes((EClass) obj);
				}
				else {
					superTypes.set(i, ((EClass)obj));
				}
			}
		}
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Program node) {
		MofSerializable part = defaultHandleVisitPart(node);
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Interface interfaceNode) {
		
		MofSerializable part = handleVisitPart(interfaceNode);
		handleContents(interfaceNode, part);
		
		for (Node node : (List<Node>)interfaceNode.getExtendedTypes()) {
			
			ITypeBinding tb = (ITypeBinding)((org.eclipse.edt.compiler.core.ast.Name)node).resolveBinding();
			if (Binding.isValidBinding(tb) && tb.getKind() == ITypeBinding.INTERFACE_BINDING) {
				EObject obj = mofTypeFor(tb);
				if (obj instanceof StructPart) {
					((EGLClass)part).getSuperTypes().add((StructPart)obj);
				}
			}
		}

		handleEndVisitPart(interfaceNode, part);

		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Handler handler) {
		Handler part = (Handler)defaultHandleVisitPart(handler);
		for (Node node : (List<Node>)handler.getImplementedInterfaces()) {
			ITypeBinding tb = (ITypeBinding)((org.eclipse.edt.compiler.core.ast.Name)node).resolveBinding();
			if (Binding.isValidBinding(tb) && tb.getKind() == ITypeBinding.INTERFACE_BINDING) {
				EObject obj = mofTypeFor(tb);
				if (obj instanceof StructPart) {
					((EGLClass)part).getSuperTypes().add((StructPart)obj);
				}
			}
		}
		
		stack.push(part);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Library node) {
		MofSerializable part = defaultHandleVisitPart(node);
		stack.push(part);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DataItem dataItem) {
		DataItem part = factory.createDataItem();
		part.setName(dataItem.getName().getCaseSensitiveIdentifier());
		DataItemBinding type = (DataItemBinding)dataItem.getName().resolveBinding();
		part.setBaseType((Type)mofTypeFor(type.getPrimitiveTypeBinding()));
		part.setPackageName(concatWithSeparator(type.getPackageName(), ".").toUpperCase().toLowerCase());
		createAnnotations(type, part);
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DataTable node) {
		MofSerializable part = defaultHandleVisitPart(node);
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Delegate delegate) {
		Delegate part = factory.createDelegate();
		DelegateBinding binding = (DelegateBinding)delegate.getName().resolveBinding();
		part.setName(binding.getCaseSensitiveName());
		part.setPackageName(concatWithSeparator(binding.getPackageName(), ".").toUpperCase().toLowerCase());
		
		if (binding.getReturnType() != null) {
			part.setReturnType((Type)mofTypeFor(binding.getReturnType()));		
			part.setIsNullable(binding.getReturnType().isNullable());
		}
		else {
			part.setIsNullable(false);
		}

		partProcessingStack.push(part);
	
		for (org.eclipse.edt.compiler.core.ast.Parameter parm : (List<org.eclipse.edt.compiler.core.ast.Parameter>)delegate.getParameters()) {
			parm.accept(this);
			part.addMember((FunctionParameter)stack.pop());
		}
		setElementInformation(delegate, part);
		partProcessingStack.pop();
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FormGroup formGroup) {
		FormGroup group = factory.createFormGroup();
		group.setName(formGroup.getName().getCaseSensitiveIdentifier());
		FormGroupBinding groupBinding = (FormGroupBinding)formGroup.getName().resolveBinding();
		group.setPackageName(concatWithSeparator(groupBinding.getPackageName(), ".").toUpperCase().toLowerCase());
		stack.push(group);
		for (Node node : (List<Node>)formGroup.getContents()) {
			//TODO handle USE statment for top level forms
			if (node instanceof NestedForm) {
				NestedForm form = (NestedForm) node;
				form.accept(this);
				Form eForm = (Form)stack.pop();
				eForm.setContainer(group);
				group.getForms().add(eForm);
			}
		}
		createAnnotations(groupBinding, group);
		setElementInformation(formGroup, group);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.NestedForm nestedForm) {
		Form part = factory.createForm();
		part.setName(nestedForm.getName().getCaseSensitiveIdentifier());
		FormBinding binding = (FormBinding)nestedForm.getName().resolveBinding();
		stack.push(part);
		for (Node field : (List<Node>)nestedForm.getContents()) {
			field.accept(this);
			FormField f = (FormField)stack.pop();
			if (f != null)
				part.addMember(f);
		}
		createAnnotations(binding, part);
		setElementInformation(nestedForm, part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.TopLevelForm topLevelForm) {
		MofSerializable part = defaultHandleVisitPart(topLevelForm);
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Service service) {
		Service part = (Service)defaultHandleVisitPart(service);
		for (Node node : (List<Node>)service.getImplementedInterfaces()) {
			
			ITypeBinding tb = (ITypeBinding)((org.eclipse.edt.compiler.core.ast.Name)node).resolveBinding();
			if (Binding.isValidBinding(tb) && tb.getKind() == ITypeBinding.INTERFACE_BINDING) {
				EObject obj = mofTypeFor(tb);
				if (obj instanceof StructPart) {
					((EGLClass)part).getSuperTypes().add((StructPart)obj);
				}
			}
		}
		stack.push(part);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.TopLevelFunction node) {
		TopLevelFunctionBinding binding = (TopLevelFunctionBinding) node.getName().resolveBinding();
		FunctionPart part = (FunctionPart)handleVisitPart(node);
		Function function = factory.createFunction();
		setElementInformation(node, function);
		part.setFunction(function);
		function.setName(part.getName());
		for (Node parm : (List<Node>)node.getFunctionParameters()) {
			parm.accept(this);
			function.addMember((FunctionParameter)stack.pop());
		}
		if (node.getReturnDeclaration() != null) {
			function.setType((Type)mofTypeFor(node.getReturnType().resolveTypeBinding()));
		}
		StatementBlock block = factory.createStatementBlock();
		block.setContainer(function);
		function.setStatementBlock(block);
		setCurrentFunctionMember(function);
		for (Node stmt : (List<Node>)node.getStmts()) {
			stmt.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		setCurrentFunctionMember(null);
		setElementInformation(node, part);
		stack.push(part);
		return false;
	}
	
	public boolean visit(UseStatement stmt) {
		for ( org.eclipse.edt.compiler.core.ast.Name name : (List<org.eclipse.edt.compiler.core.ast.Name>)stmt.getNames()) {
			ITypeBinding partBinding = (ITypeBinding)name.resolveBinding();
			if (!IBinding.NOT_FOUND_BINDING.equals(partBinding)) {
				Part part = (Part)mofTypeFor(partBinding);
				// Assume current part is a LogicAndDataPart as only they have UseStatements
				((LogicAndDataPart)currentPart).getUsedParts().add(part);
				if (partBinding instanceof FormBinding) {
					Field formField = factory.createField();
					formField.setName(partBinding.getCaseSensitiveName());
					formField.setType(part);
					((LogicAndDataPart)currentPart).addMember(formField);
					eObjects.put(partBinding, formField);
				}
			}
		}
		stack.push(null);
		return false;
	}
	
	protected MofSerializable defaultHandleVisitPart(org.eclipse.edt.compiler.core.ast.Part node) {
		IBinding binding = node.getName().resolveBinding();
		MofSerializable part;
		part = handleVisitPart(node);
		handleContents(node, part);
		handleParms(node, part);
		handleEndVisitPart(node, part);
		
		return part;
	}
	
	private void handleParms(org.eclipse.edt.compiler.core.ast.Part part, EObject container) {
		if (part instanceof Program) {
			Program pgmAst = (Program) part;
			org.eclipse.edt.mof.egl.Program program = (org.eclipse.edt.mof.egl.Program) container;
			for (ProgramParameter parmAst : (List<ProgramParameter>)pgmAst.getParameters()) {
				parmAst.accept(this);
				org.eclipse.edt.mof.egl.ProgramParameter parm = (org.eclipse.edt.mof.egl.ProgramParameter) stack.pop();
				parm.setContainer(program);
				program.getParameters().add(parm);
			}
			program.setIsCallable(pgmAst.isCallable());
		}
	}

	protected MofSerializable handleVisitPart(org.eclipse.edt.compiler.core.ast.Part part) {
		IPartBinding partBinding = (IPartBinding)part.getName().resolveBinding();

		// EGL Enumerations are treated as straight Mof EEnum types
		inMofProxyContext = isMofProxy(partBinding);
		inEMetadataTypeContext = isEMetadataType(partBinding);
		inAnnotationTypeContext = isAnnotationType(partBinding) || isStereotypeType(partBinding);
		inMofContext = isMofReflectType(partBinding) || inEMetadataTypeContext;
		EClass typeClass = (EClass)getMofSerializable(mofPartTypeSignatureFor(part));
		MofSerializable eObj = (MofSerializable)typeClass.newInstance();
		partProcessingStack.push(eObj);
		// Use dynamic interface to handle both MOF or EGL types
		eObj.eSet("name", partBinding.getCaseSensitiveName());
		eObj.eSet("packageName", concatWithSeparator(partBinding.getPackageName(), ".").toUpperCase().toLowerCase());
		IAnnotationBinding subtype = partBinding.getSubTypeAnnotationBinding();
		if (!inMofProxyContext) setReflectTypeValues(eObj, subtype);
		
		env.save(mofSerializationKeyFor(partBinding), eObj, false);
		if (!mofSerializationKeyFor(partBinding).equals(eObj.getMofSerializationKey())) {
			env.save(((MofSerializable)eObj).getMofSerializationKey(), eObj, false);
		}
		if (!inMofContext) { 
			eObjects.put(partBinding, eObj);
			currentPart = eObj;
		}
		return eObj;
	}
	
	private void createInitializerStatements(org.eclipse.edt.compiler.core.ast.Part astPart, StructPart structPart) {
		
		final Egl2MofPart converter = this;
		final List<Statement> statements = new ArrayList<Statement>();
		
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.SettingsBlock settingsBlock) {
				for (Node node : (List<Node>)settingsBlock.getSettings()) {
					node.accept(this);
				}
				return false;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.Assignment assignment) {
				
				//ignore annotations
				if (assignment.resolveBinding() == null && Binding.isValidBinding(assignment.getLeftHandSide().resolveDataBinding())) {
					assignment.accept(converter);
					Assignment assign = (Assignment)stack.pop();
					AssignmentStatement stmt = factory.createAssignmentStatement();
					stmt.setExpr(assign);
					setElementInformation(assignment, stmt);
					statements.add(stmt);
				}
				
				return false;
			}
			
		};
		
		for (Node node : (List<Node>)astPart.getContents()) {
			node.accept(visitor);
		}
		
		if (!statements.isEmpty()) {
			StatementBlock block = factory.createStatementBlock();
			structPart.setInitializerStatements(block);
			
			for (Statement stmt : statements) {
				block.getStatements().add(stmt);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void handleEndVisitPart(org.eclipse.edt.compiler.core.ast.Part astPart, MofSerializable mofPart) {
		MofSerializable part = partProcessingStack.pop();
		
		// Set the stereotype value if necessary
		IPartBinding partBinding = (IPartBinding)astPart.getName().resolveBinding();
		IAnnotationBinding subtype = partBinding.getSubTypeAnnotationBinding();

		if (mofPart instanceof EClass) {
			createAnnotations(partBinding, (EClass)mofPart);
//			setPartInformation(astPart, (EClass)mofPart);
		}
		else if (mofPart instanceof Part) {
			createAnnotations(partBinding, (Part)mofPart);
			if (mofPart instanceof StructPart)
				setDefaultSuperType((StructPart)mofPart);
			setElementInformation(astPart,  (Part)mofPart);
			
			if (mofPart instanceof StructPart) {
				createInitializerStatements(astPart, (StructPart)mofPart);
			}
		}
		
		// Process statements of function members now since
		// All members that expressions may reference have been
		// created and resolved
		for (Node processNode : functionsToProcess) {
			
			IBinding binding;
			List<org.eclipse.edt.compiler.core.ast.Node> stmts;
			if (processNode instanceof NestedFunction) {
				binding = ((NestedFunction)processNode).getName().resolveDataBinding().getType();
				stmts = ((NestedFunction)processNode).getStmts();
			}
			else {
				//must be a constructor
				binding = ((org.eclipse.edt.compiler.core.ast.Constructor)processNode).getBinding().getType();
				stmts = ((org.eclipse.edt.compiler.core.ast.Constructor)processNode).getStmts();
			}
			
			FunctionMember irFunc = (FunctionMember)getEObjectFor(binding);
			setCurrentFunctionMember(irFunc);
			for (org.eclipse.edt.compiler.core.ast.Node node : stmts) {
				if (node instanceof org.eclipse.edt.compiler.core.ast.Statement) {
					node.accept(this);
					Statement irStmt = (Statement)stack.pop();
					irFunc.getStatements().add(irStmt);
				}
			}
			setCurrentFunctionMember(null);
		}
		if (part instanceof LogicAndDataPart)
			IRUtils.markOverloadedFunctions((LogicAndDataPart)part);
						
		if (inMofProxyContext) {
			EMetadataObject metadata = (EMetadataObject)mofValueFrom(subtype);
			((EClass)mofPart).getMetadataList().add(metadata);
		}
		
		
		for (Entry<IBinding, ProxyEObject> entry : proxies.entrySet()) {
			EObject real = eObjects.get(entry.getKey());
			updateProxyReferences(entry.getKey(), real);
		}

	}
	
	private void handleImplicits(org.eclipse.edt.compiler.core.ast.Part part, EObject container){
		IDataBinding[] impFields = getImplicitFields(part.getName().resolveBinding());
		if (impFields != null) {
			for (int i = 0; i < impFields.length; i++) {
				((Container)container).addMember(createImplicitField(impFields[i]));
			}
		}
		
		NestedFunctionBinding[] implFunctions = getImplicitFunctions(part.getName().resolveBinding());
		if (implFunctions != null) {
			for (int i = 0; i < implFunctions.length; i++) {
				((Container)container).addMember(createImplicitFunction(implFunctions[i]));
			}	
		}
	}
	
	private Member createImplicitField(IDataBinding data) {
		EClass fieldClass = mofMemberTypeFor(data);
		Field f = (Field)fieldClass.newInstance();
		setUpEglTypedElement(f, data);
		if (data instanceof ClassFieldBinding) {
			ClassFieldBinding cfb = (ClassFieldBinding)data;
			f.setIsStatic(cfb.isStatic());
			if (cfb.isPrivate()) {
				f.setAccessKind(AccessKind.ACC_PRIVATE);
			}			
		}

		for (IAnnotationBinding ann : (List<IAnnotationBinding>)data.getAnnotations()) {
			Annotation value = (Annotation)mofValueFrom(ann);
			f.getAnnotations().add(value);
		}
		
		eObjects.put(data, f);

		return f;
	}
	
	private Member createImplicitFunction(NestedFunctionBinding function) {

		FunctionBinding functionBinding = (FunctionBinding) function.getType();
		EClass funcClass = mofMemberTypeFor(function);
		Function func = (Function)funcClass.newInstance();
		setUpEglTypedElement(func, function);
				
		StatementBlock stmts = factory.createStatementBlock();
		stmts.setContainer(func);
		func.setStatementBlock(stmts);

		for (FunctionParameterBinding parmBinding : (List<FunctionParameterBinding>)functionBinding.getParameters()) {

			FunctionParameter parm = factory.createFunctionParameter();
			ParameterKind parmKind;
			if (parmBinding.isInput())
				parmKind=ParameterKind.PARM_IN;
			else if (parmBinding.isInputOutput())
				parmKind=ParameterKind.PARM_INOUT;
			else if (parmBinding.isOutput()) 
				parmKind=ParameterKind.PARM_OUT;
			else
				parmKind=ParameterKind.PARM_INOUT;
			parm.setParameterKind(parmKind);
			setUpEglTypedElement(parm, parmBinding);
			eObjects.put(parmBinding, parm);
			func.addMember(parm);
		}

		if (functionBinding.isPrivate()) {
			func.setAccessKind(AccessKind.ACC_PRIVATE);
		}
		
		if (functionBinding.isStatic()) {
			func.setIsStatic(true);
		}
		eObjects.put(function.getType(), func);	
		return func;
	}
	
	private IDataBinding[] getImplicitFields(IBinding binding) {
		if (binding instanceof FunctionContainerBinding) {
			List<ClassFieldBinding> list =  new ArrayList<ClassFieldBinding>();
			FunctionContainerBinding fc = (FunctionContainerBinding) binding;			
			for (ClassFieldBinding field : (List<ClassFieldBinding>)fc.getClassFields()) {
				if (field.isImplicit()) {
					list.add(field);
				}
			}
			if (list.size() > 0) {
				return (IDataBinding[])list.toArray(new IDataBinding[list.size()]);
			}
		}
		
		if (binding instanceof FlexibleRecordBinding) {
			List<FlexibleRecordFieldBinding> list =  new ArrayList<FlexibleRecordFieldBinding>();
			FlexibleRecordBinding rec = (FlexibleRecordBinding) binding;			
			for (FlexibleRecordFieldBinding field : (List<FlexibleRecordFieldBinding>)rec.getDeclaredFields()) {
				if (field.isImplicit()) {
					list.add(field);
				}
			}
			if (list.size() > 0) {
				return (IDataBinding[])list.toArray(new IDataBinding[list.size()]);
			}
		}
		
		return null;
	}
	
	private NestedFunctionBinding[] getImplicitFunctions(IBinding binding) {
		if (binding instanceof FunctionContainerBinding) {
			List<NestedFunctionBinding> list =  new ArrayList<NestedFunctionBinding>();
			FunctionContainerBinding fc = (FunctionContainerBinding) binding;			
			for (NestedFunctionBinding function : (List<NestedFunctionBinding>)fc.getDeclaredFunctions()) {
				if (((FunctionBinding)function.getType()).isImplicit()) {
					list.add(function);
				}
			}
			if (list.size() > 0) {
				return (NestedFunctionBinding[])list.toArray(new NestedFunctionBinding[list.size()]);
			}
		}
		
		return null;
	}

	

	@SuppressWarnings("unchecked")
	private void handleContents(org.eclipse.edt.compiler.core.ast.Part part, EObject container) {
		
		if (!inMofContext) {
			handleImplicits(part, container);
		}
		
		for (Node n : (List<Node>)part.getContents()) {
			n.accept(this);
			
			Object obj = stack.pop();
			if (obj instanceof List) {
				for (EObject eobj : (List<EObject>)obj) {
					handleMember(eobj, container);
				}
			}
			else {
				handleMember((EObject)obj, container);
			}
			
		}
	}
	
	private void handleMember(EObject mofObj, EObject container) {
		if (mofObj != null) {
			if (inMofContext) {
				((EMemberContainer)container).addMember((EMember)mofObj);
				if (mofObj instanceof EField && (container instanceof AnnotationType || container instanceof EMetadataType)) {
					((EField)mofObj).setContainment(true);
				}
			}
			else {
				((Container)container).addMember((Member)mofObj);
			}
		}
	}
	
	private MofSerializable resolveProxy(ProxyEClass proxy) {
		String key = proxy.getMofSerializationKey();
		EObject result = getMofSerializable(key);
		
		if (result instanceof MofSerializable) {
			return (MofSerializable)result;
		}
		
		//look for it with an EGL scheme if we are not in Mof context
		if (!inMofContext) {
			if (key.indexOf(Type.KeySchemeDelimiter) < 0) {
				key = Type.EGL_KeyScheme + Type.KeySchemeDelimiter + key;
				result = getMofSerializable(key);
				
				if (result instanceof MofSerializable) {
					return (MofSerializable)result;
				}
			}
		}
		
		return null;
	}
	
	private void setDefaultSuperType(StructPart part) {
		if (part.getSuperTypes().isEmpty()) {
			Stereotype stereotype = part.getStereotype();
			StructPart superType = null;
			if (stereotype != null) {
				
				MofSerializable mofST = ((StereotypeType)stereotype.getEClass()).getDefaultSuperType();
				
				if (mofST instanceof ProxyEClass) {
					mofST = resolveProxy((ProxyEClass) mofST);
				}
				
				//make sure that it is a StructPart and not a proxy. Also make sure that the default supertype is not the same as the object being compiled
				if (mofST instanceof StructPart && !mofST.getMofSerializationKey().equalsIgnoreCase(part.getMofSerializationKey())) {
					superType = (StructPart)mofST;
				}
			}
			if (superType == null) {
				String typeSignature = Type_EGLAny;
				if (part instanceof Record)
					typeSignature = Type_AnyRecord;
				else if (part instanceof StructuredRecord) {
					typeSignature = Type_AnyStruct;
				}
				else if (part instanceof AnnotationType) {
					typeSignature = Type_Annotation;
				}
				else if (part instanceof StereotypeType) {
					typeSignature = Type_Stereotype;
				}
				superType = (StructPart)getMofSerializable(typeSignature);
			}
			if (superType != null && !superType.getMofSerializationKey().equalsIgnoreCase(part.getMofSerializationKey()))
				part.getSuperTypes().add(superType);
				
		}
	}
		
	protected void setCurrentFunctionMember(FunctionMember function) {
		currentFunction = function;
	}
	
	protected FunctionMember getCurrentFunctionMember() {
		return currentFunction;
	}
	
	protected void setElementInformation(org.eclipse.edt.compiler.core.ast.Node node, EObject obj) {
		
		if (obj instanceof Statement) {
			Statement stmt = (Statement) obj;
			if (stmt.getContainer() == null && (currentPart instanceof Container)) {
				stmt.setContainer((Container)currentPart);
			}
		}
		
		super.setElementInformation(node, obj);
	}

}

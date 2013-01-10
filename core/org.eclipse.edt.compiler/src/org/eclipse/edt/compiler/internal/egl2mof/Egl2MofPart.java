/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EMemberContainer;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EMetadataType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ProxyEClass;
import org.eclipse.edt.mof.serialization.ProxyEObject;



abstract class Egl2MofPart extends Egl2MofBase {
	public MofSerializable currentPart;
	public Part currentBindingLevelPart;
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
		
		// Parts already have element information.
		if (!(part instanceof Part)) {
			setElementInformation(node, part);
		}
		
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExternalType node) {
		Part partBinding = (Part)node.getName().resolveType();
		setFlags(partBinding);
		
		MofSerializable part = handleVisitPart(node);
		handleContents(node, part);
		if (part instanceof EClass) {
			// handle all super types together to allow
			// proper handling of multiple inheritance
			List<EClass> superTypes = new ArrayList<EClass>();
			for (Object name : node.getExtendedTypes()) {
				Part superType = (Part)((Name)name).resolveType();
				if (superType != null) {
					
					EType proxiedType = BindingUtil.getETypeFromProxy(superType);
					EClass superTypeClass;
					if (proxiedType instanceof EClass) {
						superTypeClass = (EClass) proxiedType;
					}
					else {
						superTypeClass = (EClass)mofTypeFor(superType);
					}
					
					fixSuperTypes(superTypeClass);
					superTypes.add(superTypeClass);
				}
			}
			((EClass)part).addSuperTypes(superTypes);
		}
		else {
			for (Name name : node.getExtendedTypes()) {
				Part superType = (Part)name.resolveType();
				if (superType instanceof ExternalType) {
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
		
		setFlags((Part)interfaceNode.getName().resolveType());
		org.eclipse.edt.mof.egl.Interface part = (org.eclipse.edt.mof.egl.Interface)handleVisitPart(interfaceNode);
		handleContents(interfaceNode, part);
		
		addInterfaces(part, interfaceNode.getExtendedTypes());
		
		handleEndVisitPart(interfaceNode, part);

		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Handler handler) {
		Handler part = (Handler)defaultHandleVisitPart(handler);
		addInterfaces(part, handler.getImplementedInterfaces());
		
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Class eglClass) {
		EGLClass part = (EGLClass)defaultHandleVisitPart(eglClass);
		
		if (eglClass.getExtends() != null) {
			Type tb = eglClass.getExtends().resolveType();
			if (tb instanceof EGLClass) {
				EObject obj = mofTypeFor(tb);
				if (obj instanceof StructPart) {
					//remove the default supertype
					part.getSuperTypes().clear();
					part.getSuperTypes().add((StructPart)obj);
				}
			}
		}
		
		addInterfaces(part, eglClass.getImplementedInterfaces());
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
	public boolean visit(org.eclipse.edt.compiler.core.ast.Delegate delegate) {
		Delegate part = factory.createDelegate();
		Delegate binding = (Delegate)delegate.getName().resolveType();
		part.setName(binding.getCaseSensitiveName());
		part.setPackageName(binding.getCaseSensitivePackageName());
		
		if (binding.getReturnType() != null) {
			part.setReturnType((Type)mofTypeFor(binding.getReturnType()));		
			part.setIsNullable(binding.isNullable());
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
		
		if (delegate.isPrivate()) {
			part.setAccessKind(AccessKind.ACC_PRIVATE);
		}
		stack.push(part);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Service service) {
		Service part = (Service)defaultHandleVisitPart(service);
		addInterfaces(part, service.getImplementedInterfaces());
		stack.push(part);
		return false;
	}

	private void addInterfaces(StructPart part, List<Name> implemented) {
		for (Name name : implemented) {
			Type intfce = name.resolveType();
			if(intfce instanceof org.eclipse.edt.mof.egl.Interface){
				EObject obj = mofTypeFor(intfce);
				if (obj instanceof StructPart) {
					part.getSuperTypes().add((StructPart)intfce);
				}
			}
		}
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.UseStatement stmt) {
		for ( Name name : (List<Name>)stmt.getNames()) {
			Type partBinding = name.resolveType();
			if (partBinding != null) {
				Part part = (Part)mofTypeFor(partBinding);
				// Assume current part is a LogicAndDataPart as only they have UseStatements
				((LogicAndDataPart)currentPart).getUsedParts().add(part);
			}
		}
		stack.push(null);
		return false;
	}
	
	private MofSerializable defaultHandleVisitPart(org.eclipse.edt.compiler.core.ast.Part node) {
		setFlags((Part)node.getName().resolveType());
		
		MofSerializable part = handleVisitPart(node);
		handleContents(node, part);
		handleEndVisitPart(node, part);
		
		return part;
	}
	
	private void setFlags(Part partBinding) {
		// EGL Enumerations are treated as straight Mof EEnum types
		inMofProxyContext = isMofProxy(partBinding);
		inEMetadataTypeContext = isEMetadataType(partBinding);
		inAnnotationTypeContext = (partBinding instanceof AnnotationType) && !inEMetadataTypeContext;
		inMofContext = inAnnotationTypeContext || isMofReflectType(partBinding) || inEMetadataTypeContext;
	}
	private MofSerializable handleVisitPart(org.eclipse.edt.compiler.core.ast.Part node) {
		Part partBinding = (Part)node.getName().resolveType();
		

		EClass typeClass = (EClass)getMofSerializable(mofPartTypeSignatureFor(node));
		MofSerializable eObj = (MofSerializable)typeClass.newInstance();
		partProcessingStack.push(eObj);
		// Use dynamic interface to handle both MOF or EGL types
		eObj.eSet("name", partBinding.getCaseSensitiveName());
		eObj.eSet("packageName", partBinding.getCaseSensitivePackageName());
		if (!inMofProxyContext) {
			if (partBinding instanceof AnnotationType) {
				setReflectTypeValues(eObj, (AnnotationType)partBinding);
			}
			else {
				setReflectTypeValues(eObj, partBinding.getStereotype());
			}
		}
		
		env.save(partBinding.getMofSerializationKey(), eObj, false);
		if (!partBinding.getMofSerializationKey().equals(eObj.getMofSerializationKey())) {
			env.save(eObj.getMofSerializationKey(), eObj, false);
		}
		if (!inMofContext) { 
			eObjects.put(partBinding, eObj);
			currentPart = eObj;
			currentBindingLevelPart = (Part)node.getName().resolveType();
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
				if (assignment.resolveBinding() == null && assignment.getLeftHandSide().resolveType() != null) {
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
	private void handleEndVisitPart(org.eclipse.edt.compiler.core.ast.Part astPart, MofSerializable mofPart) {
		MofSerializable part = partProcessingStack.peek();
		
		// Set the stereotype value if necessary
		Part partBinding = (Part)astPart.getName().resolveType();

		if (mofPart instanceof EClass) {
			createAnnotations(partBinding, (EClass)mofPart);
			setElementInformation(astPart,  (EClass)mofPart);
			if (mofPart instanceof AnnotationType) {
				if (astPart.isPrivate()) {
					((AnnotationType)mofPart).setAccessKind(AccessKind.ACC_PRIVATE);
				}
			}		
		}
		else if (mofPart instanceof Part) {
			if (astPart.isPrivate()) {
				((Part)mofPart).setAccessKind(AccessKind.ACC_PRIVATE);
			}
			
			if (astPart.isAbstract() && mofPart instanceof EGLClass) {
				((EGLClass)mofPart).setIsAbstract(true);
			}
			
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
			
			Object binding;
			List<org.eclipse.edt.compiler.core.ast.Statement> stmts;
			if (processNode instanceof NestedFunction) {
				binding = ((NestedFunction)processNode).getName().resolveElement();
				stmts = ((NestedFunction)processNode).getStmts();
			}
			else {
				//must be a constructor
				binding = ((Constructor)processNode).getBinding();
				stmts = ((org.eclipse.edt.compiler.core.ast.Constructor)processNode).getStmts();
			}
			
			FunctionMember irFunc = (FunctionMember)getEObjectFor(binding);
			setCurrentFunctionMember(irFunc);
			for (org.eclipse.edt.compiler.core.ast.Statement stmt : stmts) {
				stmt.accept(this);
				Statement irStmt = (Statement)stack.pop();
				irFunc.getStatements().add(irStmt);
			}
			setCurrentFunctionMember(null);
		}
		if (part instanceof LogicAndDataPart)
			IRUtils.markOverloadedFunctions((LogicAndDataPart)part);
						
		if (inMofProxyContext) {
			Annotation subtype = partBinding.getStereotype();
			EMetadataObject metadata = (EMetadataObject)mofValueFrom(subtype);
			((EClass)mofPart).getMetadataList().add(metadata);
		}
		
		
		for (Entry<Object, ProxyEObject> entry : proxies.entrySet()) {
			EObject real = eObjects.get(entry.getKey());
			updateProxyReferences(entry.getKey(), real);
		}

		partProcessingStack.pop();
	}
		
	@SuppressWarnings("unchecked")
	private void handleContents(org.eclipse.edt.compiler.core.ast.Part part, EObject container) {
				
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
				else if (part instanceof StructPart && !part.equals(TypeUtils.Type_ANY)) {
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
		
	private void setCurrentFunctionMember(FunctionMember function) {
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

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
package org.eclipse.edt.compiler.internal.egl2mof;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AnnotationBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.ConstantFormFieldBinding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.ProgramParameterBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.VariableFormFieldBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeManager;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.EnumerationField;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BytesLiteral;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.ConstantFormField;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.DynamicAccess;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.InvalidName;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Parameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.PrimitiveTypeLiteral;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.VariableFormField;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.IBMiFactory;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.utils.EList;


class Egl2MofMember extends Egl2MofPart {

	Egl2MofMember(IEnvironment env) {
		super(env);
	}

 	
	@Override
	public boolean visit(ClassDataDeclaration node) {
		
		List<EObject> list = new ArrayList<EObject>();

		for (org.eclipse.edt.compiler.core.ast.Name name : (List<org.eclipse.edt.compiler.core.ast.Name>)node.getNames()) {
			IDataBinding field = name.resolveDataBinding();
			
			// Do not create members that have invalid types!
			if (!Binding.isValidBinding(field)) {
				stack.push(null);
				return false;
			}
			
			EObject obj;
					
			if (inMofContext) {
				EField f = mof.createEField(true);
				setUpMofTypedElement(f, field);
				setInitialValue(node, f);
				obj = f;
			}
			else {
				EClass fieldClass = mofMemberTypeFor(field);
				Field f = (Field)fieldClass.newInstance();
				setUpEglTypedElement(f, field);
				if (field instanceof ClassFieldBinding) {
					f.setIsStatic(((ClassFieldBinding)field).isStatic());
				}
				if (node.isPrivate()) {
					f.setAccessKind(AccessKind.ACC_PRIVATE);
				}
				addInitializers(node, f, node.getType());
				obj = f;
			}
			eObjects.put(field, obj);
			setElementInformation(node, obj);
			list.add(obj);
		}
		
		if (list.isEmpty()) {
			stack.push(null);
		}
		else {
			if (list.size() == 1) {
				stack.push(list.get(0));
			}
			else {
				stack.push(list);
			}
		}
		return false;
	}

	private void setMetadata(IDataBinding binding, EField field) {
		for (AnnotationBinding annotation : (List<AnnotationBinding>)binding.getAnnotations()) {
			field.getMetadataList().add((EMetadataObject)mofValueFrom(annotation));
		}
	}	

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.StructureItem node) {
		IDataBinding field = node.getName() == null ? (IDataBinding)node.resolveBinding() : node.getName().resolveDataBinding();
		// Do not create members that have invalid types!
		if (!Binding.isValidBinding(field)) {
			stack.push(null);
			return false;
		}
		EObject obj;
		if (inMofContext) {
			EField f = mof.createEField(true);
			setUpMofTypedElement(f, field);
			setInitialValue(node, f);
			setMetadata(field, f);
			obj = f;
		}
		else {
			//TODO must handle embedded and structure items that type to a struct record
			if (node.isEmbedded()) {
				stack.push(null);
				return false;
			}
			EClass fieldClass = mofMemberTypeFor(field);
			Field f = (Field)fieldClass.newInstance();
			setUpEglTypedElement(f, field);
			addInitializers(node, f, node.getType());
			if (f instanceof StructuredField) {
				StructuredField s = (StructuredField)f;
				
				if (node.getOccurs() == null) {
					if (field instanceof StructureItemBinding) {
						s.setOccurs(((StructureItemBinding)field).getOccurs());
					}
					else {
						s.setOccurs(1);
					}
				}
				else {
					s.setOccurs(Integer.decode(node.getOccurs()));
				}
				StructureItemBinding parentBinding = ((StructureItemBinding)field).getParentItem();
				if (parentBinding != null) {
					StructuredField parent = (StructuredField)getEObjectFor(parentBinding);
					parent.addChild(s);
				}
				createElementAnnotations((StructureItemBinding)field, (StructuredField)f);
			}
			obj = f;
		}
		eObjects.put(field, obj);
		setElementInformation(node, obj);
		stack.push(obj);
		return false;
	}
	
	

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ConstantFormField node) {
		ConstantFormFieldBinding binding = (ConstantFormFieldBinding)node.resolveBinding();
		// Do not create members that have invalid types!
		if (!Binding.isValidBinding(binding)) {
			stack.push(null);
			return false;
		}
		ConstantFormField field = factory.createConstantFormField();		
		field.setOccurs(binding.getOccurs() == 0 ? 1 : binding.getOccurs());
		createAnnotations(binding, field);
		createElementAnnotations(binding, field);
		setElementInformation(node, field);
		stack.push(field);
		return false;
	}


	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.VariableFormField node) {
		VariableFormFieldBinding binding = (VariableFormFieldBinding)node.getName().resolveDataBinding();
		// Do not create members that have invalid types!
		if (!Binding.isValidBinding(binding)) {
			stack.push(null);
			return false;
		}
		VariableFormField field = factory.createVariableFormField();
		field.setOccurs(binding.getOccurs() == 0 ? 1 : binding.getOccurs());
		setUpEglTypedElement(field, binding);
		createAnnotations(binding, field);
		createElementAnnotations(binding, field);
		eObjects.put(binding, field);
		stack.push(field);
		setElementInformation(node, field);
		return false;
	}


	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.NestedFunction node) {
		NestedFunctionBinding function = (NestedFunctionBinding)node.getName().resolveDataBinding();
		// Do not create members that have invalid types!
		if (!Binding.isValidBinding(function)) {
			stack.push(null);
			return false;
		}
		EObject obj = null;
		if (inMofContext) {
			EFunction func = mof.createEFunction(true);
			setUpMofTypedElement(func, function);
			obj = func;
		}
		else {
			Function func; 
//FIXME JV this need to be extensible 		
			IAnnotationBinding ibmiProgram = function.getAnnotation(new String[]{"eglx", "jtopen","annotations"}, "IBMiProgram");
			if(ibmiProgram == null){
				EClass funcClass = mofMemberTypeFor(function);
				func = (Function)funcClass.newInstance();
			}
			else{
				func = IBMiFactory.INSTANCE.createIBMiFunction();
			}
			if (func instanceof Operation) {
				IAnnotationBinding ann = this.getAnnotation(function.getType(), "Operation");
				((Operation)func).setOpSymbol((String)getFieldValue(ann, "opSymbol"));
			}
			setUpEglTypedElement(func, function);
			func.setIsStatic(node.isStatic());
					
			if (!node.isAbstract()) {
				StatementBlock stmts = factory.createStatementBlock();
				stmts.setContainer(func);
				func.setStatementBlock(stmts);
				functionsToProcess.add(node);
			}
			if (!node.getStmts().isEmpty() && node.getStmts().get(0) instanceof SettingsBlock) {
				processSettings(func, (SettingsBlock)node.getStmts().get(0));
			}
			if (node.isPrivate()) {
				func.setAccessKind(AccessKind.ACC_PRIVATE);
			}
			obj = func;
		}
		
		for (org.eclipse.edt.compiler.core.ast.FunctionParameter parm : (List<org.eclipse.edt.compiler.core.ast.FunctionParameter>)node.getFunctionParameters()) {
			parm.accept(this);
			if (inMofContext) {
				((EFunction)obj).addMember((EParameter)stack.pop());
			}
			else {
				((FunctionMember)obj).addMember((Parameter)stack.pop());
			}
		}
		eObjects.put(function.getType(), obj);
		setElementInformation(node, obj);
		stack.push(obj);
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.Constructor node) {
		org.eclipse.edt.compiler.core.ast.Part etAST = (org.eclipse.edt.compiler.core.ast.Part)node.getParent();
		IPartBinding constBinding = (IPartBinding)etAST.getName().resolveBinding();
		if (!Binding.isValidBinding(constBinding)) {
			stack.push(null);
			return false;
		}

		ConstructorBinding constructor = node.getBinding();
				
		EObject obj = null;
		if (inMofContext) {  //not possible
			obj = null;
		}
		else {
			
			EClass constClass = mofMemberTypeFor(constructor);
			Constructor cons = (Constructor)constClass.newInstance();
			
			if (node.getStmts() != null) {
				StatementBlock stmts = factory.createStatementBlock();
				stmts.setContainer(cons);
				cons.setStatementBlock(stmts);
				functionsToProcess.add(node);
			}

			if (node.isPrivate()) {
				cons.setAccessKind(AccessKind.ACC_PRIVATE);
			}

			setUpEglTypedElement(cons, constructor);
			
			obj = cons;
					
		}
		
		for (org.eclipse.edt.compiler.core.ast.FunctionParameter parm : (List<org.eclipse.edt.compiler.core.ast.FunctionParameter>)node.getParameters()) {
			parm.accept(this);
			if (inMofContext) {
				((EFunction)obj).addMember((EParameter)stack.pop());
			}
			else {
				((FunctionMember)obj).addMember((Parameter)stack.pop());
			}
		}
		eObjects.put(constructor.getType(), obj);
		setElementInformation(node, obj);
		stack.push(obj);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionParameter node) {
		FunctionParameterBinding parameter = (FunctionParameterBinding)node.getName().resolveDataBinding();
		// Do not create members that have invalid types!
		if (!Binding.isValidBinding(parameter)) {
			stack.push(null);
			return false;
		}
		EObject obj;
		if (inMofContext) {
			EParameter parm = mof.createEParameter(true);
			parm.setName(parameter.getCaseSensitiveName());
			setUpMofTypedElement(parm, parameter);
			obj = parm;
		}
		else {
			FunctionParameter parm = factory.createFunctionParameter();
			ParameterKind parmKind;
			if (parameter.isInput())
				parmKind=ParameterKind.PARM_IN;
			else if (parameter.isInputOutput())
				parmKind=ParameterKind.PARM_INOUT;
			else if (parameter.isOutput()) 
				parmKind=ParameterKind.PARM_OUT;
			else
				parmKind=ParameterKind.PARM_INOUT;
			parm.setParameterKind(parmKind);
			
			parm.setIsConst(parameter.isConst());
			parm.setIsField(parameter.isField());
			parm.setIsDefinedSqlNullable(parameter.isSQLNullable());
			
			setUpEglTypedElement(parm, parameter);
			obj = parm;
		}
		eObjects.put(parameter, obj);
		setElementInformation(node, obj);
		stack.push(obj);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ProgramParameter node) {
		ProgramParameterBinding parameter = (ProgramParameterBinding)node.getName().resolveDataBinding();
		// Do not create members that have invalid types!
		if (!Binding.isValidBinding(parameter)) {
			stack.push(null);
			return false;
		}
		EObject obj;
		ProgramParameter parm = factory.createProgramParameter();
		setUpEglTypedElement(parm, parameter);
		eObjects.put(parameter, parm);
		setElementInformation(node, parm);
		stack.push(parm);
		return false;
	}

	public boolean visit(SettingsBlock node) {
		stack.push(null);
		return false;
	}
	
	@Override
	public boolean visit(EnumerationField enumField) {
		if (Binding.isValidBinding(enumField.getName().resolveDataBinding())) {
			EnumerationDataBinding binding = (EnumerationDataBinding)enumField.getName().resolveDataBinding();
			Integer value = binding.geConstantValue();
			if (inMofContext) {
				EEnumLiteral literal = (EEnumLiteral)mof.getEEnumLiteralClass().newInstance();
				literal.setName(binding.getCaseSensitiveName());
				literal.setValue(value);
				createAnnotations(binding, literal);
				stack.push(literal);
			}
			else {
				EnumerationEntry entry = factory.createEnumerationEntry();
				entry.setName(binding.getCaseSensitiveName());
				entry.setValue(value);
				eObjects.put(binding, entry);
				createAnnotations(binding, (Element)entry);
				setElementInformation(enumField, entry);
				stack.push(entry);
			}
		}
		return false;
	}

	public void setUpMofTypedElement(EMember obj, IDataBinding edtObj) {
		EObject eObj = mofTypeFromTypedElement(edtObj);
		
		if (eObj instanceof EType) {
			EType type = (EType)eObj;
			obj.setEType(type);
			if (edtObj.getType().isNullable()) { 
				obj.setIsNullable(true);
			}
			if (obj instanceof EField) {
				IAnnotationBinding ann = this.getAnnotation(edtObj, "transient");
				if (ann != null) ((EField)obj).setIsTransient((Boolean)getValue(edtObj, ann.getValue(), false));
				
				ann = this.getAnnotation(edtObj, "containment");
				if (ann != null) ((EField)obj).setContainment((Boolean)getValue(edtObj, ann.getValue(), false));
			}
			IAnnotationBinding mofName = this.getAnnotation(edtObj, "mofName");
			if (mofName != null) 
				obj.setName((String)getValue(null, mofName.getValue(), false));
			else 
				obj.setName(edtObj.getCaseSensitiveName());
		}
	}
	
	protected LHSExpr setAccessForDynamicAccess(DynamicAccess da, Expression expr) {
		if (expr == null) {
			return da;
		}
		
		if (expr instanceof MemberName) {
			org.eclipse.edt.mof.egl.StringLiteral index = factory.createStringLiteral();
			index.setValue(((MemberName)expr).getId());
			da.setAccess(index);
			return da;
		}

		if (expr instanceof DynamicAccess) {
			DynamicAccess oldDA = (DynamicAccess)expr;
			setAccessForDynamicAccess(da, (oldDA.getExpression()));
			if (oldDA.getExpression() instanceof MemberName) {
				oldDA.setExpression(da);
			}
			return oldDA;
		}
		
		if (expr instanceof ArrayAccess) {
			ArrayAccess oldAA = (ArrayAccess)expr;
			setAccessForDynamicAccess(da, (oldAA.getArray()));
			if (oldAA.getArray() instanceof MemberName) {
				oldAA.setArray(da);
			}
			return oldAA;
		}
		
		return null;

	}
	
	public void processSettings(Element context, SettingsBlock settings) {
		StatementBlock block = null;
		int arrayIndex = 0;
		for (Node expr : (List<Node>)settings.getSettings()) {
			if (expr instanceof AnnotationExpression && ((AnnotationExpression)expr).getName().resolveDataBinding() instanceof AnnotationBinding) {
				Annotation value = (Annotation)mofValueFrom(((AnnotationExpression)expr).getName().resolveDataBinding());
				context.getAnnotations().add(value);
			}
			else if (expr instanceof org.eclipse.edt.compiler.core.ast.Assignment) {
				// Check if this is an Annotation value
				org.eclipse.edt.compiler.core.ast.Assignment assignment = (org.eclipse.edt.compiler.core.ast.Assignment)expr;
				org.eclipse.edt.compiler.core.ast.Expression lhsExpr = assignment.getLeftHandSide();
				if (Binding.isValidBinding(lhsExpr.resolveDataBinding())) {
					if (lhsExpr.resolveDataBinding() instanceof IAnnotationBinding) {
						Annotation value = (Annotation)mofValueFrom(lhsExpr.resolveDataBinding());
						if (assignment.getRightHandSide() instanceof NullLiteral && value != null) {
							value.setValue(factory.createNullLiteral());
						}
						context.getAnnotations().add(value);
					}
					// Check if this is setting a value into the context element, i.e. setting contents into a DataTable
					else if (context instanceof Part) {
						if (context.getEClass().getEField(((org.eclipse.edt.compiler.core.ast.Name)lhsExpr).getNameComponents()[0]) != null) {
							EField field = context.getEClass().getEField(((org.eclipse.edt.compiler.core.ast.Name)lhsExpr).getNameComponents()[0]);
							Object value = evaluateExpression(assignment.getRightHandSide());
							context.eSet(field, value);
						}
					}
					else {
						expr.accept(this);
						Assignment assign = (Assignment)stack.pop();
						org.eclipse.edt.mof.egl.Type type = null;
						if (context instanceof TypedElement) {
							type = ((TypedElement)context).getType();
						}
						if (TypeUtils.isDynamicType(type)) {
							DynamicAccess da = factory.createDynamicAccess();
							setElementInformation(expr, da);
							LHSExpr newLHS = setAccessForDynamicAccess(da, assign.getLHS());
							
							if (context instanceof Expression) {
								da.setExpression((Expression)context);
							}
							else {
								if (context instanceof Member) {
									MemberName exp = factory.createMemberName();
									exp.setId(((Member)context).getName());
									exp.setMember((Member)context);	
									da.setExpression(exp);
								}
							}
							assign.setLHS(newLHS);
						}
						else {
							assign.setLHS(addQualifier(context, assign.getLHS()));
						}
						if (block == null)
							block = factory.createStatementBlock();
						AssignmentStatement stmt = createAssignmentStatement(assign);
						setElementInformation(expr, stmt);
						block.getStatements().add(stmt);
					}
				}
			}
			else if (expr instanceof SetValuesExpression && ((SetValuesExpression)expr).getExpression() instanceof AnnotationExpression) {
				if (((AnnotationExpression)((SetValuesExpression)expr).getExpression()).resolveDataBinding() instanceof IAnnotationBinding) { 
					IAnnotationBinding annBinding = (IAnnotationBinding)((AnnotationExpression)((SetValuesExpression)expr).getExpression()).resolveDataBinding();
					if (Binding.isValidBinding(annBinding) && !isEMetadataObject(annBinding)) {
						Annotation value = (Annotation)evaluateExpression(expr);
						context.getAnnotations().add(value);
					}
				}
			}
			else {
				expr.accept(this);
				Element result = (Element)stack.pop();
				if (block == null)
					block = factory.createStatementBlock();
				if (result instanceof StatementBlock) {
					// Need to now add the qualifier to each LHS of each assignment
					// which is the element associated with the block - context
					for (Statement stmt : ((StatementBlock)result).getStatements()) {
						if (stmt instanceof AssignmentStatement) {
							Assignment assignExpr = ((AssignmentStatement)stmt).getAssignment();
							LHSExpr lhs = addQualifier(context, assignExpr.getLHS());
							assignExpr.setLHS(lhs);
						}
						block.getStatements().add(stmt);
					}
				}
				else {
					if (result instanceof org.eclipse.edt.mof.egl.SetValuesExpression) {
						org.eclipse.edt.mof.egl.SetValuesExpression sve = (org.eclipse.edt.mof.egl.SetValuesExpression) result;
						//add the qualifier to any statements in the settings block
						for (Statement stmt : sve.getSettings().getStatements()) {
							if (stmt instanceof AssignmentStatement) {
								Assignment assignExpr = ((AssignmentStatement)stmt).getAssignment();
								
								org.eclipse.edt.mof.egl.Type type = null;
								if (context instanceof TypedElement) {
									type = ((TypedElement)context).getType();
								}

								if (TypeUtils.isDynamicType(type)) {
									DynamicAccess da = factory.createDynamicAccess();
									setElementInformation(expr, da);
									LHSExpr newLHS = setAccessForDynamicAccess(da, assignExpr.getLHS());
									
									if (context instanceof Expression) {
										da.setExpression((Expression)context);
									}
									else {
										if (context instanceof Member) {
											MemberName exp = factory.createMemberName();
											exp.setId(((Member)context).getName());
											exp.setMember((Member)context);	
											da.setExpression(exp);
										}
									}
									assignExpr.setLHS(newLHS);
								}
								else {
									LHSExpr lhs = addQualifier(context, assignExpr.getLHS());
									assignExpr.setLHS(lhs);
								}

								
								
							}
							block.getStatements().add(stmt);
						}

					}
				
					else {
						// result must be an Expression
						arrayIndex++;
						Expression qualifier;
						if (context instanceof Member) {
							qualifier = factory.createMemberName();
							((MemberName)qualifier).setId(((Member)context).getId());
							((MemberName)qualifier).setMember((Member)context);
						}
						else {
							qualifier = (Expression)context;
						}
						org.eclipse.edt.mof.egl.IntegerLiteral indexExpr = factory.createIntegerLiteral();
						indexExpr.setType(IRUtils.getEGLPrimitiveType(Type_Int));
						indexExpr.setValue(String.valueOf(arrayIndex));
						QualifiedFunctionInvocation func = factory.createQualifiedFunctionInvocation();
						setElementInformation(expr, func);
						func.setQualifier(qualifier);
						func.setId("appendElement");
						func.getArguments().add((Expression)result);
						FunctionStatement stmt = factory.createFunctionStatement();
						setElementInformation(expr, stmt);
						stmt.setExpr(func);
						block.getStatements().add(stmt);
					}
				}
			}
		}
		if (block != null) {
			if (context instanceof Part) {
				// TODO: Handle initializers on Parts
			}
			else if (context instanceof Field) {
				((Field)context).setInitializerStatements(block);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Evaluate an AST Expression in the MOF context
	 */
	public Object evaluateExpression(Node expr) {
		Object value = null;
		if (expr instanceof ArrayLiteral) {
			value = new EList();
			for (org.eclipse.edt.compiler.core.ast.Expression ex : (List<org.eclipse.edt.compiler.core.ast.Expression>)(((ArrayLiteral)expr).getExpressions())) {
				((List)value).add(evaluateExpression(ex));
			}
		}
		else if (expr instanceof BooleanLiteral) {
			value = ((BooleanLiteral)expr).booleanValue() == org.eclipse.edt.compiler.core.Boolean.YES ? Boolean.TRUE : Boolean.FALSE;
		}
		else if (expr instanceof BytesLiteral) {
			value = ((BytesLiteral)expr).getValue();
		}
		else if (expr instanceof IntegerLiteral) {
			try {
				switch (((IntegerLiteral)expr).getLiteralKind()) {
					case LiteralExpression.BIGINT_LITERAL:
						value = Long.parseLong(((IntegerLiteral)expr).getValue());
						break;
					case LiteralExpression.SMALLINT_LITERAL:
						value = Short.parseShort(((IntegerLiteral)expr).getValue());
						break;
					case LiteralExpression.INTEGER_LITERAL:
					default:
						value = Integer.parseInt(((IntegerLiteral)expr).getValue());
						break;
				}
			} catch (NumberFormatException e) {
			}
		}
		else if (expr instanceof StringLiteral) {
			value = ((StringLiteral)expr).getValue();
		}
		else if (expr instanceof FloatLiteral) {
			value = Float.parseFloat(((FloatLiteral)expr).getValue());
		}
		else if (expr instanceof AnnotationExpression) {
			ITypeBinding typeBinding = ((AnnotationExpression)expr).resolveTypeBinding();
			EClass annType = (EClass)mofTypeFor(typeBinding);
			if (annType != null) {
				value = annType.newInstance();
			}
		}
		else if (expr instanceof SetValuesExpression) {
			value = evaluateExpression(((SetValuesExpression)expr).getExpression());
			if (((SetValuesExpression)expr).getSettingsBlock() != null)
				executeSettings(value, ((SetValuesExpression)expr).getSettingsBlock());		
		}
		else if (expr instanceof org.eclipse.edt.compiler.core.ast.Name) {
			expr.accept(this);
			value = stack.pop();
		}
		
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public void executeSettings(Object context, SettingsBlock settings) {
		if (settings == null) return;
		for (Node setting : (List<Node>)settings.getSettings()) {
			if (context instanceof List) {
				((List)context).add(evaluateExpression(setting));
			}
			else if (context instanceof Annotation){
				EObject target = (EObject)context;
				if (setting instanceof org.eclipse.edt.compiler.core.ast.Assignment) {
					org.eclipse.edt.compiler.core.ast.Assignment assignment = (org.eclipse.edt.compiler.core.ast.Assignment)setting;
					org.eclipse.edt.compiler.core.ast.Name nameExpr = (org.eclipse.edt.compiler.core.ast.Name)assignment.getLeftHandSide();
					Object source = null;
					// Look for field reference in the context
					// If not found the assignment is really an AnnotationExpression
					// as in "displayName = "abc" being the same thing as "@DisplayName{"abc")"
					EField field = (EField)target.getEClass().getEField((nameExpr.getNameComponents()[0]));
					if (field == null) {
						if (Binding.isValidBinding(nameExpr.resolveBinding())) {
							source = mofValueFrom(nameExpr.resolveBinding());
							((Element)target).getAnnotations().add((Annotation)source);
						}
					}
					else {
						if (assignment.getRightHandSide() instanceof NullLiteral) {
							source = factory.createNullLiteral();
						}
						else {
							source = evaluateExpression(assignment.getRightHandSide());
						}
						String[] names = nameExpr.getNameComponents();
						if (nameExpr.getNameComponents().length > 1) {
							for (int i=1; i<names.length; i++) {
								target = (EObject)target.eGet(field);
								field = target.getEClass().getEField(names[i]);
							}
						}
						eSet(target, field, source);
					}
				}
				else if (setting instanceof SetValuesExpression) {
					Annotation value = (Annotation)evaluateExpression(setting);
					((Element)target).getAnnotations().add(value);
				}
				else {
					Object value = evaluateExpression(setting);
					eSet(target, target.getEClass().getEFields().get(0), value);
				}
			}	
		}
	}
	
	public void setInitialValue(StructureItem node, EField field) {
		setInitialValue(node.getInitializer(), node.getSettingsBlock(), field);
	}
	
	public void setInitialValue(ClassDataDeclaration node, EField field) {
		setInitialValue(node.getInitializer(), node.getSettingsBlockOpt(), field);
	}
	
	@SuppressWarnings("unchecked")
	public void setInitialValue(org.eclipse.edt.compiler.core.ast.Expression initializer, SettingsBlock settings, EField field) {
		Object value = null;
		if (initializer != null) {
			
			//Store literal initial values in AnnotationType and StereoTypeType as Literal expressions
			if (initializer instanceof LiteralExpression && inAnnotationTypeContext && !inEMetadataTypeContext) {
				initializer.accept(this);
				value = stack.pop();
			}
			else {
				value = evaluateExpression(initializer);
			}
		}
		else if (settings != null && isInitializer(settings)) {
			EType type = field.getEType();
			if (type instanceof EClass) {
				value = ((EClass)type).newInstance();
			}
			else {
				// Must be a List type
				value = new EList();
			}
			executeSettings(value, settings);
		}
		field.setInitialValue(value);		
	}
	
	/**
	 * Returns true if the settings contain instance initializers and not just annotations
	 * @param settings
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isInitializer(SettingsBlock settings) {
		for (Node expr : (List<Node>)settings.getSettings()) {
			if (expr instanceof org.eclipse.edt.compiler.core.ast.Assignment) {
				// Check if this is an Annotation value
				org.eclipse.edt.compiler.core.ast.Assignment assignment = (org.eclipse.edt.compiler.core.ast.Assignment)expr;
				org.eclipse.edt.compiler.core.ast.Name nameExpr = (org.eclipse.edt.compiler.core.ast.Name)assignment.getLeftHandSide();
				if (!(nameExpr.resolveDataBinding() instanceof IAnnotationBinding)) {
					return true;
				}
			}
			else if (!(expr instanceof AnnotationExpression)) {
				return true;
			}
		}
		return false;
	}
	
	public void addInitializers(StructureItem node, Field field, Type type) {
		addInitializers(node.getInitializer(), node.getSettingsBlock(), field, type);
	}
	
	public void addInitializers(ClassDataDeclaration node, Field field, Type type) {
		addInitializers(node.getInitializer(), node.getSettingsBlockOpt(), field, type);
	}
	
	public void addInitializers(org.eclipse.edt.compiler.core.ast.Expression initializer, SettingsBlock settingsBlock, Field field, Type type) {
		if (field.getType() == null) {
			return;
		}
		
		if (initializer != null) {

			//handle the annotations
			if (settingsBlock != null) {
				processSettings(field, settingsBlock);
			}
			
			//Initializers may have been created when processing the settings
			if (field.getInitializerStatements() == null) {
				field.setInitializerStatements(factory.createStatementBlock());
			}
			
			setElementInformation(initializer, field.getInitializerStatements());
			initializer.accept(this);
			Expression expr = (Expression)stack.pop();
			if (field instanceof ConstantField && expr instanceof PrimitiveTypeLiteral) {
				((ConstantField)field).setValue((PrimitiveTypeLiteral)expr);
			}
			Statement stmt = createAssignmentStatement(field, expr);
			setElementInformation(initializer, stmt);
			field.getInitializerStatements().getStatements().add(stmt);
			field.setHasSetValuesBlock(true);
		}
		else {
			if (settingsBlock != null) {
				processSettings(field, settingsBlock);
			}
		}
		
	}

	
	public AssignmentStatement createAssignmentStatement(Element context, Expression rhs) {
		if (context instanceof Field) return createAssignmentStatement((Field)context, rhs);
		else if (context instanceof Expression) return createAssignmentStatement((Expression)context, rhs);
		else throw new IllegalArgumentException("Context " + context.toString() + " must be a Field or Expression");
	}
	
	public AssignmentStatement createAssignmentStatement(Field field, Expression rhs) {
		Assignment assign = factory.createAssignment();
		assign.setRHS(rhs);
		MemberName nameExpr = factory.createMemberName();
		nameExpr.setMember(field);
		nameExpr.setId(field.getName());
		assign.setLHS(nameExpr);
		return createAssignmentStatement(assign);
	}
		
	public AssignmentStatement createAssignmentStatement(Assignment assign) {
		AssignmentStatement stmt = factory.createAssignmentStatement();
		stmt.setExpr(assign);
		return stmt;
	}
	
	public LHSExpr addQualifier(Element context, LHSExpr expr) {
				
		if (context instanceof Member) {
			
			return addQualifier((Member)context, (LHSExpr)expr);
		}
		
		if (context instanceof Expression) {
			if (expr instanceof InvalidName) {
				return expr;
			}
			return ((LHSExpr)expr).addQualifier((Expression)context);
		}
		
		throw new IllegalArgumentException("Qualifier " + context.toString() + " must be a Member or Expression");
	}
	
	public LHSExpr addQualifier(Member context, LHSExpr lhsExpr) {
		MemberName qualifier = createMemberName(context);
		return lhsExpr.addQualifier(qualifier);
	}


	public MemberName createMemberName(Member context) {
		MemberName mbrName = factory.createMemberName();
		mbrName.setId(context.getName());
		mbrName.setMember(context);
		return mbrName;
	}
	
	public LHSExpr addQualifier(Part context, Name nameExpr) {
		PartName qualifier = factory.createPartName();
		qualifier.setId(context.getName());
		qualifier.setType(context);
		qualifier.setPackageName(context.getPackageName());
		return nameExpr.addQualifier(qualifier);
	}
	
}

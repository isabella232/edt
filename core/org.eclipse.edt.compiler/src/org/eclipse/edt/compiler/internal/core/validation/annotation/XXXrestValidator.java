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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public abstract class XXXrestValidator implements IAnnotationValidationRule {
	
	private static class SubstitutionVar {
		int startOffset;
		int endOffset;
		String varName;
		public SubstitutionVar(int startOffset, int endOffset, String uriTemplate) {
			super();
			this.endOffset = endOffset;
			this.startOffset = startOffset;
			this.varName = uriTemplate.substring(startOffset, endOffset);
		}
		
		public int getStartOffset() {
			return startOffset;
		}
		public int getEndOffset() {
			return endOffset;
		}
		public String getVarName() {
			return varName;
		}		
	}	
	
	private Map allAnnotations;
	private HashMap parmNamesToNodes = new HashMap();
	private HashMap namesToSubstitutionVars = new HashMap();
	private Node errorNode;
	private Node uriTemplateNode;
	private Node responseFormatNode;
	private Node requestFormatNode;
	
	private boolean isResourceType(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}

		// If responseFormat is JSON, allow single dimension array of strings or flexible records 
		if (getResponseFormat() == InternUtil.intern("json") && type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			type = ((ArrayTypeBinding)type).getElementType();
		}
		
		if (type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			return ((PrimitiveTypeBinding)type).getPrimitive() == Primitive.STRING;
		}
		
		return type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING;
	}
	
	private boolean isIn(FunctionParameter parm) {
		if (parm.getName().resolveBinding() instanceof FunctionParameterBinding) {
			return ((FunctionParameterBinding)parm.getName().resolveBinding()).isInput();
		}
		
		return parm.getUseType() == UseType.IN;
	}
	
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
		this.errorNode = errorNode;
		this.allAnnotations = allAnnotations;
		final boolean[] isFunction = new boolean[1];
		final Type[] returnType = new Type[1];
		final List parms = new ArrayList();
		final String[] funcName = new String[1];
		
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.NestedFunction nestedFunction) {
				isFunction[0] = true;
				if (nestedFunction.hasReturnType()) {
					returnType[0] = nestedFunction.getReturnType();
				}
				parms.addAll(nestedFunction.getFunctionParameters());
				funcName[0] = nestedFunction.getName().getCanonicalName();
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.TopLevelFunction topLevelFunction) {
				isFunction[0] = true;
				if (topLevelFunction.hasReturnType()) {
					returnType[0] = topLevelFunction.getReturnType();
				}
				parms.addAll(topLevelFunction.getFunctionParameters());
				funcName[0] = topLevelFunction.getName().getCanonicalName();
				return false;
			}
		};
		target.accept(visitor);
		
		if (!isFunction[0]) {
			return;
		}
		
		final boolean[] correctPart = new boolean[1];
		target.getParent().accept(new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.Library node) {
				correctPart[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.Handler node) {
				correctPart[0] = true;
				return false;
			}
		});
		
		if (!correctPart[0]) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.ANNOTATION_NOT_APPLICABLE, IMarker.SEVERITY_ERROR, new String[] {getName()});
		}

		//All parameters must be IN
		Iterator i = parms.iterator();
		while (i.hasNext()) {
			FunctionParameter parm = (FunctionParameter)i.next();
			parmNamesToNodes.put(parm.getName().getCanonicalName().toUpperCase().toLowerCase(), parm);
			
			if (!isIn(parm)) {
				problemRequestor.acceptProblem(parm, IProblemRequestor.XXXREST_ALL_PARMS_MUST_BE_IN, IMarker.SEVERITY_ERROR, new String[] { parm.getName().getCanonicalName(), funcName[0], getName()});
			}
		}
		
		if (returnType[0] != null) {
			if (Binding.isValidBinding(returnType[0].resolveTypeBinding())) {
				//If the function returns a type, it must be a resource
				if (!isResourceType(returnType[0].resolveTypeBinding())) {
					problemRequestor.acceptProblem(returnType[0], IProblemRequestor.XXXREST_MUST_RETURN_RESOURCE, IMarker.SEVERITY_ERROR, new String[] {funcName[0], getName()});
				}
				//If the return type is String, the responseFormat must be NONE if is is specified
				if (returnType[0].resolveTypeBinding() == PrimitiveTypeBinding.getInstance(Primitive.STRING)) {
					String reqForm = getRequestFormat();
					String respForm = getResponseFormat();
					if (respForm != null && InternUtil.intern("none") != respForm) {
						problemRequestor.acceptProblem(getResponseFormatNode(), IProblemRequestor.XXXREST_FORMAT_MUST_BE_NONE, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_RESPONSEFORMAT});
					}
				}
			}
		}
		
		parseSubtitutionVars();
		
		//loop through the parms and do resource/non-resource checks
		boolean foundResourceParm = false;
		i = parmNamesToNodes.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			FunctionParameter parm = (FunctionParameter) parmNamesToNodes
					.get(key);
			if (namesToSubstitutionVars.get(key) == null) {

				if (supportsResourceParm()) {
					// Only 1 resource parameter is allowed!
					if (foundResourceParm) {
						problemRequestor.acceptProblem(parm,
								IProblemRequestor.XXXREST_ONLY_1_RESOURCE_PARM,
								IMarker.SEVERITY_ERROR, new String[] {
										funcName[0], getName(),
										parm.getName().getCanonicalName() });
					} else {
						foundResourceParm = true;
						// resource param must be resource
						if (Binding.isValidBinding(parm.getType()
								.resolveTypeBinding())) {
							if (!isResourceType(parm.getType()
									.resolveTypeBinding())) {
								problemRequestor
										.acceptProblem(
												parm.getType(),
												IProblemRequestor.XXXREST_RESOURCE_PARM_MUST_BE_RESOURCE,
												IMarker.SEVERITY_ERROR,
												new String[] {
														parm
																.getName()
																.getCanonicalName(),
														funcName[0], getName() });
							} else {
								// if the resource parameter is String, the
								// request Format has to be NONE if specified
								if (parm.getType().resolveTypeBinding() == PrimitiveTypeBinding
										.getInstance(Primitive.STRING)) {
									String reqForm = getRequestFormat();
									String respForm = getResponseFormat();
									if (reqForm != null
											&& InternUtil.intern("none") != reqForm) {
										problemRequestor
												.acceptProblem(
														getRequestFormatNode(),
														IProblemRequestor.XXXREST_FORMAT_MUST_BE_NONE,
														IMarker.SEVERITY_ERROR,
														new String[] { IEGLConstants.PROPERTY_REQUESTFORMAT });
									}
								}
								// If the requestFormat is formData, then the
								// resource parameter must be a flat record
								if (InternUtil.intern("formdata") == getRequestFormat()) {
									ITypeBinding type = parm.getType()
											.resolveTypeBinding();
									if (!isFlatRecord(type)) {
										problemRequestor
												.acceptProblem(
														parm.getType(),
														IProblemRequestor.XXXREST_PARM_TYPE_MUST_BE_FLAT_RECORD,
														IMarker.SEVERITY_ERROR,
														new String[] {
																parm
																		.getName()
																		.getCanonicalName(),
																funcName[0] });
									}
								}
							}
						}
					}
				}
				else {
					problemRequestor.acceptProblem(parm,
							IProblemRequestor.XXXREST_NO_RESOURCE_PARM,
							IMarker.SEVERITY_ERROR, new String[] {
									funcName[0], getName(),
									parm.getName().getCanonicalName() });
				}
			} else {
				// non resource parm must be compatible with String
				if (Binding.isValidBinding(parm.getType().resolveTypeBinding())) {
					if (!TypeCompatibilityUtil.isMoveCompatible(
							PrimitiveTypeBinding.getInstance(Primitive.STRING),
							parm.getType().resolveTypeBinding(), null,
							compilerOptions)) {
						problemRequestor
								.acceptProblem(
										parm.getType(),
										IProblemRequestor.XXXREST_NON_RESOUCE_MUST_BE_STRING_COMPAT,
										IMarker.SEVERITY_ERROR, new String[] {
												parm.getName()
														.getCanonicalName(),
												funcName[0], getName() });
					}
				}
			}
		}
		
		//loop through the substitution variables
		i = namesToSubstitutionVars.keySet().iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			SubstitutionVar var = (SubstitutionVar)namesToSubstitutionVars.get(key);
			int absStart = getUriTemplateNode().getOffset() + 1;
			FunctionParameter parm = (FunctionParameter) parmNamesToNodes.get(key);
			if (parm == null) {
				//substitution variable must match a parameter name
				problemRequestor.acceptProblem(absStart + var.getStartOffset(), absStart + var.getEndOffset(), IMarker.SEVERITY_ERROR, IProblemRequestor.XXXREST_UMATCHED_SUBS_VAR, new String[] {var.getVarName(), funcName[0]});

			}
			else {
				
			}
		}
		
		//validate that responseFormat is not formData
		if (getResponseFormat() == InternUtil.intern("formData")) {
			problemRequestor.acceptProblem(getResponseFormatNode(), IProblemRequestor.XXXREST_RESPONSEFORMAT_NOT_SUPPORTD, IMarker.SEVERITY_ERROR, new String[] { getResponseFormat(), IEGLConstants.PROPERTY_RESPONSEFORMAT});
		}
		
		if (!methodIsValid()) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.XXXREST_NO_METHOD, IMarker.SEVERITY_ERROR, new String[] {});
		}
		
		
	}

	protected boolean methodIsValid(){
		return getMethod() != null;
	}
	
	private boolean isFlatRecord(ITypeBinding binding) {
		if (!Binding.isValidBinding(binding)) {
			return false;
		}
		
		if (binding.getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING) {
			return false;
		}
		
		FlexibleRecordBinding flexRec = (FlexibleRecordBinding)binding;
		IDataBinding[] fields = flexRec.getFields();
		for (int i = 0; i < fields.length; i++) {
			if (Binding.isValidBinding(fields[i].getType()) && Binding.isValidBinding(fields[i].getType().getBaseType())) {
				if (fields[i].getType().getBaseType().getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
					return false;
				}
			}
		}
		return true;
		
	}
	private Node getRequestFormatNode() {
		if (requestFormatNode == null) {
			requestFormatNode = getAnnotationValueNode(IEGLConstants.PROPERTY_REQUESTFORMAT);
		}
		return requestFormatNode;
		
	}

	private Node getResponseFormatNode() {
		if (responseFormatNode == null) {
			responseFormatNode = getAnnotationValueNode(IEGLConstants.PROPERTY_RESPONSEFORMAT);
		}
		return responseFormatNode;		
	}

	private IAnnotationBinding getMethod() {
		return (IAnnotationBinding)allAnnotations.get(InternUtil.intern("method"));

	}

	private String getRequestFormat() {
		IAnnotationBinding binding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_REQUESTFORMAT));
		if (binding == null) {
			return null;
		}
		
		return ((IDataBinding)binding.getValue()).getName();
	}

	private String getResponseFormat() {
		IAnnotationBinding binding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_RESPONSEFORMAT));
		if (binding == null) {
			return null;
		}
		
		return ((IDataBinding)binding.getValue()).getName();
	}

	private Node getUriTemplateNode() {
		if (uriTemplateNode == null) {
			uriTemplateNode = getAnnotationValueNode(IEGLConstants.PROPERTY_URITEMPLATE);
		}
		return uriTemplateNode;
	}

	private Node getAnnotationValueNode(String annName) {
		final Node[] result = new Node[1];
		final String name = InternUtil.intern(annName);
		errorNode.getParent().accept(new AbstractASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.Assignment assignment) {
				if (assignment.resolveBinding() != null && assignment.resolveBinding().getName() == name) {
					result[0] = assignment.getRightHandSide();
				}
				return false;
			}
		});
			
		return result[0];
		
	}

	
	private void parseSubtitutionVars() {
		IAnnotationBinding uriTempAnn = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_URITEMPLATE));
		if (uriTempAnn == null) {
			return;
		}
		String value = (String)uriTempAnn.getValue();
		char[] chars = value.toCharArray();
		
		int lOffset = 0;
		
		boolean lookingForL = true;
		for (int i = 0; i < chars.length; i++) {
			if (lookingForL) {
				if (chars[i] == '{') {
					lOffset = i;
					lookingForL = false;
				}
			}
			else {
				if (chars[i] == '}') {
					SubstitutionVar var = new SubstitutionVar(lOffset + 1, i, value);
					namesToSubstitutionVars.put(var.getVarName().toUpperCase().toLowerCase(), var);
					lookingForL = true;					
				}
			}
		}						
	}
	
	protected abstract String getName();
	
	private boolean supportsResourceParm() {
		IAnnotationBinding method = getMethod();
		return !(method != null && method.getValue() != null && "_GET".equalsIgnoreCase(method.getValue().toString()));
	}
}

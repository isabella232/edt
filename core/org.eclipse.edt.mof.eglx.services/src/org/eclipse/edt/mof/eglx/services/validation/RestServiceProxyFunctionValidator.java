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
package org.eclipse.edt.mof.eglx.services.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.FunctionValidator;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;

/**
 * @author demurray
 */
public class RestServiceProxyFunctionValidator extends
		ServiceProxyFunctionValidator {

	private static class SubstitutionVar {
		int startOffset;
		int endOffset;
		String varName;

		public SubstitutionVar(int startOffset, int endOffset,
				String uriTemplate) {
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

	private NestedFunction nestedFunction;
	private Node uriTemplateNode;
	private Node responseFormatNode;
	private Node requestFormatNode;

	protected void validate(NestedFunction nestedFunction,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {

		Function function = (Function) nestedFunction.getName().resolveMember();
		Annotation restAnnotation = function.getAnnotation("eglx.rest.Rest");
		this.nestedFunction = nestedFunction;

		final List<FunctionParameter> parms = new ArrayList<FunctionParameter>();
		if (function.getReturnType() != null) {
			// If the function returns a type, it must be a resource
			if (!isResourceType(function.getReturnType(), restAnnotation)) {
				problemRequestor.acceptProblem(function.getReturnType(),
						ResourceKeys.XXXREST_MUST_RETURN_RESOURCE,
						IMarker.SEVERITY_ERROR,
						new String[] { function.getName(), getName() },
						ResourceKeys.getResourceBundleForKeys());
			}
			// If the return type is String, the responseFormat must be NONE if
			// is is specified
			if (function.getReturnType().equals(TypeUtils.Type_STRING)) {
				String respForm = (String) restAnnotation
						.getValue(IEGLConstants.PROPERTY_RESPONSEFORMAT);
				if (respForm != null && respForm.length() > 0) {
					problemRequestor
							.acceptProblem(
									getResponseFormatNode(),
									ResourceKeys.XXXREST_FORMAT_MUST_BE_NONE,
									IMarker.SEVERITY_ERROR,
									new String[] { IEGLConstants.PROPERTY_RESPONSEFORMAT },
									ResourceKeys.getResourceBundleForKeys());
				}
			}
		}

		HashMap<String, FunctionParameter> parmNamesToNodes = new HashMap<String, FunctionParameter>();
		// All parameters must be IN
		for (FunctionParameter parm : parms) {
			parmNamesToNodes.put(parm.getName().getCanonicalName()
					.toLowerCase(), parm);

			if (parm.getUseType() != UseType.IN) {
				problemRequestor.acceptProblem(parm,
						ResourceKeys.XXXREST_ALL_PARMS_MUST_BE_IN,
						IMarker.SEVERITY_ERROR,
						new String[] { parm.getName().getCanonicalName(),
								function.getName(), getName() },
						ResourceKeys.getResourceBundleForKeys());
			}
		}

		HashMap<String, SubstitutionVar> namesToSubstitutionVars = parseSubtitutionVars(restAnnotation);

		// loop through the parms and do resource/non-resource checks
		boolean foundResourceParm = false;
		for (FunctionParameter parm : parmNamesToNodes.values()) {
			if (namesToSubstitutionVars.get(parm.getName().getCanonicalName()) == null) {

				if (supportsResourceParm(restAnnotation)) {
					// Only 1 resource parameter is allowed!
					if (foundResourceParm) {
						problemRequestor.acceptProblem(parm,
								ResourceKeys.XXXREST_ONLY_1_RESOURCE_PARM,
								IMarker.SEVERITY_ERROR,
								new String[] { function.getName(), getName(),
										parm.getName().getCanonicalName() },
								ResourceKeys.getResourceBundleForKeys());
					} else {
						foundResourceParm = true;
						// resource param must be resource
						if (parm.getType().resolveType() != null) {
							if (!isResourceType(parm.getType().resolveType(),
									restAnnotation)) {
								problemRequestor
										.acceptProblem(
												parm.getType(),
												ResourceKeys.XXXREST_RESOURCE_PARM_MUST_BE_RESOURCE,
												IMarker.SEVERITY_ERROR,
												new String[] {
														parm.getName()
																.getCanonicalName(),
														function.getName(),
														getName() },
												ResourceKeys
														.getResourceBundleForKeys());
							} else {
								// if the resource parameter is String, the
								// request Format has to be NONE if specified
								if (parm.getType().resolveType()
										.equals(TypeUtils.Type_STRING)) {
									String reqForm = (String) restAnnotation
											.getValue(IEGLConstants.PROPERTY_REQUESTFORMAT);
									if (reqForm != null
											&& "none".equals(reqForm)) {
										problemRequestor
												.acceptProblem(
														getRequestFormatNode(),
														ResourceKeys.XXXREST_FORMAT_MUST_BE_NONE,
														IMarker.SEVERITY_ERROR,
														new String[] { IEGLConstants.PROPERTY_REQUESTFORMAT },
														ResourceKeys
																.getResourceBundleForKeys());
									}
								}
								// If the requestFormat is formData, then the
								// resource parameter must be a flat record
								if ("formdata"
										.equals((String) restAnnotation
												.getValue(IEGLConstants.PROPERTY_REQUESTFORMAT))) {
									org.eclipse.edt.mof.egl.Type type = parm
											.getType().resolveType();
									if (!isFlatRecord(type)) {
										problemRequestor
												.acceptProblem(
														parm.getType(),
														ResourceKeys.XXXREST_PARM_TYPE_MUST_BE_FLAT_RECORD,
														IMarker.SEVERITY_ERROR,
														new String[] {
																parm.getName()
																		.getCanonicalName(),
																function.getName() },
														ResourceKeys
																.getResourceBundleForKeys());
									}
								}
							}
						}
					}
				} else {
					problemRequestor.acceptProblem(parm,
							ResourceKeys.XXXREST_NO_RESOURCE_PARM,
							IMarker.SEVERITY_ERROR,
							new String[] { function.getName(), getName(),
									parm.getName().getCanonicalName() },
							ResourceKeys.getResourceBundleForKeys());
				}
			} else {
				// non resource parm must be compatible with String
				if (parm.getType().resolveType() != null) {
					if (!TypeCompatibilityUtil
							.isMoveCompatible(TypeUtils.Type_STRING, parm
									.getType().resolveType(), null,
									compilerOptions)) {
						problemRequestor
								.acceptProblem(
										parm.getType(),
										ResourceKeys.XXXREST_NON_RESOUCE_MUST_BE_STRING_COMPAT,
										IMarker.SEVERITY_ERROR,
										new String[] {
												parm.getName()
														.getCanonicalName(),
												function.getName(), getName() },
										ResourceKeys.getResourceBundleForKeys());
					}
				}
			}
		}

		// loop through the substitution variables
		for (String key : namesToSubstitutionVars.keySet()) {
			SubstitutionVar var = namesToSubstitutionVars.get(key);
			int absStart = getUriTemplateNode().getOffset() + 1;
			FunctionParameter parm = (FunctionParameter) parmNamesToNodes
					.get(key);
			if (parm == null) {
				// substitution variable must match a parameter name
				problemRequestor.acceptProblem(absStart + var.getStartOffset(),
						absStart + var.getEndOffset(), IMarker.SEVERITY_ERROR,
						ResourceKeys.XXXREST_UMATCHED_SUBS_VAR, new String[] {
								var.getVarName(), function.getName() },
						ResourceKeys.getResourceBundleForKeys());

			} else {

			}
		}

		// validate that responseFormat is not formData
		if ("formData".equals((String) restAnnotation
				.getValue(IEGLConstants.PROPERTY_RESPONSEFORMAT))) {
			problemRequestor
					.acceptProblem(
							restAnnotation,
							ResourceKeys.XXXREST_RESPONSEFORMAT_NOT_SUPPORTD,
							IMarker.SEVERITY_ERROR,
							new String[] {
									(String) restAnnotation
											.getValue(IEGLConstants.PROPERTY_RESPONSEFORMAT),
									IEGLConstants.PROPERTY_RESPONSEFORMAT },
							ResourceKeys.getResourceBundleForKeys());
		}

	}

	private boolean isResourceType(org.eclipse.edt.mof.egl.Type type,
			Annotation restAnnotation) {
		if (type == null) {
			return false;
		}

		// If responseFormat is JSON, allow single dimension array of strings or
		// flexible records
		if ("json"
				.equals(((String) restAnnotation
						.getValue(IEGLConstants.PROPERTY_RESPONSEFORMAT))
						.toLowerCase())
				&& type instanceof ArrayType) {
			type = ((ArrayType) type).getElementType();
		}

		if (type.equals(TypeUtils.Type_STRING)) {
			return true;
		}

		return type instanceof Record;
	}

	private boolean isFlatRecord(org.eclipse.edt.mof.egl.Type type) {
		if (type == null) {
			return false;
		}

		if (!(type instanceof Record)) {
			return false;
		}

		for (Member member : ((Record) type).getMembers()) {
			if (member.getType() instanceof Record) {
				return false;
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

	private Node getUriTemplateNode() {
		if (uriTemplateNode == null) {
			uriTemplateNode = getAnnotationValueNode(IEGLConstants.PROPERTY_URITEMPLATE);
		}
		return uriTemplateNode;
	}

	private Node getAnnotationValueNode(String annName) {
		final Node[] result = new Node[1];
		final String name = InternUtil.intern(annName);
		nestedFunction.getParent().accept(new AbstractASTVisitor() {
			public boolean visit(
					org.eclipse.edt.compiler.core.ast.Assignment assignment) {
				if (assignment.resolveBinding() != null
						&& assignment.resolveBinding().getEClass()
								.getETypeSignature().equals(name)) {
					result[0] = assignment.getRightHandSide();
				}
				return false;
			}
		});

		return result[0];

	}

	private HashMap<String, SubstitutionVar> parseSubtitutionVars(
			Annotation restAnnotation) {
		HashMap<String, SubstitutionVar> namesToSubstitutionVars = new HashMap<String, SubstitutionVar>();
		String uriTemp = (String) restAnnotation
				.getValue(IEGLConstants.PROPERTY_URITEMPLATE);
		char[] chars = uriTemp.toCharArray();

		int lOffset = 0;

		boolean lookingForL = true;
		for (int i = 0; i < chars.length; i++) {
			if (lookingForL) {
				if (chars[i] == '{') {
					lOffset = i;
					lookingForL = false;
				}
			} else {
				if (chars[i] == '}') {
					SubstitutionVar var = new SubstitutionVar(lOffset + 1, i,
							uriTemp);
					namesToSubstitutionVars.put(var.getVarName().toUpperCase()
							.toLowerCase(), var);
					lookingForL = true;
				}
			}
		}
		return namesToSubstitutionVars;
	}

	private boolean supportsResourceParm(Annotation restAnnotation) {
		String method = (String) restAnnotation.getValue("method");
		return !(method != null && "_get"
				.equalsIgnoreCase(method.toLowerCase()));
	}

	protected String getName() {
		return "REST";
	}

}

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

import java.util.HashMap;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.services.ext.Utils;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author demurray
 */
public class RestServiceProxyFunctionValidator extends ServiceProxyFunctionValidator {

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

	@Override
	protected Annotation getAnnotation(Function function) {
		return function.getAnnotation("eglx.rest.Rest");
	}
	@Override
	protected void validate(NestedFunction nestedFunction) {

		Function function = (Function) nestedFunction.getName().resolveMember();
		Annotation restAnnotation = getAnnotation(function);

		if (function.getReturnType() != null) {
			// If the function returns a type, it must be a resource
			if (!isResourceType(function.getReturnType(), restAnnotation)) {
				problemRequestor.acceptProblem(nestedFunction.getReturnType(),
						ResourceKeys.XXXREST_MUST_RETURN_RESOURCE,
						IMarker.SEVERITY_ERROR,
						new String[] { function.getCaseSensitiveName(), getName() },
						ResourceKeys.getResourceBundleForKeys());
			}
			// If the return type is String, the responseFormat must be NONE if
			// is is specified
			if (function.getReturnType().equals(TypeUtils.Type_STRING)) {
				EnumerationEntry respForm = (EnumerationEntry) restAnnotation.getValue(IEGLConstants.PROPERTY_RESPONSEFORMAT);
				if (respForm != null) {
					problemRequestor.acceptProblem(
									nestedFunction.getReturnType(),
									ResourceKeys.XXXREST_FORMAT_MUST_BE_NONE,
									IMarker.SEVERITY_ERROR,
									new String[] { IEGLConstants.PROPERTY_RESPONSEFORMAT },
									ResourceKeys.getResourceBundleForKeys());
				}
			}
		}

		HashMap<String, FunctionParameter> parmNamesToNodes = new HashMap<String, FunctionParameter>();
		// All parameters must be IN
		for (Object parm : nestedFunction.getFunctionParameters()) {
			parmNamesToNodes.put(((FunctionParameter)parm).getName().getCanonicalName().toLowerCase(),(FunctionParameter)parm);

			if (((FunctionParameter)parm).getUseType() != UseType.IN) {
				problemRequestor.acceptProblem((FunctionParameter)parm,
						ResourceKeys.XXXREST_ALL_PARMS_MUST_BE_IN,
						IMarker.SEVERITY_ERROR,
						new String[] { ((FunctionParameter)parm).getName().getCanonicalName(),
								function.getCaseSensitiveName(), getName() },
						ResourceKeys.getResourceBundleForKeys());
			}
		}

		HashMap<String, SubstitutionVar> namesToSubstitutionVars = parseSubtitutionVars(restAnnotation);

		// loop through the parms and do resource/non-resource checks
		boolean foundResourceParm = false;
		for (FunctionParameter parm : parmNamesToNodes.values()) {
			if (namesToSubstitutionVars.get(NameUtile.getAsName(parm.getName().getCanonicalName())) == null) {

				if (supportsResourceParm(restAnnotation)) {
					// Only 1 resource parameter is allowed!
					if (foundResourceParm) {
						problemRequestor.acceptProblem(parm,
								ResourceKeys.XXXREST_ONLY_1_RESOURCE_PARM,
								IMarker.SEVERITY_ERROR,
								new String[] { function.getCaseSensitiveName(), getName(),
										parm.getName().getCanonicalName() },
								ResourceKeys.getResourceBundleForKeys());
					} else {
						foundResourceParm = true;
						// resource param must be resource
						if (parm.getType().resolveType() != null) {
							if (!isResourceType(parm.getType().resolveType(),
									restAnnotation)) {
								problemRequestor.acceptProblem(
												parm,
												ResourceKeys.XXXREST_RESOURCE_PARM_MUST_BE_RESOURCE,
												IMarker.SEVERITY_ERROR,
												new String[] {
														parm.getName().getCanonicalName(),
														function.getCaseSensitiveName(),
														getName() },
												ResourceKeys.getResourceBundleForKeys());
							} else {
								// if the resource parameter is String, the
								// request Format has to be NONE if specified
								if (parm.getType().resolveType().equals(TypeUtils.Type_STRING)) {
									EnumerationEntry reqForm = (EnumerationEntry) restAnnotation.getValue(IEGLConstants.PROPERTY_REQUESTFORMAT);
									if (reqForm != null
											&& !NameUtile.equals(NameUtile.getAsName("none"), reqForm.getName())) {
										problemRequestor.acceptProblem(
														Utils.getRequestFormat(nestedFunction),
														ResourceKeys.XXXREST_FORMAT_MUST_BE_NONE,
														IMarker.SEVERITY_ERROR,
														new String[] { IEGLConstants.PROPERTY_REQUESTFORMAT },
														ResourceKeys.getResourceBundleForKeys());
									}
								}
								// If the requestFormat is formData, then the
								// resource parameter must be a flat record
								EnumerationEntry reqForm = (EnumerationEntry) restAnnotation.getValue(IEGLConstants.PROPERTY_REQUESTFORMAT);
								if (reqForm != null && NameUtile.equals(NameUtile.getAsName("_form"), reqForm.getName())) {
									org.eclipse.edt.mof.egl.Type type = parm.getType().resolveType();
									if (!isFlatRecord(type)) {
										problemRequestor.acceptProblem(
														parm,
														ResourceKeys.XXXREST_PARM_TYPE_MUST_BE_FLAT_RECORD,
														IMarker.SEVERITY_ERROR,
														new String[] {
																parm.getName().getCanonicalName(),
																function.getCaseSensitiveName() },
														ResourceKeys
																.getResourceBundleForKeys());
									}
								}
							}
						}
					}
				} else {
					problemRequestor.acceptProblem(nestedFunction,
							ResourceKeys.XXXREST_NO_RESOURCE_PARM,
							IMarker.SEVERITY_ERROR,
							new String[] { function.getCaseSensitiveName(), getName(),
									parm.getName().getCanonicalName() },
							ResourceKeys.getResourceBundleForKeys());
				}
			} else {
				// non resource parm must be compatible with String
				if (parm.getType().resolveType() != null) {
					Member member =  parm.getName().resolveMember();
					if (member != null && !IRUtils.isMoveCompatible(TypeUtils.Type_STRING, member.getType(), member)) {
						problemRequestor.acceptProblem(
										parm,
										ResourceKeys.XXXREST_NON_RESOUCE_MUST_BE_STRING_COMPAT,
										IMarker.SEVERITY_ERROR,
										new String[] {
												parm.getName().getCanonicalName(),
												function.getCaseSensitiveName(), getName() },
										ResourceKeys.getResourceBundleForKeys());
					}
				}
			}
		}

		// loop through the substitution variables
		for (String key : namesToSubstitutionVars.keySet()) {
			SubstitutionVar var = namesToSubstitutionVars.get(key);
			int absStart = Utils.getUriTemplateNode(nestedFunction).getOffset() + 1;
			FunctionParameter parm = (FunctionParameter) parmNamesToNodes.get(key);
			if (parm == null) {
				// substitution variable must match a parameter name
				problemRequestor.acceptProblem(absStart + var.getStartOffset(),
						absStart + var.getEndOffset(), IMarker.SEVERITY_ERROR,
						ResourceKeys.XXXREST_UMATCHED_SUBS_VAR, new String[] {
								var.getVarName(), function.getCaseSensitiveName() },
						ResourceKeys.getResourceBundleForKeys());

			} else {

			}
		}
	}

	private boolean isResourceType(org.eclipse.edt.mof.egl.Type type, Annotation restAnnotation) {
		if (type == null) {
			return false;
		}

		// If responseFormat is JSON, allow single dimension array of strings or
		// flexible records
		EnumerationEntry responseFormat = (EnumerationEntry) restAnnotation.getValue(IEGLConstants.PROPERTY_RESPONSEFORMAT);
		if (responseFormat != null && NameUtile.equals(NameUtile.getAsName("json"), responseFormat.getName()) && 
				type instanceof ArrayType) {
			type = ((ArrayType) type).getElementType();
		}

		if (type.equals(TypeUtils.Type_STRING)) {
			return true;
		}

		return type instanceof Record;
	}

	private boolean isFlatRecord(Type type) {
		if (type == null) {
			return false;
		}

		if (isSupportedContainer(type)) {
			for (Member member : ((Container) type).getMembers()) {
				if (member.getType() instanceof Record) {
					return false;
				}
			}
			return true;
		}
		if (type instanceof Handler) {
			for (Member member : ((Handler)type).getMembers()) {
				if (isSupportedContainer(member.getType())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isSupportedContainer(Type type){
		return type instanceof Record ||
				type instanceof Handler;
	}


	private HashMap<String, SubstitutionVar> parseSubtitutionVars( Annotation restAnnotation) {
		HashMap<String, SubstitutionVar> namesToSubstitutionVars = new HashMap<String, SubstitutionVar>();
		String uriTemp = (String) restAnnotation.getValue(IEGLConstants.PROPERTY_URITEMPLATE);
		char[] chars = uriTemp != null ? uriTemp.toCharArray() : new char[0];

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
					SubstitutionVar var = new SubstitutionVar(lOffset + 1, i, uriTemp);
					namesToSubstitutionVars.put(NameUtile.getAsName(var.getVarName()), var);
					lookingForL = true;
				}
			}
		}
		return namesToSubstitutionVars;
	}

	private boolean supportsResourceParm(Annotation restAnnotation) {
		EnumerationEntry method = (EnumerationEntry) restAnnotation.getValue("method");
		return !(method != null && "_get".equalsIgnoreCase(method.getName()));
	}

	@Override
	protected String getName() {
		return "REST";
	}

}

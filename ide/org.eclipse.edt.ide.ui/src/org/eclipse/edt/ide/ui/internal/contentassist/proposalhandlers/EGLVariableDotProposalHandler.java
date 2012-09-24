/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLVariableDotProposalHandler extends EGLAbstractProposalHandler {
	private Type qualifierType;
	private Expression qualifierExpression;
	private boolean isVariable;   //tells if this is myVar.<ca>, as opposed to myType.<ca>

	public EGLVariableDotProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		Type qualifierType,
		boolean isVariable,
		Expression qualifierExpression) {
			
			super(viewer, documentOffset, prefix, editor);
			this.qualifierType = qualifierType;
			this.qualifierExpression = qualifierExpression;
			this.isVariable = isVariable;
	}

	public List getProposals(List propertyBlockList) {
		return getProposals(true, propertyBlockList);
	}

	public List getProposals(boolean includeFunctions, List propertyBlockList) {
		return getProposals(includeFunctions, false, false, propertyBlockList);
	}
	
	public List getProposals(boolean includeFunctions, boolean addEquals, List propertyBlockList) {
		return getProposals(includeFunctions, addEquals, false, propertyBlockList);
	}
	
	public List getProposals(boolean includeFunctions, boolean addEquals, boolean includePrivateFields, List propertyBlockList) {
		List result = new ArrayList();
		if(qualifierType != null) {
			
			if (isVariable) {
				result.addAll(getFieldProposals(BindingUtil.getAllFields(qualifierType), addEquals, includePrivateFields, propertyBlockList));
				if(includeFunctions) {
					result.addAll(getFunctionProposals(
						BindingUtil.getAllFunctions(qualifierType),
						null,
						EGLCompletionProposal.RELEVANCE_MEDIUM));
				}
				return result;
			}
			else {
				result.addAll(getFieldProposals(BindingUtil.getAllFields(qualifierType), addEquals, includePrivateFields, propertyBlockList));
				if(includeFunctions) {
					result.addAll(getFunctionProposals(
						BindingUtil.getAllFunctions(qualifierType),
						null,
						EGLCompletionProposal.RELEVANCE_MEDIUM));
				}
				return result;
			}
			
			
		}
		if(Binding.isValidBinding(qualifierTypeBinding)) {
			switch(qualifierTypeBinding.getKind()) {
				case ITypeBinding.ENUMERATION_BINDING:
					return getFieldProposals(((EnumerationTypeBinding) qualifierTypeBinding).getEnumerations(), addEquals, includePrivateFields, propertyBlockList);					
			
				case ITypeBinding.EXTERNALTYPE_BINDING:
					result.addAll(getFieldProposals(filterStaticDataBindings(((ExternalTypeBinding) qualifierTypeBinding).getDeclaredAndInheritedData()), addEquals, includePrivateFields, propertyBlockList));
					if(includeFunctions) {
						result.addAll(getFunctionProposals(
							filterStaticDataBindings(((ExternalTypeBinding) qualifierTypeBinding).getDeclaredAndInheritedFunctions()),
							null,
							EGLCompletionProposal.RELEVANCE_MEDIUM));
					}
					return result;
				
				case ITypeBinding.FLEXIBLE_RECORD_BINDING:
					result.addAll(getFieldProposals(((FlexibleRecordBinding) qualifierTypeBinding).getDeclaredFields(true), addEquals, includePrivateFields, propertyBlockList, PluginImages.IMG_OBJS_STRUCTUREITEM));
					return result;

				case ITypeBinding.LIBRARY_BINDING:
					result.addAll(getFieldProposals(((LibraryBinding) qualifierTypeBinding).getDeclaredData(true), addEquals, includePrivateFields, propertyBlockList));
					if(includeFunctions) {
						result.addAll(getFunctionProposals(
							((LibraryBinding) qualifierTypeBinding).getDeclaredFunctions(true),
							UINlsStrings.bind(UINlsStrings.CAProposal_LibraryFunction, qualifierTypeBinding.getCaseSensitiveName()),
							EGLCompletionProposal.RELEVANCE_LIBRARY_FUNCTION));
					}
					return result;

				case ITypeBinding.SERVICE_BINDING:
					if(qualifierExpression instanceof ThisExpression) {
						result.addAll(getFieldProposals(((ServiceBinding) qualifierTypeBinding).getDeclaredData(true), addEquals, includePrivateFields, propertyBlockList));
					}
					if(includeFunctions) {
						result.addAll(getFunctionProposals(((ServiceBinding) qualifierTypeBinding).getDeclaredFunctions(true),
								UINlsStrings.bind(UINlsStrings.CAProposal_ServiceFunction, qualifierTypeBinding.getCaseSensitiveName()),
								EGLCompletionProposal.RELEVANCE_LIBRARY_FUNCTION));
					}
					return result;

				case ITypeBinding.INTERFACE_BINDING:
					if(includeFunctions) {
						result.addAll(getFunctionProposals(((InterfaceBinding) qualifierTypeBinding).getDeclaredAndInheritedFunctions(),
								UINlsStrings.bind(UINlsStrings.CAProposal_InterfaceFunction, qualifierTypeBinding.getCaseSensitiveName()),
								EGLCompletionProposal.RELEVANCE_LIBRARY_FUNCTION));
					}
					return result;
				
				case ITypeBinding.HANDLER_BINDING:
					result.addAll(getFieldProposals(((HandlerBinding) qualifierTypeBinding).getDeclaredData(true), addEquals, includePrivateFields, propertyBlockList));
					if(includeFunctions) {
						result.addAll(getFunctionProposals(((HandlerBinding) qualifierTypeBinding).getDeclaredFunctions(true),
								UINlsStrings.bind(UINlsStrings.CAProposal_HandlerFunction, qualifierTypeBinding.getCaseSensitiveName()),
								EGLCompletionProposal.RELEVANCE_LIBRARY_FUNCTION));
					}
					return result;
					
				case ITypeBinding.ARRAY_TYPE_BINDING:
					if(includeFunctions) {
						result.addAll(getSystemWordProposals(ArrayTypeBinding.getARRAY_FUNCTIONS(), UINlsStrings.CAProposal_ArrayFunctionSystemWord));
					}
					return result;	
					
				case ITypeBinding.PRIMITIVE_TYPE_BINDING:
					if(includeFunctions){
						result.addAll(getPrimitiveSystemFunctionProposals(((PrimitiveTypeBinding)qualifierTypeBinding)));
					}
					return result;	
			}
		}
		return Collections.EMPTY_LIST;
	}
	
	private List getPrimitiveSystemFunctionProposals(PrimitiveTypeBinding primitiveBinding){
		Primitive prim = primitiveBinding.getPrimitive();
		String primType = prim.getName();
		
		if(primType.equalsIgnoreCase(IEGLConstants.STRING_STRING)){
			return(getSystemWordProposals(PrimitiveTypeBinding.getStringFunctions(), UINlsStrings.CAProposal_StringLibrary));
		}else if(primType.equalsIgnoreCase(IEGLConstants.TIMESTAMP_STRING)){
			return(getSystemWordProposals(PrimitiveTypeBinding.getTimestampFunctions(), UINlsStrings.CAProposal_TimeStampLibrary));
		}else if(primType.equalsIgnoreCase(IEGLConstants.DATE_STRING)){
			return(getSystemWordProposals(PrimitiveTypeBinding.getDateFunctions(), UINlsStrings.CAProposal_DateLibrary));
		}else if(primType.equalsIgnoreCase(IEGLConstants.TIME_STRING)){
			return(getSystemWordProposals(PrimitiveTypeBinding.getTimeFunctions(), UINlsStrings.CAProposal_TimeLibrary));
		}
		
		return Collections.EMPTY_LIST;
	}

	private List filterStaticDataBindings(List dataBindings) {
		List filteredList = new ArrayList();
		for(Iterator iter = dataBindings.iterator(); iter.hasNext();) {
			IDataBinding next = (IDataBinding) iter.next();
			switch(next.getKind()) {
				case IDataBinding.CLASS_FIELD_BINDING :
					if(((ClassFieldBinding) next).isStatic()) {
						filteredList.add(next);
					}
					break;
				case IDataBinding.NESTED_FUNCTION_BINDING :
					if(((IFunctionBinding) next.getType()).isStatic()) {
						filteredList.add(next);
					}
					break;				
			}
		}
		return filteredList;
	}

	private List getFieldProposals(List<Field> fields, boolean addEquals, boolean includePrivateFields, List propertyBlockList, String ImgKeyStr) {
		List result = new ArrayList();
		String fieldImgKeyStr = ImgKeyStr == "" ? PluginImages.IMG_OBJS_ENV_VAR : ImgKeyStr;

		for(Iterator iter = fieldDataBindings.iterator(); iter.hasNext();) {
			IDataBinding dataBinding = (IDataBinding) iter.next();
			if(!includePrivateFields && dataBinding instanceof VariableBinding && ((VariableBinding) dataBinding).isPrivate() && !(qualifierExpression instanceof ThisExpression)) {
				continue;
			}
			if(AmbiguousDataBinding.getInstance() != dataBinding) {
				String proposalString = dataBinding.getCaseSensitiveName();
				if (proposalString.toUpperCase().startsWith(getPrefix().toUpperCase())) {
					if(!"*".equals(dataBinding.getName())) { //$NON-NLS-1$
						String displayString = proposalString + " : " + dataBinding.getType().getCaseSensitiveName() +  " - " + dataBinding.getDeclaringPart().getName();	//$NON-NLS-1$;
						if (!containsProperty(proposalString, propertyBlockList)) {
							if (addEquals) {
								if (needSetFunctionForTheField(dataBinding)) {
									proposalString = proposalString + " ::= "; //$NON-NLS-1$
								} else {
									proposalString = proposalString + " = "; //$NON-NLS-1$
								}
							}
							result.add(new EGLCompletionProposal(viewer,
													displayString,
													proposalString,
													getAdditionalInfo(dataBinding),
													getDocumentOffset() - getPrefix().length(),
													getPrefix().length(),
													proposalString.length(),
													EGLCompletionProposal.RELEVANCE_MEDIUM-1,
													fieldImgKeyStr));
						}
					}
				}
			}
		}
		return result;
	}
	
	private List getFieldProposals(List<Field> fields, boolean addEquals, boolean includePrivateFields, List propertyBlockList) {
		return this.getFieldProposals(fields, addEquals, includePrivateFields, propertyBlockList, "");
	}
	
	private List getFunctionProposals(List<Function> functions, String additionalInformation, int relevance) {
		List result = new ArrayList();
		for(Iterator iter = functionBindings.iterator(); iter.hasNext();) {
			IDataBinding nestedFunctionBinding = (IDataBinding) iter.next();
			IFunctionBinding functionBinding = (IFunctionBinding) nestedFunctionBinding.getType();
			//Adding this check on 10/04/2006 for RATLC01129262.  From what I can tell private functions should never
			//be returned.  If I am wrong, need to parameterize a boolean to determine whether or not to return private functions.
			if (!functionBinding.isPrivate()) {
				if (nestedFunctionBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					result.addAll(createFunctionInvocationProposals(functionBinding, additionalInformation, relevance, false));
				}
			}
		}
		return result;
	}

	public static boolean needSetFunctionForTheField(Field field){
		for (Annotation ann : field.getAnnotations()) {
			if ((ann.getEClass() != null && (ann.getEClass().getName().equalsIgnoreCase("Property") || 
					ann.getEClass().getName().equalsIgnoreCase("EGLProperty")))) {
				Object value = ann.getValue(org.eclipse.edt.compiler.core.IEGLConstants.PROPERTY_SETMETHOD);
				if(value != null && value.equals("")){
					return true;
				}
			}
		}
		
		return(false);
	}

}

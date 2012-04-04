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

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.OverloadedFunctionSet;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ServiceBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.SystemFunctionBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLVariableDotProposalHandler extends EGLAbstractProposalHandler {
	private IDataBinding qualifierDataBinding;
	private ITypeBinding qualifierTypeBinding;
	private Expression qualifierExpression;

	public EGLVariableDotProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		IDataBinding qualifierDataBinding,
		ITypeBinding qualifierTypeBinding,
		Expression qualifierExpression) {
			
			super(viewer, documentOffset, prefix, editor);
			this.qualifierDataBinding = qualifierDataBinding;
			this.qualifierTypeBinding = qualifierTypeBinding;
			this.qualifierExpression = qualifierExpression;
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
		if(qualifierDataBinding != null && IBinding.NOT_FOUND_BINDING != qualifierDataBinding) {
			switch(qualifierDataBinding.getKind()) {
				case IDataBinding.STRUCTURE_ITEM_BINDING:
					result.addAll(getFieldProposals(((StructureItemBinding) qualifierDataBinding).getChildren(), addEquals, includePrivateFields, propertyBlockList));
					return result;
			}
			
			ITypeBinding dataBindingType = qualifierDataBinding.getType();
			if(dataBindingType != null && IBinding.NOT_FOUND_BINDING != dataBindingType) {
				if(Binding.isValidBinding(qualifierTypeBinding) &&
				   ITypeBinding.EXTERNALTYPE_BINDING == qualifierTypeBinding.getKind() &&
				   ITypeBinding.EXTERNALTYPE_BINDING == dataBindingType.getBaseType().getKind() &&
				   IDataBinding.EXTERNALTYPE_BINDING != qualifierDataBinding.getKind()) {
					result.addAll(getFieldProposals(((ExternalTypeBinding) dataBindingType.getBaseType()).getDeclaredAndInheritedData(), addEquals, includePrivateFields, propertyBlockList));
					if(includeFunctions) {
						result.addAll(getFunctionProposals(
							((ExternalTypeBinding) dataBindingType.getBaseType()).getDeclaredAndInheritedFunctions(),
							null,
							EGLCompletionProposal.RELEVANCE_MEDIUM));
					}
					return result;
				}
				
				switch(dataBindingType.getKind()) {
					case ITypeBinding.ARRAYDICTIONARY_BINDING:
						return result;
						
					case ITypeBinding.DICTIONARY_BINDING:
						result.addAll(getSystemWordProposals(DictionaryBinding.getSYSTEM_FUNCTIONS(), UINlsStrings.CAProposal_FunctionSystemWord));
						if(AbstractBinder.dataBindingIs(qualifierDataBinding, EGLLANG, IEGLConstants.KEYWORD_SYSLIB, IEGLConstants.SYSTEM_WORD_CURRENTEXCEPTION)) {
						    Set dBindings = new HashSet();
						    dBindings.addAll(((FlexibleRecordBinding) SystemPartManager.ANYEXCEPTION_BINDING).getDeclaredFields());
						    result.addAll(getFieldProposals((Collection) dBindings, addEquals, includePrivateFields, propertyBlockList));
						}
						return result;
				}
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

				case ITypeBinding.FIXED_RECORD_BINDING:
					Set dBindings = new HashSet();
					dBindings.addAll(((FixedRecordBinding) qualifierTypeBinding).getSimpleNamesToDataBindingsMap().values());
					dBindings.addAll(((FixedRecordBinding) qualifierTypeBinding).getStructureItems());
					result.addAll(getFieldProposals(dBindings, addEquals, includePrivateFields, propertyBlockList, PluginImages.IMG_OBJS_STRUCTUREITEM));
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
		}
		
		return null;
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

	private List getFieldProposals(Collection fieldDataBindings, boolean addEquals, boolean includePrivateFields, List propertyBlockList, String ImgKeyStr) {
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
	
	private List getFieldProposals(Collection fieldDataBindings, boolean addEquals, boolean includePrivateFields, List propertyBlockList) {
		return this.getFieldProposals(fieldDataBindings, addEquals, includePrivateFields, propertyBlockList, "");
	}
	
	private List getFunctionProposals(List functionBindings, String additionalInformation, int relevance) {
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

	private List getSystemWordProposals(Map systemWords, String additionalInfo) {
		List proposals = new ArrayList();
		for (Iterator iter = systemWords.values().iterator(); iter.hasNext();) {
			IBinding functionBinding = (IBinding)iter.next();
			if(functionBinding instanceof NestedFunctionBinding){
				NestedFunctionBinding systemFunctionBinding = (NestedFunctionBinding) functionBinding;
				getNestedFunctionProposal(systemFunctionBinding, additionalInfo, proposals);
			}else if(functionBinding instanceof OverloadedFunctionSet){
				for (Iterator iterator = ((OverloadedFunctionSet) (functionBinding)).getNestedFunctionBindings().iterator(); iterator.hasNext();) {
					getNestedFunctionProposal((NestedFunctionBinding)iterator.next(), additionalInfo, proposals);
				}
			}else if(functionBinding instanceof SystemFunctionBinding){
				SystemFunctionBinding systemFunctionBinding = (SystemFunctionBinding) functionBinding;
				if (systemFunctionBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					String displayString = systemFunctionBinding.getCaseSensitiveName();
					String parameterStr = getParmString(systemFunctionBinding);
					String proposalString = displayString + "(" + parameterStr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					int cusorPos = parameterStr.length() == 0 ? displayString.length() + 2 : displayString.length() + 1;
					proposals.add(
							new EGLCompletionProposal(viewer,
								displayString,
								proposalString,
								additionalInfo,
								getDocumentOffset() - getPrefix().length(),
								getPrefix().length(),
								cusorPos,
								EGLCompletionProposal.RELEVANCE_SYSTEM_WORD,
								getFirstParmLength(systemFunctionBinding),
								PluginImages.IMG_OBJS_FUNCTION));
				}
			}
		}
		
		return proposals;
	}
	
	private void getNestedFunctionProposal(NestedFunctionBinding nestedFunctionBinding, String additionalInfo, List proposals){
		if (nestedFunctionBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
			String displayString = nestedFunctionBinding.getCaseSensitiveName();
			String parameterStr = getParmString((IFunctionBinding) nestedFunctionBinding.getType());
			String proposalString = displayString + "(" + parameterStr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			int cusorPos = parameterStr.length() == 0 ? displayString.length() + 2 : displayString.length() + 1;
			proposals.add(new EGLCompletionProposal(viewer, 
												displayString,
												proposalString, 
												additionalInfo, 
												getDocumentOffset() - getPrefix().length(), 
												getPrefix().length(),
												cusorPos, 
												EGLCompletionProposal.RELEVANCE_SYSTEM_WORD,
												getFirstParmLength((IFunctionBinding) nestedFunctionBinding.getType()),
												PluginImages.IMG_OBJS_FUNCTION));
		}
	}

	public static boolean needSetFunctionForTheField(IDataBinding dataBinding){
		List<IAnnotationBinding> annArr = dataBinding.getAnnotations();
		for (Iterator iterator = annArr.iterator(); iterator.hasNext();) {
			IAnnotationBinding iAnnotationBinding = (IAnnotationBinding) iterator.next();
			if ((iAnnotationBinding.getName().equalsIgnoreCase("Property") || 
					iAnnotationBinding.getName().equalsIgnoreCase("EGLProperty"))) {
				IDataBinding aDataBinding = iAnnotationBinding.findData(org.eclipse.edt.compiler.core.IEGLConstants.PROPERTY_SETMETHOD);
				AnnotationFieldBinding field = aDataBinding instanceof AnnotationFieldBinding ? (AnnotationFieldBinding)aDataBinding : null;
				if(null != field && null != field.getValue() && field.getValue().equals("")){
					return true;
				}
			}
		}
		
		return(false);
	}

}

/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
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
import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ForeignLanguageTypeBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.SystemFunctionParameterSpecialTypeBinding;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLJavaLibArgumentProposalHandler extends EGLAbstractProposalHandler {

	private Node boundNode;

	public EGLJavaLibArgumentProposalHandler(ITextViewer viewer, int documentOffset, String prefix, Node boundNode) {
		super(viewer, documentOffset, prefix);
		this.boundNode = boundNode;
	}

	public List getProposals() {
		ITypeBinding parameterType = getMatchingParameterType(boundNode);
		if(parameterType != null) {
			if(SystemFunctionParameterSpecialTypeBinding.OBJIDTYPE == parameterType ||
			   SystemFunctionParameterSpecialTypeBinding.OBJIDTYPEOPT == parameterType) {
				return createObjIdProposals();
			}
			else if(SystemFunctionParameterSpecialTypeBinding.ANYEGLORASJAVA == parameterType) {
				return createJavaCastProposals();
			}
					
		}
		return Collections.EMPTY_LIST;
	}
	
	private List createObjIdProposals() {
		return createProposals(new String[] {ForeignLanguageTypeBinding.OBJIDJAVA.getCaseSensitiveName()});
	}
	
	private List createJavaCastProposals() {
		Node node = boundNode;
		while(!(node instanceof AsExpression)) {
			node = node.getParent();
		}
		ITypeBinding tBinding = ((AsExpression) node).getExpression().resolveTypeBinding();
		if(Binding.isValidBinding(tBinding)) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
				Primitive prim = ((PrimitiveTypeBinding) tBinding).getPrimitive();
				if(Primitive.BOOLEAN == prim) {
					return createProposals(new String[] {ForeignLanguageTypeBinding.JAVABOOLEAN.getCaseSensitiveName()});
				}
				else if(Primitive.isStringType(prim)) {
					return createProposals(new String[] {ForeignLanguageTypeBinding.JAVACHAR.getCaseSensitiveName()});
				}
				else if(Primitive.isNumericType(prim)) {
					return createProposals(new String[] {
						ForeignLanguageTypeBinding.JAVASHORT.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVAINT.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVALONG.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVAFLOAT.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVADOUBLE.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVABOOLEAN.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVABIGINTEGER.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVABIGDECIMAL.getCaseSensitiveName(),
						ForeignLanguageTypeBinding.JAVABYTE.getCaseSensitiveName()
					});
				}
			}
		}
		return new ArrayList();
	}

	private ITypeBinding getMatchingParameterType(Node boundNode) {
		Node lastNode = boundNode;
		Node parentNode = boundNode.getParent();
		while(!(parentNode instanceof FunctionInvocation)) {
			lastNode = parentNode;
			parentNode = lastNode.getParent();
		}
		FunctionInvocation functionInvocation = (FunctionInvocation) parentNode;
		IFunctionBinding fBinding = (IFunctionBinding) functionInvocation.getTarget().resolveDataBinding().getType();
		int argNum = functionInvocation.getArguments().indexOf(lastNode);
		List parameters = fBinding.getParameters();
		if(argNum < parameters.size()) {
			return ((FunctionParameterBinding) parameters.get(argNum)).getType();
		}
		return null;
	}
	
	private List createProposals(String[] strings) {
		List result = new ArrayList();
		for(int i = 0; i < strings.length; i++) {
			if(strings[i].toUpperCase().startsWith(getPrefix().toUpperCase())) {
				result.add(createProposal(strings[i]));
			}
		}
		return result;
	}
	
	private EGLCompletionProposal createProposal(String caseSensitiveName) {
		String proposalString = needQuotes() ? "\"" + caseSensitiveName + "\"" : caseSensitiveName;
		return
			new EGLCompletionProposal(viewer,
				caseSensitiveName,
				proposalString,
				null,
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				proposalString.length(),
				EGLCompletionProposal.RELEVANCE_PRIMITIVE+1,
				EGLCompletionProposal.NO_IMG_KEY);
	}

	private boolean needQuotes() {
		return !(boundNode instanceof StringLiteral) || getDocumentOffset() <= boundNode.getOffset();
	}
}

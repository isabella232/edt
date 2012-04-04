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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.EGLPropertiesHandler;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.ParseNode;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPropertyValueProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLPropertyListValueReferenceCompletion extends EGLAbstractReferenceCompletion {
	public static final int LOCATION_PROPERTY_LIST_NONE = 0;
	public static final int LOCATION_PROPERTY_LIST_OUTLINE = 1;

	private String prefix;
	private ITextViewer viewer;
	private int documentOffset;
	private List structureItems;
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a { name = ["); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		this.prefix = prefix.toUpperCase();
		this.viewer = viewer;
		this.documentOffset = documentOffset;
		final List proposals = new ArrayList();
		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {
			public void processBoundNode(Node node) {
				structureItems = getStructureItems(node);
				
				EGLPropertyRule propertyRule = getPropertyListPropertyRule(parseStack);
				if (propertyRule != null) {
					if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_KEYITEMS)) {
						proposals.addAll(getListValueKeyItemsProposals(viewer, documentOffset, prefix, false));
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_OUTLINE)) {
						proposals.addAll( getListValueOutlineProposals(viewer, documentOffset, prefix, false, parseStack, propertyRule));
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_PAGESIZE)) {
						proposals.addAll( getListValueNumericLiteralProposal());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_POSITION)) {
						proposals.addAll( getListValueNumericLiteralProposal());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDVALUES)) {
						proposals.addAll( getListValueNumericLiteralProposal());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZE)) {
						proposals.addAll( getListValueNumericLiteralProposal());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZES)) {
						proposals.addAll( handleScreenSizes(parseStack));
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_FORMSIZE)) {
						proposals.addAll( getListValueNumericLiteralProposal());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_TABLENAMES)) {
						proposals.addAll( getListValueTableNamesLiteralProposals());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_TABLENAMEVARIABLES)) {
						proposals.addAll( getListValueTableNameVariablesLiteralProposals());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS)) {
						proposals.addAll( getListValueValidationBypassKeysProposals(viewer, documentOffset, prefix, false));
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS)) {
						proposals.addAll( getListValueValidationBypassFunctionsProposals(viewer, documentOffset, prefix, false));
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_SEGMENTS)) {
						proposals.addAll( getListValueSegmentsProposal());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_LINKPARMS)) {
						proposals.addAll( getListValueLinkParameterProposal());
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_PCBPARMS)) {
						proposals.addAll( getListValuePCBParmsProposal(node));
					}
					else if (propertyRule.getName().equalsIgnoreCase(IEGLConstants.PROPERTY_HIERARCHY)) {
						proposals.addAll( getListValueHierarchyProposal());
					}
				}
			}
		});
		return proposals;
	}

	/**
	 * 
	 */
	private List getListValueLinkParameterProposal() {
		String proposalString = "@" + IEGLConstants.PROPERTY_LINKPARAMETER + " {}";  //$NON-NLS-1$ //$NON-NLS-2$ 	$NON-NLS-2$
		return createProposal(
			viewer,
			IEGLConstants.PROPERTY_LINKPARAMETER,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			documentOffset,
			proposalString.length()-1,
			0);
	}

	/**
	 * 
	 */
	private List getListValueHierarchyProposal() {
		String proposalString = "@" + IEGLConstants.PROPERTY_RELATIONSHIP + " {}";  //$NON-NLS-1$ //$NON-NLS-2$ 	$NON-NLS-2$
		return createProposal(
			viewer,
			IEGLConstants.PROPERTY_RELATIONSHIP,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			documentOffset,
			proposalString.length()-1,
			0);
	}

	private List getListValuePCBParmsProposal(Node boundNode) {
		final List proposals = new ArrayList();
		if(boundNode != null) {
			 proposals.addAll(new EGLDeclarationProposalHandler(
				viewer,
				documentOffset,
				prefix,
				boundNode).getProgramParameterRecordProposals(EGLDeclarationProposalHandler.BASIC_RECORD, false, false));
		}
		return proposals;
	}

	protected List handleScreenSizes(ParseStack parseStack) {
		return getListValueNumericLiteralProposal();
	}

	private EGLPropertyRule getPropertyListPropertyRule(ParseStack parseStack) {
		String propertyName = getPropertyName(parseStack);
		if (propertyName.length() > 0) {
			return getRule(propertyName);
		}
		return null;
	}

	private String getPropertyName(ParseStack parseStack) {
		ParseStack tempStack = parseStack.copy();
		ParseNode parseNode;
		//skip over top node which represents the comma
		tempStack.deleteContext(1);
		int stackSize = tempStack.getStack().size();
		while (stackSize > 0) {
			parseNode = tempStack.deleteContext(1)[0];
			if (parseNode.getText().startsWith("=")) { //$NON-NLS-1$
				parseNode = tempStack.deleteContext(1)[0];
				return parseNode.getText().trim();
			}
			stackSize--;
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @param propertyName
	 * @return
	 */
	private EGLPropertyRule getRule(String propertyName) {
		if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_KEYITEMS))
			return getRule(EGLPropertiesHandler.getSQLRecordPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_OUTLINE))
			return getRule(EGLPropertiesHandler.getDataItemPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_PAGESIZE))
			return getRule(EGLPropertiesHandler.getPrintFloatingAreaPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_POSITION))
			return getRule(EGLPropertiesHandler.getTextFormPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDVALUES))
			return getRule(EGLPropertiesHandler.getDataItemPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZE))
			return getRule(EGLPropertiesHandler.getScreenFloatingAreaPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SCREENSIZES))
			return getRule(EGLPropertiesHandler.getTextFormPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_FORMSIZE))
			return getRule(EGLPropertiesHandler.getTextFormPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_TABLENAMES))
			return getRule(EGLPropertiesHandler.getSQLRecordPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_TABLENAMEVARIABLES))
			return getRule(EGLPropertiesHandler.getSQLRecordPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS))
			return getRule(EGLPropertiesHandler.getPageHandlerPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS))
			return getRule(EGLPropertiesHandler.getTextFormPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SEGMENTS))
			return getRule(EGLPropertiesHandler.getConsoleFieldPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_LINKPARMS))
			return getRule(EGLPropertiesHandler.getLinkParmsPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_PCBPARMS))
			return getRule(EGLPropertiesHandler.getPcbParmsPropertyRules(), propertyName);
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_HIERARCHY))
			return getRule(EGLPropertiesHandler.getHierarchyPropertyRules(), propertyName);

		return null;
	}

	/**
	 * @param propertyName
	 * @return
	 */
	private EGLPropertyRule getRule(List rules, String propertyName) {
		for (Iterator iter = rules.iterator(); iter.hasNext();) {
			EGLPropertyRule propertyRule = (EGLPropertyRule) iter.next();
			if (propertyRule.getName().equalsIgnoreCase(propertyName))
				return propertyRule;
		}
		return null;
	}

	/**
	 * @return proposal list
	 */
	private List getListValueNumericLiteralProposal() {
		return createProposal(
			viewer,
			EGLPropertyValueProposalHandler.NUMERIC_LITERAL,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			documentOffset,
			0,
			EGLPropertyValueProposalHandler.NUMERIC_LITERAL.length());
	}

	/**
	 * @return proposal list
	 */
	protected List getListValueScreenSizesLiteralProposal() {
		String proposalString = EGLPropertyValueProposalHandler.SIZE_LIST_PROPOSAL;
		return createProposal(
			viewer,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			documentOffset,
			1,
			5);
	}

	/**
	 * @return proposal list
	 */
	protected List getListValueSegmentsProposal() {
		return createProposal(
				viewer,
				EGLPropertyValueProposalHandler.SEGMENTS_LIST_PROPOSAL,
				prefix,
				UINlsStrings.CAProposal_PropertyValue,
				documentOffset,
				1,
				4);
	}

	/**
	 * @return proposal list
	 */
	private List getListValueTableNamesLiteralProposals() {
		return createProposal(
			viewer,
			EGLPropertyValueProposalHandler.TABLENAMES_LIST_PROPOSAL,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			documentOffset,
			2,
			9);
	}

	/**
	 * @return proposal list
	 */
	private List getListValueTableNameVariablesLiteralProposals() {
		return createProposal(
				viewer,
				EGLPropertyValueProposalHandler.TABLENAMEVARIABLES_LIST_PROPOSAL,
				prefix,
				UINlsStrings.CAProposal_PropertyValue,
				documentOffset,
				2,
				12);
	}

	public List getStructureItems() {
		return structureItems;
	}

}

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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.EGLPropertiesHandler;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion;
import org.eclipse.edt.ide.ui.internal.editor.CodeConstants;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLPropertyValueProposalHandler extends EGLAbstractProposalHandler {

	public static final String NUMERIC_LITERAL = "numericLiteral"; //$NON-NLS-1$
	public static final String ALIAS_PROPOSAL = "runtimeName"; //$NON-NLS-1$
	public static final String SEGMENTS_LIST_PROPOSAL = "[line,column,length]"; //$NON-NLS-1$
	public static final String TCPIPLOCATION_LIST_PROPOSAL = "host:port"; //$NON-NLS-1$

	public static final String ROW_CONTENTS = "rowContents"; //$NON-NLS-1$
	public static final String SQL_CONDITION = "condition"; //$NON-NLS-1$

	private String prefix=""; //$NON-NLS-1$

	boolean hasQuoteBegin;
	boolean hasQuoteEnd;
	private EGLAbstractReferenceCompletion referenceCompletion;
	private ParseStack parseStack;
	private Node boundNode;
	private org.eclipse.edt.mof.egl.Part part;
	
	/**
	 * @param viewer
	 * @param getDocumentOffset()
	 * @param prefix
	 * @param editor
	 */
	public EGLPropertyValueProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		EGLAbstractReferenceCompletion referenceCompletion,
		ParseStack parseStack,
		Node boundNode) {
		super(viewer, documentOffset, prefix, editor);
		this.prefix = prefix;
		this.referenceCompletion = referenceCompletion;
		this.parseStack = parseStack;
		this.boundNode = boundNode;
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof Part) {
				part = (org.eclipse.edt.mof.egl.Part) ((Part) boundNode).getName().resolveType();
			}
			boundNode = boundNode.getParent();
		}
	}
	
	public List getProposals(EGLPropertyRule propertyRule) {
		List proposals = new ArrayList();

		prepareGetProposals();
		proposals.addAll(handleProperty(propertyRule.getName(), viewer, getDocumentOffset(), propertyRule));

		return proposals;
	}

	private void prepareGetProposals() {
		try {
			String characterBeforeCursor = viewer.getDocument().get(getDocumentOffset()-1, 1);
			hasQuoteBegin = characterBeforeCursor.equals("\""); //$NON-NLS-1$
			String characterAfterCursor = viewer.getDocument().get(getDocumentOffset(), 1);
			hasQuoteEnd = characterAfterCursor.equals("\""); //$NON-NLS-1$
		} catch (BadLocationException e) {
			//This should only happen for characterAfterCursor above
			hasQuoteEnd = false;
		}
	}	

	public List getProposals(Annotation ann, String fieldName) {
		List proposals = new ArrayList();

		prepareGetProposals();
		
		if (fieldName == null) {
			proposals.addAll(handleProperty(ann.getEClass().getName(), viewer, getDocumentOffset(), new EGLPropertyRule((AnnotationType)ann.getEClass())));
		}
		else {
			proposals.addAll(handleProperty(fieldName, viewer, getDocumentOffset(), new EGLPropertyRule((AnnotationType)ann.getEClass(), fieldName)));
		}

		return proposals;
	}

	public List getProposals(Field field) {
		List proposals = new ArrayList();

		prepareGetProposals();
		proposals.addAll(handleProperty(field.getCaseSensitiveName(), viewer, getDocumentOffset(), new EGLPropertyRule(field)));
		return proposals;
	}

	/**
	 * @param parseStack
	 * @param viewer
	 * @param getDocumentOffset()
	 * @param location
	 */
	private List handleProperty(String propertyName, ITextViewer viewer, int documentOffset, EGLPropertyRule rule) {
		List proposals = new ArrayList();
		
		if (propertyName.equalsIgnoreCase(rule.getName())) {
			if (rule.hasType(EGLPropertiesHandler.integerValue))
				proposals.addAll(getIntegerValueProposals());			
			if (rule.hasType(EGLPropertiesHandler.arrayOf))
				proposals.addAll(getArrayOfProposals(propertyName));
			if (rule.hasType(EGLPropertiesHandler.arrayOfArrays))
				proposals.addAll(getArrayOfArraysProposals(propertyName));
			if (rule.hasType(EGLPropertiesHandler.literalValue))
				proposals.addAll(getLiteralValueProposals());
			if (rule.hasType(EGLPropertiesHandler.nameValue))
				proposals.addAll(getNameValueProposals(viewer, getDocumentOffset(), propertyName));
			if (rule.hasType(EGLPropertiesHandler.quotedValue))
				proposals.addAll(getQuotedValueProposal(propertyName));
			if (rule.hasType(EGLPropertiesHandler.specificValue))
				proposals.addAll(getSpecificValueProposals(rule));
			if (rule.hasType(EGLPropertiesHandler.sqlValue))
				proposals.addAll(getSqlValueProposal(propertyName));
		}

		return proposals;
	}

	/**
	 * 
	 */
	private List getIntegerValueProposals() {
		return referenceCompletion.createProposal(
			viewer,
			NUMERIC_LITERAL,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			getDocumentOffset(),
			0);
	}

	private List getListValueProposal(String value, int selectionLength) {
		return getListValueProposal(value, 0, selectionLength);
	}

	private List getListValueProposal(String value, int offset, int selectionLength) {
		String openBracket = "["; //$NON-NLS-1$
		String closeBracket = "]"; //$NON-NLS-1$
		String proposalString = openBracket + value + closeBracket;
		return referenceCompletion.createProposal(
			viewer,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			getDocumentOffset(),
			openBracket.length() + offset,
			selectionLength);
	}

	/**
	 * 
	 */
	private List getLiteralArrayValueProposals(String propertyName) {
		String value = ""; //$NON-NLS-1$
		if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_CONTENTS))
			value = ROW_CONTENTS;
		String openBrackets = "[["; //$NON-NLS-1$
		String closeBrackets = "]]"; //$NON-NLS-1$
		String proposalString = openBrackets + value + closeBrackets;
		return referenceCompletion.createProposal(
			viewer,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			getDocumentOffset(),
			openBrackets.length(),
			value.length());
	}

	/**
	 * 
	 */
	private List getArrayOfProposals(String propertyName) {
		String value = ""; //$NON-NLS-1$
		String displayString = ""; //$NON-NLS-1$
		if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_LINKPARMS)) {
			displayString = IEGLConstants.PROPERTY_LINKPARAMETER;
			value = "@" + IEGLConstants.PROPERTY_LINKPARAMETER + " {}";  //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_HIERARCHY)) {
			displayString = IEGLConstants.PROPERTY_RELATIONSHIP;
			value = "@" + IEGLConstants.PROPERTY_RELATIONSHIP + " {}";  //$NON-NLS-1$ //$NON-NLS-2$
		}
		String openBracket = "["; //$NON-NLS-1$
		String closeBracket = "]"; //$NON-NLS-1$
		String proposalString = openBracket + value + closeBracket;
		if (displayString.length() > 0)
			return referenceCompletion.createProposal(
				viewer,
				displayString,
				proposalString,
				prefix,
				UINlsStrings.CAProposal_PropertyValue,
				getDocumentOffset(),
				proposalString.length()-2,
				0);
		else
			return new ArrayList();
	}

	/**
	 * 
	 */
	private List getArrayOfArraysProposals(String propertyName) {
		String value = ""; //$NON-NLS-1$
		int selectionLength = 0;
		String proposalString = "[" + value + "]";	//$NON-NLS-1$	//$NON-NLS-2$
		return referenceCompletion.createProposal(
			viewer,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			getDocumentOffset(),
			3,
			selectionLength);
	}

	/**
	 * 
	 */
	private List getNameValueRecordNames(int documentOffset) {
		return
			new EGLPartSearchProposalHandler(viewer, getDocumentOffset(), prefix, editor).getProposals(
				IEGLSearchConstants.RECORD, "", new String[] {IEGLConstants.RECORD_SUBTYPE_BASIC}, false); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	private List getLiteralValueProposals() {
		List proposals = getQuotedValueProposal(""); //$NON-NLS-1$
		proposals.addAll(getIntegerValueProposals());
		return proposals;
	}

	/**
	 * 
	 */
	private List getNameValueProposals(ITextViewer viewer, int documentOffset, String propertyName) {
		List proposals = new ArrayList();
		if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION)
			|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_ONPRERENDERFUNCTION)
			|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_ONPOSTRENDERFUNCTION)
			|| 	propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_ONVALUECHANGEFUNCTION))
				proposals.addAll(getNameValueFunctionProposals(getDocumentOffset()));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_REDEFINES))
			proposals.addAll(getNameValueRedefinesProposals(getDocumentOffset()));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SELECTFROMLISTITEM))
			proposals.addAll(getNameValueSelectFromListProposals(getDocumentOffset()));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SELECTEDROWITEM))
			proposals.addAll(getNameValueSelectedRowItemProposals(getDocumentOffset()));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SELECTEDVALUEITEM))
			proposals.addAll(getNameValueSelectedValueItemProposals(getDocumentOffset()));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS))
			proposals.addAll(referenceCompletion.getListValueValidationBypassFunctionsProposals(viewer, getDocumentOffset(), prefix, true));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATORFUNCTION))
			proposals.addAll(getNameValueValidatorFunctionProposals(getDocumentOffset()));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_NUMELEMENTSITEM)
				|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_MSGFIELD)
				|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_KEYITEM)
				|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_EVENTVALUEITEM)
				|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SELECTEDINDEXITEM)
				|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_COMMANDVALUEITEM)
				|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_LABELITEM)
				|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALUEITEM))
					proposals.addAll(getNameValueItemNames(viewer, getDocumentOffset(), null));
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_LENGTHITEM)) {
			proposals.addAll(getNameValueItemNames(viewer, getDocumentOffset(), null));
			proposals.addAll(getNameValueItemNames2(viewer, getDocumentOffset(), null));
		}
		else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_GETOPTIONSRECORD)
			|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD)
			|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_OPENOPTIONSRECORD)
			|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_PUTOPTIONSRECORD)
			|| propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD))
				proposals.addAll(getNameValueRecordNames(getDocumentOffset()));
		return proposals;
	}

	/**
	 * @return proposal list
	 */
	private List getNamedValueHelpKeyProposals(int documentOffset, String proposalString) {
		List proposals = new ArrayList();
		proposals.addAll(referenceCompletion.createProposal(
				viewer,
				proposalString,
				prefix,
				UINlsStrings.CAProposal_KeyValue,
				getDocumentOffset(),
				proposalString.length()-1,
				1));
		return proposals;
	}

	/**
	 * @param getDocumentOffset()
	 * @return
	 */
	private List getQuotedValueActionProposals(int documentOffset) {
		//WebTrans - not sure if we can give proposals for UIRecord???
		//If in a page handler return a list of functions in the page handler
			return new ArrayList();
	}
	
	private List getNameValueClassFieldNames(ITextViewer viewer, int documentOffset, String typeName) {
		List proposals = new ArrayList();
		List itemNames = referenceCompletion.getItemNames(viewer, getDocumentOffset(), typeName, false);
		String displayString;
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			displayString = (String) iter.next();
			proposals.addAll(
				referenceCompletion.createProposal(
					viewer,
					displayString,
					displayString,
					prefix,
					UINlsStrings.CAProposal_ItemName,
					getDocumentOffset(),
					displayString.length(),
					0));
		}
		return proposals;
	}
	
	private List getNameValueItemNames(ITextViewer viewer, int documentOffset, String typeName) {
		List proposals = new ArrayList();
		List itemNames = referenceCompletion.getItemNames(viewer, getDocumentOffset(), typeName);
		String displayString;
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			displayString = (String) iter.next();
			proposals.addAll(
				referenceCompletion.createProposal(
					viewer,
					displayString,
					displayString,
					prefix,
					UINlsStrings.CAProposal_ItemName,
					getDocumentOffset(),
					displayString.length(),
					0));
		}
		return proposals;
	}
	
	private List getNameValueItemNames2(ITextViewer viewer, int documentOffset, String typeName) {
		List proposals = new ArrayList();
		Node eglPart = referenceCompletion.getPart(viewer, documentOffset);
		List itemNames = referenceCompletion.getItemNames2(viewer, documentOffset, null, eglPart);
		String displayString;
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			displayString = (String) iter.next();
			proposals.addAll(
				referenceCompletion.createProposal(
					viewer,
					displayString,
					displayString,
					prefix,
					UINlsStrings.CAProposal_Variable,
					getDocumentOffset(),
					displayString.length(),
					0));
		}
		return proposals;
	}

	/**
	 * 
	 */
	public List getQuotedValueProposal(String propertyName) {
		List proposals = new ArrayList();

		if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_SERVICENAME))
			proposals.addAll(getQuotedValueServiceNameProposals(getDocumentOffset()));
		else {
			proposals.addAll(getQuotedValueProposal(getDocumentOffset()));
		}

		return proposals;
	}

	/**
	 * @param getDocumentOffset()
	 * @return
	 */
	private Collection getQuotedValueUIRecordNameProposals(int documentOffset) {
		String[] types = new String [] {IEGLConstants.RECORD_SUBTYPE_VGUI};
		List proposals = new EGLPartSearchProposalHandler(viewer, getDocumentOffset(), prefix, editor).getProposals(
			IEGLSearchConstants.RECORD, "", types, false); //$NON-NLS-1$
		if (proposals.size() == 0)
			proposals.addAll(getQuotedValueProposal(getDocumentOffset()));

		return proposals;
	}

	/**
	 * @param getDocumentOffset()
	 * @return
	 */
	private Collection getQuotedValueServiceNameProposals(int documentOffset) {
		List proposals = new EGLPartSearchProposalHandler(viewer, getDocumentOffset(), prefix, editor).getProposals(
			IEGLSearchConstants.SERVICE, "", null, false); //$NON-NLS-1$
		if (proposals.size() == 0)
			proposals.addAll(getQuotedValueProposal(getDocumentOffset()));

		return proposals;
	}

	/**
	 * @param getDocumentOffset()
	 * @return
	 */
	private Collection getQuotedValueDebugImplProposals(int documentOffset) {
		return getQuotedValueServiceNameProposals(getDocumentOffset());
	}

	/**
	 * @param getDocumentOffset()
	 * @return
	 */
	private Collection getQuotedValueTcpipLocationProposals() {
		StringBuffer buffer = new StringBuffer();
		int cursorPosition;
		
		cursorPosition = buffer.toString().length();
		buffer.append(TCPIPLOCATION_LIST_PROPOSAL);
		
		return referenceCompletion.createProposal(
			viewer,
			TCPIPLOCATION_LIST_PROPOSAL,
			buffer.toString(),
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			getDocumentOffset(),
			cursorPosition,
			TCPIPLOCATION_LIST_PROPOSAL.length());
	}

	/**
	 * @param getDocumentOffset()
	 * @return
	 */
	private List getQuotedValueProgramNameProposals(int documentOffset) {
		String[] types = new String [] {IEGLConstants.PROGRAM_SUBTYPE_VG_WEB_TRANSACTION};
		List proposals = new EGLPartSearchProposalHandler(viewer, getDocumentOffset(), prefix, editor).getProposals(
				IEGLSearchConstants.PROGRAM, "", types, false); //$NON-NLS-1$
		if (proposals.size() == 0)
			proposals.addAll(getQuotedValueProposal(getDocumentOffset()));

		return proposals;
	}

	/**
	 * @param file
	 * @return
	 */
	private List createJSPProposal(IFile file) {
		return referenceCompletion.createProposal(
			viewer,
			"\"" + file.getName() +"\"", //$NON-NLS-1$ //$NON-NLS-2$
			prefix,
			UINlsStrings.CAProposal_JSPFile,
			getDocumentOffset(),
			file.getName().length() + 2,
			0);
	}

	/**
	 * 
	 */
	private List getSpecificValueProposals(EGLPropertyRule rule) {
		List proposals = new ArrayList();
		for (int i = 0; i < rule.getSpecificValues().length; i++) {
			String proposalString = rule.getSpecificValues()[i];
			if(proposalString.toLowerCase().startsWith(prefix.toLowerCase())) {
				proposals.addAll(
					referenceCompletion.createProposal(
						viewer,
						proposalString,
						prefix,
						UINlsStrings.CAProposal_PropertyValue,
						getDocumentOffset()));
			}
		}
		return proposals;
	}

	/**
	 * 
	 */
	private List getSqlValueProposal(String propertyName) {
		String value = ""; //$NON-NLS-1$
		String proposalString = CodeConstants.EGL_SQL_PARTITION_START + " " + value + " }"; //$NON-NLS-1$ //$NON-NLS-2$
		if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_DEFAULTSELECTCONDITION)) {
			value = SQL_CONDITION;
			proposalString = CodeConstants.EGL_SQL_CONDITION_PARTITION_START + " " + value + " }"; //$NON-NLS-1$   //$NON-NLS-2$
		}
		else {
			proposalString = CodeConstants.EGL_SQL_PARTITION_START + "  }"; //$NON-NLS-1$
		}
		return referenceCompletion.createProposal(
			viewer,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_PropertyValue,
			getDocumentOffset(),
			CodeConstants.EGL_SQL_CONDITION_PARTITION_START.length() + 1,
			value.length());
	}

	/**
	 * @return proposal list
	 */
	private List getQuotedValueAliasProposal(int documentOffset) {
		StringBuffer buffer = new StringBuffer();
		int cursorPosition;
		
		cursorPosition = buffer.toString().length();
		buffer.append(ALIAS_PROPOSAL);
		
		return referenceCompletion.createProposal(
			viewer,
			ALIAS_PROPOSAL,
			buffer.toString(),
			prefix,
			UINlsStrings.CAProposal_KeyValue,
			getDocumentOffset(),
			cursorPosition,
			ALIAS_PROPOSAL.length());
	}

	/**
	 * @return proposal list
	 */
	private List getNameValueValidatorFunctionProposals(int documentOffset) {
		List proposals = new ArrayList();
		
		if (part instanceof Handler ||
			part instanceof Record) {
			proposals.addAll(
					new EGLPartSearchProposalHandler(viewer, getDocumentOffset(), prefix, editor).getProposals(
						IEGLSearchConstants.FUNCTION, "", new String[0], false)); //$NON-NLS-1$
		}
		if (proposals.size() == 0)
			proposals.addAll(getNameValueFunctionProposals(getDocumentOffset()));

		return proposals;
	}

	/**
	 * @return proposal list
	 */
	private List getNameValueRecordProposals(int documentOffset, int recordTypes) {
		//add variable record proposals
		return new EGLDeclarationProposalHandler(
			viewer,
			getDocumentOffset(),
			prefix,
			boundNode).getRecordProposals(recordTypes, false, false);
	}

	/**
	 * @return proposal list
	 */
	private List getNameValueRedefinesProposals(int documentOffset) {
		//add all variable record proposals
		return getNameValueRecordProposals(getDocumentOffset(), EGLDeclarationProposalHandler.ALL_RECORDS);
	}

	/**
	 * @return proposal list
	 */
	private List getNameValueProposals(int documentOffset, List itemNames) {
		List proposals = new ArrayList();
		String displayString;
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			displayString = (String) iter.next();
			proposals.addAll(
				referenceCompletion.createProposal(
					viewer,
					displayString,
					displayString,
					prefix,
					UINlsStrings.CAProposal_ItemName,
					getDocumentOffset(),
					displayString.length(),
					0));
		}
		return proposals;
	}

	private List getNameValueSelectFromListProposals(int documentOffset) {
		List itemNames = referenceCompletion.getArrayItemNames(viewer, getDocumentOffset(), null);
		return getNameValueProposals(documentOffset, itemNames);
	}

	private List getNameValueSelectedValueItemProposals(int documentOffset) {
		List itemNames = referenceCompletion.getItemNames(viewer, getDocumentOffset(), null, false);
		return getNameValueProposals(documentOffset, itemNames);
	}

	private List getNameValueSelectedRowItemProposals(int documentOffset) {
		List itemNames = referenceCompletion.getItemNames(viewer, getDocumentOffset(), null, false);
		return getNameValueProposals(documentOffset, itemNames);
	}

	/**
	 * @return proposal list
	 */
	private List getNameValueVariableRecordProposals(int documentOffset, int recordType) {
		//add variable basic record proposals
		List proposals = new ArrayList();
		if(part instanceof Program) {
			proposals = new EGLDeclarationProposalHandler(
				viewer,
				getDocumentOffset(),
				prefix,
				boundNode).getRecordProposals(recordType, false, false);
		}
		return proposals;
	}

	/**
	 * @return proposal list
	 */
	private List getQuotedValueHelpMsgKeyProposals(int documentOffset, String displayString) {
		List proposals = new ArrayList();
		StringBuffer buffer = new StringBuffer();
		int cursorPosition;
		
		buffer.append(displayString);
		
		if (hasQuoteEnd)
			cursorPosition = buffer.toString().length() - 1;
		else
			cursorPosition = buffer.toString().length() - 2;
		proposals.addAll(referenceCompletion.createProposal(
				viewer,
				displayString,
				buffer.toString(),
				prefix,
				UINlsStrings.CAProposal_KeyValue,
				getDocumentOffset(),
				cursorPosition,
				1));
		return proposals;
	}

	/**
	 * @return proposal list
	 */
	private List getQuotedValueHelpFormProposals(int documentOffset) {
		List proposals = new ArrayList();
		//How can I get a list of help forms?  They must be in the program's helpGroup
		return proposals;
	}

	/**
	 * @return proposal list
	 */
	private List getNameValueFunctionProposals(int documentOffset) {
		List proposals = new ArrayList();
		List itemNames = referenceCompletion.getFunctionNames(viewer, getDocumentOffset());
		// create the proposals
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			String displayString = (String) iter.next();
			String proposalString = displayString;
			proposals.addAll(
				referenceCompletion.createProposal(
					viewer,
					displayString,
					proposalString,
					prefix,
					UINlsStrings.CAProposal_NestedFunction,
					getDocumentOffset(),
					displayString.length(),
					0));
		}
		return proposals;
	}

	/**
	 * @param getDocumentOffset()
	 * @return
	 */
	private List getQuotedValueProposal(int documentOffset) {
		return referenceCompletion.createProposal(
			viewer,
			"\"\"", //$NON-NLS-1$
			prefix,
			"", //$NON-NLS-1$
			getDocumentOffset(),
			1,
			0);
	}
}

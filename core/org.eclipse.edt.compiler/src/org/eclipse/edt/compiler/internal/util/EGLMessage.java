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
package org.eclipse.edt.compiler.internal.util;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.edt.compiler.internal.interfaces.IEGLMessageContributor;
import org.eclipse.edt.compiler.internal.interfaces.IEGLNestedMessageContributor;
import org.eclipse.edt.compiler.internal.interfaces.IEGLStatementNode;


/**
 * Insert the type's description here.
 * Creation date: (9/7/2001 9:06:42 AM)
 * @author: 
 */
public class EGLMessage extends Object  implements IGenerationResultsMessage{

	protected int severity = 0;
	private String id = null;
	private String[] params = null;
	private String groupName = null;
	private int startLineNumber = 0;
	private int endLineNumber = 0;
	private int endOffset = 0;
	private int startOffset = 0;

	private IEGLMessageContributor messageContributor;
	private int startColumnNumber = 0;
	private int endColumnNumber = 0;

	private String builtMessage;

	private static ResourceBundle messageBundle;
	private String partName;

	/**
	 * @return Returns the partName.
	 */
	public String getPartName() {
		return partName;
	}
	/**
	 * @param partName The partName to set.
	 */
	public void setPartName(String partName) {
		this.partName = partName;
	}
	/**
	 * An object that fills in the blanks in the error messages.
	 */
	private static final MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$

	//Severities:
	public static final int EGL_ERROR_MESSAGE = 1;
	public static final int EGL_WARNING_MESSAGE = 2;
	public static final int EGL_INFORMATIONAL_MESSAGE = 3;

	//Groups:
	public static final String EGLMESSAGE_GROUP_VALIDATION = "Validation"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_DEPLOYMENT = "Deployment"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_EDITOR = "Editor"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_XML_VALIDATION = "XML Validation"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_INPUT = "Batch Input"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_STATEMENT_PARSER = "StatementParser"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_SYNTAX = "Syntax"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_INVALID_NAME_LENGTH = "3001"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_CHARACTER_IN_NAME = "3002"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EZE_NOT_ALLOWED = "3003"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_RESERVED_WORD_NOT_ALLOWED = "3019"; //$NON-NLS-1$
	public static final String EGLMESSAGE_XML_VALIDATION_ERROR = "3998"; //$NON-NLS-1$
	public static final String EGLMESSAGE_XML_VALIDATION_ERROR_IN_FILE = "3999"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_UNSUPPORTED_SQL_TYPE_ON_RETRIEVE = "4584"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_DECIMAL_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4585"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_DECIMAL_DECIMALS_SHORTENED_FROM_ON_RETRIEVE = "4586"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_HEX_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4587"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_INVALID_LENGTH_SET_TO_ZERO_ON_RETRIEVE = "4589";	//$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_CHAR_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4590"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_DBCHAR_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4591"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_UNICODE_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4592"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_INVALID_DECIMALS_SET_TO_ZERO_ON_RETRIEVE = "4593";	//$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYING_RUIHANDLER = "8303"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_COMPLETE = "8304"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED = "8305"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_EXCEPTION = "8306"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_NLS_FILE = "8307"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_LOCATE_NLS_FILE = "8308"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_LOCATE_EGLDD_FILE = "8310"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_HTML_FILE = "8312"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_PROPERTY_FILE = "8314"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_PROPERTY_FILE = "8315"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_BIND_FILE = "8316"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_RT_PROPERTY_FILE = "8318"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_RT_PROPERTY_FILE = "8319"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_HTML_FILE = "8320"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_PROPERTIES_FOLDER = "8325"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_LOCALE_PROCESSING_FAILED = "8326"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_CREATED_RESOURCE_REFS = "8329"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEFINE_DATASOURCE = "8330"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_WRITE_CONTEXTDOTXML = "8331"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEFINED_DATASOURCES = "8332"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_SERVER_NOT_TOMCAT = "8333"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PARTNOTFOUND = "9001"; //$NON-NLS-1$
	public static final String EGLMESSAGE_COMPILE_ERRORS = "9991"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_GENERATION_FAILED = "9997"; //$NON-NLS-1$
	public static final String EGLMESSAGE_EXCEPTION_MESSAGE = "9998"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EXCEPTION_STACKTRACE = "9999"; //$NON-NLS-1$

	/**
	 * EGLMessageconstructor comment.
	 * @param aBundleName java.lang.String
	 * @param aSeverity int (expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE)
	 * @param anId java.lang.String (error message number == key of message in resource bundle)
	 * @param groupName java.lang.String (see EGLMESSAGE_GROUP_CORE for an example)
	 */
	public EGLMessage(
		String aBundleName,
		int aSeverity,
		String anId,
		String groupName,
		Object messageContributor,
		java.lang.String[] aParams,
		int aStartOffset,
		int anEndOffset) {

		//  read the resource bundle and pass along to the real EGLMessage constructor

		this(
			ResourceBundle.getBundle(aBundleName, Locale.getDefault()),
			aSeverity,
			anId,
			groupName,
			messageContributor,
			aParams,
			aStartOffset,
			anEndOffset);

	}

	/**
	 * EGLMessageconstructor comment.
	 * @param aBundleName java.lang.String
	 * @param aSeverity int (expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE)
	 * @param anId java.lang.String (error message number == key of message in resource bundle)
	 * @param groupName java.lang.String (see EGLMESSAGE_GROUP_CORE for an example)
	 */
	public EGLMessage(
		String aBundleName,
		int aSeverity,
		String anId,
		String groupName,
		Object messageContributor,
		java.lang.String[] aParams,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		//  read the resource bundle and pass along to the real EGLMessage constructor

		this(
			ResourceBundle.getBundle(aBundleName, Locale.getDefault()),
			aSeverity,
			anId,
			groupName,
			messageContributor,
			aParams,
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);

	}
	
	public EGLMessage() {
		super();
	}
	/**
	 * EGLMessage constructor comment.
	 * @param bundle java.util.ResourceBundle
	 * @param aSeverity int (expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE)
	 * @param anId java.lang.String (error message number == key of message in resource bundle)
	 * @param groupName java.lang.String (see EGLMESSAGE_GROUP_CORE for an example)
	 */
	public EGLMessage(ResourceBundle bundle, int aSeverity, String anId, String groupName, java.lang.String[] aParams) {

		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);
		messageBundle = bundle;

		builtMessage = buildMessageText(anId, aParams);
	}
	/**
	 * EGLMessage constructor comment.
	 * @param aBundle       The resource bundle that contains the message text.
	 * @param aSeverity     expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE
	 * @param anId          error message number == key of message in resource bundle
	 * @param groupName     see EGLMESSAGE_GROUP_CORE for an example
	 * @param messageContributor The holder of the part in error.
	 * @param aParams       The inserts for the message
	 * @param aStartOffset  The offset where the error starts.
	 * @param anEndOffset   The offset where the error ends.
	 */
	public EGLMessage(
		ResourceBundle aBundle,
		int aSeverity,
		String anId,
		String groupName,
		Object aMessageContributor,
		java.lang.String[] aParams,
		int aStartOffset,
		int anEndOffset) {
						
		messageBundle = aBundle;
		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);
		setStartOffset(aStartOffset);
		setEndOffset(anEndOffset);
		//	messageBundle = ResourceBundle.getBundle(aBundleName, Locale.getDefault()); //$NON-NLS-1$

		// Now read the message text from the resource bundle
		// and format with the inserts 	

		builtMessage = buildMessageText(anId, aParams);

		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartOffset == -1) {
					//no startline given, see if we can find it from the IMessageContributor
					if (mc.getStart() != null) {
						setStartLine(mc.getStart().getLine());
						setStartColumn(mc.getStart().getColumn());
						setStartOffset(mc.getStart().getOffset());
					}
				}
				if (anEndOffset == -1) {
					// no endline given, see if we can find it from the IMessageContributor 
					if (mc.getEnd() != null) {
						setEndLine(mc.getEnd().getLine());
						setEndColumn(mc.getEnd().getColumn());
						setEndOffset(mc.getEnd().getOffset() + 1);
					}
				}
			}
		}

	}
	/**
	 * EGLMessage constructor comment.
	 * @param aBundle       The resource bundle that contains the message text.
	 * @param aSeverity     expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE
	 * @param anId          error message number == key of message in resource bundle
	 * @param groupName     see EGLMESSAGE_GROUP_CORE for an example
	 * @param messageContributor The holder of the part in error.
	 * @param aParams       The inserts for the message
	 * @param aStartLine    The line where the error starts.
	 * @param aStartColum   The column where the error starts.
	 * @param anEndLine     The line where the error ends.
	 * @param anEndColum    The column where the error ends.
	 */
	public EGLMessage(
		ResourceBundle aBundle,
		int aSeverity,
		String anId,
		String groupName,
		Object aMessageContributor,
		java.lang.String[] aParams,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		int aStartOffset = 0;
		int anEndOffset = 0;
						
		messageBundle = aBundle;
		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);

		//	messageBundle = ResourceBundle.getBundle(aBundleName, Locale.getDefault()); //$NON-NLS-1$

		// Now read the message text from the resource bundle
		// and format with the inserts 	

		builtMessage = buildMessageText(anId, aParams);

		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartLine == -1) {
					//no startline given, see if we can find it from the IMessageContributor
					if (mc.getStart() != null) {
						aStartLine = mc.getStart().getLine();
						aStartColumn = mc.getStart().getColumn();
						aStartOffset = mc.getStart().getOffset();
					}
				}
				if (anEndLine == -1) {
					// no endline given, see if we can find it from the IMessageContributor
					if (mc.getEnd() != null) {
						anEndLine = mc.getEnd().getLine();
						anEndColumn = mc.getEnd().getColumn();
						anEndOffset = mc.getEnd().getOffset() + 1;
					}
				}
			}
		}

		setStartLine(aStartLine);
		setStartColumn(aStartColumn);
		setEndLine(anEndLine);
		setEndColumn(anEndColumn);
		setStartOffset(aStartOffset);
		setEndOffset(anEndOffset);
	}
	/**
	 * EGLMessage constructor comment.
	 * @param aBundle       The resource bundle that contains the message text.
	 * @param aSeverity     expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE
	 * @param anId          error message number == key of message in resource bundle
	 * @param groupName     see EGLMESSAGE_GROUP_CORE for an example
	 * @param messageContributor The holder of the part in error.
	 * @param aParams       The inserts for the message
	 * @param aStartLine    The line where the error starts.
	 * @param aStartColum   The column where the error starts.
	 * @param anEndLine     The line where the error ends.
	 * @param anEndColum    The column where the error ends.
	 */
	public EGLMessage(
		String aBundle,
		int aSeverity,
		String anId,
		String groupName,
		Object aMessageContributor,
		java.lang.String[] aParams,
		int aStartLine,
		int aStartColumn,
		int aStartOffset,
		int anEndLine,
		int anEndColumn,
		int anEndOffset) {

		messageBundle = ResourceBundle.getBundle(aBundle, Locale.getDefault());
		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);

		// Now read the message text from the resource bundle
		// and format with the inserts 	

		builtMessage = buildMessageText(anId, aParams);

		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartLine == -1) {
					//no startline given, see if we can find it from the IMessageContributor
					if (mc.getStart() != null) {
						aStartLine = mc.getStart().getLine();
						aStartColumn = mc.getStart().getColumn();
						aStartOffset = mc.getStart().getOffset();
					}
				}
				if (anEndLine == -1) {
					// no endline given, see if we can find it from the IMessageContributor
					if (mc.getEnd() != null) {
						anEndLine = mc.getEnd().getLine();
						anEndColumn = mc.getEnd().getColumn();
						anEndOffset = mc.getEnd().getOffset() + 1;
					}
				}
			}
		}

		setStartLine(aStartLine);
		setStartColumn(aStartColumn);
		setEndLine(anEndLine);
		setEndColumn(anEndColumn);
		setStartOffset(aStartOffset);
		setEndOffset(anEndOffset);
	}
	/**
	 * Returns the localized version of the message that corresponds to the key.
	 *
	 * @param key      the message key, one of the constants defined in this class.
	 * @param inserts  the message inserts.
	 * @return the localized version of the message that corresponds to the key.
	 */
	public static String buildMessageText(String key, Object[] inserts) {
		try {
			String message = messageBundle.getString(key);
			if (message == null || inserts == null || inserts.length == 0) {
				return message;
			}

			formatter.applyPattern(message);
			return formatter.format(insertsWithoutNulls(inserts));
		} catch (MissingResourceException mrx) {
			return key;
		}
	}

	public static Object[] insertsWithoutNulls(Object[] originalInserts) {

		int numberInserts = originalInserts.length;
		Object[] newInserts = new Object[numberInserts];
		for (int i = 0; i < numberInserts; i++) {
			if (originalInserts[i] != null)
				newInserts[i] = originalInserts[i];
			else
				newInserts[i] = ""; //$NON-NLS-1$
		}
		return newInserts;
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorErrorMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(resource, EGLMessage.EGL_ERROR_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorErrorMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_ERROR_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorErrorMessage(ResourceBundle resource, String messageID, String insert) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { insert });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorInformationalMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorInformationalMessage(ResourceBundle resource, String messageID, String insert) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { insert });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorInformationalMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_INFORMATIONAL_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorWarningMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorWarningMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_WARNING_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLInputErrorMessage(String messageID, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_INPUT,
			null,
			inserts,
			-1,
			-1,
			-1,
			-1);

	}
	/**
	 *
	 */
	public static EGLMessage createEGLInputErrorMessage(String messageID, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_INPUT,
			null,
			new String[] { insert },
			-1,
			-1,
			-1,
			-1);
	}
	
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(String messageID, Object messageContributor) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { null },
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			-1,
			-1,
			-1,
			-1);
	}
	public static EGLMessage createEGLDeploymentErrorMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
				getValidationResourceBundleName(),
				EGLMessage.EGL_ERROR_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_DEPLOYMENT,
				messageContributor,
				inserts,
				-1,
				-1,
				-1,
				-1);
	}
	public static EGLMessage createEGLDeploymentInformationalMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
				getValidationResourceBundleName(),
				EGLMessage.EGL_INFORMATIONAL_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_DEPLOYMENT,
				messageContributor,
				inserts,
				-1,
				-1,
				-1,
				-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String[] inserts,
		int aStartLine,
		int aStartColumn) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartLine,
			aStartColumn,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String[] inserts,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationPartErrorMessage(
		ResourceBundle resource,
		String messageID,
		Object messageContributor,
		String[] inserts,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);
	}	

	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String[] inserts,
		IEGLStatementNode node) {

		EGLMessage message =
			new EGLMessage(
				getValidationResourceBundleName(),
				EGLMessage.EGL_ERROR_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
				messageContributor,
				inserts,
				node.getStartLine(),
				node.getStartColumn(),
				node.getEndLine(),
				node.getEndColumn());
		message.setStartOffset(node.getStartOffset());
		message.setEndOffset(node.getEndOffset() + 1);
		return message;
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(String messageID, Object messageContributor, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String insert,
		int aStartOffset,
		int anEndOffset) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			aStartOffset,
			anEndOffset);
		}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String bundleName,
		String messageID,
		String[] inserts,
		int aStartOffset,
		int anEndOffset) {
	
		Object messageContributor = null;

		return new EGLMessage(
			bundleName,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartOffset,
			anEndOffset);
		}		
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String insert,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationInformationalMessage(String messageID, Object messageContributor) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { null },
			1,
			1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationInformationalMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			1,
			1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationInformationalMessage(String messageID, Object messageContributor, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			1,
			1,
			-1,
			-1);
	}
	
	public static EGLMessage createEGLValidationWarningMessage(
		String bundleName,
		String messageID,
		String[] inserts,
		int aStartOffset,
		int anEndOffset) {
	
		Object messageContributor = null;

		return new EGLMessage(
			bundleName,
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartOffset,
			anEndOffset);
		}		
	
	/**
	 *
	 */
	public static EGLMessage createEGLValidationWarningMessage(String messageID, Object messageContributor) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { null },
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationWarningMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationWarningMessage(String messageID, Object messageContributor, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			-1,
			-1,
			-1,
			-1);
	}
	
	public static EGLMessage createEGLSytaxErrorMessage(
		String bundleName,
		String messageID,
		String[] inserts,
		int aStartOffset,
		int anEndOffset) {
	
		Object messageContributor = null;

		return new EGLMessage(
			bundleName,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_SYNTAX,
			messageContributor,
			inserts,
			aStartOffset,
			anEndOffset);
	}
	
	public static EGLMessage createEGLSyntaxWarningMessage(
			String bundleName,
			String messageID,
			String[] inserts,
			int aStartOffset,
			int anEndOffset) {
		
			Object messageContributor = null;

			return new EGLMessage(
				bundleName,
				EGLMessage.EGL_WARNING_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_SYNTAX,
				messageContributor,
				inserts,
				aStartOffset,
				anEndOffset);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/9/2001 2:18:31 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.utils.EGLMessage
	 * @param String messageString
	 * @param String fileName
	 * @param lineNumber int
	 * @param columnNumber int
	 * @param messageContributor object
	 */
	public static EGLMessage createEGLXMLValidationErrorMessage(
		String messageString,
		String fileName,
		int lineNumber,
		int columnNumber,
		Object messageContributor) {

		String messageNumber;
		String[] messageInserts;

		if (fileName == null) {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR;
			messageInserts = new String[] { messageString };
		} else {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR_IN_FILE;
			messageInserts = new String[] { messageString, fileName };
		}

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageNumber,
			EGLMessage.EGLMESSAGE_GROUP_XML_VALIDATION,
			messageContributor,
			messageInserts,
			lineNumber,
			columnNumber,
			lineNumber,
			columnNumber);

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/9/2001 2:18:31 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.utils.EGLMessage
	 * @param String messageString
	 * @param String fileName
	 * @param lineNumber int
	 * @param columnNumber int
	 * @param messageContributor object
	 */
	public static EGLMessage createEGLXMLValidationWarningMessage(
		String messageString,
		String fileName,
		int lineNumber,
		int columnNumber,
		Object messageContributor) {

		String messageNumber;
		String[] messageInserts;

		if (fileName == null) {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR;
			messageInserts = new String[] { messageString };
		} else {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR_IN_FILE;
			messageInserts = new String[] { messageString, fileName };
		}

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageNumber,
			EGLMessage.EGLMESSAGE_GROUP_XML_VALIDATION,
			messageContributor,
			messageInserts,
			lineNumber,
			columnNumber,
			lineNumber,
			columnNumber);

	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with inserts already applied.
	 */
	public String getBuiltMessage() {

		String flag = " "; //$NON-NLS-1$
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE :
				flag = "e"; //$NON-NLS-1$
				break;
			case EGL_WARNING_MESSAGE :
				flag = "w"; //$NON-NLS-1$
				break;
			case EGL_INFORMATIONAL_MESSAGE :
				flag = "i"; //$NON-NLS-1$
		}

		return "IWN." + getMessagePrefix() + "." + id + "." + flag + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		startLineNumber + "/" + startColumnNumber + " " + builtMessage; //$NON-NLS-1$ //$NON-NLS-2$
	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with inserts already applied, and no lone or column information.
	 */
	public String getBuiltMessageWithoutLineAndColumn() {

		String flag = " "; //$NON-NLS-1$
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE :
				flag = "e"; //$NON-NLS-1$
				break;
			case EGL_WARNING_MESSAGE :
				flag = "w"; //$NON-NLS-1$
				break;
			case EGL_INFORMATIONAL_MESSAGE :
				flag = "i"; //$NON-NLS-1$
		}

		return "IWN." + getMessagePrefix() + "." + id + "." + flag + " " + builtMessage; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with inserts already applied.
	 */
	public String getBuiltMessageWithLineAndColumn() {

		String flag = " "; //$NON-NLS-1$
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE :
				flag = "e"; //$NON-NLS-1$
				break;
			case EGL_WARNING_MESSAGE :
				flag = "w"; //$NON-NLS-1$
				break;
			case EGL_INFORMATIONAL_MESSAGE :
				flag = "i"; //$NON-NLS-1$
		}

		if (messageContributor instanceof IEGLMessageContributor && ((IEGLMessageContributor) messageContributor).getResourceName() != null) { // have project/file information
			return "IWN." + getMessagePrefix() + "." + id + "." + flag //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+" - " + ((IEGLMessageContributor) messageContributor).getResourceName() //$NON-NLS-1$
			+" - " + startLineNumber + "/" + startColumnNumber //$NON-NLS-1$ //$NON-NLS-2$
			+" - " + builtMessage; //$NON-NLS-1$
		} else {
			return "IWN." + getMessagePrefix() + "." + id + "." + flag //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+" - " + startLineNumber + "/" + startColumnNumber //$NON-NLS-1$ //$NON-NLS-2$
			+" - " + builtMessage; //$NON-NLS-1$

		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @return int
	 */
	public int getEndColumn() {
		return endColumnNumber;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * line number associated with it. If the line number was not set, "0" will
	 * be returned.
	 */
	public int getEndLine() {
		return endLineNumber;
	}
	/**
	 * Returns the id of the message.  Message ids are used as the constants in property bundles which
	 * localize the description of the message in a locale-independent fashion.
	 * The id may not be null or the empty string.
	 * @return java.lang.String
	 */
	public String getId() {
		return id;
	}
	/**
	 * Returns whether the message is a syntax message or not
	 */
	public boolean isSyntaxMessage() {

		if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_SYNTAX )) {
			return true;
		} else
			return false;	
	}	
	/**
	 * Returns a message prefix for the groupName 
	 */
	public String getMessagePrefix() {

		if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_VALIDATION)) {
			return ("VAL"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_STATEMENT_PARSER)) {
			return ("EGL"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_XML_VALIDATION)) {
			return ("XML"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_EDITOR)) {
			return ("EDT"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_INPUT)) {
			return ("INP"); //$NON-NLS-1$
		} else if( groupName.equals(EGLMessage.EGLMESSAGE_GROUP_SYNTAX )) {
			return ("SYN"); //$NON-NLS-1$
		} else if( groupName.equals(EGLMessage.EGLMESSAGE_GROUP_DEPLOYMENT )) {
			return ("DEP"); //$NON-NLS-1$
		} else
			return ("XXX"); //$NON-NLS-1$	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 4:46:02 PM)
	 * @return java.lang.Object
	 */
	public java.lang.Object getMessageContributor() {
		return messageContributor;
	}
	public static ResourceBundle getResourceBundle() {

		return ResourceBundle.getBundle(getResourceBundleName());
	}
	/**
	 *
	 */
	public static String getResourceBundleName() {

		return org.eclipse.edt.compiler.internal.IEGLBaseConstants.EGL_VALIDATION_RESOURCE_BUNDLE_NAME;
	}
	// ---------- resource bundle -------------

	/**
	 * We don't want to crash because of a missing String.
	 * Returns the key if not found.
	 */
	public static String getResourceString(String key) {

		try {
			return getResourceBundle().getString(key);
		} catch (MissingResourceException x) {
			return key;
		} catch (NullPointerException x) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	/**
	 * Returns the severity level of the message.  One of SeverityEnum.XXX constants.
	 * @return int
	 */
	public int getSeverity() {
		return severity;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @return int
	 */
	public int getStartColumn() {
		return startColumnNumber;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * starting offset associated with it. If the offset was not set, "0" will
	 * be returned.
	 */
	public int getStartOffset() {
		return startOffset;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * ending offset associated with it. If the offset was not set, "0" will
	 * be returned.
	 */
	public int getEndOffset() {
		return endOffset;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * line number associated with it. If the line number was not set, "0" will
	 * be returned.
	 */
	public int getStartLine() {
		return startLineNumber;
	}
	/**
	 *
	 */
	public static String getValidationResourceBundleName() {

		return org.eclipse.edt.compiler.internal.IEGLBaseConstants.EGL_VALIDATION_RESOURCE_BUNDLE_NAME;
	}

	//public static String getWebServicesResourceBundleName() {
	//
	//	return "org.eclipse.edt.compiler.internal.webservices.parteditor.EGLWebServicesPartEditorResources";
	//}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/14/2001 10:33:09 PM)
	 * @return boolean
	 */
	public boolean isError() {
		return getSeverity() == EGLMessage.EGL_ERROR_MESSAGE;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/5/2001 11:33:50 AM)
	 * @return int
	 */
	public boolean isInformational() {
		if ((getSeverity()) == EGL_INFORMATIONAL_MESSAGE) {
			return true;
		} else
			return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/5/2001 11:33:50 AM)
	 * @return int
	 */
	public boolean isWarning() {
		if ((getSeverity()) == EGL_WARNING_MESSAGE) {
			return true;
		} else
			return false;
	}
	/**
	 * Returns the raw message text
	 */
	public String primGetBuiltMessage() {

		return builtMessage;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @param newUserData java.lang.Object
	 */
	public void setBuiltMessage(String msgText) {
		builtMessage = msgText;
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * ending column number may be set. To indicate no column number or use of endPosition,
	 * use "0".
	 * @param newColumnNumber int
	 */
	public void setEndColumn(int newColumnNumber) {
		if (newColumnNumber < 0) {
			endColumnNumber = 0;
		} else {
			endColumnNumber = newColumnNumber;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * ending line number may be set. To indicate no line number or use of endPosition,
	 * use "0".
	 */
	public void setEndLine(int lineNumber) {
		if (lineNumber < 0) {
			this.endLineNumber = 0;
		} else {
			this.endLineNumber = lineNumber;
		}
	}
	/**
	 * To support removal of a subset of validation messages, an IValidator
	 * may assign group names to IMessages. An IMessage subset will be identified
	 * by the name of its group. Default (null) means no group.
	 */
	public void setGroupName(String name) {
		groupName = name;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 4:46:02 PM)
	 * @param newPart java.lang.Object
	 */
	public void setMessageContributor(IEGLMessageContributor newPart) {
		messageContributor = newPart.getMessageContributor();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 4:46:02 PM)
	 * @param newPart java.lang.Object
	 */
	public void setMessageContributor(IEGLNestedMessageContributor newPart) {
		messageContributor = newPart.getMessageContributor();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @param newColumnNumber int
	 */
	public void setStartColumn(int newColumnNumber) {
		if (newColumnNumber < 0) {
			startColumnNumber = 0;
		} else {
			startColumnNumber = newColumnNumber;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * line number which may be set. To indicate no line number, use "0".
	 */
	public void setStartLine(int lineNumber) {
		if (lineNumber < 0) {
			this.startLineNumber = 0;
		} else {
			this.startLineNumber = lineNumber;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * starting offset which may be set. To indicate no offset, use "0".
	 */
	public void setStartOffset(int offset) {
		if (offset < 0) {
			this.startOffset = 0;
		} else {
			this.startOffset = offset;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * starting offset which may be set. To indicate no offset, use "0".
	 */
	public void setEndOffset(int offset) {
		if (offset < 0) {
			this.endOffset = 0;
		} else {
			this.endOffset = offset;
		}
	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with the bundle loaded by the default ClassLoader.
	 */
	public String toString() {

		return getId() + ": " + builtMessage; //$NON-NLS-1$
	}
	/**
	 * @return
	 */
	public String[] getParams() {
		return params;
	}
	
	public String getResourceName() {
		if (getMessageContributor() instanceof IEGLMessageContributor) {
			return ((IEGLMessageContributor)getMessageContributor()).getResourceName();
		}
		return null;
	}

}

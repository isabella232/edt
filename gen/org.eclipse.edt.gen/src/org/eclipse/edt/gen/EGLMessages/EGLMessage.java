/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.EGLMessages;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.interfaces.IEGLMessageContributor;
import org.eclipse.edt.compiler.internal.interfaces.IEGLNestedMessageContributor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.mof.egl.Annotation;

public class EGLMessage extends Object implements IGenerationResultsMessage {

	public static final String IncludeEndOffset = "IncludeEndOffsetInGenerationMessage";
	protected int severity = 0;
	private String id = null;
	private String[] params = null;
	private IEGLMessageContributor messageContributor;
	private int startLineNumber = 0;
	private int endLineNumber = 0;
	private int endOffset = 0;
	private int startOffset = 0;
	private int startColumnNumber = 0;
	private int endColumnNumber = 0;

	private String builtMessage;

	private Map<String, String> messageBundle;
	private String partName;

	private static final MessageFormat formatter = new MessageFormat("");

	// Severities:
	public static final int EGL_ERROR_MESSAGE = 1;
	public static final int EGL_WARNING_MESSAGE = 2;
	public static final int EGL_INFORMATIONAL_MESSAGE = 3;

	public EGLMessage() {
		super();
	}

	public EGLMessage(Map<String, String> mapping, int aSeverity, String anId, java.lang.String[] aParams) {
		severity = aSeverity;
		id = anId;
		params = aParams;
		messageBundle = mapping;
		builtMessage = buildMessageText(anId, aParams);
	}

	public EGLMessage(Map<String, String> mapping, int aSeverity, String anId, Object aMessageContributor, java.lang.String[] aParams, int aStartOffset,
		int anEndOffset) {
		messageBundle = mapping;
		severity = aSeverity;
		id = anId;
		params = aParams;
		setStartOffset(aStartOffset);
		setEndOffset(Math.max(anEndOffset, aStartOffset));
		builtMessage = buildMessageText(anId, aParams);
		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartOffset == -1) {
					// no startline given, see if we can find it from the IMessageContributor
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

	public EGLMessage(Map<String, String> mapping, int aSeverity, String anId, Object aMessageContributor, java.lang.String[] aParams, int aStartLine,
		int aStartColumn, int anEndLine, int anEndColumn) {
		int aStartOffset = aStartColumn;
		int anEndOffset = Math.max(anEndColumn, aStartOffset);
		messageBundle = mapping;
		severity = aSeverity;
		id = anId;
		params = aParams;
		builtMessage = buildMessageText(anId, aParams);
		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartLine == -1) {
					// no startline given, see if we can find it from the IMessageContributor
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

	public String buildMessageText(String key, Object[] inserts) {
		String message = messageBundle.get(key);
		if (message != null) {
			if (inserts == null || inserts.length == 0)
				return message;
			formatter.applyPattern(message);
			return formatter.format(insertsWithoutNulls(inserts));
		} else
			return key;
	}

	public static Object[] insertsWithoutNulls(Object[] originalInserts) {
		int numberInserts = originalInserts.length;
		Object[] newInserts = new Object[numberInserts];
		for (int i = 0; i < numberInserts; i++) {
			if (originalInserts[i] != null)
				newInserts[i] = originalInserts[i];
			else
				newInserts[i] = "";
		}
		return newInserts;
	}

	public static EGLMessage createEGLMessage(Map<String, String> mapping, int aSeverity, String messageID, Object messageContributor, String[] inserts, Annotation eglLocation) {
		int startLine = 0;
		int startOffset = 0;
		int endOffset = 0;
		if (eglLocation != null) {
			if (eglLocation.getValue(IEGLConstants.EGL_PARTLINE) != null)
				startLine = ((Integer) eglLocation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			if (eglLocation.getValue(IEGLConstants.EGL_PARTOFFSET) != null)
				startOffset = ((Integer) eglLocation.getValue(IEGLConstants.EGL_PARTOFFSET)).intValue();
			if (eglLocation.getAnnotation(IncludeEndOffset) != null &&
					eglLocation.getValue(IEGLConstants.EGL_PARTLENGTH) != null) {
				endOffset = startOffset + ((Integer) eglLocation.getValue(IEGLConstants.EGL_PARTLENGTH)).intValue();
				eglLocation.removeAnnotation(eglLocation.getAnnotation(IncludeEndOffset));
			}
		}
		return new EGLMessage(mapping, aSeverity, messageID, messageContributor, inserts, startLine, startOffset, 0, endOffset);
	}

	public static EGLMessage createEGLStackTraceMessage( Map<String, String> mapping, int aSeverity, 
			String messageID, Throwable exception, String[] inserts, Annotation eglLocation )
	{
		EGLMessage msg = createEGLMessage( mapping, aSeverity, messageID, exception, inserts, eglLocation );
		StringWriter sw = new StringWriter();
		exception.printStackTrace( new PrintWriter( sw ) );
		msg.builtMessage = msg.builtMessage + '\n' + sw;
		return msg;
	}

	public String getBuiltMessage() {
		String flag = " ";
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE:
				flag = "e";
				break;
			case EGL_WARNING_MESSAGE:
				flag = "w";
				break;
			case EGL_INFORMATIONAL_MESSAGE:
				flag = "i";
		}
		return "IWN." + messageBundle.get("group") + "." + id + "." + flag + " " + startLineNumber + "/" + startColumnNumber + " " + builtMessage;
	}

	public String getBuiltMessageWithoutLineAndColumn() {
		String flag = " ";
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE:
				flag = "e";
				break;
			case EGL_WARNING_MESSAGE:
				flag = "w";
				break;
			case EGL_INFORMATIONAL_MESSAGE:
				flag = "i";
		}
		return "IWN." + messageBundle.get("group") + "." + id + "." + flag + " " + builtMessage;
	}

	public String getBuiltMessageWithLineAndColumn() {
		String flag = " ";
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE:
				flag = "e";
				break;
			case EGL_WARNING_MESSAGE:
				flag = "w";
				break;
			case EGL_INFORMATIONAL_MESSAGE:
				flag = "i";
		}
		if (messageContributor instanceof IEGLMessageContributor && ((IEGLMessageContributor) messageContributor).getResourceName() != null)
			return "IWN." + messageBundle.get("group") + "." + id + "." + flag + " - " + ((IEGLMessageContributor) messageContributor).getResourceName()
				+ " - " + startLineNumber + "/" + startColumnNumber + " - " + builtMessage;
		else
			return "IWN." + messageBundle.get("group") + "." + id + "." + flag + " - " + startLineNumber + "/" + startColumnNumber + " - " + builtMessage;
	}

	public int getEndColumn() {
		return endColumnNumber;
	}

	public int getEndLine() {
		return endLineNumber;
	}

	public String getId() {
		return id;
	}

	public java.lang.Object getMessageContributor() {
		return messageContributor;
	}

	public int getSeverity() {
		return severity;
	}

	public int getStartColumn() {
		return startColumnNumber;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public int getStartLine() {
		return startLineNumber;
	}

	public boolean isError() {
		return getSeverity() == EGLMessage.EGL_ERROR_MESSAGE;
	}

	public boolean isInformational() {
		return getSeverity() == EGL_INFORMATIONAL_MESSAGE;
	}

	public boolean isWarning() {
		return getSeverity() == EGL_WARNING_MESSAGE;
	}

	public String primGetBuiltMessage() {
		return builtMessage;
	}

	public void setBuiltMessage(String msgText) {
		builtMessage = msgText;
	}

	public void setEndColumn(int newColumnNumber) {
		if (newColumnNumber < 0)
			endColumnNumber = 0;
		else
			endColumnNumber = newColumnNumber;
	}

	public void setEndLine(int lineNumber) {
		if (lineNumber < 0)
			this.endLineNumber = 0;
		else
			this.endLineNumber = lineNumber;
	}

	public void setMessageContributor(IEGLMessageContributor newPart) {
		messageContributor = newPart.getMessageContributor();
	}

	public void setMessageContributor(IEGLNestedMessageContributor newPart) {
		messageContributor = newPart.getMessageContributor();
	}

	public void setStartColumn(int newColumnNumber) {
		if (newColumnNumber < 0)
			startColumnNumber = 0;
		else
			startColumnNumber = newColumnNumber;
	}

	public void setStartLine(int lineNumber) {
		if (lineNumber < 0)
			this.startLineNumber = 0;
		else
			this.startLineNumber = lineNumber;
	}

	public void setStartOffset(int offset) {
		if (offset < 0)
			this.startOffset = 0;
		else
			this.startOffset = offset;
	}

	public void setEndOffset(int offset) {
		if (offset < 0)
			this.endOffset = 0;
		else
			this.endOffset = offset;
	}

	public String toString() {
		return getId() + ": " + builtMessage;
	}

	public String[] getParams() {
		return params;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getResourceName() {
		if (getMessageContributor() instanceof IEGLMessageContributor)
			return ((IEGLMessageContributor) getMessageContributor()).getResourceName();
		return null;
	}
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (o instanceof EGLMessage) {
			EGLMessage msg = (EGLMessage)o;
			return msg.endColumnNumber == endColumnNumber && msg.endLineNumber == endLineNumber && msg.endOffset == endOffset &&
					msg.severity == severity && msg.startColumnNumber == startColumnNumber && msg.startLineNumber == startLineNumber &&
					msg.startOffset == startOffset && equals(msg.id, id) && equals(msg.partName, partName) && equals(msg.builtMessage, builtMessage) &&
					equals(msg.params, params) && equals(msg.messageBundle, messageBundle);
		}
		return false;
	}
	
	private boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}
	
	private boolean equals(Object[] o1, Object[] o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return Arrays.equals(o1, o2);
	}
}

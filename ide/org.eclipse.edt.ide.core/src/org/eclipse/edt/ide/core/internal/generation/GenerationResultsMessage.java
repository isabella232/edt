/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;

import org.eclipse.edt.compiler.internal.interfaces.IEGLComponentMessageContributor;
import org.eclipse.edt.compiler.internal.interfaces.IEGLLocation;
import org.eclipse.edt.compiler.internal.interfaces.IEGLMessageContributor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;

public class GenerationResultsMessage implements IGenerationResultsMessage{

	String resourceName;
	int severity;
	String[] inserts;
	String id;
	int startOffset;
	int endOffset;
	int startLine;

	   
	public GenerationResultsMessage(EGLMessage eglMsg) {
		super();
		if (eglMsg != null) {
			resourceName = eglMsg.getResourceName();
			severity = eglMsg.getSeverity();
			inserts = eglMsg.getParams();
			id = eglMsg.getId();
			startOffset = eglMsg.getStartOffset();
			endOffset = eglMsg.getEndOffset();
			startLine = eglMsg.getStartLine();
		}
		
	}
	
	
	public String[] getInserts() {
		return inserts;
	}


	public String getBuiltMessage() {
		return createEGLMessage().getBuiltMessage();
	}


	public String getBuiltMessageWithLineAndColumn() {
		return createEGLMessage().getBuiltMessageWithLineAndColumn();
	}


	public int getEndOffset() {
		return endOffset;
	}


	public String getId() {
		return id;
	}


	public String getResourceName() {
		return resourceName;
	}


	public int getSeverity() {
		return severity;
	}


	public int getStartLine() {
		return startLine;
	}


	public int getStartOffset() {
		return startOffset;
	}


	public boolean isError() {
		return getSeverity() == EGL_ERROR_MESSAGE;
	}
	
	public boolean isWarning() {
		return getSeverity() == EGL_WARNING_MESSAGE;
	}
	
	public boolean isInformational() {
		return getSeverity() == EGL_INFORMATIONAL_MESSAGE;
	}
	
	private IEGLMessageContributor getContributor() {
		if (resourceName == null) {
			return null;
		}
		return new IEGLMessageContributor() {

			public IEGLLocation getEnd() {
				return getLocation(endOffset, 0);
			}

			public IEGLComponentMessageContributor getMessageContributor() {
				return null;
			}

			public String getResourceName() {
				return resourceName;
			}

			public IEGLLocation getStart() {
				return getLocation(startOffset, startLine);
			}
			
		};
	}
	
	private IEGLLocation getLocation(final int offset, final int line) {
		return new IEGLLocation() {

			public int getColumn() {
				return 0;
			}

			public int getLength() {
				return 0;
			}

			public int getLine() {
				return line;
			}

			public int getOffset() {
				return offset;
			}
			
		};
	}
	
	private EGLMessage createEGLMessage() {
		
		if (severity == EGL_ERROR_MESSAGE) {
			return EGLMessage.createEGLValidationErrorMessage(getId(), getContributor(), getInserts());
		}
		
		if (severity == EGL_WARNING_MESSAGE) {
			return EGLMessage.createEGLValidationWarningMessage(getId(), getContributor(), getInserts());
		}
		return EGLMessage.createEGLValidationInformationalMessage(getId(), getContributor(), getInserts());
		
	}


}

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


/**
 * @author harmon
 *
 * This is a lobotomized version of EGLMessage. It will only return the message String that was given to it, and answer to the severity
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EGLDietMessage extends EGLMessage {
	
	private String messageText;

	public EGLDietMessage(
		int aSeverity,
		String messageText) {
		
		this.severity = aSeverity;
		this.messageText = messageText;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.util.EGLMessage#getBuiltMessage()
	 */
	public String getBuiltMessage() {
		return messageText;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.util.EGLMessage#getBuiltMessageWithLineAndColumn()
	 */
	public String getBuiltMessageWithLineAndColumn() {
		return messageText;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.util.EGLMessage#getBuiltMessageWithoutLineAndColumn()
	 */
	public String getBuiltMessageWithoutLineAndColumn() {
		return messageText;
	}

}

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
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.File;
import java.util.ResourceBundle;

public class SDKProblemRequestor extends SDKSyntaxProblemRequestor {

	String partName;
	File file;
	public SDKProblemRequestor(File file, String partName) {
		super(file, "VAL");//$NON-NLS-1$
		this.file = file;
		this.partName = partName;
		
	}
	
	@Override
	protected String createMessage(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
		String message;
		if(messagesWithLineNumberInserts.contains(new Integer(problemKind))) {
			inserts = shiftInsertsIfNeccesary(problemKind, inserts);
			inserts[0] = partName;
			int lineNumber = getLineNumberOfOffset(startOffset);
			inserts[inserts.length-2] = Integer.toString(lineNumber);
			inserts[inserts.length-1] = file.getAbsolutePath();
			message = super.createMessage(startOffset, endOffset, lineNumber, severity, problemKind, inserts, bundle); 
		}
		else {
			message = super.createMessage(startOffset, endOffset, severity, problemKind, inserts, bundle);
		}
		
		return message;		   
	}
}

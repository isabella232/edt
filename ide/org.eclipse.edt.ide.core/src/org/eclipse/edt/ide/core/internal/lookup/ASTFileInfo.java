/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

public class ASTFileInfo extends FileInfo implements IASTFileInfo{

	private List errors = null;
	
	public void setErrors(List errors){
		this.errors = errors;
	}

	public void accept(IProblemRequestor problemRequestor){
		for (Iterator iter = errors.iterator(); iter.hasNext();) {
			FileInfoError error = (FileInfoError) iter.next();
			problemRequestor.acceptProblem(error.offset, error.offset + error.length, error.type, error.inserts);
		}
	}
}

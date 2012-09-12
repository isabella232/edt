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
package org.eclipse.edt.ide.core.internal.compiler.workingcopy;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.Node;

public class WorkingCopyCompilationResult {

	private Node boundPart = null;
	private IPartBinding partBinding = null;
	private IFile declaredFile = null;
	
	public WorkingCopyCompilationResult(Node part,IPartBinding binding ,IFile file) {
		boundPart = part;
		declaredFile = file;
		partBinding = binding;
	}

	public Node getBoundPart() {
		return boundPart;
	}

	public IFile getDeclaringFile() {
		return declaredFile;
	}

	public IPartBinding getPartBinding() {
		return partBinding;
	}
}

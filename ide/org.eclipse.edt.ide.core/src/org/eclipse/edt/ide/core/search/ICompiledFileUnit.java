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
package org.eclipse.edt.ide.core.search;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;

public interface ICompiledFileUnit {
	File getFile();
	FileBinding getFileBinding();
	List getFileParts();
	HashMap getReferencedFiles();
	IFile getContainingFile(Node part);
	Node getPartAST(IBinding partBinding);
	Node getPartInContextAST(FunctionContainerBinding functionContainerBinding, TopLevelFunctionBinding functionBinding);
	Node[] getAllParts();
	List<IBinding> getIBindingsFromIR();
}

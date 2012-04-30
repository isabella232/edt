/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.FileBindingCompletor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */
public class FileBinder extends AbstractBinder {

    private FileBinding fileBinding;
    private IProblemRequestor problemRequestor;
    private Scope scope;
	
    public FileBinder(FileBinding fileBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, fileBinding.getPackageName(), dependencyRequestor, compilerOptions);
        this.fileBinding = fileBinding;
        this.problemRequestor = problemRequestor;
        this.scope = scope;
    }
    
    public boolean visit(File file){
    	file.accept(new FileBindingCompletor(scope, fileBinding, dependencyRequestor, problemRequestor, compilerOptions));
    	
    	return false;
    }
}

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
package org.eclipse.edt.compiler.internal.core.dependency;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;


/**
 * @author svihovec
 *
 */
public class NullDependencyRequestor implements IDependencyRequestor {

	private static final NullDependencyRequestor INSTANCE = new NullDependencyRequestor();
	
	private NullDependencyRequestor(){
	}
	
	public static NullDependencyRequestor getInstance(){
		return INSTANCE;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordSimpleName(java.lang.String)
	 */
	public void recordSimpleName(String simpleName) {}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordPartBinding(org.eclipse.edt.compiler.binding.IPartBinding)
	 */
	public void recordTypeBinding(ITypeBinding typeBinding) {}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordFileDependency(java.lang.String[], java.lang.String)
	 */
	public void recordFileDependency(String[] packageName, String fileName) {}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordName(org.eclipse.edt.compiler.core.ast.Name)
	 */
	public void recordName(Name name) {}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordBinding(org.eclipse.edt.compiler.binding.IBinding)
	 */
	public void recordBinding(IBinding binding) {}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordPackageBinding(org.eclipse.edt.compiler.binding.IPackageBinding)
	 */
	public void recordPackageBinding(IPackageBinding result) {}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordFunctionBinding(org.eclipse.edt.compiler.binding.IFunctionBinding)
	 */
	public void recordTopLevelFunctionBinding(IFunctionBinding binding) {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#recordFunctionContainerScope(org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope)
	 */
	public void recordFunctionContainerScope(FunctionContainerScope scope) {}

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#stopRecordingTopLevelFunctionBindings()
     */
    public void stopRecordingTopLevelFunctionBindings() {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor#startRecordingTopLevelFunctionBindings()
     */
    public void startRecordingTopLevelFunctionBindings() {
        // TODO Auto-generated method stub
        
    }

}

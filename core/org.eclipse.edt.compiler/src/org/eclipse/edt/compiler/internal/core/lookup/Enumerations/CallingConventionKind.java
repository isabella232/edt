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
package org.eclipse.edt.compiler.internal.core.lookup.Enumerations;

import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationTypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class CallingConventionKind extends Enumeration{
    public final static CallingConventionKind INSTANCE = new CallingConventionKind();
	public final static int TYPE_CONSTANT = CALLINGCONVENTIONKIND;

	public final static int I4GL_CONSTANT = 1;
	public final static int LIBRARY_CONSTANT = 2;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("CallingConventionKind"), CALLINGCONVENTIONKIND);
	public final static SystemEnumerationDataBinding I4GL = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("I4GL"), null, TYPE, I4GL_CONSTANT);
	public final static SystemEnumerationDataBinding LIBRARY = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("library"), null, TYPE, LIBRARY_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(I4GL);
		TYPE.addEnumeration(LIBRARY);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

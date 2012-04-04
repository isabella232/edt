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


public class ScopeKind extends Enumeration{
    public final static ScopeKind INSTANCE = new ScopeKind();
	public final static int TYPE_CONSTANT = SCOPEKIND;

	public final static int SESSION_CONSTANT = 1;
	public final static int REQUEST_CONSTANT = 2;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("ScopeKind"), SCOPEKIND);
	public final static SystemEnumerationDataBinding SESSION = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("session"), null, TYPE, SESSION_CONSTANT);
	public final static SystemEnumerationDataBinding REQUEST = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("request"), null, TYPE, REQUEST_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(SESSION);
		TYPE.addEnumeration(REQUEST);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

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


public class SignKind extends Enumeration{
    public final static SignKind INSTANCE = new SignKind();
	public final static int TYPE_CONSTANT = SIGNKIND;

	public final static int LEADING_CONSTANT = 1;
	public final static int NONE_CONSTANT = 2;
	public final static int PARENS_CONSTANT = 3;
	public final static int TRAILING_CONSTANT = 4;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("SignKind"), SIGNKIND);
	public final static SystemEnumerationDataBinding LEADING = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("leading"), null, TYPE, LEADING_CONSTANT);
	public final static SystemEnumerationDataBinding NONE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("none"), null, TYPE, NONE_CONSTANT);
	public final static SystemEnumerationDataBinding PARENS = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("parens"), null, TYPE, PARENS_CONSTANT);
	public final static SystemEnumerationDataBinding TRAILING = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("trailing"), null, TYPE, TRAILING_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(LEADING);
		TYPE.addEnumeration(NONE);
		TYPE.addEnumeration(PARENS);
		TYPE.addEnumeration(TRAILING);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

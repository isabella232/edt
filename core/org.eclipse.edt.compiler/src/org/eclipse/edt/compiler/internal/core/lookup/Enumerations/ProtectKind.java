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


public class ProtectKind extends Enumeration{
    public final static ProtectKind INSTANCE = new ProtectKind();
	public final static int TYPE_CONSTANT = PROTECTKIND;

	public final static int SKIP_CONSTANT = 1;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("ProtectKind"), PROTECTKIND);
	public final static SystemEnumerationDataBinding SKIP = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("skip"), null, TYPE, SKIP_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(SKIP);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

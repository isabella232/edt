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


public class IndexOrientationKind extends Enumeration{
    public final static IndexOrientationKind INSTANCE = new IndexOrientationKind();
	public final static int TYPE_CONSTANT = INDEXORIENTATIONKIND;

	public final static int ACROSS_CONSTANT = 1;
	public final static int DOWN_CONSTANT = 2;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("IndexOrientationKind"), INDEXORIENTATIONKIND);
	public final static SystemEnumerationDataBinding ACROSS = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("across"), null, TYPE, ACROSS_CONSTANT);
	public final static SystemEnumerationDataBinding DOWN = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("down"), null, TYPE, DOWN_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(ACROSS);
		TYPE.addEnumeration(DOWN);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

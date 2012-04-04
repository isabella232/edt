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


public class SelectTypeKind extends Enumeration{
    public final static SelectTypeKind INSTANCE = new SelectTypeKind();
	public final static int TYPE_CONSTANT = SELECTTYPEKIND;

	public final static int INDEX_CONSTANT = 1;
	public final static int VALUE_CONSTANT = 2;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("SelectTypeKind"), SELECTTYPEKIND);
	public final static SystemEnumerationDataBinding INDEX = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("index"), null, TYPE, INDEX_CONSTANT);
	public final static SystemEnumerationDataBinding VALUE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("value"), null, TYPE, VALUE_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(INDEX);
		TYPE.addEnumeration(VALUE);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

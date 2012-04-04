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


public class OrderingKind extends Enumeration{
    public final static OrderingKind INSTANCE = new OrderingKind();
	public final static int TYPE_CONSTANT = ORDERINGKIND;

	public final static int BYKEY_CONSTANT = 1;
	public final static int BYINSERTION_CONSTANT = 2;
	public final static int NONE_CONSTANT = 3;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("OrderingKind"), ORDERINGKIND);
	public final static SystemEnumerationDataBinding BYKEY = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("byKey"), null, TYPE, BYKEY_CONSTANT);
	public final static SystemEnumerationDataBinding BYINSERTION = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("byInsertion"), null, TYPE, BYINSERTION_CONSTANT);
	public final static SystemEnumerationDataBinding NONE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("none"), null, TYPE, NONE_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(BYKEY);
		TYPE.addEnumeration(BYINSERTION);
		TYPE.addEnumeration(NONE);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

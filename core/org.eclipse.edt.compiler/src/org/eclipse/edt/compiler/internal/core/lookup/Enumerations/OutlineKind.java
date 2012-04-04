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


public class OutlineKind extends Enumeration{
    public final static OutlineKind INSTANCE = new OutlineKind();
	public final static int TYPE_CONSTANT = OUTLINEKIND;

	public final static int BOTTOM_CONSTANT = 1;
	public final static int LEFT_CONSTANT = 2;
	public final static int RIGHT_CONSTANT = 3;
	public final static int TOP_CONSTANT = 4;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("OutlineKind"), OUTLINEKIND);
	public final static SystemEnumerationDataBinding BOTTOM = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("bottom"), null, TYPE, BOTTOM_CONSTANT);
	public final static SystemEnumerationDataBinding LEFT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("left"), null, TYPE, LEFT_CONSTANT);
	public final static SystemEnumerationDataBinding RIGHT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("right"), null, TYPE, RIGHT_CONSTANT);
	public final static SystemEnumerationDataBinding TOP = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("top"), null, TYPE, TOP_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(BOTTOM);
		TYPE.addEnumeration(LEFT);
		TYPE.addEnumeration(RIGHT);
		TYPE.addEnumeration(TOP);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}

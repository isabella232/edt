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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.ISystemLibrary;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public abstract class FixedRecordBinding extends FixedStructureBinding implements IRecordBinding {
	
	protected FixedRecordBinding(FixedRecordBinding old) {
		super(old);
	}
	
	public static final SystemVariableBinding RESOURCEASSOCIATION =
		new SystemVariableBinding(
			InternUtil.internCaseSensitive("resourceAssociation"),
			PrimitiveTypeBinding.getInstance(Primitive.CHAR, 300),
			ISystemLibrary.ResourceAssociation_var,
			false);
	
	public FixedRecordBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
}

/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.egl2mof.MofConversion;
import org.eclipse.edt.mof.egl.utils.IRUtils;


/**
 * Special type that is the type of the <code>NullLiteral</code> expression <code>null</code>
 *
 */
public interface NullType extends Type {
	NullType INSTANCE = (NullType)((EClass)IRUtils.getType(MofConversion.Type_NullType)).newInstance();
}

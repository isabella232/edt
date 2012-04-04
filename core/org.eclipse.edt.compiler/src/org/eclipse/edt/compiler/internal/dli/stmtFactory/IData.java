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
package org.eclipse.edt.compiler.internal.dli.stmtFactory;

import org.eclipse.edt.compiler.binding.ITypeBinding;

/**
 * @author Harmon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IData {
    boolean isPSBRecord();
    boolean isDLISegmentRecord();
    String getName();
    void setName(String name);
    ITypeBinding getType();
    void setType(ITypeBinding type);

}

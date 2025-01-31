/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.io;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.io.IIOBufferWriter;


/**
 * @author svihovec
 *
 */
public interface IIOBufferWriterFactory {

	IIOBufferWriter getWriter(IPath bufferPath);

}

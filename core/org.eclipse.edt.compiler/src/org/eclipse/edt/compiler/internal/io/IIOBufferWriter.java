/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.io;

import java.io.IOException;


/**
 * @author svihovec
 *
 */
public interface IIOBufferWriter {

	public void beginWriting() throws IOException;
	public void writeEntry(String entryName, Object value) throws IOException;
	public void finishWriting() throws IOException;


	// Notify the writer that all entries have been removed - there is nothing to write
	public void allEntriesRemoved();	
}

/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface EGLResource {

	boolean exists();
	boolean isFile();
	File toFile();
	InputStream getInputStream() throws IOException;
	String getName();
	String getFullName();
	long getLocalTimeStamp();
	
	
}

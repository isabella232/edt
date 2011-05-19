/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.requestors;


import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public interface CommandRequestor {
	String[] findFiles(String fileName);
	String getFileContents(String fileName) throws Exception;
	String getXmlFileContents(String fileName) throws Exception;
	String getFullFileName(String fileName);
	boolean isWorkbenchAvailable();
	String getDate(String fileName);
	String getTime(String fileName);
	boolean folderExists(String folderName, String fileNameContext);
	InputSource getInputSource(String fileName) throws Exception;
	String getProjectName(String fileName);
	boolean getVAGCompatiblity();
	int getGenerationMode();
	boolean isValidProjectName(String name);
	Boolean isJavaProject(String name);
	Boolean isWebProject(String name);
	Boolean projectSupportsJaxWS(String name);
	Boolean projectContainsJaxRPC(String name);
	Boolean projectContainsJaxWS(String name, boolean hasServices, boolean hasWebBindings);

}

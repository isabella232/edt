/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.changes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;

public class CreateTextFileChange extends CreateFileChange {
	
	private final String fTextType;
	
	public CreateTextFileChange(IPath path, String source, String encoding, String textType) {
		super(path, source, encoding);
		fTextType= textType;
	}
	
	public String getTextType() {
		return fTextType;
	}
	
	public String getCurrentContent() throws EGLModelException {
		IFile file= getOldFile(new NullProgressMonitor());
		if (! file.exists())
			return ""; //$NON-NLS-1$
		InputStream stream= null;
		try{
			stream= file.getContents();
			String c= readString(stream);
			return (c == null) ? "": c; //$NON-NLS-1$
		} catch (CoreException e){
			throw new EGLModelException(e, IEGLModelStatusConstants.CORE_EXCEPTION);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException x) {
			}
		}
	}
	
	public String getPreview() {
		return getSource();
	}
	
	public static String readString(InputStream is) {
		if (is == null)
			return null;
		BufferedReader reader= null;
		try {
			StringBuffer buffer= new StringBuffer();
			char[] part= new char[2048];
			int read= 0;
			reader= new BufferedReader(new InputStreamReader(is, "8859_1")); //$NON-NLS-1$

			while ((read= reader.read(part)) != -1)
				buffer.append(part, 0, read);

			return buffer.toString();

		} catch (IOException ex) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
				}
			}
		}
		return null;
	}
}


/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.util.SimpleLineTracker;


public class SDKContext extends Context{

	private File file;
	
	private SimpleLineTracker lineTracker;

	
	public SDKContext(File file, ICompiler compiler) {
		super(file.getAbsolutePath(), file.getName(), compiler);
		this.file = file;
	}

	private File getFile() {
		if (file == null) {
			if (getAbsolutePath() != null) {
		        file = new File(getAbsolutePath());
			}
		}
		return file;
	}

	protected String getFileContents() {
		try {
			if (getFile() == null) {
				return null;
			}
			
			FileReader reader = new FileReader(getFile());
			int length;
			char[] fileContents = new char[length = (int) getFile().length()];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len < length)) {
				len += readSize;
				readSize = reader.read(fileContents, len, length - len);
			}
			reader.close();
			if (len != length)
				System.arraycopy(fileContents, 0, (fileContents = new char[len]), 0, len);
			return new String(fileContents);
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

	}

	private SimpleLineTracker getLineTracker() {
		if (lineTracker == null) {
			lineTracker = new SimpleLineTracker(getFileContents());
		}
		return lineTracker;
	}	

	public int getLineNumber(Node node) {
		int[] lineOffsets = getLineTracker().getLineOffsets();
		for (int i = 0; i < lineOffsets.length; i++) {
			if (lineOffsets[i] == node.getOffset()) {
				return i;
			}
			if (lineOffsets[i] > node.getOffset()) {
				return i - 1;
			}
		}
		return 0;
	}
	
    public String getAbsolutePath(String fileName) {

        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            return fileName;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

}

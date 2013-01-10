/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.eclipse.edt.compiler.internal.eglar.EglarManifest;

public class EglarOutputStream extends JarOutputStream {
	
	public EglarOutputStream(OutputStream out, EglarManifest man) throws IOException {
		super(out);
		addManEntry(man);
	}

	public EglarOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	public void addManEntry(EglarManifest man) throws IOException {
		if (man == null) {
			throw new NullPointerException("man");
		}
		ZipEntry e = new ZipEntry(JarFile.MANIFEST_NAME);
		putNextEntry(e);
		man.write(new BufferedOutputStream(this));
		closeEntry();
	}
}

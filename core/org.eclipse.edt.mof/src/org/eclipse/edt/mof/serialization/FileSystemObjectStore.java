/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.serialization;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class FileSystemObjectStore extends AbstractObjectStore implements ObjectStore {
	static final String MOFBIN = ".mofbin";
	static final String MOFXML = ".mofxml"; 

	File root;
	String fileExtension;
	
	
	public FileSystemObjectStore(File root, IEnvironment env) {
		super(env);
		this.root = root;
	}
	
	public FileSystemObjectStore(File root, IEnvironment env, String storageFormat) {
		super(env, storageFormat);
		this.root = root;
		this.fileExtension = storageFormat == ObjectStore.XML ? MOFXML : MOFBIN;
	}

	public FileSystemObjectStore(File root, IEnvironment env, String storageFormat, String fileExtension) {
		super(env, storageFormat);
		this.root = root;
		this.fileExtension = fileExtension;
	}
	
	public Deserializer createDeserializer(String typeSignature) {
		String path = typeSignature.replace('.', '/') + getFileExtension();
		Deserializer deserializer = null;
		File typeFile = new File(root, path);
		try {
			FileInputStream fileIn = new FileInputStream(typeFile);
			byte[] bytes = new byte[fileIn.available()];
			fileIn.read(bytes);
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			deserializer = factory.createDeserializer(in, env);
			return deserializer;
		} catch (Exception e) {
			return null;
		}	
	}

	public void store(String typeSignature, Object obj) {
		if (!(obj instanceof byte[])) throw new IllegalArgumentException("Object not of type: byte[]");
		String ext = getFileExtension();
		int i = typeSignature.lastIndexOf('.');
		String packagePath = i != -1 ? typeSignature.substring(0, i).replace('.', '/') : null;
		String path = typeSignature.replace('.', '/')+ ext;
		if (packagePath != null) {
			File folder = new File(root, packagePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
		try {
			File outFile = new File(root, path);
			FileOutputStream file = new FileOutputStream(outFile);
			String data = new String((byte[])obj, "iso-8859-1");
			OutputStreamWriter out = new OutputStreamWriter(file, "iso-8859-1");
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public void primRemove(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(String key) {
		key = removeSchemeFromKey(key);
		String path = key.replace('.', '/')+ getFileExtension();
		File file = new File(root, path);
		return file.exists();
	}
	
	@Override
	public long lastModified(String key) {
		key = removeSchemeFromKey(key);
		String path = key.replace('.', '/')+ getFileExtension();
		File file = new File(root, path);
		if (file.exists()) {
			return file.lastModified();
		}
		else {
			return -1;
		}

	}
	
	public String getFileExtension() {
		if (fileExtension == null) {
			fileExtension = storageFormat.equals(BINARY) ? MOFBIN : MOFXML;
		}
		return fileExtension;
	}

}


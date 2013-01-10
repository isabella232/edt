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
package org.eclipse.edt.mof.serialization;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


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
		File typeFile = new File(root, path);

		try {
			byte[] bytes = null;
			FileInputStream fileIn = null;
			try {
				fileIn = new FileInputStream(typeFile);
				bytes = new byte[fileIn.available()];
				fileIn.read(bytes);
			} finally {
				if (fileIn != null) {
					fileIn.close();
				}
			}
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			return factory.createDeserializer(in, env);
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
		
		File folder;
		if (packagePath != null) {
			folder = new File(root, packagePath);
		}else{
			folder = root;
		}
		
		if (!folder.exists()) {
			folder.mkdirs();
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
	
	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		if (!containsPkg(pkg)) {
			return new ArrayList<String>();
		}
		String path = pkg.replace('.', '/');
		File pkgDir = new File(root, path);
		return getAllKeysFromPkg(pkgDir, pkg, includeSubPkgs);
		
	}
	
	private List<String> getAllKeysFromPkg(File pkgDir, String pkg,  boolean includeSubPkgs) {

		List<String> list = new ArrayList<String>();
		File[] contents = pkgDir.listFiles();
		if (contents == null) {
			return list;
		}
		for (File file : contents) {
			if (file.isDirectory()) {
				if (includeSubPkgs) {
					String subPkgName;
					if (pkg.length() > 0) {
						subPkgName = pkg + "." + file.getName();
					}
					else {
						subPkgName = file.getName();
					}
					list.addAll(getAllKeysFromPkg(file, subPkgName, includeSubPkgs));
				}
			}
			else {
				if (getFileExtension().equals(getFileExtension(file))) {
					if (pkg.length() > 0)
						list.add(getScheme() + pkg + "." + getFileName(file));
					else
						list.add(getScheme() + getFileName(file));						
				}
			}
		}
		
		return list;
	}
	
	private String getFileExtension(File file) {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if (index < 0) {
			return null;
		}
		
		return name.substring(index);
	}

	private String getFileName(File file) {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if (index < 0) {
			return name;
		}
		
		return name.substring(0, index);
	}

	@Override
	public boolean containsKey(String key) {
		key = removeSchemeFromKey(key);
		String path = key.replace('.', '/')+ getFileExtension();
		File file = new File(root, path);
		return file.exists();
	}
	
	private boolean containsPkg(String pkg) {
		String path = pkg.replace('.', '/');
		File file = new File(root, path);
		return file.exists() && file.isDirectory();
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
	
	private String getScheme() {
		if (getFileExtension().equals(MOFBIN) || getFileExtension().equals(MOFXML))  {
			return "";
		}
		else {
			return "egl:";
		}
	}
	
	public String getFileExtension() {
		if (fileExtension == null) {
			fileExtension = storageFormat.equals(BINARY) ? MOFBIN : MOFXML;
		}
		return fileExtension;
	}
	
	@Override
	public String toString() {
		// For easier debugging.
		return "FileSystemObjectStore root=" + root + " scheme=" + getScheme();
	}
}


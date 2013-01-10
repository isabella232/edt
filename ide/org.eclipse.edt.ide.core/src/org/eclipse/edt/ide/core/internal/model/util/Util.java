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
package org.eclipse.edt.ide.core.internal.model.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.internal.model.EGLModelResources;

public class Util {

	public static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	public static char[] LINE_SEPARATOR_CHARS = LINE_SEPARATOR.toCharArray();
	public final static char[] SUFFIX_class = ".class".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_CLASS = ".CLASS".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_java = ".java".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_JAVA = ".JAVA".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_jar = ".jar".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_JAR = ".JAR".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_zip = ".zip".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_ZIP = ".ZIP".toCharArray(); //$NON-NLS-1$
		
	private static final int DEFAULT_READING_SIZE = 8192;
	
	/**
	 * Returns the given bytes as a char array using a given encoding (null means platform default).
	 */
	public static char[] bytesToChar(byte[] bytes, String encoding) throws IOException {

		return getInputStreamAsCharArray(new ByteArrayInputStream(bytes), bytes.length, encoding);

	}
	/**
	 * Returns the contents of the given file as a byte array.
	 * @throws IOException if a problem occured reading the file.
	 */
	public static byte[] getFileByteContent(File file) throws IOException {
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
			return getInputStreamAsByteArray(stream, (int) file.length());
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	/**
	 * Returns the contents of the given file as a char array.
	 * When encoding is null, then the platform default one is used
	 * @throws IOException if a problem occured reading the file.
	 */
	public static char[] getFileCharContent(File file, String encoding) throws IOException {
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
			return Util.getInputStreamAsCharArray(stream, (int) file.length(), encoding);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	/**
	 * Returns the given input stream's contents as a byte array.
	 * If a length is specified (ie. if length != -1), only length bytes
	 * are returned. Otherwise all bytes in the stream are returned.
	 * Note this doesn't close the stream.
	 * @throws IOException if a problem occured reading the stream.
	 */
	public static byte[] getInputStreamAsByteArray(InputStream stream, int length)
		throws IOException {
		byte[] contents;
		if (length == -1) {
			contents = new byte[0];
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE);  // read at least 8K
				
				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(
						contents,
						0,
						contents = new byte[contentsLength + amountRequested],
						0,
						contentsLength);
				}

				// read as many bytes as possible
				amountRead = stream.read(contents, contentsLength, amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1); 

			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(
					contents,
					0,
					contents = new byte[contentsLength],
					0,
					contentsLength);
			}
		} else {
			contents = new byte[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual read size.
				len += readSize;
				readSize = stream.read(contents, len, length - len);
			}
		}

		return contents;
	}
	/**
	 * Returns the given input stream's contents as a character array.
	 * If a length is specified (ie. if length != -1), only length chars
	 * are returned. Otherwise all chars in the stream are returned.
	 * Note this doesn't close the stream.
	 * @throws IOException if a problem occured reading the stream.
	 */
	public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding)
		throws IOException {
		InputStreamReader reader = null;
		reader = encoding == null
					? new InputStreamReader(stream)
					: new InputStreamReader(stream, encoding);
		char[] contents;
		if (length == -1) {
			contents = CharOperation.NO_CHAR;
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE);  // read at least 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(
						contents,
						0,
						contents = new char[contentsLength + amountRequested],
						0,
						contentsLength);
				}

				// read as many chars as possible
				amountRead = reader.read(contents, contentsLength, amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(
					contents,
					0,
					contents = new char[contentsLength],
					0,
					contentsLength);
			}
		} else {
			contents = new char[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual read size.
				len += readSize;
				readSize = reader.read(contents, len, length - len);
			}
			// See PR 1FMS89U
			// Now we need to resize in case the default encoding used more than one byte for each
			// character
			if (len != length)
				System.arraycopy(contents, 0, (contents = new char[len]), 0, len);
		}

		return contents;
	}
	
	/**
	 * Returns the contents of the given zip entry as a byte array.
	 * @throws IOException if a problem occured reading the zip entry.
	 */
	public static byte[] getZipEntryByteContent(ZipEntry ze, ZipFile zip)
		throws IOException {

		InputStream stream = null;
		try {
			stream = new BufferedInputStream(zip.getInputStream(ze));
			return getInputStreamAsByteArray(stream, (int) ze.getSize());
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	/**
	 * Returns true iff str.toLowerCase().endsWith(".jar") || str.toLowerCase().endsWith(".zip")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isArchiveFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_JAR.length;
		if (nameLength < suffixLength) return false;

		// try to match as JAR file
		for (int i = 0; i < suffixLength; i++) {
			char c = name.charAt(nameLength - i - 1);
			int suffixIndex = suffixLength - i - 1;
			if (c != SUFFIX_jar[suffixIndex] && c != SUFFIX_JAR[suffixIndex]) {

				// try to match as ZIP file
				suffixLength = SUFFIX_ZIP.length;
				if (nameLength < suffixLength) return false;
				for (int j = 0; j < suffixLength; j++) {
					c = name.charAt(nameLength - j - 1);
					suffixIndex = suffixLength - j - 1;
					if (c != SUFFIX_zip[suffixIndex] && c != SUFFIX_ZIP[suffixIndex]) return false;
				}
				return true;
			}
		}
		return true;		
	}	
	/**
	 * Returns true iff str.toLowerCase().endsWith(".class")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isClassFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_CLASS.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0; i < suffixLength; i++) {
			char c = name.charAt(nameLength - i - 1);
			int suffixIndex = suffixLength - i - 1;
			if (c != SUFFIX_class[suffixIndex] && c != SUFFIX_CLASS[suffixIndex]) return false;
		}
		return true;		
	}	
	/**
	 * Returns true iff str.toLowerCase().endsWith(".class")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isClassFileName(char[] name) {
		int nameLength = name == null ? 0 : name.length;
		int suffixLength = SUFFIX_CLASS.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name[offset + i];
			if (c != SUFFIX_class[i] && c != SUFFIX_CLASS[i]) return false;
		}
		return true;		
	}		
	/**
	 * Returns true iff str.toLowerCase().endsWith(".java")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isJavaFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_JAVA.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0; i < suffixLength; i++) {
			char c = name.charAt(nameLength - i - 1);
			int suffixIndex = suffixLength - i - 1;
			if (c != SUFFIX_java[suffixIndex] && c != SUFFIX_JAVA[suffixIndex]) return false;
		}
		return true;		
	}
	/**
	 * Returns true iff str.toLowerCase().endsWith(".java")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isJavaFileName(char[] name) {
		int nameLength = name == null ? 0 : name.length;
		int suffixLength = SUFFIX_JAVA.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name[offset + i];
			if (c != SUFFIX_java[i] && c != SUFFIX_JAVA[i]) return false;
		}
		return true;		
	}
	
	/**
	 * Name of file containing project EGLPath
	 */
	public static final String EGLPATH_FILENAME = ".eglPath"; //$NON-NLS-1$
	/*
	 * Value of the project's raw EGLPath if the .EGLPath file contains invalid
	 * entries.
	 */
	public static final IEGLPathEntry[] INVALID_EGLPATH = new IEGLPathEntry[0];
	
	public static boolean isBinaryProject(File file) {
		//TODO Rocky, as did in the isReadOnly() method in EGLProject file.
		//1) Check if can find .readonly file 2)Check if can find the outputLocation in the ".eglpath" file.
		if(!file.isDirectory()) {
			return false;
		}
		File[] files = file.listFiles();
		if(files == null) {
			return false;
		}
		for(File tempFile : files) {
			if(".readonly".equalsIgnoreCase(tempFile.getName())) {
				return true;
			}
		}
		return isValidEGLFileAndNoOutputEntry(file);
	}
	
	public static boolean isBinaryProject(IProject proj){
		return isBinaryProject(new File(proj.getLocation().toString()));
	}
	
	public static String[] getEGLSourceFolders(File file){
		List<String> eglSourceFoldersList = new ArrayList<String>();
		Element cpElement = getEGLPathRoot(file);
		if(cpElement == null)
			return null;
		try{
			if (!cpElement.getNodeName().equalsIgnoreCase("eglpath")) { //$NON-NLS-1$
				throw new IOException(EGLModelResources.fileBadFormat);
			}
			NodeList list = cpElement.getElementsByTagName("eglpathentry"); //$NON-NLS-1$
			int length = list.getLength();

			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					//e.g. <eglpathentry kind="src" path="/EGLSource"/>
					if(((Element)node).hasAttribute("kind")) {
						String kindAttr = ((Element)node).getAttribute("kind");
						if("src".equalsIgnoreCase(kindAttr)) {
							if(((Element)node).hasAttribute("path")){
								String pathAttr = ((Element)node).getAttribute("path");
								if(!pathAttr.startsWith("/")){
									eglSourceFoldersList.add(pathAttr);
								}
							}
						}
					}
				}
			}
			
		} catch (IOException e) {
			// bad format
			return null;
		}
		return eglSourceFoldersList.toArray(new String[eglSourceFoldersList.size()]);
	}
	
	private static Element getEGLPathRoot(File file){
		String xmlPath = file.getAbsolutePath() + "/" + EGLPATH_FILENAME;
		Element cpElement;
		try {
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(xmlPath));
			byte[] bytes = org.eclipse.edt.ide.core.internal.model.util.Util.getInputStreamAsByteArray(inputStream, -1);
			String contents = new String(bytes, "UTF-8");
			StringReader reader = new StringReader(contents);
			
			try {
				DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
			} catch (SAXException e) {
				throw new IOException(EGLModelResources.fileBadFormat);
			} catch (ParserConfigurationException e) {
				throw new IOException(EGLModelResources.fileBadFormat);
			} finally {
				reader.close();
			}
		}
		catch (IOException e) {
			// bad format
			return null;
		} catch (Exception e) {
			// failed creating CP entries from file
			return null;
		}		
		return cpElement;
			
	}
	
	/**
	 * Return true only if the eglpath is a valid eglpath file, and 
	 */
	private static boolean isValidEGLFileAndNoOutputEntry(File file) {
		Element cpElement = getEGLPathRoot(file);
		if(cpElement == null)
			return false;
//		String xmlPath = file.getAbsolutePath() + "/" + EGLPATH_FILENAME;
//		if (xmlPath == null)
//			return false;
		try {
//			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(xmlPath));
//			byte[] bytes = com.ibm.etools.egl.model.internal.core.util.Util.getInputStreamAsByteArray(inputStream, -1);
//			String contents = new String(bytes, "UTF-8");
//			if (contents == null)
//				return false;
//			StringReader reader = new StringReader(contents);
//			Element cpElement;
//			try {
//				DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//				cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
//			} catch (SAXException e) {
//				throw new IOException(EGLModelResources.fileBadFormat);
//			} catch (ParserConfigurationException e) {
//				throw new IOException(EGLModelResources.fileBadFormat);
//			} finally {
//				reader.close();
//			}

			if (!cpElement.getNodeName().equalsIgnoreCase("eglpath")) { //$NON-NLS-1$
				throw new IOException(EGLModelResources.fileBadFormat);
			}
			NodeList list = cpElement.getElementsByTagName("eglpathentry"); //$NON-NLS-1$
			int length = list.getLength();

			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					if(((Element)node).hasAttribute("kind")) {
						String kindAttr = ((Element)node).getAttribute("kind");
						if("output".equalsIgnoreCase(kindAttr)) {
							return false;
						}
					}
				}
			}
		} catch (IOException e) {
			// bad format
			return false;
//		} catch (Exception e) {
//			// failed creating CP entries from file
//			return false;
		}
		return true;
	}	
}

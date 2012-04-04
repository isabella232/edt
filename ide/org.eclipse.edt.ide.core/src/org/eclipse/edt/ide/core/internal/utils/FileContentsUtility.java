/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.utils;

import java.io.*;

import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.*;

public class FileContentsUtility {

	
	private static class SAXEncodingDetector extends DefaultHandler {

	    private String encoding;
	    private Locator2 locator;
		
	    public static String getEncoding(String fileName) throws SAXException, IOException {
	        XMLReader parser = XMLReaderFactory.createXMLReader();
	        SAXEncodingDetector handler = new SAXEncodingDetector();
	        parser.setContentHandler(handler);
	        try {	        	
	        	parser.parse(new File(fileName).toURI().toURL().toExternalForm());
	        } 
	        catch (SAXException ex) {
		        return handler.encoding;
	        }
	        catch (Exception ex) {
	        	return null;
	        }
	        return null;
	    }
	    	    
	    @Override
	    public void setDocumentLocator(Locator locator) {
	        if (locator instanceof Locator2) {
	            this.locator = (Locator2) locator;
	        }
	        else {
	            this.encoding = null;
	        }
	    }
	    
	    @Override
	    public void startDocument() throws SAXException {
	        if (locator != null) {
	            this.encoding = locator.getEncoding();
	        }
	        throw new SAXException("Kill Me Now");
	    }
	    
	}  

	
	public static String getFileContents(String fileName) throws java.lang.Exception {
		File file = new File(getFullFileName(fileName));
		Reader reader = getFileReader(file);
		int length;
		char[] fileContents = new char[length = (int) file.length()];
		int len = 0;
		int readSize = 0;
		while ((readSize != -1) && (len != length)) {
			len += readSize;
			readSize = reader.read(fileContents, len, length - len);
		}
		reader.close();
		// Now we need to resize in case the default encoding used more than one
		// byte for each
		// character
		if (len != length)
			System.arraycopy(fileContents, 0, (fileContents = new char[len]), 0, len);
		return new String(fileContents);
	}
	
	public static String getFileContents(String fileName, String encoding) throws java.lang.Exception{
		InputStream is = new FileInputStream(new File(fileName));
		byte[] bytes = new byte[is.available()];
		is.read(bytes);
		is.close();

		is = new ByteArrayInputStream(bytes);
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(is, encoding));

		return buildStringBuffer(inputReader).toString();
	}

	private static StringBuffer buildStringBuffer(BufferedReader inputReader) {
		StringBuffer s = new StringBuffer();
		try {
			char cbuf[] = new char[4096];
			int length = 0;
			while ((length = inputReader.read(cbuf)) >= 0) {
				s.append(cbuf, 0, length);
			}
		} catch (IOException e) {
		}
		return s;
	}

	
	public static String getFileContentsFromXML(String fileName) throws java.lang.Exception{
		String encoding = new SAXEncodingDetector().getEncoding(getFullFileName(fileName));
		if (encoding == null) {
			return getFileContents(fileName);
		}
		
		try{
			return getFileContents(fileName, encoding);
		}
		catch (java.lang.Exception ex) {
			return getFileContents(fileName);
		}
	}

	public static String getFullFileName(String fileName) {

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

	protected static Reader getFileReader(File file) throws Exception {
		return new FileReader(file);
	}
	
}

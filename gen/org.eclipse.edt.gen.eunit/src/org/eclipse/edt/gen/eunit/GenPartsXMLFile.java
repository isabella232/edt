/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.eunit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

public class GenPartsXMLFile {
	/**
	 * example xml file
		<?xml version="1.0" encoding="UTF-8"?>
		  <partsGroup>
	 		<part name="fvt.baseStatements.TestWhileStatementLib"/>
			<part name="fvt.baseStatements.TestForStatementLib"/>
			<part name="fvt.baseStatements.TestForRecordBin"/>
		</partsGroup>
	*/	
	
	Document doc;	

	private File file; 
	
	private static String ELEMENT_TAR_FOLDER = "partsGroup";
	private static String ELEMENT_PART = "part";
	private static String ATTR_PART_NAME = "name";
	
	public GenPartsXMLFile(String fullPath) {
		setPath(fullPath);
		initialize();
	}
	
	/**
	 * 
	 * @param filePath
	 */
	private void setPath(String filePath) {
		file = new File(filePath);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("GenPartsXMLFile::setPath(), IOException");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Set up this command file document
	 */
	private void initialize()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			doc = factory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			System.out.println("GenPartsXMLFile::initialize(), ParserConfigurationException");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param entries
	 * @throws IOException 
	 */
	public void saveGenerationEntries(String targetFolder, List<String> entries) throws IOException {
		if(entries == null || entries.isEmpty()) {
			return;
		}
		Element root = doc.createElement(ELEMENT_TAR_FOLDER);
		for(String entry : entries) {
			Element element = doc.createElement(ELEMENT_PART);
			element.setAttribute(ATTR_PART_NAME, entry);
			root.appendChild(element);
		}
		doc.appendChild(root);
		XMLPrintHandler.writeFile(doc, file);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> getGenerationEntries() {
		List<String> entries = new ArrayList<String>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			documentBuilder.setErrorHandler(new DefaultHandler());
			doc = documentBuilder.parse(file);
			Element root = doc.getDocumentElement();
			if (root != null) {
				NodeList list = root.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element xmlEntry = (Element) list.item(i);
						if(xmlEntry.hasAttribute(ATTR_PART_NAME)) {
							entries.add(xmlEntry.getAttribute(ATTR_PART_NAME));
						}
					}
						
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return entries;
	}
}

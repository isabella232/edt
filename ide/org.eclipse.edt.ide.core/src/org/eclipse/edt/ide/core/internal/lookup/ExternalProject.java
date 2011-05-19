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
package org.eclipse.edt.ide.core.internal.lookup;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.internal.model.EGLModelResources;
import org.eclipse.edt.ide.core.internal.model.EGLPathEntry;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.internal.utils.FileContentsUtility;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ExternalProject {
	
	String name;
	String installLocation;
	IEGLPathEntry[] pathEntries;
	private IProject referencingProject;

	public ExternalProject(String name, String installLocation) {
		super();
		this.name = name;
		this.installLocation = installLocation;
	}

	public String getName() {
		return name;
	}
	
	public String getLocation() {
		return installLocation;
	}
	
	public IEGLPathEntry[] getResolvedEGLPath() {
		
		if (pathEntries != null) {
			return pathEntries;
		}
		
		pathEntries = primGetResolvedEGLPath();
		return pathEntries;
	}
	
	
	private IEGLPathEntry[] primGetResolvedEGLPath() {
		
		if (installLocation == null) {
			return new IEGLPathEntry[0];
		}
		
		String fileName = installLocation + File.separator + EGLProject.EGLPATH_FILENAME;
		String contents;
		try {
			contents = getFileContents(fileName);
			IEGLPathEntry[] entries = decodeEGLPath(contents);
			return getResolvedEGLPath(entries);
		} catch (Exception e) {
			e.printStackTrace();
			Util.log(e, "Exception while reading " + this.installLocation //$NON-NLS-1$
					+"/.eglpath, will mark eglpath as invalid"); //$NON-NLS-1$

		}		
		return new IEGLPathEntry[0];		
	}
	
	
	public IEGLPathEntry[] getResolvedEGLPath(IEGLPathEntry[] eglpathEntries){

				IEGLModelStatus status;

				int length = eglpathEntries.length;
				ArrayList resolvedEntries = new ArrayList();

				for (int i = 0; i < length; i++) {

					IEGLPathEntry rawEntry = eglpathEntries[i];
					IPath resolvedPath;
					status = null;

					switch (rawEntry.getEntryKind()) {

						case IEGLPathEntry.CPE_VARIABLE :

							IEGLPathEntry resolvedEntry = EGLCore.getResolvedEGLPathEntry(rawEntry);
							if (resolvedEntry == null) {
							} else {
								resolvedEntries.add(resolvedEntry);
							}
							break;

						case IEGLPathEntry.CPE_CONTAINER :
							resolvedEntries.add(rawEntry);

							break;
						case IEGLPathEntry.CPE_LIBRARY:
							// resolve ".." in library path
							resolvedEntry = ((EGLPathEntry) rawEntry).resolvedDotDot();
							if (resolvedEntry == null) {
								
							} else {
								resolvedEntries.add(resolvedEntry);
							}
							break;
						default:
							resolvedEntries.add(rawEntry);

					}
				}

				IEGLPathEntry[] resolvedPath = new IEGLPathEntry[resolvedEntries.size()];
				resolvedEntries.toArray(resolvedPath);

				return resolvedPath;
			}

	
	private String getFileContents(String fileName) throws java.lang.Exception {
		return FileContentsUtility.getFileContentsFromXML(fileName);
	}
		


	/**
	 * Reads and decode an XML classpath string
	 */
	private IEGLPathEntry[] decodeEGLPath(String xmlPath) {

		ArrayList paths = new ArrayList();
		IEGLPathEntry defaultOutput = null;
		try {
			if (xmlPath == null)
				return null;
			StringReader reader = new StringReader(xmlPath);
			Element cpElement;

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

			if (!cpElement.getNodeName().equalsIgnoreCase("eglpath")) { //$NON-NLS-1$
				throw new IOException(EGLModelResources.fileBadFormat);
			}
			NodeList list = cpElement.getElementsByTagName("eglpathentry"); //$NON-NLS-1$
			int length = list.getLength();

			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					IEGLPathEntry entry = EGLPathEntry.elementDecode((Element) node, new Path("/" + name), name);
					if (entry != null) {
						if (entry.getContentKind() == EGLPathEntry.K_OUTPUT) {
							defaultOutput = entry; // separate output
						} else {
							paths.add(entry);
						}
					}
				}
			}
		} catch (IOException e) {
			// bad format
				Util.log(e, "Exception while retrieving " + this.installLocation //$NON-NLS-1$
				+"/.eglpath, will mark eglpath as invalid"); //$NON-NLS-1$
			return new IEGLPathEntry[0];
		} catch (Exception e) {
			// failed creating CP entries from file
				Util.log(e, "Exception while retrieving " + this.installLocation //$NON-NLS-1$
				+"/.eglpath, will mark eglpath as invalid"); //$NON-NLS-1$
			return new IEGLPathEntry[0];
		}
		int pathSize = paths.size();
		if (pathSize > 0 || defaultOutput != null) {
			IEGLPathEntry[] entries = new IEGLPathEntry[pathSize + (defaultOutput == null ? 0 : 1)];
			paths.toArray(entries);
			if (defaultOutput != null)
				entries[pathSize] = defaultOutput;
			// ensure output is last item
			return entries;
		} else {
			return null;
		}
	}

	public IProject getReferencingProject() {
		return referencingProject;
	}

	public void setReferencingProject(IProject referencingProject) {
		this.referencingProject = referencingProject;
	}
	

}

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
package org.eclipse.edt.ide.core.internal.model.bde;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.bde.IPluginBase;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import org.eclipse.edt.ide.core.model.EGLCore;

/**
 * Stores additional information from the manifest files of plugins and stores
 * this information in separate xml file.  Accessed through PDEState.
 */
public class BDEAuxiliaryState {

	private static String CACHE_EXTENSION = ".pluginInfo"; //$NON-NLS-1$

	private static String ATTR_BUNDLE_ID = "bundleID"; //$NON-NLS-1$
//	private static String ATTR_BUNDLE_STRUCTURE = "isBundle"; //$NON-NLS-1$
//	private static String ATTR_CLASS = "class"; //$NON-NLS-1$
//	private static String ATTR_EXTENSIBLE_API = "hasExtensibleAPI"; //$NON-NLS-1$
//	private static String ATTR_LOCALIZATION = "localization"; //$NON-NLS-1$
	private static String ATTR_NAME = "name"; //$NON-NLS-1$
//	private static String ATTR_PATCH = "patch"; //$NON-NLS-1$
//	private static String ATTR_PROVIDER = "provider"; //$NON-NLS-1$
//	private static String ATTR_BUNDLE_SOURCE = "bundleSource"; //$NON-NLS-1$

	private static String ELEMENT_BUNDLE = "bundle"; //$NON-NLS-1$
//	private static String ELEMENT_LIB = "library"; //$NON-NLS-1$
	private static String ELEMENT_ROOT = "map"; //$NON-NLS-1$

	protected Map<String, PluginInfo> fPluginInfos;

	/**
	 * Constructor
	 */
	protected BDEAuxiliaryState() {
		fPluginInfos = new HashMap<String, PluginInfo>();
	}

	/**
	 * Constructor, gets the plugin info objects stored in the passed state
	 * and adds them to this state.
	 * @param state state containing plugin infos to initialize this state with 
	 */
	protected BDEAuxiliaryState(BDEAuxiliaryState state) {
		fPluginInfos = new HashMap(state.fPluginInfos);
	}

	/**
	 * Provides a simple way of storing auxiliary data for a plugin 
	 */
	class PluginInfo {
		String name;
		String project;
	}

	/**
	 * Helper method to create a plugin info object for the given
	 * element.  The plugin info object is added to the map.
	 * @param element
	 */
	private void createPluginInfo(Element element) {
		PluginInfo info = new PluginInfo();
		if (element.hasAttribute(ATTR_NAME))
			info.name = element.getAttribute(ATTR_NAME);

		NodeList libs = element.getChildNodes();
		ArrayList list = new ArrayList(libs.getLength());
		for (int i = 0; i < libs.getLength(); i++) {
			if (libs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element lib = (Element) libs.item(i);
				list.add(lib.getAttribute(ATTR_NAME));
			}
		}
		fPluginInfos.put(element.getAttribute(ATTR_BUNDLE_ID), info);
	}

	public String getPluginName(long bundleID) {
		PluginInfo info = (PluginInfo) fPluginInfos.get(Long.toString(bundleID));
		return info == null ? null : info.name;
	}

	public String getProject(long bundleID) {
		PluginInfo info = (PluginInfo) fPluginInfos.get(Long.toString(bundleID));
		return info == null ? null : info.project;
	}


	/**
	 * Builds an xml document storing the auxiliary plugin info.
	 * @param dir directory location to create the file
	 */
	protected void savePluginInfo(File dir) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc = factory.newDocumentBuilder().newDocument();
			Element root = doc.createElement(ELEMENT_ROOT);

			Iterator iter = fPluginInfos.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next().toString();
				Element element = doc.createElement(ELEMENT_BUNDLE);
				element.setAttribute(ATTR_BUNDLE_ID, key);
				PluginInfo info = (PluginInfo) fPluginInfos.get(key);
				if (info.name != null)
					element.setAttribute(ATTR_NAME, info.name);
				root.appendChild(element);
			}
			doc.appendChild(root);
			XMLPrintHandler.writeFile(doc, new File(dir, CACHE_EXTENSION));
		} catch (Exception e) {
			EDTCoreIDEPlugin.log(e);
		}
	}

	/**
	 * Loads plugin info objects from the pluginInfo xml file stored in the
	 * given directory.
	 * @param dir location to look for the pluginInfo file
	 * @return true if the file was read successfully, false otherwise
	 */
	protected boolean readPluginInfoCache(File dir) {
		File file = new File(dir, CACHE_EXTENSION);
		if (file.exists() && file.isFile()) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = factory.newDocumentBuilder();
				documentBuilder.setErrorHandler(new DefaultHandler());
				Document doc = documentBuilder.parse(file);
				Element root = doc.getDocumentElement();
				if (root != null) {
					NodeList list = root.getChildNodes();
					for (int i = 0; i < list.getLength(); i++) {
						if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
							createPluginInfo((Element) list.item(i));
					}
				}
				return true;
			} catch (org.xml.sax.SAXException e) {
				EDTCoreIDEPlugin.log(e);
			} catch (IOException e) {
				EDTCoreIDEPlugin.log(e);
			} catch (ParserConfigurationException e) {
				EDTCoreIDEPlugin.log(e);
			}
		}
		return false;
	}
	
	public BinaryProjectDescription[] getPluginInfoFromCache() {
		BinaryProjectDescription[] retValues = new BinaryProjectDescription[fPluginInfos.size()];
		Iterator<String> pluginInfosKeysIter = fPluginInfos.keySet().iterator();
		int i = 0;
		BDEPreferencesManager pref = EDTCoreIDEPlugin.getPlugin().getPreferencesManager();
		String pltPath = pref.getString(ICoreConstants.PLATFORM_PATH);
		while(pluginInfosKeysIter.hasNext()) {
			String key = pluginInfosKeysIter.next();
			retValues[i] = new BinaryProjectDescription();
			retValues[i].setId(Long.parseLong(key));
			retValues[i].setName(fPluginInfos.get(key).name);
			retValues[i].setLocation(pltPath + "/" + fPluginInfos.get(key).name);
			i++;
		}
		return retValues;
	}

	/**
	 * Writes out auxiliary information from the given models to an xml file
	 * in the given destination directory.
	 * @param models models to collect information from
	 * @param destination directory to create the xml file in
	 */
	public static void writePluginInfo(IPluginModelBase[] models, File destination) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();

			Element root = doc.createElement(ELEMENT_ROOT);
			doc.appendChild(root);
			for (int i = 0; i < models.length; i++) {
				IPluginBase plugin = models[i].getPluginBase();
				BinaryProjectDescription desc = models[i].getBundleDescription();
				if (desc == null)
					continue;
				Element element = doc.createElement(ELEMENT_BUNDLE);
				element.setAttribute(ATTR_BUNDLE_ID, Long.toString(desc.getId()));
				root.appendChild(element);
			}
			XMLPrintHandler.writeFile(doc, new File(destination, CACHE_EXTENSION));
		} catch (ParserConfigurationException e) {
		} catch (FactoryConfigurationError e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Collects auxiliary information from the manifest and stores it in this state.
	 * @param desc bundle description for the given manifest
	 * @param manifest dictionary of headers in the bundle's manifest file
	 * @param hasBundleStructure whether the plugin has bundle structure
	 */
	protected void addAuxiliaryData(BinaryProjectDescription desc, Dictionary manifest, boolean hasBundleStructure) {
		PluginInfo info = new PluginInfo();
		info.name = (String) manifest.get(Constants.BUNDLE_SYMBOLICNAME);
		fPluginInfos.put(String.valueOf(desc.getId()), info);
	}

	/**
	 * Retrieves the classpath entries from the manifest dictionary
	 * @param manifest dictionary containing manifest headers
	 * @return string array of classpath entries
	 */
	protected String[] getClasspath(Dictionary manifest) {
		String fullClasspath = (String) manifest.get(Constants.BUNDLE_CLASSPATH);
		String[] result = new String[0];
		try {
			if (fullClasspath != null) {
				ManifestElement[] classpathEntries = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, fullClasspath);
				result = new String[classpathEntries.length];
				for (int i = 0; i < classpathEntries.length; i++) {
					result[i] = classpathEntries[i].getValue();
				}
			}
		} catch (BundleException e) {
		}
		return result;
	}

	/**
	 * Clears the plugin info object map.
	 */
	protected void clear() {
		fPluginInfos.clear();
	}

}


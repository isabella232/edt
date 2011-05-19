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
package org.eclipse.edt.ide.core.internal.model.bde.target;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.model.bde.ICoreConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.eclipse.edt.ide.core.model.EGLCore;

/**
 * Provides static methods that will serialize and deserialize xml representing a target definition
 * 
 * @see ITargetDefinition
 */
public class TargetDefinitionPersistenceHelper {

	/**
	 * Constants for XML element names and attributes
	 */
	static final String ROOT = "target"; //$NON-NLS-1$
	static final String ATTR_NAME = "name"; //$NON-NLS-1$
	static final String LOCATIONS = "locations"; //$NON-NLS-1$
	static final String LOCATION = "location"; //$NON-NLS-1$
	static final String ATTR_LOCATION_PATH = "path"; //$NON-NLS-1$
	static final String ATTR_LOCATION_TYPE = "type"; //$NON-NLS-1$
	static final String ATTR_USE_DEFAULT = "useDefault"; //$NON-NLS-1$
	static final String INCLUDE_BUNDLES = "includeBundles"; //$NON-NLS-1$
	static final String OPTIONAL_BUNDLES = "optionalBundles"; //$NON-NLS-1$

	static final String IMPLICIT = "implicitDependencies"; //$NON-NLS-1$
	static final String PLUGIN = "plugin"; //$NON-NLS-1$
	static final String PDE_INSTRUCTION = "pde"; //$NON-NLS-1$
	static final String ATTR_ID = "id"; //$NON-NLS-1$
	static final String INSTALLABLE_UNIT = "unit"; //$NON-NLS-1$
	static final String REPOSITORY = "repository"; //$NON-NLS-1$
	static final String ATTR_INCLUDE_MODE = "includeMode"; //$NON-NLS-1$
	public static final String MODE_SLICER = "slicer"; //$NON-NLS-1$
	public static final String MODE_PLANNER = "planner"; //$NON-NLS-1$
	static final String ATTR_INCLUDE_ALL_PLATFORMS = "includeAllPlatforms"; //$NON-NLS-1$
	static final String ATTR_OPTIONAL = "optional"; //$NON-NLS-1$
	static final String ATTR_VERSION = "version"; //$NON-NLS-1$
	static final String ATTR_CONFIGURATION = "configuration"; //$NON-NLS-1$
	static final String CONTENT = "content"; //$NON-NLS-1$
	static final String ATTR_USE_ALL = "useAllPlugins"; //$NON-NLS-1$
	static final String PLUGINS = "plugins"; //$NON-NLS-1$
	static final String EXTRA_LOCATIONS = "extraLocations"; //$NON-NLS-1$
	private static ITargetPlatformService fTargetService;

	/**
	 * Serializes a target definition to xml and writes the xml to the given stream
	 * @param definition target definition to serialize
	 * @param output output stream to write xml to
	 * @throws CoreException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException 
	 */
	public static void persistXML(ITargetDefinition definition, OutputStream output) throws CoreException, ParserConfigurationException, TransformerException, IOException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		ProcessingInstruction instruction = doc.createProcessingInstruction(PDE_INSTRUCTION, ATTR_VERSION + "=\"" + ICoreConstants.TARGET36 + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		doc.appendChild(instruction);

		Element rootElement = doc.createElement(ROOT);

		if (definition.getName() != null) {
			rootElement.setAttribute(ATTR_NAME, definition.getName());
		}

//		if (((TargetDefinition) definition).getUIMode() == TargetDefinition.MODE_FEATURE) {
//			rootElement.setAttribute(ATTR_INCLUDE_MODE, FEATURE);
//		}

		IBundleContainer[] containers = definition.getBundleContainers();
		if (containers != null && containers.length > 0) {
			Element containersElement = doc.createElement(LOCATIONS);
			for (int i = 0; i < containers.length; i++) {
				Element containerElement = serializeBundleContainer(doc, (AbstractBundleContainer) containers[i]);
				containersElement.appendChild(containerElement);
			}
			rootElement.appendChild(containersElement);
		}

		NameVersionDescriptor[] included = definition.getIncluded();
		if (included != null) {
			Element includedElement = doc.createElement(INCLUDE_BUNDLES);
			serializeBundles(doc, includedElement, included);
			rootElement.appendChild(includedElement);
		}
		NameVersionDescriptor[] optional = definition.getOptional();
		if (optional != null) {
			Element optionalElement = doc.createElement(OPTIONAL_BUNDLES);
			serializeBundles(doc, optionalElement, optional);
			rootElement.appendChild(optionalElement);
		}

		NameVersionDescriptor[] implicitDependencies = definition.getImplicitDependencies();
		if (implicitDependencies != null && implicitDependencies.length > 0) {
			Element implicit = doc.createElement(IMPLICIT);
			for (int i = 0; i < implicitDependencies.length; i++) {
				Element plugin = doc.createElement(PLUGIN);
				plugin.setAttribute(ATTR_ID, implicitDependencies[i].getId());
				if (implicitDependencies[i].getVersion() != null) {
					plugin.setAttribute(ATTR_VERSION, implicitDependencies[i].getVersion());
				}
				implicit.appendChild(plugin);
			}
			rootElement.appendChild(implicit);
		}

		doc.appendChild(rootElement);
		DOMSource source = new DOMSource(doc);

		StreamResult outputTarget = new StreamResult(output);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		transformer.transform(source, outputTarget);
	}

	/**
	 * Parses an xml document from the input stream and deserializes it into a target definition.
	 * 
	 * @param definition definition to be filled with the result of deserialization
	 * @param input stream to get xml input from
	 * @throws CoreException
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static void initFromXML(ITargetDefinition definition, InputStream input) throws CoreException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		parser.setErrorHandler(new DefaultHandler());
		Document doc = parser.parse(new InputSource(input));

		Element root = doc.getDocumentElement();
		if (!root.getNodeName().equalsIgnoreCase(ROOT)) {
			throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Messages.TargetDefinitionPersistenceHelper_0));
		}
		TargetPersistenceHelper.initFromDoc(definition, root);
	}

	static ITargetPlatformService getTargetPlatformService() throws CoreException {
		if (fTargetService == null) {
			fTargetService = (ITargetPlatformService) EDTCoreIDEPlugin.getPlugin().acquireService(ITargetPlatformService.class.getName());
			if (fTargetService == null) {
				throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Messages.TargetDefinitionPersistenceHelper_1));
			}
		}
		return fTargetService;
	}

	/**
	 * Returns the value of any text nodes stored as children of the given element
	 * @param element the element to check for text content
	 * @return string containing text content of element or empty string
	 * @throws DOMException
	 */
	static String getTextContent(Element element) throws DOMException {
		NodeList children = element.getChildNodes();
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < children.getLength(); ++i) {
			Node currentNode = children.item(i);
			if (currentNode.getNodeType() == Node.TEXT_NODE) {
				result.append(currentNode.getNodeValue());
			}
		}
		return result.toString();
	}

	private static Element serializeBundleContainer(Document doc, AbstractBundleContainer container) throws CoreException {
		Element containerElement = doc.createElement(LOCATION);
		if (container instanceof DirectoryBundleContainer) {
			containerElement.setAttribute(ATTR_LOCATION_PATH, container.getLocation(false));
		}
		containerElement.setAttribute(ATTR_LOCATION_TYPE, container.getType());
		return containerElement;
	}

	private static void serializeBundles(Document doc, Element parent, NameVersionDescriptor[] bundles) {
		for (int j = 0; j < bundles.length; j++) {
//			if (bundles[j].getType() == NameVersionDescriptor.TYPE_FEATURE) {
//				Element includedBundle = doc.createElement(FEATURE);
//				includedBundle.setAttribute(ATTR_ID, bundles[j].getId());
//				String version = bundles[j].getVersion();
//				if (version != null) {
//					includedBundle.setAttribute(ATTR_VERSION, version);
//				}
//				parent.appendChild(includedBundle);
//			} else {
				Element includedBundle = doc.createElement(PLUGIN);
				includedBundle.setAttribute(ATTR_ID, bundles[j].getId());
				String version = bundles[j].getVersion();
				if (version != null) {
					includedBundle.setAttribute(ATTR_VERSION, version);
				}
				parent.appendChild(includedBundle);
//			}
		}
	}
}

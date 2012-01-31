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
package org.eclipse.edt.ide.deployment.services.operation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.internal.web.WebXML;
import org.eclipse.edt.ide.deployment.internal.web.WebXMLManager;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.DeploymentResultMessageRequestor;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.services.generators.ServiceUtilities;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ResAuthTypeBase;
import org.eclipse.jst.j2ee.common.ResSharingScopeType;
import org.eclipse.jst.j2ee.common.ResourceRef;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigureDataSourcesOperation extends AbstractDeploymentOperation {
	
	DeploymentResultMessageRequestor messageRequestor;
	private WebXML webXML;
	private boolean contextDotXMLUpdated;
	private boolean resourceRefAdded;
	private IProject targetProject;
	private Document contextDoc;
	private IFile contextFile;
	
	@Override
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException {
		
		messageRequestor = new DeploymentResultMessageRequestor(resultsCollector);
		targetProject = context.getTargetProject();
		webXML = WebXMLManager.instance.getWebXMLUtil(targetProject);
		
		// 1. For jndi binding URIs add a resource-ref
		addJNDIResourceRefs(context.getDeploymentDesc());
		for (DeploymentDesc nextDD : context.getDependentModels()) {
			addJNDIResourceRefs(nextDD);
		}
		
		// 2. For the other binding types, and the server is tomcat, add/update the data source to context.xml. If
		//    this is successful, add a corresponding resource-ref.
		if (ServiceUtilities.isTomcatProject(targetProject)) {
			updateContextDotXML(context.getDeploymentDesc());
			for (DeploymentDesc nextDD : context.getDependentModels()) {
				updateContextDotXML(nextDD);
			}
			
			if (contextDotXMLUpdated) {
				try {
					saveContextDoc();
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
							EGLMessage.EGL_DEPLOYMENT_DEFINED_DATASOURCES,
							null,
							new String[] {contextFile.getName()}));
				}
				catch (Exception e) {
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_FAILED_WRITE_CONTEXTDOTXML,
							null,
							new String[] {contextFile.getName(), DeploymentUtilities.createExceptionMessage(e)}));
				}
			}
		}
		else {
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
					EGLMessage.EGL_DEPLOYMENT_SERVER_NOT_TOMCAT,
					null,
					null));
		}
		
		if (resourceRefAdded) {
			WebXMLManager.instance.updateModel(targetProject);
			
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
					EGLMessage.EGL_DEPLOYMENT_CREATED_RESOURCE_REFS,
					null,
					null));
		}
	}
	
	private void addJNDIResourceRefs(DeploymentDesc ddModel) {
		for (Binding binding : ddModel.getBindings()) {
			if (binding.isUseURI() && binding instanceof SQLDatabaseBinding) {
				String uri = binding.getUri();
				if (uri != null && uri.startsWith("jndi://")) {
					addResourceRef(uri.substring(7)); // "jndi://".length()
				}
			}
		}
	}
	
	private void addResourceRef(String name) {
		if (name != null && (name = name.trim()).length() > 0) {
			ResourceRef ref = CommonFactory.eINSTANCE.createResourceRef();
			ref.setName(name);
			ref.setAuth(ResAuthTypeBase.CONTAINER_LITERAL);
			ref.setType("javax.sql.DataSource");
			ref.setResSharingScope(ResSharingScopeType.SHAREABLE_LITERAL);
			
			webXML.addResourceRef(ref);
			resourceRefAdded = true;
		}
	}
	
	private void updateContextDotXML(DeploymentDesc ddModel) {
		for (Binding binding : ddModel.getBindings()) {
			if (binding instanceof SQLDatabaseBinding) {
				SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
				
				if (!sqlBinding.isDeployAsJndi()) {
					continue;
				}
				
				// Must be hardcoded or a workspace:// URI for us to configure the data source.
				boolean addResourceRef = false;
				String jndiName = sqlBinding.getJndiName();
				
				if (jndiName == null || (jndiName = jndiName.trim()).length() == 0) {
					jndiName = "jdbc/" + binding.getName();
				}
				
				try {
					if (binding.isUseURI()) {
						String uri = binding.getUri();
						
						if (uri != null && uri.startsWith("workspace://")) {
							IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(uri.substring(12)); // "workspace://".length()
							if (profile != null) {
								addResourceRef = configureDataSource(EGLSQLUtility.getSQLUserId(profile),
										EGLSQLUtility.getSQLPassword(profile), EGLSQLUtility.getSQLJDBCDriverClassPreference(profile),
										EGLSQLUtility.getSQLConnectionURLPreference(profile), jndiName);
							}
						}
					}
					else {
						addResourceRef = configureDataSource(sqlBinding.getSqlID(), sqlBinding.getSqlPassword(), sqlBinding.getSqlJDBCDriverClass(),
								sqlBinding.getSqlDB(), jndiName);
					}
					
					if (addResourceRef) {
						addResourceRef(jndiName);
					}
				}
				catch (Exception e) {
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_FAILED_DEFINE_DATASOURCE,
							null,
							new String[] {DeploymentUtilities.createExceptionMessage(e)}));
				}
			}
		}
	}
	
	private boolean configureDataSource(String user, String pass, String driverClass, String url, String jndiName) throws Exception {
		if (notEmpty(jndiName) && notEmpty(url) && notEmpty(driverClass)) {
			if (user == null) {
				user = "";
			}
			if (pass == null) {
				pass = "";
			}
			
			initContextDoc();
			
			NodeList rootKids = contextDoc.getChildNodes();
			
			Node contextNode = null;
			int rootSize = rootKids.getLength();
			for (int i = 0; contextNode == null && i < rootSize; i++) {
				Node next = rootKids.item(i);
				String name = next.getNodeName();
				if ("Context".equalsIgnoreCase(name)) {
					contextNode = next;
				}
			}
			
			if (contextNode == null) {
				contextNode = contextDoc.createElement("Context");
				contextDoc.appendChild(contextNode);
			}
			
			if (true) {
				boolean found = false;
				NodeList contextKids = contextNode.getChildNodes();
				int kidSize = contextKids.getLength();
				for (int i = 0; !found && i < kidSize; i++) {
					Node nextKid = contextKids.item(i);
					String name = nextKid.getNodeName();
					if ("Resource".equalsIgnoreCase(name)) {
						NamedNodeMap attrs = nextKid.getAttributes();
						Node nameAttr = attrs.getNamedItem("name");
						if (nameAttr != null && jndiName.equals(nameAttr.getNodeValue())) {
							found = true;
							
							// Update any values that might have changed.
							updateAttr("username", user, attrs);
							updateAttr("password", pass, attrs);
							updateAttr("driverClassName", driverClass, attrs);
							updateAttr("url", url, attrs);
						}
					}
				}
				
				if (!found) {
					// Add a new entry.
					Node newChild = contextDoc.createElement("Resource");
					NamedNodeMap attrs = newChild.getAttributes();
					
					createAttr("name", jndiName, attrs);
					createAttr("username", user, attrs);
					createAttr("password", pass, attrs);
					createAttr("driverClassName", driverClass, attrs);
					createAttr("url", url, attrs);
					createAttr("maxActive", "4", attrs);
					createAttr("maxIdle", "2", attrs);
					createAttr("maxWait", "5000", attrs);
					createAttr("auth", "Container", attrs);
					createAttr("type", "javax.sql.DataSource", attrs);
					
					contextNode.appendChild(newChild);
					contextDotXMLUpdated = true;
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	private void updateAttr(String name, String value, NamedNodeMap attrs) {
		Node attr = attrs.getNamedItem(name);
		if (attr == null) {
			attr = contextDoc.createAttribute(name);
			attrs.setNamedItem(attr);
			attr.setNodeValue(value);
			contextDotXMLUpdated = true;
		}
		else if (!value.equals(attr.getNodeValue())) {
			attr.setNodeValue(value);
			contextDotXMLUpdated = true;
		}
	}
	
	private void createAttr(String name, String value, NamedNodeMap attrs) {
		Attr attr = contextDoc.createAttribute(name);
		attr.setValue(value);
		attrs.setNamedItem(attr);
	}
	
	private boolean notEmpty(String value) {
		return value != null && value.trim().length() != 0;
	}
	
	private IFile getContextDotXMLFile(IProject project) throws CoreException {
		IVirtualComponent component = ComponentCore.createComponent(project);
		IFolder metaInfFolder = project.getFolder(component.getRootFolder().getFolder("META-INF").getProjectRelativePath());
		if (!metaInfFolder.exists()) {
			CoreUtility.createFolder(metaInfFolder, true, true, null);
		}
		
		return metaInfFolder.getFile("context.xml");
	}
	
	private void initContextDoc() throws Exception {
		if (contextDoc == null) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			contextFile = getContextDotXMLFile(targetProject);
			if (!contextFile.exists()) {
				contextDoc = docBuilder.newDocument();
			}
			else {
				contextDoc = docBuilder.parse(contextFile.getContents(true));
			}
		}
	}
	
	private void saveContextDoc() throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(new DOMSource(contextDoc), result);
		
		String contents = result.getWriter().toString();
		ByteArrayInputStream inputStream;
		try {
			String encoding = contextDoc.getXmlEncoding();
			if (encoding == null || encoding.length() == 0) {
				encoding = "UTF-8";
			}
			inputStream = new ByteArrayInputStream(contents.getBytes(encoding));
		}
		catch (UnsupportedEncodingException e) {
			inputStream = new ByteArrayInputStream(contents.getBytes());
		}
		
		try {
			if (!contextFile.exists()) {
				contextFile.create(inputStream, true, null);
			}
			else {
				contextFile.setContents(inputStream, true, true, null);
			}
		}
		finally {
			try
			{
				inputStream.close();
			}
			catch (IOException e) {
			}
		}
	}
}

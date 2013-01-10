/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.utils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.internal.model.IProjectsChangedListener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * File Format
 * 
 * <resource name="a">
 * 		<storedValue key="xyz", value="123"/>
 * 		<resource name="b">
 * 			<resource name="c">
 * 				<storedValue key="lmn", value="456"/>
 * 			</resource>
 * 		</resource>
 * </resource>
 * 
 * @author svihovec
 */
public class ResourceValueStoreUtility implements IResourceChangeListener, IProjectsChangedListener {
	private final String RESOURCE_ELEMENT = "resource"; //$NON-NLS-1$
	private final String STORED_VALUE_ELEMENT = "storedValue"; //$NON-NLS-1$
	private final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private final String KEY_ATTRIBUTE = "key"; //$NON-NLS-1$
	private final String VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$
	
	// Thoughts 
	//- figure out how to store portions of a file at one time - by folder 
		// would have to potentially read the whole file more than once to find more than one folder at a time
		// would have to be able to write out only segments to a file
		// users will be doing more gets than sets
		// users will probably be doing gets from same set of folders over and over
	//- need to handle the case where a user modified the store file while we were using it, or if its format is bad.
	//- Is there ever a situation where I can get an update on a resource that has already been removed?
		// - check a resource for existance before updating the store
	//- do all work on a background thread or using jobs
	public class ResourceValueStoreNode {
		
		private String name = null;
		private HashMap valuesMap = new HashMap();
		private ResourceValueStoreNode parent = null;
		private ArrayList children = new ArrayList();
						
		public ResourceValueStoreNode(String name){
			this.name = name;
		}
		
		public void setValue(QualifiedName key, String value){
			valuesMap.put(key, value);
		}
		
		public String getValue(QualifiedName key){
			return (String)valuesMap.get(key);
		}
		
		public String getName(){
			return name;
		}
		
		public ResourceValueStoreNode[] getChildren(){
			return (ResourceValueStoreNode[])children.toArray(new ResourceValueStoreNode[children.size()]);
		}
		
		public QualifiedName[] getStoredValueKeys(){
			Set valueKeys = valuesMap.keySet();
			return (QualifiedName[])valueKeys.toArray(new QualifiedName[valueKeys.size()]);
		}
		
		/**
		 * @return Returns the parent.
		 */
		public ResourceValueStoreNode getParent() {
			return parent;
		}
		
		public void setParent(ResourceValueStoreNode parent){
			this.parent = parent;
		}
		
		public void addChild(ResourceValueStoreNode child){
			this.children.add(child);
		}

		public void setName(String name) {
			this.name = name;
		}

		public HashMap getValuesMap() {
			return valuesMap;
		}
	}
	
	private class ResourceValueStoreDefaultHandler extends DefaultHandler{
		
		private Stack pathStack = new Stack();
		private IPath currentPath = null;
		private ResourceValueStore valueStore;
		
		public ResourceValueStoreDefaultHandler(ResourceValueStore store){
			this.valueStore = store;
		}
		
		public void startElement(String namespaceURI, String localName,	String rawName, Attributes atts) {
			
			// If we have a resource, create a new resource node, using the current node as the parent 
			if(rawName.equals(RESOURCE_ELEMENT)){
				String name = atts.getValue(NAME_ATTRIBUTE);
				IPath newPath = new Path(name);
				
				if(currentPath != null){
					newPath = currentPath.append(name);
					pathStack.push(currentPath);
				}
				
				currentPath = newPath;
			}
			
			// If we have a storedValue, add the value to the current node
			else if(rawName.equals(STORED_VALUE_ELEMENT)){
				if(currentPath != null){
					QualifiedName qName = createQualifiedName(atts.getValue(KEY_ATTRIBUTE));
					String value = atts.getValue(VALUE_ATTRIBUTE);
					
					valueStore.setValue(currentPath.makeAbsolute(), qName, value);
				}
			}			
		}
		
		public void endElement(String namespaceURI, String localName, String rawName) {
		
			// at the end of a resource, set the current value to the last value on the stack if applicable
			if(rawName.equals(RESOURCE_ELEMENT)){
				if(pathStack.size() > 0){
					currentPath = (IPath)pathStack.pop();
				}else{
					currentPath = null;
				}
			}
		}
		
		private QualifiedName createQualifiedName(String name){
			int colonLocation = name.indexOf(":"); //$NON-NLS-1$
			String qualifier = null;
			String localName = null;
			
			if(colonLocation != -1){
				qualifier = name.substring(0, colonLocation);
			}
			localName = name.substring(colonLocation + 1, name.length());
			return new QualifiedName(qualifier, localName);
		}
	}
	
	public class ResourceValueStoreFileWriter {
		
		StringBuffer output = new StringBuffer();
		int tabCount = 0;
		private IFile outputFile;
		private String xmlEncode = "";
		public ResourceValueStoreFileWriter(IFile outputFile){
			this.outputFile = outputFile;
		}
		
		public void writeStart(String xmlEncoding){
			this.xmlEncode = xmlEncoding;
			output.append("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>\n");
		}
		
		public String getOutputString() {
			return output.toString();
		}
		
		public void writeEnd(){
			if(outputFile != null){
				ByteArrayInputStream inputStream = null;
				try {
					inputStream = new ByteArrayInputStream(getOutputString().getBytes(xmlEncode));
					if(!outputFile.exists()){
						outputFile.create(inputStream, true, null);
					}else{
						outputFile.setContents(inputStream, true, false, null);
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void writeResourceStart(String name){
			outputTabs(tabCount);
			output.append("<"); //$NON-NLS-1$
			output.append(RESOURCE_ELEMENT);
			output.append(" "); //$NON-NLS-1$
			output.append(NAME_ATTRIBUTE);
			output.append("=\""); //$NON-NLS-1$
			output.append(name);
			output.append("\">\n"); //$NON-NLS-1$
			tabCount++;
		}
		
		public void writeResourceEnd(){
			tabCount--;
			outputTabs(tabCount);
			output.append("</");			 //$NON-NLS-1$
			output.append(RESOURCE_ELEMENT);			
			output.append(">\n");			 //$NON-NLS-1$
		}
		
		public void writeStoredValue(String key, String value){
			if (value != null) {
				outputTabs(tabCount);
				output.append("<"); //$NON-NLS-1$
				output.append(STORED_VALUE_ELEMENT);
				output.append(" "); //$NON-NLS-1$
				output.append(KEY_ATTRIBUTE);
				output.append("=\""); //$NON-NLS-1$
				output.append(key);
				output.append("\" "); //$NON-NLS-1$
				output.append(VALUE_ATTRIBUTE);
				output.append("=\""); //$NON-NLS-1$
				output.append(value);
				output.append("\"/>\n"); //$NON-NLS-1$
			}
		}
		
		/**
		 * @param tabCount
		 */
		private void outputTabs(int tabCount) {
			for(int i=0; i<tabCount; i++){
				output.append("\t"); //$NON-NLS-1$
			}
		}	
	}
	
	/**
	 * A Resource Value Store maintains the mappings of Resource Value Store nodes to resource paths. 
	 * 
	 * @author svihovec
	 */
	public class ResourceValueStore {
		private HashMap resourcesToNodes = new HashMap();
		private IProject project = null;
		private String encode = "";
		/**
		 * @param project
		 */
		public ResourceValueStore(IProject project) {
			this.project = project;
		}
		
		public String getEncode() {
			return encode;
		}

		public void setEncode(String encode) {
			this.encode = encode;
		}

		private ResourceValueStoreNode getNode(IResource resource) {
			return (ResourceValueStoreNode)resourcesToNodes.get(resource.getFullPath());
		}
		
		private void removeNode(IResource resource) {
			resourcesToNodes.remove(resource.getFullPath());
		}
		
		private void addNode(IResource resource, ResourceValueStoreNode node) {
			resourcesToNodes.put(resource.getFullPath(), node);
		}

		public String getValue(IResource resource, QualifiedName key){
			String result = null;
			ResourceValueStoreNode node = getNode(resource);
			if(node != null){
				result = node.getValue(key);
			}
			return result;
		}
		
		public void setValue(IResource resource, QualifiedName key, String value){
			setValue(resource.getFullPath(), key, value);
		}
		
		public void setValue(IPath path, QualifiedName key, String value){
			ResourceValueStoreNode node = (ResourceValueStoreNode)resourcesToNodes.get(path);
			if(node == null){
				node = createNode(path);
			}
			node.setValue(key, value);
		}
		
		private ResourceValueStoreNode createNode(IPath path){
			ResourceValueStoreNode result = null;
			String resourceName = path.lastSegment();
			
			if(path.segmentCount() > 1){
				IPath parentPath = path.removeLastSegments(1);
				ResourceValueStoreNode parentNode = (ResourceValueStoreNode)resourcesToNodes.get(parentPath);
				if(parentNode == null){
					parentNode = createNode(parentPath);
				}
			
				if(parentNode != null){
					result = new ResourceValueStoreNode(resourceName);
					result.setParent(parentNode);
					parentNode.addChild(result);
				}
			}else{
				result = new ResourceValueStoreNode(resourceName);
			}
			
			if(result != null){
				resourcesToNodes.put(path, result);
			}
			return result;
		}
	
		public void write(ResourceValueStoreFileWriter writer){
			ResourceValueStoreNode node = (ResourceValueStoreNode)resourcesToNodes.get(project.getFullPath());
			if(node != null){
				writer.writeStart(getEncode());
				writeResource(writer, node);
				writer.writeEnd();
			}
		}
		
		private void writeResource(ResourceValueStoreFileWriter writer, ResourceValueStoreNode node){
			writer.writeResourceStart(node.getName());
			writeStoredValues(writer, node);
			
			ResourceValueStoreNode[] children = node.getChildren();
			for (int i = 0; i < children.length; i++) {
				ResourceValueStoreNode child = children[i];
				writeResource(writer, child);
			}

			writer.writeResourceEnd();
		}
		
		private void writeStoredValues(ResourceValueStoreFileWriter writer, ResourceValueStoreNode node){
			QualifiedName[] keys = node.getStoredValueKeys();
			
			for (int i = 0; i < keys.length; i++) {
				QualifiedName key = keys[i];
				writer.writeStoredValue(key.toString(), node.getValue(key));
			}
		}

		public void setProject(IProject newProject) {
			if (project != null) {
				ResourceValueStoreNode node = getNode(project);
				if (node != null) {
					node.setName(newProject.getName());
					removeNode(project);
					addNode(newProject, node);
				}
			}
			this.project = newProject;
		}

		public HashMap getResourcesToNodes() {
			return resourcesToNodes;
		}
	}
	
	private static final ResourceValueStoreUtility INSTANCE = new ResourceValueStoreUtility();
	private static final String RESOURCE_VALUE_STORE_FILE_NAME = ".eglproject"; //$NON-NLS-1$
	
	private HashMap resourceValueStores = new HashMap();
	private ArrayList resourceStoreLRUList = new ArrayList();
	private static final int MAX_RESOURCE_STORES = 2;
	
	private static SAXParser parser = null; // one parser for multiple projects
	
	private ResourceValueStoreUtility(){
		EGLModelManager.getEGLModelManager().deltaProcessor.addProjectsChangedListener(this);
	}
	
	private SAXParser initParser(){
		SAXParser result = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			
		} catch (SAXException e) {
			
		}		
		return result;
	}
	
	public static final ResourceValueStoreUtility getInstance(){
		return INSTANCE;
	}
	
	public synchronized ResourceValueStore getNewResourceValueStore(IProject project){
		return readStore(project, true);
	}

	public synchronized ResourceValueStore getResourceValueStore(IProject project){
		return getResourceValueStore(project, true);
	}
	
	public synchronized ResourceValueStore getResourceValueStore(IProject project, boolean forceMigration){
		ResourceValueStore valueStore = (ResourceValueStore)resourceValueStores.get(project);
		
		if(valueStore == null){
			if(resourceStoreLRUList.size() + 1 > MAX_RESOURCE_STORES){
				resourceValueStores.remove(resourceStoreLRUList.remove(resourceStoreLRUList.size() -1));
			}
			
			valueStore = readStore(project, forceMigration);
			resourceValueStores.put(project, valueStore);
		}else{
			resourceStoreLRUList.remove(project);
		}
		resourceStoreLRUList.add(0, project);
		
		
		return valueStore;
	}
	
	public String getValue(IResource resource, QualifiedName key) throws CoreException{
		return getValue(resource, key, true);
	}
	
	public String getValue(IResource resource, QualifiedName key, boolean forceMigration) throws CoreException{
		validateParameters(resource, key);
		return getResourceValueStore(resource.getProject(), forceMigration).getValue(resource, key);
	}
	
	/**
	 * Fix for RATLC00317502. Delete the project in the workspace, the EGL source code will 
	 * not be migrated. Read the project information(.eglproject) from file instead of memory cache.
	 * @param resource
	 * @param key
	 * @return
	 * @throws CoreException
	 */
	public String getValueWithoutCache(IResource resource, QualifiedName key) throws CoreException{
		validateParameters(resource, key);
		IProject project = resource.getProject();
		ResourceValueStore valueStore = null;
		valueStore = readStore(project, true);
		return valueStore.getValue(resource, key);
	}
	
	public void setValue(IResource resource, QualifiedName key, String value) throws CoreException {
		validateParameters(resource, key);
		
		IProject project = resource.getProject();
		ResourceValueStore valueStore = getResourceValueStore(project, true);
		valueStore.setValue(resource, key, value);
		writeStore(project, valueStore);
	}	

	/**
	 * @param resource
	 * @param key
	 */
	private void validateParameters(IResource resource, QualifiedName key) throws CoreException {
		if(resource.exists()){
			if(resource.getType() == IResource.PROJECT){
				if(!((IProject)resource).isOpen()){
					// TODO Set plugin id
					// TODO Set plugin specific status code
					throw new CoreException(new Status(IStatus.ERROR, "myplugin", 0, "Project is closed", null)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}else{
			// TODO Set plugin id
			// TODO Set plugin specific status code
			throw new CoreException(new Status(IStatus.ERROR, "myplugin", 0, "Resource does not exist", null)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if(key == null){
			// TODO Set plugin id
			// TODO Set plugin specific status code
			throw new CoreException(new Status(IStatus.ERROR, "myplugin", 0, "Key is null", null)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private ResourceValueStore readStore(IProject project, boolean forceMigration){
		ResourceValueStore valueStore = new ResourceValueStore(project);
		
		try {
			String encode = "UTF-8";
			if(forceMigration)
				encode = migrateEGLProjectFile2UTF8(project);
			IFile resourceFile = project.getFile(RESOURCE_VALUE_STORE_FILE_NAME);
			if(resourceFile.exists()){
				ResourceValueStoreDefaultHandler handler = new ResourceValueStoreDefaultHandler(valueStore);
				if(parser == null){
					initParser();
				}
				InputStream inputStream = new BufferedInputStream(resourceFile.getContents(true));
				try{
					parser.parse(inputStream, handler);
				}finally{
					inputStream.close();
				}
			}
			else {
//				getOldMetaData(project, valueStore);
			}
			valueStore.setEncode(encode);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return valueStore;
	}
	
	/**
	 * For the WI 29715, the migration process does not work correctly with current encoding of .eglproject.
	 * Migrate the contents of .eglproject to the UTF-8 encoding and save the contents to
	 * .eglproject.
	 * @param project
	 * @throws CoreException 
	 */
	private String migrateEGLProjectFile2UTF8(IProject project) throws CoreException {
		String encode = "UTF-8";
		IFile resourceFile = project.getFile(RESOURCE_VALUE_STORE_FILE_NAME);
		BufferedInputStream inputStream;
		try {
			if(resourceFile.exists()) {
				inputStream = new BufferedInputStream(resourceFile.getContents(true));
				byte[] bytesContents = org.eclipse.edt.ide.core.internal.model.util.Util
						.getInputStreamAsByteArray(inputStream, -1);
				inputStream.close();
				String reg = "<\\s*\\?\\s*xml\\s*version\\s*=.*\\?\\s*>";
				Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(new String(bytesContents));
				//Found the xml version and encoding definition
				if(matcher.find()){     
					String decl = matcher.group();
					reg = "encoding\\s*=\\s*\".*\"";
					pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(decl);
					if(matcher.find()) {
						pattern = Pattern.compile("\".*\"", Pattern.CASE_INSENSITIVE);
						matcher = pattern.matcher(matcher.group());
						if(matcher.find()){
							encode = matcher.group().replaceAll("\"", "").trim();
						}
					}
					return encode;
				}
				//Not find, then migrate the contents to the UTF-8
				StringBuffer temp = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				temp.append(new String(bytesContents));				
				IFile outputFile = project.getFile(RESOURCE_VALUE_STORE_FILE_NAME);
				outputFile.setContents(new ByteArrayInputStream(temp.toString().getBytes("UTF-8")), true, false, null);
				outputFile.setCharset("UTF-8", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encode;
	}
	
//	private void getOldMetaData(IProject project, ResourceValueStore valueStore) {
		//Migrate the data in the V6.x meta data to the .eglProjectInfo file
		//This code can probably be deleted in RAD V8.0 or so
//		String[] targetPartWrapper = getOldDefaultBuildDescriptor(IEGLBuildDescriptorLocator.RUNTIME_DEFAULT_BUILD_DESCRIPTOR_TYPE, project);
//		String[] debugPartWrapper = getOldDefaultBuildDescriptor(IEGLBuildDescriptorLocator.DEBUG_DEFAULT_BUILD_DESCRIPTOR_TYPE, project);
//		if (targetPartWrapper != null) {
//			valueStore.setValue(project, new QualifiedName(null, EGLProjectInfoUtility.DEFAULT_BUILD_DESCRIPTOR_TARGET_NAME), targetPartWrapper[0]);
//			valueStore.setValue(project, new QualifiedName(null, EGLProjectInfoUtility.DEFAULT_BUILD_DESCRIPTOR_TARGET_PATH), targetPartWrapper[1]);
//		}
//		if (debugPartWrapper != null) {
//			valueStore.setValue(project, new QualifiedName(null, EGLProjectInfoUtility.DEFAULT_BUILD_DESCRIPTOR_DEBUG_NAME), debugPartWrapper[0]);
//			valueStore.setValue(project, new QualifiedName(null, EGLProjectInfoUtility.DEFAULT_BUILD_DESCRIPTOR_DEBUG_PATH), debugPartWrapper[1]);
//		}
//		if (targetPartWrapper != null || debugPartWrapper != null)
//			writeStore(project, valueStore);
//		valueStore.setValue(project, EGLBasePlugin.VALUESTOREKEY_EGLFEATURE, Integer.toString(EGLBasePlugin.EGLFEATURE_JASPER_MASK));
//	}

//	public String[] getOldDefaultBuildDescriptor(int defaultType, IResource resource) {
//
//		QualifiedName EGL_DEFAULT_RUNTIME_BD_NAME_KEY = new QualifiedName(
//				"com.ibm.etools.egl.internal.utilities", //$NON-NLS-1$
//				"EGLDefaultRuntimeBuildDescriptorNameKey"); //$NON-NLS-1$
//
//		QualifiedName EGL_DEFAULT_RUNTIME_BD_PATH_KEY = new QualifiedName(
//				"com.ibm.etools.egl.internal.utilities", //$NON-NLS-1$
//				"EGLDefaultRuntimeBuildDescriptorPathKey"); //$NON-NLS-1$
//
//		QualifiedName EGL_DEFAULT_DEBUG_BD_NAME_KEY = new QualifiedName(
//				"com.ibm.etools.egl.internal.utilities", //$NON-NLS-1$
//				"EGLDefaultDebugBuildDescriptorNameKey"); //$NON-NLS-1$
//
//		QualifiedName EGL_DEFAULT_DEBUG_BD_PATH_KEY = new QualifiedName(
//				"com.ibm.etools.egl.internal.utilities", //$NON-NLS-1$
//				"EGLDefaultDebugBuildDescriptorPathKey"); //$NON-NLS-1$
//
//		String[] result = null;
//		String bdName = null;
//		String bdPath = null;
//
//		// get the default bd string for this type and resource
//		if (resource != null) {
//			try {
//				// based on the default type, get the correct property
//				switch (defaultType) {
//				case IEGLBuildDescriptorLocator.RUNTIME_DEFAULT_BUILD_DESCRIPTOR_TYPE:
//					bdName = resource
//							.getPersistentProperty(EGL_DEFAULT_RUNTIME_BD_NAME_KEY);
//					bdPath = resource
//							.getPersistentProperty(EGL_DEFAULT_RUNTIME_BD_PATH_KEY);
//					break;
//				case IEGLBuildDescriptorLocator.DEBUG_DEFAULT_BUILD_DESCRIPTOR_TYPE:
//					bdName = resource
//							.getPersistentProperty(EGL_DEFAULT_DEBUG_BD_NAME_KEY);
//					bdPath = resource
//							.getPersistentProperty(EGL_DEFAULT_DEBUG_BD_PATH_KEY);
//					break;
//				default:
//					break;
//				}
//
//				//need to return a null result instead of an EGLPartWrapper
//				// with empty strings
//				if (bdName != null && bdName.length() == 0)
//					bdName = null;
//				if (bdPath != null && bdPath.length() == 0)
//					bdPath = null;
//
//				if (bdName != null && bdPath != null)
//					result = new String[] {bdName, bdPath};
//			} catch (CoreException e) {
//			}
//		}
//
//		return result;
//	}

	public void writeStore(IProject project, ResourceValueStore valueStore){
		valueStore.write(new ResourceValueStoreFileWriter(project.getFile(RESOURCE_VALUE_STORE_FILE_NAME)));
	}
	
	/**
	 * For testing purposes only
	 *
	 */
	public void clearCache(){
		resourceStoreLRUList.clear();
		resourceValueStores.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		//TODO 
		//	how to handle multiple threaded requests?  (i.e. a change is fired, and then a delete, but the delete gets there first)
		//		- have all of the requests get to this tool and than have this tool background job them?
		//		- check all resources being changed for existance before processing!

		// we are only interested in POST_CHANGE events
		if (event.getType() != IResourceChangeEvent.POST_CHANGE)
			return;

		IResourceDelta rootDelta = event.getDelta();
		final ArrayList removed = new ArrayList();
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

			public boolean visit(IResourceDelta delta) {
				// TODO How are moves handles, will I get a removed, and added, or do I have to check the flags for moved?
				// if I get a moved, can I be smarter about preserving properties?
				//only interested in deleted resources (not added or changed) 
				if (delta.getKind() != IResourceDelta.REMOVED)
					return true;
				IResource resource = delta.getResource();
				removed.add(resource);
				return true;
			}
		};
		try {
			rootDelta.accept(visitor);
		} catch (CoreException e) {
			//TODO open error dialog with syncExec or print to plugin log file
		}
		
		 //nothing more to do if there were no removed files
        if (removed.size() != 0){
        	// TODO deal with the removed files
        }
	}

	public void projectsChanged(IProject[] projects) {
		resourceValueStores.clear();
	}
}

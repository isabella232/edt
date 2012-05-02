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
package resources.edt.binding;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;

import eglx.java.JavaObjectException;
import eglx.lang.AnyException;
import eglx.lang.DynamicAccessException;
import eglx.lang.Resources.ResourceLocator;
import eglx.lang.SysLib;

public class BindingResourceProcessor {
	
	protected static Map<QName, Binding> bindings = new HashMap<QName, Binding>();
	protected ResourceLocator resourceLocator;
	
	public BindingResourceProcessor() {
		resourceLocator = new RuntimeResourceLocator();
	}
	
	public Object resolve(URI uri) throws AnyException{
		URI dd = null;
		String name;
		if(uri.getFragment() == null){
			dd = getDefaultDDURI();
			name = uri.getSchemeSpecificPart();
		}
		else{
			try {
				dd = new URI(uri.getSchemeSpecificPart());
			} catch (URISyntaxException e) {
				JavaObjectException jox = new JavaObjectException();
				jox.exceptionType = URI.class.getName();
				jox.initCause( e );
				throw jox.fillInMessage( Message.RESOURCE_URI_EXCEPTION, uri );
			}
			name = uri.getFragment();
		}
		Binding binding = getBinding(name, dd, resourceLocator);
		if(binding != null && binding.getType() != null){
			return getResource(binding, dd);
		}
		else{
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			throw dax.fillInMessage( Message.ERROR_RESOURCE_BINDING_NOT_FOUND,name, dd );
		}
	}
	
	protected Object getResource(Binding binding, URI dd) throws AnyException{
		String factoryName;
		String factoryPackage;
		int idx = binding.getType().lastIndexOf('.');
		if(idx > -1){
			factoryName = binding.getType().substring(idx+1);
			factoryPackage = binding.getType().substring(0,idx+1);
		}
		else{
			factoryName = binding.getType();
			factoryPackage = "";
		}
		
		factoryName = new StringBuffer(factoryName).replace(0, 1, String.valueOf(factoryName.charAt(0)).toUpperCase()).toString() + "Factory";
		
		String implFactory = "resources." + factoryPackage + factoryName;
	
		BindingFactory factory = null;
		try {
			Class<?> clazz = Runtime.getRunUnit().getClass().getClassLoader().loadClass(implFactory);
			factory = (BindingFactory)clazz.newInstance();
		} catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = binding.getName();
			dax.initCause( e );
			throw dax.fillInMessage( Message.ERROR_RESOURCE_FACTORY_NOT_FOUND, binding.getName(), binding.getType(), dd );
		}
		try {
			Object resource = factory.createResource(binding);
			if(resource == null){
				DynamicAccessException dax = new DynamicAccessException();
				dax.key = binding.getName();
				throw dax.fillInMessage( Message.ERROR_NO_RESOURCE_IMPLEMENTATION, binding.getName(), binding.getType(), dd );
			}
			return resource;
		} catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = binding.getName();
			dax.initCause( e );
			throw dax.fillInMessage( Message.ERROR_RESOURCE_IMPLEMENTATION_EXCEPTION, binding.getName(), binding.getType(), dd );
		}
	}
	
	protected URI getDefaultDDURI() throws AnyException {
		Executable app = Runtime.getRunUnit().getActiveExecutable();
		String dd = "";
		if (app != null) {
			dd = SysLib.getProperty(Constants.APPLICATION_PROPERTY_FILE_NAME_KEY + "." + app.getClass().getCanonicalName());
		}
		if (dd == null) {
			dd = SysLib.getProperty(Constants.APPLICATION_PROPERTY_FILE_NAME_KEY);
		}
		if (dd == null || dd.isEmpty()) {
			AnyException ae = new AnyException();
			throw ae.fillInMessage( Message.MISSING_DEFAULT_DD );
		}
		try {
			return createFileURI(dd);
		} catch (URISyntaxException e) {
		}
		return null;
	}
	
	protected URI createFileURI(String fileName) throws URISyntaxException{
		return new URI("file:" + fileName);
	}
	protected Binding getBinding(String bindingURI, URI propertyFileURI, ResourceLocator resourceLocator) {
		QName resourceId = new QName(propertyFileURI.toASCIIString(), bindingURI);
		Binding binding = bindings.get(resourceId);
		if(binding == null){
			RuntimeDeploymentDesc dd = getDeploymentDesc(propertyFileURI, resourceLocator);
			binding = getBinding(bindingURI, dd);
			if(binding == null){
				Set<String> seenDDs = new HashSet<String>();
				seenDDs.add(dd.getName());
				binding = getBinding(bindingURI, dd.getIncludedDescs(), resourceLocator, seenDDs);
			}
			if(binding != null){
				bindings.put(resourceId, binding);
			}
		}
		return binding;
	}
	
	protected RuntimeDeploymentDesc getDeploymentDesc(URI propertyFileURI, ResourceLocator resourceLocator){
		return resourceLocator.getDeploymentDesc(propertyFileURI);
	}

	protected Binding getBinding(String name, List<String> includes, ResourceLocator resourceLocator, Set<String> seenDDs)throws AnyException{
		List<RuntimeDeploymentDesc> includedDDs = new ArrayList<RuntimeDeploymentDesc>();
		Binding binding = null;
		for(String ddName : includes){
			if (seenDDs.contains(ddName)) {
				continue;
			}
			seenDDs.add(ddName);
			
			RuntimeDeploymentDesc includedDD;
			try {
				includedDD = getDeploymentDesc(createFileURI(ddName), resourceLocator);
				binding = getBinding(name, includedDD);
				if(binding != null){
					break;
				}
				else{
					includedDDs.add(includedDD);
				}
			} catch (URISyntaxException e) {
				JavaObjectException jox = new JavaObjectException();
				jox.exceptionType = URI.class.getName();
				jox.initCause( e );
				throw jox.fillInMessage( Message.RESOURCE_URI_EXCEPTION, ddName );
			}
		}
		if (binding == null) {
			for(RuntimeDeploymentDesc includedDD : includedDDs){
				binding = getBinding(name, includedDD.getIncludedDescs(), resourceLocator, seenDDs);
				if(binding != null){
					break;
				}
			}
		}
		return binding;
	}
	protected Binding getBinding(String name, RuntimeDeploymentDesc dd){
		for(Binding binding : dd.getBindings()){
			if(name.equalsIgnoreCase(binding.getName())){
				return binding;
			}
		}
		return null;
	}
	public static Map<QName, Binding> getBindings() {
		return bindings;
	}
}

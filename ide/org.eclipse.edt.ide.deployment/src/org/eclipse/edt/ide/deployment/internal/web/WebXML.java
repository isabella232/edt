/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.internal.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.EnvEntry;
import org.eclipse.jst.j2ee.common.EnvEntryType;
import org.eclipse.jst.j2ee.common.ResAuthTypeBase;
import org.eclipse.jst.j2ee.common.ResSharingScopeType;
import org.eclipse.jst.j2ee.common.ResourceRef;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.javaee.core.DisplayName;
import org.eclipse.jst.javaee.core.EmptyType;
import org.eclipse.jst.javaee.core.JavaeeFactory;
import org.eclipse.jst.javaee.core.ParamValue;
import org.eclipse.jst.javaee.core.UrlPatternType;

public class WebXML {
	
	private IProject project;
	private Map environmentEntries;
	private Map servlets;
	private Map filters;
	private Map resourceRefs;
	private Boolean isDistributable;
	
	public WebXML( IProject project )
	{
		this.project = project;
		environmentEntries = new HashMap();
		servlets = new HashMap();
		filters = new HashMap();
		resourceRefs = new HashMap();
	}
	
	
	public void addServlet( final String servletName, final String displayName, final String servletClassName, final Set<String> urls, final Map paramList, final int loadOnStartUp)
	{
		servlets.put( servletName, new Servlet(servletName, displayName, servletClassName, urls, paramList, loadOnStartUp));
	}
	
	public void addServlet( final String servletName, final String servletClassName, final String url, final Map paramList)
	{
		servlets.put( servletName, new Servlet(servletName, servletClassName, url, paramList));
	}
	
	public void addFilter( final String filterName, final String filterClassName, final List urlPatterns, final Map paramList)
	{
		filters.put( filterName, new Filter(filterName, filterClassName, urlPatterns, paramList));
	}
	
	public void addResourceRef( ResourceRef ref )
	{
		resourceRefs.put(ref.getName(), ref );
	}

	public void isDistributable(boolean isDistributable) 
	{
		this.isDistributable = new Boolean(isDistributable);
	}

	public void addEnvironmentEntries( ArrayList envEntries )
	{
		for( Iterator itr = envEntries.iterator(); itr.hasNext();)	
		{
			EnvEntry entry = (EnvEntry)itr.next();
			environmentEntries.put(entry.getName(), entry);
		}
	}
	
	private class Servlet
	{
		private String servletName;
		private int loadOnStartUp;
		private String servletClassName;
		private String displayName;
		private Set<String> urls;
		private Map paramList;
		public Servlet(String servletName, String servletClassName, String url, Map paramList) {
			this(servletName, servletName, servletClassName, new HashSet<String>(), paramList, -1);
			urls.add(url);
		}
		
		public Servlet(String servletName, String displayName, String servletClassName, Set<String> urls, Map paramList, int loadOnStartUp) {
			this.servletName = servletName;
			this.displayName = displayName;
			this.servletClassName = servletClassName;
			this.urls = urls;
			this.paramList = paramList;
			this.loadOnStartUp = loadOnStartUp;
		}
		
	}
	
	private class Filter
	{
		private String filterName;
		private String filterClassName;
		private List urlPatterns;
		private Map paramList;
		public Filter(String filterName, String filterClassName, List urlPatterns, Map paramList) {
			this.filterName = filterName;
			this.filterClassName = filterClassName;
			this.urlPatterns = urlPatterns;
			this.paramList = paramList;
		}
		
	}
	
	private interface EglWebApp
	{
		public void registerServlet( final String servletName, final String displayName, final String servletClassName, final Set<String> urlMappings, final Map<String, String> paramList, final int loadOnStartUp );
		public void registerFilter( final String servletName, final String servletClassName, final Map paramList, final List urlMpatterns );
		public void addResourceRef( final ResourceRef resourceRef );
		public void updateAddEnvironmentEntry( final EnvEntry envEntry );
		public void updateDistributable();
	}
	private class J2eeWebApp implements EglWebApp
	{
		org.eclipse.jst.j2ee.webapplication.WebApp webApp;
		public J2eeWebApp(org.eclipse.jst.j2ee.webapplication.WebApp webApp) {
			this.webApp = webApp;
		}
		
		public void updateDistributable() 
		{
			if( ((org.eclipse.jst.j2ee.webapplication.WebApp)webApp).isDistributable() != isDistributable.booleanValue() )
			{
				((org.eclipse.jst.j2ee.webapplication.WebApp)webApp).setDistributable( isDistributable.booleanValue() );
			}
		}
		public void addResourceRef(ResourceRef resourceRef) {
			EList resourceRefs = webApp.getResourceRefs();
			ResourceRef existingRef = null;
			for ( Iterator it = resourceRefs.iterator(); it.hasNext(); )
			{
				ResourceRef ref = (ResourceRef)it.next();
				if ( ref.getName().equals( resourceRef.getName() ) )
				{
					existingRef = ref;
					break;
				}
			}
			
			if ( existingRef == null )
			{
				ResourceRef ref = CommonFactory.eINSTANCE.createResourceRef();
				ref.setName( resourceRef.getName() );
				ref.setType( resourceRef.getType() );
				ref.setResSharingScope( resourceRef.getResSharingScope() );
				ref.setAuth( resourceRef.getAuth() );
				resourceRefs.add( ref );
			}
			else
			{
				existingRef.setType( resourceRef.getType() );
				existingRef.setResSharingScope( resourceRef.getResSharingScope() );
				existingRef.setAuth( resourceRef.getAuth() );
			}
		}
		public void updateAddEnvironmentEntry(EnvEntry newEnvEntry) {
			boolean found = false;
			for ( Iterator itr = webApp.getEnvironmentProperties().iterator(); itr.hasNext(); )
			{
				EnvEntry existingEntry = (EnvEntry)(itr.next());
				if ( existingEntry.getName().equals(newEnvEntry.getName()) )
				{
					//If the entry was found then update it only if it is not one 
					//of the options below.
					if ( !existingEntry.getName().equalsIgnoreCase("vgj.trace.type") &&
						 !existingEntry.getName().equalsIgnoreCase("vgj.trace.device.option") &&
						 !existingEntry.getName().equalsIgnoreCase("vgj.trace.device.spec") )
					{
						existingEntry.setValue( newEnvEntry.getValue() );
						existingEntry.setType( newEnvEntry.getType() );
					}
					found = true;
				}
			}
			if ( !found )
			{
				webApp.getEnvironmentProperties().add( newEnvEntry );
			}
		}
		/**
		 * This is for a Dynamic web module version 2.4
		 * @param webApp
		 * @param servletName
		 * @param servletClassName
		 * @param urlMapping
		 * @param paramList
		 */
		public void registerServlet( final String servletName, final String displayName, final String servletClassName, final Set<String> urlMappings, final Map<String, String> paramList, final int loadOnStartUp )
		{
			
			org.eclipse.jst.j2ee.webapplication.Servlet servlet = getJ2eeServlet( servletName, displayName, servletClassName, loadOnStartUp );
			
			List initParams = servlet.getInitParams();
			initParams.clear();
			org.eclipse.jst.j2ee.common.ParamValue initParam;		
			for( Iterator<String> iter = paramList.keySet().iterator(); iter.hasNext(); )
			{
				initParam = org.eclipse.jst.j2ee.common.CommonFactory.eINSTANCE.createParamValue();
				String key = iter.next();
				initParam.setName(key);
				initParam.setValue(paramList.get(key));
				initParams.add(initParam);
			}
					
			//remove all of the mappings for this servlet
			List mappings = webApp.getServletMappings();
			if (mappings != null && !mappings.isEmpty()) 
			{
				List<org.eclipse.jst.j2ee.webapplication.ServletMapping> removalList = new ArrayList<org.eclipse.jst.j2ee.webapplication.ServletMapping>(); 
				org.eclipse.jst.j2ee.webapplication.ServletMapping map;
				for( Iterator<org.eclipse.jst.j2ee.webapplication.ServletMapping> iter = mappings.iterator(); iter.hasNext(); )
				{
					map = iter.next();
					if( map.getServlet().getServletName().equals(servlet.getServletName()) )
					{
						removalList.add( map );
					}
				}
				if( !removalList.isEmpty() )
				{
					mappings.removeAll( removalList );
				}
			}

			//add the restservice mapping
			for( Iterator<String> itr = urlMappings.iterator(); itr.hasNext();){
				org.eclipse.jst.j2ee.webapplication.ServletMapping mapping = org.eclipse.jst.j2ee.webapplication.WebapplicationFactory.eINSTANCE.createServletMapping();
				mapping.setServlet(servlet);
				mapping.setWebApp(webApp);
				mapping.setUrlPattern(itr.next());
				mappings.add( mapping );	
			}
		}
		
		/*
		 * If a servlet doesn't exist and the classname is not null the method will create a new servlet
		 * and populate the default values
		 */
		private org.eclipse.jst.j2ee.webapplication.Servlet getJ2eeServlet( final String servletName, final String displayName, final String servletClassName, final int loadOnStartUp )
		{
			org.eclipse.jst.j2ee.webapplication.Servlet servlet = webApp.getServletNamed( servletName );
			
			if( servlet == null && servletClassName != null )
			{
				List servlets = webApp.getServlets();
				servlet = org.eclipse.jst.j2ee.webapplication.WebapplicationFactory.eINSTANCE.createServlet();
				servlets.add(servlet);
				servlet.setWebApp(webApp);
				
				servlet.setServletName(servletName);
				org.eclipse.jst.j2ee.webapplication.ServletType servletType = org.eclipse.jst.j2ee.webapplication.WebapplicationFactory.eINSTANCE.createServletType();
				servletType.setClassName(servletClassName);
				servlet.setWebType(servletType);
				servlet.setLoadOnStartup(new Integer(loadOnStartUp));
				servlet.setDisplayName(displayName);		
			}
			return servlet; 
		}
		/**
		 * This is for a Dynamic web module version 2.4
		 * @param webApp
		 * @param filterName
		 * @param filterClassName
		 * @param urlMapping
		 * @param paramList
		 */
		public void registerFilter( final String filterName, final String filterClassName, final Map paramList, final List urlPatternList )
		{
			
			org.eclipse.jst.j2ee.webapplication.Filter filter = getJ2eeFilter( filterName, filterClassName );
			
			List initParams = filter.getInitParamValues();
			initParams.clear();
			for( Iterator iter = paramList.keySet().iterator(); iter.hasNext(); )
			{
				org.eclipse.jst.j2ee.common.ParamValue initParam = org.eclipse.jst.j2ee.common.internal.impl.CommonFactoryImpl.eINSTANCE.createParamValue();
				Object key = iter.next();
				initParam.setName((String)key);
				initParam.setValue((String)paramList.get(key));
				initParams.add((Object)initParam);
			}
					
			//remove all of the mappings for this servlet
			List mappings = webApp.getFilterMappings();
			if (mappings != null && !mappings.isEmpty()) 
			{
				List removalList = new ArrayList(); 
				org.eclipse.jst.j2ee.webapplication.FilterMapping map;
				for( Iterator iter = mappings.iterator(); iter.hasNext(); )
				{
					map = (org.eclipse.jst.j2ee.webapplication.FilterMapping)iter.next();
					if( map.getFilter().getName().equals(filter.getName()) )
					{
						removalList.add( map );
					}
				}
				if( !removalList.isEmpty() )
				{
					mappings.removeAll( removalList );
				}
			}

			for( Iterator iter = urlPatternList.iterator(); iter.hasNext(); )
			{
				org.eclipse.jst.j2ee.webapplication.FilterMapping mapping = org.eclipse.jst.j2ee.webapplication.WebapplicationFactory.eINSTANCE.createFilterMapping();
				mapping.setFilter( filter );
				String urlPattern = (String)iter.next();
				mapping.setUrlPattern(urlPattern);
				mappings.add( mapping );
			}
							
		}
		
		/*
		 * If a filter doesn't exist and the classname is not null the method will create a new filter
		 * and populate the default values
		 */
		private org.eclipse.jst.j2ee.webapplication.Filter getJ2eeFilter( final String filterName, final String filterClassName )
		{
			org.eclipse.jst.j2ee.webapplication.Filter filter = webApp.getFilterNamed( filterName );
			
			if( filter == null && filterClassName != null )
			{
				List filters = webApp.getFilters();
				filter = org.eclipse.jst.j2ee.webapplication.WebapplicationFactory.eINSTANCE.createFilter();
				filters.add(filter);
				
				filter.setName(filterName);
				filter.setDisplayName(filterName);
				filter.setFilterClassName(filterClassName);
			}
			return filter; 
		}
		
	}
	private class JavaeeWebApp implements EglWebApp
	{
		org.eclipse.jst.javaee.web.WebApp webApp;
		public JavaeeWebApp(org.eclipse.jst.javaee.web.WebApp webApp) {
			this.webApp = webApp;
		}
		
		public void updateDistributable() 
		{
			List distributables = ((org.eclipse.jst.javaee.web.WebApp)webApp).getDistributables();
			boolean appIsDistributable = !distributables.isEmpty();
			if( appIsDistributable != isDistributable.booleanValue() )
			{
				if( !distributables.isEmpty() )
				{

					List list = new ArrayList();
					list.addAll( distributables );
					((org.eclipse.jst.javaee.web.WebApp)webApp).getDistributables().removeAll(list);
					((org.eclipse.jst.javaee.web.WebApp)webApp).getDistributables().clear();
				}
				else
				{ 
					ArrayList list = new ArrayList();
					EmptyType emptyType = JavaeeFactory.eINSTANCE.createEmptyType();
					list.add(emptyType);
					((org.eclipse.jst.javaee.web.WebApp)webApp).getDistributables().addAll(list);
				}
			}
		}

		public void addResourceRef(ResourceRef resourceRef) {
			List resourceRefs = webApp.getResourceRefs();
			org.eclipse.jst.javaee.core.ResourceRef existingRef = null;
			for ( Iterator it = resourceRefs.iterator(); it.hasNext(); )
			{
				org.eclipse.jst.javaee.core.ResourceRef ref = (org.eclipse.jst.javaee.core.ResourceRef)it.next();
				if ( resourceRef.getName().equals( ref.getResRefName() ) )
				{
					existingRef = ref;
					break;
				}
			}
			
			if ( existingRef == null )
			{
				org.eclipse.jst.javaee.core.ResourceRef ref = org.eclipse.jst.javaee.core.JavaeeFactory.eINSTANCE.createResourceRef();
				ref.setResRefName( resourceRef.getName() );
				if( resourceRef.getResSharingScope() == ResSharingScopeType.SHAREABLE_LITERAL )
				{
					ref.setResSharingScope(org.eclipse.jst.javaee.core.ResSharingScopeType.SHAREABLE_LITERAL);
				}
				else if( resourceRef.getResSharingScope() == ResSharingScopeType.UNSHAREABLE_LITERAL )
				{
					ref.setResSharingScope(org.eclipse.jst.javaee.core.ResSharingScopeType.UNSHAREABLE_LITERAL);
				}
				if( resourceRef.getAuth() == ResAuthTypeBase.CONTAINER_LITERAL )
				{
					ref.setResAuth( org.eclipse.jst.javaee.core.ResAuthType.CONTAINER_LITERAL );
				}
				else if( resourceRef.getAuth() == ResAuthTypeBase.APPLICATION_LITERAL )
				{
					ref.setResAuth( org.eclipse.jst.javaee.core.ResAuthType.APPLICATION_LITERAL );
				}
				ref.setResType(resourceRef.getType());
				resourceRefs.add( ref );
			}
			else
			{
				if( resourceRef.getResSharingScope() == ResSharingScopeType.SHAREABLE_LITERAL )
				{
					existingRef.setResSharingScope(org.eclipse.jst.javaee.core.ResSharingScopeType.SHAREABLE_LITERAL);
				}
				else if( resourceRef.getResSharingScope() == ResSharingScopeType.UNSHAREABLE_LITERAL )
				{
					existingRef.setResSharingScope(org.eclipse.jst.javaee.core.ResSharingScopeType.UNSHAREABLE_LITERAL);
				}
				if( resourceRef.getAuth() == ResAuthTypeBase.CONTAINER_LITERAL )
				{
					existingRef.setResAuth( org.eclipse.jst.javaee.core.ResAuthType.CONTAINER_LITERAL );
				}
				else if( resourceRef.getAuth() == ResAuthTypeBase.APPLICATION_LITERAL )
				{
					existingRef.setResAuth( org.eclipse.jst.javaee.core.ResAuthType.APPLICATION_LITERAL );
				}
				existingRef.setResType(resourceRef.getType());
			}
		}
		public void updateAddEnvironmentEntry(EnvEntry newEnvEntry) {
			boolean found = false;
			for ( Iterator itr = webApp.getEnvEntries().iterator(); itr.hasNext(); )
			{
				org.eclipse.jst.javaee.core.EnvEntry existingEntry = (org.eclipse.jst.javaee.core.EnvEntry)(itr.next());
				if ( existingEntry.getEnvEntryName().equals(newEnvEntry.getName()) )
				{
					//If the entry was found then update it only if it is not one 
					//of the options below.
					if ( !existingEntry.getEnvEntryName().equalsIgnoreCase("vgj.trace.type") &&
						 !existingEntry.getEnvEntryName().equalsIgnoreCase("vgj.trace.device.option") &&
						 !existingEntry.getEnvEntryName().equalsIgnoreCase("vgj.trace.device.spec") )
					{
						existingEntry.setEnvEntryValue( newEnvEntry.getValue() );
						existingEntry.setEnvEntryType( convert( newEnvEntry.getType() ) );
					}
					found = true;
				}
			}
			if ( !found )
			{
				org.eclipse.jst.javaee.core.EnvEntry entry = org.eclipse.jst.javaee.core.JavaeeFactory.eINSTANCE.createEnvEntry();
				entry.setEnvEntryName(newEnvEntry.getName());
				entry.setEnvEntryType(convert( newEnvEntry.getType() ));
				entry.setEnvEntryValue(newEnvEntry.getValue());
				webApp.getEnvEntries().add( entry );
			}
		}
		
		private org.eclipse.jst.javaee.core.EnvEntryType convert( EnvEntryType type )
		{
			switch( type.getValue() )
			{
				case EnvEntryType.BOOLEAN:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_BOOLEAN_LITERAL;
				case EnvEntryType.BYTE:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_BYTE_LITERAL;
				case EnvEntryType.CHARACTER:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_CHARACTER_LITERAL;
				case EnvEntryType.DOUBLE:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_DOUBLE_LITERAL;
				case EnvEntryType.FLOAT:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_FLOAT_LITERAL;
				case EnvEntryType.INTEGER:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_INTEGER_LITERAL;
				case EnvEntryType.LONG:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_LONG_LITERAL;
				case EnvEntryType.SHORT:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_SHORT_LITERAL;
				case EnvEntryType.STRING:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_STRING_LITERAL;
				default:
					return org.eclipse.jst.javaee.core.EnvEntryType.JAVA_LANG_STRING_LITERAL;
			}
		}
		/**
		 * This is for a Dynamic web module version 2.5
		 * @param webApp
		 * @param servletName
		 * @param servletClassName
		 * @param urlMapping
		 * @param paramList
		 */
		public void registerServlet( final String servletName, String displayName, final String servletClassName, final Set<String> urlMappings, final Map<String, String> paramList, final int loadOnStartUp )
		{

			org.eclipse.jst.javaee.web.Servlet servlet = getJavaeeServlet( servletName, displayName, servletClassName, loadOnStartUp );

			List<ParamValue> initParams = servlet.getInitParams();
			initParams.clear();
			org.eclipse.jst.javaee.core.ParamValue initParam;
			for( Iterator<String> iter = paramList.keySet().iterator(); iter.hasNext(); )
			{
				initParam = org.eclipse.jst.javaee.core.JavaeeFactory.eINSTANCE.createParamValue();
				String key = iter.next();
				initParam.setParamName(key);
				initParam.setParamValue(paramList.get(key));
				initParams.add(initParam);
			}
			
			org.eclipse.jst.javaee.web.ServletMapping mapping = null;
			List<org.eclipse.jst.javaee.web.ServletMapping> mappings = webApp.getServletMappings();
			//get the mapping
			//do this by creating a new map and add any non restservice servlet mapping 
			//then clear the existing map
			if (mappings != null && !mappings.isEmpty()) 
			{
				org.eclipse.jst.javaee.web.ServletMapping map;
				for( Iterator<org.eclipse.jst.javaee.web.ServletMapping> iter = mappings.iterator(); iter.hasNext(); )
				{
					map = iter.next();
					if( map.getServletName().equals(servlet.getServletName()))
					{
						mapping = map;
						break;
					}
				}
			}

			if( mapping == null )
			{
				//add a map for the restservice servlet
				mapping = org.eclipse.jst.javaee.web.WebFactory.eINSTANCE.createServletMapping();
				mapping.setServletName(servletName);
				mappings.add(mapping);
			}
			List<UrlPatternType> urls = mapping.getUrlPatterns();
			urls.clear();
			
			for( Iterator<String> itr = urlMappings.iterator(); itr.hasNext();){
				org.eclipse.jst.javaee.core.UrlPatternType urlPattern = org.eclipse.jst.javaee.core.JavaeeFactory.eINSTANCE.createUrlPatternType();
				urlPattern.setValue( itr.next() );
				urls.add( urlPattern );
			}
			
		}

		/*
		 * If a servlet doesn't exist and the classname is not null the method will create a new servlet
		 * and populate the default values
		 */
		private org.eclipse.jst.javaee.web.Servlet getJavaeeServlet(final String servletName, final String displayName, final String servletClassName, final int loadOnStartUp )
		{
			List<org.eclipse.jst.javaee.web.Servlet> servlets = webApp.getServlets();
			org.eclipse.jst.javaee.web.Servlet tempServlet;
			for( Iterator<org.eclipse.jst.javaee.web.Servlet> iter = servlets.iterator(); iter.hasNext(); )
			{
				tempServlet = iter.next();
				if( servletName.equalsIgnoreCase( tempServlet.getServletName() ) )
				{
					return tempServlet;
				}
			}
			tempServlet = null;
			if( servletClassName != null && servletClassName.length() > 0 )
			{
				tempServlet = org.eclipse.jst.javaee.web.WebFactory.eINSTANCE.createServlet();
				tempServlet.setLoadOnStartup(new Integer(loadOnStartUp));
				tempServlet.setServletName(servletName);
				tempServlet.setServletClass(servletClassName);
				DisplayName dName = org.eclipse.jst.javaee.core.JavaeeFactory.eINSTANCE.createDisplayName();
				dName.setValue(displayName);
				tempServlet.getDisplayNames().add(dName);
				
				servlets.add( tempServlet );
			}
			return tempServlet;
		}
		
		/**
		 * This is for a Dynamic web module version 2.5
		 * @param webApp
		 * @param filterName
		 * @param filterClassName
		 * @param urlMapping
		 * @param paramList
		 */
		public void registerFilter(  final String filterName, final String filterClassName, final Map paramList, final List urlPatternList )
		{
			org.eclipse.jst.javaee.web.Filter filter = getJavaeeFilter( filterName, filterClassName );

			List initParams = filter.getInitParams();
			initParams.clear();
			org.eclipse.jst.javaee.core.ParamValue initParam;
			for( Iterator iter = paramList.keySet().iterator(); iter.hasNext(); )
			{
				initParam = org.eclipse.jst.javaee.core.JavaeeFactory.eINSTANCE.createParamValue();
				Object key = iter.next();
				initParam.setParamName((String)key);
				initParam.setParamValue((String)paramList.get(key));
				initParams.add(initParam);
			}
			
			org.eclipse.jst.javaee.web.FilterMapping mapping = null;
			List mappings = webApp.getFilterMappings();
			//get the mapping
			//do this by creating a new map and add any mapping 
			//then clear the existing map
			if (mappings != null && !mappings.isEmpty()) 
			{
				org.eclipse.jst.javaee.web.FilterMapping map;
				for( Iterator iter = mappings.iterator(); iter.hasNext(); )
				{
					map = (org.eclipse.jst.javaee.web.FilterMapping)iter.next();
					if( map.getFilterName().equals(filter.getFilterName()))
					{
						mapping = map;
						break;
					}
				}
			}

			if( mapping == null )
			{
				//add a map for the restservice servlet
				mapping = org.eclipse.jst.javaee.web.WebFactory.eINSTANCE.createFilterMapping();
				mapping.setFilterName(filterName);
				mappings.add(mapping);
			}
			List urls = mapping.getUrlPatterns();
			urls.clear();

			for( Iterator iter = urlPatternList.iterator(); iter.hasNext(); )
			{
				String pattern = (String)iter.next();
				org.eclipse.jst.javaee.core.UrlPatternType urlPattern = org.eclipse.jst.javaee.core.JavaeeFactory.eINSTANCE.createUrlPatternType();
				urlPattern.setValue( pattern );
				urls.add( urlPattern );
			}
			
		}	
		
		
		/*
		 * If a filter doesn't exist and the classname is not null the method will create a new filter
		 * and populate the default values
		 */
		private org.eclipse.jst.javaee.web.Filter getJavaeeFilter( final String filterName, final String filterClassName )
		{
			List filters = webApp.getFilters();
			org.eclipse.jst.javaee.web.Filter tempFilter;
			for( Iterator iter = filters.iterator(); iter.hasNext(); )
			{
				tempFilter = (org.eclipse.jst.javaee.web.Filter)iter.next();
				if( filterName.equalsIgnoreCase( tempFilter.getFilterName() ) )
				{
					return tempFilter;
				}
			}
			tempFilter = null;
			if( filterClassName != null && filterClassName.length() > 0 )
			{
				tempFilter = org.eclipse.jst.javaee.web.WebFactory.eINSTANCE.createFilter();
				tempFilter.setFilterName(filterName);
				tempFilter.setFilterClass(filterClassName);
				
				filters.add( tempFilter );
			}
			return tempFilter;
		}
		
		
	}
	void updateModel()
	{
		final IModelProvider provider = ModelProviderManager.getModelProvider( project );		
		if( provider != null )
		{
			IPath path = (provider.getModelObject() instanceof org.eclipse.jst.javaee.web.WebApp) ? IModelProvider.FORCESAVE : null;
			provider.modify(new Runnable(){
				public void run() {
					Object object =  provider.getModelObject(); 
					
					EglWebApp webApp = null;
					if (object instanceof org.eclipse.jst.javaee.web.WebApp) 
					{
						webApp = new JavaeeWebApp((org.eclipse.jst.javaee.web.WebApp) object);
					}
					else if( object instanceof org.eclipse.jst.j2ee.webapplication.WebApp )
					{
						webApp = new J2eeWebApp((org.eclipse.jst.j2ee.webapplication.WebApp) object);
					}
					
					if( webApp != null )
					{
						for( Iterator itr = servlets.values().iterator(); itr.hasNext();)
						{
							Servlet servlet = (Servlet)itr.next();
							webApp.registerServlet(servlet.servletName, servlet.displayName, servlet.servletClassName, servlet.urls, servlet.paramList, servlet.loadOnStartUp);
						}
						for( Iterator itr = filters.values().iterator(); itr.hasNext();)
						{
							Filter filter = (Filter)itr.next();
							webApp.registerFilter(filter.filterName, filter.filterClassName, filter.paramList, filter.urlPatterns);
						}
						for( Iterator itr = resourceRefs.values().iterator(); itr.hasNext();)
						{
							ResourceRef resourceRef = (ResourceRef)itr.next();
							webApp.addResourceRef(resourceRef);
						}
						for( Iterator itr = environmentEntries.values().iterator(); itr.hasNext();)
						{
							EnvEntry envEntry = (EnvEntry)itr.next();
							webApp.updateAddEnvironmentEntry(envEntry);
						}
						if( isDistributable != null )
						{
							webApp.updateDistributable();
						}
					}
				}
			}, path);
		}
	}
	
}

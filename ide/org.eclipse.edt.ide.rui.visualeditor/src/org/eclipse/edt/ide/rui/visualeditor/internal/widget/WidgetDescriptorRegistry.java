/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.swt.widgets.Display;


public class WidgetDescriptorRegistry implements IWidgetDescriptorRegistry {
	
	protected static HashMap	_widgetDescriptorMap		= new HashMap();
	/**
	 * A class that runs the registry initialization in its own thread.
	 */
	protected class Updater extends Job {
		
		private class UpdaterSchedulingRule implements ISchedulingRule{

			public boolean contains(ISchedulingRule rule) {
				if( this == rule ){
					return true;
				}
				
				return false;
			}

			public boolean isConflicting(ISchedulingRule rule) {
				if(rule instanceof UpdaterSchedulingRule){
					return true;
				}
				return false;
			}			
		}
		
		boolean cancelled = false;
		
		public Updater() {
			super( Messages.NL_WidgetRegistryUpdateJob_Name );
			setRule(new UpdaterSchedulingRule());
		}
		
		protected IStatus run(IProgressMonitor monitor) {
			initializeRegistry(monitor);
			return Status.OK_STATUS;
		}
		
		/**
		 * Called by the updater thread to discover and register widgets.
		 * @param monitor 
		 */
		protected void initializeRegistry(IProgressMonitor monitor) {

			_hashDescriptorGroups.clear();
			_hashDescriptors.clear();
		
			try {
				long startTime = System.currentTimeMillis();
				//
				List projects = org.eclipse.edt.ide.core.internal.utils.Util.getEGLProjectPath(_project, false, false);
				for (Iterator iterator = projects.iterator(); iterator.hasNext();) {
					EGLProject eglProject = (EGLProject) iterator.next();
					IProject project = eglProject.getProject();
					
					WidgetDescriptorFactory factory = new WidgetDescriptorFactory( project );
					// Find all RUIWidgets in project
					IPart[] ruiWidgets = org.eclipse.edt.ide.rui.utils.Util.searchForRUIWidgets( project );
					for( int j = 0; j < ruiWidgets.length; j++ ) {
						if(ruiWidgets[j] instanceof SourcePart) {
							EGLFile file = (EGLFile)EGLCore.create( ruiWidgets[ j ].getResource() );
							createWidgetDescriptor(factory, org.eclipse.edt.ide.core.internal.utils.Util.stringArrayToQualifiedName(file.getPackageName()), file.getElementName(), ruiWidgets[j] );
						} else if(ruiWidgets[j] instanceof BinaryPart) {
							ClassFile file = (ClassFile) ((BinaryPart)ruiWidgets[j]).getClassFile();
							IEGLElement parent = file.getParent();
							File eglarFile = null;
							while( ! (parent instanceof EGLProject) ) {
								if ( parent instanceof EglarPackageFragmentRoot ) {
									eglarFile = eglProject.getEGLPathEntryFor( parent.getPath() ).getPath().toFile();
									break;
								}
								parent = parent.getParent();
							}
							createWidgetDescriptorFromEglar(factory, org.eclipse.edt.ide.core.internal.utils.Util.stringArrayToQualifiedName(file.getPackageName()), file.getElementName(), ruiWidgets[j], eglarFile );
						}
					}
				}
				
				//sort the widgets in the widget groups.
				Iterator iter = _hashDescriptorGroups.values().iterator();
				while( iter.hasNext() ) {
					((WidgetDescriptorGroup)iter.next()).sortWidgets();
				}
				
				//build hashDataTemplateMapping. 
				_hashDataTemplateMappings.clear();
				buildHashDataTemplateMapping();
				
				//
				debug( "Finished initializing widget registry in " + ( System.currentTimeMillis() - startTime ) + "ms." );
			}
			catch( Exception e ) {
				//TODO Log
				e.printStackTrace();
			}

			finally {
				// Notify the palette root
				//------------------------
				Display.getDefault().asyncExec( new Runnable() {
					public void run() {
						for( int i = 0; i < _listListeners.size(); ++i ) {
							IWidgetDescriptorRegistryListener listener = (IWidgetDescriptorRegistryListener)_listListeners.get( i );
							listener.widgetDescriptorRegistryChanged();
						}
					}
				} );
			}
		}
	}
	
	private void buildHashDataTemplateMapping(){
		//key:purpose.forArray.isContainer.eglDataType
		Collection descriptors = _hashDescriptors.values();
		Iterator iterator = descriptors.iterator();
		while(iterator.hasNext()){
			WidgetDescriptor descriptor = (WidgetDescriptor)iterator.next();
			if(descriptor.getDataTemplates() != null){
				for(DataTemplate dataTemplate : descriptor.getDataTemplates()){
					EnumerationEntry[] purposes = dataTemplate.getPurposes();
					for(EnumerationEntry _purpose : purposes){
						DataMapping dataMapping = dataTemplate.getDataMapping();
						EnumerationEntry[] mappings = dataMapping.getMappings();
						for(EnumerationEntry mapping : mappings){
							String key = new StringBuffer(_purpose.getId()).append(".").append(dataMapping.isForArray()).append(".").append(dataMapping.isContainer()).append(".").append(mapping.getId()).toString();
							List<DataTemplate> dataTemplates = _hashDataTemplateMappings.get(key.toString());
							if(dataTemplates == null){
								dataTemplates = new ArrayList<DataTemplate>();
								_hashDataTemplateMappings.put(key, dataTemplates);
							}
							dataTemplates.add(dataTemplate);
//							System.out.println(key + ": " + dataTemplate.getName());
						}
						
					}
				}
			}
		}
		
		
	}
	
	protected class UserUpdater extends Updater{
		public UserUpdater(){
			super();
			setUser(true);
			setSystem(false);
		}
	}
	
	protected class SystemUpdater extends Updater{
		public SystemUpdater(){
			super();
			setUser(false);
			setSystem(true);
		}
	}

	/**
	 * 
	 */
	public static WidgetDescriptorRegistry getInstance(IProject project) {
		WidgetDescriptorRegistry	_instance	= null;
		if (WidgetDescriptorRegistry._widgetDescriptorMap.containsKey(project.getName())) {
			_instance = (WidgetDescriptorRegistry)WidgetDescriptorRegistry._widgetDescriptorMap.get(project.getName());
		} else {
			_instance = new WidgetDescriptorRegistry(project);
			_instance.initializeRegistry();
			WidgetDescriptorRegistry._widgetDescriptorMap.put(project.getName(), _instance);
		}
		return _instance;
	}

	protected TreeMap	_hashDescriptorGroups	= new TreeMap();
	protected HashMap	_hashDescriptors		= new HashMap();
	protected HashMap<String, List<DataTemplate>> _hashDataTemplateMappings	= new HashMap<String, List<DataTemplate>>();
	protected ArrayList	_listListeners			= new ArrayList();
	protected IProject 	_project				= null;

	/**
	 * Prevented from creating multiple instances.  Use getInstance() method to obtain the singleton.
	 * This constructor initializes the widget descriptor registry from information stored in
	 * the widget provider extension point.
	 */
	private WidgetDescriptorRegistry(IProject project) {
		this._project = project;
	}

	/**
	 * This will only be called once by the EvPaletteRoot singleton instance, and by each
	 * editor instance.  The editors remove their listeners when closing.
	 */
	public void addWidgetDescriptorRegistryListener(IWidgetDescriptorRegistryListener listener ) {
		if( _listListeners.contains( listener ) == false )
			_listListeners.add( listener );
	}
	
	protected void createWidgetDescriptorFromEglar(WidgetDescriptorFactory factory, String packageName, String elementName, IPart part, File eglarFile) {
		factory.setEglarFile( eglarFile );
		createWidgetDescriptor( factory, packageName, elementName, part );
	}

	/**
	 * Creates a descriptor for a widget.
	 */
	protected void createWidgetDescriptor(WidgetDescriptorFactory factory, String packageName, String elementName, IPart part) {
		// Create descriptor for widget
		WidgetDescriptor widgetDescriptor = factory.createWidgetDescriptor( NameUtile.getAsName( packageName ), NameUtile.getAsName( part.getElementName() ) );
		//
		if( widgetDescriptor != null ) {
			debug("RUIWidget: " + elementName);
			String groupName = widgetDescriptor.getGroup();
			
			if(groupName != null && groupName != ""){
				WidgetDescriptorGroup widgetGroup = (WidgetDescriptorGroup)_hashDescriptorGroups.get( groupName );
				//
				if( widgetGroup == null ) {
					widgetGroup = new WidgetDescriptorGroup();
					widgetGroup.setID( widgetDescriptor._strGroup );
					widgetGroup.setName( widgetDescriptor._strGroup );
					_hashDescriptorGroups.put( widgetGroup.getID(), widgetGroup );
				}
				if( widgetGroup != null ) {
					widgetGroup.addWidgetDescriptor( widgetDescriptor );
				}
			}
			//
			_hashDescriptors.put( widgetDescriptor.getID(), widgetDescriptor );
		}
	}
	
	private List getParts(IEGLFile file){
		List result = new ArrayList();
		
		try {
			IPart[] allParts = file.getAllParts();
			for (int i = 0; i < allParts.length; i++) {
				IPart part = allParts[i];
				if(((SourcePart)part).isExternalType() || ((SourcePart)part).isHandler()){
					result.add(part);
				}
			}
		} catch (EGLModelException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error reading parts from file: " + file.getElementName(), e));
		}
		return result;
	}

	/**
	 * Declared in IWidgetDescriptorRegistry.
	 * Returns the descriptor for a widget given its ID which is the concatenation of
	 * the widget's project name, EvConstants.WIDGET_ID_SEPARATOR widget package name, EvConstants.WIDGET_ID_SEPARATOR, and widget type name 
	 */
	public WidgetDescriptor getDescriptor( String strWidgetTypeID ) {
		if( strWidgetTypeID == null )
			return null;

		// Look for a fully qualified ID consisting of the
		// widget type's project name, package name and type name
		//------------------------------------------------------- 
		if( _hashDescriptors.containsKey( strWidgetTypeID ) == true )
			return (WidgetDescriptor)_hashDescriptors.get( strWidgetTypeID );
		
		return null;
	}

	/**
	 * Declared in IWidgetDescriptorRegistry.
	 * Returns the widget descriptor with the specified identifier. 
	 */
	public WidgetDescriptor getDescriptor( String strPackageName, String strWidgetType ) {
		if( strPackageName == null || strWidgetType == null )
			return null;
		
		String strWidgetID = strPackageName + Util.WIDGET_ID_SEPARATOR + strWidgetType;
		
		// An ID with the widget package and widget type name, or just the widget type name
		//---------------------------------------------------------------------------------
		Set setKeys = _hashDescriptors.keySet();
		Iterator iterKeys = setKeys.iterator();
		
		while( iterKeys.hasNext() ){
			String strDescriptorID = (String)iterKeys.next();

			if( strDescriptorID.endsWith( strWidgetID ) == true ){
				return (WidgetDescriptor)_hashDescriptors.get( strDescriptorID );
			}
		}
		
		return null;
	}

	/**
	 * Declared in IWidgetDescriptorRegistry.
	 * Returns an ordered set of descriptor groups.
	 */
	public Iterator getDescriptorGroups() {
		return _hashDescriptorGroups.values().iterator();
	}

	/**
	 * Initializes the registry
	 */
	private void initializeRegistry() {
		new SystemUpdater().schedule();
	}

	/**
	 * Empties and refills the registry, then notifies the palette root.
	 * This is called by the design page when the 'Refresh palette' toolbar button is pressed.
	 */
	public void reinitialize() {
		new UserUpdater().schedule();
	}
	
	/**
	 * This is called by each editor instance when the editor is disposed.
	 */
	public void removeWidgetDescriptorRegistryListener( IWidgetDescriptorRegistryListener listener ) {
		if( _listListeners.contains( listener ) == true )
			_listListeners.remove( listener );
	}

	/**
	 * 
	 * @param s
	 */
	private void debug(String s) {
//		System.out.println(s);
	}

	public List<DataTemplate> getMappingDescriptorDataTemplates(String purpose, String eglDataType, Set<String> eglDataTypeDetails, boolean forArray, boolean isContainer) {
		List<DataTemplate> dataTemplates = new ArrayList<DataTemplate>();
		String firstKey = new StringBuffer(purpose).append(".").append(forArray).append(".").append(isContainer).append(".").toString();
		eglDataTypeDetails.add(eglDataType);
		
		for(String eglDataTypeDetail : eglDataTypeDetails){
			List<DataTemplate> fitDataTemplates = _hashDataTemplateMappings.get(firstKey + eglDataTypeDetail);
			if(fitDataTemplates != null && !fitDataTemplates.isEmpty()){
				dataTemplates.addAll(fitDataTemplates);
			}
		}
		
		return dataTemplates;
	}

}

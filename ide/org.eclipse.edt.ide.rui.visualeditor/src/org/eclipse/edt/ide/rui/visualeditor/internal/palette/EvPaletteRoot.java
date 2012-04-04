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
package org.eclipse.edt.ide.rui.visualeditor.internal.palette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.IWidgetDescriptorRegistryListener;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetCreationFactory;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorGroup;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;


public class EvPaletteRoot extends PaletteRoot implements IWidgetDescriptorRegistryListener, IPropertyChangeListener{

	protected Map _mapNodeTypeToImageDescriptor	= new TreeMap();
	protected static HashMap _paletteRootMap = new HashMap();
	private IProject project = null;
	//
	public static EvPaletteRoot getInstance(IProject project) {
		EvPaletteRoot _instance = null;
		if (EvPaletteRoot._paletteRootMap.containsKey(project.getName())) {
			_instance = (EvPaletteRoot)EvPaletteRoot._paletteRootMap.get(project.getName());
		} else {
			_instance = new EvPaletteRoot(project);
			IPreferenceStore preferences = EDTCoreIDEPlugin.getPlugin().getPreferenceStore();
			preferences.addPropertyChangeListener( _instance ); 			// IBMBIDI Append		
			EvPaletteRoot._paletteRootMap.put(project.getName(), _instance);
		}
		return _instance;
	}

	/**
	 * Returns an image descriptor given a node type ID.
	 */
	public static ImageDescriptor getImageDescriptorForNodeType( String strNodeTypeID ) {
		
		Iterator iterRoot = EvPaletteRoot._paletteRootMap.values().iterator();
		while (iterRoot.hasNext()){
			EvPaletteRoot root = (EvPaletteRoot)iterRoot.next();
			// Check for exact match: Project@@Package@@TypeName
			//--------------------------------------------------
			if( root._mapNodeTypeToImageDescriptor.containsKey( strNodeTypeID ) == true )
				return (ImageDescriptor)root._mapNodeTypeToImageDescriptor.get( strNodeTypeID );
			
			// Check for partial match: Package@@TypeName or TypeName
			//-------------------------------------------------------
			Set setKeys = root._mapNodeTypeToImageDescriptor.keySet();
			Iterator iterKeys = setKeys.iterator();
			
			while( iterKeys.hasNext() ){
				String strNodeID = (String)iterKeys.next();

				if( strNodeID.endsWith( strNodeTypeID ) == true ){
					return (ImageDescriptor)root._mapNodeTypeToImageDescriptor.get( strNodeID );
				}
			}			
		}
	
		return null; 
	}

	/**
	 * Creating of multiple instances is prevented.  Use the getInstance() method to obtain the singleton.
	 */
	private EvPaletteRoot(IProject project) {
		super();
		this.project = project;
		// Listen for changes to the widget descriptor registry
		///-----------------------------------------------------
		WidgetDescriptorRegistry.getInstance(project).addWidgetDescriptorRegistryListener( this );
	}

	/**
	 * 
	 */
	protected Object getPaletteItem( WidgetDescriptorGroup group ) {

		String strGroupName = group.getName();
		if(strGroupName == null){
			return null;
		}
		
		String strGroupNameTranslated = translateGroupName( strGroupName );
		
		PaletteDrawer drawer = new PaletteDrawer( strGroupNameTranslated );
		ImageDescriptor smallIcon = group.getIconSmall();
		ImageDescriptor largeIcon = group.getIconLarge();
		drawer.setSmallIcon( smallIcon );
		drawer.setLargeIcon( largeIcon );
		drawer.setInitialState( PaletteDrawer.INITIAL_STATE_CLOSED );

		List drawerEntries = new ArrayList();

		ArrayList listWidgetDescriptors = group.getWidgetDescriptors();
		// IBMBIDI Change Start
		boolean isBidi = BidiUtils.isBidi();
		for( int i = 0; i < listWidgetDescriptors.size(); i++ ) {
			WidgetDescriptor widgetDescriptor = (WidgetDescriptor)listWidgetDescriptors.get( i );
			CombinedTemplateCreationEntry groupElement = getPaletteEntry( widgetDescriptor );
			if (isBidi || (!isBidi && !groupElement.getLabel().startsWith("BIDI")))
				drawerEntries.add( groupElement );		
		}
		// IBMBIDI Change End
		if(drawerEntries.isEmpty()){
			return null;
		}else{
			drawer.addAll( drawerEntries );
			return drawer;
		}
		
	}

	/**
	 * 
	 */
	protected CombinedTemplateCreationEntry getPaletteEntry( WidgetDescriptor descriptor ) {
		CombinedTemplateCreationEntry entry = null;

		ImageDescriptor smallIcon = descriptor.getIconSmall();
		ImageDescriptor largeIcon = descriptor.getIconLarge();

		String strID      	  = descriptor.getID();
		String strLabel       = (descriptor.getProvider() != null && descriptor.getProvider().length() != 0) ? descriptor.getLabel() + " (" + descriptor.getProvider() + ")" : descriptor.getLabel();
		String strDescription = descriptor.getDescription();

		entry = new EvPaletteCreationEntry( strLabel, strDescription, strID, WidgetCreationFactory.getInstance(), smallIcon, largeIcon, false );

		_mapNodeTypeToImageDescriptor.put( strID, smallIcon );

		return entry;
	}

	/**
	 * Translates a widget category into a translated name for presenting as a group label.
	 */
	protected String translateGroupName( String strGroupName ){
		if( strGroupName == null )
			return null;
		
		if( strGroupName.equals( "EGL Widgets" ) == true )
			return Messages.NL_EGL_Widgets;
		
		return strGroupName;
	}
	
	/**
	 * 
	 */
	public void updatePalette(){
		
		List listPaletteDrawers = new ArrayList();

		// Add selection and marquee tools to a Palette drawer
		//----------------------------------------------------		
		if( false ) {
			PaletteDrawer paletteDrawer = new PaletteDrawer( "Tools" );

			// Selection tool
			//---------------
			ToolEntry tool = new SelectionToolEntry();
			paletteDrawer.add( tool );
			setDefaultEntry( tool );

			// Marquee selection tool
			//-----------------------
			paletteDrawer.add( new MarqueeToolEntry() );

			listPaletteDrawers.add( paletteDrawer );
		}

		// Add all other palette drawers
		//------------------------------		
		WidgetDescriptorRegistry registry = WidgetDescriptorRegistry.getInstance(this.project);
		
		Iterator iterGroups = registry.getDescriptorGroups();

		while( iterGroups.hasNext() ) {
			WidgetDescriptorGroup group = (WidgetDescriptorGroup)iterGroups.next();
			Object objPaletteItem = getPaletteItem( group );

			if( objPaletteItem instanceof PaletteDrawer == false )
				continue;

			PaletteDrawer drawer = (PaletteDrawer)objPaletteItem;
			drawer.setInitialState( PaletteDrawer.INITIAL_STATE_PINNED_OPEN );
			listPaletteDrawers.add( drawer );
		}

		// Add palette drawers to this palette container
		//----------------------------------------------
		setChildren( listPaletteDrawers );
	}
	
	/**
	 * Declared in IWidgetDescriptorRegistryListener.
	 * Called when the descriptor registry has changed.
	 */
	public void widgetDescriptorRegistryChanged() {
		updatePalette();
	}
	
	// IBMBIDI Append Start
	public void propertyChange(PropertyChangeEvent event) {
//TODO EDT BIDI
//		if (EDTCoreIDEPlugin.BIDI_ENABLED_OPTION.equals(event.getProperty()))
//			updatePalette();
	}
	// IBMBIDI Append End
}

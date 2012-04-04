/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jst.j2ee.common.EnvEntry;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.wst.common.componentcore.ComponentCore;

public class WebUtilities
{

    /**
	 * Update the deployment descriptor.
	 *
	 * @param 	beanName	Bean name needed for EJBs
	 * @param  options		Compiler options
	 * @param  envEntries  List of environment entries
	 */
	/*TODO - EDT
	public static void updateDeploymentDescriptor( String beanName, final BuildDescriptor options, ArrayList envEntries )
	{
		//Run the updater.  If something goes wrong, an exception will be thrown.
		runUpdater( new DeploymentDescriptorUpdater( beanName, options, envEntries ) );
	}
	*/
 	
	/**
     * Sets up up the undo management and call individual methods that
     * add entries to the web.XML file.
     * 
     * @param projectName  The name of the Web project
     * @param envEntries   The new environment entries to add
     */
    public static void addEntriesToWebXML( IProject projectName, ArrayList envEntries )
    {
		WebArtifactEdit webEditModel = null;
		try 
		{
			webEditModel = WebArtifactEdit.getWebArtifactEditForWrite( projectName );
			if ( webEditModel != null )
			{
				WebApp webApp = webEditModel.getWebApp();
				if ( webApp != null )
				{
					addEnvEntry( webApp, envEntries );
				}
				webEditModel.saveIfNecessary( null );
			}
		}
		catch ( Exception e )
		{
			//TODO - EDT
//			throw new JavaGenException( e );
		}
		finally 
		{
			if ( webEditModel != null ) 
			{
				//Release the edit model
				webEditModel.dispose();
			}
		}
	}
    
	/**
     * Add or update environment entries in the Web project.
     * If the environment variable already exists in the deployment
     * descriptor, update the value and type.  Otherwise, add a new entry.
     * 
     * @param webApp         The web project containing the deployment descriptor
     * @param newEnvEntries  The new environment entries to add
     */
    private static void addEnvEntry( WebApp webApp, ArrayList newEnvEntries )
    {
     	EList oldEnvEntries = webApp.getEnvironmentProperties();
       	Iterator iterator = newEnvEntries.iterator();
       	EnvEntry envEntry;
		while ( iterator.hasNext() )
		{
			envEntry = (EnvEntry)iterator.next();
			if ( !updateEntry( envEntry, oldEnvEntries ) )
			{
				webApp.getEnvironmentProperties().add( envEntry );
			}
	    }
    }
	/**
     *  Loop through existing entries looking for matching name.
	 *  If found, reset the value and type only if the entry starts
	 *  with <code>cso.linkageOptions.</code>.  There should only be
	 *  one entry per name but if there are more than one, update
	 *  them all.
	 *  @param  newEnvEntry    New environment entry
	 *  @param  oldEnvEntries  Existing environment entries
	 *  @return boolean  		Whether or not this method found a matching entry
	 */
    protected static boolean updateEntry( EnvEntry newEnvEntry, EList oldEnvEntries )
    {
		Iterator oldIterator = oldEnvEntries.iterator();
		boolean found = false;
		EnvEntry oldEntry;
			
		while ( oldIterator.hasNext() )
		{
			oldEntry = (EnvEntry)(oldIterator.next());
			if ( oldEntry.getName().equals(newEnvEntry.getName()) )
			{
				//If the entry was found then update it only if it is not one 
				//of the options below.
				if ( !oldEntry.getName().equalsIgnoreCase("vgj.trace.type") &&
					 !oldEntry.getName().equalsIgnoreCase("vgj.trace.device.option") &&
					 !oldEntry.getName().equalsIgnoreCase("vgj.trace.device.spec") )
				{
					oldEntry.setValue( newEnvEntry.getValue() );
					oldEntry.setType( newEnvEntry.getType() );
				}
				found = true;
			}
		}
		return found;
    }
    
	/**
	 * Returns the path for the Web module folder for the specified project.
	 * Returns null if it can't.
	 * 
	 * @param project  The project.
	 * @return Web module's IPath
	 */
	public static IPath getWebModulePath( IProject project )
	{
		return ComponentCore.createComponent(project).getRootFolder().getUnderlyingFolder().getFullPath();
	}
	
	public static String getContextRoot( IProject project ) {
		String contextRoot = null;
		if( project != null )
		{
			String warFileName = project.getName() + ".war";
			IProject[] projects = J2EEProjectUtilities.getReferencingEARProjects(project);
			if( projects != null )
			{
				int i = 0;
				for( int idx = 0; idx < projects.length; idx++ )
				{
					IModelProvider model = ModelProviderManager.getModelProvider( projects[idx] );
					Object obj = model.getModelObject();
					List ears = null;
					Object module;
					if( obj instanceof org.eclipse.jst.javaee.application.Application )
					{
						ears = ((org.eclipse.jst.javaee.application.Application)obj).getModules();
						if( ears != null )
						{
							org.eclipse.jst.javaee.application.Web war;
							for( Iterator itr = ears.iterator(); itr.hasNext(); )
							{
								module = itr.next();
								if( module instanceof org.eclipse.jst.javaee.application.Module )
								{
									war = ((org.eclipse.jst.javaee.application.Module)module).getWeb();
									if(war != null && warFileName.equalsIgnoreCase(war.getWebUri()))
									{
										contextRoot = war.getContextRoot();
										break;
									}
								}
							}
						}
					}
					else if( obj instanceof org.eclipse.jst.j2ee.application.Application )
					{
						ears = ((org.eclipse.jst.j2ee.application.Application)obj).getModules();
						if( ears != null )
						{
							for( Iterator itr = ears.iterator(); itr.hasNext(); )
							{
								module = itr.next();
								if( module instanceof org.eclipse.jst.j2ee.application.WebModule )
								{
									if(warFileName.equalsIgnoreCase(((org.eclipse.jst.j2ee.application.WebModule)module).getUri()))
									{
										contextRoot = ((org.eclipse.jst.j2ee.application.WebModule)module).getContextRoot();
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		if( contextRoot == null || contextRoot.length() == 0)
		{
			contextRoot = J2EEProjectUtilities.getServerContextRoot(project);
		}
		return contextRoot;
	}
}

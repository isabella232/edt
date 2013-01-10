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
package org.eclipse.edt.ide.deployment.rui.internal.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.deployment.core.IDeploymentConstants;
import org.eclipse.edt.ide.deployment.internal.Logger;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.rui.internal.HelpContextIDs;
import org.eclipse.edt.ide.deployment.rui.internal.editor.RUIFormPage.RUIHandlerRowItem;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.RUIApplication;
import org.eclipse.edt.ide.ui.internal.deployment.RUIHandler;
import org.eclipse.edt.ide.ui.internal.deployment.ui.DeploymentUtilities;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDBaseFormPage;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.deployment.ui.IEGLDDContributionPageProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RUIFormPageProvider implements IEGLDDContributionPageProvider
{
	public static final String PAGE_ID = "page.eglbind.rui"; //$NON-NLS-1$
	
	public EGLDDBaseFormPage createPage( FormEditor editor )
	{
		return new RUIFormPage( editor, PAGE_ID, Messages.editor_title );
	}
	
	public Table createTable( Composite client, FormToolkit toolkit )
	{
		return RUIFormPage.createRUIHandlerTableControl( toolkit, client, false );
	}
	
	public IContentProvider getTableContentProvider( IProject project )
	{
		return new RUIHandlerOverviewContentProvider( project );
	}
	
	public ILabelProvider getTableLabelProvider()
	{
		return new RUIFormPage.RUIHandlerLabelProvider();
	}
	
	public String getOverviewTitle()
	{
		return Messages.editor_master_page_title;
	}
	
	public String getOverviewDescription()
	{
		return Messages.editor_master_page_desc;
	}
	
	public String getTargetReferenceTitle()
	{
		return Messages.target_detail_page_rui_section_title;
	}
	
	public String getTargetReferenceDescription()
	{
		return Messages.target_detail_page_rui_section_desc;
	}
	
	public String getDetailPageId()
	{
		return PAGE_ID;
	}
	
	public String getHelpId()
	{
		return HelpContextIDs.RUI_DD_EDITOR_OVERVIEW_PAGE;
	}
	
	private static class RUIHandlerOverviewContentProvider implements IStructuredContentProvider
	{
		private IEGLProject project;
		
		public RUIHandlerOverviewContentProvider( IProject project )
		{
			this.project = EGLCore.create( project );
		}
		
		/**
		 * Display only the selected handlers.
		 */
		public Object[] getElements( Object inputElement )
		{
			List children = new ArrayList();
			
			if ( inputElement instanceof EGLDeploymentRoot )
			{
				EGLDeploymentRoot root = (EGLDeploymentRoot)inputElement;
				try
				{
					RUIApplication application = root.getDeployment().getRuiapplication();
					boolean displayAll = application == null || application.isDeployAllHandlers();
					
					int i = 0;
					if ( displayAll )
					{
						Map map = DeploymentUtilities.getAllRUIHandlersInProject( project );
						DeploymentFactory factory = DeploymentFactory.eINSTANCE;
						
						for ( Iterator it = map.entrySet().iterator(); it.hasNext(); )
						{
							Map.Entry next = (Map.Entry)it.next();
							String impl = (String)next.getKey();
							
							RUIHandler handler = factory.createRUIHandler();
							handler.setEnableGeneration( true );
							handler.setImplementation( impl );
							
							Parameters parms = factory.createParameters();
							EGLDDRootHelper.addOrUpdateParameter( parms, IDeploymentConstants.PARAMETER_HTML_FILE_NAME, (String)next.getValue() );
							handler.setParameters( parms );
							
							RUIHandlerRowItem row = new RUIHandlerRowItem( handler, i );
							children.add( row );
							i++;
						}
					}
					else
					{
						for ( Iterator it = application.getRuihandler().iterator(); it.hasNext(); )
						{
							RUIHandler nextHandler = (RUIHandler)it.next();
							if ( nextHandler.isEnableGeneration() && project.findPart( nextHandler.getImplementation() ) != null )
							{
								RUIHandlerRowItem row = new RUIHandlerRowItem( nextHandler, i );
								children.add( row );
								i++;
							}
						}
					}
				}
				catch ( EGLModelException e )
				{
					e.printStackTrace();
					Logger.logException( e );
				}
			}
			
			return children.toArray();
		}
		
		public void dispose() {}
		public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {}
	}
}

/*******************************************************************************
 * Copyright © 2009, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.ide.deployment.internal.actions.DeployAction;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.rui.internal.Images;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDeploymentDescriptorEditor;
import org.eclipse.edt.ide.ui.internal.deployment.ui.IEGLDDContributionToolbarProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

/**
 * Adds a 'Deploy' button to every page in the DD editor.
 */
public class RUIDDToolbarProvider implements IEGLDDContributionToolbarProvider
{
	public Action[] getActions( EGLDeploymentDescriptorEditor editor )
	{
		IEditorInput input = editor.getEditorInput();
		if ( input instanceof IFileEditorInput )
		{
			IFile file = ((IFileEditorInput)input).getFile();
			if ( file != null )
			{
				return new Action[]{ new DeployToolbarAction( file ) };
			}
		}
		
		return new Action[ 0 ];
	}
	
	private static class DeployToolbarAction extends Action
	{
		private final IFile eglddFile;
		
		public DeployToolbarAction( IFile file )
		{
			super( Messages.deployment_toolbar_deploy_text, Images.getDeployDesciptorImage() );
			setToolTipText( Messages.deployment_toolbar_deploy_tooltip );
			this.eglddFile = file;
		}

		public void run()
		{
			DeployAction action = new DeployAction();
			action.selectionChanged( this, new StructuredSelection( eglddFile ) );
			action.run(this);
		}
	}
}

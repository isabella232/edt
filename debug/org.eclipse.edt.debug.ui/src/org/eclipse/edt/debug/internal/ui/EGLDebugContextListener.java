/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Keeps track of the active context. When an EGL debugger is running, the "eglDebuggerActive" system property is set to true.
 */
public class EGLDebugContextListener implements IDebugContextListener
{
	private static final String EGL_DEBUGGER_ACTIVE = EDTDebugUIPlugin.PLUGIN_ID + ".eglDebuggerActive"; //$NON-NLS-1$
	
	private static final EGLDebugContextListener INSTANCE = new EGLDebugContextListener();
	
	private final Set<IWorkbenchPage> activePages;
	
	private EGLDebugContextListener()
	{
		activePages = new HashSet<IWorkbenchPage>();
		DebugUITools.getDebugContextManager().addDebugContextListener( this );
	}
	
	public static EGLDebugContextListener getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public void debugContextChanged( DebugContextEvent event )
	{
		if ( (event.getFlags() & DebugContextEvent.ACTIVATED) > 0 )
		{
			IWorkbenchPart part = event.getDebugContextProvider().getPart();
			if ( part != null )
			{
				IWorkbenchPage page = part.getSite().getPage();
				ISelection selection = event.getContext();
				if ( selection instanceof IStructuredSelection )
				{
					IStructuredSelection ss = (IStructuredSelection)selection;
					if ( ss.size() == 1 )
					{
						Object element = ss.getFirstElement();
						if ( element instanceof IAdaptable )
						{
							IEGLStackFrame frame = (IEGLStackFrame)((IAdaptable)element).getAdapter( IEGLStackFrame.class );
							if ( frame != null )
							{
								activePages.add( page );
								System.setProperty( EGL_DEBUGGER_ACTIVE, "true" ); //$NON-NLS-1$
								return;
							}
						}
					}
				}
				activePages.remove( page );
				if ( activePages.isEmpty() )
				{
					System.setProperty( EGL_DEBUGGER_ACTIVE, "false" ); //$NON-NLS-1$
				}
			}
		}
	}
}

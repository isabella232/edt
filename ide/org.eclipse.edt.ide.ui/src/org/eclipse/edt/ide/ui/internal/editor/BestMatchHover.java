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
package org.eclipse.edt.ide.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class BestMatchHover extends AbstractEGLTextHover implements ITextHoverExtension, ITextHoverExtension2
{
	private static final String eglTextHovers = "eglTextHovers";
	
	private List hovers;
	private EGLTextHover defaultHover;
	private ITextHover bestHover;
	
	public BestMatchHover( IEditorPart editor )
	{
		setEditor( editor );
		installHovers();
	}
	
	public String getHoverInfo( ITextViewer textViewer, IRegion hoverRegion )
	{
		bestHover = null;
		int size = hovers.size();
		for ( int i = 0; i < size; i++ )
		{
			ITextHover hover = (ITextHover)hovers.get( i );
			
			String s = hover.getHoverInfo( textViewer, hoverRegion );
			if ( s != null && s.trim().length() > 0 )
			{
				bestHover = hover;
				return s;
			}
		}
		
		// next try the default hover
		if ( defaultHover == null )
		{
			defaultHover = new EGLTextHover();
			defaultHover.setEditor( getEditor() );
		}
		
		bestHover = defaultHover;
		return defaultHover.getHoverInfo( textViewer, hoverRegion );
	}
	
	public Object getHoverInfo2( ITextViewer textViewer, IRegion hoverRegion )
	{
		bestHover = null;
		int size = hovers.size();
		for ( int i = 0; i < size; i++ )
		{
			ITextHover hover = (ITextHover)hovers.get( i );
			
			if ( hover instanceof ITextHoverExtension2 )
			{
				Object o = ((ITextHoverExtension2)hover).getHoverInfo2( textViewer, hoverRegion );
				if ( o != null )
				{
					bestHover = hover;
					return o;
				}
			}
			else
			{
				String s = hover.getHoverInfo( textViewer, hoverRegion );
				if ( s != null && s.trim().length() > 0 )
				{
					bestHover = hover;
					return s;
				}
			}
		}
		
		// Next try the default hover.
		if ( defaultHover == null )
		{
			defaultHover = new EGLTextHover();
			defaultHover.setEditor( getEditor() );
		}
		
		bestHover = defaultHover;
		return defaultHover.getHoverInfo( textViewer, hoverRegion );
	}

	private void installHovers()
	{
		hovers = new ArrayList();
		
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor( EDTUIPlugin.PLUGIN_ID, eglTextHovers );
		for ( int i = 0; i < elements.length; i++ )
		{
			try
			{
				Object hover = elements[ i ].createExecutableExtension( "class" );
				if ( hover instanceof AbstractEGLTextHover )
				{
					((AbstractEGLTextHover)hover).setEditor( getEditor() );
					hovers.add( hover );
				}
				else if ( hover instanceof ITextHover )
				{
					hovers.add( hover );
				}
				
			}
			catch ( CoreException ce )
			{
			}
		}
	}
	
	public IInformationControlCreator getHoverControlCreator()
	{
		if ( bestHover instanceof ITextHoverExtension )
		{
			return ((ITextHoverExtension)bestHover).getHoverControlCreator();
		}
		
		return null;
	}
}

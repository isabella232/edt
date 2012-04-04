/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.debug.javascript.EDTJavaScriptDebugPlugin;
import org.eclipse.edt.ide.debug.javascript.internal.server.DebugContext;
import org.eclipse.edt.ide.debug.javascript.internal.utils.RUIDebugUtil;
import org.eclipse.edt.ide.rui.server.IContext;
import org.eclipse.edt.ide.rui.server.IContextResolver;

public class RUIDebugContextResolver implements IContextResolver
{
	private static final int maxToRemember = 20;
	private static final RUIDebugContextResolver INSTANCE = new RUIDebugContextResolver();
	
	private final Map<Integer, DebugContext> contextMap;
	private final Map<Integer, DebugContext> terminatedContextMap;
	
	private RUIDebugContextResolver()
	{
		contextMap = new HashMap<Integer, DebugContext>();
		terminatedContextMap = new LinkedHashMap<Integer, DebugContext>( maxToRemember, 0.75f, true ) {
			private static final long serialVersionUID = 1L;
			
			protected boolean removeEldestEntry( java.util.Map.Entry eldest )
			{
				return size() >= maxToRemember;
			}
		};
	}
	
	public static RUIDebugContextResolver getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public IContext findContext( Integer contextKey )
	{
		synchronized ( contextMap )
		{
			DebugContext context = contextMap.get( contextKey );
			if ( context != null )
			{
				return context;
			}
			
			context = terminatedContextMap.get( contextKey );
			if ( context != null )
			{
				// Relaunch its target.
				try
				{
					RUIDebugUtil.relaunchContext( context );
					
					// If it was successful, it will have been added to contextMap.
					return contextMap.get( contextKey );
				}
				catch ( CoreException e )
				{
					EDTJavaScriptDebugPlugin.log( e );
				}
			}
		}
		
		return null;
	}
	
	public void addContext( DebugContext context )
	{
		synchronized ( contextMap )
		{
			contextMap.put( context.getKey(), context );
			terminatedContextMap.remove( context.getKey() );
		}
	}
	
	public void removeContext( DebugContext context )
	{
		synchronized ( contextMap )
		{
			contextMap.remove( context.getKey() );
			terminatedContextMap.put( context.getKey(), context );
			context.clear();
		}
	}
}

/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.debug.core.java.filters.ClasspathEntryFilter;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.ide.core.EDTRuntimeContainerEntry;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * Filters out classes within the EDT runtime containers (contributed via the generators).
 */
public class JavaRuntimeContainerFilter extends ClasspathEntryFilter
{
	@Override
	protected IClasspathEntry[] getCommonClasspathEntries()
	{
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		for ( IGenerator gen : EDTCoreIDEPlugin.getPlugin().getGenerators() )
		{
			EDTRuntimeContainer[] containers = gen.getRuntimeContainers();
			if ( containers != null )
			{
				for ( EDTRuntimeContainer container : containers )
				{
					EDTRuntimeContainerEntry[] containerEntries = container.getEntries();
					if ( containerEntries != null )
					{
						for ( EDTRuntimeContainerEntry nextEntry : containerEntries )
						{
							IClasspathEntry cpEntry = nextEntry.getClasspathEntry();
							if ( cpEntry != null )
							{
								list.add( cpEntry );
							}
						}
					}
				}
			}
		}
		return list.toArray( new IClasspathEntry[ list.size() ] );
	}
}

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
package org.eclipse.edt.debug.ui.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.JavaLaunchableTester;

@SuppressWarnings("restriction")
public class EGLLaunchableTester extends PropertyTester
{
	private static final String PROPERTY_HAS_PROJECT_NATURES = "hasProjectNatures"; //$NON-NLS-1$
	private static final String HAS_JAVA_MAIN_PROGRAM = "hasJavaMainProgram"; //$NON-NLS-1$
	
	private static final JavaLaunchableTester tester = new JavaLaunchableTester();
	
	protected Object resolveElement( Object receiver )
	{
		Object element = null;
		if ( receiver instanceof IAdaptable )
		{
			IEGLElement eglElement = (IEGLElement)((IAdaptable)receiver).getAdapter( IEGLElement.class );
			if ( eglElement != null && eglElement.exists() )
			{
				element = eglElement;
			}
			else
			{
				IFile ifile = (IFile)((IAdaptable)receiver).getAdapter( IFile.class );
				if ( ifile != null && ifile.exists() )
				{
					element = ifile;
				}
			}
		}
		return element;
	}
	
	@Override
	public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
	{
		Object element = resolveElement( receiver );
		if ( element == null )
		{
			return false;
		}
		
		if ( HAS_JAVA_MAIN_PROGRAM.equals( property ) )
		{
			return hasJavaMainProgram( element );
		}
		if ( PROPERTY_HAS_PROJECT_NATURES.equals( property ) )
		{
			return args.length > 0 && hasProjectNatures( element, args );
		}
		
		return false;
	}
	
	private boolean hasJavaMainProgram( Object element )
	{
		IFile eglFile = null;
		if ( element instanceof IFile )
		{
			eglFile = (IFile)element;
		}
		else if ( element instanceof IEGLElement )
		{
			IResource resource = ((IEGLElement)element).getResource();
			if ( resource.getType() == IResource.FILE )
			{
				eglFile = (IFile)resource;
			}
		}
		
		if ( eglFile != null )
		{
			List<IFile> files = getMainJavaOutputFiles( eglFile );
			return files != null && files.size() > 0;
		}
		
		return false;
	}
	
	private boolean hasProjectNatures( Object element, Object[] natures )
	{
		IProject project = null;
		if ( element instanceof IEGLElement )
		{
			IEGLProject eproj = ((IEGLElement)element).getEGLProject();
			if ( eproj != null )
			{
				project = eproj.getProject();
			}
		}
		else if ( element instanceof IFile )
		{
			project = ((IFile)element).getProject();
		}
		
		if ( project != null )
		{
			return hasProjectNatures( project, natures );
		}
		return false;
	}
	
	private boolean hasProjectNatures( IProject project, Object[] natures )
	{
		try
		{
			if ( project.isAccessible() )
			{
				for ( int i = 0; i < natures.length; i++ )
				{
					if ( !project.hasNature( (String)natures[ i ] ) )
					{
						return false;
					}
				}
				return true;
			}
		}
		catch ( CoreException ce )
		{
		}
		return false;
	}
	
	public static List<IFile> getMainJavaOutputFiles( IFile eglFile )
	{
		List<IFile> files = getJavaOutputFiles( eglFile );
		if ( files != null && files.size() > 0 )
		{
			List<IFile> mainFiles = new ArrayList<IFile>( files.size() );
			for ( IFile file : files )
			{
				if ( tester.test( file, "hasMain", new Object[ 0 ], null ) ) //$NON-NLS-1$
				{
					mainFiles.add( file );
				}
			}
			return mainFiles;
		}
		return null;
	}
	
	public static List<IFile> getJavaOutputFiles( IFile eglFile )
	{
		IGenerator[] gens = ProjectSettingsUtility.getGenerators( eglFile );
		if ( gens.length > 0 )
		{
			List<IGenerator> javaGens = new ArrayList<IGenerator>( gens.length );
			for ( IGenerator gen : gens )
			{
				if ( "Java".equalsIgnoreCase( gen.getLanguage() ) ) //$NON-NLS-1$
				{
					javaGens.add( gen );
				}
			}
			
			if ( javaGens.size() > 0 )
			{
				Object o = EGLCore.create( eglFile );
				if ( o instanceof IEGLFile )
				{
					IEGLFile element = (IEGLFile)o;
					ProjectEnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment( eglFile.getProject() );
					try
					{
						Environment.pushEnv( env.getIREnvironment() );
						env.initIREnvironments();
						
						List<IFile> files = new ArrayList<IFile>( javaGens.size() );
						
						String[] pkg;
						IPackageDeclaration[] pkgDecl = element.getPackageDeclarations();
						if ( pkgDecl != null && pkgDecl.length > 0 )
						{
							pkg = IRFileNameUtility.toIRFileName( pkgDecl[ 0 ].getElementName().split( "\\." ) ); //$NON-NLS-1$
						}
						else
						{
							pkg = new String[ 0 ];
						}
						
						for ( IPart ipart : element.getParts() )
						{
							if ( ipart instanceof SourcePart && ((SourcePart)ipart).isProgram() )
							{
								try
								{
									String name = IRFileNameUtility.toIRFileName( ipart.getElementName() );
									Part part = env.findPart( InternUtil.intern( pkg ), InternUtil.intern( name ) );
									for ( IGenerator gen : javaGens )
									{
										if ( gen instanceof org.eclipse.edt.ide.core.IGenerator )
										{
											try
											{
												for ( IFile file : ((org.eclipse.edt.ide.core.IGenerator)gen).getOutputFiles( eglFile, part ) )
												{
													if ( JavaCore.isJavaLikeFileName( file.getName() ) && !files.contains( file ) )
													{
														files.add( file );
													}
												}
											}
											catch ( CoreException ce )
											{
											}
										}
									}
								}
								catch ( PartNotFoundException e )
								{
									e.printStackTrace();
								}
							}
						}
						return files;
					}
					catch ( EGLModelException e )
					{
						e.printStackTrace();
					}
					finally
					{
						Environment.popEnv();
					}
				}
			}
		}
		return null;
	}
}

/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.internal.launching.JavaLaunchableTester;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.dialogs.ListDialog;

@SuppressWarnings("restriction")
public class EGLJavaLaunchShortcut extends AbstractEGLLaunchShortcut
{
	@Override
	public void doLaunch( IFile eglFile, String mode )
	{
		final List<IFile> files = getMainJavaOutputFiles( eglFile );
		if ( files == null || files.size() == 0 )
		{
			MessageDialog.openError( DebugUtil.getShell(), EGLLaunchingMessages.launch_error_dialog_title,
					NLS.bind( EGLLaunchingMessages.java_launch_no_files, eglFile.getName() ) );
			return;
		}
		
		IFile file = null;
		if ( files.size() > 1 )
		{
			ListDialog dialog = new ListDialog( DebugUtil.getShell() );
			dialog.setTitle( EGLLaunchingMessages.java_launch_file_selection_title );
			dialog.setMessage( EGLLaunchingMessages.java_launch_file_selection_msg );
			dialog.setContentProvider( new IStructuredContentProvider() {
				@Override
				public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
				{
				}
				
				@Override
				public void dispose()
				{
				}
				
				@Override
				public Object[] getElements( Object inputElement )
				{
					return files.toArray();
				}
			} );
			dialog.setLabelProvider( new LabelProvider() {
				public String getText( Object element )
				{
					if ( element instanceof IFile )
					{
						return ((IFile)element).getFullPath().toString();
					}
					return super.getText( element );
				}
			} );
			dialog.setInput( files );
			
			if ( dialog.open() == Window.OK )
			{
				Object[] result = dialog.getResult();
				if ( result != null && result.length > 0 && result[ 0 ] instanceof IFile )
				{
					file = (IFile)result[ 0 ];
				}
			}
		}
		else
		{
			file = files.get( 0 );
		}
		
		if ( file != null && file.exists() )
		{
			JavaApplicationLaunchShortcut shortcut = new JavaApplicationLaunchShortcut();
			shortcut.launch( new StructuredSelection( file ), mode );
		}
	}
	
	public static List<IFile> getMainJavaOutputFiles( IFile eglFile )
	{
		List<IFile> files = EGLJavaLaunchShortcut.getJavaOutputFiles( eglFile );
		if ( files != null && files.size() > 0 )
		{
			List<IFile> mainFiles = new ArrayList<IFile>( files.size() );
			JavaLaunchableTester tester = new JavaLaunchableTester();
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
				IEGLFile element = (IEGLFile)EGLCore.create( eglFile );
				if ( element != null )
				{
					ProjectEnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment( eglFile.getProject() );
					try
					{
						Environment.pushEnv( env.getIREnvironment() );
						env.getIREnvironment().initSystemEnvironment( env.getSystemEnvironment() );
						
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
													files.add( file );
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

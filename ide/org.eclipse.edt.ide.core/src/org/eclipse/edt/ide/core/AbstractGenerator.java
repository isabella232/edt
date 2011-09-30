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
package org.eclipse.edt.ide.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Base implementation of IGenerator intended to be subclassed by clients.
 */
public abstract class AbstractGenerator extends org.eclipse.edt.compiler.AbstractGenerator implements IGenerator {
		
	/**
	 * The runtime containers.
	 */
	protected EDTRuntimeContainer[] runtimeContainers;
			
	@Override
	public boolean supportsProject(IProject project) {
		return true;
	}
			
	@Override
	public EDTRuntimeContainer[] getRuntimeContainers() {
		return runtimeContainers;
	}
	
	@Override
	public IFile[] getOutputFiles(IFile eglFile, Part part) throws CoreException {
		String relativeFilePath = getRelativeFilePath(eglFile, part);
		String outputDirectory = getOutputDirectory(eglFile);
		
		IContainer container = EclipseUtilities.getOutputContainer(outputDirectory, eglFile, relativeFilePath);
		IPath filePath = EclipseUtilities.getOutputFilePath(outputDirectory, eglFile, relativeFilePath);
		return new IFile[]{container.getFile(filePath)};
	}
	
	/**
	 * Returns the output directory to use for writing a file in Eclipse.
	 * The default implementation will use {@link #getGenerationDirectoryPropertyKey()},
	 * {@link #getProjectSettingsPluginId()}, {@link #getGenerationDirectoryPreferenceKey()},
	 * and {@link #getPreferenceStore()} to determine the value, but sub-classes may override this.
	 * 
	 * @param resource  The egl resource, could be a project, a package or a file. 
	 */
	public String getOutputDirectory(IResource resource) {	
		IProject project = resource.getProject();
		return ProjectSettingsUtility.getGenerationDirectory(resource, getPreferenceStore(),
				new ProjectScope(project).getNode(getProjectSettingsPluginId()),
				getGenerationDirectoryPropertyKey(),
				getGenerationDirectoryPreferenceKey());
	}
	
	public String[] getPrjojectOutputDirectors(IProject project) {	
		
		Preferences propertyPrefs = new ProjectScope(project).getNode(getProjectSettingsPluginId()).node(  getGenerationDirectoryPropertyKey() );
		try {
			String[] keys = propertyPrefs.keys();
			HashSet<String> dirs = new HashSet<String>();
			for ( int i = 0; i < keys.length; i ++ ) {
				dirs.add( (String)propertyPrefs.get( keys[i], null ) );
			}
			
			String[] result = new String[ dirs.size() + 1 ]; 
			dirs.toArray( result );
			
			result[ result.length - 1 ] = getOutputDirectory(project);
			return result;
		} catch (BackingStoreException e) {	}
		return null;
	}
	
	
	/**
	 * Returns the generation argument array, possibly null.
	 * The default implementation will use {@link #getGenerationDirectoryPropertyKey()},
	 * {@link #getProjectSettingsPluginId()}, {@link #getGenerationDirectoryPreferenceKey()},
	 * and {@link #getPreferenceStore()} to determine the value, but sub-classes may override this.
	 * 
	 * @param eglFile  The source .egl file
	 */
	public String[] getGenerationArguments(IResource resource) {	
		IProject project = resource.getProject();
		String args = ProjectSettingsUtility.getGenerationArgument(resource, getPreferenceStore(),
				new ProjectScope(project).getNode(getProjectSettingsPluginId()),
				getGenerationArgumentsPropertyKey());
		
		return splitIntoArgs(args);
	}
	
	protected String[] buildArgs(IFile file, Part part) throws Exception {
		int numArgs = 6;
		
		String[] additionalArgs = getGenerationArguments(file);
		if (additionalArgs != null) {
			numArgs += additionalArgs.length;
		}
		
		String[] args = new String[numArgs];
		
		// Output directory (e.g. JavaSource folder). This is a property on the project, and it might be a directory in some other folder.
		int idx = 0;
		args[idx++] = "-o"; //$NON-NLS-1$
		args[idx++] = getOutputDirectory(file);
		
		// this isn't used but it's a required parameter.
		args[idx++] = "-p"; //$NON-NLS-1$
		args[idx++] = part.getName();
		
		// this isn't used but it's a required parameter.
		args[idx++] = "-r"; //$NON-NLS-1$
		args[idx++] = file.getFullPath().toOSString();
		
		if (additionalArgs != null) {
			for (String arg : additionalArgs) {
				args[idx++] = arg;
			}
		}
		
		return args;
	}
	
	private String[] splitIntoArgs(String args) {
		if (args == null) {
			return null;
		}
		
		args = args.trim();
		if (args.length() != 0) {
			// Need to split up using whitespace as a delimiter, while supporting whitespace inside quotes, and delimited quotes inside quotes.
			// This matches how args are processed by java on the command line.
			char[] chars = args.toCharArray();
			int size = chars.length;
			int curr = 0;
			boolean quoted = false;
			boolean escaped = false;
			List<String> entries = new ArrayList<String>( 10 );
			
			StringBuilder buf = new StringBuilder( 50 );
			while (curr < size) {
				char c = chars[curr];
				
				if (escaped && c != '"') {
					// You can only escape a double quote or another escape. But we still append
					// the \ if the next char was an escape, because it doesn't get added below.
					buf.append('\\');
				}
				
				if ( c == '\\' ) {
					if (escaped) {
						// We actually want another quote. \\ should remain \\. \c remains \c. but \" becomes just "
						buf.append(c);
					}
					escaped = !escaped;
				}
				else if (Character.isWhitespace(c)) {
					escaped = false;
					if (!quoted) {
						// End of an entry.
						if (buf.length() != 0) {
							entries.add(buf.toString());
							buf = new StringBuilder( 50 );
						}
					}
					else {
						buf.append(c);
					}
				}
				else if ( c == '"' ) {
					if (escaped) {
						escaped = false;
						buf.append(c);
					}
					else {
						quoted = !quoted;
					}
				}
				else {
					escaped = false;
					buf.append(c);
				}
				
				curr++;
			}
			
			// If last chunk didn't end with whitespace.
			if (buf.length() > 0) {
				entries.add(buf.toString());
			}
			
			return entries.toArray(new String[entries.size()]);
		}
		return null;
	}
	
	/**
	 * Returns the relative path for the output file.
	 * 
	 * @param eglFile  The source .egl file
	 * @param part     The IR
	 * @return the relative path for the output file.
	 */
	protected abstract String getRelativeFilePath(IFile eglFile, Part part);
	
	/**
	 * @return the key for the project settings generation directory.
	 */
	protected abstract String getGenerationDirectoryPropertyKey();
	
	/**
	 * @return the key for the project settings generation arguments.
	 */
	protected abstract String getGenerationArgumentsPropertyKey();
	
	/**
	 * @return the key for the default generation directory in preferences; this may
	 *         be null if there is no default generation directory.
	 */
	protected abstract String getGenerationDirectoryPreferenceKey();
	
	/**
	 * @return the preference store containing the setting for the key returned by
	 *         {@link #getGenerationDirectoryPreferenceKey()}; this may be null if
	 *         there is no default generation directory.
	 */
	protected abstract IPreferenceStore getPreferenceStore();
}

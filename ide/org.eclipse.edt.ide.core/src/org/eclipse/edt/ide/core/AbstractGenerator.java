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
package org.eclipse.edt.ide.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Base implementation of IGenerator intended to be subclassed by clients.
 */
public abstract class AbstractGenerator extends org.eclipse.edt.compiler.AbstractGenerator implements IGenerator {
	
	private static final EDTRuntimeContainer[] EMPTY_CONTAINERS = {};
	
	/**
	 * All the runtime contributions.
	 */
	protected static GenerationContributorEntry[] contributions;
	
	/**
	 * Contributions associated with this generator.
	 */
	protected List<GenerationContributorEntry> contributionsUsed;
	
	/**
	 * Constructor.
	 */
	public AbstractGenerator() {
		registerContributions();
	}
	
	public GenerationContributorEntry[] getContributions() {
		return contributions;
	}

	/**
	 * The runtime containers.
	 */
	protected EDTRuntimeContainer[] runtimeContainers;

	@Override
	public boolean supportsProject(IProject project) {
		return true;
	}

	@Override
	public synchronized EDTRuntimeContainer[] getRuntimeContainers() {
		if (runtimeContainers == null) {
			EDTRuntimeContainer[] baseContainers = resolveBaseRuntimeContainers();
			EDTRuntimeContainer[] contributedContainers = resolveContributedRuntimeContainers();
			
			int baseSize = baseContainers == null ? 0 : baseContainers.length;
			int contributedSize = contributedContainers == null ? 0 : contributedContainers.length;
			
			if (baseSize + contributedSize == 0) {
				runtimeContainers = EMPTY_CONTAINERS;
			}
			else {
				runtimeContainers = new EDTRuntimeContainer[baseSize + contributedSize];
				int start = 0;
				if (baseSize > 0) {
					System.arraycopy(baseContainers, 0, runtimeContainers, start, baseSize);
					start += baseSize;
				}
				if (contributedSize > 0) {
					System.arraycopy(contributedContainers, 0, runtimeContainers, start, contributedSize);
					start += contributedSize;
				}
			}
		}
		return runtimeContainers;
	}
	
	/**
	 * Subclasses should use this to return the runtime containers provided specifically by this generator.
	 * This may return null.
	 * @see AbstractGenerator#getRuntimeContainers()
	 */
	public EDTRuntimeContainer[] resolveBaseRuntimeContainers() {
		return null;
	}
	
	/**
	 * By default this will return the runtime containers from the contributions being used by this generator.
	 * This may return null, and subclasses may override this behavior.
	 * @see AbstractGenerator#getRuntimeContainers()
	 */
	public EDTRuntimeContainer[] resolveContributedRuntimeContainers() {
		initContributionsIfNecessary();
		
		if (contributionsUsed == null || contributionsUsed.size() == 0) {
			return null;
		}
		
		List<EDTRuntimeContainer> allContainers = new ArrayList<EDTRuntimeContainer>(10);
		for (GenerationContributorEntry entry : contributionsUsed) {
			EDTRuntimeContainer[] entryContainers = entry.getRuntimeContainers();
			if (entryContainers != null && entryContainers.length > 0) {
				allContainers.addAll(Arrays.asList(entryContainers));
			}
		}
		return allContainers.toArray(new EDTRuntimeContainer[allContainers.size()]);
	}

	@Override
	public IFile[] getOutputFiles(IFile eglFile, Part part) throws CoreException {
		String relativeFilePath = getRelativeFilePath(eglFile, part);
		String outputDirectory = getOutputDirectory(eglFile);

		IContainer container = EclipseUtilities.getOutputContainer(outputDirectory, eglFile, relativeFilePath);
		IPath filePath = EclipseUtilities.getOutputFilePath(relativeFilePath);
		return new IFile[] { container.getFile(filePath) };
	}

	/**
	 * Returns the output directory to use for writing a file in Eclipse. The default implementation will use
	 * {@link #getGenerationDirectoryPropertyKey()}, {@link #getProjectSettingsPluginId()},
	 * {@link #getGenerationDirectoryPreferenceKey()}, and {@link #getPreferenceStore()} to determine the value, but
	 * sub-classes may override this.
	 * @param resource The egl resource, could be a project, a package or a file.
	 */
	public String getOutputDirectory(IResource resource) {
		IProject project = resource.getProject();
		return ProjectSettingsUtility.getGenerationDirectory(resource, getPreferenceStore(), new ProjectScope(project).getNode(getProjectSettingsPluginId()),
				getGenerationDirectoryPropertyKey(), getGenerationDirectoryPreferenceKey());
	}

	public String[] getPrjojectOutputDirectors(IProject project) {

		Preferences propertyPrefs = new ProjectScope(project).getNode(getProjectSettingsPluginId()).node(getGenerationDirectoryPropertyKey());
		try {
			String[] keys = propertyPrefs.keys();
			HashSet<String> dirs = new HashSet<String>();
			for (int i = 0; i < keys.length; i++) {
				dirs.add((String) propertyPrefs.get(keys[i], null));
			}

			String[] result = new String[dirs.size() + 1];
			dirs.toArray(result);

			result[result.length - 1] = getOutputDirectory(project);
			return result;
		}
		catch (BackingStoreException e) {
		}
		return null;
	}

	private synchronized void registerContributions() {
		// process if we haven't done this before
		if (contributions == null) {
			// for each of the contributions, we need to add it to a list of class names
			IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
					EDTCoreIDEPlugin.PLUGIN_ID + "." + EDTCoreIDEPlugin.PT_GENERATIONCONTRIBUTORS);
			if (elements != null) {
				List<GenerationContributorEntry> contributionsList = new ArrayList<GenerationContributorEntry>();
				for (int i = 0; i < elements.length; i++) {
					try {
						elements[i].createExecutableExtension(EDTCoreIDEPlugin.CLASS); // makes sure the class exists.
						GenerationContributorEntry contribution = new GenerationContributorEntry();
						contribution.setClassName(elements[i].getAttribute(EDTCoreIDEPlugin.CLASS));
						contribution.setProvider(elements[i].getAttribute(EDTCoreIDEPlugin.PROVIDER));
						contribution.setIdentifier(elements[i].getAttribute(EDTCoreIDEPlugin.ID));
						contribution.setRequires(elements[i].getAttribute(EDTCoreIDEPlugin.REQUIRES));
						
						IConfigurationElement[] containers = elements[i].getChildren(EDTCoreIDEPlugin.RUNTIMECONTAINER);
						if (containers != null && containers.length > 0) {
							List<EDTRuntimeContainer> runtimeContainers = new ArrayList<EDTRuntimeContainer>(containers.length);
							
							for (int j = 0; j < containers.length; j++) {
								IConfigurationElement container = containers[j];
								String id = container.getAttribute(EDTCoreIDEPlugin.ID);
								String name = container.getAttribute(EDTCoreIDEPlugin.NAME);
								String desc = container.getAttribute(EDTCoreIDEPlugin.DESCRIPTION);
								
								IConfigurationElement[] entries = container.getChildren(EDTCoreIDEPlugin.RUNTIMECONTAINERENTRY);
								if (entries != null && entries.length > 0) {
									EDTRuntimeContainerEntry[] runtimeEntries = new EDTRuntimeContainerEntry[entries.length];
									for (int k = 0; k < entries.length; k++) {
										IConfigurationElement element = entries[k];
										String bundleId = element.getAttribute(EDTCoreIDEPlugin.BUNDLEID);
										String bundleRoot = element.getAttribute(EDTCoreIDEPlugin.BUNDLEROOT);
										String versionRange = element.getAttribute(EDTCoreIDEPlugin.VERSIONRANGE);
										String sourceBundleId = element.getAttribute(EDTCoreIDEPlugin.SOURCEBUNDLEID);
										String sourceBundleRoot = element.getAttribute(EDTCoreIDEPlugin.SOURCEBUNDLEROOT);
										String javadocLocation = element.getAttribute(EDTCoreIDEPlugin.JAVADOCLOCATION);
										EDTRuntimeContainerEntry entry = new EDTRuntimeContainerEntry(bundleId, bundleRoot, new VersionRange(versionRange),
												sourceBundleId, sourceBundleRoot, javadocLocation);
										
										runtimeEntries[k] = entry;
									}
									
									runtimeContainers.add(new EDTRuntimeContainer(id, name, desc, runtimeEntries));
								}
							}
							
							if (runtimeContainers.size() > 0) {
								contribution.setRuntimeContainers(runtimeContainers.toArray(new EDTRuntimeContainer[runtimeContainers.size()]));
							}
						}
						
						contributionsList.add(contribution);
					}
					catch (CoreException e) {
						EDTCoreIDEPlugin.log(e);
					}
				}
				contributions = contributionsList.toArray(new GenerationContributorEntry[contributionsList.size()]);
			} else {
				contributions = new GenerationContributorEntry[0];
			}
		}
	}

	/**
	 * Returns the generation argument array, possibly null. The default implementation will use
	 * {@link #getGenerationDirectoryPropertyKey()}, {@link #getProjectSettingsPluginId()},
	 * {@link #getGenerationDirectoryPreferenceKey()}, and {@link #getPreferenceStore()} to determine the value, but
	 * sub-classes may override this.
	 * @param eglFile The source .egl file
	 */
	public String[] getGenerationArguments(IResource resource) {
		IProject project = resource.getProject();
		String args = ProjectSettingsUtility.getGenerationArgument(resource, getPreferenceStore(),
			new ProjectScope(project).getNode(getProjectSettingsPluginId()), getGenerationArgumentsPropertyKey());

		return splitIntoArgs(args);
	}

	protected String[] buildArgs(IFile file, Part part) throws Exception {
		int numArgs = 6;

		String[] additionalArgs = getGenerationArguments(file);
		if (additionalArgs != null) {
			numArgs += additionalArgs.length;
		}
		// add in the contribution parms
		initContributionsIfNecessary();
		if (contributionsUsed != null) {
			numArgs += contributionsUsed.size() + 1;
		}

		// get the array
		String[] args = new String[numArgs];

		// Output directory (e.g. JavaSource folder). This is a property on the project, and it might be a directory in some
		// other folder.
		int idx = 0;
		args[idx++] = "-o"; //$NON-NLS-1$
		args[idx++] = getOutputDirectory(file);

		// this isn't used but it's a required parameter.
		args[idx++] = "-p"; //$NON-NLS-1$
		args[idx++] = part.getCaseSensitiveName();

		// this isn't used but it's a required parameter.
		args[idx++] = "-r"; //$NON-NLS-1$
		args[idx++] = file.getFullPath().toOSString();

		if (additionalArgs != null) {
			for (String arg : additionalArgs) {
				args[idx++] = arg;
			}
		}

		if (contributionsUsed != null) {
			args[idx++] = "-c";
			for (GenerationContributorEntry arg : contributionsUsed) {
				args[idx++] = arg.getClassName();
			}
		}

		return args;
	}
	
	private synchronized void initContributionsIfNecessary() {
		if (contributionsUsed == null && getId() != null) {
			contributionsUsed = new ArrayList<GenerationContributorEntry>();
			determineContributions(getId(), contributionsUsed);
		}
	}

	public static void determineContributions(String provider, List<GenerationContributorEntry> contributionsUsed) {
		// take the passed generator id and determine the contribution id
		for (int i = 0; i < contributions.length; i++) {
			GenerationContributorEntry contribution = contributions[i];
			if (provider.equals(contribution.getProvider())) {
				locateContributions(contribution.getIdentifier(), contributionsUsed);
			}
		}
	}

	private static void locateContributions(String contributionId, List<GenerationContributorEntry> contributionsUsed) {
		// we create a list of contributions here. we first look for all of the contributions
		// that match a specific id. those get added. then, for each of the ones added, we check
		// to for all required contributions and add those to the list, then we check each of those.
		// this ends up giving us a list where each level of contributions is a complete group.
		List<String> requires = new ArrayList<String>();
		for (int i = 0; i < contributions.length; i++) {
			GenerationContributorEntry contribution = contributions[i];
			if (contributionId.equals(contribution.getIdentifier())) {
				contributionsUsed.add(contribution);
				if (contribution.getRequires() != null) {
					if (!requires.contains(contribution.getRequires())) {
						requires.add(contribution.getRequires());
					}
				}
			}
		}
		for (int i = 0; i < requires.size(); i++) {
			locateContributions(requires.get(i), contributionsUsed);
		}
	}

	private String[] splitIntoArgs(String args) {
		if (args == null) {
			return null;
		}

		args = args.trim();
		if (args.length() != 0) {
			// Need to split up using whitespace as a delimiter, while supporting whitespace inside quotes, and delimited
			// quotes inside quotes.
			// This matches how args are processed by java on the command line.
			char[] chars = args.toCharArray();
			int size = chars.length;
			int curr = 0;
			boolean quoted = false;
			boolean escaped = false;
			List<String> entries = new ArrayList<String>(10);

			StringBuilder buf = new StringBuilder(50);
			while (curr < size) {
				char c = chars[curr];

				if (escaped && c != '"') {
					// You can only escape a double quote or another escape. But we still append
					// the \ if the next char was an escape, because it doesn't get added below.
					buf.append('\\');
				}

				if (c == '\\') {
					if (escaped) {
						// We actually want another quote. \\ should remain \\. \c remains \c. but \" becomes just "
						buf.append(c);
					}
					escaped = !escaped;
				} else if (Character.isWhitespace(c)) {
					escaped = false;
					if (!quoted) {
						// End of an entry.
						if (buf.length() != 0) {
							entries.add(buf.toString());
							buf = new StringBuilder(50);
						}
					} else {
						buf.append(c);
					}
				} else if (c == '"') {
					if (escaped) {
						escaped = false;
						buf.append(c);
					} else {
						quoted = !quoted;
					}
				} else {
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
	 * @param eglFile The source .egl file
	 * @param part The IR
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
	 * @return the key for the default generation directory in preferences; this may be null if there is no default
	 * generation directory.
	 */
	protected abstract String getGenerationDirectoryPreferenceKey();

	/**
	 * @return the preference store containing the setting for the key returned by
	 * {@link #getGenerationDirectoryPreferenceKey()}; this may be null if there is no default generation directory.
	 */
	protected abstract IPreferenceStore getPreferenceStore();
}

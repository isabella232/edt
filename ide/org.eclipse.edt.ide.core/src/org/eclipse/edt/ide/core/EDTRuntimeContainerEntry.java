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
package org.eclipse.edt.ide.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.equinox.frameworkadmin.BundleInfo;
import org.eclipse.equinox.simpleconfigurator.manipulator.SimpleConfiguratorManipulator;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 * An entry in a container can be a single jar or directory. It may specify a source attachment. The source
 * attachment may either be a file or folder within the main bundle, or it may be a separate source bundle.
 */
public class EDTRuntimeContainerEntry {
	
	/**
	 * The symbolic name for the bundle providing the runtime.
	 */
	private final String bundleId;
	
	/**
	 * An optional path within the resolved bundle. This is only used when the
	 * resolved bundle is a directory.
	 */
	private final String bundleRoot;
	
	/**
	 * An optional symbolic name for a source bundle. This is only used if
	 * sourceBundleRoot wasn't specified or it didn't resolve to a valid path
	 * within bundleId.
	 */
	private final String sourceBundleId;
	
	/**
	 * An optional path within bundleId specifying the source.
	 */
	private final String sourceBundleRoot;
	
	/**
	 * An optional version range. If specified we make sure bundleId is within this range, otherwise
	 * we'll use the highest version found.
	 */
	private final VersionRange bundleVersionRange;
	
	/**
	 * Can be a URL or a location on the file system. For locations within a bundle, you can
	 * use {@link #findBestBundle(String, VersionRange, boolean) to find where it's located.
	 */
	private final String javadocLocation;
	
	/**
	 * The resolved classpath entry, or null if it couldn't be resolved.
	 */
	private IClasspathEntry entry;
	
	/**
	 * The version of the resolved bundle.
	 */
	private String bundleVersion;
	
	public EDTRuntimeContainerEntry(String bundleId, String bundleRoot, VersionRange versionRange, String sourceBundleId,
			String sourceBundleRoot, String javadocLocation) {
		this.bundleId = bundleId;
		this.bundleVersionRange = versionRange;
		this.bundleRoot = bundleRoot;
		this.sourceBundleId = sourceBundleId;
		this.sourceBundleRoot = sourceBundleRoot;
		this.javadocLocation = javadocLocation;
		
		create();
	}
	
	/**
	 * Uses the provided information to resolve the classpath entry. Clients may subclass this if they have other resolution needs.
	 */
	protected void create() {
		try {
			BundleContext context = EDTCoreIDEPlugin.getPlugin().getBundleContext();
			if (context == null) {
				return;
			}
			ServiceReference ref = context.getServiceReference(SimpleConfiguratorManipulator.class.getName());
			if (ref == null) {
				return;
			}
			SimpleConfiguratorManipulator manipulator = (SimpleConfiguratorManipulator)context.getService(ref);
			if (manipulator == null) {
				return;
			}
			
			// Main bundle (required).
			BundleInfo mainBundle = findBestBundle(bundleId, manipulator.loadConfiguration(context, null), bundleVersionRange);
			if (mainBundle == null) {
				return;
			}
			
			// Store the version in case any clients need to know which version was chosen.
			bundleVersion = mainBundle.getVersion();
			
			URL bundleURL = FileLocator.toFileURL(URIUtil.toURL(mainBundle.getLocation()));
			String path = bundleURL.getPath();
			path = URLDecoder.decode(path, "UTF-8"); //$NON-NLS-1$
			IPath bundlePath = new Path(path);
			if (bundleRoot != null && bundleRoot.length() != 0 && bundlePath.toFile().isDirectory()) {
				bundlePath = bundlePath.append(bundleRoot);
			}

			// Source bundle (optional).
			IPath srcBundlePath = null;
			IPath srcRootPath = null;
			
			// First check for the source root inside the original bundle, which may or may not be a jar.
			if (sourceBundleRoot != null && sourceBundleRoot.length() != 0) {
				File bundleFile = new File(bundleURL.getFile());
				if (bundleFile.isDirectory()) {
					File srcFile = new File(bundleFile, sourceBundleRoot);
					if (srcFile.exists()) {
						srcBundlePath = new Path(srcFile.getPath());
						if (srcFile.isDirectory()) {
							srcBundlePath = srcBundlePath.addTrailingSeparator();
						}
					}
				}
				else if (bundleFile.exists()) {
					try {
						ZipFile zip = new ZipFile(bundleFile);
						if (zip.getEntry(sourceBundleRoot) != null) {
							srcBundlePath = bundlePath;
							srcRootPath = new Path(sourceBundleRoot);
						}
					}
					catch (Exception e) {
					}
				}
			}
			
			// Not found, so try resolving the source bundle.
			if (srcBundlePath == null && (sourceBundleId != null && sourceBundleId.length() != 0)) {
				BundleInfo srcBundle = findBestBundle(sourceBundleId, manipulator.loadConfiguration(context, SimpleConfiguratorManipulator.SOURCE_INFO), bundleVersionRange);
				if (srcBundle != null) {
					path = FileLocator.toFileURL(URIUtil.toURL(srcBundle.getLocation())).getPath();
					path = URLDecoder.decode(path, "UTF-8"); //$NON-NLS-1$
					srcBundlePath = new Path(path);
				}
			}
			
			IClasspathAttribute[] attrs;
			if (javadocLocation !=  null && javadocLocation.length() != 0) {
				attrs = new IClasspathAttribute[]{JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, javadocLocation)};
			}
			else {
				attrs = new IClasspathAttribute[0];
			}
			
			entry = JavaCore.newLibraryEntry(bundlePath, srcBundlePath, srcRootPath, new IAccessRule[0], attrs, false);
		}
		catch (IOException e) {
			EDTCoreIDEPlugin.log(e);
		}
	}
	
	/**
	 * Finds the best-matching bundle. If no range is specified, the highest version is returned.
	 * 
	 * @param id        The bundle's symbolic name.
	 * @param range     An optional version range; this may be null.
	 * @param isSource  Flag indicating if we should look for a source bundle.
	 * @return the best-matching bundle, or null if there are no matches.
	 * @throws IOException
	 */
	public static BundleInfo findBestBundle(String id, VersionRange range, boolean isSource) throws IOException {
		BundleContext context = EDTCoreIDEPlugin.getPlugin().getBundleContext();
		if (context == null) {
			return null;
		}
		ServiceReference ref = context.getServiceReference(SimpleConfiguratorManipulator.class.getName());
		if (ref == null) {
			return null;
		}
		SimpleConfiguratorManipulator manipulator = (SimpleConfiguratorManipulator)context.getService(ref);
		if (manipulator == null) {
			return null;
		}
		
		return findBestBundle(id, manipulator.loadConfiguration(context, isSource ? SimpleConfiguratorManipulator.SOURCE_INFO : null), range);
	}
	
	/**
	 * Finds the best-matching bundle. If no range is specified, the highest version is returned.
	 * 
	 * @param id     The bundle's symbolic name.
	 * @param infos  The available BundleInfos.
	 * @param range  An optional version range; this may be null.
	 * @return the best-matching bundle, or null if there are no matches.
	 */
	public static BundleInfo findBestBundle(String id, BundleInfo[] infos, VersionRange range) {
		if (infos == null) {
			return null;
		}
		
		BundleInfo bestMatch = null;
		Version bestVersion = null;
		for (int i = 0; i < infos.length; i++) {
			BundleInfo info = infos[i];
			if (id.equals(info.getSymbolicName())) {
				URI location = info.getLocation();
				if (location != null) {
					Version version = new Version(info.getVersion());
					if (range == null || range.isIncluded(version)) {
						try {
							String path = FileLocator.toFileURL(URIUtil.toURL(location)).getPath();
							path = URLDecoder.decode(path, "UTF-8"); //$NON-NLS-1$
							IPath bundlePath = new Path(path);
							if (bundlePath.toFile().exists()) {
								if (bestMatch == null || bestVersion.compareTo(version) < 0) {
									bestMatch = info;
									bestVersion = version;
								}
							}
						}
						catch (IOException e) {
						}
					}
				}
			}
		}
		return bestMatch;
	}
	
	/**
	 * @return the classpath entry, or null if the bundle could not be resolved.
	 */
	public IClasspathEntry getClasspathEntry() {
		return entry;
	}
	
	public String getBundleId() {
		return bundleId;
	}
	
	public String getBundleRoot() {
		return bundleRoot;
	}
	
	public VersionRange getVersionRange() {
		return bundleVersionRange;
	}
	
	public String getSourceBundleId() {
		return sourceBundleId;
	}
	
	public String getSourceBundleRoot() {
		return sourceBundleRoot;
	}
	
	public String getJavadocLocation() {
		return javadocLocation;
	}
	
	public String getBundleVersion() {
		return bundleVersion;
	}
}

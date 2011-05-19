/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.bde;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
//import org.eclipse.equinox.frameworkadmin.BundleInfo;
//import org.eclipse.equinox.simpleconfigurator.manipulator.SimpleConfiguratorManipulator;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.EGLCore;

/**
 * Utilities to read and write p2 files
 * 
 * @since 3.4
 */
public class P2Utils {

	public static final String P2_FLAVOR_DEFAULT = "tooling"; //$NON-NLS-1$

	public static final String TYPE_ECLIPSE_BUNDLE = "bundle"; //$NON-NLS-1$
	public static final String NAMESPACE_ECLIPSE_TYPE = "org.eclipse.equinox.p2.eclipse.type"; //$NON-NLS-1$
	public static final String CAPABILITY_NS_JAVA_PACKAGE = "java.package"; //$NON-NLS-1$

	/**
	 * Returns bundles defined by the 'bundles.info' file in the
	 * specified location, or <code>null</code> if none. The "bundles.info" file
	 * is assumed to be at a fixed location relative to the configuration area URL.
	 * This method will also look for a "source.info".  If available, any source
	 * bundles found will also be added to the returned list.  If bundle URLs found
	 * in the bundles.info are relative, they will be appended to platformHome to
	 * make them absolute.
	 * 
	 * @param platformHome absolute path in the local file system to an installation
	 * @param configurationArea url location of the configuration directory to search for bundles.info and source.info
	 * @return URLs of all bundles in the installation or <code>null</code> if not able
	 * 	to locate a bundles.info
	 */
	public static URL[] readBundlesTxt(String platformHome, URL configurationArea) {
//		if (configurationArea == null) {
			return null;
//		}
//		try {
//			BundleInfo[] bundles = readBundles(platformHome, configurationArea);
//			if (bundles == null) {
//				return null;
//			}
//			int length = bundles.length;
//			BundleInfo[] srcBundles = readSourceBundles(platformHome, configurationArea);
//			if (srcBundles != null) {
//				length += srcBundles.length;
//			}
//			URL[] urls = new URL[length];
//			copyURLs(urls, 0, bundles);
//			if (srcBundles != null && srcBundles.length > 0) {
//				copyURLs(urls, bundles.length, srcBundles);
//			}
//			return urls;
//		} catch (MalformedURLException e) {
//			Activator.log(e);
//			return null;
//		}
	}

	/**
	 * Returns bundles defined by the 'bundles.info' relative to the given
	 * home and configuration area, or <code>null</code> if none.
	 * The "bundles.info" file is assumed to be at a fixed location relative to the
	 * configuration area URL.
	 * 
	 * @param platformHome absolute path in the local file system to an installation
	 * @param configurationArea url location of the configuration directory to search
	 *  for bundles.info
	 * @return all bundles in the installation or <code>null</code> if not able
	 * 	to locate a bundles.info
	 */
//	public static BundleInfo[] readBundles(String platformHome, URL configurationArea) {
//		IPath basePath = new Path(platformHome);
//		if (configurationArea == null) {
//			return null;
//		}
//		try {
//			URL bundlesTxt = new URL(configurationArea.getProtocol(), configurationArea.getHost(), new File(configurationArea.getFile(), SimpleConfiguratorManipulator.BUNDLES_INFO_PATH).getAbsolutePath());
//			File home = basePath.toFile();
//			BundleInfo bundles[] = getBundlesFromFile(bundlesTxt, home);
//			if (bundles == null || bundles.length == 0) {
//				return null;
//			}
//			return bundles;
//		} catch (MalformedURLException e) {
//			Activator.log(e);
//			return null;
//		} catch (IOException e) {
//			Activator.log(e);
//			return null;
//		}
//	}

	/**
	 * Returns source bundles defined by the 'source.info' file in the
	 * specified location, or <code>null</code> if none. The "source.info" file
	 * is assumed to be at a fixed location relative to the configuration area URL.
	 * 
	 * @param platformHome absolute path in the local file system to an installation
	 * @param configurationArea url location of the configuration directory to search for bundles.info and source.info
	 * @return all source bundles in the installation or <code>null</code> if not able
	 * 	to locate a source.info
	 */
//	public static BundleInfo[] readSourceBundles(String platformHome, URL configurationArea) {
//		IPath basePath = new Path(platformHome);
//		if (configurationArea == null) {
//			return null;
//		}
//		try {
//			File home = basePath.toFile();
//			URL srcBundlesTxt = new URL(configurationArea.getProtocol(), configurationArea.getHost(), configurationArea.getFile().concat(SimpleConfiguratorManipulator.SOURCE_INFO_PATH));
//			BundleInfo srcBundles[] = getBundlesFromFile(srcBundlesTxt, home);
//			if (srcBundles == null || srcBundles.length == 0) {
//				return null;
//			}
//			return srcBundles;
//		} catch (MalformedURLException e) {
//			Activator.log(e);
//			return null;
//		} catch (IOException e) {
//			Activator.log(e);
//			return null;
//		}
//	}

	/**
	 * Copies URLs from the given bundle info objects into the specified array starting at the given index.
	 * 
	 * @param dest array to copy URLs into
	 * @param start index to start copying into
	 * @param infos associated bundle infos
	 * @throws MalformedURLException
	 */
//	private static void copyURLs(URL[] dest, int start, BundleInfo[] infos) throws MalformedURLException {
//		for (int i = 0; i < infos.length; i++) {
//			dest[start++] = new File(infos[i].getLocation()).toURL();
//		}
//	}

	/**
	 * Returns a list of {@link BundleInfo} for each bundle entry or <code>null</code> if there
	 * is a problem reading the file.
	 * 
	 * @param file the URL of the file to read
	 * @param home the path describing the base location of the platform install
	 * @return list containing URL locations or <code>null</code>
	 * @throws IOException 
	 */
//	private static BundleInfo[] getBundlesFromFile(URL fileURL, File home) throws IOException {
//		SimpleConfiguratorManipulator manipulator = (SimpleConfiguratorManipulator) Activator.getPlugin().acquireService(SimpleConfiguratorManipulator.class.getName());
//		if (manipulator == null) {
//			return null;
//		}
//		// the input stream will be buffered and closed for us
//		try {
//			return manipulator.loadConfiguration(fileURL.openStream(), home.toURI());
//		} catch (FileNotFoundException e) {
//			return null;
//		}
//	}
}

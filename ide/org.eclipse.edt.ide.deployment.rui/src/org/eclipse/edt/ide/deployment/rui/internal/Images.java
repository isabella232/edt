/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.edt.ide.deployment.rui.Activator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 *
 */
public class Images {

	/**
	 * 
	 */
	
	private static ImageRegistry imageRegistry =
		Activator.getDefault().getImageRegistry();
	private static URL installURL =
	    Activator.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
	
	public Images() {
	}
	
	public static final ImageDescriptor getDeployDesciptorImage() {
		return getImageDescriptor("deploydesc"); //$NON-NLS-1$
	}
	
	public static final ImageDescriptor getDeployProjectImage() {
		return getImageDescriptor("deployproj"); //$NON-NLS-1$
	}
	
	public static final ImageDescriptor getRUIDeployImage() {
		return getImageDescriptor("egldeploy"); //$NON-NLS-1$
	}
	
	public static final Image getCheckedImage() {
		return getImage("checked"); //$NON-NLS-1$
	}
	
	public static final Image getUnCheckedImage() {
		return getImage("unchecked"); //$NON-NLS-1$
	}
	
	public static final Image getErrorImage() {
		return getImage("message_error"); //$NON-NLS-1$
	}
	
	public static final Image getInfoImage() {
		return getImage("message_info"); //$NON-NLS-1$
	}
	
	public static final Image getWarningImage() {
		return getImage("message_warning"); //$NON-NLS-1$
	}
	
	public static final ImageDescriptor getRUIDeployDescriptor() {
		return getImageDescriptor("pub_resource_server_wiz"); //$NON-NLS-1$
	}
	
	/**
	 * Get a .gif from the image registry, which caches the icons folder.
	 * @param key
	 * @return Image
	 */
	private static Image getImage(String key) {
		Image image = imageRegistry.get(key);
		if (image == null) {
			imageRegistry.put(key, getImageDescriptor(key));
			image = imageRegistry.get(key);
		}
		return image;
	}
	
	/**
	 * Create and return a new ImageDescriptor for the image corresponding to the key.
	 * If it can't be found, try to return a "not found" icon.
	 * If that doesn't work, return null.
	 * 
	 * @param key
	 * @return ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor imageDescriptor = primGetImageDescriptor(key);
		if (imageDescriptor == null)
			imageDescriptor = primGetImageDescriptor(getNotFoundKeyFor(key));
		return imageDescriptor;
	}
	
	/*
	 * Create and return a new ImageDescriptor for the image corresponding to the key.
	 * If it can't be found, return null.
	 */
	private static ImageDescriptor primGetImageDescriptor(String key) {
		ImageDescriptor imageDescriptor = null;
		try {
			imageDescriptor = ImageDescriptor.createFromURL(
				new URL(installURL, "icons/" + key + ".gif"));  //$NON-NLS-1$//$NON-NLS-2$
		} catch (MalformedURLException exception) {
//			Logger.log(Logger.ERROR, "Failed to load image for \"" + key + "\"", exception);  //$NON-NLS-1$//$NON-NLS-2$
		}

//		if (imageDescriptor == null)
//			Logger.log(Logger.ERROR, "No image found for " + key); //$NON-NLS-1$

		return imageDescriptor;
	}

	private static String getNotFoundKeyFor(String key) {

		return "notfound_obj"; //$NON-NLS-1$
	}

}

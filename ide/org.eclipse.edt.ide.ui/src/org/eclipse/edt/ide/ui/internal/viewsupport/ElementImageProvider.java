/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRootContainer;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ElementImageProvider {

	/**
	 * Flags for the JavaImageLabelProvider:
	 * Generate images with overlays.
	 */
	public final static int OVERLAY_ICONS = 0x1;

	/**
	 * Generate small sized images.
	 */
	public final static int SMALL_ICONS = 0x2;

	/**
	 * Use the 'light' style for rendering types.
	 */
	public final static int LIGHT_TYPE_ICONS = 0x4;

	public static final Point SMALL_SIZE = new Point(16, 16);
	public static final Point BIG_SIZE = new Point(22, 16);

	private static ImageDescriptor DESC_OBJ_PROJECT_CLOSED;
	private static ImageDescriptor DESC_OBJ_PROJECT;
//	private static ImageDescriptor DESC_OBJ_FOLDER;
	{
		ISharedImages images = EDTUIPlugin.getDefault().getWorkbench().getSharedImages();
		DESC_OBJ_PROJECT_CLOSED = images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
		DESC_OBJ_PROJECT = images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
//		DESC_OBJ_FOLDER = images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_FOLDER);
	}

	private ImageDescriptorRegistry fRegistry;

	public ElementImageProvider() {
		fRegistry = null; // lazy initialization
	}

	/**
	 * Returns the icon for a given element. The icon depends on the element type
	 * and element properties. If configured, overlay icons are constructed for
	 * <code>ISourceReference</code>s.
	 * @param flags Flags as defined by the JavaImageLabelProvider
	 */
	public Image getImageLabel(Object element, int flags) {
		return getImageLabel(computeDescriptor(element, flags));
	}

	/**
	 * 
	 * Returns the icon with the appropriate overlay icons constructed for it.
	 * @param baseIcon
	 * @param flags
	 * @return
	 */
	public Image getImageLabel(ImageDescriptor baseIcon, int flags) {
		return getImageLabel(getEGLImageDescriptor(baseIcon, flags));
	}

	private Image getImageLabel(ImageDescriptor descriptor) {
		if (descriptor == null)
			return null;
		return getRegistry().get(descriptor);
	}

	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry = EDTUIPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}

	private ImageDescriptor computeDescriptor(Object element, int flags) {
		if (element instanceof IEGLElement) {
			return getEGLImageDescriptor((IEGLElement) element, flags);
		}
		//		else if (element instanceof IProject){
		//			try{
		//				if(((IProject)element).hasNature(EGLNature.EGL_NATURE_ID)){
		//					IEGLProject eglProject = EGLCore.create((IProject)element);
		//					return getEGLImageDescriptor((IEGLElement) eglProject, flags);
		//				}
		//			}
		//			catch(CoreException e){
		//				if(element instanceof IAdaptable)
		//					return getWorkbenchImageDescriptor((IAdaptable) element, flags);
		//				else
		//					return null;
		//			}
		//		}
		else if (element instanceof IFile) {
			IFile file = (IFile) element;
			if ("egl".equals(file.getFileExtension())) { //$NON-NLS-1$
				return getCUResourceImageDescriptor(file, flags); // image for a CU not on the build path
			}
			return getWorkbenchImageDescriptor(file, flags);
		} else if (element instanceof IAdaptable) {
			return getWorkbenchImageDescriptor((IAdaptable) element, flags);
		}
		return null;
	}

//	private static boolean showOverlayIcons(int flags) {
//		return (flags & OVERLAY_ICONS) != 0;
//	}

	private static boolean useSmallSize(int flags) {
		//MATT Determine why this evaluates to false instead of true when no flags should be set.
		//return (flags & SMALL_ICONS) != 0;
		return true;
	}

//	private static boolean useLightIcons(int flags) {
//		return (flags & LIGHT_TYPE_ICONS) != 0;
//	}

	/**
	 * Returns an image descriptor for a compilatio unit not on the class path.
	 * The descriptor includes overlays, if specified.
	 */
	public ImageDescriptor getCUResourceImageDescriptor(IFile file, int flags) {
		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new EGLElementImageDescriptor(PluginImages.DESC_OBJS_CUNIT_RESOURCE, 0, size);
	}

	/**
	 * Returns an image descriptor for an EGL element. The descriptor includes overlays, if specified.
	 */
	public ImageDescriptor getEGLImageDescriptor(IEGLElement element, int flags) {
		int adornmentFlags = computeEGLAdornmentFlags(element, flags);
		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new EGLElementImageDescriptor(getBaseImageDescriptor(element, flags), adornmentFlags, size);
	}

	/**
	 * Returns an image descriptor that includes overlays, if specified (only error ticks apply).
	 * Returns <code>null</code> if no image could be found.
	 */
	public ImageDescriptor getEGLImageDescriptor(ImageDescriptor icon, int flags) {
		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new EGLElementImageDescriptor(icon, flags, size);
	}

	/**
	 * Returns an image descriptor for a IAdaptable. The descriptor includes overlays, if specified (only error ticks apply).
	 * Returns <code>null</code> if no image could be found.
	 */
	public ImageDescriptor getWorkbenchImageDescriptor(IAdaptable adaptable, int flags) {
		IWorkbenchAdapter wbAdapter = (IWorkbenchAdapter) adaptable.getAdapter(IWorkbenchAdapter.class);
		if (wbAdapter == null) {
			return null;
		}
		ImageDescriptor descriptor = wbAdapter.getImageDescriptor(adaptable);
		if (descriptor == null) {
			return null;
		}

		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new EGLElementImageDescriptor(descriptor, flags, size);
	}

	// ---- Computation of base image key -------------------------------------------------

	/**
	 * Returns an image descriptor for a java element. This is the base image, no overlays.
	 */
	public ImageDescriptor getBaseImageDescriptor(IEGLElement element, int renderFlags) {

		try {
			switch (element.getElementType()) {

				//MATT Commented out 12/03 - Don't think this is ever needed
//				case IEGLElement.PACKAGE_DECLARATION :
//					return PluginImages.DESC_OBJS_PACKDECL;
//
//					//				case IEGLElement.TYPE: {
//					//					IType type= (IType) element;
//					//					boolean isInterface= type.isInterface();
//					//					
//					//					if (useLightIcons(renderFlags)) {
//					//						return isInterface ? NewEGLPluginImages.DESC_OBJS_INTERFACEALT : NewEGLPluginImages.DESC_OBJS_CLASSALT;
//					//					}
//					//					boolean isInner= type.getDeclaringType() != null;
//					//					return getTypeImageDescriptor(isInterface, isInner, type.getFlags());
//					//				}

				case IEGLElement.PACKAGE_FRAGMENT_ROOT :
					{
						IPackageFragmentRoot root = (IPackageFragmentRoot) element;
						if(root instanceof EglarPackageFragmentRootContainer){
							return PluginImages.DESC_OBJS_PACKFRAG_ROOT_EGLAR_CONTAINER;
						}else if(root instanceof EglarPackageFragmentRoot){
							return PluginImages.DESC_OBJS_PACKFRAG_ROOT_EGLAR;
						}else {
							return PluginImages.DESC_OBJS_PACKFRAG_ROOT;
						}
//						if (root.isArchive()) {
//							IPath attach= root.getSourceAttachmentPath();
//							IPath attach = null;
//							if (root.isExternal()) {
//								if (attach == null) {
//									return PluginImages.DESC_OBJS_EXTJAR;
//								} else {
//									return PluginImages.DESC_OBJS_EXTJAR_WSRC;
//								}
//							} else {
//								if (attach == null) {
//									return PluginImages.DESC_OBJS_JAR;
//								} else {
//									return PluginImages.DESC_OBJS_JAR_WSRC;
//								}
//							}			
//							//TODO Rocky
//							return PluginImages.DESC_OBJS_PACKFRAG_ROOT_EGLAR;
//						} 
					}

				case IEGLElement.PACKAGE_FRAGMENT :
					return getPackageFragmentIcon(element, renderFlags);

				case IEGLElement.EGL_FILE :
					//case IEGLElement.COMPILATION_UNIT:
					return PluginImages.DESC_OBJS_EGLFILE;

				case IEGLElement.CLASS_FILE :
					/* this is too expensive for large packages
					try {
						IClassFile cfile= (IClassFile)element;
						if (cfile.isClass())
							return NewEGLPluginImages.IMG_OBJS_CFILECLASS;
						return NewEGLPluginImages.IMG_OBJS_CFILEINT;
					} catch(EGLModelException e) {
						// fall through;
					}*/
					return PluginImages.DESC_OBJS_CFILE;

				case IEGLElement.EGL_PROJECT :
					IEGLProject jp = (IEGLProject) element;
					if (jp.getProject().isOpen()) {
						IProject project = jp.getProject();
						IWorkbenchAdapter adapter = (IWorkbenchAdapter) project.getAdapter(IWorkbenchAdapter.class);
						if (adapter != null) {
							ImageDescriptor result = adapter.getImageDescriptor(project);
							if (result != null)
								return result;
						}
						return DESC_OBJ_PROJECT;
					}
					return DESC_OBJ_PROJECT_CLOSED;

				case IEGLElement.EGL_MODEL :
					return PluginImages.DESC_OBJS_EGL_MODEL;
				
				case IEGLElement.FUNCTION:
					return PluginImages.DESC_OBJS_FUNCTION;
			}

			Assert.isTrue(false, UINlsStrings.EGLImageLabelprovider_assert_wrongImage);
			return null;

		} catch (EGLModelException e) {
			if (e.isDoesNotExist())
				return PluginImages.DESC_OBJS_UNKNOWN;
			EDTUIPlugin.log(e);
			return PluginImages.DESC_OBJS_GHOST;
		}
	}

	protected ImageDescriptor getPackageFragmentIcon(IEGLElement element, int renderFlags) throws EGLModelException {
		IPackageFragment fragment = (IPackageFragment) element;
		boolean containsEGLElements = false;
		try {
			containsEGLElements = fragment.hasChildren();
		} catch (EGLModelException e) {
			// assuming no children;
		}
		if (!containsEGLElements && (fragment.getNonEGLResources().length > 0))
			return PluginImages.DESC_OBJS_EMPTY_PACKAGE;
		else if (!containsEGLElements)
			return PluginImages.DESC_OBJS_EMPTY_PACKAGE;
		return PluginImages.DESC_OBJS_PACKAGE;
	}

	public void dispose() {
	}

	// ---- Methods to compute the adornments flags ---------------------------------

	private int computeEGLAdornmentFlags(IEGLElement element, int renderFlags) {
		int flags = 0;
		/*
		 * Currently EGL Provides no additional adornments.  Let flags through until then.
		 */
		if (renderFlags != 0)
			return renderFlags;
		else
			return flags;
	}
}

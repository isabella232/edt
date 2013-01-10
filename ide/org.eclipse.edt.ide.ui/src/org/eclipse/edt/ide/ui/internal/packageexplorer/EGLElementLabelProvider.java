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
package org.eclipse.edt.ide.ui.internal.packageexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRootContainer;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.viewsupport.EGLElementLabels;
import org.eclipse.edt.ide.ui.internal.viewsupport.ElementImageProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

public class EGLElementLabelProvider extends LabelProvider implements ICommonLabelProvider{

	/**
	 * Flag (bit mask) indicating that methods labels include the method return type. (appended)
	 */
	public final static int SHOW_RETURN_TYPE=				0x001;
	
	/**
	 * Flag (bit mask) indicating that method label include method parameter types.
	 */
	public final static int SHOW_PARAMETERS=				0x002;
	
	/**
	 * Flag (bit mask) indicating that the label of a member should include the container.
	 * For example, include the name of the type enclosing a field.
	 * @deprecated Use SHOW_QUALIFIED or SHOW_ROOT instead
	 */
	public final static int SHOW_CONTAINER=				0x004;

	/**
	 * Flag (bit mask) indicating that the label of a type should be fully qualified.
	 * For example, include the fully qualified name of the type enclosing a type.
	 * @deprecated Use SHOW_QUALIFIED instead
	 */
	public final static int SHOW_CONTAINER_QUALIFICATION=	0x008;

	/**
	 * Flag (bit mask) indicating that the label should include overlay icons
	 * for element type and modifiers.
	 */
	public final static int SHOW_OVERLAY_ICONS=			0x010;

	/**
	 * Flag (bit mask) indicating thata field label should include the declared type.
	 */
	public final static int SHOW_TYPE=					0x020;

	/**
	 * Flag (bit mask) indicating that the label should include the name of the
	 * package fragment root (appended).
	 */
	public final static int SHOW_ROOT=					0x040;
	
	/**
	 * Flag (bit mask) indicating that the label qualification of a type should
	 * be shown after the name.
	 * @deprecated SHOW_POST_QUALIFIED instead
	 */
	public final static int SHOW_POSTIFIX_QUALIFICATION=		0x080;

	/**
	 * Flag (bit mask) indicating that the label should show the icons with no space
	 * reserved for overlays.
	 */
	public final static int SHOW_SMALL_ICONS= 			0x100;
	
	/**
	 * Flag (bit mask) indicating that the packagefragment roots from variables should
	 * be rendered with the variable in the name
	 */
	public final static int SHOW_VARIABLE= 			0x200;
	
	/**
	 * Flag (bit mask) indicating that Complation Units, Class Files, Types, Declarations and Members
	 * should be rendered qualified.
	 * Examples: java.lang.String, java.util.Vector.size()
	 * 
	 * @since 2.0
	 */
	public final static int SHOW_QUALIFIED=				0x400;

	/**
	 * Flag (bit mask) indicating that Complation Units, Class Files, Types, Declarations and Members
	 * should be rendered qualified. The qualifcation is appended
	 * Examples: String - java.lang, size() - java.util.Vector
	 * 
	 * @since 2.0
	 */
	public final static int SHOW_POST_QUALIFIED=	0x800;	
	
	
	/**
	 * Constant (value <code>0</code>) indicating that the label should show 
	 * the basic images only.
	 */
	public final static int SHOW_BASICS= 0x000;
	
	
	/**
	 * Constant indicating the default label rendering.
	 * Currently the default is equivalent to
	 * <code>SHOW_PARAMETERS | SHOW_OVERLAY_ICONS</code>.
	 */
	public final static int SHOW_DEFAULT= new Integer(SHOW_PARAMETERS | SHOW_OVERLAY_ICONS).intValue();

	public ElementImageProvider fImageLabelProvider;
	
	private int fFlags;
	private int fImageFlags;
	private int fTextFlags;
	
	//private final Image PKG_ICON=EGLPluginImages.get(EGLPluginImages.IMG_OBJS_PACKAGE);
	//private final Image FINAL_ICON;//=EGLPluginImages.get("icons\formgroup.gif");

	public EGLElementLabelProvider() {
		fImageLabelProvider= new ElementImageProvider();
	}
	
	/**
	 * Creates a new label provider.
	 *
	 * @param flags the initial options; a bitwise OR of <code>SHOW_* </code> constants
	 */
	public EGLElementLabelProvider(int flags) {
		fImageLabelProvider= new ElementImageProvider();
		fFlags= flags;
		updateImageProviderFlags();
		updateTextProviderFlags();		
	}

	public Image getImage(Object element) {
		fImageFlags = 0;
		
		IEGLElement eglElement;
		IResource eglResource = null;
		IMarker[] eglMarkers = null;
		int searchDepth = IResource.DEPTH_INFINITE;
		
		try{
			if(element instanceof EglarPackageFragmentRootContainer) {
				
			}
			else if(element instanceof IEGLProject || element instanceof IEGLElement){
				eglElement = ((IEGLElement)element);
				eglResource = eglElement.getUnderlyingResource();
				if(eglElement instanceof IPackageFragment){
					if(((IPackageFragment)eglElement).isDefaultPackage()){
						searchDepth = IResource.DEPTH_ONE;
					}
				}
			}
			else if((element instanceof IProject)&&(((IProject)element).hasNature(EGLCore.NATURE_ID))){
				eglResource = ((IProject)element);
			}
			else if(element instanceof IFile) {
				String fileExtension = ((IFile)element).getFileExtension();
				if(fileExtension != null && "eglbld".equals(fileExtension.toLowerCase())){
					eglResource = ((IFile)element);
				}
			}
			
			if(eglResource!=null){
				eglMarkers = eglResource.findMarkers(IMarker.MARKER, true, searchDepth);
				if(eglMarkers.length>0){
					boolean foundError = false;
					for(int markerIndex=0; markerIndex<eglMarkers.length; markerIndex++){
						switch(eglMarkers[markerIndex].getAttribute(IMarker.SEVERITY, 0)) {
							case IMarker.SEVERITY_ERROR:
								foundError = true;
								fImageFlags = EGLElementImageDescriptor.ERROR;
								break;

							case IMarker.SEVERITY_WARNING:
								if(!foundError) {
									fImageFlags = EGLElementImageDescriptor.WARNING;
								}
								break;
						}
					}
				}
			}
		}
		catch(CoreException e3){
			//Error occurred during testing the nature
		}
			
		Image result= fImageLabelProvider.getImageLabel(element, fImageFlags);
		if (result != null) {
			return result;
		}

//		if (element instanceof IStorage) 
//			return fStorageLabelProvider.getImage(element);

		return result;
	}


	public String getText(Object element) {
		String text= EGLElementLabels.getTextLabel(element, 0);
		if (text.length() > 0) {
			return text;
		}
		return text;
	}
	
	private boolean getFlag( int flag) {
		return (fFlags & flag) != 0;
	}

	private void updateImageProviderFlags() {
		fImageFlags= 0;
		if (getFlag(SHOW_OVERLAY_ICONS)) {
			fImageFlags |= ElementImageProvider.OVERLAY_ICONS;
		}
		if (getFlag(SHOW_SMALL_ICONS)) {
			fImageFlags |= ElementImageProvider.SMALL_ICONS;
		}
	}	
	
	private void updateTextProviderFlags() {
		fTextFlags= 0;
		if (getFlag(SHOW_RETURN_TYPE)) {
			fTextFlags |= EGLElementLabels.M_APP_RETURNTYPE;
		}
		if (getFlag(SHOW_PARAMETERS)) {
			fTextFlags |= EGLElementLabels.M_PARAMETER_TYPES;
		}		
		if (getFlag(SHOW_CONTAINER)) {
			fTextFlags |= EGLElementLabels.P_POST_QUALIFIED | EGLElementLabels.T_POST_QUALIFIED | EGLElementLabels.CF_POST_QUALIFIED  | EGLElementLabels.CU_POST_QUALIFIED | EGLElementLabels.M_POST_QUALIFIED | EGLElementLabels.F_POST_QUALIFIED;
		}
		if (getFlag(SHOW_POSTIFIX_QUALIFICATION)) {
			fTextFlags |= (EGLElementLabels.T_POST_QUALIFIED | EGLElementLabels.CF_POST_QUALIFIED  | EGLElementLabels.CU_POST_QUALIFIED);
		} else if (getFlag(SHOW_CONTAINER_QUALIFICATION)) {
			fTextFlags |=(EGLElementLabels.T_FULLY_QUALIFIED | EGLElementLabels.CF_QUALIFIED  | EGLElementLabels.CU_QUALIFIED);
		}
		if (getFlag(SHOW_TYPE)) {
			fTextFlags |= EGLElementLabels.F_APP_TYPE_SIGNATURE;
		}
		if (getFlag(SHOW_ROOT)) {
			fTextFlags |= EGLElementLabels.APPEND_ROOT_PATH;
		}			
		if (getFlag(SHOW_VARIABLE)) {
			fTextFlags |= EGLElementLabels.ROOT_VARIABLE;
		}
		if (getFlag(SHOW_QUALIFIED)) {
			fTextFlags |= (EGLElementLabels.F_FULLY_QUALIFIED | EGLElementLabels.M_FULLY_QUALIFIED | EGLElementLabels.I_FULLY_QUALIFIED 
				| EGLElementLabels.T_FULLY_QUALIFIED | EGLElementLabels.D_QUALIFIED | EGLElementLabels.CF_QUALIFIED  | EGLElementLabels.CU_QUALIFIED);
		}
		if (getFlag(SHOW_POST_QUALIFIED)) {
			fTextFlags |= (EGLElementLabels.F_POST_QUALIFIED | EGLElementLabels.M_POST_QUALIFIED | EGLElementLabels.I_POST_QUALIFIED 
			| EGLElementLabels.T_POST_QUALIFIED | EGLElementLabels.D_POST_QUALIFIED | EGLElementLabels.CF_POST_QUALIFIED  | EGLElementLabels.CU_POST_QUALIFIED);
		}		
	}

	public void initialize(String aViewerId) {
		// TODO Auto-generated method stub
		
	}

	public String getDescription(Object anElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(ICommonContentExtensionSite aConfig) {
		// TODO Auto-generated method stub
		
	}

	public void restoreState(IMemento aMemento) {
		// TODO Auto-generated method stub
		
	}

	public void saveState(IMemento aMemento) {
		// TODO Auto-generated method stub
		
	}

}

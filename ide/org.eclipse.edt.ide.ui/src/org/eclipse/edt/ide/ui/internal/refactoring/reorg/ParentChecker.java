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
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

public class ParentChecker {
	private IResource[] fResources;
	private IEGLElement[] fEGLElements;

	public ParentChecker(IResource[] resources, IEGLElement[] eglElements) {
		Assert.isNotNull(resources);
		Assert.isNotNull(eglElements);
		fResources= resources;
		fEGLElements= eglElements;
	}
	
	public boolean haveCommonParent() {
		return getCommonParent() != null;
	}
	
	public Object getCommonParent(){
		if (fEGLElements.length == 0 && fResources.length == 0)
			return null;
		if (! resourcesHaveCommonParent() || ! eglElementsHaveCommonParent())
			return null;
		if (fEGLElements.length == 0){
			IResource commonResourceParent= getCommonResourceParent();
			Assert.isNotNull(commonResourceParent);
			IEGLElement convertedToEGL= EGLCore.create(commonResourceParent);
			if (convertedToEGL != null && convertedToEGL.exists())
				return convertedToEGL;
			else
				return commonResourceParent;
		}
		if (fResources.length == 0)
			return getCommonEGLElementParent();
			
		IResource commonResourceParent= getCommonResourceParent();
		IEGLElement commonEGLElementParent= getCommonEGLElementParent();
		Assert.isNotNull(commonEGLElementParent);
		Assert.isNotNull(commonResourceParent);
		IEGLElement convertedToEGL= EGLCore.create(commonResourceParent);
		
		//For default package
		if(IEGLElement.PACKAGE_FRAGMENT_ROOT == convertedToEGL.getElementType()) {
			convertedToEGL = ((IPackageFragmentRoot) convertedToEGL).getPackageFragment("");
		}
		
		if (convertedToEGL == null || 
			! convertedToEGL.exists() || 
			! commonEGLElementParent.equals(convertedToEGL))
			return null;
		return commonEGLElementParent;	
	}

	private IEGLElement getCommonEGLElementParent() {
		Assert.isNotNull(fEGLElements);
		Assert.isTrue(fEGLElements.length > 0);//safe - checked before
		return fEGLElements[0].getParent();
	}

	private IResource getCommonResourceParent() {
		Assert.isNotNull(fResources);
		Assert.isTrue(fResources.length > 0);//safe - checked before
		return fResources[0].getParent();
	}

	private boolean eglElementsHaveCommonParent() {
		if (fEGLElements.length == 0)
			return true;
		IEGLElement firstParent= fEGLElements[0].getParent();
		Assert.isNotNull(firstParent); //this should never happen			
		for (int i= 1; i < fEGLElements.length; i++) {
			if (! firstParent.equals(fEGLElements[i].getParent()))
				return false;
		}
		return true;
	}

	private boolean resourcesHaveCommonParent() {
		if (fResources.length == 0)
			return true;
		IResource firstParent= fResources[0].getParent();
		Assert.isNotNull(firstParent);
		for (int i= 1; i < fResources.length; i++) {
			if (! firstParent.equals(fResources[i].getParent()))
				return false;
		}
		return true;
	}
	
	public IResource[] getResources(){
		return fResources;
	}		
		
	public IEGLElement[] getEGLElements(){
		return fEGLElements;
	}

	public void removeElementsWithAncestorsOnList(boolean removeOnlyEGLElements) {
		if (! removeOnlyEGLElements){
			removeResourcesDescendantsOfResources();
			removeResourcesDescendantsOfEGLElements();
		}
		removeEGLElementsDescendantsOfEGLElements();
	}
				
	private void removeResourcesDescendantsOfEGLElements() {
		List subResources= new ArrayList(3);
		for (int i= 0; i < fResources.length; i++) {
			IResource subResource= fResources[i];
			for (int j= 0; j < fEGLElements.length; j++) {
				IEGLElement superElements= fEGLElements[j];
				if (isDescendantOf(subResource, superElements))
					subResources.add(subResource);
			}
		}
		removeFromSetToDelete((IResource[]) subResources.toArray(new IResource[subResources.size()]));
	}

	private void removeEGLElementsDescendantsOfEGLElements() {
		List subElements= new ArrayList(3);
		for (int i= 0; i < fEGLElements.length; i++) {
			IEGLElement subElement= fEGLElements[i];
			for (int j= 0; j < fEGLElements.length; j++) {
				IEGLElement superElement= fEGLElements[j];
				if (isDescendantOf(subElement, superElement))
					subElements.add(subElement);
			}
		}
		removeFromSetToDelete((IEGLElement[]) subElements.toArray(new IEGLElement[subElements.size()]));
	}

	private void removeResourcesDescendantsOfResources() {
		List subResources= new ArrayList(3);
		for (int i= 0; i < fResources.length; i++) {
			IResource subResource= fResources[i];
			for (int j= 0; j < fResources.length; j++) {
				IResource superResource= fResources[j];
				if (isDescendantOf(subResource, superResource))
					subResources.add(subResource);
			}
		}
		removeFromSetToDelete((IResource[]) subResources.toArray(new IResource[subResources.size()]));
	}

	public static boolean isDescendantOf(IResource subResource, IEGLElement superElement) {
		IResource parent= subResource.getParent();
		while(parent != null){
			IEGLElement el= EGLCore.create(parent);
			if (el != null && el.exists() && el.equals(superElement))
				return true;
			parent= parent.getParent();
		}
		return false;
	}

	public static boolean isDescendantOf(IEGLElement subElement, IEGLElement superElement) {
		if (subElement.equals(superElement))
			return false;
		IEGLElement parent= subElement.getParent();
		while(parent != null){
			if (parent.equals(superElement))
				return true;
			parent= parent.getParent();
		}
		return false;
	}

	public static boolean isDescendantOf(IResource subResource, IResource superResource) {
		return ! subResource.equals(superResource) && superResource.getFullPath().isPrefixOf(subResource.getFullPath());
	}

	private void removeFromSetToDelete(IResource[] resourcesToNotDelete) {
		fResources= ReorgUtils.setMinus(fResources, resourcesToNotDelete);
	}
	
	private void removeFromSetToDelete(IEGLElement[] elementsToNotDelete) {
		fEGLElements= ReorgUtils.setMinus(fEGLElements, elementsToNotDelete);
	}
}

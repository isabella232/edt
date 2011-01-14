/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.sdk.compile;

import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;


/**
 *
 */
public class EGLCEnvironment implements IEnvironment {
    
	private static final String[] defaultPackage = InternUtil.intern(new String[0]);
	
	private PartPathEntry[] partPathEntries;
	private PartPathEntry  irOutputPathEntry = null;
	
	private PackageBinding rootPackageBinding = new PackageBinding(defaultPackage, null, this);

	public void setPartPathEntries(PartPathEntry[] partPathEntries){
    	this.partPathEntries = partPathEntries;
    }
	
	public void setIROutputPathEntry(PartPathEntry partPathEntry){
    	this.irOutputPathEntry = partPathEntry;
    }
	
	public PartPathEntry getIROutputPathEntry(){
    	return this.irOutputPathEntry;
    }
    public IPartBinding getPartBinding(String[] packageName, String partName) {
       IPartBinding result = SourcePathEntry.getInstance().getPartBinding(packageName, partName);
       
    	if(result == null){
        	boolean hasSourcePart = false;
	        PartPathEntry partPathEntry = null;
	        
	        if(SourcePathEntry.getInstance().hasPart(packageName, partName) != ITypeBinding.NOT_FOUND_BINDING){
		        hasSourcePart = true;
	        }
	        
	        for (int i = 0; i < partPathEntries.length; i++) {
	        	if(partPathEntries[i].hasPart(packageName, partName)){
	        		partPathEntry = partPathEntries[i];
	        		if(partPathEntry != null){
	        			break;
	        		}
	        	}
	        }
        
	        if(hasSourcePart && partPathEntry != null){
	        	long sourceLastModified = SourcePathEntry.getInstance().lastModified(packageName, partName);
	        	long irLastModified = partPathEntry.lastModified(packageName, partName);
	        	
	        	if(sourceLastModified > irLastModified){
	        		// Add this part to the processor and process it
	        		return SourcePathEntry.getInstance().getOrCompilePartBinding(packageName, partName);
	        	}else{
	        		return partPathEntry.getPartBinding(packageName, partName);
	        	}
        	}else if(hasSourcePart){
	        	return SourcePathEntry.getInstance().getOrCompilePartBinding(packageName, partName);
	        }else if(partPathEntry != null){
	        	return partPathEntry.getPartBinding(packageName, partName);
	        } 
	        
	        return SystemEnvironment.getInstance().getPartBinding(packageName, partName);
    	}else{
    		return result;
    	}
    }
    
    public IPartBinding getNewPartBinding(String[] packageName, String caseSensitiveInternedPartName, int kind) {
    	return SourcePathEntry.getInstance().getNewPartBinding(packageName, caseSensitiveInternedPartName, kind);
    }
    
    public boolean hasPackage(String[] packageName) {
        if(SourcePathEntry.getInstance().hasPackage(packageName)){
	      	return true;
	    }
	    	
    	for (int i = 0; i < partPathEntries.length; i++) {
			if(partPathEntries[i].hasPackage(packageName)){
				return true;
			}
		}
    	
    	return SystemEnvironment.getInstance().hasPackage(packageName);
    }
    
    public IPackageBinding getRootPackage() {
        return rootPackageBinding;
    }
    
    public IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName) {
    	String caseInsensitiveInternedPartName = InternUtil.intern(caseSensitiveInternedPartName);
	    int partType = SourcePathEntry.getInstance().hasPart(packageName, caseInsensitiveInternedPartName);
			
	    if(partType != ITypeBinding.NOT_FOUND_BINDING){
	        IPartBinding result = PartBinding.newPartBinding(partType, packageName, caseSensitiveInternedPartName);
            result.setEnvironment(EGLCEnvironment.this);
            return result;
	    }
	    return IBinding.NOT_FOUND_BINDING;
	}
}

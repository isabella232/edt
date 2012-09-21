/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.ZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.IZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.compiler.internal.sdk.utils.Util;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.IWorkingCopyBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.utils.NameUtile;

public class WrapperedZipFileBuildPathEntry implements IZipFileBindingBuildPathEntry, IWorkingCopyBuildPathEntry{
	ZipFileBindingBuildPathEntry zipEntry;
	IProject project;
	
	private HashMap<String,IPartOrigin> partOriginByPart = new HashMap<String,IPartOrigin>(32);

	public WrapperedZipFileBuildPathEntry(ZipFileBindingBuildPathEntry zipEntry, IProject project) {
		super();
		this.zipEntry = zipEntry;
		this.project = project;
	}

	@Override
	public IPartOrigin getPartOrigin(String packageName, String partName) {
		IPartOrigin partOrigin = null;
		try {
			Part part = findPart(packageName, partName);
			
			String sourceName = null;
			String irName = "";
			if(part != null) {
				sourceName = part.eGet("filename").toString();
				irName = IRFileNameUtility.toIRFileName(part.getCaseSensitiveName());
			} else {
				ZipFile zipFile = null;
				try {
					  zipFile = new ZipFile(getID());
					  if(partName != null) {
						  ZipEntry ze = zipFile.getEntry(partName); 
						  if(ze != null) {
							  sourceName = partName;
						  }
						  else if (Util.isEGLFileName(partName)) {
							  // Sometimes the case-insensitive file is passed in, which won't find the entry via zipFile.getEntry().
							  // We must manually check in an "interned" way.
							  Enumeration<? extends ZipEntry> entries = zipFile.entries();
							  while (entries.hasMoreElements()) {
								  ZipEntry entry = entries.nextElement();
								  if (NameUtile.equals(partName, NameUtile.getAsName(entry.getName()))) {
									  sourceName = entry.getName();
									  break;
								  }
							  }
						  }
					  }
					  zipFile.close(); 
				  } catch(IOException io){ 
				  } 
			}
			
			if(sourceName != null) {
				StringBuffer key = new StringBuffer(new Path(getID()).toString());
				key.append(BinaryReadOnlyFile.EGLAR_IR_SEPARATOR);
				key.append(sourceName);
				partOrigin = partOriginByPart.get( key.toString() );
				
				if ( partOrigin == null) {
					final BinaryReadOnlyFile brf = new BinaryReadOnlyFile(getID(), sourceName, irName);
					brf.setProject(project);
					partOrigin = new IPartOrigin() {

						@Override
						public boolean isOriginEGLFile() {
							return true;
						}

						@Override
						public IFile getEGLFile() {
							return brf;
						}

						@Override
						public boolean isSourceCodeAvailable() {
							return brf.exists();
						}};
						
					partOriginByPart.put(brf.getIrFullPathString(), partOrigin);
				} 
			}
		    
		} catch (PartNotFoundException e) {
		}
		
		return partOrigin;
	}

	@Override
	public IPartBinding getPartBinding(String packageName, String partName) {
		return zipEntry.getPartBinding(packageName, partName);
	}

	@Override
	public boolean hasPackage(String packageName) {
		return zipEntry.hasPackage(packageName);
	}

	@Override
	public int hasPart(String packageName, String partName) {
		int typeBindingKind = zipEntry.hasPart(packageName, partName);
		if(ITypeBinding.NOT_FOUND_BINDING == typeBindingKind) {
			ZipFile zipFile = null;
			try {
				  zipFile = new ZipFile(getID());
				  if(partName != null) {
					  ZipEntry ze = zipFile.getEntry(partName); 
					  if(ze != null) {
						  typeBindingKind = ITypeBinding.FILE_BINDING;
					  }
					  else if (Util.isEGLFileName(partName)) {
						  // Sometimes the case-insensitive file is passed in, which won't find the entry via zipFile.getEntry().
						  // We must manually check in an "interned" way.
						  Enumeration<? extends ZipEntry> entries = zipFile.entries();
						  while (entries.hasMoreElements()) {
							  ZipEntry entry = entries.nextElement();
							  if (NameUtile.equals(partName, NameUtile.getAsName(entry.getName()))) {
								  typeBindingKind = ITypeBinding.FILE_BINDING;
								  break;
							  }
						  }
					  }
				  }
				  zipFile.close(); 
			  } catch(IOException io){ 
			  } 
		}
		return typeBindingKind;
	}

	@Override
	public IEnvironment getRealizingEnvironment() {
		return zipEntry.getRealizingEnvironment();
	}

	@Override
	public IPartBinding getCachedPartBinding(String packageName, String partName) {
		return zipEntry.getCachedPartBinding(packageName, partName);
	}

	@Override
	public ObjectStore[] getObjectStores() {
		return zipEntry.getObjectStores();
	}

	@Override
	public Part findPart(String packageName, String name) throws PartNotFoundException {
		return zipEntry.findPart(packageName, name);
	}

	@Override
	public boolean isZipFile() {
		return true;
	}

	@Override
	public boolean isProject() {
		return false;
	}

	@Override
	public String getID() {
		return zipEntry.getID();
	}
	
	@Override
	public void clear() {
		//do not clear this, as the zipEntry is shared between multiple projects
	}

	@Override
	public boolean hasEntry(String entry) {
		return zipEntry.hasEntry(entry);
	}

	@Override
	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		return zipEntry.getAllKeysFromPkg(pkg, includeSubPkgs);
	}
	
	@Override
	public String toString() {
		// for easier debugging
		return zipEntry.getObjectStore().toString();
	}
}

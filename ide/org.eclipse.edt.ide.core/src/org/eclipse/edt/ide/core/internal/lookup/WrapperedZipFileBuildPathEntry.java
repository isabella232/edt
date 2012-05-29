package org.eclipse.edt.ide.core.internal.lookup;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.ZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.IZipFileBindingBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.IWorkingCopyBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.ObjectStore;

public class WrapperedZipFileBuildPathEntry implements IZipFileBindingBuildPathEntry, IWorkingCopyBuildPathEntry{
	ZipFileBindingBuildPathEntry zipEntry;
	IProject project;

	public WrapperedZipFileBuildPathEntry(ZipFileBindingBuildPathEntry zipEntry, IProject project) {
		super();
		this.zipEntry = zipEntry;
		this.project = project;
	}

	@Override
	public IPartOrigin getPartOrigin(String[] packageName, String partName) {
		try {
			Part part = findPart(packageName, partName);
			if (part != null) {
				String sourceName = part.eGet("filename").toString();
				final BinaryReadOnlyFile brf = new BinaryReadOnlyFile(getID(), sourceName);
				brf.setProject(project);
				return new IPartOrigin() {

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
			}
		} catch (PartNotFoundException e) {
		}
		return null;
	}

	@Override
	public IPartBinding getPartBinding(String[] packageName, String partName) {
		return zipEntry.getPartBinding(packageName, partName);
	}

	@Override
	public boolean hasPackage(String[] packageName) {
		return zipEntry.hasPackage(packageName);
	}

	@Override
	public int hasPart(String[] packageName, String partName) {
		return zipEntry.hasPart(packageName, partName);
	}

	@Override
	public IEnvironment getRealizingEnvironment() {
		return zipEntry.getRealizingEnvironment();
	}

	@Override
	public IPartBinding getCachedPartBinding(String[] packageName,
			String partName) {
		return zipEntry.getCachedPartBinding(packageName, partName);
	}

	@Override
	public void addPartBindingToCache(IPartBinding partBinding) {
		zipEntry.addPartBindingToCache(partBinding);
	}

	@Override
	public ObjectStore[] getObjectStores() {
		return zipEntry.getObjectStores();
	}

	@Override
	public Part findPart(String[] packageName, String name)
			throws PartNotFoundException {
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
	
}

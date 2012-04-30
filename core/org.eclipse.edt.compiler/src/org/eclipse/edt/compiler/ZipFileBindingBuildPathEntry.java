package org.eclipse.edt.compiler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.io.ZipFileBuildPathEntry;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.CachingObjectStore;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;
import org.eclipse.edt.mof.serialization.ObjectStore;

public abstract class ZipFileBindingBuildPathEntry extends ZipFileBuildPathEntry implements IBuildPathEntry, IZipFileEntryManager{

	private ObjectStore store;
	private String fileExtension;
	
	public ZipFileBindingBuildPathEntry(String path, String fileExtension) {
		super(path);
		this.fileExtension = fileExtension;
		processEntries();
	}


	private Map<String, Map<String, IRPartBinding>> partBindingsByPackage = new HashMap<String, Map<String,IRPartBinding>>();
	private Map<String, IRPartBinding> partBindingsWithoutPackage = new HashMap<String, IRPartBinding>();
	
	protected abstract IEnvironment getEnvironment();
	
	public void clear(){
		super.clear();
		partBindingsByPackage.clear();
		partBindingsWithoutPackage.clear();
		
		if (store instanceof CachingObjectStore) {
			((CachingObjectStore)store).clearCache();
		}
	}

	public IPartBinding getPartBinding(String packageName,String partName){
		IPartBinding partBinding = getCachedPartBinding(packageName,partName);
		
		if (partBinding == null){
			partBinding = getPartBinding(getEntry(packageName,partName));
		}

		return partBinding;
	}
	
	public IRPartBinding getCachedPartBinding(String packageName,String partName){
		IRPartBinding partBinding = null;
		if (packageName == null || packageName.length() == 0){
			partBinding = partBindingsWithoutPackage.get(partName);
		}else{
			Map<String, IRPartBinding> partpackage = partBindingsByPackage.get(packageName);
			if (partpackage != null){
				partBinding = partpackage.get(partName);
			}
		}
		
		return partBinding;
	}
	
	public boolean isProject(){
		return false;
	}

	public int hasPart(String packageName,String partName){
		IPartBinding partBinding = getPartBinding(packageName,partName);
		if (partBinding != null){
			return partBinding.getKind();
		}
		
		return ITypeBinding.NOT_FOUND_BINDING;
	}
	
	public Part findPart(String packageName, String partName) throws PartNotFoundException {
		if (hasPart(packageName, partName) != ITypeBinding.NOT_FOUND_BINDING) {
			EObject obj = getPartObject(getEntry(packageName, partName));
			if (obj instanceof Part) {
				return (Part)obj;
			}
		}
		return null;
	}
	
	protected Map<String, IRPartBinding> getPackagePartBinding(String packageName) {
		Map<String, IRPartBinding> map = partBindingsByPackage.get(packageName);
	    if (map == null) {
	        map = new HashMap<String, IRPartBinding>();
	        partBindingsByPackage.put(packageName, map);
	    }
	    return map;
	}

	public Map<String, IRPartBinding> getPartBindingsWithoutPackage() {
		return partBindingsWithoutPackage;
	}

	public IEnvironment getRealizingEnvironment(){
		throw new UnsupportedOperationException();
	}
	
	protected String getFileExtension() {
		return fileExtension;
	}
	
	
	public void setStore(ObjectStore store) {
		this.store = store;
	}
		
	public IRPartBinding getPartBinding(String entry){
		if (entry == null || entry.length() == 0){
			return null;
		}
		
		IRPartBinding retVal = null;
		
		String partname = getPartName(entry);
		String packageName = getPackageName(entry);

		retVal = getCachedPartBinding(packageName, partname);
		if (retVal != null) {
			return retVal;
		}
		
		EObject part = getPartObject(entry);
		if(part != null){
			
    		retVal = BindingUtil.createPartBinding(part);
    		if (retVal != null){
    			Map<String, IRPartBinding> partpackage = getPackagePartBinding(packageName);
    			partpackage.put(retVal.getIrPart().getName(),retVal);
    			retVal.setEnvironment(getEnvironment());
    			bindingLoaded(retVal);   			
    		}
    	}
    	return retVal;
		
	}

	protected EObject getPartObject(String entry){
		
		if (entry == null || entry.length() == 0){
			return null;
		}			
		String key = convertToStoreKey(entry);
		try {
			return store.get(key);
		} catch (DeserializationException e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}
	
	protected void bindingLoaded(IRPartBinding partBinding) {
		//default is to do nothing
	}
	
	public ObjectStore getObjectStore() {
		return store;
	}

	public ObjectStore[] getObjectStores() {
		return new ObjectStore[]{store};
	}

	public boolean hasEntry(String entry) {
		
		entry = entry.toUpperCase().toLowerCase();
		String[] entries = getAllEntries();
		for (int i = 0; i < entries.length; i++) {
			if (entry.equalsIgnoreCase(entries[i])) {
				return true;
			}
		}
		return false;
	}
	
}

package org.eclipse.edt.compiler;

import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.lookup.EnumerationManager;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibraryManager;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;

public interface ISystemEnvironment extends IBindingEnvironment {
	Map<String, List<ObjectStore>> getStores();
	IEnvironment getIREnvironment();
	EnumerationManager getEnumerationManager();
	SystemLibraryManager getSystemLibraryManager();
}

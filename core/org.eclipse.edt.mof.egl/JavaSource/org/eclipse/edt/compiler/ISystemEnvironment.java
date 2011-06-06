package org.eclipse.edt.compiler;

import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;

public interface ISystemEnvironment extends IBindingEnvironment {
	void clearParts();
	Map<String, List<ObjectStore>> getStores();
	IEnvironment getIREnvironment();
}

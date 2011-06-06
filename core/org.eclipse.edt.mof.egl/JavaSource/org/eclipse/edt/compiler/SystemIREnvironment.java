package org.eclipse.edt.compiler;

import java.util.List;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.lookup.EglLookupDelegate;
import org.eclipse.edt.mof.impl.Bootstrap;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.ObjectStore;

public class SystemIREnvironment extends Environment {

	
	public SystemIREnvironment() {
		super();
		Bootstrap.initialize(this);
	}

	@Override
	public void reset() {
		super.reset();
		registerLookupDelegate(Type.EGL_KeyScheme, new EglLookupDelegate());
	}
		
}

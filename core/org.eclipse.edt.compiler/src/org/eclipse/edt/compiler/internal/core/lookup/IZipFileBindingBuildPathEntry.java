package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.mof.serialization.IZipFileEntryManager;

public interface IZipFileBindingBuildPathEntry extends IBuildPathEntry, IZipFileEntryManager {
	void clear();
}

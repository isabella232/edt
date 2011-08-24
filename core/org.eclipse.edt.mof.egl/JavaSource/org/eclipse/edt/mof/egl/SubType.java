package org.eclipse.edt.mof.egl;

import java.util.List;

public interface SubType {
	List<StructPart> getSuperTypes();
	boolean isSubtypeOf(StructPart part);

}

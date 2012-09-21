package org.eclipse.edt.gen.egldoc;

import java.util.Comparator;

import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementComparator implements Comparator<NamedElement> {

	@Override
	public int compare(NamedElement arg0, NamedElement arg1) {
		return arg0.getCaseSensitiveName().compareTo(arg1.getCaseSensitiveName());
	}

	
	
}

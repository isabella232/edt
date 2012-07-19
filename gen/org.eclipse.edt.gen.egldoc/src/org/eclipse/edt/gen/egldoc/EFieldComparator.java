package org.eclipse.edt.gen.egldoc;

import java.util.Comparator;
import org.eclipse.edt.mof.EField;

public class EFieldComparator implements Comparator<EField> {

	@Override
	public int compare(EField arg0, EField arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}	
}

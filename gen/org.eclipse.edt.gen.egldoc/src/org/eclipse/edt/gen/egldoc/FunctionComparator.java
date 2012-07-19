package org.eclipse.edt.gen.egldoc;

import java.util.Comparator;
import org.eclipse.edt.mof.egl.Function;

public class FunctionComparator implements Comparator<Function> {

	@Override
	public int compare(Function arg0, Function arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}	
}

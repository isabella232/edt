package org.eclipse.edt.gen.egldoc;

import java.util.Comparator;

import org.eclipse.edt.mof.egl.Field;

public class FieldComparator implements Comparator<Field> {

	@Override
	public int compare(Field arg0, Field arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}

	
	
}

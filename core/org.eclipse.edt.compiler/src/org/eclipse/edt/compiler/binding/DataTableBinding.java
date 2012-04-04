/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding;


/**
 * @author Dave Murray
 */
public class DataTableBinding extends FixedStructureBinding {
	
	transient private DataTableDataBinding staticTableDataBinding;
	
    public DataTableBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
	public int getKind() {
		return DATATABLE_BINDING;
	}

	public void clear() {
		super.clear();
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public IDataBinding getStaticDataTableDataBinding() {
		if(staticTableDataBinding == null) {
			staticTableDataBinding = new DataTableDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticTableDataBinding;
	}
	
	public boolean isDeclarablePart() {
		return false;
	}

	public StaticPartDataBinding getStaticPartDataBinding() {
		return (StaticPartDataBinding)getStaticDataTableDataBinding();
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}

}

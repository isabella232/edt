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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Dave Murray
 */
public class VariableFormFieldBinding extends FormFieldBinding {
	
	private Object initialValue;
    protected transient DataItemBinding dataItemReference;


	public VariableFormFieldBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
    }

	public boolean isVariable() {
		return true;
	}

	public Object getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
	}

	public DataItemBinding getDataItemReference() {
		return dataItemReference;
	}

	public void setDataItemReference(DataItemBinding dataItemReference) {
		this.dataItemReference = dataItemReference;
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.defaultWriteObject();
        writeTypeBindingReference(out, dataItemReference);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
        dataItemReference = (DataItemBinding)readTypeBindingReference(in);
     }
}

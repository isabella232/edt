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
import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;


public class AnnotationFieldBinding extends AnnotationBinding {

	private IAnnotationTypeBinding enclosingAnnotationType;
	private transient Object value;
	
	public AnnotationFieldBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding, IAnnotationTypeBinding enclosingAnnotationType) {
		super(caseSensitiveInternedName, declarer, typeBinding, true);
		this.enclosingAnnotationType = enclosingAnnotationType;
	}
	
	public boolean isAnnotationField() {
		return true;	
	}
	
	public IAnnotationTypeBinding getEnclosingAnnotationType() {
		return enclosingAnnotationType;
	}
	
    protected boolean shouldSerializeTypeBinding() {
    	return false;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        
        if (value instanceof ITypeBinding) {
            out.writeBoolean(true);
            writeTypeBindingReference(out, (ITypeBinding) value);
        }
        else {
            out.writeBoolean(false);
            if (value instanceof Node) {
                out.writeObject(null);
            }
            else {
                out.writeObject(value); 
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        typeBinding = PrimitiveTypeBinding.getInstance(Primitive.ANY);
        boolean isType = in.readBoolean();
        if (isType) {
            value = readTypeBindingReference(in);
        }
        else {
            value = in.readObject();
        }
    }
    
    public Object getValue() {
	    if (value instanceof ITypeBinding) {
	        return realizeTypeBinding((ITypeBinding)value, getDeclaringPart() != null ? getDeclaringPart().getEnvironment() : null);
	    }
	    return value;
    }
    
    protected void primSetValue(Object value) {
    	this.value = value;
    }
    
    public AnnotationBinding createCopy() {
    	AnnotationFieldBinding copy = new AnnotationFieldBinding(getCaseSensitiveName(), getDeclaringPart(), getType(), getEnclosingAnnotationType());
    	populateCopy(copy);
    	return copy;
    }
    
	protected void populateCopy(AnnotationBinding copy) {
		super.populateCopy(copy);
		copy.primSetValue(getValue());
	}

}

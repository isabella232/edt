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
package org.eclipse.edt.compiler.internal.sql;


import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Primitive;

/**
 * Insert the type's description here.
 * Creation date: (6/18/2001 4:03:11 PM)
 * @author: Paul R. Harmon
 */
public class ItemNameToken extends Token {
	private Expression expression = null;
	private IDataBinding sqlIOObject;
	private boolean followsLikeKeyword = false;
	/**
	 * ItemNameToken constructor comment.
	 */
	public ItemNameToken() {
		super();
	}
	/**
	 * ItemNameToken constructor comment.
	 * @param string java.lang.String
	 * @param sqlIOObject org.eclipse.edt.compiler.internal.compiler.parts.SQLIOObject
	 */
	public ItemNameToken(String string, IDataBinding sqlIOObject) {

		this.sqlIOObject = sqlIOObject;
		this.string = string;
	}


	public boolean isHostVariableToken() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.compiler.sql.Token#getSQLString()
	 */
	public String getSQLString() {
		if (getDataBinding() == null) {
			return "";
		}

		if (isConstant(getDataBinding())) {
		    
		    Object value = ((VariableBinding) getDataBinding()).getConstantValue();
		    if (value instanceof String) {
		        if (isDBChar(getDataBinding())) {
					return "G\"" + value.toString() + "\"";
		        }
		        else {
					return "\"" + value.toString() + "\"";
		        }
		    }
		    else {
		        return value.toString();
		    }
		}

		return "\"" + getExpression().getCanonicalString() + "\"";
	}
	public void setFollowsLike(){
		followsLikeKeyword = true;
	}
	public boolean getFollowsLike(){
		return followsLikeKeyword;	
	}
	
	public boolean isItemNameToken() {
		return true;
	}

    public Expression getExpression() {
        return expression;
    }
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    
    private IDataBinding getDataBinding() {
        if (getExpression() == null) {
            return null;
        }
        if (getExpression().resolveDataBinding() == null || getExpression().resolveDataBinding() == IBinding.NOT_FOUND_BINDING) {
            return null;
        }
        return getExpression().resolveDataBinding();
    }
    
    private boolean isConstant(IDataBinding dataBinding) {
        if (dataBinding == null || dataBinding.getType() == null || dataBinding.getType() == IBinding.NOT_FOUND_BINDING) {
            return false;
        }
        if (dataBinding.getKind() == IDataBinding.LOCAL_VARIABLE_BINDING || dataBinding.getKind() == IDataBinding.CLASS_FIELD_BINDING) {
            VariableBinding var = (VariableBinding) dataBinding;
            return var.isConstant();
        }
        return false;
    }
    
    private boolean isDBChar(IDataBinding dataBinding) {
        if (dataBinding == null || dataBinding.getType() == null || dataBinding.getType() == IBinding.NOT_FOUND_BINDING) {
            return false;
        }
        if (dataBinding.getType().getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
            Primitive prim = ((PrimitiveTypeBinding) dataBinding.getType()).getPrimitive();
            return prim.getType() == Primitive.DBCHAR_PRIMITIVE;
        }
        return false;
    }
}

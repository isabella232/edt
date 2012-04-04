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
package org.eclipse.edt.compiler.internal.dli;

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.DLIDefaultStatementFactory;


/**
 * @author Harmon
 */
public class DLIInfo {

    private DLIModel model;
    private DLIDefaultStatementFactory statementFactory;
    private boolean hasDLICall;
    private ITypeBinding[] typeBindings;
    private boolean defaultStatement;
    private int inlineValueStart;
    
    
    public boolean isHasDLICall() {
        return hasDLICall;
    }
    public void setHasDLICall(boolean hasDLICall) {
        this.hasDLICall = hasDLICall;
    }
    public DLIModel getModel() {
        return model;
    }
    public void setModel(DLIModel model) {
        this.model = model;
    }
    public DLIDefaultStatementFactory getStatementFactory() {
        return statementFactory;
    }
    public void setStatementFactory(DLIDefaultStatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }
    public ITypeBinding[] getTypeBindings() {
        return typeBindings;
    }
    public void setTypeBindings(ITypeBinding[] typeBindings) {
        this.typeBindings = typeBindings;
    }
    public DLIInfo() {
        super();
    }

    public boolean isDefaultStatement() {
        return defaultStatement;
    }
    public void setDefaultStatement(boolean defaultStatement) {
        this.defaultStatement = defaultStatement;
    }
    public int getInlineValueStart() {
        return inlineValueStart;
    }
    public void setInlineValueStart(int inlineValueStart) {
        this.inlineValueStart = inlineValueStart;
    }
}

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

import java.util.HashMap;

import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLParser;


/**
 * @author Harmon
 */
public class SQLInfo {
    private boolean defaultStatment;
    private HashMap clauseMap = new HashMap();
    private EGLSQLGenerationTokens genTokens;
    private EGLSQLParser parser;
    private int inlineValueStart;

    public HashMap getClauseMap() {
        return clauseMap;
    }
    public void setClauseMap(HashMap clauseMap) {
        this.clauseMap = clauseMap;
    }
    public boolean isDefaultStatement() {
        return defaultStatment;
    }
    public void setDefaultStatment(boolean defaultStatment) {
        this.defaultStatment = defaultStatment;
    }
    public int getInlineValueStart() {
        return inlineValueStart;
    }
    public void setInlineValueStart(int inlineValueStart) {
        this.inlineValueStart = inlineValueStart;
    }
    public EGLSQLParser getParser() {
        return parser;
    }
    public void setParser(EGLSQLParser parser) {
        this.parser = parser;
    }
    public EGLSQLGenerationTokens getGenTokens() {
        return genTokens;
    }
    public void setGenTokens(EGLSQLGenerationTokens genTokens) {
        this.genTokens = genTokens;
    }
 }

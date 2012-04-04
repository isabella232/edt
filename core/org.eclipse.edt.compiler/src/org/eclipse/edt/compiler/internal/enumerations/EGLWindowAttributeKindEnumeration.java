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
package org.eclipse.edt.compiler.internal.enumerations;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.edt.compiler.internal.IEGLConstants;


public class EGLWindowAttributeKindEnumeration extends EGLEnumeration {

    private static final EGLWindowAttributeKindEnumeration INSTANCE = new EGLWindowAttributeKindEnumeration();
    
    public static final EGLEnumerationValue COMMENT_LINE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_COMMENTLINE, IEGLConstants.WINDOWATTRIBUTEKIND_COMMENT_LINE);
    public static final EGLEnumerationValue ERROR_LINE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_ERRORLINE, IEGLConstants.WINDOWATTRIBUTEKIND_ERROR_LINE);
    public static final EGLEnumerationValue FORM_LINE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_FORMLINE, IEGLConstants.WINDOWATTRIBUTEKIND_FORM_LINE);
    public static final EGLEnumerationValue MENU_LINE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_MENULINE, IEGLConstants.WINDOWATTRIBUTEKIND_MENU_LINE);
    public static final EGLEnumerationValue MESSAGE_LINE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_MESSAGELINE, IEGLConstants.WINDOWATTRIBUTEKIND_MESSAGE_LINE);
    public static final EGLEnumerationValue PROMPT_LINE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_PROMPTLINE, IEGLConstants.WINDOWATTRIBUTEKIND_PROMPT_LINE);
    public static final EGLEnumerationValue COLOR = new EGLEnumerationValue(IEGLConstants.MNEMONIC_COLOR, IEGLConstants.WINDOWATTRIBUTEKIND_COLOR);
    public static final EGLEnumerationValue INTENSITY = new EGLEnumerationValue(IEGLConstants.MNEMONIC_INTENSITY, IEGLConstants.WINDOWATTRIBUTEKIND_INTENSITY);
    public static final EGLEnumerationValue HIGHLIGHT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_HIGHLIGHT, IEGLConstants.WINDOWATTRIBUTEKIND_HIGHLIGHT);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(COMMENT_LINE.getName().toUpperCase().toLowerCase(), COMMENT_LINE);
        valuesMap.put(ERROR_LINE.getName().toUpperCase().toLowerCase(), ERROR_LINE);
        valuesMap.put(FORM_LINE.getName().toUpperCase().toLowerCase(), FORM_LINE);
        valuesMap.put(MENU_LINE.getName().toUpperCase().toLowerCase(), MENU_LINE);
        valuesMap.put(MESSAGE_LINE.getName().toUpperCase().toLowerCase(), MESSAGE_LINE);
        valuesMap.put(PROMPT_LINE.getName().toUpperCase().toLowerCase(), PROMPT_LINE);
        valuesMap.put(COLOR.getName().toUpperCase().toLowerCase(), COLOR);
        valuesMap.put(INTENSITY.getName().toUpperCase().toLowerCase(), INTENSITY);
        valuesMap.put(HIGHLIGHT.getName().toUpperCase().toLowerCase(), HIGHLIGHT);
    }
    
    private EGLWindowAttributeKindEnumeration() {}
    
    public static EGLWindowAttributeKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return WINDOWATTRIBUTEKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_WINDOWATTRIBUTEKIND;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getValue(java.lang.String)
     */
    public EGLEnumerationValue getValue(String valueName) {
        return (EGLEnumerationValue)valuesMap.get(valueName.toUpperCase().toLowerCase());
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getValues()
     */
    public Collection getValues() {
        return valuesMap.values();
    }
	public boolean isResolvable() {
		return true;
	}
}

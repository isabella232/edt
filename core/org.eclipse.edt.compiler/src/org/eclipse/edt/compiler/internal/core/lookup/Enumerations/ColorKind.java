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
package org.eclipse.edt.compiler.internal.core.lookup.Enumerations;

import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationTypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
public class ColorKind extends Enumeration{
    public final static ColorKind INSTANCE = new ColorKind();
    public final static int TYPE_CONSTANT = COLORKIND;

    public final static int BLACK_CONSTANT = 1;
    public final static int BLUE_CONSTANT = 2;
    public final static int CYAN_CONSTANT = 3;
    public final static int DEFAULTCOLOR_CONSTANT = 4;
    public final static int GREEN_CONSTANT = 5;
    public final static int MAGENTA_CONSTANT = 6;
    public final static int RED_CONSTANT = 7;
    public final static int WHITE_CONSTANT = 8;
    public final static int YELLOW_CONSTANT = 9;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("ColorKind"), COLORKIND);
    public final static SystemEnumerationDataBinding BLACK = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("black"), null, TYPE, BLACK_CONSTANT);
    public final static SystemEnumerationDataBinding BLUE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("blue"), null, TYPE, BLUE_CONSTANT);
    public final static SystemEnumerationDataBinding CYAN = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("cyan"), null, TYPE, CYAN_CONSTANT);
    public final static SystemEnumerationDataBinding DEFAULTCOLOR = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("defaultColor"), null, TYPE, DEFAULTCOLOR_CONSTANT);
    public final static SystemEnumerationDataBinding GREEN = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("green"), null, TYPE, GREEN_CONSTANT);
    public final static SystemEnumerationDataBinding MAGENTA = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("magenta"), null, TYPE, MAGENTA_CONSTANT);
    public final static SystemEnumerationDataBinding RED = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("red"), null, TYPE, RED_CONSTANT);
    public final static SystemEnumerationDataBinding WHITE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("white"), null, TYPE, WHITE_CONSTANT);
    public final static SystemEnumerationDataBinding YELLOW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("yellow"), null, TYPE, YELLOW_CONSTANT);

    static {
        TYPE.setValid(true);
        TYPE.addEnumeration(BLACK);
        TYPE.addEnumeration(BLUE);
        TYPE.addEnumeration(CYAN);
        TYPE.addEnumeration(DEFAULTCOLOR);
        TYPE.addEnumeration(GREEN);
        TYPE.addEnumeration(MAGENTA);
        TYPE.addEnumeration(RED);
        TYPE.addEnumeration(WHITE);
        TYPE.addEnumeration(YELLOW);
    };
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}

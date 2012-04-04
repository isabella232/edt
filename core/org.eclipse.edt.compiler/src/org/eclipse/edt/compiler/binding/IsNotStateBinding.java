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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class IsNotStateBinding extends DataBinding {
	
	public static final int EVENT_KEY = 0;
	public static final int SYSTEM_TYPE = 1;
	public static final int IO_ERROR = 2;
	public static final int TEXTFIELD = 3;
	public static final int TEXTFIELD_OR_STRING = 4;
	public static final int VALID_FOR_NUMERIC = 5;
	public static final int TRUNC_KIND = 7;
	public static final int MODIFIED_KIND = 8;
	
	public static final IsNotStateBinding ENTER = new IsNotStateBinding(IEGLConstants.MNEMONIC_ENTER, EVENT_KEY);
	public static final IsNotStateBinding BYPASS = new IsNotStateBinding(IEGLConstants.MNEMONIC_BYPASS, EVENT_KEY);
	public static final IsNotStateBinding CLEAR = new IsNotStateBinding(IEGLConstants.MNEMONIC_CLEAR, EVENT_KEY);
	public static final IsNotStateBinding PAKEY = new IsNotStateBinding(IEGLConstants.MNEMONIC_PAKEY, EVENT_KEY);
	public static final IsNotStateBinding PFKEY = new IsNotStateBinding(IEGLConstants.MNEMONIC_PFKEY, EVENT_KEY);
	public static final IsNotStateBinding PA1 = new IsNotStateBinding("pa1", EVENT_KEY);
	public static final IsNotStateBinding PA2 = new IsNotStateBinding("pa2", EVENT_KEY);
	public static final IsNotStateBinding PA3 = new IsNotStateBinding("pa3", EVENT_KEY);
	public static final IsNotStateBinding PF1 = new IsNotStateBinding("pf1", EVENT_KEY);
	public static final IsNotStateBinding PF2 = new IsNotStateBinding("pf2", EVENT_KEY);
	public static final IsNotStateBinding PF3 = new IsNotStateBinding("pf3", EVENT_KEY);
	public static final IsNotStateBinding PF4 = new IsNotStateBinding("pf4", EVENT_KEY);
	public static final IsNotStateBinding PF5 = new IsNotStateBinding("pf5", EVENT_KEY);
	public static final IsNotStateBinding PF6 = new IsNotStateBinding("pf6", EVENT_KEY);
	public static final IsNotStateBinding PF7 = new IsNotStateBinding("pf7", EVENT_KEY);
	public static final IsNotStateBinding PF8 = new IsNotStateBinding("pf8", EVENT_KEY);
	public static final IsNotStateBinding PF9 = new IsNotStateBinding("pf9", EVENT_KEY);
	public static final IsNotStateBinding PF10 = new IsNotStateBinding("pf10", EVENT_KEY);
	public static final IsNotStateBinding PF11 = new IsNotStateBinding("pf11", EVENT_KEY);
	public static final IsNotStateBinding PF12 = new IsNotStateBinding("pf12", EVENT_KEY);
	public static final IsNotStateBinding PF13 = new IsNotStateBinding("pf13", EVENT_KEY);
	public static final IsNotStateBinding PF14 = new IsNotStateBinding("pf14", EVENT_KEY);
	public static final IsNotStateBinding PF15 = new IsNotStateBinding("pf15", EVENT_KEY);
	public static final IsNotStateBinding PF16 = new IsNotStateBinding("pf16", EVENT_KEY);
	public static final IsNotStateBinding PF17 = new IsNotStateBinding("pf17", EVENT_KEY);
	public static final IsNotStateBinding PF18 = new IsNotStateBinding("pf18", EVENT_KEY);
	public static final IsNotStateBinding PF19 = new IsNotStateBinding("pf19", EVENT_KEY);
	public static final IsNotStateBinding PF20 = new IsNotStateBinding("pf20", EVENT_KEY);
	public static final IsNotStateBinding PF21 = new IsNotStateBinding("pf21", EVENT_KEY);
	public static final IsNotStateBinding PF22 = new IsNotStateBinding("pf22", EVENT_KEY);
	public static final IsNotStateBinding PF23 = new IsNotStateBinding("pf23", EVENT_KEY);
	public static final IsNotStateBinding PF24 = new IsNotStateBinding("pf24", EVENT_KEY);
	public static final IsNotStateBinding DEBUG = new IsNotStateBinding(IEGLConstants.MNEMONIC_DEBUG, SYSTEM_TYPE);
	public static final IsNotStateBinding ISERIESC = new IsNotStateBinding(IEGLConstants.MNEMONIC_ISERIESC, SYSTEM_TYPE);
	public static final IsNotStateBinding ISERIESJ = new IsNotStateBinding(IEGLConstants.MNEMONIC_ISERIESJ, SYSTEM_TYPE);
	public static final IsNotStateBinding ZOSBATCH = new IsNotStateBinding(IEGLConstants.MNEMONIC_ZOSBATCH, SYSTEM_TYPE);
	public static final IsNotStateBinding ZOSCICS = new IsNotStateBinding(IEGLConstants.MNEMONIC_ZOSCICS, SYSTEM_TYPE);
	public static final IsNotStateBinding AIX = new IsNotStateBinding(IEGLConstants.MNEMONIC_AIX, SYSTEM_TYPE);
	public static final IsNotStateBinding LINUX = new IsNotStateBinding(IEGLConstants.MNEMONIC_LINUX, SYSTEM_TYPE);
	public static final IsNotStateBinding WIN = new IsNotStateBinding(IEGLConstants.MNEMONIC_WIN, SYSTEM_TYPE);
	public static final IsNotStateBinding USS = new IsNotStateBinding(IEGLConstants.MNEMONIC_USS, SYSTEM_TYPE);
	public static final IsNotStateBinding VSEBATCH = new IsNotStateBinding(IEGLConstants.MNEMONIC_VSEBATCH, SYSTEM_TYPE);
	public static final IsNotStateBinding VSECICS = new IsNotStateBinding(IEGLConstants.MNEMONIC_VSECICS, SYSTEM_TYPE);
	public static final IsNotStateBinding HPUX = new IsNotStateBinding(IEGLConstants.MNEMONIC_HPUX, SYSTEM_TYPE);
	public static final IsNotStateBinding SOLARIS = new IsNotStateBinding(IEGLConstants.MNEMONIC_SOLARIS, SYSTEM_TYPE);
	public static final IsNotStateBinding IMSVS = new IsNotStateBinding(IEGLConstants.MNEMONIC_IMSVS, SYSTEM_TYPE);
	public static final IsNotStateBinding IMSBMP = new IsNotStateBinding(IEGLConstants.MNEMONIC_IMSBMP, SYSTEM_TYPE);
	public static final IsNotStateBinding JAVASCRIPT = new IsNotStateBinding(IEGLConstants.MNEMONIC_JAVASCRIPT, SYSTEM_TYPE);

	public static final IsNotStateBinding AIXCICS = new IsNotStateBinding(IEGLConstants.MNEMONIC_AIXCICS, SYSTEM_TYPE);
	public static final IsNotStateBinding NTCICS = new IsNotStateBinding(IEGLConstants.MNEMONIC_NTCICS, SYSTEM_TYPE);
	public static final IsNotStateBinding OS2 = new IsNotStateBinding(IEGLConstants.MNEMONIC_OS2, SYSTEM_TYPE);
	public static final IsNotStateBinding OS2CICS = new IsNotStateBinding(IEGLConstants.MNEMONIC_OS2CICS, SYSTEM_TYPE);
	public static final IsNotStateBinding OS2GUI = new IsNotStateBinding(IEGLConstants.MNEMONIC_OS2GUI, SYSTEM_TYPE);
	public static final IsNotStateBinding SCO = new IsNotStateBinding(IEGLConstants.MNEMONIC_SCO, SYSTEM_TYPE);
	public static final IsNotStateBinding SOLACICS = new IsNotStateBinding(IEGLConstants.MNEMONIC_SOLACICS, SYSTEM_TYPE);
	public static final IsNotStateBinding TSO = new IsNotStateBinding(IEGLConstants.MNEMONIC_TSO, SYSTEM_TYPE);
	public static final IsNotStateBinding VMCMS = new IsNotStateBinding(IEGLConstants.MNEMONIC_VMCMS, SYSTEM_TYPE);
	public static final IsNotStateBinding VMBATCH = new IsNotStateBinding(IEGLConstants.MNEMONIC_VMBATCH, SYSTEM_TYPE);
	public static final IsNotStateBinding WINGUI = new IsNotStateBinding(IEGLConstants.MNEMONIC_WINGUI, SYSTEM_TYPE);
	public static final IsNotStateBinding ZLINUX = new IsNotStateBinding(IEGLConstants.MNEMONIC_ZLINUX, SYSTEM_TYPE);
	
	public static final IsNotStateBinding IOERROR = new IsNotStateBinding(IEGLConstants.MNEMONIC_IOERROR, IO_ERROR);
	public static final IsNotStateBinding HARDIOERROR = new IsNotStateBinding(IEGLConstants.MNEMONIC_HARDIOERROR, IO_ERROR);
	public static final IsNotStateBinding SOFTIOERROR = new IsNotStateBinding(IEGLConstants.MNEMONIC_SOFTIOERROR, IO_ERROR);
	public static final IsNotStateBinding ENDOFFILE = new IsNotStateBinding(IEGLConstants.MNEMONIC_ENDOFFILE, IO_ERROR);
	public static final IsNotStateBinding NORECORDFOUND = new IsNotStateBinding(IEGLConstants.MNEMONIC_NORECORDFOUND, IO_ERROR);
	public static final IsNotStateBinding DUPLICATE = new IsNotStateBinding(IEGLConstants.MNEMONIC_DUPLICATE, IO_ERROR);
	public static final IsNotStateBinding DEADLOCK = new IsNotStateBinding(IEGLConstants.MNEMONIC_DEADLOCK, IO_ERROR);
	public static final IsNotStateBinding INVALIDFORMAT = new IsNotStateBinding(IEGLConstants.MNEMONIC_INVALIDFORMAT, IO_ERROR);
	public static final IsNotStateBinding FILENOTAVAILABLE = new IsNotStateBinding(IEGLConstants.MNEMONIC_FILENOTAVAILABLE, IO_ERROR);
	public static final IsNotStateBinding FILENOTFOUND = new IsNotStateBinding(IEGLConstants.MNEMONIC_FILENOTFOUND, IO_ERROR);
	public static final IsNotStateBinding FULL = new IsNotStateBinding(IEGLConstants.MNEMONIC_FULL, IO_ERROR);
	public static final IsNotStateBinding UNIQUE = new IsNotStateBinding(IEGLConstants.MNEMONIC_UNIQUE, IO_ERROR);
	public static final IsNotStateBinding CURSOR = new IsNotStateBinding(IEGLConstants.MNEMONIC_CURSOR, TEXTFIELD);
	public static final IsNotStateBinding DATA = new IsNotStateBinding(IEGLConstants.MNEMONIC_DATA, TEXTFIELD);
	public static final IsNotStateBinding BLANKS = new IsNotStateBinding(IEGLConstants.MNEMONIC_BLANKS, TEXTFIELD_OR_STRING);
	public static final IsNotStateBinding NUMERIC = new IsNotStateBinding(IEGLConstants.MNEMONIC_NUMERIC, VALID_FOR_NUMERIC);
	public static final IsNotStateBinding MODIFIED = new IsNotStateBinding(IEGLConstants.MNEMONIC_MODIFIED, MODIFIED_KIND);
	public static final IsNotStateBinding TRUNC = new IsNotStateBinding(IEGLConstants.MNEMONIC_TRUNC, TRUNC_KIND);
	
	private static Map STATES = new HashMap();
	static {
		STATES.put(ENTER.getName(), ENTER);
		STATES.put(BYPASS.getName(), BYPASS);
		STATES.put(CLEAR.getName(), CLEAR);
		STATES.put(PAKEY.getName(), PAKEY);
		STATES.put(PFKEY.getName(), PFKEY);
		STATES.put(PA1.getName(), PA1);
		STATES.put(PA2.getName(), PA2);
		STATES.put(PA3.getName(), PA3);
		STATES.put(PF1.getName(), PF1);
		STATES.put(PF2.getName(), PF2);
		STATES.put(PF3.getName(), PF3);
		STATES.put(PF4.getName(), PF4);
		STATES.put(PF5.getName(), PF5);
		STATES.put(PF6.getName(), PF6);
		STATES.put(PF7.getName(), PF7);
		STATES.put(PF8.getName(), PF8);
		STATES.put(PF9.getName(), PF9);
		STATES.put(PF10.getName(), PF10);
		STATES.put(PF11.getName(), PF11);
		STATES.put(PF12.getName(), PF12);
		STATES.put(PF13.getName(), PF13);
		STATES.put(PF14.getName(), PF14);
		STATES.put(PF15.getName(), PF15);
		STATES.put(PF16.getName(), PF16);
		STATES.put(PF17.getName(), PF17);
		STATES.put(PF18.getName(), PF18);
		STATES.put(PF19.getName(), PF19);
		STATES.put(PF20.getName(), PF20);
		STATES.put(PF21.getName(), PF21);
		STATES.put(PF22.getName(), PF22);
		STATES.put(PF23.getName(), PF23);
		STATES.put(PF24.getName(), PF24);
		
		STATES.put(DEBUG.getName(), DEBUG);
		STATES.put(ISERIESC.getName(), ISERIESC);
		STATES.put(ISERIESJ.getName(), ISERIESJ);
		STATES.put(ZOSBATCH.getName(), ZOSBATCH);
		STATES.put(ZOSCICS.getName(), ZOSCICS);
		STATES.put(AIX.getName(), AIX);
		STATES.put(LINUX.getName(), LINUX);
		STATES.put(WIN.getName(), WIN);
		STATES.put(USS.getName(), USS);
		STATES.put(VSEBATCH.getName(), VSEBATCH);
		STATES.put(VSECICS.getName(), VSECICS);
		STATES.put(HPUX.getName(), HPUX);
		STATES.put(SOLARIS.getName(), SOLARIS);
		STATES.put(IMSVS.getName(), IMSVS);
		STATES.put(IMSBMP.getName(), IMSBMP);
		STATES.put(JAVASCRIPT.getName(), JAVASCRIPT);

		STATES.put(AIXCICS.getName(), AIXCICS);
		STATES.put(NTCICS.getName(), NTCICS);
		STATES.put(OS2.getName(), OS2);
		STATES.put(OS2CICS.getName(), OS2CICS);
		STATES.put(OS2GUI.getName(), OS2GUI);
		STATES.put(SCO.getName(), SCO);
		STATES.put(SOLACICS.getName(), SOLACICS);
		STATES.put(TSO.getName(), TSO);
		STATES.put(VMCMS.getName(), VMCMS);
		STATES.put(VMBATCH.getName(), VMBATCH);
		STATES.put(WINGUI.getName(), WINGUI);
		STATES.put(ZLINUX.getName(), ZLINUX);
		
		
		STATES.put(IOERROR.getName(), IOERROR);
		STATES.put(HARDIOERROR.getName(), HARDIOERROR);
		STATES.put(SOFTIOERROR.getName(), SOFTIOERROR);
		STATES.put(ENDOFFILE.getName(), ENDOFFILE);
		STATES.put(NORECORDFOUND.getName(), NORECORDFOUND);
		STATES.put(DUPLICATE.getName(), DUPLICATE);
		STATES.put(DEADLOCK.getName(), DEADLOCK);
		STATES.put(INVALIDFORMAT.getName(), INVALIDFORMAT);
		STATES.put(FILENOTAVAILABLE.getName(), FILENOTAVAILABLE);
		STATES.put(FILENOTFOUND.getName(), FILENOTFOUND);
		STATES.put(FULL.getName(), FULL);
		STATES.put(UNIQUE.getName(), UNIQUE);
		STATES.put(CURSOR.getName(), CURSOR);
		STATES.put(DATA.getName(), DATA);
		STATES.put(BLANKS.getName(), BLANKS);
		STATES.put(NUMERIC.getName(), NUMERIC);
		STATES.put(MODIFIED.getName(), MODIFIED);
		STATES.put(TRUNC.getName(), TRUNC);
	}
	
	public static IsNotStateBinding toIsNotStateBinding(String identifier) {
		return (IsNotStateBinding) STATES.get(identifier);
	}

	private int kind;

	private IsNotStateBinding(String identifier, int kind) {
		super(InternUtil.internCaseSensitive(identifier), null, null);
		this.kind = kind;
	}

	public int getKind() {
		return IS_NOT_STATE;
	}
	
	public int getIsNotStateKind() {
		return kind;
	}
}

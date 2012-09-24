/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.handlers;

import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public interface IDataTypeBindingHandler {
	public void handle(Member dataBinding, Type typeBinding, InsertDataModel insertDataModel);
}

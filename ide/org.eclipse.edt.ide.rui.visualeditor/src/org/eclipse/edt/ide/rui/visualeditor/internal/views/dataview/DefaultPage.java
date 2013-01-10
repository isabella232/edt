/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview;

import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.ui.part.MessagePage;


public class DefaultPage extends MessagePage {
	public DefaultPage(){
		this.setMessage(Messages.NL_PDV_No_Page_Data_To_View);
	}
}

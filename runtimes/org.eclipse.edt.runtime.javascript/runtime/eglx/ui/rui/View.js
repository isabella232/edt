/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass(
	'eglx.ui.rui', 'View',
	{
	"constructor" : function() {
		this.properties = null;
		if (egl.Document == null)
			egl.Document = new egl.eglx.ui.rui.Document();
		this.document = egl.Document;
	},
	"setLogicalParent" : function(container){
		if (this.initialUI) {
			for (var n= 0; n< this.initialUI.length; n++) {
				var child = egl.unboxAny(this.initialUI[n]);
				child.setLogicalParent(parent);
			}
		}
	},
	"setParent" : function(parent) {
		if (parent == egl.Document)
			parent = parent.body;
		if (this.initialUI) {
			for (var n= 0; n< this.initialUI.length; n++) {
				var child = egl.unboxAny(this.initialUI[n]);
				if (!child) {
					throw egl.createRuntimeException( "CRRUI0001E", [n+1]);
				}
				else {
					if (child.eze$$DOMElement)
						parent.appendChild(child);
					else
						child.setParent(parent);
				}
			}
		}
	}
});

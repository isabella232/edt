/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.eglx.xml;

import java.util.HashMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;




public class DictionaryAdapter extends XmlAdapter<HashMap, eglx.lang.EDictionary> {

	@Override
	public EDictionary unmarshal(HashMap v) throws Exception {
		eglx.lang.EDictionary dict = new EDictionary();
		return null;
	}

	@Override
	public HashMap marshal(eglx.lang.EDictionary v) throws Exception {
		String[] keys = v.getKeyArray();
		HashMap fields = new HashMap();
		for(String key : keys){
			fields.put(key, v.get(key).toString());
		}
		return fields;
	}


}

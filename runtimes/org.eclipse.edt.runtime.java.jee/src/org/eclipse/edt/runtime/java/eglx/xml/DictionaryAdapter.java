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

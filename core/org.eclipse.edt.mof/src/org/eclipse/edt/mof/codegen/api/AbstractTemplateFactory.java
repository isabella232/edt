package org.eclipse.edt.mof.codegen.api;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AbstractTemplateFactory implements TemplateFactory {
	Map<String, Class<? extends Template>> templates = new HashMap<String, Class<? extends Template>>();

	public AbstractTemplateFactory() { super(); }
		
	public Template createTemplate(String key) throws TemplateException {
		Class<? extends Template> clazz = templates.get(key);
		if (clazz == null) throw new TemplateException("Template not found: " + key);
		try {
			return (Template)clazz.newInstance();
		} catch (IllegalAccessException e) {
			throw new TemplateException(e);
		} catch (InstantiationException e2) {
			throw new TemplateException(e2);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void load(String propertiesName) throws TemplateException {
		ResourceBundle bundle = ResourceBundle.getBundle(propertiesName);
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String className = bundle.getString(key);
			try {
				Class clazz = Class.forName(className);
				templates.put(key, clazz);
			} catch (ClassNotFoundException e3) {
				templates.put(key, MissingTemplate.class);
			} catch (NoClassDefFoundError err) {
				templates.put(key, MissingTemplate.class);
			}
		}
	}
	
	public abstract String getPropertyFileName();
}

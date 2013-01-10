/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Control;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Controls;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Group;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfileFactory;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Setting;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

import com.ibm.icu.util.StringTokenizer;

public class FormatProfileRootHelper {	
	//bundle is ConstructedFormattingMessages.properties file in the current directory
	private static ResourceBundle bundleForConstructedKeys = ResourceBundle.getBundle("org.eclipse.edt.ide.ui.internal.formatting.ui.ConstructedFormattingMessages"); //$NON-NLS-1$
	
	private static final String PROFILE_VERSION = "1.0"; //$NON-NLS-1$
	public static final String DELIMITER_COMMA = ","; //$NON-NLS-1$
	public static final String NLSKEY_LEADINGCHAR = "%"; //$NON-NLS-1$
	
	public static EGLFormatProfileRoot getEGLFormatProfileModel(IPath formatProfile){
		ResourceSet resourceSet = new ResourceSetImpl();	
		return getEGLFormatProfileModel(formatProfile, resourceSet);
	}
	
	private static EGLFormatProfileRoot getEGLFormatProfileModel(IPath formatProfile, ResourceSet resourceSet){		
		URI uri = null;
		if ( formatProfile.toOSString().startsWith( "jar:") ) {
			String path = formatProfile.toOSString();
			path = path.replaceAll( "\\\\", "/" );
			uri = URI.createURI( path );
		} else {
			uri = URI.createFileURI(formatProfile.toOSString());
		}
		
		Resource resource = resourceSet.getResource(uri, true);
		
		return (EGLFormatProfileRoot)(resource.getContents().get(0));
	}
	
	public static EGLFormatProfileRoot createNewEGLFormatProfileModel(IPath formatProfile){
		String encodingName = EDTUIPlugin.getDefault().getPreferenceStore().getString(EGLBasePlugin.OUTPUT_CODESET);
		return createNewEGLFormatProfileModel(formatProfile, encodingName);
	}
	
	private static EGLFormatProfileRoot createNewEGLFormatProfileModel(IPath formatProfile, String encoding){
		EGLFormatProfileRoot docRoot = ProfileFactory.eINSTANCE.createEGLFormatProfileRoot();
		FormatProfiles root = ProfileFactory.eINSTANCE.createFormatProfiles();
		root.setVersion(PROFILE_VERSION);
		docRoot.setFormatProfiles(root);
		try{
			URI uri = URI.createFileURI(formatProfile.toOSString());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.createResource(uri);
			resource.getContents().add(docRoot);
			if(encoding != null && encoding.length() > 0){
				Map options = new HashMap();
				options.put(XMLResource.OPTION_ENCODING, encoding);
				resource.save(options);		//create the file				
			}
			else
				resource.save(null);			
		}
		catch (IOException e) {
			e.printStackTrace();
			docRoot = null;
		}		
		
		return docRoot;
	}
	
	public static void saveEGLFormatProfile(IPath formatFilePath,  EGLFormatProfileRoot docRoot){
		try{
			persisEGLFormatProfile(formatFilePath, docRoot);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void persisEGLFormatProfile(IPath formatFilePath, EGLFormatProfileRoot docRoot) throws IOException{
		URI uri = URI.createFileURI(formatFilePath.toOSString());
		Resource savedResource = docRoot.eResource();
		Resource resource = savedResource;
		Map options = Collections.EMPTY_MAP;
		if(!savedResource.getURI().equals(uri)){		
			ResourceSet resourceSet = new ResourceSetImpl();
			resource = resourceSet.createResource(uri);
			
			//by default, create it with EGL preference
			String encoding = EDTUIPlugin.getDefault().getPreferenceStore().getString(EGLBasePlugin.OUTPUT_CODESET);
			if(encoding != null && encoding.length()>0){
				options = new HashMap();			
				options.put(XMLResource.OPTION_ENCODING, encoding);
			}						
		}
		resource.getContents().add(docRoot);
		resource.save(options);		//create the file					
	}
	
	/**
	 * try to find the profile node within <egl:format_profiles>, whose name is profileName
	 * 
	 * @param profileName
	 * @param formatprofiles
	 * @return - null if not found
	 */
	public static Profile findProfileByName(String profileName, FormatProfiles formatprofiles){
		EList profiles = formatprofiles.getProfile();
		for(Iterator it=profiles.iterator(); it.hasNext();){
			Profile profile = (Profile)it.next();
			if(profileName.equals(profile.getName())){
				return profile;				
			}				
		}
		return null;
	}
	
	public static Control getReferencedControl(DefaultProfile defaultProfile, ReferenceControl refControl){
		String refName = refControl.getRef();
		return getControlByName(defaultProfile, refName);		
	}
	
	private static Control getControlByName(DefaultProfile defaultProfile, String controlName){
		Controls controls = defaultProfile.getControls();
		EList cntrls = controls.getControl();
		for(Iterator it=cntrls.iterator(); it.hasNext();){
			Control control = (Control)it.next();
			String controlElemName = control.getName();
			if(controlElemName != null && controlElemName.equals(controlName))
				return control;
		}
		return null;
	}
	
	public static String[] parseChoices(String strChoices){
		return parseTokenizedString(strChoices, DELIMITER_COMMA) ;
	}

	public static String[] parseTokenizedString(String string2BeParsed, String demiliter) {
		StringTokenizer tokenizer = new StringTokenizer(string2BeParsed, demiliter);
		String[] choices = new String[tokenizer.countTokens()];
		int index = 0;
		//each token is the display name for the column
		while(tokenizer.hasMoreTokens()){
			choices[index] = tokenizer.nextToken();
			index++;
		}		
		return choices;
	}
	
	private static Category getCategoryByID(DefaultProfile defaultProfile, String categoryId){
		EList cats = defaultProfile.getCategory();
		for(Iterator it=cats.iterator(); it.hasNext();){
			Category cat = (Category)it.next();
			if(categoryId.equals(cat.getId()))
				return cat;
		}
		return null;
	}
	
	public static Preference getPreferenceByID(DefaultProfile defaultProfile, String categoryId, String prefId){
		Category cat = getCategoryByID(defaultProfile, categoryId);
		if(cat != null){		//if found category
			EList groups = cat.getGroup();
			for(Iterator grpIt = groups.iterator(); grpIt.hasNext();){
				Group group = (Group)grpIt.next();
				EList prefs = group.getPref();
				for(Iterator it = prefs.iterator(); it.hasNext();){
					Preference pref = (Preference)it.next();
					if(prefId.equals(pref.getId()))
						return pref;
				}
			}
		}
		return null;
	}
	
	public static Setting getPreferenceSettingByID(Profile profile, String categoryId, String prefId){
		EList settings = profile.getSetting();
		if(settings != null){
			for(Iterator it=settings.iterator(); it.hasNext();){
				Setting setting = (Setting)it.next();
				if(categoryId.equals(setting.getCategory()) && prefId.equals(setting.getPref()))
					return setting;
			}
		}
		return null;
	}
	
	public static List getProfilesWhoseBaseIs(String baseName, FormatProfiles profilesroot){
		List results = new ArrayList();
		EList profiles = profilesroot.getProfile();
		for(Iterator it= profiles.iterator(); it.hasNext();){
			Profile profile = (Profile)it.next();
			String profileBase = profile.getBase();
			if(profileBase != null && profileBase.equals(baseName))
				results.add(profile);
		}
		return results;
	}

	/**
	 * the key could be a delimitered string, need to get the resource string for each of its element
	 * 
	 * @param nlsKey
	 * @param delimiter
	 * @return - delimited nls resource string
	 */
	public static String getFormattingProfileNLSString(String nlsKey, String delimiter){	
		String[] nlsKeys = parseTokenizedString(nlsKey, delimiter);
		StringBuffer str = new StringBuffer();
		for(int i=0; i<nlsKeys.length; i++){
			if(i>0)
				str.append(delimiter);
			str.append(FormatProfileRootHelper.getFormattingProfileNLSString(nlsKeys[i]));
		}
		return str.toString();
	}

	/**
	 * the key should start with %, we will remove the %, then try to get the NLS resource string based on the key w/o %
	 * 
	 * @param nlsKey - should start with %
	 * @return - nls resource string
	 */
	public static String getFormattingProfileNLSString(String nlsKey){		
		//remove the leading %
		if(nlsKey.startsWith(FormatProfileRootHelper.NLSKEY_LEADINGCHAR))
			nlsKey = nlsKey.substring(nlsKey.indexOf(FormatProfileRootHelper.NLSKEY_LEADINGCHAR)+1);
		if(nlsKey.length()>0){
			try{
				return FormatProfileRootHelper.bundleForConstructedKeys.getString(nlsKey);
			}
			catch(MissingResourceException e){
				e.printStackTrace();
				return nlsKey;
			}
		}
		else
			return nlsKey;
	}


}

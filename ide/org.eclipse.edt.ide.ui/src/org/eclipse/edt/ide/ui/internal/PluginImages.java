/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * Bundle of most images used by the EGL plugin.
 */
public class PluginImages {

	private static final String NAME_PREFIX= "org.eclipse.edt.ide.ui."; //$NON-NLS-1$
	private static final int    NAME_PREFIX_LENGTH= NAME_PREFIX.length();

	private static URL fgIconBaseURL= null;

	// Determine display depth. If depth > 4 then we use high color images. Otherwise low color
	// images are used
	static {
		fgIconBaseURL= EDTUIPlugin.getDefault().getBundle().getEntry("icons/"); //$NON-NLS-1$
	}

	// The plugin registry
	private static ImageRegistry fgImageRegistry= null;
	private static HashMap fgAvoidSWTErrorMap= null;

	/*
	 * Available cached Images in the EGL plugin image registry.
	 */
	public static final String IMG_OBJS_EGL_BINARY_OBJECT = NAME_PREFIX + "binary_obj.gif";
	public static final String IMG_OBJS_EGL_BINARY_PROJECT_OPEN = NAME_PREFIX + "binproject_open_obj.gif";
	public static final String IMG_OBJS_EGL_BINARY_PROJECT_CLOSE = NAME_PREFIX + "binproject_close2_obj.gif";
	public static final String IMG_OBJS_EGL_MODEL= NAME_PREFIX + "egl_model_obj.gif"; //$NON-NLS-1$
	
	public static final String IMG_OBJS_PACKFRAG_ROOT= NAME_PREFIX + "eglsrcfldr_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_PACKAGE= NAME_PREFIX + "eglpkg_obj.gif"; 		//$NON-NLS-1$
	public static final String IMG_OBJS_EMPTY_PACKAGE= NAME_PREFIX + "emppkg_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_PACKFRAG_ROOT_EGLAR= NAME_PREFIX + "eglar_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_PACKFRAG_ROOT_EGLAR_CONTAINER = NAME_PREFIX + "libry_obj.gif"; //$NON-NLS-1$
	
	public static final String IMG_OBJS_EGLFILE= NAME_PREFIX + "eglfile_obj.gif"; 			//$NON-NLS-1$
	
	public static final String IMG_OBJS_BLDDESC= NAME_PREFIX + "builddescriptor_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_EGLBLD= NAME_PREFIX + "eglbld_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_PGM= NAME_PREFIX + "prgm_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_LIBRARY = NAME_PREFIX + "libry_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_FORMGRP= NAME_PREFIX + "frmgrp_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_TABLE= NAME_PREFIX + "dtatbl_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_UIRECORD= NAME_PREFIX + "uirec_obj.gif";	 //$NON-NLS-1$
	public static final String IMG_OBJS_PAGE= NAME_PREFIX + "paghdl_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_PAGEDATA= NAME_PREFIX + "pghdld_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_HANDLER= NAME_PREFIX + "handler_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_INTERFACE= NAME_PREFIX + "int_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_SERVICE= NAME_PREFIX + "svc_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_SERVICEBINDING= NAME_PREFIX + "svbv_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_WSDL= NAME_PREFIX + "wsdl_obj.gif"; 				//$NON-NLS-1$	
	public static final String IMG_OBJS_RECORD= NAME_PREFIX + "record_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_FUNCTION= NAME_PREFIX + "funct_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_PRIVATE_FUNCTION= NAME_PREFIX + "private_funct_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_DELEGATE= NAME_PREFIX + "delgat_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_EXTERNALTYPE= NAME_PREFIX + "exttyp_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_ENUMERATION= NAME_PREFIX + "enum_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_FORM= NAME_PREFIX + "form_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_DATAITEM = NAME_PREFIX + "dtaitm_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_STRUCTUREITEM = NAME_PREFIX + "stritm_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_CLASS= NAME_PREFIX + "class_obj.gif"; 			//$NON-NLS-1$

	public static final String IMG_OBJS_IMPORT = NAME_PREFIX + "import.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_IMPORTS = NAME_PREFIX + "imports.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_USEFORM = NAME_PREFIX + "usefrm_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_USESTATEMENT = NAME_PREFIX + "usestm_obj.gif"; //$NON-NLS-1$
			
	public static final String IMG_OBJS_CONSTANTDECL = NAME_PREFIX + "cnstnt_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_CONSTANTFORMFIELD = NAME_PREFIX + "cnstfm_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_VARIABLEDECL = NAME_PREFIX + "varfld_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_VARIABLEFORMFIELD = NAME_PREFIX + "vrfmfd_obj.gif"; //$NON-NLS-1$

	public static final String IMG_OBJS_SEARCH_DECL= NAME_PREFIX + "search_decl_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_SEARCH_REF= NAME_PREFIX + "search_ref_obj.gif"; 	//$NON-NLS-1$
	public static final String IMG_OBJS_CUNIT_RESOURCE= NAME_PREFIX + "resource_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_CFILE= NAME_PREFIX + "classf_obj.gif";  			//$NON-NLS-1$
	
	public static final String IMG_OBJS_EGL_VALIDATE= NAME_PREFIX + "eglDebugValidate.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_EGL_GENERATION= NAME_PREFIX + "eglgeneration.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_SQL_CHECK= NAME_PREFIX + "sqlcheck.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_PARTS_LIST= NAME_PREFIX + "partsbwser.gif"; //$NON-NLS-1$
	
	public static final String IMG_OBJS_GEN_SUCCESS= NAME_PREFIX + "gensuc_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_GEN_FAIL= NAME_PREFIX + "genfal_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_GEN_RUN= NAME_PREFIX + "statusProgressTransition_obj16.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_GEN_WARNING= NAME_PREFIX + "warning_obj.gif"; //$NON-NLS-1$
	
	public static final String IMG_OBJS_EGL_BUILDPATH_ORDER= NAME_PREFIX + "ep_order_obj.gif"; //$NON-NLS-1$
	
	public static final String IMG_OBJS_ERROR= NAME_PREFIX + "error_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_WARNING= NAME_PREFIX + "warning_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_UNKNOWN= NAME_PREFIX + "unknown_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_GHOST= NAME_PREFIX + "ghost.gif"; 				//$NON-NLS-1$

	
	public static final String IMG_OBJS_PARAM_CONTAINER= NAME_PREFIX + "prmcnt_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_IMPLEMENTS_CONTAINER= NAME_PREFIX + "impl_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_PARAM_ELEMENT= NAME_PREFIX + "parmnd_obj.gif"; 				//$NON-NLS-1$	
	public static final String IMG_OBJS_DATADECL_CONTAINER= NAME_PREFIX + "dtacnt_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_USEDECL_CONTAINER= NAME_PREFIX + "usecnt_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_RETURN_TYPE_ELEMENT= NAME_PREFIX + "rtntyp_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_TRANSFER_TRANSACTION_ELEMENT= NAME_PREFIX + "trnsfr_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_CONSOLE_FORM_ELEMENT= NAME_PREFIX + "cnslfm_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_HIDEDATA= NAME_PREFIX + "nodtls_tsk.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_REFRESH= NAME_PREFIX + "refrsh_tsk.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_LINK_EDITOR= NAME_PREFIX + "synced.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_PREV= NAME_PREFIX + "backward_nav.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_NEXT= NAME_PREFIX + "forward_nav.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_HISTORY= NAME_PREFIX + "prevos_tsk.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_FLAT= NAME_PREFIX + "flatlay_tsk.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSREF_TOOL_HIER= NAME_PREFIX + "hierlay_tsk.gif"; 				//$NON-NLS-1$
	public static final String IMG_PARTSLIST_TOOL_FILTER= NAME_PREFIX + "filter_tsk.gif"; 				//$NON-NLS-1$
	public static final String IMG_TOOL_HORIZONTAL = NAME_PREFIX + "horiz_mode.gif"; 				//$NON-NLS-1$
	public static final String IMG_TOOL_VERTICAL = NAME_PREFIX + "verti_mode.gif"; 				//$NON-NLS-1$

	public static final String IMG_OBJS_TEMPLATE= NAME_PREFIX + "template_obj.gif"; 		//$NON-NLS-1$
	public static final String IMG_OBJS_ANNOTATION= NAME_PREFIX + "annotation_obj.gif";		//$NON-NLS-1$

	public static final String IMG_OBJS_ENV_VAR= NAME_PREFIX + "envvar_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_ENV_LOCAL_VAR = NAME_PREFIX + "localvariable_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_ENV_VAR_PRIVATE= NAME_PREFIX + "private_co.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_JAR= NAME_PREFIX + "jar_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_FLDR= NAME_PREFIX + "fldr_obj.gif"; 			//$NON-NLS-1$
	public static final String IMG_OBJS_PRJ= NAME_PREFIX + "prj_obj.gif"; 				//$NON-NLS-1$
	public static final String IMG_OBJS_DEFAULT_COMP = NAME_PREFIX + "dftcpt_tbl.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_COMP = NAME_PREFIX + "compnt_tbl.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_COMP_PROP = NAME_PREFIX + "svcpty_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_ENTRYPOINT = NAME_PREFIX + "ntrypt_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_EXTERNALSERVICE = NAME_PREFIX + "extsvc_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_MODULE = NAME_PREFIX + "eglmdlx_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_EGLDD = NAME_PREFIX + "dds_obj.gif";//$NON-NLS-1$
	public static final String IMG_OBJS_COPYEGLDD = NAME_PREFIX + "cpydds_obj.gif";//$NON-NLS-1$
	public static final String IMG_SOURCE_ATTACHMENT = NAME_PREFIX + "source_attach_attrib.gif";
	/*
	 * Prefixes
	 */
//	private static final String T_ROOT= ""; 			//$NON-NLS-1$
	private static final String T_OBJ= "obj16"; 		//$NON-NLS-1$
	private static final String T_OVR= "ovr16"; 		//$NON-NLS-1$
	private static final String T_WIZBAN= "wizban"; 	//$NON-NLS-1$
	private static final String T_CTOOL= "ctool16"; 	//$NON-NLS-1$
	private static final String T_CLCL= "clcl16";		//$NON-NLS-1$
	private static final String T_DLCL= "dlcl16";		//$NON-NLS-1$
	private static final String T_VIEW= "cview16";		//$NON-NLS-1$
	private static final String T_ELCL= "elcl16";		//$NON-NLS-1$
	
	/*
	 * Model Object icons
	 */
	public static final ImageDescriptor DESC_OBJS_EGL_BINARY_OBJECTS = createManaged(T_OBJ, IMG_OBJS_EGL_BINARY_OBJECT);
	public static final ImageDescriptor DESC_OBJS_EGL_BINARY_PROJECT_OPEN= createManaged(T_OBJ, IMG_OBJS_EGL_BINARY_PROJECT_OPEN);
	public static final ImageDescriptor DESC_OBJS_EGL_BINARY_PROJECT_CLOSE= createManaged(T_OBJ, IMG_OBJS_EGL_BINARY_PROJECT_CLOSE);
	public static final ImageDescriptor DESC_OBJS_EGL_MODEL= createManaged(T_OBJ, IMG_OBJS_EGL_MODEL);
	
	public static final ImageDescriptor DESC_OBJS_PACKFRAG_ROOT= createManaged(T_OBJ, IMG_OBJS_PACKFRAG_ROOT);
	public static final ImageDescriptor DESC_OBJS_PACKFRAG_ROOT_EGLAR= createManaged(T_OBJ, IMG_OBJS_PACKFRAG_ROOT_EGLAR);
	public static final ImageDescriptor DESC_OBJS_PACKFRAG_ROOT_EGLAR_CONTAINER= createManaged(T_OBJ, IMG_OBJS_PACKFRAG_ROOT_EGLAR_CONTAINER);
	public static final ImageDescriptor DESC_OBJS_PACKAGE= createManaged(T_OBJ, IMG_OBJS_PACKAGE);
	public static final ImageDescriptor DESC_OBJS_EMPTY_PACKAGE= createManaged(T_OBJ, IMG_OBJS_EMPTY_PACKAGE);		
	
	public static final ImageDescriptor DESC_OBJS_EGLFILE= createManaged(T_OBJ, IMG_OBJS_EGLFILE);

	public static final ImageDescriptor DESC_OBJS_EGLBLD= createManaged(T_OBJ, IMG_OBJS_EGLBLD);
	public static final ImageDescriptor DESC_OBJS_BLDDESC= createManaged(T_OBJ, IMG_OBJS_BLDDESC);
	public static final ImageDescriptor DESC_OBJS_PGM= createManaged(T_OBJ, IMG_OBJS_PGM);
	public static final ImageDescriptor DESC_OBJS_LIBRARY = createManaged(T_OBJ, IMG_OBJS_LIBRARY);	
	public static final ImageDescriptor DESC_OBJS_FORMGRP= createManaged(T_OBJ, IMG_OBJS_FORMGRP);
	public static final ImageDescriptor DESC_OBJS_TABLE= createManaged(T_OBJ, IMG_OBJS_TABLE);
	public static final ImageDescriptor DESC_OBJS_UIRECORD= createManaged(T_OBJ, IMG_OBJS_UIRECORD);
	public static final ImageDescriptor DESC_OBJS_PAGE= createManaged(T_OBJ, IMG_OBJS_PAGE);
	public static final ImageDescriptor DESC_OBJS_PAGEDATA= createManaged(T_OBJ, IMG_OBJS_PAGEDATA);
	public static final ImageDescriptor DESC_OBJS_HANDLER = createManaged(T_OBJ, IMG_OBJS_HANDLER);
	public static final ImageDescriptor DESC_OBJS_INTERFACE = createManaged(T_OBJ, IMG_OBJS_INTERFACE);
	public static final ImageDescriptor DESC_OBJS_SERVICE = createManaged(T_OBJ, IMG_OBJS_SERVICE);
	public static final ImageDescriptor DESC_OBJS_SERVICEBINDING = createManaged(T_OBJ, IMG_OBJS_SERVICEBINDING);	
	public static final ImageDescriptor DESC_OBJS_WSDL = createManaged(T_OBJ, IMG_OBJS_WSDL);	
	public static final ImageDescriptor DESC_OBJS_RECORD= createManaged(T_OBJ, IMG_OBJS_RECORD);
	public static final ImageDescriptor DESC_OBJS_FUNCTION= createManaged(T_OBJ, IMG_OBJS_FUNCTION);
	public static final ImageDescriptor DESC_OBJS_PRIVATE_FUNCTION= createManaged(T_OBJ, IMG_OBJS_PRIVATE_FUNCTION);
	public static final ImageDescriptor DESC_OBJS_DELEGATE= createManaged(T_OBJ, IMG_OBJS_DELEGATE);
	public static final ImageDescriptor DESC_OBJS_EXTERNALTYPE= createManaged(T_OBJ, IMG_OBJS_EXTERNALTYPE);
	public static final ImageDescriptor DESC_OBJS_ENUMERATION= createManaged(T_OBJ, IMG_OBJS_ENUMERATION);
	public static final ImageDescriptor DESC_OBJS_FORM= createManaged(T_OBJ, IMG_OBJS_FORM);
	public static final ImageDescriptor DESC_OBJS_DATAITEM = createManaged(T_OBJ, IMG_OBJS_DATAITEM);
	public static final ImageDescriptor DESC_OBJS_STRUCTUREITEM = createManaged(T_OBJ, IMG_OBJS_STRUCTUREITEM);
	public static final ImageDescriptor DESC_OBJS_CLASS= createManaged(T_OBJ, IMG_OBJS_CLASS);

	public static final ImageDescriptor DESC_OBJS_IMPORT = createManaged(T_OBJ, IMG_OBJS_IMPORT);
	public static final ImageDescriptor DESC_OBJS_IMPORTS = createManaged(T_OBJ, IMG_OBJS_IMPORTS);
	public static final ImageDescriptor DESC_OBJS_USEFORM = createManaged(T_OBJ, IMG_OBJS_USEFORM);
	public static final ImageDescriptor DESC_OBJS_USESTATEMENT = createManaged(T_OBJ, IMG_OBJS_USESTATEMENT);

	public static final ImageDescriptor DESC_OBJS_CONSTANTDECL = createManaged(T_OBJ, IMG_OBJS_CONSTANTDECL);
	public static final ImageDescriptor DESC_OBJS_CONSTANTFORMFIELD = createManaged(T_OBJ, IMG_OBJS_CONSTANTFORMFIELD);
	public static final ImageDescriptor DESC_OBJS_VARIABLEDECL = createManaged(T_OBJ, IMG_OBJS_VARIABLEDECL);	
	public static final ImageDescriptor DESC_OBJS_VARIABLEFORMFIELD = createManaged(T_OBJ, IMG_OBJS_VARIABLEFORMFIELD);
	
	public static final ImageDescriptor DESC_OBJS_GEN_SUCCESS = createManaged(T_OBJ, IMG_OBJS_GEN_SUCCESS);
	public static final ImageDescriptor DESC_OBJS_GEN_FAIL = createManaged(T_OBJ, IMG_OBJS_GEN_FAIL);
	public static final ImageDescriptor DESC_OBJS_GEN_RUN = createManaged(T_OBJ, IMG_OBJS_GEN_RUN);
	public static final ImageDescriptor DESC_OBJS_GEN_WARNING = createManaged(T_OBJ, IMG_OBJS_GEN_WARNING);

	
	public static final ImageDescriptor DESC_OBJS_SEARCH_DECL= createManaged(T_OBJ, IMG_OBJS_SEARCH_DECL);
	public static final ImageDescriptor DESC_OBJS_SEARCH_REF= createManaged(T_OBJ, IMG_OBJS_SEARCH_REF);	
	public static final ImageDescriptor DESC_OBJS_CUNIT_RESOURCE= createManaged(T_OBJ, IMG_OBJS_CUNIT_RESOURCE);
	public static final ImageDescriptor DESC_OBJS_CFILE= createManaged(T_OBJ, IMG_OBJS_CFILE);
	public static final ImageDescriptor DESC_OBJS_EGLBUILDPATH_ORDER= createManaged(T_OBJ, IMG_OBJS_EGL_BUILDPATH_ORDER);

	public static final ImageDescriptor DESC_OBJS_ERROR= createManaged(T_OBJ, IMG_OBJS_ERROR);
	public static final ImageDescriptor DESC_OBJS_WARNING= createManaged(T_OBJ, IMG_OBJS_WARNING);
	public static final ImageDescriptor DESC_OBJS_UNKNOWN= createManaged(T_OBJ, IMG_OBJS_UNKNOWN);
	public static final ImageDescriptor DESC_OBJS_GHOST= createManaged(T_OBJ, IMG_OBJS_GHOST);
	
	public static final ImageDescriptor DESC_OBJS_PARAM_CONTAINER= createManaged(T_OBJ, IMG_OBJS_PARAM_CONTAINER);
	public static final ImageDescriptor DESC_OBJS_IMPLEMENTS_CONTAINER= createManaged(T_OBJ, IMG_OBJS_IMPLEMENTS_CONTAINER);
	public static final ImageDescriptor DESC_OBJS_PARAM_ELEMENT= createManaged(T_OBJ, IMG_OBJS_PARAM_ELEMENT);
	public static final ImageDescriptor DESC_OBJS_DATADECL_CONTAINER= createManaged(T_OBJ, IMG_OBJS_DATADECL_CONTAINER);
	public static final ImageDescriptor DESC_OBJS_USEDECL_CONTAINER= createManaged(T_OBJ, IMG_OBJS_USEDECL_CONTAINER);
	public static final ImageDescriptor DESC_OBJS_RETURN_TYPE_ELEMENT= createManaged(T_OBJ, IMG_OBJS_RETURN_TYPE_ELEMENT);
	public static final ImageDescriptor DESC_OBJS_TRANSFER_TRANSACTION_ELEMENT= createManaged(T_OBJ, IMG_OBJS_TRANSFER_TRANSACTION_ELEMENT);
	public static final ImageDescriptor DESC_OBJS_CONSOLE_FORM_ELEMENT= createManaged(T_OBJ, IMG_OBJS_CONSOLE_FORM_ELEMENT);
	public static final ImageDescriptor DESC_OBJS_OBJS_ENV_VAR= createManaged(T_OBJ, IMG_OBJS_ENV_VAR);
	public static final ImageDescriptor DESC_OBJS_OBJS_ENV_LOCAL_VAR = createManaged(T_OBJ, IMG_OBJS_ENV_LOCAL_VAR);
	public static final ImageDescriptor DESC_OBJS_OBJS_ENV_VAR_PRIVATE= createManaged(T_OBJ, IMG_OBJS_ENV_VAR_PRIVATE);
	public static final ImageDescriptor DESC_OBJS_OBJS_JAR= createManaged(T_OBJ, IMG_OBJS_JAR);
	public static final ImageDescriptor DESC_OBJS_OBJS_FLDR= createManaged(T_OBJ, IMG_OBJS_FLDR);
	public static final ImageDescriptor DESC_OBJS_OBJS_PRJ= createManaged(T_OBJ, IMG_OBJS_PRJ);
	public static final ImageDescriptor DESC_OBJS_DEFAULT_COMP= createManaged(T_OBJ, IMG_OBJS_DEFAULT_COMP);
	public static final ImageDescriptor DESC_OBJS_COMP = createManaged(T_OBJ, IMG_OBJS_COMP);
	public static final ImageDescriptor DESC_OBJS_COMP_PROP= createManaged(T_OBJ, IMG_OBJS_COMP_PROP);
	public static final ImageDescriptor DESC_OBJS_ENTRYPOINT= createManaged(T_OBJ, IMG_OBJS_ENTRYPOINT);
	public static final ImageDescriptor DESC_OBJS_EXTERNALSERVICE = createManaged(T_OBJ, IMG_OBJS_EXTERNALSERVICE);	
	public static final ImageDescriptor DESC_OBJS_MODULE = createManaged(T_OBJ, IMG_OBJS_MODULE);
	public static final ImageDescriptor DESC_OBJS_EGLDD = createManaged(T_OBJ, IMG_OBJS_EGLDD);
	public static final ImageDescriptor DESC_OBJS_COPYEGLDD = createManaged(T_OBJ, IMG_OBJS_COPYEGLDD);
	public static final ImageDescriptor DESC_OBJS_SOURCE_ATTACHMENT = createManaged(T_OBJ, IMG_SOURCE_ATTACHMENT);
	public static final ImageDescriptor DESC_OBJS_TEMPLATE = createManaged(T_OBJ, IMG_OBJS_TEMPLATE);
	public static final ImageDescriptor DESC_OBJS_ANNOTATION = createManaged(T_OBJ, IMG_OBJS_ANNOTATION);
	
	
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_HIDEDATA= createManaged(T_ELCL, IMG_PARTSREF_TOOL_HIDEDATA);
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_REFRESH= createManaged(T_ELCL, IMG_PARTSREF_TOOL_REFRESH);
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_LINK_EDITOR= createManaged(T_ELCL, IMG_PARTSREF_TOOL_LINK_EDITOR);
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_PREV= createManaged(T_ELCL, IMG_PARTSREF_TOOL_PREV);
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_NEXT= createManaged(T_ELCL, IMG_PARTSREF_TOOL_NEXT);
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_HISTORY= createManaged(T_ELCL, IMG_PARTSREF_TOOL_HISTORY);
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_FLAT= createManaged(T_ELCL, IMG_PARTSREF_TOOL_FLAT);
	public static final ImageDescriptor DESC_ELCL_PARTSREF_TOOL_HIER= createManaged(T_ELCL, IMG_PARTSREF_TOOL_HIER);
	public static final ImageDescriptor DESC_ELCL_PARTSLIST_TOOL_FILTER= createManaged(T_ELCL, IMG_PARTSLIST_TOOL_FILTER);
	public static final ImageDescriptor DESC_ELCL_TOOL_HORIZONTAL = createManaged(T_ELCL, IMG_TOOL_HORIZONTAL);
	public static final ImageDescriptor DESC_ELCL_TOOL_VERTICAL = createManaged(T_ELCL, IMG_TOOL_VERTICAL);
	
	
	/*
	 * Overlays
	 */
	public static final ImageDescriptor DESC_OVR_EGL= create(T_OVR, "egl_ovr.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_EGLWEB= create(T_OVR, "eglweb_ovr.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_WARNING= create(T_OVR, "warning_co.gif"); 					//$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_ERROR= create(T_OVR, "error_co.gif"); 						//$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_AMBIG= create(T_OVR, "ambgos_ovr.gif"); 						//$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_REDEFINED= create(T_OVR, "rdefnd_ovr.gif"); 						//$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_VALIDATOR= create(T_OVR, "valdtr_ovr.gif"); 						//$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_WEBSERVICE = create(T_OVR, "websvc_tsk.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_TCPIP = create(T_OVR, "tcpip_tsk.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_CICS = create(T_OVR, "cics_tsk.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_OVR_SERVICEREF = create(T_OVR, "svcref_tsk.gif"); //$NON-NLS-1$
	
	/*
	 * Wizard Banner icons
	 */
	public static final ImageDescriptor DESC_WIZBAN_NEWEGLPROJECT= create(T_WIZBAN, "newegl_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWEGLWEBPROJECT= create(T_WIZBAN, "neweglweb_wiz.gif");  //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWSRCFOLDR= create(T_WIZBAN, "newsrc_wiz.gif"); 	//$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWSRCFILE= create(T_WIZBAN, "newsrcfile_wiz.gif");   //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWPACK= create(T_WIZBAN, "newpkg_wiz.gif"); 			//$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWPROGRAM= create(T_WIZBAN, "newpgm_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWRUIHANDLER= create(T_WIZBAN, "newruihandler_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWRUIWIDGET= create(T_WIZBAN, "newruiwidget_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWLIBRARY= create(T_WIZBAN, "newlib_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWHANDLER= create(T_WIZBAN, "newhandler_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWFORMGROUP= create(T_WIZBAN, "newfrm_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWDATATABLE= create(T_WIZBAN, "newdta_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWUIRECORD= create(T_WIZBAN, "newuir_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_EGLGENERATION= create(T_WIZBAN, "eglgen_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWEGLPART= create(T_WIZBAN, "newpartegl_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWEGLINTERFACE= create(T_WIZBAN, "intb_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWEGLSERVICE= create(T_WIZBAN, "svcb_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_WSDL2EGL= create(T_WIZBAN, "wsdl_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_ENTRYPOINT = create(T_WIZBAN, "ntrypt_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_EXTERNALSERVICE = create(T_WIZBAN, "extsvc_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_GENWSDL = create(T_WIZBAN, "wsdlfl_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWEGLDD = create(T_WIZBAN, "newedd_wiz.gif");//$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_COPYEGLDD = create(T_WIZBAN, "cpydds_wiz.gif");//$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWTEMPLATESELECTION = create(T_WIZBAN, "newxsltemplate_wiz.gif");//$NON-NLS-1$
	
	//Source Assistant banners
	public static final ImageDescriptor DESC_WIZBAN_SA_DATAITEM= create(T_WIZBAN, "disa_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_SA_BASICRECORDPROPERTIES= create(T_WIZBAN, "sa_basicrecordprops_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_SA_INDEXEDRECORDPROPERTIES= create(T_WIZBAN, "sa_indexedrecordprops_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_SA_MQRECORDPROPERTIES= create(T_WIZBAN, "sa_mqrecordprops_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_SA_RELATIVERECORDPROPERTIES= create(T_WIZBAN, "sa_relativerecordprops_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_SA_SERIALRECORDPROPERTIES= create(T_WIZBAN, "sa_serialrecordprops_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_SA_SQLRECORDPROPERTIES= create(T_WIZBAN, "sa_sqlrecordprops_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_SA_JSFCOMPONENTTREE= create(T_WIZBAN, "jsfs_wiz.gif"); //$NON-NLS-1$
	
	//source assistant tab icon
	public static final ImageDescriptor DESC_VIEW_SA_DATAITEM_FORMATTING = createManaged(T_VIEW, NAME_PREFIX+"frmt_nav.gif"); //formatting //$NON-NLS-1$
	public static final ImageDescriptor DESC_VIEW_SA_DATAITEM_PAGEHANDLER = createManaged(T_VIEW, NAME_PREFIX+"pgit_nav.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_VIEW_SA_DATATITEM_ANNOTATION = createManaged(T_VIEW, NAME_PREFIX+"anno_nav.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_VIEW_SA_DATAITEM_FIELDPRESENTATION = createManaged(T_VIEW, NAME_PREFIX+"fdpr_nav.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_VIEW_SA_DATAITEM_SQLRECORD = createManaged(T_VIEW, NAME_PREFIX+"sqli_nav.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_VIEW_SA_DATAITEM_VALIDATION = createManaged(T_VIEW, NAME_PREFIX+"vldt_nav.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_VIEW_SA_DATAITEM_VAIRABLEFIELD = createManaged(T_VIEW, NAME_PREFIX+"vrfd_nav.gif"); //$NON-NLS-1$

	/*
	 * Toolbar Icons
	 */
	public static final ImageDescriptor DESC_TOOL_OPENPART= create(T_CTOOL, "opnprt_nav.gif"); 					//$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_EGLSEARCH= create(T_CTOOL, "esrch_nav.gif"); 					//$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_SHOWSELECTED= create(T_CTOOL, "shwsel_nav.gif"); //$NON-NLS-1$
	
	public static final ImageDescriptor DESC_TOOL_EGLPROJECT= create(T_CTOOL, "newegl_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_EGLWEBPROJECT= create(T_CTOOL, "newweb_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWPACKROOT= create(T_CTOOL, "newsrc_wiz.gif"); 		//$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWPACKAGE= create(T_CTOOL, "newpkg_wiz.gif"); 			//$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWEGLFILE= create(T_CTOOL, "newfle_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWPROGRAM= create(T_CTOOL, "newpgm_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWLIBRARY= create(T_CTOOL, "newlib_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWHANDLER= create(T_CTOOL, "newhandler_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWFORMGROUP= create(T_CTOOL, "newfrm_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWDATATABLE= create(T_CTOOL, "newdta_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWUIRECORD= create(T_CTOOL, "newuir_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_NEWFACESJSP= create(T_CTOOL, "newjsp_wiz.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_TOOL_HELP= create(T_CTOOL, "help.gif"); //$NON-NLS-1$
	
	/*
	 * Task Icons
	 */
	public static final ImageDescriptor DESC_CLCL_HIDEDETAILS= create(T_CLCL, "nodtls_tsk.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_DLCL_HIDEDETAILS= create(T_DLCL, "nodtls_tsk.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_CLCL_SHOWPUBLIC= create(T_CLCL, "public_tsk.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_DLCL_SHOWPUBLIC= create(T_DLCL, "public_tsk.gif"); //$NON-NLS-1$
	
	/*
	 * Perspective Icons
	 */
	public static final ImageDescriptor DESC_VIEW_EGLPERSPECTIVE= create(T_VIEW, "eglperspective.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_VIEW_EGLWEBPERSPECTIVE= create(T_VIEW, "eglwebperspective.gif"); //$NON-NLS-1$
	
	/*
	 * View Icons
	 */
	 public static final ImageDescriptor DESC_VIEW_SQLERROR= create(T_VIEW, IMG_OBJS_SQL_CHECK);
	 public static final ImageDescriptor DESC_VIEW_EGLGEN_RESULTS= create(T_VIEW, IMG_OBJS_EGL_GENERATION);
	 public static final ImageDescriptor DESC_VIEW_EGLVALIDATION_RESULTS= create(T_VIEW, IMG_OBJS_EGL_VALIDATE);
	 public static final ImageDescriptor DESC_VIEW_PARTS_LIST= create(T_VIEW, IMG_OBJS_PARTS_LIST);

	/**
	 * Returns the image managed under the given key in this registry.
	 * 
	 * @param key the image's key
	 * @return the image managed under the given key
	 */ 
	public static Image get(String key) {
		return getImageRegistry().get(key);
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an action. The actions
	 * are retrieved from the *tool16 folders.
	 */
	public static void setToolImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "tool16", iconName); //$NON-NLS-1$
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an action. The actions
	 * are retrieved from the *lcl16 folders.
	 */
	public static void setLocalImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
	}

	/*
	 * Helper method to access the image registry from the org.eclipse.edt.ide.ui Plugin class.
	 */
	/* package */ static ImageRegistry getImageRegistry() {
		if (fgImageRegistry == null) {
			fgImageRegistry= new ImageRegistry();
			for (Iterator iter= fgAvoidSWTErrorMap.keySet().iterator(); iter.hasNext();) {
				String key= (String) iter.next();
				fgImageRegistry.put(key, (ImageDescriptor) fgAvoidSWTErrorMap.get(key));
			}
			fgAvoidSWTErrorMap= null;
		}
		return fgImageRegistry;
	}

	//---- Helper methods to access icons on the file system --------------------------------------

	private static void setImageDescriptors(IAction action, String type, String relPath) {
	
		try {
			ImageDescriptor id= ImageDescriptor.createFromURL(makeIconFileURL("d" + type, relPath)); //$NON-NLS-1$
			if (id != null)
				action.setDisabledImageDescriptor(id);
		} catch (MalformedURLException e) {
		}

		try {
			ImageDescriptor id= ImageDescriptor.createFromURL(makeIconFileURL("c" + type, relPath)); //$NON-NLS-1$
			if (id != null)
				action.setHoverImageDescriptor(id);
		} catch (MalformedURLException e) {
		}

		action.setImageDescriptor(create("e" + type, relPath)); //$NON-NLS-1$
	}

	private static ImageDescriptor createManaged(String prefix, String name) {
		try {
			ImageDescriptor result= ImageDescriptor.createFromURL(makeIconFileURL(prefix, name.substring(NAME_PREFIX_LENGTH)));
			if (fgAvoidSWTErrorMap == null) {
				fgAvoidSWTErrorMap= new HashMap();
			}
			fgAvoidSWTErrorMap.put(name, result);
			if (fgImageRegistry != null) {
				EDTUIPlugin.logErrorMessage("Image registry already defined"); //$NON-NLS-1$
			}
			return result;
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (fgIconBaseURL == null)
			throw new MalformedURLException();
		
		StringBuffer buffer= new StringBuffer(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(fgIconBaseURL, buffer.toString());
	}
}

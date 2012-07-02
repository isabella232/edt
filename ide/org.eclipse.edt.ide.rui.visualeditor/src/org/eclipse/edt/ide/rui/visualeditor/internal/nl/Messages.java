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
package org.eclipse.edt.ide.rui.visualeditor.internal.nl;

import org.eclipse.osgi.util.NLS;

public class Messages {
	private static final String	BUNDLE_NAME	= "org.eclipse.edt.ide.rui.visualeditor.internal.nl.messages";	//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages( BUNDLE_NAME, Messages.class );
	}
	
	// Editor
	//-------
	public static String	NL_Design;
	public static String	NL_Preview;
	public static String	NL_Source;
	public static String	NL_IE_Out_of_resources_message;
	public static String	NL_XULRunner_Out_of_resources_message;
	public static String	NL_WEBKIT_Out_of_resources_message;
	
	// Overlay
	//--------
	public static String	NL_Drag_an_item_from_the_palette_and_drop_it_here;
	public static String	NL_Move;
	
	// New event handling function dialog
	//-----------------------------------
	public static String	NL_EGL_Rich_UI_Editor;
	
	// Properties view
	//----------------
	public static String	NL_Event;
	public static String	NL_Events;
	public static String	NL_Function;
	public static String	NL_NEW;
	public static String	NL_none;
	public static String	NL_Position;
	public static String	NL_Properties;
	public static String	NL_Menu_Filter;
	public static String	NL_Menu_Filter_show_all;
	public static String	NL_Menu_Filter_hide_excluded;
	public static String	NL_Properties_View_Widget_Label;
	
	// Color selection dialog
	//-----------------------
	public static String	NL_Color;
	public static String	NL_Color_name;
	public static String	NL_Color_selection;
	public static String	NL_Choose_a_color_and_a_format;
	public static String	NL_Custom;
	public static String	NL_Format;
	public static String	NL_Hexadecimal;
	public static String	NL_Name_format;
	public static String	NL_Number_format;
	public static String	NL_RGB;
	public static String	NL_ValueXcolonX;
	
	// String list dialog
	//-------------------
	public static String 	NL_Add_or_remove_strings_from_the_list;
	public static String	NL_String_array;
	public static String	NL_Add;
	public static String	NL_Move_down;
	public static String	NL_Move_up;
	public static String	NL_Remove;
	
	// Preferences
	//------------
	public static String	NL_Browser_size;
	public static String	NL_Colors;
	public static String	NL_Configure_the_design_area_browser_size_controls;
	public static String	NL_Default;
	public static String	NL_Dotted_transparency_pattern;
	public static String	NL_Editor_tab;
	public static String	NL_Enable_semiXhyphenXtransparency_while_dragging;
	public static String	NL_Fully_transparent;
	public static String	NL_General;
	public static String	NL_Horizontal;
	public static String	NL_Maximum;
	public static String	NL_Minimum;
	public static String	NL_Languages;
	public static String	NL_Optimize_for_better_responsiveness;
	public static String	NL_Optimize_to_use_fewer_resources;
	public static String	NL_Performance;
	public static String	NL_Potential_drop_locationXcolonX;
	public static String	NL_Prompt_before_adding_project_dependencies;
	public static String	NL_Prompt_for_a_variable_name;
	public static String	NL_Rich_UI_handler_locale;
	public static String 	NL_Runtime_messages_locale;
	public static String	NL_Select_which_tab_is_shown_when_the_editor_is_opened;
	public static String	NL_Selected_drop_locationXcolonX;
	public static String	NL_Selection_borderXcolonX;
	public static String	NL_Show_the_browser_size_controls_when_the_editor_is_opened;
	public static String	NL_Show_transparency_controls;
	public static String	NL_Superimpose_a_pattern;
	public static String	NL_These_settings_are_applied_when_the_editor_is_opened;
	public static String	NL_This_setting_is_applied_when_the_editor_is_opened;
	public static String	NL_Tranparency;
	public static String	NL_Variable_transparency;
	public static String	NL_Vertical;
	public static String	NL_Widget_creation;
	public static String	NL_VisualEditor_RenderEngion;
	public static String	NL_VisualEditor_RenderEngion_USER_CONFIGURED;
	public static String	NL_VisualEditor_RenderEngion_WEBKIT;
	public static String	NL_VisualEditor_RenderEngion_XULRUNNER;
	public static String	NL_VisualEditor_RenderEngion_IE;
	
	// Function name dialog
	//---------------------
	public static String	NL_A_function_with_the_same_name_already_exists;
	public static String	NL_Function_nameXcolonX;
	public static String	NL_New_Event_Handler;
	
	// Widget name dialog
	//-------------------
	public static String	NL_New_Variable;
	public static String	NL_The_name_already_exists;
	public static String	NL_The_name_is_not_allowed;
	public static String	NL_Variable_nameXcolonX;
	
	// Dialog Strings
	//--------------------------------
	public static String	NL_You_may_also_change_this_setting_in_the_preferences;
	
	// Project dependency warning dialog
	//----------------------------------
	public static String	NL_The_project_containing_the_widget_type_cannot_be_added_as_a_project_dependency;
	
	// Error messages
	//---------------
	public static String	NL_An_unknown_error_has_occurred;
	public static String	NL_A_package_cannot_be_found_in_the_project;
	
	// Bidirectional properties
	//-------------------------
	public static String NL_Bidirectional_options;
	public static String NL_Bidirectional_Properties;
	
	public static String NL_BIDI_Bidirectional;
	public static String NL_BIDI_Contextual;
	public static String NL_BIDI_Logical;
	public static String NL_BIDI_LTR;
	public static String NL_BIDI_National;
	public static String NL_BIDI_No;
	public static String NL_BIDI_Nominal;
	public static String NL_BIDI_Numeric_SwappingXcolonX;
	public static String NL_BIDI_Page_Description;
	public static String NL_BIDI_Reverse_Text_DirectionXcolonX;
	public static String NL_BIDI_RTL;
	public static String NL_BIDI_Symmetric_SwappingXcolonX;
	public static String NL_BIDI_Text_LayoutXcolonX;
	public static String NL_BIDI_Visual;
	public static String NL_BIDI_Widget_OrientationXcolonX;
	public static String NL_BIDI_Yes;
	public static String NL_BIDI_Panel_Instructions;
	
	// Widget group categories and property categories
	//------------------------------------------------
	public static String NL_EGL_Widgets;
	public static String NL_Accessibility;
	public static String NL_Appearance;
	public static String NL_Spacing;
	public static String NL_Border;
	public static String NL_Bidi;
	public static String NL_Layout;
	
	public static String NL_WidgetRegistryUpdateJob_Name;
	
	// Page Data View
	//------------------------------------------------
	public static String NL_PDV_No_Page_Data_To_View;
	public static String NL_PDV_Context_Menu_New_Sub_Menu;
	public static String NL_PDV_Context_Menu_New_EGL_Variable_Action;
	
	// Insert Widget Wizard
	//------------------------------------------------
	public static String NL_IWW_Title;
	public static String NL_IWWP_Title;
	public static String NL_IWWP_Description;
	public static String NL_IWWP_Create_Widgets_Label;
	public static String NL_IWWP_Display_Widgets_Button;
	public static String NL_IWWP_Create_Widgets_Button;
	public static String NL_IWWP_Update_Widgets_Button;
	public static String NL_IWWP_Configure_Widgets_Label;
	public static String NL_IWWP_CWT_Field_Name_Column;
	public static String NL_IWWP_CWT_Field_Type_Column;
	public static String NL_IWWP_CWT_Label_Text_Column;
	public static String NL_IWWP_CWT_Widget_Type_Column;
	public static String NL_IWWP_CWT_Widget_Name_Column;
	public static String NL_IWWP_Select_All_Button;
	public static String NL_IWWP_Deselect_All_Button;
	public static String NL_IWWP_Is_Add_Formatting_And_Validation_Button;
	public static String NL_IWWP_Is_Add_Error_Message_Button;
	public static String NL_IWWP_Gen_Task_Compose_Generation_Model;
	public static String NL_IWWP_Gen_Task_Write_Code;
	public static String NL_IWWP_Error_Message_Can_Not_Find_Widget;
	public static String NL_IWWP_Error_Message_Duplicate_Widget_Name_Found;
	public static String NL_IWWP_Error_Message__Widget_Name_Is_Not_Valid;
	public static String NL_IWWP_Error_Message_No_Element_Selected;
	public static String NL_IWWP_Error_Message_Parent_Element_Is_Not_Selected;
	
	// New EGL Variable Wizard
	//------------------------------------------------
	public static String NL_NEVW_Title;
	public static String NL_NEVWP_Title;
	public static String NL_NEVWP_Description;
	public static String NL_NEVWP_Type_Selection_Group;
	public static String NL_NEVWP_Type_Creation_Group;
	public static String NL_NEVWP_Array_Properties_Group;
	public static String NL_NEVWP_Preview_Group;
	public static String NL_NEVWP_Primitive_Type_Button;
	public static String NL_NEVWP_DataItem_Button;
	public static String NL_NEVWP_Record_Button;
	public static String NL_NEVWP_Is_Array_Button;
	public static String NL_NEVWP_Array_Size_Label;
	public static String NL_NEVWP_Array_Size_Intro_Label;
	public static String NL_NEVWP_Is_Insert_Widget_Intro_Label;
	public static String NL_NEVWP_Is_Insert_Widget_Button;
	public static String NL_NEVWP_Primitive_Type_Detail_Group;
	public static String NL_NEVWP_Primitive_Type_Label;
	public static String NL_NEVWP_Primitive_Dimensions_Label;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Bigint;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Bin;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Boolean;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Char;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Date;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Decimal;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Float;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Int;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Money;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Num;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Smallfloat;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Smallint;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_String;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Limited_String;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Time;
	public static String NL_NEVWP_Primitive_Type_Detail_Message_Timestamp;
	public static String NL_NEVWP_Search_DataItem_Group;
	public static String NL_NEVWP_Search_Record_Group;
	public static String NL_NEVWP_Field_Name_Label;
	public static String NL_NEVWP_Error_Message_Field_Name_Is_None;
	public static String NL_NEVWP_Error_Message_Field_Name_Is_Duplicate;
	public static String NL_NEVWP_Error_Message_Field_Name_Is_Not_Valid;
	public static String NL_NEVWP_Error_Message_Array_Size_Text_Input_Is_Not_Number;
	public static String NL_NEVWP_Error_Message_Array_Size_Text_Input_Is_Out_Of_Range;
	public static String NL_NEVWP_Error_Message_Array_Size_Is_Less_Than_Max_Size;
	public static String NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_Not_Valid;
	public static String NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_None;
	public static String NL_NEVWP_Error_Message_Primitive_Type_Dimensions_First_Is_Small_Then_Second;
	
	// GridLayout Widget Context Menu Actions
	//------------------------------------------------
	public static String NL_GLWCMA_Insert;
	public static String NL_GLWCMA_Insert_Row_Above;
	public static String NL_GLWCMA_Insert_Row_Below;
	public static String NL_GLWCMA_Insert_Column_To_The_Left;
	public static String NL_GLWCMA_Insert_Column_To_The_Right;
	public static String NL_GLWCMA_Delete;
	public static String NL_GLWCMA_Delete_Row;
	public static String NL_GLWCMA_Delete_Column;
	
	// User agent dialog
	//------------------------------------------------
	public static String NL_User_Agent_Title;
	public static String NL_User_Agent_Error_Retrieving;
}

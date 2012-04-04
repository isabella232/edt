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
package org.eclipse.edt.compiler.dev.tools;


/**
 * @author winghong
 */
public class EGLPropertyEnumerator {

//    // types expected for the property
//    public static final int nameValue = 0;
//    public static final int quotedValue = 1;
//    public static final int specificValue = 2;
//    public static final int integerValue = 3;
//    public static final int literalValue = 4;
//    public static final int listValue = 5;
//    public static final int literalArray = 6;
//    public static final int nestedValue = 7;
//    public static final int sqlValue = 8;
//    
//    
//    static String yesOrNo[] = new String [] {IEGLConstants.MNEMONIC_YES, IEGLConstants.MNEMONIC_NO };
//    static int specificValueValid[] = new int [] {specificValue};
//    static int nameValueValid[] = new  int [] {nameValue};
//    static int integerValueValid[] = new int [] {integerValue};
//    static int listValueValid[] = new int [] {listValue};
//    static int quotedValueValid[] = new int [] {quotedValue};
//
//    public static EGLPropertyRule action = new EGLPropertyRule(IEGLConstants.PROPERTY_ACTION, nameValueValid);
//    public static EGLPropertyRule addSpaceForSOSI = new EGLPropertyRule(IEGLConstants.PROPERTY_ADDSPACEFORSOSI, specificValueValid, yesOrNo );
//    public static EGLPropertyRule alias = new EGLPropertyRule(IEGLConstants.PROPERTY_ALIAS, new int [] {nameValue, quotedValue} );
//    public static EGLPropertyRule align = new EGLPropertyRule(IEGLConstants.PROPERTY_ALIGN, specificValueValid, new String [] { IEGLConstants.MNEMONIC_LEFT, IEGLConstants.MNEMONIC_RIGHT, IEGLConstants.MNEMONIC_NONE, IEGLConstants.MNEMONIC_CENTER }, true );
//    public static EGLPropertyRule bool = new EGLPropertyRule(IEGLConstants.PROPERTY_BOOLEAN, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule bottomMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_BOTTOMMARGIN, integerValueValid );
//    public static EGLPropertyRule bypassKeys = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS, new int [] {nameValue, listValue} );
//    public static EGLPropertyRule bypassValidation = new EGLPropertyRule(IEGLConstants.PROPERTY_BYPASSVALIDATION, specificValueValid, yesOrNo );
//    public static EGLPropertyRule color = new EGLPropertyRule(IEGLConstants.PROPERTY_COLOR, specificValueValid, new String [] { IEGLConstants.MNEMONIC_DEFAULTCOLOR, IEGLConstants.MNEMONIC_BLUE, IEGLConstants.MNEMONIC_GREEN, IEGLConstants.MNEMONIC_MAGENTA, IEGLConstants.MNEMONIC_RED, IEGLConstants.MNEMONIC_CYAN, IEGLConstants.MNEMONIC_YELLOW, IEGLConstants.MNEMONIC_WHITE, IEGLConstants.MNEMONIC_BLACK } );
//    public static EGLPropertyRule column = new EGLPropertyRule(IEGLConstants.PROPERTY_COLUMN, new int [] {nameValue, quotedValue});
//    public static EGLPropertyRule columns = new EGLPropertyRule(IEGLConstants.PROPERTY_COLUMNS, integerValueValid );
//    public static EGLPropertyRule commandValueItem = new EGLPropertyRule(IEGLConstants.PROPERTY_COMMANDVALUEITEM, nameValueValid);
//    public static EGLPropertyRule containerContextDependent = new EGLPropertyRule(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, specificValueValid, yesOrNo );
//    public static EGLPropertyRule contents = new EGLPropertyRule(IEGLConstants.PROPERTY_CONTENTS, new int [] {literalArray} );
//    public static EGLPropertyRule currency = new EGLPropertyRule(IEGLConstants.PROPERTY_CURRENCY, new int [] {specificValue, quotedValue, nameValue}, yesOrNo, true );
//    public static EGLPropertyRule cursor =  new EGLPropertyRule(IEGLConstants.PROPERTY_CURSOR, specificValueValid, yesOrNo );
//    public static EGLPropertyRule dateFormat = new EGLPropertyRule(IEGLConstants.PROPERTY_DATEFORMAT, new int [] {specificValue, quotedValue}, new String [] {IEGLConstants.MNEMONIC_ISO, IEGLConstants.MNEMONIC_USA, IEGLConstants.MNEMONIC_EUR, IEGLConstants.MNEMONIC_JIS, IEGLConstants.MNEMONIC_LOCALE, IEGLConstants.MNEMONIC_SYSTEMGREGORIAN, IEGLConstants.MNEMONIC_SYSTEMJULIAN }, true );
//    public static EGLPropertyRule defaultSelectCondition = new EGLPropertyRule(IEGLConstants.PROPERTY_DEFAULTSELECTCONDITION, new int [] {sqlValue} );
//    public static EGLPropertyRule deleteAfterUse = new EGLPropertyRule(IEGLConstants.PROPERTY_DELETEAFTERUSE, specificValueValid, yesOrNo );
//    public static EGLPropertyRule detectable =  new EGLPropertyRule(IEGLConstants.PROPERTY_DETECTABLE, specificValueValid, yesOrNo );
//    public static EGLPropertyRule deviceType = new EGLPropertyRule(IEGLConstants.PROPERTY_DEVICETYPE, new int[] {specificValue}, new String [] {IEGLConstants.MNEMONIC_SINGLEBYTE, IEGLConstants.MNEMONIC_DOUBLEBYTE } );
//    public static EGLPropertyRule displayName = new EGLPropertyRule(IEGLConstants.PROPERTY_DISPLAYNAME, quotedValueValid);
//    public static EGLPropertyRule displayUse =new EGLPropertyRule(IEGLConstants.PROPERTY_DISPLAYUSE, specificValueValid, new String [] { IEGLConstants.MNEMONIC_INPUT, IEGLConstants.MNEMONIC_OUTPUT, IEGLConstants.MNEMONIC_SECRET, IEGLConstants.MNEMONIC_BUTTON, IEGLConstants.MNEMONIC_HYPERLINK, IEGLConstants.MNEMONIC_TABLE } );
//    public static EGLPropertyRule fieldLen = new EGLPropertyRule(IEGLConstants.PROPERTY_FIELDLEN, integerValueValid );
//    public static EGLPropertyRule fileName = new EGLPropertyRule(IEGLConstants.PROPERTY_FILENAME, new int [] {nameValue, quotedValue} );
//    public static EGLPropertyRule fill = new EGLPropertyRule(IEGLConstants.PROPERTY_FILL, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule fillcharacter =new EGLPropertyRule(IEGLConstants.PROPERTY_FILLCHARACTER, new int [] {specificValue, quotedValue, nameValue}, new String [] {IEGLConstants.MNEMONIC_NULL}, true );
//    public static EGLPropertyRule getOptions = new EGLPropertyRule(IEGLConstants.PROPERTY_GETOPTIONSVAR, nameValueValid );
//    public static EGLPropertyRule help = new EGLPropertyRule(IEGLConstants.PROPERTY_HELP, quotedValueValid );
//    public static EGLPropertyRule helpForm = new EGLPropertyRule(IEGLConstants.PROPERTY_HELPFORM, nameValueValid );
//    public static EGLPropertyRule helpGroup = new EGLPropertyRule(IEGLConstants.PROPERTY_HELPGROUP, specificValueValid, yesOrNo );
//    public static EGLPropertyRule helpKey = new EGLPropertyRule(IEGLConstants.PROPERTY_HELPKEY, nameValueValid );
//    public static EGLPropertyRule highlight = new EGLPropertyRule(IEGLConstants.PROPERTY_HIGHLIGHT, new int []  {specificValue}, new String [] { IEGLConstants.MNEMONIC_NOHIGHLIGHT, IEGLConstants.MNEMONIC_BLINK, IEGLConstants.MNEMONIC_REVERSE, IEGLConstants.MNEMONIC_UNDERLINE } );
//    public static EGLPropertyRule includeFunctions = new EGLPropertyRule(IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS, specificValueValid, yesOrNo );
//    public static EGLPropertyRule includeMsgInTransaction = new EGLPropertyRule(IEGLConstants.PROPERTY_INCLUDEMSGINTRANSACTION, specificValueValid, yesOrNo );
//    public static EGLPropertyRule indexOrientation = new EGLPropertyRule(IEGLConstants.PROPERTY_INDEXORIENTATION, specificValueValid, new String [] {IEGLConstants.MNEMONIC_ACROSS, IEGLConstants.MNEMONIC_DOWN } );
//    public static EGLPropertyRule initialized = new EGLPropertyRule(IEGLConstants.PROPERTY_INITIALIZED, specificValueValid, yesOrNo );
//    public static EGLPropertyRule inputForm = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTFORM, nameValueValid );
//    public static EGLPropertyRule inputPageRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTPAGERECORD, nameValueValid );
//    public static EGLPropertyRule inputRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTRECORD, nameValueValid );
//    public static EGLPropertyRule inputRequired = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTREQUIRED, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule inputRequiredMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTREQUIREDMSGKEY, new int [] {quotedValue, integerValue}, null, true );
//    public static EGLPropertyRule intensity = new EGLPropertyRule(IEGLConstants.PROPERTY_INTENSITY, specificValueValid, new String [] { IEGLConstants.MNEMONIC_NORMALINTENSITY, IEGLConstants.MNEMONIC_BOLD, IEGLConstants.MNEMONIC_INVISIBLE, IEGLConstants.MNEMONIC_DIM, IEGLConstants.MNEMONIC_MASKED } );
//    public static EGLPropertyRule isDecimalDigit = new EGLPropertyRule(IEGLConstants.PROPERTY_ISDECIMALDIGIT, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule isHexDigit = new EGLPropertyRule(IEGLConstants.PROPERTY_ISHEXDIGIT, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule isNullable = new EGLPropertyRule(IEGLConstants.PROPERTY_ISNULLABLE, specificValueValid, yesOrNo );
//    public static EGLPropertyRule isReadOnly = new EGLPropertyRule(IEGLConstants.PROPERTY_ISREADONLY, specificValueValid, yesOrNo );
//    public static EGLPropertyRule keyItem = new EGLPropertyRule(IEGLConstants.PROPERTY_KEYITEM, nameValueValid );
//    public static EGLPropertyRule keyItems = new EGLPropertyRule(IEGLConstants.PROPERTY_KEYITEMS, new int [] {nameValue, listValue} );
//    public static EGLPropertyRule leftMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_LEFTMARGIN, integerValueValid );
//    public static EGLPropertyRule lengthItem = new EGLPropertyRule(IEGLConstants.PROPERTY_LENGTHITEM, nameValueValid );
//    public static EGLPropertyRule linesBetweenRows = new EGLPropertyRule(IEGLConstants.PROPERTY_LINESBETWEENROWS, integerValueValid );
//    public static EGLPropertyRule lowercase = new EGLPropertyRule(IEGLConstants.PROPERTY_LOWERCASE, specificValueValid, yesOrNo );
//    public static EGLPropertyRule masked = new EGLPropertyRule(IEGLConstants.PROPERTY_MASKED, specificValueValid, yesOrNo );
//    public static EGLPropertyRule maxSize = new EGLPropertyRule(IEGLConstants.PROPERTY_MAXSIZE, integerValueValid );
//    public static EGLPropertyRule minimumInput = new EGLPropertyRule(IEGLConstants.PROPERTY_MINIMUMINPUT, integerValueValid, null, true );
//    public static EGLPropertyRule minimumInputMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_MINIMUMINPUTMSGKEY, new int [] {quotedValue, integerValue}, null, true );
//    public static EGLPropertyRule modified = new EGLPropertyRule(IEGLConstants.PROPERTY_MODIFIED, specificValueValid, yesOrNo );
//    public static EGLPropertyRule msgDescriptor = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGDESCRIPTORVAR, nameValueValid );
//    public static EGLPropertyRule msgField = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGFIELD, nameValueValid );
//    public static EGLPropertyRule msgResource = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGRESOURCE, new int [] {nameValue, quotedValue} );
//    public static EGLPropertyRule msgTablePrefix = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGTABLEPREFIX, new int [] {nameValue, quotedValue} );
//    public static EGLPropertyRule needsSOSI = new EGLPropertyRule(IEGLConstants.PROPERTY_NEEDSSOSI, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule newWindow = new EGLPropertyRule(IEGLConstants.PROPERTY_NEWWINDOW, specificValueValid, yesOrNo );
//    public static EGLPropertyRule numElementsItem = new EGLPropertyRule(IEGLConstants.PROPERTY_NUMELEMENTSITEM, nameValueValid );
//    public static EGLPropertyRule numericSeparator = new EGLPropertyRule(IEGLConstants.PROPERTY_NUMERICSEPARATOR, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule onPageLoad = new EGLPropertyRule(IEGLConstants.PROPERTY_ONPAGELOADFUNCTION, nameValueValid );
//    public static EGLPropertyRule openOptions = new EGLPropertyRule(IEGLConstants.PROPERTY_OPENOPTIONSVAR, nameValueValid );
//    public static EGLPropertyRule openQueueExclusive = new EGLPropertyRule(IEGLConstants.PROPERTY_OPENQUEUEEXCLUSIVE, specificValueValid, yesOrNo );
//    public static EGLPropertyRule outline = new EGLPropertyRule(IEGLConstants.PROPERTY_OUTLINE, new int [] {specificValue, listValue}, new String [] { IEGLConstants.MNEMONIC_NOOUTLINE, IEGLConstants.MNEMONIC_BOX, IEGLConstants.MNEMONIC_RIGHT, IEGLConstants.MNEMONIC_LEFT, IEGLConstants.MNEMONIC_OVER, IEGLConstants.MNEMONIC_UNDER } );
//    public static EGLPropertyRule pageSize = new EGLPropertyRule(IEGLConstants.PROPERTY_PAGESIZE, listValueValid );
//    public static EGLPropertyRule pattern = new EGLPropertyRule(IEGLConstants.PROPERTY_PATTERN, quotedValueValid );
//    public static EGLPropertyRule pfEquate = new EGLPropertyRule(IEGLConstants.PROPERTY_PFKEYEQUATE, specificValueValid, yesOrNo );
//    public static EGLPropertyRule position = new EGLPropertyRule(IEGLConstants.PROPERTY_POSITION, listValueValid );
//    public static EGLPropertyRule printFloatingArea = new EGLPropertyRule(IEGLConstants.PROPERTY_PRINTFLOATINGAREA, new int [] {nestedValue} );
//	public static EGLPropertyRule printFormHighlight = new EGLPropertyRule(IEGLConstants.PROPERTY_HIGHLIGHT, specificValueValid, new String [] {IEGLConstants.MNEMONIC_UNDERLINE } );
//    public static EGLPropertyRule protect =  new EGLPropertyRule(IEGLConstants.PROPERTY_PROTECT, specificValueValid, new String [] {IEGLConstants.MNEMONIC_YES, IEGLConstants.MNEMONIC_SKIP } );
//    public static EGLPropertyRule protectSkip = new EGLPropertyRule(IEGLConstants.PROPERTY_PROTECT, new int []  {specificValue}, new String [] { IEGLConstants.MNEMONIC_YES, IEGLConstants.MNEMONIC_NO, IEGLConstants.MNEMONIC_SKIP } );
//    public static EGLPropertyRule putOptions = new EGLPropertyRule(IEGLConstants.PROPERTY_PUTOPTIONSVAR, nameValueValid );
//    public static EGLPropertyRule queueDescriptor = new EGLPropertyRule(IEGLConstants.PROPERTY_QUEUEDESCRIPTORVAR, nameValueValid );
//    public static EGLPropertyRule queueName = new EGLPropertyRule(IEGLConstants.PROPERTY_QUEUENAME, new int [] {nameValue, quotedValue} );
//    public static EGLPropertyRule range = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDVALUES, listValueValid, null, true );
//    public static EGLPropertyRule rangeMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDVALUESMSGKEY, new int [] {quotedValue, integerValue}, null, true );
//    public static EGLPropertyRule redefines = new EGLPropertyRule(IEGLConstants.PROPERTY_REDEFINESRECORD, nameValueValid );
//    public static EGLPropertyRule resident = new EGLPropertyRule(IEGLConstants.PROPERTY_RESIDENT, specificValueValid, yesOrNo );
//    public static EGLPropertyRule rightMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_RIGHTMARGIN, integerValueValid );
//    public static EGLPropertyRule runValidatorFromProgram = new EGLPropertyRule(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM, specificValueValid, yesOrNo );
//    public static EGLPropertyRule screenFloatingArea = new EGLPropertyRule(IEGLConstants.PROPERTY_SCREENFLOATINGAREA, new int [] {nestedValue} );
//    public static EGLPropertyRule screenSize = new EGLPropertyRule(IEGLConstants.PROPERTY_SCREENSIZE, listValueValid );
//    public static EGLPropertyRule screenSizes = new EGLPropertyRule(IEGLConstants.PROPERTY_SCREENSIZES, listValueValid );
//    public static EGLPropertyRule segmented = new EGLPropertyRule(IEGLConstants.PROPERTY_SEGMENTED, specificValueValid, yesOrNo );
//    public static EGLPropertyRule selectFromList =new EGLPropertyRule(IEGLConstants.PROPERTY_SELECTFROMLISTITEM, nameValueValid);
//    public static EGLPropertyRule selectType =new EGLPropertyRule(IEGLConstants.PROPERTY_SELECTTYPE, specificValueValid, new String [] { IEGLConstants.MNEMONIC_INDEX, IEGLConstants.MNEMONIC_VALUE } );
//    public static EGLPropertyRule shared = new EGLPropertyRule(IEGLConstants.PROPERTY_SHARED, specificValueValid, yesOrNo );
//    public static EGLPropertyRule sign = new EGLPropertyRule(IEGLConstants.PROPERTY_SIGN, specificValueValid, new String [] { IEGLConstants.MNEMONIC_NONE, IEGLConstants.MNEMONIC_LEADING, IEGLConstants.MNEMONIC_TRAILING, IEGLConstants.MNEMONIC_PARENS }, true );
//    public static EGLPropertyRule formSize = new EGLPropertyRule(IEGLConstants.PROPERTY_FORMSIZE, listValueValid );
//    public static EGLPropertyRule spacesBetweenColumns = new EGLPropertyRule(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS, integerValueValid );
//    public static EGLPropertyRule sqlDataCode =new EGLPropertyRule(IEGLConstants.PROPERTY_SQLDATACODE, integerValueValid);
//	public static EGLPropertyRule sqlVar = new EGLPropertyRule(IEGLConstants.PROPERTY_SQLVARIABLELEN, specificValueValid, yesOrNo );
//    public static EGLPropertyRule tableNameVariables = new EGLPropertyRule(IEGLConstants.PROPERTY_TABLENAMEVARIABLES, new int [] {listValue, nameValue} );
//    public static EGLPropertyRule tableNames = new EGLPropertyRule(IEGLConstants.PROPERTY_TABLENAMES, new int [] {listValue, quotedValue, nameValue} );
//    public static EGLPropertyRule timeFormat = new EGLPropertyRule(IEGLConstants.PROPERTY_TIMEFORMAT, new int [] {specificValue, quotedValue}, new String [] {IEGLConstants.MNEMONIC_ISO, IEGLConstants.MNEMONIC_USA, IEGLConstants.MNEMONIC_EUR, IEGLConstants.MNEMONIC_JIS }, true );
//    public static EGLPropertyRule title = new EGLPropertyRule(IEGLConstants.PROPERTY_TITLE,  new int [] {literalValue, nameValue} );
//    public static EGLPropertyRule topMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_TOPMARGIN, integerValueValid );
//    public static EGLPropertyRule typeChkMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_TYPECHKMSGKEY, new int [] {quotedValue, integerValue}, null, true );
//    public static EGLPropertyRule unqualifiedItems = new EGLPropertyRule(IEGLConstants.PROPERTY_ALLOWUNQUALIFIEDITEMREFERENCES, specificValueValid, yesOrNo );
//    public static EGLPropertyRule uppercase = new EGLPropertyRule(IEGLConstants.PROPERTY_UPPERCASE, specificValueValid, yesOrNo, true );
//    public static EGLPropertyRule validationBypassFunctions = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS, listValueValid );
//    public static EGLPropertyRule validationOrder = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATIONORDER, integerValueValid );
//    public static EGLPropertyRule validator = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORFUNCTION, nameValueValid, null, true );
//	public static EGLPropertyRule validatorMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORFUNCTIONMSGKEY, new int [] {quotedValue, integerValue}, null, true );
//    public static EGLPropertyRule validatorTable = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORDATATABLE, nameValueValid, null, true);
//    public static EGLPropertyRule validatorTableMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORDATATABLEMSGKEY, new int [] {quotedValue, integerValue}, null, true );
//    public static EGLPropertyRule value = new EGLPropertyRule(IEGLConstants.PROPERTY_VALUE, new int [] {literalValue});
//    public static EGLPropertyRule valueQuoted = new EGLPropertyRule(IEGLConstants.PROPERTY_VALUE, quotedValueValid );
//    public static EGLPropertyRule view = new EGLPropertyRule(IEGLConstants.PROPERTY_VIEW, quotedValueValid );
//    public static EGLPropertyRule wordWrap = new EGLPropertyRule(IEGLConstants.PROPERTY_WORDWRAP, specificValueValid, yesOrNo );
//    public static EGLPropertyRule zeroFormat = new EGLPropertyRule(IEGLConstants.PROPERTY_ZEROFORMAT, specificValueValid, yesOrNo, true );
//    
//    private static Map map = new HashMap();
//    
//    public static void main(String[] args) throws Exception {
//        Class handlerClass = EGLPropertyEnumerator.class;
//        Field[] fields = handlerClass.getFields();
//        for (int i = 0; i < fields.length; i++) {
//            Field field = fields[i];
//            if(field.getType() == EGLPropertyRule.class) {
//                EGLPropertyRule rule = (EGLPropertyRule) field.get(null);
//                process(rule);
//            }
//        }
//        
//        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
//            PropertyType pType = (PropertyType) iter.next();
//            System.out.println(pType.toString());
//            
//            ArrayList list = (ArrayList) map.get(pType);
//            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
//                EGLPropertyRule rule = (EGLPropertyRule) iterator.next();
//                System.out.println("\t" + rule.getName());
//            }
//        }
//            
//    }
//    
//    private static void process(EGLPropertyRule rule) {
//        PropertyType pType = new PropertyType(rule.getTypes());
//        if(map.get(pType) == null) {
//            map.put(pType, new ArrayList());
//        }
//        ArrayList list = (ArrayList) map.get(pType);
//        list.add(rule);
////        int[] type = rule.getTypes();
////        for (int i = 0; i < type.length; i++) {
////            if(type[i] == specificValue && rule.getSpecificValues() != yesOrNo) {
////                System.out.println(rule.getName());
////            }
////        }
//        if(rule.getSpecificValues() == yesOrNo) {
//            System.out.println(rule.getName());
//        }
//    }
//    
    public static class PropertyType {
        private int[] type;
        public PropertyType(int[] type) {
            this.type = type;
        }
        public boolean equals(Object object) {
            if(this.getClass() != object.getClass()) return false;
            PropertyType other = (PropertyType) object;
            if(this.type.length != other.type.length) return false;
            for (int i = 0; i < this.type.length; i++) {
                if(this.type[i] != other.type[i]) return false;
            }
            return true;
        }
        public int hashCode() {
            return 0;
        }
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append('[');
            boolean first = true;
            for (int i = 0; i < type.length; i++) {
                if(!first) {
                    buffer.append(", ");
                }
                buffer.append(getType(type[i]));
                first = false;
            }
            buffer.append(']');
            return buffer.toString();
        }
        private String getType(int type) {
            switch (type) {
	            case 0: return "reference";
	            case 1: return "string";
	            case 2: return "enum";
	            case 3: return "int";
	            case 4: return "literal";
	            case 5: return "list";
	            case 6: return "literalArray";
	            case 7: return "nested";
	            case 8: return "sql";
	            default: return "unknown";
            }
        }
    }
}

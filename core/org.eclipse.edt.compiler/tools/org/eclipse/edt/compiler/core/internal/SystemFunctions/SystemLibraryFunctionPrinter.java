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
package org.eclipse.edt.compiler.core.internal.SystemFunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.SystemFunctionBinding;
import org.eclipse.edt.compiler.binding.SystemVariableBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;


/**
 * @author Harmon
 */
public class SystemLibraryFunctionPrinter {

    private static boolean typeMapNeeded;
    private static boolean typeMapListNeeded;
    private static List typeMapList;
    private static String typeMap;
    /**
     *  
     */
    public SystemLibraryFunctionPrinter() {
        super();
    }

    public static void main(String[] args) {
//        printLibrary(ConsoleLib.LIBRARY);
//        printLibrary(ConverseLib.LIBRARY);
//        printLibrary(ConverseVar.LIBRARY);
//        printLibrary(DateTimeLib.LIBRARY);
//        printLibrary(DLILib.LIBRARY);
//        printLibrary(DLIVar.LIBRARY);
//        printLibrary(J2EELib.LIBRARY);
//        printLibrary(JavaLib.LIBRARY);
//        printLibrary(LobLib.LIBRARY);
//        printLibrary(MathLib.LIBRARY);
//        printLibrary(ReportLib.LIBRARY);
//        printLibrary(ServiceLib.LIBRARY);
//        printLibrary(StrLib.LIBRARY);
//        printLibrary(SysLib.LIBRARY);
//        printLibrary(SysVar.LIBRARY);
//        printLibrary(VGLib.LIBRARY);
//        printLibrary(VGVar.LIBRARY);
        
    }
    
    private static void printLibrary(LibraryBinding lib) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("package ");
        String[] pkg = lib.getPackageName();
        for (int i = 0; i < pkg.length; i++) {
            buffer.append(pkg[i]);
            if (i < pkg.length -1) {
                buffer.append(".");
            }
        }
        buffer.append(";");
        buffer.append("\n\n");
        
        buffer.append("library ");
        buffer.append(lib.getName());
        buffer.append("\n\n");

        
        Iterator i = lib.getDeclaredFunctions().iterator();
        while (i.hasNext()) {
            buffer.append("\t");
            SystemFunctionBinding binding = (SystemFunctionBinding) i.next();
            print(binding, buffer);
            buffer.append("\n\n");
        }
        i = lib.getClassFields().iterator();
        while (i.hasNext()) {
            buffer.append("\t");
            SystemVariableBinding binding = (SystemVariableBinding) i.next();
            print(binding, buffer);
            buffer.append("\n");
        }
        
        buffer.append("end");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append("\n");
        System.out.println(buffer.toString());
    }

    private static void print(SystemVariableBinding binding, StringBuffer buffer) {
        typeMap = null;
        typeMapNeeded = false;

        buffer.append(binding.getName());
        buffer.append(" ");
        buffer.append(getTypeString(binding.getType(), false));
        buffer.append(" ");
        printAnnotations(binding, buffer);
        buffer.append(";");
    }

    
    private static void print(SystemFunctionBinding binding, StringBuffer buffer) {
        typeMapList = new ArrayList();
        typeMapListNeeded = false;
        typeMap = null;
        typeMapNeeded = false;

        buffer.append("function ");
        buffer.append(binding.getName());
        buffer.append("(");
        printParms(binding, buffer);
        buffer.append(")");
        printReturnType(binding, buffer);
        buffer.append("\n");
        buffer.append("\t\t");
        printAnnotations(binding, buffer);
        buffer.append("\n");
        buffer.append("\tend");
    }

    private static void printAnnotations(SystemFunctionBinding binding, StringBuffer buffer) {
        buffer.append("{");
        buffer.append("eglSystemConstant = ");
        buffer.append(binding.getSystemFunctionType());
        if (typeMapListNeeded) {
            buffer.append(", eglSystemParameterTypes = [");
            printtypeMapListString(buffer);
            buffer.append("]");
        }
        if (typeMapNeeded) {
            buffer.append(", eglSystemType = \"");
            buffer.append(typeMap);
            buffer.append("\"");
        }
        buffer.append("}");
        
    }

    private static void printAnnotations(SystemVariableBinding binding, StringBuffer buffer) {
        buffer.append("{");
        buffer.append("eglSystemConstant = ");
        buffer.append(binding.getSystemVariableType());
        if (typeMapNeeded) {
            buffer.append(", eglSystemType = \"");
            buffer.append(typeMap);
            buffer.append("\"");
        }
        buffer.append("}");
        
    }

    private static void printtypeMapListString(StringBuffer buffer) {
        Iterator i = typeMapList.iterator();
        while (i.hasNext()) {
            buffer.append("\"");
            buffer.append((String)i.next());
            buffer.append("\"");
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
    }
    
    private static void printParms(SystemFunctionBinding binding, StringBuffer buffer) {
        Iterator i = binding.getParameters().iterator();
        while (i.hasNext()) {
            FunctionParameterBinding parm = (FunctionParameterBinding) i.next();
            printParm(parm, buffer);
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
    }

    private static void printParm(FunctionParameterBinding parm, StringBuffer buffer) {
        String name = parm.getName();
        String type = getTypeString(parm.getType(), true);
        String usage = getUsageString(parm);
        buffer.append(name);
        buffer.append(" ");
        buffer.append(type);
        if (usage.length() > 0) {
	        buffer.append(" ");
	        buffer.append(usage);
        }
    }
    
    private static void printReturnType(SystemFunctionBinding binding, StringBuffer buffer) {
        if (binding.getReturnType() == null) {
            return;
        }
        buffer.append(" returns(");
        buffer.append(getTypeString(binding.getReturnType(), false));
        buffer.append(")");
    }

    private static String getUsageString(FunctionParameterBinding parm) {
        if (parm.isInput()) {
            return "in";
        }
        if (parm.isOutput()) {
            return "out";
        }
        return "";
    }

    private static String getTypeString(ITypeBinding type, boolean useTypeMapList) {
        
        if (type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
            return getTypeString(((ArrayTypeBinding) type).getElementType(), useTypeMapList) + "[]";
        }
        if (type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
            if (useTypeMapList) {
                typeMapList.add("");
            }
            return getPrimitiveString((PrimitiveTypeBinding) type);
        }
        
        if (type.getKind() == ITypeBinding.SPECIALSYSTEMFUNCTIONPARAMETER_BINDING) {
            if (useTypeMapList) {
	            typeMapListNeeded = true;
	            typeMapList.add(type.getName());
            }
            else {
	            typeMapNeeded = true;
	            typeMap = type.getName();
            }
            return "int";
        }

        if (type.getKind() == ITypeBinding.ENUMERATION_BINDING) {
            if (useTypeMapList) {
	            typeMapListNeeded = true;
	            typeMapList.add(type.getName());
            }
            else {
	            typeMapNeeded = true;
	            typeMap = type.getName();
            }
            return "int";
        }

        if (useTypeMapList) {
            typeMapList.add("");
        }
        return type.getName();
    }
    
    private static String getPrimitiveString(PrimitiveTypeBinding primType) {
        int lenInt = primType.getLength();
        String len = Integer.toString(lenInt);
        int decInt = primType.getDecimals();
        String dec = Integer.toString(decInt);
        String pattern = primType.getTimeStampOrIntervalPattern();
        Primitive prim = primType.getPrimitive();
        if (prim == Primitive.ANY) {
            return "any";
        }
        if (prim == Primitive.BIGINT) {
            return "bigint";
        }
        if (prim == Primitive.BIN) {
            String out = "bin(";
            out = out + len;
            if (decInt > 0) {
                out = out + "," + dec;
            }
            out = out + ")";
            return out;
        }
        if (prim == Primitive.BOOLEAN) {
            return "boolean";
        }

        if (prim == Primitive.CHAR) {
            String out = "char";
            if (lenInt > 0) {
                out = out + "(" + len + ")";
            }
            return out;
        }
        if (prim == Primitive.DBCHAR) {
            String out = "dbchar";
            if (lenInt > 0) {
                out = out + "(" + len + ")";
            }
            return out;
        }
        if (prim == Primitive.DECIMAL) {
            String out = "decimal(";
            out = out + len;
            if (decInt > 0) {
                out = out + "," + dec;
            }
            out = out + ")";
            return out;
        }
        if (prim == Primitive.FLOAT) {
            return "float";
        }
        if (prim == Primitive.HEX) {
            String out = "hex";
            if (lenInt > 0) {
                out = out + "(" + len + ")";
            }
            return out;
        }
        if (prim == Primitive.INT) {
            return "int";
        }
        if (prim == Primitive.MBCHAR) {
            String out = "mbchar";
            if (lenInt > 0) {
                out = out + "(" + len + ")";
            }
            return out;
        }
        if (prim == Primitive.MONEY) {
            String out = "money";
            if (lenInt > 0) {
                out = out + "(" + len;

                if (decInt > 0) {
                    out = out + "," + dec;
                }
                out = out + ")";
            }
            return out;
        }
        if (prim == Primitive.NUM) {
            String out = "num(";
            out = out + len;
            if (decInt > 0) {
                out = out + "," + dec;
            }
            out = out + ")";
            return out;
        }
        if (prim == Primitive.NUMBER) {
            return "number";
        }
        if (prim == Primitive.NUMC) {
            String out = "numc(";
            out = out + len;
            if (decInt > 0) {
                out = out + "," + dec;
            }
            out = out + ")";
            return out;
        }
        if (prim == Primitive.PACF) {
            String out = "pacf(";
            out = out + len;
            if (decInt > 0) {
                out = out + "," + dec;
            }
            out = out + ")";
            return out;
        }
        if (prim == Primitive.SMALLFLOAT) {
            return "smallfloat";
        }
        if (prim == Primitive.SMALLINT) {
            return "smallint";
        }
        if (prim == Primitive.STRING) {
            return "string";
        }
        if (prim == Primitive.UNICODE) {
            String out = "unicode";
            if (lenInt > 0) {
                out = out + "(" + len + ")";
            }
            return out;
        }
        if (prim == Primitive.BLOB) {
            return "blob";
        }
        if (prim == Primitive.CLOB) {
            return "clob";
        }
        if (prim == Primitive.DATE) {
            return "date";
        }
        if (prim == Primitive.MONTHSPAN_INTERVAL ||
            prim == Primitive.SECONDSPAN_INTERVAL) {
            String out = "interval";
            if (pattern != null) {
                out = out + "(" + pattern + ")";
            }
            return out;
        }
        if (prim == Primitive.TIME) {
            return "time";
        }
        if (prim == Primitive.TIMESTAMP) {
            String out = "timestamp";
            if (pattern != null) {
                out = out + "(" + pattern + ")";
            }
            return out;
        }

        return "unknown prmitive!!";
    }
}


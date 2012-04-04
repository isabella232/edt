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
package org.eclipse.edt.ide.ui.internal.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.TypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceFrExternalTypeConfiguration;

public class ExternalType2RuiInterfaceConverter {
	private final static String NEWLINE = System.getProperty( "line.separator" );
	private final static String INDENT = "  ";
	
	private ExternalType2RuiInterfaceConverter(){
	}
	
	public static Map convert(ExtractInterfaceFrExternalTypeConfiguration config) {
		final HashSet completedTypes = new HashSet();
		final Map buffers = new HashMap();

		StringBuffer interfaceBuf = new StringBuffer();
		
		List econfigs = config.getExtractInterfaceConfigurations();
		for(Iterator it=econfigs.iterator(); it.hasNext();) {
			ExtractInterfaceConfiguration econfig = (ExtractInterfaceConfiguration)it.next();
			Part part = econfig.getTheBoundPart();
			if(part instanceof ExternalType)
				convert(econfig, (ExternalType)part, interfaceBuf, buffers, completedTypes, config.getFPackage() );
		}
		
		StringBuffer defaultBuf = getBuffer(config.getFPackage(), buffers);
		defaultBuf.insert(0, interfaceBuf);
		return buffers;
	}
	
	private static void convert( final ExtractInterfaceConfiguration config, ExternalType et, final StringBuffer interfaceBuf, 
			final Map buffers, final HashSet completedTypes, final String currFilePkgName ) {
		
		interfaceBuf.append( IEGLConstants.KEYWORD_INTERFACE );
		interfaceBuf.append( ' ' );
		interfaceBuf.append( config.getInterfaceName());
		interfaceBuf.append( " {@" );
		interfaceBuf.append( IEGLConstants.PROPERTY_XML );
		interfaceBuf.append( "{" );
		interfaceBuf.append( IEGLConstants.PROPERTY_NAME );
		interfaceBuf.append( "=\"" );
		interfaceBuf.append( getHostProgramName( et ) );
		interfaceBuf.append( "\" }}" );
		interfaceBuf.append( NEWLINE );
		et.accept(new DefaultASTVisitor(){
			private int idx;
			public boolean visit(ExternalType externaltype) {return true;}
			
			public boolean visit(NestedFunction nestedFunction) {
				if(config.getFunctionSelectionState( idx++ ) && !nestedFunction.isPrivate()) //only extract the public functions into the interface
				{
		        	handleFunction(nestedFunction, interfaceBuf, buffers, completedTypes, currFilePkgName );
				}
				return false;
			};
		});
		
		interfaceBuf.append( IEGLConstants.KEYWORD_END );
		interfaceBuf.append( NEWLINE );
		interfaceBuf.append( NEWLINE );
	}

	private static void handleFunction(NestedFunction func, final StringBuffer interfaceBuf, 
			final Map buffers, final HashSet completedTypes, String currFilePkgName) {
		interfaceBuf.append( INDENT );
		interfaceBuf.append( ' ' );
		interfaceBuf.append( IEGLConstants.KEYWORD_FUNCTION );
		interfaceBuf.append( ' ' );
		interfaceBuf.append( func.getName().getCaseSensitiveIdentifier());
		interfaceBuf.append( "( " );
		
		DefaultASTVisitor recordVisitor = new DefaultASTVisitor(){
			public boolean visit(NestedFunction nestedFunction) {
				return true;
			}
			public boolean visit(SimpleName name) {
				createNewFlexRecord(name.resolveBinding(), buffers, completedTypes);
				return false;
			}
			public boolean visit(QualifiedName name) {
				createNewFlexRecord(name.resolveBinding(), buffers, completedTypes);
				return false;
			}
		};
		
		List parms = func.getFunctionParameters();
		int numParms = (parms == null) ? 0 : parms.size();
		FunctionParameter functionParameter;
		for( int parmIndex = 0; parmIndex < numParms; parmIndex++ ) {
			functionParameter = (FunctionParameter)parms.get( parmIndex );
			if( parmIndex > 0 ) {
				interfaceBuf.append( ", " );
			}
			interfaceBuf.append( functionParameter.getName().getIdentifier() );
			interfaceBuf.append( ' ' );
			if( functionParameter.getType().getKind() == Type.NAMETYPE ) {
				interfaceBuf.append( ExtractInterfaceConfiguration.getQualifiedTypeString(functionParameter, currFilePkgName, true) );//((NameType)functionParameter.getType()).getName().getCanonicalName() );
				createNewFlexRecord(((NameType)functionParameter.getType()).getName().resolveBinding(), buffers, completedTypes );
			} else if( functionParameter.getType().isPrimitiveType() ) {
				interfaceBuf.append( type( (PrimitiveType)functionParameter.getType() ) );
			}
			interfaceBuf.append( ' ' );
			interfaceBuf.append( functionParameter.getUseType() == null ? UseType.INOUT.toString() : functionParameter.getUseType().toString() );
		}
		
		interfaceBuf.append( " )" );
		Type retType = func.getReturnDeclaration()== null ? null : func.getReturnType();
		if( retType != null ) {
			interfaceBuf.append( ' ' );
			interfaceBuf.append( IEGLConstants.KEYWORD_RETURNS );
			interfaceBuf.append( "( " );
			if( retType.getKind() == Type.NAMETYPE ) {
				interfaceBuf.append( ((NameType)retType).getName().getCanonicalName() );
				((NameType)retType).getName().accept(recordVisitor);
			} else if( retType.isPrimitiveType() ) {
				interfaceBuf.append( type( (PrimitiveType)retType) );
			}
			interfaceBuf.append( " )" );
		}
		interfaceBuf.append( " {@" );
		interfaceBuf.append( IEGLConstants.PROPERTY_XML );
		interfaceBuf.append( "{" );
		interfaceBuf.append( IEGLConstants.PROPERTY_NAME );
		interfaceBuf.append( "=\"" );
		interfaceBuf.append( getHostFunctionName( func ) );
		interfaceBuf.append( "\"}};\n" );
	}

	private static StringBuffer getBuffer( String qualifier, Map buffers ) {
		StringBuffer buf = (StringBuffer)buffers.get(qualifier);
		if( buf == null ) {
			buf = new StringBuffer();
			buffers.put(qualifier, buf);
		}
		return buf;
	}
	
	private static String getHostProgramName( ExternalType et ) {
		return et.getIdentifier();
	}
	
	private static String getHostFunctionName( NestedFunction function ) {
		return function.getName().getIdentifier();
	}
	
	private static void createNewFlexRecord( IBinding binding, Map buffers, HashSet completedTypes ) {
		FixedRecordBinding rec;
		if( !Binding.isValidBinding(binding) ||
				!binding.isTypeBinding() || 
				((TypeBinding)binding).getKind() != TypeBinding.FIXED_RECORD_BINDING ||
				!completedTypes.add( (rec = (FixedRecordBinding)binding).getPackageQualifiedName() )) {
			//return if it's not a valid fixed record
			return;
		}
		createNewFlexRecord( rec.getName(), getQualifier(rec), rec.getStructureItems(), buffers, completedTypes );
	}

	private static void createNewFlexRecord( String recordName, String quailifier, List fields, 
			Map buffers, HashSet completedTypes ) {
		StringBuffer recordBuf = new StringBuffer();
		recordBuf.append( IEGLConstants.KEYWORD_RECORD + " " + recordName + " type " +
				     IEGLConstants.PROPERTY_BASICRECORD + NEWLINE );
		
		int numFields = (fields == null) ? 0 : fields.size();
		for( int i = 0; i < numFields; i++ ) {
			convert( (StructureItemBinding)fields.get( i ), quailifier, recordBuf, buffers, completedTypes );
		}
		
		recordBuf.append( IEGLConstants.KEYWORD_END +  NEWLINE + NEWLINE );
		StringBuffer qualifierBuf = getBuffer(quailifier, buffers);
		qualifierBuf.append( recordBuf );
	}

	private static String type( PrimitiveType primitiveType ) {
		return type( primitiveType.getPrimitive(), primitiveType.getPrimLength(), primitiveType.getPrimDecimals() );
	}
	
	private static String type( PrimitiveTypeBinding primitiveTypeBinding ) {
		return type( primitiveTypeBinding.getPrimitive(), String.valueOf(primitiveTypeBinding.getLength()), String.valueOf(primitiveTypeBinding.getDecimals()) );
	}
	
	private static String type( Primitive primitive, String lengthValue, String precisionValue ) {
		String length = "", precision = ""; 
		if( lengthValue != null ) {
			precision = "( " + lengthValue;
			if( precisionValue != null ) {
				precision += ", " + precisionValue;
			}
			precision += " )";
			length = "( " + lengthValue + " )";
		}
		
		switch( primitive.getType() ) {
			case Primitive.BOOLEAN_PRIMITIVE:
				return IEGLConstants.KEYWORD_BOOLEAN;
				
			case Primitive.HEX_PRIMITIVE:
				return IEGLConstants.KEYWORD_HEX + length;
				
			case Primitive.CHAR_PRIMITIVE:
			case Primitive.DBCHAR_PRIMITIVE:
			case Primitive.MBCHAR_PRIMITIVE:
			case Primitive.STRING_PRIMITIVE:
			case Primitive.UNICODE_PRIMITIVE:
				return IEGLConstants.KEYWORD_STRING;
				
			case Primitive.DATE_PRIMITIVE:
				return IEGLConstants.KEYWORD_DATE;
				
			case Primitive.TIME_PRIMITIVE:
				return IEGLConstants.KEYWORD_TIME;
				
			case Primitive.TIMESTAMP_PRIMITIVE:
				return IEGLConstants.KEYWORD_TIMESTAMP;
				
			case Primitive.INTERVAL_PRIMITIVE:
				return IEGLConstants.KEYWORD_INTERVAL;
				
			case Primitive.INT_PRIMITIVE:
				return IEGLConstants.KEYWORD_INT;
			case Primitive.SMALLINT_PRIMITIVE:
				return IEGLConstants.KEYWORD_SMALLINT;
			case Primitive.BIGINT_PRIMITIVE:
				return IEGLConstants.KEYWORD_BIGINT;
				
			case Primitive.FLOAT_PRIMITIVE:
				return IEGLConstants.KEYWORD_SMALLFLOAT;
			case Primitive.SMALLFLOAT_PRIMITIVE:
				return IEGLConstants.KEYWORD_FLOAT;
				
			case Primitive.BIN_PRIMITIVE:
			case Primitive.PACF_PRIMITIVE:
			case Primitive.NUMC_PRIMITIVE:
			case Primitive.NUM_PRIMITIVE:
				return IEGLConstants.KEYWORD_NUM + precision;
			case Primitive.DECIMAL_PRIMITIVE:
				return IEGLConstants.KEYWORD_DECIMAL + precision;
			case Primitive.MONEY_PRIMITIVE:
				return IEGLConstants.KEYWORD_MONEY + precision;
		}
		
		return IEGLConstants.KEYWORD_ANY;
	}
	
	private static String getTypeNameForSubstructuredFieldRecord( StructureItemBinding sField ) {
		StringBuffer sbuf = new StringBuffer( sField.getParentQualifiedName().replaceAll( "\\.", "_" ) );
		sbuf.insert( 0, sField.getDeclaringPart().getName() + "_" );
		return sbuf.toString();
	}
	
	private static String getQualifier(IPartBinding binding) {
		String[] parts;
		if( Binding.isValidBinding(binding) && 
				(parts = binding.getPackageName()) != null && 
				parts.length > 0 ) {
			String qualifier = "";
			for( int idx = 0; idx < parts.length; idx++ ) {
				if( idx > 0 ) {
					qualifier += '.';
				}
				qualifier += parts[idx];
			}
			return qualifier;
		} else {
			return "";
		}
	}
	
	private static void convert( StructureItemBinding sField, String quailifier, 
			StringBuffer sbuf, Map buffers, HashSet completedTypes ) {
		sbuf.append( INDENT );

		sbuf.append( sField.getName() + " " );
		if( sField.getChildren() != null && sField.getChildren().size() > 0 ) {
			StructureItemBinding binding = (StructureItemBinding)sField.getChildren().get(0);
			if( Binding.isValidBinding(binding)) {
				String subRecordName = getTypeNameForSubstructuredFieldRecord(sField);
				sbuf.append( subRecordName );
				createNewFlexRecord(subRecordName, quailifier, sField.getChildren(), buffers, completedTypes );
			}
		} else if( sField.getType().getKind() ==  TypeBinding.PRIMITIVE_TYPE_BINDING ) {
			
			// If this is a leaf field, generate it "as is" -- just map the type
			sbuf.append( type( (PrimitiveTypeBinding)sField.getType() ) );
		}
		
		if( sField.hasOccurs() ) {
			sbuf.append( "[" + sField.getOccurs() + "]" );
		}
		
		sbuf.append( ";" + NEWLINE );
	}
}

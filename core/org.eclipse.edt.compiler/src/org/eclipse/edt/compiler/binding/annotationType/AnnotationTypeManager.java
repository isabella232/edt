/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Name;


/**
 * @author Harmon
 */
public class AnnotationTypeManager {
    private static AnnotationTypeManager INSTANCE = new AnnotationTypeManager();
    private Map annotationTypeMap = Collections.EMPTY_MAP;
    private List resolvableTypes = null; 
    
    private Map systemPackageAnnotations = new HashMap();
    
    public static AnnotationTypeManager getInstance() {
        return INSTANCE;
    }
    
    public IAnnotationTypeBinding getAnnotationType(String name) {
        return (IAnnotationTypeBinding) getAnnotationTypeMap().get(name);
    }
    
    public IAnnotationTypeBinding getAnnotationType(Name name) {
    	Map annotationTypeMap = getAnnotationTypeMap();
        return (IAnnotationTypeBinding) annotationTypeMap.get(name.getIdentifier());
    }

    /**
     * @return Returns the annotationTypeMap.
     */
    public Map getAnnotationTypeMap() {
        if (annotationTypeMap == Collections.EMPTY_MAP) {
            intializeAnnotationTypeMap();
        }
        return annotationTypeMap;
    }
    
    private void intializeAnnotationTypeMap() {
        annotationTypeMap = new HashMap();
        
        annotationTypeMap.put(AnnotationAnnotationTypeBinding.name, AnnotationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(StereotypeAnnotationTypeBinding.name, StereotypeAnnotationTypeBinding.getInstance());
        
//        annotationTypeMap.put(FieldDeclarationAnnotationTypeBinding.name, FieldDeclarationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(FinalAnnotationTypeBinding.name, FinalAnnotationTypeBinding.getInstance());
//        annotationTypeMap.put(FunctionDeclarationAnnotationTypeBinding.name, FunctionDeclarationAnnotationTypeBinding.getInstance());
        
        annotationTypeMap.put(EGLAnnotationGroupAnnotationTypeBinding.name, EGLAnnotationGroupAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLImplicitExtendedTypeAnnotationTypeBinding.name, EGLImplicitExtendedTypeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLDataItemPropertyProblemsAnnotationTypeBinding.name, EGLDataItemPropertyProblemsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLDataItemPropertyProblemAnnotationTypeBinding.name, EGLDataItemPropertyProblemAnnotationTypeBinding.getInstance());        
        annotationTypeMap.put(EGLIsReadOnlyAnnotationTypeBinding.name, EGLIsReadOnlyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLIsSystemAnnotationAnnotationTypeBinding.name, EGLIsSystemAnnotationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLIsBIDIEnabledAnnotationTypeBinding.name, EGLIsBIDIEnabledAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLIsSystemPartAnnotationTypeBinding.name, EGLIsSystemPartAnnotationTypeBinding.getInstance());        
        annotationTypeMap.put(EGLNotInCurrentReleaseAnnotationTypeBinding.name, EGLNotInCurrentReleaseAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLSpecificValuesAnnotationTypeBinding.name, EGLSpecificValuesAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLSystemConstantAnnotationTypeBinding.name, EGLSystemConstantAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLSystemParameterTypesAnnotationTypeBinding.name, EGLSystemParameterTypesAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLLineNumberAnnotationTypeBinding.name, EGLLineNumberAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLLocationAnnotationTypeBinding.name, EGLLocationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLValidNumberOfArgumentsAnnotationTypeBinding.name, EGLValidNumberOfArgumentsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(EGLValueCanBeExpressionForOpenUIAnnotationTypeBinding.name, EGLValueCanBeExpressionForOpenUIAnnotationTypeBinding.getInstance());

/*        
        //Part subtypes:

        annotationTypeMap.put(BasicRecordAnnotationTypeBinding.name, BasicRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ConsoleFormAnnotationTypeBinding.name, ConsoleFormAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DLISegmentAnnotationTypeBinding.name, DLISegmentAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IndexedRecordAnnotationTypeBinding.name, IndexedRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MQRecordAnnotationTypeBinding.name, MQRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(PSBRecordAnnotationTypeBinding.name, PSBRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(RelativeRecordAnnotationTypeBinding.name, RelativeRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SerialRecordAnnotationTypeBinding.name, SerialRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SQLRecordAnnotationTypeBinding.name, SQLRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(VGUIRecordAnnotationTypeBinding.name, VGUIRecordAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ExceptionAnnotationTypeBinding.name, ExceptionAnnotationTypeBinding.getInstance());
        
        annotationTypeMap.put(BasicTableAnnotationTypeBinding.name, BasicTableAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MatchInvalidTableAnnotationTypeBinding.name, MatchInvalidTableAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MatchValidTableAnnotationTypeBinding.name, MatchValidTableAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MsgTableAnnotationTypeBinding.name, MsgTableAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(RangeChkTableAnnotationTypeBinding.name, RangeChkTableAnnotationTypeBinding.getInstance());
                
        annotationTypeMap.put(PrintFormAnnotationTypeBinding.name, PrintFormAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(TextFormAnnotationTypeBinding.name, TextFormAnnotationTypeBinding.getInstance());
        
        annotationTypeMap.put(BasicProgramAnnotationTypeBinding.name, BasicProgramAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(TextUIProgramAnnotationTypeBinding.name, TextUIProgramAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(VGWebTransactionAnnotationTypeBinding.name, VGWebTransactionAnnotationTypeBinding.getInstance());
        
        annotationTypeMap.put(BasicLibraryAnnotationTypeBinding.name, BasicLibraryAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(NativeLibraryAnnotationTypeBinding.name, NativeLibraryAnnotationTypeBinding.getInstance());
        
        annotationTypeMap.put(JasperReportAnnotationTypeBinding.name, JasperReportAnnotationTypeBinding.getInstance());
        
        annotationTypeMap.put(BasicInterfaceAnnotationTypeBinding.name, BasicInterfaceAnnotationTypeBinding.getInstance());
        
        annotationTypeMap.put(ActionAnnotationTypeBinding.name, ActionAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(AliasAnnotationTypeBinding.name, AliasAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(AlignAnnotationTypeBinding.name, AlignAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(AllowAppendAnnotationTypeBinding.name, AllowAppendAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(AllowDeleteAnnotationTypeBinding.name, AllowDeleteAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(AllowInsertAnnotationTypeBinding.name, AllowInsertAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(AllowUnqualifiedItemReferencesAnnotationTypeBinding.name, AllowUnqualifiedItemReferencesAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(BindingAnnotationTypeBinding.name, BindingAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(BindingByNameAnnotationTypeBinding.name, BindingByNameAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(BypassValidationAnnotationTypeBinding.name, BypassValidationAnnotationTypeBinding.getInstance());        
        annotationTypeMap.put(CaseSensitiveAnnotationTypeBinding.name, CaseSensitiveAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ColorAnnotationTypeBinding.name, ColorAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ColumnAnnotationTypeBinding.name, ColumnAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ColumnsAnnotationTypeBinding.name, ColumnsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ContainerContextDependentAnnotationTypeBinding.name, ContainerContextDependentAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ContentsAnnotationTypeBinding.name, ContentsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(CurrencyAnnotationTypeBinding.name, CurrencyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(CurrencySymbolAnnotationTypeBinding.name, CurrencySymbolAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(CurrentArrayCountAnnotationTypeBinding.name, CurrentArrayCountAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(CursorAnnotationTypeBinding.name, CursorAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DataTypeAnnotationTypeBinding.name, DataTypeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DateFormatAnnotationTypeBinding.name, DateFormatAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DeleteAfterUseAnnotationTypeBinding.name, DeleteAfterUseAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DetectableAnnotationTypeBinding.name, DetectableAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DisplayNameAnnotationTypeBinding.name, DisplayNameAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DisplayOnlyAnnotationTypeBinding.name, DisplayOnlyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DisplayUseAnnotationTypeBinding.name, DisplayUseAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DLIAnnotationTypeBinding.name, DLIAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(DLIFieldNameAnnotationTypeBinding.name, DLIFieldNameAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(FieldLenAnnotationTypeBinding.name, FieldLenAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(FillAnnotationTypeBinding.name, FillAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(FillCharacterAnnotationTypeBinding.name, FillCharacterAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(FormSizeAnnotationTypeBinding.name, FormSizeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(HandleHardIOErrorsAnnotationTypeBinding.name, HandleHardIOErrorsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(HelpAnnotationTypeBinding.name, HelpAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(HelpGroupAnnotationTypeBinding.name, HelpGroupAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(HelpKeyAnnotationTypeBinding.name, HelpKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(HelpMsgKeyAnnotationTypeBinding.name, HelpMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(HighlightAnnotationTypeBinding.name, HighlightAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IncludeReferencedFunctionsAnnotationTypeBinding.name, IncludeReferencedFunctionsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IndexOrientationAnnotationTypeBinding.name, IndexOrientationAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(InitializedAnnotationTypeBinding.name, InitializedAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(InputRequiredAnnotationTypeBinding.name, InputRequiredAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(InputRequiredMsgKeyAnnotationTypeBinding.name, InputRequiredMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IntensityAnnotationTypeBinding.name, IntensityAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IsBooleanAnnotationTypeBinding.name, IsBooleanAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IsConstructAnnotationTypeBinding.name, IsConstructAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IsDecimalDigitAnnotationTypeBinding.name, IsDecimalDigitAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IsHexDigitAnnotationTypeBinding.name, IsHexDigitAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IsNullableAnnotationTypeBinding.name, IsNullableAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(IsReadOnlyAnnotationTypeBinding.name, IsReadOnlyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(LineWrapAnnotationTypeBinding.name, LineWrapAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(LinesBetweenRowsAnnotationTypeBinding.name, LinesBetweenRowsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(LinkParameterAnnotationTypeBinding.name, LinkParameterAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(LocalSQLScopeAnnotationTypeBinding.name, LocalSQLScopeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(LowerCaseAnnotationTypeBinding.name, LowerCaseAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MaskedAnnotationTypeBinding.name, MaskedAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MaxArrayCountAnnotationTypeBinding.name, MaxArrayCountAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MaxLenAnnotationTypeBinding.name, MaxLenAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MaxSizeAnnotationTypeBinding.name, MaxSizeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MinimumInputAnnotationTypeBinding.name, MinimumInputAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MinimumInputMsgKeyAnnotationTypeBinding.name, MinimumInputMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ModifiedAnnotationTypeBinding.name, ModifiedAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(MsgFieldAnnotationTypeBinding.name, MsgFieldAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(NeedsSOSIAnnotationTypeBinding.name, NeedsSOSIAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(NewWindowAnnotationTypeBinding.name, NewWindowAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(NumElementsItemAnnotationTypeBinding.name, NumElementsItemAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(NumericSeparatorAnnotationTypeBinding.name, NumericSeparatorAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(OrderingAnnotationTypeBinding.name, OrderingAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(OrientIndexAcrossAnnotationTypeBinding.name, OrientIndexAcrossAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(OutlineAnnotationTypeBinding.name, OutlineAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(PatternAnnotationTypeBinding.name, PatternAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(PCBAnnotationTypeBinding.name, PCBAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(PersistentAnnotationTypeBinding.name, PersistentAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(PFKeyEquateAnnotationTypeBinding.name, PFKeyEquateAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(PositionAnnotationTypeBinding.name, PositionAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(PrintFloatingAreaAnnotationTypeBinding.name, PrintFloatingAreaAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ProgramLinkDataAnnotationTypeBinding.name, ProgramLinkDataAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ProtectAnnotationTypeBinding.name, ProtectAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(RedefinesAnnotationTypeBinding.name, RedefinesAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(RelationshipAnnotationTypeBinding.name, RelationshipAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ResidentAnnotationTypeBinding.name, ResidentAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(RunValidatorFromProgramAnnotationTypeBinding.name, RunValidatorFromProgramAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ScreenFloatingAreaAnnotationTypeBinding.name, ScreenFloatingAreaAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SegmentsAnnotationTypeBinding.name, SegmentsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SelectFromListItemAnnotationTypeBinding.name, SelectFromListItemAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SelectTypeAnnotationTypeBinding.name, SelectTypeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SelectedIndexItemAnnotationTypeBinding.name, SelectedIndexItemAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ServicePropertyAnnotationTypeBinding.name, ServicePropertyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SetInitialAnnotationTypeBinding.name, SetInitialAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SharedAnnotationTypeBinding.name, SharedAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SignAnnotationTypeBinding.name, SignAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SpacesBetweenColumnsAnnotationTypeBinding.name, SpacesBetweenColumnsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SQLDataCodeAnnotationTypeBinding.name, SQLDataCodeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(SQLVariableLenAnnotationTypeBinding.name, SQLVariableLenAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ThrowNrfEofExceptionsAnnotationTypeBinding.name, ThrowNrfEofExceptionsAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(TimeFormatAnnotationTypeBinding.name, TimeFormatAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(TimeStampFormatAnnotationTypeBinding.name, TimeStampFormatAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(TypeChkMsgKeyAnnotationTypeBinding.name, TypeChkMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(UITypeAnnotationTypeBinding.name, UITypeAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(UpperCaseAnnotationTypeBinding.name, UpperCaseAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidValuesAnnotationTypeBinding.name, ValidValuesAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidValuesMsgKeyAnnotationTypeBinding.name, ValidValuesMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidationBypassKeysAnnotationTypeBinding.name, ValidationBypassKeysAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidationOrderAnnotationTypeBinding.name, ValidationOrderAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidatorDataTableAnnotationTypeBinding.name, ValidatorDataTableAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidatorDataTableMsgKeyAnnotationTypeBinding.name, ValidatorDataTableMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidatorFunctionAnnotationTypeBinding.name, ValidatorFunctionAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValidatorFunctionMsgKeyAnnotationTypeBinding.name, ValidatorFunctionMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ValueAnnotationTypeBinding.name, ValueAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(VerifyAnnotationTypeBinding.name, VerifyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(VerifyMsgKeyAnnotationTypeBinding.name, VerifyMsgKeyAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(XMLAnnotationTypeBinding.name, XMLAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(XSFacetAnnotationTypeBinding.name, XSFacetAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(XSPrimitiveAnnotationTypeBinding.name, XSPrimitiveAnnotationTypeBinding.getInstance());
        annotationTypeMap.put(ZeroFormatAnnotationTypeBinding.name, ZeroFormatAnnotationTypeBinding.getInstance());
*/
    }
    
    public void addSystemPackageRecord(FlexibleRecordBinding binding) {
    	systemPackageAnnotations.put(binding.getName(), binding);
    }
    
    /**
     * @deprecated This is part of a temporary solution to bypass the fact that the
     *             search mechanism does not return system parts. It should only be
     *             used by the class EGLPropertiesHandler or EGLNewPropertiesHandler. 
     */
    public Map getSystemPackageAnnotations() {
    	return systemPackageAnnotations;
    }
}

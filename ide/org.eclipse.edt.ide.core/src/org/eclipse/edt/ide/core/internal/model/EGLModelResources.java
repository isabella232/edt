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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.osgi.util.NLS;

public class EGLModelResources extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.core.internal.model.EGLModelResources"; //$NON-NLS-1$

	private EGLModelResources() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, EGLModelResources.class);
	}

	public static String elementDoesNotExist;
	public static String elementCannotReconcile;
	public static String elementReconciling;
	public static String elementInvalidType;
	public static String elementEGLpathCycle;
	public static String elementOnlyOneEGLModel;
	public static String elementProjectDoesNotExist;
	public static String elementInvalidResourceForProject;
	public static String elementNullName;
	public static String elementNullType;
	public static String elementIllegalParent;
	public static String elementNotPresent;

	public static String operationNeedElements;
	public static String operationNeedName;
	public static String operationNeedPath;
	public static String operationNeedAbsolutePath;
	public static String operationNeedString;
	public static String operationNotSupported;
	public static String operationCancelled;
	public static String operationNullContainer;
	public static String operationNullName;
	public static String operationCopyElementProgress;
	public static String operationMoveElementProgress;
	public static String operationRenameElementProgress;
	public static String operationCopyResourceProgress;
	public static String operationMoveResourceProgress;
	public static String operationRenameResourceProgress;
	public static String operationCreateUnitProgress;
	public static String operationCreateEGLFileProgress;
	public static String operationCreateFieldProgress;
	public static String operationCreateImportsProgress;
	public static String operationCreateInitializerProgress;
	public static String operationCreateMethodProgress;
	public static String operationCreatePackageProgress;
	public static String operationCreatePackageFragmentProgress;
	public static String operationCreateTypeProgress;
	public static String operationDeleteElementProgress;
	public static String operationDeleteResourceProgress;
	public static String operationCannotRenameDefaultPackage;
	public static String operationPathOutsideProject;
	public static String operationSortelements;

	public static String workingCopyCommit;

	public static String buildPreparingBuild;
	public static String buildReadStateProgress;
	public static String buildSaveStateProgress;
	public static String buildSaveStateComplete;
	public static String buildReadingDelta;
	public static String buildAnalyzingDeltas;
	public static String buildAnalyzingSources;
	public static String buildValidating;
	public static String buildGenerating;
	public static String buildFoundHeader;
	public static String buildFixedHeader;
	public static String buildOneError;
	public static String buildOneWarning;
	public static String buildMultipleErrors;
	public static String buildMultipleWarnings;
	public static String buildDone;
	public static String buildCleaningBuildState;

	public static String buildWrongFileFormat;
	public static String buildCannotSaveState;
	public static String buildCannotSaveStates;
	public static String buildInitializationError;
	public static String buildSerializationError;

	public static String buildDuplicateEGLPart;
	public static String buildDuplicateResource;
	public static String buildInconsistentEGLFile;
	public static String buildInconsistentProject;
	public static String buildIncompleteEGLpath;
	public static String buildMissingEGLFile;
	public static String buildPrereqProjectHasEGLpathProblems;
	public static String buildPrereqProjectMustBeRebuilt;
	public static String buildAbortDueToEGLpathProblems;

	public static String statusCannotUseDeviceOnPath;
	public static String statusCoreException;
	public static String statusDefaultPackeReadOnly;
	public static String statusEvaluationError;
	public static String statusIOException;
	public static String statusIndexOutOfBounds;
	public static String statusInvalidContents;
	public static String statusInvalidDestination;
	public static String statusInvalidName;
	public static String statusInvalidPackage;
	public static String statusInvalidPath;
	public static String statusInvalidProject;
	public static String statusInvalidResource;
	public static String statusInvalidResourceType;
	public static String statusInvalidSibling;
	public static String statusNameCollision;
	public static String statusNoLocalContents;
	public static String statusOK;
	public static String statusReadOnly;
	public static String statusTargetException;
	public static String statusUpdateConflict;

	public static String eglpathBuildPath;
	public static String eglpathCannotReadEGLpathFile;
	public static String eglpathCannotReferToItself;
	public static String eglpathClosedProject;
	public static String eglpathCouldNotWriteEGLpathFile;
	public static String eglpathCycle;
	public static String eglpathDuplicateEntryPath;
	public static String eglpathIllegalContainerPath;
	public static String eglpathIllegalEntryInEGLpathFile;
	public static String eglpathIllegalProjectPath;
	public static String eglpathIllegalSourceFolderPath;
	public static String eglpathIllegalEGLARPath;
	public static String eglpathIllegalVariablePath;
	public static String eglpathInvalidEGLpathInEGLpathFile;
	public static String eglpathInvalidContainer;
	public static String eglpathMustEndWithSlash;
	public static String eglpathUnboundContainerPath;
	public static String eglpathUnboundProject;
	public static String eglpathSettingProgress;
	public static String eglpathUnboundSourceFolder;
	public static String eglpathUnboundVariablePath;
	public static String eglpathUnknownKind;
	public static String eglpathXmlFormatError;
	public static String eglpathDisabledExclusionPatterns;
	public static String eglpathCannotNestOutputInOutput;
	public static String eglpathCannotNestEntryInEntry;
	public static String eglpathCannotNestEntryInLibrary;
	public static String eglpathCannotNestOutputInEntry;
	public static String eglpathCannotNestEntryInOutput;
	public static String eglpathCannotUseDistinctSourceFolderAsOutput;
	public static String eglpathCannotUseLibraryAsOutput;
	public static String eglpathDisabledMultipleOutputLocations;

	public static String eglPICannotReadEGLProjectInfoFile;
	public static String eglPIIllegalEntryInEGLProjectInfoFile;
	public static String eglPIXmlFormatError;
	
	public static String fileNotFound;
	public static String fileBadFormat;
	public static String variableBadFormat;
	public static String optionBadFormat;
	public static String pathNullPath;
	public static String pathMustBeAbsolute;
	public static String cacheInvalidLoadFactor;

	public static String codeAssistNullRequestor;

	public static String conventionUnitNullName;
	public static String conventionUnitNotEGLName;
	public static String conventionIllegalIdentifier;
	public static String conventionImportNullImport;
	public static String conventionImportUnqualifiedImport;
	public static String conventionTypeNullName;
	public static String conventionTypeNameWithBlanks;
	public static String conventionTypeDollarName;
	public static String conventionTypeLowercaseName;
	public static String conventionTypeInvalidName;
	public static String conventionPackageNullName;
	public static String conventionPackageEmptyName;
	public static String conventionPackageDotName;
	public static String conventionPackageNameWithBlanks;
	public static String conventionPackageConsecutiveDotsName;
	public static String conventionPackageUppercaseName;
	public static String conventionUnitNotIRName;
	
	public static String evalNeedBuiltState;
	
	public static String eglarNoSourceAttachmentContent;

}

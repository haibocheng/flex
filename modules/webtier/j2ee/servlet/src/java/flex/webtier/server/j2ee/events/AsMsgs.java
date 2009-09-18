////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2002-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.events;

public class AsMsgs
{
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kNoError = 0;
    public static final int kSyntaxError = 1000;
    public static final int kMalformedIncludeError = 1001;
    public static final int kUnterminatedStringError = 1002;
    public static final int kExpectHexError = 1003;
    public static final int kExpectOperandError = 1004;
    public static final int kUnsupportedJavaScriptError = 1005;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExpectLiteralError = 1006; // doesn't appear to be used anymore
    public static final int kExpectIdentifierError = 1007;
    public static final int kExpectFunctionNameError = 1008;
    public static final int kExpectParamNameError = 1009;
    public static final int kIllegalFunctionError = 1010;
    public static final int kExpectEventError = 1011;
    public static final int kExpectKeyCodeError = 1012;
    public static final int kBareElseError = 1013;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInvalidPropertyError = 1014;
    public static final int kExpectedOnError = 1015;
    public static final int kNestedFrameLoadedError = 1016;
    public static final int kFlash5RequiredError = 1017;
    public static final int kExprFlash5RequiredError = 1018;
    public static final int kFlash4RequiredError = 1019;
    public static final int kExprFlash4RequiredError = 1020;
    public static final int kStartDragParamError = 1021;
    public static final int kLockCenterError = 1022;
    public static final int kParamCount2Error = 1023;
    public static final int kNestedOnError = 1024;
    public static final int kInvalidLValueError = 1025;
    public static final int kUnterminatedCommentError = 1026;
    public static final int kMouseEventsForbiddenError = 1027;
    public static final int kClipEventsForbiddenError = 1028;
    public static final int kUnexpectedError = 1029;
    public static final int kUnterminatedBlockError = 1030;
    public static final int kUnterminatedInitError = 1031;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kParenError = 1032; // doesn't appear to be used anymore
    public static final int kExpectError = 1033;
    public static final int kExpect2Error = 1034;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kUnknownFunctionError = 1035; // doesn't appear to be used anymore
    public static final int kParamCountError = 1036;
    public static final int kPropertyExpectedError = 1037;
    public static final int kInternalError = 1038;
    public static final int kInvalidEventError = 1039;
    public static final int kInvalidKeycodeError = 1040;
    public static final int kDuplicateEventError = 1041;
    public static final int kDuplicateParameterError = 1042;
    public static final int kIncludeNotFoundError = 1043;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kDuplicateVariableError = 1044;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInvalidTargetError = 1045;  // doesn't appear to be used anymore
    public static final int kSceneTypeError = 1046;
    public static final int kInvalidMethodError = 1047;
    public static final int kInvalidBoundsTypeError = 1048;
    public static final int kFieldExpectedError = 1049;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kBracketMismatchError = 1050;  // doesn't appear to be used anymore
    public static final int kInvalidClipEventError = 1051;
    public static final int kTrailingGarbageError = 1052;
    public static final int kCaseOnlySupportedInsideSwitch = 1053;
    public static final int kMissingColonForCase = 1054;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kCodeBeneathBreakInCase = 1055;
    public static final int kCodeAboveFirstCaseIgnored = 1056;
    public static final int kEmptySwitch = 1057;
    public static final int kMultipleDefaultCases = 1090;
    public static final int kInitClipNotAllowedError = 1058;
    public static final int kFlash6RequiredError = 1059;
    public static final int kExprFlash6RequiredError = 1060;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInvalidVariableError = 1061;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kVariableRequiredError = 1062;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExpressionRequiredError = 1063;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExcessGotoError = 1064;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExcessElseError = 1065;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExcessElseIfError = 1066;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExcessTellTargetError = 1067;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInvalidFrameNumberError = 1068;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInvalidLevelNumberError = 1069;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kFunctionNameRequiredError = 1070;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInvalidFunctionNameError = 1071;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExcessIfFrameLoadedError = 1072;
// --Recycle Bin START (2/7/03 10:33 AM):
//    //#ifdef FEATURE_STICKY_ACTIONS_MODE
//    public static final int kExpertModeStuckError = 1073;
// --Recycle Bin STOP (2/7/03 10:33 AM)
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kNoScriptSelectedError = 1074;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExpertModeFormattingWarning = 1075;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kAutoFormatError = 1076;
// --Recycle Bin START (2/7/03 10:33 AM):
//    //#endif /* FEATURE_STICKY_ACTIONS_MODE */
//    public static final int kInvalidOrderError = 1077;
// --Recycle Bin STOP (2/7/03 10:33 AM)
    public static final int kMalformedInitClipError = 1078;
// --Recycle Bin START (2/7/03 10:33 AM):
//    //#ifdef FEATURE_INVOKE_ACTION
//    public static final int kMethodRequiredError = 1079;
// --Recycle Bin STOP (2/7/03 10:33 AM)
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInvalidMethodNameError = 1080;
// --Recycle Bin START (2/7/03 10:33 AM):
//    //#endif /* FEATURE_INVOKE_ACTION */
//    public static final int kCaseMismatchError = 1081;
// --Recycle Bin STOP (2/7/03 10:33 AM)
    public static final int kBuiltInCaseMismatchError = 1082;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kCheckSyntaxNoError = 1083;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kCheckSyntaxErrors = 1084;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kCheckSyntaxWarnings = 1085;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kSelectionCannotHaveActions = 1086;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kMaximumRecursionExceeded = 1087;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kWithStatementFailed = 1088;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kPrototypeChainTooLong = 1089;
    public static final int kNoEndInitClipError = 1090;
    public static final int kNoInitClipError = 1091;
    public static final int kInitClipNestedError = 1092;
    public static final int kExpectClassNameError = 1093;
    public static final int kExpectExtendsClassNameError = 1094;
    public static final int kInvalidAttributeUse = 1095;
    public static final int kDuplicateMemberName = 1096;
    public static final int kUnnamedMemberFunction = 1097;
    public static final int kNotAllowedInClassDefinition = 1099;
    public static final int kDuplicateClassNameError = 1100;
    public static final int kTypeMismatchError = 1101;
    public static final int kClassNotFoundError = 1102;
    public static final int kPropertyNotFoundError = 1103;
    public static final int kCallOfNonFunctionError = 1104;
    public static final int kAssignmentTypeMismatchError = 1105;
    public static final int kPrivateMemberAccessError = 1106;
    public static final int kNoVarsInInterfacesError = 1107;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kNoEventsInInterfacesError = 1108;
    public static final int kNoGetSetsInInterfacesError = 1109;
    public static final int kNoPrivatesInInterfacesError = 1110;
    public static final int kNoCodeInInterfacesError = 1111;
    public static final int kClassExtendsItselfError = 1112;
    public static final int kInterfaceExtendsItselfError = 1113;
    public static final int kInterfaceNotFoundError = 1114;
    public static final int kClassExtendsInterfaceError = 1115;
    public static final int kInterfaceExtendsClassError = 1116;
    public static final int kExpectImplementsNameError = 1117;
    public static final int kClassImplementsClassError = 1118;
    public static final int kMethodNotImplementedError = 1119;
    public static final int kImplementationNotMethodError = 1120;
    public static final int kDuplicateInterfaceError = 1121;
    public static final int kImplementationMismatchError = 1122;
    public static final int kActionScript1RequiredError = 1123;
    public static final int kActionScript2RequiredError = 1124;
    public static final int kNoStaticsInInterfacesError = 1125;
    public static final int kReturnTypeMismatchError = 1126;
    public static final int kNoReturnStatementError = 1127;
    public static final int kAttributeUsedOutsideClass = 1128;
    public static final int kReturnValueNotAllowedError = 1129;
    public static final int kExtendsAfterImplementsError = 1130;
    public static final int kExpectTypeIdentifierError = 1131;
    public static final int kInterfaceMustExtendError = 1132;
    public static final int kClassExtendsMultipleError = 1133;
    public static final int kInterfaceExtendsMultipleError = 1134;
    public static final int kMethodNotFoundError = 1135;
    public static final int kNotAllowedInInterfaceDefinition = 1136;
    public static final int kSetRequiresOneParameter = 1137;
    public static final int kGetRequiresNoParameters = 1138;
	public static final int kClassNotInPackageError       = 1139;
	public static final int kPackageRequiresOnlyClasses   = 1140;
	public static final int kInvalidPackageNameError      = 1141;
	public static final int kUnableToLoadPackage	       = 1142;
	public static final int kInterfaceNotInPackageError   = 1143;
	public static final int kInstanceVarInStaticError     = 1144;
	public static final int kClassInClassError	       = 1145;
	public static final int kInvalidStaticRefError       = 1146;
	public static final int kSuperMismatchError	       = 1147;
	public static final int kIllegalInterfaceAttribute    = 1148;
	public static final int kImportNotDirectiveError      = 1149;
	public static final int kFlash7RequiredError          = 1150;
	public static final int kExprFlash7RequiredError      = 1151;
	public static final int kImproperExceptionClauseError = 1152;
	public static final int kDuplicateConstructorError    = 1153;
	public static final int kReturnInConstructorError     = 1154;
	public static final int kReturnTypeInConstructorError = 1155;
	public static final int kVoidVariableError            = 1156;
	public static final int kVoidParameterError           = 1157;
	public static final int kStaticMemberWriteError       = 1158;
	public static final int kInterfaceOverloadError       = 1159;
	public static final int kSameClassNameError	       = 1160;
	public static final int kCantDeleteClassError	       = 1161;
	public static final int kPackageMemberError	       = 1162;
	public static final int kAS2KeywordError	       = 1163;
	public static final int kUnterminatedAttributeError   = 1164;
	public static final int kMultipleClassesDefinedError  = 1165;
	public static final int kIncorrectClassDefinedError   = 1166;
	public static final int kClassNameRequiredError       = 1167;
	public static final int kInvalidClassNameError	       = 1168;
	public static final int kInvalidInterfaceNameError    = 1169;
	public static final int kInvalidBaseClassNameError    = 1170;
	public static final int kInvalidBaseInterfaceNameError = 1171;
	public static final int kInterfaceNameRequiredError   = 1172;
	public static final int kClassIntfNameRequiredError   = 1173;
	public static final int kInvalidClassIntfNameError    = 1174;
    public static final int kNotAccessibleInScopeError    = 1175;
    public static final int kDuplicateAttributeError      = 1176;
    public static final int kInvalidClassAttributeUse     = 1177;
    public static final int kInstanceVarAssignStaticError = 1178;
    public static final int kRuntimeCircularitiesError    = 1179;
    //public static final int kDebuggingNotSupportedError   = 1180;
    public static final int kReleaseOutsideNotSupportedError = 1181;
    public static final int kDragOverNotSupportedError    = 1182;
    public static final int kDragOutNotSupportedError     = 1183;
    public static final int kDragNotSupportedError        = 1184;
    public static final int kLoadMovieNotSupportedError   = 1185;
    public static final int kGetURLNotSupportedError      = 1186;
    public static final int kFSCommandNotSupportedError   = 1187;
    public static final int kImportInClassError	          = 1188;
    public static final int kImportCollidesWithDefined    = 1189;
    public static final int kImportCollidesWithImport     = 1190;
    public static final int kInstanceVarAssignError       = 1191;
    public static final int kOverrideSuperclassConstructor = 1192;
    public static final int kInvalidPackageNameGeneric    = 1193;
	public static final int kSuperNotFirstError           = 1194;
	public static final int kBuiltInCaseMismatchWarning   = 1195;
	public static final int kClassFilenameMismatchWarning = 1196;
	public static final int kMisusedWildcardError         = 1197;
	public static final int kConstructorCaseWarning       = 1198;
	public static final int kForInTypeError               = 1199;
	public static final int kReturnInSetterError          = 1200;
	public static final int kConstructorAttributesError   = 1201;
    public static final int kNoTopLevelWarning            = 1202;
 	// --Recycle Bin (1/7/04 10:12 AM): public static final int kClassExceedsIfJumpLimit      = 1203;
	public static final int kClassNotFoundInPackage       = 1204;
	public static final int kIncompleteStatement          = 1205;
	public static final int kAmbigousClassReferenceError  = 1206;
    public static final int kMemberConflictsWithAncestorMember  = 1207;
    public static final int kFunctionConflictsWithAncestorFunction = 1208;
    public static final int kReturnTypesDiffer = 1209;
    public static final int kParameterTypesDiffer = 1210;
    public static final int kParameterNumbersDiffer = 1211;
	public static final int kDeprecatedWarning            = 1999;
	public static final int kDeprecatedWithSuggestionWarning = 2000;
	public static final int kInvalidEnumerationError		= 2001;
	public static final int kClassConflictsWithAncestorFunction = 2002;
	public static final int kClassConflictsWithAncestorMember = 2003;
    public static final int kFunctionConflictsWithClass = 2004;
    public static final int kMemberConflictsWithClass = 2005;
    public static final int kFunctionConflictsWithPackage = 2006;
    public static final int kMemberConflictsWithPackage = 2007;
	public static final int kImportConflictsWithFunction = 2008;
    public static final int kImportConflictsWithMember = 2009;
	public static final int kClassConflictsWithFunction = 2010;
	public static final int kClassConflictsWithMember = 2011;
	public static final int kBranchLimitExceeded = 2012;
	public static final int kIncludeReadFileError = 2013;
	public static final int kCatchExprNotValidError = 2014;
    public static final int kStyleError = 2015;
    public static final int kEventError = 2016;
    public static final int kEmbedConflictsWithValue = 2017;
    public static final int kCircularInheritanceError    = 2018;
	public static final int kNewInterfaceError = 2019;
    public static final int kInvalidImageWarning = 2020;

    // --Recycle Bin (2/7/03 10:33 AM): public static final int kIteratorField = 100;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kObjectField = 101;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kSceneField = 102;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kCommentField = 103;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kPathField = 104;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kVariablesField = 105;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kInitialExpressionField = 106;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kNextExpressionField = 107;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kExpressionField = 108;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kURLField = 109;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kWindowField = 110;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kTargetField = 111;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kCommandField = 112;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kArgumentsField = 113;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kLevelNumberField = 114;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kConditionField = 115;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kFrameField = 116;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kVariableNameField = 117;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kValueField = 118;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kDepthField = 119;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kTopField = 120;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kLeftField = 121;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kBottomField = 122;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kRightField = 123;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kMessageField = 124;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kNewNameField = 125;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kFunctionNameField = 126;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kFunctionParametersField = 127;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kNotSetYetField = 128;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kVariableField = 129;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kOrderField = 130;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kMethodField = 131;
    // --Recycle Bin (2/7/03 10:33 AM): public static final int kFunctionField = 132;
	// --Recycle Bin (2/7/03 10:33 AM): public static final int kClassNameField		       = 133;
	// --Recycle Bin (2/7/03 10:33 AM): public static final int kInterfaceNameField	       = 134;
	// --Recycle Bin (2/7/03 10:33 AM): public static final int kImportNameField	       = 135;


// --Recycle Bin START (2/7/03 10:33 AM):
//	// Output Window info display flags
//	public static final int kOutputNone = 500;
// --Recycle Bin STOP (2/7/03 10:33 AM)
	// --Recycle Bin (2/7/03 10:33 AM): public static final int kOutputErrors = 501;
	// --Recycle Bin (2/7/03 10:33 AM): public static final int kOutputWarnings = 502;
	// --Recycle Bin (2/7/03 10:33 AM): public static final int kOutputVerbose = 503;
	// --Recycle Bin (2/7/03 10:33 AM): public static final int kOutputDebug = 504;

// --Recycle Bin START (2/7/03 10:31 AM):
//    public String getErrorMessage(int errorID)
//    {
//        return AsMsgsInternal.getErrorMessage(errorID);
//    }
// --Recycle Bin STOP (2/7/03 10:31 AM)

// --Recycle Bin START (2/7/03 10:32 AM):
//    public String getFieldText(int fieldID)
//    {
//        return AsMsgsInternal.getFieldText(fieldID);
//    }
// --Recycle Bin STOP (2/7/03 10:32 AM)

// --Recycle Bin START (2/7/03 10:31 AM):
//    public String convertNewlinesInText(String str)
//    {
//        return AsMsgsInternal.convertNewlinesInText(str);
//    }
// --Recycle Bin STOP (2/7/03 10:31 AM)
}

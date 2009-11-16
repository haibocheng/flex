
*INFO

 dependencychecker is a Java program used to analyze the dependencies of a SWC 
 to make sure classes aren't bringing in too many other classes.  It does this 
 through a series of rules defined for each SWC.  

 For framework.swc, there's a 
 rule set defined in FrameworkSwcDependencyRules.java.  These rules are just used 
 as a guideline to determine what packages a class can bring in.  For instance, 
 a class in the mx.component package can only be dependent on classes in the 
 mx.manager category, the mx.component category, etc...  Classes in the mx.component 
 package cannot be dependent on classes in the mx.skin package.

 However, there are exceptions to these general rules.  These exceptions are 
 listed in a third file.  See exceptionsListExample.txt as a sample.  The 
 list of framework.swc exceptions is frameworkSwcExceptionsList.txt.

*FIXING FAILURES

 If dependency checker is failing, you'll see an error message, like:

 dependencychecker-framework:     [java] mx.graphics.shaderClasses:ColorBurnShader is imported into mx.core:UIComponent     [java] mx.graphics.shaderClasses:ColorDodgeShader is imported into mx.core:UIComponent     [java] mx.graphics.shaderClasses:ColorShader is imported into mx.core:UIComponent     [java] mx.graphics.shaderClasses:ExclusionShader is imported into mx.core:UIComponent     [java] mx.graphics.shaderClasses:HueShader is imported into mx.core:UIComponent     [java] mx.graphics.shaderClasses:LuminosityShader is imported into mx.core:UIComponent     [java] mx.graphics.shaderClasses:SaturationShader is imported into mx.core:UIComponent     [java] mx.graphics.shaderClasses:SoftLightShader is imported into mx.core:UIComponent 

 Your options to fix this are:

 1) Remove those dependencies because they shouldn't be allowed
 2) Add a new rule in FrameworkSwcDependencyRules.java
 3) Add a new exception to frameworkSwcExceptionsList.txt by copying the error messages 
    you got in to that file.
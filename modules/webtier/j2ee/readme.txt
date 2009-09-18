This readme.txt file describes the Flex compiler module for J2EE application servers.


*******************************
*   Introduction              *
*******************************

The Flex compiler module for J2EE application servers is a Web Application aRchive (WAR) file that you can deploy 
to most J2EE application servers that support servlets. This lets you rapidly compile, 
test, and deploy an application: Instead of compiling your MXML file into a SWF file and 
then deploying it and its wrapper files on a web server, you can just refresh the MXML 
file in your browser.

The Flex compiler module for J2EE application servers also provides a JSP tag library 
that lets you write Flex applications in JSPs.

The Flex compiler module for J2EE application is not for production use. It is strongly recommended that the Flex compiler 
module web application not be made available on a public-facing web server.


************************************
*  License and Third-Party Notices *
************************************

License...

Mozilla Public License Files:

The contents of the files contained in Flex compiler module for J2EE application servers are subject to the Mozilla Public License Version 1.1 (the "License"); you may not use these files except in compliance with the License. You may obtain a copy of the License here: Mozilla Public License Version 1.1 or http://www.mozilla.org/MPL/.

NOTE: The following files are not covered under the MPL license: jsp-api.jar and servlet-api.jar, which are governed by the Sun Microsystems, Inc. Binary Code License Agreement

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License.

The Original Code consists of the files listed in http://opensource.adobe.com/svn/opensource/flex/sdk/trunk/modules/webtier.

The Initial Developer of the Original Code is Adobe Systems Incorporated.
By downloading, modifying, distributing, using and/or accessing any files in this directory, you agree to the terms and conditions of the applicable end user license agreement.

Third-party Notices...

NOTICES RELATED TO CERTAIN THIRD PARTY MATERIALS:

OpenSymphony Group
This product includes software developed by the OpenSymphony Group (http://www.opensymphony.com/). 

Copyright © 2001-2004 The OpenSymphony Group. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

3. The end-user documentation included with the redistribution, if any, must include the following acknowledgment: "This product includes software developed by the
 OpenSymphony Group (http://www.opensymphony.com/)." Alternately, this acknowledgment may appear in the software itself, if and wherever such third-party acknowledgments normally appear.

4. The names "OpenSymphony" and "The OpenSymphony Group" must not be used to endorse or promote products derived from this software without prior written permission. For written permission, please contact license@opensymphony.com .

5. Products derived from this software may not be called "OpenSymphony" or "OsCore", nor may "OpenSymphony" or "OsCore" appear in their name, without prior written permission of the OpenSymphony Group.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*******************************
*   Requirements              *
*******************************

The Flex compiler module for J2EE application servers comes as a WAR file. You can deploy 
it to any of the following J2EE web application servers:
 * Adobe JRun 4 Updater 7
 * Apache Tomcat 6.0.x
 * IBM WebSphere 6.1.x € IBM 1.5
 * BEA WebLogic 10 € Sun 1.5
 * JBoss 4.2.x

 
*******************************
*   Installation              *
*******************************

To install the Flex compiler module for J2EE application servers, create an application root 
directory and extract the contents of the WAR file into that directory. Your application server
might have a facility for deploying WAR files that you should use. After you deploy the WAR file,
restart your application server.

For example, on Tomcat, create a new directory under the {tomcat_install_dir}/webapps directory 
and expand the WAR file into that new directory. Save your MXML and JSP files in that directory.


*******************************
*   Configuration             *
*******************************

The Flex compiler module for J2EE application servers uses the following configuration files:

WEB-INF/flex-config.xml 
Defines the default settings for the Flex compiler.

WEB-INF/flex-webtier-config.xml 
Defines settings specific to the web tier, such as logging, caching, and debugging.

WEB-INF/web.xml 
Defines the web component settings for the Flex web application, such as servlet definitions 
and mappings, and tag library definitions.

To use custom SWC files or ActionScript classes in your applications, add them to the 
WEB-INF/flex/user-classes directory. 

If you want to use data visualization components in your Flex applications, add the appropriate 
SWC files to the WEB-INF/flex/libs directory.

****************************************************************
*   Integrating with LiveCycle Data Services ES 2.6 or BlazeDS *
****************************************************************

LiveCycle Data Services ES 2.6 and BlazeDS do not include an integrated web tier compiler, but you can easily integrate the Flex compiler module for J2EE into a LiveCycle Data Services ES or BlazeDS web application.

   1. Unzip the webtier.war file to a directory called webtier.
   2. Copy all of the files in the webtier/WEB-INF/lib to to the corresponding WEB-INF/lib directory of your LiveCycle Data Service ES web application. Do not overwrite any existing files.
   3. Make a backup copy of the services-config.xml file WEB-INF/flex directory of your LiveCycle Data Service ES web application.
   3. With the exception of the services-config.xml file, copy all of the files and directories in the webtier/WEB-INF/flex directory to the corresponding WEB-INF/flex directory of your LiveCycle Data Service ES web application. Do not overwrite any existing files. In the event that you accidentally overwrite the services-config.xml file in your web application, restore it using the backup copy you created.
   4. Copy the player and locale directories as well as the datavisualization.swc file from the LiveCycle Data Services ES or BlazeDS install_root/resources/frameworks/libs directory to the WEB-INF/flex/libs directory of your LiveCycle Data Service ES web application.
   5. From the following web.xml sample content, add the text contained between
   <!-- start from webtier web.xml -->
   and
   <!-- end from webtier web.xml -->
   to the web.xml file in the WEB-INF directory of your LiveCycle Data Service ES or BlazeDS web application:

<?xml version="1.0" encoding="UTF-8"?>
 <!DOCTYPE web-app PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN" "http://java.sun.com/dtd/web-app_2_3.dtd">

<web-app>
   <display-name>LiveCycle Data Services Samples</display-name>
   <description>LiveCycle Data Services Application with Samples</description>
   
   <!-- start from webtier web.xml -->
 <context-param>
 <param-name>flex.class.path</param-name>
 <param-value>/WEB-INF/flex/hotfixes,/WEB-INF/flex/jars</param-value>
 </context-param>
 <!-- end from webtier web.xml --> 

 <!-- Http Flex Session attribute and binding listener support -->
   <listener>
   <listener-class>flex.messaging.HttpFlexSession</listener-class>
   </listener>

 <!-- MessageBroker Servlet -->
   <servlet>
   <servlet-name>MessageBrokerServlet</servlet-name>
   <display-name>MessageBrokerServlet</display-name>
   <servlet-class>flex.messaging.MessageBrokerServlet</servlet-class>
   <init-param>
   <param-name>services.configuration.file</param-name>
   <param-value>/WEB-INF/flex/services-config.xml</param-value>
   </init-param>
   <load-on-startup>1</load-on-startup>
   </servlet>
   
   <servlet>
   <servlet-name>PDFResourceServlet</servlet-name>
   <display-name>Helper for retrieving dynamically generated PDF documents.</display-name>
   <servlet-class>flex.samples.pdfgen.PDFResourceServlet</servlet-class>
   </servlet>
   
   <!-- start from webtier web.xml -->
 <servlet>
 <servlet-name>FlexMxmlServlet</servlet-name>
 <display-name>MXML Processor</display-name>
 <description>Servlet wrapper for the Mxml Compiler</description>
 <servlet-class>flex.bootstrap.BootstrapServlet</servlet-class>
 <init-param>
 <param-name>servlet.class</param-name>
 <param-value>flex.webtier.server.j2ee.MxmlServlet</param-value>
 </init-param>
 <init-param>
 <param-name>webtier.configuration.file</param-name>
 <param-value>/WEB-INF/flex/flex-webtier-config.xml</param-value>
 </init-param>
 <load-on-startup>1</load-on-startup>
 </servlet>

 <servlet>
 <servlet-name>FlexSwfServlet</servlet-name>
 <display-name>SWF Retriever</display-name>
 <servlet-class>flex.bootstrap.BootstrapServlet</servlet-class>
 <init-param>
 <param-name>servlet.class</param-name>
 <param-value>flex.webtier.server.j2ee.SwfServlet</param-value>
 </init-param>
 <!-- SwfServlet must be initialized after MxmlServlet -->
 <load-on-startup>2</load-on-startup>
 </servlet>

 <servlet>
 <servlet-name>FlexForbiddenServlet</servlet-name>
 <display-name>Prevents access to *.as/*.swc files</display-name>
 <servlet-class>flex.bootstrap.BootstrapServlet</servlet-class>
 <init-param>
 <param-name>servlet.class</param-name>
 <param-value>flex.webtier.server.j2ee.ForbiddenServlet</param-value>
 </init-param>
 </servlet>

 <servlet>
 <servlet-name>FlexInternalServlet</servlet-name>
 <servlet-class>flex.bootstrap.BootstrapServlet</servlet-class>
 <init-param>
 <param-name>servlet.class</param-name>
 <param-value>flex.webtier.server.j2ee.filemanager.FileManagerServlet</param-value>
 </init-param>
 <load-on-startup>10</load-on-startup>
 </servlet>
 <!-- end from webtier web.xml --> 
   
   <servlet-mapping>
   <servlet-name>MessageBrokerServlet</servlet-name>
   <url-pattern>/messagebroker/*</url-pattern>
   </servlet-mapping>

 <servlet-mapping>
   <servlet-name>PDFResourceServlet</servlet-name>
   <url-pattern>/dynamic-pdf/*</url-pattern>
   </servlet-mapping>

<!-- start from webtier web.xml --> 

 <servlet-mapping>
 <servlet-name>FlexMxmlServlet</servlet-name>
 <url-pattern>*.mxml</url-pattern>
 </servlet-mapping>

 <servlet-mapping>
 <servlet-name>FlexSwfServlet</servlet-name>
 <url-pattern>*.swf</url-pattern>
 </servlet-mapping>

 <servlet-mapping>
 <servlet-name>FlexForbiddenServlet</servlet-name>
 <url-pattern>*.as</url-pattern>
 </servlet-mapping>

 <servlet-mapping>
 <servlet-name>FlexForbiddenServlet</servlet-name>
 <url-pattern>*.swc</url-pattern>
 </servlet-mapping>

 <servlet-mapping>
 <servlet-name>FlexInternalServlet</servlet-name>
 <url-pattern>/flex-internal/*</url-pattern>
 </servlet-mapping>   
 <!-- end from webtier web.xml --> 
<welcome-file-list>
   <welcome-file>index.html</welcome-file>
   <welcome-file>index.htm</welcome-file>
   </welcome-file-list>
<!-- for WebSphere deployment, please uncomment -->
   <!--
   <resource-ref>
   <description>Flex Messaging WorkManager</description>
   <res-ref-name>wm/MessagingWorkManager</res-ref-name>
   <res-type>com.ibm.websphere.asynchbeans.WorkManager</res-type>
   <res-auth>Container</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
   </resource-ref>
   -->

 <!--
   <security-constraint>
   <web-resource-collection>
   <web-resource-name>Protected Sample</web-resource-name>

 <url-pattern>/messagebroker/amf/SampleSalaryRO</url-pattern>
   <http-method>GET</http-method>
   <http-method>POST</http-method>
   </web-resource-collection>

 <auth-constraint>
   <role-name>sampleusers</role-name>
   </auth-constraint>
   </security-constraint>
   -->

 <login-config>
   <auth-method>BASIC</auth-method>
   </login-config>

 <!--
   <security-role>
   <role-name>sampleusers</role-name>
   </security-role>
   -->
   
   <!-- start from webtier web.xml --> 
 <taglib>
 <taglib-uri>FlexTagLib</taglib-uri>
 <taglib-location>/WEB-INF/lib/flex-bootstrap-jsp.jar</taglib-location>
 </taglib>
 <!-- end from webtier web.xml -->
</web-app>


*******************************
*   About the JSP Tag Library *
*******************************

The Flex Tag Library for JSP is a set of JSP tags that you use to embed Flex applications 
in a JSP page. You should use the Flex Tag Library for JSP to do any of the following:
 * Include an MXML (Flex) application in an existing HTML/JSP page.
 * Include history management, player detection, or Express Install for one application but 
 not another application.
 * Present an MXML application if the correct version of the Adobe Flash Player is installed, 
 or an HTML version otherwise.
 * Present one of a few versions of the same basic application determined through logic in a 
 JSP page. 

The <mxml> tag compiles the MXML code, if required, and then generates the HTML fragment to 
load Adobe Flash Player and the resulting SWF file in your JSP page. You can do this in two 
ways. The source tag attribute lets you specify the source file to compile. This is useful 
because the JSP tag writes the HTML fragment for you. The other approach is to specify the 
source as the body content of the tag. Then, you use JSP scriptlets to generate the MXML source.

The <flashvar> tag lets you pass variables to a Flex application. You access these variables 
by using the Application.application.parameters object.

Although the Flex Tag Library for JSP is similar to the tag library in earlier versions of 
Flex, there are some differences. The tag attributes of the <mxml> tag are simpler; full 
control of all the HTML wrapper attributes is no longer available. However, the tag library 
includes attributes that enable history management and Flash Player detection on a per-tag basis. 
The <param> and <flash> tags were eliminated to simplify tag usage. 


*******************************
*  Using the JSP Tag Library  *
*******************************

To use the Flex Tag Library in a JSP page, add the following tag library declaration 
to your JSP page:

<%@ taglib uri="FlexTagLib" prefix="mm" %>

As with any JSP taglib directive, this line must appear before you use any tags in the 
Flex JSP tag library.

You can then use the <mxml> tag to insert a Flex application in the JSP page. You can 
either reference a separate MXML file to be included in the JSP page, or you can add the MXML 
syntax directly in the JSP page.

The following example shows the <mxml> tag with the source attribute to include an external 
MXML file:

<mm:mxml source="CustomerServiceChat.mxml"/>

Alternatively, you can use the <mxml> tag with inline MXML source code, as the following 
example shows:

<mm:mxml>
    <mx:Application xmlns:mx="http://www.adobe.com/2006/mxml" xmlns="*">
        <mx:Text label="Hello World">
    </mx:Application>
</mm:mxml>

Use the <mxml> tag carefully, as any JSP execution that results in different MXML source 
code causes recompilation. Each distinct instance of MXML source code is cached separately 
and checked for recompilation separately.

For example, the following JSP code results in two sets of MXML source code; one is 
created when request.isUserInRole("admin") is true, and another is created when 
request.isUserInRole("admin") is false:

<mm:mxml>
    <mx:Application xmlns:mx="http://www.adobe.com/2006/mxml" xmlns="*">
        <% if (request.isUserInRole("admin")) { %>
            <AdminConsole/>
        <% } else { %>
            <UserConsole/>
        <% } %>
    </mx:Application>
</mm:mxml>

Updating the <flashvar> tag does not cause a recompilation. 

You can mix JSP scriptlets with MXML to create a dynamic application. The following example 
uses <mxml> and <flashvar> tags with inline MXML code to get the Java version and current date:

<%@ taglib uri="FlexTagLib" prefix="mm" %>
<mm:mxml height="300" width="600" usePlayerDetection="true" useExpressInstall="false">
    <mm:flashvar name="javaVersion" value='<%= System.getProperty("java.version") %>'/>
    <mm:flashvar name="currentDate" value="<%= new java.util.Date().toString() %>"/>
    <mx:Application xmlns:mx="http://www.adobe.com/2006/mxml" width="400" height="300">
        <mx:VBox>
            <mx:HBox>
                <mx:Label text="Java version: "/>
                <mx:Label text="{Application.application.parameters.javaVersion}" fontWeight="bold"/>
            </mx:HBox>
            <mx:HBox>
                <mx:Label text="Current Time: "/>
                <mx:Label text="{Application.application.parameters.currentDate}" fontWeight="bold"/>
            </mx:HBox>
        </mx:VBox>
    </mx:Application>
</mm:mxml>

The following example shows how to invoke an external MXML file while passing dynamic 
flashvar variables to it:

<%@ taglib uri="FlexTagLib" prefix="mm" %>
<mm:mxml source="flashvarTest.mxml" width="400" height="200">
    <mm:flashvar name="javaVersion" value='<%= System.getProperty("java.version") %>'/>
    <mm:flashvar name="currentDate" value="<%= new java.util.Date().toString() %>"/>
</mm:mxml>

You can use the <mxml> tag attributes to enforce the use of a minimum Flash Player version. 
You can return one version of a Flex application when the required version of Flash Player 
is available, or an alternate version of the application when the required Player is unavailable. 
You can also use the <mxml> tag to set up Express Install so that clients that do not have 
the minimum required Player are prompted to install it.

You set the minimum required version in the <flash-player> in the flex-webtier-config.xml 
file.

The following example enables Flash Player Detection but disables Express Install. With this 
configuration, the server returns the alternateContentPage instead of proceeding to upgrade 
Flash Player through Express Install if the client does not have the minimum required Player 
version.

<mm:mxml 
    source="FlexApplication.mxml" 
    usePlayerDetection="true" 
    useExpressInstall="false" alternateContentPage="MyLegacyApplication.html"
/>


*******************************
*   Tag Descriptions          *
*******************************

<mxml>    
Compiles the MXML code and generates the HTML wrapper. Includes MXML source as body content, 
or specify an external source file with the source attribute.  

The <mxml> tag has the following attributes:
 
 * source - Location of the MXML code to compile.
 
 * id  - Name used to expose the SWF file through the id or name attribute of the HTML object 
 or embed tag.
 
 * height - Height of the MXML wrapper.
 
 * width -  Width of the MXML wrapper.
 
 * useHistoryManagement - Include history management for the Flex application.
 
 * usePlayerDetection - Include Flash Player version detection for the Flex application. The 
 minimum required version is set in the flex-webtier-config.xml file.
 
 * useExpressInstall - Install Flash Player through Express Install; ignored if Flash Player 
 version detection is disabled.
 
 * alternateContentPage - Present this alternate page when the Flash Player version is unavailable; 
 ignored if Flash Player version detection is disabled.


<flashvar>    
Specifies a variable to pass to your Flex application.  

The <flashvar> tag has the following attributes:
 
 * name -   Specifies the name of the flashvar variable.
 
 * value -  Specifies the value of the flashvar variable.




Using Flex SDK Eclipse Projects
===============================

The SDK svn repository also includes sample Eclipse projects to assist testing,
debugging and modifying the framework and compiler source. The SDK framework
swcs are represented Flex Library projects, and the various modules of the SDK
compiler are represented as Java projects.

Note that these projects require Flex Builder 3 / Eclipse 3.3 (or later). The
Java projects require JDK 1.5.0. If Eclipse is used, the framework projects
require Flex Builder 3 plugin.

Before importing any projects into a workspace, you must sync the SDK svn
repository and check that ant builds successfully. You must also define a few
workspace variables so that relative paths resolve to your sync of the SDK.

Note that the framework projects do not rely on the compiler module projects,
and the compiler module projects do not rely on the framework projects.
However, within each type of project, there may be dependencies (such as the
rpc project depends on the framework project and so both must be imported in
order to use the rpc project). The two types of projects are separated in svn
as follows:

development/eclipse/flex
development/eclipse/java


Framework Flex Library projects
-------------------------------

Before you import the framework Flex Library projects, you must create a Linked
Resource Variable named FLEX_SDK (that points to the root of your sync of the
SDK from svn). Go to the Window menu and select Preferences..., navigate to
General > Workspace > Linked Resources, then click the New... button to create
a Linked Resource Variable.
e.g.

c:/dev/svn/flex/sdk/trunk
FLEX_SDK

To use these projects in Gumbo, you need to also create a Flex SDK named Flex 4
that should point to your the root of your sync of the SDK in svn. Go to the
Window menu and select Preferences..., navigate to Flex > Installed Flex SDKs,
then click Add... to add a new SDK.
e.g.

Flex 4
c:/dev/svn/flex/sdk/trunk


Compiler Java projects
----------------------

Before you import the compiler module Java projects, you must create a Linked
Resource Variables named FLEX_SDK (that points to the root of your sync of the
SDK from svn). Go to the Window menu and select Preferences..., navigate to
General > Workspace > Linked Resources, then click the New... button to create
a Linked Resource Variable.
e.g.

c:/dev/svn/flex/sdk/trunk
FLEX_SDK

You must also create two Java Classpath Variables named FLEX_SDK (that points
to the root folder of your sync of the SDK from svn) and ANT17_JAR (that
points to the ant.jar file from your installation of ANT 1.7.0). Go to the
Window menu and select Preferences..., navigate to Java > Build Path >
Classpath Variables, then click the New... button to create a new Classpath
Variable.
e.g.

c:/dev/svn/flex/sdk/trunk
FLEX_SDK

c:/dev/ant1.7.0/lib/ant.jar
ANT17_JAR


Importing projects
------------------
Once you have setup your Eclipse workspace, all variables and run ant
successfully to build the SDK, you can import the SDK projects.

Go to the File menu and select Import..., navigate to General > Existing
Projects into Workspace, and then click the Browse... button to navigate to
your sync of the /development/eclipse subdirectory of the SDK from svn. You may
include a subset of the projects if you are familiar with the dependencies
between them.

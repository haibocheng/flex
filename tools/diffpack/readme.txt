
*INFO

 diffpack is a script for packaging changed and base files in folder structure
 which is suitable input for many third-party diffing applications.
 Primary reason for DiffPack is to facilitate code reviews since it packages
 base files along with the changed files.

 diffpack consists of two parts - the script and an Adobe AIR app for the
 GUI part, where users can select files.

*PREREQUISITES

1. You need some sort of a diffing application installed. We have tested
   diffpack with Araxis and WinMerge on Windows and Araxis on Mac. It should
   be easy to configure other apps as well (step 3 in the installation, or
   run 'diffpack setup' from the console) 

2. If you're using cygwin make sure that the cygwin has the ‘zip’ and ‘unzip’
   commands. If you’re missing those, you’ll need to install the additional
   utilities from the optional cygwin archive package www.cygwin.com/setup.exe
   
3. If you're using cygwin make sure you have the "diff" utility. You can install
   if from the cygwin util package www.cygwin.com/setup.exe

4. Make sure you have command-line svn client installed and added to the path.
   Verify by running 'svn info' in your console. If you don't have svn, you
   can install it from here
   http://subversion.tigris.org/files/documents/15/39559/svn-1.4.5-setup.exe

*INSTALLATION

1. Install the 'diffpack.air' application. You can find the binary in the project,
   the project is checked in the flex/sdk/trunk/tools/diffpack/ folder.
   If you change the default installation folder you will need to edit
   the diffpack script and update the AIRApp variable at the top of the file!

2. Copy the 'diffpack' script from flex/sdk/trunk/tools/diffpack folder to your
   bin folder (or some other folder that's on your path)

3. Set up the diffing appliaction. From the console run 'diffpack', it will
   run the GUI client with the focus set on the Settings tab.
   
   [DEFAULT]
   If you have WinMerge or Araxis (Windows/Mac) installed in their default
   installation folders, the settings tab will be pre-populated with those
   choices and the appropriate command-line arguments.

   [CUSTOM]
   Note that Araxis (Compare.exe) needs the "-wait" argument and
   WinMerge.exe needs "/r" argument if you are setting up those manually.

   If you are specifying some other diffing application, you can put whatever
   arguments you need or leave the fields empty.
   
   After you edit your settings, hit the "Save Settings" button to save those.

   [TEST SETTINGS]
   To test your saved settings click the "Test Saved Settings" button. This will
   close the GUI app, run the diffing application on a specially generated 
   temp file/folder structure to let you how viewing changes would look. 
   After you close the diffing app, the GUI will come back and you can make
   more modifications if necessary.
   
   Hit "quit" when done.

*USAGE

 ! Always run the script and don't runt the app directly.
 ! Make sure you're running the script from a svn managed folder.
  
 - 'diffpack help' prints help.

 - 'diffpack' with no arguments will start the GUI where users can select
   files to package or preview.

 - 'diffpack pack YourPackagePath'
   This will run the AIR component to show you the list of modified files
   in the current folder or its sub-folders (hint - multiple select works).
   Click 'package' and the AIR app will close and the script will create
   the package.
   
 - 'diffpack diff YourPackagePath'
   will unzip the package in a temp folder and diff the
   'base' and 'changes' subfolders with your diff app. Close the diff app
   to allow the script to remove the temp folder that contains the unzipped
   files. 

 - 'diffpack diff YourPackagePath1 YourPackagePath2'
   will unzip both packages in a temp folder and diff the 'changes'
   subfolders. Especially useful for iterative code-reviews.
   
 - 'diffpack setup' starts the GUI to allow users to configure the
   diffing applicatoins diffpack will use to view packages. 

*PACKAGE CONTENTS EXAMPLE

   If you have some files modified say:

   cygdrive/c/src/svnRoot/opensource/flex/button.as
   cygdrive/c/src/svnRoot/opensource/flex/buttonSkin.as
   
   and you run pack like this:

   cygdrive/c/src/svnRoot/opensource/@diffpack pack myFile

   you will be prompted to select the files in the AIR GUI app,
   after clicking 'package' the script will create a single file
   
   cygdrive/c/src/svnRoot/opensource/myFile.zip
   
   with the following contents:
   
   /myFile
          + root.txt               - this contains the svn root URL
          |
          + myFile.patch           - this file is the svn diff file which you can apply
          |
          + base
          |     + svnRoot
          |              + opensource
          |                          + flex
          |                                + button.as       - base version of the file
          |                                + buttonSkin.as   - base version of the file
          + changes
                + svnRoot
                         + opensource
                                     + flex
                                           + button.as       - your changed version of the file
                                           + buttonSkin.as   - your changed version of the file
                                           
   Now you can email/share myFile.zip for other to look at/apply.

   When you use 'diffpack diff myFile' it will unzip the contents in a temp folder
   and call your diff app with 'base' and 'changes' as arguments. It will
   remove the folder as soon as you close the diff app.
                                       

*BUILDING/UPDATING

 When you make significant enough changes, please update this file (including the history) and
 also export a new release build in the diffpack project root folder.

*HISTORY

1.3.1
- Fix: the preview and package buttons don't close the air component after preview if the package path is empty.
- Fix: spaces in the package file name break the script.
- Fix: closing the air app after running the script outside of svn controlled folder causes infinite loop in the script.

1.3
- Added the command for creating patches to the settings tab.
- Package file path text field editable
- Fixes to the package command to always warn you if the file already exists
- Preview doesn't lose the package path anymore

1.2
- fixed .patch creation to create patch with the selected files only, remove, fix newlines in the patch file.

1.1
- fixed .patch creation to bypass svn configured diffing apps and use the diff utility instead.
- window does paint correctly on startup on mac.
- choice 1 in settings tab is now populated from the saved settings
- minor UI fixes

1.0
- added setup and storing settings in a separate file to facilitate updates.
- fixed support for paths with spaces (i.e diffpack pack "c:/my documents/egeorgie/package")

0.9
- fixed to handle relative paths in the arguments
- fixed to create and include a .patch file in the package
- added readme.txt

0.8
- initial version of diffpack
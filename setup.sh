################################################################################
##
##  ADOBE SYSTEMS INCORPORATED
##  Copyright 2008 Adobe Systems Incorporated
##  All Rights Reserved.
##
##  NOTICE: Adobe permits you to use, modify, and distribute this file
##  in accordance with the terms of the license agreement accompanying it.
##
################################################################################

#
# This shell script sets the environment variables JAVA_HOME, ANT_HOME,
# and PATH appropriately for building, testing, and developing
# with this branch of the Flex SDK.
#
# It ensures that the right version of Java and Ant are used
# and that when you invoke a Flex SDK tool such as mxmlc,
# the right copy of that tool is executed.
#
# Usage:
#   cd <some_branch_of_the_Flex_SDK>
#   source setup.sh [-q]
#
# -q will disable any output from the script.
#
# It assumes that Java and Ant are installed at standard locations
# in your system. If you have placed them elsewhere, edit the paths.
#
# Note that you must run this script with the 'source' command.
#
# After running it, you can execute commands such as
#   ant -q main checkintests
# and
#   mxmlc MyApp.mxml
#

# IMPORTANT: If you update this script, be sure to update README.txt

# Which platform are we on?
case `uname` in
    CYGWIN*)
        OS="Windows"
    ;;
    Linux*)
        OS="Linux"
    ;;
    *)
        OS="Unix"
esac

# quiet
if [ "$1" = "-q" ]; then
    # alias to noop in bash
    alias echo=': '
fi

# Remember what the PATH is before we prepend any branch-specific directories,
# so that when we switch to another branch we can start over.
if [ "$ORIG_PATH" = "" ]; then
    ORIG_PATH="$PATH"
fi

#Which version of the player is being used?
PLAYER_PATH=in/player/10

# Use the right version of Java and Ant to build this branch
if [ $OS = "Windows" ]; then
    ANT_HOME="/cygdrive/c/apache-ant-1.7.0"
    JAVA_HOME="/cygdrive/c/Program Files/Java/jdk1.5.0_13"

# This was tested on Ubuntu
elif [ $OS = "Linux" ]; then
    ANT_HOME=/usr/share/ant
    JAVA_HOME=`readlink /etc/alternatives/javac | sed -n 's/[\\\/]bin[\\\/]javac$//p'`

    # Don't unpack the player every time, unless it's been updated
    find $PLAYER_PATH/lnx/ -name flashplayer ! -cnewer $PLAYER_PATH/lnx/flashplayer.tar.gz -exec rm {} \;
    if [ ! -f "$PLAYER_PATH/lnx/flashplayer" ]; then
        echo "setup.sh: Unpacking player to $PLAYER_PATH/lnx/flashplayer"
        tar zxf $PLAYER_PATH/lnx/flashplayer.tar.gz 
        mv flashplayer $PLAYER_PATH/lnx/flashplayer
    fi

    if [ ! -z "$JAVA_HOME" -a ! -f "$JAVA_HOME/lib/tools.jar" ]; then
        echo "setup.sh: $JAVA_HOME is not a Java SDK, maybe it's just the run time. Set JAVA_HOME and try again."
    fi

# Mac
elif [ $OS = "Unix" ]; then
    ANT_HOME=~/bin/apache-ant-1.7.0
    JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home"
fi

export ANT_HOME
export JAVA_HOME

echo "setup.sh: Setting default  ANT_HOME=$ANT_HOME"
echo "setup.sh: Setting default JAVA_HOME=$JAVA_HOME"

# Ensure that this branches' tools (such as bin/mxmlc) are found on the PATH.
PATH="`pwd`/bin:$ANT_HOME/bin:$JAVA_HOME/bin:$ORIG_PATH"

# Test that each path exists
if [ ! -d "$JAVA_HOME" ]; then
    echo "setup.sh: setup.sh: WARNING: JAVA_HOME does not exist:"
    echo "    $JAVA_HOME"
fi
if [ ! -d "$ANT_HOME" ]; then
    echo "setup.sh: WARNING: ANT_HOME does not exist:"
    echo "    $ANT_HOME"
fi
if [ ! -f `pwd`/bin/mxmlc ]; then
    echo "setup.sh: WARNING: mxmlc was not found (did your \`source setup.sh\` outside the branch's root?):"
    echo "    `pwd`/bin/mxmlc"
fi

# Enable Java assertions for development in mxmlc, compc, and fcsh
# (only works when using bin/mxmlc on Mac/Linux/Cygwin, not mxmlc.exe)
export SETUP_SH_VMARGS="-ea"

# re-enable echo
if [ "$1" = "-q" ]; then
    unalias echo
fi


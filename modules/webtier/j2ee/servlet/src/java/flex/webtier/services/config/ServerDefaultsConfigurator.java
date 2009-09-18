////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

import flex.webtier.util.J2EEUtil;
import flex2.compiler.config.ConfigurationBuffer;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.common.DefaultsConfigurator;

import java.util.LinkedList;
import javax.servlet.ServletContext;

/**
 * This class makes the default values go in through the configuration buffer instead of just the
 * value set on the actual object.  This is useful for dependency checking and also for reporting
 * the source location of a particular configuration value.
 */
public class ServerDefaultsConfigurator
{
    private static void set( ConfigurationBuffer cfgbuf, String var, String val ) throws ConfigurationException
    {
        LinkedList args = new LinkedList();
        args.add( val );
        cfgbuf.setVar( var, args, "defaults", -1 );
    }

    private static void set( ConfigurationBuffer cfgbuf, String[] vars, String val ) throws ConfigurationException
    {
        for (int i = 0; i < vars.length; ++i)
        {
            set( cfgbuf, vars[i], val );
        }
    }

    public static void loadDefaults( ConfigurationBuffer cfgbuf, ServletContext servletContext ) throws ConfigurationException
    {
        Class flexConfig = cfgbuf.getChildConfigClass( "flex-config" );
        ConfigurationBuffer fcbuf = new ConfigurationBuffer( flexConfig );
        fcbuf.setDefaultVar("flex-specs");
        DefaultsConfigurator.loadDefaults(fcbuf);
        cfgbuf.mergeChild( "flex-config", fcbuf );

        set( cfgbuf, "production-mode", "true" );

        // must set debugging defaults to be sure that production-mode is fully processed
        set( cfgbuf, "debugging.process-debug-query-params", "false");
        set( cfgbuf, "debugging.generate-profile-swfs", "false");
        set( cfgbuf, "debugging.keep-generated-swfs", "false");
        set( cfgbuf, "debugging.show-all-warnings", "false");
        set( cfgbuf, "debugging.show-override-warnings", "false");
        set( cfgbuf, "debugging.show-stacktraces-in-browser", "false");
        set( cfgbuf, "debugging.create-compile-report", "false");
        set( cfgbuf, "debugging.show-source-in-compiler-errors", "false");
        set( cfgbuf, "debugging.log-compiler-errors", "false");
        
        // set the default service config file path
        set( cfgbuf, "flex-config.compiler.services", J2EEUtil.getRealPath("/WEB-INF/flex/services-config.xml", servletContext) );
    }

}


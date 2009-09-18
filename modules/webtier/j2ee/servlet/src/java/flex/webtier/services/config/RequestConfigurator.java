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

import flex2.compiler.config.ConfigurationBuffer;
import flex2.compiler.config.ConfigurationException;
import javax.servlet.http.HttpServletRequest;

class RequestConfigurator
{

    public static String ACCESSIBLE_QUERY = "accessible";
    public static String DEBUG_QUERY = "debug";
    public static String PROFILE_QUERY = "asprofile";
    public static String SHOW_ALL_WARNINGS_QUERY = "showAllWarnings";
    public static String SHOW_BINDING_WARNINGS_QUERY = "showBindingWarnings";
    public static String VERBOSE_STACKTRACES_QUERY = "verboseStacktraces";

    public static String URL_SOURCE = "url query";
    public static String ACCESSIBLE_VAR = "flex-config.compiler.accessible";
    public static String DEBUG_VAR = "flex-config.compiler.debug";
    public static String PROFILE_VAR = "flex-config.compiler.profile";
    public static String SHOW_ALL_WARNINGS_VAR = "debugging.show-all-warnings";
    public static String SHOW_BINDING_WARNINGS_VAR = "flex-config.compiler.show-binding-warnings";
    public static String VERBOSE_STACKTRACES_VAR = "flex-config.compiler.verbose-stacktraces";
    public static String CONTEXT_ROOT_VAR = "flex-config.compiler.context-root";

    public void parse(ConfigurationBuffer cfg, ServerConfiguration defaultConfig, HttpServletRequest request) throws ConfigurationException
    {
        String contextPath = request.getContextPath();
        cfg.setVar(CONTEXT_ROOT_VAR, contextPath, URL_SOURCE, -1);

        String accessibleOverride = request.getParameter(ACCESSIBLE_QUERY);
        if (accessibleOverride != null)
        {
            cfg.setVar(ACCESSIBLE_VAR, accessibleOverride, URL_SOURCE, -1);
        }

        boolean productionMode = defaultConfig.isProductionMode();
        boolean processDebugQueryParams = defaultConfig.getDebuggingConfiguration().processDebugQueryParams();
        if (!productionMode && processDebugQueryParams)
        {
            String debugOverride = request.getParameter(DEBUG_QUERY);
            if ((debugOverride != null) &&
                (("true".equalsIgnoreCase(debugOverride)) || "false".equalsIgnoreCase(debugOverride)))
            {
                cfg.setVar(DEBUG_VAR, debugOverride, URL_SOURCE, -1);
            }

            String profileOverride = request.getParameter(PROFILE_QUERY);
            if ((profileOverride != null) &&
                (("true".equalsIgnoreCase(profileOverride)) || "false".equalsIgnoreCase(profileOverride)))
            {
                cfg.setVar(PROFILE_VAR, profileOverride, URL_SOURCE, -1);
            }

            String showAllWarningsOverride = request.getParameter(SHOW_ALL_WARNINGS_QUERY);
            if ((showAllWarningsOverride != null) &&
                ("true".equalsIgnoreCase(showAllWarningsOverride) || "false".equalsIgnoreCase(showAllWarningsOverride)))
            {
                cfg.setVar(SHOW_ALL_WARNINGS_VAR, showAllWarningsOverride, URL_SOURCE, -1);
            }

            String showBindingWarningsOverride = request.getParameter(SHOW_BINDING_WARNINGS_QUERY);
            if ((showBindingWarningsOverride != null) &&
                ("true".equalsIgnoreCase(showBindingWarningsOverride) || "false".equalsIgnoreCase(showBindingWarningsOverride)))
            {
                cfg.setVar(SHOW_BINDING_WARNINGS_VAR, showBindingWarningsOverride, URL_SOURCE, -1);
            }

            String verboseStacktracesOverride = request.getParameter(VERBOSE_STACKTRACES_QUERY);
            if ((verboseStacktracesOverride != null) &&
                (("true".equalsIgnoreCase(verboseStacktracesOverride)) || "false".equalsIgnoreCase(verboseStacktracesOverride)))
            {
                cfg.setVar(VERBOSE_STACKTRACES_VAR, verboseStacktracesOverride, URL_SOURCE, -1);
            }
        }
    }
}

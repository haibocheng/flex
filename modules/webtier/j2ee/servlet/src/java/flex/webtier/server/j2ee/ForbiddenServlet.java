////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a utility servlet that can be used to return a 403 Forbidden response for stuff
 * we don't want exposed via the browser, like ActionScript source files.
 *
 * @author Paul Reilly
 */

public class ForbiddenServlet extends HttpServlet
{
    static final long serialVersionUID = 2018768697233079778L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}

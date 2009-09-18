////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.swf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adobe.internal.fxg.dom.AbstractFXGNode;

import flash.swf.SwfConstants;
import flash.swf.types.CurvedEdgeRecord;
import flash.swf.types.LineStyle;
import flash.swf.types.Rect;
import flash.swf.types.ShapeRecord;
import flash.swf.types.StraightEdgeRecord;
import flash.swf.types.StyleChangeRecord;

/**
 * A collection of utilities to help create SWF Shapes and ShapeRecords.
 * 
 * @author Peter Farland
 * @author Sujata Das
 */
public class ShapeHelper implements SwfConstants
{
    /**
     * Creates a List of ShapeRecord to draw a line from the given
     * origin (startX, startY) to the specified coordinates (in pixels).
     * 
     * @param startX The origin x coordinate in pixels.
     * @param startY The origin y coordinate in pixels.
     * @param endX The end x coordinate in pixels.
     * @param endY The end y coordinate in pixels.
     * @return list of ShapeRecords representing the rectangle.
     */
    public static List<ShapeRecord> line(double startX, double startY, double endX, double endY)
    {
        List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();
        shapeRecords.add(move(startX, startY));
        shapeRecords.add(straightEdge(startX, startY, endX, endY));
        return shapeRecords;
    }


    /**
     * Creates a List of ShapeRecord to draw a line that represents an implicit closepath
     * origin (startX, startY) to the specified coordinates (in pixels). 
     * 
     * @param startX The origin x coordinate in pixels.
     * @param startY The origin y coordinate in pixels.
     * @param endX The end x coordinate in pixels.
     * @param endY The end y coordinate in pixels.
     * @return list of ShapeRecords representing the rectangle.
     */
    public static List<ShapeRecord> implicitClosepath(double startX, double startY, double endX, double endY)
    {
        List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();
        StyleChangeRecord scr = new StyleChangeRecord();
        scr.setMove((int)startX*TWIPS_PER_PIXEL, (int)startY*TWIPS_PER_PIXEL);
        scr.setLinestyle(0);
        shapeRecords.add(scr);
        shapeRecords.add(straightEdge(startX, startY, endX, endY));
        return shapeRecords;
    }
    
    /**
     * Creates a List of ShapeRecord to draw a rectangle from the given
     * origin (startX, startY) for the specified width and height (in pixels).
     * 
     * @param startX The origin x coordinate in pixels.
     * @param startY The origin y coordinate in pixels.
     * @param width The rectangle width in pixels.
     * @param height The rectangle width in pixels.
     * @return list of ShapeRecords representing the rectangle.
     */
    public static List<ShapeRecord> rectangle(double startX, double startY, double width, double height)
    {
        List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();
        shapeRecords.add(move(startX, startY));
        shapeRecords.add(straightEdge(startX, startY, width, startY));
        shapeRecords.add(straightEdge(width, startY, width, height));
        shapeRecords.add(straightEdge(width, height, startX, height));
        shapeRecords.add(straightEdge(startX, height, startX, startY));
        return shapeRecords;
    }

    /**
     * Creates a List of ShapeRecord to draw a rectangle from the given
     * origin (startX, startY) for the specified width and height (in pixels)
     * and radiusX and radiusY for rounded corners.
     * 
     * @param startX The origin x coordinate in pixels.
     * @param startY The origin y coordinate in pixels.
     * @param width The rectangle width in pixels.
     * @param height The rectangle width in pixels.
     * @param radiusX The radiusX for rounded corner in pixels
     * @param radiusY The radius for rounded corner in pixels
     * @return list of ShapeRecords representing the rectangle.
     */
    public static List<ShapeRecord> rectangle(double startx, double starty, 
    		double width, double height, double radiusX, double radiusY, 
    		double topLeftRadiusX, double topLeftRadiusY, double topRightRadiusX, 
    		double topRightRadiusY, double bottomLeftRadiusX, double bottomLeftRadiusY,
    		double bottomRightRadiusX, double bottomRightRadiusY)
    {
    
        List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();
        
        if (radiusX == 0.0) 
        {
            radiusY = radiusX = 0;
         }
        else if (radiusY == 0.0)
        {
            radiusY = radiusX;
        }
        
        if ( radiusX > width/2.0 )
            radiusX = width/2.0;
        if ( radiusY > height/2.0 )
            radiusY = height/2.0;          

        double[] topLeftRadius = getCornerRadius(topLeftRadiusX, topLeftRadiusY, radiusX, radiusY, width, height);
        topLeftRadiusX = topLeftRadius[0];
        topLeftRadiusY = topLeftRadius[1];
        
        double[] topRightRadius = getCornerRadius(topRightRadiusX, topRightRadiusY, radiusX, radiusY, width, height);
        topRightRadiusX = topRightRadius[0];
        topRightRadiusY = topRightRadius[1];

        double[] bottomLeftRadius = getCornerRadius(bottomLeftRadiusX, bottomLeftRadiusY, radiusX, radiusY, width, height);
        bottomLeftRadiusX = bottomLeftRadius[0];
        bottomLeftRadiusY = bottomLeftRadius[1];
        
        double[] bottomRightRadius = getCornerRadius(bottomRightRadiusX, bottomRightRadiusY, radiusX, radiusY, width, height);
        bottomRightRadiusX = bottomRightRadius[0];
        bottomRightRadiusY = bottomRightRadius[1];
 
        double c0 = 0.923879532511;
        double c1 = 0.382683432365;
        double c3 = 0.707106781187;
  
        double rx = bottomRightRadiusX;
        double ry = bottomRightRadiusY;
                
        double tx = rx / 0.923879532511;
        double ty = ry / 0.923879532511;

        double dx, currentx;
        double dy, currenty;

        dx = startx + width - rx;
        dy = starty + height - ry;
        shapeRecords.add(move( (dx + rx), dy ));
        currentx = (dx + rx);
        currenty = dy;
        if ( bottomRightRadiusX != 0.0 ) 
        {
            shapeRecords.add(curvedEdge(currentx, currenty, (dx + c0 * tx), (dy + c1 * ty), (dx + c3 * rx), (dy + c3 * ry) ));
            shapeRecords.add(curvedEdge((dx + c3 * rx), (dy + c3 * ry), (dx + c1 * tx), (dy + c0 * ty), dx, (dy + ry)) );
            currentx = dx;
            currenty = dy + ry;
        }
        
        rx = bottomLeftRadiusX;
        ry = bottomLeftRadiusY;
        tx = rx / 0.923879532511;
        ty = ry / 0.923879532511;
        dx = startx + rx;
        dy = starty + height - ry;
        shapeRecords.add(straightEdge(currentx, currenty, dx, (dy + ry) ));
        currentx = dx;
        currenty = dy + ry;
        if ( bottomLeftRadiusX != 0.0 ) 
        {
            shapeRecords.add(curvedEdge(currentx, currenty, (dx - c1 * tx), (dy + c0 * ty), (dx - c3 * rx), (dy + c3 * ry) ));
            shapeRecords.add(curvedEdge((dx - c3 * rx), (dy + c3 * ry), (dx - c0 * tx), (dy + c1 * ty), (dx - rx), dy ));
            currentx = dx - rx;
            currenty = dy;
        }
        
        rx = topLeftRadiusX;
        ry = topLeftRadiusY;
        tx = rx / 0.923879532511;
        ty = ry / 0.923879532511;
        dx = startx + rx;
        dy = starty + ry;
        shapeRecords.add(straightEdge(currentx, currenty, (dx - rx), dy ));
        currentx = dx - rx;
        currenty = dy;
        if ( topLeftRadiusX != 0.0 ) 
        {
            shapeRecords.add(curvedEdge(currentx, currenty, (dx - c0 * tx), (dy - c1 * ty), (dx - c3 * rx), (dy - c3 * ry) ));
            shapeRecords.add(curvedEdge((dx - c3 * rx), (dy - c3 * ry), (dx - c1 * tx), (dy - c0 * ty), dx, (dy - ry) ));
            currentx = dx;
            currenty = dy - ry;
        }
        
        rx = topRightRadiusX;
        ry = topRightRadiusY;
        tx = rx / 0.923879532511;
        ty = ry / 0.923879532511;
        dx = startx + width - rx;
        dy = starty + ry;
        shapeRecords.add(straightEdge(currentx, currenty, dx, (dy - ry) ));
        currentx = dx;
        currenty = dy - ry;
        if ( topRightRadiusX != 0.0 ) 
        {
            shapeRecords.add(curvedEdge(currentx, currenty, (dx + c1 * tx), (dy - c0 * ty), (dx + c3 * rx), (dy - c3 * ry) ));
            shapeRecords.add(curvedEdge((dx + c3 * rx), (dy - c3 * ry), (dx + c0 * tx), (dy - c1 * ty), (dx + rx), dy ));
            currentx = (dx + rx);
            currenty = dy;
        }
        
        rx = bottomRightRadiusX;
        ry = bottomRightRadiusY;
        tx = rx / 0.923879532511;
        ty = ry / 0.923879532511;
        dx = startx + width - rx;
        dy = starty + height - ry;
        shapeRecords.add(straightEdge(currentx, currenty, (dx + rx), dy ));

        return shapeRecords;
        
    }

    /**
     * Creates a List of ShapeRecord to draw a rectangle from the
     * origin (0.0, 0.0) for the specified width and height (in pixels).
     * 
     * @param width The rectangle width in pixels.
     * @param height The rectangle width in pixels.
     * @return list of ShapeRecords representing the rectangle.
     */
    public static List<ShapeRecord> rectangle(double width, double height)
    {
        return rectangle(0.0, 0.0, width, height);
    }
    
    /**
     * Sets the style information for the first StyleChangeRecord in a list
     * of ShapeRecords.
     * 
     * @param shapeRecords
     * @param lineStyleIndex The ShapeWithStyle LineStyle index (starting at 1)
     * or 0 if none.
     * @param fillStyle0Index The ShapeWithStyle FillStyle index (starting at 1)
     * or 0 if none.
     * @param fillStyle1Index The ShapeWithStyle FillStyle index (starting at 1)
     * or 0 if none. 
     */
    public static void setStyles(List<ShapeRecord> shapeRecords,
            int lineStyleIndex, int fillStyle0Index, int fillStyle1Index)
    {
        if (shapeRecords != null && shapeRecords.size() > 0)
        {
            ShapeRecord firstRecord = shapeRecords.get(0);
            if (firstRecord instanceof StyleChangeRecord)
            {
                StyleChangeRecord scr = (StyleChangeRecord)firstRecord;

                if (fillStyle0Index > 0)
                    scr.setFillStyle0(fillStyle0Index);

                if (fillStyle1Index > 0)
                    scr.setFillStyle1(fillStyle1Index);

                if (lineStyleIndex > 0)
                    scr.setLinestyle(lineStyleIndex);
            }
        }
    }


    /**
     * Sets the style information for the all the StyleChangeRecords in a list
     * of ShapeRecords.
     * 
     * @param shapeRecords
     * @param lineStyleIndex The ShapeWithStyle LineStyle index (starting at 1)
     * or 0 if none.
     * @param fillStyle0Index The ShapeWithStyle FillStyle index (starting at 1)
     * or 0 if none.
     * @param fillStyle1Index The ShapeWithStyle FillStyle index (starting at 1)
     * or 0 if none. 
     */
    public static void setPathStyles(List<ShapeRecord> shapeRecords,
            int lineStyleIndex, int fillStyle0Index, int fillStyle1Index)
    {

        if (shapeRecords != null && shapeRecords.size() > 0)
        {
            for (int i = 0; i < shapeRecords.size(); i++)
            {
                ShapeRecord record = shapeRecords.get(i);
                if (record instanceof StyleChangeRecord)
                {
                    StyleChangeRecord scr = (StyleChangeRecord)record;

                    if (fillStyle0Index > 0)
                        scr.setFillStyle0(fillStyle0Index);

                    if (fillStyle1Index > 0)
                        scr.setFillStyle1(fillStyle1Index);

                    if ((!scr.stateLineStyle) && (lineStyleIndex > 0))
                        scr.setLinestyle(lineStyleIndex);
                    
                }
            }
        }
    }

    /**
     * Creates a StyleChangeRecord to represent a move command without changing
     * style information. All coordinates are to be specified in pixels and will
     * be converted to twips.
     * 
     * @param x The x coordinate in pixels.
     * @param y The y coordinate in pixels.
     * @return StyleChangeRecord recording the move and styles. 
     */
    public static StyleChangeRecord move(double x, double y)
    {
        x *= TWIPS_PER_PIXEL;
        y *= TWIPS_PER_PIXEL;

        int moveX = (int)x;
        int moveY = (int)y;

        StyleChangeRecord scr = new StyleChangeRecord();
        
        scr.setMove(moveX, moveY);

        return scr;
    }

    /**
     * Creates a StraightEdgeRecord to represent a line as the delta between
     * the pair of coordinates (xFrom,yFrom) and (xTo,yTo). All coordinates
     * are to be specified in pixels and will be converted to twips.
     * 
     * @param xFrom The start x coordinate in pixels.
     * @param yFrom The start y coordinate in pixels.
     * @param xTo The end x coordinate in pixels.
     * @param yTo The end y coordinate in pixels.
     * @return StraightEdgeRecord representing a line. 
     */
    public static StraightEdgeRecord straightEdge(double xFrom, double yFrom, double xTo, double yTo)
    {
        xFrom *= TWIPS_PER_PIXEL;
        yFrom *= TWIPS_PER_PIXEL;
        xTo *= TWIPS_PER_PIXEL;
        yTo *= TWIPS_PER_PIXEL;

        int dx = (int)xTo - (int)xFrom;
        int dy = (int)yTo - (int)yFrom;

        StraightEdgeRecord ser = new StraightEdgeRecord(dx, dy);
        return ser;
    }

    /**
     * Creates a CurvedEdgeRecord to represent a quadratic curve by calculating
     * the deltas between the start coordinates and the control point
     * coordinates, and between the control point coordinates and the anchor
     * coordinates. All coordinates are to be specified in pixels and will be
     * converted to twips.
     * 
     * @param xFrom The start x coordinate in pixels.
     * @param yFrom The start y coordinate in pixels.
     * @param controlX The control point x coordinate in pixels.
     * @param controlY The control point y coordinate in pixels.
     * @param anchorX The anchor x coordinate in pixels.
     * @param anchorY The anchor y coordinate in pixels.
     * @return CurvedEdgeRecord representing a quadratic curve.
     */
    public static CurvedEdgeRecord curvedEdge(double startX, double startY,
            double controlX, double controlY, double anchorX, double anchorY)
    {
        startX *= TWIPS_PER_PIXEL;
        startY *= TWIPS_PER_PIXEL;
        controlX *= TWIPS_PER_PIXEL;
        controlY *= TWIPS_PER_PIXEL;
        anchorX *= TWIPS_PER_PIXEL;
        anchorY *= TWIPS_PER_PIXEL;

        int dcx = (int)controlX - (int)startX;
        int dcy = (int)controlY - (int)startY;
        int dax = (int)anchorX - (int)controlX;
        int day = (int)anchorY - (int)controlY;

        CurvedEdgeRecord cer = new CurvedEdgeRecord();
        cer.controlDeltaX = dcx;
        cer.controlDeltaY = dcy;
        cer.anchorDeltaX = dax;
        cer.anchorDeltaY = day;
        return cer;
    }

    /**
     * Approximates a cubic Bezier as a series of 4 quadratic CurvedEdgeRecord
     * with the method outlined by Timothee Groleau in ActionScript (which was
     * based on Helen Triolo's approach of using Casteljau's approximation).
     * 
     * Using a fixed level of 4 quadratic curves should be a fast way of
     * achieving a reasonable approximation of the original curve.
     * 
     * All coordinates are to be specified in pixels and will be converted to
     * twips.
     * 
     * @param startX The start x coordinate in pixels.
     * @param startY The start y coordinate in pixels.
     * @param control1X The first control point x coordinate in pixels.
     * @param control1Y The first control point y coordinate in pixels.
     * @param control2X The second control point x coordinate in pixels.
     * @param control2Y The second control point y coordinate in pixels.
     * @param anchorX The anchor x coordinate in pixels.
     * @param anchorY The anchor y coordinate in pixels.
     * @return a List of 4 CurvedEdgeRecords approximating the cubic Bezier.
     * 
     * @link http://timotheegroleau.com/Flash/articles/cubic_bezier_in_flash.htm
     */
    public static List<ShapeRecord> cubicToQuadratic(final double startX, final double startY,
            final double control1X, final double control1Y,
            final double control2X, final double control2Y,
            final double anchorX, final double anchorY)
    {
        // First, calculate useful base points
        double ratio = 3.0 / 4.0;
        double pax = startX + ((control1X - startX) * ratio);
        double pay = startY + ((control1Y - startY) * ratio);
        double pbx = anchorX + ((control2X - anchorX) * ratio);
        double pby = anchorY + ((control2Y - anchorY) * ratio);

        // Get 1/16 of the [anchor, start] segment
        double dx = (anchorX - startX) / 16.0;
        double dy = (anchorY - startY) / 16.0;

        // Calculate control point 1
        ratio = 3.0 / 8.0;
        double c1x = startX + ((control1X - startX) * ratio);
        double c1y = startY + ((control1Y - startY) * ratio);

        // Calculate control point 2
        double c2x = pax + ((pbx - pax) * ratio);
        double c2y = pay + ((pby - pay) * ratio);
        c2x = c2x - dx;
        c2y = c2y - dy;

        // Calculate control point 3
        double c3x = pbx + ((pax - pbx) * ratio);
        double c3y = pby + ((pay - pby) * ratio);
        c3x = c3x + dx;
        c3y = c3y + dy;

        // Calculate control point 4
        double c4x = anchorX + ((control2X - anchorX) * ratio);
        double c4y = anchorY + ((control2Y - anchorY) * ratio);

        // Calculate the 3 anchor points (as midpoints of the control segments)
        double a1x = (c1x + c2x) / 2.0;
        double a1y = (c1y + c2y) / 2.0;

        double a2x = (pax + pbx) / 2.0;
        double a2y = (pay + pby) / 2.0;

        double a3x = (c3x + c4x) / 2.0;
        double a3y = (c3y + c4y) / 2.0;

        // Create the four quadratic sub-segments
        List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>(4);
        shapeRecords.add(curvedEdge(startX, startY, c1x, c1y, a1x, a1y));
        shapeRecords.add(curvedEdge(a1x, a1y, c2x, c2y, a2x, a2y));
        shapeRecords.add(curvedEdge(a2x, a2y, c3x, c3y, a3x, a3y));
        shapeRecords.add(curvedEdge(a3x, a3y, c4x, c4y, anchorX, anchorY));
        return shapeRecords;
    }

    /**
     * Note this utility was ported to Java from the ActionScript class
     * 'flex.graphics.Path' - specifically its 'data' property setter function.
     * 
     * @param path The path to populate.
     * @param data A condensed String representation of a path data.
     */
    public static List<ShapeRecord> path(String data, boolean fill)
    {
        List<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();

        if (data.length() == 0)
            return shapeRecords;
               
        // Split letter followed by number (i.e. "M3" becomes "M 3")
        String temp = data.replaceAll("([A-Za-z])([0-9\\-\\.])", "$1 $2");

        // Split number followed by letter (i.e. "3M" becomes "3 M")
        temp = temp.replaceAll("([0-9\\.])([A-Za-z\\-])", "$1 $2");

        // Split letter followed by letter (i.e. "zM" becomes "z M")
        temp = temp.replaceAll("([A-Za-z\\-])([A-Za-z\\-])", "$1 $2");

        //support scientific notation for floats/doubles
        temp = temp.replaceAll("([0-9])( )([eE])( )([0-9\\-])", "$1$3$5");
 
        // Replace commas with spaces
        temp = temp.replace(',', ' ');

        // Trim leading and trailing spaces
        temp = temp.trim();

        // Finally, split the string into an array 
        String[] args = temp.split("\\s+");
        
        char ic = 0;
        char prevIc = 0;
        double lastMoveX = 0.0;
        double lastMoveY = 0.0;
        double prevX = 0.0;
        double prevY = 0.0;
        double x = 0.0;
        double y = 0.0;
        double controlX = 0.0;
        double controlY = 0.0;
        double control2X = 0.0;
        double control2Y = 0.0;
        boolean firstMove = true;
       
        for (int i = 0; i < args.length; )
        {
            boolean relative = false;
            char c = args[i].toCharArray()[0];
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')
            {
                ic = c;
                i++;
            }

            switch (ic)
            {
                case 'm':
                    relative = true;
                case 'M':
                     if (firstMove) {
                        x = Double.parseDouble(args[i++]);
                        y = Double.parseDouble(args[i++]);
                        shapeRecords.add(move(x, y));
                        firstMove = false;
                    }
                    else 
                    {
                        //add an implicit closepath, if needed
                        if (fill && (Math.abs(prevX-lastMoveX) > AbstractFXGNode.EPSILON || Math.abs(prevY-lastMoveY) > AbstractFXGNode.EPSILON)) 
                        {
                           shapeRecords.addAll(implicitClosepath(prevX, prevY, lastMoveX, lastMoveY));
                        }
                        x = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                        y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                        shapeRecords.add(move(x, y));
                    }
                    lastMoveX = x;
                    lastMoveY = y;
                    ic = (relative) ? 'l' : 'L';
                    break;

                case 'l':
                    relative = true;
                case 'L':
                    x = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    shapeRecords.add(straightEdge(prevX, prevY, x, y));
                    break;

                case 'h':
                    relative = true;
                case 'H':
                    x = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    y = prevY;
                    shapeRecords.add(straightEdge(prevX, prevY, x, y));
                    break;

                case 'v':
                    relative = true;
                case 'V':
                    x = prevX;
                    y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    shapeRecords.add(straightEdge(prevX, prevY, x, y));
                    break;

                case 'q':
                    relative = true;
                case 'Q':
                    controlX = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    controlY = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    x = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    shapeRecords.add(curvedEdge(prevX, prevY, controlX, controlY, x, y));
                    break;

                case 't':
                    relative = true;
                case 'T':
                    // control is a reflection of the previous control point
                    if ((prevIc == 'T') || (prevIc == 't') || (prevIc == 'q') || (prevIc == 'Q'))
                    {
                        controlX = prevX + (prevX - controlX);
                        controlY = prevY + (prevY - controlY);
                    }
                    else
                    {
                        controlX = prevX;
                        controlY = prevY;
                    }
                    x = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    shapeRecords.add(curvedEdge(prevX, prevY, controlX, controlY, x, y));
                    break;

                case 'c':
                    relative = true;
                case 'C':
                    controlX = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    controlY = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    control2X = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    control2Y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    x = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    shapeRecords.addAll(cubicToQuadratic(prevX, prevY, controlX, controlY, control2X, control2Y, x, y));
                    break;

                case 's':
                    relative = true;
                case 'S':
                    // Control1 is a reflection of the previous control2 point
                    if ((prevIc == 'S') || (prevIc == 's') || (prevIc == 'c') || (prevIc == 'C'))
                    {
                        controlX = prevX + (prevX - control2X);
                        controlY = prevY + (prevY - control2Y);
                    }
                    else
                    {
                        controlX = prevX;
                        controlY = prevY;
                    }
                    control2X = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    control2Y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    x = Double.parseDouble(args[i++]) + (relative ? prevX : 0);
                    y = Double.parseDouble(args[i++]) + (relative ? prevY : 0);
                    shapeRecords.addAll(cubicToQuadratic(prevX, prevY, controlX, controlY, control2X, control2Y, x, y));
                    break;
                    
                case 'z':
                case 'Z':
                    shapeRecords.add(straightEdge(prevX, prevY, lastMoveX, lastMoveY));
                    x = lastMoveX;
                    y = lastMoveY;
                    break;

                default:
                    // unknown identifier, throw error?
                    return null;
            }

            prevX = x;
            prevY = y;
            prevIc = ic;
       }
        
        //do an implicit closepath, if needed
        if (fill && (Math.abs(prevX-lastMoveX) > AbstractFXGNode.EPSILON) || (Math.abs(prevY-lastMoveY) > AbstractFXGNode.EPSILON))  
        {
            shapeRecords.addAll(implicitClosepath(prevX, prevY, lastMoveX, lastMoveY));
        }
        return shapeRecords;
    }


    /**
     * Utility method that calculates the minimum bounding rectangle that
     * encloses a list of ShapeRecords, taking into account the possible maximum
     * stroke width of any of the supplied linestyles.
     * 
     * @param records
     * @param lineStyles
     * @return
     */
    public static Rect getBounds(List<ShapeRecord> records, List<LineStyle> lineStyles)
    {
        if (records == null || records.size() == 0)
            return new Rect();

        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        int x = 0;
        int y = 0;
        boolean firstMove = true;

        Iterator<ShapeRecord> iterator = records.iterator();
        while (iterator.hasNext())
        {
            ShapeRecord r = iterator.next();

            if (r == null)
                continue;

            if (r instanceof StyleChangeRecord)
            {
                StyleChangeRecord scr = (StyleChangeRecord)r;
                x = scr.moveDeltaX;
                y = scr.moveDeltaY;
                if (firstMove)
                {
                    x1 = x;
                    y1 = y;
                    x2 = x;
                    y2 = y;
                    firstMove = false;
                }
            }
            else if (r instanceof StraightEdgeRecord)
            {
                StraightEdgeRecord ser = (StraightEdgeRecord)r;
                x = x + ser.deltaX;
                y = y + ser.deltaY;
            }
            else if (r instanceof CurvedEdgeRecord)
            {
                CurvedEdgeRecord cer = (CurvedEdgeRecord)r;
                
                Rect curvBounds = computeCurveBounds(x, y, cer);
                
                if (curvBounds.xMin < x1) x1 = curvBounds.xMin;
                if (curvBounds.yMin < y1) y1 = curvBounds.yMin;
                if (curvBounds.xMax > x2) x2 = curvBounds.xMax;
                if (curvBounds.yMax > y2) y2 = curvBounds.yMax;
                                
                x = x + cer.controlDeltaX + cer.anchorDeltaX;
                y = y + cer.controlDeltaY + cer.anchorDeltaY;
            }

            //update x1, y1 to min values and x2, y2 to max values
            if (x < x1) x1 = x;
            if (y < y1) y1 = y;
            if (x > x2) x2 = x;
            if (y > y2) y2 = y;
        }

        // Consider maximum potential stroke width of each linestyle
        if (lineStyles != null && lineStyles.size() > 0)
        {
            Iterator<LineStyle> lineIterator = lineStyles.iterator();
            int width = TWIPS_PER_PIXEL;
            while (lineIterator.hasNext())
            {
                LineStyle ls = lineIterator.next();
                if (ls == null)
                {
                    continue;
                }
                else
                {
                    if (ls.hasMiterJoint())
                    {
                        double miterLimit = ((double) ls.miterLimit) / SwfConstants.FIXED_POINT_MULTIPLE_8;
                        if (miterLimit < 1) miterLimit = 1;                      
                        int jointwidth = (int) (ls.width * miterLimit);
                        if (width < jointwidth)
                            width = jointwidth;
                    } 
                    else
                    {
                        if (width < ls.width)
                            width = ls.width;
                    }
                }
            }

            int stroke = (int)Math.rint(width / 2.0);
            x1 = x1 - stroke;
            y1 = y1 - stroke;
            x2 = x2 + stroke;
            y2 = y2 + stroke;
        }

        return new Rect(x1, x2, y1, y2);
    }

    private static Rect computeCurveBounds(int x0, int y0, CurvedEdgeRecord curve)
    {
        int x1 = x0 + curve.controlDeltaX;
        int y1 = y0 + curve.controlDeltaY;
        int x2 = x1 + curve.anchorDeltaX;
        int y2 = y1 + curve.anchorDeltaY;
        
        //initialize xmin, ymin, xmax, ymax to the anchor points of curve
        int xmin = x0, xmax = x0;
        int ymin = y0, ymax = y0;
        if (x2 < xmin) xmin = x2;
        if (y2 < ymin) ymin = y2;
        if (x2 > xmax) xmax = x2;
        if (y2 > ymax) ymax = y2;
     
        //compute t at extrema point for x and the corresponding x, y values 
        double t = computeTExtrema(x0, x1, x2);
        if (t == Double.NaN)
        {
            //use control point
            if (x1 < xmin) xmin = x1;
            if (y1 < ymin) ymin = y1;
            if (x1 > xmax) xmax = x1;
            if (y1 > ymax) ymax = y1;
        }
        else if ((t > 0) && (t < 1))
        {
            int x, y;
            x = computeValueForCurve(x0, x1, x2, t);
            y = computeValueForCurve(y0, y1, y2, t);
            if (x < xmin) xmin = x;
            if (y < ymin) ymin = y;
            if (x > xmax) xmax = x;
            if (y > ymax) ymax = y;
        }
        
        //compute t at extrema point for y and the corresponding x, y values 
        t = computeTExtrema(y0, y1, y2);
        if (t == Double.NaN)
        {
            //use control point
            if (x1 < xmin) xmin = x1;
            if (y1 < ymin) ymin = y1;
            if (x1 > xmax) xmax = x1;
            if (y1 > ymax) ymax = y1;
        }
        else if ((t > 0) && (t < 1))
        {
            int x, y;
            x = computeValueForCurve(x0, x1, x2, t);
            y = computeValueForCurve(y0, y1, y2, t);
            if (x < xmin) xmin = x;
            if (y < ymin) ymin = y;
            if (x > xmax) xmax = x;
            if (y > ymax) ymax = y;
        }
        
        Rect r = new Rect(xmin, xmax, ymin, ymax);        
        return r;
    }
    
    //compute value for quadratic bezier curve at t
    // the quadratic bezier curve is p0*(1-t)^2 + 2*p1*(1-t)*t + p2*t^2 
    private static int computeValueForCurve(int p0, int p1, int p2, double t)
    {
        return (int)(p0*(1-t)*(1-t) + 2*p1*(1-t)*t + p2*t*t);
        
    }
    
    //compute the extrema which corresponds to derivative equal to 0
    private static double computeTExtrema(int p0, int p1, int p2)
    {
        // the quadratic bezier curve  is p0*(1-t)^2 + 2*p1*(1-t)*t + p2*t^2, 
        // its first derivative (with respect to t) is 2*(p0 - 2*p1 + p2)*t + 2*(p1 - p0),
        // which is zero for t = (p0 - p1)/(p0 - 2*p1 + p2)
        
        int denom = (p0 - 2*p1 + p2);
        if (denom == 0)
        {
            //cannot compute the derivative - use the control point for extrema
            return Double.NaN;
        }
        else
        {
            double t = (p0 - p1)/(double) denom;
            return t;
        }
        
    }
    
    private static double[] getCornerRadius(double cornerRadiusX, double cornerRadiusY, 
    		double radiusX, double radiusY, double width, double height)
    {
    	double[] newRadius = new double[2];
        if (Double.isNaN(cornerRadiusX))
        {
        	cornerRadiusX = radiusX; 
        	if (Double.isNaN(cornerRadiusY))
        		cornerRadiusY = radiusY;
        	else
        		cornerRadiusY = cornerRadiusX;
        }
        else if (Double.isNaN(cornerRadiusY))
        {
        	cornerRadiusY = cornerRadiusX;
        }
        if ( cornerRadiusX > width/2.0 )
        	cornerRadiusX = width/2.0;
        if ( cornerRadiusY > height/2.0 )
        	cornerRadiusY = height/2.0;     
        
        newRadius[0] = cornerRadiusX;
        newRadius[1] = cornerRadiusY;
        return newRadius;
    }
}

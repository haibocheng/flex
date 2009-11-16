////////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2003-2006 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package com.adobe.internal.fxg.types;

import com.adobe.internal.fxg.dom.transforms.MatrixNode;

import flash.swf.SwfConstants;

import flash.swf.builder.types.Point;
import flash.swf.types.Matrix;
import flash.swf.types.Rect;

/**
 * Utility class to help with matrix transformation for coordinate transformation.
 * 
 * @author Sujata Das
 */
public class FXGMatrix
{

	public double a; //x-axis scaling
    public double b; //x-axis skew
    public double c; //y-axis skew
    public double d; //y-axis scaling
    public double tx; //x-axis translation
    public double ty; //y-axis translation
    
    //constructor
    public FXGMatrix(double a, double b, double c, double d, double tx, double ty)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.tx = tx;
        this.ty = ty;
    }
    
    //constructor - intializes matrix to identity matrix
    public FXGMatrix()
    {
        this.identity();
    }

    public FXGMatrix(MatrixNode m)
    {
        this.a = m.a;
        this.b = m.b;
        this.c = m.c;
        this.d = m.d;
        this.tx = m.tx;
        this.ty = m.ty;
    }

    public void identity() 
    {
        this.a = 1;
        this.b = 0;
        this.c = 0;
        this.d = 1;
        this.tx = 0;
        this.ty = 0;        
    }
    
    //concatenates matrix m to the current matrix
    public void concat(FXGMatrix m)
    {
        // Matrix multiplication 
        double new_a = a * m.a + b * m.c;
        double new_b = a * m.b + b * m.d;
        double new_c = c * m.a + d * m.c;
        double new_d = c * m.b + d * m.d;
        double new_tx = tx * m.a + ty * m.c + m.tx;
        double new_ty = tx * m.b + ty * m.d + m.ty;

        a  = new_a;
        b  = new_b;
        c  = new_c;
        d  = new_d;
        tx = new_tx;
        ty = new_ty;        
    }
    
    //concatenates a rotation matrix with rotation angle to the current matrix
    public void rotate(double angle)
    {
        double cos = Math.cos(angle*Math.PI/180.0);
        double sin = Math.sin(angle*Math.PI/180.0);
        FXGMatrix newM = new FXGMatrix(cos, sin, -sin, cos, 0, 0);
        this.concat (newM);
    }
    
    //concatenates a scaling matrix with scale factors scaleX and scaleY to the current matrix
    public void scale(double scaleX, double scaleY)
    {
        FXGMatrix newM = new FXGMatrix(scaleX, 0, 0, scaleY, 0, 0);
        this.concat (newM);     
    }
    
    //concatenates a transaltion matrix with translations (dx, dy) to the current matrix
    public void translate(double dx, double dy)
    {
        tx += dx;
        ty += dy;
    }
    
    //inverts the current matrix - return true if inversion succeeds 
    //                           - returns false if matrix in not invertible
    public boolean invert()
    {

        // Invert a general matrix
        double a0, a1, a2, a3, det;

        a0 = a;
        a1 = b;
        a2 = c;
        a3 = d;

        det = a0 * a3 - a1 * a2;
        if (det == 0.0)
        {
            return false;
        }
        det = 1.0 / det;

        a = a3 * det;
        b = -a1 * det;
        c = -a2 * det;
        d = a0 * det;
        
        return true;
    }
    
    
    //creates a matrix from the discrete transform parameters
    public static FXGMatrix convertToMatrix(double scaleX, double scaleY, double rotation, double tx, double ty)
    {
        FXGMatrix m = new FXGMatrix();
        m.scale (scaleX, scaleY);
        m.rotate (rotation);
        m.translate(tx, ty);        
        return m;
    }

    //returns a SWF Matrix data type that is equivalent to the current matrix
    public Matrix toSWFMatrix()
    {
        
        /*SWF matrices need to be invertible - check if it is invertible
         * disabled it for now - other apps seem to allow it
        FXGMatrix newm = new FXGMatrix(a, b, c, d, tx, ty);
        if (!newm.invert())
            throw new FXGException("MatrixNotInvertible");
        */
        
        Matrix sm = new Matrix();
        if (b != 0 || c != 0)
            sm.hasRotate = true;        
        if (a != 0 || d != 0)
            sm.hasScale = true;
        sm.scaleX = (int) (a * SwfConstants.FIXED_POINT_MULTIPLE);
        sm.scaleY =  (int) (d * SwfConstants.FIXED_POINT_MULTIPLE);
        sm.rotateSkew0 = (int) (b * SwfConstants.FIXED_POINT_MULTIPLE);
        sm.rotateSkew1 =  (int) (c * SwfConstants.FIXED_POINT_MULTIPLE);
        sm.translateX = (int) (tx * SwfConstants.TWIPS_PER_PIXEL);
        sm.translateY = (int) (ty * SwfConstants.TWIPS_PER_PIXEL);
        
        return sm;        
    }
    
    //apply transform to rect
    public Rect apply(Rect rc)
    {
        Rect newRect = new Rect();
        newRect.xMax = (int) (rc.xMax*a + rc.yMax*c + tx* SwfConstants.TWIPS_PER_PIXEL);
        newRect.yMax = (int) (rc.xMax*b + rc.yMax*d + ty* SwfConstants.TWIPS_PER_PIXEL);
        newRect.xMin = (int) (rc.xMin*a + rc.yMin*c + tx* SwfConstants.TWIPS_PER_PIXEL);
        newRect.yMin = (int) (rc.xMin*b + rc.yMin*d + ty* SwfConstants.TWIPS_PER_PIXEL);
        
        return newRect;
    }
    
    /**
     * Set matrix attribute values with values in this FXGMatrix object.
     * @param node - the matrix node whose attribute values will be updated.
     */
    public void setMatrixNodeValue(MatrixNode node)
    {
        node.a = this.a;
        node.b = this.b;
        node.c = this.c;
        node.d = this.d;
        node.tx = this.tx;
        node.ty = this.ty;
    }
    
    /**
     * Apply transform to a point.
     * @param point - the point to be transformed.
     * @return the transformed point.
     */
    public Point apply(Point point)
    {
        Point newPoint = new Point();
        newPoint.x = a * point.x + c * point.y + tx * SwfConstants.TWIPS_PER_PIXEL;
        newPoint.y = b * point.x + d * point.y + ty * SwfConstants.TWIPS_PER_PIXEL;
        return newPoint;
    }
}

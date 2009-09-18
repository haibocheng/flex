////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.dom;

import com.adobe.internal.fxg.dom.transforms.MatrixNode;

public interface ScalableGradientNode
{
    /**
     * @return The horizontal distance to translate the gradient.
     */
    double getX();
    
    /**
     * @return The vertical distance to translate the gradient.
     */
    double getY();

    /**
     * @return The horizontal distance of the unrotated gradient (that will be
     * compared to the target's width to calculate a scale ratio). Note this
     * is different from a shape transform scale.
     */
    double getScaleX();

    /**
     * @return The horizontal distance of the unrotated gradient (that will be
     * compared to the target's width to calculate a scale ratio). Note this
     * is different from a shape transform scale.
     */
    double getScaleY();

    /**
     * @return The clockwise rotation angle in degrees. 
     */
    double getRotation();

    /**
     * @return A pre-calculated matrix to be used instead of the individual
     * transform properties.
     */
    MatrixNode getMatrixNode();

    /**
     * @return true if this gradient is linear.
     */
    boolean isLinear();
}

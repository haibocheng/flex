////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package com.adobe.internal.fxg.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import flash.swf.Frame;
import flash.swf.Movie;
import flash.swf.MovieEncoder;
import flash.swf.SwfConstants;
import flash.swf.Tag;
import flash.swf.TagEncoder;
import flash.swf.tags.DefineSprite;
import flash.swf.tags.PlaceObject;
import flash.swf.types.Matrix;
import flash.swf.types.Rect;

/**
 * Utility class that allows writing out a DefineSprite to an Output Stream
 * 
 * @author sdas
 */
public class SWFWriter
{

    public SWFWriter()
    {
    }

    int version = 10;
    int framerate = 24;
    int width = 800;
    int height = 800;
    int depth = 1;

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public int getFramerate()
    {
        return framerate;
    }

    public void setFramerate(int framerate)
    {
        this.framerate = framerate;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * method that writes the sprite to output stream
     * 
     * @param sprite
     * @param fout
     * @throws IOException
     */
    public void writeToFile(DefineSprite sprite, OutputStream fout)
            throws IOException
    {
        Movie movie = new Movie();
        movie.version = version;
        movie.framerate = framerate;
        movie.width = width;
        movie.height = height;
        movie.size = new Rect(width * SwfConstants.TWIPS_PER_PIXEL, height * SwfConstants.TWIPS_PER_PIXEL); 

        Frame frame = new Frame();
        movie.frames = new ArrayList<Frame>();
        movie.frames.add(frame);

        PlaceObject po3 = new PlaceObject(Tag.stagPlaceObject3);
        po3.matrix = new Matrix();
        po3.setRef(sprite);
        po3.depth = depth;
        frame.controlTags.add(po3);

        TagEncoder tagEncoder = new TagEncoder();
        MovieEncoder movieEncoder = new MovieEncoder(tagEncoder);
        movieEncoder.export(movie);
        tagEncoder.writeTo(fout);

    }

}

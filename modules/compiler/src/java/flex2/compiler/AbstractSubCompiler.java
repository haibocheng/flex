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

package flex2.compiler;

import flex2.compiler.util.PerformanceData;

public abstract class AbstractSubCompiler implements SubCompiler
{
    protected CompilerBenchmarkHelper benchmarkHelper;
    protected CompilerBenchmarkHelper benchmarkEmbeddedHelper;	// used by compilers that have embedded compilers

    /**
     * @return accumulated performance data since initBenchmarks() was called.
     * @see initBenchmarks
     */
    public PerformanceData[] getBenchmarks()
    {
    	PerformanceData[] ret = null;
    	if (benchmarkHelper==null)
    	{
    		ret = new PerformanceData[0];
    	}
    	else if (benchmarkEmbeddedHelper != null)
    	{
    		// subtract out the part contributed by the embedded compilers, as we will 
    		// report that separately
    		ret = benchmarkHelper.subtract(benchmarkEmbeddedHelper);
    	}
    	else
        {
            ret = benchmarkHelper.getBenchmarks();
        }
        return ret;
    }
    
  /**
   * @return accumulated performance data for embedded compiler, if any
   * @see getBenchmarks
   */
    public PerformanceData[] getEmbeddedBenchmarks()
    {
    	PerformanceData[] ret = null;
    	if (benchmarkEmbeddedHelper != null)
    	{
    		ret = benchmarkEmbeddedHelper.getBenchmarks();
    	}
    	return ret;
    }

    /**
     * Reset benchmark performance data.
     */
    public void initBenchmarks()
    {
        benchmarkHelper = new CompilerBenchmarkHelper(getName());
        benchmarkHelper.initBenchmarks();
        benchmarkEmbeddedHelper=null;		// normally not used. compilers with embedded compilers must 
        								// override initBenchmarks()
    }
    
    /**
     * receive a compiler helper from outside.
     * This is typically used when an "outer" compiler wants to benchmark an embedded compiler
     * @param helper is the helper passed into us
     * @param isEmb is true if the helper being set is the embedded one, false if the main one
     */
    public void setHelper(CompilerBenchmarkHelper helper, boolean isEmbedded)
    {
    	if (isEmbedded)
    		benchmarkEmbeddedHelper = helper;
    	else
    		benchmarkHelper = helper;	
    }

    /**
     * Report benchmark information to the given Logger.
     * @param logger The Logger to receive benchmarking information.
     */
    public void logBenchmarks(Logger logger)
    {
        if (benchmarkHelper != null)
            benchmarkHelper.logBenchmarks(logger);
        if (benchmarkEmbeddedHelper != null)
        {
        	benchmarkEmbeddedHelper.logBenchmarks(logger);
        }
    }
}

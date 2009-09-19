package flex2.compiler.util;

/**
 * Data to describe the performance of a class method.
 * 
 * Contains the number of times the method was called and
 * the total amount of time in the method.
 * 
 * @author dloverin
 *
 */
public class PerformanceData
{
    /**
     * Number of time a method was invoked.
     */
    public long invocationCount;

    /**
     * Total amount of time (ms) in a method.
     */
    public long totalTime;
    
    
    public PerformanceData()
    {
        
    }
}

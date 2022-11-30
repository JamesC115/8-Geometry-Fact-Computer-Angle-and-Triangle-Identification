package preprocessor.delegates;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import geometry_objects.points.PointNamingFactory;

public class ImplicitPointPreprocessor
{
	/**
	 * It is possible that some of the defined segments intersect
	 * and points that are not named; we need to capture those
	 * points and name them.
	 * 
	 * Algorithm:
	 *    TODO
	 */
	public static Set<Point> compute(PointDatabase givenPoints, List<Segment> givenSegments)
	{
		//seems very inefficient
		
		PointNamingFactory pnf = new PointNamingFactory();
        for(Segment seg1: givenSegments) {
        	for(Segment seg2: givenSegments) {
        		if(seg1.segmentIntersection(seg2) != null) {
        			
        			pnf.put(seg1.segmentIntersection(seg2));
        		}
        	}
        }
        return pnf.getAllPoints();
	}

}

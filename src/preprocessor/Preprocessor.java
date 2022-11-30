package preprocessor;

import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import preprocessor.delegates.ImplicitPointPreprocessor;
import geometry_objects.Segment;
import geometry_objects.delegates.SegmentDelegate;

public class Preprocessor
{
	// The explicit points provided to us by the user.
	// This database will also be modified to include the implicit
	// points (i.e., all points in the figure).
	protected PointDatabase _pointDatabase;

	// Minimal ('Base') segments provided by the user
	protected Set<Segment> _givenSegments;

	// The set of implicitly defined points caused by segments
	// at implicit points.
	protected Set<Point> _implicitPoints;

	// The set of implicitly defined segments resulting from implicit points.
	protected Set<Segment> _implicitSegments;

	// Given all explicit and implicit points, we have a set of
	// segments that contain no other subsegments; these are minimal ('base') segments
	// That is, minimal segments uniquely define the figure.
	protected Set<Segment> _allMinimalSegments;

	// A collection of non-basic segments
	protected Set<Segment> _nonMinimalSegments;

	// A collection of all possible segments: maximal, minimal, and everything in between
	// For lookup capability, we use a map; each <key, value> has the same segment object
	// That is, key == value. 
	protected Map<Segment, Segment> _segmentDatabase;
	public Map<Segment, Segment> getAllSegments() { return _segmentDatabase; }

	public Preprocessor(PointDatabase points, Set<Segment> segments)
	{
		_pointDatabase  = points;
		_givenSegments = segments;
		
		_segmentDatabase = new HashMap<Segment, Segment>();
		
		analyze();
	}

	/**
	 * Invoke the precomputation procedure.
	 */
	
	public void analyze()
	{
		//
		// Implicit Points
		//
		_implicitPoints = ImplicitPointPreprocessor.compute(_pointDatabase, _givenSegments.stream().toList());

		//
		// Implicit Segments attributed to implicit points
		//
		_implicitSegments = computeImplicitBaseSegments(_implicitPoints);

		//
		// Combine the given minimal segments and implicit segments into a true set of minimal segments
		//     *givenSegments may not be minimal
		//     * implicitSegmen
		//
		_allMinimalSegments = identifyAllMinimalSegments(_implicitPoints, _givenSegments, _implicitSegments);

		//
		// Construct all segments inductively from the base segments
		//
		_nonMinimalSegments = constructAllNonMinimalSegments(_allMinimalSegments);

		//
		// Combine minimal and non-minimal into one package: our database
		//
		_allMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
		_nonMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
	}
	
	public Set<Segment> computeImplicitBaseSegments(Set<Point> _implicitPoints){
		
		Set<Segment> set = new HashSet<Segment>();
		for(Segment s: _givenSegments) {
			for(Point p: _implicitPoints) {
				if(s.pointLiesBetweenEndpoints(p)) {
					set.add(new Segment(p, s.getPoint1()));
					set.add(new Segment(p, s.getPoint2()));
				}
			}
			
		}
		return set;
	}
	
	
	/*
	 * @param set of implicit points, segments and implicit segments
	 * @return set of all minimal segments
	 */
	public Set<Segment> identifyAllMinimalSegments(Set<Point> _implicitPoints, Set<Segment> _givenSegments, Set<Segment>_implicitSegments){
		
		Set<Segment> minimalSet = new HashSet<Segment>();
		Set<Segment> set2 = new HashSet<Segment>();
		
		//add all segments that have no overlap
		for(Segment s: _givenSegments) {
			if (noOverlap(s, _givenSegments))
				minimalSet.add(s);
		}
		
		//add all implicit segments that have no overlap, those with
		//are added to a new set
		for(Segment s: _implicitSegments) {
			if (noOverlap(s, _implicitSegments)) 
				minimalSet.add(s);
			else set2.add(s);
		}
		
		//checks the segments with overlap and creates the
		//minimal segments from them
		for(Segment s: set2) {
			Point p1 = implicitInSeg(s, _implicitPoints);
			Point p2;
			if(_implicitPoints.contains(s.getPoint1())) {
				p2 = s.getPoint1();
			} else {
				p2 = s.getPoint2();
			}
			if (!minimalSet.contains(new Segment(p2, p1))) {
				minimalSet.add(new Segment(p1, p2));
			}
			
		}
		
		return minimalSet;
	}
	
	/*
	 * @param segment s and set of implicit points
	 * @return the first point from the set that lies between a segment 
	 */
	private Point implicitInSeg(Segment s, Set<Point> _implicitPoints) {
		for (Point p: _implicitPoints) {
			if(s.pointLiesBetweenEndpoints(p))
				return p;
			
		}
		return null;
	}
	
	/*
	 * @param s --a segment and _givensegments -- set of segments
	 * @return true / false if the segment doesn't overlap with any other 
	 * segment in the given list
	 */
	private boolean noOverlap(Segment s, Set<Segment> _givenSegents) {
		for(Segment s2 : _givenSegments) {
			if (s.equals(s2)) continue;
			if(SegmentDelegate.middleCrosses(s, s2) || SegmentDelegate.standingOn(s, s2)) return false;
		}
		return true;
	}
	
	/*
	 * @param minimalSegment
	 * @return set of all non-minimal edges
	 */
	public Set<Segment> constructAllNonMinimalSegments(Set<Segment> _allMinimalSegments){
		Queue<Segment> que = new LinkedList<Segment>();
		Set<Segment> set = new HashSet<Segment>();
		que.addAll(_allMinimalSegments);
		
		while (!que.isEmpty()) {
			Segment s = que.poll();
			for(Segment s2: _allMinimalSegments) {
		
				
				Point p = s.sharedVertex(s2);
				if(s.coincideWithoutOverlap(s2) && p != null && !set.contains(new Segment(s.other(p), s2.other(p))))  {
					que.add(new Segment(s.other(p), s2.other(p)));
					set.add(new Segment(s.other(p), s2.other(p)));
				}
			
			}
		}
		
		/*for(Segment s: _allMinimalSegments) {
			for(Segment s2: _allMinimalSegments) {
				
				Point p = s.sharedVertex(s2);
				if(s.isCollinearWith(s2) && p != null)  {
					set.add(new Segment(s.other(p), s2.other(p)));
				}
				
			}
		}
		
		
		for (Segment s: set) {
			for(Segment s2: _allMinimalSegments) {
				
				Point p = s.sharedVertex(s2);
				if(s.coincideWithoutOverlap(s2) && p != null && !_allMinimalSegments.contains(new Segment(s.other(p), s2.other(p))))  {
					//set2.add(new Segment(s.other(p), s2.other(p)));
				}
				
			}
			
		}
	//	set.addAll(set2);
	 
	 */
		return set;
	}

}

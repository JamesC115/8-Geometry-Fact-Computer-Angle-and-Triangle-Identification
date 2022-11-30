package input;

import static org.junit.jupiter.api.Assertions.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import geometry_objects.Segment;
import input.builder.GeometryBuilder;
import input.components.ComponentNode;
import input.components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;
import input.parser.JSONParser;

/**
* An input facade for extracting figures and creating geometry representations
*
* <p>Bugs: TBT
*
* @author Jace, James, Emil
* @date 10-26-22
*/
public class InputFacade
{
	/**
	 * Acquire a figure from the given JSON file.
     *
	 * @param filename -- the name of a file
	 * @return a FigureNode object corresponding to the input file.
	 */
	public static FigureNode extractFigure(String filename)
	{
		//TODO need any checking for valid filename? 
		String figureStr = utilities.io.FileUtilities.readFileFilterComments(filename);
		GeometryBuilder geoBuilder = new GeometryBuilder();
		JSONParser parser = new JSONParser(geoBuilder);
		
		return (FigureNode) parser.parse(figureStr);
	}
	
	/**
	 * 1) Read in a figure from a JSON file.
	 * 2) Convert the PointNode and SegmentNode objects to a Point and Segment objects 
	 *    (those classes have more meaningful, geometric functionality).
     *
	 * @param filename
	 * @return a pair <set of points as a database, set of segments>
	 */
	public static Map.Entry<PointDatabase, Set<Segment>> toGeometryRepresentation(String filename)
	{
		//Create figureNode
		FigureNode figure = extractFigure(filename); 
		//create PointDatabase
		PointDatabase pointDB = createPointDatabase(figure.getPointsDatabase());
		//create SegmentSet
		Set<Segment> segmentSet = createSegmentSet(figure.getSegments());
		
		return new AbstractMap.SimpleEntry<PointDatabase, Set<Segment>>(pointDB, segmentSet);
	}
	
	
	/**
	 * Converts a pointNode object into a Point object
	 * @param point
	 * @return a Point object with the same data as the input pointNode
	 */
	private static Point convertPointNode(PointNode pointNode) 
	{
		double x = pointNode.getX();
		double y = pointNode.getY();
		String name = pointNode.getName();
		
		return new Point(name, x, y);
	}
	
	/**
	 * Converts a segmentNode into a Segment object
	 * @param segment
	 * @return a Segment object with the same data as the input segmentNode
	 */
	private static Segment convertSegmentNode(SegmentNode segment) 
	{
		Point p1 = convertPointNode(segment.getPoint1());
		Point p2 = convertPointNode(segment.getPoint2());
		
		return new Segment(p1, p2);
	}
	/**
	 * Create a pointDatabase based on an input PointNodeDatabase
	 * @param nodeDB
	 * @return a new PointDatabase corresponding to the input PointNodeDatabase
	 */
	private static PointDatabase createPointDatabase (PointNodeDatabase nodeDB) 
	{
		//convert PointNodes to Points and add to arrayList
		List<Point> pointList = new ArrayList<Point>();
		
		for (PointNode pointNode: nodeDB.getPointsSet()) {
			Point point = convertPointNode(pointNode);
			pointList.add(point);
		}
		
		return new PointDatabase(pointList);
	}
	
	/**
	 * Creates a set of Segments based on an input SegmentNodeDatabase
	 * @param nodeDB
	 * @return a set of Segment objects
	 */
	private static Set<Segment> createSegmentSet (SegmentNodeDatabase segmentNodeDB) 
	{
		//get list of segmentNodes
		List<SegmentNode> segmentNodeList = segmentNodeDB.asUniqueSegmentList();
		Set<Segment> segmentSet = new HashSet<Segment>();
		
		//convert segmentNodes to segments and add to set
		for (SegmentNode segNode: segmentNodeList) {
			segmentSet.add(convertSegmentNode(segNode));
		}
		
		return segmentSet;
	}
}

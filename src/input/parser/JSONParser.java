package input.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import input.builder.DefaultBuilder;
import input.builder.GeometryBuilder;
import input.components.*;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNodeDatabase;
import input.exception.ParseException;

/* A JSON parser that takes a string representation of a JSON file and builds
 * a new FigureNode 
 * 
* <p>Bugs: 
* 
* @author Jace Rettig, Jack, and George
* @Date 10-2-22
*/
public class JSONParser
{
	protected ComponentNode  _astRoot;
	protected DefaultBuilder _builder;

	public JSONParser(DefaultBuilder UserBuilder)
	{
		_astRoot = null;
		_builder = UserBuilder;
		
	}

	private void error(String message)
	{
		throw new ParseException("Parse error: " + message);
	}
	
	/**
	 * Checks if JSON string is invalid
	 * @param str
	 * @return
	 */
	private boolean checkValidJSON(String str) {
		//check if valid Figure String
		if (!(str.contains("Figure"))) {
			error("JSON Figure not Found");
			return false;
		}
		//check if valid description
		if (!(str.contains("Description"))) {
			error("JSON Description Not Found");
			return false;
		}
		//valid points
		if (!(str.contains("Points"))) {
			error("JSON Points Not Found");
			return false;
		}
		//valid segments
		if (!(str.contains("Segments"))) {
			error("JSON Segments Not Found");
			return false;
		}
		return true;
		
	}
	
	/**
	 * Creates a new FigureNode by parsing a string representation
	 * of a JSON file
	 * @param JSON file contents as a string
	 * @return a FigureNode tree
	 * @throws ParseException
	 */
	public ComponentNode parse(String str) throws ParseException
	{
		//check if valid Figure String
		if (!checkValidJSON(str)) return null;
		// Parsing is accomplished via the JSONTokenizer class.
		JSONTokener tokenizer = new JSONTokener(str);
		JSONObject  JSONroot = (JSONObject)tokenizer.nextValue();
		JSONObject figure = JSONroot.getJSONObject("Figure");
			
		//build description
		String description = buildDescription(figure); 
		
		//build points
		JSONArray pointsArray = figure.getJSONArray("Points");
		PointNodeDatabase pointDB = buildPointNodeDatabase(pointsArray);
		
		//build segments
		JSONArray segmentsArray = figure.getJSONArray("Segments");
		SegmentNodeDatabase segmentDB = buildSegmentNodeDatabase(segmentsArray, pointsArray);
		
		//create tree
		//Build the whole AST, check for return class object, and return the root	
		return _astRoot = _builder.buildFigureNode(description, pointDB, segmentDB); 
	}
	
	/**
	 * Parses the JSON figure and gets the description
	 * @param json figure
	 * @return the description of the figure
	 */
	private String buildDescription(JSONObject json) {
		return json.getString("Description");
	}
	
	/**
	 * Parses the JSON figure and builds the PointNodeDatabase
	 * @param json figure
	 * @return a corresponding PointNodeDatabase
	 */
	private PointNodeDatabase buildPointNodeDatabase(JSONArray pointArray) 
	{
		List<PointNode> pointList = new ArrayList<PointNode>();
		//loop through point objects
		for(Object point: pointArray)
		{
			//cast to JSONObjects and get the name as a String
			JSONObject currentPoint = (JSONObject) point;
			String name = currentPoint.getString("name"); 
			//create pointNodeObjects and add to pointList
			PointNode pointA = getPointNode(name, pointArray);
			pointList.add(pointA);
		}
		return _builder.buildPointDatabaseNode(pointList);
	}
	
	/**
	 * Takes a JSON pointNode String as input and finds 
	 * and returns a new pointNode with that name/coords
	 * @param String key
	 * @return a new PointNode
	 */
	private PointNode getPointNode (String name, JSONArray points) {
		//find input key
		for (Object point:points) {
			JSONObject currentPoint = (JSONObject) point;
			if (currentPoint.get("name").equals(name)) {
				double x = currentPoint.getDouble("x");
				double y = currentPoint.getDouble("y");
				return _builder.buildPointNode(name, x, y);
			}
		}
		return null;
	}
	
	/**
	 * Parse the Figure's segment input and create a new segmentNode Database
	 * @param JSON figure
	 * @return a corresponding SegmentNodeDatabase
	 */
	private SegmentNodeDatabase buildSegmentNodeDatabase(JSONArray segments, JSONArray JSONPointsArray) {
		//parse the JSON string and create segmentNodeDatabase
		SegmentNodeDatabase segmentDB = _builder.buildSegmentNodeDatabase();	
		//Loop through each AdjList and create points
		for (Object adjList: segments) {
			//get the adjList point name and create a new pointNodeA
			JSONObject currentAdjList = (JSONObject) adjList;
			String key = currentAdjList.keys().next();
			PointNode pointA = getPointNode(key, JSONPointsArray);

			//create array of point names from adjList
			JSONArray SegmentPointsArray = currentAdjList.getJSONArray(key);
			List<PointNode> pointList = buildSegmentPointList(SegmentPointsArray, JSONPointsArray);
			
			//add new adjList with pointA and pointsB
			segmentDB.addAdjacencyList(pointA, pointList);
			pointList.clear();
		}
		return segmentDB;
	}
	
	/**
	 * Creates a pointList of points for a single AdjLists
	 * @param SegmentPointsArray
	 * @param points
	 * @return
	 */
	private List<PointNode> buildSegmentPointList(JSONArray SegmentPointsArray, JSONArray points) 
	{
		List<PointNode> pointList = new ArrayList<PointNode>();
		//create array of point names from adjList
		for (Object segmentPoint: SegmentPointsArray) {
			//create new pointNodeBs and add them to points list
			String currentPoint = (String) segmentPoint;
			PointNode pointB = getPointNode(currentPoint, points);
			pointList.add(pointB);
		}
		return pointList;
	}

}

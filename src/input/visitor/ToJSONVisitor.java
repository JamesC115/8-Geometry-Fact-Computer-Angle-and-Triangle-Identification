package input.visitor;

import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import input.components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;
import utilities.io.StringUtilities;

/**
 * A toJSON visitor class that turns the respective nodes of a figure node into a JSON object 
*
* <p>Bugs: 
* 
* @author Jace Rettig, Jack, and George
* @Date 10-2-22
*/
public class ToJSONVisitor implements ComponentNodeVisitor
{

	
	/*
	 * This method collects all of the components of a figure node object
	 * and returns its corresponding JSON object
	 */
	@Override
	public Object visitFigureNode(FigureNode node, Object o) {

		//make empty figure JSON object to add to
		JSONObject result = new JSONObject(); 
		
		//make empty objects for components of figurenode
		JSONObject descriptionJSON = new JSONObject();
		JSONObject pointsJSON = new JSONObject(); 
		JSONObject segmentJSON = new JSONObject(); 

		//get description from node and add it to the result
		String description = node.getDescription();
		descriptionJSON.put("Description", description);
				
		//make segment node database and convert it to JSON array
		SegmentNodeDatabase snDB = node.getSegments();
		JSONArray segmentJSONArray = (JSONArray) visitSegmentDatabaseNode(snDB, null);
		
		//add segment node database array to segment JSON object
		segmentJSON.put("Segments", segmentJSONArray);
		
		//make point node database and convert it to JSON array
		PointNodeDatabase pnDB = node.getPointsDatabase();
		JSONArray pointsJSONArray = (JSONArray) visitPointNodeDatabase(pnDB, null);
		
		//add point node database array to point JSON object
		pointsJSON.put("Points", pointsJSONArray);
		
		//add description, points, and segment JSON objects to result
		result.append("Figure", descriptionJSON);
		result.append("Figure", pointsJSON);
		result.append("Figure", segmentJSON);
		
		return result; 
		
	}

		
	/*
	 * Takes a segment node database object and returns a JSON 
	 * version of it
	 */
	@Override
	public Object visitSegmentDatabaseNode(SegmentNodeDatabase node, Object o) {
		
		JSONArray segmentArray = new JSONArray(); 
		
		for (Map.Entry<PointNode, Set<PointNode>> key: node.asAdjList().entrySet()) {
			
			JSONObject segmentObject = new JSONObject();
						
			for (PointNode value: key.getValue()) {
				
				segmentObject.append(key.getKey().getName(), value.getName());
			}
			
			segmentArray.put(segmentObject); 
		}
		
		return segmentArray;
	}

	/*
	 * Takes a segment node and returns a JSON object of it
	 */
	@Override
	public Object visitSegmentNode(SegmentNode node, Object o) {
		
		String p1 = node.getPoint1().getName();
		String p2 = node.getPoint2().getName();
		String name = p1 + p2; 
		
		JSONObject result = new JSONObject(); 
		JSONArray values = new JSONArray(); 
		
		values.put(p1);
		values.put(p2);
		
		result.put(name, values); 
		
		return result; 
		
	}

	
	/*
	 * Takes a point node object and returns a JSON version of it
	 */
	@Override
	public Object visitPointNode(PointNode node, Object o) {
		
		JSONObject pointJSON = new JSONObject();
		
		String name = node.getName(); //split into two steps for readability 
		Double x = node.getX();
		Double y = node.getY();
		
		pointJSON.put("name", name);
		pointJSON.put("x", x);
		pointJSON.put("y", y);
		
		return pointJSON; 
		
	}
	
	
	/*
	 * takes a point node database and returns a JSON version of it
	 */
	@Override
	public Object visitPointNodeDatabase(PointNodeDatabase node, Object o) {
		// TODO Auto-generated method stub
		
		JSONArray pointArray = new JSONArray(); 
		
		for(PointNode pn: node.getPointsSet())
		{
			pointArray.put(visitPointNode(pn, null));
		}
		
		return pointArray;
		
	}
	
}

package input.visitor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import input.components.*;
import input.components.point.*;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;
import utilities.io.StringUtilities;

//
// A JSON file may contain:
//
//     Figure:
//       Points
//       Segments
//
/**
 * An Unparse Visitor Class that prints out a toString representation of the JSON file
*
* <p>Bugs: 
* 
* @author Jace Rettig, Jack, and George
* @Date 10-2-22
*/
public class UnparseVisitor implements ComponentNodeVisitor
{
	@Override
	public Object visitFigureNode(FigureNode node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		sb.append("Figure\n{\n");
		sb.append(StringUtilities.indent(level + 1));
		
		//unparsing the children
		sb.append("Description: " + node.getDescription() +"\n");
		//get points
		visitPointNodeDatabase(node.getPointsDatabase(), pair);
		//get segments
		visitSegmentDatabaseNode(node.getSegments(), pair);
		
		sb.append("}\n\n---------------------------------------------------------");
        return sb.toString();
	}

	@Override
	public Object visitSegmentDatabaseNode(SegmentNodeDatabase node, Object o)
	{
			@SuppressWarnings("unchecked")
			AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
			StringBuilder sb = pair.getKey();
			int level = pair.getValue();
			
			sb.append(StringUtilities.indent(level + 1));
			sb.append("Segments: \n");
			sb.append(StringUtilities.indent(level + 1));
			sb.append("{\n");
			
			//loop over the segments
			for (Map.Entry<PointNode, Set<PointNode>> key: node.asAdjList().entrySet()) {
				sb.append(StringUtilities.indent(level+2));
				sb.append(key.getKey().getName() + " : ");
				for (PointNode value: key.getValue()) {
					sb.append(value.getName() + "  ");
				}
				sb.append("\n");
			}
			
			sb.append(StringUtilities.indent(level + 1));
			sb.append("}\n");
		
        return null;
	}
	

	/**
	 * This method should NOT be called since the segment database
	 * uses the Adjacency list representation
	 */
	@Override
	public Object visitSegmentNode(SegmentNode node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		//add points 1 and 2 to string builder
		sb.append(node.getPoint1());
		sb.append(node.getPoint2());
		sb.append("\n");
		
		return null;
	}

	@Override
	public Object visitPointNodeDatabase(PointNodeDatabase node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		sb.append(StringUtilities.indent(level + 1));
		sb.append("Points: \n");
		sb.append(StringUtilities.indent(level + 1));
		sb.append("{\n");
        
		Set<PointNode> points = node.getPointsSet();
		for (PointNode p: points) 
		{
			visitPointNode(p, pair);
		}
		
		sb.append(StringUtilities.indent(level + 1));
		sb.append("}\n");
		return null;
	}
	
	@Override
	public Object visitPointNode(PointNode node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		sb.append(StringUtilities.indent(level + 2));
		sb.append("Point(" + node.getName() + ")(" + node.getX() + ", " + node.getY() + ")");
		sb.append("\n");
		
        return null;
	}
}
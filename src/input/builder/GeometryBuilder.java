package input.builder;

import java.util.List;

import input.components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;

public class GeometryBuilder extends DefaultBuilder {

	
	public GeometryBuilder() 
	{
		
	}

	@Override
    public FigureNode buildFigureNode(String description,
    		                          PointNodeDatabase points,
    		                          SegmentNodeDatabase segments)
    {
        return new FigureNode(description, points, segments);
    }
    
	@Override
    public SegmentNodeDatabase buildSegmentNodeDatabase()
    {
        return new SegmentNodeDatabase();

    }
    
	@Override
    public void addSegmentToDatabase(SegmentNodeDatabase segments, PointNode from, PointNode to)
    {
    	if (segments != null) segments.addUndirectedEdge(from, to);
    }	
	/**
	 * Adds an AdjList to the input segmentNodeDatabase
	 *
	 * Created to avoid having to rewrite JSON parser to create segments instead of AdjList
	 * @param segments
	 * @param point
	 * @param pointList
	 */
	@Override
	public void addAdjListToSegmentDatabase(SegmentNodeDatabase segments, PointNode point, List<PointNode> pointList) 
	{
		segments.addAdjacencyList(point, pointList);
	}
    
	@Override
    public SegmentNode buildSegmentNode(PointNode pt1, PointNode pt2)
    {
        return new SegmentNode(pt1, pt2);
    }
    
	@Override
    public PointNodeDatabase buildPointDatabaseNode(List<PointNode> points)
    {
        return new PointNodeDatabase(points);
    }
    
	@Override
    public PointNode buildPointNode(String name, double x, double y)
    {
        return new PointNode(name, x, y);
    }
	
}

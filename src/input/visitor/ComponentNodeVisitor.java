package input.visitor;

import input.components.*;
import input.components.point.*;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;

//
// A JSON file may contain:
//
//     Figure:
//       Description
//       Points
//       Segments
//
public interface ComponentNodeVisitor
{
	Object visitFigureNode(FigureNode node, Object o);

	Object visitSegmentDatabaseNode(SegmentNodeDatabase node, Object o);
	
	Object visitSegmentNode(SegmentNode node, Object o);
	
	Object visitPointNode(PointNode node, Object o);

	Object visitPointNodeDatabase(PointNodeDatabase node, Object o);
	
}
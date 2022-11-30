 

package geometry_objects.points;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* A Point naming factory built on a Map structure. Allows the user to 
* create points, put in points, get points, etc. If the user does not 
* provide a name for the point, a name will be generated. 
*
*
* @author Emil westling
* @date October 27
*/
public class PointNamingFactory
{
	private static final String _PREFIX = "*_"; // Distinguishes generated names

	private static final char START_LETTER = 'A';	
	private static final char END_LETTER = 'Z';

	private String _currentName = "A";
	private int _numLetters = 1;

	//
	// A hashed container for the database of points;
	// This requires the Point class implement equals
	// based solely on the individual values and not a name
	// We need a get() method; HashSet doesn't offer one.
	// Each entry is a <Key, Value> pair where Key == Value
	//
	protected Map<Point, Point> _database;

	public PointNamingFactory()
	{
		_database = new LinkedHashMap<Point, Point>();
	}

	/**
	 * 
	 * @param points -- a list of points, named or not named
	 */
	public PointNamingFactory(List<Point> points)
	{
		_database = new LinkedHashMap<Point, Point>();
		
		for (Point p : points) {
			put(p);
		}
	}

	/**
	 * @param pt -- (x, y) coordinate pair object
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database
	 */
	public Point put(Point pt)
	{
		Point point = lookupExisting(pt.getName(), pt.getX(), pt.getY());
	//	System.out.println(point.getName() + " " + point.getX() + " " + point.getY());
		_database.put(point, point);
		Set<Point> ptset = _database.keySet();
	/*	for (Point p : ptset) {
			System.out.println(p.getName() + " " + point.getX() + " " + point.getY());
		} */
		
		return point;
	}

	/**
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that has been added to the database
	 */
	public Point put(double x, double y)
	{
		Point pt = lookupExisting("", x, y);
		_database.put(pt, pt);
		return pt;
	}

	/**
	 * @param name -- the name of the point 
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database.
	 *         
	 *         If the point is in the database and the name differs from what
	 *         is given, nothing in the database will be changed; essentially
	 *         this means we use the first name given for a point.
	 *         
	 *         The exception is that a valid name can overwrite an unnamed point.
	 */
	public Point put(String name, double x, double y)
	{
		Point pt = lookupExisting(name, x, y);
		_database.put(pt, pt);
		return pt;
	}

	/**
	 * Strict access (read-only of the database)
	 * 
	 * @param x
	 * @param y
	 * @return stored database Object corresponding to (x, y) 
	 */
	public Point get(double x, double y)
	{
		//TODO what if point already exists??????
		//need to find point in database that corresponds to that points x and y
		//if (contains(x, y)) return FIND
		/*
		for (Point p: _database.keySet()) {
			if ((Double.compare(x, p.getX()) == 0) && (Double.compare(y, p.getY()) == 0)) {
				return p;
			}
			
		}
		*/
		
				
		return _database.get(new Point(x,y));
	}
	
	public Point get(Point pt)
	{
		return _database.get(pt);
	}

	/**
	 * @param name -- the name of the point 
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database.
	 *         
	 *         If the point is in the database and the name differs from what
	 *         is given, nothing in the database will be changed; essentially
	 *         this means we use the first name given for a point.
	 *         
	 *         The exception is that a valid name can overwrite an unnamed point.
	 */
	private Point lookupExisting(String name, double x, double y)
	{
		// if the point is in the database
		if (contains(x,y)) {
			Point pt = get(x,y);
			// if the already existing point is unnamed and the passed name is valid
			if (_database.get(pt).getName().substring(0, 2).equals("*_") && 
					(!name.equals(Point.ANONYMOUS) && !name.equals(""))) {
				_database.remove(pt);
				return createNewPoint(name, x, y);
			}
			return pt;
		}
		else {
			return createNewPoint(name, x, y);
		}
	}

	/**
	 * @param name -- the name of the point 
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database.
	 *         
	 *         If the point is in the database and the name differs from what
	 *         is given, nothing in the database will be changed; essentially
	 *         this means we use the first name given for a point.
	 *         
	 *         The exception is that a valid name can overwrite an unnamed point.
	 */
	private Point createNewPoint(String name, double x, double y)
	{
		if (!name.equals(Point.ANONYMOUS) && !name.equals("")) {
			return new Point(name,x,y);
		}
		// generates name if an invalid name is given
		return new Point(_PREFIX + getCurrentName(), x, y);
	}

	/**
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return simple containment; no updating
	 */
	public boolean contains(double x, double y) { return _database.containsKey(new Point(x,y)); }
	public boolean contains(Point p) { return _database.containsKey(p); }

	/**
	 * @return acquires and returns the next name in sequence; also
	 * generates the next name in a 'lazy list' manner 
	 */
	private String getCurrentName()
	{
        String name = _currentName;
        updateName();
        return name;
	}

	/**
	 * Advances the current generated name to the next letter in the alphabet:
	 * 'A' -> 'B' -> 'C' -> 'Z' --> 'AA' -> 'BB'
	 */
	private void updateName()
	{
		char name = _currentName.charAt(0);
		_currentName = "";
		if (name == END_LETTER) { 
			for (int i = 0; i <= _numLetters; i++) _currentName += START_LETTER;
			_numLetters++;
		}
		else {
			name++;
			for (int i = 0; i < _numLetters; i++) _currentName += name;
		}
	}
	
	/**
	 * @return The entire database of points.
	 */
	public  Set<Point> getAllPoints()
	{
        return _database.keySet();
	}

	public void clear() { 
		_database.clear();
		_currentName = "A";
		_numLetters = 1;
		}
	
	public int size() { return _database.size(); }
	
	@Override
	public String toString()
	{
		StringBuilder strBuild = new StringBuilder();
        Set<Point> pts = _database.keySet();
        for (Point p : pts) {
        	strBuild.append("(" + p.getName() + ", " + p.getX() + ", " + p.getY() + "), ");
        }
        if (strBuild.length() > 0) {
        	strBuild.deleteCharAt(strBuild.length()-1);
        	strBuild.deleteCharAt(strBuild.length()-1);
        }
        return strBuild.toString();
	}
}
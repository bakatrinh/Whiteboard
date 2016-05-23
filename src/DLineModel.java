import java.awt.Color;
import java.awt.Point;

/**
 * @author Trinh Nguyen
 *
 */
public class DLineModel extends DShapeModel {
	private int _thickness;
	private Point p1;
	private Point p2;

	public DLineModel() {
		super();
		p1 = new Point(0, 0);
		p2 = new Point(0, 0);
		_thickness = 2;
	}

	public DLineModel(Color color, int x1, int y1, int x2, int y2, int thickness) {
		super();
		super.setColor(color);
		p1 = new Point(0, 0);
		p2 = new Point(0, 0);
		p1.x = x1;
		p1.y = y1;
		p2.x = x2;
		p2.y = y2;
		_thickness = thickness;
	}
	
	public int getThickness() {
		return _thickness;
	}
	
	public void setThickness(int thickness) {
		_thickness = thickness;
		super.fire();
	}
	
	public Point getP1() {
		return p1;
	}
	
	public Point getP2() {
		return p2;
	}
	
	public void setP1(Point p) {
		p1.x = p.x;
		p1.y = p.y;
		super.fire();
	}
	
	public void setP2(Point p) {
		p2.x = p.x;
		p2.y = p.y;
		super.fire();
	}
	
	@Override
	public int getLocationX() {
		return p1.x;
	}

	@Override
	public int getLocationY() {
		return p1.y;
	}

	@Override
	public int getWidth() {
		return p2.x;
	}

	@Override
	public int getHeight() {
		return p2.y;
	}
	
	@Override
	public void mimic(DShapeModel other) {
		super.mimic(other);
		_thickness = ((DLineModel) other).getThickness();
		p1 = ((DLineModel) other).getP1();
		p2 = ((DLineModel) other).getP2();
		fire();
	}
	
	@Override
	public String toString() {
		return "line";
	}
}

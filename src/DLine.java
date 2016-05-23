import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * @author Trinh Nguyen
 *
 */
public class DLine extends DShape {
	
	public DLine() {
		super.setShapeModel(new DLineModel());
	}
	
	public DLine(DShapeModel e) {
		super.setShapeModel(e);
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke oldStroke = g2.getStroke();
		DLineModel temp = (DLineModel) super.getShapeModel();
		g2.setColor(temp.getColor());
        g2.setStroke(new BasicStroke(temp.getThickness()));
        g2.draw(new Line2D.Float(temp.getP1(), temp.getP2()));
        g2.setStroke(oldStroke);
	}
	
	@Override
	public Rectangle getBounds() {
		DLineModel temp = (DLineModel) super.getShapeModel();
		int x = temp.getP1().x < temp.getP2().x ? temp.getP1().x : temp.getP2().x;
		int y = temp.getP1().y < temp.getP2().y ? temp.getP1().y : temp.getP2().y;
		int width = Math.abs(temp.getP1().x - temp.getP2().x);
		int height = Math.abs(temp.getP1().y - temp.getP2().y);
		
		if (Math.abs(temp.getP1().y - temp.getP2().y) < 12) {
			height = height + 18;
			y = y - 9;
		}
		if (Math.abs(temp.getP1().x - temp.getP2().x) < 12) {
			width = width + 18;
			x = x - 9;
		}
		
		return new Rectangle(x, y, width, height);
	}
	
	@Override
	public List<Point> getKnobs() {
		List<Point> temp = new ArrayList<Point>();
		DLineModel temp2 = (DLineModel) super.getShapeModel();
		temp.add(temp2.getP1());
		temp.add(temp2.getP2());
		return temp;
	}
	
	@Override
	public void moveShape(int deltaX, int deltaY) {
		DLineModel temp = (DLineModel) getShapeModel();
		temp.setP1(new Point(temp.getP1().x + deltaX, temp.getP1().y + deltaY));
		temp.setP2(new Point(temp.getP2().x + deltaX, temp.getP2().y + deltaY));
	}
	
	@Override
	public void resizeShape(int newX, int newY, int anchorX, int anchorY) {
		DLineModel temp = (DLineModel) getShapeModel();
		temp.setP1(new Point(anchorX, anchorY));
		temp.setP2(new Point(newX, newY));
	}
}

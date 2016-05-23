import java.awt.Graphics;

/**
 * @author Trinh Nguyen
 *
 */
public class DOval extends DShape {
	
	public DOval() {
		super.setShapeModel(new DOvalModel());
	}
	
	public DOval(DShapeModel e) {
		super.setShapeModel(e);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(super.getShapeModel().getColor());
		g.fillOval(super.getShapeModel().getLocationX(), super.getShapeModel().getLocationY(), super.getShapeModel().getWidth(), super.getShapeModel().getHeight());
	}
}

import java.awt.Graphics;

/**
 * @author Trinh Nguyen
 *
 */
public class DRect extends DShape {
	
	public DRect() {
		super.setShapeModel(new DRectModel());
	}
	
	public DRect(DShapeModel e) {
		super.setShapeModel(e);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(super.getShapeModel().getColor());
		g.fillRect(super.getShapeModel().getLocationX(), super.getShapeModel().getLocationY(), super.getShapeModel().getWidth(), super.getShapeModel().getHeight());
	}
}

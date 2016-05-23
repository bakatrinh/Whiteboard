import java.awt.Color;

/**
 * @author Trinh Nguyen
 *
 */
public class DRectModel extends DShapeModel {
	
	public DRectModel() {
		super();
	}

	public DRectModel(Color color, int x, int y, int width, int height) {
		super.setColor(color);
		super.setLocationX(x);
		super.setLocationY(y);
		super.setWidth(width);
		super.setHeight(height);
	}
	
	@Override
	public String toString() {
		return "rect";
	}
}

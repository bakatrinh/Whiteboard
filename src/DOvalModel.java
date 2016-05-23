import java.awt.Color;

/**
 * @author Trinh Nguyen
 *
 */
public class DOvalModel extends DShapeModel {
	
	public DOvalModel() {
		super();
	}

	public DOvalModel(Color color, int x, int y, int width, int height) {
		super.setColor(color);
		super.setLocationX(x);
		super.setLocationY(y);
		super.setWidth(width);
		super.setHeight(height);
	}
	
	@Override
	public String toString() {
		return "oval";
	}
}

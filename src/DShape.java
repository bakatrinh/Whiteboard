import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trinh Nguyen
 *
 */
public class DShape implements ModelListener {
	private DShapeModel _shapeModel;
	
	public DShape() {
		_shapeModel = new DShapeModel();
	}
	
	public DShape(DShapeModel shapeModel) {
		_shapeModel = shapeModel;
	}

	public DShapeModel getShapeModel() {
		return _shapeModel;
	}
	
	public void setShapeModel(DShapeModel shapeModel) {
		_shapeModel = shapeModel;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(_shapeModel.getLocationX(), _shapeModel.getLocationY(), _shapeModel.getWidth(), _shapeModel.getHeight());
	}
	
	public List<Point> getKnobs() {
		List<Point> temp = new ArrayList<Point>();
		temp.add(new Point(getShapeModel().getLocationX(), getShapeModel().getLocationY()));
		temp.add(new Point(getShapeModel().getLocationX(), getShapeModel().getLocationY() + getShapeModel().getHeight()));
		temp.add(new Point(getShapeModel().getLocationX() + getShapeModel().getWidth(), getShapeModel().getLocationY()));
		temp.add(new Point(getShapeModel().getLocationX() + getShapeModel().getWidth(), getShapeModel().getLocationY() + getShapeModel().getHeight()));
		return temp;
	}
	
	public void draw(Graphics g) {
		
	}
	
	public void moveShape(int deltaX, int deltaY) {
		int newX = getShapeModel().getLocationX() + deltaX;
		int newY = getShapeModel().getLocationY() + deltaY;
		getShapeModel().setLocationX(newX);
		getShapeModel().setLocationY(newY);
	}
	
	public void resizeShape(int newX, int newY, int anchorX, int anchorY) {
		if (newX < anchorX) {
			getShapeModel().setLocationX(newX);
			getShapeModel().setWidth(anchorX - newX);
		}
		else {
			getShapeModel().setLocationX(anchorX);
			getShapeModel().setWidth(newX - anchorX);
		}
		if (newY < anchorY) {
			getShapeModel().setLocationY(newY);
			getShapeModel().setHeight(anchorY - newY);
		}
		else {
			getShapeModel().setLocationY(anchorY);
			getShapeModel().setHeight(newY - anchorY);
		}
	}

	@Override
	public void modelChanged(DShapeModel model) {
		
	}

	@Override
	public void modelDeleted(DShapeModel model) {
		model.removeListener(this);
	}

	@Override
	public void modelAdded() {
		
	}
}

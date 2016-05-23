import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Trinh Nguyen
 *
 */
public class DShapeModel {
	private int _locationX;
	private int _locationY;
	private int _width;
	private int _height;
	private int _id;
	private Color _color;
	private List<ModelListener> _listeners = new ArrayList<ModelListener>();

	public DShapeModel() {
		_locationX = 0;
		_locationY = 0;
		_width = 0;
		_height = 0;
		_color = Color.GRAY;
	}

	public DShapeModel(Color color, int x, int y, int width, int height) {
		_color = color;
		_locationX = x;
		_locationY = y;
		_width = width;
		_height = height;
		_id = 0;
	}

	public DShapeModel(Color color, int x, int y, int width, int height, int id) {
		_color = color;
		_locationX = x;
		_locationY = y;
		_width = width;
		_height = height;
		_id = id;
	}

	public int getLocationX() {
		return _locationX;
	}

	public void setLocationX(int locationX) {
		this._locationX = locationX;
		fire();
	}

	public int getLocationY() {
		return _locationY;
	}

	public void setLocationY(int locationY) {
		this._locationY = locationY;
		fire();
	}

	public int getWidth() {
		return _width;
	}

	public void setWidth(int width) {
		this._width = width;
		fire();
	}

	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		this._height = height;
		fire();
	}

	public Color getColor() {
		return _color;
	}

	public void setColor(Color color) {
		this._color = color;
		fire();
	}

	public Rectangle getBounds() {
		return new Rectangle(_locationX, _locationY, _width, _height);
	}

	public int getID() {
		return _id;
	}

	public void setID(int id) {
		_id = id;
	}

	public void mimic(DShapeModel other) {
		_locationX = other.getLocationX();
		_locationY = other.getLocationY();
		_width = other.getWidth();
		_height = other.getHeight();
		_color = other.getColor();
		if (!(this instanceof DLineModel) || !(this instanceof DTextModel)) {
			fire();
		}
	}

	public void fire() {
		if (!_listeners.isEmpty()) {
			for (ModelListener listener : _listeners) {
				listener.modelChanged(this);
			}
		}
	}

	public void addListener(ModelListener listener) {
		_listeners.add(listener);
		for (ModelListener l : _listeners) {
			l.modelAdded();
		}
	}

	public void removeListener(ModelListener listener) {
		_listeners.remove(listener);
	}

	public void removeAllListeners() {
		if (!_listeners.isEmpty()) {
			Iterator<ModelListener> it = new ArrayList<ModelListener>(_listeners).iterator();
			while (it.hasNext()) {
				ModelListener e = it.next();
				try {
					e.modelDeleted(this);
				}
				catch (Exception ex) {
					
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "";
	}
}

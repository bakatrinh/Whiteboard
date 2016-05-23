import java.awt.Color;
import java.awt.Font;

/**
 * @author Trinh Nguyen
 *
 */
public class DTextModel extends DShapeModel {
	private String _text;
	private Font _font;

	public DTextModel() {
		super();
		_text = "Hello";
		_font = new Font("Dialog", Font.PLAIN, 1);
	}

	public DTextModel(Color color, int x, int y, int width, int height, String text, Font font) {
		super.setColor(color);
		super.setLocationX(x);
		super.setLocationY(y);
		super.setWidth(width);
		super.setHeight(height);
		_text = text;
		_font = font;
	}
	
	public String getText() {
		return _text;
	}
	
	public void setText(String text) {
		_text = text;
		super.fire();
	}
	
	public Font getFont() {
		return _font;
	}
	
	public void setFont(Font font) {
		_font = font;
		super.fire();
	}
	
	@Override
	public void mimic(DShapeModel other) {
		super.mimic(other);
		_text = ((DTextModel) other).getText();
		_font = ((DTextModel) other).getFont();
		fire();
	}
	
	@Override
	public String toString() {
		return "text";
	}
}

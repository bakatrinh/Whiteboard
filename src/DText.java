import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * @author Trinh Nguyen
 *
 */
public class DText extends DShape {
	private int _cacheHeight = 0;
	private String _cacheFont = "";

	public DText() {
		super.setShapeModel(new DTextModel());
	}

	public DText(DShapeModel e) {
		super.setShapeModel(e);
	}

	@Override
	public void draw(Graphics g) {
		DTextModel temp = (DTextModel) super.getShapeModel();
		Graphics2D g2 = (Graphics2D) g;
		if (_cacheHeight != super.getShapeModel().getHeight() || !_cacheFont.equals(temp.getFont().getFontName())) {
			computeFont(g2);
		}
		g2.setFont(temp.getFont());
		g2.setColor(temp.getColor());
		FontMetrics metrics = g2.getFontMetrics(temp.getFont());
		Shape oldClip = g2.getClip();
		g2.setClip(oldClip.getBounds().createIntersection(getBounds()));
		int d = metrics.getHeight() - metrics.getAscent();
		g2.drawString(temp.getText(), temp.getLocationX(), temp.getLocationY()+temp.getHeight() - d);
		g2.setClip(oldClip);
	}

	private void computeFont(Graphics2D g2) {
		DTextModel temp = (DTextModel) super.getShapeModel();
		boolean bigEnough = false;
		Font tempFont = null;
		double size = 1.0;
		while (!bigEnough) {
			tempFont = new Font(temp.getFont().getName(), temp.getFont().getStyle(), (int) size);
			FontMetrics metrics = g2.getFontMetrics(tempFont);
			int d = metrics.getHeight();
			if (d < temp.getHeight()) {
				size = (size*1.10)+1; 
			}
			else {
				bigEnough = true;
			}
		}
		temp.setFont(tempFont);
		_cacheHeight = super.getShapeModel().getHeight();
		_cacheFont = tempFont.getName();
	}
}

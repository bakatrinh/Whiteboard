import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Trinh Nguyen
 *
 */
public class ToolColorBox extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel _colorBox;
	private MainController _mainController;
	public ToolColorBox(MainController mainController) {
		_mainController = mainController;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		Component horizontalStrut = Box.createHorizontalStrut(5);
		add(horizontalStrut);

		_colorBox = new JPanel();
		_colorBox.setMaximumSize(new Dimension(25, 25));
		_colorBox.setPreferredSize(new Dimension(25, 25));
		_colorBox.setMinimumSize(new Dimension(25, 25));
		_colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		_colorBox.setBackground(Color.GRAY);
		_colorBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!_mainController.getIsClient()) {
					Color newColor = JColorChooser.showDialog(
							_mainController.getWhiteboard(),
							"Choose Color",
							_colorBox.getBackground());
					if (newColor != null) {
						_mainController.colorChanged(newColor);
					}
				}
			}
		});
		_colorBox.setLayout(new GridLayout(1, 1, 0, 0));
		_colorBox.setFocusable(false);

		JLabel lblPrimary = new JLabel("Color: ");
		add(lblPrimary);

		add(_colorBox);

		Component horizontalStrut2 = Box.createHorizontalStrut(5);
		add(horizontalStrut2);
	}

	public void setColor(Color currentColor) {
		_colorBox.setBackground(currentColor);
	}

	public Color getColor() {
		return _colorBox.getBackground();
	}
}

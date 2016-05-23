import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

/**
 * @author Trinh Nguyen
 *
 */
public class ToolOptionSelect extends JPanel {

	private static final long serialVersionUID = 1L;
	private MainController _mainController;
	private ToolColorBox _toolColorBox;
	
	public ToolOptionSelect(MainController mainController) {
		_mainController = mainController;
		_toolColorBox = new ToolColorBox(_mainController);
		setBorder(BorderFactory.createTitledBorder(""));
		_toolColorBox.setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(_toolColorBox);
	}
	
	public void updateColor(Color primary) {
		_toolColorBox.setColor(primary);
	}
	
	public Color getColor() {
		return _toolColorBox.getColor();
	}
}

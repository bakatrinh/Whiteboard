import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

/**
 * @author Trinh Nguyen
 *
 */
public class ToolOptionOval extends JPanel {

	private static final long serialVersionUID = 1L;
	private MainController _mainController;
	private ToolColorBox _toolColorBox;
	private JButton _btnDone;
	private JButton _btnAddShape;

	public ToolOptionOval(MainController mainController) {
		_mainController = mainController;
		_toolColorBox = new ToolColorBox(_mainController);
		setBorder(BorderFactory.createTitledBorder(""));
		_toolColorBox.setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(_toolColorBox);
		
		_btnAddShape = new JButton("Add Shape");
		_btnAddShape.setFocusable(false);
		_btnAddShape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.addShapeButton(_toolColorBox.getColor());
			}
		});
		add(_btnAddShape);
		
		_btnDone = new JButton("Done");
		_btnDone.setFocusable(false);
		_btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.doneButtonClicked();
			}
		});
		add(_btnDone);
	}

	public void updateColor(Color primary) {
		_toolColorBox.setColor(primary);
	}
	
	public void setBtnAddShapeEnabled(boolean b) {
		_btnAddShape.setEnabled(b);
	}
	
	public Color getColor() {
		return _toolColorBox.getColor();
	}
}

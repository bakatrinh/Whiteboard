import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Trinh Nguyen
 *
 */
public class ToolOptionLine extends JPanel {

	private static final long serialVersionUID = 1L;
	private MainController _mainController;
	private ToolColorBox _toolColorBox;
	private JButton _btnDone;
	private JSpinner _spinner;

	public ToolOptionLine(MainController mainController) {
		_mainController = mainController;
		_toolColorBox = new ToolColorBox(_mainController);
		setBorder(BorderFactory.createTitledBorder(""));
		_toolColorBox.setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(_toolColorBox);

		JLabel thickness = new JLabel("Thickness");
		add(thickness);
		_spinner = new JSpinner();
		_spinner.setModel(new javax.swing.SpinnerNumberModel(2, 1, 100, 1));
		_spinner.setMaximumSize(new Dimension(60, 25));
		_spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_mainController.thicknessChanged((int) _spinner.getValue());
			}
		});
		add(_spinner);
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
	
	public Color getColor() {
		return _toolColorBox.getColor();
	}

	public int getThickness() {
		return (int) _spinner.getValue();
	}

	public void setThickness(int value) {
		_spinner.setValue(value);
	}
	
	public void setThicknessEnabled(boolean b) {
		_spinner.setEnabled(b);
	}
}

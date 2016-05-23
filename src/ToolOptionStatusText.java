import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

/**
 * @author Trinh Nguyen
 *
 */
public class ToolOptionStatusText extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel _statusText;
	public ToolOptionStatusText() {
		setMaximumSize(new Dimension(10000, 25));
		setPreferredSize(new Dimension(0, 25));
		setMinimumSize(new Dimension(0, 25));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		_statusText = new JLabel("");
		add(_statusText);
	}

	public void setStatusText(String text) {
		_statusText.setText(text);
	}
}
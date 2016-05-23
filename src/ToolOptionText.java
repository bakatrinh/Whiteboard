import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JComboBox;

/**
 * @author Trinh Nguyen
 *
 */
public class ToolOptionText extends JPanel {

	private static final long serialVersionUID = 1L;
	private MainController _mainController;
	private ToolColorBox _toolColorBox;
	private JButton _btnDone;
	private JTextField _textField;
	private JComboBox<Font> _fontComboBox;

	public ToolOptionText(MainController mainController) {
		_mainController = mainController;
		_toolColorBox = new ToolColorBox(_mainController);
		setBorder(BorderFactory.createTitledBorder(""));
		_toolColorBox.setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(_toolColorBox);

		JLabel thickness = new JLabel("Text:");
		add(thickness);

		_textField = new JTextField("Hello");
		_textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				_mainController.textChanged(_textField.getText());
			}
		});
		add(_textField);
		_textField.setColumns(10);
		_textField.setMaximumSize(new Dimension(160, 25));

		GraphicsEnvironment graphEnviron = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] allFonts = graphEnviron.getAllFonts();
		
		_fontComboBox = new JComboBox<Font>(allFonts);
		_fontComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value != null) {
					Font font = (Font) value;
					value = font.getName();
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		_fontComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Font font = (Font) _fontComboBox.getSelectedItem();
				String fontName = font.getName();
				_mainController.fontChanged(fontName);
			}
		});
		for (int index = 0; index < _fontComboBox.getItemCount(); index++) {
			Font temp = (Font) _fontComboBox.getItemAt(index);
			String fontName = temp.getName().toLowerCase();
			if (fontName.contains("dialog")) {
				_fontComboBox.setSelectedIndex(index);
				break;
			}
		}
		
		JLabel font = new JLabel("Font:");

		_btnDone = new JButton("Done");
		_btnDone.setFocusable(false);
		_btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.doneButtonClicked();
			}
		});
		JPanel comboBoxContainer = new JPanel();
		comboBoxContainer.setLayout(new BoxLayout(comboBoxContainer, BoxLayout.X_AXIS));
		_fontComboBox.setMaximumSize(new Dimension(230, 25));
		_fontComboBox.setPreferredSize(new Dimension(80, 25));
		_fontComboBox.setMinimumSize(new Dimension(80, 25));
		add(font);
		comboBoxContainer.add(_fontComboBox);
		add(comboBoxContainer);
		add(_btnDone);
	}

	public void updateColor(Color primary) {
		_toolColorBox.setColor(primary);
	}
	
	public Color getColor() {
		return _toolColorBox.getColor();
	}

	public JComboBox getFontComboBox() {
		return _fontComboBox;
	}

	public JTextField getTextField() {
		return _textField;
	}
	
	public void setTextFieldEnabled(boolean b) {
		_textField.setEnabled(b);
	}
	
	public void setFontComboBoxEnabled(boolean b) {
		_fontComboBox.setEnabled(b);
	}
	
	public String getFontName() {
		Font font = (Font) _fontComboBox.getSelectedItem();
		return font.getName();
	}
	
	public void setFont(String fontName) {
		for (int index = 0; index < _fontComboBox.getItemCount(); index++) {
			Font temp = (Font) _fontComboBox.getItemAt(index);
			String font = temp.getName().toLowerCase();
			if (font.equals(fontName.toLowerCase())) {
				_fontComboBox.setSelectedIndex(index);
				break;
			}
		}
	}
}

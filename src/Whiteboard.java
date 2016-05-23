import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import java.awt.GridLayout;
import java.awt.Rectangle;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Component;
import java.awt.Dialog;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @author Trinh Nguyen
 *
 */
public class Whiteboard extends JFrame {

	private static final long serialVersionUID = 1L;
	private MainController _mainController;
	private CardLayout _toolsCardLayout;
	private JPanel _toolsOptionsPanel;
	private ToolOptionStatusText _toolOptionStatusText;
	private Canvas _canvas;
	private ToolOptionSelect _toolSelect;
	private ToolOptionRect _toolRect;
	private ToolOptionOval _toolOval;
	private ToolOptionLine _toolLine;
	private ToolOptionText _toolText;
	private Border _lineBorders;
	private JButton _btnRect;
	private JButton _btnOval;
	private JButton _btnLine;
	private JButton _btnText;
	private JTable _shapesJTable;
	private JButton _btnMoveFront;
	private JButton _btnMoveBack;
	private JButton _btnRemoveShape;
	private JButton _btnOpen;
	private JButton _btnSave;
	private JButton _btnSize;
	private JLabel _connectionModeStatus;
	private JFileChooser _fileChooser;
	private JFileChooser _fileChooserImage;
	private JSpinner _widthSpinner;
	private JSpinner _heightSpinner;

	public Whiteboard(MainController mainController) {
		_mainController = mainController;
		_canvas = new Canvas();
		_mainController.linkWhiteBoard(this, _canvas);

		_fileChooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		_fileChooser.setCurrentDirectory(workingDirectory);
		_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		_fileChooser.setFileFilter(new FileNameExtensionFilter("XML File", "xml"));

		_fileChooserImage = new JFileChooser();
		_fileChooserImage.setCurrentDirectory(workingDirectory);
		_fileChooserImage.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		_fileChooserImage.setFileFilter(new FileNameExtensionFilter("PNG File", "png"));

		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel _mainCenter = new JPanel();
		getContentPane().add(_mainCenter, BorderLayout.CENTER);
		_mainCenter.setLayout(new BorderLayout(0, 0));

		_toolsOptionsPanel = new JPanel();
		_toolsOptionsPanel.setMaximumSize(new Dimension(10000, 30));
		_toolsOptionsPanel.setPreferredSize(new Dimension(0, 30));
		_toolsOptionsPanel.setMinimumSize(new Dimension(0, 30));
		_mainCenter.add(_toolsOptionsPanel, BorderLayout.NORTH);
		_toolsOptionsPanel.setLayout(new CardLayout(0, 0));

		_toolRect = new ToolOptionRect(_mainController);
		_toolOval = new ToolOptionOval(_mainController);
		_toolLine = new ToolOptionLine(_mainController);
		_toolText = new ToolOptionText(_mainController);
		_toolSelect = new ToolOptionSelect(_mainController);

		_toolsOptionsPanel.add(_toolRect, "RECT");
		_toolsOptionsPanel.add(_toolOval, "OVAL");
		_toolsOptionsPanel.add(_toolLine, "LINE");
		_toolsOptionsPanel.add(_toolText, "TEXT");
		_toolsOptionsPanel.add(_toolSelect, "SELECT");

		JPanel _mainCenterJPanel = new JPanel();
		JPanel _mainCenterJPanelContainer = new JPanel();
		_mainCenterJPanelContainer.setLayout(new BorderLayout(10, 10));
		_mainCenterJPanelContainer.add(_mainCenterJPanel, BorderLayout.CENTER);
		_mainCenterJPanel.setLayout(new BoxLayout(_mainCenterJPanel, BoxLayout.Y_AXIS));

		_mainCenterJPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		_mainCenterJPanel.add(Box.createVerticalGlue());

		JPanel _canvasContainer = new JPanel();
		_canvasContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		_canvasContainer.setMaximumSize(_canvas.getSize());
		_canvasContainer.setLayout(new BorderLayout(0, 0));
		_canvasContainer.add(_canvas, BorderLayout.CENTER);

		_mainCenterJPanel.add(_canvasContainer);
		_mainCenterJPanel.add(Box.createVerticalGlue());

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		_mainCenterJPanelContainer.add(horizontalStrut_1, BorderLayout.EAST);

		Component verticalStrut_1 = Box.createVerticalStrut(20);
		_mainCenterJPanelContainer.add(verticalStrut_1, BorderLayout.SOUTH);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		_mainCenterJPanelContainer.add(horizontalStrut, BorderLayout.WEST);

		_toolOptionStatusText = new ToolOptionStatusText();
		_mainCenterJPanelContainer.add(_toolOptionStatusText, BorderLayout.NORTH);

		JScrollPane _mainCenterJPanelContainerScrollPane = new JScrollPane();
		_mainCenter.add(_mainCenterJPanelContainerScrollPane, BorderLayout.CENTER);

		_mainCenterJPanelContainerScrollPane.setViewportView(_mainCenterJPanelContainer);

		JPanel _westJpanel = new JPanel();
		getContentPane().add(_westJpanel, BorderLayout.WEST);
		_westJpanel.setLayout(new BorderLayout(0, 0));

		JPanel _toolsJPanel = new JPanel();
		_westJpanel.add(_toolsJPanel, BorderLayout.NORTH);
		_toolsJPanel.setLayout(new GridLayout(0, 2, 2, 2));

		_btnRect = new JButton("Rect");
		_btnRect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.setToolRect();
			}
		});
		_btnRect.setFocusable(false);
		_toolsJPanel.add(_btnRect);

		_btnOval = new JButton("Oval");
		_btnOval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.setToolOval();
			}
		});
		_btnOval.setFocusable(false);
		_toolsJPanel.add(_btnOval);

		_btnLine = new JButton("Line");
		_btnLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.setToolLine();
			}
		});
		_btnLine.setFocusable(false);
		_toolsJPanel.add(_btnLine);

		_btnText = new JButton("Text");
		_btnText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.setToolText();
			}
		});
		_btnText.setFocusable(false);
		_toolsJPanel.add(_btnText);

		_toolsJPanel.setBorder(BorderFactory.createTitledBorder("Tools"));

		JPanel _selectedShapesJPanel = new JPanel();
		_westJpanel.add(_selectedShapesJPanel, BorderLayout.CENTER);

		// adjusts the width of the tools panel here
		_selectedShapesJPanel.setPreferredSize(new Dimension(300, 0));

		_selectedShapesJPanel.setBorder(BorderFactory.createTitledBorder("Shapes List"));

		_selectedShapesJPanel.setLayout(new BorderLayout(0, 2));

		JPanel _selectedShapesJPanelCenter = new JPanel();
		_selectedShapesJPanel.add(_selectedShapesJPanelCenter, BorderLayout.CENTER);
		_lineBorders = BorderFactory.createLineBorder(Color.BLACK, 1);
		_selectedShapesJPanelCenter.setLayout(new BorderLayout(2, 2));

		/**
		 * 
		 * @author Trinh Nguyen Table Model used to display all the shapes. Only
		 *         single shapes data values are updated when changed (it is
		 *         added as a listener to each shapes). This is done to maintain
		 *         efficiency.
		 */
		class ShapesTableModel extends AbstractTableModel implements ModelListener {

			private static final long serialVersionUID = 1L;
			private List<DShape> _DShapes;

			public ShapesTableModel(List<DShape> DShapes) {
				_DShapes = DShapes;
			}

			public void addRow() {
				fireTableRowsInserted(_DShapes.size() - 1, _DShapes.size() - 1);
			}

			public void deleteRow(int index) {
				fireTableRowsDeleted(index, index);
			}

			@Override
			public int getRowCount() {
				return _DShapes.size();
			}

			@Override
			public int getColumnCount() {
				return 5;
			}

			@Override
			public String getColumnName(int column) {
				String name = "";
				switch (column) {
				case 0:
					name = "type";
					break;
				case 1:
					name = "x1";
					break;
				case 2:
					name = "y1";
					break;
				case 3:
					name = "x2/width";
					break;
				case 4:
					name = "y2/height";
					break;
				}
				return name;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				Class type = String.class;
				switch (columnIndex) {
				default:
					type = Integer.class;
					break;
				}
				return type;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				DShapeModel tempShape = _DShapes.get(rowIndex).getShapeModel();
				Object value = null;
				switch (columnIndex) {
				case 0:
					value = tempShape;
					break;
				case 1:
					value = tempShape.getLocationX();
					break;
				case 2:
					value = tempShape.getLocationY();
					break;
				case 3:
					value = tempShape.getWidth();
					break;
				case 4:
					value = tempShape.getHeight();
					break;
				}
				return value;
			}

			@Override
			public void modelChanged(DShapeModel model) {
				int rowNum = 0;
				for (DShape e : _DShapes) {
					if (model.getID() == e.getShapeModel().getID()) {
						fireTableRowsUpdated(rowNum, rowNum);
						break;
					}
					rowNum++;
				}
			}

			@Override
			public void modelAdded() {
				addRow();
			}

			@Override
			public void modelDeleted(DShapeModel model) {
				int index = 0;

				Iterator<DShape> it = _DShapes.iterator();
				while (it.hasNext()) {
					DShape e = it.next();
					try {
						if (e.getShapeModel().getID() == model.getID()) {
							break;
						}
						index++;
					} catch (Exception ex) {
					}
				}
				deleteRow(index);
				model.removeListener(this);
			}
		}

		ShapesTableModel shapesTableModel = new ShapesTableModel(_mainController.getDShapes());
		_shapesJTable = new JTable(shapesTableModel);

		_shapesJTable.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (_mainController.getCurrentViewingShape() != null) {
					selectTableObject(_mainController.getCurrentViewingShape().getShapeModel().getID());
				}
			}
		});
		_shapesJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_shapesJTable.setFocusable(false);

		_shapesJTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				int index = _shapesJTable.getSelectedRow();
				if (index != -1) {
					_mainController.tableSelectedShape(index);
				}
			}
		});

		((DefaultTableCellRenderer) _shapesJTable.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.RIGHT);
		
		// Sets the "type" column of the table to max size 45 and left aligned
		TableColumn column = null;
		column = _shapesJTable.getColumnModel().getColumn(0);
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		column.setCellRenderer(leftRenderer);
		column.setMaxWidth(40);
		
		column = _shapesJTable.getColumnModel().getColumn(1);
		column.setMaxWidth(55);
		column = _shapesJTable.getColumnModel().getColumn(2);
		column.setMaxWidth(55);
		column = _shapesJTable.getColumnModel().getColumn(3);
		column.setMaxWidth(80);
		column = _shapesJTable.getColumnModel().getColumn(4);
		column.setMaxWidth(80);

		JScrollPane _shapesScrollPane = new JScrollPane(_shapesJTable);
		_selectedShapesJPanelCenter.add(_shapesScrollPane);
		_shapesScrollPane.setPreferredSize(new Dimension(0, 400));
		_shapesScrollPane.setBorder(_lineBorders);

		JPanel _shapesAdjustmentJPanel = new JPanel();
		_selectedShapesJPanel.add(_shapesAdjustmentJPanel, BorderLayout.SOUTH);
		_shapesAdjustmentJPanel.setLayout(new GridLayout(2, 1, 2, 2));

		JPanel _shapesAdjustmentJPanelNorth = new JPanel();
		_shapesAdjustmentJPanel.add(_shapesAdjustmentJPanelNorth);
		_shapesAdjustmentJPanelNorth.setLayout(new GridLayout(1, 0, 2, 2));

		_btnMoveFront = new JButton("Move Front");
		_btnMoveFront.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_mainController.isEditingShape() && !_mainController.getIsClient()) {
					_mainController.shapeShiftFront(_mainController.getCurrentViewingShape().getShapeModel().getID());
				}
			}
		});
		_btnMoveFront.setFocusable(false);
		_shapesAdjustmentJPanelNorth.add(_btnMoveFront);

		_btnMoveBack = new JButton("Move Back");
		_btnMoveBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_mainController.isEditingShape() && !_mainController.getIsClient()) {
					_mainController.shapeShiftBack(_mainController.getCurrentViewingShape().getShapeModel().getID());
				}
			}
		});
		_btnMoveBack.setFocusable(false);
		_shapesAdjustmentJPanelNorth.add(_btnMoveBack);

		JPanel _shapesAdjustmentJPanelCenter = new JPanel();
		_shapesAdjustmentJPanel.add(_shapesAdjustmentJPanelCenter);
		_shapesAdjustmentJPanelCenter.setLayout(new GridLayout(1, 0, 2, 2));

		JDialog canvasOptionsPopup = new JDialog(this, "", Dialog.ModalityType.DOCUMENT_MODAL);
		((JPanel)canvasOptionsPopup.getContentPane()).setBorder(_lineBorders);
		canvasOptionsPopup.setResizable(false);
		canvasOptionsPopup.setModal(true);
		canvasOptionsPopup.setUndecorated(true);
		canvasOptionsPopup.setLocationRelativeTo(this);
		canvasOptionsPopup.pack();
		canvasOptionsPopup.setSize(new Dimension(560, 34));
		canvasOptionsPopup.setLayout(new BorderLayout(0, 0));
		
		JPanel canvasOptionsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		JLabel lblwidth = new JLabel("Width:");
		canvasOptionsContainer.add(lblwidth);

		_widthSpinner = new JSpinner();
		_widthSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if ((int) _widthSpinner.getValue() >= 100 && (int) _widthSpinner.getValue() <= 1000
						&& (int) _heightSpinner.getValue() >= 100 && (int) _heightSpinner.getValue() <= 1000) {
					_mainController.setCanvasSize((int) _widthSpinner.getValue(), (int) _heightSpinner.getValue());
				}
			}
		});
		_widthSpinner.setModel(new javax.swing.SpinnerNumberModel(400, 100, 1000, 20));
		_widthSpinner.setMaximumSize(new Dimension(40, 25));
		canvasOptionsContainer.add(_widthSpinner);

		JLabel lblheight = new JLabel("Height:");
		canvasOptionsContainer.add(lblheight);

		_heightSpinner = new JSpinner();
		_heightSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if ((int) _widthSpinner.getValue() >= 100 && (int) _widthSpinner.getValue() <= 1000
						&& (int) _heightSpinner.getValue() >= 100 && (int) _heightSpinner.getValue() <= 1000) {
					_mainController.setCanvasSize((int) _widthSpinner.getValue(), (int) _heightSpinner.getValue());
				}
			}
		});
		_heightSpinner.setModel(new javax.swing.SpinnerNumberModel(400, 100, 1000, 20));
		_heightSpinner.setMaximumSize(new Dimension(40, 25));
		canvasOptionsContainer.add(_heightSpinner);

		JButton btnJDialogCanvasClearClose = new JButton("Clear Canvas");
		btnJDialogCanvasClearClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.clear();
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, canvasOptionsPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - canvasOptionsPopup.getSize().width) / 2;
				int y = r.y + r.height - canvasOptionsPopup.getSize().height - 20;
				canvasOptionsPopup.setLocation(x, y);
				canvasOptionsPopup.setVisible(false);
			}
		});
		canvasOptionsContainer.add(btnJDialogCanvasClearClose);

		JButton btnJDialogCanvasOptionsClose = new JButton("Close");
		btnJDialogCanvasOptionsClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((int) _widthSpinner.getValue() >= 100 && (int) _widthSpinner.getValue() <= 1000
						&& (int) _heightSpinner.getValue() >= 100 && (int) _heightSpinner.getValue() <= 1000) {
					_mainController.setCanvasSize((int) _widthSpinner.getValue(), (int) _heightSpinner.getValue());
				}

				canvasOptionsPopup.setVisible(false);
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, canvasOptionsPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - canvasOptionsPopup.getSize().width) / 2;
				int y = r.y + r.height - canvasOptionsPopup.getSize().height - 20;
				canvasOptionsPopup.setLocation(x, y);
			}
		});
		canvasOptionsContainer.add(btnJDialogCanvasOptionsClose);
		canvasOptionsPopup.getRootPane().setDefaultButton(btnJDialogCanvasOptionsClose);

		_btnSize = new JButton("Canvas");
		_btnSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_mainController.getIsClient()) {
					JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, canvasOptionsPopup);
					Rectangle r = frame.getBounds();
					int x = r.x + (r.width - canvasOptionsPopup.getSize().width) / 2;
					int y = r.y + r.height - canvasOptionsPopup.getSize().height - 20;
					canvasOptionsPopup.setLocation(x, y);
					canvasOptionsPopup.setVisible(true);
				}
			}
		});
		_btnSize.setFocusable(false);
		_shapesAdjustmentJPanelCenter.add(_btnSize);
		
		canvasOptionsPopup.add(canvasOptionsContainer, BorderLayout.CENTER);

		_btnRemoveShape = new JButton("Remove Shape");
		_btnRemoveShape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_mainController.isEditingShape() && !_mainController.getIsClient()) {
					_mainController.removeShape(_mainController.getCurrentViewingShape().getShapeModel().getID());
				}
			}
		});
		_btnRemoveShape.setFocusable(false);
		_shapesAdjustmentJPanelCenter.add(_btnRemoveShape);

		JPanel _shapesAdjustmentJPanelSouthContainer = new JPanel();
		_shapesAdjustmentJPanelSouthContainer.setLayout(new BorderLayout(0, 0));
		JPanel _shapesAdjustmentJPanelSouth = new JPanel();
		_shapesAdjustmentJPanelSouth.setBorder(BorderFactory.createTitledBorder("Options"));
		_shapesAdjustmentJPanelSouthContainer.add(_shapesAdjustmentJPanelSouth, BorderLayout.CENTER);
		_westJpanel.add(_shapesAdjustmentJPanelSouthContainer, BorderLayout.SOUTH);
		_shapesAdjustmentJPanelSouth.setLayout(new GridLayout(1, 1, 2, 2));

		JPanel _shapesAdjustmentJPanelSouthNorth = new JPanel();
		_shapesAdjustmentJPanelSouth.add(_shapesAdjustmentJPanelSouthNorth);
		_shapesAdjustmentJPanelSouthNorth.setLayout(new GridLayout(3, 2, 2, 2));

		_btnOpen = new JButton("Open");
		_btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_mainController.getIsClient()) {
					_mainController.openFile();
				}
			}
		});
		_btnOpen.setFocusable(false);
		_shapesAdjustmentJPanelSouthNorth.add(_btnOpen);

		_btnSave = new JButton("Save");
		_btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.saveFile();
			}
		});
		_btnSave.setFocusable(false);
		_shapesAdjustmentJPanelSouthNorth.add(_btnSave);

		JButton btnStartServer = new JButton("Server Start");
		btnStartServer.setFocusable(false);
		_shapesAdjustmentJPanelSouthNorth.add(btnStartServer);

		JButton btnStartClient = new JButton("Client Start");
		btnStartClient.setFocusable(false);
		_shapesAdjustmentJPanelSouthNorth.add(btnStartClient);

		JPanel serverStatusContainer = new JPanel();
		serverStatusContainer.setLayout(new BoxLayout(serverStatusContainer, BoxLayout.X_AXIS));
		_connectionModeStatus = new JLabel(" ");
		serverStatusContainer.add(_connectionModeStatus);

		JDialog startServerPopup = new JDialog(this, "", Dialog.ModalityType.DOCUMENT_MODAL);
		((JPanel)startServerPopup.getContentPane()).setBorder(_lineBorders);
		startServerPopup.setResizable(false);
		startServerPopup.setModal(true);
		startServerPopup.setUndecorated(true);
		startServerPopup.setLocationRelativeTo(this);
		startServerPopup.setSize(new Dimension(370, 34));
		startServerPopup.setLayout(new BorderLayout(0, 0));

		JPanel startServerPopupContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		JLabel lblPort = new JLabel("Port:");
		startServerPopupContainer.add(lblPort);

		JTextField portTextField = new JTextField();
		portTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String x = portTextField.getText();
				x = x.replaceAll("[^0-9]", "");
				if (x.length() > 5) {
					x = x.substring(0, 5);
				}
				portTextField.setText(x);
			}
		});
		portTextField.setText("39531");
		startServerPopupContainer.add(portTextField);
		portTextField.setColumns(10);

		JButton btnJDialogServerStart = new JButton("Start");
		btnJDialogServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_mainController.getIsClient() && !portTextField.getText().equals("")) {
					int port = Integer.parseInt(portTextField.getText().trim());
					_mainController.startServerMode(port);
				}
				startServerPopup.setVisible(false);
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, startServerPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - startServerPopup.getSize().width) / 2;
				int y = r.y + r.height - startServerPopup.getSize().height - 20;
				startServerPopup.setLocation(x, y);
			}
		});
		startServerPopupContainer.add(btnJDialogServerStart);

		JButton btnJDialogServerCancel = new JButton("Cancel");
		btnJDialogServerCancel.setFocusable(false);
		btnJDialogServerCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startServerPopup.setVisible(false);
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, startServerPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - startServerPopup.getSize().width) / 2;
				int y = r.y + r.height - startServerPopup.getSize().height - 20;
				startServerPopup.setLocation(x, y);
			}
		});
		startServerPopupContainer.add(btnJDialogServerCancel);
		startServerPopup.getRootPane().setDefaultButton(btnJDialogServerStart);

		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, startServerPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - startServerPopup.getSize().width) / 2;
				int y = r.y + r.height - startServerPopup.getSize().height - 20;
				startServerPopup.setLocation(x, y);
				portTextField.requestFocus();
				portTextField.selectAll();
				startServerPopup.setVisible(true);
			}
		});
		
		startServerPopup.add(startServerPopupContainer, BorderLayout.CENTER);
		

		JDialog startClientPopup = new JDialog(this, "", Dialog.ModalityType.DOCUMENT_MODAL);
		((JPanel)startClientPopup.getContentPane()).setBorder(_lineBorders);
		startClientPopup.setResizable(false);
		startClientPopup.setModal(true);
		startClientPopup.setUndecorated(true);
		startClientPopup.setLocationRelativeTo(this);
		startClientPopup.pack();
		startClientPopup.setSize(new Dimension(440, 34));
		startClientPopup.setLayout(new BorderLayout(0, 0));
		
		JPanel startClientPopupContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		JLabel lblAddress = new JLabel("Address:");
		startClientPopupContainer.add(lblAddress);

		JTextField addressTextField = new JTextField();
		addressTextField.setText("127.0.0.1:39531");
		startClientPopupContainer.add(addressTextField);
		addressTextField.setColumns(14);

		JButton btnJDialogClientStart = new JButton("Start");
		btnJDialogClientStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_mainController.getIsServer() && !addressTextField.getText().equals("")
						&& addressTextField.getText().contains(":")) {
					_mainController.clear();
					String[] parts = addressTextField.getText().split(":");
					_mainController.startClientMode(parts[0].trim(), Integer.parseInt(parts[1].trim()));
				}
				startClientPopup.setVisible(false);
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, startClientPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - startClientPopup.getSize().width) / 2;
				int y = r.y + r.height - startClientPopup.getSize().height - 20;
				startClientPopup.setLocation(x, y);
			}
		});
		startClientPopupContainer.add(btnJDialogClientStart);

		JButton btnJDialogClientCancel = new JButton("Cancel");
		btnJDialogClientCancel.setFocusable(false);
		btnJDialogClientCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startClientPopup.setVisible(false);
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, startClientPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - startClientPopup.getSize().width) / 2;
				int y = r.y + r.height - startClientPopup.getSize().height - 20;
				startClientPopup.setLocation(x, y);
			}
		});
		startClientPopupContainer.add(btnJDialogClientCancel);
		startClientPopup.getRootPane().setDefaultButton(btnJDialogClientStart);

		btnStartClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, startClientPopup);
				Rectangle r = frame.getBounds();
				int x = r.x + (r.width - startClientPopup.getSize().width) / 2;
				int y = r.y + r.height - startClientPopup.getSize().height - 20;
				startClientPopup.setLocation(x, y);
				addressTextField.requestFocus();
				addressTextField.selectAll();
				startClientPopup.setVisible(true);
			}
		});
		
		startClientPopup.add(startClientPopupContainer, BorderLayout.CENTER);

		
		JButton btnSavePNG = new JButton("Save PNG...");
		btnSavePNG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_mainController.saveImage();
			}
		});
		btnSavePNG.setFocusable(false);
		_shapesAdjustmentJPanelSouthNorth.add(btnSavePNG);

		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setFocusable(false);
		_shapesAdjustmentJPanelSouthNorth.add(btnExit);

		_shapesAdjustmentJPanelSouthContainer.add(serverStatusContainer, BorderLayout.SOUTH);
		showSelectToolsOption();
	}

	public void showRectToolsOption() {
		_toolsCardLayout = (CardLayout) (_toolsOptionsPanel.getLayout());
		_toolsCardLayout.show(_toolsOptionsPanel, "RECT");
		ClearSelectedToolsButtons();
		_btnRect.setSelected(true);
	}

	public void showOvalToolsOption() {
		_toolsCardLayout = (CardLayout) (_toolsOptionsPanel.getLayout());
		_toolsCardLayout.show(_toolsOptionsPanel, "OVAL");
		ClearSelectedToolsButtons();
		_btnOval.setSelected(true);
	}

	public void showLineToolsOption() {
		_toolsCardLayout = (CardLayout) (_toolsOptionsPanel.getLayout());
		_toolsCardLayout.show(_toolsOptionsPanel, "LINE");
		ClearSelectedToolsButtons();
		_btnLine.setSelected(true);
	}

	public void showTextToolsOption() {
		_toolsCardLayout = (CardLayout) (_toolsOptionsPanel.getLayout());
		_toolsCardLayout.show(_toolsOptionsPanel, "TEXT");
		ClearSelectedToolsButtons();
		_btnText.setSelected(true);
	}

	public void showSelectToolsOption() {
		_mainController.deselectShape();
		_toolsCardLayout = (CardLayout) (_toolsOptionsPanel.getLayout());
		_toolsCardLayout.show(_toolsOptionsPanel, "SELECT");
		ClearSelectedToolsButtons();
	}

	public void showSelectToolsJTableOption() {
		_toolsCardLayout = (CardLayout) (_toolsOptionsPanel.getLayout());
		_toolsCardLayout.show(_toolsOptionsPanel, "SELECT");
		ClearSelectedToolsButtons();
	}

	public void ClearSelectedToolsButtons() {
		_btnRect.setSelected(false);
		_btnOval.setSelected(false);
		_btnLine.setSelected(false);
		_btnText.setSelected(false);
	}

	public ToolOptionSelect getToolSelect() {
		return _toolSelect;
	}

	public ToolOptionRect getToolRect() {
		return _toolRect;
	}

	public ToolOptionOval getToolOval() {
		return _toolOval;
	}

	public ToolOptionLine getToolLine() {
		return _toolLine;
	}

	public ToolOptionText getToolText() {
		return _toolText;
	}

	public JButton getBtnRect() {
		return _btnRect;
	}

	public JButton getBtnOval() {
		return _btnOval;
	}

	public JButton getBtnLine() {
		return _btnLine;
	}

	public JButton getBtnText() {
		return _btnText;
	}

	public void setStatusText(String text) {
		_toolOptionStatusText.setStatusText(text);
	}

	public JTable getShapesJTable() {
		return _shapesJTable;
	}

	public void selectTableObject(int id) {
		int index = 0;

		Iterator<DShape> it = _mainController.getDShapes().iterator();
		while (it.hasNext()) {
			DShape e = it.next();
			try {
				if (id == e.getShapeModel().getID()) {
					_shapesJTable.scrollRectToVisible(_shapesJTable.getCellRect(index, 0, true));
					_shapesJTable.setRowSelectionInterval(index, index);
					break;
				}
				index++;
			} catch (Exception ex) {
			}
		}

	}

	public void setConnectionModeStatus(String text) {
		_connectionModeStatus.setText(text);
	}

	public JFileChooser getFileChooser() {
		return _fileChooser;
	}

	public JFileChooser getFileChooserImage() {
		return _fileChooserImage;
	}

	public Dimension getCanvasSize() {
		return new Dimension((int) _widthSpinner.getValue(), (int) _heightSpinner.getValue());
	}

	/**
	 * The main method, run this to start the program
	 * 
	 * @param args
	 *            Command line arguments. None is used for this program
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				MainController mainController = new MainController();
				Whiteboard mainGUI = new Whiteboard(mainController);
				mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainGUI.pack();
				mainGUI.setResizable(true);
				mainGUI.setLocationRelativeTo(null);
				mainGUI.setVisible(true);
			}
		});
	}
}

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;

/**
 * @author Trinh Nguyen
 *
 */
public class MainController {
	private static final int KNOBSIZE = 9;
	private Whiteboard _whiteboard;
	private Canvas _canvas;
	private ArrayList<DShape> _DShapes;
	private ToolType _toolType;
	private boolean _firstClick;
	private boolean _editingShape;
	private boolean _resizeShape;
	private boolean _moveShape;
	private boolean _selected;
	private boolean _isServer;
	private boolean _isClient;
	private DShape _currentViewingShape;
	private Color _backgroundColor;
	private Color _oldColor;
	private String _oldFont;
	private int _shapeCreationX;
	private int _shapeCreationY;
	private int _shapeCreationWidth;
	private int _shapeCreationHeight;
	private int _mouseX;
	private int _mouseY;
	private int _deltaX;
	private int _deltaY;
	private int _anchorX;
	private int _anchorY;
	private int _oldThickness;
	private int _modelCount;
	private File _file;
	private java.util.List<ObjectOutputStream> _outputs;
	private ClientHandler _clientHandler;
	private ServerAccepter _serverAccepter;
	private ModelListener _serverModelChangeListener;

	public MainController() {
		_DShapes = new ArrayList<DShape>();
		_toolType = ToolType.SELECT;
		_editingShape = false;
		_resizeShape = false;
		_moveShape = false;
		_firstClick = true;
		_isServer = false;
		_isClient = false;
		_outputs = new ArrayList<ObjectOutputStream>();
		_modelCount = 0;
		_oldFont = "dialog";
		_oldThickness = 2;
		_oldColor = Color.GRAY;
		_backgroundColor = Color.WHITE;

		_serverModelChangeListener = new ModelListener() {
			@Override
			public void modelChanged(DShapeModel model) {
				if (_isServer) {
					sendData("change", model);
				}
			}
			@Override
			public void modelDeleted(DShapeModel model) {
				model.removeListener(this);
			}
			@Override
			public void modelAdded() {

			}
		};
	}

	public void linkWhiteBoard(Whiteboard whiteboard, Canvas canvas) {
		_whiteboard = whiteboard;
		_canvas = canvas;
		_canvas.linkeShapeModelsAndController(this);
		_canvas.setBackground(_backgroundColor);
	}

	public void addShapeToModelsList(DShapeModel shapeModel) {
		DShape temp = null;
		if (shapeModel instanceof DRectModel) {
			temp = new DRect(shapeModel);
		} else if (shapeModel instanceof DOvalModel) {
			temp = new DOval(shapeModel);
		} else if (shapeModel instanceof DTextModel) {
			temp = new DText(shapeModel);
		} else if (shapeModel instanceof DLineModel) {
			temp = new DLine(shapeModel);
		}
		if (temp != null) {
			if (!_isClient) {
				shapeModel.setID(_modelCount);
			}
			_modelCount++;
			temp.getShapeModel().removeAllListeners();
			_DShapes.add(temp);
			addCommonListeners(shapeModel, temp);
			if (!_isClient) {
				setEditingShape(_DShapes.get(_DShapes.size() - 1));
			}
			if (_isServer) {
				sendData("add", shapeModel);
			}
		}
	}

	public void openFile() {
		int returnVal = _whiteboard.getFileChooser().showOpenDialog(_whiteboard);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			_file = _whiteboard.getFileChooser().getSelectedFile();
			List<DShapeModel> temp = new ArrayList<>();
			try {
				XMLDecoder xmlIn = new XMLDecoder(new BufferedInputStream(new FileInputStream(_file)));
				temp = (ArrayList<DShapeModel>) xmlIn.readObject();
				xmlIn.close();
				clear();
				for (DShapeModel e : temp) {
					addShapeToModelsList(e);
				}
				_currentViewingShape = null;
				deselectShape();
				setToolSelect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveFile() {
		int returnVal = _whiteboard.getFileChooser().showSaveDialog(_whiteboard);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			_file = _whiteboard.getFileChooser().getSelectedFile();
			String filePath = _file.getAbsolutePath();
			if (!filePath.endsWith(".xml")) {
				_file = new File(filePath + ".xml");
			}
			List<DShapeModel> temp = new ArrayList<>();

			Iterator<DShape> it = _DShapes.iterator();
			while (it.hasNext()) {
				DShape e = it.next();
				try {
					temp.add(e.getShapeModel());
				}
				catch (Exception ex) {
				}
			}

			try {
				XMLEncoder xmlOut = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(_file)));
				xmlOut.writeObject(temp);
				xmlOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Used to clear out all shapes. Usually called when a new file is opened
	 * or the program enters Client Server Mode.
	 */
	public void clear() {
		deselectShape();
		_currentViewingShape = null;
		_modelCount = 0;

		Iterator<DShape> it = _DShapes.iterator();
		while (it.hasNext()) {
			DShape e = it.next();
			try {
				e.getShapeModel().removeAllListeners();
				it.remove();
			}
			catch (Exception ex) {
			}
		}

		if (_isClient) {
			deselectShape();
			_currentViewingShape = null;
		}
		if (_isServer) {
			sendDataClear("clear");
		}
	}

	public void clearClientOnly() {
		if (_isClient) {
			deselectShape();
			_currentViewingShape = null;
			_modelCount = 0;
			Iterator<DShape> it = _DShapes.iterator();
			while (it.hasNext()) {
				DShape e = it.next();
				try {
					e.getShapeModel().removeAllListeners();
					it.remove();
				}
				catch (Exception ex) {
				}
			}
			deselectShape();
			_currentViewingShape = null;
		}
	}

	public void saveImage() {

		int returnVal = _whiteboard.getFileChooserImage().showSaveDialog(_whiteboard);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			_file = _whiteboard.getFileChooserImage().getSelectedFile();
			String filePath = _file.getAbsolutePath();
			if (!filePath.endsWith(".png")) {
				_file = new File(filePath + ".png");
			}
			deselectShape();
			BufferedImage image = (BufferedImage) _canvas.createImage(_canvas.getWidth(), _canvas.getHeight());
			Graphics g = image.getGraphics();
			_canvas.paintAll(g);
			g.dispose();
			try {
				javax.imageio.ImageIO.write(image, "PNG", _file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void setCanvasSize(int width, int height) {
		_canvas.setSize(width, height);
		_canvas.setVisible(false);
		_canvas.setVisible(true);
		if (_isServer) {
			sendDataResize(new Dimension(width, height));
		}
	}

	public void setToolRect() {
		deselectShape();
		if (_toolType != ToolType.RECT) {
			_toolType = ToolType.RECT;
			_whiteboard.showRectToolsOption();
			if (!_isClient) {
				_whiteboard.setStatusText("Drag mouse to create Rectangle");
			} else {
				_whiteboard.setStatusText("");
			}
		} else {
			setToolSelect();
		}
		_editingShape = false;
	}

	public void setToolOval() {
		deselectShape();
		if (_toolType != ToolType.OVAL) {
			_toolType = ToolType.OVAL;
			_whiteboard.showOvalToolsOption();
			if (!_isClient) {
				_whiteboard.setStatusText("Drag mouse to create Oval");
			} else {
				_whiteboard.setStatusText("");
			}
		} else {
			setToolSelect();
		}
		_editingShape = false;
	}

	public void setToolLine() {
		deselectShape();
		if (_toolType != ToolType.LINE) {
			_toolType = ToolType.LINE;
			_whiteboard.showLineToolsOption();
			if (!_isClient) {
				_whiteboard.setStatusText("Drag mouse to create Line");
			} else {
				_whiteboard.setStatusText("");
			}
		} else {
			setToolSelect();
		}
		_editingShape = false;
	}

	public void setToolText() {
		deselectShape();
		if (_toolType != ToolType.TEXT) {
			_toolType = ToolType.TEXT;
			_whiteboard.showTextToolsOption();
			if (!_isClient) {
				_whiteboard.setStatusText("Drag mouse to create Text");
			} else {
				_whiteboard.setStatusText("");
			}
		} else {
			setToolSelect();
		}
		_editingShape = false;
	}

	public void setToolSelect() {
		_toolType = ToolType.SELECT;
		_whiteboard.showSelectToolsOption();
		if (!_isClient) {
			_whiteboard.setStatusText("Click a shape you would like to modify");
		} else {
			_whiteboard.setStatusText("");
		}
		_editingShape = false;
	}

	public void doneButtonClicked() {
		deselectShape();
		setToolSelect();
	}

	private void setNewPrimaryColor(Color colorChoice) {
		_whiteboard.getToolLine().updateColor(colorChoice);
		_whiteboard.getToolOval().updateColor(colorChoice);
		_whiteboard.getToolRect().updateColor(colorChoice);
		_whiteboard.getToolText().updateColor(colorChoice);
		_whiteboard.getToolSelect().updateColor(colorChoice);
	}

	private void currentViewingShapeChanged(DShapeModel temp) {
		if (_isClient) {
			Iterator<DShape> it = _DShapes.iterator();
			while (it.hasNext()) {
				DShape e = it.next();
				try {
					if (e.getShapeModel().getID() == temp.getID()) {
						e.getShapeModel().mimic(temp);
						if (_editingShape) {
							if (temp.getID() == _currentViewingShape.getShapeModel().getID()) {
								_currentViewingShape = e;
								modifyShape();	
							}
							_whiteboard.selectTableObject(_currentViewingShape.getShapeModel().getID());
						}
						break;
					}
				}
				catch (Exception ex) {

				}
			}
		}
	}

	public void colorChanged(Color chosenColor) {
		if (!_editingShape) {
			_oldColor = chosenColor;
		} else {
			_currentViewingShape.getShapeModel().setColor(chosenColor);
		}
		setNewPrimaryColor(chosenColor);
	}

	public void thicknessChanged(int value) {
		if (!_editingShape) {
			_oldThickness = value;
		} else {
			DLineModel temp = (DLineModel) _currentViewingShape.getShapeModel();
			temp.setThickness(value);
		}
	}

	public void fontChanged(String font) {
		if (!_editingShape) {
			_oldFont = font;
		} else {
			DTextModel temp = (DTextModel) _currentViewingShape.getShapeModel();
			Font tempFont = new Font(font, temp.getFont().getStyle(), temp.getFont().getSize());
			temp.setFont(tempFont);
		}
	}

	public void textChanged(String text) {
		if (_editingShape) {
			DTextModel temp = (DTextModel) _currentViewingShape.getShapeModel();
			temp.setText(text);
		}
	}

	public void setEditingShape(DShape currentViewingShape) {
		_currentViewingShape = currentViewingShape;
		_whiteboard.setStatusText("");
		_editingShape = true;
		_canvas.repaint();
	}

	public void tableSelectedShape(int index) {
		setEditingShape(_DShapes.get(index));
		modifyShape();
	}

	public DShapeModel makeShapeModelType() {
		switch (_toolType) {
		case LINE:
			return new DLineModel(_whiteboard.getToolLine().getColor(), _shapeCreationX, _shapeCreationY,
					_shapeCreationWidth, _shapeCreationHeight, _whiteboard.getToolLine().getThickness());
		case OVAL:
			return new DOvalModel(_whiteboard.getToolOval().getColor(), _shapeCreationX, _shapeCreationY,
					_shapeCreationWidth, _shapeCreationHeight);
		case RECT:
			return new DRectModel(_whiteboard.getToolRect().getColor(), _shapeCreationX, _shapeCreationY,
					_shapeCreationWidth, _shapeCreationHeight);
		case SELECT:
			return null;
		case TEXT:
			Font tempFont = new Font(_whiteboard.getToolText().getFontName(), Font.PLAIN, 1);
			return new DTextModel(_whiteboard.getToolText().getColor(), _shapeCreationX, _shapeCreationY,
					_shapeCreationWidth, _shapeCreationHeight, _whiteboard.getToolText().getTextField().getText(),
					tempFont);
		default:
			return null;
		}
	}

	private void calculateNewWidthHeight(MouseEvent e) {
		if (e.getX() > _shapeCreationX) {
			_shapeCreationWidth = e.getX() - _shapeCreationX;
		} else {
			_shapeCreationWidth = _shapeCreationX - e.getX();
			_shapeCreationX = e.getX();
		}
		if (e.getY() > _shapeCreationY) {
			_shapeCreationHeight = e.getY() - _shapeCreationY;
		} else {
			_shapeCreationHeight = _shapeCreationY - e.getY();
			_shapeCreationY = e.getY();
		}
	}

	private void calculateNewLineP2(MouseEvent e) {
		_shapeCreationWidth = e.getX();
		_shapeCreationHeight = e.getY();
	}

	public void mouseClickedOrReleased(MouseEvent e) {
		if (_toolType == ToolType.SELECT && !_moveShape && !_resizeShape) {
			selectShape(e);
		} else {
			mouseReleased(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (!_editingShape) {
			switch (_toolType) {
			case RECT:
				rectMouseClickReleased(e);
				break;
			case LINE:
				lineMouseClickReleased(e);
				break;
			case OVAL:
				ovalMouseClickReleased(e);
				break;
			case TEXT:
				textMouseClickReleased(e);
				break;
			case SELECT:
				break;
			default:
				break;
			}
		} else {
			if (!_isClient) {
				_mouseX = e.getX();
				_mouseY = e.getY();
				Rectangle temp = null;
				if (_currentViewingShape instanceof DRect) {
					temp = _currentViewingShape.getBounds();
					checkResizeOvalRectText();
					if (!_resizeShape) {
						checkMoveShape(temp);
					}
				} else if (_currentViewingShape instanceof DOval) {
					temp = _currentViewingShape.getBounds();
					checkResizeOvalRectText();
					if (!_resizeShape) {
						checkMoveShape(temp);
					}
				} else if (_currentViewingShape instanceof DLine) {
					temp = _currentViewingShape.getBounds();
					checkResizeLine();
					if (!_resizeShape) {
						checkMoveShape(temp);
					}
				} else if (_currentViewingShape instanceof DText) {
					temp = _currentViewingShape.getBounds();
					checkResizeOvalRectText();
					if (!_resizeShape) {
						checkMoveShape(temp);
					}
				}
			}
		}
	}

	public void checkMoveShape(Rectangle temp) {
		if (_mouseX >= temp.x && _mouseX <= (temp.x + temp.width)) {
			if (_mouseY >= temp.y && _mouseY <= (temp.y + temp.height)) {
				_moveShape = true;
			} else {
				_moveShape = false;
			}
		}
	}

	public void checkResizeOvalRectText() {
		List<Point> temp = _currentViewingShape.getKnobs();
		int range = KNOBSIZE / 2;
		boolean valid = false;
		int index = 0;
		for (; index < temp.size(); index++) {
			if ((Math.abs(_mouseX - temp.get(index).x) <= range) && (Math.abs(_mouseY - temp.get(index).y) <= range)) {
				switch (index) {
				case 0:
					_anchorX = temp.get(3).x;
					_anchorY = temp.get(3).y;
					break;
				case 1:
					_anchorX = temp.get(2).x;
					_anchorY = temp.get(2).y;
					break;
				case 2:
					_anchorX = temp.get(1).x;
					_anchorY = temp.get(1).y;
					break;
				case 3:
					_anchorX = temp.get(0).x;
					_anchorY = temp.get(0).y;
					break;
				}
				valid = true;
				_moveShape = false;
				_resizeShape = true;
				break;
			}
		}
		if (!valid) {
			_resizeShape = false;
		}
	}

	public void checkResizeLine() {
		List<Point> temp = _currentViewingShape.getKnobs();
		int range = KNOBSIZE / 2;
		boolean valid = false;
		int index = 0;
		for (; index < temp.size(); index++) {
			if ((Math.abs(_mouseX - temp.get(index).x) <= range) && (Math.abs(_mouseY - temp.get(index).y) <= range)) {
				switch (index) {
				case 0:
					_anchorX = temp.get(1).x;
					_anchorY = temp.get(1).y;
					break;
				case 1:
					_anchorX = temp.get(0).x;
					_anchorY = temp.get(0).y;
					break;
				}
				valid = true;
				_moveShape = false;
				_resizeShape = true;
				break;
			}
		}
		if (!valid) {
			_resizeShape = false;
		}
	}

	public boolean isMoveShape() {
		return _moveShape;
	}

	public boolean isResizeShape() {
		return _resizeShape;
	}

	public void resizeOvalRectText(MouseEvent e) {
		_currentViewingShape.resizeShape(e.getX(), e.getY(), _anchorX, _anchorY);
	}

	public void moveOvalRectText(MouseEvent e) {
		if (!_resizeShape) {
			_deltaX = e.getX() - _mouseX;
			_deltaY = e.getY() - _mouseY;

			_currentViewingShape.moveShape(_deltaX, _deltaY);

			_mouseX = e.getX();
			_mouseY = e.getY();
		}
	}

	public void resizeLine(MouseEvent e) {
		_currentViewingShape.resizeShape(e.getX(), e.getY(), _anchorX, _anchorY);
	}

	public void moveLine(MouseEvent e) {
		if (!_resizeShape) {
			_deltaX = e.getX() - _mouseX;
			_deltaY = e.getY() - _mouseY;
			_currentViewingShape.moveShape(_deltaX, _deltaY);
			_mouseX = e.getX();
			_mouseY = e.getY();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (!_editingShape) {
			switch (_toolType) {
			case RECT:
				rectMouseClickReleased(e);
				break;
			case LINE:
				lineMouseClickReleased(e);
				break;
			case OVAL:
				ovalMouseClickReleased(e);
				break;
			case TEXT:
				textMouseClickReleased(e);
				break;
			case SELECT:
				break;
			default:
				break;
			}
		} else {
			Rectangle temp = _currentViewingShape.getBounds();
			if (!withinBound(temp, e)) {
				deselectShape();
			}
		}
		_moveShape = false;
		_resizeShape = false;
	}

	public boolean withinBound(Rectangle bound, MouseEvent e) {
		if (e.getX() >= bound.getX() && e.getX() <= (bound.getX()+bound.getWidth())
				&& e.getY() >= bound.getY() && e.getY() <= (bound.getY()+bound.getHeight())) {
			return true;
		}
		return false;
	}

	public void selectShape(MouseEvent e) {
		_mouseX = e.getX();
		_mouseY = e.getY();
		Rectangle temp = null;
		_selected = false;
		for (int i = _DShapes.size() - 1; i >= 0; i--) {
			if (_DShapes.get(i) instanceof DRect) {
				temp = _DShapes.get(i).getBounds();
				if (withinBound(temp, e)) {
					setEditingShape(_DShapes.get(i));
					_whiteboard.selectTableObject(_DShapes.get(i).getShapeModel().getID());
					_oldColor = _whiteboard.getToolRect().getColor();
					setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
					_toolType = ToolType.RECT;
					_whiteboard.showRectToolsOption();
					_selected = true;
					break;
				}
			}
			if (_DShapes.get(i) instanceof DText) {
				temp = _DShapes.get(i).getBounds();
				if (withinBound(temp, e)) {
					setEditingShape(_DShapes.get(i));
					_whiteboard.selectTableObject(_DShapes.get(i).getShapeModel().getID());
					_oldColor = _whiteboard.getToolText().getColor();
					setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
					_oldFont = _whiteboard.getToolText().getFontName();
					DTextModel textModel = (DTextModel) _currentViewingShape.getShapeModel();
					String tempFont = textModel.getFont().getName();
					_whiteboard.getToolText().setFont(tempFont);
					_whiteboard.getToolText().getTextField().setText(textModel.getText());
					_toolType = ToolType.TEXT;
					_whiteboard.showTextToolsOption();
					_selected = true;
					break;
				}
			}
			if (_DShapes.get(i) instanceof DOval) {
				temp = _DShapes.get(i).getBounds();
				if (withinBound(temp, e)) {
					setEditingShape(_DShapes.get(i));
					_whiteboard.selectTableObject(_DShapes.get(i).getShapeModel().getID());
					_oldColor = _whiteboard.getToolOval().getColor();
					setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
					_toolType = ToolType.OVAL;
					_whiteboard.showOvalToolsOption();
					_selected = true;
					break;
				}
			}
			if (_DShapes.get(i) instanceof DLine) {
				temp = _DShapes.get(i).getBounds();
				if (withinBound(temp, e)) {
					setEditingShape(_DShapes.get(i));
					_whiteboard.selectTableObject(_DShapes.get(i).getShapeModel().getID());
					_oldColor = _whiteboard.getToolLine().getColor();
					setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
					_oldThickness = _whiteboard.getToolLine().getThickness();
					DLineModel lineModel = (DLineModel) _currentViewingShape.getShapeModel();
					_whiteboard.getToolLine().setThickness(lineModel.getThickness());
					_toolType = ToolType.LINE;
					_whiteboard.showLineToolsOption();
					_selected = true;
					break;
				}
			}
		}
		if (!_selected) {
			deselectShape();
		}
		_canvas.repaint();
	}

	public void deselectShape() {
		_editingShape = false;
		_whiteboard.getToolText().getTextField().setText("Hello");
		_whiteboard.getToolLine().setThickness(_oldThickness);
		_whiteboard.getToolText().setFont(_oldFont);
		setNewPrimaryColor(_oldColor);
		_whiteboard.getShapesJTable().clearSelection();
		_canvas.repaint();
	}

	public void modifyShape() {
		_whiteboard.setStatusText("");
		if (_currentViewingShape instanceof DRect) {
			_oldColor = _whiteboard.getToolRect().getColor();
			setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
			_toolType = ToolType.RECT;
			_whiteboard.showRectToolsOption();
		}
		if (_currentViewingShape instanceof DText) {
			_oldColor = _whiteboard.getToolText().getColor();
			setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
			_toolType = ToolType.TEXT;
			_oldFont = _whiteboard.getToolText().getFontName();
			DTextModel temp = (DTextModel) _currentViewingShape.getShapeModel();
			String tempFont = temp.getFont().getName();
			_whiteboard.getToolText().setFont(tempFont);
			_whiteboard.getToolText().getTextField().setText(temp.getText());
			_whiteboard.showTextToolsOption();
		}
		if (_currentViewingShape instanceof DOval) {
			_oldColor = _whiteboard.getToolOval().getColor();
			setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
			_toolType = ToolType.OVAL;
			_whiteboard.showOvalToolsOption();
		}
		if (_currentViewingShape instanceof DLine) {
			_oldColor = _whiteboard.getToolLine().getColor();
			setNewPrimaryColor(_currentViewingShape.getShapeModel().getColor());
			_toolType = ToolType.LINE;
			DLineModel temp = (DLineModel) _currentViewingShape.getShapeModel();
			_whiteboard.getToolLine().setThickness(temp.getThickness());
			_whiteboard.showLineToolsOption();
		}
	}

	private void rectMouseClickReleased(MouseEvent e) {
		if (!_isClient) {
			if (_firstClick) {
				_firstClick = false;
				_shapeCreationX = e.getX();
				_shapeCreationY = e.getY();
				_canvas.enablePreviewShape(_whiteboard.getToolRect().getColor(), _shapeCreationX, _shapeCreationY, 0,
						"", null);
			} else {
				calculateNewWidthHeight(e);
				_canvas.disablePreviewShape();
				DShapeModel temp = makeShapeModelType();
				if (shapeBigEnough(temp)) {
					_firstClick = true;
					addShapeToModelsList(temp);
					_whiteboard.setStatusText("");
				} else {
					_firstClick = true;
					_toolType = ToolType.SELECT;
					selectShape(e);
					if (!_editingShape) {
						setToolRect();
					}
				}
			}
		} else {
			selectShape(e);
		}
	}

	private void ovalMouseClickReleased(MouseEvent e) {
		if (!_isClient) {
			if (_firstClick) {
				_firstClick = false;
				_shapeCreationX = e.getX();
				_shapeCreationY = e.getY();
				_canvas.enablePreviewShape(_whiteboard.getToolOval().getColor(), _shapeCreationX, _shapeCreationY, 0,
						"", null);
			} else {
				calculateNewWidthHeight(e);
				_canvas.disablePreviewShape();
				DShapeModel temp = makeShapeModelType();
				if (shapeBigEnough(temp)) {
					_firstClick = true;
					addShapeToModelsList(temp);
					_whiteboard.setStatusText("");
				} else {
					_firstClick = true;
					_toolType = ToolType.SELECT;
					selectShape(e);
					if (!_editingShape) {
						setToolOval();
					}
				}
			}
		} else {
			selectShape(e);
		}
	}

	private void textMouseClickReleased(MouseEvent e) {
		if (!_isClient) {
			if (_firstClick) {
				_firstClick = false;
				_shapeCreationX = e.getX();
				_shapeCreationY = e.getY();
				Font tempFont = new Font(_whiteboard.getToolText().getFontName(), Font.PLAIN, 1);
				_canvas.enablePreviewShape(_whiteboard.getToolText().getColor(), _shapeCreationX, _shapeCreationY, 0,
						_whiteboard.getToolText().getTextField().getText(), tempFont);
			} else {
				calculateNewWidthHeight(e);
				_canvas.disablePreviewShape();
				DShapeModel temp = makeShapeModelType();
				if (shapeBigEnough(temp)) {
					_firstClick = true;
					addShapeToModelsList(temp);
					_whiteboard.setStatusText("");
				} else {
					_firstClick = true;
					_toolType = ToolType.SELECT;
					selectShape(e);
					if (!_editingShape) {
						setToolText();
					}
				}
			}
		} else {
			selectShape(e);
		}
	}

	private void lineMouseClickReleased(MouseEvent e) {
		if (!_isClient) {
			if (_firstClick) {
				_firstClick = false;
				_shapeCreationX = e.getX();
				_shapeCreationY = e.getY();
				_canvas.enablePreviewShape(_whiteboard.getToolLine().getColor(), _shapeCreationX, _shapeCreationY,
						_whiteboard.getToolLine().getThickness(), "", null);
			} else {
				calculateNewLineP2(e);
				_canvas.disablePreviewShape();
				DLineModel temp = (DLineModel) makeShapeModelType();
				if (lineLongEnough(temp)) {
					_firstClick = true;
					addShapeToModelsList(temp);
					_whiteboard.setStatusText("");
				} else {
					_firstClick = true;
					_toolType = ToolType.SELECT;
					selectShape(e);
					if (!_editingShape) {
						setToolLine();
					}
				}
			}
		} else {
			selectShape(e);
		}
	}

	public void removeShape(int id) {
		boolean shapeModelFound = false;

		Iterator<DShape> it = _DShapes.iterator();
		while (it.hasNext()) {
			DShape e = it.next();
			try {
				if (e.getShapeModel().getID() == id) {
					shapeModelFound = true;
					DShapeModel temp = e.getShapeModel();
					if (_currentViewingShape != null && _currentViewingShape.getShapeModel().getID() == id) {
						deselectShape();
					}
					if (_isServer) {
						sendData("remove", temp);
					}
					temp.removeAllListeners();
					it.remove();
					if (_isClient) {
						_canvas.repaint();
					}
					break;
				}
			}
			catch (Exception ex) {
			}
		}
		if (!shapeModelFound) {
			System.out.println("ShapeModel with ID: " + id + " not found! No Shapes deleted.");
		}
	}

	public void addShapeButton(Color chosenColor) {
		DShapeModel temp;
		switch (_toolType) {
		case RECT:
			temp = new DRectModel(chosenColor, 10, 10, 20, 20);
			addShapeToModelsList(temp);
			_whiteboard.setStatusText("");
			break;
		case LINE:
			break;
		case OVAL:
			temp = new DOvalModel(chosenColor, 10, 10, 20, 20);
			addShapeToModelsList(temp);
			_whiteboard.setStatusText("");
			break;
		case SELECT:
			break;
		case TEXT:
			break;
		default:
			break;
		}
	}

	public void shapeShiftBack(int id) {
		int index = 0;
		DShapeModel tempModel = null;
		Iterator<DShape> it = _DShapes.iterator();
		while (it.hasNext()) {
			DShape e = it.next();
			try {
				if (id == e.getShapeModel().getID()) {
					tempModel = e.getShapeModel();
					break;
				}
				index++;
			}
			catch (Exception ex) {
			}
		}

		if (index > 0) {
			Collections.swap(_DShapes, index - 1, index);
			AbstractTableModel temp = (AbstractTableModel) _whiteboard.getShapesJTable().getModel();
			temp.fireTableRowsUpdated(index - 1, index);
			if (_isServer) {
				if (tempModel != null) {
					sendData("move back", tempModel);
				}
			}
		}
		if (!_isClient) {
			_whiteboard.selectTableObject(id);
		}
		else {
			if (_editingShape) {
				_whiteboard.selectTableObject(_currentViewingShape.getShapeModel().getID());
			}
		}
		_canvas.repaint();
	}

	public void shapeShiftFront(int id) {
		int index = 0;
		DShapeModel tempModel = null;
		Iterator<DShape> it = _DShapes.iterator();
		while (it.hasNext()) {
			DShape e = it.next();
			try {
				if (id == e.getShapeModel().getID()) {
					tempModel = e.getShapeModel();
					break;
				}
				index++;
			}
			catch (Exception ex) {
			}
		}
		
		if (index < _DShapes.size() - 1) {
			Collections.swap(_DShapes, index, index + 1);
			AbstractTableModel temp = (AbstractTableModel) _whiteboard.getShapesJTable().getModel();
			temp.fireTableRowsUpdated(index, index + 1);
			if (_isServer) {
				if (tempModel != null) {
					sendData("move front", tempModel);
				}
			}
		}
		if (!_isClient) {
			_whiteboard.selectTableObject(id);
		}
		else {
			if (_editingShape) {
				_whiteboard.selectTableObject(_currentViewingShape.getShapeModel().getID());
			}
		}
		_canvas.repaint();
	}

	private boolean shapeBigEnough(DShapeModel tempShape) {
		if (tempShape.getWidth() > 5 && tempShape.getHeight() > 5) {
			return true;
		} else {
			return false;
		}
	}

	private boolean lineLongEnough(DLineModel tempShape) {
		double sqrtValue = Math.pow(tempShape.getP1().getX() - tempShape.getP2().getX(), 2)
				+ Math.pow(tempShape.getP1().getY() - tempShape.getP2().getY(), 2);
		double d = Math.sqrt(sqrtValue);
		if (d > 6) {
			return true;
		} else {
			return false;
		}
	}

	private void addCommonListeners(DShapeModel temp, DShape shape) {
		temp.addListener(_canvas);
		temp.addListener((ModelListener) _whiteboard.getShapesJTable().getModel());
		if (_isServer) {
			temp.addListener(_serverModelChangeListener);
		}
		temp.addListener(shape);
	}

	public boolean getIsClient() {
		return _isClient;
	}

	public boolean getIsServer() {
		return _isServer;
	}

	public ArrayList<DShape> getDShapes() {
		return _DShapes;
	}

	public JFrame getWhiteboard() {
		return _whiteboard;
	}

	public boolean isEditingShape() {
		return _editingShape;
	}

	public DShape getCurrentViewingShape() {
		return _currentViewingShape;
	}

	public int getKnobSize() {
		return KNOBSIZE;
	}

	public ToolType getToolType() {
		return _toolType;
	}

	public void setModelCount(int modelCount) {
		_modelCount = modelCount;
	}

	//
	// Network code starts here.
	//
	public void startClientMode(String address, int port) {
		_clientHandler = new ClientHandler(address, port);
		_clientHandler.start();
	}

	public void startServerMode(int port) {
		_serverAccepter = new ServerAccepter(port);
		_serverAccepter.start();
	}

	/**
	 * Adds a client to the list. The server sends data to every clients on this list.
	 * If client connects to server when the server has already drawn objects,
	 * The client is caught up here.
	 * @param out
	 */
	public synchronized void addOutput(ObjectOutputStream out) {

		// Helps new clients catch up to the current Server state
		if (!_DShapes.isEmpty()) {
			String xmlString = null;
			Dimension d = new Dimension(_whiteboard.getCanvasSize());

			OutputStream memStream = new ByteArrayOutputStream();
			XMLEncoder encoder = new XMLEncoder(memStream);
			encoder.writeObject(d);
			encoder.close();
			xmlString = memStream.toString();

			try {
				out.writeObject("resize canvas");
				out.writeObject(xmlString);
				out.flush();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}

			Iterator<DShape> it = _DShapes.iterator();
			while (it.hasNext()) {
				DShapeModel e = it.next().getShapeModel();
				memStream = new ByteArrayOutputStream();
				encoder = new XMLEncoder(memStream);
				encoder.writeObject(e);
				encoder.close();
				xmlString = memStream.toString();
				try {
					out.writeObject("add");
					out.writeObject(xmlString);
					out.flush();
				}
				catch (Exception ex) {

				}
			}
		}

		_outputs.add(out);
	}

	/**
	 * Goes through each client in the _outputs and send them the action
	 * and the referenced shapeModel
	 * @param shapeModel
	 */
	public synchronized void sendData(String verb, DShapeModel shapeModel) {
		OutputStream memStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(memStream);
		encoder.writeObject(shapeModel);
		encoder.close();
		String xmlString = memStream.toString();
		// Now write that xml string to all the clients.
		Iterator<ObjectOutputStream> it = _outputs.iterator();
		while (it.hasNext()) {
			ObjectOutputStream out = it.next();
			try {
				out.writeObject(verb);
				out.writeObject(xmlString);
				out.flush();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				it.remove();
			}
		}
	}

	public synchronized void sendDataClear(String verb) {
		OutputStream memStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(memStream);
		encoder.writeObject(null);
		encoder.close();
		String xmlString = memStream.toString();
		// Now write that xml string to all the clients.
		Iterator<ObjectOutputStream> it = _outputs.iterator();
		while (it.hasNext()) {
			ObjectOutputStream out = it.next();
			try {
				out.writeObject(verb);
				out.writeObject(xmlString);
				out.flush();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				it.remove();
			}
		}
	}

	public synchronized void sendDataResize(Dimension newSize) {
		OutputStream memStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(memStream);
		encoder.writeObject(newSize);
		encoder.close();
		String xmlString = memStream.toString();

		Iterator<ObjectOutputStream> it = _outputs.iterator();
		while (it.hasNext()) {
			ObjectOutputStream out = it.next();
			try {
				out.writeObject("resize canvas");
				out.writeObject(xmlString);
				out.flush();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				it.remove();
			}
		}
	}

	class ServerAccepter extends Thread {
		private int port;
		ServerAccepter(int port) {
			this.port = port;
		}
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				_isServer = true;

				if (!_DShapes.isEmpty()) {
					Iterator<DShape> it = _DShapes.iterator();
					while (it.hasNext()) {
						try {
							it.next().getShapeModel().addListener(_serverModelChangeListener);
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				InetAddress IP = InetAddress.getLocalHost();
				_whiteboard.setConnectionModeStatus(" Server Mode (" + IP.getHostAddress() + ":" + port + ")");
				while (true) {
					Socket toClient = null;
					toClient = serverSocket.accept();
					// This loop keeps running as it waits for any new client to connect
					addOutput(new ObjectOutputStream(toClient.getOutputStream()));
				}
			} catch (IOException ex) {
				ex.printStackTrace(); 
			}
		}
	}

	private class ClientHandler extends Thread {
		private String name;
		private int port;

		ClientHandler(String name, int port) {
			this.name = name;
			this.port = port;
		}

		@Override
		public void run() {
			try {
				Socket toServer = new Socket(name, port);
				ObjectInputStream in = new ObjectInputStream(toServer.getInputStream());
				_isClient = true;
				_modelCount = 0;
				_whiteboard.setConnectionModeStatus(" Client Mode (" + name + ":" + port + ")");
				_whiteboard.getToolRect().setBtnAddShapeEnabled(false);
				_whiteboard.getToolOval().setBtnAddShapeEnabled(false);
				_whiteboard.getToolLine().setThicknessEnabled(false);
				_whiteboard.getToolText().setTextFieldEnabled(false);
				_whiteboard.getToolText().setFontComboBoxEnabled(false);

				while (true) {
					String verb = (String) in.readObject();
					String xmlString = (String) in.readObject();
					XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
					DShapeModel shapeModel = null;
					Dimension newSize = null;
					if (!verb.equals("resize canvas")) {
						shapeModel = (DShapeModel) decoder.readObject();
					}
					else if (verb.equals("resize canvas")){
						newSize = (Dimension) decoder.readObject();
					}
					switch(verb) {
					case ("add"):
						addShapeToModelsList(shapeModel);
					break;
					case ("remove"):
						removeShape(shapeModel.getID());
					break;
					case ("move front"):
						shapeShiftFront(shapeModel.getID());
					break;
					case ("move back"):
						shapeShiftBack(shapeModel.getID());
					break;
					case ("change"):
						currentViewingShapeChanged(shapeModel);
					break;
					case ("clear"):
						clear();
					break;
					case ("clear client"):
						clearClientOnly();
					break;
					case ("resize canvas"):
						setCanvasSize(newSize.width, newSize.height);
					break;
					}
					decoder.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

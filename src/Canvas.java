import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author Trinh Nguyen
 *
 */
public class Canvas extends JPanel implements ModelListener {

	private static final long serialVersionUID = 1L;
	private Dimension _canvasSize;
	private MainController _mainController;
	private Color _backgroundColor;
	private DShape _tempPreviewShape;
	private int _anchorX;
	private int _anchorY;
	private boolean _shapePreview;
	public Canvas() {
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {

			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if (!_mainController.getIsClient()) {
					switch (_mainController.getToolType()) {
					case RECT:
						if (_shapePreview) {
							calculateRectPreviewDraw(e);
						}
						else if (_mainController.isResizeShape()) {
							_mainController.resizeOvalRectText(e);
						}
						else if (_mainController.isMoveShape()) {
							_mainController.moveOvalRectText(e);
						}
						break;
					case LINE:
						if (_shapePreview) {
							calculateLinePreviewDraw(e);
						}
						else if (_mainController.isResizeShape()) {
							_mainController.resizeLine(e);
						}
						else if (_mainController.isMoveShape()) {
							_mainController.moveLine(e);
						}
						break;
					case OVAL:
						if (_shapePreview) {
							calculateOvalPreviewDraw(e);
						}
						else if (_mainController.isResizeShape()) {
							_mainController.resizeOvalRectText(e);
						}
						else if (_mainController.isMoveShape()) {
							_mainController.moveOvalRectText(e);
						}
						break;
					case SELECT:
						DShape temp = _mainController.getCurrentViewingShape();
						if (temp instanceof DRect) {
							if (_mainController.isResizeShape()) {
								_mainController.resizeOvalRectText(e);
							}
							else if (_mainController.isMoveShape()) {
								_mainController.moveOvalRectText(e);
							}
						}
						else if (temp instanceof DOval) {
							if (_mainController.isResizeShape()) {
								_mainController.resizeOvalRectText(e);
							}
							else if (_mainController.isMoveShape()) {
								_mainController.moveOvalRectText(e);
							}
						}
						else if (temp instanceof DLine) {
							if (_mainController.isResizeShape()) {
								_mainController.resizeLine(e);
							}
							else if (_mainController.isMoveShape()) {
								_mainController.moveLine(e);
							}
						}
						else if (temp instanceof DText) {
							if (_mainController.isResizeShape()) {
								_mainController.resizeOvalRectText(e);
							}
							else if (_mainController.isMoveShape()) {
								_mainController.moveOvalRectText(e);
							}
						}
						break;
					case TEXT:
						if (_shapePreview) {
							calculateTextPreviewDraw(e);
						}
						else if (_mainController.isResizeShape()) {
							_mainController.resizeOvalRectText(e);
						}
						else if (_mainController.isMoveShape()) {
							_mainController.moveOvalRectText(e);
						}
						break;
					default:
						break;
					}
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				_mainController.mousePressed(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				_mainController.mouseClickedOrReleased(e);
			}
		});
		_backgroundColor = Color.WHITE;
		_canvasSize = new Dimension(400, 400);
		_shapePreview = false;
		setPreferredSize(_canvasSize);
		setMaximumSize(_canvasSize);
		repaint();
	}
	
	@Override
	public void setSize(int width, int height) {
		_canvasSize.setSize(width, height);
		setPreferredSize(_canvasSize);
		setMaximumSize(_canvasSize);
	}
	
	@Override
	public Dimension getSize() {
		return _canvasSize;
	}

	public void linkeShapeModelsAndController(MainController mainController) {
		_mainController = mainController;
	}

	public void setBackgroundColor(Color backgroundColor) {
		_backgroundColor = backgroundColor;
	}

	public void enablePreviewShape(Color color, int x, int y, int thickness, String text, Font font) {
		_anchorX = x;
		_anchorY = y;
		switch (_mainController.getToolType()) {
		case RECT:
			_tempPreviewShape = new DRect(new DRectModel(color, x, y, 0, 0));
			break;
		case LINE:
			_tempPreviewShape = new DLine(new DLineModel(color, x, y, x, y, thickness));
			break;
		case OVAL:
			_tempPreviewShape = new DOval(new DOvalModel(color, x, y, 0, 0));
			break;
		case SELECT:
			break;
		case TEXT:
			_tempPreviewShape = new DText(new DTextModel(color, x, y, 0, 0, text, font));
			break;
		default:
			break;
		}
		_tempPreviewShape.getShapeModel().addListener(this);
		_shapePreview = true;
	}

	public void disablePreviewShape() {
		_shapePreview = false;
		_tempPreviewShape.getShapeModel().removeAllListeners();
		_tempPreviewShape = null;
	}

	private void calculateRectPreviewDraw(MouseEvent e) {
		_tempPreviewShape.resizeShape(e.getX(), e.getY(), _anchorX, _anchorY);
	}

	private void calculateOvalPreviewDraw(MouseEvent e) {
		_tempPreviewShape.resizeShape(e.getX(), e.getY(), _anchorX, _anchorY);
	}

	private void calculateTextPreviewDraw(MouseEvent e) {
		_tempPreviewShape.resizeShape(e.getX(), e.getY(), _anchorX, _anchorY);
	}

	private void calculateLinePreviewDraw(MouseEvent e) {
		_tempPreviewShape.resizeShape(e.getX(), e.getY(), _anchorX, _anchorY);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(_backgroundColor);
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
		
		//A copy of the array is made here and drawn. This is required to prevent
		//concurrent modification exceptions which may happen during Client Server Mode.
		Iterator<DShape> it = new ArrayList<DShape>(_mainController.getDShapes()).iterator();
		while (it.hasNext()) {
			DShape e = it.next();
			try {
				e.draw(g);
			}
			catch (Exception ex) {
			}
		}
		
		if (_shapePreview) {
			_tempPreviewShape.draw(g);
		}

		if (_mainController.isEditingShape()) {
			addKnobsToSelectedShape(g);
		}
	}

	private void addKnobsToSelectedShape(Graphics g) {
		List<Point> knobs = _mainController.getCurrentViewingShape().getKnobs();
		g.setColor(Color.BLACK);
		for (Point e : knobs) {
			int x = e.x - (_mainController.getKnobSize()/2);
			int y = e.y - (_mainController.getKnobSize()/2);
			int width = (_mainController.getKnobSize());
			int height = (_mainController.getKnobSize());
			g.fillRect(x, y, width, height);
		}
	}

	@Override
	public void modelChanged(DShapeModel model) {
		repaint();
	}

	@Override
	public void modelAdded() {
		repaint();
	}

	@Override
	public void modelDeleted(DShapeModel model) {
		model.removeListener(this);
		repaint();
	}
}

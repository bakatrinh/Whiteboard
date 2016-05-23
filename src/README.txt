Group Members: Trinh Nguyen (010381943)

HW 6: Whiteboard
===============================
Whiteboard.java contains the main method to run the program.

ModelListener:
------------------------------------------

ModelListener is used by 4 types of objects:

1) JTableModel: used to listen for changes and update values

2) Canvas: repaints when a model changes

3) _serverModelChangeListener: ModelListener object in MainController.java. Sends out changed model to Clients if the program is in Server Mode.

4) Every DShape made. For now, DShapes does not do anything when notified its DShapeModel was changed since all of its methods are calculated on the fly (including the draw method) and no fields are stored in DShape except for its DShapeModel.


Move Font & Move Back:
------------------------------------------

I interpret the requirement as moving one layer at a time. This still allows the shape to move all the way to the end or all the way to the back if the button is clicked often enough.

Canvas Button:
------------------------------------------

Gives the user the ability to resize the canvas. This resizes all connected client's canvas to match the current canvas as well. The user can also clear the canvas and start over which does the same to the connected client.

Tool JPanel:
------------------------------------------

Each tool has their own JPanel at the top with features relevant to the shape the user wants to create or edit. Clicking on each tool button brings up the tool panel at the top which allows the user to change color, add shape, change thickness, change font, etc. Clicking on the button again deselects any shape being worked on and hide that tool JPanel. The done button also does the same thing.

Changing Color - the little color box allows the user to change the color of future shapes to be created or the color of the current selected shape. Clicking on the box pops up the color chooser to allows the user to pick a color.

Shape selection - when a shape is selected, the corresponding tool and tool JPanel will selected and changed based on the type of shape chosen. All information in the tool JPanel will be the same as the current selected shape such as color, text, font, line thickness. Deselecting the shape will change all values back to what they were before the shape was selected.

Shapes JTable:
------------------------------------------

This table lists all shapes on the canvas in the order they were created and their layer status. Clicking on the shape on the JTable will select that shape on the canvas and will change and select the appropriate tool and tool JPanel similar to selecting a shape on the canvas.


Status text for Server mode / Client mode:
------------------------------------------

When the user start server mode or client mode, a text on the bottom left of the window will indicate the mode they are in.


PaintComponent strategy:
------------------------------------------

Every time canvas repaints, it makes a copy (by reference) of the current DShapes array. It then iterates over the copy and draw each object. This is required since concurrent modification exceptions may be thrown if canvas is iterating over the original DShapes array while objects in the array are being modified, shifted, or deleted. This is more likely to happen during Client or Server Mode.
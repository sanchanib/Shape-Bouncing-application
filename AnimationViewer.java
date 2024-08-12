/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * YOUR UPI: sbra486
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field;

class AnimationViewer extends JComponent implements Runnable, TreeModel{
	private Thread animationThread = null; // the thread for animation
	private static int DELAY = 120; // the current animation speed
	private ShapeType currentShapeType = Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType = Shape.DEFAULT_PATHTYPE; // the current path type
	private Color currentColor = Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private Color currentBorderColor = Shape.DEFAULT_BORDER_COLOR;
	private int currentPanelWidth = Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT,currentWidth = Shape.DEFAULT_WIDTH, currentHeight = Shape.DEFAULT_HEIGHT;
	private String currentLabel = Shape.DEFAULT_LABEL;
	protected NestedShape root;
	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
	protected DefaultListModel<Shape> listModel;

	public AnimationViewer() {
		root = new NestedShape(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT);
		start();
		listModel = new DefaultListModel<Shape>();
	}

	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape currentShape : root.getAllInnerShapes()) {
			currentShape.move();
			currentShape.draw(g);
			currentShape.drawHandles(g);
			currentShape.drawString(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight();
		for (Shape currentShape : root.getAllInnerShapes())
			currentShape.resetPanelSize(currentPanelWidth, currentPanelHeight);
	}

	public NestedShape getRoot(){
		return root;
	}

	public boolean isLeaf(Object node){
		if (!(node instanceof NestedShape)){
			return true;
		}
			return false;
	}

	public boolean isRoot(Shape selectedNode){
		if(selectedNode == root){
			return true;
		}
		else{
			return false;
		}
	}

	public Object getChild(Object parent, int index) {
		if(!(parent instanceof NestedShape)){
			return null;
		}
		NestedShape parentObj = (NestedShape) parent;
		if(parentObj.getAllInnerShapes().size() < 0 || index >= parentObj.getAllInnerShapes().size()){
			return null;
		}
		return parentObj.getAllInnerShapes().get(index);
		
	}

	public int getChildCount(Object parent) {

		if(!(parent instanceof NestedShape)){
			return 0;
		}
		NestedShape parentObj = (NestedShape) parent;
		return parentObj.getAllInnerShapes().size();
	}

	public int getIndexOfChild(Object parent, Object child) {
		NestedShape parentObj = (NestedShape) parent;
		if(!(parent instanceof NestedShape)){
			return -1;
		}
		return parentObj.getAllInnerShapes().indexOf(child);
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {

	}

	public void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
		for (TreeModelListener listener : treeModelListeners) {
			listener.treeNodesInserted(event);
		}
		System.out.printf("Called fireTreeNodesInserted: path=%s, childIndices=%s, children=%s\n",
				Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
	}
	
	public void addShapeNode(NestedShape selectedNode) {
		ShapeType shapeType = getCurrentShapeType();
		PathType pathType = getCurrentPathType();
		Shape newChild = selectedNode.createInnerShape(pathType, shapeType);
		listModel.addElement(newChild);
		int childIndex = selectedNode.getSize() - 1;
		Object[] path = selectedNode.getPath();
		int[] childIndices = {childIndex};
		Object[] children = {newChild};
		fireTreeNodesInserted(this, path, childIndices, children);
	}
	
	public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children){
		TreeModelEvent aTree = new TreeModelEvent(source, path, childIndices, children);
		for(TreeModelListener x : treeModelListeners){
			x.treeNodesRemoved(aTree);
		}
		System.out.printf("Called fireTreeNodesRemoved: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
	}
	
	public void removeNodeFromParent(Shape selectedNode){
		NestedShape parent = selectedNode.getParent();
		int index = parent.indexOf(selectedNode);
		parent.removeInnerShape(selectedNode);
		Object source = this;
		Shape[] path = parent.getPath();
		int[] childIndices = {index};
		Object [] children = {selectedNode};
		fireTreeNodesRemoved(this,path, childIndices,children);
		
	}

	public void reload(Shape selectedNode) {
		listModel.clear();
		if (selectedNode instanceof NestedShape) {
			NestedShape nestedShape = (NestedShape) selectedNode;
			ArrayList<Shape> innerShapes = nestedShape.getAllInnerShapes();
			for (Shape innerShape : innerShapes) {
				listModel.addElement(innerShape);
			}
		}
	}

	// you don't need to make any changes after this line ______________
	public String getCurrentLabel() {return currentLabel;}
	public int getCurrentHeight() { return currentHeight; }
	public int getCurrentWidth() { return currentWidth; }
	public Color getCurrentColor() { return currentColor; }
	public Color getCurrentBorderColor() { return currentBorderColor; }
	public void setCurrentShapeType(ShapeType value) {currentShapeType = value;}
	public void setCurrentPathType(PathType value) {currentPathType = value;}
	public ShapeType getCurrentShapeType() {return currentShapeType;}
	public PathType getCurrentPathType() {return currentPathType;}
	public void update(Graphics g) {
		paint(g);
	}
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while (animationThread == myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long) milliseconds);
		} catch (InterruptedException ie) {}
	}
}

// NestedShape.java : the Nested Shape class
// UPI: sbra486

import java.util.*;
import java.awt.*;

class NestedShape extends RectangleShape{
    private ArrayList<Shape> innerShapes;

    public NestedShape() {
        super();
        innerShapes = new ArrayList<>();
        createInnerShape(0, 0, super.width / 5, super.height / 5, getColor(), getBorderColor(), PathType.BOUNCING, ShapeType.RECTANGLE);
    }

    public NestedShape(int x, int y, int w, int h, int pw, int ph, Color c, Color bc, PathType pt) {
        super(x, y, w, h, pw, ph, c, bc, pt);
        innerShapes = new ArrayList<>();
        int inner_width = w / 5;
        int inner_height = h / 5;
        createInnerShape(0, 0, inner_width, inner_height, c, bc, PathType.BOUNCING, ShapeType.RECTANGLE);
    }

    public NestedShape(int w, int h) {
        super(0, 0, w, h, DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT, Shape.DEFAULT_COLOR, Shape.DEFAULT_BORDER_COLOR, PathType.BOUNCING);
        innerShapes = new ArrayList<>();
    }
    
    public Shape createInnerShape(int x, int y, int w, int h, Color c, Color bc, PathType pt, ShapeType st) {
        Shape innerShape;
        switch (st) {
            case RECTANGLE:
                innerShape = new RectangleShape(x, y, w, h, super.width, super.height, c, bc, pt);
                break;
            case OVAL:
                innerShape = new OvalShape(x, y, w, h, super.width, super.height, c, bc, pt);
                break;
            case NESTED:
                innerShape = new NestedShape(x, y, w, h, super.width, super.height, c, bc, pt);
                break;
            default:
                innerShape = null;
                break;
        }
        if (innerShape != null) {
            innerShape.setParent(this);
            innerShapes.add(innerShape);
        }
        return innerShape;
    }

    public Shape createInnerShape(PathType pt, ShapeType st) {
        int inner_width = super.width / 5;
        int inner_height = super.height / 5;
        return createInnerShape(0, 0, inner_width, inner_height, getColor(), getBorderColor(), pt, st);
    }

    public Shape getInnerShapeAt(int index) {
        if (index >= 0 && index < innerShapes.size()) {
            return innerShapes.get(index);
        }
        return null;
    }

    public int getSize() {
        return innerShapes.size();
    }

    public void draw(Graphics g){
        g.setColor(Color.black);
        g.drawRect(x, y, width, height);
        g.translate(x, y);
        
        for (int i = 0; i < innerShapes.size(); i++){
            Shape inner_shape = innerShapes.get(i);
            inner_shape.draw(g);
            inner_shape.drawString(g);
            
            if (inner_shape.isSelected()){
                inner_shape.drawHandles(g);
            }
        }
        g.translate(-x, -y);
    }
    
    public void move(){
        super.move(); 
       
        for (int i = 0; i < innerShapes.size(); i++){
            Shape inner_shape = innerShapes.get(i);
            inner_shape.move();
        }
    }

    public int indexOf(Shape s){
        return innerShapes.indexOf(s);
    }
    
    public void addInnerShape(Shape s){
        s.setParent(this);
        innerShapes.add(s);
    }
    
    public void removeInnerShape(Shape s){
        s.setParent(null);
        innerShapes.remove(s);
    }
    
    public void removeInnerShapeAt(int index){
        Shape removed_shape = innerShapes.remove(index);
        removed_shape.setParent(null);
    }
    
    public ArrayList<Shape> getAllInnerShapes(){
        return innerShapes;
    }
}

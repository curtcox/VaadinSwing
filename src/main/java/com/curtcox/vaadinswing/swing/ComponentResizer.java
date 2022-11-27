package com.curtcox.vaadinswing.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 *  The ComponentResizer allows you to resize a component by dragging a border
 *  of the component.
 *  See https://stackoverflow.com/questions/24476496/drag-and-resize-undecorated-jframe
 *  See https://tips4java.wordpress.com/2009/09/13/resizing-components/
 *  See http://www.camick.com/java/source/ComponentResizer.java
 */
final class ComponentResizer implements MouseListener, MouseMotionListener {
    private final static Dimension MINIMUM_SIZE = new Dimension(10, 10);
    private final static Dimension MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private static Map<Integer, Integer> cursors = new HashMap<>();

    static {
        cursors.put(1, Cursor.N_RESIZE_CURSOR);
        cursors.put(2, Cursor.W_RESIZE_CURSOR);
        cursors.put(4, Cursor.S_RESIZE_CURSOR);
        cursors.put(8, Cursor.E_RESIZE_CURSOR);
        cursors.put(3, Cursor.NW_RESIZE_CURSOR);
        cursors.put(9, Cursor.NE_RESIZE_CURSOR);
        cursors.put(6, Cursor.SW_RESIZE_CURSOR);
        cursors.put(12, Cursor.SE_RESIZE_CURSOR);
    }

    private final Insets dragInsets = new Insets(5, 5, 5, 5);
    private final Dimension snapSize = new Dimension(1, 1);

    private int direction;
    private static final int NORTH = 1;
    private static final int WEST = 2;
    private static final int SOUTH = 4;
    private static final int EAST = 8;

    private Cursor sourceCursor;
    private boolean resizing;
    private Rectangle bounds;
    private Point pressed;
    private boolean autoscrolls;

    private final Dimension minimumSize = MINIMUM_SIZE;
    private final Dimension maximumSize = MAXIMUM_SIZE;

    /**
     *  Add the required listeners to the specified component
     *
     *  @param component  the component the listeners are added to
     */
    public void registerComponent(Component component) {
        component.addMouseListener( this );
        component.addMouseMotionListener( this );
    }

    /**
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Component source = source(e);
        Point location = e.getPoint();
        direction = 0;

        if (location.x < dragInsets.left)                            direction += WEST;
        if (location.x > source.getWidth() - dragInsets.right - 1)   direction += EAST;
        if (location.y < dragInsets.top)                             direction += NORTH;
        if (location.y > source.getHeight() - dragInsets.bottom - 1) direction += SOUTH;

        //  Mouse is no longer over a resizable border
        if (direction == 0) {
            source.setCursor( sourceCursor );
        } else {
            // use the appropriate resizable cursor
            int cursorType = cursors.get( direction );
            Cursor cursor = Cursor.getPredefinedCursor( cursorType );
            source.setCursor( cursor );
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (! resizing) {
            sourceCursor = source(e).getCursor();
        }
    }

    private JFrame source(MouseEvent e) {
        Component component = e.getComponent();
        while (component.getParent()!=null) {
            component = component.getParent();
        }
        return (JFrame) component;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (! resizing) {
            source(e).setCursor( sourceCursor );
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //	The mouseMoved event continually updates this variable

        if (direction == 0) return;

        //  Setup for resizing. All future dragging calculations are done based
        //  on the original bounds of the component and mouse pressed location.

        resizing = true;

        Component source = source(e);
        pressed = e.getPoint();
        SwingUtilities.convertPointToScreen(pressed, source);
        bounds = source.getBounds();

        //  Making sure autoscrolls is false will allow for smoother resizing
        //  of components

        if (source instanceof JComponent) {
            JComponent jc = (JComponent)source;
            autoscrolls = jc.getAutoscrolls();
            jc.setAutoscrolls( false );
        }
    }

    /**
     *  Restore the original state of the Component
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        resizing = false;

        JFrame source = source(e);
        source.setCursor( sourceCursor );
    }

    /**
     *  Resize the component ensuring location and size is within the bounds
     *  of the parent container and that the size is within the minimum and
     *  maximum constraints.
     *
     *  All calculations are done using the bounds of the component when the
     *  resizing started.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (resizing == false) return;

        JFrame source = source(e);
        Point dragged = e.getPoint();
        SwingUtilities.convertPointToScreen(dragged, source);

        changeBounds(source, direction, bounds, pressed, dragged);
    }

    private void changeBounds(JFrame source, int direction, Rectangle bounds, Point pressed, Point current) {
        //  Start with original locaton and size

        int x = bounds.x;
        int y = bounds.y;
        int width = bounds.width;
        int height = bounds.height;

        //  Resizing the West or North border affects the size and location

        if (WEST == (direction & WEST)) {
            int drag = getDragDistance(pressed.x, current.x, snapSize.width);
            int maximum = Math.min(width + x, maximumSize.width);
            drag = getDragBounded(drag, snapSize.width, width, minimumSize.width, maximum);

            x -= drag;
            width += drag;
        }

        if (NORTH == (direction & NORTH)) {
            int drag = getDragDistance(pressed.y, current.y, snapSize.height);
            int maximum = Math.min(height + y, maximumSize.height);
            drag = getDragBounded(drag, snapSize.height, height, minimumSize.height, maximum);

            y -= drag;
            height += drag;
        }

        //  Resizing the East or South border only affects the size
        if (EAST == (direction & EAST)) {
            int drag = getDragDistance(current.x, pressed.x, snapSize.width);
            Dimension boundingSize = getBoundingSize();
            int maximum = Math.min(boundingSize.width - x, maximumSize.width);
            drag = getDragBounded(drag, snapSize.width, width, minimumSize.width, maximum);
            width += drag;
        }

        if (SOUTH == (direction & SOUTH)) {
            int drag = getDragDistance(current.y, pressed.y, snapSize.height);
            Dimension boundingSize = getBoundingSize();
            int maximum = Math.min(boundingSize.height - y, maximumSize.height);
            drag = getDragBounded(drag, snapSize.height, height, minimumSize.height, maximum);
            height += drag;
        }

        source.setBounds(x, y, width, height);
        source.validate();
    }

    /*
     *  Determine how far the mouse has moved from where dragging started
     */
    private int getDragDistance(int larger, int smaller, int snapSize) {
        int halfway = snapSize / 2;
        int drag = larger - smaller;
        drag += (drag < 0) ? -halfway : halfway;
        drag = (drag / snapSize) * snapSize;

        return drag;
    }

    /*
     *  Adjust the drag value to be within the minimum and maximum range.
     */
    private int getDragBounded(int drag, int snapSize, int dimension, int minimum, int maximum) {
        while (dimension + drag < minimum)
            drag += snapSize;

        while (dimension + drag > maximum)
            drag -= snapSize;

        return drag;
    }

    /*
     *  Keep the size of the component within the bounds of its parent.
     */
    private Dimension getBoundingSize() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds();
        return new Dimension(bounds.width, bounds.height);
    }
}
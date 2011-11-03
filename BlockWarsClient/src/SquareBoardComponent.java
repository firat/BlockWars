import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * The graphical component that paints the square board.
 */
public class SquareBoardComponent extends Component {
	int width;
	int height;
	int[][] matrix = new int[BlockWarsConstants.COLUMNS][BlockWarsConstants.ROWS];
	
	public int getwidth() {
		return this.width;
	}
	
	public int getheight() {
		return this.height;
	}
	
	public void initMatrix(int[][] matrix) {
		this.matrix = matrix;
	}
	
	public short updateMatrix(ArrayList<short[]> operations, short lastOperationSequence) {
		Iterator<short[]> op_it = operations.iterator();
		short[] last_op_array = null;
		while(op_it.hasNext()) {
			last_op_array = op_it.next();
			if (last_op_array[0] > lastOperationSequence || Math.abs((last_op_array[0] + lastOperationSequence)) < 4) {
				for(int i=1; i<last_op_array.length; i++){
					this.extractAndCommitOperation(last_op_array[i]);
				}
			}
		}
		return last_op_array[0];
	}
	
	private synchronized void extractAndCommitOperation(short operation) {
		boolean isRowOperation = (((operation & 32768) >> 15) == 1);
		if (isRowOperation) {
			int parameter = (operation & 63);
			int operation_type = (operation & 960) >> 6;
			if (operation_type == BlockWarsConstants.OP_ADD_NEW_LINE) {
				this.addLines(parameter);
			} else if (operation_type == BlockWarsConstants.OP_REMOVE_LINE){
				this.removeLine(parameter);
			}
		} else {
			int y = operation  & 127;
			int x = (operation & 3968) >> 7;
			int block_type = (operation & 61440) >> 12;
			this.setBlockType(x, y, block_type);
		}
	}
	
	private void setBlockType(int x, int y, int block_type) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return;
		}
		this.matrix[y][x] = block_type;
		this.invalidateSquare(x, y);
	}
	
	private void addLines(int num) {
		for(int y=0; y<height-1; y++) {
    		for(int x=0; x<width; x++) {
    			this.setBlockType(x, y, this.matrix[y+1][x]);
    			
    		}
    	}
    	for (int y=height-1; y<height; y++) {
    		for(int x=0; x<width; x++) {
    			if (x==num)
    				this.setBlockType(x, y, BlockWarsConstants.BLOCK_EMPTY);
    			else
    				this.setBlockType(x, y, BlockWarsConstants.ADDED_LINE);
        	}
    	}
	}
	
	private void removeLine(int y) {
		if (y < 0 || y >= height) {
            return;
        }

//		for(int i =0; i<width; i++) {
//			this.setBlockType(i, y, BlockWarsConstants.BLOCK_GLOW);
//		}
//		
//		this.reDraw();
//		try {
//			Thread.sleep(250);
//		} catch (Exception e) {}
//		
		
        for (; y > 0; y--) {
            for (int x = 0; x < width; x++) {
            	this.setBlockType(x, y, matrix[y - 1][x]);
            }
        }
        for (int x = 0; x < height; x++) {
        	this.setBlockType(x, 0, BlockWarsConstants.BLOCK_EMPTY);
        }
	}
	
	/**
     * The component size. If the component has been resized, that 
     * will be detected when the paint method executes. If this 
     * value is set to null, the component dimensions are unknown.
     */
    private Dimension  size = null;
    
    /**
     * The component insets. The inset values are used to create a 
     * border around the board to compensate for a skewed aspect 
     * ratio. If the component has been resized, the insets values 
     * will be recalculated when the paint method executes.
     */
    private Insets	insets = new Insets(0, 0, 0, 0);
    
    /**
     * The square size in pixels. This value is updated when the 
     * component size is changed, i.e. when the <code>size</code> 
     * variable is modified.
     */
    private Dimension  squareSize = new Dimension(0, 0);
    
    /**
     * An image used for double buffering. The board is first
     * painted onto this image, and that image is then painted 
     * onto the real surface in order to avoid making the drawing
     * process visible to the user. This image is recreated each
     * time the component size changes.
     */
    private Image  bufferImage = null;
    
    /**
     * A clip boundary buffer rectangle. This rectangle is used 
     * when calculating the clip boundaries, in order to avoid 
     * allocating a new clip rectangle for each board square.
     */
    private Rectangle  bufferRect = new Rectangle();
    
    /**
     * The board message color.
     */
    private Color  messageColor = Color.white;
    
    /**
     * A lookup table containing lighter versions of the colors.
     * This table is used to avoid calculating the lighter 
     * versions of the colors for each and every square drawn.
     */
    private Hashtable<Color,Color>  lighterColors = new Hashtable<Color,Color>();
    
    /**
     * A lookup table containing darker versions of the colors.
     * This table is used to avoid calculating the darker
     * versions of the colors for each and every square drawn.
     */
    private Hashtable<Color,Color>  darkerColors = new Hashtable<Color,Color>();
    
    /**
     * A flag set when the component has been updated.
     */
    private boolean  updated = true;
    
    /**
     * A bounding box of the squares to update. The coordinates 
     * used in the rectangle refers to the square matrix.
     */
    private Rectangle  updateRect = new Rectangle();
    
    /**
     * Creates a new square board component.
     */
    public SquareBoardComponent(int width, int height) {
    	this.width = width;
    	this.height = height;
        setBackground(Color.black);
        messageColor = Color.white;
    }
    
    /**
     * Adds a square to the set of squares in need of redrawing.
     *
     * @param x     the horizontal position (0 <= x < width)
     * @param y     the vertical position (0 <= y < height)
     */
    public void invalidateSquare(int x, int y) {
        if (updated) {
            updated = false;
            updateRect.x = x;
            updateRect.y = y;
            updateRect.width = 0;
            updateRect.height = 0;
        } else {
            if (x < updateRect.x) {
                updateRect.width += updateRect.x - x;
                updateRect.x = x;
            } else if (x > updateRect.x + updateRect.width) {
                updateRect.width = x - updateRect.x;
            }
            if (y < updateRect.y) {
                updateRect.height += updateRect.y - y;
                updateRect.y = y;
            } else if (y > updateRect.y + updateRect.height) {
                updateRect.height = y - updateRect.y;
            }
        }
    }
    
   
    /**
     * Redraws all the invalidated squares. If no squares have 
     * been marked as in need of redrawing, no redrawing will 
     * occur.
     */
    public void reDraw() {
        Graphics  g;

        if (!updated) {
            updated = true;
            g = getGraphics();
            g.setClip(insets.left + updateRect.x * squareSize.width,
                      insets.top + updateRect.y * squareSize.height,
                      (updateRect.width + 1) * squareSize.width,
                      (updateRect.height + 1) * squareSize.height);
            paint(g);
        }
    }
    
    /**
     * Redraws the whole component.
     */
    public void redrawAll() {
        Graphics  g;

        updated = true;
        g = getGraphics();
        g.setClip(insets.left, 
                  insets.top, 
                  width * squareSize.width, 
                  height * squareSize.height);
        paint(g);
    }
    
    /**
     * Returns true as this component is double buffered.
     * 
     * @return true as this component is double buffered
     */
    public boolean isDoubleBuffered() {
        return true;
    }
    
    /**
     * Returns the preferred size of this component.
     * 
     * @return the preferred component size
     */
    public Dimension getPreferredSize() {
        return new Dimension(width * 20, height * 20);
    }

    /**
     * Returns the minimum size of this component.
     * 
     * @return the minimum component size
     */
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * Returns the maximum size of this component.
     * 
     * @return the maximum component size
     */
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
    
    /**
     * Returns a lighter version of the specified color. The 
     * lighter color will looked up in a hashtable, making this
     * method fast. If the color is not found, the ligher color 
     * will be calculated and added to the lookup table for later
     * reference.
     * 
     * @param c     the base color
     * 
     * @return the lighter version of the color
     */
    private Color getLighterColor(Color c) {
        Color  lighter;
        
        lighter = (Color) lighterColors.get(c);
        if (lighter == null) {
            lighter = c.brighter().brighter();
            lighterColors.put(c, lighter);
        }
        return lighter;
    }

    /**
     * Returns a darker version of the specified color. The 
     * darker color will looked up in a hashtable, making this
     * method fast. If the color is not found, the darker color 
     * will be calculated and added to the lookup table for later
     * reference.
     * 
     * @param c     the base color
     * 
     * @return the darker version of the color
     */
    private Color getDarkerColor(Color c) {
        Color  darker;
        
        darker = (Color) darkerColors.get(c);
        if (darker == null) {
            darker = c.darker().darker();
            darkerColors.put(c, darker);
        }
        return darker;
    }
    
    /**
     * Paints this component indirectly. The painting is first 
     * done to a buffer image, that is then painted directly to 
     * the specified graphics context.
     * 
     * @param g     the graphics context to use
     */
    public synchronized void paint(Graphics g) {
        Graphics   bufferGraphics;
        Rectangle  rect;

        // Handle component size change
        if (size == null || !size.equals(getSize())) {
            size = getSize();
            squareSize.width = size.width / width;
            squareSize.height = size.height / height;
            if (squareSize.width <= squareSize.height) {
                squareSize.height = squareSize.width;
            } else {
                squareSize.width = squareSize.height;
            }
            insets.left = (size.width - width * squareSize.width) / 2;
            insets.right = insets.left;
            insets.top = 0;
            insets.bottom = size.height - height * squareSize.height;
            bufferImage = createImage(width * squareSize.width, 
                                      height * squareSize.height);
        }

        // Paint component in buffer image
        rect = g.getClipBounds();
        bufferGraphics = bufferImage.getGraphics();
        bufferGraphics.setClip(rect.x - insets.left, 
                               rect.y - insets.top, 
                               rect.width, 
                               rect.height);
        paintComponent(bufferGraphics);

        // Paint image buffer
        g.drawImage(bufferImage, 
                    insets.left,
                    insets.top, 
                    getBackground(), 
                    null);
    }
    
    /**
     * Paints this component directly. All the squares on the 
     * board will be painted directly to the specified graphics
     * context.
     * 
     * @param g     the graphics context to use
     */
    private void paintComponent(Graphics g) {

        // Paint background
        g.setColor(getBackground());
        g.fillRect(0, 
                   0, 
                   width * squareSize.width, 
                   height * squareSize.height);
        
        // Paint squares
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (this.matrix[y][x] != BlockWarsConstants.BLOCK_EMPTY) {
                    paintSquare(g, x, y);
                }
            }
        }   
    }
    
    /**
     * Paints a single board square. The specified position must 
     * contain a color object.
     *
     * @param g     the graphics context to use
     * @param x     the horizontal position (0 <= x < width)
     * @param y     the vertical position (0 <= y < height)
     */
    private void paintSquare(Graphics g, int x, int y) {
        Color color = BlockWarsConstants.COLORS[this.matrix[y][x]];

        int    xMin = x * squareSize.width;
        int    yMin = y * squareSize.height;
        int    xMax = xMin + squareSize.width - 1;
        int    yMax = yMin + squareSize.height - 1;
        int    i;

        // Skip drawing if not visible
        bufferRect.x = xMin;
        bufferRect.y = yMin;
        bufferRect.width = squareSize.width;
        bufferRect.height = squareSize.height;
        if (!bufferRect.intersects(g.getClipBounds())) {
            return;
        }

        // Fill with base color
        g.setColor(color);
        g.fillRect(xMin, yMin, squareSize.width, squareSize.height);

        // Draw brighter lines
        g.setColor(getLighterColor(color));
        for (i = 0; i < squareSize.width / 10; i++) {
            g.drawLine(xMin + i, yMin + i, xMax - i, yMin + i);
            g.drawLine(xMin + i, yMin + i, xMin + i, yMax - i);
        }

        // Draw darker lines
        g.setColor(getDarkerColor(color));
        for (i = 0; i < squareSize.width / 10; i++) {
            g.drawLine(xMax - i, yMin + i, xMax - i, yMax - i);
            g.drawLine(xMin + i, yMax - i, xMax - i, yMax - i);
        }
    }
}

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Fırat Can Başarır <fcbasarir@cs.bilgi.edu.tr>
 * @author Banuçiçek Gürcüoğlu <banucicekg@cs.bilgi.edu.tr>
 *
 * This board is used for game mechanics. It contains an integer matrix
 * representing the blocks on a tetris board. It also has the methods to
 * manipulate the board.
 * 
 */
public class SquareBoard {
	/** 
	 * Width of the board
	 */
	private int width = 0;
	
	/**
	 *  Height of the board
	 */
	private int height = 0;
	
	/** 
	 * Matrix holding the values of each cell as an int.
	 * See BlockWarsConstants for details. BLOCK_EMPTY is -1
	 */
	private int[][] matrix;
	
	/**
	 * Message to display on screen
	 */
	private String message;
	
	/**
	 * Queue for storing the operations
	 */
	private ArrayList<short[]> operationQueue;
	
	/**
	 * Sequence no of the operation short array
	 */	
	private short operationSequence;
    
	
	/**
	 * The currently moving figure on the square board;
	 */
	public Figure activeFigure;
	
	
	private Player player;
    /**
     * Creates a new square board with the specified size. The square
     * board will initially be empty.
     *
     * @param width     the width of the board (in squares)
     * @param height    the height of the board (in squares)
     */
    public SquareBoard(int width, int height, Player player) {
        this.width = width;
        this.height = height;
        this.matrix = new int[height][width];
        this.operationQueue = new ArrayList<short[]>();
        this.player = player;
        this.clear();
    }
    
    /**
     * Checks if a specified square is empty, i.e. it's type is
     * BlockWarsConstants.BLOCK_EMPTY. If the square is outside the board, 
     * false will be returned in all cases.
     *
     * @param x         the horizontal position (0 <= x < width)
     * @param y         the vertical position (0 <= y < height)
     * 
     * @return true if the square is empty, or
     *         false otherwise
     */
    public boolean isSquareEmpty(int x, int y) {
    	return (x < 0 || y < 0 || x >= width || y >= height || matrix[y][x] == BlockWarsConstants.BLOCK_EMPTY);
    }
    
    private boolean canAddNewFigure() {
        return this.isSquareEmpty(this.width/2, 0);
    }
    
    /**
     * Checks if a specified line is empty, i.e. only contains 
     * empty squares. If the line is outside the board, false will
     * always be returned.
     *
     * @param y	the vertical position (0 <= y < height)
     * 
     * @return	true if the whole line is empty, or false otherwise
     */
    public boolean isLineEmpty(int y) {
        if (y < 0 || y >= height) {
            return false;
        }
        for (int x = 0; x < width; x++) {
            if (matrix[y][x] != BlockWarsConstants.BLOCK_EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a specified line is full, i.e. only contains no empty
     * squares. If the line is outside the board, true will always be 
     * returned.
     *
     * @param y         the vertical position (0 <= y < height)
     * 
     * @return true if the whole line is full, or
     *         false otherwise
     */
    public boolean isLineFull(int y) {
        if (y < 0 || y >= height) {
            return true;
        }
        for (int x = 0; x < width; x++) {
            if (matrix[y][x] == BlockWarsConstants.BLOCK_EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the board contains any full lines.
     *
     * @return true if there are full lines on the board, or
     *         false otherwise
     */
    public boolean hasFullLines() {
        for (int y = height - 1; y >= 0; y--) {
            if (isLineFull(y)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the board height (in squares). This method returns, 
     * i.e, the number of vertical squares that fit on the board.
     * 
     * @return the board height in squares
     */
    public int getBoardHeight() {
        return height;
    }

    /**
     * Returns the board width (in squares). This method returns, i.e,
     * the number of horizontal squares that fit on the board.
     * 
     * @return the board width in squares
     */
    public int getBoardWidth() {
        return width;
    }
    
    public synchronized int[][] getMatrix() {
    	return this.matrix;
    }
    
    /**
     * Returns the current game matrix diff
     * 
     * @return the game matrix diff
     */
    public synchronized ArrayList<short[]> getMatrixDiff() {
    	return this.operationQueue;
    }

    /**
     * Returns the message to be displayed on the SquareBoard
     * 
     * @return the board message.
     */
    public String getMessage(){
    	return this.message;
    }
    
    /**
     * Changes the type of an individual square on the board.
     *
     * @param x         the horizontal position (0 <= x < width)
     * @param y         the vertical position (0 <= y < height)
     * @param figureType     the new block's type, or BlockWarsConstants.BLOCK_EMPTY for empty
     */
    public void setBlockType(int x, int y, int figureType) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }
        this.matrix[y][x] = figureType;

    }

    /**
     * Sets a message to display on the square board. This is supposed 
     * to be used when the board is not being used for active drawing, 
     * as it slows down the drawing considerably.
     *
     * @param message  a message to display, or null to remove a
     *                 previous message
     */
    public void setMessage(String message) {
        this.message = message;

    }

    /**
     * Clears the board, i.e. removes all the colored squares. As 
     * side-effects, the number of removed lines will be reset to 
     * zero, and the component will be repainted immediately.
     */
    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.matrix[y][x] = BlockWarsConstants.BLOCK_EMPTY;
            }
        }
    }

    /**
     * Removes all full lines. All lines above a removed line will be 
     * moved downward one step, and a new empty line will be added at 
     * the top. After removing all full lines, the component will be 
     * repainted.
     * 
     * @see #hasFullLines
     */
    public synchronized void removeFullLines() {
    	int counter = 0;
        // Remove full lines
        for (int y = height - 1; y >= 0; y--) {
            if (isLineFull(y)) {
            	counter++;
                removeLine(y);
                y++;
            }
        }
        if (counter > 0)
        	this.player.addLinesToOpponent(counter);
    }
    
    public synchronized void addLines(int num) {
    	this.addOperationToQueue(BlockWarsConstants.OP_DELETE_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
    	this.activeFigure.addLines(num);
    	for(int y=0; y<height-num; y++) {
    		for(int x=0; x<width; x++) {
    			this.setBlockType(x, y, this.matrix[y+num][x]);
    		}
    	}
    	
    	Random randomSeed = new Random();
    	for (int y=height-num; y<height; y++) {
    		int empty_x = randomSeed.nextInt(width-1);
    		for(int x=0; x<width; x++) {
    			if (empty_x == x)
    				this.setBlockType(x, y, BlockWarsConstants.BLOCK_EMPTY);
    			else
    				this.setBlockType(x, y, BlockWarsConstants.ADDED_LINE);
        	}
    		this.addOperationToQueue(BlockWarsConstants.OP_ADD_NEW_LINE, 0, empty_x);
    	}
    	
    	this.addOperationToQueue(BlockWarsConstants.OP_ADD_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
    }

    /**
     * Removes a single line. All lines above are moved down one step, 
     * and a new empty line is added at the top. No repainting will be 
     * done after removing the line.
     *
     * @param y         the vertical position (0 <= y < height)
     */
    private synchronized void removeLine(int y) {
        if (y < 0 || y >= height) {
            return;
        }
        
        this.addOperationToQueue(BlockWarsConstants.OP_REMOVE_LINE, 0, y);
        
        for (; y > 0; y--) {
            for (int x = 0; x < width; x++) {
                matrix[y][x] = matrix[y - 1][x];
            }
        }
        for (int x = 0; x < width; x++) {
            matrix[0][x] = BlockWarsConstants.BLOCK_EMPTY;
        }
    }
    
    /**
     * Check if a block can be moved to specified coordinate.
     * 
     * @return	true if the grid at the coordinate are empty,
     * 			false otherwise
     */
    private boolean canMoveBlockTo(int x, int y) {
    	if (y < 0 && x >= 0 && x < height)
    		return true;
        if (x < 0 || y >= height || x >= width) 
            return false;
    	return this.isSquareEmpty(x, y);
    }
    
    private boolean canMoveFigureTo(int x, int y) {
    	for(int i=0; i<4; i++) {
    		if (!this.canMoveBlockTo(x + this.activeFigure.getBlockDeltaX(i), y + this.activeFigure.getBlockDeltaY(i)))
    			return false;
    	}
    	return true;
    }
    
    private boolean canMoveFigureTo(int x, int y, int rotation) {
    	for(int i=0; i<4; i++) {
    		if (!this.canMoveBlockTo(x + this.activeFigure.getBlockDeltaX(i, rotation), y + this.activeFigure.getBlockDeltaY(i, rotation)))
    			return false;
    	}
    	return true;
    }
    
    /**
     * Moves the active figure one square to the left.
     */
    public synchronized void moveFigureLeft() {
    	if (this.activeFigure == null) {
    		return;
    	}
    	
    	int x = this.activeFigure.getX();
    	int y = this.activeFigure.getY();
    	if (this.canMoveFigureTo(x - 1, y)) {
    		this.addOperationToQueue(BlockWarsConstants.OP_DELETE_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
        	this.activeFigure.moveLeft();
        	this.addOperationToQueue(BlockWarsConstants.OP_ADD_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
    	}
    	
    }
    
    /**
     * Moves the active figure one square to the right.
     */
    public synchronized void moveFigureRight() {
    	if (this.activeFigure == null) {
    		return;
    	}
    	
    	int x = this.activeFigure.getX();
    	int y = this.activeFigure.getY();
    	if (this.canMoveFigureTo(x + 1, y)) {
    		this.addOperationToQueue(BlockWarsConstants.OP_DELETE_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
        	this.activeFigure.moveRight();
        	this.addOperationToQueue(BlockWarsConstants.OP_ADD_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
    	}
    	
    }
    
    /**
     * Moves the active figure one square down.
     */
    public synchronized void moveFigureDown() {
    	if (this.activeFigure == null) {
    		this.addNewFigure();
    	}
    	int x = this.activeFigure.getX();
    	int y = this.activeFigure.getY();
    	if (this.canMoveFigureTo(x, y+1)) {
    		this.addOperationToQueue(BlockWarsConstants.OP_DELETE_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
        	this.activeFigure.moveDown();
        	this.addOperationToQueue(BlockWarsConstants.OP_ADD_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
    	} else {
            this.landActiveFigure();
	    }
    }
    
    public synchronized void rotateFigure() {
    	if (this.canMoveFigureTo(this.activeFigure.getX(), this.activeFigure.getY(), this.activeFigure.getNextRotation())) {
    		this.addOperationToQueue(BlockWarsConstants.OP_DELETE_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
    		this.activeFigure.rotate();
    		this.addOperationToQueue(BlockWarsConstants.OP_ADD_FIGURE, this.activeFigure.getX(), this.activeFigure.getY());
    	}
    }
    
    public synchronized void addOperationToQueue(int operation_type, int x, int y) {
        short[] operation = new short[9];
        operation[0] = operationSequence++; // here may be a problem
        if (operation_type == BlockWarsConstants.OP_DELETE_FIGURE) {
        	for(int i=0; i<4; i++) {
        		operation[i+1] = this.calculateOperation(x + this.activeFigure.getBlockDeltaX(i), y + this.activeFigure.getBlockDeltaY(i), BlockWarsConstants.BLOCK_EMPTY);	
        	}
        } else if (operation_type == BlockWarsConstants.OP_ADD_FIGURE) {
        	for(int i=0; i<4; i++) {
        		operation[i+5] = this.calculateOperation(x + this.activeFigure.getBlockDeltaX(i), y + this.activeFigure.getBlockDeltaY(i), this.activeFigure.getFigureType());	
        	}
	    } else if (operation_type == BlockWarsConstants.OP_REMOVE_LINE) {
	    	operation[1] = this.calculateRowOperation(BlockWarsConstants.OP_REMOVE_LINE, y);
	    } else if (operation_type == BlockWarsConstants.OP_ADD_NEW_LINE) {
	    	operation[1] = this.calculateRowOperation(BlockWarsConstants.OP_ADD_NEW_LINE, y);
	    }
		operationQueue.add(operation);
    }
    
    private short calculateOperation(int x, int y, int block_type) {
    	return (short) ((block_type << 12) + (x << 7) + y);
    }
    
    
    private short calculateRowOperation(int operation_type, int parameter) {
    	short final_op = (short) ((1 << 15) + (operation_type << 6) + parameter);
    	return final_op;
    }
    
    
    /**
     * x is the operation sequence no that the client approved. We need to remove the operations from the beginning to that sequence no. Sequence no starts from 1, but the arraylist elements start from 0, so there is need for incrementing by one, in order to include the last approved operation to the removal.
     */
    public synchronized void shortenOperationQueue(short last_sequence) {
        try {
    	    short beginning = operationQueue.get(0)[0];
    	    int difference = last_sequence - beginning;
    	    operationQueue.subList(0,difference+1).clear();
	    } catch (IndexOutOfBoundsException e) {}
    }
    
    
    /**
     * Add new figure to the top of the board.
     * TODO: Fix the new figure's position.
     */
    public synchronized void addNewFigure() {
        if (this.canAddNewFigure()) {
        	Figure f = new Figure();
        	this.activeFigure = f;
    	} else {
    	    this.gameOver();
	    }
     }
    
     public void landActiveFigure() {
    	for(int i=0; i<4; i++) {
    		this.setBlockType(this.activeFigure.getX() + this.activeFigure.getBlockDeltaX(i), this.activeFigure.getY() + this.activeFigure.getBlockDeltaY(i), this.activeFigure.getFigureType());
     	}
    	this.removeFullLines();
    	this.addNewFigure();
    	this.moveFigureDown();
     }
     
     public void gameOver() {
    	 System.out.println("game over biatch!");
    	 this.player.gameOver();
    	 this.addOperationToQueue(BlockWarsConstants.OP_GAME_OVER, 0, 0);
     }
     
     public void victory() {
    	 this.player.victory();
    	 this.addOperationToQueue(BlockWarsConstants.OP_VICTORY, 0, 0);
     }
}
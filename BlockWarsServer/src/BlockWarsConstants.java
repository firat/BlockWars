import java.awt.Color;

/**
 * 
 * @author Fƒ±rat Can Ba≈üarƒ±r <fcbasarir@cs.bilgi.edu.tr>
 * @author Banu√ßi√ßek G√ºrc√ºoƒülu <banucicekg@cs.bilgi.edu.tr>
 *
 * This is a public class and is not intended for instantiation. It is a namespace for
 * all the constant values that will be used in the game code.
 * 
 */
public class BlockWarsConstants {
	public static final int BLOCK_EMPTY			= 0;	// block is empty 
	public static final int I_PIECE				=  1;	// part of an I piece
	public static final int O_PIECE				=  2;	// part of an O piece 
	public static final int T_PIECE				=  3;	// part of a T piece
	public static final int S_PIECE				=  4;	// part of an S piece
	public static final int Z_PIECE				=  5;	// part of a Z piece
	public static final int L_PIECE				=  6;	// part of an L piece
	public static final int J_PIECE				=  7;	// part of a J piece
	public static final int ADDED_LINE			= 8;

	public static final int MIN_THREAD_SLEEP	= 50;	// minimum value for the GameThread.sleepTimer
	
	public static final String ACCOUNTS_DB 		= "accounts.db"; // File to store accounts.
	
	public static final int ROWS 				= 30;
	public static final int COLUMNS				= 15;
	
	public static final String BOARD_BACKGROUND	= "#d4d0c8";	// background color of the board
	
	public static final Color[] COLORS 			= {
			Color.black, Color.blue, Color.red, Color.yellow, Color.orange,
			Color.cyan, Color.green, Color.magenta, Color.pink
		}; //


    public static final int OP_MOVE_DOWN        = 1;
    public static final int OP_ADD_NEW_FIGURE   = 2;
    public static final int OP_MOVE_RIGHT	    = 3;
    public static final int OP_MOVE_LEFT	    = 4;
    public static final int OP_REMOVE_LINE	    = 5;
    public static final int OP_DELETE_FIGURE		= 6;
    public static final int OP_ADD_FIGURE		= 7;
    public static final int OP_ADD_NEW_LINE		= 8;
    public static final int OP_GAME_OVER			= 9;
    public static final int OP_VICTORY 			= 10;

    public static final int KEY_NONE = 0;
    public static final int KEY_LEFT = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_DOWN = 3;
    public static final int KEY_UP = 4;
    
    public static final int MAX_PLAYERS = 2;
}


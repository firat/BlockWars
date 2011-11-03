import java.util.Random;

public class Figure {
	private static final int[] DELTA_I_PIECE_X = {0, 0, 0, 0};
	private static final int[] DELTA_I_PIECE_Y = {0, -1, 1, 2};
	
	private static final int[] DELTA_O_PIECE_X = {0, 1, 0, 1};
	private static final int[] DELTA_O_PIECE_Y = {0, 0, 1, 1};
	
	private static final int[] DELTA_T_PIECE_X = {0, -1, 1, 0};
	private static final int[] DELTA_T_PIECE_Y = {0, 0, 0, 1};
	
	private static final int[] DELTA_S_PIECE_X = {0, 1, 0, -1};
	private static final int[] DELTA_S_PIECE_Y = {0, 0, 1, 1};
	
	private static final int[] DELTA_Z_PIECE_X = {0, -1, 0, 1};
	private static final int[] DELTA_Z_PIECE_Y = {0, 0, 1, 1};
	
	private static final int[] DELTA_L_PIECE_X = {0, 0, 0, 1};
	private static final int[] DELTA_L_PIECE_Y = {0, -1, 1, 1};
	
	private static final int[] DELTA_J_PIECE_X = {0, 0, 0, -1};
	private static final int[] DELTA_J_PIECE_Y = {0, -1, 1, 1};

	private static final Random randomSeed = new Random();
	
	private int x, y, figureType, rotation, maxRotation;
	private int[] deltas_x;
	private int[] deltas_y;
	
	private static int randomType() {
		return Figure.randomSeed.nextInt(7) + 1;
	}
	
	Figure() {
		this.x = BlockWarsConstants.COLUMNS/2;
		this.figureType = Figure.randomType();
		switch(this.figureType){
			case BlockWarsConstants.I_PIECE:
				this.deltas_x = DELTA_I_PIECE_X;
				this.deltas_y = DELTA_I_PIECE_Y;
				this.maxRotation = 2;
				break;
			case BlockWarsConstants.Z_PIECE:
				this.deltas_x = DELTA_Z_PIECE_X;
				this.deltas_y = DELTA_Z_PIECE_Y;
				this.maxRotation = 2;
				break;
			case BlockWarsConstants.S_PIECE:
				this.deltas_x = DELTA_S_PIECE_X;
				this.deltas_y = DELTA_S_PIECE_Y;
				this.maxRotation = 2;
				break;
			case BlockWarsConstants.L_PIECE:
				this.deltas_x = DELTA_L_PIECE_X;
				this.deltas_y = DELTA_L_PIECE_Y;
				this.maxRotation = 4;
				break;
			case BlockWarsConstants.J_PIECE:
				this.deltas_x = DELTA_J_PIECE_X;
				this.deltas_y = DELTA_J_PIECE_Y;
				this.maxRotation = 4;
				break;
			case BlockWarsConstants.T_PIECE:	
				this.deltas_x = DELTA_T_PIECE_X;
				this.deltas_y = DELTA_T_PIECE_Y;
				this.maxRotation = 4;
				break;
			default:
				this.deltas_x = DELTA_O_PIECE_X;
				this.deltas_y = DELTA_O_PIECE_Y;
				this.maxRotation = 1;
		}
		this.rotation = randomSeed.nextInt(4);
		
		if (this.figureType == BlockWarsConstants.I_PIECE && this.rotation == 0) 
			this.y = -3;
		else
			this.y = -2;
	}
	
	public void moveLeft() {
		this.x--;
	}
	
	public void moveRight() {
		this.x++;
	}
	
	public void moveDown() {
		this.y++;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getFigureType() {
		return this.figureType;
	}
	
	public void rotate() {
		this.rotation = this.getNextRotation();
	}
	
	public int getNextRotation() {
		return (this.rotation + 1) % this.maxRotation;
	}
	
	public int getBlockDeltaX(int index) {
		switch(this.rotation) {
			case 0:
				return this.deltas_x[index];
			case 1:
				return -this.deltas_y[index];
			case 2:
				return -this.deltas_x[index];
			case 3:
				return this.deltas_y[index];
		}	
		return 0;
	}
	
	public int getBlockDeltaX(int index, int rotation) {
		switch(rotation) {
			case 0:
				return this.deltas_x[index];
			case 1:
				return -this.deltas_y[index];
			case 2:
				return -this.deltas_x[index];
			case 3:
				return this.deltas_y[index];
		}	
		return 0;
	}
	
	public int getBlockDeltaY(int index) {
		switch(this.rotation) {
		case 0:
			return this.deltas_y[index];
		case 1:
			return this.deltas_x[index];
		case 2:
			return -this.deltas_y[index];
		case 3:
			return -this.deltas_x[index];
	}
		return 0;
	}
	
	public int getBlockDeltaY(int index, int rotation) {
		switch(rotation) {
		case 0:
			return this.deltas_y[index];
		case 1:
			return this.deltas_x[index];
		case 2:
			return -this.deltas_y[index];
		case 3:
			return -this.deltas_x[index];
	}
		return 0;
	}
	
	public void addLines(int num) {
		if (this.y - num < 0)
			this.y = 0;
		else 
			this.y = this.y - num;
	}
}

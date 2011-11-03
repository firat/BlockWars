/**
 * 
 * @author Fırat Can Başarır <fcbasarir@cs.bilgi.edu.tr>
 * @author Banuçiçek Gürcüoğlu <banucicekg@cs.bilgi.edu.tr>
 *
 * 
 * This class holds the key event information of a player.
 */

public class InputThread extends Thread{
	private SquareBoard board;
	private int whichKey;
	private int inputCounter;
	private int sleepTime = 50;
	
	/**
	 * @param b	The SquareBoard for this thread to work on. 
	 */
	InputThread(SquareBoard b) {
		this.board = b;
		this.whichKey = BlockWarsConstants.KEY_NONE;
		this.inputCounter = 0;
	}
	

	/**
	 * Required method as a Thread.
	 */
	public void run() {
		for(;;) {

			if (this.whichKey != BlockWarsConstants.KEY_NONE || this.inputCounter == 0) {
				if (this.inputCounter == 0) {
					this.modifyBoardWithKey(this.whichKey);
					this.whichKey = BlockWarsConstants.KEY_NONE;
				} else if (this.inputCounter++ > 4) {
					this.modifyBoardWithKey(this.whichKey);
				}

			}
			
			try {
				Thread.sleep(this.sleepTime);
			} catch (InterruptedException e) {
			}		
		}
	}
	
	public void modifyBoardWithKey(int key) {
		synchronized (this.board) {
			if (key == BlockWarsConstants.KEY_LEFT) {
				this.board.moveFigureLeft();
			} else if (key == BlockWarsConstants.KEY_RIGHT) {
				this.board.moveFigureRight();
			} else if (key == BlockWarsConstants.KEY_DOWN) {
				this.board.moveFigureDown();
			} else if (key == BlockWarsConstants.KEY_UP && this.inputCounter == 0) {
				this.board.rotateFigure();
			}
		}
	}
	
	/**
	 * Method for setting the integer that stores the type of the key.
	 */
	public void processKeyCode(int key) {
		if (key == BlockWarsConstants.KEY_NONE) {
			this.inputCounter = 0;
		} else {
			this.inputCounter = 1;
			this.whichKey = key;
		}	
	}

	public int getKey() {
		return this.whichKey;
	}
}

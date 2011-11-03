/**
 * 
 * @author Fırat Can Başarır <fcbasarir@cs.bilgi.edu.tr>
 * @author Banuçiçek Gürcüoğlu <banucicekg@cs.bilgi.edu.tr>
 *
 * One GameThread instance is created for each player. GameThread makes sure the
 * active figure is falling down (i.e. this.board.moveFigureDown()). The speed of the
 * falling figure can be adjusted by setSleepTime.
 */
public class GameThread extends Thread {
	private SquareBoard board;
	private int sleepTime = 750;
	private boolean isPaused;
	
	/**
	 * @param b	The SquareBoard for this thread to work on. 
	 */
	GameThread(SquareBoard b) {
		this.board = b;
	}
	
	/**
	 * Required method as a Thread.
	 */
	public void run() {
		for(;;) {
			synchronized(this.board) {
				this.board.moveFigureDown();
			}
			
			if (this.isPaused) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					this.isPaused = false;
				}
			}
			
			try {
				Thread.sleep(this.sleepTime);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public void pause() {
		this.isPaused = true;
	}
	
	public boolean isPaused() {
		return this.isPaused;
	}
	
	/**
	 * Used for adjusting the speed of the falling block.
	 * 
	 * @param n	Speed of the block as int in milliseconds. 
	 * 			Lower the sleep time, faster the block falls.
	 * 			I can't be less than 50;
	 */
	public void setSleepTime(int n) {
		if (n < BlockWarsConstants.MIN_THREAD_SLEEP)
			this.sleepTime = BlockWarsConstants.MIN_THREAD_SLEEP;
		else
			this.sleepTime = n;
	}
}

/**
 * 
 * @author Fırat Can Başarır <fcbasarir@cs.bilgi.edu.tr>
 * @author Banuçiçek Gürcüoğlu <banucicekg@cs.bilgi.edu.tr>
 *
 * This is the server class implementing BlockWarsInterface used for RMI.
 * 
 * The exported object has a login method which returns a string. Once
 * the game runs (i.e. Server is started) the clients can get a "player hash" using
 * the login function. This string is unique for every player and is the base of our
 * security architecture. Every EXPORTED method, except login, requires this hash key.
 * If the hash key is invalid (i.e. no user with in the DB has the provided hash key),
 * the functions requiring the key will throw a remote exception.
 * 
 */
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class BlockWarsServer implements BlockWarsInterface {
		private boolean isGameRunning;
		private boolean isGameFinished;
		private Hashtable<String, Player> players;
		
		/**
		 * Constructor initializing the player list which is basically a hash table.
		 */
		BlockWarsServer() throws RemoteException {
			this.isGameRunning = false;
			this.isGameFinished = false;
			this.players = new Hashtable<String, Player>();
		}
		
		/**
		 * Checks if the given account information is correct (i.e. a PlayerAccount exists
		 * with the given username and password fields) and returns the Player's hash value 
		 * if it is true. Otherwise, returns null.
		 * 
		 * @param username String
		 * @param password String
		 * @return Player's hash as a String
		 * 	       null if the username and password is invalid
		 * @throws RemoteException
		 */
		public String login(String username, String password) throws RemoteException {
			Iterator<Player> playerIt = this.players.values().iterator();
			while(playerIt.hasNext()) {
				Player p = playerIt.next();
				if (p.hasPassword(password)) {
					String old_hash = p.getHash();
					p.setNewHash();
					String new_hash = p.getHash();
					this.players.put(new_hash, p);
					this.players.remove(old_hash);
					return new_hash;
				}
			}
			
			Player new_player = Player.login(username, password, this);
			String hash = new_player.getHash();
			this.players.put(hash, new_player);
			if (this.players.size() == BlockWarsConstants.MAX_PLAYERS	 && !this.isGameRunning) {
				playerIt = this.players.values().iterator();
				while (playerIt.hasNext()) {
					Player p = playerIt.next();
					p.startPlaying();
				}
				this.isGameRunning = true;
			}
			
			return new_player.getHash();
		}
		
		public synchronized void addLinesToOpponent(String hash, int num) {
			Iterator<Player> playerIt = this.players.values().iterator();
			while(playerIt.hasNext()) {
				Player p = playerIt.next();
				if (!p.getHash().equals(hash))
					p.getBoard().addLines(num);
			}
		}
		
		public synchronized void gameOver(String hash) {
			Iterator<Player> playerIt = this.players.values().iterator();
			while(playerIt.hasNext()) {
				Player p = playerIt.next();
				if (!p.getHash().equals(hash))
					p.getBoard().victory();
			}
		}
		
		public synchronized int[][] getMatrix(String hash) {
			return this.players.get(hash).getBoard().getMatrix();
		}
		
		/**
		 * @param hash	The player's hash.
		 * @return		The SquareBoard.matrix of the player.
		 * 
		 * @throws 		RemoteException
		 */
		public synchronized ArrayList<short[]> getNextSequence(String hash) throws RemoteException {
			if (this.isGameFinished)
				throw new RemoteException("Game over!");
			try {
				return this.players.get(hash).getBoard().getMatrixDiff();
			} catch (Exception e) {
				throw new RemoteException("Invalid login information.");
			}
		}
		
		public synchronized void acknowledgeSequence(String hash, short last_sequence) throws RemoteException {
			if (this.isGameFinished)
				throw new RemoteException("Game over!");
			try {
				this.players.get(hash).getBoard().shortenOperationQueue(last_sequence);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		public int getBoardWidth(String hash) throws RemoteException {
			if (this.isGameFinished)
				throw new RemoteException("Game over!");
			try {
				return this.players.get(hash).getBoard().getBoardWidth();
			} catch (Exception e) {
				throw new RemoteException("Invalid login information");
			}
		}
		
		public int getBoardHeight(String hash) throws RemoteException {
			if (this.isGameFinished)
				throw new RemoteException("Game over!");
			try {
				return this.players.get(hash).getBoard().getBoardHeight();
			} catch (Exception e) {
				throw new RemoteException("Invalid login information.");
			}
		}
		
		public synchronized void keyEvent(String hash, int key) throws RemoteException {
			if (this.isGameFinished)
				throw new RemoteException("Game over!");
			try {
				this.players.get(hash).getInputThread().processKeyCode(key);
				this.players.get(hash).getInputThread().interrupt();
			} catch (Exception e) {
				throw new RemoteException("Invalid login information");
			}
		}
		
		public synchronized int getScore(String hash) throws RemoteException {
			if (this.isGameFinished)
				throw new RemoteException("Game over!");
			return this.players.get(hash).getScore();
		}

}

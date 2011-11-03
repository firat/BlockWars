/**
 * 
 * @author Fırat Can Başarır <fcbasarir@cs.bilgi.edu.tr>
 * @author Banuçiçek Gürcüoğlu <banucicekg@cs.bilgi.edu.tr>
 *
 * This is the interface class designed to be implemented by RMI Server and Client.
 * See the implementation in BlockWarsServer for details.
 */
import java.rmi.*;
import java.util.ArrayList;

public interface BlockWarsInterface extends Remote {
	/**
	 * Tries the log the client in with the provided information.
	 * 
	 * @param username
	 * @param password
	 * @return	Client hash that will be used with every other function
	 * 			in order to provide security. Any function called with an invalid
	 * 			client hash will throw a RemoteException.
	 * @throws RemoteException
	 */
    public String login(String username, String password) throws RemoteException;
    
    public int[][] getMatrix(String hash) throws RemoteException;
    
    /**
     * This function is called at every game tick to update the current matrix in order
     * to render it.
     * 
     * @param hash The client hash
     * @return	Matrix of integers. The integer values represent the figure types.
     * 		  	See BlockWarsConstants for more info.
     * @throws RemoteException
     */
    public ArrayList<short[]> getNextSequence(String hash) throws RemoteException;
    
    public int getBoardWidth(String hash) throws RemoteException;
    public int getBoardHeight(String hash) throws RemoteException;
    public void keyEvent(String hash, int key) throws RemoteException;
    public void acknowledgeSequence(String hash, short last_squence) throws RemoteException;
}

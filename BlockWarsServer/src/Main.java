import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.rmi.AlreadyBoundException;
import java.util.Date;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try { 
			BlockWarsServer object = new BlockWarsServer();
			BlockWarsInterface shared = (BlockWarsInterface) UnicastRemoteObject.exportObject(object, 0);
			
			Registry registry = LocateRegistry.getRegistry();
			try {
				registry.bind("BlockWars", shared);
			} catch (AlreadyBoundException e) {
				registry.unbind("BlockWars");
				registry.bind("BlockWars", shared);
			}
			
			System.out.println("Server is ready");
			
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		
		for (;;){
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				break;
			}
		}
		
		System.exit(0);
	} 
}

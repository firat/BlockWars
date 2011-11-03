import java.rmi.registry.*; 

public class Main {
	public static void findAndPrintFigurePos(int[][] matrix, int width, int height) {
		for(int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				if (matrix[y][x] == BlockWarsConstants.I_PIECE)
					System.console().format("%d - %d\n", y, x);
				//else
					//System.console().format("!!! %d - %d\n", y, x);
			}
		}
	}
	
	public static void main(String[] args) {
		String host = "localhost"; 
		String objectname = "BlockWars"; 
		String password = "";
		try {
			host = args[0];
			password = args[1];
		} catch (Exception e) {
			System.out.println("USAGE: java Main <server address> <password>");
			System.exit(0);
		}

		try{ 
			//Locate the serve and the object on it 
			Registry registry = LocateRegistry.getRegistry(host);
			BlockWarsInterface server = (BlockWarsInterface) registry.lookup(objectname);
			String playerHash = server.login("lol", password);
			GameUI gui = new GameUI(server, playerHash);
			gui.initMatrix();
			for (;;) {
				gui.updateMatrix();
				Thread.sleep(50);
			}

		} catch (Exception e) { 
			e.printStackTrace();
		} 
	} 
}
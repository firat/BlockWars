import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameUI {
	private Frame frame;
	private GamePanel component;
	private BlockWarsInterface server;
	private String playerHash;
	private boolean keyPressed;
	private short lastOperationSequence;
	
	GameUI(BlockWarsInterface server, String playerHash) {
		this.frame = new Frame("Block Wars");
		this.server = server;
		this.playerHash = playerHash;
		this.component = new GamePanel();
		this.frame.add(this.component);
		this.frame.pack();
		this.frame.setVisible(true);
	}
	
	public void initMatrix() throws RemoteException {
		this.component.initMatrix(this.server.getMatrix(this.playerHash));
	}
	
	public void updateMatrix() throws RemoteException {
		ArrayList<short[]> operations = new ArrayList<short[]>();
		try {
			 operations = this.server.getNextSequence(this.playerHash);
			 if (!operations.isEmpty()) {
				this.lastOperationSequence = this.component.updateMatrix(operations, this.lastOperationSequence);
				this.server.acknowledgeSequence(this.playerHash, this.lastOperationSequence);
				this.component.reDraw();
			}
		} catch (java.rmi.UnmarshalException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
     * Handles a keyboard event. This will result in different actions
     * being taken, depending on the key pressed. In some cases, other
     * events will be launched. This method is synchronized to avoid 
     * race conditions with other asynchronous events (timer and 
     * mouse).
     * 
     * @param e         the key event
     */
    private void handleKeyEvent(KeyEvent e, boolean pressed) {
        // Handle remaining key events
    	try {
    		if (pressed) {
    			switch (e.getKeyCode()) {
    				case KeyEvent.VK_LEFT:
    					server.keyEvent(this.playerHash, BlockWarsConstants.KEY_LEFT);
    					break;
    				case KeyEvent.VK_RIGHT:
    					server.keyEvent(this.playerHash, BlockWarsConstants.KEY_RIGHT);
    					break;
    				case KeyEvent.VK_DOWN:
    					server.keyEvent(this.playerHash, BlockWarsConstants.KEY_DOWN);
    					break;
    				case KeyEvent.VK_UP:
    					server.keyEvent(this.playerHash, BlockWarsConstants.KEY_UP);
    					break;
    			}
    		} else {
    			server.keyEvent(this.playerHash, BlockWarsConstants.KEY_NONE);
    			
    		}
    	} catch (Exception exp) {}
    }
	
	private class GamePanel extends Container {
		private static final long serialVersionUID = 265931317911889680L;
		private Dimension size = null;
		private Label scoreLabel = new Label("Score: 0");
		private Label levelLabel = new Label("Level: 1");
		private Button button = new Button("Hello!");
		private SquareBoardComponent component;
		
		GamePanel() {
			super();
			initComponents();
		}
		
		public void reDraw() {
			this.component.reDraw();
		}
		
		public void initMatrix(int[][] matrix) {
			this.component.initMatrix(matrix);
		}
		
		public short updateMatrix(ArrayList<short []> operations, short lastOperationSequence) {
        	return this.component.updateMatrix(operations, lastOperationSequence);
        }
		
		/**
         * Paints the game component. This method is overridden from 
         * the default implementation in order to set the correct 
         * background color.
         * 
         * @param g     the graphics context to use
         */
        public void paint(Graphics g) {
            Rectangle  rect = g.getClipBounds();

            if (size == null || !size.equals(getSize())) {
                size = getSize();
                resizeComponents();
            }
            g.setColor(getBackground());
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
            super.paint(g);
        }
		
		@SuppressWarnings("deprecation")
		private void initComponents() {
			GridBagConstraints c;
			
			this.setLayout(new GridBagLayout());
			this.setBackground(Color.white); // TODO: Set this to BlockWarsConstants.BOARD_BACKGROUND
			this.component = new SquareBoardComponent(BlockWarsConstants.COLUMNS, BlockWarsConstants.ROWS);
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 4;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
			this.add(this.component, c);
			/*
			// Add generic button
            this.button.setBackground(Color.darkGray);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 3;
            c.weightx = 0.3;
            c.weighty = 1.0;
            c.anchor = GridBagConstraints.NORTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(15, 15, 15, 15);
            this.add(this.button, c);
            */
		
			this.component.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                	if (!keyPressed) {
                		keyPressed = true;
                		handleKeyEvent(e, true);
                	}
                }
                
                public void keyReleased(KeyEvent e) {
                	keyPressed = false;
                	handleKeyEvent(e, false);
                }
            });
			
			this.component.setFocusable(true);
			this.component.requestFocus();
		}

		
		/**
         * Resizes all the static components, and invalidates the
         * current layout.
         */
        private void resizeComponents() {
            Dimension  size = scoreLabel.getSize();
            Font       font;
            int        unitSize;
            
            // Calculate the unit size
            size = this.component.getSize();
            size.width /= this.component.getHeight();
            size.height /= this.component.getWidth();
            if (size.width > size.height) {
                unitSize = size.height;
            } else {
                unitSize = size.width;
            }

            // Adjust font sizes
            font = new Font("SansSerif", 
                            Font.BOLD, 
                            3 + (int) (unitSize / 1.8));
            scoreLabel.setFont(font);
            levelLabel.setFont(font);
            font = new Font("SansSerif", 
                            Font.PLAIN, 
                            2 + unitSize / 2);
            button.setFont(font);
            
            // Invalidate layout
            scoreLabel.invalidate();
            levelLabel.invalidate();
            button.invalidate();
        }        
    }
}

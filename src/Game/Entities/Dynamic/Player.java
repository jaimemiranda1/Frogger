package Game.Entities.Dynamic;

import Game.Entities.EntityBase;
import Game.GameStates.State;
import Game.World.WorldManager;
import Main.Handler;
import Resources.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/*
 * The Frog.
 */
public class Player extends EntityBase {
	private Handler handler;

	private Boolean moveUp, moveLeft, moveRight;
	private Rectangle player;
	public String facing = "UP";
	private Boolean moving = false;
	private int moveCoolDown=0;
	private int counter=0;
	private int frogCounter=0;
	public static int lastScore=0;

	private int index =0;

	public Player(Handler handler) {
		super(handler);
		this.handler = handler;
		this.handler.getEntityManager().getEntityList().add(this);

		player = new Rectangle(); 	// see UpdatePlayerRectangle(Graphics g) for its usage.
	}

	public void tick(){

		if(moving) {
			animateMovement();
			if((facing.equals("UP") || facing.equals("DOWN")) && WorldManager.inWater() && !WorldManager.inHazard()) {
				State.setState(handler.getGame().gameOverState);
			}
			if((facing.equals("RIGHT") || facing.equals("LEFT")) && WorldManager.inWater()){
				State.setState(handler.getGame().gameOverState);
			}
		}

		if(!moving){
			limits();
			hazardJump();
			move();
			if((facing.equals("UP") || facing.equals("DOWN")) && WorldManager.inWater() && !WorldManager.inHazard()) {
				State.setState(handler.getGame().gameOverState);
			}
			if((facing.equals("RIGHT") || facing.equals("LEFT")) && WorldManager.inWater()){
				State.setState(handler.getGame().gameOverState);
			}
		}

	}

	private void reGrid() {
		if(facing.equals("UP")) {
			if(this.getX() % 64 >= 64 / 2 ) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			}
			else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY()-64);
		}
	}

	private void move(){
		if(moveCoolDown< 25){
			moveCoolDown++;
		}
		index=0;

		/////////////////MOVE UP///////////////
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W) && !moving && facing.equals("UP") && moveUp){
			moving=true;
			score();
			WorldManager.blocking=0;
			WorldManager.cactusHit=0;
			WorldManager.bugHit=0;
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W) && !moving && !facing.equals("UP")){
			if(facing.equals("DOWN")) {
				if(this.getX() % 64 >= 64 / 2 ) {

					this.setX(this.getX() + (64 - this.getX() % 64));
				}
				else {
					this.setX(this.getX() - this.getX() % 64);
				}
				setY(getY() + 64);
			}
			if(facing.equals("LEFT")) {
				setY(getY() + 64);
			}
			if(facing.equals("RIGHT")) {
				setX(getX()-64);
				setY(getY()+64);
			}
			facing = "UP";
		}

		/////////////////MOVE LEFT///////////////
		else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A) && !moving && facing.equals("LEFT") && moveLeft){
			moving=true;
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A) && !moving&& !facing.equals("LEFT")){
			if(facing.equals("RIGHT")) {
				setX(getX()-64);
			}
			reGrid();
			facing = "LEFT";
		}

		/////////////////MOVE DOWN///////////////
		else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S) && !moving && facing.equals("DOWN")){
			moving=true;
			score();
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S) && !moving && !facing.equals("DOWN")){
			reGrid();
			if(facing.equals("RIGHT")){
				setX(getX()-64);
			}
			facing = "DOWN";
		}

		/////////////////MOVE RIGHT///////////////
		else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D) && !moving && facing.equals("RIGHT") && moveRight){
			moving=true;
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D) && !moving&& !facing.equals("RIGHT")){
			if(facing.equals("LEFT")) {
				setX(getX()+64);
			}
			if(facing.equals("UP")) {
				setX(getX()+64);
				setY(getY()-64);
			}
			if(facing.equals("DOWN")) {
				if(this.getX() % 64 >= 64 / 2 ) {
					this.setX(this.getX() + (64 - this.getX() % 64));
				}
				else {
					this.setX(this.getX() - this.getX() % 64);
				}
				setX(getX()+64);
			}
			facing = "RIGHT";
		}
	}

	private void animateMovement(){
		if(index==8) {
			moving = false;
			index = 0;
		}
		moveCoolDown = 0;
		index++;
		switch (facing) {
		case "UP":
			if (this.getX() % 64 >= 64 / 2) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			} else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY() - (8));
			break;

		case "LEFT":
			setX(getX() - (8));
			break;

		case "DOWN":
			if (this.getX() % 64 >= 64 / 2) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			} else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY() + (8));
			break;

		case "RIGHT":
			setX(getX() + (8));
			break;

		}
	}

	public void render(Graphics g){

		if(index>=8){
			index=0;
			moving = false;
		}

		switch (facing) {
		case "UP":
			g.drawImage(Images.Player[index], getX(), getY(), getWidth(), -1 * getHeight(), null);
			break;
		case "DOWN":
			g.drawImage(Images.Player[index], getX(), getY(), getWidth(), getHeight(), null);
			break;
		case "LEFT":
			g.drawImage(rotateClockwise90(Images.Player[index]), getX(), getY(), getWidth(), getHeight(), null);
			break;
		case "RIGHT":
			g.drawImage(rotateClockwise90(Images.Player[index]), getX(), getY(), -1 * getWidth(), getHeight(), null);
			break;
		}


		UpdatePlayerRectangle(g);
		paintComponent(g,10,45);

	}

	// Rectangles are what is used as "collisions." 
	// The hazards have Rectangles of their own.
	// This is the Rectangle of the Player.
	// Both come in play inside the WorldManager.
	private void UpdatePlayerRectangle(Graphics g) {

		player = new Rectangle(this.getX(), this.getY(), getWidth(), getHeight());

		if (facing.equals("UP")){
			player = new Rectangle(this.getX(), this.getY() - 64, getWidth(), getHeight());
		}
		else if (facing.equals("RIGHT")) {
			player = new Rectangle(this.getX() - 64, this.getY(), getWidth(), getHeight());
		}
	}

	//@SuppressWarnings("SuspiciousNameCombination")
	private static BufferedImage rotateClockwise90(BufferedImage src) {
		int width = src.getWidth();
		int height = src.getHeight();

		BufferedImage dest = new BufferedImage(height, width, src.getType());

		Graphics2D graphics2D = dest.createGraphics();
		graphics2D.translate((height - width) / 2, (height - width) / 2);
		graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
		graphics2D.drawRenderedImage(src, null);

		return dest;
	}

	public Rectangle getPlayerCollision() {
		return player;
	}

	private boolean limits() {
		moveUp = true;
		moveLeft = true;
		moveRight = true;

		if (player.getX() >= 8*64) {
			moveRight = false;
		}

		if(player.getX() <= 0) {
			moveLeft = false;
		}

		if (player.getY() <= 0.75*64) {
			moveUp = false;
		}

		return true;
	}
	
	public boolean hazardBounds() {
		if (facing.equals("RIGHT") && this.getX() >= 9*64
				|| (facing.equals("LEFT") && this.getX() >= 8*64)
				|| (facing.equals("UP") && this.getX() >= 8*64)
				|| (facing.equals("DOWN") && this.getX() >= 8*64))  {        		
			return false;	
		}
		return true;		
	}

	private void hazardJump() {
		if (this.getX() > 9*64) {
			this.setX(9*64);
		}
		if (this.getX() < 0) {
			this.setX(0);
		}
	}
	public void score() {
		if (WorldManager.cactusHit == 1 && moving) {
			counter-=6;
			frogCounter-=6;
		}
		if (WorldManager.bugHit == 1 && moving) {
			counter+=2;
			frogCounter+=2;
		}
		if (facing.equals("UP") && moving && WorldManager.blocking == 0 && counter == frogCounter  ) {
			counter+=1;
			frogCounter+=1;
		}
		else if (facing.equals("UP") && moving && counter > frogCounter  ) {
			
			frogCounter+=1;
		}
		else if (facing.equals("DOWN") && moving && WorldManager.blocking ==0 ) {		
			frogCounter-=1;
		}
		lastScore = counter;
	}
	public static void paintComponent(Graphics g, int x, int y) {
		g.setFont(new Font("Calibri", Font.BOLD, 44));
		g.setColor(Color.CYAN);
		g.drawString("SCORE: " + lastScore, x, y);
	}
}       			


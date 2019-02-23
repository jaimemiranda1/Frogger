package Game.World;

import Game.Entities.Dynamic.Player;
import Game.Entities.Static.LillyPad;
import Game.Entities.Static.Log;
import Game.Entities.Static.StaticBase;
import Game.Entities.Static.Tree;
import Game.Entities.Static.Turtle;
import Main.Handler;
import UI.UIManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Literally the world. This class is very important to understand.
 * Here we spawn our hazards (StaticBase), and our tiles (BaseArea)
 * 
 * We move the screen, the player, and some hazards. 
 * 				How? Figure it out.
 */
public class WorldManager{


	private ArrayList<BaseArea> AreasAvailables;			// Lake, empty and grass area (NOTE: The empty tile is just the "sand" tile. Ik, weird name.)
	private ArrayList<StaticBase> StaticEntitiesAvailables;	// Has the hazards: LillyPad, Log, Tree, and Turtle.

	private ArrayList<BaseArea> SpawnedAreas;				// Areas currently on world
	private ArrayList<StaticBase> SpawnedHazards;			// Hazards currently on world.

	Long time;
	Boolean reset = true;

	Handler handler;


	private Player player;									// How do we find the frog coordinates? How do we find the Collisions? This bad boy.

	UIManager object = new UIManager(handler);
	UI.UIManager.Vector object2 = object.new Vector();


	private ID[][] grid;									
	private int gridWidth,gridHeight;						// Size of the grid. 
	private int movementSpeed;								// Movement of the tiles going downwards.
	private int lastChoice; 
	private int lillyX;										// Random x pos for the lillys.
	private int treeX;										// Random x pos for trees.
	private int logXMin = 0;									// Random x pos for logs.
	private int logXMax = 0;

	public WorldManager(Handler handler) {
		this.handler = handler;

		AreasAvailables = new ArrayList<>();				// Here we add the Tiles to be utilized.
		StaticEntitiesAvailables = new ArrayList<>();		// Here we add the Hazards to be utilized.

		AreasAvailables.add(new GrassArea(handler, 0));		
		AreasAvailables.add(new WaterArea(handler, 0));
		AreasAvailables.add(new EmptyArea(handler, 0));

		StaticEntitiesAvailables.add(new LillyPad(handler, 0, 0));
		StaticEntitiesAvailables.add(new Log(handler, 0, 0));
		StaticEntitiesAvailables.add(new Tree(handler, 0, 0));
		StaticEntitiesAvailables.add(new Turtle(handler, 0, 0));

		SpawnedAreas = new ArrayList<>();
		SpawnedHazards = new ArrayList<>();

		player = new Player(handler);       

		gridWidth = handler.getWidth()/64;
		gridHeight = handler.getHeight()/64;
		movementSpeed = 1;
		// movementSpeed = 20; I dare you.

		/* 
		 * 	Spawn Areas in Map (2 extra areas spawned off screen)
		 *  To understand this, go down to randomArea(int yPosition) 
		 */

		for(int i=0; i<gridHeight+2; i++) {
			Random rand = new Random();
			int randNum = rand.nextInt(2);
			if (i==10) {
				if (randNum == 1) {
					SpawnedAreas.add(new GrassArea(handler,(-2+i)*64));
				}
				else {
					SpawnedAreas.add(new EmptyArea(handler,(-2+i)*64));
				}

			}
			else {
				SpawnedAreas.add(randomArea((-2+i)*64));
			}
		}

		player.setX((gridWidth/2)*64);
		player.setY((gridHeight-3)*64);

		// Not used atm.
		grid = new ID[gridWidth][gridHeight];
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridHeight; y++) {
				grid[x][y]=ID.EMPTY;
			}
		}
	}

	public void tick() {

		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[2])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[1];
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[0])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[2];
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[1])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[0];
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[3])) {
			this.object2.addVectors();
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[4]) && this.object2.isUIInstance) {
			this.object2.scalarProduct(handler);
		}

		if(this.reset) {
			time = System.currentTimeMillis();
			this.reset = false;
		}

		if(this.object2.isSorted) {

			if(System.currentTimeMillis() - this.time >= 2000) {		
				this.object2.setOnScreen(true);	
				this.reset = true;
			}

		}

		for (BaseArea area : SpawnedAreas) {
			area.tick();
		}
		for (StaticBase hazard : SpawnedHazards) {
			hazard.tick();
		}



		for (int i = 0; i < SpawnedAreas.size(); i++) {
			SpawnedAreas.get(i).setYPosition(SpawnedAreas.get(i).getYPosition() + movementSpeed);

			// Check if Area (thus a hazard as well) passed the screen.
			if (SpawnedAreas.get(i).getYPosition() > handler.getHeight()) {
				// Replace with a new random area and position it on top
				SpawnedAreas.set(i, randomArea(-2 * 64));
			}
			//Make sure players position is synchronized with area's movement
			if (SpawnedAreas.get(i).getYPosition() < player.getY()
					&& player.getY() - SpawnedAreas.get(i).getYPosition() < 3) {
				player.setY(SpawnedAreas.get(i).getYPosition());
			}
		}

		HazardMovement();

		player.tick();
		//make player move the same as the areas
		player.setY(player.getY()+movementSpeed); 

		object2.tick();

	}

	private void HazardMovement() {
		int x = player.getX();
		int y = player.getY();
		for (int i = 0; i < SpawnedHazards.size(); i++) {

			// Moves hazard down
			SpawnedHazards.get(i).setY(SpawnedHazards.get(i).getY() + movementSpeed);

			//Moves turtle to the left
			if(SpawnedHazards.get(i) instanceof Turtle) {
				SpawnedHazards.get(i).setX(SpawnedHazards.get(i).getX() - 1);

				if(SpawnedHazards.get(i).getX() < -64) {
					SpawnedHazards.get(i).setX(768);
				}
				if (SpawnedHazards.get(i).GetCollision() != null
						&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())
						&& player.hazardBounds()) {
					player.setX(player.getX() - 1);
				}
			}

			// Moves Log or to the right
			if (SpawnedHazards.get(i) instanceof Log) {
				SpawnedHazards.get(i).setX(SpawnedHazards.get(i).getX() + 1);

				if(SpawnedHazards.get(i).getX() > 568) {
					SpawnedHazards.get(i).setX(-192);
				}

				// Verifies the hazards Rectangles aren't null and
				// If the player Rectangle intersects with the Log or Turtle Rectangle, then
				// move player to the right.
				if (SpawnedHazards.get(i).GetCollision() != null
						&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())
						&& player.hazardBounds()) {
					player.setX(player.getX() + 1);
				}

			}

			if (SpawnedHazards.get(i) instanceof Tree) {
				if (SpawnedHazards.get(i).GetCollision() != null
						&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) 
					if (player.facing.equals("LEFT")) {					
						player.setX(x+16);

					}
					else if(player.facing.equals("RIGHT")) {
						player.setX(x-16);
					}
					else if(player.facing.equals("DOWN")) {
						player.setY(y-16);
					}
					else if(player.facing.equals("UP")) {
						player.setY(y+16);
					}

			}

			// if hazard has passed the screen height, then remove this hazard.
			if (SpawnedHazards.get(i).getY() > handler.getHeight()) {
				SpawnedHazards.remove(i);
			}
		}
	}


	public void render(Graphics g){

		for(BaseArea area : SpawnedAreas) {
			area.render(g);
		}

		for (StaticBase hazards : SpawnedHazards) {
			hazards.render(g);

		}

		player.render(g);       
		this.object2.render(g);      

	}

	/*
	 * Given a yPosition, this method will return a random Area out of the Available ones.)
	 * It is also in charge of spawning hazards at a specific condition.
	 */
	private BaseArea randomArea(int yPosition) {
		Random rand = new Random();

		// From the AreasAvailable, get me any random one.
		BaseArea randomArea = AreasAvailables.get(rand.nextInt(AreasAvailables.size())); 

		if(randomArea instanceof GrassArea) {
			randomArea = new GrassArea(handler, yPosition);
			SpawnTree(yPosition);
		}
		else if(randomArea instanceof WaterArea) {
			randomArea = new WaterArea(handler, yPosition);
			SpawnHazard(yPosition);
		}
		else {
			randomArea = new EmptyArea(handler, yPosition);
		}
		return randomArea;
	}

	/*
	 * Given a yPosition this method will add a tree to the SpawnedHazards ArrayList
	 */
	private void SpawnTree(int yPosition) {
		Random rand = new Random();
		int randInt;
		int choice = rand.nextInt(7);
		int randNum = rand.nextInt(4);

		for (int i = 0; i <= randNum; i++) {
			if (choice >= 2) {
				randInt = 64 * rand.nextInt(9);
				if(treeX == randInt) {
					while(treeX == randInt) {
						randInt = 64 * rand.nextInt(9);
					}
					SpawnedHazards.add(new Tree(handler, randInt, yPosition));
				}
				else {
					SpawnedHazards.add(new Tree(handler, randInt, yPosition));
				}
				treeX = randInt;
			}


		}
	}
	/*
	 * Given a yPosition this method will add a log or lillypad to the SpawnedHazards ArrayList
	 */
	private void SpawnHazard(int yPosition) {
		Random rand = new Random();
		int randInt;
		int choice = rand.nextInt(7);
		int randNum = rand.nextInt(5);
		// Chooses between Log or Lillypad

		if (choice <= 2) {
			randNum = rand.nextInt(5);
			if (randNum == 1 || randNum ==0) {
				randInt = 64 * 3;
				SpawnedHazards.add(new Log(handler, randInt, yPosition));
			}
			else if (randNum == 2) {
				for (int j=0; j<4; j+=3 ) {
					randInt = 64 * j;
					SpawnedHazards.add(new Log(handler, randInt, yPosition));
				}
			}
			else if (randNum == 3) {
				for (int j=-3; j<4; j+=3 ) {
					randInt = 64 * j;
					SpawnedHazards.add(new Log(handler, randInt, yPosition));
				}
			}
			else if (randNum ==4) {
				for (int j=-6; j<4; j+=3 ) {
					randInt = 64 * j;
					SpawnedHazards.add(new Log(handler, randInt, yPosition));
				}
			}
		}

		else if (choice >= 5){
			if(lastChoice >= 5) {
				int num = rand.nextInt(2);
				randInt = 64 * rand.nextInt(4);
				if(num == 0) {
					SpawnedHazards.add(new Log(handler, randInt, yPosition));
				}
				else {
					SpawnedHazards.add(new Turtle(handler, randInt, yPosition));
				}
			}
			else {
				for (int i = 0; i <= randNum; i++) {
					randInt = 64 * rand.nextInt(9);							
					if (lillyX == randInt) {							
						while (lillyX == randInt) {
							randInt = 64 * rand.nextInt(9);
						}
						SpawnedHazards.add(new LillyPad(handler, randInt, yPosition));
					}
					else {
						SpawnedHazards.add(new LillyPad(handler, randInt, yPosition));
					}
					lillyX = randInt;
				}
			} 
		}
		else {
			randInt = 64 * 9;
			SpawnedHazards.add(new Turtle(handler, randInt, yPosition));
		}
		lastChoice = choice;
	}

}


package Game.Entities.Static;

import java.awt.Graphics;
import java.awt.Rectangle;

import Main.Handler;
import Resources.Images;

public class Cactus extends StaticBase {
	
	private Rectangle cactus;

	public Cactus(Handler handler, int xPosition, int yPosition) {
		super(handler);
		// Sets original position to be this one.
        this.setY(yPosition);
        this.setX(xPosition);
	}

	@Override
	public void render(Graphics g) {

		g.drawImage(Images.cactus, this.getX(), this.getY(), 64, 64, null);
    	cactus = new Rectangle(this.getX(), this.getY()+5, 64, 55);
	}
	
    @Override
    public Rectangle GetCollision() {
    	
    	return cactus;
    }
}

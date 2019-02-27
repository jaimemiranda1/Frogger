package Game.Entities.Static;

import java.awt.Graphics;
import java.awt.Rectangle;

import Main.Handler;
import Resources.Images;

public class Rock extends StaticBase {
	
	private Rectangle Rock;

	public Rock(Handler handler, int xPosition, int yPosition) {
		super(handler);
		// Sets original position to be this one.
        this.setY(yPosition);
        this.setX(xPosition);
	}

	@Override
	public void render(Graphics g) {

		g.drawImage(Images.Rock, this.getX(), this.getY(), 64, 64, null);
    	Rock = new Rectangle(this.getX(), this.getY()+5, 64, 55);
	}
	
    @Override
    public Rectangle GetCollision() {
    	
    	return Rock;
    }
}

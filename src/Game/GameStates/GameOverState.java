package Game.GameStates;

import Main.Handler;
import Resources.Images;
import UI.UIImageButton;
import UI.UIManager;

import java.awt.*;


import Game.World.WorldManager;



/**
 * Created by AlexVR on 7/1/2018.
 */
public class GameOverState extends State {

    private int count = 0;
    private UIManager uiManager;

    public GameOverState(Handler handler) {
        super(handler);
        uiManager = new UIManager(handler);
        handler.getMouseManager().setUimanager(uiManager);

        /*
         * Adds a button that by being pressed changes the State
         */
        uiManager.addObjects(new UIImageButton(33, handler.getGame().getHeight() - 150, 128, 64, Images.retry, () -> {
            handler.getMouseManager().setUimanager(null);
            handler.getGame().reStart();
            State.setState(handler.getGame().gameState);
        }));

        uiManager.addObjects(new UIImageButton(33 + 192 * 2,  handler.getGame().getHeight() - 150, 128, 64, Images.BTitle, () -> {
            handler.getMouseManager().setUimanager(null);
            State.setState(handler.getGame().menuState);
        }));





    }
    

    @Override
    public void tick() {
        handler.getMouseManager().setUimanager(uiManager);
        uiManager.tick();
        count++;
        if( count>=30){
            count=30;
        }
       
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(Images.gameOver,0,0,handler.getGame().getWidth(),handler.getGame().getHeight(),null);
        uiManager.Render(g);

    }
}

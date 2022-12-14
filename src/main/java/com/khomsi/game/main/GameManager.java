package main.java.com.khomsi.game.main;

import main.java.com.khomsi.game.entity.Entity;
import main.java.com.khomsi.game.entity.Player;
import main.java.com.khomsi.game.main.tools.*;
import main.java.com.khomsi.game.tiles.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameManager extends JPanel implements Runnable {
    //Screen settings

    static final int ORIGINAL_TILE_SIZE = 16; //16x16 tiles

    //we need to scale the size of hero and game, because on big screens it'll be too small
    static final int SCALE = 3;
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; //48x48 tiles
    public static final int MAX_SCREEN_COL = 16; //16 tiles horizontal
    public static final int MAX_SCREEN_ROW = 12; //12 tiles vertical
    public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL; //768 pixels
    public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW; //576 pixels

    //World settings
    public static int maxWorldCol;

    public static int maxWorldRow;

    //TOOLS FOR GAME
    public KeyHandler keyHandler = new KeyHandler(this);
    public TileManager tileManager = new TileManager(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public UI ui = new UI(this);
    //use threads to start, stop,repeat actions.
    public Thread gameThread;
    public CheckCollision checkCollision = new CheckCollision(this);
    public PlaceObjects placeObjects = new PlaceObjects(this);
    public EventHandler eventHandler = new EventHandler(this);

    //ENTITY AND OBJECTS
    //TODO extend the massive, when you'll have more objects
    public Entity[] object = new Entity[10];
    public Entity[] npcList = new Entity[10];
    public Entity[] mobs = new Entity[10];
    List<Entity> entities = new ArrayList<>();

    public Player player = new Player(this, keyHandler);


    public static final int FPS = 60;
    //GameState
    public int gameState;
    public final int titleState = 0;
    public final int dialogueState = 3;
    public final int playState = 1;
    //depends on situation, draw dif keyInput
    public final int pauseState = 2;
    public final int characterState = 4;
    //until player doesn't press shift, he doesn't run
    public boolean playerRun = false;

    public GameManager() {
        //set size of this class
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        //if true, everything drawing from this comp will be done in an offscreen painting buffer.
        //it can improve the performance of rendering
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        //focus to receive key input
        this.setFocusable(true);
    }

    public void setupGame() {
        placeObjects.setObject();
        placeObjects.setNpc();
        placeObjects.setMobs();

        gameState = titleState;
    }

    public void startGameThread() {
        //pass gamePanel to thread
        gameThread = new Thread(this);
        //automatically calls run method
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                //update info as character position
                update();
                //draw the screen info with updated info
                repaint();
                delta--;
                drawCount++;
            }
            if (timer >= 1_000_000_000 && keyHandler.debugMode) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        //player
        if (gameState == playState) {
            player.update();
            //npc
            for (Entity npc : npcList) {
                if (npc != null) npc.update();
            }
            for (int i = 0; i < mobs.length; i++) {
                if (mobs[i] != null) {
                    if (mobs[i].alive && !mobs[i].die) {
                        mobs[i].update();
                    }
                    if (!mobs[i].alive) {
                        mobs[i] = null;
                    }
                }
            }
        }
        if (gameState == pauseState) {
            //Stop game
        }
    }

    //method to draw the components on screen
    @Override
    public void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);
        //extends graphic class and provide more control on geometry, color managment ect.
        Graphics2D graphics2D = (Graphics2D) graphics;
        //object
        long drawStart = 0;
        if (keyHandler.debugMode)
            drawStart = System.nanoTime();

        //title screen
        if (gameState == titleState) {
            ui.draw(graphics2D);
        }
        //others
        else {
            //Draw Tiles
            tileManager.draw(graphics2D);

            entities.add(player);
            //Add npc, obj, mobs to draw list
            for (Entity entityNpc : npcList) {
                if (entityNpc != null) {
                    entities.add(entityNpc);
                }
            }
            for (Entity entityObj : object) {
                if (entityObj != null)
                    entities.add(entityObj);
            }
            for (Entity mob : mobs) {
                if (mob != null)
                    entities.add(mob);
            }
            //Sort entities
            entities.sort(new EntityComparator());

            //Draw them
            for (Entity entity : entities) {
                entity.draw(graphics2D);
            }
            //Make list empty to not overload it
            entities.clear();
            //UI(text)
            ui.draw(graphics2D);
        }
        //Debug function
        if (keyHandler.debugMode) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            graphics2D.setFont(new Font("Arial", Font.PLAIN, 20));
            graphics2D.setColor(Color.WHITE);
            int x = 10;
            int y = 410;
            int lineHeight = 20;
            graphics2D.drawString("World_X: " + player.worldX, x, y);
            y += lineHeight;
            graphics2D.drawString("World_Y: " + player.worldY, x, y);
            y += lineHeight;
            graphics2D.drawString("Col: " + (player.worldX + player.solidArea.x) / TILE_SIZE, x, y);
            y += lineHeight;
            graphics2D.drawString("Row: : " + (player.worldY + player.solidArea.y) / TILE_SIZE, x, y);
            y += lineHeight;
            graphics2D.drawString("Invincible: : " + player.invincibleCounter, x, y);
            y += lineHeight;
            graphics2D.drawString("Draw Time: " + passed, x, y);
            y += lineHeight;
            graphics2D.drawString("Press Ctrl+F9 after ed map", x, y);
            y += lineHeight;
            graphics2D.drawString("Press F8 to reload tiles", x, y);
            //Show player bounds
            graphics2D.setColor(Color.RED);
            graphics2D.drawRect(player.screenX + player.solidArea.x, player.screenY + player.solidArea.y,
                    player.solidArea.width, player.solidArea.height);
        }
        //save some memory
        graphics2D.dispose();
    }

    //use method to loop the main music
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void pauseMusic(int time) {
        stopMusic();
        Timer timer = new Timer(time * 1000, arg0 -> playMusic(0));
        timer.setRepeats(false); // Only execute once
        timer.start(); // Go go go!
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }
}

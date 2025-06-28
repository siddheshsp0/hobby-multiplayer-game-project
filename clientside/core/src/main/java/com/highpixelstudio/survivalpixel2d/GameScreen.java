package com.highpixelstudio.survivalpixel2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.highpixelstudio.elements.Player;
import com.highpixelstudio.elements.PlayerDTO;
import com.highpixelstudio.networking.WebSocketHandler;


/*
 * Class for GameScreen
 */
public class GameScreen implements Screen {

    private final int SCALE_FACTOR = 32; // To set game scales
    final Main main; // Main object
    private final int GAME_RESOLUTION_X, GAME_RESOLUTION_Y; // Height and width of the game in world units (not pixels)
    private FitViewport fitViewport;
    private Camera camera;

    // Creating Arena
    private int environmentWidth, environmentHeight; // Complete part
    private float arenaWidth, arenaHeight; // Visible part, has to be even numbers
    private String environmentTexturePath;
    private Arena arena;

    // Creating character
    public int characterHeight, characterWidth;
    public String characterTexturePath;
    private Player currentPLayer;
    public float playerVelocity = 20, sprintFactor = 2f; // World units / second
    double playerX, playerY; // wrt environment, not arena

    WebSocketHandler socketHandler;
    




    // Constructor, pass the main function
    public GameScreen (Main main) {
        this.main = main;
        GAME_RESOLUTION_Y = main.ASPECT_X*SCALE_FACTOR;
        GAME_RESOLUTION_X = main.ASPECT_Y*SCALE_FACTOR;
    }

    @Override // Show method is like create method. This method is called when the screen is to be shown
    public void show() {
        camera = new OrthographicCamera();
        fitViewport = new FitViewport(GAME_RESOLUTION_X, GAME_RESOLUTION_Y, camera);
        socketHandler = new WebSocketHandler();
        socketHandler.connect("ws://localhost:8080/ws/game");

    // Creating arena
        environmentWidth = environmentHeight = 256;
        arenaWidth = GAME_RESOLUTION_X;
        arenaHeight = GAME_RESOLUTION_Y;
        environmentTexturePath = "environment.png";
        arena = new Arena(
            environmentTexturePath,
            main.batch,
            arenaWidth,
            arenaHeight,
            environmentWidth,
            environmentHeight,
            (-environmentWidth+arenaWidth)/2,
            (-environmentHeight+arenaHeight)/2,
            this
        );

    // Creating player
        playerX = environmentWidth/2; playerY = environmentHeight/2;
        characterWidth = 2; characterHeight = 3;
        characterTexturePath = "character_sprite.png";
        currentPLayer = new Player(
            GAME_RESOLUTION_X/2,
            GAME_RESOLUTION_Y/2,
            (float) playerX,
            (float) playerY,
            characterWidth,
            characterHeight,
            GAME_RESOLUTION_X,
            GAME_RESOLUTION_Y,
            characterTexturePath,
            main.batch
        );

    }


    @Override // Loop
    public void render(float delta) {
        // Batch Begin
        // Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set bg colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears Screen
        fitViewport.apply();
        main.batch.setProjectionMatrix(camera.combined);
        main.batch.begin();


        boolean sprint = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT); // Check sprint
        // boolean sprint = true;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            arena.moveArena(0.0f, -playerVelocity * delta * (sprint?sprintFactor:1.0f));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            arena.moveArena(0.0f, playerVelocity * delta * (sprint?sprintFactor:1.0f));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            arena.moveArena(playerVelocity * delta * (sprint?sprintFactor:1.0f), 0.0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            arena.moveArena(-playerVelocity * delta * (sprint?sprintFactor:1.0f), 0.0f);
        }


        float[]currentPLayerEnvCoords = arena.arenaToEnvironment(new float[]{currentPLayer.getX(), currentPLayer.getY()});

        arena.updateAndRender(delta);
        currentPLayer.updateAndRender(delta);
        //
        if (socketHandler.getSessionID()!=null){
            PlayerDTO playerDataTransferObject = new PlayerDTO(
                socketHandler.getSessionID(),
                currentPLayerEnvCoords[0],
                currentPLayerEnvCoords[1]
            );

            socketHandler.sendPlayer(playerDataTransferObject);
        }
        //

        main.batch.end();
    }





    @Override public void resize(int width, int height) {
        fitViewport.update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}

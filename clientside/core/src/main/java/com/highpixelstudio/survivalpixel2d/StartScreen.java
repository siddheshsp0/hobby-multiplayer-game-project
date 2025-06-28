package com.highpixelstudio.survivalpixel2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;



/**
 * Class for StartScreen
 */
public class StartScreen implements Screen{
    final Main main; // Main object
    private Stage stage;
    private Texture titleTexture, startButtonTexture, backgroundTexture;
    private final int GAME_RESOLUTION_X, GAME_RESOLUTION_Y; // Height and width of the game in world units (not pixels)
    private final int SCALE_FACTOR = 64;



    public StartScreen (Main main) {
        this.main = main;
        GAME_RESOLUTION_X = main.ASPECT_X*SCALE_FACTOR;
        GAME_RESOLUTION_Y = main.ASPECT_Y*SCALE_FACTOR;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_RESOLUTION_X, GAME_RESOLUTION_Y));
        Gdx.input.setInputProcessor(stage);

        titleTexture = new Texture("start_title.png");
        startButtonTexture = new Texture("start_button.png");
        backgroundTexture = new Texture("start_background.png");

        Image titleImage = new Image(titleTexture);
        int titleImageWidth = GAME_RESOLUTION_X, titleImageHeight = GAME_RESOLUTION_Y*1/3;
        titleImage.setPosition(
            (GAME_RESOLUTION_X - titleImageWidth) / 2,
            GAME_RESOLUTION_Y * 0.6f
        );
        titleImage.setSize(titleImageWidth, titleImageHeight);

        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setPosition(0f, 0f);
        backgroundImage.setSize(GAME_RESOLUTION_X, GAME_RESOLUTION_Y);
        backgroundImage.setColor(1.0f, 1.0f, 1.0f, 0.5f);

        ImageButton startButtonImage = new ImageButton(new TextureRegionDrawable(startButtonTexture));
        int startButtonImageWidth = GAME_RESOLUTION_X*4/9*2/3, startButtonImageHeight = GAME_RESOLUTION_Y*2/9*2/3;
        startButtonImage.setPosition(
            (GAME_RESOLUTION_X - startButtonImageWidth) / 2,
            GAME_RESOLUTION_Y * 0.3f
        );
        startButtonImage.setSize(startButtonImageWidth, startButtonImageHeight);
        startButtonImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new GameScreen(main));
            }
        });


        stage.addActor(backgroundImage);
        stage.addActor(titleImage);
        stage.addActor(startButtonImage);

        
    }


    @Override // Loop
    public void render(float delta) {
        // Clear screen and start batch
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set bg colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }



    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        titleTexture.dispose();
        startButtonTexture.dispose();
        stage.dispose();
    }
}


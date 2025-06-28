package com.highpixelstudio.survivalpixel2d;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.Game} implementation shared by all platforms. */
public class Main extends Game {
    // Global definitions
    public final int ASPECT_X = 1, ASPECT_Y = 1;

    public SpriteBatch batch; // Sprite batch

    public GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch(); // Init Sprite batch
        this.setScreen(new StartScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        getScreen().dispose();
    }
}

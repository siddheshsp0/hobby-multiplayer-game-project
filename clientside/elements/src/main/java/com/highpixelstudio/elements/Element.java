package com.highpixelstudio.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Element base class for all elements
 */
public class Element {
    private int environmentWidth, environmentHeight; // Environment size in environment units
    private Texture texture;
    private Sprite sprite;
    SpriteBatch batch;
    public float elementX, elementY; // Coordinates wrt arena
    
    
    /**
     * Constructor
     * @param posX Center, not bottom left
     * @param posY Center, not bottom left
     * @param elementX position on the arena
     * @param elementY position on the arena
     * @param sizeX
     * @param sizeY
     * @param environmentWidth
     * @param environmentHeight
     * @param texturePath
     * @param batch
     */

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }
    public Element(float posX, float posY, float elementX, float elementY, int sizeX, int sizeY, int environmentWidth, int environmentHeight, String texturePath, SpriteBatch batch) {
        this.environmentWidth = environmentWidth;
        this.environmentHeight = environmentHeight;
        this.texture = new Texture(texturePath);
        this.sprite = new Sprite(this.texture);
        this.sprite.setSize(sizeX, sizeY);
        this.sprite.setPosition((float)(posX - (sprite.getWidth())/2), (float)(posY - (sprite.getHeight())/2));
        this.batch = batch;
        this.elementX = elementX;
        this.elementY = elementY;
    }

    public void updateAndRender(float delta) {
        sprite.draw(batch);
    }

    public void setPosition(float x, float y) {
        sprite.setPosition((float)(x - (sprite.getWidth())/2), (float)(y - (sprite.getHeight())/2));
    }

    public boolean translate(float deltaX, float deltaY) {
        float newX = sprite.getX() + deltaX;
        float newY = sprite.getY() + deltaY;

        float clampedX = MathUtils.clamp(newX, 0, environmentWidth);
        float clampedY = MathUtils.clamp(newY, 0, environmentHeight);
        boolean withinBounds = (clampedX == newX) && (clampedY == newY);

        sprite.setPosition(clampedX, clampedY);
        return withinBounds;
    }


    public void rotate(float theta) {
        sprite.rotate(theta);
    }


}

package com.highpixelstudio.elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends Element{
    
    /**
     * 
     * @param posX
     * @param posY
     * @param sizeX
     * @param sizeY
     * @param environmentWidth
     * @param environmentHeight
     * @param texturePath
     */
    public Player(float posX, float posY, float elementX, float elementY, int sizeX, int sizeY, int environmentWidth, int environmentHeight, String texturePath, SpriteBatch batch) {
        super(posX, posY, elementX, elementY, sizeX, sizeY, environmentWidth, environmentHeight, texturePath, batch);
    }
    
}

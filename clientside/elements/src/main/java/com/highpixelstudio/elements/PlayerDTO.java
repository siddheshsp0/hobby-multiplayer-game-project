package com.highpixelstudio.elements;

/**
 * Player data transfer object
 */

public class PlayerDTO {
    private String sessionID;
    private float elementX, elementY;
    public PlayerDTO(String sessionID, float elementX, float elementY) {
        this.elementX = elementX;
        this.elementY = elementY;
        this.sessionID = sessionID;
    }

    public float getElementX() {
        return elementX;
    }
    public float getElementY() {
        return elementY;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

}

package com.highpixelstudio.survivalpixel2d;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.highpixelstudio.elements.Player;
import com.highpixelstudio.elements.PlayerDTO;
import com.highpixelstudio.networking.WebSocketHandler;

public class Arena {
    private final int environmentWidth, environmentHeight; // Complete part
    private final float arenaWidth, arenaHeight; // Visible part, has to be even numbers
    private Sprite arena;
    SpriteBatch batch;
    GameScreen screen;
    private final ConcurrentHashMap<String, Player> otherPlayersHistory = new ConcurrentHashMap<>();

    WebSocketHandler socketHandler;


    // private float playerVelocity, sprintFactor=1.5f;

    /**
     * 
     * @param arenaTexturePath
     * @param batch
     * @param arenaWidth
     * @param arenaHeight
     * @param environmentWidth
     * @param environmentHeight
     * @param initPosX Center of initial position X of arena
     * @param initPosY Center of initial position Y of arena
     */
    public Arena(String arenaTexturePath, SpriteBatch batch, float arenaWidth, float arenaHeight, int environmentWidth, int environmentHeight, float initPosX, float initPosY, GameScreen screen) {
        this.batch = batch;
        this.arenaWidth = arenaWidth; this.arenaHeight = arenaHeight;
        this.environmentWidth = environmentWidth; this.environmentHeight = environmentHeight;
        arena = new Sprite(new Texture(arenaTexturePath));
        arena.setSize(environmentWidth, environmentHeight);
        arena.setPosition(
            initPosX,
            initPosY
        );
        this.screen = screen;

        socketHandler = screen.socketHandler;

    }


    public void updateAndRender(float delta) {
        PlayerDTO[] otherPlayers = socketHandler.getOtherPlayers();
        if (otherPlayers != null) {
            HashSet<String> currentFramePlayers = new HashSet<>();

            for (PlayerDTO dto : otherPlayers) {
                String sessionID = dto.getSessionID();
                if (sessionID.equals(socketHandler.getSessionID())) continue;

                float[] newPlayerCoords = environmentToArena(new float[]{dto.getElementX(), dto.getElementY()});
                // float[]newPlayerCoords = {16,16};
                if (!otherPlayersHistory.containsKey(sessionID)) {
                    Player newPlayer = new Player(
                        newPlayerCoords[0], newPlayerCoords[1],
                        dto.getElementX(),
                        dto.getElementY(),
                        screen.characterWidth, screen.characterHeight,
                        environmentWidth, environmentHeight,
                        screen.characterTexturePath,
                        batch
                    );
                    otherPlayersHistory.put(sessionID, newPlayer);
                } else {
                    Player existing = otherPlayersHistory.get(sessionID);
                    existing.elementX = dto.getElementX();
                    existing.elementY = dto.getElementY();

                    float[] screenCoords = environmentToArena(new float[]{dto.getElementX(), dto.getElementY()});
                    existing.setPosition(screenCoords[0], screenCoords[1]);
                    // existing.setPosition(16f, 16f);
                }
                currentFramePlayers.add(sessionID);
            }

            // Remove disconnected players
            otherPlayersHistory.keySet().removeIf(id -> !currentFramePlayers.contains(id));
        }

        // Draw arena
        arena.draw(batch);
        
        int i=0;
        for (Player p : otherPlayersHistory.values()) {
            p.updateAndRender(delta);
            i++;
        }

    }


    public boolean moveArena(float deltaX, float deltaY) {
        float newX = arena.getX() + deltaX;
        float newY = arena.getY() + deltaY;

        float clampedX = MathUtils.clamp(newX, (-environmentWidth + arenaWidth/2), arenaWidth/2);
        float clampedY = MathUtils.clamp(newY, (-environmentHeight + arenaHeight/2), arenaHeight/2);

        boolean withinBounds = (clampedX == newX) && (clampedY == newY);
        arena.setPosition(clampedX, clampedY);

        return withinBounds;
    }

    public float[] environmentToArena(float[] coords) {
        return new float[]{coords[0]+arena.getX(), coords[1]+arena.getY()};
    }

    public float[] arenaToEnvironment(float[] coords) {
        return new float[]{coords[0] - arena.getX(), coords[1] - arena.getY()};
    }

}

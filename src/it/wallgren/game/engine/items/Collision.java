package it.wallgren.game.engine.items;

import it.wallgren.game.engine.items.gem.GemItem;

public class Collision {
    private final GemItem gem;
    private final Player player;
    private final Cell cell;
    
    public Collision(GemItem gem, Player player, Cell cell) {
        this.gem = gem;
        this.player = player;
        this.cell = cell;
    }
    
    public GemItem getGem() {
        return gem;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Cell getCell() {
        return cell;
    }
}

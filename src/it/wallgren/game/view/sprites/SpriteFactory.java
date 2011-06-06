package it.wallgren.game.view.sprites;

import it.wallgren.game.engine.items.HeadsUpDisplay;
import it.wallgren.game.engine.items.Item;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.gem.Arrow;
import it.wallgren.game.engine.items.gem.Laser;
import it.wallgren.game.view.sprites.weapons.ArrowSprite;
import it.wallgren.game.view.sprites.weapons.LaserSprite;
import android.content.Context;

public class SpriteFactory {
	
	public static Sprite<?,?> get(Context context, Item item) {
		if (item instanceof Player) {
			return new PlayerSprite(context, (Player) item);
		}
		
		if (item instanceof Laser) {
			return new LaserSprite(context, (Laser) item);
		}
		
		if (item instanceof HeadsUpDisplay) {
			return new HeadsUpDisplaySprite((HeadsUpDisplay) item);
		}
		
		if (item instanceof Arrow) {
			return new ArrowSprite(context, (Arrow) item);
		}
		
		throw new RuntimeException("Unable to create Sprite for item "  + item);
	}
}

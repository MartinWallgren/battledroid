package it.wallgren.game.view.sprites;

import it.wallgren.game.engine.items.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class SpriteCollection implements Iterable<Sprite<?,?>> {
    
    private TreeMap<Integer, HashMap<Item, Sprite<?,?>>> layers;
    private int size;
    
    public SpriteCollection() {
        layers = new TreeMap<Integer, HashMap<Item,Sprite<?,?>>>();
    }
    
    public void add(Sprite<?,?> sprite) {
        get(sprite.getZ()).put(sprite.getItem(), sprite);
        size++;
    }
    
    public Sprite<?,?> get(Item item) {
        return get(item.getZ()).get(item);
    }
    
    public void remove(Sprite<?,?> sprite) {
        if (get(sprite.getZ()).remove(sprite.getItem()) != null) {
            size--;
        }
    }

    private HashMap<Item, Sprite<?,?>> get(int layer){
        HashMap<Item, Sprite<?,?>> hashMap = layers.get(layer);
        
        if (hashMap == null) {
            hashMap = new HashMap<Item, Sprite<?,?>>();
            layers.put(layer, hashMap);
        }
        
        return hashMap;
    }

    public Iterator<Sprite<?,?>> iterator() {
        return new Iterator<Sprite<?,?>>() {
            int index;
            Iterator<HashMap<Item, Sprite<?,?>>> layerIterator = layers.values().iterator();
            Iterator<Sprite<?,?>> currentLayer;
            public boolean hasNext() {
                return index < size;
            }

            public Sprite<?,?> next() {
                Sprite<?,?> sprite = null;
                while(sprite == null) {
                    if(currentLayer == null || !currentLayer.hasNext()) {
                        if(layerIterator.hasNext()) {
                            currentLayer = layerIterator.next().values().iterator();
                        } else {
                            throw new NoSuchElementException("No more Sprites");
                        }
                    }
                    if(currentLayer.hasNext()) {
                        sprite = currentLayer.next();
                    }
                }
                
                index++;
                return sprite;
            }

            public void remove() {
                throw new UnsupportedOperationException("remove is not suported woup woup woup!");
            }
        };
    }
}

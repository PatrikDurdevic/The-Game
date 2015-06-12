package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;

/**
 *
 * @author ZRS
 */
public class Hunter extends Node {
    
    private Spatial playerNode;
    private Vector3f follow;
    private TerrainQuad terrain;
    private boolean atTarget; 
    private int iterator = -1;
    
    
    public Hunter(AssetManager assetManager, TerrainQuad terrainTo) {
        terrain = terrainTo;
        playerNode = assetManager.loadModel("Models/template animations9/template animations9.j3o");
        //Geometry geom = new Geometry("Spatial", b.g);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        playerNode.setLocalScale(10, 10, 10);
        playerNode.setLocalTranslation(10, 0, 10);
        playerNode.setMaterial(mat);
        atTarget = true;
        this.attachChild(playerNode);
    }
    
    public void setStaticTargetLocation(Vector3f targetLocation, int targetIterator) {
        follow = targetLocation;
        iterator = targetIterator;
        atTarget = false;
    }
    
    public int getTargetIterator() {
        return iterator;
    }
    
    public boolean needsNewLocation() {
        return atTarget;
    }
    
    
    public void movePlayerTowardsTarger() {
        if (!atTarget) {
        float x, y, z;
        x = playerNode.getLocalTranslation().x;
        z = playerNode.getLocalTranslation().z;
        float x1, y1, z1;
        x1 = follow.x;
        z1 = follow.z;
        y1 = follow.y;
        if (x1 > x) {
            x += 1;
        } else {
            x -= 1;
        }
        
        if (z1 > z) {
            z += 1;
        } else {
            z -= 1;
        }
        y = terrain.getHeight(new Vector2f(x, z));
        checkForPlayerWithTargetCollision();
        playerNode.setLocalTranslation(new Vector3f(x, y, z));
        playerNode.lookAt(new Vector3f(x1, y1, z1), Vector3f.ZERO);
        }
    }
    
    private void checkForPlayerWithTargetCollision() {
        float x, y, z;
        x = playerNode.getLocalTranslation().x;
        z = playerNode.getLocalTranslation().z;
        float x1, y1, z1;
        x1 = follow.x;
        z1 = follow.z;
        if (Math.abs(x-x1) < 5 && Math.abs(z-z1) < 5) {
            atTarget = true;
        }
    }
    
}

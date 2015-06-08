/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.Random;

/**
 *
 * @author ZRS
 */
public class Player extends Node {
    
    private Spatial playerNode;
    private Vector3f follow;
    private TerrainQuad terrain;
    private boolean atTarget; 
   
    
    public Player(AssetManager assetManager, TerrainQuad terrainTo) {
        terrain = terrainTo;
        playerNode = assetManager.loadModel("Models/template animations9/template animations9.j3o");
        //Geometry geom = new Geometry("Spatial", b.g);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.White);   // set color of material to blue
        playerNode.setLocalScale(10, 10, 10);
        playerNode.setLocalTranslation(0, 0, 0);
        playerNode.setMaterial(mat);
        atTarget = true;
        this.attachChild(playerNode);
    }
    
    public void setStaticTargetLocation(Vector3f targetLocation) {
        follow = targetLocation;
        atTarget = false;
    }
    
    public boolean needsNewLocation() {
        return atTarget;
    }
    
    public void movePlayerTowardsBox() {
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
        checkForPlayerWithBoxCollision();
        playerNode.setLocalTranslation(new Vector3f(x, y, z));
        playerNode.lookAt(new Vector3f(x1, y1, z1), Vector3f.ZERO);
        }
    }
    
    private void checkForPlayerWithBoxCollision() {
        float x, y, z;
        x = playerNode.getLocalTranslation().x;
        z = playerNode.getLocalTranslation().z;
        float x1, y1, z1;
        x1 = follow.x;
        z1 = follow.z;
        if (Math.abs(x-x1) < 2 && Math.abs(z-z1) < 2) {
            atTarget = true;
        }
    }
    
}

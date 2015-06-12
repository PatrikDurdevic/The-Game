/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
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
    private ParticleEmitter  projectile;
    
    
    public Player(AssetManager assetManager, TerrainQuad terrainTo) {
        terrain = terrainTo;
        playerNode = assetManager.loadModel("Models/template animations9/template animations9.j3o");
        //Geometry geom = new Geometry("Spatial", b.g);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.BlackNoAlpha);   // set color of material to blue
        playerNode.setLocalScale(10, 10, 10);
        playerNode.setLocalTranslation(0, 0, 0);
        playerNode.setMaterial(mat);
        atTarget = true;
        this.attachChild(playerNode);
        projectile = new ParticleEmitter("Emitter", Type.Triangle, 30);
        Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_red.setColor("Color", ColorRGBA.Red);
        projectile.setMaterial(mat_red);
        projectile.setImagesX(2); projectile.setImagesY(2); // 2x2 texture animation
        projectile.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        projectile.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        projectile.getParticleInfluencer().setInitialVelocity(new Vector3f(0,2,0));
        projectile.setStartSize(1.5f);
        projectile.setEndSize(0.1f);
        projectile.setGravity(0,0,0);
        projectile.setLowLife(0.5f);
        projectile.setHighLife(3f);
        projectile.getParticleInfluencer().setVelocityVariation(0.3f);
        projectile.setNumParticles(200);
        this.attachChild(projectile);
    }
    
    public void setStaticTargetLocation(Vector3f targetLocation) {
        follow = targetLocation;
        atTarget = false;
        fireProjectile();
    }
    
    public boolean needsNewLocation() {
        return atTarget;
    }
    
    public void fireProjectile() {
        projectile.emitAllParticles();
        this.attachChild(projectile);
    }
    
    public Vector2f getPlayerLocalTranslation() {
        
        return new Vector2f(playerNode.getLocalTranslation().x,playerNode.getLocalTranslation().z);
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
    
    public void moveProjectileTowardsTarget() {
        if (!atTarget) {
        float x, y, z;
        x = projectile.getLocalTranslation().x;
        z = projectile.getLocalTranslation().z;
        float x1, y1, z1;
        x1 = follow.x;
        z1 = follow.z;
        y1 = follow.y;
        if (x1 > x) {
            x += 2;
        } else {
            x -= 2;
        }
        
        if (z1 > z) {
            z += 2;
        } else {
            z -= 2;
        }
        y = terrain.getHeight(new Vector2f(x, z)) + 20;
        checkForProjectileWithTargetCollision();
        projectile.setLocalTranslation(new Vector3f(x, y, z));
        projectile.lookAt(new Vector3f(x1, y1, z1), Vector3f.ZERO);
        }
    }
    
    private void checkForPlayerWithTargetCollision() {
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
    
    private void checkForProjectileWithTargetCollision() {
        float x, y, z;
        x = projectile.getLocalTranslation().x;
        z = projectile.getLocalTranslation().z;
        float x1, y1, z1;
        x1 = follow.x;
        z1 = follow.z;
        if (Math.abs(x-x1) < 10 && Math.abs(z-z1) < 10) {
            projectile.killAllParticles();
            projectile.removeFromParent();
        }
    }
    
}

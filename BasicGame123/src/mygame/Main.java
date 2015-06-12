package mygame;
 
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
 
    private Player player;
    private Hunter hunter;
    private TerrainQuad terrain;
    private float oldX, oldY, oldZ;
    private Geometry geom;
    
    private List<Player> players = new ArrayList();
    private List<Hunter> hunters = new ArrayList();
   
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
 
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(500);
        
        Material mat_terrain = new Material(assetManager,
            "Common/MatDefs/Terrain/Terrain.j3md");
 
    /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("Alpha", assetManager.loadTexture(
            "Textures/alphamap.png"));
 
    /** 1.2) Add GRASS texture into the red layer (Tex1). */
    Texture grass = assetManager.loadTexture(
            "Textures/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex1", grass);
    mat_terrain.setFloat("Tex1Scale", 64f);
 
    /** 1.3) Add DIRT texture into the green layer (Tex2) */
    Texture dirt = assetManager.loadTexture(
            "Textures/images.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex2", dirt);
    mat_terrain.setFloat("Tex2Scale", 32f);
 
    /** 1.4) Add ROAD texture into the blue layer (Tex3) */
    Texture rock = assetManager.loadTexture(
            "Textures/fetch.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex3", rock);
    mat_terrain.setFloat("Tex3Scale", 128f);
 
    /** 2. Create the height map */
    AbstractHeightMap heightmap;
    Texture heightMapImage = assetManager.loadTexture(
            "Textures/mountains512.png");
    heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
    heightmap.load();
   
    int patchSize = 65;
    terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());
 
    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, 0, 0);
    terrain.setLocalScale(2f, 1f, 2f);
    rootNode.attachChild(terrain);
 
    /** 5. The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
    terrain.addControl(control);
        for (int i = 0; i < 20; i++) {
            player = new Player(assetManager, terrain);
            rootNode.attachChild(player);
            players.add(player);
        }
        for (int i = 0; i < 1; i++) {
            hunter = new Hunter(assetManager, terrain);
            rootNode.attachChild(hunter);
            hunters.add(hunter);
        }
    }
    
 
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        Vector3f pos = player.getLocalTranslation();
        float y = terrain.getHeight(new Vector2f(pos.x, pos.z));
        //initKeys();
        
        for (Player p : players) {
            p.movePlayerTowardsTarger();
            p.moveProjectileTowardsTarget();
            if (p.needsNewLocation()) {
                Random rnd = new Random();
                float x1, y1, z1;
                x1 = rnd.nextInt(1028) - 514;
                z1 = rnd.nextInt(1028) - 514;
                y1 = terrain.getHeight(new Vector2f(x1, z1));
                p.setStaticTargetLocation(new Vector3f(x1, y1, z1));
            }
        }
        for (Hunter h : hunters) {
            if (players.isEmpty()) { break; }
            h.movePlayerTowardsTarger();
            int oldIt = h.getTargetIterator();
            if (h.needsNewLocation()) {
                if (oldIt >= 0) {
                    rootNode.detachChild(players.get(oldIt));
                    players.remove(oldIt);
                }
                Random rnd = new Random();
                int size = players.size();
                if (players.isEmpty()) break;
                int it = rnd.nextInt(size);
                float x1, y1, z1;
                x1 = players.get(it).getPlayerLocalTranslation().x;
                z1 = players.get(it).getPlayerLocalTranslation().y;
                y1 = terrain.getHeight(new Vector2f(x1, z1));
                h.setStaticTargetLocation(new Vector3f(x1, y1, z1), it);
            } else {
                float x1, y1, z1;
                x1 = players.get(oldIt).getPlayerLocalTranslation().x;
                z1 = players.get(oldIt).getPlayerLocalTranslation().y;
                y1 = terrain.getHeight(new Vector2f(x1, z1));
                h.setStaticTargetLocation(new Vector3f(x1, y1, z1), oldIt);
            }
        }
    }
   
    private void initKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_K));
       
        inputManager.addListener(analogListener, "Left", "Right", "Forward", "Back");
    }
   
    private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {
        /*Vector3f pos = b.getLocalTranslation();
        float y = terrain.getHeight(new Vector2f(pos.x, pos.z));
        if (!Float.isNaN(y)) {
            if (name.equals("Left")) {
              b.setLocalTranslation(pos.x + 0.5f, y, pos.z);
              b.lookAt(new Vector3f(pos.x + 1f, y, pos.z), new Vector3f(pos.x, y, pos.z));
            }
            if (name.equals("Right")) {
              b.setLocalTranslation(pos.x - 0.5f, y, pos.z);
              b.lookAt(new Vector3f(pos.x - 1f, y, pos.z), new Vector3f(pos.x, y, pos.z));
            }
            if (name.equals("Forward")) {
              b.setLocalTranslation(pos.x, y, pos.z + 0.5f);
              b.lookAt(new Vector3f(pos.x, y, pos.z + 1f), new Vector3f(pos.x, y, pos.z));
            }
            if (name.equals("Back")) {
              b.setLocalTranslation(pos.x, y, pos.z - 0.5f);
              b.lookAt(new Vector3f(pos.x, y, pos.z - 1f), new Vector3f(pos.x, y, pos.z));
            }
        }*/
    }
  };
 
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    
}
package mygame;
 
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.Random;
 
/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
 
    private Spatial b;
    private TerrainQuad terrain;
    private float oldX, oldY, oldZ;
    private Geometry geom;
   
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
 
    @Override
    public void simpleInitApp() {
        b = assetManager.loadModel("Models/template animations9/template animations9.j3o");
        //Geometry geom = new Geometry("Spatial", b.g);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.White);   // set color of material to blue
        b.setLocalScale(10, 10, 10);
        b.setLocalTranslation(0, 0, 0);
        b.setMaterial(mat);
        rootNode.attachChild(b);
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
    AbstractHeightMap heightmap = null;
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
       
       initBox();
       randomizeBoxPosition();
       addBox();
    }
 
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        Vector3f pos = b.getLocalTranslation();
        float y = terrain.getHeight(new Vector2f(pos.x, pos.z));
        initKeys();
        
        movePlayerTowardsBox();
        /**System.out.println(y);
        if (!Float.isNaN(y)) {
            b.setLocalTranslation(pos.x+1, y, pos.z);
 
            if (oldX != pos.x || oldY != pos.y || oldZ != pos.z) {
                oldX = pos.x; oldY = pos.y; oldZ = pos.z;
                Box box = new Box(1, 1, 1); // create cube shape
                Geometry geom = new Geometry("Box", box);
                Material mat = new Material(assetManager,
                  "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Blue);
                geom.setMaterial(mat);
                rootNode.attachChild(geom);
                geom.setLocalTranslation(new Vector3f(pos.x, pos.y, pos.z));
            }
        }*/
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
        Vector3f pos = b.getLocalTranslation();
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
        }
    }
  };
 
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void checkForPlayerWithBoxCollision() {
        float x, y, z;
        x = b.getLocalTranslation().x;
        z = b.getLocalTranslation().z;
        float x1, y1, z1;
        x1 = geom.getLocalTranslation().x;
        z1 = geom.getLocalTranslation().z;
        if (Math.abs(x-x1) < 2 && Math.abs(z-z1) < 2) {
            removeBox();
            randomizeBoxPosition();
            addBox();
        }
    }
    
    private void movePlayerTowardsBox() {
        float x, y, z;
        x = b.getLocalTranslation().x;
        z = b.getLocalTranslation().z;
        float x1, y1, z1;
        x1 = geom.getLocalTranslation().x;
        z1 = geom.getLocalTranslation().z;
        y1 = geom.getLocalTranslation().y;
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
        b.setLocalTranslation(new Vector3f(x, y, z));
        b.lookAt(new Vector3f(x1, y1, z1), Vector3f.ZERO);
        checkForPlayerWithBoxCollision();
    }
    
     private void initBox() {
        Box b = new Box(1, 1, 1);
        geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
    }
    
    private void randomizeBoxPosition() {
        Random rand = new Random();
        float x, y, z;
        x = rand.nextInt(1028) - 514;
        z = rand.nextInt(1028) - 514;
        y = terrain.getHeight(new Vector2f(x, z));
        geom.setLocalTranslation(new Vector3f(x, y, z));
    }
     
    private void addBox() {
        rootNode.attachChild(geom);
    }
    
    private void removeBox() {
        rootNode.detachChild(geom);
    }
}
package test.theleo.sevensky;

/*
 * Copyright (c) 2018, Juraj Papp
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the copyright holder nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import theleo.sevensky.core.SevenSky;
import theleo.sevensky.core.SkyVars;
import theleo.sevensky.elements.Clouds;
import theleo.sevensky.elements.Sky;


public class TestSky extends SimpleApplication {

    public static void main(String[] args) {
        TestSky app = new TestSky();
		app.settings = new AppSettings(true);
//		app.settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        app.start();
    }

	AmbientLight ambient = new AmbientLight();
	SevenSky skySeven;
	Sky sky;
	Clouds clouds;
    @Override
    public void simpleInitApp() {
		getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
		getCamera().setFrustumPerspective(60, getCamera().getWidth() / (float) getCamera().getHeight(),
				0.1f, 1000);
		getCamera().setLocation(new Vector3f(0,10,0));
		flyCam.setDragToRotate(true);
		
		{ 
			Geometry geom = new Geometry("Box", new Box(1, 1, 1));
			geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

			Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
			mat.setColor("Diffuse", ColorRGBA.Blue);
			mat.setColor("Ambient", ColorRGBA.Blue);
			mat.setBoolean("UseMaterialColors", true);
			geom.setMaterial(mat);

			rootNode.attachChild(geom);
			
		}
		{
			Geometry floor = new Geometry("Box", new Box(2000, 0.1f, 2000));
			floor.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
			
			Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
			mat2.setColor("Diffuse", ColorRGBA.Gray);
			mat2.setColor("Ambient", ColorRGBA.Gray);
			mat2.setBoolean("UseMaterialColors", true);
			floor.setMaterial(mat2);

			rootNode.attachChild(floor);
		}
		
		SkyVars vars = SkyVars.Earth();
		
		skySeven = new SevenSky(vars, getCamera(), getRenderManager(), getAssetManager());
		
		sky = new Sky();
		skySeven.add(sky);

		clouds = new Clouds();
		skySeven.add(clouds);
		
		
		getViewPort().addProcessor(skySeven);

//		Vector3f sunLight = new Vector3f(1,1,0).normalizeLocal().negate();
		Vector3f sunLight = skySeven.c.getVec3(Sky.SunDir).negate();
		
	
		rootNode.addLight(ambient = new AmbientLight(ColorRGBA.Pink));
		
		DirectionalLight light = new DirectionalLight(sunLight);
		light.setColor(ColorRGBA.White);
		rootNode.addLight(light);
		
		FilterPostProcessor fpp = new FilterPostProcessor(getAssetManager());
		fpp.addFilter(new SevenSky.LightFilter(vars));
		getViewPort().addProcessor(fpp);
		
		
		

    }
	
	boolean init = false;

    @Override
    public void simpleUpdate(float tpf) {
		if(sky != null && sky.skyLightGen != null && !init) {
			init = true;
			Picture pic = new Picture("");
	//		pic.setTexture(am, pathLengthLut, false);
			pic.setTexture(getAssetManager(), sky.gen.tex, false);
			pic.setCullHint(Spatial.CullHint.Never);
			pic.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
			pic.setLocalTranslation(0, getCamera().getHeight() - 128, 0);

			pic.setWidth(128);
			pic.setHeight(128);
	//		rootNode.attachChild(pic);
	//		pic.setWidth(gen.tex.getImage().getWidth());
	//		pic.setHeight(gen.tex.getImage().getHeight());
	//		pic.setWidth(app.getCamera().getWidth());
	//		pic.setHeight(app.getCamera().getHeight());
			guiNode.attachChild(pic);
		}
		
		
		if(skySeven == null) return; 
		Vector3f amb = new Vector3f();
		for(Vector3f v : sky.lights)
			amb.addLocal(v);
//		amb.mult(0.05f/sky.lights.length);
		amb.normalizeLocal();
				
		ambient.setColor(new ColorRGBA(amb.x, amb.y, amb.z, 1f));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
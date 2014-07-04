package com.minecraft_vr.render;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.Project;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ForgeHooksClient;

public class VRRenderer extends EntityRenderer {

	EntityRenderer er;
	public VRRenderer(EntityRenderer _er) {
		super(_er.mc, _er.resourceManager);
		er = _er;
		sync(er,this);
	}

    /**
     * Will update any inputs that effect the camera angle (mouse) and then render the world and GUI
     */
    public void updateCameraAndRender(float par1)
    {
        this.mc.mcProfiler.startSection("lightTex"); 

        if (this.lightmapUpdateNeeded)
        {
            this.updateLightmap(par1);
        }

        this.mc.mcProfiler.endSection();

        this.prevFrameTime = Minecraft.getSystemTime();

        renderGuiLayer(par1);

        Framebuffer fbGUI = this.mc.getFramebuffer();
        fbGUI.unbindFramebuffer();

        setHeadOrientation(par1);
        setHeadPosition(par1);
        setBodyOrientation(par1);

        this.mc.mcProfiler.startSection("level");
        
        
        //Stuff we can do with only 1 pass
        if( this.mc.theWorld != null )
        {
        	//renderWorld()
	        if (this.mc.renderViewEntity == null)
	        {
	            this.mc.renderViewEntity = this.mc.thePlayer;
	        }
	
	        this.mc.mcProfiler.endStartSection("pick");
	        this.getMouseOver(par1);
        }
        
        
        //TODO: bind to main render FB
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        for( int j = 0; j < 2; j++ )
        {
            if( j == 0 )
            {
            	GL11.glViewport(0, 0, this.mc.displayWidth/2, this.mc.displayHeight);
            }
            else
            {
            	GL11.glViewport(this.mc.displayWidth/2, 0, this.mc.displayWidth/2, this.mc.displayHeight);
            }

            this.mc.mcProfiler.endStartSection("camera");

	        if (this.mc.theWorld != null)
	        {
		        GL11.glEnable(GL11.GL_CULL_FACE);
		        GL11.glEnable(GL11.GL_DEPTH_TEST);
		        GL11.glEnable(GL11.GL_ALPHA_TEST);
		        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
	
	        	//TODO: this will go away
		        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
		        GL11.glMatrixMode(GL11.GL_PROJECTION);
		        GL11.glLoadIdentity();
		
		        Project.gluPerspective(this.getFOVModifier(par1, true), (float)0.5 * (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
		
		        GL11.glMatrixMode(GL11.GL_MODELVIEW);
		        GL11.glLoadIdentity();
		        
		        this.orientCamera(par1);
	        	renderWorld(par1, j);
	        }
	        else
	        {
		        GL11.glMatrixMode(GL11.GL_PROJECTION);
		        GL11.glLoadIdentity();
		
		        Project.gluPerspective(90, (float)0.5 * (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
		
		        GL11.glMatrixMode(GL11.GL_MODELVIEW);
		        GL11.glLoadIdentity();
	        	//Render something? Otherwise, its a black void at the main menu
	        }
	        this.renderEndNanoTime = System.nanoTime();
	        vrGUIRender();
        }
        
        
        //vr compose, eventually (pre-warp, antialias, etc)

        fbGUI.bindFramebuffer(true); //rebind the GUI FB so all other draw calls get caught by it
    }
    
    public void preRenderPass( float par1 )
    {
    	
    }
    
    public void renderWorld( float par1, int renderpass )
    {
    	EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
        RenderGlobal renderglobal = this.mc.renderGlobal;
        EffectRenderer effectrenderer = this.mc.effectRenderer;
        this.mc.mcProfiler.endStartSection("clear");


        GL11.glEnable(GL11.GL_CULL_FACE);
        this.updateFogColor(par1);

        Frustrum frustrum = new Frustrum();
        if( renderpass == 0 )
        {
            //This extracts orientation and position from the current OpenGL matrices
            ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
            this.mc.mcProfiler.endStartSection("frustrum");
            ClippingHelperImpl.getInstance(); //updates the frustrum
	        double d0 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * (double)par1;
	        double d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * (double)par1;
	        double d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * (double)par1;
            frustrum.setPosition(d0, d1, d2);

            this.mc.mcProfiler.endStartSection("culling");
            this.mc.renderGlobal.clipRenderersByFrustum(frustrum, par1);

            this.mc.mcProfiler.endStartSection("updatechunks");
            this.mc.renderGlobal.updateRenderers(entitylivingbase, false);
        }


        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            this.setupFog(-1, par1);
            this.mc.mcProfiler.endStartSection("sky");
            renderglobal.renderSky(par1);
        }

        GL11.glEnable(GL11.GL_FOG);
        this.setupFog(1, par1);

        if (this.mc.gameSettings.ambientOcclusion != 0)
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }

        if (entitylivingbase.posY < 128.0D)
        {
            this.renderCloudsCheck(renderglobal, par1);
        }

        this.mc.mcProfiler.endStartSection("prepareterrain");
        this.setupFog(0, par1);
        GL11.glEnable(GL11.GL_FOG);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        this.mc.mcProfiler.endStartSection("terrain");
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        renderglobal.sortAndRender(entitylivingbase, 0, (double)par1);
        GL11.glShadeModel(GL11.GL_FLAT);
        EntityPlayer entityplayer;

        if (this.debugViewDirection == 0)
        {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            ForgeHooksClient.setRenderPass(0);
            renderglobal.renderEntities(entitylivingbase, frustrum, par1);
            ForgeHooksClient.setRenderPass(0);
            // ToDo: Try and figure out how to make particles render sorted correctly.. {They render behind water}
            this.enableLightmap((double)par1);
            this.mc.mcProfiler.endStartSection("litParticles");
            effectrenderer.renderLitParticles(entitylivingbase, par1);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, par1);
            this.mc.mcProfiler.endStartSection("particles");
            effectrenderer.renderParticles(entitylivingbase, par1);
            this.disableLightmap((double)par1);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glPushMatrix();

            if (this.mc.objectMouseOver != null && entitylivingbase.isInsideOfMaterial(Material.water) && entitylivingbase instanceof EntityPlayer && !this.mc.gameSettings.hideGUI)
            {
                entityplayer = (EntityPlayer)entitylivingbase;
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                this.mc.mcProfiler.endStartSection("outline");
                if (!ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), par1))
                {
                    renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, par1);
                }
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

        if (this.cameraZoom == 1.0D && entitylivingbase instanceof EntityPlayer && !this.mc.gameSettings.hideGUI && this.mc.objectMouseOver != null && !entitylivingbase.isInsideOfMaterial(Material.water))
        {
            entityplayer = (EntityPlayer)entitylivingbase;
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            this.mc.mcProfiler.endStartSection("outline");
            if (!ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), par1))
            {
                renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, par1);
            }
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }

        this.mc.mcProfiler.endStartSection("destroyProgress");
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);
        renderglobal.drawBlockDamageTexture(Tessellator.instance, entitylivingbase, par1);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        this.mc.mcProfiler.endStartSection("weather");
        this.renderRainSnow(par1);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        this.setupFog(0, par1);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        this.mc.mcProfiler.endStartSection("water");
        if (this.mc.gameSettings.fancyGraphics)
        {

            if (this.mc.gameSettings.ambientOcclusion != 0)
            {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);

            renderglobal.sortAndRender(entitylivingbase, 1, (double)par1);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glShadeModel(GL11.GL_FLAT);
        }
        else
        {
            this.mc.mcProfiler.endStartSection("water");
            renderglobal.sortAndRender(entitylivingbase, 1, (double)par1);
        }

        RenderHelper.enableStandardItemLighting();
        this.mc.mcProfiler.endStartSection("entities");
        ForgeHooksClient.setRenderPass(1);
        renderglobal.renderEntities(entitylivingbase, frustrum, par1);
        ForgeHooksClient.setRenderPass(-1);
        RenderHelper.disableStandardItemLighting();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_FOG);

        if (entitylivingbase.posY >= 128.0D)
        {
            this.mc.mcProfiler.endStartSection("aboveClouds");
            this.renderCloudsCheck(renderglobal, par1);
        }

        this.mc.mcProfiler.endStartSection("FRenderLast");
        ForgeHooksClient.dispatchRenderLast(renderglobal, par1);

        this.mc.mcProfiler.endStartSection("hand");

        if (this.cameraZoom == 1.0D)
        {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            this.renderHand(par1, renderpass);
        }

    	
    }
    
    public void vrGUIRender( )
    {
        this.mc.mcProfiler.startSection("vr-gui-render");
        Framebuffer fbGUI = this.mc.getFramebuffer();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -10.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        fbGUI.bindFramebufferTexture();
        double scale = 3;
        double aspectRatio = 2*this.mc.displayWidth/(double)this.mc.displayHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(-1);
        tessellator.addVertexWithUV( scale*aspectRatio,  scale, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(-scale*aspectRatio,  scale, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(-scale*aspectRatio, -scale, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV( scale*aspectRatio, -scale, 0.0D, 1.0D, 0.0D);
        tessellator.draw();
        fbGUI.unbindFramebufferTexture();
        GL11.glDepthMask(true);
        this.mc.mcProfiler.endSection();
    }
    
    public void setHeadOrientation( float par1 )
    {
    	
    }
    
    public void setBodyOrientation( float par1 )
    {
    	
        this.mc.mcProfiler.startSection("mouse");

        
        //TODO: this will go away
        if (this.mc.inGameHasFocus && Display.isActive())
        {
            this.mc.mouseHelper.mouseXYChange();
            float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f1 * f1 * f1 * 8.0F;
            float f3 = (float)this.mc.mouseHelper.deltaX * f2;
            float f4 = (float)this.mc.mouseHelper.deltaY * f2;
            byte b0 = 1;

            if (this.mc.gameSettings.invertMouse)
            {
                b0 = -1;
            }

            if (this.mc.gameSettings.smoothCamera)
            {
                this.smoothCamYaw += f3;
                this.smoothCamPitch += f4;
                float f5 = par1 - this.smoothCamPartialTicks;
                this.smoothCamPartialTicks = par1;
                f3 = this.smoothCamFilterX * f5;
                f4 = this.smoothCamFilterY * f5;
                this.mc.thePlayer.setAngles(f3, f4 * (float)b0);
            }
            else
            {
                this.mc.thePlayer.setAngles(f3, f4 * (float)b0);
            }
        }
    }
    
    public void setHeadPosition( float par1 )
    {
    	
    }

    
    public void renderGuiLayer( float par1 )
    {
        final ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        final int mousex = Mouse.getX() * width / this.mc.displayWidth;
        final int mousey = height - Mouse.getY() * height  / this.mc.displayHeight - 1;

        GL11.glPushAttrib( GL11.GL_COLOR_BUFFER_BIT );
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT );
        GL11.glPopAttrib();

        if (this.mc.theWorld != null)
        {
            this.mc.mcProfiler.endStartSection("gui");

            if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null)
            {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                this.mc.ingameGUI.renderGameOverlay(par1, this.mc.currentScreen != null, mousex, mousey);
            }
        }

        if (this.mc.currentScreen != null)
        {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

            try
            {
            	setupOverlayRendering();
                this.mc.currentScreen.drawScreen(mousex, mousey, par1);
            }
            catch (Throwable throwable)
            {
            	//No crash reports.... oops!

            }
        }


    }
	
    //For syncing renderer state when switching between vanilla renderer and VR renderer
	public static void sync( EntityRenderer from, EntityRenderer to)
	{
		to.farPlaneDistance = from.farPlaneDistance;
		to.rendererUpdateCount = from.rendererUpdateCount;
		to.pointedEntity = from.pointedEntity;
		to.mouseFilterXAxis = from.mouseFilterXAxis;
		to.mouseFilterYAxis = from.mouseFilterYAxis;
		to.mouseFilterDummy1 = from.mouseFilterDummy1;
		to.mouseFilterDummy2 = from.mouseFilterDummy2;
		to.mouseFilterDummy3 = from.mouseFilterDummy3;
		to.mouseFilterDummy4 = from.mouseFilterDummy4;
		to.thirdPersonDistance = from.thirdPersonDistance;
		to.thirdPersonDistanceTemp = from.thirdPersonDistanceTemp;
		to.debugCamYaw = from.debugCamYaw;
		to.prevDebugCamYaw = from.prevDebugCamYaw;
		to.debugCamPitch = from.debugCamPitch;
		to.prevDebugCamPitch = from.prevDebugCamPitch;
		to.smoothCamYaw = from.smoothCamYaw;
		to.smoothCamPitch = from.smoothCamPitch;
		to.smoothCamFilterX = from.smoothCamFilterX;
		to.smoothCamFilterY = from.smoothCamFilterY;
		to.smoothCamPartialTicks = from.smoothCamPartialTicks;
		to.debugCamFOV = from.debugCamFOV;
		to.prevDebugCamFOV = from.prevDebugCamFOV;
		to.camRoll = from.camRoll;
		to.prevCamRoll = from.prevCamRoll;
		to.fovModifierHand = from.fovModifierHand;
		to.fovModifierHandPrev = from.fovModifierHandPrev;
		to.fovMultiplierTemp = from.fovMultiplierTemp;
		to.bossColorModifier = from.bossColorModifier;
		to.bossColorModifierPrev = from.bossColorModifierPrev;
		to.cloudFog  = from.cloudFog;
		to.theShaderGroup = from.theShaderGroup;
		to.shaderIndex = from.shaderIndex;
		to.cameraZoom = from.cameraZoom;
		to.cameraYaw = from.cameraYaw;
		to.cameraPitch = from.cameraPitch;
		to.prevFrameTime = from.prevFrameTime;
		to.renderEndNanoTime = from.renderEndNanoTime;
		to.lightmapUpdateNeeded = from.lightmapUpdateNeeded;
		to.torchFlickerX = from.torchFlickerX;
		to.torchFlickerDX = from.torchFlickerDX;
		to.torchFlickerY = from.torchFlickerY;
		to.torchFlickerDY = from.torchFlickerDY;
		to.random = from.random;
		to.rainSoundCounter = from.rainSoundCounter;
		to.rainXCoords = from.rainXCoords;
		to.rainYCoords = from.rainYCoords;
		to.fogColorBuffer = from.fogColorBuffer;
		to.fogColorRed = from.fogColorRed;
		to.fogColorGreen = from.fogColorGreen;
		to.fogColorBlue = from.fogColorBlue;
		to.fogColor2 = from.fogColor2;
		to.fogColor1 = from.fogColor1;
		to.debugViewDirection = from.debugViewDirection;
	}

}

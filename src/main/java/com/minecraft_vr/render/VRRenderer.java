package com.minecraft_vr.render;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;

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
        boolean flag = Display.isActive();

        if (!flag && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1)))
        {
            if (Minecraft.getSystemTime() - this.prevFrameTime > 500L)
            {
                this.mc.displayInGameMenu();
            }
        }
        else
        {
            this.prevFrameTime = Minecraft.getSystemTime();
        }

        this.mc.mcProfiler.startSection("mouse");

        //TODO: get head orientation
        //TODO: get body orientation
        //TODO: get head position
        
        if (this.mc.inGameHasFocus && flag)
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

        this.mc.mcProfiler.endSection();

        final ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        final int mousex = Mouse.getX() * i / this.mc.displayWidth;
        final int mousey = j - Mouse.getY() * j / this.mc.displayHeight - 1;

        if (this.mc.currentScreen != null)
        {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

            try
            {
                this.mc.currentScreen.drawScreen(mousex, mousey, par1);
            }
            catch (Throwable throwable)
            {
            	//No crash reports.... oops!

            }
        }

        if (this.mc.theWorld != null)
        {
            this.mc.mcProfiler.endStartSection("gui");

            if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null)
            {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                this.mc.ingameGUI.renderGameOverlay(par1, this.mc.currentScreen != null, mousex, mousey);
            }

            this.mc.mcProfiler.startSection("level");
            this.mc.getFramebuffer().unbindFramebuffer();

            //TODO: in stereo
            this.renderWorld(par1, 0L);
           
	        //Note: shaders are disabled for now, as they would require yet another render pass

            this.renderEndNanoTime = System.nanoTime();

        }
        else
        {
        	//Render something? Otherwise, its a black void at the main menu
            GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            this.setupOverlayRendering();
        }
        
        this.mc.mcProfiler.startSection("vr-compose");

        this.mc.mcProfiler.endSection();

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

package com.minecraft_vr.render;

import net.minecraft.client.shader.Framebuffer;

//This class is used to replace the default render target.
//The primary purpose is to prevent drawing outside of the VRRenderer
//by overloading framebufferRender
public class FrameBufferShim extends Framebuffer {

	public FrameBufferShim(Framebuffer buf ) {
		super(32,32,false); //just dummy values. We'll delete it anyway
		deleteFramebuffer();
		framebufferTextureWidth = buf.framebufferTextureWidth;
		framebufferTextureHeight = buf.framebufferTextureHeight;

	    framebufferWidth = buf.framebufferWidth;
	    framebufferHeight = buf.framebufferHeight;

	    framebufferObject = buf.framebufferObject;
	    framebufferTexture = buf.framebufferTexture;

		useDepth = buf.useDepth;
	    depthBuffer = buf.depthBuffer;

	    framebufferColor = buf.framebufferColor;
	    framebufferFilter = buf.framebufferFilter;
	}

    public void framebufferRender(int width, int height )
    {
    	//Do nothing!
    }

}

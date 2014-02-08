package com.minecraft_vr.render;

import net.minecraft.client.shader.Framebuffer;

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

    public void framebufferRender(int p_147615_1_, int p_147615_2_)
    {
    	//Do nothing!
    }

}

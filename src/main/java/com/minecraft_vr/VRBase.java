package com.minecraft_vr;

import com.minecraft_vr.api.IBodyOrientation;
import com.minecraft_vr.api.IHMD;
import com.minecraft_vr.api.IHeadOrientation;
import com.minecraft_vr.api.IHeadPosition;
import com.minecraft_vr.render.FrameBufferShim;
import com.minecraft_vr.render.VRRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mod(modid = VRBase.MODID, version = VRBase.VERSION, name = VRBase.NAME, canBeDeactivated = true, useMetadata = false )
public class VRBase
{
    public static final String MODID = "com.minecraft-vr.base";
    public static final String VERSION = "1.0";
    public static final String NAME = "VR Base";
    
    EntityRenderer oldRenderer;
    EntityRenderer newRenderer;
    Framebuffer oldBuffer;
    Framebuffer newBuffer;
    
    public static VRBase GetInstance()
    {
    	return inst;
    }
    
    @Instance(MODID)
    private static VRBase inst;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	//Swap out renderer
    	swap();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @EventHandler
    public void modDisableEvent(FMLModDisabledEvent evt)
    {
    	System.out.println("Disabling VR mode");
    	if(Minecraft.getMinecraft().entityRenderer==newRenderer)
    		swap();
    }
    
    private void swap()
    {
        Minecraft mc = Minecraft.getMinecraft();
        
        if(oldBuffer == null)
        {
        	oldRenderer = mc.entityRenderer;
        	oldBuffer = mc.framebufferMc;
        	newBuffer = new FrameBufferShim(mc.framebufferMc);
        	newRenderer = new VRRenderer(mc.entityRenderer);
        }
        if(mc.entityRenderer == oldRenderer)
        {
        	mc.entityRenderer = newRenderer;
        	mc.framebufferMc = newBuffer;
        }
        else
        {
        	mc.entityRenderer = oldRenderer;
        	mc.framebufferMc = oldBuffer;
        }
        
    }
    
    public void RegisterPlugin( IBodyOrientation bodyOrient )
    {
    	
    }	   
    
    @SubscribeEvent
    public void EntityJoinWorld(EntityJoinWorldEvent evt)
    {
    	evt.entity.ignoreFrustumCheck = true;
    }
    
    public static IBodyOrientation bodyOrientation;
    public static IHeadOrientation headOrientation;
    public static IHeadPosition    headPosition;
    public static IHMD             hmd;
    
}

package com.minecraft_vr.base;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = VRBase.MODID, version = VRBase.VERSION)
public class VRBase
{
    public static final String MODID = "com.minecraft-vr.base";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        System.out.println("Welcome to VR!");
        Minecraft mc = Minecraft.getMinecraft();
        mc.entityRenderer = new VRRenderer(mc.entityRenderer);
    }
}

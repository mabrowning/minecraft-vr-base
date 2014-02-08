package com.minecraft_vr.asm;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;

public class VRModContainer extends DummyModContainer {
	public VRModContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "VRCore";
		meta.name = "Minecraft VR Core";
		meta.version = "";
		meta.credits = "";
		meta.authorList.add( "mabrowning" );
		meta.authorList.add( "StellaArtois" );
		meta.description = "CoreMod for altering the vanilla render state in support of the VR Base";
		meta.url = "https://minecraft-vr.com/";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}
}

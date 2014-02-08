package com.minecraft_vr;

import java.util.Map;

import com.minecraft_vr.asm.VRClassTransformer;
import com.minecraft_vr.asm.VRModContainer;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({"com.minecraft_vr.asm"})
public class VRLoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{ VRClassTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return VRModContainer.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}

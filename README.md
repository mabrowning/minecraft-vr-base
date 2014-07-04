Minecraft VR Forge Mod (base)
===================================

Minecraft Version: 1.7.10

Current Version: 0.0 (this doesn't do anything you want it to yet!)

Copyright StellaArtois, mabrowning 2014. See LICENSE.md for more.

See http://minecraft-vr.com/ for full project details.

IMPORTANT NOTE
--------------

This version is extremely work-in-progress. When it is the least bit stable, an
announcement will be made on all the relevant channels.

BUILDING
--------

Uses gradle.

gradle setupDevWorkspace will create a development worksapce
gradle eclipse will create an eclipse project in this directory

gradle build will build all dependencies and the mod.

Launch configuration:

MainClass: net.minecraft.launchwrapper.Launch

Args: --version 1.7 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker --accessToken FML --userProperties={} --assetIndex 1.7.10 --assetsDir $HOME/.gradle/caches/minecraft/assets

(Replace $HOME with your home directory in order to get sound effects)

JVM Args: -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.coreMods.load=com.minecraft_vr.VRLoadingPlugin



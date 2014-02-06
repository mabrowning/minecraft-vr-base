Minecraft VR Forge Mode (Minecrift)
===================================

Current Version: Minecrift 1.7.2 

StellaArtois, mabrowning 2014

With thanks to:

- Palmer Luckey and his team for creating the Oculus Rift. The future is
  finally here (well for some people anyway; mine hasn't arrived yet).
- Markus "Notch" Persson for creating Minecraft. What has it grown into?
- The team behind the MCP coders' pack, and the Minecraft community - why
  Mojang bother obfuscating the source when you guys have done such a fantastic
  job of de-obfuscating it is beyond me!
- Powback for his initial work on the Java JNI wrapper to the Oculus SDK. Seeing 
  this inspired me to get off my arse and get modding. See
  [this Reddit thread](http://www.reddit.com/r/oculus/comments/1c1vh0/java_wrapper_for_devs/)
- shakesoda and Ben (and others?) at MTBS for creating the GLSL version of the
  Oculus distortion shader.
- The guys at Valve for giving some good advice on updating a game for VR.
- @PyramidHead76 for building the MacOS libs, and toiling to produce the
  installation guide!!
- Brad Larson and his GPUImage library, for the Lanczos GLSL shader
  implementation for the FSAA.
- All the feedback and support of the folks in the MTBS3D forums!

What is Minecrift?
------------------

The cheesy name apart, Minecrift attempts to update Minecraft to support the
Oculus Rift. Initially this means allowing head-tracking input and using the
correct stereo rendering parameters for the Rift. We also are in the progress
of supporting different control schemes and positional head tracking. Minecraft
for various control schemes. If and when Minecraft officially supports the
Rift, Minecrift development might cease, but probably not.

IMPORTANT NOTE
--------------

This version is extremely work-in-progress. When it is the least bit stable, an
announcement will be made on all the relevant channels.

BULIDING
--------

Uses gradles.

gradle setupDevWorkspace will create a development worksapce
gradle eclipse will create an eclipse project in this directory

gradle build will build all dependencies and the mod.

package com.minecraft_vr.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class VRClassTransformer implements IClassTransformer {

	//We registered with SortingIndex(1001), so we can use srgnames everywhere
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if( name.equals( "net.minecraft.client.gui.GuiMainMenu" ) )
			return removeMethod( bytes, "drawPanorama", "func_73970_b", "(IIF)V" );
		else if( name.equals( "net.minecraft.client.gui.GuiIngame"))
			return removeMethod( bytes, "renderVignette", "func_73829_a", "(FII)V" );
		else if( name.equals( "net.minecraft.client.gui.GuiScreen"))
			return removeMethod( bytes, "drawWorldBackground", "func_146270_b", "(I)V" );
		
		return bytes;
	}

	private byte[] removeMethod( byte[] bytes, String mcpName, String srgName, String methodSignature )
	{
		//set up ASM class manipulation stuff. Consult the ASM docs for details
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		for( MethodNode m : classNode.methods )
		{
			if ( ( (m.name.equals(mcpName) ||
					m.name.equals(srgName) ) && m.desc.equals( methodSignature )))
			{
				//Remove all instructions: just return after doing nothing
				m.instructions.clear();
				m.instructions.add( new InsnNode(Opcodes.RETURN));
			}
		}

		//ASM specific for cleaning up and returning the final bytes for JVM processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
		
	}

}

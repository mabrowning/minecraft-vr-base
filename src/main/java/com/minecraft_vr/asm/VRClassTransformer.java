package com.minecraft_vr.asm;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class VRClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if( name.equals( "net.minecraft.client.gui.GuiMainMenu" ) )
			return transformMainMenu( bytes, false );
		else if( name.equals( "btj" ) )
			return transformMainMenu( bytes, true );
		else if( name.equals( "net.minecraft.client.gui.GuiIngame"))
			return transformGuiIngame( bytes, false );
		else if( name.equals( "bah"))
			return transformGuiIngame( bytes, true );
		
		return bytes;
	}
	
	private byte[] transformMainMenu( byte[] bytes, boolean obf )
	{
		return bytes;
	}

	private byte[] transformGuiIngame( byte[] bytes, boolean obf )
	{
		String targetMethodName = obf? "a":"renderVignette";

		//set up ASM class manipulation stuff. Consult the ASM docs for details
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		for( MethodNode m : classNode.methods )
		{
			if ((m.name.equals(targetMethodName) && m.desc.equals("(FII)V")))
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

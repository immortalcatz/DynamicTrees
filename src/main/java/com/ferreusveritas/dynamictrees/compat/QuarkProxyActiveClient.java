package com.ferreusveritas.dynamictrees.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConstants;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;

import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.client.feature.GreenerGrass;

/**
 * 
 * Quark does not have facilities for registering blocks with the {@link GreenerGrass} feature.
 * This proxy allows this registration to occur.
 * 
 * @author ferreusveritas
 *
 */
public class QuarkProxyActiveClient extends QuarkProxyBase {
	
	private GreenerGrass greenerGrass;
	private Method registrationMethod;
	private boolean affectFolliage;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void init() {
		crackQuark();
		
		if(greenerGrass != null && greenerGrass.enabled) {// Does there Quark?
			// Register the RootyDirtBlock(Put meat in Quark)
			registerGreenerColor(ModBlocks.blockRootyDirt);
			registerGreenerColor(ModBlocks.blockRootyDirtSpecies);
			
			// Conditionally register all of the base mod leaves(Fill Quark with more meats)
			if(affectFolliage) {
				HashMap<Integer, BlockDynamicLeaves> map = TreeHelper.getLeavesMapForModId(ModConstants.MODID);
				for(BlockDynamicLeaves leaves : map.values()) {
					registerGreenerColor(leaves);
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void crackQuark() {
		// Get some Quark
		greenerGrass = (GreenerGrass) ModuleLoader.featureInstances.get(GreenerGrass.class);

		if(greenerGrass != null && greenerGrass.enabled) {// Does there Quark?
			// Crack open Quark with Java hammer.
			registrationMethod = ReflectionHelper.findMethod(GreenerGrass.class, "registerGreenerColor", null, new Class[]{ Block[].class });
			affectFolliage = greenerGrass.loadPropBool("Should affect folliage", "", true);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerGreenerColor(Block block) {
		if(registrationMethod != null) {
			try {
				registrationMethod.invoke(greenerGrass, new Object[] { new Block[] {block} } );
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
}

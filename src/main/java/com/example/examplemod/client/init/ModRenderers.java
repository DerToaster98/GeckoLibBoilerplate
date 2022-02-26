package com.example.examplemod.client.init;

import com.example.examplemod.ModEntityTypes;
import com.example.examplemod.client.RenderGeoTestEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ModRenderers {

	public static void init() {
		registerEntityRenderer(ModEntityTypes.TEST_ENTITY.get(), RenderGeoTestEntity::new);
	}
	
	private static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, IRenderFactory<? super T> renderFactory) {
		RenderingRegistry.registerEntityRenderingHandler(entityType, renderFactory);
	}
	
}

package com.example.examplemod.client;

import com.example.examplemod.entity.GeoTestEntity;

import net.minecraft.util.ResourceLocation;

public class GeoTestEntityModel extends ModelGeoBase<GeoTestEntity> {
	
	protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation("examplemod", "animations/testentity.animation.json");

	public GeoTestEntityModel(ResourceLocation model, ResourceLocation textureDefault, String entityName) {
		super(model, textureDefault, entityName);
	}

	@Override
	public ResourceLocation getAnimationFileLocation(GeoTestEntity animatable) {
		return ANIMATION_RESLOC;
	}

}

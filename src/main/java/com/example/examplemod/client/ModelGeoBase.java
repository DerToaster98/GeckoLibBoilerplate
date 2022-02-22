package com.example.examplemod.client;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public abstract class ModelGeoBase<T extends LivingEntity & IAnimatable & IAnimationTickable> extends AnimatedTickingGeoModel<T> {

	protected final ResourceLocation MODEL_RESLOC;
	protected final ResourceLocation TEXTURE_DEFAULT;
	protected final String ENTITY_REGISTRY_PATH_NAME;

	public ModelGeoBase(ResourceLocation model, ResourceLocation textureDefault, final String entityName) {
		super();
		this.MODEL_RESLOC = model;
		this.TEXTURE_DEFAULT = textureDefault;
		this.ENTITY_REGISTRY_PATH_NAME = entityName;
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.TEXTURE_DEFAULT;
	}

	@Override
	public ResourceLocation getModelLocation(T object) {
		return this.MODEL_RESLOC;
	}

}

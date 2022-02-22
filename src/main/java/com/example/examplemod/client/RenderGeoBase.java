package com.example.examplemod.client;

import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public abstract class RenderGeoBase<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {

	protected float widthScale;
	protected float heightScale;

	public final Function<T, ResourceLocation> TEXTURE_GETTER;
	public final Function<T, ResourceLocation> MODEL_ID_GETTER;

	protected RenderGeoBase(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider) {
		this(renderManager, modelProvider, 1F, 1F, 0);
	}

	@SuppressWarnings("resource")
	protected void bindTexture(ResourceLocation textureLocation) {
		Minecraft.getInstance().textureManager.bind(textureLocation);
	}

	protected RenderGeoBase(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider, float widthScale, float heightScale, float shadowSize) {
		super(renderManager, modelProvider);

		this.MODEL_ID_GETTER = modelProvider::getModelLocation;
		this.TEXTURE_GETTER = modelProvider::getTextureLocation;

		this.shadowRadius = shadowSize;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
	}

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private int currentModelRenderCycle = 0;

	// Entrypoint for rendering, calls everything else
	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
		this.currentModelRenderCycle = 0;
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green,
			float blue, float alpha) {
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.currentModelRenderCycle++;
	}

	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	protected float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		this.rtb = renderTypeBuffer;
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		if (this.currentModelRenderCycle == 0 /* Pre-Layers */) {
			float width = this.getWidthScale(animatable);
			float height = this.getHeightScale(animatable);
			stackIn.scale(width, height, width);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.TEXTURE_GETTER.apply(entity);
	}

	private T currentEntityBeingRendered;
	private IRenderTypeBuffer rtb;

	@Override
	public void renderLate(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		this.currentEntityBeingRendered = animatable;
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		boolean customTextureMarker = this.currentModelRenderCycle == 0 && this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered) != null;
		ResourceLocation currentTexture = this.getTextureLocation(this.currentEntityBeingRendered);
		if (customTextureMarker) {
			currentTexture = this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered);
			this.bindTexture(currentTexture);
		}
		if (this.currentModelRenderCycle == 0) {
			// Render armor
			if (bone.getName().startsWith("armor")) {
				final ItemStack armorForBone = this.getArmorForBone(bone.getName(), currentEntityBeingRendered);
				final EquipmentSlotType boneSlot = this.getEquipmentSlotForArmorBone(bone.getName(), currentEntityBeingRendered);
				if (armorForBone != null && armorForBone.getItem() instanceof ArmorItem && boneSlot != null) {
					final ArmorItem armorItem = (ArmorItem) armorForBone.getItem();
					final BipedModel armorModel = armorItem.getArmorModel(currentEntityBeingRendered, armorForBone, boneSlot, null);
					if (armorModel != null) {
						ModelRenderer sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
						if (sourceLimb != null && !sourceLimb.cubes.isEmpty()) {
							// IMPORTANT: The first cube is used to define the armor part!!
							bone.childCubes.stream().findFirst().ifPresent((firstCube) -> {
								final float targetSizeX = firstCube.size.x();
								final float targetSizeY = firstCube.size.y();
								final float targetSizeZ = firstCube.size.z();
								final ModelBox armorCube = sourceLimb.cubes.get(0);
								float scaleX = targetSizeX / Math.abs(armorCube.maxX - armorCube.minX);
								float scaleY = targetSizeY / Math.abs(armorCube.maxY - armorCube.minY);
								float scaleZ = targetSizeZ / Math.abs(armorCube.maxZ - armorCube.minZ);

								// DONE: Copy matrix multiplication stuff from above
								// Save buffer
								// Tessellator.getInstance().draw();

								stack.pushPose();
								// multiplyMatrix(IGeoRenderer.MATRIX_STACK, bone);

								// DONE: COpy getARmorResource from LayerArmorBase to bind the correct texture
								// TODO: Check if armor is colored, if yes => color it and set overlay, also check for enchantment glint thingy
								stack.pushPose();

								stack.scale(scaleX, scaleY, scaleZ);

								ResourceLocation armorResource = this.getArmorResource(currentEntityBeingRendered, armorForBone, boneSlot, null);
								this.bindTexture(armorResource);

								// armorModel.setRotationAngles(targetSizeY, targetSizeZ, currentEntityBeingRendered.tickCount, currentEntityBeingRendered.yHeadRot, currentEntityBeingRendered.xRot, 0, currentEntityBeingRendered);
								// sourceLimb.render(0);
								IVertexBuilder ivb = ItemRenderer.getArmorFoilBuffer(rtb, RenderType.armorCutoutNoCull(armorResource), false, armorForBone.hasFoil());
								sourceLimb.render(stack, ivb, packedLightIn, packedOverlayIn, red, green, blue, alpha);

								stack.popPose();

								// Reset buffer
								stack.popPose();

								// builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
							});
							this.bindTexture(currentTexture);
							bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
						}
					}
				}
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {
					stack.pushPose();

					if (boneItem != null) {
						this.preRenderItem(boneItem, bone.getName(), this.currentEntityBeingRendered);

						Minecraft.getInstance().getItemRenderer().renderStatic(boneItem, this.getCameraTransformForItemAtBone(boneItem, bone.getName()), packedLightIn, packedOverlayIn, stack, this.rtb);

						this.postRenderItem(boneItem, bone.getName(), this.currentEntityBeingRendered);
					}
					if (boneBlock != null) {
						this.preRenderBlock(boneBlock, bone.getName(), this.currentEntityBeingRendered);

						this.renderBlock(stack, this.rtb, packedLightIn, boneBlock);

						this.postRenderBlock(boneBlock, bone.getName(), this.currentEntityBeingRendered);
					}

					stack.popPose();

					bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
				}
			}
		}
		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (customTextureMarker) {
			this.bindTexture(this.getTextureLocation(this.currentEntityBeingRendered));
		}
	}

	/*
	 * Return null, if the entity's texture is used
	 */
	@Nullable
	protected abstract ResourceLocation getTextureForBone(String boneName, T currentEntity);

	private void renderBlock(MatrixStack matrixStack, IRenderTypeBuffer rtb, int packedLightIn, BlockState iBlockState) {
	}

	/*
	 * Return null if there is no item
	 */
	@Nullable
	protected abstract ItemStack getHeldItemForBone(String boneName, T currentEntity);

	protected abstract TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName);

	/*
	 * Return null if there is no held block
	 */
	@Nullable
	protected abstract BlockState getHeldBlockForBone(String boneName, T currentEntity);

	protected abstract void preRenderItem(ItemStack item, String boneName, T currentEntity);

	protected abstract void preRenderBlock(BlockState block, String boneName, T currentEntity);

	protected abstract void postRenderItem(ItemStack item, String boneName, T currentEntity);

	protected abstract void postRenderBlock(BlockState block, String boneName, T currentEntity);

	/*
	 * Return null, if there is no armor on this bone
	 * 
	 */
	@Nullable
	protected ItemStack getArmorForBone(String boneName, T currentEntity) {
		return null;
	}

	@Nullable
	protected EquipmentSlotType getEquipmentSlotForArmorBone(String boneName, T currentEntity) {
		return null;
	}

	@Nullable
	protected ModelRenderer getArmorPartForBone(String name, BipedModel armorModel) {
		return null;
	}

	/**
	 * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
	 *
	 * @param entity Entity wearing the armor
	 * @param stack  ItemStack for the armor
	 * @param slot   Slot ID that the item is in
	 * @param type   Subtype, can be null or "overlay"
	 * @return ResourceLocation pointing at the armor's texture
	 */
	private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.<String, ResourceLocation>newHashMap();

	protected ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EquipmentSlotType slot, String type) {
		ArmorItem item = (ArmorItem) stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == EquipmentSlotType.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));

		s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = (ResourceLocation) ARMOR_TEXTURE_RES_MAP.get(s1);

		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
		}

		return resourcelocation;
	}

}

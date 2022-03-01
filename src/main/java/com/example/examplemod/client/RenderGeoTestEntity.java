package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.GeoTestEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.core.processor.IBone;

public class RenderGeoTestEntity extends RenderGeoBase<GeoTestEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/entity/testentity.png");
	private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(ExampleMod.MODID, "geo/testentity.geo.json");

	public RenderGeoTestEntity(EntityRendererManager renderManager) {
		super(renderManager, new GeoTestEntityModel(MODEL_RESLOC, TEXTURE, "testentity"));
	}

	@Override
	protected ItemStack getHeldItemForBone(String boneName, GeoTestEntity currentEntity) {
		switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return currentEntity.isLeftHanded() ? mainHand : offHand;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return currentEntity.isLeftHanded() ? offHand : mainHand;
		case DefaultBipedBoneIdents.POTION_BONE_IDENT:
			break;
		}
		return null;
	}

	@Override
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		default:
			return TransformType.NONE;
		}
	}

	@Override
	protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName, GeoTestEntity currentEntity, IBone bone) {
		if(item == this.mainHand || item == this.offHand) {
			stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			boolean flag = !currentEntity.isLeftHanded() ? item == this.offHand : item == this.mainHand;
			stack.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
			//stack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
			//stack.translate(-0.5, 0, 0);
			//stack.mulPose(Vector3f.ZP.rotationDegrees(-45F));
			/*stack.translate(0, 0, -1);
			if(item.getItem() instanceof SwordItem) {
				stack.translate(0, 0.15, 0);
			}
			if(item.getItem().isShield(item, currentEntity) ||item.getItem() instanceof ShieldItem) {
				stack.translate(-0.025, 0.125, -0.2);
			}*/
			boolean shieldFlag = item.isShield(currentEntity) || item.getItem() instanceof ShieldItem;
			stack.translate(0, 0, -0.125);
			if(item == this.mainHand) {
				stack.translate(-0.4, 0, 0);
				if(shieldFlag) {
					stack.translate(-0.05, 0, 0);
				}
			} else {
				stack.translate(0.4, 0, 0);
				if(shieldFlag) {
					stack.translate(0.05, 0, -0.5);
					stack.mulPose(Vector3f.YP.rotationDegrees(180));
				} else {
					//TODO
				}
					
				
			}
			stack.mulPose(Vector3f.YP.rotationDegrees(180));
			
			//stack.scale(0.75F, 0.75F, 0.75F);
		}
	}

	@Override
	protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, GeoTestEntity currentEntity, IBone bone) {

	}

	@Override
	protected ItemStack getArmorForBone(String boneName, GeoTestEntity currentEntity) {
		switch (boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
			return boots;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
			return leggings;
		case "armorBipedBody":
		case "armorBipedRightArm":
		case "armorBipedLeftArm":
			return chestplate;
		case "armorBipedHead":
			return helmet;
		default:
			return null;
		}
	}

	@Override
	protected EquipmentSlotType getEquipmentSlotForArmorBone(String boneName, GeoTestEntity currentEntity) {
		switch (boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
			return EquipmentSlotType.FEET;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
			return EquipmentSlotType.LEGS;
		case "armorBipedRightArm":
			return !currentEntity.isLeftHanded() ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
		case "armorBipedLeftArm":
			return currentEntity.isLeftHanded() ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
		case "armorBipedBody":
			return EquipmentSlotType.CHEST;
		case "armorBipedHead":
			return EquipmentSlotType.HEAD;
		default:
			return null;
		}
	}

	@Override
	protected ModelRenderer getArmorPartForBone(String name, BipedModel armorModel) {
		switch (name) {
		case "armorBipedLeftFoot":
		case "armorBipedLeftLeg":
			return armorModel.leftLeg;
		case "armorBipedRightFoot":
		case "armorBipedRightLeg":
			return armorModel.rightLeg;
		case "armorBipedRightArm":
			return armorModel.rightArm;
		case "armorBipedLeftArm":
			return armorModel.leftArm;
		case "armorBipedBody":
			return armorModel.body;
		case "armorBipedHead":
			return armorModel.head;
		default:
			return null;
		}
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, GeoTestEntity currentEntity) {
		return null;
	}

	@Override
	protected void preRenderBlock(BlockState block, String boneName, GeoTestEntity currentEntity) {

	}

	@Override
	protected void postRenderBlock(BlockState block, String boneName, GeoTestEntity currentEntity) {
	}

	@Override
	protected ResourceLocation getTextureForBone(String boneName, GeoTestEntity currentEntity) {
		switch(boneName) {
		default: 
			return null;
		}
	}

}

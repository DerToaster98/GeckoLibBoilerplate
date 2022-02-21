package com.example.examplemod.client;

import com.example.examplemod.entity.GeoTestEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderGeoTestEntity extends RenderGeoBase<GeoTestEntity> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("examplemod", "textures/entity/testentity.png");
	private static final ResourceLocation MODEL_RESLOC = new ResourceLocation("examplemod", "geo/testentity.geo.json");

	protected RenderGeoTestEntity(EntityRendererManager renderManager) {
		super(renderManager, new GeoTestEntityModel(TEXTURE, MODEL_RESLOC, "testentity"));
	}

	@Override
	protected ItemStack getHeldItemForBone(String boneName, GeoTestEntity currentEntity) {
		switch(boneName) {
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
		switch(boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_LEFT_HAND;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		default:
			return TransformType.NONE;
		}
	}

	

	@Override
	protected void preRenderItem(ItemStack item, String boneName, GeoTestEntity currentEntity) {
		
	}

	@Override
	protected void postRenderItem(ItemStack item, String boneName, GeoTestEntity currentEntity) {
		
	}
	
	@Override
	protected ItemStack getArmorForBone(String boneName, GeoTestEntity currentEntity) {
		switch(boneName) {
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
		switch(boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
			return EquipmentSlotType.FEET;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
			return EquipmentSlotType.LEGS;
		case "armorBipedRightArm":
			return !currentEntity.isLeftHanded() ?EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
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
		switch(name) {
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
		return null;
	}

}

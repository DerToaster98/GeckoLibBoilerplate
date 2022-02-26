package com.example.examplemod;

import com.example.examplemod.entity.GeoTestEntity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = ExampleMod.MODID, bus = Bus.MOD)
public class ModEntityTypes {
	
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ExampleMod.MODID);

	public static final RegistryObject<EntityType<GeoTestEntity>> TEST_ENTITY = registerSizedHumanoid(GeoTestEntity::new, "testentity", 1);
	
	protected static <T extends Entity>  RegistryObject<EntityType<T>> registerSizedHumanoid(IFactory<T> factory, final String entityName, int updateInterval) {
		return registerSized(factory, entityName, 0.6F, 1.875F, updateInterval);
	}
	
	protected static <T extends Entity>  RegistryObject<EntityType<T>> registerSized(IFactory<T> factory, final String entityName, float width, float height, int updateInterval) {
		return ENTITY_TYPES.register(entityName, () -> EntityType.Builder
				.<T>of(factory, EntityClassification.MISC)
				.sized(width, height)
				.setTrackingRange(128)
				.clientTrackingRange(64)
				.updateInterval(updateInterval)
				.setShouldReceiveVelocityUpdates(true)
				.build(ExampleMod.prefix(entityName).toString()));
	}
	
	@SubscribeEvent
	public static void initializeAttributes(EntityAttributeCreationEvent event) {
		event.put(TEST_ENTITY.get(), CreatureEntity.createMobAttributes().build());
	}
	
}

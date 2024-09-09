
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.kailandextras.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.mcreator.kailandextras.item.VaritaDelDictadoItem;
import net.mcreator.kailandextras.item.IconItem;
import net.mcreator.kailandextras.item.GalletaItem;
import net.mcreator.kailandextras.KailandextrasMod;

public class KailandextrasModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, KailandextrasMod.MODID);
	public static final RegistryObject<Item> VARITA_DEL_DICTADO = REGISTRY.register("varita_del_dictado", () -> new VaritaDelDictadoItem());
	public static final RegistryObject<Item> ICON = REGISTRY.register("icon", () -> new IconItem());
	public static final RegistryObject<Item> GALLETA = REGISTRY.register("galleta", () -> new GalletaItem());
}

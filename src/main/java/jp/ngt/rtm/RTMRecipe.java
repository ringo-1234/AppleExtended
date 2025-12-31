package jp.ngt.rtm;

import jp.ngt.ngtlib.item.craft.RecipeManager;
import jp.ngt.ngtlib.item.craft.RepairRecipe;
import jp.ngt.ngtlib.item.craft.ShapedRecipes55;
import jp.ngt.rtm.RTMItem.MaterialType;
import jp.ngt.rtm.item.ItemAmmunition.BulletType;
import jp.ngt.rtm.item.ItemGun.GunType;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public final class RTMRecipe
{
	public static void init()
	{
		RecipeSorter.register("rtm:shaped55", ShapedRecipes55.class, Category.SHAPED, "after:minecraft:shapeless");
		RecipeSorter.register("rtm:repair", RepairRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");

		ItemStack ingotSteel = new ItemStack(RTMItem.steel_ingot, 1, 0);
		/*ArrayList<ItemStack> ingotSteelList = OreDictionary.getOres("ingotSteel");
		ingotSteel = ingotSteelList.size() > 0 ? ingotSteelList.get(0) : new ItemStack(RTMItem.steel_ingot, 1, 0);*/

		ItemStack dyeRed     = new ItemStack(Items.DYE, 1, 1);
		ItemStack dyeBlue    = new ItemStack(Items.DYE, 1, 4);
		ItemStack dyeYellow  = new ItemStack(Items.DYE, 1, 11);
		ItemStack shaft      = new ItemStack(RTMItem.material, 1, MaterialType.SHAFT.getId());
		ItemStack wire       = new ItemStack(RTMItem.itemWire, 1, 0);
		ItemStack sheetSteel = new ItemStack(RTMItem.material, 1, MaterialType.STEEL_SHEET.getId());
		ItemStack motor      = new ItemStack(RTMItem.material, 1, MaterialType.MOTOR.getId());
		ItemStack gunpowder  = new ItemStack(RTMItem.material, 1, MaterialType.POWDER.getId());

		//WorkBench
		RecipeManager.addRecipe(new ItemStack(RTMBlock.trainWorkBench, 1, 0),new Object[]{
			"III", "ICI", "III",
			Character.valueOf('S'), ingotSteel,
			Character.valueOf('C'), Blocks.CRAFTING_TABLE}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rtm_work_bench"));
		//WorkBench
		RecipeManager.addRecipe(new ItemStack(RTMBlock.trainWorkBench, 1, 1),new Object[]{
			"ISI", "SCS", "ISI",
			Character.valueOf('I'), ingotSteel,
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('C'), Blocks.CRAFTING_TABLE}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rail_work_bench"));
		//耐火レンガ
		RecipeManager.addRecipe(new ItemStack(RTMBlock.fireBrick, 6, 0),new Object[]{
			"NDN", "DCD", "NDN",
			Character.valueOf('N'), Items.NETHERBRICK,
			Character.valueOf('D'), Blocks.DIRT,
			Character.valueOf('C'), Items.CLAY_BALL}).setRegistryName(new ResourceLocation(RTMCore.MODID, "fire_brick"));
		//熱風炉用レンガ
		RecipeManager.addRecipe(new ItemStack(RTMBlock.hotStoveBrick, 6, 0),new Object[]{
			"NDN", "DCD", "NDN",
			Character.valueOf('N'), Items.NETHERBRICK,
			Character.valueOf('D'), Blocks.SOUL_SAND,
			Character.valueOf('C'), Items.CLAY_BALL}).setRegistryName(new ResourceLocation(RTMCore.MODID, "hot_stove_brick"));
		//Slot
		RecipeManager.addRecipe(new ItemStack(RTMBlock.slot, 2, 0),new Object[]{
			"SSS", "SDS", "SSS",
			Character.valueOf('S'), Blocks.COBBLESTONE,
			Character.valueOf('D'), Blocks.DISPENSER,}).setRegistryName(new ResourceLocation(RTMCore.MODID, "slot"));
		//鋼材
		RecipeManager.addRecipe(new ItemStack(RTMBlock.steelMaterial, 2, 0),new Object[]{
			"II", "II",
			Character.valueOf('I'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "steel_material"));

		//鉄骨
		RecipeManager.addRecipe(new ItemStack(RTMBlock.framework, 6, 0),new Object[]{
			" I ", "III", " I ",
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "framework"));
		//足場
		RecipeManager.addRecipe(new ItemStack(RTMBlock.scaffold, 6, 0),new Object[]{
			"S S", "S S", "III",
			Character.valueOf('S'), shaft,
			Character.valueOf('I'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "scaffold"));
		//階段
		RecipeManager.addRecipe(new ItemStack(RTMBlock.scaffoldStairs, 6, 0),new Object[]{
			"  I", " I ", "I  ",
			Character.valueOf('I'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "scaffold_stairs"));

		//鉄柱
		RecipeManager.addRecipe(new ItemStack(RTMBlock.ironPillar, 25, 0),new Object[]{
			"IIIII", "II II", "I I I", "II II", "IIIII",
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "iron_pillor"));

		//マーカー赤
		RecipeManager.addRecipe(new ItemStack(RTMBlock.marker, 16, 0),new Object[]{
			"GGGGG", "G D G", "G D G", "G D G", "GGGGG",
			Character.valueOf('D'), dyeRed,
			Character.valueOf('G'), Blocks.GLASS}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rail_marker"));
		//斜め
		RecipeManager.addRecipe(new ItemStack(RTMBlock.marker, 16, 4),new Object[]{
			"GGGGG", "G  DG", "G D G", "GD  G", "GGGGG",
			Character.valueOf('D'), dyeRed,
			Character.valueOf('G'), Blocks.GLASS}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rail_marker2"));
		//マーカー青
		RecipeManager.addRecipe(new ItemStack(RTMBlock.markerSwitch, 16, 0),new Object[]{
			"GGGGG", "G D G", "G D G", "G D G", "GGGGG",
			Character.valueOf('D'), dyeBlue,
			Character.valueOf('G'), Blocks.GLASS}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rail_marker_switch"));
		//斜め
		RecipeManager.addRecipe(new ItemStack(RTMBlock.markerSwitch, 16, 4),new Object[]{
			"GGGGG", "G  DG", "G D G", "GD  G", "GGGGG",
			Character.valueOf('D'), dyeBlue,
			Character.valueOf('G'), Blocks.GLASS}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rail_marker2_switch"));

		//信号変換器(RS->S)
		RecipeManager.addRecipe(new ItemStack(RTMBlock.signalConverter, 1, 0),new Object[]{
			"SSSSS", "SWWWS", "SRIRS", "SWWWS", "SSSSS",
			Character.valueOf('R'), Items.REDSTONE,
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('W'), wire,
			Character.valueOf('I'), shaft}).setRegistryName(new ResourceLocation(RTMCore.MODID, "signal_converter_s"));
		//(S->RS)
		RecipeManager.addRecipe(new ItemStack(RTMBlock.signalConverter, 1, 1),new Object[]{
			"SSSSS", "SWWWS", "SRIRS", "SWWWS", "SSSSS",
			Character.valueOf('R'), Blocks.REDSTONE_TORCH,
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('W'), wire,
			Character.valueOf('I'), shaft}).setRegistryName(new ResourceLocation(RTMCore.MODID, "signal_converter_rs"));
		//++
		RecipeManager.addRecipe(new ItemStack(RTMBlock.signalConverter, 1, 2),new Object[]{
			"SSSSS", "SWIWS", "SWIWS", "SWIWS", "SSSSS",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('W'), wire,
			Character.valueOf('I'), shaft}).setRegistryName(new ResourceLocation(RTMCore.MODID, "signal_converter_increment"));
		//--
		RecipeManager.addRecipe(new ItemStack(RTMBlock.signalConverter, 1, 3),new Object[]{
			"SSSSS", "SWWWS", "SIIIS", "SWWWS", "SSSSS",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('W'), wire,
			Character.valueOf('I'), shaft}).setRegistryName(new ResourceLocation(RTMCore.MODID, "signal_converter_decrement"));


		//台車
		RecipeManager.addRecipe(new ItemStack(RTMItem.bogie, 1, 0), new Object[]{
			" W W ","PPPPP", " S S ", "PPPPP"," W W ",
			Character.valueOf('W'), new ItemStack(RTMItem.material, 1, MaterialType.WHEEL.getId()),
			Character.valueOf('P'), sheetSteel,
			Character.valueOf('S'), shaft}).setRegistryName(new ResourceLocation(RTMCore.MODID, "bogie"));
		//蛍光灯(ガラス)
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 6, 0),new Object[]{
			"GGG", "ILI", "GGG",
			Character.valueOf('I'), Items.IRON_INGOT,
			Character.valueOf('L'), Items.GLOWSTONE_DUST,
			Character.valueOf('G'), Blocks.GLASS_PANE}).setRegistryName(new ResourceLocation(RTMCore.MODID, "fruorescent"));
		//碍子
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 2, IstlObjType.INSULATOR.id),new Object[]{
			"   ", " R ", " I ",
			Character.valueOf('R'), Blocks.SANDSTONE,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "insulator"));
		//入力コネクタ
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 2, IstlObjType.CONNECTOR_IN.id),new Object[]{
			"   ", " RD", " I ",
			Character.valueOf('R'), Blocks.SANDSTONE,
			Character.valueOf('D'), new ItemStack(Items.DYE, 1, 4),
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "connector_in"));
		//出力コネクタ
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 2, IstlObjType.CONNECTOR_OUT.id),new Object[]{
			"   ", " RD", " I ",
			Character.valueOf('R'), Blocks.SANDSTONE,
			Character.valueOf('D'), dyeRed,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "conector_out"));

		//架線柱
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 6, IstlObjType.LINEPOLE.id),new Object[]{
			"ISI", "ISI", " S ",
			Character.valueOf('S'), Blocks.STONE,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "wire_pillor"));
		//踏切
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 1, 5),new Object[]{
			" YIY ", " RIR ", " BIB ", "  I  ", "  I  ",
			Character.valueOf('R'), Items.REDSTONE,
			Character.valueOf('Y'), dyeYellow,
			Character.valueOf('B'), new ItemStack(Items.DYE, 1, 0),
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "crossing_gate"));
		//ATC
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 3, IstlObjType.ATC.id),new Object[]{
			"   ", "PPP", "IRI",
			Character.valueOf('R'), Blocks.REDSTONE_TORCH,
			Character.valueOf('P'), Blocks.STONE_PRESSURE_PLATE,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "atc"));
		//検知器
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 3, IstlObjType.TRAIN_DETECTOR.id),new Object[]{
			"   ", "PPP", "IRI",
			Character.valueOf('R'), Items.REDSTONE,
			Character.valueOf('P'), Blocks.STONE_PRESSURE_PLATE,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "train_detector"));
		//Turnstile
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 2, 12),new Object[]{
			"SFS", "SRS", "SRS",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('F'), ingotSteel,
			Character.valueOf('R'), Items.REDSTONE}).setRegistryName(new ResourceLocation(RTMCore.MODID, "turnstile"));
		//BumpingPost
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 1, 13),new Object[]{
			"ISI", "I I", "I I",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), shaft}).setRegistryName(new ResourceLocation(RTMCore.MODID, "bumping_post"));
		//Point
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 1, IstlObjType.POINT.id),new Object[]{
			"     ", "  M  ", "IISI ", "  S  ", "IISI ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), shaft,
			Character.valueOf('M'), Blocks.LEVER}).setRegistryName(new ResourceLocation(RTMCore.MODID, "point"));

		//信号
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 2, IstlObjType.SIGNAL.id),new Object[]{
			"IGI", "IYI", "IRI", "IYI", "IGI",
			Character.valueOf('R'), dyeRed,
			Character.valueOf('Y'), dyeYellow,
			Character.valueOf('G'), new ItemStack(Items.DYE, 1, 2),
			Character.valueOf('I'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "signal"));
		//シャフト
		RecipeManager.addRecipe(new ItemStack(RTMItem.material, 3, 0),new Object[]{
			"I  ", "I  ", "I  ",
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "shaft"));
		//ホイール
		RecipeManager.addRecipe(new ItemStack(RTMItem.material, 4, 1), new Object[]{
			" III ", "IIIII", "II II", "IIIII", " III ",
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "sheel"));
		//ディーゼルエンジン
		RecipeManager.addRecipe(new ItemStack(RTMItem.material, 2, 2), new Object[]{
			"SSSSS", "SPPRS","SFFAA","SPPRS", "SSSSS",
			Character.valueOf('R'), Items.REDSTONE,
			Character.valueOf('P'), Blocks.PISTON,
			Character.valueOf('A'), shaft,
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('F'), Blocks.FURNACE}).setRegistryName(new ResourceLocation(RTMCore.MODID, "engine"));
		//モーター
		RecipeManager.addRecipe(new ItemStack(RTMItem.material, 2, 3), new Object[]{
			"SSSSS", "SWWRS","SIIAA","SWWRS", "SSSSS",
			Character.valueOf('I'), ingotSteel,
			Character.valueOf('W'), wire,
			Character.valueOf('R'), Items.REDSTONE,
			Character.valueOf('A'), shaft,
			Character.valueOf('S'), sheetSteel,}).setRegistryName(new ResourceLocation(RTMCore.MODID, "motor"));
		//電線
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemWire, 5, 0),new Object[]{
			"     ", "RRRRR", "IIIII", "RRRRR", "     ",
			Character.valueOf('R'), Items.REDSTONE,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "wire"));
		//レール
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemLargeRail, 1, 5), new Object[]{
			"WIWIW", " I I ", "WIWIW", " I I ", "WIWIW",
			Character.valueOf('I'), ingotSteel,
			Character.valueOf('W'), Blocks.PLANKS}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rail"));
		//鋼板
		RecipeManager.addRecipe(new ItemStack(RTMItem.material, 5, 8),new Object[]{
			"     ", "     ", "     ", "     ", "IIIII",
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "steel_slab"));
		//バール
		RecipeManager.addRecipe(new ItemStack(RTMItem.crowbar, 1),new Object[]{
			" II  ", "  I  ", "  I  ", "  I  ", "  I  ",
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "crowbar"));
		//レンチ
		RecipeManager.addRecipe(new ItemStack(RTMItem.wrench, 1),new Object[]{
			" I I ", " III ", "  I  ", "  I  ", "  I  ",
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "wrench"));
		//気動車
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemtrain, 1, 0), new Object[]{
			"FGFGF","BE EB","FGFGF",
			Character.valueOf('F'), Blocks.IRON_BLOCK,
			Character.valueOf('G'), Blocks.GLASS,
			Character.valueOf('B'), new ItemStack(RTMItem.bogie, 1, 0),
			Character.valueOf('E'), new ItemStack(RTMItem.material, 1, 2)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "diesel_car"));
		//電車
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemtrain, 1, 1), new Object[]{
			"FGFGF","BE EB","FGFGF",
			Character.valueOf('F'), Blocks.IRON_BLOCK,
			Character.valueOf('G'), Blocks.GLASS,
			Character.valueOf('B'), new ItemStack(RTMItem.bogie, 1, 0),
			Character.valueOf('E'), motor}).setRegistryName(new ResourceLocation(RTMCore.MODID, "electric_car"));
		//貨車
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemtrain, 1, 2), new Object[]{
			"FGFGF","BE EB","FGFGF",
			Character.valueOf('F'), Blocks.IRON_BLOCK,
			Character.valueOf('G'), Blocks.GLASS,
			Character.valueOf('B'), new ItemStack(RTMItem.bogie, 1, 0),
			Character.valueOf('E'), shaft}).setRegistryName(new ResourceLocation(RTMCore.MODID, "freight_car"));
		//Motorman
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemMotorman, 1, 0),new Object[]{
			"  W  ", "IIIII", "  I  ", " I I ", " I I ",
			Character.valueOf('W'), new ItemStack(Items.SKULL, 1, 1),
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "motorman"));
		//コンテナ
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemCargo, 1, 0),new Object[]{
			"SSSSS", "S   S", "S   S", "S   S", "IIIII",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "container"));
		//砲
		RecipeManager.addRecipe(new ItemStack(RTMItem.itemCargo, 1, 1),new Object[]{
			"SSSSS", "    S", "SSSSS", " III ", " III ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "artillery"));
		//鉄道標識
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 2, IstlObjType.RAILLOAD_SIGN.id),new Object[]{
			"   ", " S ", " I ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rail_road_sign"));
		//Ticket
		RecipeManager.addRecipe(new ItemStack(RTMItem.ticket, 9, 1),new Object[]{
			"   ", "OBO", "PPP",
			Character.valueOf('O'), new ItemStack(Items.DYE, 1, 14),
			Character.valueOf('B'), new ItemStack(Items.DYE, 1, 0),
			Character.valueOf('P'), Items.PAPER}).setRegistryName(new ResourceLocation(RTMCore.MODID, "ticket"));
		//金鋸
		RecipeManager.addRecipe(new ItemStack(RTMItem.iron_hacksaw),new Object[]{
			"   ", "IIS", "II ",
			Character.valueOf('S'), Items.STICK,
			Character.valueOf('I'), Items.IRON_INGOT}).setRegistryName(new ResourceLocation(RTMCore.MODID, "iron_hacksaw"));
		//パドル
		RecipeManager.addRecipe(new ItemStack(RTMItem.paddle),new Object[]{
			" II", " II", "I  ",
			Character.valueOf('I'), Items.IRON_INGOT}).setRegistryName(new ResourceLocation(RTMCore.MODID, "paddle"));
		//ふいご
		RecipeManager.addRecipe(new ItemStack(RTMItem.bellows),new Object[]{
			"SL ", " LG", "SL ",
			Character.valueOf('S'), Items.STICK,
			Character.valueOf('L'), Items.LEATHER,
			Character.valueOf('G'), Items.GOLD_INGOT}).setRegistryName(new ResourceLocation(RTMCore.MODID, "bellows"));
		//パイプ
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 4, IstlObjType.PIPE.id),new Object[]{
			"SSSSS", "S   S", "S   S", "S   S", "SSSSS",
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "pipe"));
		//看板
		RecipeManager.addRecipe(new ItemStack(RTMItem.installedObject, 2, 17),new Object[]{
			"     ", "SSSSS", "SFFFS", "SFFFS", "SSSSS",
			Character.valueOf('F'), new ItemStack(RTMItem.installedObject, 1, 0),
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "signboard"));
		//鏡
		RecipeManager.addRecipe(new ItemStack(RTMItem.mirror, 9, 0),new Object[]{
			"GGGGG", "GSSSG", "GSSSG", "GSSSG", "GGGGG",
			Character.valueOf('G'), new ItemStack(Blocks.GLASS_PANE, 1, 0),
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "mirror"));
		for(int i = 1; i < 16; i += 2)
		{
			int meta = 20 + i;
			String s0 = i == 1 ? " M   " : (i == 3 ? " MM  " : " MMM ");
			String s1 = i <= 5 ? "  G  " : (i <= 13 ? "  GM " : " MGM ");
			String s2 = i <= 7 ? "     " : (i == 9 ? "   M " : (i == 11 ? "  MM " : " MMM "));
			RecipeManager.addRecipe(new ItemStack(RTMItem.mirror, 1, meta),new Object[]{
				"     ", s0, s1, s2, "     ",
				Character.valueOf('M'), new ItemStack(RTMItem.mirror, 1, 0),
				Character.valueOf('G'), new ItemStack(Blocks.GLASS, 1, 0)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "mirror_" + i));
		}
		//移動装置
		RecipeManager.addRecipe(new ItemStack(RTMBlock.movingMachine, 1, 0),new Object[]{
			"SSS", "SMS", "SSS",
			Character.valueOf('S'), RTMBlock.steelMaterial,
			Character.valueOf('M'), motor}).setRegistryName(new ResourceLocation(RTMCore.MODID, "moving_machine"));

		//弾
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 3, 1),new Object[]{
			"  S  ", " STS ", "SSTSS", "SSTSS", "SSSSS",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('T'), Blocks.TNT}).setRegistryName(new ResourceLocation(RTMCore.MODID, "bullet_1"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 64, 5),new Object[]{
			" S   ", "SSS  ", "SSS  ", "     ", "     ",
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "bullet_2"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 64, 13),new Object[]{
			" S   ", "SSS  ", "SSS  ", "SSS  ", "     ",
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "bullet_3"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 64, 17),new Object[]{
			" S   ", "SSS  ", "SSS  ", "SSS  ", "SSS  ",
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "bullet_4"));

		//薬莢
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 5, 2),new Object[]{
			"S   S", "S   S", "S   S", "S   S", "SSISS",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "case_1"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 64, 6),new Object[]{
			"S S  ", "S S  ", "SSS  ", "     ", "     ",
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "case_2"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 64, 14),new Object[]{
			"S S  ", "S S  ", "S S  ", "SSS  ", "     ",
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "case_3"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 64, 18),new Object[]{
			"S S  ", "S S  ", "S S  ", "S S  ", "SSS  ",
			Character.valueOf('S'), sheetSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "case_4"));

		//弾薬
		for(int i = 0; i < BulletType.values().length; ++i)
		{
			if(i == 2){continue;}
			int i0 = i * 4;
			if(i == 0)
			{
				RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 1, i0),new Object[]{
					" B   ", " T   ", " C   ", "     ", "     ",
					Character.valueOf('B'), new ItemStack(RTMItem.bullet, 1, i0 + 1),
					Character.valueOf('C'), new ItemStack(RTMItem.bullet, 1, i0 + 2),
					Character.valueOf('T'), Blocks.TNT}).setRegistryName(new ResourceLocation(RTMCore.MODID, "ammo_1"));
			}
			else if(i == 3)
			{
				RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 1, i0),new Object[]{
					" B   ", " PP  ", " C   ", "     ", "     ",
					Character.valueOf('B'), new ItemStack(RTMItem.bullet, 1, i0 + 1),
					Character.valueOf('C'), new ItemStack(RTMItem.bullet, 1, i0 + 2),
					Character.valueOf('P'), gunpowder}).setRegistryName(new ResourceLocation(RTMCore.MODID, "ammo_2"));
			}
			else if(i == 4)
			{
				RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 1, i0),new Object[]{
					" B   ", "PPP  ", " C   ", "     ", "     ",
					Character.valueOf('B'), new ItemStack(RTMItem.bullet, 1, i0 + 1),
					Character.valueOf('C'), new ItemStack(RTMItem.bullet, 1, i0 + 2),
					Character.valueOf('P'), gunpowder}).setRegistryName(new ResourceLocation(RTMCore.MODID, "ammo_3"));
			}
			else if(i == 5)
			{
				;
			}
			else
			{
				RecipeManager.addRecipe(new ItemStack(RTMItem.bullet, 1, i0),new Object[]{
					" B   ", " P   ", " C   ", "     ", "     ",
					Character.valueOf('B'), new ItemStack(RTMItem.bullet, 1, i0 + 1),
					Character.valueOf('C'), new ItemStack(RTMItem.bullet, 1, i0 + 2),
					Character.valueOf('P'), gunpowder}).setRegistryName(new ResourceLocation(RTMCore.MODID, "ammo_4"));
			}
		}

		//弾倉
		RecipeManager.addRecipe(new ItemStack(RTMItem.magazine_handgun, 3, GunType.handgun.maxSize),new Object[]{
			"S S  ", "S S  ", "SIS  ", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "magazine_handgun"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.magazine_rifle, 3, GunType.rifle.maxSize),new Object[]{
			"  S  ", "  S  ", "  S  ", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "magazine_rifle"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.magazine_alr, 3, GunType.autoloading_rifle.maxSize),new Object[]{
			" S  S", " S  S", " S  S", " SIIS", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "magazine_autoloading_rifle"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.magazine_sr, 3, GunType.sniper_rifle.maxSize),new Object[]{
			" S  S", " S  S", " SIIS", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "magazine_sniper_rifle"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.magazine_smg, 3, GunType.smg.maxSize),new Object[]{
			" S S ", " S S ", " S S ", " SIS ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "magazine_smg"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.magazine_amr, 3, GunType.amr.maxSize),new Object[]{
			"S   S", "S   S", "SIIIS", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel}).setRegistryName(new ResourceLocation(RTMCore.MODID, "magazine_amr"));

		//銃
		RecipeManager.addRecipe(new ItemStack(RTMItem.handgun, 1, 0),new Object[]{
			"SSS  ", "  M  ", "     ", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('M'), new ItemStack(RTMItem.magazine_handgun, 1, 0)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "handgun"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.rifle, 1, 0),new Object[]{
			"SSSM ", "   WW", "     ", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('W'), Blocks.PLANKS,
			Character.valueOf('M'), new ItemStack(RTMItem.magazine_rifle, 1, 0)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "rifle"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.autoloading_rifle, 1, 0),new Object[]{
			"SSSS ", " M II", "     ", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel,
			Character.valueOf('M'), new ItemStack(RTMItem.magazine_alr, 1, 0)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "autoloading_rifle"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.sniper_rifle, 1, 0),new Object[]{
			" SS  ", "SSSS ", " M II", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel,
			Character.valueOf('M'), new ItemStack(RTMItem.magazine_sr, 1, 0)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "sniper_rifle"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.smg, 1, 0),new Object[]{
			"SSSS ", " M II", "     ", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel,
			Character.valueOf('M'), new ItemStack(RTMItem.magazine_smg, 1, 0)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "smg"));
		RecipeManager.addRecipe(new ItemStack(RTMItem.amr, 1, 0),new Object[]{
			" SS  ", "SSSS ", " M II", "     ", "     ",
			Character.valueOf('S'), sheetSteel,
			Character.valueOf('I'), ingotSteel,
			Character.valueOf('M'), new ItemStack(RTMItem.magazine_amr, 1, 0)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "amr"));

		RecipeManager.INSTANCE.addRecipeToManager(new RepairRecipe(RTMItem.magazine_handgun, new ItemStack(RTMItem.bullet, 1, 4)));
		RecipeManager.INSTANCE.addRecipeToManager(new RepairRecipe(RTMItem.magazine_rifle,   new ItemStack(RTMItem.bullet, 1, 12)));
		RecipeManager.INSTANCE.addRecipeToManager(new RepairRecipe(RTMItem.magazine_alr,     new ItemStack(RTMItem.bullet, 1, 12)));
		RecipeManager.INSTANCE.addRecipeToManager(new RepairRecipe(RTMItem.magazine_sr,      new ItemStack(RTMItem.bullet, 1, 12)));
		RecipeManager.INSTANCE.addRecipeToManager(new RepairRecipe(RTMItem.magazine_smg,     new ItemStack(RTMItem.bullet, 1, 4)));
		RecipeManager.INSTANCE.addRecipeToManager(new RepairRecipe(RTMItem.magazine_amr,     new ItemStack(RTMItem.bullet, 1, 16)));

		//火薬(shapelessで登録したい)
		RecipeManager.addRecipe(new ItemStack(RTMItem.material, 64, 4),new Object[]{
				"RG   ", "GG   ", "     ", "     ", "     ",
				Character.valueOf('R'), new ItemStack(Items.REDSTONE),
				Character.valueOf('G'), new ItemStack(Items.GUNPOWDER)}).setRegistryName(new ResourceLocation(RTMCore.MODID, "gun_powder"));

		GameRegistry.addSmelting(new ItemStack(Items.COAL, 1, 0), new ItemStack(RTMItem.coke), 0.25F);
	}
}
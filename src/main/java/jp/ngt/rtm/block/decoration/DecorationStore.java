/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.rtm.block.decoration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTJson;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.network.PacketNotice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class DecorationStore
{
	public static final DecorationStore INSTANCE = new DecorationStore();

	public static final String PACKET_PREFIX = "decoration:";
	public static final String FILE_SUFFIX = ".json";
	public static final String SAVE_DIR = "ngt/rtm/decoration";

	/**全Blockテクスチャ+RTM追加テクスチャ*/
	private final List<ResourceLocation> icons = new ArrayList<>();
	/**RTM追加テクスチャ(TexMap登録用)*/
	private final List<ResourceLocation> unregisteredIcons = new ArrayList<>();

	private final Map<String, DecorationModel> modelMap = new HashMap<>();
	private final Map<String, DecorationModel> serverModelMap = new HashMap<>();

	private boolean needReloadTexture;

	private DecorationStore(){}

	@SideOnly(Side.CLIENT)
	public void registerModelOnClient(DecorationModel model)
	{
		String json = model.toJson();
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice(PacketNotice.Side_SERVER, PACKET_PREFIX + json));
	}

	@SideOnly(Side.CLIENT)
	public void setModel(String json)
	{
		try
		{
			DecorationModel model = DecorationModel.fromJson(json);
			this.modelMap.put(model.name, model);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	//GUIのパケットから
	public void registerModel(String json, World world)
	{
		try
		{
			DecorationModel model = DecorationModel.fromJson(json);
			this.registerModel(model, world);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void registerModel(DecorationModel model, World world)
	{
		this.serverModelMap.put(model.name, model);
		this.saveModel(model, world);
		RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, PACKET_PREFIX + NGTJson.getJsonFromObject(model)));
	}

	public void saveModel(DecorationModel model, World world)
	{
		File saveFolder = this.getSaveFolder(world);
		NGTJson.writeToJson(NGTJson.getJsonFromObject(model), new File(saveFolder, model.name + FILE_SUFFIX));
	}

	public void loadModels(World world)
	{
		File saveFolder = this.getSaveFolder(world);
		String[] files = saveFolder.list();
		for(String fileName : files)
		{
			File file = new File(saveFolder, fileName);
			if(file.getName().endsWith(FILE_SUFFIX))
			{
				try
				{
					DecorationModel model = NGTJson.getObjectFromJson(NGTText.readText(file, false, ""), DecorationModel.class);
					this.registerModel(model, world);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public File getSaveFolder(World world)
	{
		File worldFolder = world.getSaveHandler().getWorldDirectory();
		File saveFolder = new File(worldFolder, SAVE_DIR);
		if(!saveFolder.exists())
		{
			saveFolder.mkdirs();
		}
		return saveFolder;
	}

	public DecorationModel getModel(String name)
	{
		return this.modelMap.containsKey(name) ? this.modelMap.get(name) : DecorationModel.DEFAULT_MODEL;
	}

	public List<DecorationModel> getModels()
	{
		List<DecorationModel> list = new ArrayList<>();
		for(Entry<String, DecorationModel> entry : this.modelMap.entrySet())
		{
			list.add(entry.getValue());
		}

		list.sort(new Comparator<DecorationModel>() {
			@Override
			public int compare(DecorationModel o1, DecorationModel o2)
			{
				return o1.name.compareTo(o2.name);
			}
		});
		return list;
	}

	/////////////////////////////////////////////////////////////////////////

	//ModelLoader.setupModelRegistry()参考
	public void onTextureStitch(TextureMap map)
	{
		this.loadTexture();

		for(ResourceLocation rl : this.unregisteredIcons)
		{
			map.registerSprite(rl);
		}
	}

	private void loadTexture()
	{
		this.icons.clear();
		this.unregisteredIcons.clear();
		this.needReloadTexture = true;

		List<File> fileList = NGTFileLoader.findFile((file)->{return file.getName().startsWith("deco") && file.getName().endsWith(".png");});
		for(File file : fileList)
		{
			String s = file.getAbsolutePath().replace('\\', '/');
			s = s.split("assets/")[1];//assets以上削除
			s = s.replaceAll("/textures/", ":");
			s = s.replaceAll(".png", "");
			this.unregisteredIcons.add(new ResourceLocation(s));
		}
		this.icons.addAll(this.unregisteredIcons);
	}

	//loadTextureでは読み込めないので別処理
	private void loadMCIcons()
	{
		if(this.needReloadTexture)
		{
			TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
			Map<String, TextureAtlasSprite> iconMap = (Map<String, TextureAtlasSprite>)NGTUtil.getField(TextureMap.class, texturemap, "mapUploadedSprites", "field_94252_e");
			for(Entry<String, TextureAtlasSprite> entry : iconMap.entrySet())
			{
				this.icons.add(new ResourceLocation(entry.getKey()));
			}

			this.icons.sort(new Comparator<ResourceLocation>() {
				@Override
				public int compare(ResourceLocation o1, ResourceLocation o2)
				{
					return o1.toString().compareTo(o2.toString());
				}
			});
			this.needReloadTexture = false;
		}
	}

	public List<ResourceLocation> getAllIcon()
	{
		this.loadMCIcons();
		return this.icons;
	}
}
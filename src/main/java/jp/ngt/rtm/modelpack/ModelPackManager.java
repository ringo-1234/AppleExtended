/*
 * Copyright (C) 2025 Applepie
 * This file is part of AppleExtended.
 *
 * AppleExtended includes patches derived from "fixRTM"
 * Copyright (c) 2020 anatawa12 and other authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 */

package jp.ngt.rtm.modelpack;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTJson;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.io.ResourceLocationCustom;
import jp.ngt.ngtlib.renderer.model.EncryptedModel;
import jp.ngt.ngtlib.renderer.model.IModelNGT;
import jp.ngt.ngtlib.renderer.model.ModelFormatException;
import jp.ngt.ngtlib.renderer.model.ModelLoader;
import jp.ngt.ngtlib.renderer.model.NGTOModel;
import jp.ngt.ngtlib.renderer.model.NGTZModel;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tt.TimeTableManager;
import jp.ngt.rtm.entity.util.CollisionHelper;
import jp.ngt.rtm.modelpack.cfg.ModelConfig;
import jp.ngt.rtm.modelpack.cfg.RRSConfig;
import jp.ngt.rtm.modelpack.cfg.ResourceConfig;
import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import jp.ngt.rtm.modelpack.init.ProgressStateHolder;
import jp.ngt.rtm.modelpack.init.ProgressStateHolder.ProgressState;
import jp.ngt.rtm.modelpack.model.RTMClassModels;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import jp.ngt.rtm.network.PacketModelSet;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
public final class ModelPackManager
{
	public static final ModelPackManager INSTANCE = new ModelPackManager();
	private static final Pattern SC_INCLUDE = Pattern.compile("//include <(.+)>");
	private static final String[] ENCODING = {"UTF-8", "SJIS"};
	private final Map<String, ResourceType> typeMap = new HashMap(32);
	private final Map<ResourceType, Map<String, ResourceSet>> allModelSetMap = new HashMap(256);
	private final Map<ResourceType, Map<String, ResourceSet>> smpModelSetMap = new HashMap(256);
	private final Map<String, ResourceSet> dummyMap = new HashMap(32);
	public final List<ResourceSet> unconstructSets = new ArrayList<>(256);
	private final Map<String, IModelNGT> modelCache = new HashMap(128);
	private final Map<String, Map<String, ResourceLocation>> resourceCache = new HashMap(256);
	private final Map<String, String> scriptCache = new HashMap(64);
	public final List<File> fileCache = new ArrayList(1024);
	public boolean modelLoaded;
	private ModelPackManager(){}
	public void load(ModelPackLoadThread par1)
	{
		if(!NGTCore.proxy.isServer())
		{
			this.registerResourcePacks();
		}
		List<File> fileListTemp = NGTFileLoader.findFile((file)->{
			String path = file.getAbsolutePath();
			String name = file.getName();
			return !path.contains("block") && !path.contains("item") && !name.endsWith(".json");
		});
		this.fileCache.addAll(fileListTemp);
		NGTLog.debug("[ModelPack] Start searching json");
		par1.setBarValue(ProgressStateHolder.BAR_MAIN, ProgressState.SEARCHING_MODEL);
		par1.setBarMaxValue(ProgressStateHolder.BAR_SUB, 0, "Searching...");
		List<File> fileList = NGTFileLoader.findFile((file)->{
			String path = file.getAbsolutePath();
			String name = file.getName();
			return !path.contains("block") && !path.contains("item") && name.endsWith(".json");
		});
		NGTLog.debug("[ModelPack] Find %d json", fileList.size());
		NGTLog.debug("[ModelPack] Start registering json");
		par1.setBarValue(ProgressStateHolder.BAR_MAIN, ProgressState.LOADING_MODEL);
		par1.setBarMaxValue(ProgressStateHolder.BAR_SUB, fileList.size(), "");
		int count = 0;
		for(File file : fileList)
		{
			if(!file.getName().contains("_")){continue;}
			String typeName = file.getName().split("_")[0];
			par1.setBarValue(ProgressStateHolder.BAR_SUB, count + 1, file.getName());
			if(!this.typeMap.containsKey(typeName)){continue;}
			ResourceType type = this.typeMap.get(typeName);
			try
			{
				this.registerResourceSet(type, file);
			}
			catch(ModelPackException e)
			{
				throw e;
			}
			catch(Throwable e)
			{
				throw new ModelPackException("Can't load model", file.getAbsolutePath(), e);
			}
			++count;
		}
		NGTLog.debug("[ModelPack] Register %d json", count);
		par1.setBarValue(ProgressStateHolder.BAR_MAIN, ProgressState.SEARCHING_RRS);
		par1.setBarMaxValue(ProgressStateHolder.BAR_SUB, 0, "Loading...");
		List<File> fileList2 = NGTFileLoader.findFile((file)->{
			return file.getName().startsWith("rrs_") && file.getName().endsWith(".png");});
		par1.setBarValue(ProgressStateHolder.BAR_MAIN, ProgressState.LOADING_RRS);
		par1.setBarMaxValue(ProgressStateHolder.BAR_SUB, fileList2.size(), "");
		count = 0;
		for(File file : fileList2)
		{
			++count;
			String name = file.getName();
			par1.setBarValue(ProgressStateHolder.BAR_SUB, count, name);
			RRSConfig cfg = new RRSConfig(name);
			this.registerResourceSet(RTMResource.RRS, cfg, "dummy_str");
		}
		TimeTableManager.INSTANCE.load();
		par1.loadFinished = true;
		this.modelLoaded = true;
	}
	@SideOnly(Side.CLIENT)
	private void registerResourcePacks()
	{
		List<File> modsDirs = NGTFileLoader.getModsDir();
		List<IResourcePack> defaultPacks = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.client.Minecraft.class, net.minecraft.client.Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao");
		boolean added = false;
		for(File modsDir : modsDirs)
		{
			File[] files = modsDir.listFiles();
			if(files == null) continue;
			for(File file : files)
			{
				if(file.isDirectory() && !file.getName().equals("memory_repo") && !file.getName().equals("mods"))
				{
					boolean exists = false;
					for(IResourcePack pack : defaultPacks)
					{
						if(pack.getPackName().equals(file.getName()))
						{
							exists = true;
							break;
						}
					}
					if(!exists)
					{
						defaultPacks.add(new FolderPack(file));
						NGTLog.debug("[ModelPack] Add resource pack : " + file.getName());
						added = true;
					}
				}
			}
		}
		if(added)
		{
			net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> net.minecraft.client.Minecraft.getMinecraft().refreshResources());
		}
	}
	public void registerType(ResourceType type)
	{
		boolean isSubType = (type.hasSubType && type.subType != null);
		if(isSubType)
		{
			ResourceSet dummy = this.getNewModelSet(type, new Class[]{});
			this.dummyMap.put(type.subType, dummy);
			this.unconstructSets.add(dummy);
		}
		else
		{
			this.typeMap.put(type.name, type);
			this.allModelSetMap.put(type, new HashMap<String, ResourceSet>());
			this.smpModelSetMap.put(type, new HashMap<String, ResourceSet>());
			ResourceSet dummy = this.getNewModelSet(type, new Class[]{});
			this.dummyMap.put(type.name, dummy);
		}
	}
	public ResourceType getType(String name)
	{
		return this.typeMap.get(name);
	}
	public String registerResourceSet(ResourceType type, File file) throws IOException
	{
		for(String enc : ENCODING)
		{
			try
			{
				String json = NGTText.readText(file, false, enc);
				ResourceConfig cfg = (ResourceConfig)NGTJson.getObjectFromJson(json, type.cfgClass);
				return this.registerResourceSet(type, cfg, json);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		throw new ModelPackException("Can't load model", file.getAbsolutePath());
	}
	public String registerResourceSet(ResourceType type, ResourceConfig cfg, String origJson)
	{
		cfg.init();
		ResourceSet set = this.getNewModelSet(type, new Class[]{type.cfgClass}, cfg);
		if(set != null)
		{
			NGTLog.debug("Registr resource : %s (%s)", cfg.getName(), type.name);
			this.allModelSetMap.get(type).put(cfg.getName(), set);
			this.unconstructSets.add(set);
			set.md5 = EncryptedModel.getMD5(EncryptedModel.formatJson(origJson));
			return cfg.getName();
		}
		else
		{
			throw new ModelPackException("Failed to create ResourceSet", cfg.getName());
		}
	}
	public void sendModelSetsToClient(EntityPlayerMP player)
	{
		int count = 0;
		for(Entry<ResourceType, Map<String, ResourceSet>> entry : this.allModelSetMap.entrySet())
		{
			for(ResourceSet modelSet : entry.getValue().values())
			{
				ResourceConfig cfg = modelSet.getConfig();
				RTMCore.NETWORK_WRAPPER.sendTo(new PacketModelSet(count, entry.getKey().name, cfg.getName()), player);
				NGTLog.debug("[RTM] Send model to client : " + cfg.getName());
				++count;
			}
		}
	}
	public void addModelSetName(int count, String typeName, String name)
	{
		assert NGTUtil.isSMP() && !NGTUtil.isServer();
		if(count == 0)
		{
			for(Map<String, ResourceSet> map : this.smpModelSetMap.values())
			{
				map.clear();
			}
		}
		ResourceType type = this.typeMap.get(typeName);
		ResourceSet modelSet = this.allModelSetMap.get(type).get(name);
		if(modelSet != null)
		{
			this.smpModelSetMap.get(type).put(name, modelSet);
			NGTLog.debug("[RTM] Add model to SMP map : " + name);
			if(modelSet instanceof ModelSetBase)
			{
				CollisionHelper.INSTANCE.syncCollisionObj(type, (ModelSetBase)modelSet);
			}
		}
	}
	private ResourceSet getNewModelSet(ResourceType type, Class[] parameterTypes, Object... parameters)
	{
		try
		{
			Constructor cons0 = type.setClass.getConstructor(parameterTypes);
			return (ResourceSet)cons0.newInstance(parameters);
		}
		catch(ReflectiveOperationException e)
		{
			String name = "";
			if(parameters.length > 0)
			{
				name = ((ResourceConfig)parameters[0]).getName();
			}
			throw new ModelPackException("On construct ModelSet", name, e);
		}
	}
	public <T extends ResourceSet> T getResourceSet(ResourceType type, String name)
	{
		ResourceType parent = type;
		if(type.parent != null)
		{
			parent = type.parent;
		}
		boolean isSMPClient = NGTUtil.isSMP() && !NGTUtil.isServer();
		Map<ResourceType, Map<String, ResourceSet>> map = isSMPClient ? this.smpModelSetMap : this.allModelSetMap;
		T modelSet = (T)map.get(parent).get(name);
		if(modelSet == null)
		{
			modelSet = (T)this.dummyMap.get(type.hasSubType ? type.subType : type.name);
			if(modelSet == null)
			{
				throw new ModelPackException("Default model is not registered.", name);
			}
		}
		return modelSet;
	}
	public List<ResourceSet> getModelList(ResourceType type)
	{
		if(type.parent != null)
		{
			type = type.parent;
		}
		List<ResourceSet> list = new ArrayList<>();
		Map<ResourceType, Map<String, ResourceSet>> map = NGTUtil.isSMP() ? this.smpModelSetMap : this.allModelSetMap;
		list.addAll(map.get(type).values());
		Collections.sort(list, (o1, o2)->{return o1.getConfig().getName().compareTo(o2.getConfig().getName());});
		return list;
	}
	@SideOnly(Side.CLIENT)
	public IModelNGT loadModel(String modelName, int drawMode, boolean addModelMap, ModelConfig cfg, byte[] md5)
	{
		if(addModelMap && this.modelCache.containsKey(modelName))
		{
			return this.modelCache.get(modelName);
		}
		VecAccuracy accuracy = (cfg.accuracy == null || cfg.accuracy.equals(VecAccuracy.MEDIUM.toString())) ? VecAccuracy.MEDIUM : VecAccuracy.LOW;
		String resource = "models/" + modelName;
		IModelNGT model = null;
		try
        {
			if(FileType.CLASS.match(modelName))
			{
				model = RTMClassModels.getModel(modelName);
			}
			else if(FileType.NGTO.match(modelName))
			{
				model = new NGTOModel(new ResourceLocationCustom("minecraft", resource), cfg.scale);
			}
			else if(FileType.NGTZ.match(modelName))
			{
				model = new NGTZModel(new ResourceLocationCustom("minecraft", resource), cfg.scale);
			}
			else
			{
				model = ModelLoader.loadModel(resource, accuracy, new Object[]{drawMode, md5});
			}
        }
		catch(ModelFormatException e)
		{
			throw new ModelFormatException("Can't load model : " + modelName, e);
		}
		if(model == null)
		{
			throw new ModelPackException("Can't find model file", cfg.getName());
		}
		if(addModelMap)
		{
			this.modelCache.put(modelName, model);
		}
		return model;
	}
	private static final String DEFAULT_DOMAIN = "minecraft";
	public ResourceLocation getResource(String path)
	{
		String domain = DEFAULT_DOMAIN;
		if(path.contains(":"))
		{
			String[] sa = path.split(":");
			domain = sa[0];
			path = sa[1];
		}
		return this.getResource(domain, path);
	}
	public ResourceLocation getResource(String domain, String path)
	{
		Map<String, ResourceLocation> map = this.resourceCache.get(domain);
		if(map == null)
		{
			map = new HashMap<>();
			this.resourceCache.put(domain, map);
		}
		else if(map.containsKey(path))
		{
			return map.get(path);
		}
		ResourceLocation resource = new ResourceLocationCustom(domain, path);
		map.put(path, resource);
		return resource;
	}
	public String getScript(String fileName)
	{
		try
		{
			return this.loadScript(fileName);
		}
		catch(IOException e)
		{
			throw new ModelPackException("Failed to load script", fileName, e);
		}
	}
	private String loadScript(String fileName) throws IOException
	{
		if(this.scriptCache.containsKey(fileName))
		{
			return this.scriptCache.get(fileName);
		}
		String rawScript = NGTText.append(NGTText.readText(this.getResource(fileName)), true);
		while(true)
		{
			Matcher matcher = SC_INCLUDE.matcher(rawScript);
			if(matcher.find())
			{
				String path = matcher.group(1);
				String rep = this.loadScript(path);
				rawScript = matcher.replaceFirst(rep);
			}
			else
			{
				break;
			}
		}
		this.scriptCache.put(fileName, rawScript);
		return rawScript;
	}
	public void clearCache()
	{
		this.modelCache.clear();
		this.resourceCache.clear();
		this.scriptCache.clear();
		this.fileCache.clear();
	}
	private static class FolderPack implements IResourcePack
	{
		private final File packDir;
		public FolderPack(File dir)
		{
			this.packDir = dir;
		}
		@Override
		public InputStream getInputStream(ResourceLocation location) throws IOException
		{
			File file = new File(this.packDir, "assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
			if(file.exists()) return new FileInputStream(file);
			file = new File(this.packDir, location.getResourcePath());
			if(file.exists()) return new FileInputStream(file);
			String fileName = new File(location.getResourcePath()).getName();
			file = this.findFile(this.packDir, fileName);
			if(file != null) return new FileInputStream(file);
			throw new FileNotFoundException(location.toString());
		}
		private File findFile(File dir, String fileName)
		{
			File[] files = dir.listFiles();
			if(files != null)
			{
				for(File f : files)
				{
					if(f.isDirectory())
					{
						File res = findFile(f, fileName);
						if(res != null) return res;
					}
					else if(f.getName().equals(fileName))
					{
						return f;
					}
				}
			}
			return null;
		}
		@Override
		public boolean resourceExists(ResourceLocation location)
		{
			try {
				InputStream is = getInputStream(location);
				if(is != null) { is.close(); return true; }
			} catch(IOException e) {}
			return false;
		}
		@Override
		public Set<String> getResourceDomains() {
			return new HashSet<>(Arrays.asList("minecraft", "rtm", "ngt"));
		}
		@Override
		public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
			return null;
		}
		@Override
		public BufferedImage getPackImage() throws IOException {
			return null;
		}
		@Override
		public String getPackName() {
			return this.packDir.getName();
		}
	}
}

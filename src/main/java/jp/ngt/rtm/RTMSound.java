package jp.ngt.rtm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.RTMResourceManager;
import jp.ngt.rtm.sound.SoundObj;
import net.minecraft.block.SoundType;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;

public final class RTMSound
{
	public static final List<String> ALL_OGG_FILES = new ArrayList<>();

	public static final String EMPTY = "rtm:sounds/none.ogg";
	//public static final String METAL = "rtm:sounds/block/metal.ogg";
	public static final String LEVER = "rtm:sounds/train/lever.ogg";
	public static final String ATS = "rtm:sounds/train/ats.ogg";
	public static final String ATS_BELL = "rtm:sounds/train/ats_bell.ogg";
	public static final String CP_FIN = "rtm:sounds/train/cp_fin.ogg";
	public static final String JOINT = "rtm:sounds/train/joint.ogg";
	public static final String JOINT_REVERB = "rtm:sounds/train/joint_reverb.ogg";
	public static final String GUN = "rtm:sounds/item/gun.ogg";

	public static final SoundObj BLOCK_METAL = new SoundObj("rtm:block.metal");

	//getSound()がnullでないよう注意
	public static final SoundType SOUND_METAL2 = new SoundType(1.0F, 1.0F,
			RTMSound.BLOCK_METAL.getSound(), RTMSound.BLOCK_METAL.getSound(),
			RTMSound.BLOCK_METAL.getSound(), RTMSound.BLOCK_METAL.getSound(), RTMSound.BLOCK_METAL.getSound()){

		private float[] pitches = {0.875F, 0.9375F, 1.0F, 1.0625F, 1.125F};

		@Override
		public float getPitch()
        {
            return this.pitches[NGTMath.RANDOM.nextInt(this.pitches.length)];
        }
	};

	public static void init(){}

	public static void registerSoundDomains()
	{
		NGTLog.debug("[RTMSound] Register sound domains");
		SimpleReloadableResourceManager rm = (SimpleReloadableResourceManager)NGTUtilClient.getMinecraft().getResourceManager();
		Set<String> domains = rm.getResourceDomains();
		List<File> newDomains = new ArrayList<>();
		List<File> oggFiles = NGTFileLoader.findFile((file)->{return file.getName().endsWith(".ogg");});
		ALL_OGG_FILES.clear();
		for(File file : oggFiles)
		{
			File domain = getDomain(file);
			if(domain != null)
			{
				if(!domains.contains(domain.getName()))
				{
					newDomains.add(domain);
					domains.add(domain.getName());
				}

				String path = file.getAbsolutePath();
				String name = path.substring(path.indexOf(domain.getName()) + domain.getName().length() + 1);
				name = name.replace("\\", "/");
				ALL_OGG_FILES.add(domain.getName() + ":" + name);
			}
		}

		Map rmMap = getDomainResourceManagers(rm);
		for(File domain : newDomains)
		{
			RTMResourceManager rrm = new RTMResourceManager(getMetadataSerializer(rm), domain);
			rmMap.put(domain.getName(), rrm);
			NGTLog.debug("[RTMSound] Add new domain : " + domain.getName());
		}
	}

	private static File getDomain(File file)
	{
		if(!file.getAbsolutePath().contains("sounds"))
		{
			return null;//sounds以下以外のoggファイルは除外
		}

		while(!file.getName().equals("sounds"))//ドメインの1つ下の階層まで上る
		{
			file = file.getParentFile();
			if(file == null)
			{
				return null;
			}
		}
		return file.getParentFile();
	}

	private static MetadataSerializer getMetadataSerializer(SimpleReloadableResourceManager par1)
	{
		return (MetadataSerializer)NGTUtil.getField(
				SimpleReloadableResourceManager.class, par1, new String[]{"rmMetadataSerializer", "field_110547_c"});
	}

	private static Map getDomainResourceManagers(SimpleReloadableResourceManager par1)
	{
		return (Map)NGTUtil.getField(
				SimpleReloadableResourceManager.class, par1, new String[]{"domainResourceManagers", "field_110548_a"});
	}
}
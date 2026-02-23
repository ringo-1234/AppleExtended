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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class RTMSound
{
	public static final List<String> ALL_OGG_FILES = new ArrayList<>();

	public static final String EMPTY = "rtm:sounds/none.ogg";
	public static final String LEVER = "rtm:sounds/train/lever.ogg";
	public static final String ATS = "rtm:sounds/train/ats.ogg";
	public static final String ATS_BELL = "rtm:sounds/train/ats_bell.ogg";
	public static final String CP_FIN = "rtm:sounds/train/cp_fin.ogg";
	public static final String JOINT = "rtm:sounds/train/joint.ogg";
	public static final String JOINT_REVERB = "rtm:sounds/train/joint_reverb.ogg";
	public static final String GUN = "rtm:sounds/item/gun.ogg";

	public static final SoundObj BLOCK_METAL = new SoundObj("rtm:block.metal");

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

	@SideOnly(Side.CLIENT)
	public static void registerSoundDomains()
	{
		NGTLog.debug("[RTMSound] Register sound domains");
		SimpleReloadableResourceManager simplereloadableresourcemanager = (SimpleReloadableResourceManager)NGTUtilClient.getMinecraft().getResourceManager();
		Set<String> set = simplereloadableresourcemanager.getResourceDomains();
		List<File> list = new ArrayList<>();
		List<File> oggFiles = NGTFileLoader.findFile((file)->{return file.getName().endsWith(".ogg");});
		ALL_OGG_FILES.clear();
		for(File file1 : oggFiles)
		{
			File file2 = getDomain(file1);
			if(file2 != null)
			{
				if(!set.contains(file2.getName()))
				{
					list.add(file2);
					set.add(file2.getName());
				}

				String s = file1.getAbsolutePath();
				s = s.replace("\\", "/");
				String find = "/assets/" + file2.getName() + "/";
				String s1 = s.substring(s.indexOf(find) + find.length());
				ALL_OGG_FILES.add(file2.getName() + ":" + s1);
			}
		}

		for(File file3 : list) {
			RTMResourceManager rtmresourcemanager = new RTMResourceManager(getMetadataSerializer(simplereloadableresourcemanager), file3);
			simplereloadableresourcemanager.domainResourceManagers.put(file3.getName(), rtmresourcemanager);
			NGTLog.debug("[RTMSound] Add new domain : " + file3.getName());
		}
	}

	private static File getDomain(File file)
	{
		if(!file.getAbsolutePath().contains("sounds"))
		{
			return null;
		}

		while(!file.getName().equals("sounds"))
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
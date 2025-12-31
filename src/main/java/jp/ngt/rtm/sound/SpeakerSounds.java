package jp.ngt.rtm.sound;

import java.io.File;
import java.io.IOException;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTJson;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.network.PacketNotice;

public class SpeakerSounds
{
	private static final SpeakerSounds INSTANCE_CLIENT = new SpeakerSounds(false);
	private static final SpeakerSounds INSTANCE_SERVER = new SpeakerSounds(true);

	private static final String SAVE_FILE = "rtm/speaker_sounds.json";
	public static final int MAX_SOUND_ID = 64;

	private String[] sounds = new String[MAX_SOUND_ID];
	private boolean sideServer;

	private SpeakerSounds(boolean par1)
	{
		this.sideServer = par1;
		if(NGTUtil.isServer())
		{
			this.loadSoundList();
		}
	}

	public static SpeakerSounds getInstance(boolean server)
	{
		return server ? INSTANCE_SERVER : INSTANCE_CLIENT;
	}

	/**Server Only*/
	private void loadSoundList()
	{
		File file = new File(NGTFileLoader.getModsDir().get(0), SAVE_FILE);
		if(file.exists())
		{
			try
			{
				this.sounds = NGTJson.getObjectFromJson(NGTText.readText(file, false, ""), String[].class);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**Serverの音声リストをClientと同期*/
	public void syncSoundList()
	{
		for(int i = 0; i < this.sounds.length; ++i)
		{
			this.setSound(i + 1, this.sounds[i], true);
		}
	}

	/**Server Only*/
	private void saveSoundList()
	{
		File file = new File(NGTFileLoader.getModsDir().get(0), SAVE_FILE);
		if(!file.getParentFile().exists())
		{
			file.getParentFile().mkdirs();
		}
		NGTJson.writeToJson(NGTJson.getJsonFromObject(this.sounds), file);
	}

	public String getSound(int id)
	{
		return this.sounds[id - 1];
	}

	public void setSound(int id, String sound, boolean sync)
	{
		this.sounds[id - 1] = sound;

		String msg = String.format("speaker,%d,%s", id, sound);
		if(this.sideServer)
        {
			this.saveSoundList();
			RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, msg));
        }
		else if(sync)
		{
			RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice(PacketNotice.Side_SERVER, msg));
		}
	}

	public void onGetPacket(String msg, boolean sync)
	{
		String[] sa = msg.split(",");
		int id = Integer.valueOf(sa[1]);
		this.setSound(id, sa[2], sync);
	}
}

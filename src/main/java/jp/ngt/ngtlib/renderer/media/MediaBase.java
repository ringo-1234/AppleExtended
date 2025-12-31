package jp.ngt.ngtlib.renderer.media;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.io.NGTLog;
import net.minecraft.client.renderer.GlStateManager;

public abstract class MediaBase
{
	private static final Map<MediaType, Map<String, MediaBase>> ALL_MEDIA = new HashMap<>();

	public abstract void render(float width, float height, boolean fitAspectRatio);

	public abstract void exit();

	public void bindTexture(int texture)
	{
		GlStateManager.bindTexture(texture);
	}

	public static @Nullable MediaBase getMedia(MediaType type, String key)
	{
		if(!ALL_MEDIA.containsKey(type))
		{
			ALL_MEDIA.put(type, new HashMap<>());
			NGTLog.debug("[MediaBase] Add media map : %s", type.toString());
		}

		Map<String, MediaBase> map = ALL_MEDIA.get(type);

		if(!map.containsKey(key))
		{
			MediaBase media = create(type, key);
			if(media != null)
			{
				map.put(key, media);
				NGTLog.debug("[MediaBase] Add new media : %s", key);
			}
			return media;
		}

		return map.get(key);
	}

	private static @Nullable MediaBase create(MediaType type, String key)
	{
		switch(type)
		{
		case CAMERA:
			return Camera.create(Integer.valueOf(key));
		case CAPTURE:
			return ScreenCapture.create();
		case PICTURE:
			return Picture.create(key);
		case TWEET:
			return new TweetState(key);
		case MAP:
			return MapRenderer.create(key);
		case EBB:
			return new ElectricBulletinBoard(key);
		}
		return null;
	}

	//デバッグ用("/ngt clearMedia")
	public static void clear()
	{
		for(Map<String, MediaBase> map : ALL_MEDIA.values())
		{
			for(MediaBase entry : map.values())
			{
				entry.exit();
			}
		}
		NGTLog.debug("[MediaBase] Clear media : %s", ALL_MEDIA.size());
		ALL_MEDIA.clear();
	}

	public enum MediaType
	{
		PICTURE,
		CAMERA,
		CAPTURE,
		TWEET,
		MAP,
		EBB;
	}
}
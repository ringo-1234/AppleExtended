package jp.ngt.rtm.block.tt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ngt.ngtlib.renderer.media.FontImage;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SignboardText
{
	private static final int IMAGE_SIZE = 48;
	private static final float TO_SEC = 0.001F;
	private static final String SEPARATOR = "\\|";
	/**書式 : tt(col=0,offset=0)*/
	private static final Pattern TT_SETTING = Pattern.compile("tt\\(col=(-?[0-9]+)\\,offset=([0-9]+)\\)");

	private FontImage[] texts;
	private String rawText;
	/**左上始点*/
	public float posU, posV;
	/**文字高さ(World内スケールで)*/
	public float size;
	/**描画エリアの幅(≠文字列長)*/
	public float width;
	/**0:なし, 1:横スクロール, 2:切り替え*/
	public AnimeType animeType;
	/**sec/width*/
	public float animeSpeed;
	//private int alignment;//揃え位置

	private float prevMinU;
	private long prevTime;
	private int index;

	private StationTimeTable timeTable;
	private boolean ttMode;
	private int ttOffset;
	private int ttTrack;

	public SignboardText(boolean isClient, StationTimeTable tt)
	{
		this.posU = this.posV = 0.0F;
		this.size = 0.25F;
		this.width = 1.5F;
		this.animeType = AnimeType.SWITCH;
		this.animeSpeed = 1.0F;
		this.timeTable  = tt;
		this.setText("sample|aaa", "Meiryo UI", 0, 0x000000, isClient);
	}

	public static SignboardText readFromNBT(NBTTagCompound nbt, StationTimeTable tt)
	{
		SignboardText st = new SignboardText(!NGTUtil.isServer(), tt);
		String s1 = nbt.getString("Text");
		String s2 = nbt.getString("Font");
		int i1 = nbt.getInteger("Style");
		int i2 = nbt.getInteger("Color");
		st.posU = nbt.getFloat("PosU");
		st.posV = nbt.getFloat("PosV");
		st.size = nbt.getFloat("Size");
		st.width = nbt.getFloat("Width");
		st.animeType = AnimeType.values()[nbt.getInteger("AnimeType")];
		st.animeSpeed = nbt.getFloat("AnimeSpeed");
		st.setText(s1, s2, i1, i2, !NGTUtil.isServer());
		return st;
	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Text", this.rawText);
		nbt.setString("Font", this.getText().getFont());
		nbt.setInteger("Style", this.getText().getStyle());
		nbt.setInteger("Color", this.getText().getColor());
		nbt.setFloat("PosU", this.posU);
		nbt.setFloat("PosV", this.posV);
		nbt.setFloat("Size", this.size);
		nbt.setFloat("Width", this.width);
		nbt.setInteger("AnimeType", this.animeType.ordinal());
		nbt.setFloat("AnimeSpeed", this.animeSpeed);
		return nbt;
	}

	public FontImage getText()
	{
		if(this.ttMode)
		{
			int idx = this.timeTable.getMatchTrainIndex(this.ttTrack) + this.ttOffset;
			if(idx >= this.texts.length)
			{
				idx = this.texts.length - 1;
			}
			return this.texts[idx];
		}
		else
		{
			return this.texts[this.index];
		}
	}

	public String getRawText()
	{
		return this.rawText;
	}

	public void setText(String pText, String pFont, int pStyle, int pColor, boolean isClient)
	{
		this.ttMode = false;
		this.ttOffset = 0;
		this.ttTrack = -1;

		Matcher matcher = TT_SETTING.matcher(pText);
		if(matcher.matches())
		{
			this.ttMode = true;
			int col = 0;
			try
			{
				col = Integer.valueOf(matcher.group(1));
				this.ttOffset = Integer.valueOf(matcher.group(2));
			}
			catch(NumberFormatException e)
			{
				;
			}

			this.texts = new FontImage[this.timeTable.getSize() + 1];
			for(int i = 0; i < this.texts.length - 1; ++i)
			{
				//全時刻の指定列のテキストデータを予め画像化
				String s;
				if(col == -1)
				{
					s = this.timeTable.timeTable.trainName[i];
				}
				else
				{
					s = this.timeTable.getData(i, col);
				}
				this.texts[i] = FontImage.create(s, pFont, pStyle, pColor, IMAGE_SIZE, isClient);
			}
			//該当列車なし
			this.texts[this.texts.length - 1] = FontImage.create(" ", pFont, pStyle, pColor, IMAGE_SIZE, isClient);
		}
		else
		{
			if(this.animeType == AnimeType.SWITCH)
			{
				String[] sa = pText.split(SEPARATOR);
				this.texts = new FontImage[sa.length];
				for(int i = 0; i < sa.length; ++i)
				{
					this.texts[i] = FontImage.create(sa[i], pFont, pStyle, pColor, IMAGE_SIZE, isClient);
				}
			}
			else
			{
				this.texts = new FontImage[]{FontImage.create(pText, pFont, pStyle, pColor, IMAGE_SIZE, isClient)};
			}
		}
		this.rawText = pText;
		this.index = 0;
	}

	@SideOnly(Side.CLIENT)
	public SignboardText copy()
	{
		SignboardText text = new SignboardText(true, this.timeTable);
		FontImage img = this.getText();
		text.setText(this.rawText, img.getFont(), img.getStyle(), img.getColor(), true);
		return text;
	}

	@SideOnly(Side.CLIENT)
	public void render(float x, float y, float z, float scale)
	{
		float minU = 0.0F;
		float maxU = 1.0F;
		float w2 = this.width;
		long time = System.currentTimeMillis();
		float difSec = (float)(time - this.prevTime) * TO_SEC;
		boolean drawFlag = true;

		if(this.animeType == AnimeType.SCROLL)
		{
			minU = this.prevMinU + (difSec / this.animeSpeed);
			minU %= 1.0F;
			float tw = (IMAGE_SIZE * this.width / this.size) / (float)this.getText().getWidth();
			maxU = minU + tw;
			this.prevTime = time;
			this.prevMinU = minU;
		}
		else if(this.animeType == AnimeType.FLASH)
		{
			if(difSec >= this.animeSpeed)
			{
				drawFlag = false;
				if(difSec >= this.animeSpeed * 2.0F)
				{
					this.prevTime = time;
				}
			}
		}
		else
		{
			if(this.animeType == AnimeType.SWITCH)
			{
				if(difSec >= this.animeSpeed)
				{
					this.index = (this.index + 1) % this.texts.length;
					this.prevTime = time;
				}
			}

			int tw = (int)(IMAGE_SIZE * this.width / this.size);
			maxU = (float)tw / (float)this.getText().getWidth();
			if(maxU > 1.0F)
			{
				maxU = 1.0F;
				w2 = this.size * this.getText().getWidth() / this.getText().getHeight();
			}
		}

		float h = this.size * scale;
		float w = w2 * scale;//h * this.text.getWidth() / this.text.getHeight();

		if(drawFlag)
		{
			this.getText().render(x, y, z, w, h, minU, 0.0F, maxU, 1.0F);
		}
	}

	public enum AnimeType
	{
		NONE,
		SCROLL,
		SWITCH,
		FLASH,
		FLAP;
	}
}
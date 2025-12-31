package jp.ngt.rtm.block.decoration;

import java.io.IOException;

import jp.ngt.ngtlib.io.NGTJson;

public class DecorationModel implements Cloneable
{
	public static final DecorationModel DEFAULT_MODEL = getDefaultModel("default");

	public String name;
	public Element[] elements;

	@Override
	public DecorationModel clone()
	{
		DecorationModel model = new DecorationModel();
		model.elements = new Element[this.elements.length];
		for(int i = 0; i < model.elements.length; ++i)
		{
			model.elements[i] = this.elements[i].clone();
		}
		model.name = this.name + "_copy";
		return model;
	}

	public String toJson()
	{
		return NGTJson.getJsonFromObject(this);
	}

	public static DecorationModel fromJson(String json) throws IOException
	{
		return (DecorationModel)NGTJson.getObjectFromJson(json, DecorationModel.class);
	}

	/**通常形状のブロックを返す*/
	public static DecorationModel getDefaultModel(String name)
	{
		DecorationModel model = new DecorationModel();
		model.name = name;
		model.elements = new Element[]{Element.getDefaultElement()};
		return model;
	}
}
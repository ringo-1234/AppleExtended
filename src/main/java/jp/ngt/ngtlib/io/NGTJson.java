package jp.ngt.ngtlib.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class NGTJson
{
	private static Gson GSON_OBJ;

	public static void writeToJson(String json, File file)
	{
		try
		{
			//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
			pw.println(json);
			pw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * jsonからオブジェクトを生成
	 * @param <T>
	 * @throws IOException jsonの書式が不正な場合にスロー
	 */
	public static <T> T getObjectFromJson(String json, Class<? extends T> clazz) throws IOException
	{
		try
		{
			return getGson().fromJson(json, clazz);
		}
		catch(Exception e)
		{
			String message = "Can't load json : " + json + " (" + e.getMessage() + ")";
			throw new IOException(message, e);
		}
	}

	public static String getJsonFromObject(Object object)
	{
		return getGson().toJson(object);
	}

	public static Gson getGson()
	{
		if(GSON_OBJ == null)
		{
			GSON_OBJ = new GsonBuilder().setPrettyPrinting().create();
		}
		return GSON_OBJ;
	}
}
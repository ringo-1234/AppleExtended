package jp.ngt.ngtlib.io;

import java.io.IOException;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.util.ResourceLocation;

public final class ScriptUtil
{
	private static ScriptEngineManager SEM;

	private static void init()
	{
		LaunchClassLoader loader = Launch.classLoader;
		//これないとse.getInterface()でTypeErr
		loader.addClassLoaderExclusion("javax.");
		loader.addClassLoaderExclusion("jdk.nashorn.");

		//引数にnull入れないとScriptEngine取得不可(なしor別ClassLoaderダメ)
		SEM = new ScriptEngineManager(null);

		//showScripts(SEM);
	}

	private static void showScripts(ScriptEngineManager mng)
	{
		NGTLog.debug("SEF");
		for(ScriptEngineFactory factory : mng.getEngineFactories())
		{
			System.out.println("Engine name: " + factory.getEngineName());
	        System.out.println("Engine version: " + factory.getEngineVersion());
	        System.out.println("Language name: " + factory.getLanguageName());
	        System.out.println("Language version: " + factory.getLanguageVersion());

	        for(String extension : factory.getExtensions()) {
	            System.out.println("Extension: " + extension);
	        }
	        for(String mimeType : factory.getMimeTypes()) {
	            System.out.println("MimeType: " + mimeType);
	        }
	        for(String name : factory.getNames()) {
	            System.out.println("Short name: " + name);
	        }
            System.out.println();
        }
	}

	/*public static ScriptEngine doScript(ResourceLocation resource)
	{
		try
		{
			String script = NGTText.getText(resource, true);
			return doScript(script);
		}
		catch(IOException e)
		{
			throw new RuntimeException("Script load error : " + resource.getResourcePath(), e);
		}
	}*/

	/**JavaScriptの実行*/
	public static ScriptEngine doScript(String s)
	{
		if(SEM == null){init();}

		ScriptEngine se = SEM.getEngineByName("javascript");

		try
		{
			//速度変わらん
			//CompiledScript compiledScript = ((Compilable)se).compile(s);
			//compiledScript.eval();

			se.eval("load(\"nashorn:mozilla_compat.js\");");//Java8ではimportPackage()が使えないので、その対策
			se.eval(s);
			return se;
		}
		catch(ScriptException e)
		{
			throw new RuntimeException("Script exec error", e);
		}
	}

	public static CompiledScript compile(ScriptEngine engine, ResourceLocation resource)
	{
		try
		{
			String script = NGTText.getText(resource, true);
			CompiledScript compiledScript = ((Compilable)engine).compile(script);
			compiledScript.eval();
			return compiledScript;
		}
		catch(IOException e)
		{
			throw new RuntimeException("Script load error : " + resource.getResourcePath(), e);
		}
		catch (ScriptException e)
		{
			throw new RuntimeException("Script load error : " + resource.getResourcePath(), e);
		}
	}

	public static Object doScriptFunction(ScriptEngine se, String func, Object... args)
	{
		try
		{
			return ((Invocable)se).invokeFunction(func, args);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException("Script exec error : " + func, e);
		}
		catch (ScriptException e)
		{
			throw new RuntimeException("Script exec error : " + func, e);
		}
	}

	public static Object doScriptIgnoreError(ScriptEngine se, String func, Object... args)
	{
		try
		{
			return doScriptFunction(se, func, args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Object getScriptField(ScriptEngine se, String fieldName)
	{
		return se.get(fieldName);
	}

}
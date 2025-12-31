package jp.ngt.ngtlib.io;

import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class VirtualFileManager extends ForwardingJavaFileManager<JavaFileManager>
{
	protected final Map<String, ByteCodeObject> map = new HashMap<String, ByteCodeObject>();
	protected ClassLoader loader = null;

	public VirtualFileManager(JavaCompiler compiler, DiagnosticListener<? super JavaFileObject> listener)
	{
		super(compiler.getStandardFileManager(listener, null, null));
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException
	{
		ByteCodeObject co = new ByteCodeObject(className, kind);
		this.map.put(className, co);
		return co;
	}

	@Override
	public ClassLoader getClassLoader(Location location)
	{
		if(this.loader == null)
		{
			this.loader = new Loader();
		}
		return this.loader;
	}

	public byte[] getByteData(String className)
	{
		return this.map.get(className).getBytes();
	}

	private class Loader extends SecureClassLoader
	{
		public final LaunchClassLoader parent = Launch.classLoader;

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException
		{
			ByteCodeObject co = VirtualFileManager.this.map.get(name);
			if(co == null)
			{
				return super.findClass(name);
			}

			Class<?> c = co.getDefinedClass();
			if(c == null)
			{
				byte[] b = co.getBytes();
				c = super.defineClass(name, b, 0, b.length);
				co.setDefinedClass(c);
			}
			return c;
		}
	}
}
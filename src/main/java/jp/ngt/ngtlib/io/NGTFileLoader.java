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
package jp.ngt.ngtlib.io;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.IOUtils;
import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.util.ResourceLocation;
public final class NGTFileLoader
{
	public static final String NO_ZIP = "no_zip";
	private static final String[] EXCEPT_WORD = {"lang", "block", "item", "gui"};
	private static List<File> MODS_DIR;
	private static File PREV_OPENED_FOLDER;
	public static void log(String par1, Object... par2)
	{
		if(NGTCore.debugLog)
		{
			NGTLog.debug(par1, par2);
		}
	}
	public static List<File> findFile(FileMatcher matcher)
	{
		ScanResult result = findFile(new FileMatcher[]{matcher});
		return result.asList();
	}
	public static ScanResult findFile(FileMatcher... matchers)
	{
		ScanResult findFiles = new ScanResult();
		List<File> modsDir = getModsDir();
		for(File dir : modsDir)
		{
			log("[NGTFL] Set search path : " + dir.getAbsolutePath());
			try
			{
				findFileInDirectory(findFiles, dir, matchers);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return findFiles;
	}
	public static List<File> findFileInDirectory(File dir, FileMatcher matcher) throws IOException
	{
		ScanResult result = new ScanResult();
		findFileInDirectory(result, dir, new FileMatcher[]{matcher});
		return result.asList();
	}
	public static void findFileInDirectory(ScanResult result, File dir, FileMatcher... matchers) throws IOException
	{
		findFileInDirectory(result, dir, NO_ZIP, matchers);
	}
	private static void findFileInDirectory(ScanResult result, File dir, String containerName, FileMatcher... matchers) throws IOException
	{
		String[] files = dir.list();
		if(files == null || files.length == 0)
		{
			return;
		}
		for(String entryName : files)
        {
            File entry = new File(dir, entryName);
            if(entry.isFile())
            {
            	String name = entry.getName();
            	if(FileType.ZIP.match(name) || FileType.JAR.match(name))
            	{
            		findFileInZip(result, entry, "", matchers);
            	}
            	else
            	{
            		for(FileMatcher matcher : matchers)
                	{
                		if(matcher.match(entry))
                    	{
                			result.add(containerName, matcher, entry);
                    	}
                	}
            	}
            }
            else if(entry.isDirectory() && !isExeptFolder(entry))
            {
            	String nextContainer = containerName;
            	if(containerName.equals(NO_ZIP))
            	{
            		nextContainer = entry.getName();
            	}
            	findFileInDirectory(result, entry, nextContainer, matchers);
            }
        }
	}
	private static void findFileInZip(ScanResult result, File archive, String encoding, FileMatcher... matchers)
	{
		log("[NGTFL] Scan zip : " + archive.getName());
		try
		{
			ZipFile zip = getArchive(archive, encoding);
			zip.stream().filter(x -> !x.isDirectory()).forEach(entry -> {
				File file = new File(zip.getName(), entry.getName());
				for(FileMatcher matcher : matchers)
            	{
					if(matcher.match(file))
					{
						result.add(archive.getName(), matcher, file);
					}
            	}
			});
			zip.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			NGTLog.debug("[NGTFL] IOException:" + archive.getName());
		}
		catch (IllegalArgumentException e)
		{
			if(encoding.isEmpty())
			{
				findFileInZip(result, archive, "MS932", matchers);
				return;
			}
			e.printStackTrace();
			NGTLog.debug("[NGTFL] IllegalArgumentException:" + archive.getName());
		}
	}
	private static boolean isExeptFolder(File folder)
	{
		if(folder.getAbsolutePath().contains("sounds"))
		{
			return false;
		}
		for(String word : EXCEPT_WORD)
		{
			if(folder.getName().equals(word))
			{
				return true;
			}
		}
		return false;
	}
	public static byte[] readBytes(File par1) throws IOException
	{
		InputStream is = new FileInputStream(par1);
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    byte [] buffer = new byte[1024];
	    for(int len = 0; (len = is.read(buffer)) > 0; bout.write(buffer, 0, len)){}
	    is.close();
	    return bout.toByteArray();
	}
	public static List<File> getModsDir()
	{
		if(MODS_DIR != null)
		{
			return MODS_DIR;
		}
		MODS_DIR = new ArrayList<>();
		try
		{
			File modsDir2 = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath());
			if(!modsDir2.getAbsolutePath().contains("mods"))
			{
				MODS_DIR.add(modsDir2);
				NGTLog.debug("[NGTFL] Add mods dir : " + modsDir2.getAbsolutePath());
			}
		}
		catch (NullPointerException e)
		{
			;
		}
		File modsDir = NGTCore.proxy.getMinecraftDirectory("mods");
		String modsDirPath = modsDir.getAbsolutePath();
		if(modsDirPath.contains(".") && !modsDirPath.contains(".minecraft"))
		{
			modsDirPath = modsDirPath.replace("\\.", "");
		}
		MODS_DIR.add(new File(modsDirPath));
		NGTLog.debug("[NGTFL] Add mods dir : " + modsDirPath);
		return MODS_DIR;
	}
	private static JFileChooser getCustomChooser(String title)
	{
		JFileChooser chooser = new JFileChooser(PREV_OPENED_FOLDER)
		{
			@Override
			protected JDialog createDialog(Component parent) throws HeadlessException {
				JDialog dialog = super.createDialog(parent);
				dialog.setAlwaysOnTop(true);
				return dialog;
			}
		};
		chooser.setDialogTitle(title);
		chooser.requestFocusInWindow();
		return chooser;
	}
	public static synchronized File selectFile(FileType... types)
	{
		final JFileChooser chooser = getCustomChooser("Select File");
		chooser.setAcceptAllFileFilterUsed(false);
		for(FileType type : types)
		{
			FileFilter filter = new FileNameExtensionFilter(type.getDescription(), type.getExtension());
			chooser.addChoosableFileFilter(filter);
		}
		int state = chooser.showOpenDialog(null);
		if(state == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			PREV_OPENED_FOLDER = file.getParentFile();
			return file;
		}
		return null;
	}
	public static synchronized File saveFile(FileType... types)
	{
		final JFileChooser chooser = getCustomChooser("Save File");
		chooser.setAcceptAllFileFilterUsed(false);
		for(FileType type : types)
		{
			FileFilter filter = new FileNameExtensionFilter(type.getDescription(), type.getExtension());
			chooser.addChoosableFileFilter(filter);
		}
		int state = chooser.showSaveDialog(null);
		if(state == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			PREV_OPENED_FOLDER = file.getParentFile();
			if(!file.getName().contains("."))
			{
				FileNameExtensionFilter filter = (FileNameExtensionFilter)chooser.getFileFilter();
				file = new File(file.getAbsolutePath() + "." + filter.getExtensions()[0]);
			}
			return file;
		}
		return null;
	}
	public static InputStream getInputStream(ResourceLocation par1) throws IOException
	{
		if(!NGTCore.proxy.isServer())
		{
			return NGTUtilClient.getMinecraft().getResourceManager().getResource(par1).getInputStream();
		}
		int index = par1.getResourcePath().lastIndexOf("/");
		String fileName = par1.getResourcePath().substring(index + 1);
		List<File> list = NGTFileLoader.findFile((file)->{return file.getName().equals(fileName);});
		if(list.isEmpty())
		{
			throw new FileNotFoundException("On get stream : " + fileName);
		}
		File file = list.get(0);
		return getInputStreamFromFile(file);
	}
	public static InputStream getInputStreamFromFile(File file) throws IOException
	{
		String suffix = getArchiveSuffix(file.getAbsolutePath());
		if(!suffix.isEmpty())
		{
			return getStreamFromArchive(file, suffix);
		}
		else
		{
			return new FileInputStream(file);
		}
	}
	public static InputStream getStreamFromArchive(File file, String suffix) throws IOException
	{
		String zipPath = getArchivePath(file.getAbsolutePath(), suffix);
		ZipFile zip = getArchive(new File(zipPath), "");
		Enumeration<? extends ZipEntry> enu = zip.entries();
		while(enu.hasMoreElements())
		{
			ZipEntry ze = enu.nextElement();
			if(!ze.isDirectory())
			{
				File fileInZip = new File(zipPath, ze.getName());
				if(fileInZip.getName().equals(file.getName()))
	            {
					InputStream is = zip.getInputStream(ze);
					return new BufferedInputStream(is);
	            }
			}
		}
		zip.close();
		throw new FileNotFoundException("On get stream : " + file.getName());
	}
	public static String getArchivePath(String absPath, String suffix)
	{
		int index = absPath.indexOf(suffix);
		return absPath.substring(0, index + 4);
	}
	public static ZipFile getArchive(File file, String encoding) throws IOException
	{
		String en2 = encoding.isEmpty() ? "UTF-8" : encoding;
		if(FileType.JAR.match(file.getName()))
		{
			return new JarFile(file.getAbsolutePath());
		}
		else if(FileType.ZIP.match(file.getName()))
		{
			return new ZipFile(file.getAbsolutePath(), Charset.forName(en2));
		}
		return null;
	}
	public static String getArchiveSuffix(String absPath)
	{
		if(absPath.contains(".zip"))
		{
			return ".zip";
		}
		else if(absPath.contains(".jar"))
		{
			return ".jar";
		}
		return "";
	}
	public static File createTempFile(InputStream is, String name) throws IOException
	{
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		final File tempFile = new File(tempDir, name);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(is, out);
        return tempFile;
	}
}

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

package jp.ngt.ngtlib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

public final class NBTUtil
{
	public static byte[] compress(NBTTagCompound data)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(baos)));
			CompressedStreamTools.write(data, dos);
			dos.close();//closeしてからtoByteArray()する、じゃないと値が変
			return baos.toByteArray();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static NBTTagCompound decompress(byte[] data)
	{
		try
		{
			DataInputStream dis = new DataInputStream(
					new BufferedInputStream(
							new GZIPInputStream(
									new ByteArrayInputStream(data))));
			NBTTagCompound decompData = CompressedStreamTools.read(dis, NBTSizeTracker.INFINITE);
			dis.close();
			return decompData;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
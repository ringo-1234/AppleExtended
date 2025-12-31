package jp.ngt.ngtlib.renderer.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jp.ngt.ngtlib.io.FileType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MqozModel extends MqoModel
{
	protected MqozModel(InputStream[] is, String name, int mode, VecAccuracy par3) throws ModelFormatException
	{
		super(is, name, mode, par3);
	}

	@Override
    protected void init(InputStream[] is) throws ModelFormatException
    {
		ZipInputStream zis = new ZipInputStream(is[0]);
		try
		{
			ZipEntry zEntry;
			while((zEntry = zis.getNextEntry()) != null)
			{
				String entryName = zEntry.getName();
				if(entryName != null && entryName.endsWith("mqo"))
				{
					super.init(new InputStream[]{zis});
					break;
				}
			}
			zis.close();
		}
		catch (IOException e)
		{
			throw new ModelFormatException("Exception on reading MQOZ.", e);
		}
    }

	@Override
    public FileType getType()
    {
        return FileType.MQOZ;
    }
}

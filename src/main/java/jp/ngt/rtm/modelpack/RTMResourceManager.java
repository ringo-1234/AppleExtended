package jp.ngt.rtm.modelpack;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import com.google.common.collect.Lists;
import jp.ngt.ngtlib.io.NGTFileLoader;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public final class RTMResourceManager implements IResourceManager
{
	private final Map<ResourceLocation, IResource> resourceCache = new HashMap<>();
    private final MetadataSerializer serializer;
    private final File domain;
    public RTMResourceManager(MetadataSerializer par1, File par2)
    {
        this.serializer = par1;
        this.domain = par2;
    }
    @Override
    public Set getResourceDomains()
    {
        return null;
    }
	@Override
    public IResource getResource(ResourceLocation par1) throws IOException
    {
    	if(this.domain == null)
    	{
    		throw new FileNotFoundException(par1.toString());
    	}
    	if(this.resourceCache.containsKey(par1))
    	{
    		return this.resourceCache.get(par1);
    	}
    	String absPath = this.domain.getAbsolutePath();
    	InputStream stream = null;
    	String suffix = NGTFileLoader.getArchiveSuffix(absPath);
    	if(!suffix.isEmpty())
    	{
    		String zipPath = NGTFileLoader.getArchivePath(absPath, suffix);
    		String s = par1.getResourcePath();
    		try
    		{
    			ZipFile zip = NGTFileLoader.getArchive(new File(zipPath), "");
    			Enumeration<? extends ZipEntry> enu = zip.entries();
    			while(enu.hasMoreElements())
    			{
    				ZipEntry ze = enu.nextElement();
    				if(!ze.isDirectory())
    				{
    					File fileInZip = new File(zipPath, ze.getName());
    					if(s.contains(fileInZip.getName()))
    		            {
    						stream = zip.getInputStream(ze);
    		            }
    				}
    			}
    		}
    		catch(IOException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	else
    	{
    		File resource = new File(this.domain, par1.getResourcePath());
    		if(resource.exists())
    		{
    			stream = new FileInputStream(resource);
    		}
    		else
    		{
    			File file = this.findFile(this.domain, new File(par1.getResourcePath()).getName());
    			if(file != null)
    			{
    				stream = new FileInputStream(file);
    			}
    		}
    	}
    	if(stream != null)
    	{
    		IResource resource = new SimpleResource("RTMCustom", par1, stream, null, this.serializer);
    		this.resourceCache.put(par1, resource);
    		return resource;
    	}
    	throw new ModelPackException("[RTMResourceManager] Can't get input stream", par1.getResourcePath());
    }
    @Override
    public List getAllResources(ResourceLocation par1) throws IOException
    {
    	List list = Lists.newArrayList();
    	list.add(this.getResource(par1));
    	if(!list.isEmpty())
        {
            return list;
        }
    	throw new FileNotFoundException(par1.toString());
    }
    private File findFile(File dir, String fileName)
    {
    	File[] files = dir.listFiles();
    	if(files != null)
    	{
    		for(File file : files)
    		{
    			if(file.isDirectory())
    			{
    				File found = this.findFile(file, fileName);
    				if(found != null)
    				{
    					return found;
    				}
    			}
    			else if(file.getName().equals(fileName))
    			{
    				return file;
    			}
    		}
    	}
    	return null;
    }
}

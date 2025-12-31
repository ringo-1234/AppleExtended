package jp.ngt.ngtlib.io;

import net.minecraft.util.ResourceLocation;

public final class ResourceLocationCustom extends ResourceLocation
{
	private final String rawDomain;
	private final String rawPath;

	public ResourceLocationCustom(String name)
	{
		this(getDomain(name), getPath(name));
	}

	public ResourceLocationCustom(String domain, String path)
	{
		super(domain, path);
		this.rawDomain = domain;
		this.rawPath = path;
	}

	private static String getDomain(String name)
	{
		return name.contains(":") ? name.split(":")[0] : "minecraft";
	}

	private static String getPath(String name)
	{
		return name.contains(":") ? name.split(":")[1] : name;
	}

	@Override
    public String getResourceDomain()
    {
        return this.rawDomain;
    }

	@Override
	public String getResourcePath()
    {
        return this.rawPath;
    }

	@Override
    public String toString()
    {
        return this.rawDomain + ':' + this.rawPath;
    }

	@Override
    public boolean equals(Object obj)
    {
    	if(obj == this)
    	{
    		return true;
    	}
    	else if(obj instanceof ResourceLocation)
    	{
    		ResourceLocation res = (ResourceLocation)obj;
    		return res.getResourceDomain().equals(this.getResourceDomain()) && res.getResourcePath().equals(this.getResourcePath());
    	}
    	return super.equals(obj);
    }

	@Override
    public int hashCode()
    {
        return 31 * this.getResourceDomain().hashCode() + this.getResourcePath().hashCode();
    }

	@Override
    public int compareTo(ResourceLocation location)
    {
        int i = this.getResourceDomain().compareTo(location.getResourceDomain());
        if(i == 0)
        {
            i = this.getResourcePath().compareTo(location.getResourcePath());
        }
        return i;
    }
}
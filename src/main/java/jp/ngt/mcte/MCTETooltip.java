package jp.ngt.mcte;

import jp.ngt.ngtlib.util.Usage;

public final class MCTETooltip
{
	public static void init()
	{
		Usage.INSTANCE.add(MCTE.portIn,  -1, "usage.block.port_in");
		Usage.INSTANCE.add(MCTE.portOut, -1, "usage.block.port_out");
		String[] usageEditor = new String[9];
		for(int i= 0; i < usageEditor.length; ++i)
		{
			usageEditor[i] = "usage.item.editor." + i;
		}
		Usage.INSTANCE.add(MCTE.editor,    -1, usageEditor);
		Usage.INSTANCE.add(MCTE.generator, -1, "usage.item.generator");
		Usage.INSTANCE.add(MCTE.painter,   -1, "usage.item.painter");
		//Usage.INSTANCE.add(MCTE.itemMiniature, -1, "usage.item.istlobj.fluorescent");
	}
}
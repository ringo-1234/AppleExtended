package jp.ngt.rtm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.gui.GuiSelect;
import jp.ngt.ngtlib.gui.GuiSlotCustom.SlotElement;
import jp.ngt.rtm.entity.npc.EntityMotorman;
import jp.ngt.rtm.entity.npc.macro.MacroRecorder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMotorman extends GuiSelect
{
	public static GuiMotorman getGui(final EntityMotorman entity)
	{
		File macroFolder = MacroRecorder.INSTANCE.getMacroFolder();
		List<File> list = new ArrayList<File>();
		for(File child : macroFolder.listFiles())
		{
			if(child.isFile() && child.getName().endsWith(".txt"))
			{
				list.add(child);
			}
		}

		if(list.isEmpty()){return null;}

		ItemStack icon = new ItemStack(Items.BOOK);
		SlotElement[] elements = new SlotElement[list.size()];
		for(int i = 0; i < elements.length; ++i)
		{
			File file = list.get(i);

			elements[i] = new SlotElementItem<File>((obj)->{
				try
				{
					entity.setMacro((File)obj);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}, file, file.getName(), icon);
		}
		return new GuiMotorman(entity, elements);
	}

	private GuiMotorman(EntityMotorman entity, SlotElement[] par2)
	{
		super(null, par2);
	}

	@Override
	public void initGui()
	{
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
	}

	@Override
	protected void closeScreen()
	{
		this.mc.displayGuiScreen(null);
	}
}
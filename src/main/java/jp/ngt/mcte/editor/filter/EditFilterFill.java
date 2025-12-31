package jp.ngt.mcte.editor.filter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;

import jp.ngt.mcte.editor.Editor;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.math.AABBInt;
import net.minecraft.command.ICommandSender;

public class EditFilterFill extends EditFilterBase
{
	/**exec(x, y, z)*/
	private static final String FORMULA_NAME = "exec";
	private static final Pattern FORM_EQUAL = Pattern.compile("[<>=]?=");
	private static final String[] MATH_FUNCTIONS = {"sin", "cos", "tan", "log", "sqrt", "exp", "pow", "E", "PI"};

	private Map<String, FillBase> typeMap;
	private ScriptEngine script;
	private int offsetX, offsetY, offsetZ;

	@Override
	public void init(Config par)
	{
		super.init(par);

		this.typeMap = new LinkedHashMap<String, EditFilterFill.FillBase>();
		this.typeMap.put("FillAll", new FillAll());
		this.typeMap.put("FillCylinderX", new FillCylinderX());
		this.typeMap.put("FillCylinderY", new FillCylinderY());
		this.typeMap.put("FillCylinderZ", new FillCylinderZ());
		this.typeMap.put("FillEllipsoid", new FillEllipsoid());
		this.typeMap.put("FillCustom", new FillCustom());

		String[] sa = new String[this.typeMap.size()];
		int index = 0;
		for(String key : this.typeMap.keySet())
		{
			sa[index] = key;
			++index;
		}

		par.addString("FillType", "FillAll", sa);
		par.addString("Formula", "y=x*0.5+z*0.5");
		par.addInt("OffsetX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);//選択BoxのMinを始点とする
		par.addInt("OffsetY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		par.addInt("OffsetZ", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public String getFilterName()
	{
		return "Fill";
	}

	@Override
	public boolean edit(Editor editor)
	{
		AABBInt box = editor.getSelectBox();
		BlockSet blockSet = editor.getFillItem();
		String type = this.cfg.getString("FillType");
		FillBase fillBase = this.typeMap.get(type);

		if(type.equals("FillCustom"))
		{
			String s = this.cfg.getString("Formula");
			String formula = this.formatFormula(s, editor.getEntity().getPlayer());
			if(formula.isEmpty()){return false;}
			try
			{
				this.script = ScriptUtil.doScript(formula);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				NGTLog.sendChatMessage(editor.getEntity().getPlayer(), "Invalid formula. (Please see logs.)");
				return false;
			}
			this.offsetX = this.cfg.getInt("OffsetX") + box.minX;
			this.offsetY = this.cfg.getInt("OffsetY") + box.minY;
			this.offsetZ = this.cfg.getInt("OffsetZ") + box.minZ;
		}

		if(box != null && blockSet != BlockSet.AIR && fillBase != null)
		{
			editor.record(box);
			editor.repeat(box, fillBase, 1);
			for(int[] pos : fillBase.getBlockPos())
			{
				editor.setBlock(pos[0], pos[1], pos[2], blockSet, true);
			}
			editor.updateBlocks(box);
			return true;
		}

		return false;
	}

	private String formatFormula(String par1, ICommandSender sender)
	{
		Matcher matcher = FORM_EQUAL.matcher(par1);
		if(matcher.find())
		{
			String eq = matcher.group();
			if(eq.equals("="))
			{
				eq = "==";
			}
			//par1 = matcher.replaceAll("==");
			String[] sa = par1.split(FORM_EQUAL.pattern());
			String sideL = "Math.round(" + sa[0] + ")";
			String sideR = "Math.round(" + sa[1] + ")";
			String formula = sideL + eq + sideR;

			for(String func : MATH_FUNCTIONS)
			{
				if(formula.contains(func))
				{
					formula = formula.replace(func, ("Math." + func));
				}
			}

			return "function " + FORMULA_NAME + "(x, y, z){ return " + formula + "; }";
		}
		else
		{
			NGTLog.sendChatMessage(sender, "Invalid formula. (Not contains '='.)");
			return "";
		}
	}

	private abstract class FillBase implements Repeatable
	{
		private final List<int[]> posList = new ArrayList<int[]>();

		public void init()
		{
			this.posList.clear();
		}

		public List<int[]> getBlockPos()
		{
			return this.posList;
		}

		public void addPos(int x, int y, int z)
		{
			this.posList.add(new int[]{x, y, z});
		}
	}

	private class FillAll extends FillBase
	{
		@Override
		public void processing(AABBInt box, int index, int rep, int x, int y, int z)
		{
			this.addPos(x, y, z);
		}
	}

	private class FillCylinderX extends FillBase
	{
		@Override
		public void processing(AABBInt box, int index, int rep, int x, int y, int z)
		{
			double[] center = {(double)(box.maxX + box.minX) / 2.0D, (double)(box.maxY + box.minY) / 2.0D, (double)(box.maxZ + box.minZ) / 2.0D};
	        double[] r = {(double)(box.maxX - box.minX) / 2.0D, (double)(box.maxY - box.minY) / 2.0D, (double)(box.maxZ - box.minZ) / 2.0D};
	        double y0 = (double)y + 0.5D - center[1];
			double z0 = (double)z + 0.5D - center[2];
			double d0 = (y0 * y0 / (r[1] * r[1])) + (z0 * z0 / (r[2] * r[2]));
			if(d0 <= 1.0D)
			{
				this.addPos(x, y, z);
			}
		}
	}

	private class FillCylinderY extends FillBase
	{
		@Override
		public void processing(AABBInt box, int index, int rep, int x, int y, int z)
		{
			double[] center = {(double)(box.maxX + box.minX) / 2.0D, (double)(box.maxY + box.minY) / 2.0D, (double)(box.maxZ + box.minZ) / 2.0D};
	        double[] r = {(double)(box.maxX - box.minX) / 2.0D, (double)(box.maxY - box.minY) / 2.0D, (double)(box.maxZ - box.minZ) / 2.0D};
	        double x0 = (double)x + 0.5D - center[0];
			double z0 = (double)z + 0.5D - center[2];
			double d0 = (x0 * x0 / (r[0] * r[0])) + (z0 * z0 / (r[2] * r[2]));
			if(d0 <= 1.0D)
			{
				this.addPos(x, y, z);
			}
		}
	}

	private class FillCylinderZ extends FillBase
	{
		@Override
		public void processing(AABBInt box, int index, int rep, int x, int y, int z)
		{
			double[] center = {(double)(box.maxX + box.minX) / 2.0D, (double)(box.maxY + box.minY) / 2.0D, (double)(box.maxZ + box.minZ) / 2.0D};
	        double[] r = {(double)(box.maxX - box.minX) / 2.0D, (double)(box.maxY - box.minY) / 2.0D, (double)(box.maxZ - box.minZ) / 2.0D};
	        double x0 = (double)x + 0.5D - center[0];
			double y0 = (double)y + 0.5D - center[1];
			double d0 = (x0 * x0 / (r[0] * r[0])) + (y0 * y0 / (r[1] * r[1]));
			if(d0 <= 1.0D)
			{
				this.addPos(x, y, z);
			}
		}
	}

	private class FillEllipsoid extends FillBase
	{
		@Override
		public void processing(AABBInt box, int index, int rep, int x, int y, int z)
		{
			double[] center = {(double)(box.maxX + box.minX) / 2.0D, (double)(box.maxY + box.minY) / 2.0D, (double)(box.maxZ + box.minZ) / 2.0D};
	        double[] r = {(double)(box.maxX - box.minX) / 2.0D, (double)(box.maxY - box.minY) / 2.0D, (double)(box.maxZ - box.minZ) / 2.0D};
	        double x0 = (double)x + 0.5D - center[0];
			double y0 = (double)y + 0.5D - center[1];
			double z0 = (double)z + 0.5D - center[2];
			double d0 = (x0 * x0 / (r[0] * r[0])) + (y0 * y0 / (r[1] * r[1])) + (z0 * z0 / (r[2] * r[2]));
			if(d0 <= 1.0D)
			{
				this.addPos(x, y, z);
			}
		}
	}

	private class FillCustom extends FillBase
	{
		@Override
		public void processing(AABBInt box, int index, int rep, int x, int y, int z)
		{
			boolean flag = (Boolean)ScriptUtil.doScriptIgnoreError(EditFilterFill.this.script, FORMULA_NAME,
					x - EditFilterFill.this.offsetX + 0.5, y - EditFilterFill.this.offsetY + 0.5, z - EditFilterFill.this.offsetZ + 0.5);
			if(flag)
			{
				this.addPos(x, y, z);
			}
		}
	}
}
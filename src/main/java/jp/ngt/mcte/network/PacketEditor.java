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

package jp.ngt.mcte.network;

import io.netty.buffer.ByteBuf;
import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.editor.EditEntry;
import jp.ngt.mcte.editor.Editor;
import jp.ngt.mcte.editor.EditorManager;
import jp.ngt.mcte.editor.EditorTransform;
import jp.ngt.mcte.editor.EntityEditor;
import jp.ngt.ngtlib.block.NGTObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEditor implements IMessage, IMessageHandler<PacketEditor, IMessage>
{
	private String playerName;
	private String type;
	private int[] start;
	private int[] end;
	private int[] clone;

	public PacketEditor(){}

	/**
	 * @param par4 start
	 * @param par5 end
	 * @param par6 clone
	 * */
	public PacketEditor(EntityEditor par1, String par2, int[] par4, int[] par5, int[] par6)
	{
		this.playerName = par1.getPlayer().getName();
		this.type = par2;
		this.start = par4;
		this.end = par5;
		this.clone = par6;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.playerName);
		ByteBufUtils.writeUTF8String(buffer, this.type);
		buffer.writeInt(this.start[0]);
		buffer.writeInt(this.start[1]);
		buffer.writeInt(this.start[2]);
		buffer.writeInt(this.end[0]);
		buffer.writeInt(this.end[1]);
		buffer.writeInt(this.end[2]);
		buffer.writeInt(this.clone[0]);
		buffer.writeInt(this.clone[1]);
		buffer.writeInt(this.clone[2]);
		buffer.writeInt(this.clone[3]);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.playerName = ByteBufUtils.readUTF8String(buffer);
		this.type = ByteBufUtils.readUTF8String(buffer);
		this.start = new int[3];
		this.start[0] = buffer.readInt();
		this.start[1] = buffer.readInt();
		this.start[2] = buffer.readInt();
		this.end = new int[3];
		this.end[0] = buffer.readInt();
		this.end[1] = buffer.readInt();
		this.end[2] = buffer.readInt();
		this.clone = new int[4];
		this.clone[0] = buffer.readInt();
		this.clone[1] = buffer.readInt();
		this.clone[2] = buffer.readInt();
		this.clone[3] = buffer.readInt();
	}

	@Override
    public IMessage onMessage(PacketEditor message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().player.world;
		Editor editor = EditorManager.INSTANCE.getEditor(message.playerName);
		if(editor != null)
		{
			EntityEditor entityEditor = editor.getEntity();
			entityEditor.setPos(EntityEditor.START_POS, message.start[0], message.start[1], message.start[2]);
			entityEditor.setPos(EntityEditor.END_POS, message.end[0], message.end[1], message.end[2]);
			entityEditor.setCloneBox(message.clone[0], message.clone[1], message.clone[2], message.clone[3]);

			if(message.type.equals("replace"))
			{
				entityEditor.post(new EditEntry() {
					@Override
					public void edit()
					{
						editor.editBlocks(Editor.EditType_Replace, 0.0F);
					}
				});
			}
			else if(message.type.equals("clone"))
			{
				entityEditor.post(new EditEntry() {
					@Override
					public void edit()
					{
						editor.editBlocks(Editor.EditType_Clone, 0.0F);
					}
				});
			}
			else if(message.type.startsWith("miniature"))
			{
				entityEditor.post(new EditEntry() {
					@Override
					public void edit()
					{
						String[] sa = message.type.split(":");
						float rate = Float.parseFloat(sa[1]);
						editor.editBlocks(Editor.EditType_Miniature, rate);
					}
				});
			}
			else if(message.type.startsWith("transform"))
			{
				String[] sa = message.type.split(":");
				int type = Integer.parseInt(sa[1]);
				editor.transformBlocks(EditorTransform.values()[type]);
			}
			else if(message.type.startsWith("export"))
			{
				String[] sa = message.type.split(" ");
				NGTObject ngto = editor.copy(editor.getSelectBox(), "").convertNGTO();
				MCTE.NETWORK_WRAPPER.sendTo(new PacketExportData(sa[1], ngto), ctx.getServerHandler().player);
			}
		}
		return null;
	}
}
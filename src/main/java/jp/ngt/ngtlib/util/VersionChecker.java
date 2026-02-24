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

package jp.ngt.ngtlib.util;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
public class VersionChecker {
	private static final VersionChecker checker = new VersionChecker();
	private List<PackInfo> checkList = new ArrayList<>();
	/**
	 * {name, version, homepage}
	 */
	private List<String[]> updateList = new ArrayList<>();

	private boolean finished;

	/**
	 * 更新通知リストに追加
	 */
	public static void addToCheckList(PackInfo par1) {
		checker.checkList.add(par1);
	}

	public static void checkVersion() {
		Thread thread = checker.new VersionCheckThread();
		thread.start();
	}

	/**
	 * 更新通知を表示(ClientSide)
	 */
	public static void showUpdateMessage(INetHandlerPlayClient handler) {
		if (checker.finished) {
			for (String[] sa : checker.updateList) {
				ITextComponent component = new TextComponentTranslation("message.version", new Object[]{TextFormatting.AQUA + sa[0]});
				component.appendText(" : " + TextFormatting.GREEN + sa[1]);
				//DL先リンクを追加
				if (sa[2] != null && !sa[2].isEmpty()) {
					ITextComponent component2 = new TextComponentTranslation("  §6§nDownload here", new Object[0]);
					component2.setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, sa[2])));
					component.appendSibling(component2);
				}
				//INetHandlerPlayClient.handleChat(S02PacketChat)ではエラーが出るので、パケット経由せず出力
				NGTLog.showChatMessage(component);
			}
		}
	}

	public class VersionCheckThread extends Thread {
		public VersionCheckThread() {
			super("NGT Version Check");
		}

		@Override
		public void run() {
			List<String> strings = new ArrayList<>();
			Map<String, String> latestVerMap = new HashMap<String, String>();//<name, ver>
			for (PackInfo info : checker.checkList) {
				if (latestVerMap.containsKey(info.name)) {
					String ver = latestVerMap.get(info.name);
					if (!info.version.equals(ver)) {
						checker.updateList.add(new String[]{info.name, ver, info.homepage});
						continue;
					}
				}

				String location = info.updateURL;
				if (location == null || location.length() == 0) {
					continue;
				}

				try {
					URL url = new URL(location);
					List<String> sList = NGTText.readTextL(url.openStream(), "");
					strings.addAll(sList);
				} catch (MalformedURLException e) {
					NGTLog.debug("URL:" + location);
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}

				String[] sa1 = strings.toArray(new String[strings.size()]);
				strings.clear();

				for (String s : sa1) {
					String[] sa2 = s.split(":");
					if (sa2.length == 2) {
						latestVerMap.put(sa2[0], sa2[1]);
						if (info.name.equals(sa2[0]) && !info.version.equals(sa2[1])) {
							checker.updateList.add(new String[]{info.name, sa2[1], info.homepage});
						}
					}
				}
			}
			checker.finished = true;
		}
	}
}
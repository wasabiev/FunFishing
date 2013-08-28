package net.wasabi_server.funfishing;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.wasabi_server.funfishing.util.Actions;
import net.wasabi_server.funfishing.FunFishing;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FishingEvent {

	// Logger
	public static final Logger log = FunFishing.log;
	private static final String msgPrefix = FunFishing.msgPrefix;
	private static final String logPrefix = FunFishing.logPrefix;

	// Instance
	private final FunFishing plugin;

	// Fishing Event
	HashMap<String, Integer> scores = new HashMap<String, Integer>();
	private int time_limit = 10;
	private int remain_sec = -1;
	private boolean ready = false;
	private boolean start = false;

	// Constructor
	public FishingEvent(final FunFishing plugin) {
		this.plugin = plugin;
	}

	// Scheduler
	public void startScheduler() {
		remain_sec = this.getSecond(time_limit);
		this.plugin.getServer().getScheduler()
				.scheduleSyncRepeatingTask(plugin, new Runnable() {
					public void run() {
						if (remain_sec > 0) {
							remain_sec--;
							if (remain_sec % 60 == 0) {
								sendToFisher("&a残り" + remain_sec / 60 + "分");
							} else if (remain_sec == 10
									|| (remain_sec <= 5 && remain_sec > 0)) {
								sendToFisher("&a" + remain_sec + "秒前");
							} else if (remain_sec == 0) {
								stopEvent();
							}
						}
					}
				}, 0L, 20L);
	}

	// Event
	/**
	 * イベント準備
	 * 
	 * @param sender
	 */
	public void readyEvent(CommandSender sender) {
		if (!ready && !start) {
			ready = true;
			start = false;
			Actions.broadcastMessage(msgPrefix + "釣りイベントの受付を開始しました。");
			Actions.broadcastMessage(msgPrefix
					+ "/fishing joinコマンドでイベントに参加できます。");
		} else if (ready) {
			Actions.message(sender, msgPrefix + "現在釣りイベントの受付中です。");
		} else if (start) {
			Actions.message(sender, msgPrefix + "既に釣りイベントが進行中です。");
		}
	}

	/**
	 * イベント開始
	 * 
	 * @param sender
	 */
	public void startEvent(CommandSender sender) {
		if (ready && !start) {
			ready = false;
			start = true;
			startScheduler();
			this.sendToFisher("釣りイベントを開始しました！");
		} else if (start) {
			Actions.message(sender, msgPrefix + "既に釣りイベントが開始しています。");
		} else if (!ready) {
			Actions.message(sender, msgPrefix + "開始準備が出来ていません。");
		}
	}

	/**
	 * イベント終了
	 */
	public void stopEvent() {
		// 賞品付与
		reward();
		// 初期化
		init();
	}

	/**
	 * 賞品付与
	 */
	public void reward() {

	}

	/**
	 * 初期化
	 */
	public void init() {
		this.scores.clear();
		time_limit = 10;
		remain_sec = -1;
		ready = false;
		start = false;
		log.info(logPrefix + "Initializing FishingEvent Data...Succeeded!");
	}

	// 各種機能
	/**
	 * ready?
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * start?
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * プレイヤーを追加
	 */
	public void addPlayer(Player p) {
		if (!this.scores.containsKey(p.getName())) {
			this.scores.put(p.getName(), Integer.valueOf(0));
		} else {
			Actions.message(p, msgPrefix + "既に釣りイベントに参加しています！");
		}
	}

	/**
	 * スコア追加
	 */
	public void addScore(Player p) {
		if (this.scores.containsKey(p.getName())) {
			this.scores.put(p.getName(), Integer.valueOf(getScore(p) + 1));
			Actions.message(p, msgPrefix + getScore(p) + "匹目のお魚を釣り上げました！");
		}
	}

	/**
	 * プレイヤーのデータがあるかどうか
	 */
	public boolean beScored(Player p) {
		if (this.scores.containsKey(p.getName())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * プレイヤーのスコアを取得
	 */
	public int getScore(Player p) {
		return ((Integer) this.scores.get(p.getName())).intValue();
	}

	/**
	 * トップのプレイヤーを取得
	 */
	public String getTopPlayerName() {
		int current = 0;
		String topName = "";
		for (Map.Entry entry : this.scores.entrySet()) {
			if (((Integer) entry.getValue()).intValue() > current) {
				current = ((Integer) entry.getValue()).intValue();
				topName = (String) entry.getKey();
			}
		}
		return topName;
	}

	/**
	 * トップのプレイヤーのスコアを取得
	 */
	public int getTopScore() {
		if (!this.getTopPlayerName().equals("")) {
			return ((Integer) this.scores.get(this.getTopPlayerName()))
					.intValue();
		} else {
			return 0;
		}
	}

	public void setTimeLimit(int minutes) {
		time_limit = minutes;
	}

	public int getSecond(int minutes) {
		return minutes * 60;
	}

	/**
	 * 参加者にメッセージを送信
	 */
	public void sendToFisher(String message) {
		for (Map.Entry entry : this.scores.entrySet()) {
			Player player = this.plugin.getServer().getPlayer(
					(String) entry.getKey());
			if (player.isOnline()) {
				Actions.message(player, msgPrefix + message);
			}
		}
	}
}

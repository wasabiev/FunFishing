package net.wasabi_server.funfishing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import net.wasabi_server.funfishing.command.BaseCommand;
import net.wasabi_server.funfishing.command.DebugCommand;
import net.wasabi_server.funfishing.command.HelpCommand;
import net.wasabi_server.funfishing.command.StatusAllCommand;
import net.wasabi_server.funfishing.command.StatusCommand;
import net.wasabi_server.funfishing.listener.PlayerFishEventListener;
import net.wasabi_server.funfishing.util.Actions;

public class FunFishing extends JavaPlugin {

	// ヾ(｀・ω・´)ノCaused by: java.lang.NullPointerException

	/**
	 * TODO:
	 * 
	 * HashMapの読み書き
	 * 
	 * 
	 * 
	 */

	// Logger
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static String logPrefix = "[FunFishing] ";
	public final static String msgPrefix = "&6[FunFishing] &f";

	// Listener
	PlayerFishEventListener playerFishEventListener = new PlayerFishEventListener(
			this);

	// Commands
	private List<BaseCommand> commands = new ArrayList<BaseCommand>();

	// Instance
	private static FunFishing instance;

	// Fishing
	HashMap<String, Integer> scores = new HashMap<String, Integer>();
	private int score_limit = 30;

	/**
	 * プラグイン起動処理
	 */
	@Override
	public void onEnable() {
		instance = this;
		PluginManager pm = getServer().getPluginManager();
		ScoreboardManager manager = getServer().getScoreboardManager();
		if (!pm.isPluginEnabled(this)) {
			return;
		}

		pm.registerEvents(playerFishEventListener, this);

		registerCommands();

		// メッセージ
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] version " + pdfFile.getVersion()
				+ " is enabled!");

	}

	/**
	 * プラグイン停止処理
	 */
	@Override
	public void onDisable() {
		// メッセージ
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] version " + pdfFile.getVersion()
				+ " is disabled!");
	}

	/**
	 * コマンドを登録
	 */
	private void registerCommands() {
		commands.add(new HelpCommand());
		commands.add(new DebugCommand());
		commands.add(new StatusCommand());
		commands.add(new StatusAllCommand());
	}

	/**
	 * コマンドが呼ばれた
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String args[]) {
		if (cmd.getName().equalsIgnoreCase("fishing")) {
			if (args.length == 0) {
				args = new String[] { "help" };
			}

			outer: for (BaseCommand command : commands
					.toArray(new BaseCommand[0])) {
				String[] cmds = command.getName().split(" ");
				for (int i = 0; i < cmds.length; i++) {
					if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])) {
						continue outer;
					}
					return command.run(this, sender, args, commandLabel);
				}
			}
			new HelpCommand().run(this, sender, args, commandLabel);
			return true;
		}
		return false;
	}

	// ** ある一定数釣ったらリセットされる釣り機能 **
	/**
	 * スコア追加
	 */
	public void addScore(Player p) {
		if (!this.scores.containsKey(p.getName())) {
			this.scores.put(p.getName(), Integer.valueOf(1));
			Actions.message(p, msgPrefix + getScore(p) + "匹目のお魚を釣り上げました！");
		} else {
			this.scores.put(p.getName(), Integer.valueOf(getScore(p) + 1));
			if (getScore(p) >= score_limit) {
				reward(p);
				init();
			} else {
				Actions.message(p, msgPrefix + getScore(p) + "匹目のお魚を釣り上げました！");
			}
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
	 * スコアをセット（デバック用）
	 */
	public void setScore(Player p, int i) {
		if (i < score_limit) {
			this.scores.put(p.getName(), Integer.valueOf(i));
			Actions.message(p, msgPrefix + ChatColor.RED + i + "匹に設定しました！");
		} else {
			Actions.message(p, msgPrefix + ChatColor.RED + "値が無効です！");
		}
	}

	/**
	 * 目標スコアを取得
	 */
	public int getScoreLimit() {
		return this.score_limit;
	}

	/**
	 * トップのプレイヤーを取得
	 * 
	 * @return
	 */
	public String[] getTopPlayerNames() {
		int current = 0;
		String topName = "";
		for (Map.Entry entry : this.scores.entrySet()) {
			// 大きい値を持つプレイヤー名をtopNameに代入する
			if (((Integer) entry.getValue()).intValue() > current) {
				current = ((Integer) entry.getValue()).intValue();
				topName = (String) entry.getKey();
			}
			// 同じ値の場合はカンマ切りの文字列とする
			else if (((Integer) entry.getValue()).intValue() == current) {
				topName = topName + "," + (String) entry.getKey();
			}
		}
		String[] topNames = topName.split(",");
		return topNames;
	}

	/**
	 * トップのプレイヤーの文字列を取得
	 */
	public String getTopPlayerName() {
		StringBuilder builder = new StringBuilder();
		for (String str : this.getTopPlayerNames()) {
			builder.append(str).append(", ");
		}
		String topName = builder.substring(0, builder.length() - 2);
		return topName;
	}

	/**
	 * トップのプレイヤーのスコアを取得
	 */
	public int getTopScore() {
		int topScore;
		String[] str = this.getTopPlayerNames().clone();
		if (!str[0].equals("")) {
			topScore = ((Integer) this.scores.get(str[0])).intValue();
			return topScore;
		} else {
			return 0;
		}
	}

	/**
	 * すべてのスコアを降順で表示
	 */
	public void showStatusAll(Player player) {
		// ソート用にListを生成
		List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(
				scores.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> entry1,
					Entry<String, Integer> entry2) {
				return ((Integer) entry2.getValue()).compareTo((Integer) entry1
						.getValue());
			}
		});
		// 内容を表示
		Actions.message(player, "===Fishing Status===");
		for (Entry<String, Integer> s : entries) {
			Actions.message(player, s.getKey() + ": " + s.getValue());
		}
		Actions.message(player, "==================");
	}

	/**
	 * 賞品付与
	 */
	@SuppressWarnings("deprecation")
	public void reward(Player p) {

		// 誰が(score_limit)匹釣り上げたかをブロードキャスト
		Actions.broadcastMessage(msgPrefix + p.getDisplayName() + "が見事魚を"
				+ score_limit + "匹釣り上げました！");

		// シルクタッチのエンチャント本を付与
		// ItemStack reward = new ItemStack(Material.ENCHANTED_BOOK, 1);
		// reward.addEnchantment(Enchantment.SILK_TOUCH, 1);
		// p.getInventory().addItem(reward);

		String reward = "";
		ItemStack item_reward;
		Random random = new Random();
		int rand = random.nextInt(10);
		switch (rand) {
		// シルクタッチ本
		case (0):
			reward = "シルクタッチ本１つ";
			item_reward = new ItemStack(Material.ENCHANTED_BOOK, 1);
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item_reward
					.getItemMeta();
			meta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, true);
			item_reward.setItemMeta(meta);
			p.getInventory().addItem(item_reward);
			break;
		// 粘土ブロック
		case (1):
			reward = "焼き粘土ブロック";
			item_reward = new ItemStack(Material.HARD_CLAY, 64);
			p.getInventory().addItem(item_reward);
			break;
		// 彫りの深い石
		case (2):
			reward = "サークルストーン";
			item_reward = new ItemStack(Material.SMOOTH_BRICK, 16, (short) 3);
			p.getInventory().addItem(item_reward);
			break;
		// 釣り竿
		case (3):
			reward = "釣り竿";
			item_reward = new ItemStack(Material.FISHING_ROD, 1);
			p.getInventory().addItem(item_reward);
			break;
		// 魚
		case (4):
			reward = "生魚";
			item_reward = new ItemStack(Material.RAW_FISH, 64);
			p.getInventory().addItem(item_reward);
			break;
		// チェーンヘルメット
		case (5):
			reward = "チェーンヘルメット";
			item_reward = new ItemStack(Material.CHAINMAIL_HELMET);
			p.getInventory().addItem(item_reward);
			break;
		// チェーンチェストプレート
		case (6):
			reward = "チェーンチェストプレート";
			item_reward = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
			p.getInventory().addItem(item_reward);
			break;
		// チェーンブーツ
		case (7):
			reward = "チェーンブーツ";
			item_reward = new ItemStack(Material.CHAINMAIL_BOOTS);
			p.getInventory().addItem(item_reward);
			break;
		// チェーンレギンス
		case (8):
			reward = "チェーンレギンス";
			item_reward = new ItemStack(Material.CHAINMAIL_LEGGINGS);
			p.getInventory().addItem(item_reward);
			break;
		default:
			reward = "骨";
			item_reward = new ItemStack(Material.BONE, 64);
			p.getInventory().addItem(item_reward);
			break;
		}

		// インベントリを更新（非推奨メソッド）
		p.updateInventory();

		// 釣り上げた人にメッセージ送信
		Actions.message(p, msgPrefix + "賞品として" + reward + "をあなたのインベントリに追加しました！");
	}

	/**
	 * 初期化
	 */
	public void init() {
		// 初期化実行
		this.scores.clear();
		// リセットに関してブロードキャスト
		Actions.broadcastMessage(msgPrefix
				+ "目標数に達した為、今まで釣り上げた魚のカウントをリセットしました！");
		log.info(logPrefix
				+ "Initializing Fishing Data(Not Event)...Succeeded!");
	}

	/* getter */
	/**
	 * コマンドを返す
	 */
	public List<BaseCommand> getCommands() {
		return commands;
	}

	/**
	 * インスタンスを返す
	 * 
	 * @return MotdManagerインスタンス
	 */
	public static FunFishing getInstance() {
		return instance;
	}
}

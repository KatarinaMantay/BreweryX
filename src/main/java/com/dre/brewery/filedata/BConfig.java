package com.dre.brewery.filedata;

import com.dre.brewery.BSealer;
import com.dre.brewery.Brew;
import com.dre.brewery.BreweryPlugin;
import com.dre.brewery.DistortChat;
import com.dre.brewery.MCBarrel;
import com.dre.brewery.api.events.ConfigLoadEvent;
import com.dre.brewery.integration.barrel.BlocklockerBarrel;
import com.dre.brewery.integration.barrel.WGBarrel;
import com.dre.brewery.integration.barrel.WGBarrel5;
import com.dre.brewery.integration.barrel.WGBarrel6;
import com.dre.brewery.integration.barrel.WGBarrel7;
import com.dre.brewery.integration.item.BreweryPluginItem;
import com.dre.brewery.integration.item.ItemsAdderPluginItem;
import com.dre.brewery.integration.item.MMOItemsPluginItem;
import com.dre.brewery.integration.item.OraxenPluginItem;
import com.dre.brewery.integration.item.SlimefunPluginItem;
import com.dre.brewery.recipe.BCauldronRecipe;
import com.dre.brewery.recipe.BRecipe;
import com.dre.brewery.recipe.PluginItem;
import com.dre.brewery.recipe.RecipeItem;
import com.dre.brewery.storage.records.ConfiguredDataManager;
import com.dre.brewery.storage.DataManagerType;
import com.dre.brewery.utility.BUtil;
import com.dre.brewery.utility.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BConfig {

	public static final BreweryPlugin breweryPlugin = BreweryPlugin.getInstance();
	private static final MinecraftVersion VERSION = BreweryPlugin.getMCVersion();

	public static final String configVersion = "3.1";
	public static CommandSender reloader;

	public static boolean updateCheck;
	public static ConfiguredDataManager configuredDataManager;
	public static int autoSaveInterval;


	// Third Party Enabled
	public static boolean useWG; //WorldGuard
	public static WGBarrel wg;
	public static boolean useLWC; //LWC
	public static boolean useLB; //LogBlock
	public static boolean useGP; //GriefPrevention
	public static boolean useTowny; //Towny
	public static boolean useBlocklocker; //LockBlocker
	public static boolean hasVault; // Vault
	public static boolean useCitadel; // CivCraft/DevotedMC Citadel
	public static boolean useGMInventories; // GamemodeInventories
	public static boolean hasSlimefun; // Slimefun
	public static Boolean hasMMOItems = null; // MMOItems ; Null if not checked
	public static boolean hasChestShop;
	public static boolean hasShopKeepers;
	public static boolean hasOraxen;
	public static boolean hasItemsAdder;

	// Barrel
	public static boolean openEverywhere;
	public static boolean loadDataAsync;
	public static boolean virtualChestPerms;
	public static int agingYearDuration;
	public static boolean requireKeywordOnSigns;

	// Cauldron
	public static boolean useOffhandForCauldron;
	public static boolean enableCauldronParticles;
	public static boolean minimalParticles;

	//BPlayer
	public static Map<Material, Integer> drainItems = new HashMap<>();// DrainItem Material and Strength
	public static List<Material> pukeItem;
	public static boolean showStatusOnDrink;
	public static int pukeDespawntime;
	public static float stumbleModifier;
	public static int hangoverTime;
	public static boolean overdrinkKick;
	public static boolean enableHome;
	public static boolean enableLoginDisallow;
	public static boolean enablePuke;
	public static String homeType;
	public static boolean enableWake;

	//Brew
	public static boolean colorInBarrels; // color the Lore while in Barrels
	public static boolean colorInBrewer; // color the Lore while in Brewer
	public static boolean enableEncode;
	public static boolean alwaysShowQuality; // Always show quality stars
	public static boolean alwaysShowAlc; // Always show alc%
	public static boolean showBrewer;
	public static boolean brewHopperDump; // Allow Dumping of Brew liquid into Hoppers

	//Features
	public static boolean craftSealingTable; // Allow Crafting of Sealing Table
	public static boolean enableSealingTable; // Allow Usage of Sealing Table
	public static Material sealingTableBlock;
	public static String pluginPrefix = "&2[BreweryX]&f ";

	//Item
	public static List<RecipeItem> customItems = new ArrayList<>();

	private static boolean createConfigs() {
		File cfg = new File(breweryPlugin.getDataFolder(), "config.yml");
		if (!cfg.exists()) {
			breweryPlugin.log("§1§lNo config.yml found, creating default file! You may want to choose a config according to your language!");
			breweryPlugin.log("§1§lYou can find them in plugins/Brewery/configs/");
			breweryPlugin.log("§1§lJust copy the config for your language into the Brewery folder and /brew reload");
			InputStream defconf = breweryPlugin.getResource("config/" + (VERSION.isOrLater(MinecraftVersion.V1_13) ? "v13/" : "v12/") + "en/config.yml");
			if (defconf == null) {
				breweryPlugin.errorLog("default config file not found, your jarfile may be corrupt. Disabling Brewery!");
				return false;
			}
			try {
				BUtil.saveFile(defconf, breweryPlugin.getDataFolder(), "config.yml", false);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (!cfg.exists()) {
			breweryPlugin.errorLog("default config file could not be copied, your jarfile may be corrupt. Disabling Brewery!");
			return false;
		}

		copyDefaultConfigAndLangs(false);
		return true;
	}

	private static void copyDefaultConfigAndLangs(boolean overwrite) {
		final File configs = new File(breweryPlugin.getDataFolder(), "configs");
		final File languages = new File(breweryPlugin.getDataFolder(), "languages");

		final List<String> configTypes =  new ArrayList<>(List.of("de", "en", "es", "fr", "it", "zh"));
		final List<String> langTypes = new ArrayList<>(List.of("de", "en", "es", "fr", "it", "ru", "tw", "zh"));
		if (VERSION.isOrEarlier(MinecraftVersion.V1_13)) { // not available for some versions according to original author, haven't looked. - Jsinco : 4/1
			configTypes.removeAll(List.of("es", "it", "zh"));
		}

		for (String l : configTypes) {
			try {
				BUtil.saveFile(breweryPlugin.getResource(
						"config/" + (VERSION.isOrLater(MinecraftVersion.V1_13) ? "v13/" : "v12/") + l + "/config.yml"), new File(configs, l), "config.yml", overwrite
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (String type : langTypes) {
			try {
				// Never overwrite languages, they get updated with their updater. - Original Author
				BUtil.saveFile(breweryPlugin.getResource("languages/" + type + ".yml"), languages, type + ".yml", false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

	public static FileConfiguration loadConfigFile() {
		File file = new File(breweryPlugin.getDataFolder(), "config.yml");
		if (!createConfigs()) {
			return null;
		}


		return YamlConfiguration.loadConfiguration(file);
	}

	public static void readConfig(FileConfiguration config) {
		configuredDataManager = new ConfiguredDataManager(
				DataManagerType.valueOf(config.getString("storage.type", "FLATFILE").toUpperCase()),
						config.getString("storage.database", "brewery-data"),
						config.getString("storage.tablePrefix", "brewery_"),
						config.getString("storage.address"),
						config.getString("storage.username"),
						config.getString("storage.password")
				);
		autoSaveInterval = config.getInt("autosave", 3);

		// Set the Language
		breweryPlugin.language = config.getString("language", "en");

		// Load LanguageReader
		breweryPlugin.languageReader = new LanguageReader(new File(breweryPlugin.getDataFolder(), "languages/" + breweryPlugin.language + ".yml"), "languages/" + breweryPlugin.language + ".yml");

		// Has to config still got old materials
		boolean oldMat = config.getBoolean("oldMat", false);

		// Check if config is the newest version
		String version = config.getString("version", null);
		if (version != null) {
			if (!version.equals(configVersion) || (oldMat && VERSION.isOrLater(MinecraftVersion.V1_13))) {
				File file = new File(BreweryPlugin.getInstance().getDataFolder(), "config.yml");
				copyDefaultConfigAndLangs(true);
				new ConfigUpdater(file).update(version, oldMat, breweryPlugin.language, config);
				BreweryPlugin.getInstance().log("Config Updated to version: " + configVersion);
				config = YamlConfiguration.loadConfiguration(file);
			}
		}

		// If the Update Checker should be enabled
		updateCheck = config.getBoolean("updateCheck", false);

		PluginManager plMan = breweryPlugin.getServer().getPluginManager();

		// Third-Party
		useWG = config.getBoolean("useWorldGuard", true) && plMan.isPluginEnabled("WorldGuard");
		useLWC = config.getBoolean("useLWC", true) && plMan.isPluginEnabled("LWC");
		useTowny = config.getBoolean("useTowny", true) && plMan.isPluginEnabled("Towny");
		useGP = config.getBoolean("useGriefPrevention", true) && plMan.isPluginEnabled("GriefPrevention");
		useLB = config.getBoolean("useLogBlock", false) && plMan.isPluginEnabled("LogBlock");
		useGMInventories = config.getBoolean("useGMInventories", false);
		useCitadel = config.getBoolean("useCitadel", false) && plMan.isPluginEnabled("Citadel");
		useBlocklocker = config.getBoolean("useBlockLocker", false) && plMan.isPluginEnabled("BlockLocker");
		virtualChestPerms = config.getBoolean("useVirtualChestPerms", false);
		// The item util has been removed in Vault 1.7+
		hasVault = plMan.isPluginEnabled("Vault")
			&& Integer.parseInt(plMan.getPlugin("Vault").getDescription().getVersion().split("\\.")[1]) <= 6;
		hasChestShop = plMan.isPluginEnabled("ChestShop");
		hasShopKeepers = plMan.isPluginEnabled("Shopkeepers");
		hasSlimefun = plMan.isPluginEnabled("Slimefun");
		hasOraxen = plMan.isPluginEnabled("Oraxen");
		hasItemsAdder = plMan.isPluginEnabled("ItemsAdder");

		// various Settings
		BreweryPlugin.debug = config.getBoolean("debug", false);
		pukeItem = !config.getStringList("pukeItem").isEmpty() ? config.getStringList("pukeItem").stream().map(BUtil::getMaterialSafely).collect(Collectors.toList())
				: List.of(BUtil.getMaterialSafely(config.getString("pukeItem"))); //Material.matchMaterial(config.getString("pukeItem", "SOUL_SAND"));
		hangoverTime = config.getInt("hangoverDays", 0) * 24 * 60;
		overdrinkKick = config.getBoolean("enableKickOnOverdrink", false);
		enableHome = config.getBoolean("enableHome", false);
		enableLoginDisallow = config.getBoolean("enableLoginDisallow", false);
		enablePuke = config.getBoolean("enablePuke", false);
		pukeDespawntime = config.getInt("pukeDespawntime", 60) * 20;
		stumbleModifier = ((float) config.getInt("stumblePercent", 100)) / 100f;
		showStatusOnDrink = config.getBoolean("showStatusOnDrink", false);
		homeType = config.getString("homeType", null);
		enableWake = config.getBoolean("enableWake", false);
		craftSealingTable = config.getBoolean("craftSealingTable", false);
		enableSealingTable = config.getBoolean("enableSealingTable", false);
		sealingTableBlock = Material.matchMaterial(config.getString("sealingTableBlock", "SMOKER"));
		pluginPrefix = config.getString("pluginPrefix", "&2[Brewery]&f ");
		colorInBarrels = config.getBoolean("colorInBarrels", false);
		colorInBrewer = config.getBoolean("colorInBrewer", false);
		alwaysShowQuality = config.getBoolean("alwaysShowQuality", false);
		alwaysShowAlc = config.getBoolean("alwaysShowAlc", false);
		showBrewer = config.getBoolean("showBrewer", false);
		enableEncode = config.getBoolean("enableEncode", false);
		openEverywhere = config.getBoolean("openLargeBarrelEverywhere", false);
		enableCauldronParticles = VERSION.isOrLater(MinecraftVersion.V1_9) && config.getBoolean("enableCauldronParticles", false);
		minimalParticles = config.getBoolean("minimalParticles", false);
		useOffhandForCauldron = config.getBoolean("useOffhandForCauldron", false);
		loadDataAsync = config.getBoolean("loadDataAsync", true);
		brewHopperDump = config.getBoolean("brewHopperDump", false);
		agingYearDuration = config.getInt("agingYearDuration", 20);
		requireKeywordOnSigns = config.getBoolean("requireKeywordOnSigns", true);

		if (VERSION.isOrLater(MinecraftVersion.V1_14)) {
			MCBarrel.maxBrews = config.getInt("maxBrewsInMCBarrels", 6);
			MCBarrel.enableAging = config.getBoolean("ageInMCBarrels", true);
		}

		Brew.loadSeed(config, new File(BreweryPlugin.getInstance().getDataFolder(), "config.yml"));

		if (VERSION.isOrEarlier(MinecraftVersion.V1_13)) {
			// world.getBlockAt loads Chunks in 1.12 and lower. Can't load async
			loadDataAsync = false;
		}

		PluginItem.registerForConfig("brewery", BreweryPluginItem::new);
		PluginItem.registerForConfig("mmoitems", MMOItemsPluginItem::new);
		PluginItem.registerForConfig("slimefun", SlimefunPluginItem::new);
		PluginItem.registerForConfig("exoticgarden", SlimefunPluginItem::new);
		PluginItem.registerForConfig("oraxen", OraxenPluginItem::new);
		PluginItem.registerForConfig("itemsadder", ItemsAdderPluginItem::new);

		// Loading custom items
		ConfigurationSection configSection = config.getConfigurationSection("customItems");
		if (configSection != null) {
			for (String custId : configSection.getKeys(false)) {
				RecipeItem custom = RecipeItem.fromConfigCustom(configSection, custId);
				if (custom != null) {
					custom.makeImmutable();
					customItems.add(custom);
				} else {
					breweryPlugin.errorLog("Loading the Custom Item with id: '" + custId + "' failed!");
				}
			}
		}

		// loading recipes
		configSection = config.getConfigurationSection("recipes");
		if (configSection != null) {
			List<BRecipe> configRecipes = BRecipe.getConfigRecipes();
			for (String recipeId : configSection.getKeys(false)) {
				BRecipe recipe = BRecipe.fromConfig(configSection, recipeId);
				if (recipe != null && recipe.isValid()) {
					configRecipes.add(recipe);
				} else {
					breweryPlugin.errorLog("Loading the Recipe with id: '" + recipeId + "' failed!");
				}
			}
			BRecipe.numConfigRecipes = configRecipes.size();
		}

		// Loading Cauldron Recipes
		configSection = config.getConfigurationSection("cauldron");
		if (configSection != null) {
			List<BCauldronRecipe> configRecipes = BCauldronRecipe.getConfigRecipes();
			for (String id : configSection.getKeys(false)) {
				BCauldronRecipe recipe = BCauldronRecipe.fromConfig(configSection, id);
				if (recipe != null) {
					configRecipes.add(recipe);
				} else {
					breweryPlugin.errorLog("Loading the Cauldron-Recipe with id: '" + id + "' failed!");
				}
			}
			BCauldronRecipe.numConfigRecipes = configRecipes.size();
		}

		// Recalculating Cauldron-Accepted Items for non-config recipes
		for (BRecipe recipe : BRecipe.getAddedRecipes()) {
			recipe.updateAcceptedLists();
		}
		for (BCauldronRecipe recipe : BCauldronRecipe.getAddedRecipes()) {
			recipe.updateAcceptedLists();
		}

		// loading drainItems
		List<String> drainList = config.getStringList("drainItems");
        for (String drainString : drainList) {
            String[] drainSplit = drainString.split("/");
            if (drainSplit.length > 1) {
                Material mat = BUtil.getMaterialSafely(drainSplit[0]);
                int strength = breweryPlugin.parseInt(drainSplit[1]);
                if (mat == null && hasVault && strength > 0) {
                    try {
                        net.milkbowl.vault.item.ItemInfo vaultItem = net.milkbowl.vault.item.Items.itemByString(drainSplit[0]);
                        if (vaultItem != null) {
                            mat = vaultItem.getType();
                        }
                    } catch (Exception e) {
                        BreweryPlugin.getInstance().errorLog("Could not check vault for Item Name");
                        e.printStackTrace();
                    }
                }
                if (mat != null && strength > 0) {
                    drainItems.put(mat, strength);
                }
            }
        }

        // Loading Words
		DistortChat.words = new ArrayList<>();
		DistortChat.ignoreText = new ArrayList<>();
		if (config.getBoolean("enableChatDistortion", false)) {
			for (Map<?, ?> map : config.getMapList("words")) {
				new DistortChat(map);
			}
			for (String bypass : config.getStringList("distortBypass")) {
				DistortChat.ignoreText.add(bypass.split(","));
			}
			DistortChat.commands = config.getStringList("distortCommands");
		}
		DistortChat.log = config.getBoolean("logRealChat", false);
		DistortChat.doSigns = config.getBoolean("distortSignText", false);

		// Register Sealing Table Recipe
		if (VERSION.isOrLater(MinecraftVersion.V1_14)) {
			if (craftSealingTable && !BSealer.recipeRegistered) {
				BSealer.registerRecipe();
			} else if (!craftSealingTable && BSealer.recipeRegistered) {
				BSealer.unregisterRecipe();
			}
		}

		if (useWG) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
			if (plugin != null) {
				String wgv = plugin.getDescription().getVersion();
				if (wgv.startsWith("6.")) {
					wg = new WGBarrel6();
				} else if (wgv.startsWith("5.")) {
					wg = new WGBarrel5();
				} else {
					wg = new WGBarrel7();
				}
			}
			if (wg == null) {
				BreweryPlugin.getInstance().errorLog("Failed loading WorldGuard Integration! Opening Barrels will NOT work!");
				BreweryPlugin.getInstance().errorLog("Brewery was tested with version 5.8, 6.1 and 7.0 of WorldGuard!");
				BreweryPlugin.getInstance().errorLog("Disable the WorldGuard support in the config and do /brew reload");
			}
		}
		if (useBlocklocker) {
			try {
				Class.forName("nl.rutgerkok.blocklocker.BlockLockerAPIv2");
				Class.forName("nl.rutgerkok.blocklocker.ProtectableBlocksSettings");
				BlocklockerBarrel.registerBarrelAsProtectable();
			} catch (ClassNotFoundException e) {
				useBlocklocker = false;
				BreweryPlugin.getInstance().log("Unsupported Version of 'BlockLocker', locking Brewery Barrels disabled");
			}
		}

		// The Config was reloaded, call Event
		ConfigLoadEvent event = new ConfigLoadEvent();
		BreweryPlugin.getInstance().getServer().getPluginManager().callEvent(event);


	}
}

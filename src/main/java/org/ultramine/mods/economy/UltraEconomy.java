package org.ultramine.mods.economy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.ultramine.core.economy.Currency;
import org.ultramine.core.economy.service.EconomyRegistry;
import org.ultramine.core.service.InjectService;
import org.ultramine.mods.economy.commands.EconomyHoldingsCommands;
import org.ultramine.server.ConfigurationHandler;

@Mod(modid = "UM-Economy", name = "UM-Economy", version = "@version@", acceptableRemoteVersions = "*")
public class UltraEconomy
{
	@InjectService private static EconomyRegistry economyRegistry;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		Currency currency = economyRegistry.registerCurrency("GSC", "dollar", "dollars", "$", 2, 0).getValue();
		economyRegistry.registerStartPlayerBalance(currency, ConfigurationHandler.getServerConfig().tools.economy.startBalance);
	}

	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerCommands(EconomyHoldingsCommands.class);
	}
}

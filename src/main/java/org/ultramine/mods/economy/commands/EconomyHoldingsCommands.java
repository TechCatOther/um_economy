package org.ultramine.mods.economy.commands;

import org.ultramine.commands.Command;
import org.ultramine.commands.CommandContext;
import org.ultramine.core.economy.Currency;
import org.ultramine.core.economy.account.Account;
import org.ultramine.core.economy.holdings.Holdings;
import org.ultramine.core.economy.service.DefaultCurrencyService;
import org.ultramine.core.economy.service.Economy;
import org.ultramine.core.economy.service.EconomyRegistry;
import org.ultramine.core.service.InjectService;
import org.ultramine.server.util.GlobalExecutors;

import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;
import static net.minecraft.util.EnumChatFormatting.GREEN;

public class EconomyHoldingsCommands
{
	@InjectService private static EconomyRegistry economyRegistry;
	@InjectService private static Economy economy;
	@InjectService private static DefaultCurrencyService defaultCurrency;

	@Command(
			name = "pay",
			aliases = {"mpay", "madd"},
			group = "economy",
			permissions = {"command.economy.pay"},
			syntax = {
					"<player> <amount>",
					"<player> <currency> <amount>"
			}
	)
	public static void pay(CommandContext ctx)
	{
		Currency cur = ctx.contains("currency") ? economyRegistry.getCurrency(ctx.get("currency").asString()) : defaultCurrency.getDefaultCurrency();
		Holdings from = ctx.getSenderAsPlayer().getAccount().getHoldings(cur);
		double amount = ctx.get("amount").asDouble();
		ctx.finishAfter(from.asAsync().transfer(ctx.get("player").asAccount(), amount).thenRunAsync(() -> {
			ctx.sendMessage(DARK_GREEN, GREEN, "command.pay.sended", ctx.get("player").asString(), cur.format(amount));
			ctx.get("player").asOfflinePlayer().sendMessage(DARK_GREEN, GREEN, "command.pay.received", cur.format(amount), ctx.getSenderAsPlayer().func_145748_c_());
		}, GlobalExecutors.syncServer()));
	}

	@Command(
			name = "money",
			group = "economy",
			permissions = {"command.economy.money", "command.economy.money.other"},
			syntax = {
					"",
					"<player>",
					"<player> <currency>"
			}
	)
	public static void money(CommandContext ctx)
	{
		ctx.checkPermissionIfArg("player", "command.economy.money.other", "command.money.other.fail.perm");
		Currency cur = ctx.contains("currency") ? economyRegistry.getCurrency(ctx.get("currency").asString()) : defaultCurrency.getDefaultCurrency();
		Holdings holdings = (ctx.contains("player") ? ctx.get("player").asAccount() : ctx.getSenderAsPlayer().getAccount()).getHoldings(cur);
		ctx.finishAfter(holdings.asAsync().getBalance().thenAcceptAsync(balance -> {
			ctx.sendMessage(DARK_GREEN, GREEN, "command.money.info", holdings.getAccount().getName(), cur.format(balance));
		}, GlobalExecutors.syncServer()));
	}

	@Command(
			name = "msub",
			aliases = {"msubtract"},
			group = "economy",
			permissions = {"command.economy.msub"},
			syntax = {
					"<player> <amount>",
					"<player> <currency> <amount>"
			}
	)
	public static void msub(CommandContext ctx)
	{
		Currency cur = ctx.contains("currency") ? economyRegistry.getCurrency(ctx.get("currency").asString()) : defaultCurrency.getDefaultCurrency();
		Holdings from = ctx.getSenderAsPlayer().getAccount().getHoldings(cur);
		Account to = ctx.get("player").asAccount();
		double amount = ctx.get("amount").asDouble();
		ctx.finishAfter(from.asAsync().transferUnchecked(to, amount).thenRunAsync(() -> {
			ctx.sendMessage(DARK_GREEN, GREEN, "command.msub.sended", cur.format(amount), ctx.get("player").asString());
			ctx.get("player").asOfflinePlayer().sendMessage(DARK_GREEN, GREEN, "command.msub.received", cur.format(amount));
		}, GlobalExecutors.syncServer()));
	}

	@Command(
			name = "mgive",
			group = "economy",
			permissions = {"command.economy.mgive"},
			syntax = {
					"<player> <amount>",
					"<player> <currency> <amount>"
			}
	)
	public static void mgive(CommandContext ctx)
	{
		Currency cur = ctx.contains("currency") ? economyRegistry.getCurrency(ctx.get("currency").asString()) : defaultCurrency.getDefaultCurrency();
		Holdings holdings = ctx.get("player").asAccount().getHoldings(cur);
		double amount = ctx.get("amount").asDouble();
		ctx.finishAfter(holdings.asAsync().deposit(amount).thenRunAsync(() -> {
			ctx.sendMessage(DARK_GREEN, GREEN, "command.mgive.sended", ctx.get("player").asString(), cur.format(amount));
			ctx.get("player").asOfflinePlayer().sendMessage(DARK_GREEN, GREEN, "command.mgive.received", cur.format(amount));
		}, GlobalExecutors.syncServer()));
	}

	@Command(
			name = "mset",
			group = "economy",
			permissions = {"command.economy.mset"},
			syntax = {
					"<player> <amount>",
					"<player> <currency> <amount>"
			}
	)
	public static void mset(CommandContext ctx)
	{
		Currency cur = ctx.contains("currency") ? economyRegistry.getCurrency(ctx.get("currency").asString()) : defaultCurrency.getDefaultCurrency();
		Holdings holdings = ctx.get("player").asAccount().getHoldings(cur);
		double amount = ctx.get("amount").asDouble(); //may be negative
		ctx.finishAfter(holdings.asAsync().setBalance(amount).thenAcceptAsync(last -> {
			ctx.sendMessage(DARK_GREEN, GREEN, "command.mset.sended", ctx.get("player").asString(), cur.format(last), cur.format(amount));
			ctx.get("player").asOfflinePlayer().sendMessage(DARK_GREEN, GREEN, "command.mset.received", cur.format(last), cur.format(amount));
		}, GlobalExecutors.syncServer()));
	}
}

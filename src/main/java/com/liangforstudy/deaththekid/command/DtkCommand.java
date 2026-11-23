package com.liangforstudy.deaththekid.command;

import com.liangforstudy.deaththekid.DeathTheKid;
import com.liangforstudy.deaththekid.data.KidBalance;
import com.liangforstudy.deaththekid.data.KidCapabilities;
import com.liangforstudy.deaththekid.data.KidData;
import com.liangforstudy.deaththekid.data.StyleRank;
import com.liangforstudy.deaththekid.network.ModNetwork;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * /dtk debug command for testing (operators only). Edits the running player's KidData.
 *
 * <pre>
 * /dtk show
 * /dtk mana &lt;0-1000&gt;          /dtk maxmana &lt;100-1000&gt;
 * /dtk rank &lt;D|C|B|A|S&gt;      /dtk combo &lt;n&gt;
 * /dtk lines &lt;0-3&gt;           /dtk symmetry &lt;-100..100&gt;
 * /dtk pistol &lt;liz|patty|both&gt; &lt;0-100&gt;
 * /dtk meister &lt;true|false&gt;  /dtk contracted &lt;true|false&gt;
 * /dtk reset
 * </pre>
 */
@Mod.EventBusSubscriber(modid = DeathTheKid.MOD_ID)
public final class DtkCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> rank = Commands.literal("rank");
        for (StyleRank r : StyleRank.values()) {
            rank.then(Commands.literal(r.name()).executes(ctx -> edit(ctx, d -> d.setRank(r))));
        }

        LiteralArgumentBuilder<CommandSourceStack> pistol = Commands.literal("pistol");
        for (String which : new String[]{"liz", "patty", "both"}) {
            pistol.then(Commands.literal(which)
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, (int) KidBalance.PISTOL_MAX_HEALTH))
                            .executes(ctx -> {
                                int value = IntegerArgumentType.getInteger(ctx, "value");
                                return edit(ctx, d -> {
                                    if (!which.equals("patty")) d.setLizHealth(value);
                                    if (!which.equals("liz")) d.setPattyHealth(value);
                                });
                            })));
        }

        dispatcher.register(Commands.literal("dtk")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("show").executes(ctx -> show(ctx.getSource())))
                .then(Commands.literal("mana")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, KidBalance.MANA_HARD_CAP))
                                .executes(ctx -> edit(ctx, d -> d.setMana(IntegerArgumentType.getInteger(ctx, "value"))))))
                .then(Commands.literal("maxmana")
                        .then(Commands.argument("value", IntegerArgumentType.integer(KidBalance.BASE_MAX_MANA, KidBalance.MANA_HARD_CAP))
                                .executes(ctx -> edit(ctx, d -> d.setMaxMana(IntegerArgumentType.getInteger(ctx, "value"))))))
                .then(rank)
                .then(Commands.literal("combo")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(ctx -> edit(ctx, d -> d.setCombo(IntegerArgumentType.getInteger(ctx, "value"))))))
                .then(Commands.literal("lines")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, KidBalance.MAX_SANZU_LINES))
                                .executes(ctx -> edit(ctx, d -> d.setSanzuLines(IntegerArgumentType.getInteger(ctx, "value"))))))
                .then(Commands.literal("symmetry")
                        .then(Commands.argument("value", IntegerArgumentType.integer(-100, 100))
                                .executes(ctx -> edit(ctx, d -> d.setSymmetry(IntegerArgumentType.getInteger(ctx, "value"))))))
                .then(pistol)
                .then(Commands.literal("meister")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> edit(ctx, d -> d.setMeisterMode(BoolArgumentType.getBool(ctx, "value"))))))
                .then(Commands.literal("contracted")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> edit(ctx, d -> d.setContracted(BoolArgumentType.getBool(ctx, "value"))))))
                .then(Commands.literal("reset").executes(ctx -> edit(ctx, d -> d.load(new KidData().save())))));
    }

    private static int edit(CommandContext<CommandSourceStack> ctx, Consumer<KidData> change) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Optional<KidData> data = KidCapabilities.get(player);
        if (data.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No Death the Kid data on this player."));
            return 0;
        }
        change.accept(data.get());
        ModNetwork.syncKidData(player, data.get());
        return show(ctx.getSource());
    }

    private static int show(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Optional<KidData> maybe = KidCapabilities.get(player);
        if (maybe.isEmpty()) {
            source.sendFailure(Component.literal("No Death the Kid data on this player."));
            return 0;
        }
        KidData d = maybe.get();
        String text = String.format(
                "Mana %.0f/%d | Souls %d | Sanzu %d/%d | Rank %s (combo %d) | Symmetry %.0f\n"
                        + "Liz %.0f%% | Patty %.0f%% | Meister %s | Contracted %s",
                d.getMana(), d.getMaxMana(), d.getSoulsAbsorbed(), d.getSanzuLines(), KidBalance.MAX_SANZU_LINES,
                d.getRank(), d.getCombo(), d.getSymmetry(),
                d.getLizHealth(), d.getPattyHealth(), d.isMeisterMode() ? "on" : "off", d.isContracted() ? "yes" : "no");
        source.sendSuccess(() -> Component.literal(text), false);
        return 1;
    }

    private DtkCommand() {}
}

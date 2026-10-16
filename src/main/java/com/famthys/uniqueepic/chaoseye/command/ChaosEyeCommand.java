package com.famthys.uniqueepic.chaoseye.command;

import com.famthys.uniqueepic.chaoseye.ChaosEyeAttachments;
import com.famthys.uniqueepic.chaoseye.ChaosEyeConfig;
import com.famthys.uniqueepic.chaoseye.ChaosEyeState;
import com.famthys.uniqueepic.chaoseye.Emotion;
import com.famthys.uniqueepic.chaoseye.EmotionEngine;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * 调试命令 {@code /chaoseye}。
 *
 * <p>只对玩家生效。P1 是开发阶段，故意做得很直接——方便你在游戏里把每种情绪都手动演一遍。</p>
 */
public final class ChaosEyeCommand {

    /** 手动指定情绪时给多长时长。 */
    private static final int DEBUG_EMOTION_TICKS = 30 * 20;

    private static final String USAGE = "可用值：none / pleased / bored / excited / disappointed / furious";

    private ChaosEyeCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext context,
                                Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("chaoseye")
                .requires(source -> source.getPlayer() != null)
                .then(Commands.literal("status").executes(ChaosEyeCommand::status))
                .then(Commands.literal("infect").executes(ChaosEyeCommand::infect))
                .then(Commands.literal("cure").executes(ChaosEyeCommand::cure))
                .then(Commands.literal("reset").executes(ChaosEyeCommand::reset))
                .then(Commands.literal("emotion")
                        .then(Commands.argument("value", StringArgumentType.word())
                                .executes(ChaosEyeCommand::setEmotion))));
    }

    /* ---------------- 子命令 ---------------- */

    private static int status(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ChaosEyeState state = ChaosEyeAttachments.get(player);
        long gameTime = player.level().getGameTime();

        feedback(ctx, "===== 混沌之眸 =====");
        feedback(ctx, "寄生：" + (state.infected ? "是" : "否")
                + (state.cured ? "（玩家已主动拒绝）" : ""));
        feedback(ctx, Component.literal("情绪：")
                .append(Component.translatable(state.emotion.translationKey()))
                .append(Component.literal("（" + state.emotion.id() + "）  剩余 " + state.emotionTicks + " tick")));
        feedback(ctx, "混沌能量：" + state.energy + " / 100");
        feedback(ctx, "累计死亡：" + state.deaths + " 次");
        feedback(ctx, "最近受伤：" + ago(gameTime, state.lastHurtAt)
                + "　最近还手：" + ago(gameTime, state.lastDealtAt)
                + "　最近击杀：" + ago(gameTime, state.lastKillAt));
        feedback(ctx, "恶作剧开关：" + (ChaosEyeConfig.pranksEnabled ? "开" : "关"));
        return 1;
    }

    private static int infect(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ChaosEyeState state = ChaosEyeAttachments.get(player);
        long gameTime = player.level().getGameTime();

        state.infected = true;
        state.cured = false;
        state.infectedAt = gameTime;
        state.clearCombatMemory(gameTime);

        feedback(ctx, "已强制寄生混沌之眸。");
        return 1;
    }

    private static int cure(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ChaosEyeState state = ChaosEyeAttachments.get(player);
        long gameTime = player.level().getGameTime();

        state.infected = false;
        state.cured = true;
        state.clearCombatMemory(gameTime);

        // 不主动清空玩家身上的状态效果：那是玩家自己的药水，一并抹掉太粗暴。
        feedback(ctx, "已卸除混沌之眸。在本存档内不会再被寄生（重开世界也保留）。");
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ChaosEyeState state = ChaosEyeAttachments.get(player);
        long gameTime = player.level().getGameTime();

        state.cured = false;
        state.clearCombatMemory(gameTime);
        state.energy = 0;
        state.deaths = 0;

        feedback(ctx, "情绪、能量、战斗记忆已重置。");
        return 1;
    }

    private static int setEmotion(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String raw = StringArgumentType.getString(ctx, "value");
        Emotion emotion = Emotion.byId(raw);

        if (emotion == Emotion.NONE && !Emotion.NONE.id().equals(raw)) {
            ctx.getSource().sendFailure(Component.literal("未知情绪：" + raw + "。" + USAGE));
            return 0;
        }

        ChaosEyeState state = ChaosEyeAttachments.get(player);
        state.infected = true;
        state.cured = false;
        long gameTime = player.level().getGameTime();

        EmotionEngine.setEmotion(player, state, emotion, DEBUG_EMOTION_TICKS, gameTime, false);

        feedback(ctx, Component.literal("情绪已设为：")
                .append(Component.translatable(emotion.translationKey()))
                .append(Component.literal("，持续 " + DEBUG_EMOTION_TICKS + " tick。")));
        return 1;
    }

    /* ---------------- 工具 ---------------- */

    private static void feedback(CommandContext<CommandSourceStack> ctx, String text) {
        feedback(ctx, Component.literal(text));
    }

    private static void feedback(CommandContext<CommandSourceStack> ctx, Component text) {
        ctx.getSource().sendSuccess(() -> text, false);
    }

    private static String ago(long gameTime, long then) {
        if (then <= 0L) {
            return "从未";
        }
        long delta = Math.max(0L, gameTime - then);
        return delta + " tick 前";
    }
}

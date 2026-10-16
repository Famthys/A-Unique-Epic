package com.famthys.uniqueepic.chaoseye.dialogue;

import com.famthys.uniqueepic.chaoseye.ChaosEyeConfig;
import com.famthys.uniqueepic.chaoseye.ChaosEyeState;
import com.famthys.uniqueepic.chaoseye.Emotion;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

/**
 * 混沌之眸的台词调度。
 *
 * <p>台词文本放在语言文件里（{@code assets/aunique-epic/lang/zh_cn.json}），代码只负责挑一句发出去。
 * 这样改文案不用重新编译，后续也能直接换成数据包驱动。</p>
 */
public final class EyeDialogue {

    private EyeDialogue() {
    }

    /** 每种情绪各有几句台词。想加句子就同时改这里和语言文件。 */
    private static final Map<Emotion, Integer> LINE_COUNTS = Map.of(
            Emotion.PLEASED, 3,
            Emotion.BORED, 3,
            Emotion.EXCITED, 3,
            Emotion.DISAPPOINTED, 3,
            Emotion.FURIOUS, 3
    );

    /**
     * 随机说一句当前情绪的台词。
     *
     * @param force 忽略刷屏冷却（命令调试用）
     */
    public static void say(ServerPlayer player, ChaosEyeState state, Emotion emotion, long gameTime, boolean force) {
        Integer count = LINE_COUNTS.get(emotion);
        if (count == null || count <= 0) {
            return;
        }
        if (!force && gameTime - state.lastDialogueAt < ChaosEyeConfig.dialogueCooldownTicks) {
            return;
        }

        int index = player.getRandom().nextInt(count);
        state.lastDialogueAt = gameTime;
        send(player, Component.translatable(emotion.lineKey(index)));
    }

    /** 寄生开场白。 */
    public static void sayInfect(ServerPlayer player) {
        send(player, Component.translatable("chaos_eye.line.infect"));
    }

    private static void send(ServerPlayer player, Component text) {
        player.sendSystemMessage(text.copy().withStyle(ChatFormatting.DARK_PURPLE));
    }
}

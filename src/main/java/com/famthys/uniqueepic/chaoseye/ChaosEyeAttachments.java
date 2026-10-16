package com.famthys.uniqueepic.chaoseye;

import com.famthys.uniqueepic.AUniqueEpic;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerPlayer;

/**
 * 混沌之眸的数据附着定义。
 *
 * <p>为什么用 attachment 而不是物品：设定要求"不可移除、不可丢弃、死亡不掉落、首次进世界强制寄生"。
 * 挂在玩家实体上的数据附着天生满足这四条，而做成物品就得拦住玩家丢弃、还得占一个背包格子。</p>
 *
 * <p>{@code copyOnDeath()} 直接实现了"死亡不掉落"——死亡重生时状态会被复制到新玩家实体上。</p>
 */
public final class ChaosEyeAttachments {

    private ChaosEyeAttachments() {
    }

    public static final AttachmentType<ChaosEyeState> CHAOS_EYE = AttachmentRegistry
            .<ChaosEyeState>create(AUniqueEpic.id("chaos_eye"), builder -> builder
                    .persistent(ChaosEyeState.CODEC)
                    .copyOnDeath());

    /** 触发一次静态初始化（类加载时就会注册，这里只是让调用点显式一点）。 */
    public static void init() {
        AUniqueEpic.LOGGER.debug("[混沌之眸] attachment 已注册: {}", AUniqueEpic.id("chaos_eye"));
    }

    /**
     * 取玩家身上的混沌之眸状态，没有就创建一份。
     *
     * <p>注意这里的强转不是多余的：{@code AttachmentTarget} 是 Fabric 在运行时通过 mixin
     * 注入到 {@code Entity} 上的接口，编译期的 Minecraft jar 里 {@code Entity} 并没有声明它，
     * 所以直接调 {@code player.getAttachedOrCreate(...)} 编译不过，必须显式转成接口类型。</p>
     */
    public static ChaosEyeState get(ServerPlayer player) {
        return ((AttachmentTarget) player).getAttachedOrCreate(CHAOS_EYE, ChaosEyeState::new);
    }

    /** 只读取，不创建。 */
    public static ChaosEyeState peek(ServerPlayer player) {
        return ((AttachmentTarget) player).getAttached(CHAOS_EYE);
    }

    /** 是否已经被寄生。 */
    public static boolean isInfected(ServerPlayer player) {
        ChaosEyeState state = peek(player);
        return state != null && state.infected;
    }
}

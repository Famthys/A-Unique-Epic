package com.famthys.uniqueepic.client.boss;

import com.famthys.uniqueepic.boss.CrimsonCombat;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * 猩红骑的渲染状态。
 *
 * <p>按交付说明第 4 步：渲染状态里保存「阶段 + 动画时间」，模型 {@code setupAnim} 据此驱动关键帧。</p>
 */
public class CrimsonKnightRenderState extends LivingEntityRenderState {

    /** 当前阶段（攻击中 / 死亡中 / 其他）。 */
    public CrimsonCombat.Phase crimsonPhase = CrimsonCombat.Phase.IDLE;

    /** 动画已播放秒数 = (客户端游戏刻 - 阶段开始刻 + partialTick) / 20。 */
    public float crimsonAnimSeconds;
}

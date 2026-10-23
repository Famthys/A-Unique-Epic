package com.famthys.uniqueepic.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 猩红骑（Crimson Knight）。
 *
 * <p>战斗节奏不交给原版 {@code MeleeAttackGoal}，而是交给交付的
 * {@link CrimsonCombatBridge} 状态机驱动——它自带测试（追击、卡视线不起手、
 * 第 18 tick 精确命中、锁朝向、冷却、闪避、死亡抢占命中）。</p>
 *
 * <p><b>绝不能同时挂 MeleeAttackGoal</b>：两者会争夺移动控制，并造成重复伤害。</p>
 */
public class CrimsonKnight extends Monster {

    /* ==================== 规格数值 ==================== */

    public static final double MAX_HEALTH = 520.0D;
    public static final double ATTACK_DAMAGE = 50.0D;
    public static final double ARMOR = 10.0D;
    public static final double ARMOR_TOUGHNESS = 6.0D;

    /** 死亡动画播完才移除；和核心里的 DEATH_TICKS 必须一致。 */
    private static final int DEATH_REMOVE_TICK = CrimsonCombat.DEATH_TICKS;

    /* ==================== 同步到客户端的动画状态 ==================== */

    /** 当前阶段序号，客户端据此决定播哪段动画。 */
    private static final EntityDataAccessor<Integer> DATA_PHASE =
            SynchedEntityData.defineId(CrimsonKnight.class, EntityDataSerializers.INT);
    /** 阶段开始的游戏刻；客户端算 动画时间 = (当前时间 - 开始时间 + partialTick) / 20。 */
    private static final EntityDataAccessor<Long> DATA_PHASE_START =
            SynchedEntityData.defineId(CrimsonKnight.class, EntityDataSerializers.LONG);

    /* ==================== 战斗桥 ==================== */

    /** 每个实体一份，不可静态共享（否则多只 Boss 会串状态）。 */
    private final CrimsonCombatBridge bridge = new CrimsonCombatBridge();

    private final CrimsonCombatBridge.Hooks hooks = new CrimsonCombatBridge.Hooks() {
        @Override
        public void phaseChanged(CrimsonCombat.Phase phase, long startGameTick) {
            // 只在阶段切换时同步，省流量
            entityData.set(DATA_PHASE, phase.ordinal());
            entityData.set(DATA_PHASE_START, startGameTick);
        }

        @Override
        public void deathFinished() {
            // 这里不做移除：真正的移除交给 tickDeath()，避免和原版死亡流程打架
        }
    };

    public CrimsonKnight(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 500;
    }

    /** 属性表：血量/伤害/护甲/韧性按规格，其余为让近战 Boss 站得住的常规值。 */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PHASE, CrimsonCombat.Phase.IDLE.ordinal());
        builder.define(DATA_PHASE_START, 0L);
    }

    @Override
    protected void registerGoals() {
        // 只负责"选目标"和"别淹死"；移动与出手全部由 bridge 决定
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /* ==================== 服务端 tick ==================== */

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.isAlive()) {
            bridge.tick(this, hooks);
        }
    }

    @Override
    public void tick() {
        super.tick();
        // 死亡后原版不再跑 AI，但死亡动画还要继续推进——必须手动喂 bridge
        if (!this.level().isClientSide() && this.isDeadOrDying()) {
            bridge.tick(this, hooks);
        }
    }

    /**
     * 延长尸体停留时间。
     *
     * <p>原版是 20 tick 就移除；这里拖到 {@link CrimsonCombat#DEATH_TICKS}（52 tick），
     * 让 2.6 秒的死亡动作播完。</p>
     */
    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= DEATH_REMOVE_TICK && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    /* ==================== 客户端读取动画状态 ==================== */

    /** 客户端用：当前阶段。 */
    public CrimsonCombat.Phase clientPhase() {
        int i = this.entityData.get(DATA_PHASE);
        CrimsonCombat.Phase[] all = CrimsonCombat.Phase.values();
        return i >= 0 && i < all.length ? all[i] : CrimsonCombat.Phase.IDLE;
    }

    /** 客户端用：阶段开始的游戏刻。 */
    public long clientPhaseStart() {
        return this.entityData.get(DATA_PHASE_START);
    }

    /* ==================== 常规 ==================== */

    @Override
    public boolean fireImmune() {
        return false;
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect) {
        return super.canBeAffected(effect);
    }

    /** 让 Boss 不要被普通击退推来推去（数值在属性里，这里只是留个口子）。 */
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
        // 不推别人，避免把玩家顶进墙里
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    /** 供调试命令查看当前阶段。 */
    public CrimsonCombat.Phase serverPhase() {
        return bridge.phase();
    }

    public int serverPhaseTick() {
        return bridge.phaseTick();
    }

    /** 目标是否有效（给 bridge 之外的地方复用）。 */
    public LivingEntity currentTarget() {
        return this.getTarget();
    }

    /** 兼容保留：某些工具会查这个。 */
    public static boolean isBoss(Mob mob) {
        return mob instanceof CrimsonKnight;
    }
}

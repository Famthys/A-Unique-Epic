package com.famthys.uniqueepic.dog;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 人面犬。
 *
 * <p>模型与动画来自 GeckoLib 素材包（21 骨骼 / 93 方块 / 256² 贴图），
 * 三段动画的名字是素材里写死的，不能改：</p>
 * <ul>
 *   <li>{@code animation.human_faced_dog.bite} —— 啃咬，1 秒，单次</li>
 *   <li>{@code animation.human_faced_dog.guard_head} —— 抱头防御，2 秒，保持中段</li>
 *   <li>{@code animation.human_faced_dog.death_right} —— 向右倒地，2 秒，保持末帧</li>
 * </ul>
 * <p>素材里**没有**走路/待机动画，所以移动时保持静止姿态。</p>
 */
public class HumanFacedDogEntity extends Monster implements GeoEntity {

    /* 动画名和素材里的完全一致，改一个字就播不出来 */
    public static final String ANIM_BITE = "animation.human_faced_dog.bite";
    public static final String ANIM_GUARD = "animation.human_faced_dog.guard_head";
    public static final String ANIM_DEATH = "animation.human_faced_dog.death_right";

    private static final RawAnimation BITE = RawAnimation.begin().thenPlay(ANIM_BITE);
    private static final RawAnimation GUARD = RawAnimation.begin().thenPlayAndHold(ANIM_GUARD);
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold(ANIM_DEATH);

    /** 死亡动作 2 秒 = 40 tick，比原版 20 tick 长，所以要延后移除。 */
    private static final int DEATH_REMOVE_TICK = 40;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public HumanFacedDogEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /* ==================== GeckoLib ==================== */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // receiveTriggeredAnimations() 是必须的，否则 triggerAnim 无效（不报错、就是不播）
        controllers.add(new AnimationController<HumanFacedDogEntity>("actions", 2,
                        state -> PlayState.STOP)
                .triggerableAnim("bite", BITE)
                .triggerableAnim("guard", GUARD)
                .triggerableAnim("death", DEATH)
                .receiveTriggeredAnimations());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    /* ==================== 行为 ==================== */

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit) {
            triggerAnim("actions", "bite");
        }
        return hit;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        triggerAnim("actions", "death");
    }

    /** 死亡动作要播完 2 秒，所以把尸体停留时间从原版 20 tick 延长到 40 tick。 */
    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= DEATH_REMOVE_TICK && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }
}

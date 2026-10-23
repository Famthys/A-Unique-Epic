package com.famthys.uniqueepic.boss;

import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;

public final class CrimsonPosePlayer {
    private record Track(String bone, boolean position, float[][] keys) {}

    /** 几何放大倍数，必须和 CrimsonKnightModel 里的一致。 */
    private static final float POSITION_SCALE = 2.0F;
    private static final Track[] ATTACK = {
        new Track("torso", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, -5.0F, -15.0F, -3.0F}, {0.65F, -8.0F, -24.0F, -5.0F}, {0.85F, -4.0F, -20.0F, -4.0F}, {0.9F, 10.0F, 20.0F, 5.0F}, {1.1F, 12.0F, 24.0F, 6.0F}, {1.45F, 3.0F, 8.0F, 2.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("head", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, 4.0F, 12.0F, 0.0F}, {0.65F, 8.0F, 18.0F, 0.0F}, {0.85F, 5.0F, 12.0F, 0.0F}, {0.9F, -8.0F, -8.0F, 0.0F}, {1.1F, -8.0F, -10.0F, 0.0F}, {1.45F, 0.0F, -3.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("right_arm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, -90.0F, -12.0F, -12.0F}, {0.65F, -145.0F, -20.0F, -18.0F}, {0.85F, -132.0F, -12.0F, -12.0F}, {0.9F, 42.0F, 6.0F, 4.0F}, {1.1F, 58.0F, 12.0F, 5.0F}, {1.45F, 14.0F, 0.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("right_forearm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, -25.0F, 0.0F, 0.0F}, {0.65F, -42.0F, 0.0F, 0.0F}, {0.85F, -25.0F, 0.0F, 0.0F}, {0.9F, -8.0F, 0.0F, 0.0F}, {1.1F, 0.0F, 0.0F, 0.0F}, {1.45F, -8.0F, 0.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("right_hand", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, 0.0F, 0.0F, -8.0F}, {0.65F, 0.0F, 0.0F, -12.0F}, {0.85F, 0.0F, 0.0F, -6.0F}, {0.9F, 0.0F, 0.0F, 8.0F}, {1.1F, 0.0F, 0.0F, 8.0F}, {1.45F, 0.0F, 0.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("left_arm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, -16.0F, 8.0F, 10.0F}, {0.65F, -25.0F, 12.0F, 18.0F}, {0.85F, -20.0F, 10.0F, 15.0F}, {0.9F, 12.0F, -5.0F, 12.0F}, {1.1F, 18.0F, -8.0F, 16.0F}, {1.45F, 4.0F, 0.0F, 4.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("left_forearm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, -18.0F, 0.0F, 0.0F}, {0.65F, -30.0F, 0.0F, 0.0F}, {0.85F, -25.0F, 0.0F, 0.0F}, {0.9F, -15.0F, 0.0F, 0.0F}, {1.1F, -12.0F, 0.0F, 0.0F}, {1.45F, -5.0F, 0.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("cape_upper", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, 3.0F, 0.0F, 0.0F}, {0.65F, 6.0F, 0.0F, 0.0F}, {0.85F, 9.0F, 0.0F, 0.0F}, {0.9F, -6.0F, 0.0F, 0.0F}, {1.1F, -9.0F, 0.0F, 0.0F}, {1.45F, 4.0F, 0.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("cape_lower", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, 4.0F, 0.0F, 0.0F}, {0.65F, 9.0F, 0.0F, 0.0F}, {0.85F, 12.0F, 0.0F, 0.0F}, {0.9F, -4.0F, 0.0F, 0.0F}, {1.1F, -13.0F, 0.0F, 0.0F}, {1.45F, 7.0F, 0.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
        new Track("tabard", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.4F, 2.0F, 0.0F, 0.0F}, {0.65F, 4.0F, 0.0F, 0.0F}, {0.85F, 5.0F, 0.0F, 0.0F}, {0.9F, -6.0F, 0.0F, 0.0F}, {1.1F, -8.0F, 0.0F, 0.0F}, {1.45F, 2.0F, 0.0F, 0.0F}, {2.0F, 0.0F, 0.0F, 0.0F}}),
    };
    private static final Track[] DEATH = {
        new Track("root", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, -3.0F, 0.0F, 0.0F}, {0.6F, 10.0F, 0.0F, 2.0F}, {1.0F, 35.0F, 0.0F, 3.0F}, {1.4F, 72.0F, 0.0F, 4.0F}, {1.75F, 88.0F, 0.0F, 4.0F}, {2.1F, 85.0F, 0.0F, 3.0F}, {2.6F, 85.0F, 0.0F, 3.0F}}),
        new Track("root", true, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.05F, 0.0F, 0.09236F, 0.0F}, {0.1F, 0.0F, 0.14471F, 0.0F}, {0.15F, 0.0F, 0.19705F, 0.0F}, {0.2F, 0.0F, 0.24938F, 0.0F}, {0.25F, 0.0F, 0.30168F, 0.0F}, {0.3F, 0.0F, 0.26688F, 0.0F}, {0.35F, 0.0F, 0.24669F, 0.0F}, {0.4F, 0.0F, 0.25463F, 0.0F}, {0.45F, 0.0F, 0.36615F, 0.0F}, {0.5F, 0.0F, 0.47747F, 0.0F}, {0.55F, 0.0F, 0.5885F, 0.0F}, {0.6F, 0.0F, 0.69915F, 0.0F}, {0.65F, 0.0F, 0.83816F, 0.0F}, {0.7F, 0.0F, 0.9756F, 0.0F}, {0.75F, 0.0F, 1.11109F, 0.0F}, {0.8F, 0.0F, 1.2443F, 0.0F}, {0.85F, 0.0F, 1.51938F, 0.0F}, {0.9F, 0.0F, 1.81926F, 0.0F}, {0.95F, 0.0F, 2.12303F, 0.0F}, {1.0F, 0.0F, 2.4297F, 0.0F}, {1.05F, 0.0F, 2.79435F, 0.0F}, {1.1F, 0.0F, 3.10375F, 0.0F}, {1.15F, 0.0F, 3.35706F, 0.0F}, {1.2F, 0.0F, 3.55414F, 0.0F}, {1.25F, 0.0F, 3.69553F, 0.0F}, {1.3F, 0.0F, 3.78241F, 0.0F}, {1.35F, 0.0F, 3.81664F, 0.0F}, {1.4F, 0.0F, 4.39365F, 0.0F}, {1.45F, 0.0F, 4.66676F, 0.0F}, {1.5F, 0.0F, 4.93434F, 0.0F}, {1.55F, 0.0F, 5.19529F, 0.0F}, {1.6F, 0.0F, 5.47365F, 0.0F}, {1.65F, 0.0F, 5.75729F, 0.0F}, {1.7F, 0.0F, 6.03129F, 0.0F}, {1.75F, 0.0F, 6.35268F, 0.0F}, {1.8F, 0.0F, 6.54649F, 0.0F}, {1.85F, 0.0F, 6.79033F, 0.0F}, {1.9F, 0.0F, 7.0431F, 0.0F}, {1.95F, 0.0F, 7.29682F, 0.0F}, {2.0F, 0.0F, 7.54905F, 0.0F}, {2.05F, 0.0F, 7.79966F, 0.0F}, {2.1F, 0.0F, 8.04853F, 0.0F}, {2.15F, 0.0F, 8.04853F, 0.0F}, {2.2F, 0.0F, 8.04853F, 0.0F}, {2.25F, 0.0F, 8.04853F, 0.0F}, {2.3F, 0.0F, 8.04853F, 0.0F}, {2.35F, 0.0F, 8.04853F, 0.0F}, {2.4F, 0.0F, 8.04853F, 0.0F}, {2.45F, 0.0F, 8.04853F, 0.0F}, {2.5F, 0.0F, 8.04853F, 0.0F}, {2.55F, 0.0F, 8.04853F, 0.0F}, {2.6F, 0.0F, 8.04853F, 0.0F}}),
        new Track("torso", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, -8.0F, 0.0F, 0.0F}, {0.6F, -10.0F, 0.0F, 0.0F}, {1.0F, -6.0F, 0.0F, 0.0F}, {1.4F, 0.0F, 0.0F, 0.0F}, {1.75F, 3.0F, 0.0F, 0.0F}, {2.1F, 2.0F, 0.0F, 0.0F}, {2.6F, 2.0F, 0.0F, 0.0F}}),
        new Track("head", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, -12.0F, 0.0F, 0.0F}, {0.6F, -18.0F, 0.0F, 0.0F}, {1.0F, -10.0F, 0.0F, 0.0F}, {1.4F, 6.0F, 0.0F, 0.0F}, {1.75F, 12.0F, 0.0F, 0.0F}, {2.1F, 8.0F, 0.0F, 0.0F}, {2.6F, 8.0F, 0.0F, 0.0F}}),
        new Track("right_arm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, 5.0F, 0.0F, -5.0F}, {0.6F, 15.0F, 0.0F, -10.0F}, {1.0F, 5.0F, 0.0F, -20.0F}, {1.4F, -8.0F, 0.0F, -28.0F}, {1.75F, 5.0F, 0.0F, -32.0F}, {2.1F, 4.0F, 0.0F, -30.0F}, {2.6F, 4.0F, 0.0F, -30.0F}}),
        new Track("left_arm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, 8.0F, 0.0F, 5.0F}, {0.6F, 20.0F, 0.0F, 15.0F}, {1.0F, 10.0F, 0.0F, 25.0F}, {1.4F, -5.0F, 0.0F, 35.0F}, {1.75F, 5.0F, 0.0F, 38.0F}, {2.1F, 4.0F, 0.0F, 35.0F}, {2.6F, 4.0F, 0.0F, 35.0F}}),
        new Track("right_forearm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, -5.0F, 0.0F, 0.0F}, {0.6F, -12.0F, 0.0F, 0.0F}, {1.0F, -5.0F, 0.0F, 0.0F}, {1.4F, 0.0F, 0.0F, 0.0F}, {1.75F, 0.0F, 0.0F, 0.0F}, {2.1F, 0.0F, 0.0F, 0.0F}, {2.6F, 0.0F, 0.0F, 0.0F}}),
        new Track("left_forearm", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, -8.0F, 0.0F, 0.0F}, {0.6F, -15.0F, 0.0F, 0.0F}, {1.0F, -10.0F, 0.0F, 0.0F}, {1.4F, -4.0F, 0.0F, 0.0F}, {1.75F, 0.0F, 0.0F, 0.0F}, {2.1F, 0.0F, 0.0F, 0.0F}, {2.6F, 0.0F, 0.0F, 0.0F}}),
        new Track("cape_upper", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, 5.0F, 0.0F, 0.0F}, {0.6F, 8.0F, 0.0F, 0.0F}, {1.0F, 10.0F, 0.0F, 0.0F}, {1.4F, 5.0F, 0.0F, 0.0F}, {1.75F, 3.0F, 0.0F, 0.0F}, {2.1F, 2.0F, 0.0F, 0.0F}, {2.6F, 2.0F, 0.0F, 0.0F}}),
        new Track("cape_lower", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, 4.0F, 0.0F, 0.0F}, {0.6F, 8.0F, 0.0F, 0.0F}, {1.0F, 10.0F, 0.0F, 0.0F}, {1.4F, 4.0F, 0.0F, 0.0F}, {1.75F, 2.0F, 0.0F, 0.0F}, {2.1F, 1.0F, 0.0F, 0.0F}, {2.6F, 1.0F, 0.0F, 0.0F}}),
        new Track("cape_hem", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, 2.0F, 0.0F, 0.0F}, {0.6F, 6.0F, 0.0F, 0.0F}, {1.0F, 8.0F, 0.0F, 0.0F}, {1.4F, 3.0F, 0.0F, 0.0F}, {1.75F, 1.0F, 0.0F, 0.0F}, {2.1F, 0.0F, 0.0F, 0.0F}, {2.6F, 0.0F, 0.0F, 0.0F}}),
        new Track("tabard", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, 2.0F, 0.0F, 0.0F}, {0.6F, 8.0F, 0.0F, 0.0F}, {1.0F, 14.0F, 0.0F, 0.0F}, {1.4F, 10.0F, 0.0F, 0.0F}, {1.75F, -4.0F, 0.0F, 0.0F}, {2.1F, 0.0F, 0.0F, 0.0F}, {2.6F, 0.0F, 0.0F, 0.0F}}),
        new Track("left_shin", false, new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.25F, 0.0F, 0.0F, 0.0F}, {0.6F, -12.0F, 0.0F, 0.0F}, {1.0F, -15.0F, 0.0F, 0.0F}, {1.4F, -8.0F, 0.0F, 0.0F}, {1.75F, 0.0F, 0.0F, 0.0F}, {2.1F, 0.0F, 0.0F, 0.0F}, {2.6F, 0.0F, 0.0F, 0.0F}}),
    };

    public static void apply(String clip, float seconds, Map<String, ModelPart> bones) {
        // 用于本 Boss 的独立动画。每帧恢复静止姿态，防止角度累加漂移。
        for (ModelPart part : bones.values()) part.resetPose();
        Track[] tracks = switch (clip) {
            case "attack" -> ATTACK;
            case "death" -> DEATH;
            default -> null;
        };
        if (tracks == null) return;
        for (Track track : tracks) {
            ModelPart part = bones.get(track.bone());
            if (part == null) continue;
            float[] v = sample(track.keys(), Math.max(0, seconds));
            if (track.position()) {
                // 本模组的几何整体放大了 POSITION_SCALE 倍（为了原样复用 2px/单位的贴图），
                // 位移必须同步放大，否则死亡倒地只会倒一半。角度不受缩放影响。
                part.x += v[0] * POSITION_SCALE;
                part.y -= v[1] * POSITION_SCALE;
                part.z += v[2] * POSITION_SCALE;
            } else {
                // Blockbench 的 y 向上；Java 模型 y 向下，x/z 旋转符号反转。
                part.xRot += (float)Math.toRadians(-v[0]);
                part.yRot += (float)Math.toRadians(v[1]);
                part.zRot += (float)Math.toRadians(-v[2]);
            }
        }
    }
    private static float[] sample(float[][] keys, float time) {
        float[] a = keys[0], b = a;
        for (float[] key : keys) {
            b = key;
            if (time <= key[0]) break;
            a = key;
        }
        float alpha = b[0] == a[0] ? 0 : Math.max(0, Math.min(1, (time-a[0])/(b[0]-a[0])));
        return new float[]{a[1]+(b[1]-a[1])*alpha, a[2]+(b[2]-a[2])*alpha, a[3]+(b[3]-a[3])*alpha};
    }
}

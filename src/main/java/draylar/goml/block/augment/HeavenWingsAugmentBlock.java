package draylar.goml.block.augment;

import draylar.goml.api.Claim;
import draylar.goml.block.ClaimAugmentBlock;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.player.PlayerEntity;

public class HeavenWingsAugmentBlock extends ClaimAugmentBlock {

    public static final AbilitySource HEAVEN_WINGS = Pal.getAbilitySource("goml", "heaven_wings");

    public HeavenWingsAugmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlayerEnter(Claim claim, PlayerEntity player) {
        HEAVEN_WINGS.grantTo(player, VanillaAbilities.ALLOW_FLYING);
    }

    @Override
    public void onPlayerExit(Claim claim, PlayerEntity player) {
        HEAVEN_WINGS.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
    }
}
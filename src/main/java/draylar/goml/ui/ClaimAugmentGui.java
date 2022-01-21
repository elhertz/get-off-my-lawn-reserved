package draylar.goml.ui;

import draylar.goml.api.Augment;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import draylar.goml.block.entity.ClaimAnchorBlockEntity;
import draylar.goml.item.TooltippedBlockItem;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.block.Block;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClaimAugmentGui extends PagedGui {
    private final Claim claim;
    private final boolean canModify;
    private final ClaimAnchorBlockEntity blockEntity;
    private final List<Map.Entry<BlockPos, Augment>> cachedEntries = new ArrayList<>();

    public ClaimAugmentGui(ServerPlayerEntity player, Claim claim, boolean canModify, @Nullable Runnable onClose) {
        super(player, onClose);
        this.claim = claim;
        this.blockEntity = ClaimUtils.getAnchor(player.server.getWorld(RegistryKey.of(Registry.WORLD_KEY, claim.getWorld())), claim);
        this.canModify = canModify;
        this.setTitle(new TranslatableText("text.goml.gui.augment_list.title"));
        this.updateDisplay();
        this.open();
    }

    @Override
    protected void updateDisplay() {
        this.cachedEntries.clear();
        var rest = new ArrayList<Map.Entry<BlockPos, Augment>>();

        for (var entry : this.blockEntity.getAugments().entrySet()) {
            (entry.getValue().hasSettings() ? this.cachedEntries : rest).add(entry);
        }
        
        this.cachedEntries.addAll(rest);
        super.updateDisplay();
    }

    @Override
    protected int getPageAmount() {
        return this.blockEntity.getAugments().size() / PAGE_SIZE;
    }

    @Override
    protected DisplayElement getElement(int id) {
        if (this.cachedEntries.size() > id) {
            var entry = this.cachedEntries.get(id);
            var builder = new GuiElementBuilder();
            var item = entry.getValue() instanceof Block block ? block.asItem() : null;
            builder.hideFlags();
            builder.addLoreLine(new TranslatableText("text.goml.position",
                    new LiteralText(entry.getKey().toShortString()).formatted(Formatting.WHITE)
            ).formatted(Formatting.BLUE));

            builder.setName(entry.getValue().getAugmentName());

            if (item != null) {
                builder.setItem(item);

                if (item instanceof TooltippedBlockItem tooltipped) {
                    builder.addLoreLine(LiteralText.EMPTY);

                    tooltipped.addLines(builder::addLoreLine);
                }
            }

            if (this.canModify && entry.getValue().hasSettings()) {
                builder.addLoreLine(LiteralText.EMPTY);
                builder.addLoreLine(new TranslatableText("text.goml.gui.click_to_modify").formatted(Formatting.RED));
                builder.setCallback((x, y, z) -> {
                    playClickSound(this.player);
                    entry.getValue().openSettings(this.claim, this.player, () -> {
                        new ClaimAugmentGui(this.player, this.claim, this.canModify, this.closeCallback);
                    });
                });
            }

            return DisplayElement.of(builder);
        }
        return DisplayElement.empty();
    }
}
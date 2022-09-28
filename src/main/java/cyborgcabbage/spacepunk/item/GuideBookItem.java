package cyborgcabbage.spacepunk.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.List;

public class GuideBookItem extends Item {
    private final Identifier id;
    public GuideBookItem(Identifier _id, Settings settings) {
        super(settings.maxCount(1));
        id = _id;
    }

    public Book getBook() {
        return BookRegistry.INSTANCE.books.get(id);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);

        Identifier rl = id;
        if (flagIn.isAdvanced()) {
            tooltip.add(Text.literal("Book ID: " + rl).formatted(Formatting.GRAY));
        }

        Book book = getBook();
        if (book != null && !book.getContents().isErrored()) {
            tooltip.add(book.getSubtitle().formatted(Formatting.GRAY));
        } else if (book == null) {
            if (rl == null) {
                tooltip.add(Text.translatable("item.patchouli.guide_book.undefined")
                        .formatted(Formatting.DARK_GRAY));
            } else {
                tooltip.add(Text.translatable("item.patchouli.guide_book.invalid", rl)
                        .formatted(Formatting.DARK_GRAY));
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getStackInHand(handIn);
        Book book = getBook();
        if (book == null) {
            return new TypedActionResult<>(ActionResult.FAIL, stack);
        }

        if (playerIn instanceof ServerPlayerEntity) {
            PatchouliAPI.get().openBookGUI((ServerPlayerEntity) playerIn, book.id);

            // This plays the sound to others nearby, playing to the actual opening player handled from the packet
            SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);
            playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
}

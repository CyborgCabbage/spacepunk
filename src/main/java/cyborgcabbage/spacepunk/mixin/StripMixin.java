package cyborgcabbage.spacepunk.mixin;

import com.google.common.collect.ImmutableMap;
import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
public class StripMixin {
    private static final Map<Block, Block> SPACEPUNK_STRIPPED_BLOCKS = new ImmutableMap.Builder<Block, Block>().put(Spacepunk.VENUS_WOOD, Spacepunk.STRIPPED_VENUS_WOOD).put(Spacepunk.VENUS_LOG, Spacepunk.STRIPPED_VENUS_LOG).build();

    @Inject(method="getStrippedState",at=@At("HEAD"), cancellable = true)
    private void stripInject(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir){
        Optional<BlockState> out = Optional.ofNullable(SPACEPUNK_STRIPPED_BLOCKS.get(state.getBlock())).map(block -> block.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)));
        if(out.isPresent()){
            cir.setReturnValue(out);
            cir.cancel();
        }
    }
}

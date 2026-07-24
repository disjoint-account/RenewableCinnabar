package net.disjoint.renewablecinnabar.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public abstract class MixinRenewableCinnabar {

	@Shadow
	public abstract boolean isSame(Fluid other);

	@Shadow
	protected abstract void fizz(LevelAccessor level, BlockPos pos);

	@Inject(
			method = "spreadTo",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState target, CallbackInfo ci) {
		if (direction == Direction.DOWN) {
			FluidState fluidState = level.getFluidState(pos);
			if ((this.isSame(Fluids.LAVA) || this.isSame(Fluids.FLOWING_LAVA)) && fluidState.is(FluidTags.WATER)) {
				if (state.getBlock() instanceof LiquidBlock && level.getBlockState(pos.below()).is(Blocks.POTENT_SULFUR)) {
					level.setBlock(pos, Blocks.CINNABAR.defaultBlockState(), 3);
					this.fizz(level, pos);
					ci.cancel();
				}
			}
		}
	}
}
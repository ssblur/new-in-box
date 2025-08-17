package com.ssblur.new_in_box.block

import com.ssblur.new_in_box.NewInBox
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class BoxBlock(properties: Properties) : Block(properties.noOcclusion()) {
  init {
    registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH))
  }

  override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
    builder.add(FACING)
  }

  override fun getShape(
    blockState: BlockState,
    blockGetter: BlockGetter,
    blockPos: BlockPos,
    collisionContext: CollisionContext
  ): VoxelShape? {
    val p = 1.0 / 16.0
    val shape = Shapes.or(
      Shapes.box(p * 2, 0.0, p * 4, p * 14, 1.0, p * 4.25),
      Shapes.box(p * 2, 0.0, p * 12, p * 14, 1.0, p * 12.25),
      Shapes.box(p * 2, 0.0, p * 4, p * 2.25, 1.0, p * 12),
      Shapes.box(p * 14, 0.0, p * 4, p * 14.25, 1.0, p * 12),
      Shapes.box(p * 2, p * 15.75, p * 4, p * 14, 1.0, p * 12),
    )
    val shapes = Shapes.rotateHorizontal(shape)
    return when(blockState.getValue(FACING)) {
      Direction.DOWN, Direction.UP -> Shapes.block()
      Direction.NORTH, Direction.SOUTH -> shapes[Direction.NORTH]
      Direction.WEST, Direction.EAST -> shapes[Direction.WEST]
    }
  }

  override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
    return defaultBlockState().setValue(FACING, blockPlaceContext.horizontalDirection)
  }

  override fun useWithoutItem(
    blockState: BlockState,
    level: Level,
    blockPos: BlockPos,
    player: Player,
    blockHitResult: BlockHitResult
  ): InteractionResult? {
    if(player.hasEffect(NewInBox.FIGURINE_EFFECT.ref())) {
      val step = blockState.getValue(FACING).opposite.step()
      val target = blockPos.center.add(step.x.toDouble(), step.y.toDouble(), step.z.toDouble())
      player.teleportTo(target.x, target.y, target.z)
      player.removeEffect(NewInBox.FIGURINE_EFFECT.ref())
    } else {
      player.teleportTo(blockPos.center.x, blockPos.y.toDouble(), blockPos.center.z)
      player.setYBodyRot(blockState.getValue(FACING).opposite.toYRot())
      player.yRot = blockState.getValue(FACING).opposite.toYRot()
      player.addEffect(MobEffectInstance(NewInBox.FIGURINE_EFFECT.ref(), -1, 0, true, false, true, null))
    }
    return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult)
  }

  companion object {
    val FACING = BlockStateProperties.HORIZONTAL_FACING
  }
}
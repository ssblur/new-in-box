package com.ssblur.new_in_box.block

import com.ssblur.new_in_box.NewInBox
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.util.ProblemReporter
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.phys.AABB
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
      Shapes.box(p * 2, 0.0, p * 11.75, p * 14, 1.0, p * 12),
      Shapes.box(p * 2, 0.0, p * 4, p * 2.25, 1.0, p * 12),
      Shapes.box(p * 13.75, 0.0, p * 4, p * 14, 1.0, p * 12),
      Shapes.box(p * 2, p * 15.75, p * 4, p * 14, 1.0, p * 12),
      Shapes.box(p * 2, p * -0.25, p * 4, p * 14, 0.0, p * 12),
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
    if(player.isCrouching) {
      level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState())
      val bounds = AABB.unitCubeFromLowerCorner(blockPos.center.add(-.5))
      val mob = level.getEntitiesOfClass(Mob::class.java, bounds).firstOrNull{
        it.isNoAi
      }
      val item = ItemStack(NewInBox.BOX_ITEM)
      if(mob != null) {
        mob.removeEffect(NewInBox.FIGURINE_EFFECT.ref())
        item[NewInBox.BOX_TYPE] = BuiltInRegistries.ENTITY_TYPE.getKey(mob.type)
        val output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING)
        mob.save(output)
        item[DataComponents.ITEM_NAME] = Component.translatable("item.new_in_box.box_with", mob.name)
        item[NewInBox.BOX_ENTITY] = output.buildResult()
        mob.remove(Entity.RemovalReason.DISCARDED)
      }
      if(!player.addItem(item)) {
        val pos = blockPos.center
        val entity = ItemEntity(level, pos.x, pos.y, pos.z, item)
        level.addFreshEntity(entity)
      }
    } else {
      if (player.hasEffect(NewInBox.FIGURINE_EFFECT.ref())) {
        val step = blockState.getValue(FACING).opposite.step()
        val target = blockPos.center.add(step.x.toDouble(), step.y.toDouble(), step.z.toDouble())
        player.teleportTo(target.x, target.y, target.z)
        player.removeEffect(NewInBox.FIGURINE_EFFECT.ref())
      } else {
        player.teleportTo(blockPos.center.x, blockPos.y.toDouble(), blockPos.center.z)
        player.setYBodyRot(blockState.getValue(FACING).opposite.toYRot())
        player.yRot = blockState.getValue(FACING).opposite.toYRot()
        player.addEffect(NewInBox.figurine())
      }
    }
    return InteractionResult.SUCCESS
  }

  override fun destroy(levelAccessor: LevelAccessor, blockPos: BlockPos, blockState: BlockState) {
    val bounds = AABB.unitCubeFromLowerCorner(blockPos.center.add(-.5))
    levelAccessor.getEntitiesOfClass(Mob::class.java, bounds).forEach {
      it.isNoAi = false
      it.removeEffect(MobEffects.FIRE_RESISTANCE)
    }
    super.destroy(levelAccessor, blockPos, blockState)
  }

  companion object {
    val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
  }
}
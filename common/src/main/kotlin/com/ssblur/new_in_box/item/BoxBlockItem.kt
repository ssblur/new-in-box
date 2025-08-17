package com.ssblur.new_in_box.item

import com.ssblur.new_in_box.NewInBox
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.ProblemReporter
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.level.storage.TagValueOutput
import java.util.*

class BoxBlockItem(block: Block, properties: Properties) : BlockItem(block, properties.stacksTo(1)) {
  override fun interactLivingEntity(
    itemStack: ItemStack,
    player: Player,
    livingEntity: LivingEntity,
    interactionHand: InteractionHand
  ): InteractionResult? {
    if(itemStack[NewInBox.BOX_ENTITY] == null && livingEntity is Mob) {
      if(livingEntity.health > 10) {
        player.displayClientMessage(Component.translatable("extra.new_in_box.too_much_health"), false)
        return InteractionResult.FAIL
      }
      val output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING)
      livingEntity.save(output)
      itemStack.shrink(1)
      val newItem = ItemStack(this)
      newItem[NewInBox.BOX_ENTITY] = output.buildResult()
      newItem[DataComponents.ITEM_NAME] = Component.translatable("item.new_in_box.box_with", livingEntity.name)
      newItem[NewInBox.BOX_TYPE] = BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.type)
      player.addItem(newItem)
      livingEntity.remove(Entity.RemovalReason.DISCARDED)
    }
    return InteractionResult.PASS
  }

  override fun place(blockPlaceContext: BlockPlaceContext): InteractionResult? {
    val level = blockPlaceContext.level
    if(level is ServerLevel && blockPlaceContext.itemInHand[NewInBox.BOX_ENTITY] != null) {
      val input = TagValueInput.create(
        ProblemReporter.DISCARDING,
        level.registryAccess(),
        blockPlaceContext.itemInHand[NewInBox.BOX_ENTITY]!!.asCompound().get()
      )
      val type = BuiltInRegistries.ENTITY_TYPE.get(blockPlaceContext.itemInHand[NewInBox.BOX_TYPE]!!).get().value()
      val entity = type.create(level, EntitySpawnReason.SPAWN_ITEM_USE)
      entity?.let {
        if(entity is LivingEntity) {
          entity.load(input)
          entity.uuid = UUID.randomUUID()
          entity.addEffect(NewInBox.figurine())
          val target = blockPlaceContext.clickedPos.center
          entity.teleportTo(target.x, target.y - 0.45, target.z)
          entity.yRot = blockPlaceContext.clickedFace.opposite.toYRot()
          entity.yBodyRot = blockPlaceContext.clickedFace.opposite.toYRot()
          if(entity is Mob) {
            entity.isNoAi = true
          }

          level.addFreshEntity(entity)
        }
      }
      super.place(blockPlaceContext)
      return InteractionResult.SUCCESS
    }
    return super.place(blockPlaceContext)
  }

  override fun canPlace(blockPlaceContext: BlockPlaceContext, blockState: BlockState): Boolean {
    return (!this.mustSurvive() || blockState.canSurvive(
      blockPlaceContext.level,
      blockPlaceContext.clickedPos
    ))
  }
}
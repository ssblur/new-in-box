package com.ssblur.new_in_box.effect

import com.ssblur.new_in_box.NewInBox
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes

class FigurineEffect: MobEffect(MobEffectCategory.NEUTRAL, 0x000000000u.toInt()) {
  init {
    this.addAttributeModifier(
      Attributes.SCALE, NewInBox.location("figurine_att_scale"),
      -.6, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
    )
  }

  override fun applyEffectTick(serverLevel: ServerLevel, livingEntity: LivingEntity, i: Int): Boolean {
    if(livingEntity is Mob) {
      livingEntity.ambientSoundTime -= 5
    }
    if(livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) livingEntity.setSharedFlagOnFire(false)
    return super.applyEffectTick(serverLevel, livingEntity, i)
  }

  override fun shouldApplyEffectTickThisTick(i: Int, j: Int): Boolean {
    return true
  }
}
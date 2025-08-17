package com.ssblur.new_in_box.effect

import com.ssblur.new_in_box.NewInBox
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes

class FigurineEffect: MobEffect(MobEffectCategory.NEUTRAL, 0x000000000u.toInt()) {
  init {
    this.addAttributeModifier(
      Attributes.SCALE, NewInBox.location("figurine_att_scale"),
      -.6, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
    )
  }
}
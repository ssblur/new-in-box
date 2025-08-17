package com.ssblur.new_in_box

import com.ssblur.new_in_box.block.BoxBlock
import com.ssblur.new_in_box.effect.FigurineEffect
import com.ssblur.new_in_box.item.BoxBlockItem
import com.ssblur.unfocused.ModInitializer
import com.ssblur.unfocused.extension.BlockExtension.renderType
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.chunk.ChunkSectionLayer
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object NewInBox : ModInitializer("new_in_box") {
    const val MODID = "new_in_box"
    val LOGGER: Logger = LoggerFactory.getLogger(id)

    val BOX_BLOCK = registerBlock("box") { properties ->
        BoxBlock(properties)
    }
    val BOX_ITEM = registerItem("box") { properties ->
        BoxBlockItem(BOX_BLOCK.get(), properties)
    }
    val FIGURINE_EFFECT = registerEffect("figurine") {
        FigurineEffect()
    }

    fun init() {
        LOGGER.info("New In Box loaded...")
    }

    fun clientInit() {
        BOX_BLOCK.then {
            it.renderType(ChunkSectionLayer.TRANSLUCENT)
        }
    }
}
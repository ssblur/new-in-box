package com.ssblur.new_in_box

import com.ssblur.unfocused.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object NewInBox : ModInitializer("new_in_box") {
    const val MODID = "new_in_box"
    val LOGGER: Logger = LoggerFactory.getLogger(id)

    fun init() {
        LOGGER.info("New In Box loaded...")
    }

    fun clientInit() {
        LOGGER.info("New In Box loaded...")
    }
}
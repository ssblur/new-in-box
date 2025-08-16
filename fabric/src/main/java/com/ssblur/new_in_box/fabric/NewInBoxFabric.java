package com.ssblur.new_in_box.fabric;

import com.ssblur.new_in_box.NewInBox;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class NewInBoxFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NewInBox.INSTANCE.clientInit();
    }

    @Override
    public void onInitialize() {
        NewInBox.INSTANCE.init();
    }
}

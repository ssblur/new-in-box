package com.ssblur.new_in_box.neo;

import com.ssblur.new_in_box.NewInBox;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(NewInBox.MODID)
public final class NewInBoxNeoForge {
    public NewInBoxNeoForge() {
        NewInBox.INSTANCE.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NewInBox.INSTANCE.clientInit();
        }
    }
}

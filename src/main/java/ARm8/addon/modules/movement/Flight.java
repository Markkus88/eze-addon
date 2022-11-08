package ARm8.addon.modules.movement;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

public class Flight extends Module {
    public Flight() {
        super(Categories.Movement, "flight+", "Fly around like a bird.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
            .name("speed")
            .description("How fast you want to fly.")
            .defaultValue(0.100)
            .sliderMin(0)
            .sliderMax(10)
            .build()
    );

    private final Setting<Double> verticalSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("vertical-speed")
            .description("How fast you want to fly up and down.")
            .defaultValue(0.04)
            .sliderMin(0)
            .sliderMax(10)
            .build()
    );

    private final Setting<Boolean> bypass = sgGeneral.add(new BoolSetting.Builder()
            .name("bypass")
            .description("Bypasses 'flying is not enabled' kick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> bypassdelay = sgGeneral.add(new IntSetting.Builder()
            .name("bypass-delay")
            .description(" ")
            .defaultValue(4)
            .sliderMin(1)
            .sliderMax(50)
            .visible(bypass::get)
            .build()
    );

    private final Setting<Boolean> keepYLevel = sgGeneral.add(new BoolSetting.Builder()
            .name("keep-y-level")
            .description("Will keep you on the same height.")
            .defaultValue(true)
            .visible(bypass::get)
            .build()
    );

    private final Setting<Integer> moveUpDelay = sgGeneral.add(new IntSetting.Builder()
            .name("move-up-delay")
            .description(" ")
            .defaultValue(8)
            .sliderMin(1)
            .sliderMax(50)
            .visible(keepYLevel::get)
            .build()
    );
    
    @EventHandler
    private void onTick(TickEvent.Post event) {
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(speed.get().floatValue());
        double yMotion = 0;

        if (bypass.get() && mc.player.age % this.bypassdelay.get().intValue() == 0) {
            mc.player.setVelocity(mc.player.getVelocity().x,mc.player.getVelocity().y - this.bypassdelay.get()/100.0,mc.player.getVelocity().z);
            if (keepYLevel.get() && mc.player.age % this.moveUpDelay.get().intValue() == 0) {
                mc.player.setVelocity(mc.player.getVelocity().x,mc.player.getVelocity().y + this.moveUpDelay.get()/100.0,mc.player.getVelocity().z);
            }
        }

        if(mc.player.input.jumping) yMotion+=verticalSpeed.get().floatValue();
        if(mc.player.input.sneaking) yMotion-=verticalSpeed.get().floatValue(); 

        mc.player.setVelocity(mc.player.getVelocity().x,mc.player.getVelocity().y + yMotion,mc.player.getVelocity().z);
    }

    @Override
    public void onDeactivate(){
        if(mc.player == null || mc.world == null){return;}
        mc.player.getAbilities().flying = false;
    }
}

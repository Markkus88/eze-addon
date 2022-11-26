package ARm8.eze.modules.movement;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class PacketDigits extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> digits = sgGeneral.add(new IntSetting.Builder()
            .name("digits")
            .description("How many digits to remove.")
            .defaultValue(2)
            .sliderMin(0)
            .sliderMax(5)
            .noSlider()
            .build()
    );

    private final Setting<Boolean> shouldModifyY = sgGeneral.add(new BoolSetting.Builder()
            .name("modify y-position")
            .description("whether or not to modify the y-position.")
            .defaultValue(false)
            .build()
    );

    // Constructor

    public PacketDigits() {
        super(Categories.Movement, "packet-digits", "Removes digits from your movement packets to make them smaller.");
    }

    public double round(double value) {
        int digit = (int) Math.pow(10, digits.get());
        return ((double) (Math.round(value * digit)) / digit);
    }

    public boolean shouldModifyY() {
        return shouldModifyY.get();
    }
}
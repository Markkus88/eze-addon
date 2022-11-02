package ARm8.addon.modules.movement;

import ARm8.addon.Addon;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
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

    // Constructor

    public PacketDigits() {
        super(Addon.MOVEMENT, "packet-digits", "Removes digits from your movement packets to make them smaller.");
    }

    public double round(double value) {
        int digit = (int) Math.pow(10, digits.get());
        return ((double) (Math.round(value * digit)) / digit);
    }
}
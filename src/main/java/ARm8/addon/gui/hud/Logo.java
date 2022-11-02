package ARm8.addon.gui.hud;

import ARm8.addon.Addon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class Logo extends HudElement {
    public static final HudElementInfo<Logo> INFO = new HudElementInfo<>(Addon.HUD_GROUP, "logo-hud", "Display the eze logo.", Logo::new);
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
		.name("scale")
		.description("The scale of the logo.")
		.defaultValue(3)
		.min(0.1)
		.onChanged((size) -> calculateSize())
		.sliderRange(0.1, 10)
		.build()
	);

	private final Setting<Boolean> invert = sgGeneral.add(new BoolSetting.Builder()
		.name("invert")
		.description("Invert the logo.")
		.defaultValue(false)
		.build()
	);

	private final Identifier TEXTURE = new Identifier("eze", "icon.png");


	public Logo() {
		super(INFO);
		calculateSize();
	}

	public void calculateSize() {
		box.setSize(64 * scale.get(), 50 * scale.get());
	}

	@Override
	public void render(HudRenderer renderer) {
		int w = getWidth();
		int h = getHeight();

		if (!invert.get()) {
			renderer.texture(TEXTURE, box.getRenderX(), box.getRenderY(), w, h, Color.WHITE);
		} else {
			renderer.texture(TEXTURE, box.getRenderX()+w, box.getRenderY(), -w, h, Color.WHITE);
		}
	}
}

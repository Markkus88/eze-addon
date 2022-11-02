package ARm8.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ARm8.addon.commands.*;
import ARm8.addon.gui.hud.*;
import ARm8.addon.modules.combat.*;
import ARm8.addon.modules.exploits.*;
import ARm8.addon.modules.misc.*;
import ARm8.addon.modules.movement.*;

public class Addon extends MeteorAddon {
    public static final Logger LOG = LoggerFactory.getLogger("eze-addon");
    public static final Category COMBAT = new Category("eze Combat");
    public static final Category MOVEMENT = new Category("eze Movement");
    public static final Category MISC = new Category("eze Misc");
    public static final Category EXPLOITS = new Category("Exploits");
    
    public static final HudGroup HUD_GROUP = new HudGroup("eze");

    @Override
    public void onInitialize() {
        LOG.info("Initializing eze");

        //Commands
        
        Commands.get().add(new AutoVClipCommand());
        Commands.get().add(new CenterCommand());
        Commands.get().add(new ClearChatCommand());
        Commands.get().add(new CrashItemCommand());
        Commands.get().add(new DelayCommand());
        Commands.get().add(new DesyncCommand());
        Commands.get().add(new DisconnectCommand());
        Commands.get().add(new ExportCommand());
        Commands.get().add(new ExportTerrainCommand());
        Commands.get().add(new GhostCommand());
        Commands.get().add(new ReloadBlocksCommand());
        Commands.get().add(new SeedCommand());
        Commands.get().add(new ServerCommand());
        Commands.get().add(new SleepCommand());
        Commands.get().add(new SoftLeaveCommand());
        Commands.get().add(new UUIDCommand());

        //Combat

        Modules.get().add(new Anchor());
        Modules.get().add(new AntiSurround());
        Modules.get().add(new AutoCity());
        Modules.get().add(new CevBreaker());
        Modules.get().add(new CrystalAura());
        Modules.get().add(new KillAura());
        Modules.get().add(new SmartHoleFill());
        Modules.get().add(new Surround());
        Modules.get().add(new XPThrower());

        //Misc

        Modules.get().add(new AntiScreen());
        Modules.get().add(new AutoCraft());
        Modules.get().add(new AutoSpectre());
        Modules.get().add(new ChatEncryption());
        Modules.get().add(new Config());
        Modules.get().add(new CoordLogger());
        Modules.get().add(new CustomPackets());
        Modules.get().add(new GroupChat());
        Modules.get().add(new InstaMine());
        Modules.get().add(new MultiTask());
        Modules.get().add(new NewChunks());
        Modules.get().add(new NoCollision());
        Modules.get().add(new OreSim());
        Modules.get().add(new PacketPlace());
        Modules.get().add(new PortalGodMode());
        Modules.get().add(new ServerSpoof());
        Modules.get().add(new SkeletonESP());
        Modules.get().add(new TPSSync());
        Modules.get().add(new XrayBruteforce());

        //Movement

        Modules.get().add(new EntityFly());
        Modules.get().add(new EntityPhase());
        Modules.get().add(new NoFall());
        Modules.get().add(new PacketDigits());
        Modules.get().add(new PacketFly());
        Modules.get().add(new RubberbandFly());

        //Exploits

        Modules.get().add(new AACCrash());
        Modules.get().add(new BookCrash());
        Modules.get().add(new ContainerCrash());
        Modules.get().add(new CraftingCrash());
        Modules.get().add(new CreativeCrash());
        Modules.get().add(new EntityCrash());
        Modules.get().add(new ErrorCrash());
        Modules.get().add(new InteractCrash());
        Modules.get().add(new LecternCrash());
        Modules.get().add(new MessageLagger());
        Modules.get().add(new MovementCrash());
        Modules.get().add(new PacketSpammer());
        Modules.get().add(new SequenceCrash());

        //HUD 

        Hud.get().register(ItemCounter.INFO);
        Hud.get().register(Logo.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(COMBAT);
        Modules.registerCategory(MOVEMENT);
        Modules.registerCategory(MISC);
        Modules.registerCategory(EXPLOITS);
    }

    @Override
    public String getPackage() {
        return "ARm8.addon";
    }
}

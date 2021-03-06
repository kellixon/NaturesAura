package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.chunk.effect.DrainSpotEffects;
import de.ellpeck.naturesaura.commands.CommandAura;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.entities.ModEntities;
import de.ellpeck.naturesaura.events.CommonEvents;
import de.ellpeck.naturesaura.gui.GuiHandler;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.items.OreDict;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.potion.ModPotions;
import de.ellpeck.naturesaura.proxy.IProxy;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Mod(modid = NaturesAura.MOD_ID, name = NaturesAura.MOD_NAME, version = NaturesAura.VERSION, dependencies = NaturesAura.DEPS)
public final class NaturesAura {

    public static final String MOD_ID = NaturesAuraAPI.MOD_ID;
    public static final String MOD_ID_UPPER = MOD_ID.toUpperCase(Locale.ROOT);
    public static final String PROXY_LOCATION = "de.ellpeck." + MOD_ID + ".proxy.";
    public static final String MOD_NAME = "Nature's Aura";
    public static final String VERSION = "@VERSION@";
    public static final String DEPS = "required-after:patchouli;";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Instance(value = MOD_ID)
    public static NaturesAura instance;

    @SidedProxy(modId = MOD_ID, clientSide = PROXY_LOCATION + "ClientProxy", serverSide = PROXY_LOCATION + "ServerProxy")
    public static IProxy proxy;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.GOLD_LEAF);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NaturesAuraAPI.setInstance(new InternalHooks());
        Helper.registerCap(IAuraContainer.class);
        Helper.registerCap(IAuraRecharge.class);
        Helper.registerCap(IAuraChunk.class);
        Helper.registerCap(IWorldData.class);

        new ModBlocks();
        new ModItems();
        new ModPotions();

        Compat.preInit();
        PacketHandler.init();
        ModRegistry.preInit(event);
        ModEntities.init();
        new Multiblocks();

        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModConfig.initOrReload(false);
        ModRecipes.init();
        ModRegistry.init(event);
        DrainSpotEffects.init();
        OreDict.init();
        new GuiHandler();

        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModRegistry.postInit(event);
        Compat.postInit();
        proxy.postInit(event);

        if (ModConfig.enabledFeatures.removeDragonBreathContainerItem) {
            Items.DRAGON_BREATH.setContainerItem(null);
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAura());
    }
}

package com.raizunne.redstonic;

/**
 * Created by Raizunne as a part of Redstonic
 * on 03/02/2015, 09:47 PM.
 */

import com.raizunne.redstonic.Handler.*;
import com.raizunne.redstonic.Item.RedstonicContainer;
import com.raizunne.redstonic.Network.PacketDrill;
import com.raizunne.redstonic.Network.PacketDriller;
import com.raizunne.redstonic.Proxy.CommonProxy;
import com.raizunne.redstonic.TileEntity.TEArmorModifier;
import com.raizunne.redstonic.TileEntity.TEDrillModifier;
import com.raizunne.redstonic.TileEntity.TEDriller;
import com.raizunne.redstonic.TileEntity.TEHyperSmelter;
import com.raizunne.redstonic.Util.EIOHelper;
import com.raizunne.redstonic.Util.KeyBinds;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;

import static com.raizunne.redstonic.Handler.RedstonicCommands.*;

@Mod(
    modid = Redstonic.MODID,
    version = Redstonic.VERSION,
    name = Redstonic.NAME,
    dependencies = "after:ThermalExpansion;after:EnderIO")
public class Redstonic {

    public static final String MODID = "Redstonic";
    public static final String VERSION = Tags.VERSION;
    public static final String NAME = "Redstonic";

    @Mod.Instance
    public static Redstonic instance;
    @SidedProxy(clientSide = "com.raizunne.redstonic.Proxy.ClientProxy", serverSide = "com.raizunne.redstonic.Proxy.CommonProxy")
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper network;
    public static Configuration configFile;
    public static ItemArmor.ArmorMaterial RedstonicMaterial = EnumHelper.addArmorMaterial("RedstonicArmorMaterial", 33, new int[]{1,1,1,1}, 0);

    public static CreativeTabs redTab = new CreativeTabs("Redstonic"){
        @Override
        public Item getTabIconItem() {return RedstonicItems.RedDrill;}
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){

        network = NetworkRegistry.INSTANCE.newSimpleChannel("redstonic");
        Redstonic.network.registerMessage(PacketDrill.Handler.class, PacketDrill.class, 0, Side.SERVER);
        Redstonic.network.registerMessage(PacketDriller.Handler.class, PacketDriller.class, 1, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(new RedstonicEventHandler());
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
        MinecraftForge.EVENT_BUS.register(new RedstonicContainer());
        MinecraftForge.EVENT_BUS.register(new KeyBinds());

        if(Loader.isModLoaded("EnderIO")){
            EIOHelper.init();
        }

        RedstonicItems.init();
        RedstonicBlocks.init();
        RedstonicRecipes.init();
//        KeyBinds.init();
        proxy.initRenderers();

        GameRegistry.registerTileEntity(TEDrillModifier.class, "TEDrillModifier");
        GameRegistry.registerTileEntity(TEDriller.class, "TEDriller");
        GameRegistry.registerTileEntity(TEHyperSmelter.class, "TEHyperSmelter");
        GameRegistry.registerTileEntity(TEArmorModifier.class, "TEArmorModifier");

        configFile = new Configuration(e.getSuggestedConfigurationFile());
        configFile.load();
        ConfigHandler.RedstonicConfig(configFile);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
    }

    public void load(FMLInitializationEvent event){
        new GUIHandler();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new RedstonicCommands().new RemoveMessages());
        event.registerServerCommand(new RedstonicCommands().new Redstonic());
    }

}

package pw.codehusky.huskycrates.crate.views;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import pw.codehusky.huskycrates.HuskyCrates;

import java.util.ArrayList;

/**
 * Created by lokio on 12/28/2016.
 */
public class CSGOCrateView {
    private HuskyCrates plugin;
    float updateMax = 1;
    int waitCurrent = 0;
    int offset = 0;
    int hits = 0;
    private ArrayList<ItemStack> items = new ArrayList<>();
    private Inventory disp;
    private Task updater;
    private Player ourplr;
    public CSGOCrateView(HuskyCrates plugin,Player runner){
        ourplr = runner;
        this.plugin = plugin;
        //temp static item list
        items.add(ItemStack.of(ItemTypes.DIAMOND_BLOCK,1));
        items.add(ItemStack.of(ItemTypes.DIAMOND,5));
        items.add(ItemStack.of(ItemTypes.EMERALD,15));
        items.add(ItemStack.of(ItemTypes.IRON_INGOT,32));
        items.add(ItemStack.of(ItemTypes.DIRT,15));
        items.add(ItemStack.of(ItemTypes.REDSTONE_BLOCK,5));
        items.add(ItemStack.of(ItemTypes.IRON_PICKAXE,1));
        items.add(ItemStack.of(ItemTypes.GOLD_NUGGET,15));
        items.add(ItemStack.of(ItemTypes.IRON_SHOVEL,1));
        items.add(ItemStack.of(ItemTypes.IRON_INGOT,15));
        items.add(ItemStack.of(ItemTypes.STONE,15));
        items.add(ItemStack.of(ItemTypes.TORCH,48));
        items.add(ItemStack.of(ItemTypes.COBBLESTONE,5));
        items.add(ItemStack.of(ItemTypes.REDSTONE,32));
        items.add(ItemStack.of(ItemTypes.DIAMOND_PICKAXE,1));
        offset = (int)(Math.random() * items.size());

        disp = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class,evt -> evt.setCancelled(true))
                .property(InventoryTitle.PROPERTY_NAME,InventoryTitle.of(Text.of(TextColors.BLACK,"Mining Crate")))
                .build(plugin);
        updateInv(0);
        Scheduler scheduler = Sponge.getScheduler();
        Task.Builder taskBuilder = scheduler.createTaskBuilder();
        updater = taskBuilder.execute(this::updateTick).intervalTicks(1).submit(plugin);
    }
    private void updateInv(int state) {
        ItemStack border = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR,DyeColors.BLACK).build();
        border.offer(Keys.DISPLAY_NAME,Text.of(""));
        ItemStack selector = ItemStack.of(ItemTypes.REDSTONE_TORCH,1);
        selector.offer(Keys.DISPLAY_NAME,Text.of(""));
        int slotnum = 0;
        for(Inventory e : disp.slots()){
            if(state == 0 && (slotnum == 4 || slotnum == 22 )){
                e.set(selector);
            }else if(slotnum > 9 && slotnum < 17 && state != 2){
                int itemNum = Math.abs(((slotnum - 10) + offset) % items.size());
                e.set(items.get(itemNum));
                if(slotnum == 13) {
                    giveToPlayer = items.get(itemNum);
                }
            }else if(slotnum != 13){
                if(state == 2 ){
                    e.set(confettiBorder());
                }else if(state == 0){
                    e.set(border);
                }
            }
            slotnum++;
        }
        if(!ourplr.isViewingInventory()){
            ourplr.openInventory(disp,plugin.genericCause);
        }
    }
    private ItemStack confettiBorder(){
        DyeColor[] colors = {DyeColors.BLUE,DyeColors.CYAN,DyeColors.GREEN,DyeColors.LIGHT_BLUE,DyeColors.LIME,DyeColors.MAGENTA,DyeColors.ORANGE,DyeColors.PINK,DyeColors.PURPLE,DyeColors.RED,DyeColors.YELLOW};
        ItemStack g =ItemStack.builder()
                .itemType(ItemTypes.STAINED_GLASS_PANE)
                .add(Keys.DYE_COLOR,colors[(int)Math.floor(Math.random() * colors.length)])
                .build();
        g.offer(Keys.DISPLAY_NAME,Text.of("You won an item!"));
        return g;
    }
    private ItemStack giveToPlayer;
    public void updateTick() {
        waitCurrent++;
        int waitStopper = 10;
        if (waitCurrent == Math.round(updateMax) && updateMax < waitStopper) {
            offset++;
            waitCurrent = 0;
            updateMax *= 1.05;
            updateInv(-1);
            ourplr.playSound(SoundTypes.UI_BUTTON_CLICK,ourplr.getLocation().getPosition(),0.25);
        }else if(Math.round(updateMax) == waitStopper){
            ourplr.playSound(SoundTypes.ENTITY_FIREWORK_LAUNCH,ourplr.getLocation().getPosition(),1);
            updateMax = 100;
            waitCurrent = 0;
        }else if(updateMax > waitStopper){
            if (waitCurrent == Math.round(updateMax)) {
                updater.cancel();
                ourplr.closeInventory(plugin.genericCause);
                String name = giveToPlayer.createSnapshot().getType().getTranslation().get();
                ourplr.sendMessage(Text.of("You won ", TextColors.YELLOW, giveToPlayer.getQuantity() + " " + name, TextColors.RESET, " from a ",TextColors.BLUE,"Diamond Crate",TextColors.RESET,"!"));
                ourplr.getInventory().offer(giveToPlayer);
                ourplr.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP,ourplr.getLocation().getPosition(),1);

            }else if(waitCurrent % 5 == 0){
                updateInv(2);
            }
        }

    }
    public Inventory getInventory() {
        return disp;
    }
}

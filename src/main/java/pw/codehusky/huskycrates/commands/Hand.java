package pw.codehusky.huskycrates.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import pw.codehusky.huskycrates.HuskyCrates;
import pw.codehusky.huskycrates.crate.VirtualCrate;

import java.util.Optional;

public class Hand implements CommandExecutor {

    @Override public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Optional<Player> player = commandContext.getOne("player");
        if (!player.isPresent()) {
            commandSource.sendMessage(Text.of("You need to either specify a player or be in game"));
            return CommandResult.empty();
        }
        ItemStack stack = player.get().getItemInHand(HandTypes.MAIN_HAND).get();


        if (stack.toContainer().get(DataQuery.of("UnsafeDamage")).isPresent()) {
            player.get().sendMessage(Text.of(stack.toContainer().get(DataQuery.of("UnsafeData")).get().toString()));
            player.get().sendMessage(Text.of(stack.getItem().toString()));
        }



        return CommandResult.success();

    }
}

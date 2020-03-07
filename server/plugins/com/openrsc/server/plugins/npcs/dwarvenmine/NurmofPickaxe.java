package com.openrsc.server.plugins.npcs.dwarvenmine;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class NurmofPickaxe implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 25000, 100, 60, 2, new Item(ItemId.BRONZE_PICKAXE.id(),
		6), new Item(ItemId.IRON_PICKAXE.id(), 5), new Item(ItemId.STEEL_PICKAXE.id(), 4),
		new Item(ItemId.MITHRIL_PICKAXE.id(), 3), new Item(ItemId.ADAMANTITE_PICKAXE.id(), 2), new Item(ItemId.RUNE_PICKAXE.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.NURMOF.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "greetings welcome to my pickaxe shop",
			"Do you want to buy my premium quality pickaxes");

		int option = multi(p, n, false, //do not send over
				"Yes please", "No thankyou", "Are your pickaxes better than other pickaxes then?");
		if (option == 0) {
			say(p, n, "Yes please");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			say(p, n, "No thankyou\"");
		} else if (option == 2) {
			say(p, n, "Are your pickaxes better than other pickaxes then?");
			npcsay(p, n, "Of course they are",
				"My pickaxes are made of higher grade metal than your ordinary bronze pickaxes",
				"Allowing you to have multiple swings at a rock until you get the ore from it");
		}
	}

}

package com.openrsc.server.plugins.npcs.catherby;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;
import static com.openrsc.server.plugins.Functions.multi;

public class HicktonArcheryShop implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 10000, 100, 80, 1,
		new Item(ItemId.CROSSBOW_BOLTS.id(), 200), new Item(ItemId.BRONZE_ARROWS.id(), 200), new Item(ItemId.IRON_ARROWS.id(), 200),
		new Item(ItemId.STEEL_ARROWS.id(), 0), new Item(ItemId.MITHRIL_ARROWS.id(), 0), new Item(ItemId.ADAMANTITE_ARROWS.id(), 0),
		new Item(ItemId.RUNE_ARROWS.id(), 0), new Item(ItemId.BRONZE_ARROW_HEADS.id(), 200), new Item(ItemId.IRON_ARROW_HEADS.id(), 180),
		new Item(ItemId.STEEL_ARROW_HEADS.id(), 160), new Item(ItemId.MITHRIL_ARROW_HEADS.id(), 140),
		new Item(ItemId.ADAMANTITE_ARROW_HEADS.id(), 120), new Item(ItemId.RUNE_ARROW_HEADS.id(), 100), new Item(ItemId.SHORTBOW.id(), 4),
		new Item(ItemId.LONGBOW.id(), 2), new Item(ItemId.CROSSBOW.id(), 2), new Item(ItemId.OAK_SHORTBOW.id(), 4),
		new Item(ItemId.OAK_LONGBOW.id(), 4));

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.HICKTON.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "Welcome to Hickton's Archery Store",
			"Do you want to see my wares?");
		final int option = multi(p, n, false, //do not send over
			"Yes please", "No, I prefer to bash things close up");
		if (option == 0) {
			Functions.say(p, n, "Yes Please");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			Functions.say(p, n, "No, I prefer to bash things close up");
		}
	}

}

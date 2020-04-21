package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class RanaelSkirt implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 25000, 100, 65, 1,
		new Item(ItemId.BRONZE_PLATED_SKIRT.id(), 5),
		new Item(ItemId.IRON_PLATED_SKIRT.id(), 3),
		new Item(ItemId.STEEL_PLATED_SKIRT.id(), 2),
		new Item(ItemId.BLACK_PLATED_SKIRT.id(), 1),
		new Item(ItemId.MITHRIL_PLATED_SKIRT.id(), 1),
		new Item(ItemId.ADAMANTITE_PLATED_SKIRT.id(), 1)
	);

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.RANAEL.id();
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
	public void onTalkNpc(final Player player, final Npc n) {
		final String[] options;
		npcsay(player, n, "Do you want to buy any armoured skirts?",
			"Designed especially for ladies who like to fight");
		if (player.getQuestStage(Quests.FAMILY_CREST) <= 2 || player.getQuestStage(Quests.FAMILY_CREST) >= 5) {
			options = new String[]{
				"Yes please",
				"No thank you that's not my scene"
			};
		} else {
			options = new String[]{
				"Yes please",
				"No thank you that's not my scene",
				"I'm in search of a man named adam fitzharmon"
			};
		}
		int option = multi(player, n, false, options);

		if (option == 0) {
			say(player, n, "Yes Please");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			say(player, n, "No thank you that's not my scene");
		} else if (option == 2) {
			say(player, n, "I'm in search of a man named adam fitzharmon");
			npcsay(player, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}

}

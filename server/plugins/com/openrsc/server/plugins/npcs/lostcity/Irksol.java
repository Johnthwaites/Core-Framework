package com.openrsc.server.plugins.npcs.lostcity;

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

public final class Irksol implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(false, 3000, 50, 30, 2,
		new Item(ItemId.RUBY_RING.id(), 5));

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.IRKSOL.id()) {
			npcsay(p, n, "selling ruby rings",
				"The best deals in all the planes of existance");
			int option = multi(p, n, false, //do not send over
				"I'm interested in these deals",
				"No thankyou");
			switch (option) {
				case 0:
					say(p, n, "I'm interested in these deals");
					npcsay(p, n, "Take a look at these beauties");
					p.setAccessingShop(shop);
					ActionSender.showShop(p, shop);
					break;
				case 1:
					say(p, n, "no thankyou");
					break;
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.IRKSOL.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}

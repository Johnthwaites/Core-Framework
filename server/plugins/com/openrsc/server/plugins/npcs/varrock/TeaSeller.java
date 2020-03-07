package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.multi;

public final class TeaSeller implements ShopInterface,
	TalkNpcTrigger,
	TakeObjTrigger {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.CUP_OF_TEA.id(),
		20));

	@Override
	public boolean blockTakeObj(final Player p, final GroundItem i) {
		return i.getID() == ItemId.DISPLAY_TEA.id();
	}

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.TEA_SELLER.id();
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
	public void onTakeObj(final Player p, final GroundItem i) {
		if (i.getID() == ItemId.DISPLAY_TEA.id()) {
			final Npc n = p.getWorld().getNpcById(NpcId.TEA_SELLER.id());
			if (n == null) {
				return;
			}
			n.face(p);
			npcsay(p, n, "Hey ! get your hands off that tea !",
				"That's for display purposes only",
				"Im not running a charity here !");
		}
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "Greetings!",
			"Are you in need of refreshment ?");

		final String[] options = new String[]{"Yes please", "No thanks",
			"What are you selling ?"};
		int option = Functions.multi(p, n, options);
		switch (option) {
			case 0:
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			case 1:
				npcsay(p, n, "Well, if you're sure",
					"You know where to come if you do !");
				break;
			case 2:
				npcsay(p, n, "Only the most delicious infusion",
					"Of the leaves of the tea plant",
					"Grown in the exotic regions of this world...",
					"Buy yourself a cup !");
				break;
		}
	}

}

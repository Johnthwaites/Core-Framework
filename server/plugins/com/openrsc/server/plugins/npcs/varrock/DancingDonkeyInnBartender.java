package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class DancingDonkeyInnBartender implements TalkNpcTrigger {

	public static int BARTENDER = NpcId.BARTENDER_EAST_VARROCK.id();

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == BARTENDER;
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == BARTENDER) {
			say(p, n, "hello");
			npcsay(p, n, "good day to you, brave adventurer",
				"can i get you a refreshing beer");
			int menu = multi(p, n,
				"yes please",
				"no thanks",
				"how much?");
			if (menu == 0) {
				buyBeer(p, n);
			} else if (menu == 1) {
				npcsay(p, n, "let me know if you change your mind");
			} else if (menu == 2) {
				npcsay(p, n, "two gold pieces a pint",
					"so, what do you say?");
				int subMenu = multi(p, n,
					"yes please",
					"no thanks");
				if (subMenu == 0) {
					buyBeer(p, n);
				} else if (subMenu == 1) {
					npcsay(p, n, "let me know if you change your mind");
				}
			}
		}
	}

	private void buyBeer(Player p, Npc n) {
		npcsay(p, n, "ok then, that's two gold coins please");
		if (ifheld(p, ItemId.COINS.id(), 2)) {
			p.message("you give two coins to the barman");
			remove(p, ItemId.COINS.id(), 2);
			p.message("he gives you a cold beer");
			give(p, ItemId.BEER.id(), 1);
			npcsay(p, n, "cheers");
			say(p, n, "cheers");
		} else {
			p.message("you don't have enough gold");
		}
	}
}

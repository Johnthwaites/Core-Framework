package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Thief implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return inArray(n.getID(),
			NpcId.THIEF_GENERIC.id(), NpcId.THIEF_BLANKET.id(), NpcId.HEAD_THIEF.id());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		int mood = DataConversions.getRandom().nextInt(13);

		say(p, n, "Hello", "How's it going?");

		if (mood == 0)
			npcsay(p, n, "Get out of my way", "I'm in a hurry");
		else if (mood == 1)
			p.message("The man ignores you");
		else if (mood == 2)
			npcsay(p, n, "No, I don't have any spare change");
		else if (mood == 3)
			npcsay(p, n, "Very well, thank you");
		else if (mood == 4)
			npcsay(p, n, "I'm a little worried",
				"I've heard there's lots of people going about,",
				"killing citizens at random");
		else if (mood == 5) {
			npcsay(p, n, "I'm fine", "How are you?");
			say(p, n, "Very well, thank you");
		} else if (mood == 6) {
			npcsay(p, n, "Who are you?");
			say(p, n, "I am a bold adventurer");
			npcsay(p, n, "A very noble profession");
		} else if (mood == 7) {
			npcsay(p, n, "Not too bad",
				"I'm a little worried about the increase in Goblins these days");
			say(p, n, "Don't worry. I'll kill them");
		} else if (mood == 8)
			npcsay(p, n, "Hello", "Nice weather we've been having");
		else if (mood == 9)
			npcsay(p, n, "No, I don't want to buy anything");
		else if (mood == 10) {
			npcsay(p, n, "Are you asking for a fight?");
			n.setChasing(p);
		} else if (mood == 11) {
			npcsay(p, n, "How can I help you?");
			int option = multi(p, n, "Do you wish to trade?",
				"I'm in search of a quest",
				"I'm in search of enemies to kill");
			if (option == 0)
				npcsay(p, n, "No, I have nothing I wish to get rid of",
					"If you want some trading,",
					"there are plenty of shops and market stalls around though");
			else if (option == 1)
				npcsay(p, n, "I'm sorry I can't help you there");
			else if (option == 2)
				npcsay(p, n,
					"I've heard there are many fearsome creatures under the ground");
		} else if (mood == 12) {
			npcsay(p, n, "I think we need a new king");
			npcsay(p, n, "The one we've got isn't very good");
		} else if (mood == 13) {
			npcsay(p, n, "That is classified information");
		}
	}
}

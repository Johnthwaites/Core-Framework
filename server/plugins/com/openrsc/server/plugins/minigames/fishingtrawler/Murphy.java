package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler.TrawlerBoat;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Murphy implements MiniGameInterface, TalkNpcTrigger {

	/**
	 * IMPORTANT NOTES:
	 * <p>
	 * NPC: 734
	 * START EAST: 272, 741 START WEST: 320, 741 GO
	 * UNDER EAST: 248, 729 UNDER WEST: 296, 729
	 * FAIL - AFTER GO UNDER EAST: 254, 759
	 * (SHARED) FAIL - AFTER GO UNDER WEST AND/OR QUIT MINI-GAME: 302, 759 GO
	 * BACK FROM FAIL LOCATION: 550, 711
	 */

	@Override
	public int getMiniGameId() {
		return Minigames.FISHING_TRAWLER;
	}

	@Override
	public String getMiniGameName() {
		return "Fishing Trawler (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		//mini-game complete handled already
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.MURPHY_LAND.id() || n.getID() == NpcId.MURPHY_BOAT.id() || n.getID() == NpcId.MURPHY_UNRELEASED.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.MURPHY_LAND.id()) { // Murphy on land
			if (p.isIronMan(IronmanMode.Ironman.id()) || p.isIronMan(IronmanMode.Ultimate.id())
				|| p.isIronMan(IronmanMode.Hardcore.id()) || p.isIronMan(IronmanMode.Transfer.id())) {
				p.message("As an Iron Man, you cannot use the Trawler.");
				return;
			}
			if (!p.getCache().hasKey("fishingtrawler")) {
				say(p, n, "good day to you sir");
				npcsay(p, n, "well hello my brave adventurer");
				say(p, n, "what are you up to?");
				npcsay(p, n, "getting ready to go fishing of course",
						"there's no time to waste",
						"i've got all the supplies i need from the shop at the end of the pier",
						"they sell good rope, although their bailing buckets aren't too effective");
				showStartOption(p, n, true, true, true);
			} else {
				say(p, n, "hello again murphy");
				npcsay(p, n, "good day to you land lover");
				if (p.getCache().hasKey("fishing_trawler_reward")) {
					npcsay(p, n, "It looks like your net is full from last trip");
					return;
				}
				npcsay(p, n, "fancy hitting the high seas again?");
				int option = multi(p, n, "no thanks, i still feel ill from last time", "yes, lets do it");
				if (option == 0) {
					npcsay(p, n, "hah..softy");
				} else if (option == 1) {
					letsGo(p, n);
				}
			}
		} else if (n.getID() == NpcId.MURPHY_BOAT.id()) { // Murphy on the boat.
			onship(n, p);
		} else if (n.getID() == NpcId.MURPHY_UNRELEASED.id()) { // Another murphy potentially non existent
		}
	}

	private void showStartOption(Player p, Npc n, boolean showOptionFish, boolean showOptionNotSafe, boolean showOptionHelp) {
		Menu menu = new Menu();
		if (showOptionFish) {
			menu.addOption(new Option("what fish do you catch?") {
				@Override
				public void action() {
					npcsay(p, n, "i get all sorts, anything that lies on the sea bed",
							"you never know what you're going to get until...",
							"...you pull up the net");
					showStartOption(p, n, false, true, true);
				}
			});
		}
		if (showOptionNotSafe) {
			menu.addOption(new Option("your boat doesn't look too safe") {
				@Override
				public void action() {
					npcsay(p, n, "that's because it's not, the dawn thing's full of holes");
					say(p, n, "oh, so i suppose you can't go out for a while");
					npcsay(p, n, "oh no, i don't let a few holes stop an experienced sailor like me",
							"i could sail these seas in a barrel",
							"i'll be going out soon enough");
					showStartOption(p, n, true, false, true);
				}
			});
		}
		if (showOptionHelp) {
			menu.addOption(new Option("could i help?") {
				@Override
				public void action() {
					npcsay(p, n, "well of course you can",
							"i'll warn you though, the seas are merciless",
							"and with out fishing experience you won't catch much");
					Functions.mes(p, "you need a fishing level of 15 or above to catch any fish on the trawler");
					npcsay(p, n, "on occasions the net rip's, so you'll need some rope to repair it");
					say(p, n, "rope...ok");
					npcsay(p, n, "there's also a slight problem with leaks");
					say(p, n, "leaks!");
					npcsay(p, n, "nothing some swamp paste won't fix");
					say(p, n, "swamp paste?");
					npcsay(p, n, "oh, and one more thing...",
							"..i hope you're a good swimmer");
					int gooption = multi(p, n, "actually, i think i'll leave it", "i'll be fine, lets go",
						"what's swamp paste?");
					switch (gooption) {
						case 0:
							npcsay(p, n, "bloomin' land lover's");
							break;
						case 1:
							letsGo(p, n);
							break;
						case 2:
							npcsay(p, n, "swamp tar mixed with flour...",
									"...which is then heated over a fire");
							say(p, n, "where can i find swamp tar?");
							npcsay(p, n, "unfortunately the only supply of swamp tar is in the swamps below lumbridge");
							break;
					}
				}
			});
		}
		menu.showMenu(p);
	}

	private void letsGo(Player p, Npc n) {
		npcsay(p, n, "would you like to sail east or west?");
		int choice = multi(p, n, false, //do not send over
				"east please", "west please");
		FishingTrawler instance = null;
		if (choice == 0 || choice == 1) {
			if (choice == 0) {
				instance = p.getWorld().getFishingTrawler(TrawlerBoat.EAST);
			} else if (choice == 1) {
				instance = p.getWorld().getFishingTrawler(TrawlerBoat.WEST);
			}
			if (instance != null && instance.isAvailable()) {
				npcsay(p, n, "good stuff, jump aboard",
						"ok m hearty, keep your eys pealed",
						"i need you to clog up those holes quick time");
				say(p, n, "i'm ready and waiting");
				if (!p.getCache().hasKey("fishingtrawler")) {
					p.getCache().store("fishingtrawler", true);
				}
				instance.addPlayer(p);
			} else {
				npcsay(p, n, "sorry m hearty it appeears the boat is in the middle of a game");
				p.message("The boat should be available in a couple of minutes");
			}
		}
	}

	private void onship(Npc n, Player p) {
		npcsay(p, n, "whoooahh sailor");
		int option = multi(p, n, "i've had enough,  take me back", "how you doing murphy?");
		if (option == 0) {
			npcsay(p, n, "haa .. the soft land lovers lost there see legs have they?");
			say(p, n, "something like that");
			npcsay(p, n, "we're too far out now, it'd be dangerous");
			option = multi(p, n, false, //do not send over
					"I insist murphy, take me back", "Ok then murphy, just keep us afloat");
			if (option == 0) {
				say(p, n, "i insist murphy, take me back");
				npcsay(p, n, "ok, ok, i'll try, but don't say i didn't warn you");
				mes(p, 1900, "murphy sharply turns the large ship", "the boats gone under", "you're lost at sea!");
				if (p.getWorld().getFishingTrawler(p) != null) {
					p.getWorld().getFishingTrawler(p).quitPlayer(p);
				}
				else {
					p.teleport(302, 759, false);
					ActionSender.hideFishingTrawlerInterface(p);
				}
			} else if (option == 1) {
				say(p, n, "ok then murphy, just keep us afloat");
				npcsay(p, n, "that's the attitude sailor");
			}
		}
		if (option == 1) {
			int rnd = DataConversions.random(0,2);
			if (rnd == 0) {
				npcsay(p, n, "don't bail..it's a waste of time",
						"just fill those holes");
			} else if (rnd == 1) {
				npcsay(p, n, "it's a fierce sea today traveller",
						"you best hold on tight");
			} else if (rnd == 2) {
				npcsay(p, n, "get those fishey's");
			}
		}
	}
}

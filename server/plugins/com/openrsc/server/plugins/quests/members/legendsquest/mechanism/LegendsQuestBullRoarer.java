package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestBullRoarer implements OpInvTrigger {
	private static final Logger LOGGER = LogManager.getLogger(LegendsQuestBullRoarer.class);
	private boolean inKharaziJungle(Player p) {
		return p.getLocation().inBounds(338, 869, 477, 908);
	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.BULL_ROARER.id();
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.BULL_ROARER.id()) {
			mes(p, 1300, "You start to swing the bullroarer above your head.",
				"You feel a bit silly at first, but soon it makes an interesting sound.");
			if (inKharaziJungle(p)) {
				mes(p, 1300, "You see some movement in the trees...");
				attractNatives(p);
			} else {
				mes(p, 1300, "Nothing much seems to happen though.");
				Npc forester = ifnearvisnpc(p, NpcId.JUNGLE_FORESTER.id(), 10);
				if (forester != null) {
					npcsay(p, forester, "You might like to use that when you get into the ",
						"Kharazi jungle, it might attract more natives...");
				}
			}
		}
	}

	private void attractNatives(Player p) {
		int controlRandom = DataConversions.getRandom().nextInt(4);
		if (controlRandom == 0) {
			mes(p, 1300, "...but nothing else much seems to happen.");
		} else if (controlRandom >= 1 && controlRandom <= 2) {
			mes(p, 1300, "...and a tall, dark, charismatic looking native approaches you.");
			Npc gujuo = ifnearvisnpc(p, NpcId.GUJUO.id(), 15);
			if (gujuo == null) {
				gujuo = addnpc(p.getWorld(), NpcId.GUJUO.id(), p.getX(), p.getY());
				delayedRemoveGujuo(p, gujuo);
			}
			if (gujuo != null) {
				gujuo.resetPath();
				gujuo.teleport(p.getX(), p.getY());
				gujuo.initializeTalkScript(p);
				delay(650);
				npcWalkFromPlayer(p, gujuo);
			}
		} else if (controlRandom == 3) {
			Npc nativeNpc = Functions.ifnearvisnpc(p, 5, NpcId.OOMLIE_BIRD.id(), NpcId.KARAMJA_WOLF.id(), NpcId.JUNGLE_SPIDER.id(), NpcId.JUNGLE_SAVAGE.id());
			if (nativeNpc != null) {
				mes(p, 1300, "...and a nearby " + (nativeNpc.getDef().getName().contains("bird") ? nativeNpc.getDef().getName() : "Kharazi " + nativeNpc.getDef().getName().toLowerCase()) + " takes a sudden dislike to you.");
				nativeNpc.setChasing(p);
				mes(p, 0, "And attacks...");
			} else {
				attractNatives(p);
			}
		}
	}

	private void delayedRemoveGujuo(Player p, Npc n) {
		try {
			p.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(p.getWorld(), null, 60000 * 3, "Delayed Remove Gujuo") {
				@Override
				public void run() {
					if (!p.isLoggedIn() || p.isRemoved()) {
						n.remove();
						stop();
						return;
					}
					if (n.isRemoved()) {
						stop();
						return;
					}
					if (!inKharaziJungle(p)) {
						n.remove();
						stop();
						return;
					}
					int yell = DataConversions.random(0, 3);
					if (yell == 0) {
						npcsay(p, n, "I am tired Bwana, I must go and rest...");
					}
					if (yell == 1) {
						npcsay(p, n, "I must visit my people now...");
					} else if (yell == 2) {
						npcsay(p, n, "I must go and hunt now Bwana..");
					} else if (yell == 3) {
						npcsay(p, n, "I have to collect herbs now Bwana...");
					} else {
						npcsay(p, n, "I have work to do Bwana, I may see you again...");
					}
					getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(), null, 1900, "Legends Quest Gujuo Disappears") {
						public void action() {
							p.message("Gujuo disapears into the Kharazi jungle as swiftly as he appeared...");
							n.remove();
						}
					});
					stop();
				}
			});
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}

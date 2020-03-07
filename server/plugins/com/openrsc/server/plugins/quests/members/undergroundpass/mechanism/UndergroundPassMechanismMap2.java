package com.openrsc.server.plugins.quests.members.undergroundpass.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.quests.members.undergroundpass.obstacles.UndergroundPassObstaclesMap2;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassMechanismMap2 implements UseLocTrigger {

	/**
	 * ITEMS_TO_FLAMES: Unicorn horn, coat of arms red and blue.
	 **/
	private static int[] ITEMS_TO_FLAMES = {ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), ItemId.COAT_OF_ARMS_RED.id(), ItemId.COAT_OF_ARMS_BLUE.id()};

	/**
	 * OBJECT IDs
	 **/
	private static int BOULDER = 867;

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST && item.getCatalogId() == ItemId.ROPE.id())
				|| (obj.getID() == UndergroundPassObstaclesMap2.PASSAGE && item.getCatalogId() == ItemId.PLANK.id())
				|| (obj.getID() == BOULDER && item.getCatalogId() == ItemId.RAILING.id())
				|| (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && inArray(item.getCatalogId(), ITEMS_TO_FLAMES))
				|| (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && item.getCatalogId() == ItemId.STAFF_OF_IBAN.id());
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST && item.getCatalogId() == ItemId.ROPE.id()) {
			if (p.getX() == 763 && p.getY() == 3463) {
				p.message("you can't reach the grill from here");
			} else {
				Functions.mes(p, "you tie the rope to the grill...");
				p.message("..and poke it through to the otherside");
				if (!p.getCache().hasKey("rope_wall_grill")) {
					p.getCache().store("rope_wall_grill", true);
				}
			}
		}
		else if (item.getCatalogId() == ItemId.PLANK.id() && obj.getID() == UndergroundPassObstaclesMap2.PASSAGE) {
			p.message("you carefully place the planks over the pressure triggers");
			p.message("you walk across the wooden planks");
			remove(p, ItemId.PLANK.id(), 1);
			p.teleport(735, 3489);
			delay(850);
			if (obj.getX() == 737) {
				p.teleport(732, 3489);
			} else if (obj.getX() == 733) {
				p.teleport(738, 3489);
			}
		}
		else if (obj.getID() == BOULDER && item.getCatalogId() == ItemId.RAILING.id()) {
			Functions.mes(p, "you use the pole as leverage...",
				"..and tip the bolder onto its side");
			delloc(obj);
			Functions.addloc(obj.getWorld(), obj.getLoc(), 5000);
			p.message("it tumbles down the slope");
			if (p.getQuestStage(Quests.UNDERGROUND_PASS) == 3) {
				p.updateQuestStage(Quests.UNDERGROUND_PASS, 4);
			}
		}
		else if (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && inArray(item.getCatalogId(), ITEMS_TO_FLAMES)) {
			Functions.mes(p, "you throw the " + item.getDef(p.getWorld()).getName().toLowerCase() + " into the flames");
			if (!atQuestStages(p, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
				if (!p.getCache().hasKey("flames_of_zamorak1") && item.getCatalogId() == ItemId.UNDERGROUND_PASS_UNICORN_HORN.id()) {
					p.getCache().store("flames_of_zamorak1", true);
				}
				if (!p.getCache().hasKey("flames_of_zamorak2") && item.getCatalogId() == ItemId.COAT_OF_ARMS_RED.id()) {
					p.getCache().store("flames_of_zamorak2", true);
				}
				int stage = 0;
				if (item.getCatalogId() == ItemId.COAT_OF_ARMS_BLUE.id()) {
					if (!p.getCache().hasKey("flames_of_zamorak3")) {
						p.getCache().set("flames_of_zamorak3", 1);
					} else {
						stage = p.getCache().getInt("flames_of_zamorak3");
						if (stage < 2)
							p.getCache().set("flames_of_zamorak3", stage + 1);
					}
				}
			}
			remove(p, item.getCatalogId(), 1);
			p.message("you hear a howl in the distance");
		}
		else if (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && item.getCatalogId() == ItemId.STAFF_OF_IBAN.id()) {
			Functions.mes(p, "you hold the staff above the well");
			displayTeleportBubble(p, p.getX(), p.getY(), true);
			p.message("and feel the power of zamorak flow through you");
			p.getCache().set("Iban blast_casts", 25);
		}
	}
}

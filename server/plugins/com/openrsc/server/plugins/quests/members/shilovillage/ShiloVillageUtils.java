package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.constants.*;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.DropObjTrigger;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageUtils implements DropObjTrigger, OpInvTrigger, UseInvTrigger, TakeObjTrigger {

	static void BUMPY_DIRT_HOLDER(Player p) {
		p.message("Do you want to try to crawl through the fissure?");
		if (p.getCache().hasKey("SV_DIG_ROPE")) {
			p.message("You see that a rope is attached nearby");
		}
		int menu = multi(p,
			"Yes, I'll give it a go!",
			"No thanks, it looks a bit dark!");
		if (menu == 0) {
			p.message("You start to contort your body...");
			Functions.mes(p, "With some dificulty you manage to push your body",
					"through the small crack in the rock.");
			if (!p.getCache().hasKey("SV_DIG_ROPE")) {
				Functions.mes(p, "As you squeeze out of the hole...");
				p.message("you realise that there is a huge drop underneath you");
				p.message("You begin falling....");
				p.teleport(380, 3692);
				delay(500);
				say(p, null, "Ahhhhh!");
				p.damage(1);
				p.message("Your body is battered as you hit the cavern walls.");
				say(p, null, "Ooooff!");
				p.damage(1);
				delay(500);
				p.teleport(352, 3650);
				p.damage((int) (getCurrentLevel(p, Skills.HITS) * 0.2 + 10));
				Functions.mes(p, "You hit the floor and it knocks the wind out of you!");
				say(p, null, "Ugghhhh!!");
			}
			else {
				Functions.mes(p, "You squeeze through the fissure in the granite",
						"And once through, you cleverly use the rope to slowly lower",
						"yourself to the floor.");
				say(p, null, "Yay!");
				p.teleport(352, 3650);
			}
			p.incExp(Skills.AGILITY, 30, true);
			if(p.getQuestStage(Quests.SHILO_VILLAGE) == 2) {
				p.updateQuestStage(Quests.SHILO_VILLAGE, 3);
			}
		} else if (menu == 1) {
			p.message("You think better of attempting to squeeze your body into the fissure.");
			say(p, null, "It looked very dangerous, and dark...",
				"scarey!");
		}
	}

	public static boolean succeed(Player player, int req) {
		int level_difference = getCurrentLevel(player, Skills.AGILITY) - req;
		int percent = random(1, 100);

		if (level_difference < 0)
			return true;
		if (level_difference >= 15)
			level_difference = 80;
		if (level_difference >= 20)
			level_difference = 90;
		else
			level_difference = 30 + level_difference;

		return percent <= level_difference;
	}

	// Zadimus: 589
	private void dropZadimusCorpse(Player p) {
		Functions.mes(p, "You feel an uneartly compunction to bury this corpse!");
		if (p.getLocation().inBounds(445, 749, 449, 753)) {
			Functions.mes(p, "You hear an unearthly moaning sound as you see",
				"an apparition materialises right in front of you.");
			Npc zadimus = addnpc(p.getWorld(), NpcId.ZADIMUS.id(), p.getX(), p.getY(), 60000);
			delay(500);
			if (zadimus != null) {
				npcsay(p, zadimus, "You have released me from my torture, and now I shall aid you");
				delay(500);
				npcsay(p, zadimus, "You seek to dispell the one who tortured and killed me");
				delay(500);
				npcsay(p, zadimus, "Remember this...");
				delay(500);
				npcsay(p, zadimus, "'I am the key, but only kin may approach her.'");
				Functions.mes(p, "The apparition disapears into the ground where you buried the corpse.");
				zadimus.remove();
				Functions.mes(p, "You see the ground in front of you shake ",
					"as a shard of bone forces its way to the surface.");
				p.message("You take the bone shard and place it in your inventory.");
				p.getCarriedItems().getInventory().replace(ItemId.ZADIMUS_CORPSE.id(), ItemId.BONE_SHARD.id());
				if (p.getQuestStage(Quests.SHILO_VILLAGE) == 3) {
					p.setQuestStage(Quests.SHILO_VILLAGE, 4);
				}
			}
		} else {
			Functions.mes(p, "You hear a ghostly wailing sound coming from the corpse",
				"and a whispering voice says,");
			p.message("'@yel@Zadimus: Let me rest in a sacred place and assist you I will'");
		}
	}

	@Override
	public boolean blockDropObj(Player p, Item i, Boolean fromInventory) {
		return inArray(i.getCatalogId(), ItemId.STONE_PLAQUE.id(), ItemId.CRUMPLED_SCROLL.id(), ItemId.TATTERED_SCROLL.id(), ItemId.ZADIMUS_CORPSE.id(),
				ItemId.BONE_KEY.id(), ItemId.BONE_BEADS.id(), ItemId.BONE_SHARD.id(), ItemId.LOCATING_CRYSTAL.id(), ItemId.BERVIRIUS_TOMB_NOTES.id(),
				ItemId.SWORD_POMMEL.id(), ItemId.RASHILIYA_CORPSE.id(), ItemId.BEADS_OF_THE_DEAD.id());
	}

	//533
	@Override
	public void onDropObj(Player p, Item i, Boolean fromInventory) {
		if (i.getCatalogId() == ItemId.RASHILIYA_CORPSE.id()) {
			Functions.mes(p, "The remains of Rashiliyia look quite delicate.",
				"You sense that a spirit needs to be put to rest.");
			p.message("Are you sure that you want to drop the remains ?");
			int menu = multi(p,
				"Yes, I am sure.",
				"No, I'll keep hold of the remains.");
			if (menu == 0) {
				if (p.getCache().hasKey("dolmen_zombie")) {
					p.getCache().remove("dolmen_zombie");
				}
				if (p.getCache().hasKey("dolmen_skeleton")) {
					p.getCache().remove("dolmen_skeleton");
				}
				if (p.getCache().hasKey("dolmen_ghost")) {
					p.getCache().remove("dolmen_ghost");
				}
				remove(p, ItemId.RASHILIYA_CORPSE.id(), 1);
				Functions.mes(p, "You drop Rashiliyias remains on the ground.",
					"The bones turn to dust and forms into the shape of a human figure.");
				Npc rash = addnpc(p.getWorld(), NpcId.RASHILIYIA.id(), p.getX(), p.getY(), 30000);
				Functions.mes(p, "The figure turns to you and you hear a cackling, croaky voice on the air.");
				if (rash != null) {
					npcsay(p, rash, "Many thanks for releasing me!",
						"Please excuse me, I must attend to my plans!");
					rash.remove();
				}
				p.message("The figure turns and soars away quickly disapearing into the distance.");
			} else if (menu == 1) {
				p.message("You decide to keep hold of Rashiliyias remains.");
			}
		}
		else if (i.getCatalogId() == ItemId.BEADS_OF_THE_DEAD.id()) {
			Functions.mes(p, "Are you sure you want to drop the Beads of the Dead?");
			p.message("It looks very rare and unique.");
			int menu = multi(p,
				"Yes, I'm sure.",
				"Nope, I've had second thoughts.");
			if (menu == 0) {
				Functions.mes(p, "As the necklace hits the floor, it disintigrates",
					"into a puff of white powder.");
				p.message("and you start to wonder if it ever really existed?");
				remove(p, ItemId.BEADS_OF_THE_DEAD.id(), 1);
			} else if (menu == 1) {
				p.message("You decide not to drop the Beads of the Dead.");
			}
		}
		else if (i.getCatalogId() == ItemId.BONE_BEADS.id()) {
			Functions.mes(p, "As the beads hit the floor, they disintegrate into");
			p.message("puffs of white powder.");
			remove(p, ItemId.BONE_BEADS.id(), 1);
		}
		else if (i.getCatalogId() == ItemId.BERVIRIUS_TOMB_NOTES.id()) {
			p.message("As you drop the delicate scrolls onto the floor, they");
			p.message("disintegrate immediately.");
			if (!p.getCache().hasKey("dropped_writing")) {
				p.getCache().store("dropped_writing", true);
			}
			remove(p, ItemId.BERVIRIUS_TOMB_NOTES.id(), 1);
		}
		else if (i.getCatalogId() == ItemId.LOCATING_CRYSTAL.id()) {
			p.message("Are you sure you want to drop this crystal?");
			p.message("It looks very delicate and it may break.");
			int menu = multi(p,
				"Yes, I am sure.",
				"No, I've reconsidered, I'll keep it!");
			if (menu == 0) {
				Functions.mes(p, "As you drop the cystal, it hits a rock and explodes.");
				p.message("You are lascerated by shards of glass.");
				p.damage(10);
				remove(p, ItemId.LOCATING_CRYSTAL.id(), 1);
			} else if (menu == 1) {
				p.message("You decide to keep the Locating Crystal ");
				p.message("tucked into your inventory safe and sound.");
			}
		}
		else if (i.getCatalogId() == ItemId.SWORD_POMMEL.id()) {
			Functions.mes(p, "You drop the sword pommel on the floor.");
			p.message("It turns to dust as soon as it hits the ground.");
			remove(p, ItemId.SWORD_POMMEL.id(), 1);
		}
		else if (i.getCatalogId() == ItemId.BONE_KEY.id()) {
			p.message("This looks quite valuable.");
			p.message("As you go to throw the item away");
			p.message("Zadimus' words come to you again.");
			p.message("@yel@'I am the key, but only kin may approach her'");
		}
		else if (i.getCatalogId() == ItemId.BONE_SHARD.id()) {
			p.message("You cannot bring yourself to drop this item.");
			p.message("You remember the words that Zadimus said when he appeared");
			p.message("in front of you.");
			p.message("@yel@'I am the key, but only kin may approach her.");
		}
		else if (i.getCatalogId() == ItemId.CRUMPLED_SCROLL.id()) {
			p.message("This looks quite important, are you sure you want to drop it?");
			int menu = multi(p,
				"Yes, I'm sure.",
				"No, I think I'll keep it.");
			if (menu == 0) {
				p.message("As you drop the item, it gets carried off by the wind.");
				p.message("never to be seen again.");
				remove(p, ItemId.CRUMPLED_SCROLL.id(), 1);
			} else if (menu == 1) {
				p.message("You decide against throwing the item away.");
			}
		}
		else if (i.getCatalogId() == ItemId.TATTERED_SCROLL.id()) {
			p.message("This looks quite important, are you sure you want to drop it?");
			int menu = multi(p,
				"Yes, I'm sure.",
				"No, I think I'll keep it.");
			if (menu == 0) {
				p.message("You decide to throw the item away.");
				p.message("As you drop the item, it falls down a narrow crevice.");
				p.message("never to be seen again.");
				remove(p, ItemId.TATTERED_SCROLL.id(), 1);
			} else if (menu == 1) {
				p.message("You decide against throwing the item away.");
			}
		}
		else if (i.getCatalogId() == ItemId.ZADIMUS_CORPSE.id()) {
			dropZadimusCorpse(p);
		}
		else if (i.getCatalogId() == ItemId.STONE_PLAQUE.id()) {
			p.message("This looks quite important, are you sure you want to drop it?");
			int menu = multi(p,
				"Yes, I'm sure.",
				"No, I think I'll keep it.");
			if (menu == 0) {
				p.message("As you drop the item, it bounces into a stream.");
				p.message("never to be seen again.");
				remove(p, ItemId.STONE_PLAQUE.id(), 1);
			} else if (menu == 1) {
				p.message("You decide against throwing the item away.");
			}
		}
	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return inArray(item.getCatalogId(), ItemId.ZADIMUS_CORPSE.id(), ItemId.CRUMPLED_SCROLL.id(), ItemId.TATTERED_SCROLL.id(), ItemId.STONE_PLAQUE.id(),
				ItemId.BONE_SHARD.id(), ItemId.BERVIRIUS_TOMB_NOTES.id(), ItemId.LOCATING_CRYSTAL.id(), ItemId.BONE_KEY.id(), ItemId.RASHILIYA_CORPSE.id());
	}

	@Override
	public void onOpInv(Item item, Player p, String command) { // bury corpse
		if (item.getCatalogId() == ItemId.RASHILIYA_CORPSE.id()) {
			p.message("Nothing interesting happens");
		}
		else if (item.getCatalogId() == ItemId.BONE_KEY.id()) { // bone key
			p.message("The key is intricately carved out of bone.");
		}
		else if (item.getCatalogId() == ItemId.LOCATING_CRYSTAL.id()) { // activate crystal
			Functions.mes(p, "You feel the crystal trying to draw upon your spiritual energy.");
			p.message("Do you want to let it.");
			int menu = multi(p,
				"Yes, that seems fine.",
				"No, it sounds a bit dangerous.");
			if (menu == 0) {
				if (getCurrentLevel(p, Skills.PRAYER) < 10) {
					p.message("You have no spiritual energy that the crystal can draw from.");
					delay(1200);
					p.message("You need to have at least 10 prayer points for it to work.");
					return;
				}
				int objectX = 351;
				//TODO: check ranges
				if (objectX - p.getX() <= 5 && objectX - p.getX() >= -5) {
					p.message("The crystal blazes brilliantly.");
					p.getSkills().subtractLevel(Skills.PRAYER, 1);
				} else if (objectX - p.getX() <= 7 && objectX - p.getX() >= -7) {
					p.message("@yel@The crystal is very bright.");
					p.getSkills().subtractLevel(Skills.PRAYER, 1);
				}else if (objectX - p.getX() <= 10 && objectX - p.getX() >= -10) {
					p.message("@red@The crystal glows brightly");
					p.getSkills().subtractLevel(Skills.PRAYER, 1);
				} else if (objectX - p.getX() <= 20 && objectX - p.getX() >= -20) {
					p.message("The crystal glows feintly");
					p.getSkills().subtractLevel(Skills.PRAYER, 1);
				} else {
					p.message("Nothing seems different about the Crystal.");
					p.getSkills().subtractLevel(Skills.PRAYER, 2);
				}
			} else if (menu == 1) {
				p.message("You decide not to allow the crystal to draw spiritual energy from your body.");
			}
		}
		else if (item.getCatalogId() == ItemId.BERVIRIUS_TOMB_NOTES.id()) { // read tomb notes
			p.setBusy(true);
			p.message("This scroll is a collection of writings..");
			delay(1200);
			p.message("Some of them are just scraps of papyrus with what looks like random scribblings.");
			delay(1200);
			p.message("Which would you like to read?");
			delay(1200);
			p.setBusy(false);
			int menu = multi(p,
				"Tattered Yellow papyrus",
				"Decayed White papyrus",
				"Crusty Orange papyrus");
			if (menu == 0) {
				ActionSender.sendBox(p, "...and rest like your mother who is silent in the peace of her "
					+ "tomb far to the North of Ah Za Rhoon. Near the sea, and under "
					+ "the hills deep in the underground to watch all of nature from the "
					+ "darkness of her final resting place.", false);
			} else if (menu == 1) {
				ActionSender.sendBox(p, "...Rashiliyia did so love objects of beauty. Her tomb was "
					+ "adnorned with crystals that glowed brightly when near to each other.", false);
			} else if (menu == 2) {
				ActionSender.sendBox(p, "...the sphere is activated when power of a spiritual nature is "
					+ "expended upon it, this can be very draining on the body...", false);
			}
		}
		else if (item.getCatalogId() == ItemId.BONE_SHARD.id()) {
			p.message("The words of Zadimus come back to you.");
			p.message("@yel@'I am the key, but only kin may approach her.'");
		}
		else if (item.getCatalogId() == ItemId.ZADIMUS_CORPSE.id()) {
			dropZadimusCorpse(p);
		}
		else if (item.getCatalogId() == ItemId.CRUMPLED_SCROLL.id()) {
			Functions.mes(p, "This looks like part of a scroll about Rashiliyia",
				"Would you like to read it?");
			int menu = multi(p,
				"Yes please!",
				"No thanks.");
			if (menu == 0) {
				ActionSender.sendBox(p, "Rashiliyia's rage went unchecked.% %"
					+ "She killed without mercy for revenge of her sons life.% %"
					+ "Like a spectre through the night she entered houses and one "
					+ "by one quietly strangled life from the occupants.% %"
					+ "It is said that only a handful survived, protected by necklace wards to keep the Witch Queen at bay.", true);
			} else if (menu == 1) {
				p.message("You decide to leave the scroll well alone.");
			}
		}
		else if (item.getCatalogId() == ItemId.TATTERED_SCROLL.id()) {
			Functions.mes(p, "This looks like part of a scroll about someone called Berverius..");
			p.message("Would you like to read it?");
			int menu = multi(p,
				"Yes please.",
				"No thanks.");
			if (menu == 0) {
				ActionSender.sendBox(p, "Bervirius, song of King Danthalas, was killed in battle.% %"
					+ "His devout Mother Rashiliyia was so heartbroken that she% swore fealty to Zamorak "
					+ "if he would return her son to her.% %"
					+ "Bervirius returned as an undead creature and terrorized the "
					+ "King and Queen. Many guards died fighting the Undead "
					+ "Berverious, eventually the undead Bervirius was set on fire and "
					+ "soon only the bones remained.% %"
					+ "His remains were taken far to the South, and then towards the "
					+ "setting sun to a tomb that is surrounded by and level with the "
					+ "sea. The only remedy for containing the spirits of witches and undead.", true);
			} else if (menu == 1) {
				p.message("You decide not to open the scroll but instead put it carefully back into your inventory.");
			}
		}
		else if (item.getCatalogId() == ItemId.STONE_PLAQUE.id()) {
			Functions.mes(p, "The markings are very intricate. It's a very strange language.",
					"The meaning of it evades you though.");
		}
	}

	@Override
	public boolean blockUseInv(Player p, Item item1, Item item2) {
		//chisel and pommel sword / bone beads and wire / chisel and bone shard
		return Functions.compareItemsIds(item1, item2, ItemId.CHISEL.id(), ItemId.SWORD_POMMEL.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.BONE_BEADS.id(), ItemId.BRONZE_WIRE.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.CHISEL.id(), ItemId.BONE_SHARD.id());
	}

	@Override
	public void onUseInv(Player p, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.BONE_BEADS.id(), ItemId.BRONZE_WIRE.id())) {
			if (getCurrentLevel(p, Skills.CRAFTING) < 20) {
				p.message("You need a level of 20 Crafting to craft this.");
				return;
			}
			Functions.mes(p, "You successfully craft the beads and Bronze Wire ");
			p.message("into a necklace which you name, 'Beads of the dead'");
			remove(p, ItemId.BRONZE_WIRE.id(), 1);
			p.getCarriedItems().getInventory().replace(ItemId.BONE_BEADS.id(), ItemId.BEADS_OF_THE_DEAD.id());
		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.CHISEL.id(), ItemId.BONE_SHARD.id())) {
			if (p.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				p.message("You're not quite sure what to make with this.");
				return;
			}
			if (p.getCache().hasKey("can_chisel_bone")) {
				if (getCurrentLevel(p, Skills.CRAFTING) < 20) {
					p.message("You need a level of 20 Crafting to craft this.");
					return;
				}
				Functions.mes(p, "Remembering Zadimus' words and the strange bone lock,",
					"you start to craft the bone.");
				p.message("You succesfully make a key out of the bone shard.");
				p.getCarriedItems().getInventory().replace(ItemId.BONE_SHARD.id(), ItemId.BONE_KEY.id());
				p.incExp(Skills.CRAFTING, 35, true);
			} else {
				Functions.mes(p, "You're not quite sure what to make with this.");
				p.message("Perhaps it will come to you as you discover more about Rashiliyia?");
			}
		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.CHISEL.id(), ItemId.SWORD_POMMEL.id())) {
			if (getCurrentLevel(p, Skills.CRAFTING) < 20) {
				p.message("You need a level of 20 Crafting to craft this.");
				return;
			}
			Functions.mes(p, "You prepare the ivory pommel and the chisel to start crafting...",
				"You successfully craft some of the ivory into beads.");
			p.message("They may look good as part of a necklace.");
			p.incExp(Skills.CRAFTING, 35, true);
			p.getCarriedItems().getInventory().replace(ItemId.SWORD_POMMEL.id(), ItemId.BONE_BEADS.id());
		}
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return i.getID() == ItemId.COINS.id() && i.getX() == 358 && i.getY() == 3626;
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.COINS.id() && i.getX() == 358 && i.getY() == 3626) {
			if (p.getCache().hasKey("coins_shilo_cave")) {
				i.remove();
				give(p, ItemId.COINS.id(), 10);
				p.message("The coins turn to dust in your hand...");
			} else {
				Functions.mes(p, "As soon as you touch the coins...",
					"You hear the grinding sound of bones");
				p.message("against stone as you see skeletons and ");
				delay(1000);
				p.message("Zombies rising up out of the ground.");
				addnpc(p.getWorld(), 40, p.getX() - 1, p.getY() + 1, 60000);
				addnpc(p.getWorld(), 40, p.getX() - 1, p.getY() - 1, 60000);

				addnpc(p.getWorld(), 542, p.getX() + 2, p.getY() + 1, 60000);
				addnpc(p.getWorld(), 542, p.getX() + 1, p.getY() - 1, 60000);
				p.message("The coins turn to dust in your hands.");
				p.getCache().store("coins_shilo_cave", true);
			}
		}
	}
}

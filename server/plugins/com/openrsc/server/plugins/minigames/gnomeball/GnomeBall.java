package com.openrsc.server.plugins.minigames.gnomeball;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.impl.BallProjectileEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.minigames.gnomeball.GnomeField.Zone;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeBall implements MiniGameInterface, UsePlayerTrigger, TakeObjTrigger,
	OpInvTrigger, OpLocTrigger {

	private static final int[][] SCORES_XP = {{20, 30, 35, 40, 220} , {40, 50, 60, 70, 220}};

	@Override
	public int getMiniGameId() {
		return Minigames.GNOME_BALL;
	}

	@Override
	public String getMiniGameName() {
		return "Gnome Ball (members)";
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
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		if (item.getCatalogId() == ItemId.GNOME_BALL.id()) {
			if (otherPlayer.isIronMan(IronmanMode.Ironman.id()) || otherPlayer.isIronMan(IronmanMode.Ultimate.id())
				|| otherPlayer.isIronMan(IronmanMode.Hardcore.id()) || otherPlayer.isIronMan(IronmanMode.Transfer.id())) {
				player.message(otherPlayer.getUsername() + " is an Iron Man. They stand alone.");
			} else {
				// does not matter where the players are at, neither in the field or wild,
				// nor if they have free inventory space
				player.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(player.getWorld(), player, otherPlayer, 3) {
					@Override
					public void doSpell() {
						if (otherPlayer.isPlayer()) {
							player.getCarriedItems().remove(item);
							player.message("you throw the ball");

							// only the shops interface is reset is closed if they are accessing it
							if (otherPlayer.accessingShop()) {
								otherPlayer.resetShop();
							}

							otherPlayer.getCarriedItems().getInventory().add(item);
							otherPlayer.message("Warning! " + player.getUsername() + " is shooting at you!");
							otherPlayer.message("you catch the ball");
							say(player, null, "Good catch");
						}
					}
				});
			}
		}
	}

	@Override
	public void onOpInv(Item item, Player player, String command) {
		Zone playerZone = GnomeField.getInstance().resolvePositionToZone(player);
		if (playerZone == Zone.ZONE_NO_PASS) {
			player.message("you can't make the pass from here");
		} else if (playerZone == Zone.ZONE_PASS) {
			Npc gnome_team;
			if (player.getY() <= 449) {
				gnome_team = ifnearvisnpc(player, GnomeNpcs.GNOME_BALLER_NORTH, 10);
			}
			else {
				gnome_team = ifnearvisnpc(player, GnomeNpcs.GNOME_BALLER_SOUTH, 10);
			}
			if (gnome_team != null) {
				gnome_team.initializeIndirectTalkScript(player);
			}
		} else if (playerZone == Zone.ZONE_1XP_OUTER || playerZone == Zone.ZONE_1XP_INNER) {
			player.setAttribute("throwing_ball_game", true);
			Npc goalie = ifnearvisnpc(player, GnomeNpcs.GOALIE, 15);
			player.setBusyTimer(600);
			player.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(player.getWorld(), player, goalie, 3) {
				@Override
				public void doSpell() {
					//logic to try to score from 1xp
					thinkbubble(player, new Item(ItemId.GNOME_BALL.id()));
					Functions.mes(player, "you throw the ball at the goal");
					remove(player, ItemId.GNOME_BALL.id(), 1);
					int random = DataConversions.random(0, 4);
					if (random < 2 + (playerZone == Zone.ZONE_1XP_INNER ? 2 : 0)) {
						Functions.mes(player, "it flys through the net...",
								"into the hands of the goal catcher");
						Npc cheerleader = ifnearvisnpc(player, GnomeNpcs.CHEERLEADER, 10);
						if (cheerleader != null) {
							cheerLeaderCelebrate(player, cheerleader);
						}
						handleScore(player, 0);
					} else {
						if (DataConversions.random(0, 2) < 2 || playerZone == Zone.ZONE_1XP_OUTER) {
							Functions.mes(player, "the ball flys way over the net");
						} else {
							Functions.mes(player, "the ball just misses the net");
						}
					}
				}
			});
		} else if (playerZone == Zone.ZONE_2XP_OUTER || playerZone == Zone.ZONE_2XP_INNER) {
			player.setAttribute("throwing_ball_game", true);
			Npc goalie = ifnearvisnpc(player, GnomeNpcs.GOALIE, 15);
			player.setBusyTimer(600);
			player.getWorld().getServer().getGameEventHandler().add(new BallProjectileEvent(player.getWorld(), player, goalie, 3) {
				@Override
				public void doSpell() {
					//logic to try to score from 2xp
					thinkbubble(player, new Item(ItemId.GNOME_BALL.id()));
					Functions.mes(player, "you throw the ball at the goal");
					remove(player, ItemId.GNOME_BALL.id(), 1);
					int random = DataConversions.random(0, 9);
					if (random < 4 + (playerZone == Zone.ZONE_2XP_INNER ? 2 : 0)) {
						Functions.mes(player, "it flys through the net...",
								"into the hands of the goal catcher");
						Npc cheerleader = ifnearvisnpc(player, GnomeNpcs.CHEERLEADER, 10);
						if (cheerleader != null) {
							cheerLeaderCelebrate(player, cheerleader);
						}
						handleScore(player, 1);
					} else {
						if (DataConversions.random(0, 2) < 2 || playerZone == Zone.ZONE_2XP_OUTER) {
							Functions.mes(player, "you miss by a mile!");
						} else {
							Functions.mes(player, "the ball flys way over the net");
						}
					}
				}
			});
		} else if (playerZone == Zone.ZONE_NOT_VISIBLE || playerZone == Zone.ZONE_OUTSIDE_THROWABLE) {
			thinkbubble(player, new Item(ItemId.GNOME_BALL.id()));
			Functions.mes(player, "you throw the ball at the goal",
					"you miss by a mile!",
					"maybe you should try playing on the pitch!");
		}
	}

	private void cheerLeaderCelebrate(Player p, Npc n) {

		switch(DataConversions.random(0, 2)) {
		case 0:
			npcsay(p, n, "yeah", "good goal");
			break;
		case 1:
			npcsay(p, n, "yahoo", "go go traveller");
			break;
		case 2:
			npcsay(p, n, "yeah baby", "gimme a g, gimme an o, gimme an a, gimme an l");
			break;
		}
	}

	private void loadIfNotMemory(Player p, String cacheName) {
		//load from player cache if not present in memory
		if((p.getAttribute(cacheName, -1) == -1) && p.getCache().hasKey(cacheName)) {
			p.setAttribute(cacheName, p.getCache().getInt(cacheName));
		} else if (p.getAttribute(cacheName, -1) == -1) {
			p.setAttribute(cacheName, 0);
		}
	}

	private void handleScore(Player p, int score_zone) {
		loadIfNotMemory(p, "gnomeball_goals");
		int prev_goalCount = p.getAttribute("gnomeball_goals", 0);
		p.incExp(Skills.RANGED, SCORES_XP[score_zone][prev_goalCount], true);
		p.incExp(Skills.AGILITY, SCORES_XP[score_zone][prev_goalCount], true);
		showScoreWindow(p, prev_goalCount+1);
		if (prev_goalCount+1 == 5) {
			ActionSender.sendTeleBubble(p, p.getX(), p.getY(), true);
		}
		p.setAttribute("gnomeball_goals", (prev_goalCount+1)%5);
	}

	private void showScoreWindow(Player p, int goalNum) {
		String text = "@yel@goal";
		if (goalNum > 1) {
			text += (" " + goalNum);
		}
		if (goalNum == 5) {
			text += ("% %Well Done% %@red@Agility Bonus");
		}
		ActionSender.sendBox(p, text, false);
	}

	@Override
	public void onTakeObj(Player p, GroundItem item) {
		if (item.getID() == ItemId.GNOME_BALL.id()) {
			if (p.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
				mes(p, 1200, "you can only carry one ball at a time", "otherwise it would be too easy");
			} else {
				p.getWorld().unregisterItem(item);
				give(p, ItemId.GNOME_BALL.id(), 1);
			}
		}
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem item) {
		return item.getID() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.GNOME_BALL.id();
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 702;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		if (obj.getID() == 702) {
			if (player.getY() > 456 || !player.getCarriedItems().hasCatalogID(ItemId.GNOME_BALL.id(), Optional.of(false))) {
				player.message("you open the gate");
				player.message("and walk through");
				doGate(player, obj, 357);
			}
			else {
				player.message("you have to leave the ball here");
			}
		}
	}
}

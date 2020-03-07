package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteStudents implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.STUDENT_ORANGE.id() || n.getID() == NpcId.STUDENT_PURPLE.id() || n.getID() == NpcId.STUDENT_GREEN.id();
	}

	/**
	 * Rock sample: 1149 = PURPLE STUDENT
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.STUDENT_ORANGE.id()) {
			switch (p.getQuestStage(Quests.DIGSITE)) {
				case 0:
				case 1:
					say(p, n, "Hello there");
					npcsay(p, n, "Hello there, as you can see I am a student");
					say(p, n, "What are you doing here ?");
					npcsay(p, n, "Oh I'm studying for the earth sciences exam");
					say(p, n, "Interesting....perhaps I should study it as well...");
					break;
				case 2:
					say(p, n, "Hello there");
					if (p.getCache().hasKey("student_orange_c")) { // completed orange student help
						npcsay(p, n, "How's it going ?");
						say(p, n, "There are more exam questions I'm stuck on");
						npcsay(p, n, "Hey, i'll tell you what I've learned, that may help",
							"The elligible people to use the digsite are:",
							"All that have passed the appropriate earth sciences exams");
						say(p, n, "Thanks for the information");
					} else if (p.getCache().hasKey("student_orange_s")) { // started orange student help
						if (p.getCarriedItems().hasCatalogID(ItemId.ROCK_SAMPLE_ORANGE.id(), Optional.of(false))) {
							say(p, n, "Look what I found");
							remove(p, ItemId.ROCK_SAMPLE_ORANGE.id(), 1);
							p.getCache().store("student_orange_c", true); // store completed orange student help
							p.getCache().remove("student_orange_s"); // remove started orange student help
							npcsay(p, n, "Excellent!",
								"I'm so happy",
								"Let me now help you with your exams...",
								"The elligible people to use the digsite are:",
								"All that have passed the appropriate earth sciences exams");
							say(p, n, "Thanks for the information");
						} else {
							say(p, n, "How's the study going ?");
							npcsay(p, n, "I'm getting there",
								"Have you found my rock sample yet ?");
							say(p, n, "No sorry, not yet");
							npcsay(p, n, "Oh dear, I hope it didn't fall into the stream",
								"I might never find it again...");
						}
					} else {
						say(p, n, "Can you help me with the earth sciences exams at all?");
						npcsay(p, n, "I can't do anything unless I find my rock sample");
						say(p, n, "Hey this rings a bell");
						npcsay(p, n, "?");
						say(p, n, "So if I find it you'll help me ?");
						npcsay(p, n, "I sure will");
						say(p, n, "Any ideas where it may be ?");
						npcsay(p, n, "All I remember is that I was working near the tents when I lost it...");
						say(p, n, "Okay I'll see what I can do ");
						p.getCache().store("student_orange_s", true); // started orange student help
					}
					break;
				case 3:
					say(p, n, "Hello there");
					npcsay(p, n, "How's it going ?");
					say(p, n, "There are more exam questions I'm stuck on");
					npcsay(p, n, "Hey, i'll tell you what I've learned, that may help",
						"Correct sample transportation:",
						"Samples taken in rough form, kept only in sealed containers");
					say(p, n, "Thanks for the information");
					if (!p.getCache().hasKey("student_orange_exam2")) {
						p.getCache().store("student_orange_exam2", true);
					}
					break;
				case 4:
					say(p, n, "Hello there");
					npcsay(p, n, "How's it going ?");
					say(p, n, "There are more exam questions I'm stuck on");
					npcsay(p, n, "Hey, i'll tell you what I've learned, that may help",
						"The proper technique for handling bones is:",
						"Handle bones very carefully, and keep away from other samples");
					say(p, n, "Thanks for the information");
					if (!p.getCache().hasKey("student_orange_exam3")) {
						p.getCache().store("student_orange_exam3", true);
					}
					break;
				case 5:
					say(p, n, "Hello there");
					npcsay(p, n, "Thanks a lot for finding my rock sample",
						"See you again");
					break;
				case 6:
				case -1:
					npcsay(p, n, "Hey it's the great explorer!",
						"Well done for finding the altar");
					break;
			}
		}
		else if (n.getID() == NpcId.STUDENT_GREEN.id()) {
			switch (p.getQuestStage(Quests.DIGSITE)) {
				case 0:
				case 1:
					say(p, n, "Hello there");
					npcsay(p, n, "Oh hi, i'm studying hard for an exam");
					say(p, n, "What exam is that ?");
					npcsay(p, n, "It's the earth sciences exam");
					say(p, n, "Interesting....");
					break;
				case 2:
					say(p, n, "Hello there");
					if (p.getCache().hasKey("student_green_c")) {
						npcsay(p, n, "How's it going ?");
						say(p, n, "I need more help with the exam");
						npcsay(p, n, "Well okay, this is what I have learned since I last spoke to you...",
							"The study of earthsciences is:",
							"The study of the earth, It's contents and It's history");
						say(p, n, "Okay I'll remember that");
					} else if (p.getCache().hasKey("student_green_s")) { // started green student help

						if (p.getCarriedItems().hasCatalogID(ItemId.ROCK_SAMPLE_GREEN.id(), Optional.of(false))) {
							say(p, n, "Hi, is this your rock sample ?");
							remove(p, ItemId.ROCK_SAMPLE_GREEN.id(), 1);
							p.getCache().store("student_green_c", true); // completed green student help
							p.getCache().remove("student_green_s"); // remove started green student help
							npcsay(p, n, "Oh wow! you've found it!",
								"Thank you so much",
								"I'll be glad to tell you what I know about the exam",
								"The study of earthsciences is:",
								"The study of the earth, It's contents and It's history");
							say(p, n, "Okay I'll remember that");
						} else {
							say(p, n, "How's the study going ?");
							npcsay(p, n, "Very well thanks",
								"Have you found my rock sample yet ?");
							say(p, n, "No sorry, not yet");
							npcsay(p, n, "Oh well...",
								"I am sure it's been picked up",
								"Couldn't you try looking through some pockets ?");
						}
					} else {
						say(p, n, "Can you help me with the earth sciences exams at all?");
						npcsay(p, n, "Well...maybe I will if you help me with something");
						say(p, n, "What's that ?");
						npcsay(p, n, "I have lost my rock sample");
						say(p, n, "What does it look like ?");
						npcsay(p, n, "Err....like a rock!");
						say(p, n, "Well that's not too helpful",
							"Can you remember where you last had it ?");
						npcsay(p, n, "It was around here for sure",
							"Maybe someone picked it up ?");
						say(p, n, "Okay I'll have a look for you");
						p.getCache().store("student_green_s", true); // started green student help
					}
					break;
				case 3:
					say(p, n, "Hello there");
					npcsay(p, n, "How's it going ?");
					say(p, n, "I need more help with the exam");
					npcsay(p, n, "Well okay, this is what I have learned since I last spoke to you...",
						"Correct rockpick usage:",
						"Always handle with care, strike the rock cleanly on it's cleaving point");
					say(p, n, "Okay I'll remember that");
					if (!p.getCache().hasKey("student_green_exam2")) {
						p.getCache().store("student_green_exam2", true);
					}
					break;
				case 4:
					say(p, n, "Hello there");
					npcsay(p, n, "How's it going ?");
					say(p, n, "I need more help with the exam");
					npcsay(p, n, "Well okay, this is what I have learned since I last spoke to you...",
						"Specimen brush use:",
						"Brush carefully and slowly, using short strokes");
					say(p, n, "Okay I'll remember that");
					if (!p.getCache().hasKey("student_green_exam3")) {
						p.getCache().store("student_green_exam3", true);
					}
					break;
				case 5:
					say(p, n, "Hello there");
					npcsay(p, n, "Thanks for your help, I'll pass these exams yet!",
						"See you later");
					break;
				case 6:
				case -1:
					npcsay(p, n, "Oh hi again",
						"News of your find has spread fast",
						"You are quite famous around here now");
					break;
			}
		}
		else if (n.getID() == NpcId.STUDENT_PURPLE.id()) {
			switch (p.getQuestStage(Quests.DIGSITE)) {
				case 0:
				case 1:
					say(p, n, "Hello there");
					npcsay(p, n, "Hi there, I'm studying for the earth sciences exam");
					say(p, n, "Interesting....This exam seems to be a popular one!");
					break;
				case 2:
					say(p, n, "Hello there");
					if (p.getCache().hasKey("student_purple_c")) { // completed purple student help
						npcsay(p, n, "How's it going ?");
						say(p, n, "I am stuck on some more exam questions");
						npcsay(p, n, "Okay, I'll tell you my latest notes...",
							"The proper health and safety points are:",
							"Gloves and boots to be worn at all times, proper tools must be used");
						say(p, n, "Great, thanks for your advice");
					} else if (p.getCache().hasKey("student_purple_s")) { // started purple student help
						if (p.getCarriedItems().hasCatalogID(ItemId.ROCK_SAMPLE_PURPLE.id(), Optional.of(false))) {
							say(p, n, "Guess what I found ?");
							remove(p, ItemId.ROCK_SAMPLE_PURPLE.id(), 1);
							p.getCache().store("student_purple_c", true); // completed purple student help
							p.getCache().remove("student_purple_s"); // remove started purple student help
							npcsay(p, n, "Hey! my sample!",
								"Thanks ever so much",
								"Let me help you with those questions now",
								"The proper health and safety points are:",
								"Gloves and boots to be worn at all times, proper tools must be used");
							say(p, n, "Great, thanks for your advice");
						} else {
							say(p, n, "How's the study going ?");
							npcsay(p, n, "Very well thanks",
								"Have you found my rock sample yet ?");
							say(p, n, "No sorry, not yet");
							npcsay(p, n, "I'm sure it's just outside the digsite somewhere...");
						}
					} else {
						say(p, n, "Can you help me with the exams at all?");
						npcsay(p, n, "I can if you help me...");
						say(p, n, "How can I do that");
						npcsay(p, n, "I have lost my rock sample");
						say(p, n, "What you as well ?");
						npcsay(p, n, "Err, yes it's gone somewhere");
						say(p, n, "Do you know where you dropped it ?");
						npcsay(p, n, "Well, I was doing a lot of walking that day...",
							"Oh yes, that's right...",
							"We were studying ceramics in fact",
							"I found some pottery...",
							"And it seemed to match the design that is on those large urns...",
							"...I was in the process of checking this out",
							"And when we got back to the centre...",
							"My rock sample had gone");
						say(p, n, "Leave it to me, I'll find it");
						npcsay(p, n, "Oh great!");
						p.getCache().store("student_purple_s", true); // started purple student help
					}
					break;
				case 3:
					say(p, n, "Hello there");
					npcsay(p, n, "How's it going ?");
					say(p, n, "I am stuck on some more exam questions");
					npcsay(p, n, "Okay, I'll tell you my latest notes...",
						"Finds handling:",
						"Finds must be carefully handled, and gloves worn");
					say(p, n, "Great, thanks for your advice");
					if (!p.getCache().hasKey("student_purple_exam2")) {
						p.getCache().store("student_purple_exam2", true);
					}
					break;
				case 4:
					if (p.getCache().hasKey("student_purple_exam3")) {
						say(p, n, "Hello there");
						npcsay(p, n, "Hi, the opal looks magnificent",
							"Thanks for everything you've done for me");
					} else if (p.getCache().hasKey("student_purple_opal")) {
						say(p, n, "Hello there");
						npcsay(p, n, "Oh hi again",
							"Did you bring me the opal ?");
						if (p.getCarriedItems().hasCatalogID(ItemId.UNCUT_OPAL.id(), Optional.of(false))) { // OPAL
							say(p, n, "Would that opal look like this by any chance ?");
							remove(p, ItemId.UNCUT_OPAL.id(), 1);
							p.getCache().store("student_purple_exam3", true); // completed purple student help
							p.getCache().remove("student_purple_opal"); // remove started purple student help
							npcsay(p, n, "Wow, great you've found one",
								"This will look beautiful set in my necklace",
								"Thanks for that, now I'll tell you what I know...",
								"Sample preparation:",
								"Samples cleaned and carried only in specimen jars");
							say(p, n, "Great, thanks for your advice");
						} else {
							say(p, n, "I haven't found one yet");
							npcsay(p, n, "Oh well, tell me when you do");
						}
					} else {
						say(p, n, "Hello there");
						npcsay(p, n, "What, you want more help ?");
						say(p, n, "Err... yes please!");
						npcsay(p, n, "Well.. it's going to cost you...");
						say(p, n, "Oh, well how much ?");
						npcsay(p, n, "I'll tell you what I would like...",
							"A precious stone, I don't find many of these",
							"My favourite is an opal, they are beautiful",
							"...Just like me",
							"Tee hee hee !");
						say(p, n, "Err... okay I'll see what I can do");
						if (!p.getCache().hasKey("student_purple_opal")) {
							p.getCache().store("student_purple_opal", true);
						}
					}
					break;
				case 5:
					say(p, n, "Hello there");
					npcsay(p, n, "Thanks for your help, I'll pass these exams yet!",
						"See you later");
					break;
				case 6:
				case -1:
					npcsay(p, n, "Hi there",
						"Thanks again, hey maybe I'll be asking you",
						"For help next time...",
						"It seems you are something of an expert now !");
					break;
			}
		}
	}
}

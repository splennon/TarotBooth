#!/usr/bin/perl -w

use strict;
use feature "switch";

use JSON::PP;
use Data::Dumper;



my $json = JSON::PP->new->utf8;
my $ashash = $json->decode(join("\n", <DATA>));

# 		this.put("C01", new Card("C01", "C01.png"));
binmode(STDOUT, ":encoding(UTF-8)");

foreach my $key (keys %$ashash) {

	my %card = %{%$ashash{$key}};

	given ($card{Positivity})
	{
	   when (2)   { $card{Positivity} = "Positivity.VERY_POSITIVE"; }
	   when (1)   { $card{Positivity} = "Positivity.POSITIVE"; }
	   when (0)   { $card{Positivity} = "Positivity.NEUTRAL"; }
	   when (-1)  { $card{Positivity} = "Positivity.NEGATIVE"; }
	   when (-2)  { $card{Positivity} = "Positivity.VERY_NEGATIVE"; }
	   default     { die() }
	}

	print qq|this.put("$card{Picture}", new Card("$card{Picture}", "$card{Picture}.png", "$card{Name}", "$card{Picture}.mp3", "$card{Picture}P.mp3", "$card{Picture}R.mp3", "$card{Picture}F.mp3", "$card{Past}", "$card{Present}", "$card{Future}", $card{Positivity}));\n|;

}





__DATA__
{
    "0": {
        "Picture": "M00",
        "Name": "The Fool",
        "Past": "There has been a new start and a new injection of energy. A new journey has begun.",
        "Present": "You are getting a fresh start. A new exciting journey is beginning that relates to your question.",
        "Future": "You will embark upon an exciting journey. A fresh start relating to your question will come soon.",
        "Positivity": 2
    },
    "1": {
        "Picture": "M01",
        "Name": "The Magician",
        "Past": "You or another actor have exerted professional influence, expertise or mastery upon the situation.",
        "Present": "You or another actor is exerting professional influence, expertise or mastery upon the situation.",
        "Future": "You or another actor will exert professional influence, expertise or mastery upon the situation.",
        "Positivity": 1
    },
    "2": {
        "Picture": "M02",
        "Name": "The High Priestess",
        "Past": "A strong spiritual and morally correct influence has informed the situation. There has been a spiritual looking within.",
        "Present": "A strong spiritual and morally correct influence is informing the situation. There is a spiritual looking within.",
        "Future": "A strong spiritual and morally correct influence will inform the situation. There will be a spiritual looking within.",
        "Positivity": 1
    },
    "3": {
        "Picture": "M03",
        "Name": "The Empress",
        "Past": "The power of abundance, healing and new life has informed the current situation.",
        "Present": "The power of abundance, healing and new life is informing the current situation.",
        "Future": "The power of abundance, healing and new life will inform the current situation.",
        "Positivity": 1
    },
    "4": {
        "Picture": "M04",
        "Name": "The Emperor",
        "Past": "An adult, masculine power has influenced the situation.",
        "Present": "An adult, masculine power is influencing the situation.",
        "Future": "An adult, masculine power will influence the situation.",
        "Positivity": 1
    },
    "5": {
        "Picture": "M05",
        "Name": "The Hierophant",
        "Past": "Conventional wisdom, orthodoxy and ritual have affected the situation.",
        "Present": "Conventional wisdom, orthodoxy and ritual is affecting the situation.",
        "Future": "Conventional wisdom, orthodoxy and ritual will affect the situation.",
        "Positivity": 0
    },
    "6": {
        "Picture": "M06",
        "Name": "The Lovers",
        "Past": "The power of union, sharing, trust and choice influenced the situation",
        "Present": "The power of union, sharing, trust and choice is influencing the situation",
        "Future": "The power of union, sharing and trust will influence the situation",
        "Positivity": 2
    },
    "7": {
        "Picture": "M07",
        "Name": "The Chariot",
        "Past": "There has been a clear decisive purpose that has harnessed opposing forces to progress the current situation.",
        "Present": "There is a clear decisive purpose that is harnessing opposing forces to progress the current situation.",
        "Future": "There will be a clear decisive purpose to harness opposing forces and progress the current situation.",
        "Positivity": 1
    },
    "8": {
        "Picture": "M08",
        "Name": "Strength",
        "Past": "Inner strength and conviction have progressed the current situation.",
        "Present": "Inner strength and conviction are progressing the current situation.",
        "Future": "Inner strength and conviction will progress the current situation.",
        "Positivity": 2
    },
    "9": {
        "Picture": "M09",
        "Name": "The Hermit",
        "Past": "Self-discovery, meditation and a journey within have influenced the current situation,",
        "Present": "Self-discovery, meditation and a journey within is occurring or is required at this time.",
        "Future": "Self-discovery, meditation and a journey within is necessary.",
        "Positivity": 1
    },
    "10": {
        "Picture": "M10",
        "Name": "Wheel of Fortune",
        "Past": "There has been some good luck. An upturn of fate on the sea of fortune has benefited you.",
        "Present": "Good luck is presenting. An upturn of fate on the sea of fortune is benefiting you.",
        "Future": "There will be some good luck. An upturn of fate on the sea of fortune will benefit you.",
        "Positivity": 2
    },
    "11": {
        "Picture": "M11",
        "Name": "Justice",
        "Past": "Harmony, fairness and integrity have prompted a situation. There may have been a legal issue.",
        "Present": "Harmony, fairness and integrity, morality and strategy are at play. The law is on your side.",
        "Future": "Harmony, fairness and integrity, morality and strategy will benefit you. You will get what you deserve.",
        "Positivity": 1
    },
    "12": {
        "Picture": "M12",
        "Name": "The Hanged Man",
        "Past": "There has been a delay or a new perspective. Self-sacrifice may have occurred.",
        "Present": "You are in a state of suspension and patience is required. You are gaining a new perspective.",
        "Future": "There will be a delay. You will gain a new perspective from waiting until a more suitable time.",
        "Positivity": 0
    },
    "13": {
        "Picture": "M13",
        "Name": "Death",
        "Past": "There has been a potent change or cessation.",
        "Present": "A powerful change is afoot.",
        "Future": "There will be a sudden and radical change",
        "Positivity": -1
    },
    "14": {
        "Picture": "M14",
        "Name": "Temperance",
        "Past": "There has been moderation and self-restraint.",
        "Present": "Now is the time for moderation and self-restraint.",
        "Future": "Moderation and self-restraint will be required.",
        "Positivity": 0
    },
    "15": {
        "Picture": "M15",
        "Name": "The Devil",
        "Past": "You have suffered from self-limiting beliefs and self-sabotaging actions. You have nobody to blame but yourself.",
        "Present": "You are suffering from self-limiting beliefs and self-sabotaging actions. You have nobody to blame but yourself, though you can choose a better course.",
        "Future": "You will suffer from self-limiting beliefs and self-sabotaging actions. You must be strong and objective and choose a better way.",
        "Positivity": -1
    },
    "16": {
        "Picture": "M16",
        "Name": "The Tower",
        "Past": "There has been the sudden collapse of a structure or institution.",
        "Present": "A structure or institution is collapsing. A deeply-ingrained pattern is unraveling.",
        "Future": "A structure or institution will collapse. Deeply ingrained patterns will change as a result. Hang on tight!",
        "Positivity": -1
    },
    "17": {
        "Picture": "M17",
        "Name": "The Star",
        "Past": "Great inspiration, hope, luck and optimism have influenced your current situation.",
        "Present": "There is currently great optimism, luck, tranquility and trust are present right now.",
        "Future": "A time of great hope and optimism is coming.",
        "Positivity": 2
    },
    "18": {
        "Picture": "M18",
        "Name": "The Moon",
        "Past": "Deep instinctual forces, concealment, occlusion, passivity and self-deception have weighed on the situation.",
        "Present": "Deep instinctual forces, concealment, occlusion, passivity and self-deception are weighing on the situation.",
        "Future": "Deep instinctual forces, concealment, occlusion, passivity and self-deception will weigh on the situation. Intuition from other worlds may present.",
        "Positivity": -1
    },
    "19": {
        "Picture": "M19",
        "Name": "The Sun",
        "Past": "There has been great success and fulfillment.",
        "Present": "Great success and fulfillment are afoot.",
        "Future": "There will be great success, fulfillment, reward and vitality.",
        "Positivity": 2
    },
    "20": {
        "Picture": "M20",
        "Name": "Judgment",
        "Past": "A situation came to an end and a judgment or evaluation was made.",
        "Present": "A situation is nearing its end and it is time to make an evaluation of it. A choice is being made.",
        "Future": "A situation will end soon. There will be an evaluation and possibly a choice.",
        "Positivity": 1
    },
    "21": {
        "Picture": "M21",
        "Name": "The World",
        "Past": "A situation came to a complete end. A goal was achieved. The querent is liberated.",
        "Present": "A situation is ending and liberation is attained. The end of one cycle clears way for a fresh start.",
        "Future": "A situation will come to an end soon. Freedom and liberation will result.",
        "Positivity": 1
    },
    "22": {
        "Picture": "W01",
        "Name": "Ace of Wands",
        "Past": "New inspirations and vitality have influenced the situation.",
        "Present": "New inspiration, vitality, a new venture or enterprise are afoot.",
        "Future": "There will be a new venture or enterprise accompanied by renewed inspiration and vitality.",
        "Positivity": 1
    },
    "23": {
        "Picture": "W02",
        "Name": "Two of Wands",
        "Past": "A great opportunity befell you.",
        "Present": "A great opportunity is presenting. An adventure is off to a good, but early, start.",
        "Future": "There will be an opportunity for a new venture or adventure.",
        "Positivity": 1
    },
    "24": {
        "Picture": "W03",
        "Name": "Three of Wands",
        "Past": "An enterprise has successfully begun.",
        "Present": "An enterprise is beginning.",
        "Future": "An enterprise will begin.",
        "Positivity": 1
    },
    "25": {
        "Picture": "W04",
        "Name": "Four of Wands",
        "Past": "You have enjoyed a haven of refuge where you belonged and were comfortable.",
        "Present": "You are enjoying a haven of refuge where you belong and are comfortable.",
        "Future": "You will enjoy a haven of refuge where you belong and feel comfortable.",
        "Positivity": 0
    },
    "26": {
        "Picture": "W05",
        "Name": "Five of Wands",
        "Past": "There was an exciting exchange or challenge.",
        "Present": "An exciting exchange, competition or challenge is afoot.",
        "Future": "There will be an exciting exchange, competition or challenge.",
        "Positivity": -1
    },
    "27": {
        "Picture": "W06",
        "Name": "Six of Wands",
        "Past": "You enjoyed a victory.",
        "Present": "You are enjoying a victory or the likelihood of success is high.",
        "Future": "The likelihood of succeeding is very high. You will enjoy a victory.",
        "Positivity": 1
    },
    "28": {
        "Picture": "W07",
        "Name": "Seven of Wands",
        "Past": "You held your own from a position of advantage.",
        "Present": "You are holding your own from a position of advantage.",
        "Future": "You will be required to defend your position however you have the advantage.",
        "Positivity": -1
    },
    "29": {
        "Picture": "W08",
        "Name": "Eight of Wands",
        "Past": "There was a period of very fast movement and change.",
        "Present": "Everything is moving very fast, things will happen quickly now.",
        "Future": "There will be a period of very fast movement, energy and change.",
        "Positivity": 1
    },
    "30": {
        "Picture": "W09",
        "Name": "Nine of Wands",
        "Past": "You had to stand up for yourself against an attack.",
        "Present": "You are under attack and must stand up for yourself.",
        "Future": "You will be the subject of an attack and must stand up for yourself.",
        "Positivity": -1
    },
    "31": {
        "Picture": "W10",
        "Name": "Ten of Wands",
        "Past": "You have carried a heavy load and worked hard but success was a burden not a joy.",
        "Present": "You are carrying too much or working too hard and the burden of success is great.",
        "Future": "You are at risk of working too hard and finding the burden of success too great.",
        "Positivity": 0
    },
    "32": {
        "Picture": "W11",
        "Name": "Page of Wands",
        "Past": "There was a period of daring action and possibly good news. You may have met a daring and courageous person.",
        "Present": "This is a time of daring action and good news. You may know a person who is daring and courageous.",
        "Future": "Aa time of daring and good news. You may meet a person who is daring and courageous.",
        "Positivity": 1
    },
    "33": {
        "Picture": "W12",
        "Name": "Knight of Wands",
        "Past": "There was a change in your life circumstances. You may have met a person with creative energy who brought or is bringing change.",
        "Present": "A change is afoot in your life circumstances. You may know a person who is creative and brings change.",
        "Future": "There will be a change in your life circumstances You may meet a creative person who brings sudden change.",
        "Positivity": 0
    },
    "34": {
        "Picture": "W13",
        "Name": "Queen of Wands",
        "Past": "You have enjoyed balanced life, family and career interests. You may have met an attractive, magnetic and powerful woman.",
        "Present": "You are enjoying balanced life, family and career interests. You may know an attractive, magnetic, powerful woman.",
        "Future": "You will enjoy new balance of life, career and family. You may meet an attractive, magnetic woman.",
        "Positivity": 1
    },
    "35": {
        "Picture": "W14",
        "Name": "King of Wands",
        "Past": "You have enjoyed motivation, maturity and strength. You may have met a strong man.",
        "Present": "You are enjoying motivation, maturity and strength. You may know a strong man who helps you at this time.",
        "Future": "You will enjoy a period of motivation, maturity and strength. You may meet a strong man who assists you.",
        "Positivity": 1
    },
    "36": {
        "Picture": "P01",
        "Name": "Ace of Pentacles",
        "Past": "You had a windfall or some good luck leading to comfort or new beginnings.",
        "Present": "You are enjoying a period of good luck that will bring you comfort and prosperity.",
        "Future": "You will have some good luck or a windfall bringing renewed prosperity.",
        "Positivity": 1
    },
    "37": {
        "Picture": "P02",
        "Name": "Two of Pentacles",
        "Past": "You have endured a period of balancing finances carefully.",
        "Present": "You are experiencing a challenge balancing the demands placed on you.",
        "Future": "You will experience a period where balance is important and may cause stress.",
        "Positivity": -1
    },
    "38": {
        "Picture": "P03",
        "Name": "Three of Pentacles",
        "Past": "You have applied your talents to best effect and done a good job.",
        "Present": "You are doing a great job and applying your talents effectively.",
        "Future": "You will be in a position to do an excellent job. Be sure to apply your telnets carefully to reap the rewards.",
        "Positivity": 0
    },
    "39": {
        "Picture": "P04",
        "Name": "Four of Pentacles",
        "Past": "You have experienced a period of holding tight or standing still.",
        "Present": "You are holding tight for gain or preservation. It may be to your advantage but likely doesn’t feel good.",
        "Future": "You will be forced to hold on tight to get what you want.",
        "Positivity": -1
    },
    "40": {
        "Picture": "P05",
        "Name": "Five of Pentacles",
        "Past": "You have experienced a period of financial or spiritual poverty and desperation.",
        "Present": "You are experiencing a period of poverty and desperation.",
        "Future": "A period of lack, poverty or despair may befall you. You should have another tarot reading soon.",
        "Positivity": -1
    },
    "41": {
        "Picture": "P06",
        "Name": "Six of Pentacles",
        "Past": "You have come through a period of give and take where generosity played a part in your experience.",
        "Present": "You may need to be generous or rely on the generosity of others, or both, at this time.",
        "Future": "You are entering a time where generosity will be important. You may need to be generous or accept the generosity of others.",
        "Positivity": 0
    },
    "42": {
        "Picture": "P07",
        "Name": "Seven of Pentacles",
        "Past": "You have been patient and tended your garden which has led to rewards accumulating for you.",
        "Present": "You must be patient and tend your garden. Rewards will accrue from your perseverance.",
        "Future": "You will exhibit patience and reap great rewards from your perseverance.",
        "Positivity": 0
    },
    "43": {
        "Picture": "P08",
        "Name": "Eight of Pentacles",
        "Past": "You have enjoyed an apprenticeship and acquired skills and motivation to use your talents well.",
        "Present": "You are enjoying an apprenticeship and acquiring skills and motivation to use your talents well.",
        "Future": "You will enter an apprenticeship or period of learning new skills.",
        "Positivity": 0
    },
    "44": {
        "Picture": "P09",
        "Name": "Nine of Pentacles",
        "Past": "You have been self-reliant in spiritual, life and financial affairs. This self-reliance has been important to you.",
        "Present": "You are very self-sufficient and are enjoying prosperity and success as a result.",
        "Future": "You will be required to be self-sufficient to care for your life, financial and spiritual needs.",
        "Positivity": 0
    },
    "45": {
        "Picture": "P10",
        "Name": "Ten of Pentacles",
        "Past": "You have enjoyed significant support and comfort from your family affairs bringing wealth and security.",
        "Present": "You are enjoying significant support and comfort from your family affairs bringing wealth and security.",
        "Future": "You will enjoy significant support and comfort from your family affairs bringing wealth and security.",
        "Positivity": 1
    },
    "46": {
        "Picture": "P11",
        "Name": "Page of Pentacles",
        "Past": "You have enjoyed a small financial gain or learned a lesson. You may have met a young person who is financially wise and reflective.",
        "Present": "You are learning a lesson and may be receiving a small financial gain. You may know a young, financially wise and reflective person who helps you.",
        "Future": "You may learn a lesson or receive a small financial gain. You may meet a young, financially wise and reflective person.",
        "Positivity": 1
    },
    "47": {
        "Picture": "P12",
        "Name": "Knight of Pentacles",
        "Past": "You have made steady and tangible progress. You may have met a conscientious, dependable person.",
        "Present": "You are making steady and tangible progress in life and finances. You may know a conscientious, dependable person who helps you.",
        "Future": "You will make steady and tangible progress in life and finances. You may meet a conscientious, dependable person who helps you.",
        "Positivity": 1
    },
    "48": {
        "Picture": "P13",
        "Name": "Queen of Pentacles",
        "Past": "You have taken a sensible approach. You may have met a businesswoman who helped you.",
        "Present": "You are taking a sensible approach. You may know a shrewd businesswoman who helps you.",
        "Future": "You are going to take a sensible approach. You may meet a shrewd businesswoman to help you.",
        "Positivity": 1
    },
    "49": {
        "Picture": "P14",
        "Name": "King of Pentacles",
        "Past": "You have enjoyed shrewd , practical business dealings. You may have met a wise businessman who helped you.",
        "Present": "You are enjoying a period of shrewd, practical dealings in life and business, you may know a wise businessman who helps you.",
        "Future": "You will enjoy a period of shrewd, practical dealings in life and business, you may meet a wise businessman who helps you.",
        "Positivity": 1
    },
    "50": {
        "Picture": "S01",
        "Name": "Ace of Swords",
        "Past": "You have enjoyed an infusion of strength which may have helped you in adversity.",
        "Present": "You are enjoying renewed strength in the face of adversity. You are determined and courageous at this time.",
        "Future": "You will enjoy renewed strength in the face of adversity. You will be determined and courageous.",
        "Positivity": 1
    },
    "51": {
        "Picture": "S02",
        "Name": "Two of Swords",
        "Past": "You experienced a stalemate or impasse where your energies and experiences were stuck.",
        "Present": "You are stuck in an experience of stalemate or impasse.",
        "Future": "You will experience a period of stalemate, impasse or frustration.",
        "Positivity": -1
    },
    "52": {
        "Picture": "S03",
        "Name": "Three of Swords",
        "Past": "You have gone through a period of heartache, loss or woe. It was likely necessary for your growth.",
        "Present": "You are going through a period of heartache, loss or woe. It is likely necessary for your growth.",
        "Future": "Batten down the hatches! You will experience a period of heartache. You should have another tarot reading soon.",
        "Positivity": -2
    },
    "53": {
        "Picture": "S04",
        "Name": "Four of Swords",
        "Past": "You have had a well-deserved rest.",
        "Present": "You are enjoying a period of well-deserved rest. Make the most of it!",
        "Future": "You will enjoy a period of well-deserved rest. Make the most of it!",
        "Positivity": 1
    },
    "54": {
        "Picture": "S05",
        "Name": "Five of Swords",
        "Past": "You experienced jealousy and one-upmanship, humiliation and wounded pride or you inflicted it on another.",
        "Present": "You are entwined in a game of one-upmanship. Wounded pride and humiliation are not far behind.",
        "Future": "You will get entwined in a game of one-upmanship. Wounded pride and humiliation are not far behind.",
        "Positivity": -1
    },
    "55": {
        "Picture": "S06",
        "Name": "Six of Swords",
        "Past": "You have started enjoying brighter times and maybe a reconciliation.",
        "Present": "You are enjoying brighter times and maybe a reconciliation.",
        "Future": "You will enjoy brighter times and maybe a reconciliation.",
        "Positivity": 1
    },
    "56": {
        "Picture": "S07",
        "Name": "Seven of Swords",
        "Past": "You experienced stealth or a display of the unexpected. You may have committed or experienced trickery",
        "Present": "You are experiencing stealth or a display of the unexpected. You may be committing or experiencing trickery",
        "Future": "You will experience stealth or a display of the unexpected. You may commit or experience trickery",
        "Positivity": -1
    },
    "57": {
        "Picture": "S08",
        "Name": "Eight of Swords",
        "Past": "You were trapped in an oppressive situation ",
        "Present": "You are trapped in an oppressive situation, holding a defensive perimeter.",
        "Future": "You will be trapped in an oppressive situation, holding a defensive perimeter.",
        "Positivity": -2
    },
    "58": {
        "Picture": "S09",
        "Name": "Nine of Swords",
        "Past": "You have experienced a period of despair that was troubling but may have seemed worse than it was.",
        "Present": "You are experiencing a period of despair that is troubling but may, like a nightmare, be unreal.",
        "Future": "You will experience a period of despair that is troubling but may, like a nightmare, be unreal.",
        "Positivity": -2
    },
    "59": {
        "Picture": "S10",
        "Name": "Ten of Swords",
        "Past": "There has been an irrevocable ending. You can’t do anything about it. The situation is not reversible but a new dawn is coming soon.",
        "Present": "You are experiencing an irrevocable ending. You can’t do anything about it, The situation is not reversible but a new dawn is coming soon.",
        "Future": "You will experience an irrevocable ending. Once over, the situation is not reversible but a new dawn will come soon after",
        "Positivity": 0
    },
    "60": {
        "Picture": "S11",
        "Name": "Page of Swords",
        "Past": "You may have had bad news or a met a person who is cold, clinical, precocious and decisive.",
        "Present": "You may be experiencing bad news or a know a person who is cold, clinical, precocious and decisive who will affect your circumstances.",
        "Future": "You may experience bad news or a meet a person who is cold, clinical, precocious and decisive who will affect your circumstances.",
        "Positivity": -1
    },
    "61": {
        "Picture": "S12",
        "Name": "Knight of Swords",
        "Past": "You have experienced blunt communication, or swift and sudden change. You may have met a strong, assertive leader.",
        "Present": "You are experiencing blunt communication, or swift and sudden change. You may know a strong, assertive leader to guide you.",
        "Future": "You will experience blunt communication, or swift and sudden change. You may meet a strong, assertive leader to guide you.",
        "Positivity": -1
    },
    "62": {
        "Picture": "S13",
        "Name": "Queen of Swords",
        "Past": "You have experienced  solitude which gave you time to think rationally. You may have met a strong, analytical and aloof woman.",
        "Present": "You are experiencing  solitude which gives you time to think rationally. You may know a strong, analytical and aloof woman.",
        "Future": "You will experience  solitude which gives you time to think rationally. You may meet a strong, analytical and aloof woman.",
        "Positivity": -1
    },
    "63": {
        "Picture": "S14",
        "Name": "King of Swords",
        "Past": "You experienced authority and command  without emotion. You may have met a man who is cold and authoritative.",
        "Present": "You are experiencing authority and command  without emotion. You may know a man who is cold and authoritative who is affecting your situation.",
        "Future": "You will experience authority and command  without emotion. You may meet a man who is cold and authoritative who affects your situation..",
        "Positivity": -1
    },
    "64": {
        "Picture": "C01",
        "Name": "Ace of Cups",
        "Past": "You have found new love or a stirring of passions.",
        "Present": "You are experiencing new love or happiness or a stirring of passions.",
        "Future": "You will experience new love or happiness or a stirring of passions.",
        "Positivity": 1
    },
    "65": {
        "Picture": "C02",
        "Name": "Two of Cups",
        "Past": "You have enjoyed a happy union, romance or affair.",
        "Present": "You are experiencing a happy union, romance or affair.",
        "Future": "You will experience a happy union, romance or affair.",
        "Positivity": 1
    },
    "66": {
        "Picture": "C03",
        "Name": "Three of Cups",
        "Past": "You have had a celebration, reunion or joyful occasion.",
        "Present": "You are having a celebration, reunion or joyful occasion.",
        "Future": "You will have a celebration, reunion or joyful occasion.",
        "Positivity": 1
    },
    "67": {
        "Picture": "C04",
        "Name": "Four of Cups",
        "Past": "You have experienced a period dissatisfaction, boredom and discontent. You may have been unable to see your advantages because you focused on the negative.",
        "Present": "You are experiencing dissatisfaction, boredom and discontent. You cannot see the good things in your life because you are blinded by the negative.",
        "Future": "You will experience dissatisfaction, boredom and discontent Be careful you don’t get blinded by the negative and loose sight of the good things in your life.",
        "Positivity": -1
    },
    "68": {
        "Picture": "C05",
        "Name": "Five of Cups",
        "Past": "You have experienced loss and disappointment, sadness or mourning.",
        "Present": "You are experiencing loss and disappointment, sadness or mourning.",
        "Future": "You will experience loss and disappointment, sadness or mourning. Have another tarot reading soon.",
        "Positivity": -2
    },
    "69": {
        "Picture": "C06",
        "Name": "Six of Cups",
        "Past": "You have experienced a period of innocence, nostalgia or reliving the past.",
        "Present": "You are reliving the past or experiencing nostalgia or innocence.",
        "Future": "You will enter a period of nostalgia where you relive the past and experience fond memories or innocence.",
        "Positivity": 1
    },
    "70": {
        "Picture": "C07",
        "Name": "Seven of Cups",
        "Past": "You have been daydreaming or living in a state of confusion or overwhelm.",
        "Present": "You are daydreaming or living in a state of confusion or overwhelm.",
        "Future": "You will enter a period of daydreaming  where you will experience confusion and overwhelm.",
        "Positivity": -1
    },
    "71": {
        "Picture": "C08",
        "Name": "Eight of Cups",
        "Past": "You have had a change of heart and  have begun moving on.",
        "Present": "You are having a change of heart and have begun moving on.",
        "Future": "You will have a change of heart requiring you to move on.",
        "Positivity": -1
    },
    "72": {
        "Picture": "C09",
        "Name": "Nine of Cups",
        "Past": "Your wishes have been fulfilled.",
        "Present": "Your wishes are fulfilled at this time.",
        "Future": "Your wishes will shortly be fulfilled.",
        "Positivity": 1
    },
    "73": {
        "Picture": "C10",
        "Name": "Ten of Cups",
        "Past": "You have enjoyed harmony in your interpersonal relationships.",
        "Present": "You are enjoying harmony in your interpersonal relationships.",
        "Future": "You will enjoy harmony in your interpersonal relationships.",
        "Positivity": 1
    },
    "74": {
        "Picture": "C11",
        "Name": "Page of Cups",
        "Past": "You have received an invitation or overture of friendship. You may have experienced increased sensitivity or met a young affectionate person.",
        "Present": "You are in receipt of an invitation to a friendship or social event. You may be feeling sensitive or may know a sensitive young person who has influence on your situation.",
        "Future": "You will receive an invitation or somebody will make friends with you. You may feel sensitive or make friends with a assistive person.",
        "Positivity": 1
    },
    "75": {
        "Picture": "C12",
        "Name": "Knight of Cups",
        "Past": "You have started a new relationship, romance or artistic endeavour. You may have met a romantic or idealistic young man.",
        "Present": "You are starting a new relationship, romance or artistic endeavour. You may know a romantic or idealistic young man who has bearing on your situation.",
        "Future": "You will start a new relationship, romance or artistic endeavour. You may meet a romantic or idealistic young man who will have bearing on your situation.",
        "Positivity": 1
    },
    "76": {
        "Picture": "C13",
        "Name": "Queen of Cups",
        "Past": "You have experienced deep nurturing feelings. You may have met a motherly figure.",
        "Present": "You are experiencing deep nurturing feelings. You may know a motherly figure who has bearing on your situation.",
        "Future": "You will experience deep nurturing feelings. You may meet a motherly figure who has bearing on your situation.",
        "Positivity": 1
    },
    "77": {
        "Picture": "C14",
        "Name": "King of Cups",
        "Past": "You have received or required wise counsel. You may have met a wise and emotionally intelligent man.",
        "Present": "You are receiving or you require wise counsel. You may know a wise and emotionally intelligent man who can help you.",
        "Future": "You will benefit from wise counsel. You may meet a wise and emotionally intelligent man who can help you.",
        "Positivity": 1
    }
}
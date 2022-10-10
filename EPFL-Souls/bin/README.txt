--COMMENT LANCER LE JEU "EPFL-SOULS"--

Il suffit de lancer (run) le fichier Play.java depuis Eclipse ou la console.

--CONTROLES--

UP / DOWN / LEFT / RIGHT : Mouvement cardinal.
R : "Roll" Rouler dans la direction courante du personnage.
	Le personnage aura alors un court temps d'invincibilite (i-frames).
E : "Action" active les interactions a distance.
TAB : "Switch" permet de changer l'item courant du personnage.
SPACE : "Item" permet d'utiliser le item courant du personnage. (Bomb, Bow, Sword, Staff)

In RouteChateau : 
	L : Faire spawn un LogMonster
	S : Faire spawn un FlameSkull
	B : Faire spawn une Bomb 


--SCENARIO--

1) Aller vers la Route en prenant le temps d'admirer tes plantes qui sont pas du tout des LogMonster bebes (direction est).
2) Arrive a la Route, se diriger vers la RouteTemple (direction sud-est) en evitant les LogMonster ou en les tuant.
3) Il y a une riviere qui est traversable en nageant (se fait automatiquement)
4) Arrive a la RouteTemple, se placer devant les Spikes, puis rouler par dessus les Spikes avec la touche R. 
5) Prendre le WaterStaff dans le Temple en marchant dessus.
6) Retourner vers la Route. Se diriger vers la RouteChateau (nord). 
7) Tuer le DarkLord avec le WaterStaff et prendre la CastleKey qu'il laisse tomber. 
8) Ouvrir la porte du Chateau avec la touche E et y entrer.
9) Le chateau est abandonne. Quel sens y attribuer? Reflechissons-y pendant 5 ans avant la sortie de EPFL-Souls 2.
FIN.


--IMAGES MODIFIEES--

Dans images.backgrounds.zelda, images.behaviors.zelda, et images.foregrounds.zelda : Tous.
	Pour diminuer le contraste des images (style Dark-Souls) et ajouter des ARPGCellTypes custom.

Dans images.foregrounds : lightHalo.png 
	Pour raisons esthetiques.


--IMAGES AJOUTEES--

Dans images.sprites : 1) explosion-[1, 6].png, 2) lightHalo.png, 3) YouDied.png, 4) rock.4.png
	1) Pour raisons esthetiques.
		SOURCE : Itch.io (achete)	https://ansimuz.itch.io/explosion-animations-pack
	2) Copie et modifie depuis images.foregounds, car celui-ci aurait comme parent le ARPGPlayer, et n'agirait pas comme un Foreground.
		SOURCE DE MODIFICATION : Orfeas
	3) Dark-Souls message de mort
		SOURCE : From Software, Bandai Namco https://www.teepublic.com/t-shirt/549487-dark-souls-you-died
	4) Pour raisons esthetiques
		SOURCE : Pokemon Ruby/Sapphire, Gamefreak https://www.spriters-resource.com/game_boy_advance/pokemonrubysapphire/sheet/8249/
		
Dans images.sprites.zelda : 1) babyLogMonster.png, 2) player.roll.png, 3) player.swim.png 4) spike.png
	1) Animated Decoration
		SOURCE : Orfeas
	2) L'option de se deplacer en roulant
		SOURCE : Orfeas
	3) Se deplacant en nageant
		SOURCE : Orfeas
	4) Illustration des ARPGCells de type SPIKE. 
		SOURCE : Orfeas
		


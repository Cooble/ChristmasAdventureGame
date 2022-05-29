# ChristmasAdventureGame
**Simple point-and-click adventure game written in Java.**

Currently, only first episode of the game is out. (available in GitHub [Releases](https://github.com/Cooble/ChristmasAdventureGame/releases/download/v1.0/ChristmasGameAdventure.zip))
<br>
Game achieved 2nd place at event [ITNetwork Summer 2017](https://www.itnetwork.cz/java/oop/zdrojove-kody/vanocni-adventura) (In Czech).


Have you ever wonderered, what would happen if Santa got kidnapped?
No? Well, that's sad but that's exactly what happens,
and it's up to the player to figure out the identity of the criminal
as well as to save Santa... and Xmas I guess.
## Current Episodes:

1. Santa's missing
2. Who knows? (like I really don't have a clue)

![Alt text](screen_shot.png?raw=false "")

## Story
Player is invited for yearly Christmas party with Santa at the North Pole.
At the Santa's cottage everybody is dancing to the Christmas carolls under the lights of mutlicolored reflectors
when suddenly...

Lights go out, panic bursts out, and when the lights are back up, the most important person is missing.


## Project structure
Uses Slick2D graphics engine.

Everything is working and ready to be extended, commented, and maybe to add even brand-new episodes.

Project consists of 3 modules:
- **CoobleEngine**
  - Engine which manages all low level stuff, provides locations, resource loaders etc.
  - Parses custom locations from xml files
- **ChristmasGameV2**
  - Content of the game itself, is dependent on the Engine.
  - contains xml files for locations as well as custom Locations.java (items entities...)
  - contains all resources (audio, textures, dialogs ...)
- **CoobleSandpit**
  - Graphical editor that generates xml files for locations.
  - Generates all static content from placing items in locations to writing branching dialog
  - constant `Loc.SRC_FOLDER` stores game resource folder, defaultly is set to ChristmasGameV2





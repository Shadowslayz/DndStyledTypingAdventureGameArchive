/*
 * Jian Meja
 * Angela Bautista
 * Adrian Flores
 * Brook Alamnew
 * Liana Zhu
 * CS2011
 * 05
 * Description: The Adventure Game is a text-based game that takes place
 *  in a maze, and the player's task is to escape the maze through encountering
 *  monsters and loots.  This project encouraged team members to work
 *  efficiently in large groups, effectively manage time, experient with
 *  optimal softwares to collaborate with, and constructing a lenthy program.
 */

import java.util.Scanner;

import java.util.Random;

public class AdventureGame1 {
    public static void main(String[] args) {
        // classes
        Help help = new Help();
        GameData gameData = new GameData();
        Scanner input = new Scanner(System.in);
        Random rand = new Random();
        Player player = new Player();
        Debug debug = new Debug();
        Encounter encounter = new Encounter();
        MazeTraversal mazeTraversal = new MazeTraversal();
        Maze maze = new Maze();
        Spells spells = new Spells();
        MazeData mazeData = new MazeData();
        Combat combat = new Combat();
        Loot loot = new Loot();
        Item item = new Item();
        Displays displays = new Displays();
        help.checkHelp(args, input, displays);
        // prepare data
        System.out.println("How large do you want the maze in the x-axis?");
        displays.pause(displays.pauseTime);
        int xDimension = input.nextInt();
        System.out.println("How large do you want the maze to be in the" +
                " y-axis?");
        displays.pause(displays.pauseTime);
        int yDimension = input.nextInt();
        input.nextLine();
        int xDimensionWithWalls = xDimension * 2 + 1;
        int yDimensionWithWalls = yDimension * 2 + 1;
        displays.displayPlayerStats(player);
        mazeData.walls = maze.create(xDimension, yDimension,
                        xDimensionWithWalls, yDimensionWithWalls, rand, debug,
                            mazeData, player);
        mazeData.encounters = encounter.generate(xDimensionWithWalls,
                                  yDimensionWithWalls,
                mazeData.percentageChanceOfGettingEncounter,
                rand, mazeData, debug);
        // combat.start(player, gameData, rand, spells, item, input, displays,
        // combat);
        displays.displayStoryIntro();
        while (player.mazePos[0] != mazeData.exitPos[0] ||
         player.mazePos[1] != mazeData.exitPos[1]) {
            mazeTraversal.playerMove(input, debug, player, mazeData, displays);
            encounter.check(player, mazeData, rand, debug, combat,
             loot, gameData, item, spells, input, displays);
        }
        System.out.println("You have successfully escaped the labarinth." +
                " Congrats!!");
        displays.displayStoryConclusion(player, gameData);
        displays.pause(displays.pauseTime);
        input.close();
    }
}

class GameData {
    final int lootChance = 60;
    // test 1% normally 60%
    Monster[] enemiesCast = new Monster[1000];
    int numOfEnemiesCast = 0;
    int[] living = new int[20];
    int numOfEnemiesInPlay = 0;
    String[] learnableSpells = { "cure wounds", "heal", "flame blade",
                                 "acid arrow", "burning hands", "fireball",
                                 "fire bolt", "frost bite", "lightning bolt",
                                  "poison spray", "mana shield" };

}

class MazePilot {
    int movementDirection;
    int numOfPossibleDirections;
    int[] possibleMovements = new int[4];
    int[] pos = new int[2];
    int counter = 1;
}

class MazeData {
    int[] viableCardinalDirections = new int[4];
    int[][] walls;
    int[][] encounters;
    int[] exitPos = new int[2];
    int percentageChanceOfGettingEncounter = 63;
    // test 100% normally 63%
}

class CombatData {
    int[][] board = new int[40][40];

}

class Player {
    Random rand = new Random();
    final int MAX_HP = 100;
    final int MAX_MP = rand.nextInt(51) + 50;
    int HP = MAX_HP;
    int MP = MAX_MP;
    int speed = 60;
    int numOfPotions = 2;
    final int potionHealingValue = 45;
    final int potionManaRegenValue = 30;
    int maxDamage = 14;
    int minDamage = 5;
    // String[] learnedSpells = new String[11];
    String[] learnedSpells = new String[11];
    int numOfLearnedSpells = 0;
    int[] mazePos = new int[2];
    int mazeDirectionFacing = 0;
    int numOfPossibleMovements = 0;
    int[] combatPos = new int[2];
    boolean isInvulnerable = false;
    boolean isFirstCombat = true;
    //stats
    //combat
    int numOfTimesPlayerCastSpell = 0;
    int numOfTimesPlayerMeleeAttack = 0;
    int numOfTimesPlayerMoved = 0;
    int numOfTimesPlayerUsedPotion = 0;
    double totalDistancePlayerMoved = 0;
    //maze
    int movesTakenToReachEndOfMaze = 0;
    int totalNumOfEncountersEncountered = 0;
}


/*
 * spells, weapons, enemies
 * (players have health of 100)
 * 10 enemies: name, health, speed, damage
 * damage 10-20
 * health 20-30
 * speed 20-50 (corresponds to size and ability, player has 30)
 *
 * 10 spells: name, damage, attack range
 * damage 10-20
 * attack range: 3-20
 *
 * Name of Spells: Acid Arrow, Burning Hands, Cause Fear, Cure Wounds, Healing
 * Word, Firebolt, Flame Blade, Poison Spray, Fireball
 *
 * Enemy generation
 * - have a graveyard array (houses ID of dead enemies)
 * - each enemy will have an ID that will keep them all distinct
 * - ID will be used to create the enemy using a class containing the enemy
 * stats
 * - another class will contain the enemy stats
 * - we will randomize the enemy from the enemy stat class and insert those
 * stats into the enemy class
 */

class Combat {
    public void start(Player player, GameData gameData, Random rand,
            Spells spells, Item item, Scanner input,
            Displays displays, Combat combat, Debug debug) {
        input.nextLine();
        CombatData combatData = new CombatData();
        int numOfEnemies = rand.nextInt(3) + 2;
        int turn = numOfEnemies;
        gameData.numOfEnemiesInPlay = 0;
        setPlayerPos(player, rand, combatData);
        if (player.isFirstCombat) {
            displays.tutorialCombat();
            item.learnSpell(rand, player, gameData, displays);
            player.isFirstCombat = false;
        }
        for (int x = 0; x < numOfEnemies; x++) {
            gameData.living[x] = gameData.numOfEnemiesCast;
            gameData.enemiesCast[gameData.numOfEnemiesCast] = new Monster(rand,
                                                                  combat);
            gameData.numOfEnemiesCast++;
            gameData.numOfEnemiesInPlay++;
        }
        for (int i = 0; i < gameData.numOfEnemiesInPlay; i++) {
            gridPlacing(rand, i, gameData, player, combatData);
        }
        for (int x = 0; x < gameData.numOfEnemiesInPlay; x++) {
            displays.displayMonsterStats(gameData.enemiesCast[
            gameData.living[x]]);
        }
        // create turns
        do {
            if (turn < 0) {
                turn = numOfEnemies;
            }
            if (turn < numOfEnemies) {
                monsterTurn(turn, gameData, player, rand, spells, combat,
                            debug, displays);
            } else {
                updateGrid(combatData, player, gameData);
                printBoard(combatData, displays);
                playerTurn(input, player, combatData, spells, item, rand,
                            displays, gameData, combat, debug);
            }
            turn--;
        } while (!isClear(gameData) && player.HP > 0);
        if (player.HP < 1) {
            System.out.println("You have died.\nGAME OVER");
            System.exit(0);
        }
        completionReward(player, rand, displays, item);
    }

    public void updateGrid(CombatData combatData,
                          Player player, GameData gameData) {
        clearGrid(combatData);
        combatData.board[player.combatPos[0]][player.combatPos[1]] = 1;
        for (int x = 0; x < gameData.numOfEnemiesInPlay; x++) {
            if (!gameData.enemiesCast[gameData.living[x]].isDeceased) {
                combatData.board[gameData.enemiesCast[
                gameData.living[x]].combatPos[0]][
                    gameData.enemiesCast[gameData.living[x]].combatPos[
                        1]] = 2;
            }
        }
    }

    public void clearGrid(CombatData combatData) {
        for (int x = 0; x < combatData.board.length; x++) {
            for (int y = 0; y < combatData.board[0].length; y++) {
                combatData.board[x][y] = 0;
            }
        }
    }

    public void playerTurn(Scanner input, Player player,
                            CombatData combatData, Spells spells,
            Item item, Random rand, Displays displays,
             GameData gameData, Combat combat, Debug debug) {
        String playerChoice;
        boolean end = false;
        int numOfTurns = 2;
        // display player choices
        while (!end && numOfTurns > 0) {
            // player turn display
            // player display stats
            // weapon attack, cast spells, move, consume potion, end turn
            displays.displayPlayerTurn(player);
            playerChoice = input.nextLine().toLowerCase();
            if (playerChoice.equals("attack")) {
                // weapon attack
                playerAttack(player, input, displays,
                 rand, gameData, combat, debug);
            } else if (playerChoice.equals("cast spell")) {
                // case spells
                castSpell(player, spells, input, displays, rand,
                 gameData, combat, debug);
            } else if (playerChoice.equals("move")) {
                // move
                playerMove(player, gameData, combat, input,
                 combatData, debug, displays);
            } else if (playerChoice.equals("consume potion")) {
                // consume potion
                item.usePotion(player, displays);
            } else if (playerChoice.equals("end turn")) {
                end = true;
            } else {
                // error
                System.out.println("You attempted some weird move there... " +
                        "uh... okay.");
                displays.pause(displays.pauseTime);
            }
            updateGrid(combatData, player, gameData);
            printBoard(combatData, displays);
            numOfTurns--;
        }
        System.out.println("--turn end--");
        displays.pause(displays.pauseTime);
    }

    public void playerMove(Player player, GameData gameData,
                          Combat combat, Scanner input, CombatData combatData,
            Debug debug, Displays displays) {
        int[] movePos = new int[2];
        //boolean stating that the position that player has selected is valid
        boolean isMovementValid = false;
        //display players current pos
        displays.displayPlayerPos(player);
        // loop until input is valid
        while (!isMovementValid) {
            //ask for x pos
            System.out.println("what is the x position you want to move.");
            displays.pause(displays.pauseTime);
            movePos[0] = input.nextInt() - 1;
            //ask for y pos
            System.out.println("What is the y position you want to move");
            displays.pause(displays.pauseTime);
            movePos[1] = input.nextInt() - 1;
            input.nextLine();
            //check if the point is in bounds
            if (isInBounds(player.combatPos[0],
             player.combatPos[1], combatData)) {
             //check if distance is valid
                if (distanceCheck(movePos, player.combatPos,
                player.speed, debug)) {
                    isMovementValid = true;
                    for (int x = 0; x < gameData.numOfEnemiesInPlay; x++) {
                      //check if the position is on top of an enemy
                        if (gameData.enemiesCast[gameData.living[x]].combatPos
                                                  == movePos) {
                            isMovementValid = false;
                            System.out.println("You are tying to move on top" +
                                    " of an enemy.");
                            displays.pause(displays.pauseTime);
                        }
                    }
                } else {
                    System.out.println("You are trying to move to a space" +
                            " thats too far.");
                    displays.pause(displays.pauseTime);
                }
            }
        }
        player.totalDistancePlayerMoved = player.totalDistancePlayerMoved +
          getDistance(movePos, player.combatPos);
        player.combatPos = movePos;
        player.numOfTimesPlayerMoved++;
        displays.displayPlayerPos(player);
    }
    //player choses to attack an enemy
    public void playerAttack(Player player, Scanner input,
                          Displays displays, Random rand, GameData gameData,
                          Combat combat, Debug debug) {
        //select monster the player wants to attack
        Monster chosenMonster = getDesiredTarget(input, gameData,
                                                displays, debug);
        //checks to see if the monster exists
        if (chosenMonster != null) {
          //verifies distance from monster
            if (distanceCheck(player.combatPos, chosenMonster.combatPos,
                              5, debug)) {
                //do damage
                chosenMonster.HP = chosenMonster.HP
                        - (rand.nextInt(player.maxDamage -
                          player.minDamage + 1) + player.minDamage);
                //check if monster has been killed
                isDead(chosenMonster, displays);
                System.out.println(chosenMonster.name + " has "
                        + chosenMonster.HP + " health remaining.");
                player.numOfTimesPlayerMeleeAttack++;
            } else {
                System.out.println("Enemy is not within striking range.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Your desired target is not" +
                                " at the specified position.");
            displays.pause(displays.pauseTime);
        }
    }

    public void castSpell(Player player, Spells spells,
                          Scanner input, Displays displays, Random rand,
            GameData gameData, Combat combat, Debug debug) {
              //check if player has spells in the first place
        if (player.numOfLearnedSpells > 0) {
          //ask for spell
            System.out.println("Which spell yould you like to cast");
            displays.pause(displays.pauseTime);
            //display all spells the player can cast
            displays.displayUsableSpells(player);
            String playerChoice = input.nextLine().toLowerCase();
            //check if the player can use that spell or if it exists in the
            // first place
            if (checkSpellChoice(player, playerChoice)) {
              //cast the spells
                if (playerChoice.equals("cure wounds")) {
                    spells.cureWounds(player, rand, displays);
                } else if (playerChoice.equals("heal")) {
                    spells.heal(player, displays);
                } else if (playerChoice.equals("flame blade")) {
                    Monster chosenMonster = getDesiredTarget(input,
                                                gameData, displays, debug);
                    if (chosenMonster != null) {
                        spells.flameBlade(player, rand, combat,
                                          chosenMonster, debug, displays);
                    } else {
                        System.out.println("Your desired target is not" +
                                            " at the specified position.");
                        displays.pause(displays.pauseTime);
                    }
                } else if (playerChoice.equals("acid arrow")) {
                    Monster chosenMonster = getDesiredTarget(input, gameData,
                                                      displays, debug);
                    if (chosenMonster != null) {
                        spells.acidArrow(player, rand, combat, chosenMonster,
                                              debug, displays);
                    } else {
                        System.out.println("Your desired target is not" +
                                            " at the specified position.");
                        displays.pause(displays.pauseTime);
                    }
                } else if (playerChoice.equals("burning hands")) {
                    Monster chosenMonster = getDesiredTarget(input,
                                                  gameData, displays, debug);
                    if (chosenMonster != null) {
                        spells.burningHands(player, rand, combat,
                                          chosenMonster, debug, displays);
                    } else {
                        System.out.println("Your desired target is not" +
                                            " at the specified position.");
                        displays.pause(displays.pauseTime);
                    }
                } else if (playerChoice.equals("fireball")) {
                    Monster chosenMonster = getDesiredTarget(input, gameData,
                                                      displays, debug);
                    if (chosenMonster != null) {
                        spells.fireball(player, rand, combat, chosenMonster,
                                                gameData, debug, displays);
                    } else {
                        System.out.println("Your desired target is not at" +
                                                " the specified position.");
                        displays.pause(displays.pauseTime);
                    }
                } else if (playerChoice.equals("fire bolt")) {
                    Monster chosenMonster = getDesiredTarget(input,
                                              gameData, displays, debug);
                    if (chosenMonster != null) {
                        spells.fireBolt(player, rand, combat, chosenMonster,
                                      debug, displays);
                    } else {
                        System.out.println("Your desired target is not at" +
                                        " the specified position.");
                        displays.pause(displays.pauseTime);
                    }
                } else if (playerChoice.equals("frost bite")) {
                    Monster chosenMonster = getDesiredTarget(input, gameData,
                                                displays, debug);
                    if (chosenMonster != null) {
                        spells.frostbite(player, rand, combat, chosenMonster,
                                          debug, displays);
                    } else {
                        System.out.println("Your desired target is not at" +
                                        " the specified position.");
                        displays.pause(displays.pauseTime);
                    }
                } else if (playerChoice.equals("lightning bolt")) {
                    Monster chosenMonster = getDesiredTarget(input, gameData,
                                                            displays, debug);
                    if (chosenMonster != null) {
                        spells.lightningBolt(player, rand, combat,
                                        chosenMonster, debug, displays);
                    } else {
                        System.out.println("Your desired target is not at " +
                                      "the specified position.");
                        displays.pause(displays.pauseTime);
                    }
                } else if (playerChoice.equals("poison spray")) {
                    Monster chosenMonster = getDesiredTarget(input, gameData,
                                                    displays, debug);
                    if (chosenMonster != null) {
                        spells.poisonSpray(player, rand, combat, chosenMonster,
                                        debug, displays);
                    } else {
                        System.out.println("Your desired target is not at" +
                                      " the specified position.");
                        displays.pause(displays.pauseTime);
                    }

                } else {
                    spells.manaShield(player, rand, combat, displays);
                }
            } else {
                System.out.println("You have cast the spell improperly.");
            }
        } else {
            System.out.println("You try to cast a spell, but find that you" +
                                  " havent learned any yet.");
        }
    }

    public Monster getDesiredTarget(Scanner input, GameData gameData,
                                    Displays displays, Debug debug) {
        displays.displayMonsterPos(gameData);
        int[] enemyPos = new int[2];
        //ask for the position of the monster that the player wants to attack
        System.out.println("what is the x position of the enemy you want" +
                            " to attack");
        displays.pause(displays.pauseTime);
        enemyPos[0] = input.nextInt() - 1;
        System.out.println("What is the y position of the enemy you want" +
                        " to attack");
        displays.pause(displays.pauseTime);
        enemyPos[1] = input.nextInt() - 1;
        input.nextLine();
        //checks to see if that monster exists
        for (int x = 0; x < gameData.numOfEnemiesInPlay; x++) {
            if (!gameData.enemiesCast[gameData.living[x]].isDeceased) {
                if (gameData.enemiesCast[gameData.living[x]].combatPos[0] ==
                      enemyPos[0]
                        && gameData.enemiesCast[gameData.living[x]].combatPos[1]
                            == enemyPos[1]) {
                    return gameData.enemiesCast[gameData.living[x]];
                }
            }
        }
        return null;
    }

    //mimics a diceroll by getting the type of dice and the amount of rolls
    public int diceRoll(int numOfRolls, int diceType, Random rand) {
        int rollValue = 0;
        for (int x = 0; x < numOfRolls; x++) {
            //total rollValue is stored and added for each roll value
            //rand method gets a random value based on the type of dice
            rollValue = rollValue + rand.nextInt(diceType) + 1;
        }
        return rollValue;
    }

    //checks if the players input matches any of the learned spell
    public boolean checkSpellChoice(Player player, String choice) {
        for (int x = 0; x < player.numOfLearnedSpells; x++) {
            if (player.learnedSpells[x].equals(choice)) {
                return true;
            }
        }
        return false;
    }

    //uses recursion to return the greatest common denominator of two values
    public int gcd(int n1, int n2) {
        //if either one of the values are zero, the other value is returned
        //if both values are 0 or there is no gcd, 0 is returned
        if (n2 == 0) {
            return n1;
        }
        //
        return gcd(n2, n1 % n2);
    }

    public void monsterMovement(GameData gameData, Player player, int turn,
                            Debug debug, Displays displays,
            Random rand) {
        // check the slope between the player and monster
        int[] initialPos = new int[2];
        initialPos[0] =
         gameData.enemiesCast[gameData.living[turn]].combatPos[0];
        initialPos[1] =
         gameData.enemiesCast[gameData.living[turn]].combatPos[1];
        int increments = 1;
        // 0 = x , 1 = y
        int[] slope = new int[2];
        slope[0] = player.combatPos[0] -
         gameData.enemiesCast[gameData.living[turn]].combatPos[0];
        slope[1] = player.combatPos[1] -
         gameData.enemiesCast[gameData.living[turn]].combatPos[1];
        int gcd = gcd(Math.abs(slope[0]), Math.abs(slope[1]));
        slope[0] = (slope[0] / gcd);
        slope[1] = (slope[1] / gcd);
        // distance check
        switch (rand.nextInt(2)) {
            case 0:
                do {
                  //move x axis first then y axis
                    if (increments > Math.abs(slope[1]) + Math.abs(slope[0])) {
                        increments = 1;
                    }
                    //move through x acxis
                    if (increments < Math.abs(slope[1]) + 1) {
                        if (slope[1] < 0) {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[1]--;
                        } else {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[1]++;
                        }
                    } else {
                      //move through y axis
                        if (slope[0] < 0) {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[0]--;
                        } else {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[0]++;
                        }
                    }
                    increments++;
                } while (distanceCheck(initialPos,
                        gameData.enemiesCast[gameData.living[turn]].combatPos,
                        gameData.enemiesCast[gameData.living[turn]].speed,
                         debug) && !distanceCheck(player.combatPos,
                        gameData.enemiesCast[gameData.living[turn]].combatPos,
                         5, debug));
                break;
            default:
                //move y axis first then x axis
                do {
                    //move through y axis
                    if (increments > Math.abs(slope[0]) + Math.abs(slope[1])) {
                        increments = 1;
                    }
                    if (increments < Math.abs(slope[0]) + 1) {
                        if (slope[0] < 0) {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[0]--;
                        } else {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[0]++;
                        }
                    } else {
                      //move through x axis
                        if (slope[1] < 0) {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[1]--;
                        } else {
                            gameData.enemiesCast[
                            gameData.living[turn]].combatPos[1]++;
                        }
                    }
                    increments++;
                } while (distanceCheck(initialPos,
                        gameData.enemiesCast[gameData.living[turn]].combatPos,
                        gameData.enemiesCast[gameData.living[turn]].speed,
                         debug)
                        && !distanceCheck(player.combatPos,
                         gameData.enemiesCast[gameData.living[turn]].combatPos,
                          5, debug));
        }
    }

    public void monsterTurn(int turn, GameData gameData, Player player,
                            Random rand, Spells spells, Combat combat,
                            Debug debug, Displays displays) {
        if (gameData.enemiesCast[gameData.living[turn]].isDeceased == false) {
            spells.checkBurn(gameData.enemiesCast[gameData.living[turn]],
                            rand, combat);
            spells.checkPoison(gameData.enemiesCast[gameData.living[turn]],
                                rand, combat);
            isDead(gameData.enemiesCast[gameData.living[turn]], displays);
            //check to see if enemy is parilized
            if (gameData.enemiesCast[gameData.living[turn]].isDeceased ==
                  false) {
                if (gameData.enemiesCast[gameData.living[turn]].parilizeTicks
                      > 0) {
                    gameData.enemiesCast[gameData.living[turn]].parilizeTicks--;
                    if (
                    gameData.enemiesCast[gameData.living[turn]].parilizeTicks
                    == 0) {
                        System.out.println("Parilize has worn out");
                        displays.pause(displays.pauseTime);
                    }
                } else {
                  //monster take action
                  //check to see if the monster is right next to the player
                    if (distanceCheck(
                    //monster attacks player
                    gameData.enemiesCast[gameData.living[turn]].combatPos,
                          player.combatPos, 5, debug)) {
                        gameData.enemiesCast[gameData.living[turn]].attack(
                                player, rand, combat, displays);
                    } else {
                      //monster moves closer to the player
                        monsterMovement(gameData, player, turn,
                         debug, displays, rand);
                         //checks to see if its close enough to the player
                        if (distanceCheck(
                        //attack player
                        gameData.enemiesCast[gameData.living[turn]].combatPos,
                                    player.combatPos, 5, debug)) {
                            gameData.enemiesCast[gameData.living[turn]].attack(
                                    player, rand, combat, displays);
                        }
                    }
                }
            }
        }
    }

    public static boolean isClear(GameData gameData) {
        boolean isItClear = true;
        for (int x = 0; x < gameData.numOfEnemiesInPlay; x++) {
            if (gameData.enemiesCast[gameData.living[x]].isDeceased == false) {
                isItClear = false;
            }
        }
        return isItClear;
    }

    public static void setPlayerPos(Player player, Random rand,
                              CombatData combatData) {
        player.combatPos[0] = rand.nextInt(combatData.board.length);
        player.combatPos[1] = rand.nextInt(combatData.board[0].length);
    }

    public static void gridPlacing(Random rand, int i, GameData gameData,
            Player player, CombatData combatData) {
        gameData.enemiesCast[i].combatPos[0]
        = rand.nextInt((combatData.board[0].length));
        gameData.enemiesCast[i].combatPos[1]
        = rand.nextInt((combatData.board[1].length));
        boolean isVert = rand.nextBoolean();

        if (player.combatPos[0] < (combatData.board[0].length / 2)
                && player.combatPos[1] < (combatData.board[1].length / 2)
                && gameData.enemiesCast[i].combatPos[0]
                < (combatData.board[0].length / 2)
                && gameData.enemiesCast[i].combatPos[1]
                < (combatData.board[1].length / 2)) {
            if (isVert) {
                gameData.enemiesCast[i].combatPos[1]
                += combatData.board[1].length / 2 - 1;
            } else {
                gameData.enemiesCast[i].combatPos[0]
                += combatData.board[0].length / 2 - 1;
            }
        } else if (player.combatPos[0] < (combatData.board[0].length / 2)
                && player.combatPos[1] > (combatData.board[1].length / 2)
                && gameData.enemiesCast[i].combatPos[0]
                < (combatData.board[0].length / 2)
                && gameData.enemiesCast[i].combatPos[1]
                > (combatData.board[1].length / 2)) {
            if (isVert) {
                gameData.enemiesCast[i].combatPos[1]
                -= combatData.board[1].length / 2 - 1;
            } else {
                gameData.enemiesCast[i].combatPos[0]
                += combatData.board[0].length / 2 - 1;
            }
        } else if (player.combatPos[0] > (combatData.board[0].length / 2)
                && player.combatPos[1] > (combatData.board[1].length / 2)
                && gameData.enemiesCast[i].combatPos[0]
                > (combatData.board[0].length / 2)
                && gameData.enemiesCast[i].combatPos[1]
                > (combatData.board[1].length / 2)) {
            if (isVert) {
                gameData.enemiesCast[i].combatPos[1]
                -= combatData.board[1].length / 2 - 1;
            } else {
                gameData.enemiesCast[i].combatPos[0]
                -= combatData.board[0].length / 2 - 1;
            }
        } else if (player.combatPos[0] > (combatData.board[0].length / 2)
                && player.combatPos[1] < (combatData.board[1].length / 2)
                && gameData.enemiesCast[i].combatPos[0]
                > (combatData.board[0].length / 2)
                && gameData.enemiesCast[i].combatPos[1]
                < (combatData.board[1].length / 2)) {
            if (isVert) {
                gameData.enemiesCast[i].combatPos[1]
                += combatData.board[1].length / 2 - 1;
            } else {
                gameData.enemiesCast[i].combatPos[0]
                -= combatData.board[0].length / 2 - 1;
            }
        }
    }

    public double getDistance(int[] pos1, int[] pos2){
        // quadratic formula
        double distance = Math.sqrt(Math.abs(
                (((pos2[0] * 5) - (pos1[0] * 5))
                    *
                    ((pos2[0] * 5) - (pos1[0] * 5)))
                    +
                    ((pos2[1] * 5) - (pos1[1] * 5))
                            *
                            ((pos2[1] * 5) - (pos1[1] * 5))));
        return distance;
    }
    public boolean distanceCheck(int[] pos1, int[] pos2,
     int radius, Debug debug) {
        // quadratic formula
        double distance = Math.sqrt(Math.abs(
                (((pos2[0] * 5) - (pos1[0] * 5))
                        *
                        ((pos2[0] * 5) - (pos1[0] * 5)))
                        +
                        ((pos2[1] * 5) - (pos1[1] * 5))
                                *
                                ((pos2[1] * 5) - (pos1[1] * 5))));
        if (distance < radius + 0.2) {
            return true;
        } else {
            return false;
        }
    }

//This checks if the monster is still alive or has died

    public void isDead(Monster monster, Displays displays) {
        if (monster.HP < 1) {
            monster.HP = 0;
            monster.isDeceased = true;
            System.out.println(monster.name + " has been slain.");
            displays.pause(displays.pauseTime);
        }
    }

// prints the combat board

    public void printBoard(CombatData combatData, Displays displays) {
        for (int i = combatData.board.length; i > -3; i--) {
            for (int j = -2; j < combatData.board[0].length + 1; j++) {
                if (i == -2) {
                    if (j != -2 && j != -1 && j != 40) {
                        System.out.printf("%3s", (j + 1));
                    } else {
                        System.out.print("   ");
                    }
                } else if (j == -2) {
                    if (i != -2 && i != -1 && i != 40) {
                        System.out.printf("%3s", (i + 1));
                        System.out.print("  ");
                    } else if (i == -1 || i == 40) {
                        System.out.print("     ");
                    }
                } else if (i == -1 || i == 40) {
                    System.out.print("⬛ ");
                } else if (j == -1 || j == 40) {
                    System.out.print("⬛ ");
                } else {
                    switch (combatData.board[j][i]) {
                        case 1:
                            System.out.print("P  ");
                            break;
                        case 2:
                            System.out.print("M  ");
                            break;
                        default:
                            System.out.print("▫  ");
                    }
                }
            }
            System.out.println();
        }
        displays.pause(displays.pauseTime);
    }

// This method is made so when you are able to defeat a monster, you get some
// type of loot by searching the body of the monster.

    public void completionReward(Player player, Random rand,
            Displays displays, Item item) {
        int potionAmount = rand.nextInt(2) + 1;
        System.out.println("You check the monster's body");
        displays.pause(displays.pauseTime);
        if (potionAmount >= 2) {
            System.out.println("You found " + potionAmount
            + " potions on the monster's body.");
        } else {
            System.out.println("You found " + potionAmount
            + " potion on the monster's body.");
        }
        item.obtainedRune(rand, player, displays);
        displays.pause(displays.pauseTime);
        player.numOfPotions = player.numOfPotions + potionAmount;
        System.out.println("Amount of potions in inventory: "
        + player.numOfPotions + ".");
        displays.pause(displays.pauseTime);
    }

    public boolean isInBounds(int xPos, int yPos, CombatData combatData) {
        if (xPos >= combatData.board.length || yPos
        >= combatData.board[0].length || xPos < 0 || yPos < 0) {
            return false;
        } else {
            return true;
        }
    }

}

class Loot {

/* This method gives the player different situations in which they can come
accross loot when  moving in the maze. */
    public void generateLootEncoutner(Player player, Random rand,
     GameData gameData, Item item, Displays displays) {
        int numOfObtainableItems = 0;
        //gets a random number from 1 to 3 and goes through different scenarios
        //based on which number it is using a switch case
        switch (rand.nextInt(4)) {
            case 0:
                System.out.println(
                        "You see a chest lying on the ground.\nYou " +
                        "decide to open the chest to take a look inside.");
                break;
            case 1:
                System.out.println("You come across a dead monster. \nYou " +
                "were able to scavenge some items.");
                break;
            case 2:
                System.out.println("You see an abandoned sachel on the floor." +
                " \nYou search through the sachel.");
                break;
            default:
                System.out.println("You spot a shiny object on the ground" +
                " from a far. \n You pick it up.");
                break;
        }
        displays.pause(displays.pauseTime);
        if (gameData.learnableSpells.length == player.numOfLearnedSpells) {
            numOfObtainableItems = 2;
        } else {
            numOfObtainableItems = 3;
        }
        switch (rand.nextInt(numOfObtainableItems)) {
            case 0:
                item.obtainedRune(rand, player, displays);
                break;
            case 1:
                item.getPotion(player, displays);
                break;
            default:
                item.learnSpell(rand, player, gameData, displays);
        }
    }
}

// weapon rune, spell book, potion
class Maze {
    public int[][] create(int xDimension, int yDimension,
     int xDimensionWithWalls, int yDimensionWithWalls,
            Random rand,
            Debug debug, MazeData mazeData, Player player) {
        MazePilot pilot = new MazePilot();
        boolean isMazeTraversed = false;
        boolean mazeCreated = false;
        int mazeWalls[][] = new int[xDimensionWithWalls][yDimensionWithWalls];
        int maze[][] = new int[xDimensionWithWalls][yDimensionWithWalls];
        // randomize pilot pos
        pilot.pos[0] = rand.nextInt(xDimensionWithWalls - 2) + 1;
        if (pilot.pos[0] % 2 == 0) {
            pilot.pos[0]++;
        }
        pilot.pos[1] = rand.nextInt(yDimensionWithWalls - 2) + 1;
        if (pilot.pos[1] % 2 == 0) {
            pilot.pos[1]++;
        }
        // generate starting pos
        player.mazePos[0] = pilot.pos[0];
        player.mazePos[1] = pilot.pos[1];
        // create north and south borders
        for (int x = 0; x < xDimensionWithWalls; x++) {
            mazeWalls[x][0] = 2;
            mazeWalls[x][yDimensionWithWalls - 1] = 2;
        }
        // create east and west borders
        for (int y = 0; y < yDimensionWithWalls; y++) {
            mazeWalls[0][y] = 2;
            mazeWalls[xDimensionWithWalls - 1][y] = 2;
        }
        // fill mazeWalls
        for (int x = 1; x < xDimensionWithWalls - 1; x++) {
            for (int y = 1; y < yDimensionWithWalls - 1; y++) {
                mazeWalls[x][y] = 1;
            }
        }
        // maze movement - [N,E,S,W]-[0,1,2,3]
        // initial position counter set
        maze[pilot.pos[0]][pilot.pos[1]] = pilot.counter;
        while (isMazeTraversed == false) {
            pilot.counter++;
            if (isDirectionPossible(maze, mazeWalls, debug, pilot)) {
                // wich direction will the pilot go
                pilot.movementDirection
                = pilot.possibleMovements[
                rand.nextInt(pilot.numOfPossibleDirections)];
                movePilotPosition(pilot, maze, mazeWalls);
                maze[pilot.pos[0]][pilot.pos[1]] = pilot.counter;
            } else {
                if (mazeCreated == false) {
                    mazeData.exitPos = pilot.pos;
                    mazeCreated = true;
                }
                movePilotPosition(pilot, maze, mazeWalls,
                 backtrack(pilot, maze, mazeWalls, debug), debug);
            }
            isMazeTraversed = checkMazeCompletion(maze);
        }
        // create exit
        if (mazeCreated == false) {
            mazeData.exitPos = pilot.pos;
        }
        debug.print(mazeWalls);
        System.out.println("");
        debug.print(maze);
        return mazeWalls;
    }

    public static int backtrack(MazePilot pilot, int[][] maze,
     int[][] mazeWalls, Debug debug) {
        int counterComparison = pilot.counter;
        int countDirection = 4;
        // --compare north--
        // check boundry and wall
        // debug.print(mazeWalls[pilot.pos[0]][pilot.pos[1] + 1]);
        if (mazeWalls[pilot.pos[0]][pilot.pos[1] + 1] == 0) {
            // compare count
            if (maze[pilot.pos[0]][pilot.pos[1] + 2] < counterComparison) {
                counterComparison = maze[pilot.pos[0]][pilot.pos[1] + 2];
                countDirection = 0;
            }
        }
        // --compare east--
        // check boundry and wall
        // debug.print(mazeWalls[pilot.pos[0] + 1][pilot.pos[1]]);
        if (mazeWalls[pilot.pos[0] + 1][pilot.pos[1]] == 0) {
            // compare count
            if (maze[pilot.pos[0] + 2][pilot.pos[1]] < counterComparison) {
                counterComparison = maze[pilot.pos[0] + 2][pilot.pos[1]];
                countDirection = 1;
            }
        }
        // --compare south
        // check boundry and wall
        // debug.print(mazeWalls[pilot.pos[0]][pilot.pos[1] - 1]);
        if (mazeWalls[pilot.pos[0]][pilot.pos[1] - 1] == 0) {
            // compare count
            if (maze[pilot.pos[0]][pilot.pos[1] - 2] < counterComparison) {
                counterComparison = maze[pilot.pos[0]][pilot.pos[1] - 2];
                countDirection = 2;
            }
        }
        // --compare west--
        // check boundry and wall
        // debug.print(mazeWalls[pilot.pos[0] - 1][pilot.pos[1]]);
        if (mazeWalls[pilot.pos[0] - 1][pilot.pos[1]] == 0) {
            // compare count
            if (maze[pilot.pos[0] - 2][pilot.pos[1]] < counterComparison) {
                counterComparison = maze[pilot.pos[0] - 2][pilot.pos[1]];
                countDirection = 3;
            }
        }
        return countDirection;
    }

    public static void movePilotPosition(MazePilot pilot, int[][] maze,
     int[][] mazeWalls, int direction,
            Debug debug) {
        // debug.print("direction: " + direction);
        switch (direction) {
            case 0:
                // north
                // debug.print("north");
                mazeWalls[pilot.pos[0]][pilot.pos[1] + 1] = 0;
                pilot.pos[1] = pilot.pos[1] + 2;
                break;
            case 1:
                // east
                // debug.print("east");
                mazeWalls[pilot.pos[0] + 1][pilot.pos[1]] = 0;
                pilot.pos[0] = pilot.pos[0] + 2;
                break;
            case 2:
                // south
                // debug.print("south");
                mazeWalls[pilot.pos[0]][pilot.pos[1] - 1] = 0;
                pilot.pos[1] = pilot.pos[1] - 2;
                break;
            default:
                // west
                // debug.print("west");
                mazeWalls[pilot.pos[0] - 1][pilot.pos[1]] = 0;
                pilot.pos[0] = pilot.pos[0] - 2;
                break;
        }
    }

    public static void movePilotPosition(MazePilot pilot, int[][] maze,
     int[][] mazeWalls) {
        switch (pilot.movementDirection) {
            case 0:
                mazeWalls[pilot.pos[0]][pilot.pos[1] + 1] = 0;
                pilot.pos[1] = pilot.pos[1] + 2;
                break;
            case 1:
                mazeWalls[pilot.pos[0] + 1][pilot.pos[1]] = 0;
                pilot.pos[0] = pilot.pos[0] + 2;
                break;
            case 2:
                mazeWalls[pilot.pos[0]][pilot.pos[1] - 1] = 0;
                pilot.pos[1] = pilot.pos[1] - 2;
                break;
            default:
                mazeWalls[pilot.pos[0] - 1][pilot.pos[1]] = 0;
                pilot.pos[0] = pilot.pos[0] - 2;
                break;
        }
    }

    public static boolean isDirectionPossible(int[][] maze, int[][] mazeWalls,
     Debug debug, MazePilot pilot) {
        pilot.numOfPossibleDirections = 0;
        // debug.print(pilot.pos);
        // debug.print(maze);
        // debug.print(mazeWalls);
        // check for north border
        // debug.print(mazeWalls[pilot.pos[0]][pilot.pos[1] + 1]);
        if (mazeWalls[pilot.pos[0]][pilot.pos[1] + 1] == 1) {
            // check if north isnt visited
            if (maze[pilot.pos[0]][pilot.pos[1] + 2] == 0) {
                // debug.print("north pass");
                // add north to pool
                pilot.possibleMovements[pilot.numOfPossibleDirections] = 0;
                pilot.numOfPossibleDirections++;
            }
        }
        // check for border east
        // debug.print(mazeWalls[pilot.pos[0] + 1][pilot.pos[1]]);
        if (mazeWalls[pilot.pos[0] + 1][pilot.pos[1]] == 1) {
            // check if east isnt visited
            if (maze[pilot.pos[0] + 2][pilot.pos[1]] == 0) {
                // debug.print("east pass");
                // add east to pool
                pilot.possibleMovements[pilot.numOfPossibleDirections] = 1;
                pilot.numOfPossibleDirections++;
            }
        }
        // check for border south
        // debug.print(mazeWalls[pilot.pos[0]][pilot.pos[1] - 1]);
        if (mazeWalls[pilot.pos[0]][pilot.pos[1] - 1] == 1) {
            // check if south isnt visited
            if (maze[pilot.pos[0]][pilot.pos[1] - 2] == 0) {
                // debug.print("south pass");
                // add south to pool
                pilot.possibleMovements[pilot.numOfPossibleDirections] = 2;
                pilot.numOfPossibleDirections++;
            }
        }
        // check for border west
        // debug.print(mazeWalls[pilot.pos[0] - 1][pilot.pos[1]]);
        if (mazeWalls[pilot.pos[0] - 1][pilot.pos[1]] == 1) {
            // check if west isnt visited
            if (maze[pilot.pos[0] - 2][pilot.pos[1]] == 0) {
                // debug.print("west pass");
                // add west to pool
                pilot.possibleMovements[pilot.numOfPossibleDirections] = 3;
                pilot.numOfPossibleDirections++;
            }
        }
        // debug.print(pilot.numOfPossibleDirections);
        if (pilot.numOfPossibleDirections == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkMazeCompletion(int[][] maze) {
        for (int y = 1; y < maze[0].length - 1; y = y + 2) {
            for (int x = 1; x < maze.length - 1; x = x + 2) {
                if (maze[x][y] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}

class Debug {
    public void viewMapCreatorPosition(int xMazePos, int yMazePos) {
        System.out.println("(" + xMazePos + ", " + yMazePos + ")");
    }

    public void print(int[] array) {
        System.out.print("<");
        for (int x = 0; x < array.length; x++) {
            System.out.print(array[x]);
            if (x != array.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(">");
    }

    public void print(boolean bool) {
        System.out.println(bool);
    }

    public void print(int[][] array) {
        for (int x = array[0].length - 1; x > -1; x = x - 1) {
            for (int y = 0; y < array.length; y++) {
                System.out.print(array[y][x] + "  ");
            }
            System.out.print("\n");
        }
    }

    public void printWalls(int[][] array, Player player) {
        for (int x = array[0].length - 1; x > -1; x = x - 1) {
            for (int y = 0; y < array.length; y++) {
                if (player.mazePos[0] == y && player.mazePos[1] == x){
                    System.out.print("2  ");
                } else {
                    System.out.print(array[y][x] + "  ");
                }
            }
            System.out.print("\n");
        }
    }

    public void print(String str) {
        System.out.println(str);
    }

    public void print(int num) {
        System.out.println(num);
    }

    public void print(double num) {
        System.out.println(num);
    }
}

class MazeTraversal {
    // there is something wrong with the maze traversal system
    /*
     * check possible directions
     * convert
     * ask
     * verify answer
     * move player
     * encounter check
     */
    // check possible movements (cordinal directions)
    public static void checkPossibleCardianlDirections(MazeData mazeData,
     Player player, Debug debug) {
        player.numOfPossibleMovements = 0;
        // --if no wall--
        // check for wall north
        if (mazeData.walls[player.mazePos[0]][player.mazePos[1] + 1] == 0) {
            // add north to pool
            mazeData.viableCardinalDirections[player.numOfPossibleMovements]
            = 0;
            player.numOfPossibleMovements++;
        }
        // check for wall east
        if (mazeData.walls[player.mazePos[0] + 1][player.mazePos[1]] == 0) {
            // add east to pool
            mazeData.viableCardinalDirections[player.numOfPossibleMovements]
            = 1;
            player.numOfPossibleMovements++;
        }
        // check for wall south
        if (mazeData.walls[player.mazePos[0]][player.mazePos[1] - 1] == 0) {
            mazeData.viableCardinalDirections[player.numOfPossibleMovements]
            = 2;
            player.numOfPossibleMovements++;
        }
        // check for border west
        if (mazeData.walls[player.mazePos[0] - 1][player.mazePos[1]] == 0) {
            // add west to pool
            mazeData.viableCardinalDirections[player.numOfPossibleMovements]
            = 3;
            player.numOfPossibleMovements++;
        }
    }

    public void promptViableDirections(Player player, MazeData mazeData,
     Debug debug, Displays displays) {
        String choicesPrompt = "[";
        System.out.println("Which direction would you like to go.");
        displays.pause(displays.pauseTime);
        for (int x = 0; x < player.numOfPossibleMovements; x++) {
            // check forward
            if (mazeData.viableCardinalDirections[x] == 0) {
                choicesPrompt = choicesPrompt + "North";
                System.out.println("There is an opening to the north.");
                // check right
            } else if (mazeData.viableCardinalDirections[x] == 1) {
                choicesPrompt = choicesPrompt + "East";
                System.out.println("There is an opening to the east.");
                // check behind
            } else if (mazeData.viableCardinalDirections[x] == 2) {
                choicesPrompt = choicesPrompt + "South";
                System.out.println("There is an opening to the south.");
                // left
            } else {
                choicesPrompt = choicesPrompt + "West";
                System.out.println("There is an opening to the west.");
            }
            if (x != player.numOfPossibleMovements - 1) {
                choicesPrompt = choicesPrompt + ", ";
            } else {
                choicesPrompt = choicesPrompt + "]";
            }
        }
        displays.pause(displays.pauseTime);
        System.out.println(choicesPrompt);
        displays.pause(displays.pauseTime);
    }

    public boolean verifyChoice(int convertedDirection, Player player,
     MazeData mazeData, Debug debug,
            Displays displays) {
        for (int x = 0; x < player.numOfPossibleMovements; x++) {
            if (convertedDirection == mazeData.viableCardinalDirections[x]) {
                return true;
            }
        }
        System.out.println("You are trying to walk into a wall. try another" +
                      " direction.");
        displays.pause(displays.pauseTime);
        return false;
    }

    public boolean verifyString(String answer, Displays displays) {
        System.out.println(answer);
        if (answer.equals("north") || answer.equals("south")
        || answer.equals("east") || answer.equals("west")) {
            return true;
        } else {
            System.out.println("I dont understad what you were trying to do." +
                    " Repeat that.");
            displays.pause(displays.pauseTime);
            return false;
        }
    }

    public int convertStrToIntDirection(String answer, Player player) {
        // gets cardinal dirction
        int direction;
        if (answer.equals("north")) {
            direction = 0;
        } else if (answer.equals("east")) {
            direction = 1;
        } else if (answer.equals("south")) {
            direction = 2;
        } else {
            direction = 3;
        }
        return direction;
    }

    public void playerMove(Scanner input, Debug debug, Player player,
            MazeData mazeData, Displays displays) {
        String strPlayerChoice;
        int direction;
        checkPossibleCardianlDirections(mazeData, player, debug);
        promptViableDirections(player, mazeData, debug, displays);
        // error checking
        do {
            do {
                strPlayerChoice = input.next().toLowerCase();
            } while (verifyString(strPlayerChoice, displays) == false);
            direction = convertStrToIntDirection(strPlayerChoice, player);
        } while (verifyChoice(direction, player, mazeData, debug, displays)
                 == false);
        if (strPlayerChoice.equals("north")) {
            System.out.println("You proceed to the north.");
        } else if (strPlayerChoice.equals("east")) {
            System.out.println("You proceed to the east.");
        } else if (strPlayerChoice.equals("west")) {
            System.out.println("You proceed to the west.");
        } else if (strPlayerChoice.equals("south")) {
            System.out.println("You decide to the south.");
        }
        displays.pause(displays.pauseTime);
        // move north
        if (direction == 0) {
            player.mazePos[1] = player.mazePos[1] + 2;
            // move east
        } else if (direction == 1) {
            player.mazePos[0] = player.mazePos[0] + 2;
            // move west
        } else if (direction == 3) {
            player.mazePos[0] = player.mazePos[0] - 2;
            // move south
        } else {
            player.mazePos[1] = player.mazePos[1] - 2;
        }
    }
}

class Encounter {
    // 3 = end
    // 2 = start
    // 1 = encounter
    // 0 = empty

    public int[][] generate(int xDimensionWithWalls, int yDimensionWithWalls,
            int percentageChance, Random rand, MazeData mazeData, Debug debug) {
        int[][] encounters = new int[xDimensionWithWalls][yDimensionWithWalls];
        // debug.print(mazeData.exitPos);
        for (int x = 1; x < encounters.length - 1; x = x + 2) {
            for (int y = 1; y < encounters[0].length - 1; y = y + 2) {
                if (rand.nextInt(101) <= percentageChance) {
                    encounters[x][y] = 1;
                }
            }
        }
        // debug.print(mazeData.startPos);
        encounters[mazeData.exitPos[0]][mazeData.exitPos[1]] = 3;
        // debug.print(encounters[mazeData.exitPos[0]][mazeData.exitPos[1]]);
        // debug.print(encounters[mazeData.startPos[0]][mazeData.startPos[1]]);
        return encounters;
    }

    public void check(Player player, MazeData mazeData, Random rand,
            Debug debug, Combat combat, Loot loot, GameData gameData,
            Item item, Spells spells, Scanner input, Displays displays) {
        // debug.print(player.mazePos);
        if (mazeData.encounters[player.mazePos[0]][player.mazePos[1]] == 1) {
            senarioClear(mazeData, player);
            randomizeSenario(rand, combat, loot,
                    mazeData, player, gameData, item,
                    spells, input, displays, debug);
            player.totalNumOfEncountersEncountered++;
        }
        player.movesTakenToReachEndOfMaze++;
    }

    public static void senarioClear(MazeData mazeData, Player player) {
        mazeData.encounters[player.mazePos[0]][player.mazePos[1]] = 0;
    }

    // put all of the encounter senarios here
    public void randomizeSenario(Random rand, Combat combat, Loot loot,
            MazeData mazeData, Player player, GameData gameData, Item item,
            Spells spells, Scanner input, Displays displays, Debug debug) {
        if (rand.nextInt(101) <= gameData.lootChance) {
            loot.generateLootEncoutner(player, rand, gameData, item, displays);
        } else {
            combat.start(player, gameData, rand, spells, item, input, displays,
                         combat, debug);
        }
    }
}

class Monster {
// we made variables to make their stats default to 0 so that we can change the
// values throughout the maze

    String name;
    int HP = 0;
    int speed = 0;
    int addDamage = 0;
    int multiplyDamage = 0;
    int randDamage = 0;
    int numOfAttack = 1;
    boolean isDeceased = false;
    int[] combatPos = new int[2];
    // statuses
    int burnTicks = 0;
    int poisonTicks = 0;
    int parilizeTicks = 0;

// we made this switch statement to assign each monster to an indivual number

    public Monster(Random rand, Combat combat) {
        switch (rand.nextInt(10)) {
            case 0:
                ape(rand, combat);
                break;
            case 1:
                animatedArmor(rand, combat);
                break;
            case 2:
                assassin(rand, combat);
                break;
            case 3:
                awakenedShurb(rand, combat);
                break;
            case 4:
                azer(rand, combat);
                break;
            case 5:
                baboon(rand, combat);
                break;
            case 6:
                bandit(rand, combat);
                break;
            case 7:
                barbedDevil(rand, combat);
                break;
            case 8:
                basilisk(rand, combat);
                break;
            case 9:
                beardedDevil(rand, combat);
                break;
            default:
                berserker(rand, combat);

        }
    }

/* we made a method for each monster that is in the maze that alters their
health, speed, damage, and chances of increasing their damage.*/

    public void ape(Random rand, Combat combat) {
        name = "Ape";
        HP = combat.diceRoll(3, 6, rand) + 6;
        speed = 30;
        addDamage = 3;
        multiplyDamage = 1;
        randDamage = 6;
    }

    public void animatedArmor(Random rand, Combat combat) {
        name = "Animated Armor";
        HP = combat.diceRoll(6, 8, rand) + 6;
        speed = 25;
        addDamage = 2;
        multiplyDamage = 1;
        randDamage = 6;
    }

    public void assassin(Random rand, Combat combat) {
        name = "Assassin";
        HP = combat.diceRoll(6, 8, rand) + 24;
        speed = 45;
        addDamage = 3;
        multiplyDamage = 1;
        randDamage = 6;
        numOfAttack = 2;
    }

    public void awakenedShurb(Random rand, Combat combat) {
        name = "Awakened Shurb";
        HP = combat.diceRoll(3, 6, rand);
        speed = 30;
        addDamage = -1;
        multiplyDamage = 1;
        randDamage = 4;
    }

    public void azer(Random rand, Combat combat) {
        name = "Azer";
        HP = combat.diceRoll(6, 8, rand) + 12;
        speed = 20;
        addDamage = 3;
        multiplyDamage = 1;
        randDamage = 10;
    }

    public void baboon(Random rand, Combat combat) {
        name = "Baboon";
        HP = combat.diceRoll(2, 8, rand) + 3;
        speed = 30;
        addDamage = -1;
        multiplyDamage = 1;
        randDamage = 4;
    }

    public void bandit(Random rand, Combat combat) {
        name = "Bandit";
        HP = combat.diceRoll(2, 8, rand) + 2;
        speed = 30;
        addDamage = 1;
        multiplyDamage = 1;
        randDamage = 6;
    }

    public void barbedDevil(Random rand, Combat combat) {
        name = "Barbed Devil";
        HP = combat.diceRoll(5, 8, rand) + 26;
        speed = 30;
        addDamage = 3;
        multiplyDamage = 1;
        randDamage = 6;
        numOfAttack = 2;
    }

    public void basilisk(Random rand, Combat combat) {
        name = "Basilisk";
        HP = combat.diceRoll(8, 8, rand) + 16;
        speed = 20;
        addDamage = 3;
        multiplyDamage = 2;
        randDamage = 6;
    }

    public void beardedDevil(Random rand, Combat combat) {
        name = "Bearded Devil";
        HP = combat.diceRoll(8, 8, rand) + 16;
        speed = 30;
        addDamage = 2;
        multiplyDamage = 1;
        randDamage = 8;
        numOfAttack = 2;
    }

    public void berserker(Random rand, Combat combat) {
        name = "Berserker";
        HP = combat.diceRoll(9, 8, rand) + 27;
        speed = 30;
        addDamage = 3;
        multiplyDamage = 1;
        randDamage = 12;
    }

/* we created this method for when monsters attack you, this will tell you
whether the monster was able to deal damage to the player or failed to attack.*/

    public void attack(Player player, Random rand, Combat combat,
                        Displays displays) {
        int damageVal = 0;
        if (player.isInvulnerable == false) {
            damageVal = combat.diceRoll(multiplyDamage, randDamage, rand) +
            addDamage; //
            player.HP = player.HP - damageVal;
            System.out.println("You have taken " + damageVal + " damage by " +
            name); //
            System.out.println("Player HP: " + player.HP);
        } else {
            System.out.println("The enemy attack was ineffective.");
            displays.pause(displays.pauseTime);
            player.isInvulnerable = false;
        }
    }
    /*
     * (players have health of 100)
     * 10 enemies: name, health, speed, damage
     * damage 10-20
     * health 20-30
     * speed 20-50 (corresponds to size and ability, player has 30)
     */
}

/* We created a class for spells that has a method for each spell in the game
that alters the damage, mana usage, range.*/
class Spells {
  /* there are two spells that heal the player but make sure it doesn't go
  past the max health */
    public void cureWounds(Player player, Random rand, Displays displays) {
        if (player.MP >= 10) {
            System.out.println("You cast cure wounds.");
            displays.pause(displays.pauseTime);
            player.HP = player.HP + rand.nextInt(30) + 1;
            player.MP = player.MP - 10;
            if (player.HP > player.MAX_HP) {
                player.HP = player.MAX_HP;
            }
            player.numOfTimesPlayerCastSpell++;
        } else {
            System.out.println("Not enough mana to cast Cure Wounds");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Your current health is: " + player.HP +
                           "\n Your current Mana is: " + player.MP);
        displays.pause(displays.pauseTime);
    }

    public void heal(Player player, Displays displays) {
        if (player.MP >= 40) {
            System.out.println("You cast heal.");
            displays.pause(displays.pauseTime);
            player.HP = player.HP + 70;
            player.MP = player.MP - 40;
            if (player.HP > player.MAX_HP) {
                player.HP = player.MAX_HP;
            }
            player.numOfTimesPlayerCastSpell++;
        } else {
            System.out.println("Not enough mana to cast Heal");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Your current health is: " + player.HP +
                           "\n Your current Mana is: " + player.MP);
        displays.pause(displays.pauseTime);
    }

    public void flameBlade(Player player, Random rand, Combat combat,
                          Monster monsterId, Debug debug, Displays displays) {
        if (player.MP >= 15) {
            System.out.println("You cast Flame Blade.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 15;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 5,
                                     debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(3, 6, rand);
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                    " health");
                displays.pause(displays.pauseTime);
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Not enough mana to cast Flame Blade.");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }

    public void acidArrow(Player player, Random rand, Combat combat,
                          Monster monsterId, Debug debug, Displays displays) {
        if (player.MP >= 10) {
            System.out.println("You cast Acid Arrow.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 10;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 90,
                debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(2, 4, rand);
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                  " health");
                displays.pause(displays.pauseTime);
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Not enough mana to cast Avid Arrow.");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }

// This spell burns the enemy if used against it and makes it take damage for an
// extra turn.

    public void burningHands(Player player, Random rand, Combat combat,
                            Monster monsterId, Debug debug, Displays displays) {
        if (player.MP >= 35) {
            System.out.println("You cast Burning Hands.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 35;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 5,
                debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(3, 6, rand);
                monsterId.burnTicks = monsterId.burnTicks + 2;
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                    " health");
                System.out.println(monsterId.name + " is now on fire");
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Not enough mana to cast Burning Hands.");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }
/* We made it so when the player uses the fireball it has radius in which
monsters will take damage if they are around that area. */

    public void fireball(Player player, Random rand, Combat combat,
                        Monster monsterId, GameData gameData, Debug debug,
                        Displays displays) {
        if (player.MP >= 50) {
            System.out.println("You cast Fireball.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 50;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 150,
                debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(8, 6, rand);
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                    " health");
                for (int x = 0; x < gameData.numOfEnemiesInPlay; x++) {
                    if (gameData.enemiesCast[gameData.living[x]] != monsterId) {
                        if (combat.distanceCheck(monsterId.combatPos,
                            gameData.enemiesCast[gameData.living[x]].combatPos,
                             20, debug) == true) {
                            gameData.enemiesCast[gameData.living[x]].HP =
                            gameData.enemiesCast[gameData.living[x]].HP
                                    - combat.diceRoll(4, 6, rand);
                            combat.isDead(
                                gameData.enemiesCast[gameData.living[x]],
                                displays);
                            System.out.println(
                                  gameData.enemiesCast[gameData.living[x]].name
                                  + " "
                                  + gameData.enemiesCast[gameData.living[x]].HP
                                  + " health");
                        }
                    }
                }
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Not enough mana to cast Fireball.");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }

    public void fireBolt(Player player, Random rand, Combat combat,
                          Monster monsterId, Debug debug, Displays displays) {
        if (player.MP >= 10) {
            System.out.println("You cast Fire Bolt.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 10;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 120,
                debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(1, 10, rand);
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                    " health");
                displays.pause(displays.pauseTime);
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
            System.out.println("Mana left: " + player.MP);
            displays.pause(displays.pauseTime);
        } else {
            System.out.println("Not enough mana to cast Fire Bolt.");
            displays.pause(displays.pauseTime);
        }
    }
/* When using frostbite, it has a chance to paralyze the enemy. */

    public void frostbite(Player player, Random rand, Combat combat,
                          Monster monsterId, Debug debug,
            Displays displays) {
        if (player.MP >= 10) {
            System.out.println("You cast Frostbite.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 10;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 60,
                debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(1, 6, rand);
                monsterId.parilizeTicks = monsterId.parilizeTicks + 1;
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                    " health");
                System.out.println(monsterId.name + " " + " is now parilized.");
                displays.pause(displays.pauseTime);
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Not enough mana to cast Frostbite.");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }

    public void lightningBolt(Player player, Random rand, Combat combat,
                              Monster monsterId, Debug debug,
                              Displays displays) {
        if (player.MP >= 40) {
            System.out.println("You cast Lightning Bolt.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 40;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 100,
                debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(8, 6, rand);
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                    " health");
                displays.pause(displays.pauseTime);
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Not enough mana to cast Lightning Bolt.");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }

// This spell poisons the enemy and makes them take damage for an extra turn

    public void poisonSpray(Player player, Random rand, Combat combat,
                            Monster monsterId, Debug debug,
                            Displays displays) {
        if (player.MP >= 20) {
            System.out.println("You cast Poison Spray.");
            displays.pause(displays.pauseTime);
            player.MP = player.MP - 20;
            if (combat.distanceCheck(player.combatPos, monsterId.combatPos, 10,
                debug)) {
                monsterId.HP = monsterId.HP - combat.diceRoll(1, 12, rand);
                monsterId.poisonTicks++;
                combat.isDead(monsterId, displays);
                System.out.println(monsterId.name + " " + monsterId.HP +
                                    " health");
                player.numOfTimesPlayerCastSpell++;
            } else {
                System.out.println("You were too far to hit your target.");
                displays.pause(displays.pauseTime);
            }
        } else {
            System.out.println("Not enough mana to cast Poison Spray.");
            displays.pause(displays.pauseTime);
        }
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }

// This protects the player from being hit for one turn.

    public void manaShield(Player player, Random rand, Combat combat,
                            Displays displays) {
        if (player.MP >= 20) {
            System.out.println("You cast Mana Shield.");
            player.MP = player.MP - 20;
            player.isInvulnerable = true;
            player.numOfTimesPlayerCastSpell++;
        } else {
            System.out.println("Not enough mana to cast Mana Shield.");
        }
        displays.pause(displays.pauseTime);
        System.out.println("Mana left: " + player.MP);
        displays.pause(displays.pauseTime);
    }
/* we made these two methods to make the monster take damage for an extra turn
depending if they are burned or poisoned */

    public void checkPoison(Monster monsterId, Random rand, Combat combat) {
        if (monsterId.poisonTicks > 0) {
            monsterId.HP = monsterId.HP - combat.diceRoll(1, 12, rand);
            monsterId.poisonTicks--;
        }
    }

    public void checkBurn(Monster monsterId, Random rand, Combat combat) {
        if (monsterId.burnTicks > 0) {
            monsterId.HP = monsterId.HP - combat.diceRoll(3, 6, rand);
            monsterId.burnTicks--;
        }
    }

}

// This class is for the loot the player can be found on the floor when the
// player is walking around the maze. They can either obtain a rune, learn a
// spell, or acquire a potion

class Item {

/* A rune allows the player to upgrade his weapon and make it stronger by 5
damage or can be less */

    public void obtainedRune(Random rand, Player player, Displays displays) {
        int runeValue = rand.nextInt(5) + 1;
        System.out.println("You obtained a rune for your weapon." +
                          " You apply the rune to your weapon.");
        displays.pause(displays.pauseTime);
        System.out.println("The rune adds " + runeValue +
                           " damage to your weapon.");
        displays.pause(displays.pauseTime);
        player.maxDamage = player.maxDamage + runeValue;
        System.out.println("Max Damage: " + player.maxDamage);
    }

/* This method is when the player finds a potion on the floor or on a monster's
body they will get notified about it. */

    public void getPotion(Player player, Displays displays) {
        System.out.println("You found a potion!");
        displays.pause(displays.pauseTime);
        player.numOfPotions++;
    }
/* This method is used when you are in combat fighting the monsters. When it is
your turn you have the option to use a potion and heal. */

    public void usePotion(Player player, Displays displays) {
        if (player.numOfPotions > 0) {
            player.HP = player.HP + player.potionHealingValue;
            player.MP = player.MP + player.potionManaRegenValue;
            if (player.HP > player.MAX_HP) {
                player.HP = player.MAX_HP;
            }
            if (player.MP > player.MAX_MP) {
                player.MP = player.MAX_MP;
            }
            System.out.println("HP: " + player.HP);
            System.out.println("MP: " + player.MP);
            player.numOfPotions--;
            player.numOfTimesPlayerUsedPotion++;
        } else {
            System.out.println("You reach into your pocket for a potion," +
                               " but dont seem to find any.");
            displays.pause(displays.pauseTime);
        }
    }
/* This method is used when the player finds a spell book. The spell book
  allows the player to learn a random spell with a brief description of the
  spell. */

    public void learnSpell(Random rand, Player player, GameData gameData
                        , Displays displays) {
        System.out.println("You obtain a spell book and decide to " +
                "read it's contents.");
        displays.pause(displays.pauseTime);
        String[] spellsThatYouHaventLearned = new String[11];
        int numOfSpellsHaventLearned = 0;
        boolean isLearned = true;
        for (int i = 0; i < gameData.learnableSpells.length; i++) {
            isLearned = true;
            for (int x = 0; x < player.numOfLearnedSpells; x++) {
                if (player.learnedSpells[x] == gameData.learnableSpells[i]) {
                    isLearned = false;
                }
            }
            if (isLearned) {
                spellsThatYouHaventLearned[numOfSpellsHaventLearned]
                        = gameData.learnableSpells[i];
                numOfSpellsHaventLearned++;
            }
        }
        String spell
          = spellsThatYouHaventLearned[rand.nextInt(numOfSpellsHaventLearned)];
        player.learnedSpells[player.numOfLearnedSpells] = spell;
        player.numOfLearnedSpells++;
        displays.spellInfo(spell);
    }
}

/* We created a class to display things in specific situations so it could make
things shorter for us rather than copy and pasting everything over and over. */
class Displays {

    int pauseTime = 500;

    public void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
        }
    }

// This is used in the beginning of the game and tells the player how they
// ended up in the maze

    public void displayStoryIntro() {
        System.out.println(
                "It's a bright and sunny Wednesday morning, you decide to go " +
                "on an adventure in the forest but find a weird looking cave.");
        pause(pauseTime);
        System.out.println("You decide to go inside, but fall into a hole " +
                "that leaves you stuck.");
        pause(pauseTime);
        System.out.println("You see you're in a dark room with multiple paths" +
                " to take.");
        pause(pauseTime);
        System.out.println(
                "You realize it is a maze and there is only one exist out" +
                " but remember you have to choose wisely, you never know what" +
                " you will come across.");
        pause(pauseTime);

    }
/* This whole method shows the stats of how many mosnters the player defeated
at the end of game which is when the player finds the exit to the maze. */

    public void displayMonstersKilled(GameData gameData){
        int apesKilled = 0;
        int animatedArmorsKilled = 0;
        int assassinsKilled = 0;
        int awakenedShurbsKilled = 0;
        int azersKilled = 0;
        int baboonsKilled = 0;
        int banditsKilled = 0;
        int barbedDevilsKilled = 0;
        int basilisksKilled = 0;
        int beardedDevilsKilled = 0;
        int berserkersKilled = 0;
        //counting how many of each monster was killed
        for (int x = 0; x < gameData.numOfEnemiesCast; x++){
            if (gameData.enemiesCast[x].name.equals("Ape")){
                apesKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Animated Armor")){
              animatedArmorsKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Assassin")){
                assassinsKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Awakened Shrub")){
                awakenedShurbsKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Azer")){
                azersKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Baboon")){
                baboonsKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Bandit")){
                banditsKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Barbed Devil")){
                barbedDevilsKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Basilisk")){
                basilisksKilled++;
            } else if (gameData.enemiesCast[x].name.equals("Berserker")){
                berserkersKilled++;
            } else {
                beardedDevilsKilled++;
            }
        }
        System.out.println("Apes Killed: " + apesKilled);
        pause(pauseTime);
        System.out.println("Animated Armors Killed: " + animatedArmorsKilled);
        pause(pauseTime);
        System.out.println("Assassins Killed: " + assassinsKilled);
        pause(pauseTime);
        System.out.println("Awakened Shrubs Killed: " + awakenedShurbsKilled);
        pause(pauseTime);
        System.out.println("Azers Killed: " + azersKilled);
        pause(pauseTime);
        System.out.println("Baboons Killed: " + baboonsKilled);
        pause(pauseTime);
        System.out.println("Bandits Killed: " + banditsKilled);
        pause(pauseTime);
        System.out.println("Barbed Devils Killed: " + barbedDevilsKilled);
        pause(pauseTime);
        System.out.println("Basilisks Killed: " + basilisksKilled);
        pause(pauseTime);
        System.out.println("Bearded Devils Killed: " + beardedDevilsKilled);
        pause(pauseTime);
        System.out.println("Berserkers Killed: " + berserkersKilled);
        pause(pauseTime);
        System.out.println("Total number of monsters killed: "
            + gameData.numOfEnemiesCast);
        pause(pauseTime);
    }

/* This method gives the final stats of the player when they exit the maze. */

    public void displayStoryConclusion( Player player, GameData gameData) {
        System.out.println(
                "Thank you for playing the Adventure Game! \nCreated by " +
                "Brook Alamnew, Angela Bautista Lopez, Adrian Flores" +
                " Aquino, Jian Mejia, and Liana Zhu.");
        pause(pauseTime);
        System.out.println("Final Scores:");
        pause(pauseTime);
        System.out.println("-Player Stats-");
        pause(pauseTime);
        System.out.println("Player HP: " + player.HP);
        pause(pauseTime);
        System.out.println("Player MP: " + player.MP);
        pause(pauseTime);
        System.out.println("Max Damage: " + player.maxDamage);
        pause(pauseTime);
        System.out.print("Spells learned: ");
        displayUsableSpells(player);
        pause(pauseTime);
        System.out.println("-Maze Stats-");
        pause(pauseTime);
        System.out.println("Total encounters: "
          + player.totalNumOfEncountersEncountered);
        pause(pauseTime);
        System.out.println("Total moves taken to reach exit: "
          + player.movesTakenToReachEndOfMaze);
        pause(pauseTime);
        System.out.println("-Combat Stats-");
        pause(pauseTime);
        System.out.println("Total movements taken: "
          + player.numOfTimesPlayerMoved);
        pause(pauseTime);
        System.out.println("Total distance moved: "
          + player.totalDistancePlayerMoved);
        pause(pauseTime);
        System.out.println("Total melee attacks: "
          + player.numOfTimesPlayerMeleeAttack);
        pause(pauseTime);
        System.out.println("Total spells cast: "
          + player.numOfTimesPlayerCastSpell);
        pause(pauseTime);
        System.out.println("Total potions used: "
          + player.numOfTimesPlayerUsedPotion);
        pause(pauseTime);
        displayMonstersKilled(gameData);
    }
// This gives the player the option to decide what they want to do in their turn

    public void displayPlayerTurn(Player player) {
        displayPlayerStats(player);
        System.out.println("It is your turn.");
        pause(pauseTime);
        System.out.println("What would you like to do?\n <Attack, " +
                "Cast Spell, Move, Consume Potion, End Turn>");
        pause(pauseTime);
        // player turn display
        // weapon attack, cast spells, move, consume potion, end turn
    }

// This shows the player their stats when they are in combat

    public void displayPlayerStats(Player player) {
        System.out.println("Player Stats: ");
        pause(pauseTime);
        System.out.println("\tPlayer HP: " + player.HP);
        pause(pauseTime);
        System.out.println("\tPlayer MP: " + player.MP);
        pause(pauseTime);
        System.out.println("Learned Spells: ");
        pause(pauseTime);
        System.out.print("\t");
        displayUsableSpells(player);
        pause(pauseTime);
        System.out.println("Amount of Potions: " + player.numOfPotions);
        pause(pauseTime);
    }

// This displays the monster's stats during combat

    public void displayMonsterStats(Monster monster) {
        System.out.println(monster.name + " Stats:");
        pause(pauseTime);
        System.out.println("\tHP: " + monster.HP);
        pause(pauseTime);
        System.out.println("\tDamage: " + monster.multiplyDamage + "d" +
                monster.randDamage + "+" + monster.addDamage
                + " (x" + monster.numOfAttack + ")");
        pause(pauseTime);
        System.out.println("\tSpeed: " + monster.speed);
    }

// When the player decides to move in their turn, this gives the player their
// current positon so they can see where to move.

    public void displayPlayerPos(Player player) {
        System.out.println("Your position: <"
                + (player.combatPos[0] + 1) + ", "
                + (player.combatPos[1] + 1) + ">");
        pause(pauseTime);
    }

// This shows the postion of the monsters

    public void displayMonsterPos(GameData gameData) {
        for (int i = 0; i < gameData.numOfEnemiesInPlay; i++) {
            if (!gameData.enemiesCast[gameData.living[i]].isDeceased) {
                System.out.println(gameData.enemiesCast[gameData.living[i]].name
                  + "'s position: <"
                  + (gameData.enemiesCast[gameData.living[i]].combatPos[0] + 1)
                  + ", "
                  + (gameData.enemiesCast[gameData.living[i]].combatPos[1] + 1)
                  + ">");
                System.out.println(
                  "HP: " + gameData.enemiesCast[gameData.living[i]].HP);
                pause(pauseTime);
            }
        }
    }

// This gives a brief summary of each spell when learning it.

    public void spellInfo(String spell) {
        if (spell.equals("cure wounds")) {
            System.out.println(
                    "You have leaned the spell <Cure Wounds>.\nCost: " +
                    "10MP\nRange: Self\nDescription: Heal player for 1d30 HP");
        } else if (spell == "heal") {
            System.out.println(
                    "You have leaned the spell <Heal>.\nCost: 40MP" +
                    "\nRange: Self\nDescription: Heal player for 70 HP");
        } else if (spell == "flame blade") {
            System.out.println(
                    "You have learned the spell <Flame Blade>.\nCost: 15MP" +
                    "\nRange: 5ft\nDescription: Deal 3d6 damage to an enemy.");
        } else if (spell == "acid arrow") {
            System.out.println(
                    "You have learned the spell <Acid Arrow>.\nCost: 10MP" +
                    "\nRange: 90ft\nDescription: Deal 2d4 damage to an enemy.");
        } else if (spell == "burning hands") {
            System.out.println(
                    "You have learned the spell <Burning Hands>.\nCost:" +
                    " 35MP\nRange: 5ft\nDescription: Cast burn on an enemy " +
                    "for 3 turns. \nDeal 3d6 damage to enemy cast with burn.");
        } else if (spell == "fireball") {
            System.out.println(
                    "You have learned the spell <Fireball>.\nCost: 50MP" +
                    "\nRange: 150ft\nBlast Radius: 20ft\nDescription: Deal" +
                    " 8d6 damage to an enemy and do 4d6 damage to all enemies" +
                    " within the blast radius.");
        } else if (spell == "fire bolt") {
            System.out.println(
                    "You have learned the spell <Fire Bolt>.\nCost: " +
                    "10MP\nRange: 120ft\nDescription: Deal 1d10 to an enemy.");
        } else if (spell == "frostbite") {
            System.out.println(
                    "You have learned the spell <Frostbite>.\nCost: " +
                    "10MP\nRange: 60ft\nDescription: Deal 1d6 damage to an" +
                    " enemy and parilize them for 1 turn.");
        } else if (spell == "lightning bolt") {
            System.out.println(
                    "You have learned the spell <Lightning Bolt>.\nCost: " +
                    "40MP\nRange: 100ft\nDescription: Deal 8d6 damage to an " +
                    "enemy.");
        } else if (spell == "poison spray") {
            System.out.println(
                    "You have learned the spell <Poison Spray>.\nCost: " +
                    "20MP\nRange: 10ft\nDescription: Cast poison on an enemy " +
                    "for 2 turns. \nDeal 1d12 damage to enemy cast with burn.");
        } else {
            System.out.println(
                    "You have learned the spell <Mana Shield>.\nCost: " +
                    "20MP\nRange: Self\nDescription: Grants invulnerability" +
                    " for one attack.");
        }
        pause(pauseTime);
    }

// This method was created so when the player enters their first fight, they
// will be given a summary of how to fight and what they need to know to
// be able to defeat the monsters.

    public void tutorialCombat() {
        System.out.println("Welcome! To your very first fight!");
        pause(pauseTime);
        System.out.println("The player is represented on the grid as 'P'.");
        pause(pauseTime);
        System.out.println("The enemies are represented on the grid as 'M'.");
        pause(pauseTime);
        System.out.println("Your max melee distance is 5 feet.");
        pause(pauseTime);
        System.out.println("The area in which you will be fighting is " +
                "200x200 feet.");
        pause(pauseTime);
        System.out.println("Each unit square is 5 feet.");
        pause(pauseTime);
        System.out.println(
                "Your turn will consist of two actions which can be " +
                "using melee attacks, spells, potions, or you can move");
        pause(pauseTime);
        System.out.println("You will also be given the choice to end " +
                "your turn at any time");
        pause(pauseTime);
        System.out.println(
                "You have a variety of spells that can either heal you, " +
                "give you immunity, damage the enemy or even burn them");
        pause(pauseTime);
        System.out.println(
                "Using spells comes at a cost, you will have to keep track" +
                " of your mana to be able to determine whether you have " +
                "enough mana to use the spell");
        pause(pauseTime);
        System.out.println(
                "To be able to acquire spells you first have to learn them " +
                "through obtaining spell books along your journey");
        pause(pauseTime);
        System.out.println(
                "Every monster encounter is not the same, they come in " +
                "different amounts of groups so be aware of your " +
                "surroundings.");
        pause(pauseTime);
        System.out.println("Since this is your first fight, the divine " +
                "beings of this labyrinth grant you wisdom.");
        pause(pauseTime);
    }

// This shows the player which spells they have learned so far and can use.

    public void displayUsableSpells(Player player) {
        String spells = "[";
        // print out the names of the spells the player can use
        for (int x = 0; x < player.numOfLearnedSpells; x++) {
            // check forward
            spells = spells + player.learnedSpells[x];
            if (x != player.numOfLearnedSpells - 1) {
                spells = spells + ", ";
            }
        }
        spells = spells + "]";
        System.out.println(spells);
        pause(pauseTime);
    }
}

class Help {
    public void checkHelp(String[] args, Scanner input, Displays displays) {
        if (args.length != 0 && args[0].equals("-help")) {
            displayHelp(input, displays);
            System.exit(0);
        }
    }

    // This gives the player a brief summary and the rules of the game
    public static void displayHelp(Scanner input, Displays displays) {
        boolean end = false; //used as the condition for the while loop
        System.out.println("Adventure Game Help");
        displays.pause(displays.pauseTime);
        //explaining the intro rules
        System.out.println(
                "\nThe purpose of the game is to escape the maze cavern without"
                 + " being defeated by monster enemies.");
        displays.pause(displays.pauseTime);
        System.out.println(
                "At the beginning of the game, you start off with 100 health" +
                " points (HP), a random amount of mana points (MP), ranging" +
                " from 50-100, and a minimum (3) and maximum (11) amount of" +
                " damage you can inflict.");
        displays.pause(displays.pauseTime);
        System.out.println(
                "Depending on the series of events you encounter throughout" +
                " the game, your HP, MP, and max damage will fluctuate" +
                " throughout the game.");
        displays.pause(displays.pauseTime);
        System.out.println(
                "In order to stay alive and defend yourself against monster" +
                " enemies, keep your HP above 0.");
        displays.pause(displays.pauseTime);
        System.out.println("How to Play =>> choose how large you want the" +
                " maze on the x-axis and y-axis, select " + " choice of" +
                " spell and weapon, " + "pick the position you want to move" +
                " on the X and Y-axis, use the spell you" +
                " would like to cast, select the enemy" +
                " position you would like to attack, pick the direction" +
                " you want to go N, S, W, E, Choose whether you want" +
                " to attack, cast, move, consume or end");
        // This gives the player the option to decide what sections they are
        // trying to learn about.
        // Each section corresponds to a specifc topic about the game.
        while (!end) {
            System.out.println("What would you like to know about?");
            displays.pause(displays.pauseTime);
            System.out.println("<Navigating The Maze, Loot, Encounters," +
                        " Combat, Spells, Monsters, End>");
            displays.pause(displays.pauseTime);
            String choice = input.nextLine().toLowerCase();
            //runs if the user chose navigating the maze
            if (choice.equals("navigating the maze")) {
                System.out.println("\nNavigating the Maze:");
                displays.pause(displays.pauseTime);
                System.out.println("You will have the option to go forward," +
                        " back, left or right.");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "If you run into a wall, the program will let you" +
                        " know you cannot move in that direction.");
                displays.pause(displays.pauseTime);
              //option for loot
            } else if (choice.equals("loot")) {
                System.out.println("\nLoot:");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Throughout your journey in, there's a chance you" +
                        " will find random loot spawns on the floor.");
                displays.pause(displays.pauseTime);
                System.out.println("The loot found are either potions," +
                        " spellbooks or runes.");
                displays.pause(displays.pauseTime);
                System.out.println("Runes are used to upgrade your weapon's" +
                        " max damage range against enemies.");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "When discovering a spellbook, you're able to learn" +
                        " a spell, (you have the chance to learn a total of" +
                        " 11 spells).");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Each spells have different affects, they can either" +
                        " heal you, protect you or hurt the monsters. More" +
                        " info on each spell can be found further in" +
                        " the -help section");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "When using potions, your HP and MP are increased." +
                        " However, you cannot go over max limit of points.");
                displays.pause(displays.pauseTime);
              //option for encounters
            } else if (choice.equals("encounters")) {
                System.out.println("\nEncounters:");
                displays.pause(displays.pauseTime);
                System.out.println("While in the maze, you will also" +
                        " encounter different monster enemies.");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Monsters will have different amounts of damage, HP," +
                        " and speed. More info on each monster can be found" +
                        " further in the -help- section.");
                displays.pause(displays.pauseTime);
                System.out.println("The monsters can come in different" +
                " amount of groups when encountering them.");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "When you meet a monster, you will be shown a 40 x" +
                        " 40 combat board, where you can fight the monster.");
                displays.pause(displays.pauseTime);
              //option for combat
            } else if (choice.equals("combat")) {
                System.out.println("\nCombat:");
                displays.pause(displays.pauseTime);
                System.out.println("Each spell and attack have a range " +
                "depending on what ability you decide to use.");
                displays.pause(displays.pauseTime);
                System.out.println("If you are out of range to use your " +
                "ability, you will not be able to use it.");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "When in combat you will be given the option" +
                        " to attack or simply just end your turn");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "In order to decide where you want to want to use " +
                        "your abilities, you will have to give the exact " +
                        "coordinates of where the monsters are at.");
                displays.pause(displays.pauseTime);
                System.out.println("The coordinates consist of an x-axis " +
                        "and y-axis.");
                displays.pause(displays.pauseTime);
                System.out.println("If there is no monster in the given" +
                        " coordinates you will not be able to attack.");
                displays.pause(displays.pauseTime);
              //option for spells
            } else if (choice.equals("spells")) {
                System.out.println("\nSpells: ");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Cure Wounds     \nCost: 10MP  \nRange: Self" +
                        "\nDescription: Heal player for 1d30 HP.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Heal            \nCost: 40MP  \nRange: Self " +
                        "\nDescription: Heal player for 70 HP.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Flame Blade     \nCost: 15MP  \nRange: 5ft " +
                        "\nDescription: Deal 3d6 damage to an enemy.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Acid Arrow      \nCost: 10MP  \nRange: 90ft" +
                        "\nDescription: Deal 2d4 damage to an enemy.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Burning Hands   \nCost: 35MP  \nRange: 5ft" +
                        "\nDescription: Cast burn on an enemy for 3 turns. " +
                        "Deal 3d6 damage to enemy cast with burn.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Fireball        \nCost: 50MP  \nRange: 150ft " +
                        "\nBlast Radius: 20ft  \nDescription: Deal 8d6 damage" +
                        " to an enemy and do 4d6 damage to all enemies within" +
                        " the blast radius.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Fire Bolt       \nCost: 10MP  \nRange: 120ft" +
                        "\nDescription: Deal 1d10 to an enemy.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Frostbite \nCost: 10MP  \nRange: 60ft " +
                        "\nDescription: Deal 1d6 damage to an enemy " +
                        "and parilize them for 1 turn.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Lightning Bolt  \nCost: 40MP  \nRange: 100ft " +
                        "\nDescription: Deal 8d6 damage to an enemy.\n");
                System.out.println(
                        "Name: Poison Spray    \nCost: 20MP  \nRange: 10ft" +
                        "\nDescription: Cast poison on an enemy for 2 turns." +
                        " \nDeal 1d12 damage to enemy cast with burn.\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                        "Name: Mana Shield     \nCost: 20MP  \nRange: Self" +
                        "\nDescription: Grants invulnerability for one" +
                        " attack.\n");
                displays.pause(displays.pauseTime);
              //option for monsters
            } else if (choice.equals("monsters")) {
                System.out.println("\nMonsters: ");
                System.out.println("Name: Animated Armor" +
                        "\nHealth: 6d8+6  \nSpeed: 25 feet \nDamage: 1d6+2\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Ape" +
                        "\nHealth: 3d8+6  \nSpeed: 30 feet \nDamage: 1d6+3\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Assassin \nHealth: 6d8+24 \nSpeed: " +
                        "30 feet \nDamage: 1d6+3 (x2)\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Awakened Shurb" +
                        "\nHealth: 1d4-1  \nSpeed: 20 feet \nDamage: 1d4-1\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Azer" +
                        "\nHealth: 1d10+3 \nSpeed: 30 feet \nDamage: 1d10+3\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Baboon\nHealth: 1d6\nSpeed: " +
                        "30 feet \nDamage: 1d4-1\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Bandit \nHealth: 2d8+2  \nSpeed: " +
                        "30 feet \nDamage: 1d6+1\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Barbed Devil \nHealth: 5d8+26 " +
                        "\nSpeed: 30 feet \nDamage: 1d6+3 (x2)\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Basilisk \nHealth: 8d8+16 " +
                        "\nSpeed: 20 feet \nDamage: 2d6+3\n");
                displays.pause(displays.pauseTime);
                System.out.println("Name: Bearded Devil \nHealth: 8d8+16 " +
                        "\nSpeed: 30 feet \nDamage: 1d8+2 (x2)\n");
                displays.pause(displays.pauseTime);
                System.out.println(
                  "Name: Berserker \nHealth: 9d8+27 \nSpeed: 30 feet " +
                        "\nDamage: 1d12+3\n");
              //if the user chooses to end, it changes the end variable to true
              //and exits the while loop
            } else if (choice.equals("end")) {
                end = true;
            } else {
                System.out.println("Invalid command");
            }
        }
        System.exit(0);
    }
}

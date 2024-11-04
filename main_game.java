import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class gameMap {
    static char[][] matrix;

    public static char[][] generateMap(int StartingColumn, int StartingRow, Player player, Enemy enemy, int randomDoorRow, int randomDoorColumn, int randomItemColumn, int randomItemRow) {
        matrix = new char[StartingColumn][StartingRow];
        System.out.println("Generating Map.....");

        System.out.print('|');
        for (int j = 0; j < StartingRow; j++) { 
            System.out.print('-'); 
        }      
        System.out.print('|');     
        System.out.println();


        for (int i = 0; i < StartingColumn; i++) {
            System.out.print('|');

            for (int j = 0; j < StartingRow; j++) {
                // PLAYER MARKER IS @
                if (j == player.RowPosition && i == player.ColumnPosition){
                    matrix[i][j] = '@';
                    System.out.print(matrix[i][j]);
                // ENEMY MARRKER IS !
                } if (j == enemy.RowPosition && i == enemy.ColumnPosition){
                    matrix[i][j] = '!';
                    System.out.print(matrix[i][j]);
                // DOOR MARKER IS o
                }else if (j == randomDoorRow && i == randomDoorColumn) {
                    matrix[i][j] = 'o';
                 // DOOR ITEM IS #
                }else if (j == randomItemRow && i == randomItemColumn) {
                    matrix[i][j] = '#';
                }
                
                // WALWAY MARKER IS .
                else{
                    matrix[i][j] = '.'; 
                System.out.print(matrix[i][j]);
                }
            }

            // Print the right border
            if (i == StartingColumn){
                System.out.print('/');
            }else{
                System.out.print('|');
            }
            System.out.println();
        }

        System.out.print('|'); 
        for (int j = 0; j < StartingRow; j++) { 
            System.out.print('-'); 
        }
        System.out.print('|'); 
        System.out.println();
        return matrix;
    }

    public static char[][] printMap(Player player, Enemy enemy,int randomDoorRow, int randomDoorColumn, Item item) {
        System.out.println("Generating Map.....");

        System.out.print('|');
        for (int j = 0; j < matrix[0].length; j++) { 
            System.out.print('-'); 
        }      
        System.out.print('|');     
        System.out.println();


        for (int i = 0; i < matrix.length; i++) {
            System.out.print('|');

            for (int j = 0; j < matrix[i].length; j++) {
                if (j == player.RowPosition && i == player.ColumnPosition){
                    matrix[i][j] = '@';
                    System.out.print(matrix[i][j]);
                }
                else if (j == enemy.RowPosition && i == enemy.ColumnPosition && enemy.alive) {
                    matrix[i][j] = '!';
                    System.out.print(matrix[i][j]);
                }
                else if (j == randomDoorRow && i == randomDoorColumn){
                    matrix[i][j] = 'o';
                    System.out.print(matrix[i][j]);
                }
                else if (j == item.RowPosition && i == item.ColumnPosition && item.Active) {
                    matrix[i][j] = '#';
                    System.out.print(matrix[i][j]);
                }
                else{
                    matrix[i][j] = '.'; 
                System.out.print(matrix[i][j]);
                }
            }

            // Print the right border
            System.out.print('|');
            System.out.println();
        }

        System.out.print('|'); 
        for (int j = 0; j < matrix[0].length; j++) { 
            System.out.print('-'); 
        }
        System.out.print('|'); 
        System.out.println();
        return matrix;
    }

}

class Player {
    int max_health;
    int health;
    int damage;
    int experience;
    int max_experience;

    int level;
    int gold;

    int RowPosition;
    int ColumnPosition;

    public Player(int hp, int dmg, int lvl, int gld, int exp) {
        this.max_health = hp;
        this.health = hp;
        this.damage = dmg;
        this.level = lvl;
        this.experience = exp;
        this.gold = gld;
        this.max_experience = this.level * 5;

        this.RowPosition = 1;
        this.ColumnPosition = 1;
    }

    // Deal the dmg of the Player
    public int DealDamage(Enemy enemy, Player player) {
        enemy.health -= player.damage;
        return enemy.health;
    }

    public void LevelUp(Player player){
        if (player.experience >= player.max_experience){

            player.experience = 0;
            player.level++;
            player.max_experience = player.level * 5;
            player.damage++;
            player.max_health += 5;
            player.health = player.max_health;
            player.gold += 10;

            System.err.println("YOU LEVELED UP! ALL STATS UP!");
        }
    }

    public boolean MovePlayer(char direction, Player player, Enemy enemy, Scanner UIS,int randomDoorColumn, int randomDoorRow, Item item) {
        int numRows = gameMap.matrix.length;
        int numColumns = gameMap.matrix[0].length;
    
        // Ensure input is case-insensitive
        direction = Character.toLowerCase(direction);
        
        int newRowPosition = player.RowPosition;
        int newColumnPosition = player.ColumnPosition;

        switch (direction) {
            case 'w' -> {
                // Move up
                if (player.ColumnPosition > 0) {
                    newColumnPosition = player.ColumnPosition - 1;
                }
            }
            case 's' -> {
                // Move down
                if (player.ColumnPosition < numRows - 1) {
                    newColumnPosition = player.ColumnPosition + 1;
                }
            }
            case 'a' -> {
                // Move left
                if (player.RowPosition > 0) {
                    newRowPosition = player.RowPosition - 1;
                }
            }
            case 'd' -> {
                // Move right
                if (player.RowPosition < numColumns - 1) {
                    newRowPosition = player.RowPosition + 1;
                }
            }
            default -> System.err.println("Wrong movement direction, try again.");
        }

        if (newRowPosition == enemy.RowPosition && newColumnPosition == enemy.ColumnPosition && enemy.alive){

            System.out.println("You encountered an enemy!");
            System.out.println("Fight: F | Run: R | Examine: E");

            char Input = UIS.nextLine().charAt(0);

            switch (Character.toLowerCase(Input)) {
                case 'f' -> {
                    player.DealDamage(enemy, player);
                    if (enemy.health <= 0){

                        player.experience += enemy.level;
                        player.gold += enemy.level;

                        System.err.println("\u001B[31m" + "Enemy Perished! You took 0 dmg. " + enemy.level + " XP Granted" + "\u001B[0m");

                        enemy.alive = false;
                        gameMap.matrix[enemy.ColumnPosition][enemy.RowPosition] = '.';
                        
                        player.LevelUp(player);
                    }
                    else if (enemy.health >= 1){
                        enemy.DealDamage(player, enemy);
                        System.err.println("Enemy lived! You took " + enemy.damage + " DMG ");
                    }
                }
                case 'r' -> System.err.println("Got away successfully!");
                case 'e' -> System.err.println("Enemy has " + enemy.health + " HP, and deals " + enemy.damage + " DMG");
                default -> {
                }
            }
        }else if (newRowPosition == item.RowPosition && newColumnPosition == item.ColumnPosition && item.Active) {
            player.health += item.heal;
            player.max_health += item.health;
            player.damage += item.strength;
            player.experience += item.experience;
            item.Active = false;
            player.LevelUp(player);
            System.err.println("You found a " + item.itemName + "! STR:" + item.strength + " HP " + item.heal + " MHP " + item.health + " XP " + item.experience);
        }
        else if(newRowPosition == randomDoorRow && newColumnPosition == randomDoorColumn){
            return true;
        }
        else {
            player.ColumnPosition = newColumnPosition;
            player.RowPosition = newRowPosition;
        }
        return false;
    }    
}

class Item{
    // hashmaps are offbrand dictionaries omg 
    int strength;
    int heal;
    int health;
    int experience;

    Boolean Active;
    int RowPosition;
    int ColumnPosition;

    String itemName = "null";

    public Item(int Str, int HEAL_AMOUNT, int HP, int XP, Boolean isActive, int rowPos,int columnPos, String NAME){
        this.strength = Str;
        this.heal = HEAL_AMOUNT;
        this.health = HP;
        this.experience = XP;

        this.Active = isActive;
        this.RowPosition = rowPos;
        this.ColumnPosition = columnPos;

        this.itemName = NAME;
    }

}
class Enemy {
    int health;
    int damage;
    boolean alive;
    int level;

    int RowPosition;
    int ColumnPosition;

    public Enemy(int hp, int dmg, int lvl, int rowpos, int columpos) {
        this.health = hp;
        this.damage = dmg;
        this.level = lvl;
        this.alive = true;
        this.RowPosition = rowpos;
        this.ColumnPosition = columpos;
    }

    public static Enemy spawnEnemy(Player player, int gameLoop, int rowPos, int columnPos){
        Enemy newEnemy = new Enemy(
            player.level * (gameLoop / 5) + gameLoop * 2, // Increase HP
            player.level * (gameLoop / 5) + gameLoop * 2, // Increase DMG
            player.level * (gameLoop / 5) + gameLoop * 2, // Increase LVL

            ThreadLocalRandom.current().nextInt(0,rowPos), // Position in row
            ThreadLocalRandom.current().nextInt(0,columnPos) // Position in column
        );

        return newEnemy;
    }
    // Deal the dmg of the enemy
    public int DealDamage(Player player, Enemy enemy) {
        player.health -= enemy.damage;
        return player.health;
    }
}

public class Rogue_Game {
    public static void main(String[] args) {

        // Create a list of the items in-game (STREGNTH / HEAL / HP BOOST / XP BOOST)


        try (Scanner UIS = new Scanner(System.in)) {
            Boolean isGameActive = true;
            
            Player player = new Player(25, 5, 1, 25,0);
            
            // Prompt for starting ROW
            System.out.print("What ROW would you like to start at (Try 25)?");
            int StartRow = UIS.nextInt();
            UIS.nextLine();
            
            // Prompt for starting COLUMN
            System.out.print("What COLUMN would you like to start at (Try 5)?");
            int StartColumn = UIS.nextInt();
            UIS.nextLine();

            if (StartRow > 0 && StartColumn > 0) {
                gameMap.generateMap(StartColumn,
                 StartRow,
                 player,
                  Enemy.spawnEnemy(player, 1, StartRow, StartColumn),
                    StartRow,
                    StartColumn,
                    StartRow - 1,
                    StartColumn - 1
                  );


            } else {
                System.out.println("Row and column must be greater than 0.");
            }

            int GameLoop = 1;
            int lastGameLoop = 1;

            Enemy currEnemy = Enemy.spawnEnemy(player,GameLoop,StartRow,StartColumn);
            Item itemObj = new Item(0,0,0,0,true,3,3,"NOTHING.");

            int randomDoorRow = 2;
            int randomDoorColumn = 2;

            int randomItemRow = 3;
            int randomItemColumn = 3;

            int randomItemNumber = 1;
            Item[] items = new Item[] {
                new Item(5, 0, 0, 0, true, randomItemRow, randomItemColumn, "Broken Sword"),
                new Item(10, 0, 0, 0, true, randomItemRow, randomItemColumn, "Steel Sword"),
                new Item(15, 0, 0, 0, true, randomItemRow, randomItemColumn, "Enchanted Sword"),
                new Item(0, 0, 5, 0, true, randomItemRow, randomItemColumn, "Shield"),
                new Item(0, 0, 10, 0, true, randomItemRow, randomItemColumn, "Armor"),
                new Item(0, 0, 15, 0, true, randomItemRow, randomItemColumn, "Enchanted Armor"),
                new Item(5, 0, 0, 0, true, randomItemRow, randomItemColumn, "STR Potion"),
                new Item(0, 0, 20, 0, true, randomItemRow, randomItemColumn, "HP UP Potion"),
                new Item(0, 10, 0, 0, true, randomItemRow, randomItemColumn, "HP Potion"),
                new Item(0, 5, 5, 0, true, randomItemRow, randomItemColumn, "Bandaid"),
                new Item(0, 10, 10, 0, true, randomItemRow, randomItemColumn, "Medkit"),
                new Item(0, 15, 15, 0, true, randomItemRow, randomItemColumn, "Enchanted Medkit"),
                new Item(0, 0, 0, 10, true, randomItemRow, randomItemColumn, "Small XP Potion"),
                new Item(0, 0, 0, 25, true, randomItemRow, randomItemColumn, "Medium XP Potion"),
                new Item(0, 0, 0, 50, true, randomItemRow, randomItemColumn, "Large XP Potion"),
                new Item(20, 20, 20, 20, true, randomItemRow, randomItemColumn, "!!ENCHANTED POTION!!")
            };

            while (isGameActive){

                if (player.health <= 0){
                    System.out.println("    _____      ____       __    __      _____        ____     __    __    _____   ______    ");
                    System.out.println("   / ___ \\    (    )      \\ \\  / /     / ___/       / __ \\    ) )  ( (   / ___/  (   __ \\   ");
                    System.out.println("  / /   \\_)   / /\\ \\      () \\/ ()    ( (__        / /  \\ \\  ( (    ) ) ( (__     ) (__) )  ");
                    System.out.println(" ( (  ____   ( (__) )     / _  _ \\     ) __)      ( ()  () )  \\ \\  / /   ) __)   (    __/   ");
                    System.out.println(" ( ( (__  )   )    (     / / \\/ \\ \\   ( (         ( ()  () )   \\ \\/ /   ( (       ) \\ \\  _  ");
                    System.out.println("  \\ \\__/ /   /  /\\  \\   /_/      \\_\\   \\ \\___      \\ \\__/ /     \\  /     \\ \\___  ( ( \\ \\_) ");
                    System.out.println("   \\____/   /__(  )__\\ (/          \\)   \\____\\      \\____/       \\/       \\____\\  )_) \\__/  ");
                    break;
                }

                System.out.println("Where would you like to move? (W/A/S/D)(Q to LEAVE)");

                char Input = 'W';

                String inputLine = UIS.nextLine().trim(); // Trim whitespace

                if (!inputLine.isEmpty()) {
                    Input = inputLine.charAt(0);
                    if (Input == 'Q') {
                        break;
                    }
                } else {
                    System.out.println("Please enter a valid direction (W/A/S/D) or Q to leave.");
                }

                // Game loops will determine room generation.
                if (lastGameLoop != GameLoop){

                    lastGameLoop = GameLoop;

                    int RowNumber = ThreadLocalRandom.current().nextInt(15,30);
                    int ColumnNumber = ThreadLocalRandom.current().nextInt(5,8);

                    currEnemy = Enemy.spawnEnemy(player,GameLoop,RowNumber,ColumnNumber);

                    randomDoorRow = ThreadLocalRandom.current().nextInt(0,RowNumber);
                    randomDoorColumn = ThreadLocalRandom.current().nextInt(0,ColumnNumber);

                    randomItemNumber = ThreadLocalRandom.current().nextInt(0,items.length);
                    itemObj = items[randomItemNumber];

                    System.out.println("RANDOM ITEM: " + itemObj);

                    randomItemRow = ThreadLocalRandom.current().nextInt(0,RowNumber);
                    randomItemColumn = ThreadLocalRandom.current().nextInt(0,ColumnNumber);

                    itemObj.RowPosition = randomItemRow;
                    itemObj.ColumnPosition = randomItemColumn;

                    gameMap.generateMap(ColumnNumber, RowNumber, player,currEnemy,randomDoorRow,randomDoorColumn,randomItemColumn,randomItemRow);
                }

                if (player.MovePlayer(Input,player,currEnemy,UIS, randomDoorColumn,randomDoorRow,itemObj)){
                    GameLoop++;
                    System.err.println("\u001B[31m" + "Congrats! New floor Achieved. Difficuly has been raised, goodluck" + "\u001B[0m");
                }
                else{
                gameMap.printMap(player,currEnemy,randomDoorRow,randomDoorColumn, itemObj);
                // Print the players stats

                System.out.print("LVL:" + player.level);
                System.out.print(" HP:" + player.health + "(" + player.max_health + ")");
                System.out.print(" G:" + player.gold);
                System.out.print(" DMG:" + player.damage);
                System.out.print(" EXP:" + player.experience + "(" + player.max_experience + ")");

                System.out.println("");
                }
            }
        }
    }
}

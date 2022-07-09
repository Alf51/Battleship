package battleship;

public class Game {

    public void startGame() {
        battleship.Battlefield player1 = new battleship.Battlefield("Player 1");
        battleship.Battlefield player2 = new battleship.Battlefield("Player 2");

        player1.createFleet();
        player2.createFleet();

        player1.setEnemyBattlefield(player2);
        player2.setEnemyBattlefield(player1);

        //Делаем выстрелы, пока не определим победителя
        while (true) {
            player1.takeShot(player2);
            player2.takeShot(player1);
        }
    }
}

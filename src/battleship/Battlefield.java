package battleship;

import battleship.ships.Direction;
import battleship.ships.Ship;
import battleship.ships.ShipType;
import java.io.IOException;
import java.util.Scanner;


public class Battlefield {

    private int shipCount = 0;
    private final String PLAYER_NAME;
    private final String[][] battlefield = createBattlefield();
    private final String[][] fogWarBattlefield = createBattlefield();
    private  String[][] enemyBattlefield;
    private static final int START_MEASURE_COORDINATE_Y = 64;
    private static final String OCCUPIED_CELL_MARK = " O";
    private static final String FREE_CELL_MARK = " ~";
    private static final String HIT_SHIP_MARK = " X";
    private static final String MISS_MARK = " M";
    private static final String MESSAGE_HIT_SHIP = "You hit a ship!";
    private static final String MESSAGE_MISS_SHIP = "You missed!";
    private static final String MESSAGE_TURN_NEXT_PLAYER = "Press Enter and pass the move to another player";

    public Battlefield(String PLAYER_NAME) {
        this.PLAYER_NAME = PLAYER_NAME;
    }

    // Вражеское поле, по которому ведется выстрел
    public void setEnemyBattlefield(Battlefield enemyBattlefield) {
        this.enemyBattlefield = enemyBattlefield.battlefield;
    }


    public static void showBattlefield(String[][] battlefield) {
        for (String[] strings : battlefield) {
            for (String string : strings) {
                System.out.print(string);
            }
            System.out.println();
        }
    }

    public static void showTwoField(String[][] fogWarBattlefield, String[][] battlefield) {
        showBattlefield(fogWarBattlefield);
        System.out.println("---------------------");
        showBattlefield(battlefield);
    }

    public void createFleet() {
        int numberButtonMenu = 1;
        System.out.printf("%s, place your battleship.ships on the game field\n", PLAYER_NAME);
        showBattlefield(battlefield);
        boolean isShipsStationed = false;
        while (!isShipsStationed) {
            try {
                switch (numberButtonMenu) {
                    case 1 -> {
                        createShip(ShipType.AIRCRAFT_CARRIER);
                        showBattlefield(battlefield);
                    }
                    case 2 -> {
                        createShip(ShipType.BATTLESHIP);
                        showBattlefield(battlefield);
                    }
                    case 3 -> {
                        createShip(ShipType.SUBMARINE);
                        showBattlefield(battlefield);
                    }
                    case 4 -> {
                        createShip(ShipType.CRUISER);
                        showBattlefield(battlefield);
                    }
                    case 5 -> {
                        createShip(ShipType.DESTROYER);
                        showBattlefield(battlefield);
                    }
                    case 6 -> {
                        promptEnterKey();
                        isShipsStationed = true;
                    }
                }

                numberButtonMenu++;

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public void takeShot(Battlefield battlefield2) {
        showTwoField(fogWarBattlefield, battlefield);
        System.out.printf("%s, it's your turn:\n", PLAYER_NAME);
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                String shotCoordinate = scanner.next().toUpperCase();
                int coordinateX = getStartCoordinateX(shotCoordinate);
                int coordinateY = getStartCoordinateY(shotCoordinate);
                switch (enemyBattlefield[coordinateY][coordinateX]) {
                    case OCCUPIED_CELL_MARK -> {
                        enemyBattlefield[coordinateY][coordinateX] = HIT_SHIP_MARK;
                        battlefield2.battlefield[coordinateY][coordinateX] = HIT_SHIP_MARK;
                        fogWarBattlefield[coordinateY][coordinateX] = HIT_SHIP_MARK;
                        System.out.println(getStatusMessageForHitShip(coordinateY, coordinateX));
                    }
                    case FREE_CELL_MARK -> {
                        fogWarBattlefield[coordinateY][coordinateX] = MISS_MARK;
                        System.out.println(MESSAGE_MISS_SHIP);
                    }
                    case HIT_SHIP_MARK, MISS_MARK -> System.out.println(MESSAGE_MISS_SHIP);
                    default -> throw new ArrayIndexOutOfBoundsException();
                }
                //Нажимаем Enter для передачи хода игроку
                promptEnterKey();
                break;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
            }
        }
    }

    private String[][] createBattlefield() {
        int sizeX = 11;
        int sizeY = 11;
        String[][] battlefield = new String[sizeY][sizeX];
        char yLineMark = 'A';
        for (int y = 0; y < battlefield.length; y++) {
            for (int x = 0; x < battlefield[y].length; x++) {
                if (y == 0 && x == 0) battlefield[y][x] = " ";
                else if (y == 0) {
                    battlefield[y][x] = " " + x;
                } else if (x == 0) {
                    battlefield[y][x] = Character.toString(yLineMark);
                    yLineMark++;
                } else {
                    battlefield[y][x] = " ~";
                }
            }
        }
        return battlefield;
    }

    private String getStatusMessageForHitShip(int coordinateY, int coordinateX) {
        boolean isShipShank = isShipShank(coordinateY, coordinateX);
        shipCount = isShipShank ? --shipCount : shipCount;
        if (shipCount == 0) {
            System.out.println("You sank the last ship. You won. Congratulations!");
            System.exit(0);
        }
        if (isShipShank) {
            return "You sank a ship!";
        } else {
            return MESSAGE_HIT_SHIP;
        }
    }

    private boolean isShipShank(int coordinateY, int coordinateX) {
        return switch (divineDirection(coordinateY, coordinateX)) {
            case VERTICALLY -> considerDamageOnVerticalShip(coordinateY, coordinateX);
            case HORIZONTALLY -> considerDamageOnHorizontalShip(coordinateY, coordinateX);
            case NOPE -> true;
            default -> false;
        };
    }

    private boolean considerDamageOnVerticalShip(int coordinateY, int coordinateX) {
        boolean isShipDestroyDownBoard = true;
        int shift = 1;
        String currentCell = enemyBattlefield[coordinateY][coordinateX];

        while (currentCell.equals(HIT_SHIP_MARK)) {
            if (coordinateY + shift <= 10) {
                currentCell = enemyBattlefield[coordinateY + shift][coordinateX];
                isShipDestroyDownBoard = currentCell.equals(HIT_SHIP_MARK);
                if (!isShipDestroyDownBoard) {
                    isShipDestroyDownBoard = !currentCell.equals(OCCUPIED_CELL_MARK);
                    break;
                }
                shift++;
                continue;
            }
            break;
        }

        boolean isShipDestroyUpBoard = true;
        shift = 1;
        currentCell = enemyBattlefield[coordinateY][coordinateX];
        while (currentCell.equals(HIT_SHIP_MARK)) {
            if (coordinateY - shift >= 1) {
                currentCell = enemyBattlefield[coordinateY - shift][coordinateX];
                isShipDestroyUpBoard = currentCell.equals(HIT_SHIP_MARK);
                if (!isShipDestroyUpBoard) {
                    isShipDestroyUpBoard = !currentCell.equals(OCCUPIED_CELL_MARK);
                    break;
                }
                shift++;
                continue;
            }
            break;
        }
        return isShipDestroyDownBoard && isShipDestroyUpBoard;
    }

    private boolean considerDamageOnHorizontalShip(int coordinateY, int coordinateX) {
        boolean isShipDestroyLeftBoard = true;
        int shift = 1;
        String currentCell = enemyBattlefield[coordinateY][coordinateX];
        //left side Ship
        while (currentCell.equals(HIT_SHIP_MARK)) {
            if (coordinateX - shift >= 1) {
                currentCell = enemyBattlefield[coordinateY][coordinateX - shift];
                isShipDestroyLeftBoard = currentCell.equals(HIT_SHIP_MARK);
                if (!isShipDestroyLeftBoard) {
                    isShipDestroyLeftBoard = !currentCell.equals(OCCUPIED_CELL_MARK);
                    break;
                }
                shift++;
                continue;
            }
            break;
        }

        boolean isShipDestroyRightBoard = true;
        shift = 1;
        //Right site Ship
        currentCell = enemyBattlefield[coordinateY][coordinateX];
        while (currentCell.equals(HIT_SHIP_MARK)) {
            if (coordinateX + shift <= 10) {
                currentCell = enemyBattlefield[coordinateY][coordinateX + shift];
                isShipDestroyRightBoard = currentCell.equals(HIT_SHIP_MARK);
                if (!isShipDestroyRightBoard) {
                    isShipDestroyRightBoard = !currentCell.equals(OCCUPIED_CELL_MARK);
                    break;
                }
                shift++;
                continue;
            }
            break;
        }
        return isShipDestroyLeftBoard && isShipDestroyRightBoard;
    }

    // Определяем корабли расположен вертикально, горизонтально или однопалубник для дальнейшего расчёта повреждений
    private Direction divineDirection(int coordinateY, int coordinateX) {
        boolean isDirectionHorizontal = false;
        boolean isDirectionVertical = false;

        if (coordinateX < 10) {
            isDirectionHorizontal = enemyBattlefield[coordinateY][coordinateX + 1].equals(OCCUPIED_CELL_MARK) ||
                    enemyBattlefield[coordinateY][coordinateX + 1].equals(HIT_SHIP_MARK);
        }
        isDirectionHorizontal = isDirectionHorizontal ||
                enemyBattlefield[coordinateY][coordinateX - 1].equals(OCCUPIED_CELL_MARK) ||
                enemyBattlefield[coordinateY][coordinateX - 1].equals(HIT_SHIP_MARK);
        if (isDirectionHorizontal) {
            return Direction.HORIZONTALLY;
        }

        if (coordinateY < 10) {
            isDirectionVertical = enemyBattlefield[coordinateY + 1][coordinateX].equals(OCCUPIED_CELL_MARK) ||
                    enemyBattlefield[coordinateY + 1][coordinateX].equals(HIT_SHIP_MARK);
        }
        isDirectionVertical = isDirectionVertical ||
                enemyBattlefield[coordinateY - 1][coordinateX].equals(OCCUPIED_CELL_MARK) ||
                enemyBattlefield[coordinateY - 1][coordinateX].equals(HIT_SHIP_MARK);
        if (isDirectionVertical) {
            return Direction.VERTICALLY;
        }

        return Direction.NOPE;
    }

    private void createShip(ShipType type) {
        boolean isShipCreate = false;
        System.out.printf("Enter the coordinates of the %s (%d cells):\n", type.getName(), type.getLength());
        while (!isShipCreate) {
            Scanner scanner = new Scanner(System.in);
            String fullCoordinate = scanner.nextLine();
            String coordinate1 = fullCoordinate.substring(0, 3).trim();
            String coordinate2 = fullCoordinate.substring(3).trim();
            int[] coordinate = setCoordinate(coordinate1, coordinate2);
            Ship ship = new Ship(type, coordinate);
            isShipCreate = placingShipOnField(ship);
        }
        shipCount++;
    }

    private int[] setCoordinate(String coordinate1, String coordinate2) {
        coordinate1 = coordinate1.toUpperCase();
        coordinate2 = coordinate2.toUpperCase();
        return new int[]{getStartCoordinateX(coordinate1, coordinate2),
                getStartCoordinateY(coordinate1, coordinate2),
                getEndCoordinateX(coordinate1, coordinate2),
                getEndCoordinateY(coordinate1, coordinate2)};
    }

    private boolean placingShipOnField(Ship ship) {
        boolean isFreePlaceAroundShip = checkSpaceAroundShip(ship);
        if (ship.getDirection() == Direction.VERTICALLY && isFreePlaceAroundShip) {
            for (int i = ship.getStartCoordinateY(); i <= ship.getEndCoordinateY(); i++) {
                battlefield[i][ship.getStartCoordinateX()] = OCCUPIED_CELL_MARK;
            }
            return true;
        } else if (ship.getDirection() == Direction.HORIZONTALLY && isFreePlaceAroundShip) {
            for (int i = ship.getStartCoordinateX(); i <= ship.getEndCoordinateX(); i++) {
                battlefield[ship.getStartCoordinateY()][i] = OCCUPIED_CELL_MARK;
            }
            return true;
        }
        return false;
    }

    private boolean checkSpaceAroundShip(Ship ship) {
        boolean isFreeAroundShip = false;
        switch (ship.getDirection()) {
            case VERTICALLY:
                isFreeAroundShip = chekSpaceOnVerticallyShip(ship);
                break;
            case HORIZONTALLY:
                isFreeAroundShip = chekSpaceOnHorizontalShip(ship);
                break;
        }
        if (!isFreeAroundShip) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            isFreeAroundShip = false;
        }
        return isFreeAroundShip;
    }

    private boolean chekSpaceOnVerticallyShip(Ship ship) {
        // Проверка, чтобы не выйти за пределы массива
        int cellEndShipByY = ship.getEndCoordinateY() < 10 ? ship.getEndCoordinateY() + 1 : ship.getEndCoordinateY();
        int cellRightBoard = ship.getEndCoordinateX() < 10 ? ship.getEndCoordinateX() + 1 : ship.getEndCoordinateX();

        boolean isEmptyOnShip = true;
        for (int y = ship.getStartCoordinateY(); y <= ship.getEndCoordinateY(); y++) {
            boolean inPlaceShip = !battlefield[y][ship.getStartCoordinateX()].equals(OCCUPIED_CELL_MARK);
            boolean isEmptyLeftBoard = !battlefield[y][ship.getStartCoordinateX() - 1].equals(OCCUPIED_CELL_MARK);
            boolean isEmptyRightBoard = !battlefield[y][cellRightBoard].equals(OCCUPIED_CELL_MARK);
            if (!inPlaceShip || !isEmptyRightBoard || !isEmptyLeftBoard) {
                isEmptyOnShip = false;
                break;
            }
        }
        boolean isEmptyOnStartShip = !battlefield[ship.getStartCoordinateY() + 1][ship.getStartCoordinateX()].equals(OCCUPIED_CELL_MARK);
        boolean isEmptyOnEndShip = !battlefield[cellEndShipByY][ship.getEndCoordinateX()].equals(OCCUPIED_CELL_MARK);
        return isEmptyOnStartShip && isEmptyOnEndShip && isEmptyOnShip;
    }

    private boolean chekSpaceOnHorizontalShip(Ship ship) {
        // Проверка, чтобы не выйти за пределы массива
        int cellEndShipByX = ship.getEndCoordinateX() < 10 ? ship.getEndCoordinateX() + 1 : ship.getEndCoordinateX();
        int cellRightBoardY = ship.getStartCoordinateY() < 10 ? ship.getEndCoordinateY() + 1 : ship.getEndCoordinateY();

        boolean isEmptyOnShip = true;
        for (int x = ship.getStartCoordinateX(); x <= ship.getEndCoordinateX(); x++) {
            boolean inPlaceShip = !battlefield[ship.getStartCoordinateY()][x].equals(OCCUPIED_CELL_MARK);
            boolean isEmptyLeftBoard = !battlefield[ship.getStartCoordinateY() - 1][x].equals(OCCUPIED_CELL_MARK);
            boolean isEmptyRightBoard = !battlefield[cellRightBoardY][x].equals(OCCUPIED_CELL_MARK);
            if (!inPlaceShip || !isEmptyRightBoard || !isEmptyLeftBoard) {
                isEmptyOnShip = false;
                break;
            }
        }
        boolean isEmptyOnStartShip = !battlefield[ship.getStartCoordinateY()][ship.getStartCoordinateX() - 1].equals(OCCUPIED_CELL_MARK);
        boolean isEmptyOnEndShip = !battlefield[ship.getEndCoordinateY()][cellEndShipByX].equals(OCCUPIED_CELL_MARK);
        return isEmptyOnStartShip && isEmptyOnEndShip && isEmptyOnShip;
    }

    private int getStartCoordinateX(String coordinate1) {
        coordinate1 = coordinate1.replaceAll("[^\\d]", "");
        return Integer.parseInt(coordinate1);
    }

    private int getStartCoordinateX(String coordinate1, String coordinate2) {
        coordinate1 = coordinate1.replaceAll("[^\\d]", "");
        coordinate2 = coordinate2.replaceAll("[^\\d]", "");
        return Math.min(Integer.parseInt(coordinate1), Integer.parseInt(coordinate2));
    }


    private int getEndCoordinateX(String coordinate1, String coordinate2) {
        coordinate1 = coordinate1.replaceAll("[^\\d]", "");
        coordinate2 = coordinate2.replaceAll("[^\\d]", "");
        return Math.max(Integer.parseInt(coordinate1), Integer.parseInt(coordinate2));
    }

    private int getStartCoordinateY(String coordinate1) {
        return coordinate1.charAt(0) - START_MEASURE_COORDINATE_Y;
    }

    private int getStartCoordinateY(String coordinate1, String coordinate2) {
        return Math.min(coordinate1.charAt(0), coordinate2.charAt(0)) - START_MEASURE_COORDINATE_Y;
    }

    private int getEndCoordinateY(String coordinate1, String coordinate2) {
        return Math.max(coordinate1.charAt(0), coordinate2.charAt(0)) - START_MEASURE_COORDINATE_Y;
    }

    //Нажать Enter для передачи хода игроку
    private static void promptEnterKey() {
        System.out.println(MESSAGE_TURN_NEXT_PLAYER);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

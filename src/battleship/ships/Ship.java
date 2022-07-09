package battleship.ships;

public class Ship {
    private final int startCoordinateX;
    private final int startCoordinateY;
    private final int endCoordinateX;
    private final int endCoordinateY;
    protected int length;
    private final String name;
    private Direction direction;

    public Ship(ShipType type, int[] coordinate) {
        this.length = type.getLength();
        this.name = type.getName();
        startCoordinateX = coordinate[0];
        startCoordinateY = coordinate[1];
        endCoordinateX = coordinate[2];
        endCoordinateY = coordinate[3];
        isCorrectShipLocations();
        direction = divineDirection();
        isCorrectLengthShip(direction);
    }

    public int getStartCoordinateX() {
        return startCoordinateX;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getStartCoordinateY() {
        return startCoordinateY;
    }

    public int getEndCoordinateX() {
        return endCoordinateX;
    }

    public int getEndCoordinateY() {
        return endCoordinateY;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    private boolean isCorrectShipLocations() {
        boolean isCorrectShipLocationsOnVertically = getStartCoordinateY() == getEndCoordinateY();
        boolean isCorrectShipLocationsOnHorizontally = getStartCoordinateX() == getEndCoordinateX();
        if (isCorrectShipLocationsOnVertically || isCorrectShipLocationsOnHorizontally) {
            return true;
        }
        System.out.println("Error! Wrong ship location! Try again: ");
        throw new RuntimeException();
    }

    private boolean isCorrectLengthShip(Direction direction) {
        boolean isTrueLength = false;
        switch (direction) {
            case VERTICALLY:
                isTrueLength = getLength() == getEndCoordinateY() - getStartCoordinateY() + 1;
                break;
            case HORIZONTALLY:
                isTrueLength = getLength() == getEndCoordinateX() - getStartCoordinateX() + 1;
                break;
        }
        if (isTrueLength) {
            return true;
        }
        System.out.printf("Error! Wrong length of the %s! Try again:\n", getName());;
        throw new RuntimeException();
    }

    private Direction divineDirection() {
        if (getStartCoordinateY() == getEndCoordinateY()) {
            return Direction.HORIZONTALLY;
        } else {
            return Direction.VERTICALLY;
        }
    }
}


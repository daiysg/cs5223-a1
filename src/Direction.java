public enum Direction {

    W(1),
    S(2),
    E(3),
    N(4),
    QUIT(9),
    NO_MOVE(0),
    INVALID(-1);

    private final Integer direction;

    Direction(Integer direction) {
        this.direction = direction;
    }

    public Integer getDirecton() {
        return direction;
    }


    public static Direction getDirection(String inputString) {

        try {
            Integer input = Integer.valueOf(inputString);
            switch (input) {
                case 1:
                    return W;
                case 2:
                    return S;
                case 3:
                    return E;
                case 4:
                    return N;
                case 9:
                    return QUIT;
                case 0:
                    return NO_MOVE;
                default:
                    return INVALID;
            }
        } catch (Exception ex) {
            return INVALID;
        }
    }
}

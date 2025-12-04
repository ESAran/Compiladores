public class SemanticTable {
    public static final int ERR = -1;
    public static final int OK_ = 0;
    public static final int WAR = 1;
    public static final int INT = 0;
    public static final int FLO = 1;
    public static final int CHA = 2;
    public static final int STR = 3;
    public static final int BOO = 4;
    public static final int SUM = 0;
    public static final int SUB = 1;
    public static final int MUL = 2;
    public static final int DIV = 3;
    public static final int REL = 4;
    static int[][][] expTable = new int[][][]{{{0, 0, 0, 1, 4}, {1, 1, 1, 1, 4}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}}, {{1, 1, 1, 1, 4}, {1, 1, 1, 1, 4}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}}, {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, 4}, {-1, -1, -1, -1, 4}, {-1, -1, -1, -1, -1}}, {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {3, -1, -1, -1, -1}, {3, -1, -1, -1, 4}, {-1, -1, -1, -1, -1}}, {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, 4}}};
    static int[][] atribTable = new int[][]{{0, 1, -1, -1, -1}, {0, 0, -1, -1, -1}, {-1, -1, 0, -1, -1}, {-1, -1, 0, 0, -1}, {-1, -1, -1, -1, 0}};

    static int resultType(int TP1, int TP2, int OP) {
        return expTable[TP1][TP2][OP];
    }

    static int atribType(int TP1, int TP2) {
        return atribTable[TP1][TP2];
    }
}

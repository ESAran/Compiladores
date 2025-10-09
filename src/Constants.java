public interface Constants extends ScannerConstants, ParserConstants
{
    int EPSILON  = 0;
    int DOLLAR   = 1;

    int t_ID = 2;
    int t_LIT_INT = 3;
    int t_LIT_FLOAT = 4;
    int t_KEY_PRINT = 5;
    int t_OP_POW = 6;
    int t_OP_LOG = 7;
    int t_OP_SUM = 8;
    int t_OP_SUB = 9;
    int t_OP_MULT = 10;
    int t_OP_DIV = 11;
    int t_OP_MOD = 12;
    int t_OP_INC = 13;
    int t_OP_DEC = 14;
    int t_REL_EQUAL = 15;
    int t_DEL_SEMICOLON = 16;
    int t_DEL_LPAREN = 17;
    int t_DEL_RPAREN = 18;
    int t_DEL_LBRACKET = 19;
    int t_DEL_RBRACKET = 20;
    int t_DEL_LBRACE = 21;
    int t_DEL_RBRACE = 22;

}

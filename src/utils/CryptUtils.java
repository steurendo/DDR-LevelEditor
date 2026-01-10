package utils;

public class CryptUtils {
    private final static int LV = 1997;
    private final static int PW = 94;

    private static String toHex(int x) {
        int i;
        String[] values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuilder ret;

        ret = new StringBuilder();
        for (i = 0; i < 8; i++) {
            ret.insert(0, values[x % 16]);
            x /= 16;
        }

        return ret.toString();
    }

    /*private int toDec(String x)
    {
    	int ret, i;

    	ret = 0;
    	for (i = 0; i < 8; i++)
    		ret += (x.charAt(i) >= 65 && x.charAt(i) <= 70 ? (10 + (int)x.charAt(i) - 65) : Integer.parseInt("" + x.charAt(i))) * (int)Math.pow(16, (7 - i));

    	return ret;
    }*/
    public static String crypt(int value) {
        return crypt(value, LV, PW);
    }

    private static String crypt(int value, int lv, int pw) {
        return toHex((value + lv) * pw);
    }
    /*private int decrypt(String x) { return decrypt(x, LV, PW); }
    private int decrypt(String x, int lv, int pw) { return ((toDec(x) / pw) - lv); }*/
}

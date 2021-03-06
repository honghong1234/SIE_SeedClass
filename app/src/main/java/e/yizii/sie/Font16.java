package e.yizii.sie;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;


//解析16*16的点阵字库
public class Font16 {
    private Context context;
    public Font16(Context context) {
        this.context = context;
    }

    private final static String ENCODE = "GB2312";
    private final static String ZK16 = "./HZK16";

    private static boolean[][][] arr;
    int all_16_32 = 16;
    int all_2_4 = 2;
    int all_32_128 = 32;

    public boolean[][][] drawString(String str) throws IOException {
        byte[] data;
        int[] code;
        int byteCount;
        int lCount;

        arr = new boolean[2][all_16_32][all_16_32];
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < 0x80) {
                continue;
            }

            code = getByteCode(str.substring(i, i + 1));
            data = read(code[0], code[1]);
            byteCount = 0;
            for (int line = 0; line < all_16_32; line++) {
                lCount = 0;
                for (int k = 0; k < all_2_4; k++) {
                    for (int j = 0; j < 8; j++) {
                        if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
                            arr[i][line][lCount] = true;
                            System.out.print("*");
                        } else {
                            System.out.print(" ");
                            arr[i][line][lCount] = false;
                        }
                        lCount++;
                    }
                    byteCount++;
                }
                System.out.println();
            }
        }
        return arr;
    }

    private byte[] read(int areaCode, int posCode) throws IOException {
        byte[] data;
        int area = areaCode - 0xa0;
        int pos = posCode - 0xa0;

        InputStream in = context.getResources().openRawResource(R.raw.hzk16);

        long offset = all_32_128 * ((area - 1) * 94 + pos - 1);
        in.skip(offset);
        data = new byte[all_32_128];
        in.read(data, 0, all_32_128);
        in.close();
        return data;
    }

    private int[] getByteCode(String str) {
        int[] byteCode = new int[2];
        try {
            byte[] data = str.getBytes(ENCODE);
            byteCode[0] = data[0] < 0 ? 256 + data[0] : data[0];
            byteCode[1] = data[1] < 0 ? 256 + data[1] : data[1];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }

}

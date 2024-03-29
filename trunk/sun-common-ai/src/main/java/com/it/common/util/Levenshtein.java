package com.it.common.util;

/**
 * 比较两个字符串的相似度
 * 授权码比较系统中需要用到
 *
 * 核心算法是用一个2维数组记录每个字符串是否相同，如果相同记为0，不相同记为1，每行，每列的相同个数累加，则数组最后一个数为不相同个数的总数。从而判断这两个字符串的相似度，在判断大小写时，没有区分大小写，即大小写视为相同的字符。
 */
public class Levenshtein {
    private int compare(String str, String target)
    {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) { return m; }
        if (m == 0) { return n; }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++)
        {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++)
        {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++)
        {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++)
            {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2+32 || ch1+32 == ch2)
                {
                    temp = 0;
                } else
                {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private int min(int one, int two, int three)
    {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     */

    public float getSimilarityRatio(String str, String target)
    {
        return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
    }

//    public static void main(String[] args)
//    {
//        Levenshtein lt = new Levenshtein();
//        String str = "8ZkbV4z7m4ZLTODJhiaDr0Mne0iy4uLvwVwxKZrmi5iNtFr.E0BKl1o9s9p.FCoAE1epLrF6fhuGO-2ekOK1n5Cti.TLHOPeq5SKyliOX0q76GwP8.CaB0Yekj256ls9qW6rhH3FlevLuRQpFH0jbod2GK1.3EqP8xuvNhe8Q2aca6uiRDQ_X2jj960eAQY4rzCtn1GB3Vg20QGGz3xM7mC7yzi7J6FTeLi08SHXP2CHKXF3VcUMO0NyQyv4hHwHD0IAV4y";
//        String target = "E0BKl1o9s9p.FCoAE1epLrF6fhuGO-2ekOK1n5Cti.TLHOPeq5SKyliOX0q76GwP8.CaB0Yekj256ls9qW6rhH3FlevLuRQpFH0jbod2GK1.3EqP8xuvNhe8Q2aca6uiRDQ_X2jj960eAQY4rzCtn1GB3Vg20QGGz3xM7mC7yzi7J6FTeLi08SHXP2CHKXF3VcUMO0NyQyv4hHwHD0IAV4y";
//        System.out.println("相似度=" + lt.getSimilarityRatio(str, target));
//    }
}

package org.appwork.utils.formatter;

/*
    copyright notice: Thomas Rechenmacher, AppWork GmbH - We Make Your App Work
 */

public class StringFormatter
{
    public static String fillStart(String pre, int num, String filler)
    {
        while (pre.length() < num) {
            pre = filler + pre;
        }
        return pre;
    }

    public static String filterString(String source, String filter)
    {
        if ((source == null) || (filter == null)) {
            return "";
        }
        byte[] org = source.getBytes();
        byte[] mask = filter.getBytes();
        byte[] ret = new byte[org.length];
        int count = 0;
        for (int i = 0; i < org.length; i++)
        {
            byte letter = org[i];
            for (byte element : mask) {
                if (letter == element)
                {
                    ret[count] = letter;
                    count++;
                    break;
                }
            }
        }
        return new String(ret).trim();
    }

    public static String fillString(String binaryString, String pre, String post, int length)
    {
        while (binaryString.length() < length)
        {
            if (binaryString.length() < length) {
                binaryString = pre + binaryString;
            }
            if (binaryString.length() < length) {
                binaryString = binaryString + post;
            }
        }
        return binaryString;
    }
}
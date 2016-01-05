package jd.http;

/*
    copyright notice: Thomas Rechenmacher, AppWork GmbH - We Make Your App Work
                      used inside jDownloader see: http://jdownloader.org/

                      The source code can be downloaded at:
                      http://dev.jdownloader.org/
                      https://svn.jdownloader.org/projects
                      svn://svn.jdownloader.org/jdownloader
                      svn://svn.appwork.org/utils
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import org.appwork.utils.formatter.StringFormatter;

public final class RandomUserAgent
{
    private static class System
    {
        public String platform;
        public String osName;
        public String archs;
        public boolean inverseOrder;
        public boolean useIt;

        public System(String platform, String osName, String archs, boolean inverseOrder, boolean useIt)
        {
            this.platform = platform;
            this.osName = osName;
            this.archs = archs;
            this.inverseOrder = inverseOrder;
            this.useIt = useIt;
        }
    }

    private static final String[] releaseDate = { "2005", "11", "11" };
    private static List<String> langs = new ArrayList();
    private static List<String> ffVersionInfos = new ArrayList();
    private static List<String> ieVersions = new ArrayList();
    private static String winVersions = "";
    private static List<String[]> winAddons = new ArrayList();
    private static List<String> linuxAddons = new ArrayList();
    private static List<String> macAddons = new ArrayList();
    private static List<System> system = new ArrayList();

    static
    {
        ieVersions.add("6.0");
        ieVersions.add("7.0");
        ieVersions.add("8.0");

        winVersions = "NT 5.0|NT 5.1|NT 5.2|NT 6.0|NT 6.1";

        winAddons.add(new String[] { "", ".NET CLR 1.0.3705", ".NET CLR 1.1.4322" });
        winAddons.add(new String[] { "", ".NET CLR 2.0.40607", ".NET CLR 2.0.50727" });
        winAddons.add(new String[] { "", ".NET CLR 3.0.04506.648", ".NET CLR 3.0.4506.2152" });
        winAddons.add(new String[] { "", ".NET CLR 3.5.21022", ".NET CLR 3.5.30729" });

        langs.add("en");
        langs.add("en-US");
        langs.add("en-GB");

        ffVersionInfos.add("1.8|1.5|2005.11.11");
        ffVersionInfos.add("1.8.1|2.0|2006.10.10");
        ffVersionInfos.add("1.9|3.0|2008.05.29.xx");
        ffVersionInfos.add("1.9.0.5|3.0.5|2008.12.01.xx");
        ffVersionInfos.add("1.9.0.6|3.0.6|2009.01.19.xx");
        ffVersionInfos.add("1.9.0.7|3.0.7|2009.02.19.xx");
        ffVersionInfos.add("1.9.0.8|3.0.8|2009.03.26.xx");
        ffVersionInfos.add("1.9.0.9|3.0.9|2009.04.08.xx");
        ffVersionInfos.add("1.9.0.10|3.0.10|2009.04.23.xx");

        linuxAddons.add(" ");
        linuxAddons.add("Ubuntu/8.04 (hardy)");
        linuxAddons.add("Ubuntu/8.10 (intrepid)");

        linuxAddons.add("Fedora/3.0.6-1.fc10");
        linuxAddons.add("Fedora/3.0.10-1.fc10");
        linuxAddons.add("(Gentoo)");
        linuxAddons.add("SUSE/3.0-1.2");
        linuxAddons.add("SUSE/3.0-1.1");
        linuxAddons.add("Red Hat/3.0.5-1.el5_2");

        macAddons.add("");
        macAddons.add("Mach-O");
        macAddons.add("10.4");
        macAddons.add("10.5");
        macAddons.add("10.6");

        system.add(new System("Windows", "Windows", winVersions, false, true));
        system.add(new System("X11", "Linux", "x86|x86_64|i586|i686", false, true));
        system.add(new System("Macintosh", "Mac OS X", "Intel|PPC|68K", true, true));
        system.add(new System("X11", "FreeBSD", "i386|amd64|sparc64|alpha", false, true));
        system.add(new System("X11", "OpenBSD", "i386|amd64|sparc64|alpha", false, true));
        system.add(new System("X11", "NetBSD", "i386|amd64|sparc64|alpha", false, true));
        system.add(new System("X11", "SunOS", "i86pc|sun4u", false, true));
    }

    private static String dotNetString()
    {
        Random rand = new Random();

        String dotNet10 = "; " + ((String[])winAddons.get(0))[rand.nextInt(((String[])winAddons.get(0)).length)];
        if (dotNet10.equalsIgnoreCase("; ")) {
            dotNet10 = "";
        }
        String dotNet20 = "; " + ((String[])winAddons.get(1))[rand.nextInt(((String[])winAddons.get(1)).length)];
        if (dotNet20.equalsIgnoreCase("; ")) {
            dotNet20 = "";
        }
        String dotNet30 = "";
        if (dotNet20.length() != 0) {
            dotNet30 = "; " + ((String[])winAddons.get(2))[rand.nextInt(((String[])winAddons.get(2)).length)];
        }
        if (dotNet30.equalsIgnoreCase("; ")) {
            dotNet30 = "";
        }
        String dotNet35 = "";
        if (dotNet30.length() != 0) {
            dotNet35 = "; " + ((String[])winAddons.get(3))[rand.nextInt(((String[])winAddons.get(3)).length)];
        }
        if (dotNet35.equalsIgnoreCase("; ")) {
            dotNet35 = "";
        }
        return dotNet10 + dotNet20 + dotNet30 + dotNet35;
    }

    public static String generate()
    {
        if (new Random().nextInt() % 2 == 0) {
            return generateFF();
        }
        return generateIE();
    }

    public static String generateFF()
    {
        Random rand = new Random();

        String platform = "";
        String osAndArch = "";
        String winAddon = "";
        String linuxAddon = " ";
        String macAddon = "";

        int i = rand.nextInt(system.size());
        do
        {
            platform = ((System)system.get(i)).platform;
            String osName = ((System)system.get(i)).osName;
            String[] archs = ((System)system.get(i)).archs.split("\\|");
            String arch = archs[rand.nextInt(archs.length)];
            if (!((System)system.get(i)).inverseOrder) {
                osAndArch = osName + " " + arch;
            } else {
                osAndArch = arch + " " + osName;
            }
        } while (!((System)system.get(i)).useIt);
        if (((System)system.get(i)).osName.equalsIgnoreCase("Windows"))
        {
            winAddon = dotNetString();
            if (winAddon.trim().length() > 0) {
                winAddon = (" (" + winAddon.trim() + ")").replace("(; ", "(");
            }
        }
        else if (((System)system.get(i)).osName.equalsIgnoreCase("Linux"))
        {
            linuxAddon = (String)linuxAddons.get(rand.nextInt(linuxAddons.size()));
            if (linuxAddon != " ") {
                linuxAddon = " " + linuxAddon.trim() + " ";
            }
        }
        else if (((System)system.get(i)).osName.equalsIgnoreCase("Mac OS X"))
        {
            macAddon = (String)macAddons.get(rand.nextInt(macAddons.size()));
            if (macAddon != "") {
                macAddon = " " + macAddon.trim();
            }
        }
        String lang = (String)langs.get(rand.nextInt(langs.size()));

        String[] tmpFFVersionInfos = ((String)ffVersionInfos.get(rand.nextInt(ffVersionInfos.size()))).split("\\|");
        String ffRev = tmpFFVersionInfos[0];
        String ffVersion = tmpFFVersionInfos[1];
        String[] ffReleaseDate = tmpFFVersionInfos[2].split("\\.");

        return "Mozilla/5.0 (" + platform + "; U; " + osAndArch + macAddon + "; " + lang + "; rv:" + ffRev + ") Gecko/" + randomDate(ffReleaseDate) + linuxAddon + "Firefox/" + ffVersion + winAddon;
    }

    public static String generateIE()
    {
        Random rand = new Random();

        String ieVersion = (String)ieVersions.get(rand.nextInt(ieVersions.size()));
        String winVersion = winVersions.split("\\|")[rand.nextInt(winVersions.split("\\|").length)];
        String trident = "";
        if (ieVersion.equalsIgnoreCase("8.0")) {
            trident = "; Trident/4.0";
        }
        return "Mozilla/4.0 (compatible; MSIE " + ieVersion + "; Windows " + winVersion + trident + dotNetString() + ")";
    }

    private static String randomDate(String[] releaseDate)
    {
        String returnDate = releaseDate[0] + releaseDate[1] + releaseDate[2];
        if ((releaseDate == null) || (releaseDate.length < 3) || (releaseDate.length > 4)) {
            return returnDate;
        }
        Calendar rCal = new GregorianCalendar(Integer.parseInt(releaseDate[0]), Integer.parseInt(releaseDate[1]) - 1, Integer.parseInt(releaseDate[2]));
        long rTime = rCal.getTimeInMillis();
        long cTime = new GregorianCalendar().getTimeInMillis();

        Random rand = new Random();
        int temp = (int)((cTime - rTime) / 60000L) + (int)(rTime / 60000L);
        if (temp < 0) {
            temp = -temp;
        }
        long randTime = rand.nextInt(temp);
        rCal.setTimeInMillis(randTime * 60L * 1000L);

        int year = rCal.get(1);
        String month = StringFormatter.fillString(rCal.get(2) + 1 + "", "0", "", 2);
        String day = StringFormatter.fillString(rCal.get(5) + "", "0", "", 2);
        String hour = "";
        if (releaseDate.length == 4) {
            hour = StringFormatter.fillString(rand.nextInt(24) + "", "0", "", 2);
        }
        returnDate = "" + year + month + day + hour;
        return returnDate;
    }
}
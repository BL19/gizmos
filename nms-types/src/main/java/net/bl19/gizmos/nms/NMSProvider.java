package net.bl19.gizmos.nms;

import org.bukkit.Bukkit;

public class NMSProvider {

    /**
     * The server version, e.g "1_16_R3"
     */
    public static final String VERSION = getVersion();
    private static GizmosNMS nms;
    public static boolean forceClosestSupport = false;



    private static String getVersion() {
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return "v" + packageName.substring(packageName.lastIndexOf('.') + 2);
    }
    
    private static String getPackageVersion() {
        // Get package version from bukkit
        String version = Bukkit.getBukkitVersion().split("-")[0]; // 1.14-R0.1-SNAPSHOT
        if (version.startsWith("1.19")) {
            return "v1_19_R3";
        }
        if (version.startsWith("1.20")) {
            return "v1_20_R2";
        }
        return null;
    }
    
    public static GizmosNMS getNMS() {
        if (nms == null) {
            nms = getNMSImpl();
        }
        return nms;
    }
    
    private static GizmosNMS getNMSImpl() {
        try {
            Class<?> clazz = Class.forName("net.bl19.gizmos.nms." + VERSION + ".GizmosNMSImpl");
            return (GizmosNMS) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.printf("NMS Version (%s) is not supported by Gizmos! KEY FEATURES DISABLED. Please contact the developer.%n", VERSION);
            e.printStackTrace();
            
            if(getPackageVersion() != null && forceClosestSupport) {
                try {
                    Class<?> clazz = Class.forName("net.bl19.gizmos.nms." + getPackageVersion() + ".GizmosNMSImpl");
                    return (GizmosNMS) clazz.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e2) {
                    System.out.printf("NMS Version (%s)[forced] is not supported by Gizmos! KEY FEATURES DISABLED. Please contact the developer.%n", VERSION);
                    e2.printStackTrace();
                }
            } else if (getPackageVersion() != null) {
                System.out.printf("Version forcing available for other NMS Version (%s), enable at your own risk.%n", getPackageVersion());
            }
        }
        return null;
    }
    
}

package net.dgg.utils.number;

/**
 * 关于地球的计算
 *
 * @author 马洪
 */
public final class DggEarthUtil {

    private static final double EARTH_RADIUS = 6378.137;// km

    private DggEarthUtil() {
    }

    private static double radian(double degree) {
        return degree * Math.PI / 180.0;
    }

    /**
     * 根据经纬度计算距离
     *
     * @param lng1 1点的经度
     * @param lat1 1点的纬度
     * @param lng2 2点的经度
     * @param lat2 2点的纬度
     * @return double 单位米
     */
    public static double distance(double lng1, double lat1, double lng2, double lat2) {
        double b = radian(lng1) - radian(lng2);
        double radLat1 = radian(lat1);
        double radLat2 = radian(lat2);
        double a = radLat1 - radLat2;
        return 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)))
                * EARTH_RADIUS * 1000;
    }

}

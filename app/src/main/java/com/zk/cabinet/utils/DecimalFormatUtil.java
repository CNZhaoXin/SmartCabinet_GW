package com.zk.cabinet.utils;

import java.text.DecimalFormat;

/**
 * 数字格式化工具类
 */
public class DecimalFormatUtil {

    /**
     * 保留一位小数并格式化成金钱显示的格式 000，000，000.00
     *
     * @param data
     * @return
     */
    public static String formatMoney(double data) {
        return new DecimalFormat("#,###,###,###").format(data);
    }

    /**
     * 保留一位小数并格式化成金钱显示的格式 000，000，000.00
     *
     * @param data
     * @return
     */
    public static String formatMoneyOne(float data) {
        return new DecimalFormat("#,###,###,##0.0").format(data);
    }

    /**
     * 保留两位小数并格式化成金钱显示的格式 000，000，000.00
     *
     * @param data
     * @return
     */
    public static String formatMoneyTwo(float data) {
        return new DecimalFormat("#,###,###,##0.00").format(data);
    }

    /**
     * 保留一位小数并格式化成 0.0
     *
     * @param data
     * @return
     */
    public static String formatOne(float data) {
        return new DecimalFormat("##0.0").format(data);
    }

    /**
     * 保留两位小数并格式化成0.00
     *
     * @param data
     * @return
     */
    public static String formatTwo(float data) {
        return new DecimalFormat("##0.00").format(data);
    }

    public static String formatMoneyOne(double data) {
        return new DecimalFormat("#,###,###,##0.0").format(data);
    }

    public static String formatMoneyTwo(double data) {
        return new DecimalFormat("#,###,###,##0.00").format(data);
    }

    public static String formatOne(double data) {
        return new DecimalFormat("##0.0").format(data);
    }

    public static String formatTwo(double data) {
        return new DecimalFormat("##0.00").format(data);
    }


    public static String formatMoneyOne(int data) {
        return new DecimalFormat("#,###,###,##0.0").format(data);
    }

    public static String formatMoneyTwo(int data) {
        return new DecimalFormat("#,###,###,##0.00").format(data);
    }

    public static String formatOne(int data) {
        return new DecimalFormat("##0.0").format(data);
    }

    public static String formatTwo(int data) {
        return new DecimalFormat("##0.00").format(data);
    }

}